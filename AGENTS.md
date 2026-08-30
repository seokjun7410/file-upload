# AI 에이전트 작업 지침

이 문서는 저장소 전체에 적용되는 공통 작업 지침이다. 모든 AI 에이전트는 작업을 시작하기 전에 이 문서와 관련 문서를 읽고, 요청 범위를 벗어나는 구현을 하지 않는다.

## 1. 저장소 개요

- 기술 스택: Java 21, Spring Boot 3.5, Spring Data JPA, H2 파일 DB, Thymeleaf
- 빌드 도구: Gradle Wrapper (`./gradlew`)
- 애플리케이션 패키지: `com.example.demo`
- 현재 상태: 스프린트 1의 정책 도메인·JPA 저장소·quota 기반 커스텀 등록 한도·고정 정책 초기화·정책 REST API·Axios 정책 관리 화면·multipart 파일 저장 및 업로드 정책 적용까지 구현되어 있다.
- 현재 기준 브랜치: `main`

## 2. 코드 작성 규칙 인덱스

- 코드의 가독성, 책임 분리, 도메인 모델링, Lombok 사용, 예외·트랜잭션·테스트 작성 기준은 [`docs/code-writing-guidelines.md`](docs/code-writing-guidelines.md)를 따른다.
- 이 문서는 작업 범위·문서 인덱스·브랜치·커밋·검증 절차를 정의하고, 세부 코드 표현 규칙은 위 문서에서 관리한다.

## 3. 요구사항 범위와 의사결정

- 사용자가 요청한 요구사항보다 과하게 코드를 반영하지 않는다.
- 추가 구현이 권장되거나 필요하다고 판단되면 먼저 사용자에게 묻는다.
- 선택이 필요한 추가 기능은 의사결정이 끝나기 전까지 임의로 구현하지 않는다.
- 질문은 문서로 전달하고, 질문의 배경과 사용자의 답변을 기록한다. 질문 문서에는 다음을 포함한다.
  - 왜 추가 결정이 필요한가
  - 선택지별 장점과 단점은 무엇인가
  - 각 선택이 코드와 사용자 경험에 미치는 영향은 무엇인가
  - 기대 결과를 이해할 수 있는 구체적인 예시는 무엇인가
- 사용자의 답변을 문서로 확인한 뒤 합의된 내용의 성격에 맞는 문서로 전환한다.

### ADR 작성 정책

- ADR에는 비즈니스 불변식, 장기간 유지할 정책, 데이터·모듈 경계, 상태 일관성과 실패 처리처럼 구현이 바뀌어도 유지해야 하는 아키텍처 결정을 기록한다.
- 스프린트 구현 범위, 작업 순서와 완료 상태, 브랜치·커밋 절차, 테스트 도구와 검증 결과, 파일·메서드 수준 구현 방법은 ADR에 기록하지 않는다. 해당 내용은 질문 문서, 스프린트 문서, 체크리스트 또는 AI 활용 기록에 둔다.
- 합의된 내용에 비즈니스·정책·아키텍처 결정이 없으면 ADR을 만들지 않는다. 기존 ADR에 성격이 다른 내용이 섞이면 적절한 문서로 이동하거나 제거한다.
- 기능 작업은 `feat/<작업명>` 브랜치에서 진행한다.
- 스프린트가 끝나고 테스트가 통과하면 `main`에 병합한다.
- 문서 산출물은 코드와 별도 라이프사이클로 관리한다. 문서 변경은 `docs` 브랜치에서 진행하고 주기적으로 `main`과 병합한다.
- 병합 전 다음을 확인한다.
  - 미커밋 코드 변경이 남아 있지 않은가
  - 실패한 테스트가 없는가
  - 필요한 문서 변경이 병합 대상에 포함되었는가
- 병합 후 기준 브랜치에서 테스트를 다시 실행한다.
- 브랜치는 사용자가 요청하거나 명시적으로 동의한 경우에만 삭제한다.

### 커밋 규칙

- 커밋·브랜치·병합·push 절차의 단일 기준은 이 `AGENTS.md`다. 다른 문서에는 커밋 실행 규칙을 중복해서 작성하지 않는다.
- 하나의 커밋에는 하나의 목적만 담고 Conventional Commit 형식을 사용한다.
  - 새로운 사용자 기능과 아직 커밋되지 않은 신규 기능 코드는 `feat: <작업 요약>`으로 커밋한다.
  - 이미 커밋된 기능의 동작을 유지하면서 패키지·구조·가독성만 바꾸는 변경은 `refactor: <작업 요약>`으로 분리한다.
  - 요구사항·API 계약·패키지 인덱스·AI 활용 기록 같은 문서 변경은 `docs: <작업 요약>`으로 분리한다.
