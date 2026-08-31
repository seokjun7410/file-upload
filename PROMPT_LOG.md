# AI 활용 기록

이 문서는 제출용 AI 활용 기록이다. 프롬프트·AI 제안·사용한 skill/plugin·사람의 판단·검증 결과·회고를 사건 발생 시각 기준으로 누적한다.

작성 규칙은 [`docs/ai-usage-guidelines.md`](docs/ai-usage-guidelines.md)를 따른다. 각 제목은 `YYYY-MM-DDTHH:MM:SS+09:00` ISO-8601 형식으로 시작하므로 문자열 정렬만으로 시간순 정렬할 수 있다. 새 사건은 맨 아래에 추가하고, 뒤늦게 발견한 과거 사건은 시간 근거를 밝힌 뒤 올바른 위치에 삽입한다.

## 2026-08-28T14:45:00+09:00 — Spring Boot 최소 프로젝트 구성 확정

- 상태: 수정 채택
- 시간 근거: Gradle Wrapper 생성과 애플리케이션 기동 로그의 실행 시각 및 대화 순서. 사용자 메시지의 정확한 시각 메타데이터는 확인할 수 없어 정렬용 시각으로 기록한다.
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
- 시간 근거: Thymeleaf 페이지 추가 직후 실행한 Gradle 테스트·bootRun 로그의 실행 시각 및 대화 순서. 정확한 사용자 메시지 시각은 확인할 수 없어 정렬용 시각으로 기록한다.
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

## 2026-08-28T15:00:00+09:00 — IntelliJ 프로젝트 열기

- 상태: 채택
- 시간 근거: 앞선 페이지 구현과 Git 초기 커밋 사이의 대화 순서. 사용자 메시지의 정확한 시각 메타데이터가 없어 정렬용 시각으로 기록한다.
- 스프린트/범위: 로컬 개발 환경 연결
- 관련 문서·코드: 프로젝트 경로 `/Users/hong/Documents/ChatGPT/파일업로드`
- 요청·질문 요약: 생성한 프로젝트 경로를 IntelliJ IDEA에서 연다.
- 배경과 제약: IntelliJ IDEA Ultimate가 설치되어 있었고 기존 프로젝트 창이 열려 있었다. 작업 폴더의 코드나 설정을 변경하지 않는다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `computer-use`
  - plugin/도구: IntelliJ IDEA UI 상태 확인, macOS 앱 열기
- AI 제안: IntelliJ의 프로젝트 열기 동작으로 현재 폴더를 새 프로젝트로 연결한다.
- 사람의 판단과 이유: 채택. UI 접근성 상태에서 기존 프로젝트 검색 패널이 먼저 잡혀 앱 경로로 직접 폴더를 전달하는 방식으로 보완했다.
- 코드·사용자 경험 영향: IntelliJ 창 제목이 `파일업로드`로 표시되며 해당 프로젝트를 바로 개발할 수 있다. 코드 동작에는 영향이 없다.
- 검증 근거: IntelliJ IDEA 상태에서 프로젝트 창 제목 `파일업로드`를 확인했다.
- 결과와 연결 커밋: 코드·문서 커밋 없음.
- 회고와 후속 조치: IDE 연결은 로컬 환경 작업이므로 저장소 변경과 별도로 관리한다.

## 2026-08-28T15:11:48+09:00 — Git main 브랜치와 init 커밋

- 상태: 수정 채택
- 시간 근거: 실제 초기 커밋 `65d1ee9`의 커미터 시각. 원격 push 시도는 같은 작업 흐름으로 기록한다.
- 스프린트/범위: 로컬 Git 초기화와 GitHub 원격 반영 준비
- 관련 문서·코드: Git 저장소 메타데이터, 초기 커밋 `65d1ee9`
- 요청·질문 요약: `file-upload` 저장소에 `main` 브랜치와 `init` 커밋으로 올린다.
- 배경과 제약: 기존 `.git` 디렉터리는 있었지만 커밋이 없었다. 원격 주소는 없었고 GitHub CLI 인증 상태도 유효하지 않았다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: Git 브랜치·커밋, GitHub CLI 상태 확인
- AI 제안: 로컬 브랜치를 `main`으로 정리하고 `init` 커밋을 만든 뒤 GitHub 원격 저장소를 생성·push한다.
- 사람의 판단과 이유: 수정 채택. 로컬 `main` 브랜치와 `init` 커밋은 완료했지만, 인증 실패 상태에서 원격 생성이나 push를 성공으로 기록하지 않았다.
- 코드·사용자 경험 영향: 프로젝트 파일이 로컬 Git으로 추적되기 시작했다. GitHub에는 당시 반영되지 않았다.
- 검증 근거: `git status`에서 `main`과 clean 상태, `git log -1`에서 `65d1ee9 init`을 확인했다. `gh auth status`는 유효하지 않은 인증 상태를 반환했고 원격은 없었다.
- 결과와 연결 커밋: `65d1ee9 init`; GitHub push는 미완료.
- 회고와 후속 조치: 인증·원격 저장소 생성 여부는 로컬 커밋과 분리해 확인한다. 토큰이나 인증값은 기록하지 않는다.

## 2026-08-28T16:10:48+09:00 — 에이전트 지침과 문서·패키지 인덱스 생성

- 상태: 채택
- 시간 근거: 커밋 `65d292f` 작성 시각
- 스프린트/범위: 저장소 공통 AI 작업 지침과 파일 업로드 확장자 정책 문서 탐색
- 관련 문서·코드: [`AGENTS.md`](AGENTS.md), [`docs/sprints/sprint-1/sprint-1-file-upload-extension-policy.md`](docs/sprints/sprint-1/sprint-1-file-upload-extension-policy.md), [`docs/sprints/sprint-1/sprint-1-file-upload-api.md`](docs/sprints/sprint-1/sprint-1-file-upload-api.md)
- 요청·질문 요약: 모든 AI가 읽을 수 있는 에이전트 지침에 가독성, 범위 준수, 의사결정 기록, TDD와 문서·패키지 인덱스를 포함한다.
- 배경과 제약: 기존 저장소에는 Spring Boot 예제 코드와 파일 업로드 설계 문서가 있었지만 공통 에이전트 지침은 없었다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: 저장소 파일 검색·편집, Git 상태 확인, Gradle 테스트
- AI 제안: 루트 `AGENTS.md`를 만들고 현재 문서·Java 패키지·테스트 패키지를 인덱싱한다.
- 사람의 판단과 이유: 채택. 에이전트가 기능 구현 전에 프로젝트 맥락과 작업 규칙을 확인할 수 있어야 하며, 현재 저장소 구조에 맞춘 인덱스가 필요하다.
- 코드·사용자 경험 영향: 제품 기능에는 영향이 없고, 이후 개발·문서화 작업의 일관성과 탐색성이 높아진다.
- 검증 근거: 문서와 패키지 경로를 확인하고 `./gradlew test`를 실행해 성공을 확인했다.
- 결과와 연결 커밋: `65d292f docs: add agent guidelines`
- 회고와 후속 조치: 문서 수명주기와 기능 브랜치의 동기화 순서를 실제 작업에서 검증하고 지침에 보완한다.

## 2026-08-28T16:11:34+09:00 — 문서 브랜치와 기능 브랜치 초기 동기화

- 상태: 채택
- 시간 근거: 병합 커밋 `642c23f` 작성 시각 및 현재 채팅 기록
- 스프린트/범위: 문서 전용 `docs` 브랜치와 스프린트 기능 브랜치 운영
- 관련 문서·코드: [`AGENTS.md`](AGENTS.md)
- 요청·질문 요약: `docs` 브랜치를 만들고 에이전트 지침을 Conventional Commit으로 커밋한 뒤, `main`에서 스프린트용 `feat/file-upload-extension-policy`를 분기해 문서 브랜치를 병합한다.
- 배경과 제약: 문서와 기능 코드를 별도 라이프사이클로 관리하되 기능 작업자는 최신 문서 지침을 받아야 한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: Git 브랜치·커밋·push·병합
- AI 제안: `docs`에서 문서를 커밋하고 기능 브랜치에 병합한다. 원격 push 결과는 로컬 병합과 별도로 확인한다.
- 사람의 판단과 이유: 채택. 기능 브랜치에 문서 기준을 명시적으로 포함하면 구현 범위와 작업 규칙을 재현할 수 있다.
- 코드·사용자 경험 영향: 제품 기능에는 영향이 없고, 기능 구현의 기준 문서가 명확해진다.
- 검증 근거: `docs` 커밋 후 `feat/file-upload-extension-policy`를 생성하고 병합했다. `origin/docs` push는 GitHub 403 권한 오류로 완료되지 않았다.
- 결과와 연결 커밋: `65d292f docs: add agent guidelines`, `642c23f merge: integrate docs guidelines into sprint branch`
- 회고와 후속 조치: 원격 push 성공 여부를 “문서 반영 완료”와 동일하게 취급하지 않는다.

## 2026-08-28T16:15:19+09:00 — 누락된 스프린트·API 문서 보완과 절차 명시

- 상태: 채택
- 시간 근거: 문서 커밋 `389e7fd` 작성 시각 및 현재 채팅 기록
- 스프린트/범위: 파일 업로드 확장자 정책 스프린트 문서와 API 명세
- 관련 문서·코드: [`docs/sprints/sprint-1/sprint-1-file-upload-extension-policy.md`](docs/sprints/sprint-1/sprint-1-file-upload-extension-policy.md), [`docs/sprints/sprint-1/sprint-1-file-upload-api.md`](docs/sprints/sprint-1/sprint-1-file-upload-api.md), [`AGENTS.md`](AGENTS.md)
- 요청·질문 요약: 스프린트·API 문서가 `docs` 브랜치에 커밋되지 않아 기능 작업이 어려운 문제를 해결하고, 같은 누락이 재발하지 않게 지침을 보완한다.
- 배경과 제약: 문서 파일이 기능 브랜치 작업 트리에만 미추적 상태로 남으면 `docs` 병합 결과로 확인할 수 없다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: Git 상태·커밋·push·병합, 문서 편집, Gradle 테스트
- AI 제안: 스프린트·API 문서를 `docs`에서 커밋하고, `AGENTS.md`에 문서 갱신·커밋·push·기능 브랜치 병합 순서를 명시한다.
- 사람의 판단과 이유: 채택. 문서가 커밋된 뒤에만 기능 브랜치로 병합하도록 강제해야 문서 기준 누락을 막을 수 있다.
- 코드·사용자 경험 영향: 제품 기능에는 직접 영향이 없고, 구현자가 확정된 API 계약과 완료 조건을 기준으로 작업할 수 있다.
- 검증 근거: 문서 인덱스와 문서 브랜치 상태를 확인하고 기능 브랜치에 병합했다. `./gradlew test`는 성공했다. `origin/docs` push는 403으로 실패했다.
- 결과와 연결 커밋: `389e7fd docs: add sprint and api specifications`, `dbd1cce merge: synchronize sprint documentation`
- 회고와 후속 조치: 문서 파일의 미추적 상태와 브랜치별 포함 여부를 커밋 전후 `git status`로 확인한다.

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

## 2026-08-28T17:17:09+09:00 — AI 활용 기록 지침과 로그 생성

- 상태: 채택
- 시간 근거: 문서 커밋 `cf1e9f0` 작성 시각 및 현재 채팅 기록
- 스프린트/범위: 제출 항목의 AI 활용 기록과 문서화 체계
- 관련 문서·코드: [`docs/ai-usage-guidelines.md`](docs/ai-usage-guidelines.md), [`PROMPT_LOG.md`](PROMPT_LOG.md), [`AGENTS.md`](AGENTS.md)
- 요청·질문 요약: AI가 식별한 요구사항·예외·위험·대안·검증·회고를 별도 문서에 꾸준히 기록할 수 있는 지침을 생성한다.
- 배경과 제약: 제출 항목은 프롬프트, skill/plugin 사용 내역, 사람의 판단과 회고를 요구하며, 제품 코드와 별도로 기록해야 한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: 문서 편집, Git worktree·커밋·push·병합, Gradle 테스트
- AI 제안: 기록 시점, 필수 항목, 검증 근거, 보안상 기록하지 않을 정보, 재사용 템플릿을 지침으로 만들고 `PROMPT_LOG.md`에 초기 기록을 남긴다.
- 사람의 판단과 이유: 채택. AI의 제안과 사람의 최종 판단을 분리해야 무검증 코드 복사가 아니라 비판적 활용 과정을 제출할 수 있다.
- 코드·사용자 경험 영향: 제품 기능에는 직접 영향이 없고, 설계·검증·결정의 추적성이 높아진다.
- 검증 근거: 문서 인덱스에 두 파일을 추가하고 기능 브랜치에 병합했다. `./gradlew test`는 성공했다. `origin/docs` push는 403으로 실패했다.
- 결과와 연결 커밋: `cf1e9f0 docs: add AI usage recording guidance`, `25966c0 merge: add AI usage documentation`
- 회고와 후속 조치: 이후 새로운 판단·예외·검증 결과가 생길 때마다 로그를 갱신한다.

## 2026-08-28T17:24:53+09:00 — 기존 채팅 기록 검토와 시간순 정렬 보완

- 상태: 채택
- 시간 근거: 현재 작업을 시작할 때 확인한 시스템 시각
- 스프린트/범위: `PROMPT_LOG.md`의 제출용 기록 품질과 시간순 정렬
- 관련 문서·코드: [`PROMPT_LOG.md`](PROMPT_LOG.md), [`docs/ai-usage-guidelines.md`](docs/ai-usage-guidelines.md), [`AGENTS.md`](AGENTS.md)
- 요청·질문 요약: 로그 문서가 생성된 이후의 현재 채팅 기록을 다시 검토해 누락된 기록 대상을 추가하고, 시간순으로 정렬 가능하게 만든다.
- 배경과 제약: 기존 로그는 여러 작업을 하나의 날짜 제목으로 묶었고, 시간 정보가 없어 사건 순서를 문자열로 정렬할 수 없었다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: Git 커밋 시각 조회, 문서 검토·편집
- AI 제안: 커밋 시각을 검증 가능한 시간 근거로 사용해 과거 항목을 작업별로 분리하고, ISO-8601 제목·시간 근거 필드·지연 발견 시 삽입 규칙을 추가한다.
- 사람의 판단과 이유: 채택. 기록의 사건 순서와 기록 작성 시점을 구분해야 제출자가 AI 활용 과정을 시간순으로 재현할 수 있다.
- 코드·사용자 경험 영향: 제품 기능에는 영향이 없으며, 제출 문서의 추적성과 검토 가능성이 향상된다.
- 검증 근거: 기존 채팅에서 확인 가능한 브랜치·문서·커밋·테스트 작업을 분리해 기록하고, 모든 제목을 동일한 ISO-8601 접두사로 정렬했다.
- 결과와 연결 커밋: 이 기록을 포함한 문서 커밋으로 연결한다.
- 회고와 후속 조치: 새 사건은 맨 아래에 추가하고, 과거 사건을 늦게 발견하면 시간 근거를 남긴 뒤 적절한 위치에 삽입한다.

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

## 2026-08-28T19:50:49+09:00 — 4단계 정책 REST API 구현과 커밋 보류

