# Performance Summary Template

## 시나리오 A - 주문 경합

```text
[주문 경합 테스트]
- 조건: 동일 productId={PRODUCT_ID}, {THREADS} threads, ramp-up {RAMP_UP}s, duration {DURATION}s
- JMeter 결과: Samples {SAMPLES}, Error {ERROR_PERCENT}%, Throughput {THROUGHPUT}/sec, Avg {AVG_MS}ms, P95 {P95_MS}ms, P99 {P99_MS}ms
- Grafana 관측: 2xx 비율 {SUCCESS_RATIO}, 429 비율 피크 {RATIO_429_PEAK}, Redis commands/sec 피크 {REDIS_COMMANDS_PEAK}
- 해석: 주문 경합이 증가할수록 응답 지연과 429 비율이 함께 상승했지만, 락 기반 제어로 재고 경쟁 상황을 명시적으로 제한했다.
```

## 시나리오 B - 상품 목록 조회 캐시

```text
[상품 목록 조회 캐시 테스트]
- 조건: `/api/products?page={page}&size=20&sort=createdAt,desc`, hot page 50%(0..9), random page 50%(10..59), {THREADS} threads, ramp-up {RAMP_UP}s, duration {DURATION}s
- 캐시 OFF: Throughput {OFF_THROUGHPUT}/sec, Avg {OFF_AVG_MS}ms, P95 {OFF_P95_MS}ms, P99 {OFF_P99_MS}ms, Redis {OFF_REDIS_CMDS}/sec
- 캐시 ON: Throughput {ON_THROUGHPUT}/sec, Avg {ON_AVG_MS}ms, P95 {ON_P95_MS}ms, P99 {ON_P99_MS}ms, Redis {ON_REDIS_CMDS}/sec
- Grafana 관측: Redis commands/sec {REDIS_COMMANDS_NOTE}, 5xx 비율 {ERROR_RATIO}
- 해석: 상품 목록 조회에서 hot/random page가 섞인 읽기 패턴 기준으로 캐시 활성화 시 지연 지표가 개선되고, 읽기 트래픽 일부가 Redis로 이동하는 패턴을 확인했다.
```

## 짧은 한 줄 버전

```text
주문 경합 테스트에서 {THREADS}개 동시 요청 기준 P95 {P95_MS}ms, 429 피크 {RATIO_429_PEAK}를 관측해 락 경합 구간의 성능 한계를 확인했다.
```

```text
상품 목록 조회 캐시 테스트에서 캐시 ON/OFF를 비교한 결과 P95가 {OFF_P95_MS}ms -> {ON_P95_MS}ms로 변해 캐시 적용 효과를 확인했다.
```
