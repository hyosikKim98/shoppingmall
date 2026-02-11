# Troubleshooting

## 1) 주문 동시 처리 시 재고 충돌

- 문제(증상)
- 같은 상품에 동시 주문이 몰리면 재고 부족/락 실패가 간헐적으로 발생.

- 원인(근거)
- 주문 생성은 상품별 락 획득 후 재고 차감을 수행한다.
- 락 획득 실패 시 `TOO_MANY_REQUESTS(429)`를 반환한다.
- 근거 파일:
- `/Users/hyosik981010/Desktop/study/shopping/src/main/java/project/shopping/domain/order/service/OrderService.java`
- `/Users/hyosik981010/Desktop/study/shopping/src/main/java/project/shopping/infrastructure/persistence/redis/RedisLockService.java`

- 해결(변경)
- Redis 락 재시도(지수 백오프 + 지터) 설정값(`redis.lock.retry.*`)을 조정한다.
- 주문 API 클라이언트는 429 수신 시 지수 백오프 재시도를 적용한다.

- 결과
- 동시성 상황에서 중복 차감은 방지되고, 실패 요청은 명시적 429로 관측 가능.
- 정량 측정은 API 429 비율/평균 재시도 횟수로 추적 권장.

## 2) Refresh 토큰 재발급 실패(401)

- 문제(증상)
- `/api/auth/refresh` 호출 시 `Invalid refresh token` 또는 401이 발생.

- 원인(근거)
- refresh 토큰은 DB의 기존 토큰, 만료 여부, revoked=false 조건을 동시에 만족해야 회전된다.
- 또한 JWT `tokenType=refresh` 검증을 통과해야 한다.
- 근거 파일:
- `/Users/hyosik981010/Desktop/study/shopping/src/main/java/project/shopping/domain/user/service/AuthService.java`
- `/Users/hyosik981010/Desktop/study/shopping/src/main/java/project/shopping/common/security/JwtTokenProvider.java`
- `/Users/hyosik981010/Desktop/study/shopping/src/main/resources/mybatis/mapper/UserMapper.xml`

- 해결(변경)
- 로그인 시 발급된 최신 refresh 토큰만 사용한다(구 토큰 재사용 금지).
- 서버/DB 시간 동기화로 만료 비교 오차를 줄인다.
- 운영 DB에 `refresh_tokens` 스키마가 누락되지 않았는지 점검한다.

- 결과
- 토큰 회전 정책과 DB 상태가 일치하면 refresh 성공률이 안정화된다.
- 측정 포인트: `/api/auth/refresh` 401 비율, 토큰 회전 update hit rate.
