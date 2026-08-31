# AI 활용 기록

이 문서는 제출용 AI 활용 기록이다. 프롬프트·AI 제안·사용한 skill/plugin·사람의 판단·검증 결과·회고를 사건 발생 시각 기준으로 누적한다.

작성 규칙은 [`docs/ai-usage-guidelines.md`](docs/ai-usage-guidelines.md)를 따른다. 각 제목은 `YYYY-MM-DDTHH:MM:SS+09:00` ISO-8601 형식으로 시작하므로 문자열 정렬만으로 시간순 정렬할 수 있다. 새 사건은 맨 아래에 추가하고, 뒤늦게 발견한 과거 사건은 시간 근거를 밝힌 뒤 올바른 위치에 삽입한다.

## 2026-08-28T14:45:00+09:00 — Spring Boot 최소 프로젝트 구성 확정

- 상태: 수정 채택
- 시간 근거: Gradle Wrapper 생성과 애플리케이션 기동 로그의 실행 시각.
- 스프린트/범위: 빈 저장소에 Java 21 기반 Spring Boot JPA 시작 프로젝트 구성
- 관련 문서·코드: [`build.gradle`](build.gradle), [`application.yml`](src/main/resources/application.yml), [`DemoApplication`](src/main/java/com/example/demo/DemoApplication.java), [`ExampleEntity`](src/main/java/com/example/demo/domain/ExampleEntity.java), [`ExampleRepository`](src/main/java/com/example/demo/domain/ExampleRepository.java)
- 요청·질문 요약: Gradle Groovy DSL, Java 21, JPA를 사용하는 최소 Spring Boot 프로젝트를 생성한다.
- 배경과 제약: 작업 폴더에는 Git 메타데이터만 있었고 Java 21은 설치되어 있었다. 실제 도메인 기능과 불필요한 인프라는 시작 단계에서 제외한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `create-jpa-domain`
  - plugin/도구: 공식 Spring 문서 검색, 저장소 파일 검색·편집, Gradle Wrapper, Gradle 테스트
- AI 제안: Spring Boot 최신 안정 계열과 3.5 계열, H2 메모리 DB와 파일 DB, 웹 계층 포함 범위를 질문으로 나눠 결정하도록 제안했다. JPA 매핑 검증용 `ExampleEntity`와 Repository 테스트도 제안했다.
- 사람의 판단과 이유: 수정 채택. 사용자는 호환성 우선으로 Spring Boot 3.5 계열, H2 파일 DB, `demo / com.example.demo`, Groovy DSL, Web·JPA·Validation·Thymeleaf, `application.yml`, JPA 예제를 선택했다. Lombok과 추가 도메인 공통 계층은 최소 구성 요구에 따라 제외했다.
- 코드·사용자 경험 영향: 실행 가능한 Gradle 프로젝트와 H2 파일 DB가 생겼고, 이후 JPA 도메인 기능을 추가할 기본 패키지가 마련됐다.
- 검증 근거: `./gradlew clean test` 성공. 기본 포트 8080은 기존 프로세스가 사용 중이어서 18080으로 애플리케이션을 기동했고, H2 연결 및 `data/demo.mv.db` 생성을 확인했다.
- 결과와 연결 커밋: 초기 파일은 `65d1ee9 init`에 포함됐다.
- 회고와 후속 조치: 시작 단계의 선택은 사용자 답변으로 고정했으며, 실제 파일 업로드 도메인과 API는 별도 요구사항으로 다룬다.

## 2026-08-28T14:52:00+09:00 — 루트 테스트 페이지 추가

- 상태: 수정 채택
- 시간 근거: Thymeleaf 페이지 추가 직후 실행한 Gradle 테스트·bootRun 로그의 실행 시각.
- 스프린트/범위: JPA 저장·조회 결과를 브라우저에서 확인하는 최소 Thymeleaf 화면
- 관련 문서·코드: [`ExamplePageController`](src/main/java/com/example/demo/web/ExamplePageController.java), [`index.html`](src/main/resources/templates/index.html), [`ExamplePageControllerTests`](src/test/java/com/example/demo/web/ExamplePageControllerTests.java)
- 요청·질문 요약: `localhost/` 기준으로 DB 조회를 확인할 수 있는 테스트 페이지를 추가한다.
- 배경과 제약: REST API나 실제 화면 설계가 아닌 개발용 확인 화면이 필요했다. 기존 JPA Repository를 재사용하고 별도 서비스 계층은 추가하지 않았다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: 저장소 파일 편집, Gradle 테스트, 로컬 HTTP 요청 검증
- AI 제안: 루트 GET으로 목록을 보여주고, 테스트 데이터 생성을 위해 이름 저장 POST 폼을 함께 제공하는 최소 화면을 제안했다.
- 사람의 판단과 이유: 수정 채택. 조회 검증을 쉽게 하려면 테스트 데이터 입력 경로도 필요하다고 판단해 저장 폼을 포함했다. 실제 사용자용 기능이나 REST API로 확장하지 않았다.
- 코드·사용자 경험 영향: `/`에서 저장된 Entity 목록을 볼 수 있고, 이름을 입력해 H2 파일 DB에 저장할 수 있다. 빈 이름은 오류 메시지로 표시한다.
- 검증 근거: `./gradlew clean test` 성공. 애플리케이션을 18080 포트에서 기동한 뒤 `curl http://localhost:18080/`로 Thymeleaf HTML 렌더링을 확인했다.
- 결과와 연결 커밋: 초기 구현은 `65d1ee9 init`에 포함됐다.
- 회고와 후속 조치: 테스트 페이지는 개발 보조 화면으로 유지하고, 실제 파일 업로드 화면은 확정된 스프린트 API 계약에 맞춰 별도 구현한다.

## 2026-08-28T17:03:53+09:00 — 확장자 정책 모델링 선택지와 ADR 전 검토 문서화

- 상태: 검토 중
- 시간 근거: ADR 전 검토 문서 커밋 `33c3012`의 작성 시각. 구현 편향 제거는 후속 커밋 `5e62b98`의 2026-08-28T17:15:26+09:00 시각으로 확인했다.
- 스프린트/범위: fixed/custom 정책의 데이터 모델과 검증 책임 결정 전 검토
- 관련 문서·코드: [`sprint-1-extension-policy-modeling-options.md`](docs/questions/sprint-1-extension-policy-modeling-options.md), `adr-predecision-review` skill
- 요청·질문 요약: fixed와 custom을 각각 엔티티로 둘지 하나의 엔티티와 유형으로 관리할지, 그 외 선택지까지 구체적 예시와 장단점으로 비교하되 최종 결정은 보류한다.
- 배경과 제약: 현재 구현과 변경 비용은 초안 단계의 판단 근거에서 제외하고, 데이터 의미·불변식·향후 행위 규칙을 중심으로 비교해야 했다. 유효성 검증 책임 객체는 validator 패키지와 postfix 클래스명으로 컨벤션화하는 요구도 있었다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `adr-predecision-review`, `skill-creator`
  - plugin/도구: 저장소 문서 검색·편집, Git 문서 커밋
- AI 제안: fixed/custom 분리 엔티티, 단일 정책 엔티티와 유형, 기본 카탈로그와 사용자 정책을 분리하는 모델을 데이터 중복·불가능 상태·쿼리·확장성 관점에서 비교하고 추천 후보는 제시하되 결론은 비워 두도록 제안했다.
- 사람의 판단과 이유: 수정 채택. 사용자는 구현 편의나 기존 코드 변경 부담이 모델 선택을 선행하지 않도록 문서를 수정하고, ADR 결정 전 원문을 재사용 가능한 skill로 만들도록 했다.
- 코드·사용자 경험 영향: 이 단계에서는 코드 변경 없이 이후 ADR과 도메인 구현이 검토 문서의 용어와 불변식 비교를 따르게 됐다.
- 검증 근거: 검토 문서가 최종 결정을 포함하지 않는지, 구현 현황을 선택 근거로 사용하지 않는지 diff로 확인했다.
- 결과와 연결 커밋: `33c3012 docs: record extension policy modeling options`, `5e62b98 docs: remove implementation bias from design review`
- 회고와 후속 조치: 추천안과 확정 결정을 구분하고, 사용자 결정이 내려진 뒤에만 ADR과 코드를 갱신한다.

## 2026-08-28T17:27:04+09:00 — fixed/custom 단일 정책 엔티티 ADR 확정

- 상태: 채택
- 시간 근거: ADR 커밋 `3c1da81`의 작성 시각
- 스프린트/범위: 확장자 차단 정책의 영속 모델과 유형별 행위 책임
- 관련 문서·코드: [`0001-unify-extension-policies.md`](docs/adr/0001-unify-extension-policies.md), [`sprint-1-extension-policy-modeling-options.md`](docs/questions/sprint-1-extension-policy-modeling-options.md)
- 요청·질문 요약: 앞서 제시한 사용자의 개인 의견은 결정문 근거로 사용하지 않고, fixed와 custom이 모두 정규화된 확장자에 대한 차단 정책이라는 공통 의미를 가진다는 문장으로 ADR을 확정한다.
- 배경과 제약: fixed는 기본 정책의 차단 상태 변경, custom은 등록·삭제로 차단 여부를 표현하지만 두 유형은 독립 데이터 개념이 아니라 정책 유형이다. 유형별 행위는 WAS가 검증하고 중복·필수값 같은 구조적 무결성은 DB가 보장해야 했다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `adr-predecision-review`
  - plugin/도구: 문서 편집, Git diff·커밋·병합
- AI 제안: 검토 문서의 선택지를 ADR의 맥락으로 연결하고, 단일 `ExtensionPolicy`와 `policy_type`을 결정으로 기록하며 유형별 행위와 구조적 무결성의 책임을 분리하도록 제안했다.
- 사람의 판단과 이유: 채택. 사용자는 fixed/custom을 별도 엔티티로 분리하지 않고 하나의 정책 엔티티로 합치며, 확장자 중복과 필수값은 DB 제약으로 보장하도록 확정했다.
- 코드·사용자 경험 영향: 이후 도메인은 `extension`, `policyType`, `blocked`를 한 행에 저장하고 API가 유형별 응답으로 재구성하는 방향으로 고정됐다.
- 검증 근거: ADR 상태가 accepted인지, 결정문이 사용자 확정 문장과 일치하는지 문서 diff로 확인했다.
- 결과와 연결 커밋: `3c1da81 docs: accept unified extension policy model`
- 회고와 후속 조치: 구현 전에 DB에서 막아야 하는 상태 조합과 WAS에서 검증할 카탈로그 규칙을 테스트로 구체화한다.

## 2026-08-28T19:07:41+09:00 — 정책 도메인 구현과 독립 리뷰 누락 보완

- 상태: 수정 채택
- 시간 근거: 최초 기능 커밋 `c5e2df1`의 작성 시각. 해당 커밋은 이후 이력 정리로 `ad915e6`으로 대체됐다.
- 스프린트/범위: 작업 순서 1~3단계의 단일 정책 엔티티, JPA, 초기화, 커스텀 quota, 예제 제거
- 관련 문서·코드: [`ExtensionPolicy`](src/main/java/com/example/demo/domain/ExtensionPolicy.java), [`ExtensionPolicyQuota`](src/main/java/com/example/demo/domain/ExtensionPolicyQuota.java), [`ExtensionPolicyService`](src/main/java/com/example/demo/service/ExtensionPolicyService.java), 도메인·JPA·서비스 테스트
- 요청·질문 요약: 기존 논란 코드를 제거하고 ADR 기준으로 처음부터 구현한 뒤, 별도 세션 리뷰에서 제기된 soft delete 충돌, 카탈로그 불변식, 최대 200개 동시성, `policy_type` DB 제약, null·unique·정규화 경계 테스트 누락을 모두 수정한다.
- 배경과 제약: custom은 항상 차단 상태여야 하고 물리 삭제해야 한다. 고정 확장자는 정의된 7개만, custom은 고정 목록 외 값만 허용해야 하며 동시 등록에서도 200개를 넘으면 안 됐다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경과 별도 Codex 리뷰 세션
  - skill: 명시적 사용 기록 없음
  - plugin/도구: JPA/H2, Gradle 테스트, Git diff·커밋
- AI 제안: 단일 엔티티에 유형·상태 DB `CHECK`를 두고, fixed/custom 카탈로그는 도메인에서 검증하며, 별도 quota 행을 비관적 잠금해 동시 등록 한도를 보장하도록 제안했다.
- 사람의 판단과 이유: 수정 채택. 사용자는 리뷰에서 발견한 다섯 누락을 모두 구현·테스트에 포함하고, soft delete 대신 물리 삭제 계약을 ADR에 명시하도록 했다.
- 코드·사용자 경험 영향: 불가능 상태와 잘못된 유형 값은 DB에서도 거부되고, 커스텀 최대 개수는 동시 요청에도 유지된다. 기존 Example 기능은 제거되고 서버 `Model`에 정책을 싣지 않는 최소 페이지로 교체됐다.
- 검증 근거: 도메인 규칙, DB `CHECK`·null·unique, 초기화 상태 유지, quota 동시성 테스트와 전체 `./gradlew test`, `git diff --check`가 성공했다.
- 결과와 연결 커밋: 최종 기능 커밋 `ad915e6 feat: implement extension policy domain`; ADR 보완 `b12de60 docs: record extension policy database invariants`
- 회고와 후속 조치: 구현 직후에는 도메인 테스트뿐 아니라 DB 직접 삽입과 동시성 경계까지 별도 리뷰 체크리스트로 확인한다.

## 2026-08-28T20:16:16+09:00 — 정책 API 요청 검증 보완

- 상태: 수정 채택
- 시간 근거: 코드 리뷰 수정 작업 직전 확인한 시스템 시각과 테스트 실행 순서
- 스프린트/범위: 정책 REST API 요청 검증
- 관련 문서·코드: [`CustomExtensionPolicyRequest`](src/main/java/com/example/demo/web/dto/CustomExtensionPolicyRequest.java), [`ExtensionPolicyRestControllerTests`](src/test/java/com/example/demo/web/ExtensionPolicyRestControllerTests.java)
- 요청·질문 요약: 커스텀 정책 등록 요청의 필수 필드 검증을 보완한다.
- 배경과 제약: 필수 필드 누락은 `INVALID_REQUEST`여야 하며, 공백 문자열은 기존 계약대로 `INVALID_EXTENSION`으로 유지한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `tdd`
  - plugin/도구: 적용 패치, Gradle 테스트, Git diff 검사
- AI 제안: 누락 필드 행동을 MockMvc 테스트로 먼저 고정한 뒤 DTO에 `@NotNull`을 추가한다.
- 사람의 판단과 이유: 수정 채택. 필드 누락과 값 형식 오류를 구분해야 API 클라이언트가 오류를 일관되게 해석할 수 있다.
- 코드·사용자 경험 영향: `{}` 커스텀 등록 요청은 `400 INVALID_REQUEST`와 공통 오류 JSON을 반환한다.
- 검증 근거: 새 테스트는 수정 전 `NullPointerException`으로 실패했고, `@NotNull` 적용 후 대상 테스트와 전체 `./gradlew test --rerun-tasks`가 성공했다. `git diff --check HEAD`와 변경 파일의 trailing whitespace 검사도 성공했다.
- 결과와 연결 커밋: 정책 REST API 구현에 반영했다.
- 회고와 후속 조치: DTO에 제약이 없는 요청 record를 추가할 때는 누락·null·공백을 각각 계약에 맞게 구분하는 테스트를 함께 작성한다.

## 2026-08-28T21:36:53+09:00 — Axios 정책 조회 화면 구현 계획 승인

- 상태: 채택
- 시간 근거: 사용자가 정책 조회 화면 구현 계획의 실행을 요청한 직후 확인한 시스템 시각
- 스프린트/범위: `GET /api/v1/extension-policies` Axios 화면·최신 DB 상태 통합 검증·브라우저 확인
- 관련 문서·코드: [`sprint-1-file-upload-checklist.md`](docs/sprints/sprint-1/sprint-1-file-upload-checklist.md), `index.html`, `extension-policy.js`, `FileUploadPageControllerTests`, `ExtensionPolicyApiIntegrationTests`
- 요청·질문 요약: 이미 구현된 정책 조회 BE를 유지하고 Axios WebJar로 화면을 조립하며, 정책 변경·등록·삭제 후 GET의 최신 상태와 실제 브라우저 요청을 검증한다.
- 배경과 제약: 이번 범위는 GET 화면으로 한정하고 PATCH·POST·DELETE 화면 조작과 파일 업로드는 추가하지 않는다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `next-work-briefing`, `tdd`, `korean-domain-test-policy`, `browser:control-in-app-browser`
  - plugin/도구: Maven Central 의존성 확인, MockMvc, Gradle 테스트, Git 브랜치·경로 감사, 인앱 브라우저
- AI 제안: Axios 1.8.4를 WebJar로 고정하고 정적 JavaScript가 페이지 로드마다 GET을 한 번 호출하여 fixed 체크 상태, custom 목록, 로딩·성공·오류 상태를 표시한다. 실제 HTTP 변경 후 GET은 격리 H2 통합 테스트로 검증한다.
- 사람의 판단과 이유: 채택. 사용자가 제안된 계획 전체를 명시적으로 구현하도록 요청했다.
- 코드·사용자 경험 영향: 루트 페이지가 서버 Model 데이터 대신 정책 REST API를 조회해 고정·커스텀 정책을 표시한다. GET 전용 단계의 체크박스는 비활성화한다.
- 검증 근거: 페이지 테스트는 화면 영역 없음으로 Red를 확인한 뒤 Green으로 전환했다. 격리 H2를 사용한 API 통합 테스트로 초기 GET, PATCH·POST·DELETE 후 GET의 최신 상태를 확인했다. `./gradlew test --rerun-tasks`, `node --check`, `git diff --check`가 성공했다. 인앱 브라우저에서 고정 7개·빈 custom 목록, `exe` 변경·`sh` 등록·삭제 후 새로고침 결과를 확인했고 Tomcat access log에 `GET_/api/v1/extension-policies_200`이 남았다.
- 결과와 연결 문서: 코드 커밋 `2f2bc22 feat: implement extension policy query screen`, [`sprint-1-file-upload-checklist.md`](docs/sprints/sprint-1/sprint-1-file-upload-checklist.md).
- 회고와 후속 조치: 브라우저 도구의 네트워크 상세 조회 한계를 화면 DOM과 서버 access log를 함께 확인해 보완했다.

## 2026-08-28T22:16:13+09:00 — 정책 조회 테스트 리뷰 의견 반영

- 상태: 수정 채택
- 시간 근거: 사용자가 `ExtensionPolicyApiIntegrationTests` 와 `FileUploadPageControllerTests`의 단언 범위와 인수 테스트 수를 지적한 직후 시스템 시각
- 스프린트/범위: `GET /api/v1/extension-policies` 통합 테스트의 경직된 카탈로그 단언과 화면 정적 HTML 단언 축소
- 관련 문서·코드: `ExtensionPolicyApiIntegrationTests`, `FileUploadPageControllerTests`, `index.html`
- 요청·질문 요약: 고정 확장자 7개 개수와 배열 인덱스에 의존한 테스트를 점검하고, 인수 테스트가 너무 많아지지 않도록 축소하며, 페이지 HTML 단언의 세부성을 줄인다.
- 배경과 제약: 고정 카탈로그는 향후 개수가 변할 수 있고, 정책 API 통합 경로는 최신 DB 반영을 확인해야 한다. 페이지의 Axios 버전·스크립트 URL은 정적 템플릿의 핵심 행동이 아니며 브라우저 스모크에서 실제 로드를 검증할 수 있다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `tdd`, `korean-domain-test-policy`
  - plugin/도구: 테스트 전체 구조 검색, Gradle 테스트, Git diff
- AI 제안: 초기 정책은 고정 목록 비어 있지 않음·모든 항목 미차단만 검증하고, 변경 시나리오는 하나의 통합 테스트로 운영하며, 페이지 테스트는 JS 식별자에 필요한 컨테이너만 확인하도록 축소한다.
- 사람의 판단과 이유: 수정 채택. 개수와 인덱스는 향후 카탈로그 변경에 정당한 실패를 발생시키므로 제거한다. 통합 테스트는 최신 조회 결과를 사용자 관점에서 검증하되 초기 상태와 변경 흐름을 각각 한 테스트로 남겨 진단성을 유지한다. HTML 테스트는 조회 로직을 대체하지 않는 최소 컨테이너 계약만 검증한다.
- 코드·사용자 경험 영향: 카탈로그 개수 추가·삭제가 통합 테스트를 불필요하게 깨뜨리지 않고, 실제 정책 조회 상태와 스크립트의 핵심 DOM 계약은 유지된다.
- 검증 근거: 수정 후 대상 테스트와 전체 `./gradlew test --rerun-tasks`, `git diff --check`, `node --check`를 실행해 모두 성공했다. 수정 전에도 기존 전체 테스트가 성공했고, 리뷰 수정 후 통합 테스트는 2개 테스트로 축소된 상태로 통과했다.
- 결과와 연결 문서: `416da6e refactor: reduce policy query test coupling`, 기존 기능 커밋 `2f2bc22`, 문서 병합 `aa98714`.
- 회고와 후속 조치: 정책 테스트에서 정책 카탈로그의 사업 규모를 확정하는 단언과 행동 계약을 구분하고, 실제 화면 검증으로 스크립트 동작을 보완한다.

