---
status: review
reviewed_at: 2026-08-31
implementation_baseline: feat/upload-policy-reliability@7944a30
---

# ADR 구현 상태 점검과 다음 작업

## 점검 기준

ADR의 `accepted` 표기와 구현 완료를 분리한다. 구현 완료는 현재 기준 브랜치의 Java 코드·설정·프런트 JavaScript·테스트에서 확인되는 경우에만 표시한다.

이번 점검의 기준 코드는 `feat/upload-policy-reliability@7944a30`이다. 문서 전용 `docs` 브랜치의 `src`는 구현 근거로 사용하지 않는다. 이 기준 브랜치에는 MIME 검증·multipart 제한·파일 하나 검증·저장 루트 외부화·requestId 멱등 업로드·업로드 상태 전이·정책 감사 이력·다중 확장자 구간 차단이 병합되어 있다.

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
| [0003](0003-server-generated-file-storage-policy.md) | 구현 완료 | `LocalFileStorage`가 UUID와 최종 확장자로 설정된 저장 루트에 저장한다. |
| [0004](0004-use-extension-name-value-object.md) | 구현 완료 | `ExtensionName`이 정규화·기본 확장자 검증을 담당한다. |
| [0005](0005-limit-upload-to-known-non-executable-types.md) | 구현 완료 | Tika 콘텐츠 감지, 실행 MIME denylist, `UNKNOWN` MIME 허용, `BLOCKED_EXECUTABLE_MIME` 매핑과 테스트가 존재한다. 감지 실패 fail-close는 ADR 0018로 보완한다. |
| [0006](0006-persist-upload-file-name-mapping.md) | 구현 완료 | `UploadFile`에 원본 basename·서버 저장 파일명·상태를 영속화하고 원본 파일명 검증을 적용한다. |
| [0007](0007-use-final-file-extension-for-upload-blocking.md) | 대체됨 | 최종 확장자만 검사하던 정책을 ADR 0017이 대체한다. |
| [0017](0017-scan-all-extension-segments-for-upload-blocking.md) | 구현 완료 | `FileExtensionExtractor`가 모든 확장자 구간을 추출하고, 차단 구간을 왼쪽부터 검사하는 서비스·API 테스트가 존재한다. |
| [0018](0018-fail-closed-on-mime-detection-failure.md) | 구현 완료 | `FAILED` MIME 결과를 `FILE_TYPE_DETECTION_FAILED` 500 오류로 변환하고, 정책 조회·예약·저장 전에 거부하는 서비스·REST 테스트가 존재한다. |
| [0009](0009-limit-multipart-upload-size.md) | 구현 완료 | multipart 파일 10MB·전체 요청 12MB 설정과 `FILE_SIZE_EXCEEDED` 413 매핑이 존재한다. `file` part가 두 개 이상인 요청은 별도 `400 MULTIPLE_FILES_NOT_ALLOWED`로 저장 전에 거부한다. |
| [0010](0010-limit-extension-name-characters.md) | 부분 구현·보류 | 기존 정규화·20자·점 거부는 유지하고 허용 문자 강화는 보류한다. |
| [0011](0011-externalize-upload-storage-path.md) | 구현 완료 | `file.upload.storage-path` 설정을 `LocalFileStorage`에 주입하고 기본값 `./uploads`를 유지한다. |
| [0012](0012-preserve-policy-change-history-for-operations.md) | 구현 완료 | 정책 초기화·생성·상태 변경·삭제 이력과 원자성 테스트가 존재한다. 이벤트 후 상태는 `state` 하나로 저장한다. |
| [0013](0013-use-request-id-and-frontend-owned-upload-messages.md) | 구현 완료 | UUID v4 `Idempotency-Key`를 requestId로 검증하고 응답·오류·로그의 안전한 context에 연결한다. |
| [0014](0014-persist-upload-state-before-file-and-finalize-atomically.md) | 구현 완료 | `RECEIVING` 선저장, 임시 파일, atomic move, `COMPLETED`·`FAILED` 전환과 stale 복구가 존재한다. |
| [0015](0015-separate-upload-retry-idempotency-and-state.md) | 구현 완료 | 동일 requestId 결과 재사용, 처리 중 `409 + Retry-After`, FE 재시도 상한을 구현했다. requestId 만료·정리 로직은 현재 파일 업로드 범위에 포함하지 않는다. |
| [0016](0016-migrate-to-allowlist-when-policy-requires.md) | 의도적 보류 | `proposed` ADR이며 정책 집합·scope·allowlist·shadow 구현을 시작하지 않는다. |

## 현재 코드 기준 요약

현재 기준 브랜치에는 스프린트 2의 MIME 검증, MIME 감지 실패 fail-close, multipart 제한, 저장 루트 설정, 원본 파일명 메타데이터, requestId 멱등성·재시도, 업로드 상태 복구, 정책 감사 이력, 다중 확장자 구간 차단이 포함되어 있다. FE 오류 code 기반 한국어 매핑과 정책·업로드 실패 후 포커스 복귀도 구현했다. 브라우저 smoke의 MIME·413·정책 한도·반응형 주요 시나리오와 실제 `409 + Retry-After` API는 확인했으며, VoiceOver·실제 200% 확대·409 화면 주입은 대기 중이다. requestId 만료·정리는 현재 범위에서 제외한다.

통합 후 `./gradlew test`는 성공했다. MIME `UNKNOWN` fallback, `FAILED` 감지 거부 및 REST 오류 계약 테스트를 포함한다. 브라우저에서는 정상 `.txt` 업로드, 차단된 `env` 업로드, MIME·413 오류 안내, 20자·200개·201번째 등록, 320px·640px 유효 폭의 가로 overflow 부재를 확인했다. 동시 동일 requestId API에서는 `201`과 `409 + Retry-After`를 확인했다.

## 다음 작업과 선결 조건

1. IAB에서 재현이 어려운 `409` 화면 재시도 분기를 별도 수동 또는 결정적 주입 환경에서 확인한다. 현재 FE 분기와 API 계약은 존재하지만 실제 화면 확인 근거는 없다.
2. VoiceOver와 실제 브라우저 200% 확대 검증을 수행한다.
3. 브랜치 직접 커밋 경로와 최종 회귀 결과를 감사한다.
4. ADR 0016은 제품·운영 결정이 끝날 때까지 보류한다.
