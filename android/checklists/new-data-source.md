# new-data-source — 데이터 소스·Repository 추가
읽을 references: [11-architecture-layers, 14-di, 15-data-layer, 22-coroutines-flow, 23-error-handling, 30-testing]
함께 로드할 공식 스킬: [testing-setup(테스트 인프라가 없을 때만)]

## 컨텍스트 수집 (구현 전)
- 기존 Repository 하나의 인터페이스·`Default*` 구현·DataSource 짝
- Room·DataStore·Retrofit 인스턴스를 소유한 core 모듈과 노출 타입
- 디스패처 qualifier 이름과 제공 모듈
- 이 데이터를 쓸 화면·UseCase 목록

## 결정 항목 (구현 전 전부 명시)
| # | 결정 | 규칙 | 애매하면 질문할 것 |
|---|---|---|---|
| 1 | Remote/Local 종류 — 하나가 하나의 소스만 담당 | R-15-02, R-15-03 | 두 소스를 합쳐야 하는지 |
| 2 | DTO↔모델 매핑 위치 | R-11-07, R-15-13 | 매퍼를 어느 모듈에 둘지 |
| 3 | 오프라인 우선 여부와 읽기 SSOT | R-15-07, R-15-08 | 로컬 캐시가 필요한지 |
| 4 | 에러 매핑 — 도메인별 sealed 타입 | R-15-11, R-23-01, R-23-04, R-23-05 | 호출자가 어떤 실패를 구분해야 하는지 |
| 5 | 디스패처 — DataSource가 주입받아 이동 | R-14-08, R-15-05, R-22-02 | IO 외 디스패처가 필요한지 |
| 6 | 노출 형태 — suspend vs Flow | R-15-04, R-22-06 | 계속 관찰해야 하는 값인지 |

Repository 인터페이스와 `Default*` 구현은 둘 다 data 계층에 두고, 모델·에러·결과 타입만 `model` 패키지에 둔다. data는 domain·ui를 참조하지 않는다 (R-11-01, R-11-02, R-15-01, R-15-06).

## 구현 순서
1. `model` 타입 — 모델, sealed Error, Result 타입 — `templates/model/{{Feature}}.kt`
   → 검증: `model`은 다른 계층을 import하지 않음, Android 타입 없음 (R-11-01, R-16-08)
2. DTO·Entity 정의와 매퍼 확장 함수
   → 검증: `toDomain()`·`toEntity()` 명명, data 모듈 안에 위치 (R-15-13)
3. DataSource — `templates/data/{{Feature}}RemoteDataSource.kt`, `templates/data/{{Feature}}LocalDataSource.kt`
   → 검증: 하나가 하나의 소스만 담당, 디스패처 주입받아 `withContext`로 이동 (R-15-03, R-15-05)
4. Repository 인터페이스(data 계층) — `templates/data/{{Feature}}Repository.kt`
   → 검증: `model` 패키지 타입만 노출, 일회성은 suspend·관찰은 `Flow` (R-15-01, R-15-04)
5. 구현(data 계층) — `templates/data/Default{{Feature}}Repository.kt`
   → 검증: 예외를 경계에서 잡아 `model` 에러 타입으로 변환, 재시도 정책도 여기 (R-15-11, R-23-05, R-23-10)
6. DI 바인딩 — `templates/di/{{Feature}}Module.kt`, `templates/di/Dispatchers.kt`
   → 검증: 인터페이스는 `@Binds`, 외부 타입은 `@Provides`, 디스패처는 qualifier (R-14-05, R-14-08)
7. Fake + Repository 테스트 — `templates/test/Fake{{Feature}}Repository.kt`
   → 검증: fake DataSource로 조립, Flow 검증은 Turbine, fake는 테스트 소스셋에만 (R-30-10, R-30-08, R-30-05)
8. ktlint·detekt·Konsist·단위 테스트 실행
   → 검증: 0 이슈 (R-31-01)

## 산출물 검증
`checklists/review.md`를 실행해 "표준 준수 보고" 표를 응답 끝에 붙인다. 결정 3·4의 판정을 표에 남긴다 (R-32-01).

제출 전 자기검사 2가지:
- 응답 순서: 유형 → 결정 항목 표 → 구현 → 검증 → 표준 준수 보고
- 인용한 R-ID 전부 제목 확인(오인용 0)