- 상태: 수정 채택
- 시간 근거: 4단계 진행 문서 커밋 `ce5115d`의 작성 시각과 같은 작업 흐름의 대화·테스트 결과
- 스프린트/범위: 파일 저장을 제외한 확장자 정책 REST API와 공통 오류 응답
- 관련 문서·코드: [`sprint-1-file-upload-api.md`](docs/sprints/sprint-1/sprint-1-file-upload-api.md), `ExtensionPolicyRestController`, `ExtensionPolicyRestExceptionHandler`, REST DTO, MockMvc 테스트
- 요청·질문 요약: 정책 조회·고정 상태 변경·커스텀 추가·삭제 API를 구현하되 파일 업로드는 5단계로 남기고, 기존 코드와 신규 코드의 불필요한 `final`을 제거하며 생성자 주입은 Lombok을 사용한다.
- 배경과 제약: API 계층은 `@RestController`와 record DTO만 사용하고 Thymeleaf `Model`, view, redirect를 사용하지 않는다. 오류는 상황별 semantic code와 `{code, message}` 형식으로 반환해야 했다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 명시적 사용 기록 없음
  - plugin/도구: MockMvc, Gradle 테스트, Git diff
- AI 제안: 네 정책 엔드포인트, 공통 advice, API·도메인 공유 validator, 서비스 조회·변경·삭제 기능과 오류별 예외 타입을 추가하도록 제안했다.
- 사람의 판단과 이유: 수정 채택. 사용자는 기능 구현은 진행하되 4단계 변경은 아직 커밋하지 않고, 1~3단계에 속하는 스타일 리팩터링은 이전 기능 커밋에 포함해야 한다고 확정했다.
- 코드·사용자 경험 영향: 정책 API와 오류 응답은 구현됐지만 파일 저장과 Axios 화면은 여전히 미구현이다. 4단계 코드는 기능 브랜치 작업 트리에 미커밋 상태로 유지한다.
- 검증 근거: 네 엔드포인트의 정상·오류 MockMvc 테스트, 서비스 테스트와 전체 `./gradlew test`, `git diff --check`가 성공했다.
- 결과와 연결 커밋: 진행 문서 `ce5115d docs: update sprint 1 rest api progress`; 4단계 코드는 사용자 요청에 따라 미커밋
- 회고와 후속 조치: 기능 완료 여부와 커밋 여부를 구분하고, 다음 단계에서는 multipart 저장과 `BLOCKED_EXTENSION`만 추가한다.

## 2026-08-28T20:01:24+09:00 — 코드·문서·스타일 커밋 경계 오류 복구

- 상태: 수정 채택
- 시간 근거: 최종 기능 커밋 `ad915e6`의 커미터 시각과 문서 병합 커밋 `c5e77fb`의 작성 시각
- 스프린트/범위: 1~3단계 기능 커밋, docs 브랜치, 4단계 미커밋 작업의 이력 분리
- 관련 문서·코드: Git 커밋 `ad915e6`, `b12de60`, `c5e77fb`, 기능 브랜치 작업 트리
- 요청·질문 요약: docs 브랜치에는 코드 변경이 들어가면 안 되고, 1~3단계 기능은 하나의 코드 전용 커밋으로 유지하며, `final` 제거와 Lombok 생성자 주입도 그 기능 커밋에 포함한다. 4단계 REST 변경은 커밋하지 않는다.
- 배경과 제약: 최초 기능 커밋 `c5e2df1`에는 코드와 문서가 섞였고, 첫 이력 재작성에서는 원본에서 삭제된 Example 파일을 복원 명령이 삭제하지 못했다. 다음 재작성에서는 스타일 변경이 4단계 작업 트리에 남아 기능 커밋의 최종 상태와 어긋났다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `korean-domain-test-policy`
  - plugin/도구: Git stash·reset·restore·amend·merge, Gradle 테스트, `git diff-tree`, `git grep`
- AI 제안: 미커밋 코드를 stash로 보존하고 기능 커밋을 파일 경계에 맞춰 재구성한 뒤 docs를 병합하고 4단계 파일만 복원하도록 제안했다.
- 사람의 판단과 이유: 수정 채택. 사용자는 AI가 만든 혼합 커밋과 잘못된 귀속을 거부하고, 코드 전용 기능 커밋·문서 전용 docs 커밋·미커밋 4단계라는 세 경계를 명확히 요구했다.
- 코드·사용자 경험 영향: `ad915e6`에는 `build.gradle`, `src/**`와 Example 삭제만 포함되고, 매개변수·지역변수 `final` 제거와 `@RequiredArgsConstructor`가 반영됐다. docs 조상 커밋은 `AGENTS.md`, `PROMPT_LOG.md`, `docs/**`만 변경하며 4단계 REST 코드는 작업 트리에 남았다.
- 검증 근거: `git show --name-only ad915e6`, `git log --name-only main..docs`, `git grep '\bfinal\b' ad915e6`, 전체 `./gradlew test`, `git diff --check`를 확인했다.
- 결과와 연결 커밋: `ad915e6 feat: implement extension policy domain`, `b12de60 docs: record extension policy database invariants`, `c5e77fb merge: synchronize sprint 1 docs`
- 회고와 후속 조치: 이력 재작성 전 변경 파일 allowlist와 삭제 파일 목록을 만든다. 재작성 후에는 커밋별 경로, 삭제 상태, 컨벤션 grep, 작업 트리 잔여 변경을 모두 확인한 뒤에만 병합한다.

## 2026-08-28T20:08:29+09:00 — AI 활용 기록 누락 감사와 완료 게이트 강화

- 상태: 수정 채택
- 시간 근거: 사용자의 누락 여부 확인 요청 직후 확인한 시스템 시각
- 스프린트/범위: `PROMPT_LOG.md` 운영과 AI 작업 절차의 재발 방지
- 관련 문서·코드: [`PROMPT_LOG.md`](PROMPT_LOG.md), [`ai-usage-guidelines.md`](docs/ai-usage-guidelines.md), [`AGENTS.md`](AGENTS.md)
- 요청·질문 요약: 이번 대화에서 기록할 내용이 없었던 것인지 AI가 누락한 것인지 판단하고, AI 실수라면 누락 기록과 재발 방지 대책을 추가한다.
- 배경과 제약: 기록 지침은 중요한 판단·실패·수정·검증을 즉시 남기도록 했지만 기존 로그는 17:24에서 끝나 이후 모델링 결정, 구현, 독립 리뷰, REST API, 커밋 이력 복구를 기록하지 않았다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: Git 커밋 시각·경로 감사, 문서 검색·편집
- AI 제안: 누락을 AI의 절차 위반으로 명시하고 과거 사건을 커밋 시각 기준으로 보충하며, 커밋·브랜치 전환·최종 보고 전 로그 대조를 완료 게이트로 강제하도록 제안했다.
- 사람의 판단과 이유: 수정 채택. 사용자는 누락 원인을 판단한 뒤 실수라면 실제 문서 수정과 재발 방지 대책까지 요구했다.
- 코드·사용자 경험 영향: 제품 코드에는 영향이 없다. 이후 AI 작업은 로그 추가 항목 또는 미추가 사유를 최종 보고에 반드시 포함해야 한다.
- 검증 근거: 기존 마지막 기록 시각과 이후 Git 커밋·대화 사건을 대조하고, 추가 항목이 사건 시각 오름차순인지 확인한다. 문서 커밋 전 문서 경로 allowlist와 `git diff --check`를 검증했다. `git push origin docs`는 AI 활용 로그의 외부 전송 대상 신뢰가 확인되지 않아 실행 환경 정책에서 거부됐다.
- 결과와 연결 커밋: 이 기록과 누락 방지 게이트를 포함하는 로컬 docs 문서 커밋. `origin/docs` 반영은 미완료다.
- 회고와 후속 조치: “즉시 기록”이라는 선언만으로는 누락을 막지 못했다. 앞으로는 커밋·브랜치 전환·최종 보고 세 지점에서 기록 여부를 명시적으로 판정하고 보고한다.

## 2026-08-28T20:16:16+09:00 — 미커밋 정책 API 코드 리뷰 지적사항 수정

- 상태: 수정 채택
- 시간 근거: 코드 리뷰 수정 작업 직전 확인한 시스템 시각과 테스트 실행 순서
- 스프린트/범위: 정책 REST API 요청 검증과 에이전트 저장소 상태 문서
- 관련 문서·코드: [`CustomExtensionPolicyRequest`](src/main/java/com/example/demo/web/dto/CustomExtensionPolicyRequest.java), [`ExtensionPolicyRestControllerTests`](src/test/java/com/example/demo/web/ExtensionPolicyRestControllerTests.java), [`AGENTS.md`](AGENTS.md)
- 요청·질문 요약: 미커밋 코드 리뷰에서 발견된 두 지적사항을 모두 해결한다.
- 배경과 제약: 커스텀 정책 등록 요청에서 필수 필드 누락은 `INVALID_REQUEST`여야 하며, 저장소 개요는 이미 구현된 정책 REST API를 반영해야 한다. 공백 문자열은 기존 계약대로 `INVALID_EXTENSION`으로 유지한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `tdd`
  - plugin/도구: 적용 패치, Gradle 테스트, Git diff 검사
- AI 제안: 누락 필드 행동을 MockMvc 테스트로 먼저 고정한 뒤 DTO에 `@NotNull`을 추가하고, `AGENTS.md`의 현재 상태를 정책 REST API 구현 완료로 갱신한다.
- 사람의 판단과 이유: 수정 채택. 필드 누락과 값 형식 오류를 구분해야 API 클라이언트가 오류를 일관되게 해석할 수 있고, 작업 지침의 저장소 상태도 실제 코드와 일치해야 한다.
- 코드·사용자 경험 영향: `{}` 커스텀 등록 요청은 `400 INVALID_REQUEST`와 공통 오류 JSON을 반환하며, 저장소 개요가 정책 REST API 구현 상태를 정확히 설명한다.
- 검증 근거: 새 테스트는 수정 전 `NullPointerException`으로 실패했고, `@NotNull` 적용 후 대상 테스트와 전체 `./gradlew test --rerun-tasks`가 성공했다. `git diff --check HEAD`와 변경 파일의 trailing whitespace 검사도 성공했다.
- 결과와 연결 커밋: 커밋하지 않은 작업 트리에 반영했다.
- 회고와 후속 조치: DTO에 제약이 없는 요청 record를 추가할 때는 누락·null·공백을 각각 계약에 맞게 구분하는 테스트를 함께 작성한다.

## 2026-08-28T20:28:30+09:00 — 패키지 구조 재정비와 커밋 경계 확정

- 상태: 채택
- 시간 근거: 패키지 구조 작업 직전 확인한 시스템 시각
- 스프린트/범위: 기존 확장자 정책 기능의 구조 리팩터링과 미커밋 정책 REST API 기능
- 관련 문서·코드: [`AGENTS.md`](AGENTS.md), `BaseEntity`, `ExtensionPolicyService`, `ExtensionPolicyServiceImpl`, 정책 컨트롤러·DTO·예외
- 요청·질문 요약: `web`을 `controller`로 바꾸고, 커스텀 예외를 별도 패키지로 분리하며, 서비스 인터페이스와 `impl` 구현체를 도입하고, 엔티티 응답 변환은 DTO 메서드로 옮기며, 초기화 코드와 공통 식별자를 `common`에 둔다. 기존 작업물의 구조 변경은 `refactor`, 현재 미커밋 REST 기능은 `feat` 커밋으로 분리한다.
- 배경과 제약: 현재 `ExtensionPolicy`와 서비스 테스트에는 이전 작업물 변경과 신규 API 기능이 섞여 있어 파일 단위 커밋만으로는 경계가 보장되지 않는다. ADR 0001의 물리 삭제 결정 때문에 공통 엔티티에 soft delete 상태를 추가할 수 없다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경과 읽기 전용 구조 탐색 에이전트
  - skill: `setup-matt-pocock-skills`, `improve-codebase-architecture`
  - plugin/도구: Git 변경 분류, 적용 패치, Gradle 전체 테스트
- AI 제안: `BaseEntity`에는 자동 생성 식별자만 두고, 기존 서비스의 등록 동작만 먼저 인터페이스·구현체로 분리해 독립적인 refactor 상태를 만든 뒤 신규 REST 기능을 새 패키지에 적용한다. 실제 엔티티 응답 조립은 DTO 정적 팩토리로 이동하고 요청 DTO는 도메인 생성 규칙을 침범하지 않도록 값 전달 책임을 유지한다.
- 사람의 판단과 이유: 채택. 사용자가 지정한 패키지 방향과 커밋 경계를 따르되, 도메인 불변식과 quota 조정은 서비스·엔티티에 유지한다.
- 코드·사용자 경험 영향: HTTP 계약과 정책 동작은 유지하면서 컨트롤러, 예외, 서비스 구현, 공통 엔티티 책임의 위치가 명확해진다.
- 검증 근거: 변경 전, 기존 기능만 옮긴 refactor 중간 상태, 두 커밋 완료 후의 세 시점에서 전체 테스트가 성공했다. 최종 `./gradlew test --rerun-tasks`는 45개 테스트가 모두 통과했고, 이전 `web` 패키지와 서비스 패키지의 커스텀 예외 참조가 남지 않았으며 `git diff --check HEAD`와 깨끗한 작업 트리를 확인했다. 커밋별 `git show --name-status`로 구조 이동 9개 파일과 신규 기능 19개 파일의 경계도 확인했다.
- 결과와 연결 커밋: `044b8f3 refactor: reorganize application package structure`, `e760e16 feat: implement extension policy REST API`, 문서 `1018c62 docs: update package structure index`
- 회고와 후속 조치: 서로 다른 커밋 성격이 한 파일에 섞인 경우 최종 파일을 한 번에 stage하지 않고, 각 커밋이 독립적으로 컴파일되는 중간 상태를 먼저 검증한다.

## 2026-08-28T20:36:47+09:00 — 공통 REST 오류 처리와 DTO 요청·응답 패키지 분리

- 상태: 채택
- 시간 근거: 사용자 후속 요청 직후 확인한 시스템 시각
- 스프린트/범위: 정책 REST 오류 처리와 DTO 패키지 구조 리팩터링
- 관련 문서·코드: `ExtensionPolicyRestExceptionHandler`, `ErrorResponse`, 정책 요청·응답 DTO, [`AGENTS.md`](AGENTS.md)
- 요청·질문 요약: REST 예외 핸들러는 컨트롤러가 아니라 공통 패키지 책임으로 이동하고 커밋한다.
- 배경과 제약: 작업 트리에는 사용자가 준비한 핸들러·오류 응답의 `common` 이동과 요청·응답 DTO의 `req`/`res` 이동이 이미 stage되어 있었지만 package 선언과 import가 이전 위치를 가리키고 있었다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경과 읽기 전용 구조 탐색 에이전트
  - skill: `improve-codebase-architecture`
  - plugin/도구: Git staged/unstaged diff 검토, 적용 패치, Gradle 테스트
- AI 제안: 사용자가 준비한 이동을 보존하면서 `common`에 핸들러와 오류 응답을 함께 두고, 정책 DTO는 `controller.dto.req`와 `controller.dto.res`로 package 선언과 import를 일치시킨다.
- 사람의 판단과 이유: 채택. 공통 오류 변환 책임을 컨트롤러 모듈에서 분리하고 이미 stage된 요청·응답 DTO 구분도 같은 구조 변경으로 완성한다.
- 코드·사용자 경험 영향: HTTP 상태·오류 JSON 계약은 바뀌지 않고 패키지 위치와 참조만 변경된다.
- 검증 근거: package 선언과 import를 새 경로로 맞춘 뒤, 코드 커밋 전과 문서 병합 후 `./gradlew test --rerun-tasks`가 모두 성공했다. `git diff --check`와 커밋의 9개 변경 경로를 확인했다.
- 결과와 연결 커밋: `813e54e refactor: move REST error handling to common`, 문서 `166e09e docs: refine common and DTO package index`
- 회고와 후속 조치: stage된 rename만 보고 완료로 판단하지 않고 package 선언과 모든 import가 새 경로를 따르는지 함께 확인한다.

