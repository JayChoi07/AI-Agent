# Room 스키마 변경(마이그레이션) 규칙 조사 노트
조사일: 2026-09-30 (모든 버전·날짜는 이 날 페이지·저장소에서 확인한 값)

## 요약
- **Room 2.x는 공식 문서에서 "deprecated"로 표기됐고, Room 3.0(`androidx.room3`)이 2026-07-01 정식 출시됐다.** 안정 버전은 3.0.3(2026-09-09), 2.x 마지막은 2.8.5(2026-09-09). 팩의 Room 규칙은 3.x 기준으로 쓸지 2.x로 쓸지부터 정해야 한다(아래 "팩 결정 필요").
- 아래 인용부호 문장은 curl로 받은 HTML을 태그 제거·공백 정규화한 텍스트(또는 raw 소스)에 글자 그대로 포함되는지 스크립트로 재확인한 것만 썼다. 확인 못 한 것은 "요약"이라고 적었다.
- 문서 본문이 Room 3 기준으로 이미 교체돼 있어, Room 2.x 시절 마이그레이션 가이드 원문은 열지 못했다(`/room/v2` 하위 마이그레이션 페이지는 404).

## 출처
| # | 조직 | 문서명 | URL | 종류 | 갱신일/버전 |
|---|---|---|---|---|---|
| S1 | Google | androidx.room 릴리스 노트(2.x) | https://developer.android.com/jetpack/androidx/releases/room | 릴리스 노트 | 2.8.5 (2026-09-09) |
| S2 | Google | androidx.room3 릴리스 노트(3.x) | https://developer.android.com/jetpack/androidx/releases/room3 | 릴리스 노트 | 3.0.3 (2026-09-09), 3.1.0-alpha01 |
| S3 | Google | Save data in a local database using Room (설정) | https://developer.android.com/training/data-storage/room | 공식 가이드 | 3.0.3 기준 |
| S4 | Google | Migrate your Room database | https://developer.android.com/training/data-storage/room/migrating-db-versions | 공식 가이드 | 2026-09-09 |
| S5 | Google | Test and debug your database | https://developer.android.com/training/data-storage/room/testing-db | 공식 가이드 | 2026-09-08 |
| S6 | Google | Migrate from Room 2.x to Room 3.0 | https://developer.android.com/training/data-storage/room/migration-2-to-3 | 공식 가이드 | 미표기 |
| S7 | Google | Room 2.x(Legacy) 가이드 | https://developer.android.com/training/data-storage/room/v2 | 공식 가이드 | 미표기 |
| S8 | Google | API 레퍼런스 `androidx.room3.RoomDatabase.Builder` | https://developer.android.com/reference/kotlin/androidx/room3/RoomDatabase.Builder | 공식 API 문서 | 2026-09-09 |
| S9 | Google | API 레퍼런스 `androidx.room.RoomDatabase.Builder`(2.x) | https://developer.android.com/reference/kotlin/androidx/room/RoomDatabase.Builder | 공식 API 문서 | 2.8 계열 |
| S10 | Google | API 레퍼런스 `androidx.room3.Database` | https://developer.android.com/reference/kotlin/androidx/room3/Database | 공식 API 문서 | 3.x |
| S11 | Google | API 레퍼런스 `Migration` / `AutoMigration` / `AutoMigrationSpec` (room3) | https://developer.android.com/reference/kotlin/androidx/room3/migration/Migration , https://developer.android.com/reference/kotlin/androidx/room3/AutoMigration , https://developer.android.com/reference/kotlin/androidx/room3/migration/AutoMigrationSpec | 공식 API 문서 | 2026-09-09 |
| S12 | Google | API 레퍼런스 `androidx.room3.testing.MigrationTestHelper` | https://developer.android.com/reference/kotlin/androidx/room3/testing/MigrationTestHelper | 공식 API 문서 | 3.x |
| S13 | Google | Write asynchronous DAO queries | https://developer.android.com/training/data-storage/room/async-queries | 공식 가이드 | 미표기 |
| S14 | Google | Auto Backup for Apps | https://developer.android.com/identity/data/autobackup | 공식 가이드 | 2026-02-26 |
| S15 | Google | Back up user data with Auto Backup(개요) | https://developer.android.com/identity/data/backup | 공식 가이드 | 미표기 |
| S16 | SQLite | ALTER TABLE | https://www.sqlite.org/lang_altertable.html | 공식 문서 | 조사일 조회본 |
| S17 | Google(AOSP) | androidx 저장소 소스 — `room3-runtime`의 `RoomConnectionManager.kt`, `util/MigrationUtil.kt`, `util/DBUtil.kt` / `room3-compiler`의 `vo/Database.kt`, `processor/ProcessorErrors.kt` (GitHub 미러 `androidx/androidx`, 브랜치 androidx-main) | https://github.com/androidx/androidx/tree/androidx-main/room3 | 공식 저장소 소스 | androidx-main, 조사일 |
| S18 | Google(AOSP) | 소스 `room3-testing`의 `MigrationTestHelper.android.kt` / `.jvm.kt` | https://github.com/androidx/androidx/tree/androidx-main/room3/room3-testing/src | 공식 저장소 소스(KDoc) | androidx-main, 조사일 |
| S19 | Google | Google Maven `maven-metadata.xml`(room3-runtime·room3-testing·room3-compiler·room3-gradle-plugin·room-runtime·sqlite-bundled)와 `room3-runtime-android-3.0.3.module` | https://dl.google.com/dl/android/maven2/androidx/room3/room3-runtime/maven-metadata.xml 등 | 저장소 메타데이터 | lastUpdated 2026-09-09 |
| S20 | Google | Set up Room Database for KMP | https://developer.android.com/kotlin/multiplatform/room | 공식 가이드 | 문서 예시는 room3 3.1.0-alpha01 |
| S21 | Google | androidx.sqlite 릴리스 노트 | https://developer.android.com/jetpack/androidx/releases/sqlite | 릴리스 노트 | 2.7.1 (2026-09-09) 안정 |
| S22 | LemonAppDev | Konsist — Verify Classes / `KoAnnotationProvider`·`KoAnnotationDeclaration` 소스 | https://docs.konsist.lemonappdev.com/veryfying-codebase/verify-classes.md , https://github.com/LemonAppDev/konsist | 공식 문서·소스 | main |
| S23 | (내부) | 팩 조사 노트 `research/release-cd.md` — versionCode 다운그레이드 방지 인용 | (로컬) | 팩 내부 노트, 인용용 | 2026-09-30 시점 |