## 2026-08-28T22:57:34+09:00 — 고정 확장자 PATCH 화면 연동 결정

- 상태: 채택
- 시간 근거: 구현 시작 전 시스템 시각과 사용자 승인 메시지
- 스프린트/범위: `PATCH /api/v1/extension-policies/fixed/{extension}` 화면 연동
- 관련 문서·코드: [`sprint-1-fixed-policy-change-screen-options.md`](docs/questions/sprint-1-fixed-policy-change-screen-options.md), [`0002-use-server-policy-state-as-source-of-truth.md`](docs/adr/0002-use-server-policy-state-as-source-of-truth.md), `extension-policy.js`
- 요청·질문 요약: 고정 확장자 체크박스를 Axios PATCH와 연결하고 실패 시 서버 상태로 복구하며 새로고침 유지까지 검증한다.
- 배경과 제약: PATCH 백엔드 계약과 자동화 테스트는 이미 구현되어 있다. 파일 업로드와 새 JavaScript 테스트 도구는 이번 범위에서 제외한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `tdd`, `browser:control-in-app-browser`
  - plugin/도구: Gradle 기준선 테스트, Git docs worktree, 실제 브라우저 smoke 예정
- AI 제안: 요청 중 모든 고정 체크박스를 잠그고, 성공 시 응답을 최종 상태로 사용하며, 실패 시 공통 오류 메시지를 보존한 채 전체 정책을 재조회한다. 재조회도 실패하면 클릭 전 상태를 복원하고 새로고침을 안내한다.
- 사람의 판단과 이유: 채택. 사용자는 PATCH 화면 연동만 구현하고 실패 시 전체 정책 재조회, 현재 테스트 도구 유지를 선택했다.
- 코드·사용자 경험 영향: 고정 확장자 체크·해제가 DB에 저장되고 오류 발생 시 화면이 서버 상태로 재동기화된다. 파일 업로드 판정에는 아직 영향을 주지 않는다.
- 검증 근거: 구현 전 기존 PATCH MockMvc·API 통합·페이지 테스트가 모두 통과했다. 구현과 브라우저 검증 결과는 후속 기록에 보완한다.
- 결과와 연결 문서: 질문 문서와 ADR을 docs 브랜치에 추가할 예정이다.
- 회고와 후속 조치: PATCH 실패 뒤 재조회까지 실패하는 경로를 실제 브라우저에서 확인하고, 업로드 허용·거부 항목은 완료 처리하지 않는다.

## 2026-08-28T23:06:42+09:00 — 고정 확장자 PATCH 화면 구현과 브라우저 검증

- 상태: 채택
- 시간 근거: 전체 회귀 테스트 완료 직후 확인한 시스템 시각
- 스프린트/범위: 고정 확장자 체크·해제 PATCH, 실패 재동기화, 새로고침 유지
- 관련 문서·코드: `extension-policy.js`, [`sprint-1-file-upload-checklist.md`](docs/sprints/sprint-1/sprint-1-file-upload-checklist.md), [`0002-use-server-policy-state-as-source-of-truth.md`](docs/adr/0002-use-server-policy-state-as-source-of-truth.md)
- 요청·질문 요약: 승인된 계획에 따라 고정 확장자 체크박스를 PATCH API에 연결하고 성공·실패·재조회 실패를 검증한다.
- 배경과 제약: Java 백엔드 계약은 변경하지 않고 새 JavaScript 테스트 도구도 도입하지 않는다. 실제 파일 업로드 허용·거부는 후속 범위다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `tdd`, `browser:control-in-app-browser`
  - plugin/도구: Gradle, Node 문법 검사, 인앱 브라우저, 인메모리 H2, 임시 로컬 HTTP 프록시
- AI 제안: 초기 조회와 실패 복구가 같은 조회·렌더링 함수를 사용하고, PATCH 중 모든 fixed 체크박스를 잠그며, 실패 뒤 GET 성공 여부에 따라 서버 상태 재렌더링 또는 직전 상태 복원을 수행한다.
- 사람의 판단과 이유: 채택. 응답 유실처럼 PATCH 적용 여부가 불확실한 상황에서도 화면을 서버 상태에 맞추고, 재조회까지 실패하면 미확정 상태를 사용자에게 명시할 수 있다.
- 코드·사용자 경험 영향: 고정 확장자 체크·해제가 DB에 저장되고 성공 메시지가 표시된다. 404는 공통 `message`를 표시한 뒤 전체 정책을 재조회하며, 재조회 실패는 클릭 전 상태와 입력 가능 상태를 복원하고 새로고침을 안내한다.
- 검증 근거: 기존 PATCH 관련 테스트 기준선이 통과했다. 구현 후 `node --check`, 관련 MockMvc·통합·페이지 테스트, 전체 `./gradlew test --rerun-tasks`가 성공했다. 실제 브라우저에서 `PATCH /api/v1/extension-policies/fixed/exe`와 `{"blocked":true}`의 200 응답, 체크·해제 후 새로고침 유지, 404 뒤 GET 200 재동기화, PATCH 503과 GET 503 뒤 직전 상태 복원·전체 체크박스 재활성화를 확인했다.
- 결과와 연결 커밋: 문서 결정 `8e7a899`, 결정 문서 병합 `437df36`, 기능 구현 `de14242`, 완료 문서 `c889fa1`, 완료 문서 병합 `bc9f9d2`.
- 회고와 후속 조치: 브라우저 검증에서는 화면 상태와 HTTP 요청 결과를 함께 확인했다. 파일 업로드 허용·거부는 후속 범위로 분리했다.

## 2026-08-29T01:22:45+09:00 — 커스텀 확장자 POST 화면 구현

- 상태: 채택
- 시간 근거: 사용자 구현 요청과 브라우저 smoke 검증을 마친 시각
- 스프린트/범위: `POST /api/v1/extension-policies/custom` 커스텀 확장자 추가 화면
- 관련 문서·코드: `index.html`, `extension-policy.js`, `FileUploadPageControllerTests`, `sprint-1-file-upload-checklist.md` (docs/sprints/sprint-1/)
- 요청·질문 요약: 기존 정책 REST API를 유지하면서 커스텀 확장자 입력·POST·전체 GET 재조회·목록 반영·오류 메시지 표시를 구현한다.
- 배경과 제약: 백엔드 API·도메인·JPA·MockMvc 검증은 완료되어 있었고, 커스텀 삭제 화면·파일 업로드는 범위에서 제외했다. 화면 성공 후 서버 최신 상태를 기준으로 목록을 재조회한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `tdd`, `browser:control-in-app-browser`
  - plugin/도구: Gradle, Node 문법 검사, 인앱 브라우저, Git
- AI 제안: 커스텀 폼을 추가하고 Axios POST 성공 뒤 전체 정책 GET으로 재동기화하며, POST 오류와 재조회 오류의 공통 `message`를 상태 영역에 표시한다.
- 사람의 판단과 이유: 채택. 사용자는 커스텀 추가 화면만 구현하고 성공 후 전체 정책 재조회를 사용하기로 결정했다. 프런트에서 길이·형식 검증을 중복하지 않아 서버 계약을 단일 기준으로 유지했다.
- 코드·사용자 경험 영향: 입력창·추가 버튼이 화면에 추가되고 요청 중 잠긴다. ` SH ` 입력은 `sh`로 목록에 반영되고 새로고침 후 유지되며, 중복·길이 초과 오류는 서버 메시지로 표시된다.
- 검증 근거: DOM 계약 테스트를 먼저 Red로 확인한 뒤 화면 구현 후 해당 테스트와 `./gradlew test`가 성공했다. `node --check src/main/resources/static/js/extension-policy.js`와 `git diff --check`도 성공했다. 인앱 브라우저에서 정규화 등록, POST 후 GET 목록 반영, 새로고침 유지, 중복·20자 초과 오류 표시를 확인했다. 기존 파일 H2에 남은 구 스키마의 `created_at` 제약으로 첫 smoke가 실패했으나, 코드 변경 없이 격리 인메모리 H2로 재검증했다.
- 결과와 연결 문서: 기능 커밋 `6e6fbc5 feat: implement custom extension policy screen`, 체크리스트 문서 갱신 예정
- 회고와 후속 조치: 파일 DB 마이그레이션 문제가 별도로 발견되었으므로 운영·개발 데이터의 스키마 호환성은 파일 업로드 단계 전에 별도 결정한다. 커스텀 삭제 화면과 실제 파일 업로드 검증은 후속 작업이다.

## 2026-08-29T16:03:46+09:00 — 파일 업로드 API·화면 구현

- 상태: 구현·자동화 검증 및 브라우저 화면 smoke 완료, 네트워크 세부 확인 보완 예정
- 시간 근거: 사용자가 체크리스트 6번 구현을 요청하고 파일 저장·오류 정책을 확정한 뒤 테스트를 완료한 시각
- 스프린트/범위: `POST /api/v1/files` multipart 업로드, 서버 정책 판정, 로컬 파일 저장, 업로드 화면
- 관련 문서·코드: `FileUploadRestController`, `FileUploadService`, `LocalFileStorage`, `index.html`, `extension-policy.js`, 스프린트 1 API·체크리스트
- 요청·질문 요약: 허용 파일을 프로젝트 `./uploads`에 UUID 파일명으로 저장하고, fixed/custom 차단 정책과 공통 오류 응답을 실제 업로드에 적용한다.
- 배경과 제약: 원본 파일명은 저장 경로에 사용하지 않으며, 누락·빈 파일·무확장 파일은 `400 INVALID_FILE`, 차단 확장자는 `422 BLOCKED_EXTENSION`, 저장 장애는 `500 FILE_UPLOAD_FAILED`로 구분한다. 기존 5번 커스텀 삭제 화면 변경은 보존한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `tdd`, `browser:control-in-app-browser`
  - plugin/도구: Gradle, MockMvc, 인메모리 H2, 인앱 브라우저
- AI 제안: 정책 차단 판정과 파일 저장을 서비스·로컬 저장소로 분리하고, 화면은 정책을 중복 판단하지 않고 서버 응답을 표시한다.
- 사람의 판단과 이유: 채택. 사용자는 프로젝트 고정 `./uploads`, UUID 기반 파일명 응답, 무확장 파일 거부, 저장 장애 `500 FILE_UPLOAD_FAILED`를 선택했다.
- 코드·사용자 경험 영향: 파일 선택·업로드 폼이 추가되고, 성공 시 서버 생성 파일명과 완료 메시지를 표시한다. 차단·입력·저장 오류는 서버 공통 오류 메시지를 업로드 상태 영역에 표시한다.
- 검증 근거: `compileTestJava`의 테스트 헬퍼 타입 오류와 임시 디렉터리 단언 오류를 수정했다. 대상 테스트와 전체 `./gradlew test`가 성공했고, `node --check src/main/resources/static/js/extension-policy.js`, `git diff --check`도 성공했다. 인메모리 H2 통합 테스트로 fixed 차단·해제, custom 등록·삭제 후 업로드, 저장 파일 미생성을 확인했다. 인앱 브라우저에서 허용 파일의 UUID 저장명·성공 메시지, custom 차단의 `BLOCKED_EXTENSION`, 무확장 파일의 `INVALID_FILE` 메시지를 확인했다.
- 결과와 연결 문서: `file-upload-api.md` (docs/), `sprint-1-file-upload-checklist.md` (docs/sprints/sprint-1/), `sprint-1-file-upload-storage-and-error-options.md`, ADR 0003
- 실패·미완료: `.git` 메타데이터 쓰기 권한으로 stale docs worktree 정리·생성이 실패해 docs 브랜치 별도 커밋은 수행하지 못했다. 5번 삭제와 6번 업로드의 인앱 브라우저 smoke는 브라우저 데이터 삭제 확인 절차가 남아 있다.
- 회고와 후속 조치: 브라우저 smoke에서 테스트용 파일 업로드·정책 변경·삭제를 확인하고, 가능해지면 문서 변경을 docs 브랜치에서 별도 커밋한 뒤 기능 브랜치에 병합한다.

## 2026-08-29T16:47:29+09:00 — 파일 업로드 잔여 체크리스트 완료 검증

- 상태: 수정 채택
- 시간 근거: 인앱 브라우저에서 fixed/custom 정책 변경과 파일 업로드 흐름을 재검증하고 최종 회귀 테스트를 완료한 시스템 시각
- 스프린트/범위: 스프린트 1 잔여 검증 1~8번, 체크리스트 완료 상태 갱신
- 관련 문서·코드: `sprint-1-file-upload-checklist.md` (docs/sprints/sprint-1/), `file-upload-api.md` (docs/), `ExtensionPolicyApiIntegrationTests`, `FileUploadRestController`, `extension-policy.js`
- 요청·질문 요약: 고정·커스텀 정책 변경 직후 실제 업로드 차단·허용, 커스텀 삭제 후 화면·DB 동기화, 브라우저 요청 계약, 페이지 기동과 최종 문서·테스트 검증을 완료한다.
- 배경과 제약: 기존 구현과 자동화 테스트는 이미 존재했으므로 검증 범위를 넘어 기능을 확장하지 않는다. 테스트용 파일과 업로드 산출물은 검증 후 제거한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `browser:control-in-app-browser`
  - plugin/도구: 인앱 브라우저, Gradle, MockMvc, H2 Shell, Git diff 검사
- AI 제안: 기존 자동화 테스트를 기준선으로 사용하고, 브라우저에서는 정책 변경·multipart 업로드·삭제 후 재조회·새로고침 유지까지 수직 흐름을 확인한다. 구 H2 파일 DB 문제는 기존 데이터를 삭제하지 않고 호환성을 복구한다.
- 사람의 판단과 이유: 수정 채택. 첫 브라우저 커스텀 등록이 구 H2 파일 DB의 `created_at NOT NULL` 제약으로 실패했으나, 정책 7개와 quota를 보존해야 하므로 DB 파일을 삭제하지 않고 사용하지 않는 레거시 컬럼을 nullable로 조정했다. 현재 스프린트에는 별도 마이그레이션 기능을 추가하지 않는다.
- 코드·사용자 경험 영향: 제품 코드 동작은 변경하지 않았고, 체크리스트·패키지 인덱스·API 문서의 완료 상태만 실제 구현과 맞췄다. 브라우저 화면은 `exe` 차단 시 거부 메시지, 해제 시 UUID 파일명 성공 메시지, `sh` 추가·삭제에 따른 차단·허용과 목록 재동기화를 표시했다.
- 검증 근거: 인앱 브라우저에서 루트 페이지·정책 GET, `PATCH /api/v1/extension-policies/fixed/exe`, `POST /api/v1/extension-policies/custom`, `DELETE /api/v1/extension-policies/custom/sh`, `POST /api/v1/files` 흐름과 응답 메시지를 확인했다. `extension-policy.js`의 업로드 URL·POST·FormData `file` 필드와 컨트롤러 계약을 대조했다. `./gradlew test --rerun-tasks`와 `git diff --check`가 성공했다. 테스트 파일과 `uploads` 산출물은 제거했다.
- 결과와 연결 문서: 체크리스트 완료 상태 갱신, `AGENTS.md` 문서 인덱스 상태 갱신, 기존 API·스프린트 문서의 구현 상태 유지
- 회고와 후속 조치: 브라우저에서 `localhost`가 다른 로컬 서비스로 연결되어 `127.0.0.1:8080`을 사용했다. 구 H2 스키마를 사용하는 환경에서는 향후 자동 마이그레이션 정책을 별도로 결정한다.

## 2026-08-30T10:40:16+09:00 — ExtensionName 값 객체 전환 완성

- 상태: 수정 채택
- 시간 근거: `ExtensionName` 기반 서비스 계약과 업로드 흐름을 변경한 뒤 실행한 `./gradlew test`의 성공 시각
- 스프린트/범위: 확장자 이름 값 객체의 JPA 임베디드 매핑과 정책·업로드 기능 전체 전달
- 관련 문서·코드: [`ExtensionName`](src/main/java/com/example/demo/file/domain/value/ExtensionName.java), [`ExtensionPolicy`](src/main/java/com/example/demo/file/domain/entity/ExtensionPolicy.java), [`ExtensionPolicyService`](src/main/java/com/example/demo/file/service/ExtensionPolicyService.java), [`FixedExtensionCatalog`](src/main/java/com/example/demo/file/domain/FixedExtensionCatalog.java), [`FileUploadServiceImpl`](src/main/java/com/example/demo/file/service/impl/FileUploadServiceImpl.java)
- 요청·질문 요약: `ExtensionName`을 JPA 매핑을 위한 땜빵 타입으로 두지 말고, 정규화·검증·정책·업로드 흐름에서 실제 값 객체로 활용한다.
- 배경과 제약: 기존에는 문자열 기반 팩토리와 서비스 계약이 남아 있어 값 객체가 저장 필드에만 사용됐다. DB 컬럼명 `extension`과 REST API 문자열 계약은 유지해야 하며, API와 파일 입력의 오류 의미는 기능별 예외로 변환해야 한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: 저장소 전체 검색, `apply_patch`, Gradle 테스트, `git diff --check`
- AI 제안: `ExtensionName`만 받는 도메인 팩토리와 서비스 계약으로 변경하고, 카탈로그·repository·업로드 저장소까지 값을 전달한다. 문자열 변환은 REST 입력·응답과 최종 파일명 생성 지점으로 제한한다.
- 사람의 판단과 이유: 수정 채택. 사용자는 임베디드 매핑 자체가 아니라 값 객체로서의 완전한 전환을 요구했고, 문자열 기반 팩토리 오버로드를 남기지 않는 방향을 선택했다.
- 코드·사용자 경험 영향: `ExtensionPolicy`, `ExtensionPolicyService`, `FileStorage`, `BlockedExtensionException`이 `ExtensionName`을 직접 사용한다. REST 응답과 파일명에는 기존 문자열 형식이 유지되고, 입력 정규화·검증은 객체 생성 시 한 번 수행된다.
- 검증 근거: 서비스 계약 전환 후 업로드 테스트 3곳의 문자열 호출이 컴파일 실패했으며, 테스트도 `ExtensionName.from(...)`을 사용하도록 수정했다. 이후 `./gradlew test`가 성공했고 `git diff --check`도 통과했다. 문자열 기반 서비스·팩토리·카탈로그 계약이 남아 있지 않은지 검색으로 확인했다.
- 결과와 연결 문서: [`ExtensionName`](src/main/java/com/example/demo/file/domain/value/ExtensionName.java), [`ExtensionPolicy`](src/main/java/com/example/demo/file/domain/entity/ExtensionPolicy.java), [`ExtensionPolicyService`](src/main/java/com/example/demo/file/service/ExtensionPolicyService.java), [`ExtensionNameApiParser`](src/main/java/com/example/demo/file/controller/ExtensionNameApiParser.java)
- 회고와 후속 조치: 값 객체를 저장 필드에만 도입하면 문자열 계약이 서비스 내부에 계속 남을 수 있으므로, 타입 전환 시 생성자·인터페이스·예외·테스트 호출부까지 함께 검색한다. JPA 기본 생성자는 프레임워크 요구로 남기고 도메인 생성 규칙은 명시적으로 유지한다.

## 2026-08-30T11:13:42+09:00 — 파일 확장자 추출 모듈 분리 구현

- 상태: 수정 채택
- 시간 근거: 추출 전용 테스트와 전체 회귀 테스트가 모두 성공한 시스템 시각
- 스프린트/범위: 파일 업로드 확장자 추출 책임 분리와 테스트 경계 개선
- 관련 문서·코드: [`FileExtensionExtractor`](src/main/java/com/example/demo/file/service/impl/FileExtensionExtractor.java), [`FileUploadServiceImpl`](src/main/java/com/example/demo/file/service/impl/FileUploadServiceImpl.java), [`FileExtensionExtractorTests`](src/test/java/com/example/demo/service/impl/FileExtensionExtractorTests.java), [`FileUploadServiceTests`](src/test/java/com/example/demo/service/impl/FileUploadServiceTests.java)
- 요청·질문 요약: private `extractExtensionName`을 별도 클래스로 분리해 파일명 추출 케이스를 직접 검증하고 업로드 서비스 테스트는 orchestration에 집중한다.
- 배경과 제약: 파일명 검증·경로 정리·마지막 확장자 추출·`ExtensionName` 변환·파일 입력 예외 변환은 업로드 정책 판정·저장과 다른 책임이다. 기존 REST/API 동작과 `tar.gz`의 마지막 확장자 `gz` 추출 규칙은 유지한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `tdd`
  - plugin/도구: `apply_patch`, Gradle 대상 테스트·전체 테스트, `git diff --check`, 저장소 검색