## 2026-08-28T20:42:44+09:00 — 커밋 규칙을 AGENTS.md로 단일화

- 상태: 채택
- 시간 근거: 문서 전체의 커밋 규칙 검색 직후 확인한 시스템 시각
- 스프린트/범위: 저장소의 커밋·브랜치·문서 동기화 작업 지침
- 관련 문서·코드: [`AGENTS.md`](AGENTS.md), 스프린트 문서, 모델링 질문 문서, 로컬 AI 활용 기록 지침
- 요청·질문 요약: 문서 변경을 `docs` 브랜치에서 수행하고, 여러 문서에 흩어진 커밋 관련 실행 규칙을 제거해 `AGENTS.md`만 단일 기준으로 사용한다.
- 배경과 제약: 단일 커밋 전용 문서와 스프린트·질문 문서의 참조, AI 활용 기록 지침의 Git 절차가 `AGENTS.md`의 브랜치·커밋 규칙과 중복됐다. 반면 AI 활용 기록의 커밋 해시는 실행 규칙이 아니라 결과 추적 근거다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `backend-documentation`
  - plugin/도구: Markdown 전체 검색, Git docs worktree, 적용 패치
- AI 제안: 전용 단일 커밋 문서를 삭제하고 참조를 제거하며, `AGENTS.md`에 `feat`·`refactor`·`docs` 분리, staged diff 검사, 독립 테스트 기준을 모은다. 로컬 AI 활용 지침에서는 Git 실행 절차만 제거하고 결과 커밋을 기록하는 감사 형식은 유지한다.
- 사람의 판단과 이유: 채택. 실행 규칙의 출처가 하나여야 이후 작업자가 오래된 스프린트 규칙을 잘못 적용하지 않는다.
- 코드·사용자 경험 영향: 제품 코드와 API 동작에는 영향이 없고 AI 에이전트의 커밋 분류와 문서 브랜치 사용 기준만 단일화된다.
- 검증 근거: `AGENTS.md`와 `PROMPT_LOG.md`를 제외한 Markdown 전체에서 커밋·브랜치·병합·push 실행 규칙이 더 이상 검색되지 않았고, 삭제 문서 참조도 남지 않았다. `git diff --check`와 docs 브랜치 staged 경로를 확인했으며 기능 브랜치 병합 후 `./gradlew test`가 성공했다.
- 결과와 연결 커밋: `5a42f20 docs: centralize commit rules in AGENTS`
- 회고와 후속 조치: 검색 시 절차 문장과 역사적 커밋 근거를 구분해 감사 기록은 보존한다.

## 2026-08-28T20:57:19+09:00 — 기능·문서 브랜치 직접 커밋 경로 감사와 이력 정리

- 상태: 채택
- 시간 근거: 브랜치 이력 재구성 후 전체 테스트를 완료한 시스템 시각
- 스프린트/범위: `feat/file-upload-extension-policy`와 `docs` 브랜치의 커밋 경계
- 관련 문서·코드: [`AGENTS.md`](AGENTS.md), 기능 브랜치와 docs 브랜치 Git 이력
- 요청·질문 요약: 기능 브랜치의 직접 문서 변경과 docs 브랜치의 직접 코드 변경을 모두 감지하고 잘못된 이력을 수정한다.
- 배경과 제약: 기능 브랜치 직접 커밋 `e64329d`가 `AGENTS.md`를 변경했고, 최신 문서 병합 커밋의 첫 번째 부모에 포함돼 있었다. docs 브랜치 직접 커밋에는 애플리케이션 코드가 없었다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: Git first-parent 이력·경로 감사, 복구용 백업 브랜치, Gradle 전체 테스트
- AI 제안: docs 브랜치에 직접 커밋 경로 감사 규칙을 추가하고, 마지막 코드 커밋 `a173dc1`에 최신 docs HEAD를 merge해 기능 브랜치를 재구성한다. 기존 HEAD는 `codex/backup-feat-file-upload-before-scope-cleanup`으로 보존한다.
- 사람의 판단과 이유: 채택. 문서 내용은 유지하되 기능 브랜치의 직접 docs 커밋을 제거하고 docs merge로만 반영해야 브랜치 책임이 명확해진다.
- 코드·사용자 경험 영향: 최종 코드와 문서 내용은 유지되고 Git 이력의 소유 브랜치만 바로잡혔다.
- 검증 근거: 기능 브랜치 first-parent 비병합 커밋에서 `AGENTS.md`, `docs/**`가 검색되지 않았고 docs 브랜치 비병합 커밋에서 `src/**`, 빌드 파일, 애플리케이션 리소스·테스트 코드가 검색되지 않았다. 재구성된 HEAD에서 `./gradlew test --rerun-tasks`가 성공했다.
- 결과와 연결 커밋: `f945afd docs: enforce branch path separation`, `4c5885c merge: synchronize audited docs branch`
- 회고와 후속 조치: 최종 보고 전 커밋 메시지뿐 아니라 first-parent 기준 변경 경로를 함께 감사한다.

## 2026-08-28T21:36:53+09:00 — Axios 정책 조회 화면 구현 계획 승인

- 상태: 채택
- 시간 근거: 사용자가 정책 조회 화면 구현 계획의 실행을 요청한 직후 확인한 시스템 시각
- 스프린트/범위: `GET /api/v1/extension-policies` Axios 화면·최신 DB 상태 통합 검증·브라우저 확인
- 관련 문서·코드: [`sprint-1-file-upload-checklist.md`](docs/sprints/sprint-1/sprint-1-file-upload-checklist.md), `index.html`, `extension-policy.js`, `FileUploadPageControllerTests`, `ExtensionPolicyApiIntegrationTests`
- 요청·질문 요약: 이미 구현된 정책 조회 BE를 유지하고 Axios WebJar로 화면을 조립하며, 정책 변경·등록·삭제 후 GET의 최신 상태와 실제 브라우저 요청을 검증한다.
- 배경과 제약: 이번 범위는 GET 화면으로 한정하고 PATCH·POST·DELETE 화면 조작과 파일 업로드는 추가하지 않는다. `PROMPT_LOG.md`는 기존 결정으로 Git 추적에서 제외되어 로컬 기록으로만 관리한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `next-work-briefing`, `tdd`, `korean-domain-test-policy`, `browser:control-in-app-browser`
  - plugin/도구: Maven Central 의존성 확인, MockMvc, Gradle 테스트, Git 브랜치·경로 감사, 인앱 브라우저
- AI 제안: Axios 1.8.4를 WebJar로 고정하고 정적 JavaScript가 페이지 로드마다 GET을 한 번 호출하여 fixed 체크 상태, custom 목록, 로딩·성공·오류 상태를 표시한다. 실제 HTTP 변경 후 GET은 격리 H2 통합 테스트로 검증한다.
- 사람의 판단과 이유: 채택. 사용자가 제안된 계획 전체를 명시적으로 구현하도록 요청했다.
- 코드·사용자 경험 영향: 루트 페이지가 서버 Model 데이터 대신 정책 REST API를 조회해 고정·커스텀 정책을 표시한다. GET 전용 단계의 체크박스는 비활성화한다.
- 검증 근거: 페이지 테스트는 화면 영역 없음으로 Red를 확인한 뒤 Green으로 전환했다. 격리 H2를 사용한 API 통합 테스트로 초기 GET, PATCH·POST·DELETE 후 GET의 최신 상태를 확인했다. `./gradlew test --rerun-tasks`, `node --check`, `git diff --check`가 성공했다. 인앱 브라우저에서 고정 7개·빈 custom 목록, `exe` 변경·`sh` 등록·삭제 후 새로고침 결과를 확인했고 Tomcat access log에 `GET_/api/v1/extension-policies_200`이 남았다.
- 결과와 연결 문서: 코드 커밋 `2f2bc22 feat: implement extension policy query screen`, 문서 커밋 `be4772e docs: record policy query screen completion`, 문서 병합 `aa98714 merge: synchronize policy query screen docs`, [`sprint-1-file-upload-checklist.md`](docs/sprints/sprint-1/sprint-1-file-upload-checklist.md). 병합 후 전체 테스트와 최종 브라우저 스모크 검증도 성공했다. `git push origin docs`는 GitHub `403 Permission denied`로 실패해 원격 반영은 미완료다.
- 회고와 후속 조치: 브라우저 도구의 `networkidle`과 Performance Resource Timing 조회가 지원되지 않아, 화면 DOM 스냅샷과 격리 서버 access log를 결합해 URL·메서드·상태 코드를 검증했다. `PROMPT_LOG.md`는 이미 Git 추적 제외 상태이므로 로컬 기록을 유지하고, 추적 문서만 `docs` 브랜치에서 커밋한다. GitHub 권한이 복구되면 `be4772e`를 `origin/docs`에 다시 push한다.

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

## 2026-08-28T22:26:06+09:00 — 정책 테스트 리뷰 재발 방지 기준 문서화

- 상태: 채택
- 시간 근거: 리뷰 대응 문서를 docs 브랜치에 커밋하고 기능 브랜치에 병합한 직후 시스템 시각
- 스프린트/범위: 정책 조회 테스트의 목록 개수·순서 결합, 통합 테스트 기동 비용, 페이지 HTML 단언 범위에 대한 작업 기준
- 관련 문서·코드: [`AGENTS.md`](AGENTS.md), [`sprint-1-file-upload-extension-policy.md`](docs/sprints/sprint-1/sprint-1-file-upload-extension-policy.md), [`sprint-1-file-upload-checklist.md`](docs/sprints/sprint-1/sprint-1-file-upload-checklist.md)
- 요청·질문 요약: 같은 리뷰가 반복되지 않도록 테스트 작성자가 따라야 할 유지보수 기준을 적절한 문서에 반영한다.
- 배경과 제약: 현재 fixed 카탈로그 7개는 제품 계약이지만 변경 가능성이 있으며, 통합 테스트와 페이지 테스트가 그 내부 표현까지 중복 검증하면 변경 비용과 실행 시간이 커진다. 문서 변경은 docs 브랜치에서만 수행한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `backend-documentation`
  - plugin/도구: Markdown 문서 점검, Git docs worktree, Gradle 전체 테스트
- AI 제안: `AGENTS.md`에 공통 테스트 유지보수 원칙을 두고, 스프린트 문서와 체크리스트에는 카탈로그 계약·통합 시나리오·브라우저 smoke의 적용 기준을 구체화한다.
- 사람의 판단과 이유: 채택. 원천/계약 테스트만 정확한 카탈로그 불변식을 소유하고, 통합 테스트는 의미 기반 상태를 한 시나리오에서 검증하며, 페이지 테스트는 JavaScript 최소 DOM 계약만 확인하도록 역할을 분리한다.
- 코드·사용자 경험 영향: 제품 코드는 변경하지 않는다. 이후 카탈로그 추가·삭제나 정적 리소스 교체가 통합·화면 테스트를 불필요하게 깨뜨리지 않고, 실제 사용자 흐름 검증은 브라우저 smoke에서 유지된다.
- 검증 근거: docs 브랜치에서 `AGENTS.md`와 두 스프린트 문서만 staged 상태로 확인하고 `git diff --cached --check` 및 `git diff --check`를 통과시켰다. 문서 커밋 후 기능 브랜치에 병합했으며 `./gradlew test`와 `git diff --check`가 성공했다.
- 결과와 연결 커밋: `a6a900d docs: codify maintainable test design rules`, `600be63 merge: synchronize test design guidance`.
- 실패·미완료: `git push origin docs`는 GitHub `403 Permission denied`로 실패해 원격 docs 브랜치 반영은 미완료다.
- 회고와 후속 조치: 새 테스트는 먼저 계약 소유 계층을 정하고, 목록이 계약이 아니면 의미 기반 선택자를 사용하며, 전체 애플리케이션 기동이 필요한지와 브라우저 smoke로 대체할 수 있는지를 검토한다. 원격 권한이 복구되면 docs 브랜치를 다시 push한다.

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
- 실패·미완료: 두 차례의 `git push origin docs`는 모두 GitHub 403 권한 오류로 실패했다. 임시 로컬 프록시는 최초 샌드박스 포트 바인딩 제한을 승인된 로컬 실행으로 전환해 검증했다. 파일 업로드 허용·거부는 구현하지 않았다.
- 회고와 후속 조치: 브라우저 도구가 네트워크 본문을 직접 제공하지 않아 프로젝트 밖 임시 프록시로 메서드·경로·본문·상태를 기록했다. 다음 단계에서는 커스텀 POST·DELETE 화면 또는 파일 업로드 기능을 별도 범위로 진행한다.

## 2026-08-29T00:36:35+09:00 — ADR 기록 대상 정책 정비

- 상태: 채택
- 시간 근거: 사용자 요청 직후 확인한 시스템 시각
- 스프린트/범위: 저장소 공통 ADR 작성 정책과 ADR 0002 내용 정비
- 관련 문서·코드: [`AGENTS.md`](AGENTS.md), [`0002-use-server-policy-state-as-source-of-truth.md`](docs/adr/0002-use-server-policy-state-as-source-of-truth.md), [`sprint-1-fixed-policy-change-screen-options.md`](docs/questions/sprint-1-fixed-policy-change-screen-options.md)
- 요청·질문 요약: ADR에는 비즈니스·정책·아키텍처 결정만 기록하고 구현 범위 같은 실행 정보를 제거한다.
- 배경과 제약: ADR 0002에 구현 범위, URL 처리, 테스트 도구, 검증 방식이 상태 일관성 결정과 함께 기록되어 있었다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `backend-documentation`
  - plugin/도구: Markdown 근거 검색, Git docs worktree
- AI 제안: ADR의 기록 대상을 저장소 공통 정책으로 명시하고, ADR 0002에는 서버 저장 상태를 단일 기준으로 사용하는 일관성 결정과 결과만 남긴다. 구현 범위와 테스트 선택은 질문·스프린트 문서에 유지한다.
- 사람의 판단과 이유: 채택. 사용자는 ADR이 구현 절차 문서가 아니라 장기간 유지할 비즈니스·정책·아키텍처 결정 문서여야 한다고 확정했다.
- 코드·사용자 경험 영향: 제품 코드와 동작은 변경하지 않는다. 이후 ADR은 구현 세부가 바뀌어도 유지할 결정만 포함한다.
- 검증 근거: ADR 0002에서 스프린트 범위, 파일 업로드 후속 계획, URL 인코딩, JavaScript 테스트 도구, 브라우저 검증 문구를 제거하고 관련 기록이 질문 문서에 남아 있는지 확인한다.
- 결과와 연결 문서: 문서 커밋 `b1e9df4`, 기능 브랜치 문서 병합 `a027a56`.
- 실패·미완료: `git push origin docs`는 GitHub 403 권한 오류로 실패해 원격 반영은 미완료다.
- 회고와 후속 조치: 합의가 끝났다는 이유만으로 모든 선택을 ADR로 승격하지 않고, 먼저 결정의 수명과 영향 범위를 분류한다.

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
- 결과와 연결 문서: `sprint-1-file-upload-api.md`, `sprint-1-file-upload-checklist.md` (docs/sprints/sprint-1/), `sprint-1-file-upload-storage-and-error-options.md`, ADR 0003
- 실패·미완료: `.git` 메타데이터 쓰기 권한으로 stale docs worktree 정리·생성이 실패해 docs 브랜치 별도 커밋은 수행하지 못했다. 5번 삭제와 6번 업로드의 인앱 브라우저 smoke는 브라우저 데이터 삭제 확인 절차가 남아 있다.
- 회고와 후속 조치: 브라우저 smoke에서 테스트용 파일 업로드·정책 변경·삭제를 확인하고, 가능해지면 문서 변경을 docs 브랜치에서 별도 커밋한 뒤 기능 브랜치에 병합한다.