## 핵심 내용 (출처별)

### Q1. 현재 버전·호환·Gradle 플러그인
- **Room 3.0 = 새 패키지·새 좌표.** "Room 3.0 (package androidx.room3) is a major version update of Room 2.x package (androidx.room) that focuses on Kotlin Multiplatform (KMP)." 좌표는 `androidx.room3:room3-runtime`, `androidx.room3:room3-compiler`(ksp), 테스트는 `androidx.room3:room3-testing`. [S2]
- 3.0.0 정식 2026-07-01, 3.0.1 07-29, 3.0.2 08-26, **3.0.3 09-09**, 3.1.0-alpha01 09-09. Google Maven 버전 목록도 3.0.3이 마지막 안정 버전이다. [S2][S19]
- 2.x는 2.8.5(2026-09-09)까지. Room 2.x 가이드 페이지 상단: "Caution: This guide covers Room 2.x, which is deprecated." [S7][S1]
- 3.0의 큰 차이(릴리스 노트): 새 패키지 / SupportSQLite 없음(래퍼 아티팩트 `room3-sqlite-wrapper` 예외) / DB 작업은 코루틴 기반 / Kotlin 코드 생성만 / "Kotlin Symbol Processing (KSP) is required." / "A SQLiteDriver is now required to build a RoomDatabase" / DAO 함수는 리액티브 타입이 아니면 `suspend`. [S2]
- 3.0 노트는 KAPT·Java 어노테이션 프로세서를 쓸 수 없다고 하고, "It is recommended to have a multi-module project where Room usage is concentrated" 라고 적는다(Room 사용을 한 모듈에 모으고 그 모듈에만 Kotlin Gradle Plugin·KSP 적용). R-15-14와 방향이 같다. [S2]
- 이 릴리스 노트가 버전을 바꾼 지점: `fallbackToDestructiveMigration*`의 `dropAllTables` 인자에 기본값(true) 추가(3.0.0-alpha02), `MigrationTestHelper`·`Migration.migrate`·`RoomDatabase.Callback`이 `SQLiteConnection` 기반이며 `migrate`는 `suspend`. [S2][S11]
- Room 3 의존성 요건(Gradle 모듈 메타데이터): `androidx.sqlite:sqlite`·`sqlite-async`·`sqlite-framework` 2.7.1, kotlin-stdlib 2.1.20, kotlinx-coroutines 1.9.0. androidx.sqlite 안정 최신은 2.7.1(2026-09-09), `sqlite-bundled`의 최신은 2.8.0-alpha01이라 안정 번들 드라이버는 2.7.1이다. [S19][S21]
- **AGP 9.4·KSP 2.3.x·Kotlin 2.4와의 호환 문장은 Room 릴리스 노트에 없다(침묵).** 있는 것은 2.8.0-rc02의 "Update the minimum Android Gradle Plugin (AGP) version compatible with the Room Gradle Plugin from 8.1 to 8.4." 뿐이며 이는 2.x 플러그인 기준이다. Room 3 플러그인의 최소 AGP는 노트에 없다. [S1][S2]
- **Room Gradle 플러그인**: Room 3 플러그인 id는 `androidx.room3`, 확장은 `room3 { schemaDirectory("$projectDir/schemas") }`. "Setting a schemaDirectory is required when using the Room Gradle Plugin." 플러그인은 스키마(컴파일 출력이자 자동 마이그레이션 입력)를 "reproducible and cacheable builds"가 되도록 구성한다(요약). 플레이버·빌드 타입별로 `schemaDirectory(variantMatchName, path)`를 여러 번 써서 나눌 수 있고 "Make sure these are exhaustive and cover all variants." [S2][S4]
- **KSP 인자와의 관계**: 플러그인 없이 쓸 때는 KSP 인자 `room.schemaLocation`을 `CommandLineArgumentProvider`(`RoomSchemaArgProvider`)로 넘기라고 안내한다. 인자 이름은 Room 3에서도 `room.schemaLocation`이다. 플러그인과 명시적 `room.schemaLocation` 옵션을 함께 쓰면 컴파일러가 오류를 낸다(요약, `INVALID_GRADLE_PLUGIN_AND_SCHEMA_LOCATION_OPTION`). [S4][S17]
- 플러그인 ID 표기 불일치: MigrationTestHelper KDoc 예시는 아직 `id 'androidx.room'`·`room { }`로 적혀 있으나 컴파일러 오류 문구와 릴리스 노트는 `androidx.room3`·`room3 { }`이다. 문서 예시보다 릴리스 노트·컴파일러를 따른다. [S12][S17][S2]
- 2.x 플러그인은 `androidx.room` + `android { room { schemaDirectory(...) } }`. [S1]