- AI 제안: 별도 interface나 Spring bean을 추가하지 않고 `FileExtensionExtractor` 구체 모듈의 `extract(MultipartFile)`가 검증된 `ExtensionName`을 반환하도록 한다. 추출 모듈은 파일 입력 오류를 `InvalidFileException`으로 변환하고, 업로드 서비스는 정책과 저장만 조정한다.
- 사람의 판단과 이유: 수정 채택. 사용자는 추출 로직을 직접 테스트하기 편한 구조를 승인했고, 테스트만을 위한 과한 seam은 만들지 않기로 했다.
- 코드·사용자 경험 영향: 사용자가 경험하는 업로드 API 응답·저장 파일명·오류 의미는 변하지 않는다. 코드상 파일명 케이스의 실패 원인은 추출 모듈에서 바로 확인할 수 있고, 업로드 서비스 테스트는 차단 정책과 저장 동작에 집중한다.
- 검증 근거: 먼저 추출 공개 계약 테스트를 추가해 RED를 확인한 뒤 최소 구현으로 GREEN을 확인했다. 이후 마지막 확장자, 확장자 없음, 빈 파일, 길이 초과 케이스를 추가했고 대상 테스트와 전체 `./gradlew test`가 성공했다. `git diff --check`와 기존 private 메서드 참조 제거도 확인했다.
- 결과와 연결 문서: [`FileExtensionExtractor`](src/main/java/com/example/demo/file/service/impl/FileExtensionExtractor.java), [`FileUploadServiceImpl`](src/main/java/com/example/demo/file/service/impl/FileUploadServiceImpl.java), [`FileExtensionExtractorTests`](src/test/java/com/example/demo/service/impl/FileExtensionExtractorTests.java)
- 회고와 후속 조치: 한 구현만 있는 현재 seam에는 interface를 추가하지 않는다. 추출 규칙이 다른 입력 형식이나 두 번째 adapter가 실제로 생길 때만 interface 도입을 재검토한다.

## 2026-08-30T17:06:50+09:00 — MIME 기반 비실행 파일 허용정책 ADR 작성

- 상태: 채택
- 시간 근거: 사용자 요청 직후 ADR 초안 작성, 문서 인덱스 갱신, `git diff --check` 검증을 수행한 시스템 시각
- 스프린트/범위: 파일 업로드 콘텐츠 검증 라이브러리 선택과 허용 범위에 대한 ADR
- 관련 문서·코드: [`0005-limit-upload-to-known-non-executable-types.md`](docs/adr/0005-limit-upload-to-known-non-executable-types.md), [`AGENTS.md`](AGENTS.md)
- 요청·질문 요약: 여러 MIME 감지 라이브러리 중 Apache Tika를 선택하는 이유, MIME 불일치 허용·거부의 한계, 실행파일·악성코드 검증의 책임 범위를 비교한 뒤 ADR을 생성한다.
- 배경과 제약: MIME 감지만으로 실행 가능성이나 악성 여부를 완전히 판단할 수 없다. 사용자에게 노출되는 정책과 내부 기술 검증을 혼동하지 않으며, Parser와 악성코드 검사는 별도 후속 범위로 둔다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `adr-predecision-review`
  - plugin/도구: Apache Tika 공식 문서·Oracle Java 문서 검색, 저장소 문서 확인, `apply_patch`, `git diff --check`
- AI 제안: `tika-core`를 MIME 감지 전용으로 사용하고, 지원이 명확한 비실행 형식만 허용하는 보수적 정책을 제안했다. MIME 불일치를 허용하는 방식, 호환되는 경우만 허용하는 방식, Parser·악성코드 검사까지 도입하는 방식을 비교했다.
- 사람의 판단과 이유: 채택. 사용자는 과도한 세부사항과 특정 구현 맥락을 제외하고, MIME 감지의 한계를 인정하면서 현재는 비실행 파일 형식만 허용하고 향후 실행파일 식별·악성코드 검사·격리 체계가 마련되면 허용 범위를 재검토하는 ADR을 생성하도록 결정했다.
- 코드·사용자 경험 영향: 이번 변경은 문서만 수정한다. 허용 형식과 MIME 호환성 검증이 사용자에게 어떤 오류로 노출되는지는 후속 구현·API 계약에서 구체화한다.
- 검증 근거: Tika 공식 문서에서 `tika-core`의 감지 범위와 Parser 의존성을 확인했고, Oracle 문서에서 JDK MIME 감지의 플랫폼 의존성을 확인했다. 새 ADR 링크와 Markdown 문법을 확인했으며 `git diff --check`가 성공했다.
- 결과와 연결 문서: [`0005-limit-upload-to-known-non-executable-types.md`](docs/adr/0005-limit-upload-to-known-non-executable-types.md), [`AGENTS.md`](AGENTS.md)
- 회고와 후속 조치: ADR은 MIME 감지와 악성코드 검사를 동일시하지 않는다. 실제 구현 전 허용 파일 형식 목록, MIME 불일치 오류 의미, Parser 격리·악성코드 검사 도입 조건을 API·스프린트 문서와 함께 구체화해야 한다.

## 2026-08-30T17:12:04+09:00 — 원본 파일명과 서버 저장 파일명 매핑 ADR 초안

- 상태: 검토 중
- 시간 근거: 사용자와 원본 파일명 보존, 서버 생성 저장 파일명, 매핑 DB, 길이 제한의 인과관계를 확인한 현재 대화 시각.
- 스프린트/범위: 파일 업로드 메타데이터와 원본·저장 파일명 매핑 정책 초안
- 관련 문서·코드: [`0003-server-generated-file-storage-policy.md`](docs/adr/0003-server-generated-file-storage-policy.md), [`0006-persist-upload-file-name-mapping.md`](docs/adr/0006-persist-upload-file-name-mapping.md), [인터널 독스 3번](.internal-docs/file-upload-risk-analysis/03-name-shape-and-length.md), [`UploadedFile`](src/main/java/com/example/demo/file/service/UploadedFile.java), [`LocalFileStorage`](src/main/java/com/example/demo/file/service/impl/LocalFileStorage.java)
- 요청·질문 요약: 파일명 충돌을 해결하기 위해 서버 내부 저장 파일명을 사용하고, 원본 파일명과의 매핑을 `UploadFile` Entity로 관리할 때 원본 파일명 길이 제한을 함께 결정해야 하는지 검토한 뒤 ADR 초안을 작성한다.
- 배경과 제약: 서버 생성 UUID 파일명은 물리 파일명 충돌을 줄이지만, 원본 파일명을 저장하지 않으면 업로드 후 표시·다운로드를 위한 매핑을 복원할 수 없다. 인터널 독스 3번은 원본 파일명을 저장하지 않는 현재 상태에서도 전송·파싱·로그 위험 때문에 입력 길이 제한이 필요하다고 설명한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: 저장소 문서·코드 검색, ADR 비교, `apply_patch`, `git diff --check`
- AI 제안: `UploadFile`은 파일 바이트가 아니라 원본 파일명, 서버 저장 파일명, 생성 시각을 보유하는 메타데이터 엔티티로 두고, 서버 저장 파일명에는 unique 제약을 적용한다. 원본 파일명을 보존하기로 결정한 결과 애플리케이션과 DB에 길이 제한을 두며, DB와 파일 시스템 사이의 보상 삭제 정책도 기록한다.
- 사람의 판단과 이유: 수정 채택. 사용자는 파일명 충돌 회피만이 아니라 원본 파일명과 서버 저장 파일명의 매핑을 DB에서 유지해야 한다는 흐름을 확인했고, 원본 파일명을 저장하는 모델이라면 길이 제한이 필요한 것으로 판단했다. ADR은 구현을 확정하기 전 검토 가능한 `proposed` 초안으로 작성한다.
- 코드·사용자 경험 영향: 이번 변경은 문서만 수정한다. 향후 구현 시 사용자는 원본 파일명을 표시할 수 있고, 서버는 충돌 없는 저장 파일명을 유지한다. 원본 파일명 최대 255 Unicode code point, 경로·제어문자 처리, DB-파일 시스템 보상 삭제는 구현 전 검토 대상이다.
- 검증 근거: 인터널 독스 3번의 전송·애플리케이션·저장 계층 구분과 현재 `LocalFileStorage`의 UUID 저장 정책을 대조했다. 기존 API의 `filename`이 서버 저장 파일명을 반환한다는 계약도 확인했다. 구현·테스트 실행은 하지 않았다.
- 결과와 연결 문서: [`0006-persist-upload-file-name-mapping.md`](docs/adr/0006-persist-upload-file-name-mapping.md), [`AGENTS.md`](AGENTS.md)
- 회고와 후속 조치: 255자 제한은 초안의 제안값이므로 제품 요구사항과 UI 표시 정책을 확인한 뒤 accepted로 승격한다. 구현 시 DB 저장 실패 후 물리 파일 보상 삭제와 원본 파일명 경계값 테스트를 먼저 고정한다.

## 2026-08-30T17:23:28+09:00 — 확장자 허용 문자 범위 ADR 반영

- 상태: 채택
- 시간 근거: 사용자 결정 직후 문서 변경을 수행한 시스템 시각과 현재 대화 순서
- 스프린트/범위: 확장자 이름 값 객체의 허용 문자 검증 정책
- 관련 문서·코드: [`0004-use-extension-name-value-object.md`](docs/adr/0004-use-extension-name-value-object.md), [`파일 업로드 API 명세`](docs/file-upload-api.md), [`sprint-1-file-upload-checklist.md`](docs/sprints/sprint-1/sprint-1-file-upload-checklist.md)
- 요청·질문 요약: 특수문자를 제외하고 한글·영문·숫자만 허용하는 확장자 검증 규칙을 ADR에 반영한다.
- 배경과 제약: 기존 문서는 점 제외와 정규화·길이만 정의하고 허용 문자 범위는 명시하지 않았다. 숫자를 허용하면 `mp3`, `7z`와 같은 일반적인 확장자를 표현할 수 있다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: 저장소 문서 검색, `apply_patch`, `git diff --check`
- AI 제안: 한글·영문·숫자를 허용하고 점·공백·기타 특수문자를 거부하는 규칙을 `ExtensionValidator`의 형식 검증으로 둔다. 정규화와 값 객체 경계는 유지한다.
- 사람의 판단과 이유: 채택. 사용자는 숫자가 포함된 정상 확장자도 허용할 수 있도록 한글·영문·숫자 범위를 확정했다.
- 코드·사용자 경험 영향: 이번 변경은 문서만 수정한다. 향후 구현 시 커스텀 확장자 입력과 업로드 확장자 검증에서 허용 문자 범위를 동일하게 적용해야 한다.
- 검증 근거: ADR, API 계약, 완료 체크리스트의 허용 문자 문구를 대조하고 문서 링크·Markdown 형식을 확인한다. 코드와 테스트는 아직 변경하지 않았다.
- 결과와 연결 문서: [`0004-use-extension-name-value-object.md`](docs/adr/0004-use-extension-name-value-object.md), [`파일 업로드 API 명세`](docs/file-upload-api.md), [`sprint-1-file-upload-checklist.md`](docs/sprints/sprint-1/sprint-1-file-upload-checklist.md)
- 회고와 후속 조치: 다음 구현에서는 허용·거부 경계값을 한글 테스트명으로 먼저 고정하고, 기존 코드가 점만 검사하는지 확인한 뒤 검증 로직을 적용한다.

## 2026-08-30T17:27:09+09:00 — 최종 확장자 기준 업로드 차단 ADR 생성

- 상태: 채택
- 시간 근거: 사용자와 다중 점 파일명의 판정 기준을 확정한 뒤 ADR 생성과 문서 인덱스 갱신을 수행한 시스템 시각
- 스프린트/범위: 파일 업로드 확장자 차단 정책의 다중 점 파일명 처리
- 관련 문서·코드: [`0007-use-final-file-extension-for-upload-blocking.md`](docs/adr/0007-use-final-file-extension-for-upload-blocking.md), [인터널 독스 2번](.internal-docs/file-upload-risk-analysis/02-case-double-extension.md), [`FileExtensionExtractor`](src/main/java/com/example/demo/file/service/FileExtensionExtractor.java)
- 요청·질문 요약: `file.txt.exe`는 최종 확장자 `exe`이므로 차단하고, `file.exe.txt`는 최종 확장자 `txt`이므로 중간 `exe`만으로 자동 차단하지 않는 정책을 ADR로 생성한다.
- 배경과 제약: 모든 중간 확장자를 차단하면 정상적인 다중 점 파일명을 과도하게 거부할 수 있다. 반면 확장자 검사는 파일 내용·MIME·악성코드 여부를 보장하지 않으므로 최종 확장자 정책의 책임 범위를 분리해야 한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: 저장소 문서·코드 확인, `apply_patch`, `git diff --check`
- AI 제안: 모든 확장자 세그먼트를 검사하는 보수적 정책, 부분 문자열 검사, 최종 확장자 exact match를 비교했다. 최종 확장자 exact match를 채택하되 이중 확장자 기만과 콘텐츠 검사는 별도 책임으로 명시했다.
- 사람의 판단과 이유: 채택. 사용자는 파일명의 최종 확장자를 기준으로 판단하는 정책을 확정했다. `file.txt.exe`와 `file.exe.txt`의 사용자 의미 차이를 반영하면서 정상 파일 오탐을 줄이는 것이 이유다.
- 코드·사용자 경험 영향: 이번 변경은 문서만 수정한다. 현재 구현의 마지막 확장자 추출 계약을 ADR로 명확히 하며, 중간 확장자 자동 차단이나 복합 확장자 정책 등록은 추가하지 않는다.
- 검증 근거: 인터널 독스 2번, ADR 0003·0004, 현재 `FileExtensionExtractor`의 마지막 점 이후 추출 동작을 대조했다. 새 ADR 링크와 Markdown 문법을 확인하고 `git diff --check`를 실행한다.
- 결과와 연결 문서: [`0007-use-final-file-extension-for-upload-blocking.md`](docs/adr/0007-use-final-file-extension-for-upload-blocking.md), [`AGENTS.md`](AGENTS.md)
- 회고와 후속 조치: 다운로드·실행·압축 해제·미리보기 기능이 추가되면 중간 확장자 위험, MIME 불일치, 파일 내용 검사를 별도 결정한다.

## 2026-08-30T17:30:00+09:00 — multipart 업로드 용량 제한 ADR 생성

- 상태: 채택
- 시간 근거: 사용자가 업로드 용량 제한을 100MB로 확정한 현재 대화 시각. 전체 요청 제한은 multipart 부가 정보 여유를 고려해 110MB로 정했다.
- 스프린트/범위: 서버 multipart 단계의 파일 업로드 용량 제한
- 관련 문서·코드: [`0009-limit-multipart-upload-size.md`](docs/adr/0009-limit-multipart-upload-size.md), [`AGENTS.md`](AGENTS.md)
- 요청·질문 요약: 서버에서 YML의 multipart 설정으로 파일 용량을 제한하는 방안을 검토하고, 100MB 기준으로 ADR을 생성한다.
- 배경과 제약: 애플리케이션 서비스보다 앞선 multipart 처리 단계에서 큰 요청을 차단해 메모리·디스크·처리 자원 낭비를 줄인다. 확장자·MIME 검증과 원본·저장 파일명 매핑은 기존 ADR의 책임으로 분리한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: 저장소 문서 확인, `apply_patch`, `git diff --check`
- AI 제안: 파일 1개를 100MB로 제한하고 multipart 전체 요청은 경계·부가 정보 여유를 포함해 110MB로 제한하며, 용량 초과를 `413 Payload Too Large` 의미로 처리하는 ADR을 제안했다.
- 사람의 판단과 이유: 채택. 사용자는 업로드 용량을 100MB로 확정했다. 파일 1개 한도와 전체 요청 한도를 구분해 정상적인 100MB 파일이 multipart 오버헤드 때문에 거부되지 않도록 했다.
- 코드·사용자 경험 영향: 이번 변경은 문서만 수정한다. 사용자는 100MB 이하 파일을 업로드할 수 있고, 초과 요청은 저장·파일 형식 검증 전에 거부되는 정책을 갖는다. 실제 YML과 공통 오류 핸들러 구현은 후속 코드 작업이다.
- 검증 근거: 기존 ADR 0005·0006의 책임 범위와 `AGENTS.md`의 ADR 작성·문서 인덱스 규칙을 대조했다. ADR과 인덱스 작성 후 Markdown 구조 및 `git diff --check`를 확인한다.
- 결과와 연결 문서: [`0009-limit-multipart-upload-size.md`](docs/adr/0009-limit-multipart-upload-size.md), [`AGENTS.md`](AGENTS.md)
- 회고와 후속 조치: 실제 구현 시 Spring Boot multipart 설정, 413 오류의 공통 응답 변환, 프록시의 업로드 제한을 함께 검증한다. 사용자가 전체 요청도 100MB로 제한하기를 원하면 `max-request-size`를 별도 조정해야 한다.

## 2026-08-30T17:42:16+09:00 — 업로드 저장 루트 외부화 ADR 생성

- 상태: 채택
- 시간 근거: 사용자가 구현된 ADR 0003을 수정하지 않고 저장 경로의 YAML 외부화를 별도 ADR로 기록하라고 결정한 현재 대화 시각
- 스프린트/범위: 업로드 물리 저장 루트의 환경 설정 외부화
- 관련 문서·코드: [`0011-externalize-upload-storage-path.md`](docs/adr/0011-externalize-upload-storage-path.md), [`0003-server-generated-file-storage-policy.md`](docs/adr/0003-server-generated-file-storage-policy.md), [`0006-persist-upload-file-name-mapping.md`](docs/adr/0006-persist-upload-file-name-mapping.md)
- 요청·질문 요약: 기존 ADR 0003은 이미 구현된 저장 정책이므로 변경하지 않고, 업로드 저장 경로를 YML 설정으로 관리하는 결정을 새 ADR로 생성한다.
- 배경과 제약: 서버 생성 저장 파일명과 원본 표시 파일명은 별도 책임으로 유지해야 한다. 저장 루트를 요청값이나 원본 파일명에서 파생하면 경로 조작과 운영 경계 혼동이 생기므로 서버 설정으로 한정한다. 기본 `./uploads` 동작은 유지한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: 저장소 ADR·AGENTS.md·PROMPT_LOG.md 확인, `apply_patch`, `git diff --check`
- AI 제안: `file.upload.storage-path` 설정 키와 `./uploads` 기본값을 정하고, HTTP 요청에서 경로를 받지 않으며, 설정 경로 변경 시 기존 파일 자동 이동은 수행하지 않는 별도 ADR을 작성한다.
- 사람의 판단과 이유: 채택. 사용자는 이미 구현된 ADR 0003의 의미를 보존하고, 저장 경로 설정 외부화라는 운영 경계를 독립적인 ADR로 추적하기로 결정했다.
- 코드·사용자 경험 영향: 이번 변경은 문서만 수정한다. 후속 구현에서는 환경별 저장 루트를 설정할 수 있지만 원본 파일명은 물리 저장 경로에 사용하지 않는다.
- 검증 근거: ADR 0003·0006의 책임 범위와 현재 문서 브랜치 상태를 대조했다. 새 ADR 번호와 문서 인덱스 경로를 확인하고 Markdown·공백 검증을 수행한다. 코드·테스트 실행은 하지 않았다.
- 결과와 연결 문서: [`0011-externalize-upload-storage-path.md`](docs/adr/0011-externalize-upload-storage-path.md), [`AGENTS.md`](AGENTS.md)
- 회고와 후속 조치: 후속 코드 작업에서 설정 바인딩, 디렉터리 생성·권한 오류, 테스트별 임시 저장 루트 주입을 구현 범위로 구체화한다. 저장소를 오브젝트 스토리지로 바꾸는 경우에는 별도 ADR을 작성한다.

## 2026-08-30T17:45:31+09:00 — 정책 변경 이력의 운영 원인 추적 목적 확정