- 신규 기능과 기존 코드 리팩터링이 같은 파일에 섞이면 기존 동작만 보존하는 refactor 중간 상태를 먼저 만들고 테스트한 뒤, 신규 기능을 feat 변경으로 적용한다.
- 커밋 전 `git diff --cached --name-status`와 `git diff --cached --check`로 staged 경로와 공백 오류를 확인한다.
- 코드 커밋은 관련 테스트와 `./gradlew test`가 통과한 뒤 생성한다. 각 refactor·feat 커밋은 가능하면 해당 커밋 단독으로 컴파일되고 테스트를 통과해야 한다.
- 문서 파일을 기능 커밋이나 refactor 커밋에 섞지 않는다. 문서 변경은 아래 동기화 절차에 따라 `docs` 브랜치에서 먼저 커밋한다.
- `docs` 브랜치의 직접 커밋은 `AGENTS.md`와 `docs/**`만 변경한다. `src/**`, 빌드 파일, 애플리케이션 리소스, 테스트 코드는 포함하지 않는다.
- 기능 브랜치의 직접 커밋은 `AGENTS.md`와 `docs/**`를 변경하지 않는다. 확정된 문서는 `docs` 브랜치의 merge commit으로만 기능 브랜치에 반영한다.
- 최종 보고 전 `git log --first-parent --no-merges --name-only main..<브랜치>`로 각 브랜치의 직접 커밋 경로를 감사한다. 기능 브랜치의 문서 직접 커밋이나 `docs` 브랜치의 코드 직접 커밋을 발견하면 완료로 보고하지 않는다.

### 문서 브랜치와 기능 브랜치 동기화 절차

문서가 기능 작업의 기준이 되는 경우에는 다음 순서를 지킨다.

1. `docs` 브랜치로 이동하고, 스프린트 문서·API 명세·`AGENTS.md` 및 AI 활용 기록을 최신 내용으로 갱신한다.
2. 문서 인덱스에 새 문서와 변경된 문서의 목적·상태가 반영되었는지 확인한다.
3. `git status`로 문서 변경을 확인하고, 문서 파일만 Conventional Commit 형식으로 커밋한다. 예: `docs: update file upload sprint specification`
4. 문서 브랜치의 커밋을 원격 `origin/docs`에 push한다. push가 실패하면 권한·인증 문제를 확인할 때까지 원격 반영 완료로 간주하지 않는다.
5. `main`으로 이동해 최신 기준을 확인한 뒤 `feat/<작업명>` 기능 브랜치를 생성한다.
6. 기능 브랜치에서 `docs`를 병합해 기능 구현자가 확정된 문서와 최신 `AGENTS.md`를 기준으로 작업하도록 한다.
7. 병합 후 `git log`와 `git status`로 문서 커밋이 기능 브랜치에 포함되었고, 의도하지 않은 변경이 없는지 확인한다.

문서 파일을 문서 브랜치에서 커밋하지 않은 채 기능 브랜치로 이동하지 않는다. 기능 브랜치에 남아 있는 미추적 문서는 문서 브랜치의 병합 결과가 아니므로, 문서 기준으로 사용할 수 없다.

## 4. 테스트 우선 구현

기능 구현 전 TDD/BDD 방식으로 행동과 테스트를 먼저 고정한다.

1. 구현 목표의 행동 명세와 기대 결과를 정의한다.
2. 이해하기 쉬운 한글 `@DisplayName` 테스트를 작성한다.
3. 테스트를 실행해 Red 상태와 실패 원인을 확인한다.
4. 테스트를 통과시키는 최소 Green 구현을 작성한다.
5. 중복을 제거하고 가독성 중심으로 리팩터링한다.

테스트는 테스트를 통과시키기 위한 과도한 구현이나 테스트 무력화를 허용하지 않는다. 도메인 규칙은 도메인 단위 테스트로 먼저 검증하고, 이후 JPA 저장소 테스트와 MockMvc 또는 통합 테스트로 저장·화면·요청 흐름을 검증한다.

### 테스트 유지보수 원칙