### Q2. 스키마 내보내기
- `@Database.exportSchema` 기본값은 `true`. 문장: "Even though it is not mandatory, it is a good practice to have version history of your schema in your codebase." / "If you do export schemas then you should commit the schema files into your version control system (but don't ship them with your app!)." 끄는 경우로 in-memory 전용 DB를 예로 든다(요약). [S10]
- 가이드: "Store these files in your version control system so that you can recreate lower versions of the database for testing and support automated migration generation." [S4]
- 위치: 플러그인 사용 시 `schemas/<플레이버·빌드타입>/<DB 클래스 전체이름>/<버전>.json` 형태(예시 `schemas/flavorOneDebug/com.package.MyDatabase/1.json`). 이 파일들은 "checked into the repository to be used for validation and auto-migrations." [S2]
- `@AutoMigration`이 하나라도 있으면 `exportSchema`는 반드시 `true`. "If an auto migration is defined for a database, then androidx.room3.Database.exportSchema must be set to true" [S11], 컴파일러 오류 문구 "Cannot create auto migrations when the exportSchema annotation value is false." [S17]
- `exportSchema=true`인데 스키마 디렉터리를 컴파일러에 안 주면 컴파일 오류(`MISSING_SCHEMA_EXPORT_DIRECTORY`, 요약). [S17]
- 스키마가 바뀌면 컴파일러는 같은 버전 파일이라도 **조용히 덮어쓴다**(기존 파일과 `isSchemaEqual`이면 쓰지 않고, 다르면 씀 — 요약). 즉 "버전 올리기를 잊음"은 컴파일 오류가 아니라 git diff로만 보인다. [S17]

### Q3. 수동 Migration vs @AutoMigration
- 가이드: "Room supports both automated and manual options for incremental migration. Automatic migrations work for most basic schema changes, but you might need to manually define migration paths for more complex changes." [S4]
- 자동 마이그레이션 전제: "Automated Room migrations rely on the generated database schema for both the lower and the higher versions of the database." "If exportSchema is set to false , or if you haven't yet compiled the database with the higher version number, then automated migrations fail." [S4]
- 모호한 변경: "If Room detects ambiguous schema changes and it can't generate a migration plan without more input, it throws a compile-time error and you must provide an AutoMigrationSpec implementation." 대표 사례는 테이블 삭제·이름 변경, 컬럼 삭제·이름 변경("Deleting or renaming a table." / "Deleting or renaming a column."). 스펙 클래스에 `@DeleteTable`·`@RenameTable`·`@DeleteColumn`·`@RenameColumn`을 붙이고 `@AutoMigration(from, to, spec = ...)`로 지정. 같은 종류가 여러 개면 컨테이너(`@RenameTable.Entries`) 필요. [S4]
- `AutoMigrationSpec.onPostMigrate(connection)`으로 자동 마이그레이션 뒤 추가 작업. "The functions defined in this interface will be called on a background thread ... the functions are all in a transaction when it is called."(요약 + 원문 일부: "It is important to note that the functions are all in a transaction when it is called.") [S4][S11]
- 스펙 인스턴스를 직접 주입해야 하면 `@ProvidedAutoMigrationSpec` + `RoomDatabase.Builder.addAutoMigrationSpec(...)`. [S8][S11]
- 수동 `Migration`: 데이터를 나누는 등 Room이 판단할 수 없는 변경은 `Migration(start, end)` + `Builder.addMigrations(...)`. "If you define both an automated migration and a manual migration for the same version, then Room uses the manual migration." Caution: "use full queries instead of referencing constants that represent the queries" (원문: "To keep your migration logic functioning as expected, use full queries instead of referencing constants that represent the queries"). [S4]
- 여러 버전 건너뛰기: `Migration`은 여러 버전을 한 번에 이동할 수 있다. "If Room opens a database at version 3 and latest version is 5, Room will use the migration object that can migrate from 3 to 5 instead of 3 to 4 and 4 to 5." 경로 탐색은 큰 점프 우선으로 시작 버전에서 끝 버전까지 연쇄한다(소스 요약: `getSortedDescendingNodes`). [S11][S8][S17]
- 콜백: 마이그레이션 함수는 `suspend fun migrate(connection: SQLiteConnection)`, 마이그레이션 SQL은 `connection.executeSQL(...)`(Room 3은 `androidx.sqlite.async.executeSQL`). DB 콜백(`onCreate`·`onOpen`·`onDestructiveMigration`)도 `SQLiteConnection` 인자. [S4][S6][S20]
- 마이그레이션 실행 중 검증: 경로를 다 돌린 뒤 `onValidateSchema`로 기대 스키마와 비교하고 다르면 `"Migration didn't properly handle:"` 오류(소스). [S17]

### Q4. 파괴적 마이그레이션
- **현재 시그니처(Room 3, 3.0.0부터)**: `fallbackToDestructiveMigration(dropAllTables: Boolean = true)`, `fallbackToDestructiveMigrationFrom(dropAllTables: Boolean = true, vararg startVersions: Int)`, `fallbackToDestructiveMigrationOnDowngrade(dropAllTables: Boolean = true)`. [S8]
- **Room 2.x**: 인자 없는 `fallbackToDestructiveMigration()`·`...From(vararg)`·`...OnDowngrade()`는 "Deprecated in 2.7.0" — "This function is deprecated. Replace by overloaded version with parameter to indicate if all tables should be dropped or not." 대체는 `dropAllTables: Boolean` 인자 오버로드(2.7.0 추가, 기본값 없음). [S9]
- `dropAllTables` 설명(3.x): "Set to true if all tables should be dropped during destructive migration including those not managed by Room, otherwise only Room managed tables are dropped. Default value is true as otherwise Room could leave obsolete data when table names or existence changes between versions." [S8]
- 마이그레이션 경로가 없을 때: "If it cannot find the set of Migration s that will bring the database to the current version, it will throw an IllegalStateException ." 파괴적 옵션은 이 동작을 "re-create the database tables instead of crashing"으로 바꾼다(원문 일부). 소스의 예외 문구는 "A migration from $oldVersion to $newVersion was required but not found." [S8][S17]
- `...From`은 시작 버전을 지정. 문장: "Using this method is preferable to fallbackToDestructiveMigration if you want to allow destructive migrations from some schema versions while still taking advantage of exceptions being thrown due to unintentionally missing migrations." 그리고 `...From`에 준 버전이 `addMigrations`의 시작·끝 버전에도 있으면 예외. [S8]
- **프로덕션 사용에 대한 공식 문장**: API 문서에는 "쓰지 말라"는 문장이 없다. 가이드의 경고가 유일하다 — "Warning: Setting this option in your app's database builder means that Room permanently deletes all data from the tables in the user's database ..."(원문은 이 문장의 확인 결과 "Room permanently deletes all data from the tables in the user's database when it attempts to perform a migration and there's no defined migration path." 포함). 사용자 데이터 보존은 가이드 첫 문단이 명시: "It's important to preserve user data that is already in the on-device database when an app update changes the database schema." [S4]
- 가이드는 전면 파괴 대신 `...From`(특정 버전만)·`...OnDowngrade`(다운그레이드만)를 대안으로 안내한다. [S4]
- 파괴적 마이그레이션이 일어나면 `RoomDatabase.Callback.onDestructiveMigration(connection)`이 호출된다(소스·KMP 가이드 예시). [S17][S20]

