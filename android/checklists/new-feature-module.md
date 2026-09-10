# new-feature-module — feature 모듈 추가
읽을 references: [10-project-structure, 11-architecture-layers, 14-di, 31-ci-cd]
함께 로드할 공식 스킬: [없음]

## 컨텍스트 수집 (구현 전)
- settings.gradle(.kts)의 include 목록과 `build-logic` included build 존재 여부
- 기존 convention plugin id 목록(`convention.android.*`)과 버전 카탈로그 별칭
- 참고할 기존 `:feature:*` 하나의 build.gradle.kts와 패키지 트리
- CI 워크플로 파일에서 모듈을 열거하는 곳이 있는지

## 결정 항목 (구현 전 전부 명시)
| # | 결정 | 규칙 | 애매하면 질문할 것 |
|---|---|---|---|
| 1 | 모듈 이름 — `:feature:{{feature}}` 단일 모듈로 시작 | R-10-01, R-10-13 | 이름이 도메인 용어와 맞는지 |
| 2 | 의존 대상 core 모듈 | R-10-02, R-10-03, R-10-05 | `:core:*` 중 무엇이 필요한지 |
| 3 | convention plugin 선택 | R-10-09, R-10-10, R-10-14 | 기존 6종으로 충분한지 |
| 4 | 버전 선언 위치 | R-10-12 | 카탈로그에 없는 의존이 있는지 |
| 5 | CI 매트릭스 반영 | R-31-01, R-31-02 | 워크플로가 모듈을 열거하는지 |

다른 feature가 이 feature의 NavKey를 직접 참조해야 할 때만 `:api`/`:impl`로 나눈다. 그 전까지는 단일 모듈이고 화면 간 이동은 콜백과 `:app` 조합층으로 처리한다 (R-10-13, R-13-02).

## 구현 순서
1. `build.gradle.kts` 작성 — `templates/module/build.gradle.kts`
   → 검증: convention plugin id만 적용, 의존은 `implementation`, 버전 리터럴 없음 (R-10-10, R-10-05, R-10-12)
2. 패키지 트리 생성 — `{{package}}.feature.{{feature}}` 아래 `ui`·`domain`·`data`·`model`·`di`
   → 검증: 계층 이름 5종만 존재 (R-10-06, R-11-01)
3. settings.gradle(.kts)에 `include(":feature:{{feature}}")` 등록, `:app`에서만 참조
   → 검증: 다른 `:feature:*`가 이 모듈을 의존하지 않음 (R-10-02, R-10-08)
4. 빈 화면 1개 배치 — `checklists/new-screen.md`의 순서 1~3, 5를 최소로 실행
   → 검증: `:app` entryProvider에서 진입 가능, Preview 2종 (R-13-04, R-17-02)
5. DI 모듈 뼈대 — `templates/di/{{Feature}}Module.kt`
   → 검증: `@Module` + `@InstallIn`, `di/` 패키지 (R-14-04)
6. CI 확인 — PR 게이트 실행
   → 검증: ktlintCheck → detektDebug → 단위 테스트 → assembleDebug 순서로 새 모듈이 포함됨 (R-31-01, R-31-03)

## 산출물 검증
`checklists/review.md`를 실행해 "표준 준수 보고" 표를 응답 끝에 붙인다. "모듈 위치" 행에 새 모듈 경로와 의존 대상 core 모듈을 적는다 (R-32-01).

제출 전 자기검사 2가지:
- 응답 순서: 유형 → 결정 항목 표 → 구현 → 검증 → 표준 준수 보고
- 인용한 R-ID 전부 제목 확인(오인용 0)