- 컬렉션의 개수나 순서가 해당 테스트의 계약이 아니라면 통합·화면 테스트에서 `hasSize`, 배열 인덱스, 전체 목록의 순서를 하드코딩하지 않는다. 의미 있는 식별자나 필드로 항목을 찾고 상태·필드 존재·빈 목록 여부처럼 행동을 검증한다.
- 요구사항이 고정 카탈로그의 개수·순서를 명시한 경우에만 원천 카탈로그·초기화·API 계약 테스트 중 가장 가까운 한 곳에서 정확한 값을 검증한다. 같은 개수와 순서 단언을 서비스·REST·통합 테스트에 반복하지 않는다.
- `@SpringBootTest`는 HTTP→서비스→DB처럼 계층을 가로지르는 동작을 검증할 때 사용한다. 같은 컨텍스트에서 검증할 수 있는 변경 흐름은 하나의 시나리오로 묶고, 단순한 계층 동작에는 순수 단위 테스트·JPA slice·`@WebMvcTest`를 우선해 불필요한 애플리케이션 기동을 늘리지 않는다.
- 페이지 테스트는 HTTP 응답과 JavaScript가 의존하는 최소 DOM 앵커만 검증한다. 외부 리소스 URL·WebJar 버전·항목별 HTML 문자열처럼 구현 세부에 묶인 단언은 피하고, 스크립트 로딩과 실제 네트워크 요청은 브라우저 smoke 검증으로 보완한다.

## 5. 문서 인덱스

문서를 변경하거나 기능을 구현할 때 아래 문서를 관련 범위에 따라 먼저 확인한다.

| 문서 | 역할 | 현재 상태 |
|---|---|---|
| [`docs/sprints/sprint-1-file-upload-extension-policy.md`](docs/sprints/sprint-1-file-upload-extension-policy.md) | 스프린트 목표, 기대 결과, 구현 순서, 테스트 목록, 완료 조건, 범위 제외 사항 | 정책 관리·파일 업로드 구현 완료 기준 |
| [`docs/sprints/sprint-1-file-upload-api.md`](docs/sprints/sprint-1-file-upload-api.md) | 확장자 정책 조회·변경, 커스텀 확장자, 파일 업로드 API 계약과 오류 형식 | 정책·파일 업로드 API 계약 |
| [`docs/sprints/sprint-1-file-upload-checklist.md`](docs/sprints/sprint-1-file-upload-checklist.md) | API 계약 기준 FE·BE 구현과 테스트·수동 검증의 완료 조건 | 스프린트 1 정책 관리·파일 업로드 구현 및 검증 완료 |
| [`docs/questions/sprint-1-extension-policy-modeling-options.md`](docs/questions/sprint-1-extension-policy-modeling-options.md) | fixed/custom 엔티티 모델과 검증 책임 컨벤션의 ADR 전 선택지·장단점·추천 의견 | ADR 결정 배경 원문 |
| [`docs/questions/sprint-1-fixed-policy-change-screen-options.md`](docs/questions/sprint-1-fixed-policy-change-screen-options.md) | 고정 확장자 PATCH 화면 범위·실패 복구·프런트 검증 선택지와 사용자 결정 | 요구사항·ADR 결정 배경 원문 |
| [`docs/questions/sprint-1-file-upload-storage-and-error-options.md`](docs/questions/sprint-1-file-upload-storage-and-error-options.md) | 파일 저장 위치·서버 생성 파일명·업로드 오류 코드 선택지와 사용자 결정 | 요구사항·ADR 결정 배경 원문 |
| [`docs/adr/0001-unify-extension-policies.md`](docs/adr/0001-unify-extension-policies.md) | fixed/custom을 단일 `ExtensionPolicy` 엔티티와 유형으로 관리하는 결정과 결과 | accepted |
| [`docs/adr/0002-use-server-policy-state-as-source-of-truth.md`](docs/adr/0002-use-server-policy-state-as-source-of-truth.md) | 고정 정책 변경 결과가 불확실할 때 서버 저장 상태를 기준으로 일관성을 복구하는 결정 | accepted |
| [`docs/adr/0003-server-generated-file-storage-policy.md`](docs/adr/0003-server-generated-file-storage-policy.md) | 서버 생성 파일명·로컬 저장 위치·업로드 오류 상태와 코드의 장기 정책 | accepted |
| [`docs/adr/0004-use-extension-name-value-object.md`](docs/adr/0004-use-extension-name-value-object.md) | 확장자 정규화·검증을 `ExtensionName` 값 객체로 통합하는 모듈 경계 | accepted |
| [`docs/adr/0005-limit-upload-to-known-non-executable-types.md`](docs/adr/0005-limit-upload-to-known-non-executable-types.md) | Tika 기반 MIME 감지와 알려진 비실행 파일 형식 허용 범위 | accepted |
| [`docs/adr/0006-persist-upload-file-name-mapping.md`](docs/adr/0006-persist-upload-file-name-mapping.md) | 원본 파일명·서버 저장 파일명 매핑을 `UploadFile` 메타데이터로 영속화하고 원본 파일명 길이를 제한하는 정책 | proposed |
| [`docs/adr/0007-use-final-file-extension-for-upload-blocking.md`](docs/adr/0007-use-final-file-extension-for-upload-blocking.md) | 다중 점 파일명의 최종 확장자만 기준으로 업로드 차단하는 정책 | accepted |
| [`docs/adr/0010-limit-extension-name-characters.md`](docs/adr/0010-limit-extension-name-characters.md) | 커스텀 확장자 이름을 한글·영문·숫자로 제한하는 입력 정책 | accepted |
| [`docs/adr/0009-limit-multipart-upload-size.md`](docs/adr/0009-limit-multipart-upload-size.md) | multipart 업로드 파일 1개를 100MB, 전체 요청을 110MB로 제한하는 정책 | accepted |
| [`docs/code-writing-guidelines.md`](docs/code-writing-guidelines.md) | 코드 가독성·책임 분리·도메인 모델링·Lombok·예외·트랜잭션·테스트 작성 규칙 | 세션 리팩터링 기준으로 정리한 코드 작성 기준 |
| [`docs/ai-usage-guidelines.md`](docs/ai-usage-guidelines.md) | AI 프롬프트·스킬·플러그인·검증·회고를 누적 기록하는 방법과 필수 항목 | AI 기록 작성 기준 |
| [`PROMPT_LOG.md`](PROMPT_LOG.md) | 실제 AI 활용 과정에서 식별된 요구사항, 판단, 검증 결과와 회고의 누적 기록 | 제출용 누적 기록 |

