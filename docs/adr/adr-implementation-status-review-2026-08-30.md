---
status: review
reviewed_at: 2026-08-30
implementation_baseline: main@0f6b53c + feat/upload-policy-reliability@2745a89
---

# ADR 구현 상태 점검과 즉시 구현 후보

## 점검 기준

이 문서는 ADR의 `accepted` 표기만으로 구현 완료를 판단하지 않는다. 2026-08-30 기준 `main@0f6b53c`의 Java 코드, `application.yml`, 프런트 JavaScript, 테스트와 `feat/upload-policy-reliability@2745a89`의 ADR 0011 구현을 대조했다.

현재 체크아웃은 문서 전용 `docs` 브랜치다. 이 브랜치의 `src`는 예제 애플리케이션이므로 구현 근거로 사용하지 않았고, 파일 업로드 기능이 존재하는 `main`을 기준으로 판정했다. `docs` 브랜치에서 아직 커밋되지 않은 ADR 초안은 문서 상태만 확인했다.

상태의 의미는 다음과 같다.

- `구현 완료`: ADR의 핵심 결정이 코드·설정에 존재하고 관련 테스트 근거가 있다.
- `부분 구현`: 일부 결정은 존재하지만, ADR이 요구하는 핵심 규칙 또는 계약이 빠져 있다.
- `미구현`: 현재 코드·설정·테스트에서 ADR의 구현 근거를 찾지 못했다.
- `의도적 보류`: `proposed` ADR이며, 문서가 현재 기능의 도입을 보류한다고 명시한다.
- `부분 구현·보류`: 기존 일부 동작은 유지하지만, 나머지 정책 구현은 필요성이 확인될 때까지 진행하지 않는다.

## ADR별 구현 상태

| ADR | 상태 | 확인한 근거 또는 미충족 사항 |
|---|---|---|
| [0001](0001-unify-extension-policies.md) | 구현 완료 | `ExtensionPolicy` 단일 엔티티·unique/`CHECK` 제약, fixed/custom 행위, 물리 삭제, quota 잠금과 동시성 테스트가 존재한다. |
| [0002](0002-use-server-policy-state-as-source-of-truth.md) | 구현 완료 | `extension-policy.js`가 고정 정책 PATCH 실패 뒤 전체 정책을 재조회하고, 재조회 실패 시 클릭 전 상태로 복구하며 조작을 잠근다. |
| [0003](0003-server-generated-file-storage-policy.md) | 구현 완료 | `LocalFileStorage`가 UUID와 최종 확장자로 `./uploads`에 저장하고, 업로드 예외 handler가 `INVALID_FILE`·`BLOCKED_EXTENSION`·`FILE_UPLOAD_FAILED`를 구분한다. |
| [0004](0004-use-extension-name-value-object.md) | 구현 완료 | `ExtensionName`이 정규화·기본 형식 검증을 담당하며 엔티티·서비스·저장소·파일 추출 흐름이 이 값을 사용한다. 허용 문자 강화는 후속 ADR 0010 항목이다. |
| [0005](0005-limit-upload-to-known-non-executable-types.md) | 구현 완료 | `tika-core` 기반 콘텐츠 MIME 감지, 실행 MIME denylist, 미확인 MIME 경고·허용, `BLOCKED_EXECUTABLE_MIME` 422 매핑과 위장 파일·텍스트·감지 실패 테스트가 존재한다(`b171051`). |
| [0006](0006-persist-upload-file-name-mapping.md) | 의도적 보류 | ADR이 `proposed`이며 `UploadFile` 엔티티·`p_upload_file` 테이블·원본 파일명 검증·메타데이터 조회가 없다. |
| [0007](0007-use-final-file-extension-for-upload-blocking.md) | 구현 완료 | `FileExtensionExtractor`가 basename의 마지막 점 뒤 확장자만 추출하고, 다중 점·확장자 없음 테스트가 이를 고정한다. |
| [0009](0009-limit-multipart-upload-size.md) | 구현 완료 | multipart 파일 10MB·전체 요청 12MB 설정과 `FILE_SIZE_EXCEEDED` 413 매핑, 설정·MockMvc·통합 테스트가 존재한다. |
| [0010](0010-limit-extension-name-characters.md) | 부분 구현·보류 | 공백 제거·소문자화·빈 값·20자·점 거부는 존재하지만, 한글·영문·숫자 전용 검증은 보류한다. 전체 원본 파일명 제한은 이 ADR의 대상이 아니다. |
| [0011](0011-externalize-upload-storage-path.md) | 구현 완료 | `file.upload.storage-path` 기본값을 선언하고 설정 주입으로 `LocalFileStorage` 저장 루트를 변경한다. 기본 경로·override 경로·저장 실패·기존 업로드 회귀 테스트가 통과했다(`2745a89`). |
| [0012](0012-preserve-policy-change-history-for-operations.md) | 미구현 | append-only 이력 엔티티·저장소·정책 변경과 같은 트랜잭션의 이력 저장이 없다. |
| [0013](0013-use-request-id-and-frontend-owned-upload-messages.md) | 미구현 | `ErrorResponse`는 `code`, `message`만 포함한다. `requestId`·안전한 `context`·구조화 로그·FE 오류 코드 메시지 매핑이 없다. |
| [0014](0014-persist-upload-state-before-file-and-finalize-atomically.md) | 미구현 | 업로드 메타데이터 상태가 없고, 파일을 임시 경로가 아닌 최종 경로에 직접 복사한다. atomic move·stale `RECEIVING` 복구도 없다. |
| [0015](0015-separate-upload-retry-idempotency-and-state.md) | 미구현 | `Idempotency-Key` 수신·요청 지문·멱등 결과 영속화·동일 키 결과 재사용·처리 중 응답·정리 작업이 없다. FE도 업로드 재시도를 구현하지 않는다. |
| [0016](0016-migrate-to-allowlist-when-policy-requires.md) | 의도적 보류 | ADR이 `proposed`이며 현재 전역 denylist를 유지한다고 명시한다. 정책 집합·조직/사용자 연결·allowlist 판정·shadow 평가 구현이 없다. |