- 상태: 채택
- 시간 근거: 사용자가 정책 이력 테이블의 핵심 가치를 “내부 로직 오류인지, 누가 삭제했다가 다시 추가했는지 확인해 운영 대응하는 것”으로 확정한 현재 대화 시각.
- 스프린트/범위: 정책 변경·삭제·재등록 이력과 운영 장애 원인 추적을 위한 ADR
- 관련 문서·코드: [`0012-preserve-policy-change-history-for-operations.md`](docs/adr/0012-preserve-policy-change-history-for-operations.md), [정책 변경 이력 분석](.internal-docs/file-upload-risk-analysis/10-policy-audit-history.md), [`0001-unify-extension-policies.md`](docs/adr/0001-unify-extension-policies.md), [`AGENTS.md`](AGENTS.md)
- 요청·질문 요약: 정책 변경 이력 테이블이 왜 필요한지 검토하고, 운영 원인 추적을 중심으로 ADR을 생성한다.
- 배경과 제약: 현재 `updatedAt`은 최신 수정 시각만 보존하고, 커스텀 정책 물리 삭제는 삭제 사실을 현재 상태에서 제거한다. 인증·인가가 없으므로 실제 개인 actor를 추정해 기록하지 않는다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: 저장소 문서·코드 검색, 기존 ADR 비교, `apply_patch`, `git diff --check`
- AI 제안: 별도 append-only 이력 테이블에 정책 변경의 시각·동작·전후 상태·변경 경로·요청 식별자를 기록하고, 정책 변경과 이력 저장을 같은 트랜잭션에서 처리하는 방향을 제안했다.
- 사람의 판단과 이유: 채택. 사용자는 감사 자체보다 운영 중 정책 상태가 이상해졌을 때 사람의 변경, 삭제 후 재등록, 내부 로직 오류를 구분할 수 있는 것이 실질적인 가치라고 판단했다. 이에 따라 이력 보존을 운영 원인 추적을 위한 장기 정책으로 ADR에 기록한다.
- 코드·사용자 경험 영향: 이번 변경은 문서만 수정한다. 후속 구현에서는 정책 생성·차단 변경·삭제 이벤트를 이력으로 남기고, 인증 도입 전에는 시스템 주체와 변경 경로 수준으로만 원인을 추적한다.
- 검증 근거: 기존 정책 엔티티의 `createdAt`·`updatedAt` 한계, 커스텀 정책 물리 삭제, 기존 단일 엔티티 결정과 인터널 위험 분석 문서를 대조했다. ADR 문서 인덱스와 Markdown 공백 검증을 수행한다. 코드·테스트 실행은 하지 않았다.
- 결과와 연결 문서: [`0012-preserve-policy-change-history-for-operations.md`](docs/adr/0012-preserve-policy-change-history-for-operations.md), [`AGENTS.md`](AGENTS.md)
- 회고와 후속 조치: 이력 도입만으로 과거 변경을 복원할 수는 없다. 구현 시 모든 정책 변경 경로의 기록 누락과 DB 직접 변경 경계를 확인하고, 인증·인가·보존 기간·조회 API는 별도 결정으로 구체화한다.

## 2026-08-30T17:55:44+09:00 — 업로드 오류 requestId 및 FE 메시지 책임 ADR 생성

- 상태: 채택
- 시간 근거: 사용자가 서버 로그 연결을 위한 `requestId` 포함, 재시도 정책 제외, 사용자 메시지의 FE 관리 결정을 확정한 현재 대화 시각.
- 스프린트/범위: 파일 업로드 오류 응답의 추적 식별자와 사용자 메시지 책임
- 관련 문서·코드: [`0013-use-request-id-and-frontend-owned-upload-messages.md`](docs/adr/0013-use-request-id-and-frontend-owned-upload-messages.md), [`파일 업로드 API 명세`](docs/file-upload-api.md), [차단 메시지 분석](.internal-docs/file-upload-risk-analysis/13-block-message.md)
- 요청·질문 요약: 업로드 오류 응답에 재시도 가능 여부와 `requestId`를 포함할지, 서버 메시지와 FE 메시지 중 어느 쪽이 책임을 가질지 검토한 뒤 ADR을 생성한다.
- 배경과 제약: `requestId`는 사용자 문의를 서버 로그와 연결해야 한다. 과제 단순화를 위해 FE 구현은 이번 범위에서 제외하고, `retryable`은 멱등성·중복 업로드 정책과 얽히므로 다루지 않는다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `adr-predecision-review` 지침을 참고한 ADR 정리
  - plugin/도구: 저장소 문서·main 구현 대조, `apply_patch`, `git diff --check`
- AI 제안: `requestId`는 오류 응답과 구조화 로그에 포함하고, `retryable`은 멱등성 정책 확정 전 보류하며, FE가 `code`와 안전한 context로 사용자 메시지를 조립하는 방향을 제안했다.
- 사람의 판단과 이유: 채택. 사용자는 서버 로그 연결을 위해 `requestId`를 포함하고, 과제 단순화를 위해 FE 구현은 제외하며, 재시도 가능 여부는 ADR 범위에서 다루지 않기로 결정했다. 서버 문구는 번역·표현 변경에 결합되므로 FE가 메시지를 관리하도록 확정했다.
- 코드·사용자 경험 영향: 이번 변경은 문서만 수정한다. 목표 업로드 오류 응답은 `code`, `requestId`, 안전한 `context`를 중심으로 하며, FE는 오류 코드별 메시지를 조립한다. 실제 서버 응답·로그·FE 전환 구현은 후속 작업이다.
- 검증 근거: `main`의 현재 `FileUploadExceptionHandler`, `FileUploadServiceImpl`, FE 오류 표시, 업로드 오류 테스트와 기존 ADR 0003·API 계약을 대조했다. `retryable`을 새 계약에 넣지 않았고, 문서 링크·Markdown·공백 검증은 변경 후 수행한다.
- 결과와 연결 문서: [`0013-use-request-id-and-frontend-owned-upload-messages.md`](docs/adr/0013-use-request-id-and-frontend-owned-upload-messages.md), [`파일 업로드 API 명세`](docs/file-upload-api.md), [`AGENTS.md`](AGENTS.md)
- 회고와 후속 조치: 구현 시 `requestId` 생성·전파·로그 필드의 경계를 정하고, 기존 `message` 의존 FE와의 호환 전환을 검토한다. 재시도와 멱등성은 별도 요구가 생길 때 독립적으로 결정한다.

## 2026-08-30T17:58:49+09:00 — 파일 업로드 네트워크 재시도와 멱등성 검토 시작

- 상태: 검토 중
- 시간 근거: 사용자가 파일 업로드의 네트워크 오류 시 FE 자동 재시도와 `Idempotency-Key` 도입 의견을 제시한 현재 대화 시각
- 스프린트/범위: 로컬 파일시스템 기반 동기 파일 업로드의 응답 유실·중복 저장 대응
- 관련 문서·코드: [`sprint-1-upload-retry-idempotency-options.md`](docs/questions/sprint-1-upload-retry-idempotency-options.md), [`파일 업로드 API 명세`](docs/file-upload-api.md), [`0013-use-request-id-and-frontend-owned-upload-messages.md`](docs/adr/0013-use-request-id-and-frontend-owned-upload-messages.md), [로딩·오류·네트워크 실패 상태 설계](.internal-docs/file-upload-risk-analysis/14-loading-error-network.md)
- 요청·질문 요약: 네트워크 오류 시 FE가 자동 재시도하고 멱등성 키 하나를 유지하는 방안이 적절한지 검토한다.
- 배경과 제약: 현재 업로드 API는 서버 생성 UUID 파일명만 사용하고 업로드 작업 ID·결과 조회·멱등성 기록이 없다. 같은 키를 재전송하는 것만으로는 서버가 키를 기억하지 않으므로 중복 저장 방지가 보장되지 않는다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `adr-predecision-review`, `diagnose`
  - plugin/도구: 저장소 문서·API 계약·ADR 확인, `apply_patch`, `git diff --check`
- AI 제안: 논리적 업로드 시도마다 FE가 키 하나를 만들고 네트워크 오류에 한해 같은 키로 제한적 자동 재시도하되, 장기적으로 서버가 키·요청 지문·처리 상태·성공 응답을 영속화하는 선택지를 권장 후보로 제시한다. 명시적 4xx는 자동 재시도하지 않는다.
- 사람의 판단과 이유: 미결정. 자동 재시도와 키 유지 방향의 적절성을 검토 중이며, 키 보존 기간·동시 요청·파일시스템과 기록 저장의 장애 구간은 추가 결정이 필요하다.
- 코드·사용자 경험 영향: 현재는 문서만 추가한다. 향후 구현 시 응답 유실을 결과 미확정으로 안내하고, 멱등성 기록이 도입되면 같은 키 재요청에서 동일 성공 결과를 복원한다.
- 검증 근거: 업로드 API 계약의 현재 응답·오류 코드와 ADR 0003의 서버 생성 파일명 정책, ADR 0013의 재시도·멱등성 범위 제외를 대조했다. 새 결정 전 검토 문서에 세 선택지와 실패 모드를 기록한다.
- 결과와 연결 문서: [`sprint-1-upload-retry-idempotency-options.md`](docs/questions/sprint-1-upload-retry-idempotency-options.md), [`AGENTS.md`](AGENTS.md)
- 회고와 후속 조치: 사용자가 자동 재시도 범위와 서버 키 결과 저장을 확정하면 API 계약을 갱신하고 별도 ADR로 전환한다. 그 전에는 코드에 재시도나 `Idempotency-Key`를 임의로 구현하지 않는다.

## 2026-08-30T18:03:34+09:00 — 멱등키 구성 방식 구체화

- 상태: 검토 중
- 시간 근거: 사용자가 파일 업로드 멱등키의 구체적인 구성 방식을 질문한 현재 대화 시각
- 스프린트/범위: FE 자동 재시도와 서버 멱등성 기록의 키 생성·수명·요청 지문
- 관련 문서·코드: [`sprint-1-upload-retry-idempotency-options.md`](docs/questions/sprint-1-upload-retry-idempotency-options.md), [`파일 업로드 API 명세`](docs/file-upload-api.md), [`0013-use-request-id-and-frontend-owned-upload-messages.md`](docs/adr/0013-use-request-id-and-frontend-owned-upload-messages.md)
- 요청·질문 요약: 업로드 멱등키를 어떤 값으로 구성하고 재시도 동안 어떻게 유지할지 검토한다.
- 배경과 제약: 파일명·사용자 ID·시각·파일 해시 조합은 개인정보, 의도적 중복 업로드 구분, 정규화와 충돌 문제를 만들 수 있다. 현재 API에는 인증·업로드 메타데이터·멱등성 기록이 없다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `adr-predecision-review`
  - plugin/도구: 저장소 API 계약·ADR·결정 전 검토 문서 확인, `apply_patch`, `git diff --check`
- AI 제안: FE가 `crypto.randomUUID()`로 논리적 업로드마다 불투명한 UUID v4를 만들고, 네트워크 재시도에는 같은 키를 유지한다. 서버는 `scope + route + key`를 식별자로 저장하고, 서버 계산 요청 지문으로 다른 본문 재사용을 거부한다. `requestId`는 요청마다 새로 발급하는 로그 추적 ID로 분리한다.
- 사람의 판단과 이유: 미결정. UUID 키의 생성 주체·보존 기간·동시 요청·요청 지문 범위를 검토 중이다.
- 코드·사용자 경험 영향: 현재는 문서만 갱신한다. 향후 사용자는 네트워크 오류 시 같은 논리적 업로드를 안전하게 재시도할 수 있고, 의도적인 새 업로드는 새 키로 구분된다.
- 검증 근거: 파일 업로드 API가 현재 서버 생성 UUID 파일명을 사용하고, ADR 0013이 재시도·멱등성을 범위에서 제외한다는 점을 대조했다. 키 구성·생명주기·서버 저장 구조를 결정 전 문서에 추가했다.
- 결과와 연결 문서: [`sprint-1-upload-retry-idempotency-options.md`](docs/questions/sprint-1-upload-retry-idempotency-options.md)
- 회고와 후속 조치: 자동 재시도 범위와 서버 영속화 방식을 확정한 뒤 API 계약과 별도 ADR을 갱신한다. 결정 전에는 `Idempotency-Key`를 API나 코드에 추가하지 않는다.

## 2026-08-30T18:06:19+09:00 — 클라이언트 UUID의 충분성 검토

- 상태: 검토 중
- 시간 근거: 사용자가 클라이언트 UUID만으로 업로드 멱등성이 충분한지 추가 질문한 현재 대화 시각
- 스프린트/범위: 멱등키 형식과 서버 측 보장 조건
- 관련 문서·코드: [`sprint-1-upload-retry-idempotency-options.md`](docs/questions/sprint-1-upload-retry-idempotency-options.md), [`파일 업로드 API 명세`](docs/file-upload-api.md), [`0003-server-generated-file-storage-policy.md`](docs/adr/0003-server-generated-file-storage-policy.md)
- 요청·질문 요약: FE가 생성한 UUID를 멱등키로 사용하는 것만으로 충분한지 검토한다.
- 배경과 제약: 현재 파일명은 서버 UUID로 생성되며, 업로드 요청의 멱등키와 처리 결과를 서버가 저장하는 구조는 없다. 클라이언트 키 형식과 서버 멱등성 보장 장치를 구분해야 한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `adr-predecision-review`
  - plugin/도구: 저장소 결정 전 검토 문서·API 계약·ADR 확인, `apply_patch`, `git diff --check`
- AI 제안: UUID v4는 키 형식으로 충분하지만 단독으로는 부족하며, 서버의 키·결과 영속화와 요청 지문 검증이 함께 필요하다고 정리했다. UUID v7은 정렬성만 제공하고 멱등성 강도는 높이지 않는다.
- 사람의 판단과 이유: 미결정. UUID 기반 키를 채택할지와 서버 기록·지문·보존 정책을 함께 결정해야 한다.
- 코드·사용자 경험 영향: 현재는 문서만 갱신한다. 향후 같은 논리적 업로드의 재시도는 같은 키를 유지하고, 다른 파일에 재사용된 키는 충돌로 안내한다.
- 검증 근거: 기존 업로드 API의 서버 생성 UUID 파일명과 멱등성·결과 조회 부재를 대조하고, 결정 전 문서에 UUID의 충분한 범위와 한계를 추가했다.
- 결과와 연결 문서: [`sprint-1-upload-retry-idempotency-options.md`](docs/questions/sprint-1-upload-retry-idempotency-options.md)
- 회고와 후속 조치: 서버 저장 모델과 요청 지문 범위를 확정한 뒤 API 계약·ADR을 별도로 갱신한다. 클라이언트 UUID만 추가하는 부분 구현은 멱등성 보장으로 보고하지 않는다.

## 2026-08-30T18:08:26+09:00 — 스프린트 2 확장자 정책 한도 UX 검증 요구사항 작성

- 상태: 요구사항 정의·브라우저 검증 대기
- 시간 근거: 사용자가 스프린트 2 개요 문서에 20자·200개 한도의 실제 UI/UX 검증 요구사항을 기록해 달라고 요청한 현재 대화 시각
- 스프린트/범위: 20자 입력, 200개 전체 목록, 201번째 등록 실패의 브라우저 UX 검증
- 관련 문서·코드: [`sprint-2-extension-limit-ux-validation.md`](docs/sprints/sprint-2/sprint-2-extension-limit-ux-validation.md), [제한값 근거와 UX 분석](.internal-docs/file-upload-risk-analysis/11-limit-rationale-and-ux.md), [`파일 업로드 API 명세`](docs/file-upload-api.md), [`AGENTS.md`](AGENTS.md)
- 요청·질문 요약: 20자·200개 한도를 UI/UX와 일반 확장자 길이 관점에서 검증하고, 결과에 따라 UX 구현 또는 ADR 필요 여부를 결정하는 내용을 스프린트 2 요구사항으로 남긴다.
- 배경과 제약: 현재 값은 API 계약과 구현에 존재하지만, 실제 경계값의 화면 사용성은 확인되지 않았다. 200개는 DB 성능의 절대 한계로 단정하지 않고 전체 목록 관리성·응답·렌더링 부담을 함께 확인해야 한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `backend-documentation`
  - plugin/도구: 기존 스프린트 문서·API 계약·구현 근거 확인, `apply_patch`, `git diff --check`
- AI 제안: 검증 계획은 ADR이 아닌 스프린트 개요 요구사항으로 기록하고, 실제 검증 후 현재 정책 유지·최소 UX 개선·검색/페이징·장기 정책 ADR 여부를 분기한다.
- 사람의 판단과 이유: 채택. 사용자는 스프린트 2에서 실제 20자·200개 경계값을 화면에 적용해 확인하기로 했으며, 검증 전에는 숫자나 구현을 임의로 변경하지 않는다.
- 코드·사용자 경험 영향: 이번 변경은 스프린트 2 요구사항 문서와 문서 인덱스·AI 활용 기록만 수정한다. 브라우저 검증 결과에 따라 후속 UX 구현 또는 ADR을 별도로 결정한다.
- 검증 근거: 현재 `ExtensionValidator`의 20자 제한, `ExtensionPolicyQuota`의 200개 quota, 전체 목록 조회 및 한도 초과 오류 계약을 `main` 구현과 스프린트 1 문서에서 확인했다. 새 개요 문서 작성 후 Markdown 공백 검사를 수행한다.
- 결과와 연결 문서: [`sprint-2-extension-limit-ux-validation.md`](docs/sprints/sprint-2/sprint-2-extension-limit-ux-validation.md), [`AGENTS.md`](AGENTS.md)
- 회고와 후속 조치: 브라우저에서 20자·200개·201개 경계를 검증한 뒤 결과를 분석 문서와 이 로그에 추가한다. 장기 불변식이나 페이징 전환 기준을 확정할 때만 ADR을 검토한다.

## 2026-08-30T18:10:00+09:00 — 동일 멱등키 처리 중 요청의 고착·중복 방지 검토

- 상태: 검토 중
- 시간 근거: 사용자가 동일 멱등키 요청이 `PROCESSING`일 때 영구 대기 또는 중복 파일 생성이 될 수 있는 문제를 제기한 현재 대화 흐름의 시각
- 스프린트/범위: 동일 키 동시 요청, 처리 중 응답, stale 복구와 FE 재시도 종료 조건
- 관련 문서·코드: [`sprint-1-upload-retry-idempotency-options.md`](docs/questions/sprint-1-upload-retry-idempotency-options.md), [`파일 업로드 API 명세`](docs/file-upload-api.md)
- 요청·질문 요약: 동일 키의 업로드가 진행 중일 때 예외 응답은 고착을 만들고, 무시하면 중복 파일이 생길 수 있으므로 안전한 처리 방법을 검토한다.
- 배경과 제약: 동기 파일 업로드는 파일시스템과 멱등성 기록이 한 트랜잭션이 아니다. 첫 처리의 응답 유실·프로세스 종료·대용량 처리 지연을 구분해야 한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `adr-predecision-review`
  - plugin/도구: 저장소 결정 전 검토 문서·API 계약 확인, `apply_patch`, `git diff --check`
- AI 제안: `PROCESSING`을 최종 예외가 아닌 중간 상태로 다루고, 동시 중복 요청은 파일 저장 없이 `202`와 재시도 안내를 반환한다. 서버는 lease·owner token·attempt version으로 stale 복구와 늦은 요청의 덮어쓰기를 막고, FE는 전체 대기 기한 후 `UNKNOWN`으로 종료한다.
- 사람의 판단과 이유: 미결정. 동기 요청의 bounded wait와 `202` 결과 조회를 도입할지, 처음부터 비동기 업로드 작업 모델로 갈지 결정이 필요하다.
- 코드·사용자 경험 영향: 현재는 문서만 갱신한다. 향후 처리 중 사용자는 무한 오류가 아니라 재시도 가능한 진행 상태를 보고, 서버 장애 후에는 만료·복구 규칙에 따라 같은 키를 재처리한다.
- 검증 근거: 기존 검토 문서의 `PROCESSING` 동시 요청·lease 필요성 언급을 구체화하고, 파일 저장 후 응답 유실과 프로세스 종료의 장애 구간을 연결했다.
- 결과와 연결 문서: [`sprint-1-upload-retry-idempotency-options.md`](docs/questions/sprint-1-upload-retry-idempotency-options.md)
- 회고와 후속 조치: `202`·`Retry-After`·결과 조회 API·lease 시간·고아 임시 파일 정리 규칙을 확정한 뒤 API 계약과 ADR을 갱신한다. 결정 전에는 처리 중 동시 요청 코드를 구현하지 않는다.

+
## 2026-08-30T18:12:54+09:00 — 업로드 상태 선저장과 원자적 파일 확정 결정