## 2026-08-29T16:47:29+09:00 — 파일 업로드 잔여 체크리스트 완료 검증

- 상태: 수정 채택
- 시간 근거: 인앱 브라우저에서 fixed/custom 정책 변경과 파일 업로드 흐름을 재검증하고 최종 회귀 테스트를 완료한 시스템 시각
- 스프린트/범위: 스프린트 1 잔여 검증 1~8번, 체크리스트 완료 상태 갱신
- 관련 문서·코드: `sprint-1-file-upload-checklist.md`, `sprint-1-file-upload-api.md` (docs/sprints/sprint-1/), `ExtensionPolicyApiIntegrationTests`, `FileUploadRestController`, `extension-policy.js`
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

## 2026-08-29T16:52:16+09:00 — 최종 브랜치 경로 감사의 역사적 예외 기록

- 상태: 채택
- 시간 근거: main 병합 후 feature·docs 브랜치의 first-parent 비병합 커밋 경로를 재감사한 시스템 시각
- 스프린트/범위: 기능·문서 브랜치 직접 커밋 경계 최종 확인
- 관련 문서·코드: `AGENTS.md`, feature/docs 브랜치 Git 이력
- 요청·질문 요약: 기능 브랜치에는 문서 직접 커밋이 없고 docs 브랜치에는 코드 직접 커밋이 없는지 확인한다.
- 배경과 제약: 현재 규칙은 docs 브랜치 직접 커밋을 `AGENTS.md`와 `docs/**`로 제한한다. 과거 `d87d996 docs: keep AI usage records local`은 AI 기록을 로컬 ignore 대상으로 지정하면서 `.gitignore`도 함께 변경했다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: Git first-parent·경로 감사, `git status`, `git diff --check`
- AI 제안: 현재 작업의 새 문서 변경은 `docs` 브랜치에 분리 커밋하고 기능 브랜치에는 docs merge로 반영한다. 과거 `.gitignore` 커밋은 이력 재작성 없이 역사적 예외로 기록한다.
- 사람의 판단과 이유: 수정 채택. 과거 로컬 docs 브랜치의 이력을 강제 재작성하면 기존 merge 이력과 main 동기화 관계를 훼손할 수 있으므로, 이번 작업에서는 현재 변경 경계를 준수하고 예외를 투명하게 기록한다.
- 코드·사용자 경험 영향: 현재 main의 코드·문서·업로드 기능에는 영향이 없다. `.gitignore`의 로컬 AI 기록 제외와 업로드 산출물 제외 설정은 유지된다.
- 검증 근거: 기능 브랜치의 직접 커밋에서 `AGENTS.md`·`docs/**`가 검색되지 않았고, docs 브랜치의 최신 문서 커밋 `238e8a4`는 `AGENTS.md`와 `docs/**`만 변경했다. 과거 예외는 `d87d996`의 변경 경로로 확인했다. main에서 `./gradlew test --rerun-tasks`와 `git diff --check`가 성공했다.
- 결과와 연결 문서: main 병합 커밋 `9258f44`, 기능 커밋 `a0dea08`, 문서 커밋 `238e8a4`, 문서 병합 커밋 `a188193`
- 회고와 후속 조치: 이후 docs 브랜치 커밋은 `.gitignore`를 포함하지 않는다. 원격 `origin/main`·`origin/docs` push는 별도 요청과 권한 확인 후 수행한다.

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

## 2026-08-30T10:44:07+09:00 — Lombok 활용 원칙 확대

- 상태: 수정 채택
- 시간 근거: Lombok 적용 후 실행한 `./gradlew test`의 성공 시각
- 스프린트/범위: 반복 보일러플레이트 제거와 저장소 공통 에이전트 지침 보완
- 관련 문서·코드: [`AGENTS.md`](AGENTS.md), [`ExtensionName`](src/main/java/com/example/demo/file/domain/value/ExtensionName.java), [`ExtensionNormalizer`](src/main/java/com/example/demo/file/domain/normalizer/ExtensionNormalizer.java), [`ExtensionValidator`](src/main/java/com/example/demo/file/domain/validator/ExtensionValidator.java), [`FixedExtensionCatalog`](src/main/java/com/example/demo/file/domain/FixedExtensionCatalog.java), [`ExtensionNameApiParser`](src/main/java/com/example/demo/file/controller/ExtensionNameApiParser.java)
- 요청·질문 요약: Lombok이 적용되지 않은 반복 코드가 있는지 점검하고, 적절한 부분에는 적극 활용하며 그 기준을 `AGENTS.md`에 추가한다.
- 배경과 제약: 생성자 주입과 JPA 엔티티 일부에는 이미 Lombok이 사용되고 있었지만 값 객체의 수동 `equals/hashCode`와 유틸리티 클래스의 private 생성자가 남아 있었다. 도메인 불변식 생성자와 JPA 생명주기 콜백까지 Lombok으로 숨기면 코드의 책임이 흐려질 수 있다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: Lombok 사용처 검색, `apply_patch`, Gradle 테스트, `git diff --check`
- AI 제안: 값 객체에는 `@Getter`, fluent accessor, `@EqualsAndHashCode`를 적용해 기존 `value()` API를 유지하고, 인스턴스 생성이 의미 없는 유틸리티 모듈에는 `@UtilityClass`를 적용한다. JPA 기본 생성자와 도메인 검증 생성자는 명시적으로 유지한다.
- 사람의 판단과 이유: 채택. 사용자는 Lombok을 적극 활용하되 가독성과 도메인 책임을 훼손하지 않기를 원했으므로, 보일러플레이트에만 적용하는 기준을 에이전트 지침으로 고정했다.
- 코드·사용자 경험 영향: `ExtensionName`의 수동 값 비교 코드를 제거하고 `value()` 접근 형태는 유지했다. `ExtensionNormalizer`, `ExtensionValidator`, `FixedExtensionCatalog`, `ExtensionNameApiParser`의 수동 private 생성자를 제거했다. 이후 에이전트는 반복 코드에 Lombok을 우선 검토하되 의미 있는 생성자·행위는 명시적으로 작성한다.
- 검증 근거: Lombok 적용 직후 `./gradlew test`가 성공했고 `git diff --check`도 통과했다. 기존 값 객체 equality, JPA round-trip, 정책 API, 파일 업로드 테스트가 모두 통과했다.
- 결과와 연결 문서: [`AGENTS.md`](AGENTS.md), [`ExtensionName`](src/main/java/com/example/demo/file/domain/value/ExtensionName.java)
- 회고와 후속 조치: Lombok 적용 여부를 개수로 최적화하지 않고, 생성된 코드가 도메인 계약을 숨기는지 먼저 확인한다. 향후 새 보일러플레이트가 생기면 동일 기준으로 적용 여부를 판단한다.

## 2026-08-30T10:51:11+09:00 — 과도한 구조 단순화 후보 점검

- 상태: 검토 중
- 시간 근거: 현재 코드와 관련 ADR을 검토한 시스템 시각
- 스프린트/범위: `ExtensionName` 값 객체 전환 이후의 모듈·인터페이스·JPA 매핑 단순화 검토
- 관련 문서·코드: [`ExtensionName`](src/main/java/com/example/demo/file/domain/value/ExtensionName.java), [`ExtensionNameApiParser`](src/main/java/com/example/demo/file/controller/ExtensionNameApiParser.java), [`ExtensionPolicy`](src/main/java/com/example/demo/file/domain/entity/ExtensionPolicy.java), [`ExtensionPolicyServiceImpl`](src/main/java/com/example/demo/file/service/impl/ExtensionPolicyServiceImpl.java), [`AGENTS.md`](AGENTS.md)
- 요청·질문 요약: 최근 리팩터링 결과에서 과하게 분리되거나 Lombok으로 복잡해진 부분을 점검하고, 단순화 가능한 후보를 식별한다.
- 배경과 제약: 이번 요청은 코드 변경 승인이 아니라 리뷰다. 값 객체의 정규화·검증 분리, `extension` 컬럼 유지, 기능별 예외 변환, JPA 매핑과 기존 테스트 계약은 유지해야 한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `improve-codebase-architecture`
  - plugin/도구: 관련 ADR·지침 검토, 저장소 검색, 삭제 테스트 관점의 모듈 검토
- AI 제안: API 문자열을 `ExtensionName`으로 바꾸는 얇은 `ExtensionNameApiParser`는 컨트롤러 내부 private 메서드로 합칠 수 있다. 단일 embedded 속성에 대한 `@AttributeOverrides`는 단일 `@AttributeOverride`로 줄일 수 있다. `saveAndFlush`는 트랜잭션 커밋으로 충분한지 별도 확인할 후보이며, `ExtensionNormalizer`·`ExtensionValidator` 분리는 이미 확정된 책임이므로 단순화 후보에서 제외한다.
- 사람의 판단과 이유: 검토 중. 현재는 동작 변경 없이 후보만 제시하며, 파일 수가 적다는 이유로 값 객체·validator·repository seam을 즉시 합치지 않는다. 특히 `ExtensionNameApiParser`를 합칠지는 컨트롤러의 API 예외 변환 locality와 공개 모듈 제거 이득을 비교한 뒤 결정한다.
- 코드·사용자 경험 영향: 이번 단계에서는 제품 코드와 API 동작을 변경하지 않는다. 후보를 채택하면 컨트롤러 탐색성이 좋아질 수 있지만, API 입력 변환 책임을 재사용할 seam은 줄어든다.
- 검증 근거: 전체 Java 소스와 관련 호출부를 검색하고, `@UtilityClass`, embedded 매핑, `saveAndFlush`, API parser 사용 위치를 확인했다. 기존 `./gradlew test` 성공 상태를 기준으로 리뷰했으며 이 단계에서는 테스트를 다시 실행하지 않았다.
- 결과와 연결 문서: 이 항목에 단순화 후보를 남기고, 사용자 선택 후 선택된 후보만 별도 코드 변경과 테스트로 진행한다.
- 회고와 후속 조치: 후보 우선순위는 `ExtensionNameApiParser` 내부화, 단일 `@AttributeOverride` 표기 단순화, `saveAndFlush` 제거 검토 순서다. `ExtensionName`의 값 객체·정규화·검증 책임과 기능별 예외 handler는 현재 구조를 유지한다.

## 2026-08-30T11:02:31+09:00 — 낯선 Lombok 확장 기능 제거

- 상태: 수정 채택
- 시간 근거: Lombok 확장 기능을 제거한 뒤 실행한 `./gradlew test`의 성공 시각
- 스프린트/범위: 코드 가독성을 위한 Lombok 사용 기준 재정의
- 관련 문서·코드: [`AGENTS.md`](AGENTS.md), [`ExtensionName`](src/main/java/com/example/demo/file/domain/value/ExtensionName.java), [`ExtensionNormalizer`](src/main/java/com/example/demo/file/domain/normalizer/ExtensionNormalizer.java), [`ExtensionValidator`](src/main/java/com/example/demo/file/domain/validator/ExtensionValidator.java), [`FixedExtensionCatalog`](src/main/java/com/example/demo/file/domain/FixedExtensionCatalog.java), [`ExtensionNameApiParser`](src/main/java/com/example/demo/file/controller/ExtensionNameApiParser.java)
- 요청·질문 요약: `@Accessors(fluent = true)`와 `@UtilityClass`처럼 익숙하지 않은 Lombok 기능은 사용하지 않고, 코드에서 생성 방식과 메서드 이름이 명확하게 드러나도록 변경한다.
- 배경과 제약: Lombok을 적극 활용하되 초보자도 이해할 수 있는 코드 가독성이 우선이다. 기존 `ExtensionName.value()` 호출과 정규화·검증·카탈로그·API 변환 동작은 유지해야 한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: Lombok 사용처 검색, `apply_patch`, Gradle 테스트, `git diff --check`
- AI 제안: fluent accessor로 기존 `value()` API를 유지하고 `@UtilityClass`로 유틸리티 생성자를 제거하는 방안을 앞서 적용했으나, 사용자는 이런 숨은 동작이 낯설고 과하다고 판단했다.
- 사람의 판단과 이유: 수정 채택. `@Accessors(fluent = true)`와 `@UtilityClass`를 제거하고 `ExtensionName.value()`와 각 유틸리티 클래스의 `final class`·private 생성자를 명시적으로 복원했다. Lombok은 익숙한 생성자·getter·값 비교 보일러플레이트에만 사용한다.
- 코드·사용자 경험 영향: Lombok annotation processing에 의존해 생성되던 fluent accessor와 utility class 동작이 사라져 코드 독자가 실제 메서드와 생성 제한을 바로 확인할 수 있다. `@EqualsAndHashCode`, `@Getter`, `@RequiredArgsConstructor`, `@NoArgsConstructor` 등 기존 활용은 유지한다.
- 검증 근거: 변경 후 `./gradlew test`가 성공했고, 값 객체 equality·JPA round-trip·정책 API·파일 업로드 테스트가 모두 통과했다.
- 결과와 연결 문서: [`AGENTS.md`](AGENTS.md), [`ExtensionName`](src/main/java/com/example/demo/file/domain/value/ExtensionName.java)
- 회고와 후속 조치: Lombok 적용은 보일러플레이트 감소량보다 코드 독자가 동작을 추적할 수 있는지를 기준으로 결정한다. 새 Lombok annotation을 도입할 때는 팀의 익숙함과 생성 코드의 가시성을 먼저 검토한다.

## 2026-08-30T11:04:04+09:00 — 과도한 구조 단순화 후보 반영