### Q5. 마이그레이션 테스트
- 가이드: "Migrations are often complex, and an incorrectly defined migration can cause your app to crash. To preserve your app's stability, test your migrations." 아티팩트 `androidx.room3:room3-testing`(수동·자동 모두), 전제는 스키마 내보내기. [S4]
- 테스트 절차: `MigrationTestHelper`(JUnit4 `TestRule`)로 ① `createDatabase(version)`으로 옛 버전 DB 생성(DAO는 최신 스키마를 기대하므로 SQL로 데이터 삽입) → ② `runMigrationsAndValidate(version, migrations)`로 마이그레이션 실행·검증 → ③ 데이터 보존은 직접 검증. (예시 코드 주석 요약: 헬퍼가 스키마 변경은 자동 검증하지만 데이터가 제대로 옮겨졌는지는 직접 검증해야 한다) [S4]
- `runMigrationsAndValidate` 의미(KDoc): "This function uses the same algorithm that Room performs to choose migrations such that the migrations instances provided must be sufficient to bring the database from current version to the desired version." / "Note that provided manual migrations take precedence over auto migrations if they overlap in migration paths." / "Once migrations are done, this functions validates the database schema to ensure the migration performed resulted in the expected schema." 실패 시 `IllegalStateException`. 자동 마이그레이션은 `@Database`에 있으면 목록에 이미 포함되며, `@ProvidedAutoMigrationSpec`이 필요한 자동 마이그레이션은 `autoMigrationSpecs`를 넘겨야 한다. [S12]
- "전체 마이그레이션 테스트" 권고: "Although you can test a single incremental migration, you should include a test that covers all migrations defined for your app's database." 최저 버전 DB를 만들고 닫은 뒤 `Room.databaseBuilder(...).addMigrations(*ALL_MIGRATIONS).build()`로 최신 버전을 열어 검증. [S4]
- **스키마 JSON을 테스트로 연결**: 가이드의 수동 설정은 `android { sourceSets { getByName("androidTest").assets.srcDir("$projectDir/schemas") } }`(코드 주석: 플러그인을 쓰지 않는 경우). MigrationTestHelper KDoc는 플러그인을 쓰면 스키마 자산 복사도 플러그인이 구성한다고 적는다(요약). [S4][S18]
- **가이드 자체의 불일치**: Kotlin 탭은 `testImplementation("androidx.room3:room3-testing:3.0.3")`, Groovy 탭은 `androidTestImplementation`이다. 예시 테스트는 `AndroidJUnit4`·`InstrumentationRegistry`를 쓰는 계측 테스트이므로 `androidTestImplementation`이 맞아 보인다(추론 — 문서가 어느 쪽이 맞는지 밝히지 않음). [S4]
- **로컬(JVM) 실행 가능 여부**: Android용 `MigrationTestHelper`의 KDoc은 "in Instrumentation tests"용이라고 적고 생성자는 `Instrumentation`을 받는다. `Path`/`String` 스키마 디렉터리 생성자는 `jvmMain`(JVM 데스크톱 타깃)·`nativeMain` 쪽 소스에 있다(소스 확인). 즉 **Android 모듈의 로컬 JVM 단위 테스트에서 이 헬퍼를 쓰라는 공식 안내는 없다**(침묵). 데이터베이스 일반 테스트는 "We don't recommend Android local unit tests with Robolectric. Use local JVM tests using Room KMP instead."라고 적는다. [S5][S12][S18]
- 일반 DB 테스트: in-memory DB로 hermetic 하게 — "you should create an in-memory version of your database to make your tests more hermetic". JVM 테스트에는 `BundledSQLiteDriver` 권장(같은 SQLite 버전 보장, 요약). [S5]
- Room 2.7+·3.x 변화: 3.x `MigrationTestHelper`는 `SQLiteDriver`를 생성자에 받고 `createDatabase`·`runMigrationsAndValidate`가 `SQLiteConnection`을 반환하며 `suspend`이다(`runTest` 안에서 호출). 2.x의 `SupportSQLiteDatabase` 반환 API는 3.x에 없다. 2.7·2.8 노트에는 마이그레이션 테스트 전용 변경 문장을 확인하지 못했다(미확인). [S4][S12]