- 상태: 결정 채택
- 시간 근거: 사용자가 파일 쓰기 중 장애 가능성을 이유로 임시 경로 저장 후 최종 경로 이동 방식을 확인하고 ADR 작성을 요청한 현재 대화 시각
- 스프린트/범위: `UploadFile` 메타데이터와 로컬 파일시스템 사이의 동기 업로드 상태 일관성
- 관련 문서·코드: [`0014-persist-upload-state-before-file-and-finalize-atomically.md`](docs/adr/0014-persist-upload-state-before-file-and-finalize-atomically.md), [`15-state-consistency.md`](.internal-docs/file-upload-risk-analysis/15-state-consistency.md), [`0006-persist-upload-file-name-mapping.md`](docs/adr/0006-persist-upload-file-name-mapping.md), [`0003-server-generated-file-storage-policy.md`](docs/adr/0003-server-generated-file-storage-policy.md)
- 요청·질문 요약: DB에 업로드 상태를 먼저 저장하고, 파일을 임시 경로에 동기적으로 쓴 뒤 atomic move로 확정하고, 완료 상태 변경 후 응답하는 흐름을 ADR로 기록한다.
- 배경과 제약: DB와 파일시스템은 하나의 ACID 트랜잭션이 아니며, 최종 경로에 직접 쓰면 복사 중 프로세스 종료 시 부분 파일이 남을 수 있다. 현재 요구는 비동기 작업 API가 아니라 저장 완료 후 응답하는 동기 업로드다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 명시적 사용 기록 없음
  - plugin/도구: 기존 ADR·위험 분석·멱등성 검토 문서 확인, `apply_patch`, `git diff --check`
- AI 제안: `RECEIVING` 레코드를 먼저 커밋하고 같은 파일시스템의 임시 경로에 저장한 뒤 atomic move를 수행하며, `COMPLETED` 커밋 후에만 `201`을 반환하는 상태 모델을 제안했다.
- 사람의 판단과 이유: 채택. 사용자는 쓰기 중 장애로 인한 부분 파일을 최종 경로에 남기지 않기 위해 임시 경로와 원자적 이동을 사용하고, DB 선저장·파일 저장·완료 상태 변경 순서를 ADR로 남기기로 했다.
- 코드·사용자 경험 영향: 성공 응답은 동기 저장과 `COMPLETED` DB 커밋 후 반환한다. `RECEIVING`과 `FAILED` 상태는 외부 파일 제공 대상이 아니며, 응답 유실 후 중복 방지와 결과 조회는 별도 결정으로 남긴다.
- 검증 근거: 상태 일관성 분석의 DB·파일시스템 장애 구간, `UploadFile` 메타데이터 초안의 저장 순서, 서버 생성 파일명·로컬 저장 ADR을 대조하고 관련 문서 링크와 Markdown 변경을 점검한다.
- 결과와 연결 문서: [`0014-persist-upload-state-before-file-and-finalize-atomically.md`](docs/adr/0014-persist-upload-state-before-file-and-finalize-atomically.md), [`0006-persist-upload-file-name-mapping.md`](docs/adr/0006-persist-upload-file-name-mapping.md), [`AGENTS.md`](AGENTS.md)
- 회고와 후속 조치: 구현 시 DB 트랜잭션을 파일 I/O 동안 유지하지 않고, stale `RECEIVING` 복구·임시 파일 정리·파일 무결성 확인을 별도 구현 요구사항으로 구체화한다. 멱등성·재시도 정책은 결정 전 상태를 유지한다.

## 2026-08-30T18:17:35+09:00 — FE 재시도 한도와 서버 업로드 실패·삭제 전이 분리

- 상태: 검토 중
- 시간 근거: 사용자가 `Retry-After` 기반 FE 재시도 최대 횟수 도달 시 업로드 엔티티를 `FAILED`로 전환하고 비동기 삭제할지 질문한 현재 대화 시각
- 스프린트/범위: FE 관찰 재시도, 서버 저장 시도, 업로드 상태 전이와 비동기 파일 정리
- 관련 문서·코드: [`sprint-1-upload-retry-idempotency-options.md`](docs/questions/sprint-1-upload-retry-idempotency-options.md), [`0006-persist-upload-file-name-mapping.md`](docs/adr/0006-persist-upload-file-name-mapping.md)
- 요청·질문 요약: FE가 최대 3회 재시도한 뒤 `FileUpload`를 `FAILED`로 만들고 파일을 비동기로 삭제하는 방안을 검토한다.
- 배경과 제약: FE의 재시도는 서버가 작업을 재실행했다는 뜻이 아니다. 응답 유실 후 서버 저장이 성공했을 수 있으므로 FE 재시도 한도만으로 최종 파일 삭제를 결정하면 성공 파일을 잃을 수 있다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `adr-predecision-review`
  - plugin/도구: 저장소 결정 전 검토 문서·API 계약·ADR 확인, `apply_patch`, `git diff --check`
- AI 제안: `clientObservationCount`와 `storageAttemptCount`를 분리하고, FE 최대 재시도 도달은 화면을 `UNKNOWN`으로 종료하는 조건으로만 사용한다. `FAILED`·`ABANDONED`가 서버에서 확정된 임시 파일과 유예 기간이 지난 고아 파일만 비동기 삭제한다. 비동기 작업 모델에서는 `UploadOperation`과 `FileUpload`를 분리한다.
- 사람의 판단과 이유: 미결정. FE 한도·서버 lease·최종 파일 보존 기간·비동기 삭제 대상과 `FileUpload`/`UploadOperation` 분리 여부를 결정해야 한다.
- 코드·사용자 경험 영향: 현재는 문서만 갱신한다. 향후 사용자는 재시도 한도 초과 시 실패로 오인하지 않고 결과 확인 필요 상태를 보며, 서버는 확정된 실패 파일만 안전하게 정리한다.
- 검증 근거: 기존 멱등성 검토 문서의 `PROCESSING`·lease·임시 파일 원칙에 FE 재시도와 서버 저장 시도의 차이를 대조해 상태 전이와 삭제 조건을 추가했다.
- 결과와 연결 문서: [`sprint-1-upload-retry-idempotency-options.md`](docs/questions/sprint-1-upload-retry-idempotency-options.md)
- 회고와 후속 조치: `FAILED`를 결정하는 서버 조건과 결과 조회·보존 정책을 확정한 뒤 API 계약과 ADR로 전환한다. FE 재시도 횟수만으로 최종 저장 파일을 삭제하지 않는다.

## 2026-08-30T18:23:19+09:00 — 저장 deadline과 Retry-After·실패 처리 시점 분리

