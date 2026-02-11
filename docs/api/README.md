# API Quick Guide

## Base URL

- `http://localhost:8080`

## 인증 방식

- 로그인/회원가입/리프레시를 제외한 `/api/**`는 Bearer JWT 필요.
- 헤더 예시: `Authorization: Bearer <accessToken>`

## 대표 curl 예시

### 1) 로그인

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"user@example.com","password":"secret"}'
```

### 2) 토큰 재발급

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H 'Content-Type: application/json' \
  -d '{"refreshToken":"<refresh-token>"}'
```

### 3) 주문 취소(상태 변경)

```bash
curl -X POST http://localhost:8080/api/orders/1/cancel \
  -H 'Authorization: Bearer <access-token>'
```

## 참고

- 상세 스펙은 [openapi.yaml](./openapi.yaml) 참고.
- 에러 응답 포맷은 `ErrorResponse` 스키마(`errorCode`, `message`, `traceId`, `timestamp`)를 사용.
