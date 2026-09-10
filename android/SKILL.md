---
name: android-standards
description: Android 앱(Kotlin·Compose·Navigation 3·Hilt·멀티모듈) 그린필드 개발 표준. 화면/페이지 추가, 기능 모듈 추가, API·DB 연동, 함수·유틸 작성, 버그 수정, 리팩터링, 산출물 리뷰 등 Android 작업이면 먼저 요청 유형을 분류하고 checklists/ 파이프라인대로 결정→구현→표준 준수 보고를 한다. 트리거 예 - "페이지 만들어줘", "화면 추가", "기능 붙여줘", "API 연동", "이 함수 짜줘", "버그 고쳐", "리팩터링", "리뷰해줘".
---

# Android 표준 (android-standards)

## 우선순위
프로젝트 지침 파일(CLAUDE.md/AGENTS.md) > 강제 장치가 검사하는 규칙 > 이 팩 references > 로컬 공식 Android 스킬의 구현 방법 > 일반 관행.
그린필드(Kotlin·Compose·Navigation 3·Hilt·멀티모듈) 기준이다. 레거시 프로젝트에서는 신규 코드에만 적용한다.

## 절차 (순서 고정, 건너뛰기 금지)
1. **분류** — 아래 표에서 유형 하나를 고르고 "유형: new-screen" 한 줄을 사용자에게 알린다. 둘 이상 걸치거나 모호하면 AskUserQuestion으로 묻는다.
2. **컨텍스트** — 체크리스트의 "컨텍스트 수집" 항목을 읽는다. 프로젝트 지침 파일은 항상 먼저.
3. **결정** — 체크리스트 "결정 항목" 표를 전부 채운다. 규칙으로 정해지는 것은 R-ID를 적는다. 사용자만 정할 수 있는 것은 묻는다. 임의 결정 금지.
4. **구현** — 체크리스트 "구현 순서"대로. `$PACK_ROOT/templates`를 복사해 시작한다(치환법·팩 루트 계산: `$PACK_ROOT/templates/README.md`).
5. **검증·보고** — `checklists/review.md`를 실행하고 응답 끝에 "표준 준수 보고" 표를 붙인다. 제출 전에 최종 응답을 아래 "최종 응답 구조" 순서대로 다시 훑어 결정 표가 구현보다 앞에 있는지 확인한다.

## 최종 응답 구조 (순서 고정)
최종 메시지는 반드시 이 순서로 쓴다. 내용이 맞아도 순서가 어긋나면 미준수다.
1. `유형: <type>` 한 줄.
2. `## 결정 항목` 표 — 체크리스트의 결정 항목 전부. 각 행에 R-ID와 그 규칙의 제목을 적는다.
3. `## 구현` — 파일 목록과 핵심 코드 요약.
4. `## 검증` — 실행한 명령과 결과.
5. `## 표준 준수 보고` 표 (R-32-01).

작업 중에 결정을 먼저 했더라도 최종 응답에서 구현 서술 뒤로 밀리면 안 된다. 결정 표는 구현 절 앞에 온다.

## 유형 → 체크리스트
| 유형 | 트리거 예 | 체크리스트 |
|---|---|---|
| new-screen | 페이지/화면 만들어줘, 상세 화면 추가 | checklists/new-screen.md |
| new-feature-module | 새 기능 모듈, :feature:* 추가 | checklists/new-feature-module.md |
| new-data-source | API 붙여줘, DB 테이블 추가, DataStore | checklists/new-data-source.md |
| function | 이 함수 짜줘, 유틸·확장함수, 파싱 로직 | checklists/function.md |
| bugfix | 버그 고쳐, 크래시, 이상 동작 | checklists/bugfix.md |
| refactor | 리팩터링, 정리, 구조 변경(동작 동일) | checklists/refactor.md |
| review | 리뷰해줘, 표준 준수 확인 | checklists/review.md |

## 팩 루트 (`PACK_ROOT`)
이 팩의 모든 경로는 **지금 읽은 `SKILL.md`가 들어 있는 디렉터리** 기준이다. 설치 방식마다 다르므로 `android/…`로 고정하지 않는다.

```bash
PACK_ROOT="$(dirname "$SKILL_PATH")"   # 전역 스킬: ~/.claude/skills/android-standards, bootstrap 주입: <프로젝트>/harness/android
```

references·templates·checklists는 전부 `$PACK_ROOT/references`, `$PACK_ROOT/templates`, `$PACK_ROOT/checklists`로 읽고 복사한다.

## 점진 로드 규칙
- `references/`를 전부 읽지 않는다. 체크리스트 상단 `읽을 references:` 목록만 읽는다.
- **판정·인용에 쓰는 R-ID의 소유 문서는 체크리스트 목록에 없어도 추가로 읽는다.** 제목만 보고 예외 조건·결정 매트릭스를 알 수 없으므로, 목록 밖 규칙을 근거로 삼을 때는 그 파일을 열고 인용한다.
- 규칙 ID `R-NN-MM`을 근거로 인용한다. 팩에 규칙이 없으면 "규칙 없음"이라고 쓰고 사용자에게 묻는다. 출처 없는 규칙을 만들지 않는다.
- R-ID를 인용할 때는 `R-10-04(모듈 간 배치 …)`처럼 규칙 제목(요지)을 함께 적고, 인용 전 `grep '^### R-10-04' "$PACK_ROOT"/references/*.md`로 제목을 확인한다. 제목이 인용 맥락과 다르면 그 규칙이 아니다.
- 결정이 걸린 주제는 문서의 결정 매트릭스(R-12-02 등)를 표로 채워 보여준다.

## 공식 스킬 연계
구현 방법은 로컬 공식 Android 스킬에 위임한다: navigation-3(네비 구현), testing-setup(테스트 인프라 세팅), adaptive(적응형 레이아웃), edge-to-edge(인셋), migrate-xml-views-to-jetpack-compose(이관). 이 팩은 "무엇을 어떻게 결정하고 무엇으로 검증하는가"만 담는다.

## 표준 준수 보고 형식 (R-32-01)
아래 `(R-NN-xx)` 자리에는 ID만 적지 말고 `R-10-04(모듈 간 배치 …)`처럼 제목 요지를 함께 적는다.

| 항목 | 내용 |
|---|---|
| 요청 유형 | |
| 모듈 위치 | (R-10-xx) |
| 네비게이션 | (R-13-xx) |
| 상태 아키텍처 | 매트릭스 판정 포함 (R-12-02) |
| UseCase | (R-16-02) |
| 테스트 | 통과 목록 (R-30-xx) |
| CI | 영향 |
| 어긴 규칙 | ID와 사유. 없으면 "없음" |

## 금지
- 요청 없는 기능·UI·추상화 추가.
- 검증 없이 완료 보고. 테스트 실패는 실패라고 쓴다.
- 레거시 어댑터 즉흥 제작. 그린필드 규칙이 맞지 않으면 프로젝트 지침을 따르고 그 사실을 보고에 적는다.

## 구조
references/(규칙) · checklists/(파이프라인) · templates/(코드 골격) · enforcement/(lint·Konsist·CI) · eval/(자체 검증) · research/(출처 조사)
