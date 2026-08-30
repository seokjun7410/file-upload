# 스프린트 1 API 문서

> 상태: 정책 REST API와 파일 업로드 API 구현 완료
>
> 정책 API의 구현은 `ExtensionPolicyRestController`와 `ExtensionPolicyService`, 파일 업로드 API의 구현은 `FileUploadRestController`와 `FileUploadService`를 기준으로 한다.

## 공통 사항

- Base URL: `/api/v1`
- 확장자 입력: 한글·영문·숫자로만 구성된 문자열이며 점(`.`)·공백·기타 특수문자는 제외
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

- `400 Bad Request`: 빈 값 또는 20자 초과
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

업로드 파일의 확장자를 서버에서 확인한 뒤, 저장된 차단 정책을 적용한다.

### Request

`multipart/form-data`

| 이름 | 타입 | 필수 | 설명 |
|---|---|---:|---|
| `file` | MultipartFile | 예 | 업로드할 파일 1개 |

### 허용 Response `201 Created`

```json
{
  "filename": "550e8400-e29b-41d4-a716-446655440000.txt",
  "message": "파일 업로드가 완료되었습니다."
}
```

서버는 원본 파일명을 사용하지 않고 UUID와 정규화된 마지막 확장자로 생성한 이름을 `./uploads`에 저장한다. 예를 들어 `archive.TAR.GZ`는 `*.gz` 파일로 저장된다. `.bashrc`, `README`, `file.`처럼 확장자를 추출할 수 없는 파일은 거부한다.

### 차단 Response `422 Unprocessable Entity`

```json
{
  "code": "BLOCKED_EXTENSION",
  "message": "차단된 확장자(exe)는 업로드할 수 없습니다."
}
```

차단된 파일은 파일 저장을 수행하지 않는다.

### 오류

- `400 Bad Request`, `INVALID_FILE`: 파일이 없거나 비어 있거나 확장자를 추출할 수 없는 요청
- `422 Unprocessable Entity`: 차단 확장자
- `500 Internal Server Error`, `FILE_UPLOAD_FAILED`: 서버 저장 실패

## 공통 오류 형식

```json
{
  "code": "ERROR_CODE",
  "message": "사용자에게 표시할 오류 사유"
}
```

정책 API와 파일 업로드 API는 오류 상황에 따라 다음 `code`를 사용한다.

| 상황 | 상태 | code |
|---|---:|---|
| 빈 값·점 포함·20자 초과 | `400 Bad Request` | `INVALID_EXTENSION` |
| JSON 구조·필수 필드 오류 | `400 Bad Request` | `INVALID_REQUEST` |
| 이미 등록된 확장자 | `409 Conflict` | `DUPLICATE_EXTENSION` |
| 커스텀 200개 초과 | `409 Conflict` | `CUSTOM_LIMIT_EXCEEDED` |
| 정책 엔티티를 찾을 수 없음 | `404 Not Found` | `ENTITY_NOT_FOUND` |
| 업로드 파일 누락·빈 파일·무확장 파일 | `400 Bad Request` | `INVALID_FILE` |
| 차단된 확장자 파일 업로드 | `422 Unprocessable Entity` | `BLOCKED_EXTENSION` |
| 파일 저장 실패 | `500 Internal Server Error` | `FILE_UPLOAD_FAILED` |

## 구현 시 확인할 최소 규칙

- 정책 변경과 커스텀 등록·삭제 결과는 DB에 저장한다.
- `GET /extension-policies`는 DB의 최신 상태를 반환한다.
- 파일 업로드는 화면 검증에 의존하지 않고 서버에서 확장자를 다시 판정한다.
- 대소문자와 앞뒤 공백을 정규화한 뒤 중복 및 차단 여부를 판단한다.
- 파일 업로드는 원본 파일명을 저장 경로에 사용하지 않고 서버 생성 파일명을 사용한다.