- 상태: 검토 중
- 시간 근거: 사용자가 넉넉한 파일 저장 타임아웃과 `Retry-After`를 맞추고 시간이 지나면 실패 처리하는 방식이 베스트 프랙티스인지 질문한 현재 대화 시각
- 스프린트/범위: 파일 저장 최대 실행 시간, FE 재시도 간격, 서버 실패 판정과 비동기 정리
- 관련 문서·코드: [`sprint-1-upload-retry-idempotency-options.md`](docs/questions/sprint-1-upload-retry-idempotency-options.md), [`파일 업로드 API 명세`](docs/file-upload-api.md), [RFC 9110 Retry-After](https://www.rfc-editor.org/rfc/rfc9110.html#section-10.2.3)
- 요청·질문 요약: 파일 저장 타임아웃을 넉넉하게 설정하고 그 시간에 맞춰 `Retry-After`를 정한 뒤, 시간이 지나면 업로드를 실패로 처리할지 검토한다.
- 배경과 제약: 저장 deadline, 후속 요청 대기 간격, FE 전체 대기 시간은 서로 다른 의미다. FE의 재시도 한도만으로 서버 저장 성공 여부나 최종 파일 삭제를 판단하면 응답 유실 성공을 잃을 수 있다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `adr-predecision-review`
  - plugin/도구: RFC 9110, Stripe 공식 멱등성 문서, 저장소 결정 전 검토 문서·API 계약 확인, `apply_patch`, `git diff --check`
- AI 제안: 저장 deadline은 서버 작업의 상한으로, `Retry-After`는 짧은 상태 확인 간격으로, FE 전체 제한은 UX 상한으로 분리한다. deadline 후에는 owner·lease를 확인해 `ABANDONED`/`FAILED_RETRYABLE`을 판정하고, 확정된 임시 파일만 비동기 삭제한다. 최종 파일은 멱등키 보존 기간 동안 즉시 삭제하지 않는다.
- 사람의 판단과 이유: 미결정. 동기 API의 bounded wait와 `409`, 또는 `202`와 상태 조회 API 중 하나를 선택하고 deadline·lease·보존 기간을 정해야 한다.
- 코드·사용자 경험 영향: 현재는 문서만 갱신한다. 향후 사용자는 `Retry-After`를 따른 제한된 확인을 수행하고, 결과 미확정 상태를 실패로 오인하지 않는다.
- 검증 근거: RFC 9110의 `Retry-After`와 `202 Accepted` 의미, Stripe 공식 문서의 멱등키 결과 저장·동시 요청·파라미터 비교 관행을 확인하고 기존 검토 문서에 반영했다.
- 결과와 연결 문서: [`sprint-1-upload-retry-idempotency-options.md`](docs/questions/sprint-1-upload-retry-idempotency-options.md)
- 회고와 후속 조치: 서버 deadline 후 실패 판정 조건과 동기·비동기 API 형태를 확정한 뒤 API 계약과 ADR로 전환한다. `Retry-After`를 저장 deadline과 동일한 값으로 고정하지 않는다.

## 2026-08-30T18:28:00+09:00 — 업로드 재시도·멱등키·상태 책임 분리 ADR 채택

- 상태: 채택
- 시간 근거: 사용자가 리트라이·멱등키·상태를 서로 독립적으로 관리하는 단순한 방향을 확정하고 ADR 생성을 요청한 현재 대화 시각
- 스프린트/범위: FE 재시도, `Idempotency-Key`, 서버 업로드 상태와 실패 파일 정리의 책임 경계
- 관련 문서·코드: [`0015-separate-upload-retry-idempotency-and-state.md`](docs/adr/0015-separate-upload-retry-idempotency-and-state.md), [`0014-persist-upload-state-before-file-and-finalize-atomically.md`](docs/adr/0014-persist-upload-state-before-file-and-finalize-atomically.md), [`sprint-1-upload-retry-idempotency-options.md`](docs/questions/sprint-1-upload-retry-idempotency-options.md)
- 요청·질문 요약: 리트라이는 리트라이대로, 멱등키는 동일 요청 식별대로, 상태는 실제 서버 저장 결과대로 분리하는 결정을 ADR로 기록한다.
- 배경과 제약: FE 재시도 횟수는 서버 저장 시도나 저장 실패를 의미하지 않는다. 응답 유실 후 저장 성공 가능성이 있으므로 FE 재시도 한도만으로 `FAILED` 전환이나 최종 파일 삭제를 수행하면 안 된다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `adr-predecision-review`
  - plugin/도구: 저장소 ADR·결정 전 검토 문서·API 계약 확인, `apply_patch`, `git diff --check`
- AI 제안: UUID v4 기반 멱등키는 논리적 업로드 동안 유지하고, 서버가 키·요청 지문·결과를 기억하도록 한다. FE 재시도 카운터는 서버 상태 전이에 사용하지 않으며, 확정된 실패 임시·고아 파일만 별도 정리한다.
- 사람의 판단과 이유: 채택. 사용자는 복잡한 타임아웃·lease·처리 중 HTTP 세부값은 후속 결정으로 남기고, 세 책임을 단순하게 분리하는 원칙을 ADR로 채택했다.
- 코드·사용자 경험 영향: 현재는 문서만 수정한다. 향후 FE는 같은 키로 재시도하고, 서버 상태는 실제 저장 결과만 표현하며, 재시도 포기만으로 성공 파일을 삭제하지 않는다.
- 검증 근거: ADR 0014의 `RECEIVING`·`COMPLETED`·`FAILED` 상태 및 임시 파일·원자적 확정 정책과 기존 멱등성 검토 문서를 대조했다. 새 ADR·문서 인덱스·기존 ADR 관련 링크를 갱신하고 Markdown 공백 검사를 수행한다.
- 결과와 연결 문서: [`0015-separate-upload-retry-idempotency-and-state.md`](docs/adr/0015-separate-upload-retry-idempotency-and-state.md), [`0013-use-request-id-and-frontend-owned-upload-messages.md`](docs/adr/0013-use-request-id-and-frontend-owned-upload-messages.md), [`sprint-1-upload-retry-idempotency-options.md`](docs/questions/sprint-1-upload-retry-idempotency-options.md)
- 회고와 후속 조치: 후속 구현 전 `Retry-After`, 처리 중 응답 코드, 멱등키 보존 기간, stale 복구와 비동기 정리의 구체값을 별도로 결정한다. ADR 0015의 원칙을 근거로 FE 재시도 횟수와 서버 상태 전이를 혼합하지 않는다.

## 2026-08-30T19:11:15+09:00 — 파일 업로드 로깅·모니터링 최소선 정리

- 상태: 수정 채택
- 시간 근거: 사용자가 `.internal-docs/file-upload-risk-analysis/18-logging-monitoring.md`에 로깅·메트릭 관점의 필수 제안을 요청한 현재 대화 시각
- 스프린트/범위: 파일 업로드 운영 관측성의 최소 로그·메트릭·알람 제안
- 관련 문서·코드: [`18-logging-monitoring.md`](.internal-docs/file-upload-risk-analysis/18-logging-monitoring.md), [`0012-preserve-policy-change-history-for-operations.md`](docs/adr/0012-preserve-policy-change-history-for-operations.md), [`0013-use-request-id-and-frontend-owned-upload-messages.md`](docs/adr/0013-use-request-id-and-frontend-owned-upload-messages.md), [`0014-persist-upload-state-before-file-and-finalize-atomically.md`](docs/adr/0014-persist-upload-state-before-file-and-finalize-atomically.md), [`0015-separate-upload-retry-idempotency-and-state.md`](docs/adr/0015-separate-upload-retry-idempotency-and-state.md)
- 요청·질문 요약: 로깅과 메트릭 중 운영에 필수적인 항목 위주로 위험 분석 문서를 정리한다.
- 배경과 제약: `requestId`·오류 코드·업로드 상태·정책 감사 이력은 기존 결정과 책임이 분리되어 있다. 원본 파일명·내용·내부 경로·사용자 식별자와 확장자별 메트릭 label은 개인정보 또는 고카디널리티 위험이 있다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `backend-documentation`
  - plugin/도구: 기존 ADR·스프린트 문서·구현 점검표·`build.gradle`·`application.yml` 확인, `apply_patch`, `git diff --check`
- AI 제안: 1차 운영선으로 `requestId` 기반 구조화 로그, 저카디널리티 결과·지연·저장 실패·디스크 메트릭, 저장 실패율·지연·디스크 여유 알람을 제안하고 분산 트레이싱과 사용자별 상세 분석은 후순위로 분리했다.
- 사람의 판단과 이유: 수정 채택. 사용자의 “필수적인 것 위주” 요청에 맞춰 기존 문서의 선택지·알람·메트릭을 최소 운영 질문 중심으로 재구성하고, 감사 이력과 일반 로그를 혼합하지 않도록 했다.
- 코드·사용자 경험 영향: 이번 변경은 내부 위험 분석 문서와 AI 활용 기록만 수정한다. 현재 코드에는 Actuator·Micrometer 의존성과 모니터링 설정이 없으므로 기능 구현 완료로 간주하지 않는다.
- 검증 근거: `build.gradle`에 Actuator·Micrometer가 없고 `application.yml`에 모니터링 설정이 없음을 확인했다. ADR 0012~0015의 감사 이력·requestId·업로드 상태·재시도 책임 경계를 문서 제안과 대조했으며, Markdown 공백 검사를 실행했다.
- 결과와 연결 문서: [`18-logging-monitoring.md`](.internal-docs/file-upload-risk-analysis/18-logging-monitoring.md), [`AGENTS.md`](AGENTS.md)
- 회고와 후속 조치: 실제 구현 시 requestId 생성 위치, 로그 보존·접근 권한, 디스크 임계값, 메트릭 backend를 운영 환경에 맞게 별도로 결정한다. 본 작업에서는 코드와 알람을 임의로 추가하지 않는다.

## 2026-08-30T19:13:50+09:00 — 화이트리스트 전환 조건과 단계적 마이그레이션 ADR 초안

- 상태: 검토 중
- 시간 근거: 사용자가 사용자·조직별 정책 연결 모델과 화이트리스트 전환 시점·방법을 ADR로 작성할 수 있는지 요청한 현재 대화 시각
- 스프린트/범위: 전역 denylist에서 사용자·조직별 정책 및 allowlist로 확장할 때의 장기 정책·전환 절차
- 관련 문서·코드: [`0016-migrate-to-allowlist-when-policy-requires.md`](docs/adr/0016-migrate-to-allowlist-when-policy-requires.md), [`19-future-policy-model.md`](.internal-docs/file-upload-risk-analysis/19-future-policy-model.md), [`0001-unify-extension-policies.md`](docs/adr/0001-unify-extension-policies.md)
- 요청·질문 요약: 사용자·조직별 정책은 관계 테이블로 연결하고, 화이트리스트 방식은 언제 적합하며 어떻게 안전하게 전환할지 ADR로 정리한다.
- 배경과 제약: 현재 `blocked=false`는 허용목록 항목이 아니라 해당 행을 차단하지 않는다는 의미다. 따라서 기존 행을 반전하는 단순 마이그레이션은 신규·미등록 확장자를 잘못 허용하거나 기존 정상 파일을 갑자기 차단할 수 있다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `adr-predecision-review`, `backend-documentation`
  - plugin/도구: 기존 ADR·위험 분석 문서 확인, `apply_patch`, `git diff --check`
- AI 제안: 현재 전역 denylist를 유지하고, 향후 정책 집합과 주체 연결 관계를 분리한다. 규제·폐쇄형 업무·허용목록 운영 체계·Shadow 평가·복귀 수단이 갖춰졌을 때만 신규 업로드부터 단계적으로 allowlist를 적용한다.
- 사람의 판단과 이유: 결정 전. 사용자의 관계 테이블 방향은 ADR 초안에 반영했지만, 전역 강제 차단의 override 여부, 기존 파일 소급 여부, Shadow 기간과 허용 실패율은 추가 확인이 필요하다.
- 코드·사용자 경험 영향: 이번 변경은 `proposed` ADR·문서 인덱스·AI 활용 기록만 수정한다. 즉시 화이트리스트 전환이나 사용자별 정책 코드 구현은 하지 않는다.
- 검증 근거: 현재 전역 정책과 화이트리스트 전환 위험을 기존 위험 분석 문서에서 확인하고, ADR 0001·0012·0013의 정책 모델·감사 이력·오류 안내 책임과 충돌하지 않도록 연결했다. Markdown 공백 검사를 실행했다.
- 결과와 연결 문서: [`0016-migrate-to-allowlist-when-policy-requires.md`](docs/adr/0016-migrate-to-allowlist-when-policy-requires.md), [`AGENTS.md`](AGENTS.md)
- 회고와 후속 조치: 제품 요구와 운영 데이터를 확인해 제안 상태를 accepted ADR로 확정하거나, 사용자·조직 정책의 구체적인 scope 우선순위와 API·스키마 요구사항을 별도 결정한다.

## 2026-08-30T19:53:29+09:00 — 확장자 규칙과 원본 파일명 범위 명확화

- 상태: 문서 표현과 스프린트 1 체크 상태 정정
- 요청·질문 요약: ADR 0010의 제한 대상이 전체 파일명인지 최종 확장자인지 확인하고 문서를 명확히 한다.
- 사람의 판단과 이유: 제한 대상은 전체 원본 파일명이 아니라 파일명에서 추출한 최종 확장자 값이다. 원본 파일명 보존·길이 정책은 ADR 0006의 별도 책임으로 유지한다.
- 코드·사용자 경험 영향: 애플리케이션 코드는 변경하지 않는다. ADR 0010, 스프린트 2 PRD·체크리스트, 스프린트 1 체크리스트의 표현과 실제 구현 상태를 일치시킨다.
- 검증 근거: `ExtensionName`과 `FileExtensionExtractor`의 최종 확장자 처리, ADR 0007의 최종 확장자 정책, 현재 허용 문자 검증 부재를 대조하고 Markdown 공백 검증을 통과했다.
- 결과와 연결 문서: [`0010-limit-extension-name-characters.md`](docs/adr/0010-limit-extension-name-characters.md), [`sprint-2-prd.md`](docs/sprints/sprint-2/sprint-2-prd.md), [`sprint-2-implementation-checklist.md`](docs/sprints/sprint-2/sprint-2-implementation-checklist.md), [`sprint-1-file-upload-checklist.md`](docs/sprints/sprint-1/sprint-1-file-upload-checklist.md)

## 2026-08-30T20:06:27+09:00 — ADR 0010 구현 보류 결정

- 상태: ADR 0010 구현 보류·문서 반영
- 요청·질문 요약: 한글·영문·숫자 전용 최종 확장자 제한의 필요성을 재검토하고 이번 스프린트 구현에서 제외한다.
- 사람의 판단과 이유: 현재 요구에서 해당 제한의 필요성이 확인되지 않았으므로 보류한다. 전체 원본 파일명 제한은 처음부터 이 ADR의 범위가 아니다.
- 코드·사용자 경험 영향: 애플리케이션 코드는 변경하지 않는다. 기존 빈 값·20자·점 검증과 `ExtensionName` 공유 구조를 유지하고, 스프린트 2 다음 구현 순서에서 ADR 0010을 제외한다.
- 검증 근거: `ExtensionName`, `ExtensionValidator`, `FileExtensionExtractor`의 현재 책임과 ADR 0007·0006의 범위를 대조했다.
- 결과와 연결 문서: [`0010-limit-extension-name-characters.md`](docs/adr/0010-limit-extension-name-characters.md), [`sprint-2-prd.md`](docs/sprints/sprint-2/sprint-2-prd.md), [`sprint-2-implementation-checklist.md`](docs/sprints/sprint-2/sprint-2-implementation-checklist.md), [`adr-implementation-status-review-2026-08-30.md`](docs/adr/adr-implementation-status-review-2026-08-30.md)

## 2026-08-30T20:14:21+09:00 — multipart 업로드 용량 정책을 10MB·12MB로 조정

- 상태: 사용자 결정 반영·문서 브랜치 구현 준비
- 요청·질문 요약: 파일 1개와 multipart 전체 요청의 용량 제한을 각각 10MB와 12MB로 변경한다.
- 사람의 판단과 이유: 파일 제한은 10MB로 낮추되 multipart boundary·헤더 오버헤드 때문에 정상적인 10MB 파일이 거부되지 않도록 전체 요청은 12MB로 둔다.
- 코드·사용자 경험 영향: `spring.servlet.multipart.max-file-size`와 `max-request-size`의 목표값, PRD·체크리스트·ADR의 경계값 테스트 기준을 10MB·12MB로 변경한다. 실제 설정과 413 handler 구현은 기능 브랜치에서 수행한다.
- 검증 근거: 10MB 파일과 10MB 전체 요청을 동일하게 두면 multipart 부가 정보로 정상 경계 파일이 거부될 수 있음을 확인하고, 12MB 여유를 선택했다.
- 결과와 연결 문서: [`0009-limit-multipart-upload-size.md`](docs/adr/0009-limit-multipart-upload-size.md), [`sprint-2-prd.md`](docs/sprints/sprint-2/sprint-2-prd.md), [`sprint-2-implementation-checklist.md`](docs/sprints/sprint-2/sprint-2-implementation-checklist.md), [`adr-implementation-status-review-2026-08-30.md`](docs/adr/adr-implementation-status-review-2026-08-30.md)

## 2026-08-30T20:19:30+09:00 — ADR 0009 multipart 용량 제한 구현

- 상태: 기능 구현·관련 테스트 완료
- 요청·질문 요약: 확정한 파일 10MB·전체 요청 12MB 제한을 애플리케이션에 적용하고 초과 요청을 413으로 반환한다.
- 사람의 판단과 이유: `FILE_SIZE_EXCEEDED` 오류 코드와 기존 `code`·`message` 응답 형식을 사용하고, `requestId`·`context` 확장은 ADR 0013 작업으로 분리한다.
- 코드·사용자 경험 영향: multipart 파싱 경계에서 10MB/12MB를 제한하고, 용량 초과를 안전한 메시지와 413으로 반환한다. Tomcat이 거부된 본문을 소진하도록 설정해 실제 HTTP 응답 연결을 유지한다.
- AI 활용 정보:
  - skill: `tdd`
  - 도구: `apply_patch`, `./gradlew test`
- 검증 근거: 설정값 단위 검증, 413 handler MockMvc 테스트, 실제 내장 Tomcat에서 파일 초과·전체 요청 초과 통합 테스트, 전체 `./gradlew test` 성공.
- 결과와 연결 문서: [`0009-limit-multipart-upload-size.md`](docs/adr/0009-limit-multipart-upload-size.md), [`파일 업로드 API 명세`](docs/file-upload-api.md), [`sprint-2-implementation-checklist.md`](docs/sprints/sprint-2/sprint-2-implementation-checklist.md)
- 회고와 후속 조치: 프록시·로드밸런서의 업로드 제한도 10MB/12MB와 일치하는지 운영 환경에서 확인하고, 다음 작업은 ADR 0011 저장 경로 외부화다.

## 2026-08-30T20:45:30+09:00 — ADR 0011 저장 루트 설정 외부화 구현

- 상태: 기능 구현·관련 테스트·문서 상태 갱신 완료
- 시간 근거: 사용자가 ADR 0011 구현 계획의 실행을 요청한 현재 대화 시각
- 스프린트/범위: `file.upload.storage-path` 설정으로 로컬 업로드 저장 루트 외부화
- 관련 문서·코드: [`0011-externalize-upload-storage-path.md`](docs/adr/0011-externalize-upload-storage-path.md), [`sprint-2-implementation-checklist.md`](docs/sprints/sprint-2/sprint-2-implementation-checklist.md), `LocalFileStorage`, `application.yml`
- 요청·질문 요약: 기존 `uploads` 고정 경로를 기본값으로 유지하면서 환경별 설정 override를 지원한다.
- 배경과 제약: 서버 생성 UUID 파일명, 기존 `FileStorage` 인터페이스, `FILE_UPLOAD_FAILED` 오류, 원본 파일명과 저장 경로 분리 동작은 유지한다. 기존 파일 이동·마이그레이션은 범위에서 제외한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `tdd`
  - plugin/도구: `apply_patch`, `./gradlew test`, Git
- AI 제안: 별도 설정 타입을 추가하지 않고 생성자 기반 `@Value` 주입으로 설정값을 `LocalFileStorage`에 전달하며 기존 `LocalFileStorage(Path)` 테스트 생성자를 유지한다.
- 사람의 판단과 이유: 수정 채택. 현재 구현에는 설정 바인딩 타입이 없고 저장소 구현이 하나이므로 새 공개 설정 타입을 만들지 않아 변경 범위와 의존성을 최소화했다.
- 코드·사용자 경험 영향: `file.upload.storage-path`를 변경하면 신규 업로드가 해당 경로에 저장된다. 설정이 없으면 `./uploads`를 사용하고 HTTP 응답 계약은 바뀌지 않는다.
- 검증 근거: override 테스트는 설정 미반영 상태에서 기본 경로 저장으로 Red를 확인한 뒤 설정 주입 구현 후 Green으로 전환했다. 기본 경로 회귀·저장 실패·원본 경로 분리·기존 업로드 테스트와 전체 `./gradlew test`가 통과했다.
- 결과와 연결 문서: 기능 커밋 `2745a89`, [`sprint-2-implementation-checklist.md`](docs/sprints/sprint-2/sprint-2-implementation-checklist.md), [`adr-implementation-status-review-2026-08-30.md`](docs/adr/adr-implementation-status-review-2026-08-30.md)
- 회고와 후속 조치: 설정된 새 저장 루트의 디렉터리·쓰기 권한·디스크 용량은 운영 환경에서 별도로 확인한다. 기존 `uploads/` 파일은 자동 이동하지 않는다.

## 2026-08-30T21:21:56+09:00 — 실행 가능한 MIME만 차단하는 정책 확정

- 상태: 사용자 결정 반영·기능 구현 준비
- 시간 근거: 현재 대화에서 사용자가 MIME 검증 범위를 실행 MIME 차단으로 확정한 시각
- 스프린트/범위: 스프린트 2 ADR 0005 콘텐츠 기반 MIME 검증
- 관련 문서·코드: [`0005-limit-upload-to-known-non-executable-types.md`](docs/adr/0005-limit-upload-to-known-non-executable-types.md), [`파일 업로드 API 명세`](docs/file-upload-api.md), [`sprint-2-implementation-checklist.md`](docs/sprints/sprint-2/sprint-2-implementation-checklist.md), `FileUploadServiceImpl`
- 요청·질문 요약: 확장자와 MIME이 일치하지 않는 모든 파일을 차단하지 않고, 실제 실행 가능한 MIME만 차단하며 일반 텍스트 파일을 허용한다.
- 배경과 제약: 확장자는 사용자가 바꿀 수 있지만 `.txt` 콘텐츠를 악성코드 검사 수준으로 분석할 수는 없다. 업로드·저장 단계에서 텍스트를 차단하면 정상 사용성이 낮아지므로 MIME 감지는 실행 바이너리 위장 방어에 한정한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `next-work-briefing`, `adr-predecision-review`, `tdd`
  - plugin/도구: `apply_patch`, Git, `request_user_input`
- AI 제안: Tika 콘텐츠 감지 결과가 실행 MIME 카탈로그에 포함될 때만 `BLOCKED_EXECUTABLE_MIME`으로 차단하고, `text/plain`·미확인 MIME·감지 실패는 경고 후 허용한다.
- 사람의 판단과 이유: 채택. 현재 요구의 핵심은 확장자 위장 실행 파일을 줄이는 것이며, 모든 비실행 형식 allowlist나 텍스트 의미 분석은 범위와 사용성 부담이 크다.
- 코드·사용자 경험 영향: `.txt`·`text/plain` 업로드가 허용되고, 실행 MIME 파일만 저장 전에 거부된다. 확장자와 MIME 불일치 자체는 오류가 되지 않는다.
- 검증 근거: 현재 업로드 흐름과 기존 테스트·ADR 0005를 대조했다. 구현 전 문서만 갱신했으며 MIME 기능 테스트는 후속 기능 브랜치에서 작성한다.
- 결과와 연결 문서: [`0005-limit-upload-to-known-non-executable-types.md`](docs/adr/0005-limit-upload-to-known-non-executable-types.md), [`파일 업로드 API 명세`](docs/file-upload-api.md), [`sprint-2-prd.md`](docs/sprints/sprint-2/sprint-2-prd.md), [`sprint-2-implementation-checklist.md`](docs/sprints/sprint-2/sprint-2-implementation-checklist.md)
- 회고와 후속 조치: 텍스트 안의 스크립트·HTML 의미 분석은 하지 않으며, 다운로드·미리보기·파싱 기능이 추가될 때 실행·렌더링 경계를 별도 검토한다. 다음 단계는 기능 브랜치에서 Tika 감지기와 실행 MIME 차단 테스트를 구현하는 것이다.

## 2026-08-30T21:34:07+09:00 — 실행 MIME 차단 구현 완료

- 상태: 기능 구현·관련 테스트·문서 상태 갱신 완료
- 시간 근거: 기능 커밋 `b171051`과 전체 `./gradlew test` 성공 시각
- 스프린트/범위: 스프린트 2 ADR 0005 실행 MIME denylist 기반 업로드 검증
- 관련 문서·코드: [`0005-limit-upload-to-known-non-executable-types.md`](docs/adr/0005-limit-upload-to-known-non-executable-types.md), [`파일 업로드 API 명세`](docs/file-upload-api.md), `ExecutableMimeCatalog`, `TikaMimeTypeDetector`, `FileUploadServiceImpl`
- 요청·질문 요약: 확장자와 MIME 불일치를 일괄 차단하지 않고 실행 가능한 MIME만 차단하며, `.txt`·`text/plain`과 미확인 MIME·감지 실패는 허용한다.
- 배경과 제약: 파일 확장자와 multipart `Content-Type`은 신뢰할 수 없지만 MIME 감지만으로 악성코드나 텍스트 스크립트 의미를 완전히 판정할 수 없다. 기존 확장자 denylist와 서버 생성 파일명 저장은 유지한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `tdd`
  - plugin/도구: `apply_patch`, `./gradlew test`, Git
- AI 제안: Tika를 파일 바이트에만 적용하고, 실행 MIME 카탈로그에 포함될 때 `BLOCKED_EXECUTABLE_MIME` 422로 차단한다. 미확인·감지 실패는 민감 정보 없이 경고 후 기존 정책으로 위임한다.
- 사람의 판단과 이유: 채택. 텍스트 파일 사용성을 보존하면서 명백한 바이너리 실행 파일 위장을 줄이는 수준을 현재 스프린트의 적정 보안 경계로 결정했다.
- 코드·사용자 경험 영향: `.txt` 파일은 정상 업로드되고, `MZ` 실행 파일을 `.txt`로 바꾼 대표 fixture는 저장 전에 거부된다. MIME 불일치 자체는 거부하지 않는다.
- 검증 근거: 카탈로그·Tika 감지기·서비스 단위 테스트, 실제 Tika `MZ` 헤더 테스트, MockMvc 오류 계약 테스트, 기존 업로드 회귀 테스트와 전체 `./gradlew test`가 성공했다.
- 결과와 연결 문서: 기능 커밋 `b171051`; 문서 후속 커밋에서 ADR 구현 상태·패키지 인덱스·체크리스트를 갱신한다.
- 회고와 후속 조치: `text/javascript`·`text/x-shellscript`의 의미 분석과 악성코드 검사는 하지 않는다. 다운로드·미리보기·파싱 기능을 추가할 때 실행·렌더링 경계를 별도 결정한다.

## 2026-08-30T21:36:51+09:00 — 최종 회귀 컴파일 오류 복구

- 상태: 검증 실패·수정 완료
- 시간 근거: 최종 `./gradlew test` 첫 실행 실패와 수정 후 재실행 성공 시각
- 스프린트/범위: 실행 MIME 차단 기능 최종 회귀 검증
- 관련 문서·코드: [`FileUploadServiceImpl`](src/main/java/com/example/demo/file/service/impl/FileUploadServiceImpl.java), 기능 커밋 `428950d`
- 요청·질문 요약: 문서 merge 후 전체 테스트를 실행하고 실패 원인을 수정한다.
- 배경과 제약: 명시적 import 정리 과정에서 `FileUploadService` import가 누락되어 전체 컴파일이 실패했다. MIME 로직이나 테스트 계약의 문제는 아니었다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `tdd`
  - plugin/도구: `./gradlew test`, `apply_patch`, Git
- AI 제안: 컴파일 오류의 누락 import를 복구한 뒤 동일한 전체 회귀 테스트를 다시 실행한다.
- 사람의 판단과 이유: 채택. 오류 원인이 명확한 단일 import 누락이므로 동작 변경 없이 import만 복구했다.
- 코드·사용자 경험 영향: 없음. 기능 동작은 변경하지 않고 빌드 가능 상태를 복구했다.
- 검증 근거: 첫 전체 `./gradlew test`는 `FileUploadService` symbol 오류로 실패했고, import 복구 후 전체 `./gradlew test`가 성공했다.
- 결과와 연결 문서: 기능 수정 커밋 `428950d`; 이 기록은 다음 docs merge commit에 포함한다.
- 회고와 후속 조치: 명시적 import 리팩터링 후에는 기존 compile cache에 의존하지 않고 전체 컴파일을 확인한다.

## 2026-08-30T22:26:08+09:00 — requestId와 멱등키 통합 정책 확정

- 상태: 수정 채택
- 시간 근거: 현재 대화에서 사용자가 requestId 통합 방향과 BE Retry-After 계산 방식을 확정한 시각
- 스프린트/범위: 스프린트 2 ADR 0006·0013·0014·0015 업로드 신뢰성
- 관련 문서·코드: [`0013-use-request-id-and-frontend-owned-upload-messages.md`](docs/adr/0013-use-request-id-and-frontend-owned-upload-messages.md), [`0015-separate-upload-retry-idempotency-and-state.md`](docs/adr/0015-separate-upload-retry-idempotency-and-state.md), [`0014-persist-upload-state-before-file-and-finalize-atomically.md`](docs/adr/0014-persist-upload-state-before-file-and-finalize-atomically.md), [`파일 업로드 API 명세`](docs/file-upload-api.md)
- 요청·질문 요약: 업로드 멱등키를 별도 식별자로 두지 않고 `Idempotency-Key` UUID v4를 논리적 업로드 `requestId`로 사용하며, 처리 중 재시도 대기값을 백엔드가 계산하도록 작업계획을 확정한다.
- 배경과 제약: 응답 유실 뒤 중복 파일을 만들지 않아야 하며, 현재 동기 multipart API와 별도 상태 조회 API를 유지한다. SHA-256 지문과 별도 attemptId는 범위에서 제외한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `tdd`
  - plugin/도구: `apply_patch`, Git, `request_user_input`
- AI 제안: `Idempotency-Key`를 표준 요청 헤더로 받고 응답·DB·로그의 `requestId`와 통합한다. 처리 중에는 `409`를 반환하고 백엔드가 capped exponential backoff와 jitter를 적용한 `Retry-After`를 제공한다.
- 사람의 판단과 이유: 수정 채택. 프로젝트의 단일 업로드 API에서는 식별자 하나가 구현·운영 복잡도를 줄인다. 다만 requestId는 HTTP 시도 ID가 아니라 논리적 업로드 ID가 되며, FE는 새 업로드마다 UUID를 생성해야 한다.
- 코드·사용자 경험 영향: 업로드 헤더가 필수가 되고, 완료 결과는 같은 requestId로 재사용된다. 처리 중 FE는 서버가 제공한 Retry-After를 따라 최대 3회·전체 30초까지 재시도한다.
- 검증 근거: 기존 API·ADR·서비스 구조를 정적 분석해 multipart 컨트롤러, 오류 handler, 로컬 저장소가 현재 requestId·멱등 상태를 구현하지 않음을 확인했다. 코드 구현 전 문서 계약만 갱신했다.
- 결과와 연결 문서: ADR 0006·0013·0014·0015, 스프린트 1 파일 업로드 API, 스프린트 2 PRD·구현 체크리스트·ADR 구현 상태 점검
- 회고와 후속 조치: SHA-256 미사용으로 같은 ID에 다른 파일이 와도 기존 작업 결과를 사용한다. 구현 시 ID 재사용·동시 요청·stale `RECEIVING` 테스트를 우선 작성하고, 문서 커밋을 기능 브랜치에 병합한 뒤 코드를 구현한다.

## 2026-08-30T22:49:40+09:00 — requestId 멱등 업로드 구현 및 회귀 검증 완료

- 상태: 구현 완료·브라우저 smoke 후속
- 시간 근거: 기능 커밋과 `./gradlew test`, `node --check` 실행 결과
- 스프린트/범위: requestId 통합 멱등 업로드, 상태 선저장·atomic move·stale 복구·FE 재시도
- 관련 문서·코드: 기능 커밋 `164a8e0`, ADR 0006·0013·0014·0015, `FileUploadRestController`, `UploadFile`, `FileUploadServiceImpl`, `extension-policy.js`
- 요청·질문 요약: `Idempotency-Key` UUID v4를 requestId로 통합하고, 처리 중 `409 + Retry-After`, 완료·실패 결과 재사용, FE 자동 재시도를 구현한다.
- 배경과 제약: 별도 attemptId·SHA-256 지문·상태 조회 API·비동기 worker는 도입하지 않는다. 새 논리 업로드마다 FE가 새 UUID를 생성해야 한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `tdd`
  - plugin/도구: `apply_patch`, `./gradlew test`, `node --check`, Git
- AI 제안: 상태를 파일 I/O와 분리해 `RECEIVING`을 먼저 커밋하고, 임시 저장·atomic move·완료 전환을 수행한다. 동일 requestId는 기존 결과를 재사용하고, 처리 중에는 서버 계산 Retry-After를 반환한다.
- 사람의 판단과 이유: 채택. 동기 multipart API의 범위를 유지하면서 응답 유실과 동시 요청의 중복 파일을 막고, FE 재시도와 서버 저장 상태를 분리한다.
- 코드·사용자 경험 영향: 업로드 헤더가 필수가 되고, 서버 오류에는 requestId·안전한 context가 포함된다. 처리 중에는 FE가 같은 키로 최대 3회·전체 30초까지 자동 재시도하고 이후 결과 확인 상태를 표시한다.
- 검증 근거: 전체 `./gradlew test` 99개 성공, `node --check src/main/resources/static/js/extension-policy.js` 성공, controller·integration·domain·recovery 테스트 추가. 브라우저 smoke는 아직 실행하지 않았다.
- 결과와 연결 문서: 체크리스트·ADR 구현 상태·PRD·API 계약을 갱신한다.
- 회고와 후속 조치: 키 보존 기간과 브라우저 접근성·반응형 smoke는 별도 운영·UX 검증으로 남긴다. 기존 미추적 `uploads/` 파일은 보존했다.

## 2026-08-31T00:44:25+09:00 — 정책 감사 이력 구현 계약 확정

- 상태: 채택
- 시간 근거: 현재 대화에서 정책 감사 이력 구현 계획의 세부 선택을 확정한 시각
- 스프린트/범위: 스프린트 2 ADR 0012 정책 변경 append-only 감사 이력
- 관련 문서·코드: [`0012-preserve-policy-change-history-for-operations.md`](docs/adr/0012-preserve-policy-change-history-for-operations.md), [`sprint-2-implementation-checklist.md`](docs/sprints/sprint-2/sprint-2-implementation-checklist.md), `ExtensionPolicyService`, `ExtensionPolicyInitializer`
- 요청·질문 요약: 정책 생성·초기화·고정 상태 변경·커스텀 삭제를 동일 트랜잭션의 감사 이력으로 보존하는 구현 계약을 확정한다.
- 배경과 제약: 커스텀 정책은 물리 삭제되므로 정책 ID와 확장자를 함께 보존해야 한다. 현재 정책 API에는 requestId가 없고 인증도 도입되지 않았다. 감사 조회 API와 관리자 화면은 범위에서 제외한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `create-jpa-domain`, `korean-domain-test-policy`, `tdd`
  - plugin/도구: `apply_patch`, Git, `./gradlew test`
- AI 제안: action을 `INITIALIZED`, `CREATED`, `BLOCKED_CHANGED`, `DELETED`로 고정하고, actor는 `SYSTEM`, 정책 requestId는 nullable로 기록하며, 동일 blocked 상태 PATCH는 이력을 남기지 않는다.
- 사람의 판단과 이유: 채택. 도메인 사건을 운영자가 구분할 수 있고, 현재 인증·요청 추적 계약을 무리하게 확장하지 않으면서 삭제 후 재등록을 정책 ID로 구분할 수 있다.
- 코드·사용자 경험 영향: 기존 정책 REST API 응답은 변경하지 않는다. 이후 정책 변경마다 현재 상태와 감사 이력이 함께 커밋되며, 동일 상태 PATCH는 감사 기록을 추가하지 않는다.
- 검증 근거: 현재 정책 서비스·초기화 트랜잭션과 관련 테스트를 확인했고, 기존 `./gradlew test`가 성공했다. 감사 이력 코드는 아직 구현 전이다.
- 결과와 연결 문서: ADR 0012, 스프린트 2 구현 체크리스트, ADR 구현 상태 점검 문서에 확정된 계약을 반영했다.
- 회고와 후속 조치: 문서 커밋 후 기능 브랜치에서 Red 테스트를 시작한다. 구현 완료 후 실제 테스트 결과와 패키지 인덱스를 별도 문서 커밋으로 갱신한다.

## 2026-08-31T00:52:56+09:00 — 정책 변경 감사 이력 구현 완료

- 상태: 채택
- 시간 근거: 기능 커밋 `38e1277`과 전체 회귀 테스트 통과 시각
- 스프린트/범위: 스프린트 2 ADR 0012 정책 변경 append-only 감사 이력
- 관련 문서·코드: `ExtensionPolicyAuditAction`, `ExtensionPolicyAuditHistory`, `ExtensionPolicyAuditHistoryRepository`, `ExtensionPolicyServiceImpl`, `ExtensionPolicyInitializer`
- 요청·질문 요약: 확정된 감사 이력 계약을 실제 정책 생성·초기화·변경·삭제 흐름에 연결하고 원자성을 검증한다.
- 배경과 제약: 기존 정책 API 응답은 유지하고, actor는 `SYSTEM`, 정책 requestId는 nullable, 동일 상태 PATCH는 무기록, 삭제 후 재등록은 정책 ID로 구분한다. 감사 조회 API와 관리자 화면은 도입하지 않는다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `create-jpa-domain`, `korean-domain-test-policy`, `tdd`
  - plugin/도구: `apply_patch`, Git, `./gradlew test`
- AI 제안: 정책 저장 직후 감사 이력을 같은 `@Transactional` 경계에 저장하고, 상태 변경 전 값을 캡처해 `BLOCKED_CHANGED`를 기록한다. 물리 삭제 전 정책 ID를 이력에 복사한다.
- 사람의 판단과 이유: 채택. 현재 서비스·초기화 트랜잭션을 유지하면서 별도 이력 테이블을 추가하는 최소 변경이며, 삭제 후 재등록과 실제 상태 변경을 운영적으로 구분할 수 있다.
- 코드·사용자 경험 영향: 정책 REST 응답과 업로드 사용자 경험은 변하지 않는다. 정책 변경 사건은 DB에 영속화되고, 감사 저장 실패 시 정책 변경도 롤백된다.
- 검증 근거: Red에서 누락 타입·이력 미기록·삭제 이력 누락을 확인한 뒤 수직 슬라이스별 Green으로 전환했다. 초기화·생성·상태 변경·동일 상태 무변경·삭제·재등록·감사 저장 실패 롤백 테스트와 전체 `./gradlew test`가 통과했다.
- 결과와 연결 문서: 기능 커밋 `38e1277`; docs 후속 커밋에서 ADR 0012 구현 상태, 스프린트 체크리스트, 패키지 인덱스를 갱신한다.
- 회고와 후속 조치: 정책 API requestId와 감사 조회 기능은 별도 결정 없이는 추가하지 않는다. 기존 미추적 `uploads/` 디렉터리는 커밋하지 않고 보존했다.

## 2026-08-31T01:11:47+09:00 — 정책 감사 이력 requestId 제거와 상태 enum 전환

- 상태: 수정 채택
- 시간 근거: 사용자가 diff comment로 현재 정책 감사 이력에는 requestId가 불필요하고 상태를 `BLOCKED`·`UNBLOCKED`로 표현할 수 있다고 판단한 시각
- 스프린트/범위: 스프린트 2 ADR 0012 정책 변경 append-only 감사 이력
- 관련 문서·코드: `ExtensionPolicyAuditHistory`, `ExtensionPolicyAuditState`, `ExtensionPolicyServiceImpl`, ADR 0012
- 요청·질문 요약: 현재 사용되지 않는 정책 감사 requestId를 제거하고 Boolean 전후 값을 명시적인 상태 enum으로 단순화한다.
- 배경과 제약: requestId는 업로드 API의 논리적 식별자이며 정책 API에는 현재 requestId 계약이 없다. 감사 이력은 생성·변경·삭제의 상태 차이를 설명해야 하므로 `BLOCKED`·`UNBLOCKED`와 정책 부재를 나타내는 nullable 전후 상태를 사용한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `create-jpa-domain`, `korean-domain-test-policy`
  - plugin/도구: `apply_patch`, Git, `./gradlew test`
- AI 제안: requestId를 nullable로 유지하거나 제거하는 선택지와 Boolean 전후 값 유지 또는 상태 enum 전환의 trade-off를 설명했다.
- 사람의 판단과 이유: 수정 채택. 현재 정책 API에서 생성되지 않는 requestId 컬럼은 제거하고, 상태 의미가 코드·DB에서 직접 드러나도록 `ExtensionPolicyAuditState`를 사용한다. 생성·삭제 이벤트의 정책 부재는 기존 action과 nullable 전후 상태로 구분한다.
- 코드·사용자 경험 영향: 정책 REST 응답과 업로드 requestId 계약은 변경하지 않는다. 감사 이력 DB 컬럼은 `before_state`·`after_state`로 표현되며 정책 감사 행에는 requestId가 저장되지 않는다.
- 검증 근거: Boolean getter 기반 테스트를 enum 상태 검증으로 먼저 전환해 Red를 확인했고, enum 매핑·서비스 변환 구현 후 관련 테스트와 전체 `./gradlew test`가 성공했다.
- 결과와 연결 문서: 기능 커밋 후속 refactor `145a830`; docs 후속 커밋에서 ADR 0012·체크리스트·구현 상태·패키지 인덱스를 동기화한다.
- 회고와 후속 조치: 기존 requestId 관련 기록은 당시 업로드·정책 계약 결정의 역사로 보존한다. 정책 감사 요청 추적이 필요해지면 별도 운영 결정으로 다룬다.

## 2026-08-31T01:15:16+09:00 — ADR 0016 allowlist 전환 결정 전 검토 원문 작성

- 상태: 검토 중
- 시간 근거: 다른 에이전트의 정책 감사 이력 리팩터링·문서 병합 완료 후 다음 스프린트 작업으로 착수한 시각
- 스프린트/범위: 스프린트 2 ADR 0016 정책 집합·allowlist 전환 결정 게이트
- 관련 문서·코드: [`docs/questions/sprint-2-allowlist-transition-options.md`](docs/questions/sprint-2-allowlist-transition-options.md), [`0016-migrate-to-allowlist-when-policy-requires.md`](docs/adr/0016-migrate-to-allowlist-when-policy-requires.md), [`19-future-policy-model.md`](.internal-docs/file-upload-risk-analysis/19-future-policy-model.md)
- 요청·질문 요약: 다른 에이전트 작업 완료 후 스프린트 2의 다음 실제 작업을 진행한다. 코드 구현에 앞서 ADR 0016의 결정 선택지를 정리한다.
- 배경과 제약: 현재 구현은 전역 denylist이며 사용자·조직 인증과 policy-set은 범위 밖이다. `proposed` ADR의 결정 게이트가 닫히기 전에는 스키마·API·실제 allowlist 차단을 구현하지 않는다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `adr-predecision-review`
  - plugin/도구: `apply_patch`, Git, 로컬 문서·코드 검색
- AI 제안: 전역 denylist 유지, scope별 정책 집합의 구체 범위 우선, 전역 강제 차단 guardrail을 포함한 정책 집합의 세 가지 선택지를 비교하고, 실제 allowlist 요구와 보안 불변식에 따라 조건부 추천을 제공한다.
- 사람의 판단과 이유: 검토 중. 최종 선택은 미등록 확장자의 기본 의미, GLOBAL 강제 차단의 override 가능 여부, 기본 mode, 소유자·승인자, shadow 기간·거부율, 복귀 기준에 대한 사용자 결정 후 확정한다.
- 코드·사용자 경험 영향: 현재 코드와 API는 변경하지 않는다. 결정 전까지 기존 denylist 동작과 신규 업로드 경계를 유지한다.
- 검증 근거: ADR 0016, 미래 정책 모델 문서, 현재 ExtensionPolicy·정책 판정 코드와 테스트 구조를 대조했다. Markdown 링크와 `git diff --check`는 문서 커밋 전에 확인한다.
- 결과와 연결 문서: 결정 전 검토 원문을 추가하고 문서 인덱스와 스프린트 체크리스트의 질문 문서 작성 항목을 갱신한다.
- 회고와 후속 조치: 사용자의 답변을 받은 뒤 ADR 0016과 API·데이터 모델 방향을 별도 문서 커밋으로 확정한다. 답변 전에는 policy-set 구현을 시작하지 않는다.

## 2026-08-31T01:18:30+09:00 — 정책 감사 이력 상태를 단일 값으로 단순화

- 상태: 수정 채택
- 시간 근거: 사용자가 감사 이력의 변경 전 상태는 직전 이벤트를 쿼리로 조회할 수 있으므로 단일 상태로 단순화하기로 확정한 시각
- 스프린트/범위: 스프린트 2 ADR 0012 정책 변경 append-only 감사 이력
- 관련 문서·코드: `ExtensionPolicyAuditHistory`, `ExtensionPolicyAuditState`, `ExtensionPolicyServiceImpl`, ADR 0012
- 요청·질문 요약: `beforeState`·`afterState`를 제거하고 이벤트 후 상태인 `state` 하나만 저장한다.
- 배경과 제약: 동일 정책 ID의 이력은 생성 시각과 이력 ID 순으로 정렬할 수 있고, `BLOCKED_CHANGED`의 이전 상태는 직전 이벤트의 state에서 재구성할 수 있다. 삭제 이벤트는 state를 `null`로 둔다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `create-jpa-domain`, `korean-domain-test-policy`
  - plugin/도구: `apply_patch`, Git, `./gradlew test`
- AI 제안: before/after를 유지하면 단건 조회는 쉽지만 중복 저장이 발생한다. 현재 감사 조회 API가 없고 쿼리 재구성이 허용되므로 이벤트 후 상태 하나를 사용하는 방향을 제시했다.
- 사람의 판단과 이유: 채택. `BLOCKED`·`UNBLOCKED`라는 도메인 상태를 직접 저장하고, 정책 부재는 action과 nullable state로 표현해 감사 이력 구조를 단순화한다.
- 코드·사용자 경험 영향: 감사 이력 컬럼이 `state` 하나로 줄어든다. 정책 API와 업로드 requestId 동작은 변경하지 않는다.
- 검증 근거: 테스트를 `getState()` 계약으로 먼저 전환해 Red를 확인한 뒤 엔티티·factory·서비스를 수정했고, 관련 테스트와 전체 `./gradlew test`가 성공했다.
- 결과와 연결 문서: refactor 커밋 `526d355` 이후 상태 단순화 변경; docs 후속 커밋에서 ADR 0012·체크리스트·구현 상태를 동기화한다.
- 회고와 후속 조치: 향후 감사 조회 기능을 추가할 때 동일 정책 ID의 직전 이력 조회 정렬 기준을 `createdAt`, 동률 시 `id`로 고정한다.

## 2026-08-31T15:16:50+09:00 — 업로드 FE 오류 메시지를 code·context 기준으로 전환

- 상태: 수정 채택
- 시간 근거: 사용자가 업로드 화면의 다음 작업을 진행하도록 승인한 뒤 FE 오류 표시 구현과 검증을 완료한 시각
- 스프린트/범위: 스프린트 2 ADR 0013 업로드 오류 메시지 책임과 브라우저 smoke
- 관련 문서·코드: `src/main/resources/static/js/extension-policy.js`, ADR 0013, 스프린트 2 구현 체크리스트, 스프린트 1 파일 업로드 API
- 요청·질문 요약: 업로드 오류 화면이 서버 `message` 문자열에 의존하지 않고 오류 `code`와 안전한 `context`로 사용자 문장을 조립하도록 수정한다.
- 배경과 제약: 서버는 `requestId`·`code`·`context`를 제공하지만 기존 FE 공통 함수는 `message`를 우선 반환했다. 정책 관리 API의 기존 메시지 계약과 성공 응답 문구는 이번 변경에서 유지한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `tdd`, `browser:control-in-app-browser`
  - plugin/도구: `apply_patch`, Node 정적 매핑 검사, 로컬 브라우저, `./gradlew test`
- AI 제안: 업로드 전용 오류 코드 매핑을 정책 API 오류 처리와 분리하고, `BLOCKED_EXTENSION`의 확장자 context만 안전하게 문장에 삽입하며 알 수 없는 코드는 기본 안내로 처리한다.
- 사람의 판단과 이유: 채택. requestId는 논리적 업로드 중복 방지 식별자이며 파일 업로드 만료 기능이 아니다. 보존기간·멱등키 정리는 현재 범위에 추가하지 않고, 파일 생명주기 정책이 필요해질 때 별도 결정한다.
- 코드·사용자 경험 영향: `BLOCKED_EXTENSION`, `FILE_SIZE_EXCEEDED`, `BLOCKED_EXECUTABLE_MIME`, `INVALID_REQUEST_ID`, `FILE_UPLOAD_FAILED`를 FE가 직접 매핑한다. 서버가 호환용 `message`를 보내도 업로드 오류 선택에는 사용하지 않는다.
- 검증 근거: Node 기반 매핑 확인에서 확장자 context·알 수 없는 code fallback·서버 message 무시를 확인했다. 로컬 브라우저에서 정상 `.txt` 업로드 성공, 차단 `env` 오류 문구, 320px 화면의 가로 overflow 부재를 확인했다. 전체 `./gradlew test`는 `BUILD SUCCESSFUL`로 완료했다.
- 결과와 연결 문서: 기능 커밋 `a89a5ef`; 후속 docs 커밋에서 ADR 구현 상태 점검·스프린트 API·체크리스트를 현재 코드 및 부분 smoke 결과에 맞춰 갱신한다.
- 회고와 후속 조치: MIME 거부·413 용량 초과·처리 중 409 재시도와 20자·200개·201번째 등록·키보드·스크린리더·200% 확대 검증을 남긴다. requestId 만료 구현은 다음 작업으로 올리지 않는다.

## 2026-08-31T15:48:17+09:00 — 스프린트 2 브라우저 UX 회귀 검증과 오류 복구 보완

- 상태: 수정 채택
- 시간 근거: 격리된 로컬 서버와 Codex In-app Browser에서 스프린트 2 최종 UX 시나리오를 실행하고, 오류 안내·포커스 결함의 수정과 재검증을 완료한 시각
- 스프린트/범위: 스프린트 2 최종 브라우저 smoke, 20자·200개·201번째 정책 한도, multipart·MIME·멱등 업로드 오류 안내
- 관련 문서·코드: `src/main/resources/static/js/extension-policy.js`, `src/main/resources/templates/index.html`, `docs/sprints/sprint-2/sprint-2-extension-limit-ux-validation.md`, `docs/sprints/sprint-2/sprint-2-implementation-checklist.md`, `docs/adr/adr-implementation-status-review-2026-08-30.md`
- 요청·질문 요약: 격리된 H2 DB·임시 저장 경로에서 계획된 최종 브라우저 UX와 실제 업로드 계약을 검증하고, 실패가 발견되면 최소 범위로 보완한다.
- 배경과 제약: API 계약·20자·200개 한도·allowlist 보류 정책은 변경하지 않는다. 브라우저 검증은 사용자 파일 대신 고정된 텍스트·MIME 위장·11MiB 테스트 파일을 사용한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱, Java 21, Spring Boot 로컬 서버
  - skill: `browser:control-in-app-browser`, `diagnose`
  - plugin/도구: Browser client, `apply_patch`, `curl`, Git, `./gradlew test`
- AI 제안: 정책 오류도 서버 message를 그대로 표시하지 않고 오류 code를 기준으로 한국어 안내를 조립하며, DOM 재렌더링 뒤에는 컨트롤을 다시 활성화한 다음 입력 포커스를 복귀시킨다. 정적 JS URL에는 버전 query를 붙여 브라우저 캐시로 이전 동작이 남지 않게 한다.
- 사람의 판단과 이유: 수정 채택. 21자·201번째 등록에서 서버의 영어 내부 메시지가 화면에 노출된 것은 사용자 경험 계약에 맞지 않아 `INVALID_EXTENSION`, `CUSTOM_LIMIT_EXCEEDED` 등 정책 code의 FE 한국어 매핑을 추가했다. 삭제·업로드 실패 뒤 body로 포커스가 사라진 것은 재시도 가능한 화면 조건을 위반하므로 삭제·추가 후 확장자 입력창, 업로드 종료 후 파일 입력창으로 복귀시켰다. API 응답·서버 오류 code는 변경하지 않았다.
- 코드·사용자 경험 영향: `src/main/resources/static/js/extension-policy.js`에 정책 오류 매핑과 포커스 복귀를 추가하고 `index.html`의 정적 JS에 `v=20260831` query를 추가했다. 사용자는 한도·입력·업로드 실패 사유를 한국어로 확인하고 즉시 재시도할 수 있다.
- 검증 근거: 정확히 20자 등록 성공, 21자 `INVALID_EXTENSION` 거부·입력 보존, `ux001`~`ux200` 200개 렌더링, 201번째 `409 CUSTOM_LIMIT_EXCEEDED`·입력 보존, 빈 목록 전환을 확인했다. 정상 `.txt` 업로드 성공, 실행 MIME `422 BLOCKED_EXECUTABLE_MIME`, 11MiB `413 FILE_SIZE_EXCEEDED`, 동일 requestId 동시 요청의 `201`·`409 IDEMPOTENCY_IN_PROGRESS`·`Retry-After: 2`를 확인했다. 기본 `1280px`, `320px`, `640px` 유효 폭에서 가로 overflow가 없었다. 오류 화면에는 내부 경로·stack trace가 없었다. 삭제·업로드 실패 후 포커스 복귀 수정과 전체 `./gradlew test`도 성공했다. IAB의 실제 409 화면 주입, OS VoiceOver, 실제 브라우저 200% zoom은 도구 제약으로 미확정이다.
- 결과와 연결 문서: 기능 커밋 `a840db2`; 스프린트 2 UX 검증 결과·체크리스트·ADR 구현 상태 점검을 현재 결과에 맞춰 갱신했다.
- 회고와 후속 조치: 브라우저 도구가 정적 리소스 캐시와 포커스 이벤트를 숨길 수 있어 새 탭·버전 query·DOM 상태 확인을 함께 사용했다. 실제 VoiceOver·200% zoom·409 화면 재시도 문구는 수동 환경 또는 결정적 주입 경로 확보 후 보완한다. ADR 0016 allowlist와 requestId 만료·정리는 계속 보류한다.

## 2026-08-31T16:54:41+09:00 — 스프린트 2 수동검증 일괄 실행

- 상태: 부분 검증 완료
- 시간 근거: 격리된 Spring Boot 서버·H2 DB·임시 저장 경로에서 API·정책 화면·반응형·오류 복구 시나리오를 실행한 시각
- 스프린트/범위: 스프린트 2 최종 수동검증 계획
- 관련 문서·코드: `src/main/resources/static/js/extension-policy.js`, `src/main/resources/templates/index.html`, `docs/sprints/sprint-2/sprint-2-extension-limit-ux-validation.md`, `docs/sprints/sprint-2/sprint-2-implementation-checklist.md`
- 요청·질문 요약: 기본 업로드, 정책 한도, 멱등성, 반응형, 키보드·스크린리더·확대 검증을 한 세션에 묶어 실행한다.
- 배경과 제약: 운영 데이터가 아닌 격리 환경을 사용했다. allowlist 구현과 requestId 만료·정리는 범위에 포함하지 않았다. 브라우저 UI에서 테스트 정책을 삭제하는 단계는 로컬 데이터 삭제 확인이 필요해 사용자 확인 전 보류했다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱, Java 21, Spring Boot 3.5.16, H2, Codex In-app Browser, macOS Chrome
  - skill: `backend-documentation`, `browser:control-in-app-browser`, `computer-use:computer-use`
  - 도구: `./gradlew bootRun`, `./gradlew test`, curl, Browser Playwright, macOS Computer Use, `apply_patch`
- AI 제안: 9MiB 정상 범위 fixture와 다중 동시 요청으로 처리 중 멱등성 경쟁을 재현하고, 브라우저 화면은 DOM·상태·포커스·viewport 수치와 접근성 트리로 확인한다.
- 사람의 판단과 이유: 채택. 확정된 구현의 동작 검증을 우선하고, 실행 파일 fixture는 Tika가 실제 실행 MIME으로 감지하는 MZ 헤더로 교체했다. 외부 Chrome이 사용자 조작으로 전환된 뒤에는 사용자 탭을 덮어쓰지 않고 VoiceOver·실제 200% 확대를 미확정으로 남겼다.
- 코드·사용자 경험 영향: 기능 코드 변경은 없다. 정상 `.txt` 업로드, MZ 실행 MIME `422 BLOCKED_EXECUTABLE_MIME`, 11MiB `413 FILE_SIZE_EXCEEDED`, 20자·21자, 200개·201번째 `409 CUSTOM_LIMIT_EXCEEDED`, 정책·업로드 오류 후 포커스 복귀를 확인했다.
- 검증 근거: 동일 UUID v4 requestId 12개 동시 업로드에서 `201`과 `409 IDEMPOTENCY_IN_PROGRESS`·`Retry-After: 2`를 확인했고 최종 저장 파일은 1개였다. 320px와 640px에서 `scrollWidth == clientWidth`였다. IAB의 Tab 키가 실제 포커스를 이동시키지 않았고, Chrome 외부 창은 사용자 조작으로 자동화 대상이 바뀌어 VoiceOver·실제 200% 확대·409 화면 주입은 실행하지 못했다. `./gradlew test`는 성공했다.
- 결과와 연결 문서: 스프린트 2 UX 검증 문서와 구현 체크리스트에 추가 결과를 기록했다.
- 회고와 후속 조치: 사용자 확인 후 UI 삭제·빈 상태를 검증한다. 사용자 조작이 없는 실제 Chrome 또는 수동 환경에서 VoiceOver·200% 확대·409 화면 분기를 보완한다.

## 2026-08-31T16:58:02+09:00 — 다중 확장자 차단 정책 채택

- 상태: 채택
- 시간 근거: 사용자 답변으로 확장자 경계 전체 검사와 기존 API 계약 유지가 확정된 대화 시각. 실제 사용자 메시지 메타데이터는 확인할 수 없어 작업 시각을 기록한다.
- 스프린트/범위: 파일 업로드 denylist의 다중 확장자 우회 방지
- 관련 문서·코드: [`ADR 0017`](docs/adr/0017-scan-all-extension-segments-for-upload-blocking.md), [`ADR 0007`](docs/adr/0007-use-final-file-extension-for-upload-blocking.md), [`파일 업로드 API`](docs/file-upload-api.md)
- 요청·질문 요약: `test.exe.pdf`, `test.jsp.png`처럼 중간 확장자에 차단 확장자가 있는 파일도 차단한다.
- 배경과 제약: 현재 구현은 최종 확장자만 검사한다. Tika·UUID 파일명·저장 경로 격리는 유지하고 새 오류 코드나 전체 확장자 목록 응답은 추가하지 않는다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `next-work-briefing`, `tdd`, `backend-documentation`
  - plugin/도구: 저장소 검색, Gradle 테스트, OWASP·CWE 공식 문서 검색
- AI 제안: basename의 모든 확장자 구간을 왼쪽부터 정확히 비교하고 첫 번째 차단 구간만 기존 `context.extension`에 반환한다.
- 사람의 판단과 이유: 채택. denylist 정책의 파일명 우회를 줄이면서 정상적인 복합 파일명과 기존 REST 오류 계약을 유지하기로 했다.
- 코드·사용자 경험 영향: 차단 정책이 중간 확장자에도 적용되고, 사용자에게는 기존과 동일한 `BLOCKED_EXTENSION` 응답이 제공된다.
- 검증 근거: 구현 전 기준 브랜치에서 `./gradlew test` 성공. 구현 후 다중 구간·대소문자·부분 문자열·파일 미저장 테스트를 추가한다.
- 결과와 연결 문서: ADR 0017, API 계약, 스프린트 정책·체크리스트를 갱신했다.
- 회고와 후속 조치: 구현 시 모든 구간의 정확한 경계 비교를 유지하고, allowlist 전환·MIME 불일치 차단은 별도 범위로 둔다.

## 2026-08-31T17:02:55+09:00 — 다중 확장자 차단 구현 및 회귀 검증

- 상태: 채택
- 시간 근거: 기능 구현 커밋과 `./gradlew test` 완료 시각
- 스프린트/범위: 파일 업로드 denylist의 다중 확장자 구간 검사
- 관련 문서·코드: [`FileExtensionExtractor`](src/main/java/com/example/demo/file/service/FileExtensionExtractor.java), [`FileUploadServiceImpl`](src/main/java/com/example/demo/file/service/impl/FileUploadServiceImpl.java), [`다중 확장자 테스트`](src/test/java/com/example/demo/service/impl/FileUploadServiceTests.java)
- 요청·질문 요약: 중간 확장자까지 차단하고 기존 `BLOCKED_EXTENSION` API 계약을 유지한다.
- 배경과 제약: 저장 파일명·Tika MIME 검사·저장 위치 격리·멱등성은 변경하지 않는다. 부분 문자열은 차단하지 않는다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `tdd`, `next-work-briefing`, `backend-documentation`
  - plugin/도구: `apply_patch`, Gradle Wrapper, Git
- AI 제안: 기존 최종 확장자 추출을 호환 유지하고 전체 구간을 별도로 반환해 서비스에서 왼쪽부터 정책을 판정한다.
- 사람의 판단과 이유: 채택. 여러 차단 구간이 있을 때 첫 번째 구간만 오류 context에 노출하고, REST 응답 구조는 확장하지 않았다.
- 코드·사용자 경험 영향: `test.exe.pdf`, `test.jsp.png`가 저장 전에 차단되고 `test.exefoo.pdf`는 허용된다.
- 검증 근거: Red 단계에서 `extractAll` 미구현 컴파일 실패를 확인한 뒤 Green 구현을 적용했다. 관련 단위·서비스·HTTP 통합 테스트와 전체 `./gradlew test`가 성공했고 `git diff --check`도 통과했다.
- 결과와 연결 문서: 기능 커밋 `c94f50a`, ADR 0017, API 계약, 스프린트 체크리스트에 반영했다.
- 회고와 후속 조치: 빈 중간 구간은 기존 파일명 호환성을 위해 정책 토큰에서 제외했다. allowlist 전환과 MIME 일반 불일치 차단은 별도 결정으로 유지한다.

## 2026-08-31T17:07:24+09:00 — MIME 감지 IOException fail-close 정책 채택

- 상태: 채택
- 시간 근거: 사용자가 `application/octet-stream`과 Tika 분석 `IOException`을 구분하고, 감지 실패는 `FILE_TYPE_DETECTION_FAILED`로 거부하자는 구현 방향을 확정한 대화 시각. 실제 사용자 메시지 메타데이터는 확인할 수 없어 작업 시각을 기록한다.
- 스프린트/범위: MIME 콘텐츠 감지 결과의 unknown·failed 처리와 업로드 보안 게이트
- 관련 문서·코드: [`ADR 0005`](docs/adr/0005-limit-upload-to-known-non-executable-types.md), [`ADR 0018`](docs/adr/0018-fail-closed-on-mime-detection-failure.md), [`파일 업로드 API`](docs/file-upload-api.md)
- 요청·질문 요약: 분석 성공 후 형식을 특정하지 못한 `UNKNOWN` MIME은 기존 확장자 정책으로 처리하되, 분석 자체가 `IOException`으로 실패하면 업로드를 중단한다.
- 배경과 제약: 기존 구현은 `MimeTypeDetectionResult`의 `FAILED`를 `UNKNOWN`과 함께 fallback 처리했다. 실행 MIME denylist, `application/octet-stream` 허용, 서버 생성 파일명, 저장 위치 격리, allowlist·바이러스 검사 범위 제외는 유지한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `next-work-briefing`, `tdd`
  - plugin/도구: 저장소 검색, Git, OWASP File Upload Cheat Sheet 확인
- AI 제안: `FAILED`를 독립 상태로 처리하고 `500 FILE_TYPE_DETECTION_FAILED`를 반환하며, 감지 실패는 업로드 예약 전에 거부한다.
- 사람의 판단과 이유: 채택. 콘텐츠 기반 보안 검증을 수행하지 못한 경우 기존 확장자 정책으로 허용하면 MIME 보안 계층이 장애 상황에서 fail-open이 되므로 fail-close가 더 적합하다고 판단했다. `application/octet-stream`까지 차단하면 정상 파일 사용성이 저하될 수 있어 `UNKNOWN` fallback은 유지한다.
- 코드·사용자 경험 영향: 감지 실패 파일은 정책 조회·예약·물리 저장 전에 거부되고, FE는 오류 코드 기반으로 `파일 형식을 확인하지 못했습니다. 잠시 후 다시 시도해 주세요.`를 표시한다. 감지 실패는 예약 전 발생하므로 같은 `requestId` 재요청은 다시 감지를 시도한다.
- 검증 근거: 기존 코드에서 `isUnknown()`이 `FAILED`를 포함하고, 서비스가 실패 결과를 경고 후 계속 처리하는 것을 확인했다. OWASP 공식 파일 업로드 문서에서 확장자·콘텐츠 타입·signature·저장 위치 등 다중 방어 계층 권고를 확인했다. 구현 테스트와 전체 `./gradlew test`는 기능 브랜치에서 수행한다.
- 결과와 연결 문서: ADR 0018을 추가하고 ADR 0005, 스프린트 1 API, 스프린트 2 PRD·체크리스트, `AGENTS.md` 문서 인덱스를 갱신한다.
- 회고와 후속 조치: 구현 시 `UNKNOWN`과 `FAILED`의 테스트를 분리하고, 새 REST 오류가 기존 `FILE_UPLOAD_FAILED`와 혼동되지 않는지 확인한다. MIME allowlist와 악성코드 검사는 별도 결정으로 유지한다.

## 2026-08-31T17:10:50+09:00 — MultipartException 내부 메시지 외부 노출 제거

- 상태: 수정 채택
- 시간 근거: 사용자가 `MultipartException`의 `exception.getMessage()`를 외부 응답에서 제거하고 내부 로그와 분리하도록 요청한 시각
- 스프린트/범위: 파일 업로드 API 오류 응답의 내부 정보 비노출
- 관련 문서·코드: [`FileUploadExceptionHandler`](src/main/java/com/example/demo/file/exception/handler/FileUploadExceptionHandler.java), [`FileUploadRestControllerTests`](src/test/java/com/example/demo/controller/FileUploadRestControllerTests.java), [`ADR 0013`](docs/adr/0013-use-request-id-and-frontend-owned-upload-messages.md)
- 요청·질문 요약: multipart 예외 메시지에 Spring/Tomcat 구현 세부사항이나 임시 파일 경로가 포함될 수 있으므로, 외부에는 고정된 `INVALID_FILE` 응답을 반환하고 내부 로그에만 예외와 `requestId`를 남긴다.
- 배경과 제약: 업로드 오류 응답은 `code`·`requestId`·안전한 `context`를 중심으로 하며, `InvalidFileException`의 기존 메시지 동작과 다른 업로드 오류 계약은 이번 범위에서 변경하지 않는다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `diagnose`
  - plugin/도구: `apply_patch`, Gradle Wrapper, Git, MockMvc
- AI 제안: 내부 경로를 포함한 `MultipartException`을 서비스 경계에서 재현하고, 응답 메시지를 `잘못된 파일 요청입니다.`로 고정하며 예외 객체는 `requestId`와 함께 `warn` 로그 원인으로 전달한다.
- 사람의 판단과 이유: 채택. `MultipartException`의 구현 메시지는 외부 계약에 필요한 정보가 아니므로 제거하고, 운영 추적에 필요한 원인과 `requestId`는 서버 로그에 보존한다. 변경 범위를 해당 예외 유형으로 제한해 기존 기능별 오류 메시지 계약을 불필요하게 바꾸지 않는다.
- 코드·사용자 경험 영향: `MultipartException` 응답은 `INVALID_FILE`, `잘못된 파일 요청입니다.`로 고정되고 내부 multipart 파싱 메시지·경로는 응답에서 사라진다.
- 검증 근거: 수정 전 내부 경로가 포함된 예외 메시지 테스트가 실패하는 Red 상태를 확인했다. 수정 후 대상 MockMvc 테스트와 관련 컨트롤러 테스트, `MultipartFileCountIntegrationTests`, 전체 `./gradlew test --rerun-tasks`가 성공했다. 중간의 전체 테스트 실패는 겹쳐 실행한 Gradle 작업이 동일한 XML 결과 파일을 동시에 쓰며 발생한 실행 환경 충돌이었고, 단일 재실행으로 재현되지 않았다. `git diff --check`도 통과했다.
- 결과와 연결 문서: 파일 업로드 예외 handler와 회귀 테스트를 갱신했다. ADR 0013의 내부 예외 메시지·stack trace 비노출 결정과 일치하므로 별도 ADR 변경은 없다.
- 회고와 후속 조치: 기본 Spring 오류 처리 설정에도 `server.error.include-message`·`include-stacktrace`를 명시적으로 두는 운영 설정 검토는 별도 범위로 남긴다.

## 2026-08-31T17:20:50+09:00 — MIME 감지 fail-close 구현 및 회귀 검증

- 상태: 채택
- 시간 근거: 기능 커밋 `7c69a29`와 전체 Gradle 테스트가 완료된 시각
- 스프린트/범위: MIME 콘텐츠 감지 실패를 fail-close로 처리하는 ADR 0018 구현
- 관련 문서·코드: [`MimeTypeDetectionResult`](src/main/java/com/example/demo/file/service/MimeTypeDetectionResult.java), [`FileUploadServiceImpl`](src/main/java/com/example/demo/file/service/impl/FileUploadServiceImpl.java), [`FileUploadExceptionHandler`](src/main/java/com/example/demo/file/exception/handler/FileUploadExceptionHandler.java), [`ADR 0018`](docs/adr/0018-fail-closed-on-mime-detection-failure.md)
- 요청·질문 요약: `UNKNOWN` MIME은 기존 확장자 정책으로 fallback하고 Tika `IOException`에 해당하는 `FAILED` MIME은 `FILE_TYPE_DETECTION_FAILED`로 업로드를 거부한다.
- 배경과 제약: 실행 MIME denylist, `application/octet-stream` 허용, 기존 저장·멱등성·파일명 정책은 유지한다. 기존 multipart 변경은 별도 작업으로 분리했다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `tdd`, `next-work-briefing`
  - plugin/도구: `apply_patch`, Gradle Wrapper, Git, MockMvc, Node syntax check
- AI 제안: 결과 객체의 `UNKNOWN`·`FAILED` 판정을 분리하고, 실패 시 저장 전용 오류와 REST 계약을 추가한다.
- 사람의 판단과 이유: 채택. MIME 분석을 수행하지 못한 콘텐츠를 확장자 정책만으로 허용하면 보안 검증 계층이 장애 시 fail-open이 되므로 거부한다. `application/octet-stream`은 분석 성공 후 미확인 결과이므로 기존 사용성을 유지한다.
- 코드·사용자 경험 영향: 감지 실패는 정책 판정·예약·파일 저장 전에 중단된다. API는 `500 FILE_TYPE_DETECTION_FAILED`와 빈 `context`를 반환하고 FE는 `파일 형식을 확인하지 못했습니다. 잠시 후 다시 시도해 주세요.`를 표시한다.
- 검증 근거: Red 단계에서 새 예외 클래스 부재로 테스트 컴파일 실패를 확인한 뒤 Green 구현을 적용했다. MIME 서비스·Tika detector·REST 테스트와 전체 `./gradlew test`가 성공했고 `node --check src/main/resources/static/js/extension-policy.js`, `git diff --check`도 통과했다.
- 결과와 연결 문서: 기능 커밋 `7c69a29`; docs 커밋 `f602502` 및 후속 구현 상태·검증 기록 문서 커밋으로 반영한다.
- 회고와 후속 조치: Tika IOException의 원인은 내부 로그에만 남기고 외부 응답에는 노출하지 않는다. MIME allowlist·악성코드 검사·Retry-After는 별도 범위로 유지한다.

## 2026-08-31T21:39:30+09:00 — 파일 하나 업로드 계약과 오류 코드 분리

- 상태: 채택
- 시간 근거: 현재 대화에서 파일 두 개 요청의 HTTP 의미를 확정하고 Red·Green 테스트를 실행한 시각
- 스프린트/범위: `POST /api/v1/files`의 multipart 파일 개수 검증과 오류 응답
- 관련 문서·코드: [`FileUploadRestController`](src/main/java/com/example/demo/file/controller/FileUploadRestController.java), [`FileUploadExceptionHandler`](src/main/java/com/example/demo/file/exception/handler/FileUploadExceptionHandler.java), [`FileUploadRestControllerTests`](src/test/java/com/example/demo/controller/FileUploadRestControllerTests.java), [`MultipartFileCountIntegrationTests`](src/test/java/com/example/demo/controller/MultipartFileCountIntegrationTests.java), [`파일 업로드 API 명세`](docs/file-upload-api.md)
- 요청·질문 요약: 파일 part가 두 개 이상인 요청을 한 개만 받도록 바꾸고, 새 오류 코드로 반환한다.
- 배경과 제약: 컨테이너의 `max-part-count: 1`은 파일 수와 요청 크기를 구분하지 못해 두 파일 요청을 `413 FILE_SIZE_EXCEEDED`로 오표기했다. `Idempotency-Key`는 multipart part가 아닌 헤더이며, 향후 비파일 part 추가도 고려해야 한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `tdd`
  - plugin/도구: `apply_patch`, Gradle Wrapper, MockMvc, TestRestTemplate, Node syntax check
- AI 제안: 컨테이너 전체 part 제한을 제거하고 컨트롤러에서 `file` 목록이 두 개 이상인지 판정한 뒤, 별도 기능 예외·FE 오류 code로 변환한다.
- 사람의 판단과 이유: 채택. 파일 개수 위반은 크기 제한이 아니라 잘못된 요청이므로 `400 MULTIPLE_FILES_NOT_ALLOWED`로 분리한다. 서비스·저장소 호출 전 거부해 저장 상태와 파일을 만들지 않는다.
- 코드·사용자 경험 영향: 업로드 API는 정확히 하나의 `file` part만 허용한다. 두 개 이상이면 requestId를 포함한 새 오류 코드가 반환되고, 화면은 `한 번에 파일 1개만 업로드할 수 있습니다.`를 표시한다.
- 검증 근거: 두 파일 MockMvc 테스트를 먼저 추가해 기존 코드의 `NullPointerException` Red 상태를 확인했다. 컨트롤러·예외 handler 구현 후 해당 테스트와 실제 HTTP 통합 테스트가 통과했고, JavaScript 문법 검사를 실행했다.
- 결과와 연결 문서: 기능 커밋 `7944a30`; API 명세, 스프린트 2 PRD·체크리스트, README를 새 오류 계약으로 갱신했다.
- 회고와 후속 조치: HTTP multipart part 전체 상한은 파일 수 정책과 다르다. 대량 field part에 대한 별도 자원 보호가 필요해지면 오류 의미와 허용 필드 계약을 먼저 결정한다.