### 문서 갱신 규칙

- 문서의 요구사항, API 계약, 실패 의미와 구현이 다르면 구현에 맞춰 문서를 함께 검토한다.
- 확정되지 않은 선택은 코드에 숨기지 말고 질문 문서에 기록한다.
- 합의가 끝난 선택은 의사결정 문서로 남긴다.
- AI가 요구사항·예외·위험·대안·검증 결과·사용자 결정을 식별하면 [`PROMPT_LOG.md`](PROMPT_LOG.md)에 즉시 기록한다. 기록 형식과 누락 방지 규칙은 [`docs/ai-usage-guidelines.md`](docs/ai-usage-guidelines.md)를 따른다.
- `PROMPT_LOG.md`의 각 기록 제목은 사건 발생 시각을 `YYYY-MM-DDTHH:MM:SS+09:00` 형식으로 시작하고, 파일 전체는 사건 시각 오름차순을 유지한다. 뒤늦게 발견한 과거 기록은 해당 위치에 삽입하고 기록 근거를 밝힌다.
- 커밋·브랜치 전환·최종 보고 전에는 이번 작업의 결정·실패·수정·검증 결과와 `PROMPT_LOG.md`를 대조한다. 로그를 추가하지 않으면 최종 보고에 기록 대상이 아니었던 이유를 명시한다.
- API 동작을 바꾸면 해당 API 계약 문서를 먼저 또는 같은 변경으로 갱신한다. 현재 스프린트 1 API 문서는 `docs/sprints`에 둔다.
- 스프린트 범위·완료 조건·제외 사항을 바꾸면 `docs/sprints` 문서를 갱신한다.
- 새 문서를 추가하면 이 인덱스에 목적과 상태를 추가한다.

## 6. 패키지 인덱스

패키지 책임을 지키고, 새 패키지나 주요 타입을 추가하면 이 인덱스를 갱신한다.

