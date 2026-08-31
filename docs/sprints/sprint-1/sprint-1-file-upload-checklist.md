# 스프린트 1 API별 FE/BE 구현 완료 체크리스트

> 상태: 스프린트 1 정책 관리·파일 업로드 구현 및 브라우저 검증 완료
>
> 기준 문서: [스프린트 1 확장자 차단 정책](sprint-1-file-upload-extension-policy.md), [스프린트 1 API 계약](sprint-1-file-upload-api.md)

현재 완료 범위는 `ExtensionPolicy` 단일 엔티티, fixed/custom 카탈로그 불변식, DB 제약, quota 기반 커스텀 최대 200개 등록, 고정 정책 초기화, 정책 REST API와 공통 오류 응답, Axios 기반 정책 관리·커스텀 삭제 화면, multipart 파일 저장과 업로드 통합이다.

## 1. 완료 판정 원칙

- [x] 각 API의 URL, HTTP 메서드, 요청 형식, 응답 형식, 상태 코드를 [API 계약 문서](sprint-1-file-upload-api.md)와 일치시킨다.
- [x] 각 체크박스는 코드와 자동화 테스트 또는 실제 화면 확인으로 근거를 남긴다.
- [x] FE와 BE가 동일한 정규화·오류 해석·상태 표시 규칙을 사용한다.
- [x] 아래 필수 항목을 모두 충족하고 `./gradlew test`와 `git diff --check`가 성공해야 스프린트 완료로 판정한다.

테스트는 [AGENTS.md의 테스트 유지보수 원칙](../../AGENTS.md)과 스프린트 설계 기준을 따른다. 현재 카탈로그 개수·순서는 원천 테스트에서만 고정하고, 통합·화면 테스트에서는 의미 기반 검증과 최소한의 시나리오를 사용한다.

## 2. `GET /api/v1/extension-policies` — 정책 조회

### API 계약

- 응답: `200 OK`
- `fixed`: 현재 고정 확장자 카탈로그의 `extension`, `blocked` 목록 (현재 기본 카탈로그는 7개이며 변경 가능)
- `custom`: 커스텀 차단 확장자 문자열 목록
- 커스텀 목록에는 고정 확장자를 포함하지 않는다.

### 내부 체크리스트

#### BE

- [x] DB의 최신 정책 상태를 반환한다.
- [x] 애플리케이션 최초 실행 시 고정 확장자 7개가 모두 `blocked: false`로 준비된다.
- [x] 커스텀 목록에 고정 확장자를 저장하거나 반환하지 않는다.
- [x] 정책 API 요청 처리는 `@RestController`로 구현하고 Thymeleaf view를 반환하지 않는다.

#### FE

- [x] 페이지 렌더링 시 Axios `GET /api/v1/extension-policies` 결과로 정책 목록·체크 상태·커스텀 목록을 구성한다.
- [x] 정책 화면 새로고침 시 서버의 최신 DB 상태를 다시 조회한다.
- [x] 조회 성공 결과를 화면에 표시한다.

#### 통합·테스트·수동 확인

- [x] 고정 확장자 변경과 커스텀 등록·삭제 직후 조회 결과에 최신 상태가 반영된다.
- [x] JPA 테스트로 정책 추가·조회와 초기화 후 상태 유지를 검증한다.
- [x] REST MockMvc 테스트로 조회 요청 형식, 성공 상태 코드, 응답 JSON을 검증한다.
- [x] 브라우저에서 정책 조회 요청의 URL, 메서드, 응답 상태 코드가 계약과 일치한다.

## 3. `PATCH /api/v1/extension-policies/fixed/{extension}` — 고정 확장자 차단 상태 변경

### API 계약

#### Request

```json
{
  "blocked": true
}
```

#### Response `200 OK`

```json
{
  "extension": "exe",
  "blocked": true
}
```

- 정책 엔티티를 찾을 수 없음: `404 Not Found`와 `ENTITY_NOT_FOUND`

### 내부 체크리스트

#### BE

- [x] 요청·응답을 API 계약에 맞는 DTO 또는 record로 표현한다.
- [x] 변경 결과를 DB에 저장하고 다음 조회에도 유지한다.
- [x] 고정 확장자가 아닌 입력을 `404`로 반환한다.
- [x] `Model`, redirect, 서버 렌더링용 화면 상태를 사용하지 않는다.

#### FE

- [x] 고정 확장자 체크·해제를 Axios `PATCH`로 저장한다.
- [x] Axios 요청 경로가 `/api/v1/extension-policies/fixed/{extension}`과 일치한다.
- [x] 응답의 `blocked` 상태를 화면에 반영한다.
- [x] `404`와 공통 오류 JSON의 `message`를 사용자 메시지로 표시한다.

#### 통합·테스트·수동 확인