- 상태: 수정 채택
- 시간 근거: 세 가지 구조 단순화 후 실행한 `./gradlew test`의 성공 시각
- 스프린트/범위: 확장자 정책 API 입력 변환, 정책 저장 흐름, JPA embedded 매핑의 단순화
- 관련 문서·코드: [`ExtensionPolicyRestController`](src/main/java/com/example/demo/file/controller/ExtensionPolicyRestController.java), [`ExtensionPolicyServiceImpl`](src/main/java/com/example/demo/file/service/impl/ExtensionPolicyServiceImpl.java), [`ExtensionPolicy`](src/main/java/com/example/demo/file/domain/entity/ExtensionPolicy.java)
- 요청·질문 요약: 앞선 구조 리뷰에서 식별한 `ExtensionNameApiParser` 내부화, `saveAndFlush()` 단순화, 단일 `@AttributeOverrides` 표기를 실제 코드에 반영한다.
- 배경과 제약: API 입력·파일 업로드의 오류 의미, `ExtensionName` 값 객체, 정책 유형별 동작, JPA 트랜잭션 계약은 유지해야 한다. 신규 정책은 저장되어야 하고, 변경 정책은 트랜잭션 종료 시 dirty checking으로 반영되어야 한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: 구조 리뷰, `apply_patch`, Gradle 테스트, `git diff --check`
- AI 제안: 한 컨트롤러에서만 사용하는 parser는 private 메서드로 합치고, 신규 등록은 `save()`, 조회된 엔티티의 상태 변경은 dirty checking에 맡기며, 단일 embedded 속성은 단일 `@AttributeOverride`로 선언한다.
- 사람의 판단과 이유: 수정 채택. 사용자는 리뷰에서 지적된 단순화 후보들을 모두 반영하도록 요청했다. parser 공개 모듈을 삭제해 API 입력 변환 책임의 locality를 높이고, 서비스가 즉시 flush 시점을 직접 제어하지 않도록 했다.
- 코드·사용자 경험 영향: REST API 계약과 응답은 변하지 않는다. 컨트롤러 내부에서 입력을 `ExtensionName`으로 변환하고, 정책 저장·변경은 트랜잭션 커밋 시점에 반영된다. JPA 매핑 선언도 동일한 `extension` 컬럼을 더 짧게 표현한다.
- 검증 근거: production 코드의 `ExtensionNameApiParser` 참조와 `saveAndFlush()`를 검색해 제거를 확인했다. 테스트 코드의 명시적 flush는 DB 제약·timestamp 검증 용도라 유지했다. `./gradlew test`와 `git diff --check`가 성공했다.
- 결과와 연결 문서: [`ExtensionPolicyRestController`](src/main/java/com/example/demo/file/controller/ExtensionPolicyRestController.java), [`ExtensionPolicyServiceImpl`](src/main/java/com/example/demo/file/service/impl/ExtensionPolicyServiceImpl.java), [`ExtensionPolicy`](src/main/java/com/example/demo/file/domain/entity/ExtensionPolicy.java)
- 회고와 후속 조치: 즉시 flush가 필요한 후속 조회나 외부 부작용이 생기면 그 근거를 테스트로 먼저 고정한다. 단순화 과정에서도 값 객체·validator·기능별 예외 handler처럼 책임을 얻는 모듈은 삭제하지 않는다.

## 2026-08-30T11:08:24+09:00 — 파일 확장자 추출 책임 분리 검토

- 상태: 검토 중
- 시간 근거: 현재 `FileUploadServiceImpl`과 업로드 테스트를 검토한 시스템 시각
- 스프린트/범위: 파일 업로드 기능의 파일명 확장자 추출과 업로드 orchestration 테스트 분리
- 관련 문서·코드: [`FileUploadServiceImpl`](src/main/java/com/example/demo/file/service/impl/FileUploadServiceImpl.java), [`FileUploadServiceTests`](src/test/java/com/example/demo/service/impl/FileUploadServiceTests.java), [`ExtensionName`](src/main/java/com/example/demo/file/domain/entity/vo/ExtensionName.java)
- 요청·질문 요약: private `extractExtensionName`의 테스트가 많아질 때, 추출 책임을 별도 클래스로 분리하는 것이 검증과 구조 측면에서 적절한지 검토한다.
- 배경과 제약: 현재 업로드 서비스가 파일 입력 검증·경로 정리·마지막 확장자 추출·`ExtensionName` 생성·기능별 예외 변환·정책 조회·저장을 한 메서드 흐름에서 조정한다. `ExtensionName`의 정규화·형식 검증 책임은 값 객체에 유지해야 한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `improve-codebase-architecture`
  - plugin/도구: 관련 소스·테스트 검색, 삭제 테스트 관점의 모듈 검토
- AI 제안: `FileExtensionExtractor`라는 구체 모듈이 `MultipartFile`에서 검증된 `ExtensionName`을 반환하도록 분리한다. 별도 interface나 두 번째 adapter는 만들지 않고, 추출 모듈 단위 테스트와 업로드 서비스 orchestration 테스트를 나눈다.
- 사람의 판단과 이유: 검토 중. 사용자는 추출 전용 테스트의 진단성과 편의성을 확인하고 싶어 했으며, 현재 구조에서는 추출 케이스를 검증하려면 정책 저장소와 파일 저장소까지 준비해야 한다. 추출 로직은 경로 우회 방지와 입력 오류 변환이라는 자체 계약이 있어 단순 테스트용 함수보다 분리 가치가 있다.
- 코드·사용자 경험 영향: 분리 시 API와 업로드 결과는 바뀌지 않는다. 추출 테스트는 파일명 케이스에 집중하고, 업로드 서비스 테스트는 차단 판정·저장 호출·저장 실패 같은 orchestration에 집중할 수 있다. interface까지 추가하면 현재 한 구현만 있는 seam에 불필요한 복잡성이 생긴다.
- 검증 근거: 현재 `extractExtensionName`이 private이고 `FileUploadServiceTests`가 Spring context·정책 DB·임시 저장소를 함께 준비하는 것을 확인했다. 이번 단계는 설계 검토이므로 코드와 테스트는 변경하지 않았다.
- 결과와 연결 문서: 별도 `FileExtensionExtractor` concrete module 분리를 후보로 기록한다.
- 회고와 후속 조치: 사용자가 분리를 승인하면 추출 모듈의 입력·오류·반환 계약을 먼저 테스트로 고정하고, 업로드 서비스 테스트에서는 추출 세부 파일명 케이스를 제거한다. `ExtensionName` 생성과 정규화·검증을 추출 모듈에서 재구현하지 않는다.

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

## 2026-08-30T11:17:51+09:00 — 파일 확장자 추출 모듈 의존성 주입 전환

- 상태: 수정 채택
- 시간 근거: `FileExtensionExtractor`를 Spring bean으로 등록하고 전체 테스트가 성공한 시스템 시각
- 스프린트/범위: 파일 업로드 서비스의 추출 모듈 생성 방식 정리
- 관련 문서·코드: [`FileExtensionExtractor`](src/main/java/com/example/demo/file/service/impl/FileExtensionExtractor.java), [`FileUploadServiceImpl`](src/main/java/com/example/demo/file/service/impl/FileUploadServiceImpl.java), [`FileUploadServiceTests`](src/test/java/com/example/demo/service/impl/FileUploadServiceTests.java)
- 요청·질문 요약: 추출 모듈을 서비스 내부에서 직접 생성하지 않고 의존성 주입으로 전환한다.
- 배경과 제약: 추출 책임은 이미 별도 concrete module로 분리됐고, 서비스의 생성자에 필요한 협력 대상을 명시해야 한다. 별도 interface는 현재 구현이 하나뿐이므로 추가하지 않는다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: `apply_patch`, Gradle 전체 테스트, `git diff --check`, 저장소 검색
- AI 제안: `FileExtensionExtractor`에 `@Component`를 적용하고 `FileUploadServiceImpl`의 final 필드로 주입한다. 테스트에서 직접 서비스를 생성하는 경우에는 extractor를 명시적으로 전달한다.
- 사람의 판단과 이유: 수정 채택. 사용자는 production 코드의 직접 생성보다 의존성 주입이 일관성과 테스트성에 적합하다고 판단했다. 구체 클래스 주입은 유지하되 interface까지 확장하지 않았다.
- 코드·사용자 경험 영향: 업로드 API 동작은 변하지 않는다. Spring이 추출 모듈의 생명주기를 관리하고, 서비스의 의존성이 생성자 계약에 드러난다.
- 검증 근거: production 코드의 직접 `new FileExtensionExtractor()`를 제거하고 Spring context 테스트와 수동 생성 테스트를 수정했다. `./gradlew test`와 `git diff --check`가 성공했다.
- 결과와 연결 문서: [`FileExtensionExtractor`](src/main/java/com/example/demo/file/service/impl/FileExtensionExtractor.java), [`FileUploadServiceImpl`](src/main/java/com/example/demo/file/service/impl/FileUploadServiceImpl.java)
- 회고와 후속 조치: 단일 구현의 concrete module은 먼저 직접 주입하고, 두 번째 adapter나 교체 요구가 실제로 생길 때만 interface 도입을 검토한다.

## 2026-08-30T11:25:35+09:00 — 코드 작성 규칙 문서 분리 및 인덱스 정비

- 상태: 수정 채택
- 시간 근거: 이번 세션의 리팩터링 결과와 `HEAD` 커밋(`9258f44`) 및 현재 작업 트리의 패키지 구조를 대조한 시스템 시각
- 스프린트/범위: 세션 리팩터링에서 확인된 코드 스타일을 지속 가능한 에이전트 지침으로 문서화
- 관련 문서·코드: [`AGENTS.md`](AGENTS.md), [`code-writing-guidelines.md`](docs/code-writing-guidelines.md), [`ExtensionName`](src/main/java/com/example/demo/file/domain/entity/vo/ExtensionName.java), [`ExtensionPolicyServiceImpl`](src/main/java/com/example/demo/file/service/impl/ExtensionPolicyServiceImpl.java), [`FileExtensionExtractor`](src/main/java/com/example/demo/file/service/impl/FileExtensionExtractor.java)
- 요청·질문 요약: 현재 `AGENTS.md`에 섞여 있는 코드 작성 규칙을 별도 문서로 분리하고, 커밋된 코드와 세션에서 사용자가 채택한 리팩터링을 비교해 앞으로의 작성 기준으로 정리한다.
- 배경과 제약: `AGENTS.md`는 작업 범위·문서 인덱스·브랜치·커밋·검증 절차에 집중해야 한다. 세션에서 확정된 값 객체의 끝단 전환, 도메인 행위 위임, 익숙한 Lombok만 사용, 불필요한 추상화·flush·트랜잭션 배제, 생성자 의존성 주입, move 기반 패키지 이동을 누락하지 않아야 한다. 현재 작업 트리는 이전 리팩터링 변경으로 dirty하므로 코드 변경이나 브랜치·커밋 작업은 수행하지 않는다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `backend-documentation`
  - plugin/도구: `git log`, `git status`, `find`, `rg`, 관련 소스·문서 확인, `apply_patch`, `git diff --check`
- AI 제안: 코드 표현 규칙을 `docs/code-writing-guidelines.md`로 분리하고, 사용자 확정 선호와 현재 코드에서 관찰한 관례를 구분해 기록한다. 동시에 `AGENTS.md`의 stale한 패키지 인덱스를 실제 `com.example.demo.file` 구조로 갱신한다.
- 사람의 판단과 이유: 수정 채택. 사용자는 앞으로 같은 스타일을 유지할 수 있도록 단순한 인덱스가 아니라 책임·값 객체·계층·예외·Lombok·테스트·리팩터링 점검 순서를 별도 기준으로 남기기를 요청했다. 다만 새 interface·adapter·API·코드 변경은 요청 범위를 벗어나므로 추가하지 않았다.
- 코드·사용자 경험 영향: 이후 에이전트가 `AGENTS.md`에서 운영 규칙과 코드 표현 규칙을 혼동하지 않는다. 실제 기능 동작은 바뀌지 않으며, 패키지 인덱스는 현재 파일 위치와 맞아져 링크·탐색 신뢰성이 높아진다.
- 검증 근거: `find src/main/java`로 실제 패키지 구조를 확인하고 `AGENTS.md`의 인덱스 경로를 대조했다. 새 문서와 `AGENTS.md` 링크·내용을 재검토했으며 문서 변경 후 `git diff --check`와 `./gradlew test`가 모두 성공했다.
- 결과와 연결 문서: [`docs/code-writing-guidelines.md`](docs/code-writing-guidelines.md), [`AGENTS.md`](AGENTS.md)
- 회고와 후속 조치: 이번 문서는 사용자 확정 규칙과 관찰된 관례를 구분했으므로, 이후 요구사항이 바뀌면 관례를 무비판적으로 적용하지 말고 문서를 함께 갱신한다. 문서 변경은 저장소 운영 규칙에 따라 코드 변경과 분리된 docs 작업으로 커밋한다.

## 2026-08-30T11:33:45+09:00 — 파일 업로드 보안·정책·UX·운영 구현 근거 점검

- 상태: 검토 중
- 시간 근거: 로컬 내부 체크리스트 작성과 전체 회귀 테스트를 마친 시스템 시각
- 스프린트/범위: 스프린트 1 파일 업로드와 확장자 정책의 현재 구현 현황 판정
- 관련 문서·코드: [로컬 구현 점검표](.internal-docs/file-upload-risk-implementation-checklist.md), [`FileUploadServiceImpl`](src/main/java/com/example/demo/file/service/impl/FileUploadServiceImpl.java), [`FileExtensionExtractor`](src/main/java/com/example/demo/file/service/FileExtensionExtractor.java), [`ExtensionPolicyServiceImpl`](src/main/java/com/example/demo/file/service/impl/ExtensionPolicyServiceImpl.java), [`extension-policy.js`](src/main/resources/static/js/extension-policy.js)
- 요청·질문 요약: 파일 보안, 정책/데이터, UX/예외, 운영 관점의 19개 항목을 체크박스로 변환하고, 현재 완전 구현만 체크하며 미구현·부분 구현은 미체크와 근거 링크를 남긴다. 산출물은 Git에서 관리하지 않는 내부 문서로 둔다.
- 배경과 제약: 현재 워킹 트리에 대규모 미커밋 리팩터링이 있으므로 커밋된 `main`만이 아니라 현재 파일 내용을 근거로 판정했다. 요청은 현황 문서화이므로 제품 코드·API·스키마는 변경하지 않는다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `backend-documentation`
  - plugin/도구: 저장소 검색, 코드·테스트·ADR 대조, `apply_patch`, Gradle 전체 테스트, `git diff --check`, `git check-ignore`
- AI 제안: 복합 항목에서 일부 조건만 구현되었으면 완료로 과대평가하지 않고 `미체크(부분 구현)`로 분류한다. 예를 들어 대소문자·마지막 확장자 처리는 있지만 이중 확장자 공격 정책이 없으므로 해당 복합 항목은 미체크로 둔다.
- 사람의 판단과 이유: 사용자가 제시한 “구현된 항목만 체크” 기준을 적용했다. 개별 미구현 항목의 우선순위와 스프린트 포함 여부는 사용자가 아직 확정하지 않았으므로 검토 중으로 남긴다.
- 코드·사용자 경험 영향: 제품 동작은 변하지 않는다. 점검표는 완전 구현 6개, 미구현·부분 구현 13개로 분류하고, 각 항목에 코드·테스트·확정 문서 링크를 추가했다.
- 검증 근거: `./gradlew test --rerun-tasks`를 실행해 4개 task가 모두 실행되고 `BUILD SUCCESSFUL in 13s`를 확인했다. `git diff --check`가 성공했고, `git check-ignore -v`가 `.git/info/exclude`의 `.internal-docs/` 규칙으로 점검표를 제외하는 것을 확인했다.
- 결과와 연결 문서: [파일 업로드 보안·정책·UX·운영 구현 점검표](.internal-docs/file-upload-risk-implementation-checklist.md)
- 회고와 후속 조치: 확장자 기반 차단은 서버 사이드에서 강제되지만 내용·MIME 검사와 운영 감사는 없다. 이 점검표는 우선순위 결정 전 현황 기준선으로 사용하고, 후속 구현 범위는 별도 결정으로 남긴다.

## 2026-08-30T12:02:10+09:00 — 체크박스별 전공 수준 분석 문서 분리