### Q6. 스키마 변경 안전 규칙
- 공식 가이드의 순서는 명시적 번호 목록이 아니다. 문서 구성은 ① 자동 마이그레이션(`@Database` 버전 올리고 `@AutoMigration(from,to)` 추가) → ② 수동 마이그레이션 → ③ 테스트 → ④ 경로가 없을 때 처리. "버전 올리기 → 마이그레이션 작성 → 테스트" 순서를 한 문장으로 못 박은 곳은 없다(요약). [S4]
- 마이그레이션은 트랜잭션 안에서 실행된다: "This function is already called inside a transaction and that transaction might actually be a composite transaction of all necessary Migration s." 소스: DB를 열 때 `BEGIN EXCLUSIVE TRANSACTION` 안에서 경로 전체를 실행하고 `user_version`을 갱신, 실패하면 `ROLLBACK`. [S11][S17]
- 스키마를 바꿨는데 버전을 안 올린 경우: 컴파일 오류가 아니라 **런타임** 검사다. 소스 문구: "Looks like you've changed schema but forgot to update the version number." (`checkIdentity`가 `room_master_table`의 identity hash를 비교). [S17]
- SQLite `ALTER TABLE` 한계: "The only schema altering commands directly supported by SQLite are the" rename table / rename column / add column / drop column. 나머지 변경은 새 테이블 생성 → 복사 → 기존 삭제 → 이름 변경 → 인덱스·트리거·뷰 재생성의 절차이며 "If foreign key constraints are enabled, disable them using PRAGMA foreign_keys=OFF ." 로 시작해 "If foreign key constraints were originally enabled then run PRAGMA foreign_key_check to verify that the schema change did not break any foreign key constraints." 로 끝난다(SQLite 12단계). 새 이름으로 만든 뒤 이름 변경하는 순서가 "Correct"이고 옛 테이블을 임시 이름으로 바꾸는 순서는 "Incorrect". [S16]
- **Room 문서는 외래 키·`PRAGMA foreign_keys`·`foreign_key_check`를 마이그레이션 가이드에서 언급하지 않는다(침묵).** `PRAGMA foreign_keys`는 트랜잭션 안에서 바꿀 수 없다는 SQLite 성질이 Room의 "이미 트랜잭션 안에서 호출됨"과 충돌하는지는 출처가 다루지 않는다(미확인). 소스에는 생성 코드용 `foreignKeyCheck()` 유틸이 있으나 마이그레이션 검증에서 호출되는지는 확인하지 못했다. [S4][S17]
- SQLite 문서는 Android가 번들한 SQLite가 아니라 최신 SQLite 기준이다(예: `ALTER COLUMN ... SET NOT NULL`이 문법에 있음). Android 기기별 SQLite 버전 차이는 이 문서로 확인할 수 없다. `BundledSQLiteDriver`는 "SQLite compiled from source ... the exact same version of SQLite on your host machine, Android devices, and iOS devices"(요약, 원문 근접)로 버전 불일치를 없앤다고 한다. [S5][S16]

### Q7. 다운그레이드
- `fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)`: "Allows Room to destructively recreate database tables if Migration s are not available when downgrading to old schema versions." [S8]
- 가이드: "If you want Room to fall back to destructive recreation only when migrating from a higher database version to a lower one, use fallbackToDestructiveMigrationOnDowngrade instead." [S4]
- 소스 주석: "Migrations are not required if it is a downgrade AND destructive migration during downgrade has been allowed." [S17]
- 더 큰 → 더 작은 버전 `Migration`도 경로 탐색이 받아준다(소스의 `upgrade=false` 분기: 시작 > 끝이면 내림차순 아닌 오름차순 노드 탐색). 그러나 공식 문서는 다운그레이드 마이그레이션 작성법을 설명하지 않는다(침묵). [S17]
- **앱 롤백(이전 버전 재설치) 시나리오**: Room 문서는 언급하지 않는다(침묵). OS는 낮은 `versionCode` APK 설치를 막는다는 문장이 팩 노트에 있다("The Android system uses the versionCode value to protect against downgrades by preventing users from installing an APK with a lower versionCode than the version currently ..." — S1 of `release-cd.md`를 그대로 옮긴 것으로, 이 노트에서 재확인하지 않음). 그래서 다운그레이드는 주로 백업 복원·기기 이전·개발 중 설치에서 생긴다. [S23]

### Q8. 큰 마이그레이션과 시작 시간
- Room은 메인 스레드 DB 접근을 막는다: "To prevent queries from blocking the UI, Room doesn't support database access on the main thread." API 문서: "Room ensures that Database is never accessed on the main thread because it may lock the main thread and trigger an ANR." `allowMainThreadQueries()`는 테스트용으로 안내("You may want to turn this check off for testing."). [S13][S8]
- 마이그레이션은 DB 연결을 처음 열 때 실행된다(소스: `configureDatabase` → `onMigrate`). Room 3은 이 경로가 `suspend`이며 쿼리 코루틴 컨텍스트에서 실행된다. 첫 DB 접근이 언제 일어나느냐가 곧 마이그레이션 시점이지만, 공식 문서는 "첫 접근 시점"을 문장으로 설명하지 않는다(소스로만 확인). [S17][S8]
- 오래 걸리는 마이그레이션 대처(백그라운드 선행 수행, 진행 표시, 분할 등)에 대한 공식 권고 문장은 확인하지 못했다(침묵). 관련 API로 `setQueryCoroutineContext(context)`가 있고 "If no CoroutineDispatcher is present in the context then this function will throw an IllegalArgumentException"이다. 3.1.0-alpha01에는 `setConnectionPoolTimeout(Duration)`이 추가됐다(알파). [S8][S2]
- 마이그레이션은 DB 열기의 `BEGIN EXCLUSIVE TRANSACTION` 안에서 실행되므로 마이그레이션이 끝날 때까지 다른 연결은 대기한다는 것은 소스 동작에서 나온 추론이며 문서에는 없다. [S17]

