# 파일 업로드 API 명세

> 상태: `feat/upload-policy-reliability` 기준 정책 REST API와 업로드 신뢰성 확장 계약 구현 완료. 브라우저 smoke는 후속 검증 대상이며 requestId 만료는 현재 범위 외
>
> 정책 API의 구현은 `ExtensionPolicyRestController`와 `ExtensionPolicyService`, 파일 업로드 API의 구현은 `FileUploadRestController`와 `FileUploadService`를 기준으로 한다.

## 공통 사항

- Base URL: `/api/v1`
- 확장자 입력: 앞뒤 공백을 제거하고 소문자로 정규화하며, 빈 값·20자 초과·점(`.`) 포함을 거부한다. 한글·영문·숫자 전용 제한은 적용하지 않는다.
- 확장자 비교: 앞뒤 공백 제거 후 소문자로 정규화
- 커스텀 확장자 최대 길이: 20자
- 커스텀 확장자 최대 개수: 200개
- 파일 업로드 요청: `multipart/form-data`

## 1. 확장자 차단 정책 조회

### `GET /extension-policies`

고정 확장자와 커스텀 확장자 차단 상태를 조회한다.

### Response `200 OK`

```json
{
  "fixed": [
    { "extension": "bat", "blocked": false },
    { "extension": "cmd", "blocked": true },
    { "extension": "com", "blocked": false },
    { "extension": "cpl", "blocked": false },
    { "extension": "exe", "blocked": true },
    { "extension": "js", "blocked": false },
    { "extension": "scr", "blocked": false }
  ],
  "custom": ["php", "sh"]
}
```

고정 확장자는 항상 7개를 반환하고, 커스텀 목록에는 고정 확장자를 포함하지 않는다.

## 2. 고정 확장자 차단 상태 변경

### `PATCH /extension-policies/fixed/{extension}`

고정 확장자의 차단 여부를 변경한다.

### Request

```json
{
  "blocked": true
}
```

### Response `200 OK`

```json
{
  "extension": "exe",
  "blocked": true
}
```

### 오류

- `404 Not Found`: 고정 확장자가 아닌 경우

## 3. 커스텀 확장자 추가

### `POST /extension-policies/custom`

커스텀 차단 확장자를 추가한다.

### Request

```json
{
  "extension": "sh"
}
```

### Response `201 Created`

```json
{
  "extension": "sh"
}
```

### 오류

- `400 Bad Request`: 빈 값·허용되지 않은 문자 또는 20자 초과
- `409 Conflict`: 이미 등록된 확장자 또는 커스텀 확장자 200개 초과

## 4. 커스텀 확장자 삭제

### `DELETE /extension-policies/custom/{extension}`

커스텀 차단 확장자를 삭제한다.

### Response `204 No Content`

응답 본문은 없다.

### 오류

- `404 Not Found`: 등록되지 않은 커스텀 확장자

## 5. 파일 업로드

### `POST /files`

업로드 파일의 확장자와 콘텐츠 MIME을 서버에서 확인한 뒤, 저장된 차단 정책을 적용한다. 요청에는 하나의 논리적 업로드를 식별하는 UUID v4 `Idempotency-Key` 헤더가 필요하며, 헤더 값은 응답의 `requestId`로 사용한다.

### Request

`multipart/form-data`

| 이름 | 타입 | 필수 | 설명 |
|---|---|---:|---|
| `file` | MultipartFile | 예 | 업로드할 파일 정확히 1개 |
| `Idempotency-Key` 헤더 | UUID v4 | 예 | 재시도 동안 유지할 논리적 업로드 ID |

### 허용 Response `201 Created`

```json
{
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "filename": "650e8400-e29b-41d4-a716-446655440000.txt",
  "message": "파일 업로드가 완료되었습니다."
}
```

서버는 원본 파일명을 사용하지 않고 UUID와 정규화된 마지막 확장자로 생성한 이름을 저장한다. 기본 저장 루트는 `./uploads`이며 `file.upload.storage-path` 설정으로 변경할 수 있다. 예를 들어 `archive.TAR.GZ`는 `*.gz` 파일로 저장된다. `.bashrc`, `README`, `file.`처럼 확장자를 추출할 수 없는 파일은 거부한다.

차단 여부는 basename의 첫 번째 점 이후에 있는 모든 확장자 구간을 파일명 순서대로 검사한다. 예를 들어 `test.exe.pdf`는 `exe`가 차단 정책에 등록되어 있으면 `pdf`가 허용 확장자여도 차단한다. `test.exefoo.pdf`처럼 확장자 구간이 정확히 일치하지 않는 경우에는 `exe` 정책으로 차단하지 않는다. 여러 구간이 차단되면 가장 왼쪽의 차단 확장자 하나를 `context.extension`에 반환한다.

업로드 MIME은 multipart 요청의 `Content-Type`이나 원본 파일명이 아니라 파일 콘텐츠에서 감지한다. 실행 가능한 MIME으로 판정되지 않은 파일은 기존 확장자 차단 정책을 통과하면 허용한다. `.txt`·`text/plain`은 허용하고, Tika가 분석을 완료했지만 형식을 특정하지 못한 `application/octet-stream` 등 `UNKNOWN` MIME은 경고 로그 후 업로드를 계속한다. Tika 분석 중 `IOException`이 발생한 `FAILED` 결과는 `FILE_TYPE_DETECTION_FAILED`로 거부한다.

### 차단 Response `422 Unprocessable Entity`

```json
{
  "code": "BLOCKED_EXTENSION",
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "context": {
    "extension": "exe"
  },
  "message": "차단된 확장자(exe)는 업로드할 수 없습니다."
}
```