- 상태: 검토 중
- 시간 근거: 체크박스별 분석 문서 19개 생성과 링크 검증을 마친 시스템 시각
- 스프린트/범위: 파일 업로드 보안·정책·UX·운영 점검표의 항목별 원리 분석
- 관련 문서·코드: [구현 점검표](.internal-docs/file-upload-risk-implementation-checklist.md), [항목별 분석 디렉터리](.internal-docs/file-upload-risk-analysis/)
- 요청·질문 요약: 구현 여부와 무관하게 각 체크박스 문구를 독립 문서로 분석하고, 컴퓨터공학 전공 4학년 수준의 내부 원리와 AI의 견해를 포함하며, 체크리스트에서 각 문서로 연결한다.
- 배경과 제약: 분석 문서는 Git에서 관리하지 않는 로컬 내부 문서여야 한다. 현재 코드·테스트·ADR의 근거와 일반적인 시스템 원리를 구분하고, 미구현 기능을 구현된 것처럼 표현하지 않는다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: backend-documentation
  - plugin/도구: rg, 코드·테스트·ADR 대조, apply_patch, git diff --check, 링크 대상 검증
- AI 제안: 각 항목에 현재 상태, 핵심 원리와 내부 작동, 제 의견, 근거를 고정된 구조로 두고, 파싱·정규화·신뢰 경계·자원 고갈·동시성·관측성·다중 테넌시의 트레이드오프를 구현 상태와 분리해 설명한다.
- 사람의 판단과 이유: 사용자의 독립 문서 요구를 채택했다. 체크박스 하나에 여러 보안·운영 조건이 섞인 경우에도 단일 결론으로 축약하지 않고, 현재 구현의 경계와 후속 권고를 함께 기록했다. 문서 작성은 분석 범위이며 코드·API 변경은 하지 않았다.
- 코드·사용자 경험 영향: 제품 동작은 변하지 않는다. 체크리스트의 19개 문구가 각각 하나의 링크를 가지며, 독자는 구현 근거와 설계 판단을 문서 단위로 추적할 수 있다.
- 검증 근거: 분석 문서 19개와 체크리스트 링크 19개가 일치하고 모든 링크 대상이 존재함을 확인했다. 모든 분석 문서에 제 의견과 근거 절이 있고 malformed patch 문자열이 없음을 확인했다. 기존 ./gradlew test --rerun-tasks는 BUILD SUCCESSFUL이었다.
- 결과와 연결 문서: [체크박스별 분석 문서](.internal-docs/file-upload-risk-analysis/), [링크가 추가된 점검표](.internal-docs/file-upload-risk-implementation-checklist.md)
- 회고와 후속 조치: 내용·MIME 검사, 명시적 업로드 한도, 감사·관측성, 동시 편집 충돌 정책은 별도 결정이 필요하다. 분석 문서의 권고를 구현 요구사항으로 승격할 때는 사용자 승인과 ADR 또는 스프린트 문서를 먼저 갱신한다.

## 2026-08-30T12:23:34+09:00 — 체크박스별 분석을 10분 발표 리포트로 전면 확장

- 상태: 수정 채택
- 시간 근거: 19개 분석 문서의 전면 개정과 분량·구성·링크 검증을 마친 시스템 시각
- 스프린트/범위: 로컬 파일 업로드 보안·정책·UX·운영 분석 문서의 깊이 보강
- 관련 문서·코드: [구현 점검표](.internal-docs/file-upload-risk-implementation-checklist.md), [항목별 분석 디렉터리](.internal-docs/file-upload-risk-analysis/)
- 요청·질문 요약: 기존 항목별 문서가 판정 메모 수준으로 빈약하므로, 각 문서를 약 10분 발표 분량의 리포트처럼 확장한다. 내부적으로 왜 필요한지, 무엇을 고려하는지, 구체적인 상황별 적합한 선택, 트레이드오프, 최종 추천 방향을 포함한다.
- 배경과 제약: 19개 문서는 서로 독립적으로 읽혀야 하며 현재 구현 사실과 일반 설계 원리를 구분해야 한다. 미구현 권고를 구현 사실처럼 표현하지 않고 제품 코드·API는 변경하지 않는다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: backend-documentation
  - plugin/도구: apply_patch, 문서별 문자 수·필수 절·링크 검증, git diff --check, git check-ignore
- AI 제안: 각 문서를 발표 질문, 필요성, 내부 처리 단계, 실패 시나리오, 상황별 선택, 트레이드오프, 현재 프로젝트 평가, 단계별 추천, 결론 구조로 전면 개정한다. 단순 일반론이 되지 않도록 현재 Java 클래스·DB 제약·JavaScript 상태 복구·테스트 근거에 연결한다.
- 사람의 판단과 이유: 사용자의 피드백을 수정 채택했다. 문서별로 2,745자 이상, 전체 약 60,291자로 확장하고 각 문서에 발표 질문·트레이드오프·추천·결론을 포함했다. 보안 강화를 무조건 권고하지 않고 로컬 과제, 단일 서버, 공개 업로드, 기업 운영 등 상황별 적합성을 구분했다.
- 코드·사용자 경험 영향: 제품 동작과 체크 상태는 변하지 않는다. 체크리스트 링크도 유지된다. 독자는 각 항목을 독립적인 발표·검토 자료로 사용하고 구현 우선순위를 판단할 수 있다.
- 검증 근거: 분석 문서 19개와 체크리스트 링크 19개를 확인했다. 모든 문서가 2,500자 이상이고 발표 질문, 트레이드오프, 추천, 결론을 포함한다. 분석 문서의 코드·문서 근거 링크가 모두 존재하며 malformed patch 문자열과 후행 공백이 없고 git diff --check가 성공했다. .git/info/exclude가 분석 문서를 Git 대상에서 제외한다.
- 결과와 연결 문서: [전면 개정된 항목별 분석 문서](.internal-docs/file-upload-risk-analysis/), [링크가 유지된 구현 점검표](.internal-docs/file-upload-risk-implementation-checklist.md)
- 회고와 후속 조치: 분석 문서는 방향을 제안하지만 곧바로 구현 범위를 확정하지 않는다. 사용자가 우선순위를 정하면 필요한 항목만 질문 문서·ADR·스프린트 계약으로 전환한 뒤 TDD 구현을 진행한다.

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
- 관련 문서·코드: [`0004-use-extension-name-value-object.md`](docs/adr/0004-use-extension-name-value-object.md), [`sprint-1-file-upload-api.md`](docs/sprints/sprint-1/sprint-1-file-upload-api.md), [`sprint-1-file-upload-checklist.md`](docs/sprints/sprint-1/sprint-1-file-upload-checklist.md)
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
- 결과와 연결 문서: [`0004-use-extension-name-value-object.md`](docs/adr/0004-use-extension-name-value-object.md), [`sprint-1-file-upload-api.md`](docs/sprints/sprint-1/sprint-1-file-upload-api.md), [`sprint-1-file-upload-checklist.md`](docs/sprints/sprint-1/sprint-1-file-upload-checklist.md)
- 회고와 후속 조치: 다음 구현에서는 허용·거부 경계값을 한글 테스트명으로 먼저 고정하고, 기존 코드가 점만 검사하는지 확인한 뒤 검증 로직을 적용한다.

## 2026-08-30T17:28:45+09:00 — 허용 문자 정책 ADR 분리

- 상태: 수정 채택
- 시간 근거: 사용자가 기존 `ADR 0004`가 이미 구현된 결정이므로 허용 문자 정책을 별도 ADR로 분리하자는 의견을 확정한 현재 대화 시각
- 스프린트/범위: 확장자 이름 허용 문자 정책의 ADR 이력 정리
- 관련 문서·코드: [`0004-use-extension-name-value-object.md`](docs/adr/0004-use-extension-name-value-object.md), [`0010-limit-extension-name-characters.md`](docs/adr/0010-limit-extension-name-characters.md), [`AGENTS.md`](AGENTS.md)
- 요청·질문 요약: 기존 값 객체 ADR을 수정하지 않고 한글·영문·숫자 허용 규칙을 새 ADR로 분리한다.
- 배경과 제약: `ADR 0004`는 이미 구현된 값 객체 구조와 책임 경계를 기록하므로, 새로운 입력 정책을 기존 결정에 덧붙이면 과거 결정과 후속 정책의 경계가 흐려진다. 현재 `ADR 0007`은 다른 업로드 차단 주제에 사용 중이므로 새 번호를 사용한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: 문서 검색, `apply_patch`, `git diff --check`, Git 상태·커밋 확인
- AI 제안: 기존 `0004`는 원래 의미로 복원하고 허용 문자 규칙을 새 ADR로 기록하며, API 계약·체크리스트·문서 인덱스를 동기화한다.
- 사람의 판단과 이유: 수정 채택. 사용자는 구현이 완료된 ADR의 의미를 보존하고, 이후 추가된 허용 문자 정책을 독립적인 변경으로 추적하기 위해 분리를 선택했다.
- 코드·사용자 경험 영향: 문서상 정책은 한글·영문·숫자만 허용하고 공백·점·특수문자를 거부한다. 이번 작업에서는 제품 코드와 테스트를 변경하지 않는다.
- 검증 근거: `ADR 0004`에서 허용 문자 정책 문구를 제거하고 `ADR 0010` 및 API 계약·체크리스트에 정책을 반영했다. 문서 인덱스 링크와 `git diff --check`를 확인한다.
- 결과와 연결 문서: [`0010-limit-extension-name-characters.md`](docs/adr/0010-limit-extension-name-characters.md), [`sprint-1-file-upload-api.md`](docs/sprints/sprint-1/sprint-1-file-upload-api.md), [`sprint-1-file-upload-checklist.md`](docs/sprints/sprint-1/sprint-1-file-upload-checklist.md)
- 회고와 후속 조치: 새 정책을 구현할 때 `ExtensionName`의 기존 생성 경계를 유지하고, 허용·거부 입력 테스트를 먼저 추가한다. 기존 `ADR 0004`의 결정 문구를 다시 수정하지 않는다.

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
- 관련 문서·코드: [`0013-use-request-id-and-frontend-owned-upload-messages.md`](docs/adr/0013-use-request-id-and-frontend-owned-upload-messages.md), [`sprint-1-file-upload-api.md`](docs/sprints/sprint-1/sprint-1-file-upload-api.md), [차단 메시지 분석](.internal-docs/file-upload-risk-analysis/13-block-message.md)
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
- 결과와 연결 문서: [`0013-use-request-id-and-frontend-owned-upload-messages.md`](docs/adr/0013-use-request-id-and-frontend-owned-upload-messages.md), [`sprint-1-file-upload-api.md`](docs/sprints/sprint-1/sprint-1-file-upload-api.md), [`AGENTS.md`](AGENTS.md)
- 회고와 후속 조치: 구현 시 `requestId` 생성·전파·로그 필드의 경계를 정하고, 기존 `message` 의존 FE와의 호환 전환을 검토한다. 재시도와 멱등성은 별도 요구가 생길 때 독립적으로 결정한다.

## 2026-08-30T17:58:49+09:00 — 파일 업로드 네트워크 재시도와 멱등성 검토 시작

