# 27 Room 스키마 변경

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

Room을 어디에 두는지는 R-15-14, 저장 매체 선택은 R-15-08, in-memory DB 통합 테스트는 R-15-12가 정한다. 이 문서는 Room 데이터베이스의 스키마를 바꿀 때 지킬 것 — 버전, 마이그레이션 작성 방식, 파괴적 마이그레이션, 마이그레이션 테스트 — 만 정한다. 조사 노트는 `research/room-migrations.md`다.

확정 결정 `ROOM_VERSION`(2026-09-30): **Room 3.0.3(`androidx.room3`) 기준**이다. 조사 노트가 확인한 근거는 3.0이 2026-07-01 정식 출시됐고 공식 2.x 가이드가 "deprecated"로 표기한다는 것이다. AGP 9.4·KSP 2.3·Kotlin 2.4와 Room 3의 호환을 밝힌 릴리스 노트 문장은 없고, 이 조합은 아직 실행으로 확인하지 않았다. 아래 코드는 Room 3 API 이름을 노트에서 확인한 범위까지만 쓴다.

확정 결정 `DESTRUCTIVE_MIGRATION`(2026-09-30): 릴리스 빌드에서는 `fallbackToDestructiveMigration*`을 쓰지 않는다. 확정 결정 `MIGRATION_TEST`(팩 기본값, 사용자 승인): 스키마를 바꾼 변경은 계측 마이그레이션 테스트를 통과해야 한다.

## 결정 매트릭스 — 마이그레이션 작성 방식 (R-27-06)

| 변경 종류 | `@AutoMigration` | `@AutoMigration` + `AutoMigrationSpec` | 수동 `Migration` | 기본값 |
|---|---|---|---|---|
| 컬럼·테이블 추가 등 단순 변경 | 가능 | — | 가능 | `@AutoMigration` |
| 테이블·컬럼 이름 변경·삭제 | 컴파일 오류 | 가능(`@RenameTable`·`@DeleteTable`·`@RenameColumn`·`@DeleteColumn`) | 가능 | `AutoMigrationSpec` |
| 데이터 분할·변환처럼 Room이 판단할 수 없는 변경 | 불가 | `onPostMigrate`의 추가 SQL로만 | 가능 | 수동 `Migration` |
| 같은 버전 구간에 둘 다 정의 | 수동에 밀린다 | 수동에 밀린다 | 수동이 우선 | — |

## 결정 매트릭스 — SQLiteDriver (기본값 없음)

Room 3은 데이터베이스를 만들 때 `SQLiteDriver`가 필수다(R-27-02). 어느 드라이버를 쓸지는 출처가 선택지만 주므로 팩은 기본값을 두지 않고, 새 프로젝트가 정해 프로젝트 지침 파일에 적는다(R-00-06).

| 판단 기준 | 선택지 A: 플랫폼(프레임워크) 드라이버 | 선택지 B: 번들 SQLite 드라이버 |
|---|---|---|
| 조사 노트가 확인한 출처 문장 | 공식 설정 가이드의 예시가 `AndroidSQLiteDriver()`를 쓴다 | KMP 가이드는 플랫폼 간 SQLite 불일치를 막으려고 앱에 번들하라고 권한다 |
| 기기별 SQLite 버전 | 기기 제공 버전에 따른다 | 호스트·기기·iOS에서 같은 버전(요약) |
| 팩 기본값 | 없음 | 없음 |

## 규칙

