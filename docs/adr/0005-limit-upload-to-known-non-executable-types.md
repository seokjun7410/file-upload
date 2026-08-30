---
status: accepted
---

# 실행 가능한 MIME만 차단하는 업로드 검증 정책

파일 확장자는 사용자가 변경할 수 있고 multipart의 `Content-Type`도 클라이언트가 전달하므로, 어느 하나만으로 콘텐츠의 실행 가능성을 판단할 수 없다. 그러나 MIME이 불명확하다는 이유로 모든 텍스트와 비실행 파일을 차단하면 정상적인 업로드 사용성이 불필요하게 낮아진다.

## 결정

- Apache Tika `tika-core`를 파일 콘텐츠 기반 MIME 감지에 사용한다.
- MIME 감지에는 원본 파일명과 multipart의 `Content-Type`을 사용하지 않고 파일 바이트 스트림만 사용한다.
- 애플리케이션은 실행 가능한 바이너리 MIME 카탈로그를 소유하며, 카탈로그에 포함된 MIME만 업로드에서 차단한다.
- 초기 실행 MIME 카탈로그는 `application/x-dosexec`, `application/x-msdownload`, `application/x-executable`, `application/x-elf`, `application/x-mach-binary`, `application/x-sharedlib`, `application/java-archive`, `application/x-java-archive`다.
- `.txt` 파일이 `text/plain`으로 감지되면 확장자 차단 정책을 통과하는 조건에서 업로드를 허용한다.
- `application/octet-stream` 등 미확인 MIME은 경고 로그만 남기고 업로드를 허용한다.
- Tika가 MIME을 감지하지 못하거나 감지 중 예외가 발생해도 경고 로그만 남기고 기존 확장자 정책과 저장 흐름을 계속 수행한다.
- 확장자와 MIME이 서로 다르다는 사실만으로는 차단하지 않는다.
- 기존 확장자 denylist는 유지한다. 예를 들어 `.exe` 확장자는 콘텐츠가 텍스트여도 기존 정책에 따라 차단될 수 있다.
- `text/javascript`, `text/x-shellscript` 등 텍스트로 표현되는 스크립트의 의미 분석은 수행하지 않는다. 텍스트 안의 명령어나 HTML 문자열도 MIME 차단 사유로 사용하지 않는다.
- 실행 MIME 차단 오류는 HTTP `422 Unprocessable Entity`와 `BLOCKED_EXECUTABLE_MIME` 코드를 사용한다. MIME 상세값·원본 파일명·저장 경로·파일 내용은 외부 응답에 노출하지 않는다.
- `requestId`와 안전한 `context`는 [ADR 0013](0013-use-request-id-and-frontend-owned-upload-messages.md)의 후속 계약을 따른다.
- 문서 Parser, 악성코드·바이러스 검사, 스크립트 의미 분석은 이 결정의 범위에 포함하지 않는다.

## 처리 순서

```text
파일 입력·최종 확장자 추출
→ 콘텐츠 기반 MIME 감지
→ 실행 MIME이면 BLOCKED_EXECUTABLE_MIME
→ 미확인 MIME·감지 실패면 경고 후 계속
→ 기존 확장자 차단 정책 판정
→ 서버 생성 파일명으로 저장
```

## 보안 경계와 한계

MIME 감지는 악성코드 검사나 실행 가능성의 완전한 증명이 아니다. 업로드 파일은 신뢰하지 않는 데이터로 취급하며, 이후 다운로드·미리보기·파싱 기능을 추가할 때 실행 경로 분리, `Content-Disposition: attachment`, `X-Content-Type-Options: nosniff`, HTML escape와 형식별 검증을 별도로 적용한다.

`.txt` 안에 스크립트 문법이나 HTML 문자열이 있어도 저장 단계에서는 차단하지 않는다. 해당 내용을 실행하거나 HTML로 렌더링하는 후속 기능이 추가될 때 별도 보안 결정을 만든다.

## 결과

- 확장자를 바꾼 대표적인 바이너리 실행 파일은 콘텐츠 MIME이 실행 카탈로그와 일치할 때 차단할 수 있다.
- 정상적인 `.txt`·`text/plain` 파일과 MIME이 불명확한 파일의 업로드 사용성을 유지한다.
- 미확인 MIME을 허용하므로 MIME 감지만으로 모든 우회 업로드를 막는 정책은 아니다.
- 실행 MIME 목록을 추가하거나 텍스트 스크립트를 차단하려면 별도 정책 변경과 테스트 fixture가 필요하다.

## 고려한 대안

### 알려진 비실행 MIME만 허용

보안 경계는 좁아지지만 `.txt`와 신규 정상 형식이 허용표에 없으면 업로드할 수 없다. 콘텐츠 형식이 다양하거나 텍스트 사용성이 중요한 현재 요구에는 과도한 거부가 발생한다.

### 미확인 MIME를 차단

우회 가능성은 줄어들지만 Tika가 `application/octet-stream`으로 감지하는 정상 파일까지 차단할 수 있다. 현재는 미확인 파일을 운영 로그로 관찰하고 실행 MIME 카탈로그를 보완하는 방식을 선택한다.

### Parser·악성코드 검사까지 도입

더 강한 검증을 제공하지만 격리, 타임아웃, 리소스 제한, 외부 검사 인프라가 필요하다. 후속 범위로 둔다.

## 참고

- [Apache Tika Java API](https://tika.apache.org/docs/4.0.x/using-tika/java-api/index.html)
- [Apache Tika Robustness](https://tika.apache.org/docs/4.0.x/advanced/robustness.html)
