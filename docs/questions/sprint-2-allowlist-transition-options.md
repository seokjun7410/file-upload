# ADR 0016 화이트리스트 전환 결정 전 선택지 검토 원문

> 문서 상태: ADR 결정 전 검토 원문
> 결정 상태: 미결정
> 대상 ADR: [`0016-migrate-to-allowlist-when-policy-requires.md`](../adr/0016-migrate-to-allowlist-when-policy-requires.md)

## 결정해야 하는 것

현재 전역 denylist를 유지하는 파일 업로드 정책을, 향후 조직·사용자별 정책 집합과 allowlist까지 확장할 것인지 결정해야 한다. 결정 대상은 단순히 `ALLOWLIST` 값을 추가하는 것이 아니라 다음 정책의 조합이다.

- 등록되지 않은 확장자의 기본 허용·거부 의미
- `GLOBAL`, `ORGANIZATION`, `USER` 범위의 우선순위와 상속
- 전역 강제 차단을 하위 범위가 해제할 수 있는지
- 정책 집합의 소유자·승인자와 변경 절차
- shadow 평가 기간·측정 항목·복귀 기준
- 기존 완료 파일에 대한 소급 평가 여부

## 현재 사실과 공통 규칙

현재 구현과 문서에서 확인되는 사실은 다음과 같다.

- 현재 정책은 `ExtensionPolicy(extension, policyType, blocked)` 형태의 전역 denylist다.
- 등록되지 않은 확장자는 차단 정책이 없으면 허용된다.
- fixed 정책은 고정 카탈로그, custom 정책은 quota와 중복 규칙의 적용을 받는다.
- 업로드는 확장자 정책과 콘텐츠 MIME 정책을 거쳐 저장되며, 서버 저장 파일명은 원본 파일명과 분리된다.
- 사용자·조직 인증과 정책 소유권은 아직 제품 범위에 들어오지 않았다.

어떤 선택을 하더라도 다음 규칙은 유지되어야 한다.

- denylist와 allowlist의 기본 의미를 하나의 정책 집합 안에서 암묵적으로 섞지 않는다.
- 정책 판정은 `allowed`만이 아니라 적용된 집합·scope·일치 항목·거부 이유를 설명할 수 있어야 한다.
- 기존 완료 파일은 신규 업로드 정책 전환만으로 소급 차단하지 않는다.
- 전역 강제 차단을 하위 범위가 해제할 수 있는지는 보안 불변식으로 명시한다.
- 정책 변경은 actor·시각·버전·전후 값을 감사할 수 있어야 한다.
- 정책 변경과 판정 결과의 동시성·캐시 무효화·트랜잭션 경계를 별도로 검증한다.

## 선택지 A — 전역 denylist 유지, allowlist는 도입하지 않음

### 구조 예시

```text
ExtensionPolicy(extension, policyType, blocked)
```

조직·사용자별 정책 요구가 실제로 발생하기 전까지 현재 모델을 유지한다. 새로운 보안 요구는 실행 MIME 차단이나 별도의 파일 검사 정책으로 다룬다.

### 동작 예시

`pdf`가 정책에 없으면 업로드를 허용한다. `exe`가 차단되어 있으면 모든 대상의 업로드를 거부한다. 조직 A만 `zip`을 허용하거나 사용자 B만 `csv`를 거부하는 차등 정책은 제공하지 않는다.

### 장점

- 미등록 확장자를 사용하는 일반적인 파일 공유 흐름을 유지한다.
- 허용목록 누락으로 정상 업무 파일이 조용히 차단되는 위험이 없다.
- 정책 판정과 운영 지표의 의미가 단순하다.

### 단점과 주의점

- 규제·폐쇄형 업무 환경에서 미등록 형식을 기본 허용하는 보안 요구를 충족하지 못한다.
- 조직·사용자별 예외와 업무 범위별 정책을 표현할 수 없다.
- 향후 allowlist 요구가 발생하면 shadow 데이터와 허용 형식 목록을 새로 수집해야 한다.

### 영향

- 도메인: 기존 denylist 불변식을 유지한다.
- 영속성: 새 정책 집합·적용 관계가 필요 없다.
- API: 현재 정책 API를 유지한다.
- 테스트: 현재 확장자 판정과 MIME 검증 회귀 테스트를 중심으로 유지한다.
- 운영: 허용목록 누락보다 차단목록 갱신과 예외 처리에 책임이 집중된다.

## 선택지 B — scope별 정책 집합과 가장 구체적인 범위 우선

### 구조 예시

