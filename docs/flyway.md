# Flyway Migration Guide

운영 DB 스키마 변경은 Flyway로 버전 관리합니다.

## 설정 근거

- 의존성: `build.gradle` `org.flywaydb:flyway-core`
- 활성화: `src/main/resources/application.yml` `spring.flyway.*`
- 초기 마이그레이션: `src/main/resources/db/migration/V1__init.sql`

## 규칙

- 파일명: `V{버전}__{설명}.sql`
- 예시: `V2__add_product_indexes.sql`
- 기존 파일 수정 금지, 새 버전 파일만 추가

## 운영 반영 순서

1. 변경 SQL을 `src/main/resources/db/migration`에 추가
2. 배포 시 애플리케이션 시작 단계에서 Flyway 자동 실행
3. 실패 시 해당 배포를 중단하고 SQL 수정 후 새 버전으로 재배포

## 롤백 원칙

- Flyway는 기본적으로 정방향(Forward-only) 방식 운영
- 롤백이 필요하면 이전 파일 수정 대신 복구용 신규 버전 SQL 추가