- 상태: 검토 중
- 시간 근거: 사용자가 파일 업로드의 네트워크 오류 시 FE 자동 재시도와 `Idempotency-Key` 도입 의견을 제시한 현재 대화 시각
- 스프린트/범위: 로컬 파일시스템 기반 동기 파일 업로드의 응답 유실·중복 저장 대응
- 관련 문서·코드: [`sprint-1-upload-retry-idempotency-options.md`](docs/questions/sprint-1-upload-retry-idempotency-options.md), [`sprint-1-file-upload-api.md`](docs/sprints/sprint-1/sprint-1-file-upload-api.md), [`0013-use-request-id-and-frontend-owned-upload-messages.md`](docs/adr/0013-use-request-id-and-frontend-owned-upload-messages.md), [로딩·오류·네트워크 실패 상태 설계](.internal-docs/file-upload-risk-analysis/14-loading-error-network.md)
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
- 관련 문서·코드: [`sprint-1-upload-retry-idempotency-options.md`](docs/questions/sprint-1-upload-retry-idempotency-options.md), [`sprint-1-file-upload-api.md`](docs/sprints/sprint-1/sprint-1-file-upload-api.md), [`0013-use-request-id-and-frontend-owned-upload-messages.md`](docs/adr/0013-use-request-id-and-frontend-owned-upload-messages.md)
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
- 관련 문서·코드: [`sprint-1-upload-retry-idempotency-options.md`](docs/questions/sprint-1-upload-retry-idempotency-options.md), [`sprint-1-file-upload-api.md`](docs/sprints/sprint-1/sprint-1-file-upload-api.md), [`0003-server-generated-file-storage-policy.md`](docs/adr/0003-server-generated-file-storage-policy.md)
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
- 관련 문서·코드: [`sprint-2-extension-limit-ux-validation.md`](docs/sprints/sprint-2/sprint-2-extension-limit-ux-validation.md), [제한값 근거와 UX 분석](.internal-docs/file-upload-risk-analysis/11-limit-rationale-and-ux.md), [`sprint-1-file-upload-api.md`](docs/sprints/sprint-1/sprint-1-file-upload-api.md), [`AGENTS.md`](AGENTS.md)
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
- 관련 문서·코드: [`sprint-1-upload-retry-idempotency-options.md`](docs/questions/sprint-1-upload-retry-idempotency-options.md), [`sprint-1-file-upload-api.md`](docs/sprints/sprint-1/sprint-1-file-upload-api.md)
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
- 관련 문서·코드: [`sprint-1-upload-retry-idempotency-options.md`](docs/questions/sprint-1-upload-retry-idempotency-options.md), [`sprint-1-file-upload-api.md`](docs/sprints/sprint-1-file-upload-api.md), [RFC 9110 Retry-After](https://www.rfc-editor.org/rfc/rfc9110.html#section-10.2.3)
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

## 2026-08-30T18:23:11+09:00 — 스프린트 2에 접근성·반응형 UX 검증 추가

- 상태: 채택
- 시간 근거: 사용자가 접근성과 반응형 업로드 화면 분석을 스프린트 2 요구사항에 추가할지 검토한 뒤 포함하기로 한 현재 대화 시각
- 스프린트/범위: 20자 확장자와 200개 정책 목록의 브라우저 UX 검증
- 관련 문서·코드: [`sprint-2-extension-limit-ux-validation.md`](docs/sprints/sprint-2/sprint-2-extension-limit-ux-validation.md), [`16-accessibility-responsive.md`](.internal-docs/file-upload-risk-analysis/16-accessibility-responsive.md)
- 요청·질문 요약: 접근성·반응형 분석 내용을 스프린트 2 요구사항에 미리 포함할지 판단하고 반영한다.
- 배경과 제약: 20자 입력과 200개 목록은 긴 텍스트 표시, 모바일 레이아웃, 키보드 탐색, 오류·포커스 복구와 직접 연결된다. 다만 이번 스프린트의 목표는 완전한 접근성 구현이나 UI 프레임워크 교체가 아니라 한도 UX 검증이다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: 없음
  - plugin/도구: 기존 스프린트 2 문서와 접근성·반응형 위험 분석 확인, `apply_patch`, `git diff --check`
- AI 제안: 접근성·반응형을 별도 기능 개발이 아니라 키보드·스크린리더·확대·모바일 화면의 최소 검증 항목으로 추가하고, 발견된 문제만 후속 UX 개선으로 분리한다.
- 사람의 판단과 이유: 채택. 사용자는 해당 검증 기준을 스프린트 2 요구사항에 포함하기로 했다. 한도 경계값의 실제 사용성을 판단할 때 조작 가능성과 상태 전달을 함께 확인해야 하며, 검증 전 구현 범위를 과도하게 확장하지 않는다.
- 코드·사용자 경험 영향: 스프린트 2 문서에 접근성·반응형 검증, 판정 기준, 결과 기록, 범위 외 항목을 추가한다. 이번 변경으로 코드나 UI 동작은 변경하지 않는다.
- 검증 근거: 기존 스프린트 2 문서가 이미 데스크톱·모바일 레이아웃, 긴 오류 메시지, 목록 탐색성과 오류 복구를 검증 대상으로 삼고 있음을 확인했다. 접근성·반응형 위험 분석의 키보드·스크린리더·확대·포커스 검증을 별도 항목으로 구체화했다.
- 결과와 연결 문서: [`sprint-2-extension-limit-ux-validation.md`](docs/sprints/sprint-2/sprint-2-extension-limit-ux-validation.md), [`16-accessibility-responsive.md`](.internal-docs/file-upload-risk-analysis/16-accessibility-responsive.md)
- 회고와 후속 조치: 브라우저 검증에서 실제 문제가 발견될 때만 최소 UX 개선·검색·페이징 등의 후속 요구사항을 별도로 만든다. 완전한 WCAG 준수 여부는 이번 스프린트의 완료 조건으로 확대하지 않는다.

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

## 2026-08-30T19:20:20+09:00 — ADR 구현 상태와 즉시 구현 후보 점검

- 상태: 구현 현황 검토 완료·코드 변경 없음
- 시간 근거: 사용자가 현재 ADR의 구현·미구현 구분과 즉시 구현 가능 항목의 코멘트 문서 생성을 요청한 현재 대화 시각
- 스프린트/범위: 파일 업로드·확장자 정책 ADR 0001~0016의 구현 근거 대조
- 관련 문서·코드: [`adr-implementation-status-review-2026-08-30.md`](docs/adr/adr-implementation-status-review-2026-08-30.md), [`AGENTS.md`](AGENTS.md), `main`의 확장자 정책·파일 업로드 코드와 테스트
- 요청·질문 요약: 구현된 ADR과 구현되지 않은 ADR을 구분하고, 미구현·문제 항목 중 바로 구현할 수 있는 후보와 선결 조건을 문서 하단 코멘트로 남긴다.
- 배경과 제약: 현재 체크아웃된 `docs` 브랜치의 `src`는 예제 애플리케이션이므로, 파일 업로드 기능이 구현된 `main@0f6b53c`을 판정 기준으로 사용했다. 사용자가 보유한 기존 미커밋 문서와 `uploads/` 산출물은 변경하지 않는다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `backend-documentation`
  - plugin/도구: ADR·`main` 코드·설정·테스트 대조, `apply_patch`, `git diff --check`
- AI 제안: ADR 0010, 0009, 0011을 바로 착수 가능한 작은 작업으로 두고, 0005는 MIME 호환표 확정 후, 0006·0014·0015는 상태·메타데이터·멱등성 의존성을 한 작업 단위로 확정한 뒤 구현하도록 정리했다.
- 사람의 판단과 이유: 확인 대기. 이번 요청은 상태 점검 문서 작성이며, 각 후보의 실제 구현 착수 승인은 포함하지 않는다.
- 코드·사용자 경험 영향: 코드와 API 동작은 변경하지 않는다. 구현 상태와 누락된 정책·계약을 명시해 다음 작업의 범위와 순서를 판단할 수 있게 한다.
- 검증 근거: `main`의 `ExtensionPolicy`, `ExtensionName`, `FileUploadServiceImpl`, `LocalFileStorage`, 오류 handler, `application.yml`, 프런트 JavaScript, 관련 테스트와 각 ADR의 결정 항목을 대조했다.
- 결과와 연결 문서: [`adr-implementation-status-review-2026-08-30.md`](docs/adr/adr-implementation-status-review-2026-08-30.md)
- 회고와 후속 조치: 구현을 시작할 때는 이 점검 문서의 선결 조건을 API 계약·질문 문서 또는 ADR 결정으로 전환하고, TDD 절차와 문서/기능 브랜치 동기화 절차를 따른다.

## 2026-08-30T19:28:49+09:00 — 스프린트 2 미완료 ADR 완료 목표 PRD 작성

- 상태: PRD 작성 완료·로컬 Markdown 이슈 추적기 게시
- 시간 근거: 사용자가 미구현 ADR 전체 완료를 목표로 하나의 스프린트 2 PRD 작성을 요청하고 로컬 Markdown 관리를 선택한 현재 대화 시각
- 스프린트/범위: ADR 0005, 0006, 0009, 0010, 0011, 0012, 0013, 0014, 0015, 0016의 미완료·부분 구현 항목
- 관련 문서·코드: [스프린트 2 PRD](docs/sprints/sprint-2/sprint-2-prd.md), [`adr-implementation-status-review-2026-08-30.md`](docs/adr/adr-implementation-status-review-2026-08-30.md), 관련 ADR 전체
- 요청·질문 요약: 미구현 ADR을 모두 완료 목표로 하는 단일 스프린트 2 PRD를 작성한다.
- 배경과 제약: ADR 0006과 0016은 `proposed`이고, MIME allowlist·stale 복구·멱등 처리 중 HTTP 계약·보존 기간은 구체 결정이 부족하다. PRD는 이 항목을 임의 구현하지 않고 결정 게이트로 명시한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `setup-matt-pocock-skills`, `to-prd`
  - plugin/도구: ADR·스프린트 문서·API 계약 확인, `apply_patch`, `git diff --check`
- AI 제안: 보안·자원 보호, MIME 검증, 관측성·감사, 업로드 상태, 멱등성, allowlist 전환 준비를 하나의 스프린트 목표 아래 의존 순서로 배치했다.
- 사람의 판단과 이유: 사용자는 하나의 스프린트 2 PRD로 관리하고 로컬 Markdown을 이슈 추적기로 사용하기로 했다.
- 코드·사용자 경험 영향: 이번 변경은 PRD와 에이전트 작업 추적기 설정 문서만 추가한다. API와 애플리케이션 코드는 변경하지 않는다.
- 검증 근거: ADR 구현 상태 점검과 기존 API 계약·스프린트 1 문서를 대조해, 구현 완료로 오인하면 안 되는 `proposed` ADR과 미결 계약을 PRD의 결정 게이트로 구분했다.
- 결과와 연결 문서: [스프린트 2 PRD](docs/sprints/sprint-2/sprint-2-prd.md), [`docs/agents/issue-tracker.md`](docs/agents/issue-tracker.md)
- 회고와 후속 조치: 구현 전 MIME 매핑, 원본 파일명 영속화 채택, stale 복구 수치, 멱등 처리 중 HTTP 계약·보존 기간, allowlist scope·승인·shadow 기준을 사용자 결정 문서와 ADR에 반영한다.

## 2026-08-30T19:28:49+09:00 — 스프린트 2 구현 순서와 결정 게이트 체크리스트 작성

- 상태: 체크리스트 작성 완료·구현 대기
- 시간 근거: 사용자가 PRD 기반 구현 순서와 작업별 사전 질문 규칙이 있는 스프린트 2 체크리스트 작성을 요청한 현재 대화 시각
- 스프린트/범위: 스프린트 2 PRD의 미완료 ADR 전체를 작업 순서·결정 게이트·테스트 순서로 전환
- 관련 문서·코드: [`sprint-2-implementation-checklist.md`](docs/sprints/sprint-2/sprint-2-implementation-checklist.md), [스프린트 2 PRD](docs/sprints/sprint-2/sprint-2-prd.md), 관련 ADR 0005~0016
- 요청·질문 요약: 구현 기반 작업 순서를 정의하고, 작업 시작 전 결정이 필요한 부분을 표시해 해당 작업 전에 질문하도록 구성한다.
- 배경과 제약: MIME allowlist, 원본 파일명 영속화, stale 복구, 멱등 처리, allowlist 전환에는 확정되지 않은 제품·운영 선택이 남아 있다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `next-work-briefing`
  - plugin/도구: PRD·ADR·기존 스프린트 문서 대조, `apply_patch`, `git diff --check`
- AI 제안: 결정이 완료된 0010·0009·0011부터 시작하고, 나머지는 질문 문서→사용자 답변→ADR/API 계약 갱신→구현의 중단 규칙으로 진행하도록 구성했다.
- 사람의 판단과 이유: 사용자는 모든 작업의 사전 결정 필요 여부를 명시하고, 미결이면 해당 작업 수행 전에 질문하도록 요청했다.
- 코드·사용자 경험 영향: 코드와 API 동작은 변경하지 않는다. 구현 작업은 체크리스트의 시작 전 질문이 닫힌 뒤에만 시작한다.
- 검증 근거: PRD의 구현 결정·테스트 결정과 ADR 0005, 0006, 0012~0016의 명시적 미결 항목을 대조해 작업 의존성과 중단 조건을 분리했다.
- 결과와 연결 문서: [`sprint-2-implementation-checklist.md`](docs/sprints/sprint-2/sprint-2-implementation-checklist.md)
- 회고와 후속 조치: 구현을 시작할 때 각 작업의 질문을 새 질문 문서로 먼저 기록하고, 사용자 답변 후에만 해당 기능 브랜치·TDD 작업을 시작한다.

## 2026-08-30T19:44:57+09:00 — 스프린트 2 PRD 문서 위치 정정

- 상태: 문서 위치와 내부 링크 정정 완료·문서 브랜치 커밋 대기
- 시간 근거: 사용자가 스프린트 2 PRD를 로컬 이슈 폴더가 아닌 스프린트 2 문서 폴더에 두도록 지시한 현재 대화 시각
- 요청·질문 요약: `.scratch/sprint-2/PRD.md`를 `docs/sprints/sprint-2/`로 이동한다.
- 사람의 판단과 이유: 채택. 스프린트 단위 기준 문서는 관련 체크리스트·UX 검증 문서와 같은 폴더에서 관리한다.
- 코드·사용자 경험 영향: 애플리케이션 코드와 API 동작은 변경하지 않는다. 체크리스트·문서 인덱스·기존 기록의 PRD 링크를 새 위치로 갱신하고, 스프린트 PRD와 개별 구현 이슈의 저장 위치를 구분하도록 추적기 안내를 정정한다.
- 검증 근거: 전체 저장소에서 이전 `.scratch/sprint-2/PRD.md` 참조가 남지 않았고, Markdown 공백 검증을 통과했다.
- 결과와 연결 문서: [스프린트 2 PRD](docs/sprints/sprint-2/sprint-2-prd.md), [`sprint-2-implementation-checklist.md`](docs/sprints/sprint-2/sprint-2-implementation-checklist.md), [`docs/agents/issue-tracker.md`](docs/agents/issue-tracker.md), [`AGENTS.md`](AGENTS.md)

## 2026-08-30T19:48:23+09:00 — 스프린트 2 체크리스트 진행 상태 대조

- 상태: 실제 구현·테스트 근거가 있는 기준선 항목만 체크
- 요청·질문 요약: 체크리스트에서 이미 진행된 항목과 아직 구현되지 않은 항목을 실제 코드·테스트와 대조한다.
- 사람의 판단과 이유: 기존 완료 ADR 0001, 0002, 0003, 0004, 0007은 스프린트 2 추가 작업과 구분하고, 스프린트 2 작업은 완료 조건 전체가 충족되지 않으면 완료 처리하지 않는다.
- 검증 근거: ADR 구현 상태 점검, 실제 Java 코드·테스트 검색, `./gradlew test` 성공 결과를 대조했다. ADR 0010은 `ExtensionName` 공유 구조만 진행된 상태이며 허용 문자 제한은 미완료다.
- 결과와 연결 문서: [`sprint-2-implementation-checklist.md`](docs/sprints/sprint-2/sprint-2-implementation-checklist.md), [`adr-implementation-status-review-2026-08-30.md`](docs/adr/adr-implementation-status-review-2026-08-30.md)

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
- 결과와 연결 문서: [`0009-limit-multipart-upload-size.md`](docs/adr/0009-limit-multipart-upload-size.md), [`sprint-1-file-upload-api.md`](docs/sprints/sprint-1/sprint-1-file-upload-api.md), [`sprint-2-implementation-checklist.md`](docs/sprints/sprint-2/sprint-2-implementation-checklist.md)
- 회고와 후속 조치: 프록시·로드밸런서의 업로드 제한도 10MB/12MB와 일치하는지 운영 환경에서 확인하고, 다음 작업은 ADR 0011 저장 경로 외부화다.

## 2026-08-30T20:30:40+09:00 — multipart 구현 설명 범위 단순화

- 상태: 구조·검증 범위 축소
- 요청·질문 요약: 과제 설명에 불필요한 Tomcat `max-swallow-size`, multipart 지연 해석, 실제 대용량 내장 서버 테스트를 제외하고 용량 정책을 단순하게 유지한다.
- 사람의 판단과 이유: 용량 초과 예외는 공통 REST Advice에서 처리하고, `FileUploadExceptionHandler`는 파일 업로드 도메인 예외 전용으로 유지한다. 설정은 10MB/12MB만 남긴다.
- 코드·사용자 경험 영향: 공통 Advice에 `MaxUploadSizeExceededException` 처리를 두고 파일 Advice의 `assignableTypes` 범위를 복원한다. 설정 바인딩 테스트와 413 매핑 테스트만 유지하며 실제 파일 저장 흐름은 변경하지 않는다.
- 검증 근거: `DemoApplicationTests`와 `FileUploadRestControllerTests`가 통과했고, 전체 대용량 HTTP 통합 테스트는 제거했다.
- 결과와 연결 문서: [`0009-limit-multipart-upload-size.md`](docs/adr/0009-limit-multipart-upload-size.md), [`sprint-2-implementation-checklist.md`](docs/sprints/sprint-2/sprint-2-implementation-checklist.md)

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
- 관련 문서·코드: [`0005-limit-upload-to-known-non-executable-types.md`](docs/adr/0005-limit-upload-to-known-non-executable-types.md), [`sprint-1-file-upload-api.md`](docs/sprints/sprint-1/sprint-1-file-upload-api.md), [`sprint-2-implementation-checklist.md`](docs/sprints/sprint-2/sprint-2-implementation-checklist.md), `FileUploadServiceImpl`
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
- 결과와 연결 문서: [`0005-limit-upload-to-known-non-executable-types.md`](docs/adr/0005-limit-upload-to-known-non-executable-types.md), [`sprint-1-file-upload-api.md`](docs/sprints/sprint-1/sprint-1-file-upload-api.md), [`sprint-2-prd.md`](docs/sprints/sprint-2/sprint-2-prd.md), [`sprint-2-implementation-checklist.md`](docs/sprints/sprint-2/sprint-2-implementation-checklist.md)
- 회고와 후속 조치: 텍스트 안의 스크립트·HTML 의미 분석은 하지 않으며, 다운로드·미리보기·파싱 기능이 추가될 때 실행·렌더링 경계를 별도 검토한다. 다음 단계는 기능 브랜치에서 Tika 감지기와 실행 MIME 차단 테스트를 구현하는 것이다.

## 2026-08-30T21:23:09+09:00 — docs 원격 push 권한 실패

- 상태: 외부 반영 실패·로컬 커밋 유지
- 시간 근거: `git push origin docs` 실행 결과
- 스프린트/범위: 실행 가능한 MIME만 차단하는 ADR 0005 문서 반영
- 관련 문서·코드: `docs` 브랜치 커밋 `e4d2207`
- 요청·질문 요약: 확정한 문서 커밋을 `origin/docs`에 push한다.
- 배경과 제약: 저장소 문서 절차에 따라 원격 문서 브랜치 반영이 필요하다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `tdd`
  - plugin/도구: Git
- AI 제안: 원격 push가 실패해도 유효한 로컬 문서 커밋을 기능 브랜치에 병합하고, 원격 반영 실패를 완료로 보고하지 않는다.
- 사람의 판단과 이유: 채택. 현재 오류는 코드나 문서 내용이 아니라 GitHub 저장소 권한 `403`이며, 로컬 구현을 계속할 수 있다.
- 코드·사용자 경험 영향: 없음. 원격 `origin/docs`에는 문서 커밋이 반영되지 않았고 로컬 `docs` 브랜치에는 보존되어 있다.
- 검증 근거: `git push origin docs`가 `Permission denied`와 HTTP 403으로 실패했다.
- 결과와 연결 문서: 로컬 커밋 `e4d2207`; 원격 push는 미완료
- 회고와 후속 조치: GitHub 인증·저장소 권한을 확인한 뒤 `git push origin docs`를 재시도해야 한다.

## 2026-08-30T21:34:07+09:00 — 실행 MIME 차단 구현 완료

- 상태: 기능 구현·관련 테스트·문서 상태 갱신 완료
- 시간 근거: 기능 커밋 `b171051`과 전체 `./gradlew test` 성공 시각
- 스프린트/범위: 스프린트 2 ADR 0005 실행 MIME denylist 기반 업로드 검증
- 관련 문서·코드: [`0005-limit-upload-to-known-non-executable-types.md`](docs/adr/0005-limit-upload-to-known-non-executable-types.md), [`sprint-1-file-upload-api.md`](docs/sprints/sprint-1/sprint-1-file-upload-api.md), `ExecutableMimeCatalog`, `TikaMimeTypeDetector`, `FileUploadServiceImpl`
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

## 2026-08-30T21:34:59+09:00 — 구현 문서 원격 push 재실패

- 상태: 외부 반영 실패·로컬 문서 보존
- 시간 근거: `git push origin docs` 재시도 결과
- 스프린트/범위: 실행 MIME 차단 구현 상태 문서의 원격 반영
- 관련 문서·코드: `docs` 브랜치 커밋 `a7fe4f7`
- 요청·질문 요약: 최신 구현 상태 문서를 `origin/docs`에 push한다.
- 배경과 제약: 로컬 기능·문서 커밋은 완료됐지만 GitHub 원격 저장소 인증·권한이 확인되지 않았다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `tdd`
  - plugin/도구: Git
- AI 제안: 동일 원격 권한 오류를 완료로 간주하지 않고 로컬 커밋을 보존하며 최종 보고에서 원격 반영 미완료를 명시한다.
- 사람의 판단과 이유: 채택. 오류가 코드나 문서 내용이 아니라 GitHub HTTP 403이므로 기능 구현과 원격 반영을 분리한다.
- 코드·사용자 경험 영향: 없음. 원격 `origin/docs`에는 최신 문서가 반영되지 않았다.
- 검증 근거: push 결과가 `Permission to seokjun7410/file-upload.git denied`와 HTTP 403을 반환했다.
- 결과와 연결 문서: 로컬 문서 커밋 `a7fe4f7`; 원격 push 미완료
- 회고와 후속 조치: GitHub 인증·저장소 권한을 확인한 후 `git push origin docs`를 재시도한다.

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
- 관련 문서·코드: [`0013-use-request-id-and-frontend-owned-upload-messages.md`](docs/adr/0013-use-request-id-and-frontend-owned-upload-messages.md), [`0015-separate-upload-retry-idempotency-and-state.md`](docs/adr/0015-separate-upload-retry-idempotency-and-state.md), [`0014-persist-upload-state-before-file-and-finalize-atomically.md`](docs/adr/0014-persist-upload-state-before-file-and-finalize-atomically.md), [`sprint-1-file-upload-api.md`](docs/sprints/sprint-1/sprint-1-file-upload-api.md)
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

## 2026-08-30T22:27:35+09:00 — 통합 멱등성 문서 원격 push 실패

- 상태: 외부 반영 실패·로컬 커밋 유지
- 시간 근거: `git push origin docs` 실행 결과
- 스프린트/범위: 스프린트 2 requestId 통합 멱등 업로드 계약
- 관련 문서·코드: docs 커밋 `e0e2c9f`
- 요청·질문 요약: 확정한 ADR·API·스프린트 문서를 `origin/docs`에 push한다.
- 배경과 제약: 로컬 문서 브랜치에는 계약 변경이 커밋되었지만 원격 저장소 인증 계정에 쓰기 권한이 있는지 확인되지 않았다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `tdd`
  - plugin/도구: Git
- AI 제안: 원격 반영 실패를 완료로 간주하지 않고 로컬 docs 커밋을 기능 브랜치에 병합해 구현을 계속하되, 최종 보고에 push 미완료를 명시한다.
- 사람의 판단과 이유: 채택. 오류는 문서 내용이 아니라 GitHub HTTP 403이며 로컬 결정 문서와 기능 구현을 분리해 보존할 수 있다.
- 코드·사용자 경험 영향: 없음. `origin/docs`에는 `e0e2c9f`가 반영되지 않았다.
- 검증 근거: `git push origin docs`가 `Permission to seokjun7410/file-upload.git denied`와 HTTP 403을 반환했다.
- 결과와 연결 문서: 로컬 docs 커밋 `e0e2c9f`; 원격 push 미완료
- 회고와 후속 조치: GitHub 인증·저장소 권한을 확인한 뒤 동일 push를 재시도한다.

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
- 결과와 연결 문서: `docs` 브랜치에서 체크리스트·ADR 구현 상태·PRD·API 계약·PROMPT_LOG를 갱신한다. `origin/docs` push는 GitHub HTTP 403으로 미완료다.
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

## 2026-08-31T01:37:30+09:00 — 현재 코드 기준으로 문서 구현 상태 정정

- 상태: 수정 채택
- 시간 근거: 현재 기준 브랜치 `feat/extension-policy-audit-history@ddb5698`의 코드·설정·테스트와 기존 문서 대조를 완료한 시각
- 스프린트/범위: 스프린트 2 구현 상태 문서·스프린트 1 API 계약·ADR 0012 연계 문서
- 관련 문서·코드: `docs/adr/adr-implementation-status-review-2026-08-30.md`, `docs/sprints/sprint-2/sprint-2-implementation-checklist.md`, `docs/sprints/sprint-1/sprint-1-file-upload-api.md`, `docs/sprints/sprint-2/sprint-2-prd.md`, `AGENTS.md`, `ExtensionPolicyAuditHistory`
- 요청·질문 요약: ADR과 체크리스트의 구현 완료 표시가 실제 현재 코드와 일치하는지 판단하고 문서를 코드 기준으로 갱신한다.
- 배경과 제약: 업로드 신뢰성 구현 커밋 `164a8e0`과 MIME·용량·저장 루트 구현 커밋은 별도 기능 브랜치에만 있으며 현재 기준 브랜치에 병합되지 않았다. ADR의 정책 결정과 구현 완료 상태를 혼동하지 않는다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `diagnose`, `backend-documentation`
  - plugin/도구: `apply_patch`, Git, `./gradlew test`, `rg`
- AI 제안: 현재 브랜치에 없는 구현을 완료로 표시한 상태 점검·체크리스트·API 문서를 실제 코드 기준으로 낮추고, ADR 자체의 정책 결정은 유지한다.
- 사람의 판단과 이유: 채택. 구현 여부의 진실 원천은 현재 기준 브랜치의 코드이며, 다른 기능 브랜치의 커밋은 병합 전까지 현재 구현으로 주장하지 않는다. 정책 감사 이력의 `state` 단순화는 현재 코드와 문서가 일치하므로 유지한다.
- 코드·사용자 경험 영향: 코드 변경은 없다. 현재 API는 파일 파트 기반 기본 업로드와 `code`·`message` 오류 응답만 제공하며, requestId·MIME 차단·용량 제한·업로드 상태 영속화는 구현 대기로 문서화한다.
- 검증 근거: 현재 기준 브랜치에서 `./gradlew test` 성공. `UploadFile`, `UploadRequestId`, `UploadFileRecoveryService`, Tika 감지기, `file.upload.storage-path` 설정이 현재 기준 브랜치에 없음을 확인했다. `git diff --check`도 통과했다.
- 결과와 연결 문서: ADR 구현 상태 점검, 스프린트 2 체크리스트, 스프린트 1 API 계약, 스프린트 2 PRD, `AGENTS.md`를 현재 코드 기준으로 갱신했다.
- 회고와 후속 조치: 문서 브랜치와 기능 브랜치의 병합 상태를 구현 완료 판단 전에 확인한다. `PROMPT_LOG.md` 기존 기록의 일부 시각 순서 위반은 이번 변경 범위에서 재작성하지 않고 별도 정리 대상으로 남긴다.

## 2026-08-31T14:55:10+09:00 — 업로드 신뢰성 기능과 정책 감사 이력의 단일 작업 브랜치 통합

- 상태: 채택
- 시간 근거: 사용자가 `feat/upload-policy-reliability`에서만 작업하고 관련 작업을 모두 해당 브랜치에 모으기로 확정한 시각
- 스프린트/범위: 스프린트 2 업로드 신뢰성·MIME·multipart 용량·저장 루트·정책 감사 이력과 연계 문서
- 관련 문서·코드: `feat/upload-policy-reliability@f7468c3`, `feat/upload-request-id-idempotency@d23103d`, `feat/extension-policy-audit-history@001980a`, ADR 구현 상태 점검, 스프린트 1 API 계약, 스프린트 2 구현 체크리스트
- 요청·질문 요약: 분리되어 있던 업로드 신뢰성 구현과 정책 감사 이력 구현을 `feat/upload-policy-reliability`에 통합하고, 이후 작업 기준과 문서를 하나로 맞춘다.
- 배경과 제약: ADR의 결정 상태와 실제 구현 상태가 서로 다른 브랜치에 나뉘어 있으면 완료 표기가 코드와 어긋날 수 있다. 기능 브랜치에는 코드만 직접 커밋하고 문서 변경은 `docs` 브랜치에서 커밋한 뒤 기능 브랜치에 병합한다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱 작업 환경
  - skill: `diagnose`, `backend-documentation`
  - plugin/도구: `apply_patch`, Git, `rg`, `./gradlew test`
- AI 제안: requestId·MIME·용량·저장 루트·업로드 상태와 정책 감사 이력을 하나의 기능 브랜치 기준으로 통합하고, 코드·설정·테스트에 근거해 구현 상태 문서를 다시 정렬한다.
- 사람의 판단과 이유: 채택. 이후 구현·검증·문서 갱신은 `feat/upload-policy-reliability`에서만 진행해 작업 기준을 단일화한다. ADR 0016 allowlist 전환과 ADR 0015 키 보존 기간은 결정 전까지 구현하지 않는다.
- 코드·사용자 경험 영향: 정책 API의 requestId 계약은 추가하지 않고, 파일 업로드 API의 UUID v4 `Idempotency-Key`·requestId·오류 context 계약은 유지한다. MIME 실행 파일 차단, 10MB/12MB 용량 제한, 업로드 상태 복구, 정책 감사 이력이 같은 기준 브랜치에서 함께 동작한다.
- 검증 근거: 통합 기준 브랜치에서 `./gradlew test`가 `BUILD SUCCESSFUL`로 완료되었다. 기준 커밋에 Tika MIME 감지, multipart 설정, `file.upload.storage-path`, `UploadFile` 상태 전이·복구, 정책 감사 이력 코드와 테스트가 존재함을 확인했다.
- 결과와 연결 문서: `docs` 브랜치에서 구현 상태 점검·스프린트 1 API 계약·스프린트 2 체크리스트를 `f7468c3` 기준으로 갱신한 뒤 `feat/upload-policy-reliability`에 문서 병합한다.
- 회고와 후속 조치: 다음 작업은 키 보존 기간과 만료·고아 파일 정리 상호작용 테스트 결정, 브라우저 smoke 검증이다. 기존 `PROMPT_LOG.md`의 과거 시각 순서 위반은 이번 통합 범위에서 재작성하지 않는다.

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

## 2026-08-31T15:51:11+09:00 — 스프린트 2 최종 브랜치·회귀 감사

- 상태: 채택
- 시간 근거: 기능·문서 커밋을 분리하고 기능 브랜치에 문서 merge를 반영한 뒤 최종 공백·경로·전체 테스트 감사를 완료한 시각
- 스프린트/범위: 스프린트 2 최종 검증 완료 조건과 브랜치 커밋 경로 감사
- 관련 문서·코드: `feat/upload-policy-reliability@c6e92d2`, `docs@a2f3605`, `docs/sprints/sprint-2/sprint-2-implementation-checklist.md`
- 요청·질문 요약: 기능 변경과 문서 변경이 저장소 브랜치 규칙에 맞는지 확인하고 최종 회귀 테스트를 실행한다.
- 배경과 제약: 기능 브랜치의 직접 커밋은 코드만, 문서 변경은 docs 브랜치 커밋 후 merge commit으로 반영해야 한다. allowlist와 실제 200%·VoiceOver·409 화면 검증은 완료로 주장하지 않는다.
- AI 활용 정보:
  - 모델/실행 환경: Codex 데스크톱
  - skill: `next-work-briefing`, `browser:control-in-app-browser`, `diagnose`
  - plugin/도구: Git, `./gradlew test`, `git diff --check`
- AI 제안: 기능 커밋과 문서 커밋을 분리하고, 최종 보고 전에 `main..브랜치` 직접 커밋 경로·staged 공백·전체 테스트를 확인한다.
- 사람의 판단과 이유: 채택. 기능 커밋 `a840db2`에는 FE 코드·템플릿만 두고, 검증 결과는 docs 커밋 `a2f3605`에서 관리한 뒤 기능 브랜치에 `c6e92d2`로 병합했다. 이는 기능 브랜치의 문서 직접 커밋을 방지하면서 기능 기준에서 결과 문서를 함께 확인하게 한다.
- 코드·사용자 경험 영향: 추가 코드 변경은 없다. 스프린트 체크리스트의 최종 감사 항목을 완료로 표시하고, 미확정 브라우저 환경은 후속 항목으로 유지한다.
- 검증 근거: `git log --first-parent --no-merges main..HEAD --name-only`에서 직접 기능 커밋의 경로가 `src/**`뿐임을 확인했다. `git diff --check`와 최종 `./gradlew test`가 성공했다. 최종 작업 트리는 clean이다.
- 결과와 연결 문서: 체크리스트와 이 기록을 갱신하고 기능 브랜치에 docs merge를 반영했다.
- 회고와 후속 조치: 원격 `origin/docs` push는 사용자가 별도로 승인하거나 인증 문제가 해결될 때 수행한다. 스프린트 완료 보고는 VoiceOver·실제 200% zoom·409 화면 분기를 보류한 상태로 기능 구현 완료와 수동 검증 미완료를 구분해 작성한다.

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
- 관련 문서·코드: [`ADR 0017`](docs/adr/0017-scan-all-extension-segments-for-upload-blocking.md), [`ADR 0007`](docs/adr/0007-use-final-file-extension-for-upload-blocking.md), [`파일 업로드 API`](docs/sprints/sprint-1/sprint-1-file-upload-api.md)
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
- 관련 문서·코드: [`ADR 0005`](docs/adr/0005-limit-upload-to-known-non-executable-types.md), [`ADR 0018`](docs/adr/0018-fail-closed-on-mime-detection-failure.md), [`파일 업로드 API`](docs/sprints/sprint-1/sprint-1-file-upload-api.md)
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
