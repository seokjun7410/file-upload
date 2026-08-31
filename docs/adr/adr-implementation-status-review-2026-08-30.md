---
status: review
reviewed_at: 2026-08-31
implementation_baseline: feat/extension-policy-audit-history@ddb5698
---

# ADR 구현 상태 점검과 다음 작업

## 점검 기준

ADR의 `accepted` 표기와 구현 완료를 분리한다. 구현 완료는 현재 기준 브랜치의 Java 코드·설정·프런트 JavaScript·테스트에서 확인되는 경우에만 표시한다.

이번 점검의 기준 코드는 `feat/extension-policy-audit-history@ddb5698`이다. 문서 전용 `docs` 브랜치의 `src`는 구현 근거로 사용하지 않는다. `feat/upload-policy-reliability`와 `feat/upload-request-id-idempotency`에만 존재하는 커밋은 현재 기준 브랜치에 병합되지 않았으므로 완료로 계산하지 않는다.

상태의 의미는 다음과 같다.

- `구현 완료`: 현재 기준 브랜치의 코드·설정·테스트에 핵심 동작이 존재한다.
- `결정 완료·구현 대기`: 정책과 계약은 문서로 확정했지만 현재 코드에 구현이 없다.
- `부분 구현·보류`: 일부 기존 동작은 유지하지만 추가 정책 구현을 보류한다.
- `의도적 보류`: `proposed` ADR이며 도입 조건이 충족되지 않았다.

## ADR별 구현 상태

| ADR | 상태 | 현재 기준 브랜치의 근거 |
|---|---|---|
| [0001](0001-unify-extension-policies.md) | 구현 완료 | `ExtensionPolicy` 단일 엔티티, fixed/custom 정책, quota와 동시성 테스트가 존재한다. |
| [0002](0002-use-server-policy-state-as-source-of-truth.md) | 구현 완료 | 정책 변경 실패 뒤 서버 상태를 재조회하고 화면 상태를 복구하는 흐름이 존재한다. |
| [0003](0003-server-generated-file-storage-policy.md) | 구현 완료 | `LocalFileStorage`가 서버 생성 파일명으로 기본 저장을 수행한다. |
| [0004](0004-use-extension-name-value-object.md) | 구현 완료 | `ExtensionName`이 정규화·기본 확장자 검증을 담당한다. |
| [0005](0005-limit-upload-to-known-non-executable-types.md) | 결정 완료·구현 대기 | Tika 감지기·실행 MIME 카탈로그·차단 테스트가 현재 기준 브랜치에 없다. 구현 코드는 별도 기능 브랜치에만 있다. |
| [0006](0006-persist-upload-file-name-mapping.md) | 결정 완료·구현 대기 | `UploadFile`·원본 파일명 값 객체·저장소가 현재 기준 브랜치에 없다. |
| [0007](0007-use-final-file-extension-for-upload-blocking.md) | 구현 완료 | 최종 확장자 추출과 다중 점 파일명 테스트가 존재한다. |
| [0009](0009-limit-multipart-upload-size.md) | 결정 완료·구현 대기 | `application.yml`의 10MB·12MB 설정과 413 매핑이 현재 기준 브랜치에 없다. |
| [0010](0010-limit-extension-name-characters.md) | 부분 구현·보류 | 기존 정규화·20자·점 거부는 유지하고 허용 문자 강화는 보류한다. |
| [0011](0011-externalize-upload-storage-path.md) | 결정 완료·구현 대기 | `file.upload.storage-path` 설정 주입이 현재 기준 브랜치에 없다. |
| [0012](0012-preserve-policy-change-history-for-operations.md) | 구현 완료 | 정책 초기화·생성·상태 변경·삭제 이력과 원자성 테스트가 존재한다. 이벤트 후 상태는 `state` 하나로 저장한다. |
| [0013](0013-use-request-id-and-frontend-owned-upload-messages.md) | 결정 완료·구현 대기 | `Idempotency-Key` 검증, requestId 오류 응답, 안전한 context 구현이 현재 기준 브랜치에 없다. |
| [0014](0014-persist-upload-state-before-file-and-finalize-atomically.md) | 결정 완료·구현 대기 | `UploadFile`, 임시 경로, atomic move, stale 복구 서비스가 현재 기준 브랜치에 없다. |
| [0015](0015-separate-upload-retry-idempotency-and-state.md) | 결정 완료·구현 대기 | 멱등 기록·결과 재사용·`409 + Retry-After` 구현이 현재 기준 브랜치에 없다. 키 보존 기간도 미결정이다. |
| [0016](0016-migrate-to-allowlist-when-policy-requires.md) | 의도적 보류 | `proposed` ADR이며 정책 집합·scope·allowlist·shadow 구현을 시작하지 않는다. |

## 현재 코드 기준 요약

현재 기준 브랜치에서 스프린트 2 추가 기능 중 실제 구현된 것은 정책 변경 감사 이력이다. 업로드 MIME 검증, multipart 제한, 저장 루트 외부화, 원본 파일명 메타데이터, requestId 멱등성·상태 전이는 문서상 결정은 존재하지만 코드가 아직 병합되지 않았다.

현재 기준 브랜치에서 실행한 `./gradlew test`는 성공했다. 이 결과는 현재 코드에 존재하는 정책 기능의 테스트 통과를 의미하며, 다른 기능 브랜치의 업로드 신뢰성 구현을 검증한 결과가 아니다.

## 다음 작업과 선결 조건

1. 업로드 신뢰성·MIME·용량·저장 루트 기능을 현재 기준 브랜치에 병합하거나 순서대로 구현한다.
2. 병합된 코드와 관련 테스트를 기준으로 체크리스트의 `[x]`를 갱신한다.
3. 그 이후에 requestId 오류 안내 브라우저 smoke와 키 보존 기간 후속 결정을 진행한다.

ADR 0016은 현재도 `proposed`다. 전환 조건과 운영 책임이 확정되기 전에는 policy-set 스키마·scope API·실제 allowlist 차단을 구현하지 않는다.
