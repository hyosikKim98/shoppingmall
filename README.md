# Shopping API Server

JWT 인증, 상품 관리, 주문/취소를 제공하는 Spring Boot 쇼핑몰 백엔드입니다.
핵심 가치는 판매자 상품 운영과 주문 재고 처리(락 + 조건부 차감)의 일관성입니다.

## 핵심 기능

- 회원가입/로그인/토큰 재발급(`access + refresh`)
- 판매자 상품 등록/수정/삭제
- 상품 목록 검색/단건 조회(가격/생성일 정렬, 페이징)
- 주문 생성(상품별 락 + 재고 조건부 차감)
- 주문 취소 시 상태 변경 + 재고 복구

## 기술 스택

- 서버: Java 21, Spring Boot 4.0.2, Spring MVC, Spring Security
- 데이터 접근: MyBatis 4.0.1
- DB: PostgreSQL(런타임), H2(테스트)
- 캐시/락: Redis(Spring Data Redis + Lettuce)
- 인증: JWT(jjwt)
- 빌드/테스트: Gradle, JUnit 5, Testcontainers

## 빠른 시작

### 1) 의존성

- JDK 21
- PostgreSQL
- Redis

### 2) 설정

`src/main/resources/application.yml` 기준:

- `spring.datasource.*`
- `redis.host`, `redis.port`
- `jwt.secret`, `jwt.access.expiration`, `jwt.refresh.expiration`, `jwt.issuer`

### 3) 실행

```bash
./gradlew bootRun
```

### 4) 테스트/컴파일

```bash
./gradlew test
./gradlew compileJava compileTestJava
```

## 프로젝트 구조

```text
src/main/java/project/shopping
├── common            # config/security/exception/response
├── domain            # user/product/order API + service + model + dto
└── infrastructure    # mybatis mapper/repository, redis lock
```

## 문서

- 문서 포털: [docs/INDEX.md](docs/INDEX.md)
- OpenAPI: [docs/api/openapi.yaml](docs/api/openapi.yaml)
- API 사용 가이드: [docs/api/README.md](docs/api/README.md)
- ERD: [docs/erd.md](docs/erd.md)
- Architecture: [docs/architecture.md](docs/architecture.md)
- Troubleshooting: [docs/troubleshooting.md](docs/troubleshooting.md)
- Flyway Migration: [docs/flyway.md](docs/flyway.md)