```text
PolicySet(id, mode, owner, version)
PolicyEntry(policy_set_id, extension, effect)
PolicyAssignment(scope_type, scope_id, policy_set_id, priority)
```

`USER`가 `ORGANIZATION`보다, `ORGANIZATION`이 `GLOBAL`보다 우선한다. 각 정책 집합은 `DENYLIST` 또는 `ALLOWLIST` 중 하나의 mode만 가진다.

### 동작 예시

GLOBAL allowlist가 `pdf, docx`만 허용하고 조직 A가 `xlsx`를 추가하면, 조직 A의 사용자는 세 형식을 업로드할 수 있다. 사용자 B가 자신의 allowlist를 연결하면 사용자 B는 조직 정책 대신 자신의 집합을 사용한다.

### 장점

- 조직·사용자별 업무 형식을 독립된 정책 집합으로 관리할 수 있다.
- scope와 mode가 명시되어 판정 책임을 설명하기 쉽다.
- 버전·소유자·승인자를 정책 집합에 연결하기 쉽다.

### 단점과 실패 모드

- 가장 구체적인 범위가 전역 보안 차단을 덮어쓸 수 있다.
- 정책 집합이 없는 대상의 기본 mode가 별도로 필요하다.
- 여러 assignment가 동시에 존재할 때 우선순위 충돌과 캐시 무효화가 필요하다.
- 사용자가 자기 allowlist로 조직의 보안 예외를 만들 수 있으면 권한 모델이 약해진다.

### 영향

- 도메인: scope 우선순위, 상속, override 불변식을 먼저 확정해야 한다.
- 영속성: 정책 집합·항목·적용 관계와 중복·활성 버전 제약이 필요하다.
- API: 정책 집합 생성·승인·적용·판정 결과 조회 계약이 추가된다.
- 테스트: 동일 대상의 다중 집합, 충돌, 전환 전후 판정, 동시 변경을 검증해야 한다.
- 운영: 사용자·조직별 거부율과 정책 버전을 추적해야 한다.

## 선택지 C — 정책 집합 + 전역 강제 차단 guardrail

### 구조 예시

```text
PolicySet(id, mode, owner, version)
PolicyEntry(policy_set_id, extension, effect)
PolicyAssignment(scope_type, scope_id, policy_set_id, priority)
GlobalGuardrail(extension, reason, enforced)
```

정책 집합은 scope별로 평가하되, 전역 guardrail의 `DENY`는 어떤 하위 정책도 해제할 수 없도록 한다. 실행 파일 확장자·실행 MIME처럼 시스템 전체에서 차단해야 하는 규칙을 이 경계에 둔다.

### 동작 예시

조직 A의 allowlist가 `pdf, zip, exe`를 허용하더라도 GLOBAL guardrail이 `exe`를 강제 차단하면 `exe` 업로드는 거부된다. `zip`은 조직 A에서 허용하고 조직 B에서는 allowlist에 없으면 거부할 수 있다.

### 장점

- 조직·사용자별 허용 형식을 지원하면서 전역 보안 불변식을 보존한다.
- “왜 하위 정책이 허용했는데도 거부되는가”를 guardrail과 decision reason으로 설명할 수 있다.
- allowlist 전환과 실행 MIME denylist 같은 시스템 안전장치를 분리할 수 있다.

### 단점과 실패 모드

- guardrail의 대상·소유자·변경 승인 절차가 잘못되면 정상 업무 형식까지 전역 차단될 수 있다.
- guardrail, denylist, allowlist의 평가 순서와 오류 코드가 복잡해진다.
- 정책 집합과 별도로 전역 안전장치의 버전·감사·복귀 절차를 운영해야 한다.
- 사용자가 기대하는 “내 정책에서 허용”과 실제 결과가 다를 수 있어 UI에 최종 판정 이유가 필요하다.

### 영향

- 도메인: `GLOBAL DENY`가 override 불가라는 강한 불변식을 둔다.
- 영속성: 정책 집합과 guardrail의 변경·버전·감사 경계를 분리한다.
- API: 최종 decision에 `matchedScope`, `matchedRule`, `reason`, `guardrailApplied` 같은 설명 정보가 필요하다.
- 테스트: guardrail 우선, scope별 allowlist, denylist 복귀와 신규 업로드만 적용을 함께 검증한다.
- 운영: 강제 차단 변경은 별도 승인과 높은 가시성의 알림이 필요하다.

## 비교 요약

