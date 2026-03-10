# Performance Test Guide

## 목적

- 시나리오 A: 같은 상품 주문 경쟁 시 Redis 락 경합과 응답 지연을 관측
- 시나리오 B: 같은 상품 반복 조회 시 Redis 캐시 효과를 관측

## 사전 조건

- Docker Compose 실행: `docker compose up -d --build`
- 애플리케이션 메트릭: `http://localhost:8080/actuator/prometheus`
- Grafana: `http://localhost:3000` (`admin` / `admin`)
- JMeter CLI 설치 필요

## 더미 계정/데이터

- 기본 로그인 계정은 로컬 더미 시더 기준 `user@example.com / 1234`
- 근거 파일:
  - `/Users/hyosik981010/Desktop/study/shopping/src/main/java/project/shopping/common/config/LocalDummyDataInitializer.java`
  - `/Users/hyosik981010/Desktop/study/shopping/src/main/java/project/shopping/common/config/ProductBulkDataInitializer.java`
- `productId=1` 은 예시값이다. 실제 테스트 전 주문/조회 가능한 상품 ID를 확인해 넣어야 한다.
- TODO(근거: `/Users/hyosik981010/Desktop/study/shopping/docker-compose.yml`): 기본 Compose 설정은 `SEED_BULK_ENABLED=false` 이므로, 테스트 대상 상품을 별도로 준비하거나 시드 옵션을 조정해야 한다.

## 시나리오 A 실행

```bash
THREADS=150 RAMP_UP=30 DURATION=180 PRODUCT_ID=1 \
CUSTOMER_EMAIL=user@example.com CUSTOMER_PASSWORD=1234 \
./loadtest/run-order-contention.sh
```

- JMeter CLI 직접 실행:

```bash
jmeter -n \
  -t /Users/hyosik981010/Desktop/study/shopping/loadtest/jmeter/order-contention.jmx \
  -l /Users/hyosik981010/Desktop/study/shopping/loadtest/results/order-contention/result.jtl \
  -e -o /Users/hyosik981010/Desktop/study/shopping/loadtest/results/order-contention/html \
  -Jscheme=http \
  -Jhost=localhost \
  -Jport=8080 \
  -JcustomerEmail=user@example.com \
  -JcustomerPassword=1234 \
  -JproductId=1 \
  -JorderQuantity=1 \
  -Jthreads=150 \
  -JrampUp=30 \
  -Jduration=180
```

- JMeter Plan: [loadtest/jmeter/order-contention.jmx](/Users/hyosik981010/Desktop/study/shopping/loadtest/jmeter/order-contention.jmx)
- HTML 리포트 출력: `loadtest/results/order-contention/html/index.html`
- Grafana 대시보드: `Shopping / Portfolio - Scenario A Order Contention`

## 시나리오 B 실행

```bash
THREADS=200 RAMP_UP=20 DURATION=180 PRODUCT_ID=1 \
./loadtest/run-product-cache.sh
```

- JMeter CLI 직접 실행:

```bash
jmeter -n \
  -t /Users/hyosik981010/Desktop/study/shopping/loadtest/jmeter/product-cache.jmx \
  -l /Users/hyosik981010/Desktop/study/shopping/loadtest/results/product-cache/result.jtl \
  -e -o /Users/hyosik981010/Desktop/study/shopping/loadtest/results/product-cache/html \
  -Jscheme=http \
  -Jhost=localhost \
  -Jport=8080 \
  -JproductId=1 \
  -Jthreads=200 \
  -JrampUp=20 \
  -Jduration=180
```

- 캐시 비교는 아래 두 번 실행한다.
- 1차: `REDIS_CACHE_PRODUCT_ENABLED=true`
- 2차: `REDIS_CACHE_PRODUCT_ENABLED=false`
- JMeter Plan: [loadtest/jmeter/product-cache.jmx](/Users/hyosik981010/Desktop/study/shopping/loadtest/jmeter/product-cache.jmx)
- HTML 리포트 출력: `loadtest/results/product-cache/html/index.html`
- Grafana 대시보드: `Shopping / Portfolio - Scenario B Product Cache`

## Grafana 캡처 추천 패널

- 시나리오 A: `1. Throughput`, `2. Latency`, `3. Status Mix`, `4. Redis Pressure`
- 시나리오 B: `1. Throughput`, `2. Latency`, `3. Status Mix`, `4. Redis Activity`

## JMeter 결과에서 복사할 값

- `loadtest/results/.../html/index.html` 의 Statistics 또는 Dashboard에서 아래 값만 추린다.
- 공통: `Samples`, `Error %`, `Throughput`, `Average`, `P95`, `P99`
- 시나리오 A 추가: Grafana의 `429 ratio` 피크값
- 시나리오 B 추가: 캐시 ON/OFF 각각의 `Throughput`, `P95`, `P99`

## 포트폴리오 문장 템플릿

- 복붙용 템플릿은 [docs/performance-summary-template.md](/Users/hyosik981010/Desktop/study/shopping/docs/performance-summary-template.md) 참고.

## 포트폴리오 문장 예시

- 시나리오 A: 동일 상품에 주문이 집중될 때 Redis 락 기반 재고 보호는 유지되지만, 경합 증가에 따라 p95/p99와 429 비율이 함께 상승함을 확인했다.
- 시나리오 B: 동일 상품 반복 조회에서 Redis 캐시 활성화 시 응답 지연과 서버 자원 사용량 변화를 비교 관측했다.