- [x] 고정 확장자를 차단으로 변경한 직후 해당 확장자 파일 업로드가 거부된다.
- [x] 고정 확장자를 차단 해제한 뒤 해당 확장자 파일 업로드가 허용된다.
- [x] REST MockMvc 테스트로 요청 형식, 성공 상태 코드, 응답 JSON, `404` 오류 형식을 검증한다.
- [x] 체크·해제 후 브라우저를 새로고침해도 선택 상태가 유지된다.

## 4. `POST /api/v1/extension-policies/custom` — 커스텀 확장자 추가

### API 계약

#### Request

```json
{
  "extension": "sh"
}
```

#### Response `201 Created`

```json
{
  "extension": "sh"
}
```

- 빈 값·허용되지 않은 문자 또는 20자 초과: `400 Bad Request`, `INVALID_EXTENSION`
- 이미 등록된 확장자 또는 커스텀 200개 초과: `409 Conflict`
- 오류 응답은 공통 `{ "code": "ERROR_CODE", "message": "..." }` 형식을 따른다.

### 내부 체크리스트

#### BE

- [ ] 확장자(전체 원본 파일명이 아님)를 앞뒤 공백 제거 후 소문자로 정규화하고 점(`.`)을 제외한 형식으로 저장한다. 한글·영문·숫자 전용 제한은 ADR 0010 보류로 구현하지 않는다.
- [ ] 빈 값·20자 초과·중복·커스텀 200개 초과를 거부한다. 허용 문자 범위는 향후 별도 결정에서 재검토한다.
- [x] 고정 확장자를 커스텀으로 저장하거나 응답하지 않는다.
- [x] 중복은 `DUPLICATE_EXTENSION`, 한도 초과는 `CUSTOM_LIMIT_EXCEEDED`로 반환한다.

#### FE

- [x] 커스텀 확장자 추가를 Axios `POST /api/v1/extension-policies/custom`으로 호출한다.
- [x] 커스텀 확장자 입력창에 최대 20자 제한을 표시한다.
- [x] 추가 성공 뒤 전체 정책을 재조회해 결과를 목록과 사용자 메시지에 반영한다.
- [x] `400`, `409` 및 공통 오류 JSON의 `message`를 사용자 메시지로 표시한다.

#### 통합·테스트·수동 확인

- [x] 커스텀 확장자를 추가한 직후 해당 확장자 파일 업로드가 거부된다.
- [x] 대소문자와 앞뒤 공백이 있는 입력도 동일한 정규화 규칙으로 중복·차단 판정된다.
- [x] 도메인 테스트로 정규화, 길이, 중복, 최대 개수 규칙을 검증한다.
- [x] JPA 테스트로 커스텀 정책 저장과 DB 제약을 검증한다.
- [x] REST MockMvc 테스트로 성공·검증·중복·한도 초과 응답을 검증한다.
- [x] 추가 결과가 화면 목록과 DB에 동일하게 반영된다.

## 5. `DELETE /api/v1/extension-policies/custom/{extension}` — 커스텀 확장자 삭제

### API 계약

- 성공: `204 No Content`, 응답 본문 없음
- 등록되지 않은 커스텀 확장자: `404 Not Found`, `ENTITY_NOT_FOUND`

### 내부 체크리스트

#### BE

- [x] 삭제 성공 시 본문 없는 `204 No Content`를 반환한다.
- [x] 등록되지 않은 커스텀 확장자를 `404`로 반환한다.
- [x] 삭제 결과를 DB에 저장한다.

#### FE

- [x] 커스텀 확장자 삭제를 Axios `DELETE /api/v1/extension-policies/custom/{extension}`으로 호출한다.
- [x] 각 커스텀 확장자 항목 옆에 `X` 삭제 버튼을 표시한다.
- [x] 삭제 성공 시 목록에서 항목을 제거하고 완료 상태를 표시한다.
- [x] `404`와 공통 오류 JSON의 `message`를 사용자 메시지로 표시한다.

#### 통합·테스트·수동 확인

- [x] 커스텀 확장자를 삭제한 뒤 해당 확장자 파일 업로드가 허용된다.
- [x] REST MockMvc 테스트로 `204` 응답과 없는 항목의 `404` 오류를 검증한다.
- [x] 삭제 결과가 화면 목록과 DB에 동일하게 반영된다.

## 6. `POST /api/v1/files` — 파일 업로드

### API 계약

#### Request

- 형식: `multipart/form-data`
- 필드: `file` (`MultipartFile`, 필수, 파일 1개)

#### 허용 Response `201 Created`

```json
{
  "filename": "readme.txt",
  "message": "파일 업로드가 완료되었습니다."
}
```

#### 차단 Response `422 Unprocessable Entity`

```json
{
  "code": "BLOCKED_EXTENSION",
  "message": "차단된 확장자(exe)는 업로드할 수 없습니다."
}
```

- 파일 없음 또는 업로드할 수 없는 요청: `400 Bad Request`
- 차단 파일은 파일 저장을 수행하지 않는다.

### 내부 체크리스트

#### BE