### Q9. 백업·복원과 스키마 버전
- Auto Backup은 기본적으로 `getDatabasePath(String)` 디렉터리 파일(`SQLiteOpenHelper`로 만든 파일 포함)을 포함한다: "Files in the directory returned by getDatabasePath(String)". 제어는 `android:fullBackupContent`(Android 11 이하 형식)와 `data-extraction-rules`(Android 12+, `android:dataExtractionRules`, `cloud-backup`/`device-transfer`)의 `<include>`/`<exclude domain="database" .../>`. [S14]
- 복원 시점: "Data is restored whenever the app is installed" — Play 스토어 설치, 기기 설정 마법사, `adb install`. 복원은 APK 설치 뒤 앱 실행 전에 일어난다(요약). [S14]
- **복원된 DB 파일의 스키마 버전이 설치한 앱 버전과 다를 때의 동작은 Auto Backup·백업 개요 문서 모두 침묵**한다(`restoreAnyVersion`·다운그레이드·버전 불일치 언급 없음). 그 경우는 Room의 일반 규칙(옛 버전 → 마이그레이션 경로, 더 새 버전 → 다운그레이드 처리)을 따를 뿐이라는 추론은 가능하나 문서에 없다. [S14][S15]
- `android:allowBackup="false"`를 Android 12+ 타깃에서는 일부 제조사에서 클라우드 백업만 끄고 기기 간 전송은 못 끌 수 있다는 주석이 있다(요약). [S14]

### Q10. KMP·Room 3 방향
- Room은 2.7 무렵부터 KMP 라이브러리로 리팩터링됐고, 3.0이 KMP 중심 메이저 업이다(Android, iOS, JVM, native, 3.0에서 JS/WasmJS 추가). [S1][S2]
- 3.0으로 옮기는 권장 경로는 두 단계다: "We recommend migrating in two distinct phases: first preparing and modernizing your codebase in Room 2.x, and then switching to Room 3.0." 2.x 단계에서 KAPT→KSP, DAO를 `suspend`로, `SupportSQLite`→드라이버 API, 마이그레이션·콜백을 `SQLiteConnection` 기반으로 바꾼다. 드라이버를 설정하기 전의 2.8은 "compatibility mode"(Support SQLite와 드라이버 API 병행). [S6]
- 3.0 마이그레이션의 영향(스키마 규칙에 직접): `Migration.migrate`·`AutoMigrationSpec.onPostMigrate`가 `suspend`이며 `SQLiteConnection`을 받고, `@TypeConverter` 계열은 `@ColumnTypeConverter`로 이름이 바뀐다. 스키마 JSON·`@AutoMigration` 개념은 유지된다. Android 전용 프로젝트도 Room 3.0에서는 `SQLiteDriver` 설정이 필수이며 가이드 예시는 `AndroidSQLiteDriver()`, KMP 가이드는 번들 드라이버("we recommend bundling it with your app to prevent any inconsistencies between the platform implementations of SQLite.")를 권한다. [S2][S3][S4][S20]
- Room 3은 새 패키지이므로 Room 2.x 의존 라이브러리(WorkManager 등)와 공존 가능하다고 노트가 밝힌다(요약). [S2]

### Q11. 정적 검사
- **Room 컴파일러(컴파일 시점)**: SQL 쿼리 검증("Compile-time verification of SQL queries." [S3]), 자동 마이그레이션 사전 조건 위반(스키마 파일 없음·`exportSchema=false`), 모호한 변경(스펙 필요), 스키마 디렉터리 미제공 등을 오류로 낸다. 반면 **엔티티를 바꾸고 `version`을 안 올린 경우는 컴파일 오류가 아니다** — 스키마 JSON이 덮어써져 diff에만 나타나고, 런타임에 identity hash 불일치로 `IllegalStateException`. [S3][S17]
- Android Lint에 Room 스키마·버전 관련 기본 검사가 있는지는 확인하지 못했다(미확인).
- **Konsist**: `KoAnnotationProvider`(`hasAnnotationOf`, `annotations` 등)와 `KoArgumentProvider`를 구현한 `KoAnnotationDeclaration`이 있어 `@Database`의 인자를 읽는 규칙을 쓸 수 있는 것으로 보인다(소스의 인터페이스 선언 기준). 문서(Verify Classes)는 어노테이션 "required attribute values" 검증이 가능하다고 적는다(요약). 실제 `exportSchema = false` 검출 코드는 이 조사에서 실행해 보지 않았다. detekt에 이 목적의 기본 규칙은 확인하지 못했다. [S22]
- 검증 게이트로는 (a) 스키마 JSON 변경 커밋에 대응하는 `version` 증가와 Migration 존재를 확인하는 CI 스크립트(diff 기반), (b) 마이그레이션 계측 테스트가 현실적이다 — 후자는 S4·S12 근거, 전자는 출처가 침묵하는 팩 자체 설계다.

## 후보 비교

### 수동 `Migration` vs `@AutoMigration`
| 항목 | 수동 `Migration` | `@AutoMigration` |
|---|---|---|
| 적합 범위 | 복잡한 변경(테이블 분할 등 Room이 판단 못 하는 것) [S4] | "most basic schema changes"(컬럼 추가 등 단순 변경) [S4] |
| 전제 | 없음(스키마 JSON은 테스트에 필요) [S4] | 낮은·높은 버전 스키마 JSON 둘 다 필요, `exportSchema=true` [S4][S11] |
| 이름 변경·삭제 | SQL로 직접 작성 | 컴파일 오류 → `AutoMigrationSpec` + `@Rename*`/`@Delete*` 필요 [S4] |
| 데이터 이동 | SQL에서 자유 | `onPostMigrate(connection)`에서 추가 SQL [S4][S11] |
| 등록 위치 | `Builder.addMigrations(...)` [S4] | `@Database(autoMigrations=[...])`, 주입형 스펙은 `addAutoMigrationSpec` [S4][S8] |
| 같은 버전에 둘 다 있으면 | 수동이 우선 [S4][S12] | 수동에 밀림 |
| 트랜잭션·콜백 | 이미 트랜잭션 안, 전체 경로가 한 트랜잭션(소스) [S11][S17] | `onPostMigrate`도 트랜잭션 안 [S11] |
| 테스트 | `runMigrationsAndValidate` [S12] | 동일(목록에 자동 포함, 주입형은 `autoMigrationSpecs` 전달) [S12] |

