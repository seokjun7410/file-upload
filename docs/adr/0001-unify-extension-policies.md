---
status: accepted
---

# 확장자 정책을 단일 엔티티로 관리

fixed와 custom은 독립된 데이터 개념이 아니라 정규화된 확장자에 대한 차단 정책의 유형이다. 따라서 공통 데이터는 하나의 `ExtensionPolicy` 엔티티로 관리하고, `policyType`으로 `FIXED`와 `CUSTOM`을 구분한다. fixed는 기본 정책의 `blocked` 상태를 check/uncheck로 변경하고, custom은 정책의 등록·삭제로 차단 여부를 표현한다.

정책의 유형별 행위와 비즈니스 규칙은 WAS의 도메인·애플리케이션 로직에서 검증한다. 반면 `extension` unique, 필수값, 유형과 상태의 기본 무결성처럼 모든 데이터 기록 경로에 공통인 구조적 제약은 DB가 보장한다. 고정 확장자 7개는 `blocked=false`로 초기화하고, custom 등록은 `blocked=true`로 생성하며 삭제는 행 삭제로 처리한다.

## 결정 결과

- 저장 모델은 `p_extension_policy` 단일 테이블을 사용한다.
- 핵심 필드는 `extension`, `policy_type`, `blocked`다.
- 하나의 정규화 확장자는 하나의 정책만 가질 수 있다.
- `CUSTOM + blocked=false` 같은 유형별 불변식 위반은 도메인·애플리케이션 로직에서 차단한다.
- API는 `policy_type`을 기준으로 fixed 상태 배열과 custom 목록을 조립한다.

## 결과와 주의점

- 정책 조회와 업로드 차단 판정은 하나의 저장소 조회로 통합된다.
- fixed/custom 간 중복은 단일 `extension` unique 제약으로 방어한다.
- 유형별 행위 차이가 엔티티와 서비스에 조건 분기로 나타날 수 있으므로 생성·변경 메서드가 잘못된 상태를 만들지 않도록 한다.
- custom 최대 200개와 같은 집계성 규칙은 WAS의 트랜잭션에서 검증하고 동시 요청 결과를 확인한다.