- [x] 서버에서 업로드 파일의 확장자를 추출하고 저장된 정책으로 차단 여부를 다시 판정한다.
- [x] 파일명의 모든 확장자 구간을 순서대로 검사하고, 차단 구간이 하나라도 있으면 `BLOCKED_EXTENSION`을 반환한다.
- [x] `test.exefoo.pdf`처럼 차단 확장자와 정확히 일치하지 않는 구간은 허용한다.
- [x] 차단 파일은 파일 저장 로직을 실행하지 않고 `422`와 `BLOCKED_EXTENSION`을 반환한다.
- [x] 허용 파일은 `./uploads`에 서버 생성 파일명으로 저장하고 `201` 응답을 반환한다.
- [x] 파일 없음·빈 파일·무확장 파일을 `400 INVALID_FILE`로 반환하고 저장하지 않는다.

#### FE

- [x] 파일 선택·업로드를 `FormData`의 `file` 필드와 Axios `POST /api/v1/files`로 호출한다.
- [x] `400`, `422`, `500` 및 공통 오류 JSON의 `message`를 사용자 메시지로 표시한다.
- [x] 차단 업로드 실패 시 차단 확장자와 업로드 거부 사유를 표시한다.
- [x] 허용 업로드 성공 시 API가 반환한 파일명과 완료 메시지를 표시한다.
- [x] 화면 검증만으로 업로드 허용 여부를 결정하지 않는다.

#### 통합·테스트·수동 확인

- [x] 고정·커스텀 차단 정책이 별도 업로드 요청에 적용된다.
- [x] 차단된 파일은 업로드 전후 저장 위치에 새 파일이 생성되지 않는다.
- [x] 허용된 파일은 저장되고 성공 응답이 화면에 반영된다.
- [x] multipart 업로드 테스트로 고정·커스텀 차단, 허용 저장, 차단 파일 미저장을 검증한다.
- [x] 정책 변경 후 별도 업로드 요청에 변경된 정책이 적용되는지 검증한다.
- [x] 브라우저에서 업로드 URL, 메서드, `file` 필드, 응답 상태 코드를 확인한다.

## 7. 공통 API·화면·스프린트 검증

- [x] 모든 정책 API가 `/api/v1/extension-policies` Base URL 아래에 구현되어 있다.
- [x] 파일 업로드 API가 `/api/v1/files` Base URL 아래에 구현되어 있다.
- [x] 정책 API 성공 응답과 오류 응답의 본문·상태 코드가 API 문서와 일치한다.
- [x] 확장자 입력은 모든 API에서 앞뒤 공백 제거 후 소문자로 정규화하고 점(`.`)을 제외한 형식으로 처리한다.
- [x] 정책 API 공통 오류 응답의 `code`와 `message`가 계약에 맞게 일관된다.
- [x] 파일 업로드 API도 오류 상황별 상태 코드와 공통 오류 JSON을 계약에 맞게 반환한다.

### 공통 오류 형식

모든 오류 응답은 `{ "code": "ERROR_CODE", "message": "사용자에게 표시할 오류 사유" }` 형식을 따른다.

| 상황 | 상태 | code |
|---|---:|---|
| 빈 값·허용되지 않은 문자·점 포함·20자 초과 | `400 Bad Request` | `INVALID_EXTENSION` |
| JSON 구조·필수 필드 오류 | `400 Bad Request` | `INVALID_REQUEST` |
| 이미 등록된 확장자 | `409 Conflict` | `DUPLICATE_EXTENSION` |
| 커스텀 200개 초과 | `409 Conflict` | `CUSTOM_LIMIT_EXCEEDED` |
| 정책 엔티티를 찾을 수 없음 | `404 Not Found` | `ENTITY_NOT_FOUND` |

- [x] Thymeleaf는 화면 구조와 정적 초기 페이지만 제공하며 `Model` 데이터를 사용하지 않는다.
- [x] Thymeleaf 페이지 테스트로 화면이 정상 렌더링되고 서버 `Model` 데이터에 의존하지 않는지 검증한다.
- [x] 기존 `ExampleEntity`, `ExampleRepository`, `ExamplePageController`와 예제 저장 화면·엔드포인트·관련 테스트를 제거한다.
- [x] 기존 예제 제거 후 루트 페이지와 신규 파일 업로드 페이지가 정상적으로 열리고 애플리케이션이 기동된다.
- [x] 전체 테스트 실행 결과가 성공한다: `./gradlew test`
- [x] 변경 문서와 코드의 공백 오류 검사가 성공한다: `git diff --check`

## 8. 범위 외 확인

다음 항목은 스프린트 1 완료 판정에서 제외한다.

- [ ] MIME 타입·파일 내용·악성코드 검사
- [ ] 사용자별 또는 조직별 정책 분리
- [ ] 인증·인가 및 관리자 권한
- [ ] 파일 목록·다운로드·미리보기·업로드 이력
- [ ] 대용량 업로드 최적화 및 외부 오브젝트 스토리지
- [ ] 운영 환경 바이러스 검사와 보안 감사
- [ ] 고정 확장자 목록 자체의 사용자 편집