### 파괴적 마이그레이션 옵션별 동작 (Room 3.0.3 기준)
| 옵션 | 언제 데이터를 지우나 | 비고 |
|---|---|---|
| (설정 없음) | 지우지 않는다. 경로 없으면 `IllegalStateException` [S8] | 기본값 |
| `fallbackToDestructiveMigration(dropAllTables=true)` | 경로가 없는 **모든** 업·다운 이동 | 가이드가 데이터 영구 삭제 경고 [S4]. `dropAllTables=true`가 기본, 이유는 오래된 데이터 잔존 방지 [S8] |
| `fallbackToDestructiveMigrationFrom(dropAllTables, vararg 버전)` | 지정한 시작 버전에서 이동할 때만 | 그 외 누락은 예외로 계속 드러남(문서 권장). 같은 버전에 `Migration`이 있으면 예외 [S8] |
| `fallbackToDestructiveMigrationOnDowngrade(dropAllTables)` | 더 큰 → 더 작은 버전으로 갈 때 경로가 없을 때만 | 업그레이드 누락은 여전히 예외 [S4][S8][S17] |
| 2.x 인자 없는 오버로드 | 위와 같음 | Room 2.7.0부터 deprecated, `dropAllTables` 인자형으로 대체 [S9] |

### 마이그레이션 테스트 방식
| 방식 | 실행 위치 | 근거·상태 |
|---|---|---|
| `MigrationTestHelper` + 단일 마이그레이션 | 계측 테스트(에뮬레이터·기기) | 공식 예시 `AndroidJUnit4`, `createDatabase`→`runMigrationsAndValidate` [S4][S12] |
| `Room.databaseBuilder().addMigrations(*ALL).build()`로 최저→최신 전체 검증 | 계측 테스트 | 공식 권고(전체 마이그레이션 테스트) [S4] |
| 로컬 JVM 단위 테스트(Android 모듈) | JVM | `MigrationTestHelper` Android판은 Instrumentation 전용, 공식 안내 없음 → 침묵 [S12][S18]. JVM 경로 생성자는 KMP `jvmMain` 소스에 존재 [S18] |
| 스키마 JSON 연결 | — | 플러그인 사용 시 자동(요약), 수동이면 `androidTest.assets.srcDir(스키마 폴더)` [S4][S18] |
| 일반 DAO 테스트(참고) | JVM 또는 계측 | in-memory + `BundledSQLiteDriver`, Robolectric 비권장 [S5] |

## 규칙 후보
출처가 뒷받침하는 것은 근거 S번호를 달았다. "팩 결정 필요"는 출처가 침묵하거나 선택지가 갈려 팩이 골라야 하는 항목이다.

1. Room 버전 선택: Room 3.x(`androidx.room3`, 현재 안정 3.0.3)를 기본으로 하고 2.x는 쓰지 않는다 — 근거: 2.x 가이드가 "deprecated"로 표기 [S7], 3.0 정식 출시 [S2]. 단 **적용 여부는 팩 결정 필요**(아래 결정 1).
2. Room 컴파일러는 KSP만 쓴다(kapt·annotationProcessor 금지) — Room 3은 KSP 필수 [S2][S6]. (팩의 kapt 금지와 일치)
3. 스키마 디렉터리는 Room Gradle 플러그인(`androidx.room3`)의 `room3 { schemaDirectory(...) }`로 정하고 `@Database(exportSchema = true)`를 유지한다 — [S2][S10]
4. 스키마 JSON은 버전 관리에 커밋하고 앱에는 넣지 않는다 — [S10][S4]
5. 스키마를 바꾸면 같은 변경에서 `@Database.version`을 올린다. 누락은 컴파일이 잡지 못하고 런타임 `IllegalStateException`이라 CI 검사가 필요하다 — [S17], 검사 방법은 **팩 결정 필요**.
6. 단순 변경(컬럼 추가 등)은 `@AutoMigration`, Room이 판단 못 하는 변경(분할·데이터 변환)은 수동 `Migration`. 이름 변경·삭제는 `AutoMigrationSpec` + `@Rename*`/`@Delete*` — [S4]
7. 릴리스된 버전 사이의 모든 마이그레이션 경로를 유지한다(경로가 없으면 사용자 앱이 예외로 죽는다) — [S8][S4]. "사용자가 최소 몇 버전 전부터 올라올 수 있는가"의 지원 범위는 **팩 결정 필요**.
7-1. 수동 마이그레이션 SQL에는 상수 참조 대신 전체 쿼리 문자열을 쓴다 — [S4]
8. 마이그레이션마다 `MigrationTestHelper` 테스트를 쓰고 스키마 검증과 데이터 보존을 둘 다 확인한다. 최저 버전에서 최신까지 전체 경로 테스트도 둔다 — [S4][S12]
9. 마이그레이션 테스트는 계측 테스트(`androidTest`)에서 돌린다 — [S12][S18]. CI에서 에뮬레이터를 돌릴지는 **팩 결정 필요**(`research/testing-ci.md`와 연결).
10. 프로덕션 빌드에서 인자 없는 전면 `fallbackToDestructiveMigration`을 쓰지 않는다. 필요하면 `...From`(특정 버전) 또는 `...OnDowngrade`로 범위를 좁힌다 — 근거: 가이드의 데이터 영구 삭제 경고와 좁은 대안 안내 [S4], `...From`의 "unintentionally missing migrations" 예외 유지 [S8]. 다만 이 "금지" 문장의 강도는 공식 문서에 없다 → **팩 결정 필요**(아래 결정 2).
11. 파괴적 옵션을 쓸 때는 `dropAllTables`를 기본값(`true`)에서 내리지 않는다 — [S8]
12. 마이그레이션 SQL이 테이블 재생성을 요구하면(`ALTER TABLE`로 못 하는 변경) SQLite 12단계 절차(새 이름으로 생성 → 복사 → 삭제 → 이름 변경 → 인덱스 재생성 → `foreign_key_check`)를 따른다 — [S16]. Room 쪽 근거는 침묵이라 규칙화 여부는 **팩 결정 필요**.
13. DB 접근은 메인 스레드에서 하지 않는다(`allowMainThreadQueries()`는 테스트에서만) — [S13][S8]
14. Room 3은 `SQLiteDriver` 설정이 필수다. 드라이버는 하나로 고정한다(번들 vs 프레임워크는 **팩 결정 필요**) — [S2][S20]
15. 자동 백업이 DB 파일을 복원하는 경우와 앱 버전 불일치는 문서가 침묵한다. 백업 대상 포함/제외는 `dataExtractionRules`·`fullBackupContent`의 `domain="database"`로 명시한다 — [S14]. 포함/제외 정책은 **팩 결정 필요**.

