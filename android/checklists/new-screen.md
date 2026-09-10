# new-screen — 화면 추가
읽을 references: [10-project-structure, 11-architecture-layers, 12-state-management, 13-navigation, 14-di, 16-domain-layer, 17-compose-ui, 30-testing]
함께 로드할 공식 스킬: [navigation-3, testing-setup(테스트 인프라가 없을 때만)]

## 컨텍스트 수집 (구현 전)
- settings.gradle(.kts) 모듈 목록, 기존 `:feature:*` 하나의 패키지 트리(`ui`·`domain`·`data`·`model`·`di` 5종)
- NavDisplay·backStack 소유 위치와 entryProvider 조합 파일
- `:core:designsystem` 존재 여부와 Theme 이름
- 유사 화면 1개(같은 패턴을 따른다)

## 결정 항목 (구현 전 전부 명시)
구현 전 6개를 전부 적고, 결과를 산출물의 표준 준수 보고에 그대로 옮긴다.

| # | 결정 | 규칙 | 애매하면 질문할 것 |
|---|---|---|---|
| 1 | 모듈 위치 — 새 `:feature:{{feature}}` vs 기존 feature | R-10-01, R-10-04, R-10-13 | 기존 feature에 넣을지 |
| 2 | 진입·이탈·결과 반환 | R-13-01, R-13-02, R-13-07 | 어디서 진입하는지, 결과를 돌려줘야 하는지 |
| 3 | 상태 아키텍처 — MVVM-UDF vs MVI | R-12-02 | 매트릭스 기준 해당 여부가 갈릴 때 |
| 4 | UseCase 필요 여부 | R-16-02, R-16-07 | 비즈니스 규칙이 있는지 |
| 5 | 테스트 범위 | R-30-01, R-30-03 | 스크린샷 기준 상태 목록 |
| 6 | CI 영향 — 새 모듈이면 워크플로 확인 | R-31-01 | 없음 |

결정 2는 Navigation 3 1.1.7에 결과 전달 버스가 없으므로, 결과는 공유 상태나 상위 ViewModel로 돌려받는다(R-13-07).
결정 3의 판정 표는 `references/12-state-management.md`의 R-12-02 매트릭스를 그대로 채운다. 기준은 MVI 도입 사유 5개이며 필드 수·이벤트 수 같은 규모 수치가 아니다. 2개 이상 해당하면 MVI 골격(R-12-11)을 쓴다.

## 구현 순서
1. `{{Feature}}Key` + entry 등록 — `templates/ui/{{Feature}}Route.kt`
   → 검증: `NavKey` 구현 + `@Serializable`, `:app`의 entryProvider에 추가됨 (R-13-01, R-13-04)
2. `{{Feature}}UiState` — `templates/ui/{{Feature}}UiState.kt`
   → 검증: 불변 data class 하나, 기본값 전부 있음, 로딩·에러가 명시 필드 (R-12-01, R-12-08)
3. `{{Feature}}ViewModel` — `templates/ui/{{Feature}}ViewModel.kt` (MVI면 `templates/ui/mvi/{{Feature}}ViewModel.kt`)
   → 검증: `@HiltViewModel` + `@Inject constructor`, `asStateFlow()` 노출, `init`에서 비동기 시작 없음 (R-14-01, R-12-05, R-12-07)
4. `model/{{Feature}}.kt`(모델·Error·Result) → `data/`(Repository 인터페이스·구현·DataSource) → `domain/`UseCase (결정 4가 예일 때)
   → 검증: 인터페이스·구현 모두 data 패키지, data는 domain을 import하지 않음 (R-11-01, R-11-02)
5. `{{Feature}}Screen` + Preview — `templates/ui/{{Feature}}Screen.kt`
   → 검증: 상태+콜백 파라미터만, Preview 2종(다크·폰트 배율) (R-17-01, R-17-02)
6. DI 바인딩 — `templates/di/{{Feature}}Module.kt`
   → 검증: 인터페이스는 `@Binds`, `di/` 패키지, 필드 주입 없음 (R-14-04, R-14-05, R-14-02)
7. 테스트 — `templates/test/{{Feature}}ViewModelTest.kt`, `templates/test/{{Feature}}ScreenshotTest.kt`, `templates/test/Fake{{Feature}}Repository.kt`
   → 검증: 두 테스트 통과, fake는 테스트 소스셋에만 (R-30-01, R-30-03, R-30-05)
8. ktlint·detekt·Konsist·단위 테스트 실행
   → 검증: 0 이슈 (R-31-01)

## 산출물 검증
`checklists/review.md`를 실행해 "표준 준수 보고" 표를 응답 끝에 붙인다 (R-32-01).

제출 전 자기검사 2가지:
- 응답 순서: 유형 → 결정 항목 표 → 구현 → 검증 → 표준 준수 보고
- 인용한 R-ID 전부 제목 확인(오인용 0)
