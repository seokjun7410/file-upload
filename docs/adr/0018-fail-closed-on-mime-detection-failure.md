---
status: accepted
---

# MIME 콘텐츠 감지 실패 시 업로드를 거부하는 정책

ADR 0005는 실행 가능한 MIME만 차단하고, Tika가 형식을 특정하지 못한 파일은 기존 확장자 정책으로 처리하도록 결정했다. 그러나 MIME 결과를 알 수 없는 경우와 MIME 분석 자체를 수행하지 못한 경우는 보안 의미가 다르다.

## 결정

- Tika가 분석을 완료하고 `application/octet-stream` 등 구체적인 형식을 특정하지 못한 경우에는 `UNKNOWN` 결과로 분류한다.
- `UNKNOWN` 결과는 기존 확장자 차단 정책을 계속 적용한다.
- Tika의 입력 스트림 획득·읽기·닫기 과정에서 `IOException`이 발생해 분석을 완료하지 못한 경우에는 `FAILED` 결과로 분류한다.
- `FAILED` 결과는 확장자 정책 조회, 업로드 예약, 파일 저장보다 먼저 업로드를 거부한다.
- 감지 실패 오류는 HTTP `500 Internal Server Error`, `FILE_TYPE_DETECTION_FAILED` 코드와 빈 `context`를 사용한다.
- 감지 실패의 내부 예외·MIME 상세값·원본 파일명·저장 경로·파일 내용은 외부 응답에 노출하지 않는다.
- 내부 로그에는 원인 예외와 업로드 `requestId`를 남겨 운영자가 실패를 추적할 수 있게 한다.
- MIME 감지 실패는 업로드 예약 전에 발생하므로 `UploadFile` 실패 레코드를 새로 만들지 않는다. 같은 `requestId`의 재요청은 감지를 다시 시도한다.

## 처리 순서

```text
파일 입력·확장자 추출
→ 콘텐츠 기반 MIME 감지
→ 감지 실패면 FILE_TYPE_DETECTION_FAILED
→ 실행 MIME이면 BLOCKED_EXECUTABLE_MIME
→ UNKNOWN MIME이면 경고 후 기존 확장자 정책으로 계속
→ 확장자 차단 정책 판정
→ 서버 생성 파일명으로 저장
```

## 이유

`application/octet-stream`은 분석이 성공했지만 형식 식별 결과가 불명확한 상태다. 반면 `IOException`은 콘텐츠 기반 보안 검증을 수행하지 못했다는 뜻이다. 두 결과를 동일하게 fallback 처리하면 MIME 검증 계층이 장애 상황에서 무력화되는 fail-open이 된다.

확장자 denylist, 콘텐츠 MIME, 서버 생성 파일명, 저장 위치 격리 등 다른 방어 계층은 계속 유지한다. 이 결정은 악성코드 검사나 MIME allowlist를 도입하는 결정이 아니다.

## 결과

- 정상적인 unknown MIME 파일의 업로드 사용성은 유지한다.
- MIME 분석이 불가능한 순간에는 검증되지 않은 콘텐츠가 저장되지 않는다.
- 감지 장애와 파일 저장 장애를 `FILE_TYPE_DETECTION_FAILED`와 `FILE_UPLOAD_FAILED`로 구분해 운영 지표와 프런트 안내를 분리할 수 있다.

## 기존 결정과의 관계

이 ADR은 [ADR 0005](0005-limit-upload-to-known-non-executable-types.md)의 실행 MIME 목록, unknown MIME 허용, 확장자·MIME 불일치 처리 결정을 유지한다. ADR 0005의 “감지 중 예외가 발생해도 계속 수행한다”는 처리만 이 ADR로 변경한다.