| 경로 | 책임 | 주요 구성 |
|---|---|---|
| `src/main/java/com/example/demo` | Spring Boot 애플리케이션 진입점과 전역 구성 | `DemoApplication` |
| `src/main/java/com/example/demo/common` | JPA 공통 식별자, 초기 데이터 구성, 공통 REST 오류 응답과 전역 예외 | `BaseEntity`, `EntityNotFoundException`, `ExtensionPolicyInitializer`, `ExtensionPolicyRestExceptionHandler`, `ErrorResponse` |
| `src/main/java/com/example/demo/file/controller` | Thymeleaf 페이지와 정책·파일 업로드 REST 요청 처리 | `FileUploadPageController`, `ExtensionPolicyRestController`, `FileUploadRestController` |
| `src/main/java/com/example/demo/file/controller/dto/req` | 정책 REST 요청 값을 표현하고 필수값을 검증 | 정책 요청 record |
| `src/main/java/com/example/demo/file/controller/dto/res` | 엔티티·업로드 결과를 REST 응답으로 변환 | 정책·파일 업로드 응답 record |
| `src/main/java/com/example/demo/file/domain` | 확장자 정책 유형, 고정 카탈로그, 정규화·검증 규칙 | `FixedExtensionCatalog`, `PolicyType`, `ExtensionNormalizer`, `ExtensionValidator` |
| `src/main/java/com/example/demo/file/domain/entity` | 확장자 정책과 등록 한도를 표현하는 JPA 엔티티 | `ExtensionPolicy`, `ExtensionPolicyQuota` |
| `src/main/java/com/example/demo/file/domain/entity/vo` | 엔티티와 서비스가 사용하는 정규화·검증된 값 객체 | `ExtensionName` |
| `src/main/java/com/example/demo/file/repository` | 확장자 정책과 등록 한도의 영속성 저장소 인터페이스 | `ExtensionPolicyRepository`, `ExtensionPolicyQuotaRepository` |
| `src/main/java/com/example/demo/file/exception` | 확장자 정책·파일 업로드 요청이 실패한 의미를 표현하는 커스텀 예외 | 정책·파일 입력·차단·저장 실패 예외 |
| `src/main/java/com/example/demo/file/exception/handler` | 도메인·기능별 예외를 REST 오류 응답으로 변환 | `ExtensionPolicyExceptionHandler`, `FileUploadExceptionHandler` |
| `src/main/java/com/example/demo/file/service` | 정책·파일 업로드 기능의 인터페이스와 파일 저장 포트 | `ExtensionPolicyService`, `FileUploadService`, `FileStorage` |
| `src/main/java/com/example/demo/file/service/impl` | 정책 저장소·quota·파일명 추출·로컬 파일 저장을 조정해 서비스 구현 | `ExtensionPolicyServiceImpl`, `FileExtensionExtractor`, `FileUploadServiceImpl`, `LocalFileStorage` |
| `src/main/resources` | 애플리케이션 설정과 정적·템플릿 리소스 | `application.yml` |
| `src/main/resources/static/js` | Axios로 정책을 조회·변경하고 파일 업로드 결과를 화면에 반영 | `extension-policy.js` |
| `src/main/resources/templates` | 서버 렌더링 화면 | `index.html` |
| `src/test/java/com/example/demo` | 애플리케이션 통합 테스트 | `DemoApplicationTests` |
| `src/test/java/com/example/demo/common` | 초기 데이터 구성 테스트 | `ExtensionPolicyInitializerTests` |
| `src/test/java/com/example/demo/controller` | 파일 업로드 페이지와 정책 REST 요청·응답·DB 통합 테스트 | `FileUploadPageControllerTests`, `ExtensionPolicyRestControllerTests`, `ExtensionPolicyApiIntegrationTests` |
| `src/test/java/com/example/demo/domain` | 확장자 정책 도메인·JPA·quota 테스트 | `ExtensionPolicyDomainTests`, `ExtensionPolicyRepositoryTests`, 정규화·검증·값 객체 테스트 |
| `src/test/java/com/example/demo/service` | 확장자 정책 등록·중복·최대 개수·동시성 테스트 | `ExtensionPolicyServiceTests` |
| `src/test/java/com/example/demo/service/impl` | 파일명 확장자 추출과 업로드 서비스 orchestration 테스트 | `FileExtensionExtractorTests`, `FileUploadServiceTests` |

### 패키지 설계 원칙

- 도메인 규칙은 `domain`에 두고, 컨트롤러 계층이 규칙을 직접 재구현하지 않도록 한다.
- 컨트롤러 계층은 요청을 해석하고 적절한 애플리케이션·도메인 동작을 호출하며, DTO의 변환 메서드로 화면 응답을 조립한다.
- 저장소 구현 세부사항은 저장소 인터페이스 뒤에 감추고, 호출자가 저장 방식에 의존하지 않도록 한다.
- 새 기능은 기존 패키지 책임을 먼저 확인한 뒤 가장 작은 책임 단위로 추가한다.
- 패키지 간 의존 방향이 흐려지거나 새로운 계층이 필요하면 구현 전에 그 이유와 영향 범위를 문서로 기록한다.

## 7. 기능 구현 시 기본 검증

- 관련 문서와 패키지 인덱스를 읽고 작업 범위를 확인한다.
- `./gradlew test`로 전체 테스트를 실행한다.
- 실패한 테스트가 있으면 원인을 확인한 뒤 수정하고 다시 실행한다.
- 커밋 또는 최종 보고 전에 `PROMPT_LOG.md` 기록 대상을 확인하고, 필요한 기록은 `docs` 브랜치에서 먼저 반영한다.
- 변경 파일, 테스트 결과, 문서 갱신 여부를 최종 보고에 포함한다.