## 출처가 침묵하는 것
- AGP 9.4·KSP 2.3.x·Kotlin 2.4 조합에서 Room 3 컴파일러·Gradle 플러그인 동작(Room 릴리스 노트에 호환 문장 없음). Room 3 플러그인의 최소 AGP도 미기재. [S2]
- 스키마를 바꾸고 `version`을 올리지 않은 경우를 컴파일 시점에 막는 공식 검사. 소스는 스키마 JSON을 덮어쓰고 런타임에 identity hash로 잡는다. [S17]
- 여러 버전에 걸친 마이그레이션 체인의 "몇 버전까지 유지하라"는 권고. 경로 탐색 동작만 문서화. [S8][S11]
- 다운그레이드용 `Migration` 작성 가이드와 앱 롤백 시나리오. 공식 언급은 `fallbackToDestructiveMigrationOnDowngrade` 한 줄. [S8][S4]
- 외래 키(`PRAGMA foreign_keys`, `foreign_key_check`)를 Room 마이그레이션에서 어떻게 다루라는 문장. SQLite 문서는 12단계 절차에서 다룬다. [S4][S16]
- 오래 걸리는 마이그레이션을 다루는 방법(진행 표시, 시작 시간 영향). DB는 첫 접근에서 열리고 마이그레이션이 그때 실행된다는 사실은 소스 확인뿐. [S17]
- Auto Backup 복원본과 앱 스키마 버전의 불일치 동작. [S14][S15]
- Android 모듈에서 로컬 JVM 마이그레이션 테스트를 쓰는 공식 방법. [S12][S18]
- `@Database(exportSchema = true)`를 강제하는 Android Lint·detekt 기본 규칙.
- 프로덕션에서 파괴적 마이그레이션을 "쓰지 말라"는 API 문서의 직접 문장(가이드는 경고만 함). [S8][S4]

## 미확인
- **Room 2.x 마이그레이션 가이드 원문**: `https://developer.android.com/training/data-storage/room/v2/migrating-db-versions`는 404 페이지였다. 현재 `/room/migrating-db-versions`는 Room 3 내용으로 교체돼 있다. 2.x 시절 문장과의 비교(2.7+에서 마이그레이션 테스트가 어떻게 바뀌었는지)는 못 했다.
- **Room 2.7·2.8 릴리스 노트의 마이그레이션 테스트 관련 변경 문장**: 2.8.0 요약과 2.8.0-rc02에서 확인 못 함(2.8.0-rc02에 "destructive migration ... pre-packaged database" 버그 수정 문장이 있으나 테스트 도구 변경은 아님).
- **Room 3 최소 minSdk**: 릴리스 노트에 없음(2.8.0은 API 23으로 올림 [S1]). 팩 minSdk 26에는 영향 없어 보이나 확인은 못 했다.
- **마이그레이션 검증에서 `foreign_key_check` 호출 여부**: `DBUtil.foreignKeyCheck()`는 소스에 있으나 호출 지점(생성 코드)을 열지 않았다.
- **Konsist로 `@Database` 인자 읽기**: 인터페이스 선언은 확인했으나 실제 규칙을 실행해 보지 않았다.
- **Android Lint의 Room 스키마 관련 검사** 존재 여부.
- **`MigrationTestHelper`가 계측 테스트에서 스키마 자산 복사를 플러그인이 대신 구성하는지의 정확한 조건**: KDoc 문장은 KDoc 줄 머리표(` * `) 때문에 글자 단위 재확인이 안 돼 요약으로만 적었다. [S18]
- 가이드 Kotlin 탭의 `testImplementation`이 오타인지(문서 불일치)는 문서가 밝히지 않는다.
- 인터넷 페이지 안에 에이전트에게 무언가를 하라고 지시하는 문구는 발견하지 못했다.

## 사용자 결정 항목(요약)
1. Room 2.8.5(deprecated) vs Room 3.0.3(권장, 단 코루틴·드라이버 필수, 3.0 GA 3개월).
2. 파괴적 마이그레이션 정책(전면 금지 / `...From`·`...OnDowngrade`만 허용 / 데이터 유실 허용 앱은 예외).
3. 마이그레이션 경로 유지 범위(출시된 모든 버전 / 최소 지원 버전).
4. `@AutoMigration` 우선 vs 수동 우선.
5. 마이그레이션 테스트 실행 방식(계측 테스트 필수화, CI 에뮬레이터 여부).
6. 스키마·버전 불일치를 CI에서 검사할지 및 방법(스키마 JSON diff 기반 스크립트, Konsist).
7. SQLite 드라이버(`BundledSQLiteDriver` vs `AndroidSQLiteDriver`).
8. 백업 정책(DB 포함/제외) 및 다운그레이드·복원 시나리오 대응.
9. 테이블 재생성·외래 키 절차를 규칙에 넣을지.