## 구현하지 않은 ADR 목록

현재 구현 대상이 아닌 `proposed` ADR을 포함해, 아직 완료되지 않은 ADR은 다음과 같다.

- 미구현: 0012, 0013, 0014, 0015
- 부분 구현·보류: 0010
- 의도적 보류: 0006, 0016

## 하단 코멘트: 바로 구현해도 되는 후보와 선결 조건

### 보류 — ADR 0010 허용 문자 검증

현재 제품 요구에서 한글·영문·숫자 전용 제한의 필요성이 확인되지 않아 구현하지 않는다. 기존 빈 값·20자·점 검증과 `ExtensionName` 공유 구조는 유지한다. 보안상 필요한 경로·제어문자 검증이 별도로 요구되면 새로운 결정으로 다룬다.

ADR 0010의 한글·영문·숫자 전용 제한은 보류하므로 이 정책의 추가 길이·문자 집합 결정은 구현하지 않는다. 기존 확장자 검증 동작만 유지한다.

### 구현 완료 — ADR 0009 multipart 용량 제한

설정값 10MB/12MB와 `FILE_SIZE_EXCEEDED` 413 계약을 적용했고, `MaxUploadSizeExceededException` 매핑·설정·MockMvc·통합 테스트를 완료했다.

용량 초과 예외는 파일 업로드 도메인 예외의 넓은 `MultipartException` 처리에 가려지지 않도록 공통 handler에서 413으로 변환한다.

### 구현 완료 — ADR 0011 저장 루트 외부화

`file.upload.storage-path` 설정과 기본값 `./uploads`를 선언하고 명시적 생성자 주입으로 `LocalFileStorage`에 전달했다. 기본 경로·임시 경로 override·저장 실패·기존 업로드 회귀 테스트가 전체 `./gradlew test`에서 통과했다.

기존 `LocalFileStorage(Path)` 생성자와 직접 생성 테스트 경로는 유지했다. 기존 파일 이동·마이그레이션은 수행하지 않는다.

### 구현 완료 — ADR 0005 실행 MIME 차단

Tika 콘텐츠 감지와 애플리케이션 소유 실행 MIME denylist를 분리했다. `.txt`·`text/plain`은 허용하고, `application/octet-stream` 등 미확인 MIME와 감지 실패는 경고 로그 후 기존 확장자 정책으로 계속 처리한다. 실행 MIME은 `BLOCKED_EXECUTABLE_MIME` 422로 저장 전에 차단한다.

### 결정 또는 승인 후 착수 — ADR 0006, 0014, 0015 업로드 신뢰성 묶음

세 ADR은 하나의 작업 단위로 계획해야 한다. 0006은 아직 `proposed`이고, 0014는 `UploadFile` 메타데이터를 전제로 하며, 0015는 그 상태와 멱등 결과를 재사용해야 한다.

- 0006: `proposed`를 채택하거나 원본 파일명 영속화 요구를 철회해야 한다.
- 0014: stale `RECEIVING` 판정 시간, 복구 시 완료 처리와 실패·삭제의 기준, 임시 파일 위치·정리 주체를 정해야 한다.
- 0015: 처리 중 동일 키의 HTTP 상태와 `Retry-After`, 요청 지문 범위, 멱등 기록 보존 기간, 결과 조회 여부를 API 계약으로 정해야 한다.

이 선결 조건 없이 일부만 구현하면 최종 파일은 존재하지만 상태가 불명확한 경우, 또는 같은 재시도 요청의 중복 저장 문제가 남는다.

### 설계 보강 후 착수 — ADR 0012, 0013

0012의 이력 이벤트에는 변경 경로·요청 식별자·actor가 필요하고, 0013은 그 `requestId`의 생성·오류 응답·로그 필드를 정의한다. 두 ADR은 독립적으로도 구현할 수 있지만, 먼저 0013의 오류 응답 호환 기간과 로그 형식을 정하고 0012의 이벤트 action 목록·변경 전후 스냅샷 표현을 정하면 누락 없는 같은 트랜잭션 기록으로 구현할 수 있다.

### 현재 구현하지 않음 — ADR 0016

0016은 미래의 allowlist 전환을 위한 판단 기준이다. 조직·사용자 정책, shadow 평가, 운영 대시보드, 복귀 절차가 없는 현재 단계에서 구현을 시작하면 ADR이 금지한 즉시 전역 전환이 된다. 제품 요구와 운영 책임자가 확정될 때까지 보류한다.

## 추천 구현 순서

1. ADR 0009: 앞단 요청 자원 제한과 413 계약 추가 — 구현 완료
2. ADR 0011: 운영 설정 경계 분리 — 구현 완료(`2745a89`)
3. ADR 0005: 실행 MIME denylist와 오류 계약을 확정한 뒤 구현 — 구현 완료(`b171051`)
4. ADR 0013·0012: 요청 추적과 정책 감사 이력의 공통 식별자·로그 설계 확정 후 구현
5. ADR 0006을 채택한 뒤 0014·0015를 하나의 업로드 신뢰성 작업으로 설계·구현

0016은 위 구현 순서의 완료 조건이 아니다.