차단된 파일은 파일 저장을 수행하지 않는다.

콘텐츠가 실행 가능한 MIME으로 감지된 경우에는 `BLOCKED_EXECUTABLE_MIME` 오류를 반환한다. 실행 MIME 상세값과 원본 파일명은 응답에 포함하지 않는다.

사용자에게 표시할 문장은 FE가 `code`와 `context`로 조립한다. 기존 호환을 위해 서버 `message`가 포함될 수 있지만, FE는 `message` 문자열을 분기 기준으로 사용하지 않는다.

### 오류

- `400 Bad Request`, `INVALID_FILE`: 파일이 없거나 비어 있거나 확장자를 추출할 수 없는 요청
- `400 Bad Request`, `MULTIPLE_FILES_NOT_ALLOWED`: `file` part가 두 개 이상인 요청
- `400 Bad Request`, `INVALID_REQUEST_ID`: `Idempotency-Key`가 없거나 UUID v4 형식이 아님
- `413 Payload Too Large`, `FILE_SIZE_EXCEEDED`: 파일 10MB 또는 multipart 전체 요청 12MB 초과
- `409 Conflict`, `IDEMPOTENCY_IN_PROGRESS`: 같은 `requestId`의 업로드가 처리 중이며 `Retry-After` 헤더를 함께 반환
- `422 Unprocessable Entity`, `BLOCKED_EXTENSION`: 파일명의 확장자 구간 중 하나 이상이 차단 정책에 포함됨. `context.extension`에는 가장 왼쪽의 차단 확장자 하나를 포함한다.
- `422 Unprocessable Entity`, `BLOCKED_EXECUTABLE_MIME`: 실행 가능한 MIME으로 감지된 파일
- `500 Internal Server Error`, `FILE_TYPE_DETECTION_FAILED`: 콘텐츠 MIME 분석 자체에 실패한 파일
- `500 Internal Server Error`, `FILE_UPLOAD_FAILED`: 서버 저장 실패

파일 용량 제한은 multipart 파싱 단계에서 적용되므로 확장자 정책·MIME·파일 저장 로직을 실행하지 않고 거부한다.

## 공통 오류 형식

```json
{
  "code": "ERROR_CODE",
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "context": {},
  "message": "사용자에게 표시할 오류 사유"
}
```

위 형식은 업로드 API의 오류 계약이다. 업로드 오류는 `code`, `requestId`, 안전한 `context`를 중심으로 하며, `message`는 호환 기간에만 유지한다. 정책 API는 기존 `message` 계약을 유지한다.

정책 API와 파일 업로드 API는 오류 상황에 따라 다음 `code`를 사용한다.

| 상황 | 상태 | code |
|---|---:|---|
| 빈 값·허용되지 않은 문자·점 포함·20자 초과 | `400 Bad Request` | `INVALID_EXTENSION` |
| JSON 구조·필수 필드 오류 | `400 Bad Request` | `INVALID_REQUEST` |
| 이미 등록된 확장자 | `409 Conflict` | `DUPLICATE_EXTENSION` |
| 커스텀 200개 초과 | `409 Conflict` | `CUSTOM_LIMIT_EXCEEDED` |
| 정책 엔티티를 찾을 수 없음 | `404 Not Found` | `ENTITY_NOT_FOUND` |
| 업로드 파일 누락·빈 파일·무확장 파일 | `400 Bad Request` | `INVALID_FILE` |
| 업로드 파일 두 개 이상 | `400 Bad Request` | `MULTIPLE_FILES_NOT_ALLOWED` |
| Idempotency-Key 누락·UUID v4 형식 오류 | `400 Bad Request` | `INVALID_REQUEST_ID` |
| 파일 10MB 초과 또는 전체 요청 12MB 초과 | `413 Payload Too Large` | `FILE_SIZE_EXCEEDED` |
| 같은 requestId 처리 중 재요청 | `409 Conflict` | `IDEMPOTENCY_IN_PROGRESS` |
| 차단된 확장자 파일 업로드 | `422 Unprocessable Entity` | `BLOCKED_EXTENSION` |
| 실행 가능한 MIME 파일 업로드 | `422 Unprocessable Entity` | `BLOCKED_EXECUTABLE_MIME` |
| 콘텐츠 MIME 감지 실패 | `500 Internal Server Error` | `FILE_TYPE_DETECTION_FAILED` |
| 파일 저장 실패 | `500 Internal Server Error` | `FILE_UPLOAD_FAILED` |

## 구현 시 확인할 최소 규칙

- 정책 변경과 커스텀 등록·삭제 결과는 DB에 저장한다.
- `GET /extension-policies`는 DB의 최신 상태를 반환한다.
- 파일 업로드는 화면 검증에 의존하지 않고 서버에서 확장자를 다시 판정한다.
- 대소문자와 앞뒤 공백을 정규화한 뒤 중복 및 차단 여부를 판단한다.
- 파일 업로드는 원본 파일명을 저장 경로에 사용하지 않고 서버 생성 파일명을 사용한다.
- MIME 검증은 파일 콘텐츠를 기준으로 수행하고 실행 MIME만 차단한다.
- multipart 파일 10MB·전체 요청 12MB 제한은 파싱 단계에서 적용한다.
- 파일 업로드 API는 `file` part를 정확히 하나만 허용하며, 두 개 이상이면 저장 전에 거부한다.
- 같은 논리적 업로드의 재시도는 동일한 `Idempotency-Key`를 사용하고 서버는 결과를 재사용한다.
- 업로드 상태는 `RECEIVING`·`COMPLETED`·`FAILED`로 영속화하며 최종 파일은 atomic move로 확정한다.
