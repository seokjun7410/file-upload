# 스프린트 1 API 문서

> 상태: 설계 문서
>
> 현재 프로젝트에는 아래 API가 아직 구현되어 있지 않다.

## 공통 사항

- Base URL: `/api/v1`
- 확장자 입력: 점(`.`)을 제외한 문자열
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
    { "extension": "scr", "blocked": false },
    { "extension": "js", "blocked": false }
  ],
  "custom": ["sh", "php"]
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
  "filename": "readme.txt",
  "message": "파일 업로드가 완료되었습니다."
}
```

### 차단 Response `422 Unprocessable Entity`

```json
{
  "code": "BLOCKED_EXTENSION",
  "message": "차단된 확장자(exe)는 업로드할 수 없습니다."
}
```

차단된 파일은 파일 저장을 수행하지 않는다.

### 오류

- `400 Bad Request`: 파일이 없거나 업로드할 수 없는 요청
- `422 Unprocessable Entity`: 차단 확장자

## 공통 오류 형식

```json
{
  "code": "ERROR_CODE",
  "message": "사용자에게 표시할 오류 사유"
}
```

## 구현 시 확인할 최소 규칙

- 정책 변경과 커스텀 등록·삭제 결과는 DB에 저장한다.
- `GET /extension-policies`는 DB의 최신 상태를 반환한다.
- 파일 업로드는 화면 검증에 의존하지 않고 서버에서 확장자를 다시 판정한다.
- 대소문자와 앞뒤 공백을 정규화한 뒤 중복 및 차단 여부를 판단한다.