| 기준 | A. 전역 denylist 유지 | B. 구체 scope 우선 | C. 전역 guardrail + 정책 집합 |
|---|---|---|---|
| 미등록 확장자 기본 의미 | 허용 | mode에 따라 결정 | mode에 따라 결정 |
| 조직·사용자별 정책 | 지원하지 않음 | 지원 | 지원 |
| 전역 강제 차단 | 자연스럽게 보장 | 별도 규칙 필요 | 명시적으로 보장 |
| 판정 설명 가능성 | 높음 | 중간~높음 | 높지만 모델 복잡 |
| 정상 형식 누락 위험 | 낮음 | allowlist에서 존재 | allowlist에서 존재 |
| 운영 책임 | 차단목록 관리 | 집합·scope·승인 관리 | 집합·guardrail·승인 관리 |
| 적합한 환경 | 일반 파일 공유 | 예외가 많은 조직형 서비스 | 규제·보안이 강한 조직형 서비스 |

## 결정 전 동작 예시

다음 입력을 기준으로 선택지의 차이를 확인해야 한다.

| 상황 | A | B | C |
|---|---|---|---|
| GLOBAL에서 `exe` 차단, USER가 허용 | 항상 차단 | USER 우선 규칙에 따라 달라짐 | 항상 차단 |
| 정책 집합 없는 사용자에게 `csv` 업로드 | 정책에 없으면 허용 | 기본 mode 결정 필요 | 기본 mode 결정 필요 |
| 조직 A만 `zip` 허용 | 지원하지 않음 | 조직 집합으로 허용 | 조직 집합으로 허용 |
| 기존 완료 파일이 새 allowlist에 없음 | 영향 없음 | 신규 업로드만 차단 | 신규 업로드만 차단 |

## 결정 전 추천 의견

이는 비구속적 검토 의견이다.

- 실제 조직·사용자별 정책 요구가 아직 없다면 A를 유지하고 ADR 0016을 보류하는 것이 적절하다.
- allowlist가 필요하고 전역 보안 차단을 하위 범위가 해제할 수 없어야 한다면 C를 우선 검토한다.
- 보안 guardrail이 필요 없고 scope별 완전한 override가 제품 요구라면 B를 검토할 수 있다.
- B 또는 C를 선택하더라도 구현 전에 기본 mode, scope 우선순위, 소유자·승인자, shadow 기간·허용 거부율, 복귀 조건을 확정해야 한다.

## 결정 전에 확인할 질문

1. 미등록 확장자를 기본 허용하면 안 되는 제품·규제 요구가 실제로 존재하는가?
2. GLOBAL 강제 차단을 ORGANIZATION·USER가 해제할 수 있는가?
3. 정책 집합이 없는 대상의 기본 mode는 `DENYLIST`인가 `ALLOWLIST`인가?
4. allowlist의 소유자·승인자는 누구이며, 변경 승인과 긴급 복귀는 어떻게 하는가?
5. shadow 평가 기간은 얼마이며, 최대 허용 거부율·오탐률은 얼마인가?
6. 여러 scope의 정책이 충돌할 때 가장 구체적인 범위 우선, deny 우선, guardrail 우선 중 무엇을 적용하는가?
7. 기존 완료 파일을 계속 제공한다는 정책을 유지하는가?

## ADR 전환 조건

다음 조건이 모두 충족되면 이 검토 원문을 근거로 ADR 0016을 갱신하거나 새 결정 문서로 전환한다.

- 선택지 또는 현행 유지가 사람의 결정으로 확정되었다.
- 기본 mode와 scope 우선순위가 예시로 검증되었다.
- 전역 강제 차단의 override 가능 여부가 불변식으로 명시되었다.
- 정책 집합 소유자·승인자와 감사 범위가 정해졌다.
- shadow 측정 기간·허용 거부율·복귀 기준이 수치 또는 관찰 가능한 조건으로 정해졌다.
- 기존 완료 파일과 신규 업로드의 적용 경계가 확정되었다.

그 전에는 policy-set 스키마, scope API, 실제 allowlist 차단 코드를 구현하지 않는다.

## 관련 문서

- [`0016-migrate-to-allowlist-when-policy-requires.md`](../adr/0016-migrate-to-allowlist-when-policy-requires.md)
- [`19-future-policy-model.md`](../../.internal-docs/file-upload-risk-analysis/19-future-policy-model.md)
- [`sprint-2-prd.md`](../sprints/sprint-2/sprint-2-prd.md)
- [`sprint-2-implementation-checklist.md`](../sprints/sprint-2/sprint-2-implementation-checklist.md)