### R-27-01 Room은 3.x(`androidx.room3`)만 쓰고 2.x 의존성을 새로 넣지 않는다
- 규칙: 좌표는 `androidx.room3:room3-runtime`, 컴파일러는 `androidx.room3:room3-compiler`(KSP), 테스트는 `androidx.room3:room3-testing`이고 버전은 카탈로그 한 곳에서 관리한다(R-10-12). `androidx.room:*` 의존성과 `androidx.room.*` import를 그린필드 코드에 두지 않는다. 3.x는 KSP만 지원하므로 kapt·`annotationProcessor`를 쓰지 않는다.
- 근거: Room 3.0은 새 패키지·새 좌표이고 "Kotlin Symbol Processing (KSP) is required." 라고 릴리스 노트가 적는다 [S252](https://developer.android.com/jetpack/androidx/releases/room3). 2.x 가이드 상단은 "Caution: This guide covers Room 2.x, which is deprecated." 이다 [S266](https://developer.android.com/training/data-storage/room/v2). 3.x를 고른 것은 팩 결정 `ROOM_VERSION`이다. 기존 R-15-08·R-15-12·R-15-14는 "Room"이라고만 적어 이 규칙과 충돌하지 않는다.
- 예시:
  ```kotlin
  // Good — gradle/libs.versions.toml
  androidx-room3-runtime = { group = "androidx.room3", name = "room3-runtime", version.ref = "room3" }
  androidx-room3-compiler = { group = "androidx.room3", name = "room3-compiler", version.ref = "room3" }
  // Bad: 2.x 좌표
  implementation("androidx.room:room-runtime:2.8.5")
  ```
- 체크: `androidx.room:` 좌표나 `androidx.room.` import가 있는가. Room 컴파일러가 `ksp` 외의 구성(`kapt` 등)으로 선언됐는가.

### R-27-02 Room 3 최소 구성은 KSP·Room 플러그인·`schemaDirectory`·`SQLiteDriver` 넷이다
- 규칙: 데이터베이스 모듈은 KSP 플러그인과 Room Gradle 플러그인(`androidx.room3`)을 적용하고 `room3 { schemaDirectory("$projectDir/schemas") }`를 선언한다. 플러그인을 쓰면 KSP 인자 `room.schemaLocation`을 따로 넘기지 않는다. `RoomDatabase.Builder`에는 `setDriver(...)`로 드라이버를 반드시 지정한다(선택은 위 드라이버 매트릭스). DAO 함수는 리액티브 타입(`Flow`)이 아니면 `suspend`로 선언한다(R-15-04).
- 근거: 플러그인 id는 `androidx.room3`, 확장은 `room3 { }`이고 "Setting a schemaDirectory is required when using the Room Gradle Plugin." 이다 [S252](https://developer.android.com/jetpack/androidx/releases/room3). 릴리스 노트는 "A SQLiteDriver is now required to build a RoomDatabase"라고 적는다. 플러그인과 명시적 `room.schemaLocation`을 함께 쓰면 컴파일러가 오류를 낸다는 것은 소스 확인이다 [S265](https://github.com/androidx/androidx/tree/androidx-main/room3). `MigrationTestHelper` KDoc 예시는 아직 `androidx.room`·`room { }`로 적혀 있어 릴리스 노트와 컴파일러 문구를 따랐다 [S264](https://developer.android.com/reference/kotlin/androidx/room3/testing/MigrationTestHelper). AGP 9.4·KSP 2.3.11·Kotlin 2.4.20에서 이 구성이 빌드되는지는 아직 실행으로 확인하지 않았다.
- 예시:
  ```kotlin
  // Good — :core:database/build.gradle.kts
  plugins { alias(libs.plugins.ksp); alias(libs.plugins.androidx.room3) }
  room3 { schemaDirectory("$projectDir/schemas") }
  dependencies { ksp(libs.androidx.room3.compiler) }
  // Good — 빌더
  Room.databaseBuilder(...).setDriver(/* 프로젝트가 고른 드라이버 */).build()
  // Bad: 플러그인 + 명시적 KSP 인자를 동시에 사용
  ksp { arg("room.schemaLocation", "$projectDir/schemas") }
  ```
- 체크: 빌더에 `setDriver`가 있는가. 플러그인과 `room.schemaLocation`이 함께 선언됐는가. `schemaDirectory`가 선언됐는가.

### R-27-03 Room 의존성·`@Database`·마이그레이션·스키마 JSON은 `:core:database` 한 모듈에 둔다
- 규칙: `@Database`, DAO, 마이그레이션 객체, `schemas/` 디렉터리, 마이그레이션 테스트는 모두 `:core:database`(R-15-14) 안에 둔다. 다른 모듈은 Room 플러그인·Room 컴파일러·`room3-*` 의존성을 선언하지 않는다. Hilt 같은 다른 KSP 프로세서는 이 제한의 대상이 아니다. 스키마 변경 규칙의 소유자는 이 모듈이다.
- 근거: Room 3 릴리스 노트는 "It is recommended to have a multi-module project where Room usage is concentrated" 라고 적고 Room 사용을 한 모듈에 모아 그 모듈에만 KSP를 적용하라고 안내한다(요약) [S252](https://developer.android.com/jetpack/androidx/releases/room3). 모듈 배치 자체는 R-15-14가 정하고, 스키마 JSON·마이그레이션·테스트를 같은 모듈에 두는 것은 팩 결정이다.
- 예시:
  ```text
  Good: :core:database/schemas/<DB 클래스 전체 이름>/1.json · 2.json
        :core:database/src/androidTest/.../MigrationTest.kt
  Bad:  :feature:news 가 room3-runtime 과 ksp(room3-compiler)를 선언한다
  ```
- 체크: `:core:database` 밖 빌드 스크립트에 `room3`·`androidx.room3`가 있는가. 스키마 JSON이 다른 모듈에 있는가.

### R-27-04 `exportSchema`는 `true`로 두고 스키마 JSON은 커밋하되 앱에는 넣지 않는다
- 규칙: 모든 `@Database`에 `exportSchema = true`(기본값)를 유지하고 생성된 `schemas/**/*.json`을 저장소에 커밋한다. 스키마 JSON을 앱의 assets·resources에 포함하지 않는다. `@AutoMigration`을 하나라도 쓰면 `exportSchema`는 반드시 `true`다.
- 근거: `@Database` 문서는 "If you do export schemas then you should commit the schema files into your version control system (but don't ship them with your app!)." 라고 적는다 [S262](https://developer.android.com/reference/kotlin/androidx/room3/Database). 가이드는 "Store these files in your version control system so that you can recreate lower versions of the database for testing and support automated migration generation." 이고 [S260](https://developer.android.com/training/data-storage/room/migrating-db-versions), 자동 마이그레이션에는 "If an auto migration is defined for a database, then androidx.room3.Database.exportSchema must be set to true" 조건이 있다 [S267](https://developer.android.com/reference/kotlin/androidx/room3/AutoMigration). 끄는 경우로 문서는 in-memory 전용 DB를 예로 든다(요약).
- 예시:
  ```kotlin
  // Good
  @Database(entities = [SessionEntity::class], version = 2, exportSchema = true)
  // Bad
  @Database(entities = [SessionEntity::class], version = 2, exportSchema = false)
  ```
- 체크: `exportSchema = false`인 `@Database`가 있는가(in-memory 전용이면 사유가 적혀 있는가). `schemas/`가 `.gitignore`에 들어 있거나 앱 소스셋 assets에 연결됐는가.

### R-27-05 스키마 JSON이 바뀐 변경은 `@Database.version`도 올린다
- 규칙: 엔티티·인덱스·뷰를 바꿔 `schemas/**/*.json`의 내용이 달라지는 변경은 같은 변경에서 `@Database.version`을 올리고 새 버전의 JSON을 함께 커밋한다. 이미 출시된 버전의 JSON은 수정하지 않는다. 게이트(`scripts/check.sh`)가 빌드 뒤에 부르는 `scripts/check-room-schema.sh`가 이 규칙을 검사한다 — KSP가 스키마 JSON을 만든 뒤의 작업 트리에서 upstream 대비 기존 JSON의 수정·삭제·이름 변경, 커밋하지 않은 새 JSON, 가장 큰 스키마 번호와 `@Database` `version`의 불일치를 잡는다.
- 근거: 버전 누락은 컴파일 오류가 아니다. 컴파일러는 스키마가 바뀌면 같은 버전 파일을 조용히 덮어쓰고, 런타임에 identity hash가 다르면 스키마를 바꾸고 버전을 올리지 않았다는 취지의 오류가 난다(오류 문구는 Room 소스에서만 확인했고 문서에는 없다) [S265](https://github.com/androidx/androidx/tree/androidx-main/room3). 공식 문서에는 이를 컴파일 시점에 막는 검사가 없다. 규칙으로 쓰고 스크립트로 잡는 것은 팩 결정이다. 그 검사 스크립트는 가짜 저장소에서 열세 경우(정상, 빌드가 덮어쓴 JSON 미커밋·커밋, 정상 증가, 새 JSON 미커밋, JSON만 추가, 삭제·이름 변경·전체 삭제, 스키마 없음, 같은 이름의 DB 둘, upstream 없음)로 확인했고 실제 Room 3 프로젝트로는 아직 돌려 보지 않았다.
- 예시:
  ```text
  # Good — 한 커밋
  SessionEntity.kt        열 추가
  SessionDatabase.kt      version = 2
  schemas/.../2.json      새로 생성됨
  # Bad — 열만 추가하고 version = 1 유지: 1.json 이 덮어써지고 기기에서 IllegalStateException
  ```
- 체크: 기존 버전 JSON이 수정된 diff가 있는가(신규 파일만 허용). 스키마 JSON이 바뀐 커밋에 `version` 증가가 있는가.

### R-27-06 단순 변경은 `@AutoMigration`, 이름 변경·삭제는 `AutoMigrationSpec`, 나머지는 수동 `Migration`으로 쓴다
- 규칙: 위 매트릭스 순서로 고른다. 자동 마이그레이션은 `@Database(autoMigrations = [...])`에 등록하고, 이름 변경·삭제는 스펙 클래스에 `@RenameTable`·`@DeleteTable`·`@RenameColumn`·`@DeleteColumn`을 붙여 `spec =`으로 지정한다. Room이 판단할 수 없는 변경(데이터 분할·변환)만 `Migration(start, end)`을 손으로 쓰고 `Builder.addMigrations(...)`로 등록한다. 같은 구간에 자동과 수동이 함께 있으면 수동이 쓰이므로, 수동으로 바꿀 때는 자동 정의를 지운다.
- 근거: 가이드는 "Automatic migrations work for most basic schema changes, but you might need to manually define migration paths for more complex changes." 이고 모호한 변경은 "it throws a compile-time error and you must provide an AutoMigrationSpec implementation." 이며 "If you define both an automated migration and a manual migration for the same version, then Room uses the manual migration." 이다 [S260](https://developer.android.com/training/data-storage/room/migrating-db-versions). 자동 마이그레이션을 먼저 쓰는 것은 팩 결정이다.
- 예시:
  ```kotlin
  // Good — 열 추가는 자동, 이름 변경은 스펙
  @Database(
      entities = [SessionEntity::class], version = 3,
      autoMigrations = [
          AutoMigration(from = 1, to = 2),
          AutoMigration(from = 2, to = 3, spec = Renamed2To3::class),
      ],
  )
  abstract class SessionDatabase : RoomDatabase() { /* DAO 선언 생략 */ }

  @RenameColumn(tableName = "session", fromColumnName = "label", toColumnName = "title")
  class Renamed2To3 : AutoMigrationSpec
  // Bad: 열 추가 하나에 수동 Migration 을 쓰고, 같은 구간의 @AutoMigration 도 남겨 둔다
  ```
- 체크: `ALTER TABLE ... ADD COLUMN`만 하는 수동 `Migration`이 있는가. 같은 `from`·`to`의 자동·수동 정의가 함께 있는가.

### R-27-07 출시한 모든 버전에서 현재 버전까지 마이그레이션 경로를 유지한다
- 규칙: 스토어에 출시한 모든 `@Database.version`에서 현재 버전까지 이어지는 경로(자동·수동 어느 쪽이든)를 삭제하지 않고 유지한다. 여러 버전을 한 번에 건너뛰는 `Migration(3, 5)`를 둘 수 있고, 있으면 Room은 3→4→5보다 그것을 쓴다. 경로를 지우려면 사용자에게 그 버전에서 올라오는 경우가 없다는 근거를 커밋 메시지에 적는다.
- 근거: 경로가 없으면 Room은 "If it cannot find the set of Migration s that will bring the database to the current version, it will throw an IllegalStateException ." 라고 API 문서가 적는다 [S261](https://developer.android.com/reference/kotlin/androidx/room3/RoomDatabase.Builder). `Migration` 문서는 "If Room opens a database at version 3 and latest version is 5, Room will use the migration object that can migrate from 3 to 5 instead of 3 to 4 and 4 to 5." 라고 한다 [S263](https://developer.android.com/reference/kotlin/androidx/room3/migration/Migration). 몇 버전 전까지 유지하라는 출처는 없고, 출시한 모든 버전을 유지하는 것은 팩 결정이다.
- 예시:
  ```kotlin
  // Good — v1 출시분 사용자도 v4 로 올라올 수 있다
  addMigrations(Migration1To2, Migration2To4)   // + @Database autoMigrations 의 3→4
  // Bad: v1 → v2 정의를 "오래됐다"며 지운다 → v1 사용자의 앱 시작이 예외로 멈춘다
  ```
- 체크: 출시한 각 버전에서 현재 버전까지의 경로가 전부 있는가(R-27-12의 전체 경로 테스트가 통과하는가). 경로를 지운 변경에 근거가 적혀 있는가.

### R-27-08 수동 마이그레이션 SQL은 상수 참조 없이 전체 쿼리 문자열로 쓴다
- 규칙: `Migration.migrate` 안의 SQL은 문자열 리터럴로 전부 적고, 엔티티의 테이블·컬럼 이름 상수나 쿼리 상수를 참조해 조립하지 않는다.
- 근거: 가이드는 "To keep your migration logic functioning as expected, use full queries instead of referencing constants that represent the queries" 라고 적는다 [S260](https://developer.android.com/training/data-storage/room/migrating-db-versions). 상수가 나중에 바뀌면 이미 출시한 버전의 마이그레이션 의미가 함께 바뀌기 때문이라는 이유 설명은 출처가 하지 않아 규칙 근거로 쓰지 않았다.
- 예시:
  ```kotlin
  // Good
  object Migration1To2 : Migration(1, 2) {
      override suspend fun migrate(connection: SQLiteConnection) {
          connection.executeSQL("ALTER TABLE session ADD COLUMN title TEXT NOT NULL DEFAULT ''")
      }
  }
  // Bad
  connection.executeSQL("ALTER TABLE ${SessionEntity.TABLE} ADD COLUMN $COLUMN_DEF")
  ```
- 체크: `migrate` 안에 문자열 템플릿(`$`)이나 상수 참조로 만든 SQL이 있는가.

### R-27-09 `Migration.migrate`는 SQL만 실행하고 트랜잭션을 직접 열지 않는다
- 규칙: `migrate`·`onPostMigrate` 본문에서 `BEGIN`·`COMMIT`·`ROLLBACK`이나 트랜잭션 헬퍼를 호출하지 않는다. 마이그레이션은 DB를 처음 열 때 실행되므로(첫 DB 접근 시점) 실패는 예외로 끝나게 두고 `try/catch`로 삼켜 부분 적용된 상태를 남기지 않는다.
- 근거: `Migration.migrate` KDoc은 "This function is already called inside a transaction and that transaction might actually be a composite transaction of all necessary Migration s." 라고 적고 `AutoMigrationSpec` 함수도 트랜잭션 안에서 호출된다 [S263](https://developer.android.com/reference/kotlin/androidx/room3/migration/Migration). 소스 확인으로는 DB를 열 때 `BEGIN EXCLUSIVE TRANSACTION` 안에서 경로 전체를 실행하고 실패하면 `ROLLBACK`한다 — 공식 문서 문장이 아니고 소스로만 확인했다 [S265](https://github.com/androidx/androidx/tree/androidx-main/room3). 안에서 트랜잭션을 또 열면 어떻게 되는지와 `try/catch`가 롤백을 막는지는 아직 실행으로 확인하지 않았다. "직접 열지 않는다"는 위 사실에서 끌어낸 팩 결정이다.
- 예시:
  ```kotlin
  // Good — SQL 만 실행, 실패하면 예외가 전파돼 전체가 롤백된다
  override suspend fun migrate(connection: SQLiteConnection) {
      connection.executeSQL("ALTER TABLE session ADD COLUMN title TEXT NOT NULL DEFAULT ''")
  }
  // Bad
  connection.executeSQL("BEGIN")
  try { /* ... */ } catch (e: Exception) { /* 삼킨다 */ }
  ```
- 체크: `migrate`·`onPostMigrate`에 `BEGIN`·`COMMIT`·`ROLLBACK` 문자열이나 트랜잭션 호출이 있는가. 그 안에 예외를 삼키는 `catch`가 있는가.

### R-27-10 릴리스 빌드에서는 파괴적 마이그레이션을 쓰지 않는다
- 규칙: 릴리스 빌드에서 `fallbackToDestructiveMigration`·`fallbackToDestructiveMigrationFrom`·`fallbackToDestructiveMigrationOnDowngrade`가 활성화되지 않게 한다. 경로가 없으면 예외로 멈추게 두고, 누락은 마이그레이션 테스트(R-27-11·R-27-12)로 잡는다. 허용은 debug 빌드에 한정하며, `:core:database`는 `BuildConfig`를 읽지 않고 `:app`이 넘긴 플래그를 생성자로 받는다(R-19-14). 허용할 때도 `dropAllTables`를 기본값(`true`)에서 내리지 않는다.
- 근거: 공식 문서에 "프로덕션에서 쓰지 말라"는 문장은 없고, 가이드의 경고가 있을 뿐이다 — "Room permanently deletes all data from the tables in the user's database when it attempts to perform a migration and there's no defined migration path." [S260](https://developer.android.com/training/data-storage/room/migrating-db-versions). 가이드 첫 문단은 "It's important to preserve user data that is already in the on-device database when an app update changes the database schema." 이다. `dropAllTables`의 기본값 `true`는 "otherwise Room could leave obsolete data when table names or existence changes between versions." 때문이다 [S261](https://developer.android.com/reference/kotlin/androidx/room3/RoomDatabase.Builder). **이 규칙의 강도는 출처가 아니라 팩 결정 `DESTRUCTIVE_MIGRATION`이다.**
- 예시:
  ```kotlin
  // Good — :core:database, :app 이 debug 에서만 true 를 넘긴다
  fun buildDatabase(context: Context, allowDestructive: Boolean): SessionDatabase =
      Room.databaseBuilder(...).setDriver(/* ... */)
          .apply { if (allowDestructive) fallbackToDestructiveMigration() }
          .build()
  // Bad: 릴리스에서도 켜져 경로 누락이 사용자 데이터 삭제로 조용히 넘어간다
  Room.databaseBuilder(...).fallbackToDestructiveMigration().build()
  ```
- 체크: 릴리스 빌드에서 `fallbackToDestructiveMigration*` 호출이 활성화될 수 있는가. `dropAllTables = false`가 있는가.

### R-27-11 스키마를 바꾼 변경은 계측 마이그레이션 테스트를 통과해야 한다
- 규칙: `@Database.version`을 올린 변경마다 `:core:database`의 `src/androidTest`에 `MigrationTestHelper` 테스트를 추가한다. 이전 버전 DB를 `createDatabase(version)`으로 만들고 SQL로 데이터를 넣은 뒤 `runMigrationsAndValidate(version, migrations)`를 실행하고, 스키마 검증과 별개로 데이터가 옮겨졌는지 직접 확인한다. 실행은 `scripts/instrumented.sh`(로컬 기기·에뮬레이터, R-31-18)다. `room3-testing`은 `androidTestImplementation`으로 선언한다. 이 테스트는 스키마 검증이라 R-30-12의 "대표 플로우 1~2개" 한정 대상이 아니다.
- 근거: 가이드는 "Migrations are often complex, and an incorrectly defined migration can cause your app to crash. To preserve your app's stability, test your migrations." 이고 절차는 `createDatabase` → `runMigrationsAndValidate` → 데이터 직접 검증이다(요약) [S260](https://developer.android.com/training/data-storage/room/migrating-db-versions). `runMigrationsAndValidate`는 "Once migrations are done, this functions validates the database schema to ensure the migration performed resulted in the expected schema." 이다 [S264](https://developer.android.com/reference/kotlin/androidx/room3/testing/MigrationTestHelper). 공식은 Android 모듈에서 이 헬퍼를 로컬 JVM 테스트로 돌리라고 안내하지 않는다(침묵, 소스 확인) — Android용 헬퍼는 `Instrumentation`을 받는다. 가이드 예시 코드의 Kotlin 탭은 `testImplementation`, Groovy 탭은 `androidTestImplementation`으로 어긋나는데 예시 테스트가 계측 테스트이므로 `androidTestImplementation`을 골랐다(추론, 문서가 밝히지 않음). **필수화는 팩 결정 `MIGRATION_TEST`이고 이 조합은 아직 실행으로 확인하지 않았다.**
- 예시:
  ```kotlin
  // Good — :core:database/src/androidTest
  @Test fun migrate1To2() = runTest {
      helper.createDatabase(1).use { it.executeSQL("INSERT INTO session (id) VALUES (1)") }
      val db = helper.runMigrationsAndValidate(2, listOf(Migration1To2))
      // 스키마는 헬퍼가 검증한다. 옮겨진 데이터는 여기서 직접 조회해 확인한다.
  }
  // Bad: version 만 올리고 마이그레이션 테스트를 추가하지 않는다
  ```
- 체크: `version`이 오른 변경에 대응하는 마이그레이션 테스트가 있는가. 테스트가 데이터 보존을 직접 확인하는가. `room3-testing`이 `androidTestImplementation`인가.

### R-27-12 출시한 각 버전에서 현재 버전까지 전체 경로 테스트를 둔다
- 규칙: 마이그레이션 테스트 클래스에 출시한 **각 버전**의 DB를 만들어 닫고, 등록한 전체 마이그레이션(`ALL_MIGRATIONS`)을 붙인 `Room.databaseBuilder(...)`로 현재 버전까지 열어 확인하는 테스트를 버전마다 둔다(반복문이나 매개변수화 테스트도 된다). 새 마이그레이션을 추가할 때 `ALL_MIGRATIONS` 목록에 넣는 것을 잊지 않는다. R-27-07의 경로 누락을 이 테스트가 잡는다.
- 근거: 가이드는 "Although you can test a single incremental migration, you should include a test that covers all migrations defined for your app's database." 라고 하고, 최저 버전 DB를 만들고 닫은 뒤 `addMigrations(*ALL_MIGRATIONS)`로 최신 버전을 열어 검증하는 예시를 준다(요약) [S260](https://developer.android.com/training/data-storage/room/migrating-db-versions). 가이드의 예시는 최저 버전 하나에서 시작한다. R-27-07이 허용하는 지름길(예: 1→3)이 있으면 최저 버전 테스트만으로는 중간 버전(2→3)의 경로 누락을 잡지 못하므로 각 출시 버전에서 여는 것은 팩 결정이다. 이 테스트를 필수로 두는 것도 팩 결정 `MIGRATION_TEST`다.
- 예시:
  ```kotlin
  // Good
  @Test fun migrateAllFromEachReleasedVersion() = runTest {
      for (version in RELEASED_VERSIONS) {          // 예: 1, 2, 3 (현재 버전은 4)
          helper.createDatabase(version).close()
          Room.databaseBuilder(...).setDriver(/* ... */).addMigrations(*ALL_MIGRATIONS).build()
              .use { /* 열어서 검증 */ }
      }
  }
  // Bad: 최저 버전 테스트 하나만 있어 1→4 지름길이 있으면 2→4 경로 삭제를 아무도 잡지 못한다
  ```
- 체크: 출시한 각 버전에서 현재 버전까지 여는 테스트가 있는가. `ALL_MIGRATIONS`가 새 마이그레이션과 함께 갱신됐는가.

## 출처가 침묵하는 것 (규칙으로 쓰지 않음)

- **AGP 9.4·KSP 2.3.x·Kotlin 2.4와 Room 3의 호환**: 릴리스 노트에 호환 문장이 없고 Room 3 플러그인의 최소 AGP도 없다. 이 팩의 확정 버전 조합에서 아직 실행으로 확인하지 않았다.
- **Room 3 플러그인이 스키마 JSON을 `androidTest` assets에 연결하는 정확한 조건**: KDoc 요약만 있다. 수동 설정은 `android { sourceSets { getByName("androidTest").assets.srcDir("$projectDir/schemas") } }`(플러그인을 쓰지 않는 경우)다. 첫 마이그레이션 테스트가 스키마 JSON을 못 찾으면 그때 추가한다.
- **SQLiteDriver 선택**: 위 드라이버 매트릭스로만 남긴다. 선택지만 있고 기본값이 없다.
- **테이블 재생성·외래 키 절차**: SQLite 문서는 12단계 절차(`PRAGMA foreign_keys=OFF`·`foreign_key_check`)를 주지만 Room 문서는 외래 키를 언급하지 않는다. `PRAGMA foreign_keys`는 트랜잭션 안에서 바꿀 수 없다는 SQLite 성질이 Room의 "이미 트랜잭션 안에서 호출됨"(R-27-09)과 어떻게 만나는지 출처가 다루지 않는다. 규칙으로 쓰지 않는다.
- **백업·복원과 스키마 버전**: Auto Backup은 데이터베이스 파일을 기본으로 포함하지만 복원된 파일의 스키마 버전이 앱 버전과 다를 때의 동작을 두 문서 모두 다루지 않는다.
- **다운그레이드 마이그레이션 작성법과 앱 롤백**: 공식 언급은 `fallbackToDestructiveMigrationOnDowngrade` 한 줄뿐이다. 릴리스 빌드에서 이 옵션도 쓰지 않는다는 것은 R-27-10이 이미 포함한다.
- **오래 걸리는 마이그레이션의 시작 시간 영향**: 마이그레이션은 DB를 처음 열 때 실행된다는 것을 소스로만 확인했고, 진행 표시·분할 등 대처에 대한 공식 권고는 없다. `setConnectionPoolTimeout`은 3.1.0-alpha01에만 있다.
- **Android Lint·detekt의 Room 스키마 기본 검사**: 존재 여부를 확인하지 못했다. Konsist로 `@Database`의 `exportSchema = false`를 읽는 규칙은 인터페이스 선언만 확인했고 실행하지 않았다.
- **Room 2.x 마이그레이션 가이드 원문**: 현재 URL은 Room 3 내용으로 교체돼 있어 2.x 시절 문장과 비교하지 못했다.
