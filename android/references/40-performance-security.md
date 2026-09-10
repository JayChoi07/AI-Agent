# 40 성능과 보안 최소선

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

여기 있는 것은 최소선이다. 재구성 최소화의 구현 규칙은 17-compose-ui.md가 소유하고, 이 문서는 그 규칙을 성능 관점에서 언제 확인할지만 정한다.

## 규칙

### R-40-01 성능 측정과 벤치마크는 R8을 켠 릴리스 빌드에서만 한다
- 규칙: 시작 시간·프레임·메모리 수치는 `isMinifyEnabled = true`인 릴리스 변이에서 잰다. 디버그 빌드에서 얻은 수치로 회귀를 판정하거나 최적화 여부를 결정하지 않는다.
- 근거: 디버그 모드는 성능 비용을 얹으므로 측정은 릴리스에서 해야 한다 [S16](https://developer.android.com/develop/ui/compose/performance).
- 예시:
  ```bash
  # Good
  ./gradlew :app:assembleRelease && ./gradlew :benchmark:connectedBenchmarkAndroidTest
  # Bad: 디버그 APK 로 시작 시간을 재고 "느리다"고 결론 낸다
  ```
- 체크: 보고된 수치가 릴리스 빌드에서 나온 것인가. 빌드 변이가 명시돼 있는가.

### R-40-02 시작 시간이 콜드 5초·웜 2초·핫 1.5초를 넘으면 회귀로 본다
- 규칙: 앱 시작 시간은 TTID와 TTFD를 함께 추적하고, 콜드 5초·웜 2초·핫 1.5초를 넘으면 회귀로 처리해 원인을 찾는다. 최적화 판단은 항상 콜드 스타트를 가정하고 한다.
- 근거: Vitals의 과다 기준은 "Cold startup takes 5 seconds or longer. Warm startup takes 2 seconds or longer. Hot startup takes 1.5 seconds or longer"이며 TTID·TTFD를 함께 보고 콜드 기준으로 최적화하라고 안내한다 [S36](https://developer.android.com/topic/performance/vitals/launch-time).
- 예시:
  ```kotlin
  // Good: Application.onCreate 에서 무거운 초기화를 제거하고 지연 초기화로 옮긴다
  // Bad: 스플래시에서 네트워크 응답을 기다린 뒤 첫 프레임을 그린다
  ```
- 체크: 시작 경로에 동기 I/O나 네트워크 대기가 들어갔는가. TTFD 지점이 코드로 표시돼 있는가.

### R-40-03 릴리스 빌드에 Baseline Profile과 profileinstaller를 포함한다
- 규칙: 릴리스 빌드에 Baseline Profile을 생성해 포함하고 `androidx.profileinstaller` 의존성을 넣는다. 프로파일 생성용 변이는 `isMinifyEnabled = false`, 배포 릴리스는 `true`로 둔다.
- 근거: Baseline Profile은 첫 실행부터 시작 성능을 개선하며(Startup Profile·R8 규칙 재작성이 추가 개선), 산출물은 `assets/dexopt/baseline.prof`이고 `androidx.profileinstaller`가 필요하다. 생성 변이는 minify를 끄고 릴리스는 켠다 [S37](https://developer.android.com/topic/performance/baselineprofiles/overview).
- 예시:
  ```kotlin
  // Good
  release { isMinifyEnabled = true }
  benchmarkRelease { initWith(release); isMinifyEnabled = false }
  // Bad: 프로파일 생성 변이까지 난독화해 프로파일 규칙이 실제 릴리스와 어긋난다
  ```
- 체크: 릴리스 APK에 `assets/dexopt/baseline.prof`가 있는가. 생성 변이의 minify 설정이 위와 같은가.

### R-40-04 릴리스 빌드는 R8을 켠다
- 규칙: 배포용 릴리스 변이는 `isMinifyEnabled = true`로 둔다. 크래시가 난다고 R8을 끄지 않고 keep 규칙으로 좁혀 해결하며, 규칙을 추가하면 왜 필요한지 주석으로 남긴다.
- 근거: 릴리스는 R8을 켜는 것이 전제이며 R8 규칙 재작성이 시작 성능 개선에 기여한다 [S37](https://developer.android.com/topic/performance/baselineprofiles/overview). 성능 측정도 R8 켠 상태를 기준으로 한다 [S16](https://developer.android.com/develop/ui/compose/performance).
- 예시:
  ```proguard
  # Good: 리플렉션으로 접근하는 모델만 좁혀 keep
  -keep class com.example.model.** { *; }
  # Bad
  -keep class ** { *; }
  ```
- 체크: 릴리스 변이의 minify가 켜져 있는가. 새로 추가된 keep 규칙에 이유가 적혀 있는가.

### R-40-05 재구성 비용을 늘리는 패턴을 넣지 않는다
- 규칙: 이미 읽은 상태에 되쓰기(backwards write)를 하지 않고, lazy 레이아웃에는 안정적인 `key`를 주며, 자주 바뀌는 값은 람다 기반 modifier(`Modifier.offset { }`)로 읽기를 미룬다. 구체적인 Composable 작성 규칙은 17-compose-ui.md를 따른다.
- 근거: "Never write to state that has already been read in a composable", lazy `key`·`derivedStateOf`·람다 modifier로 재구성 범위를 줄이라는 안내다 [S16](https://developer.android.com/develop/ui/compose/performance), [S17](https://developer.android.com/develop/ui/compose/performance/bestpractices).
- 예시:
  ```kotlin
  // Good
  LazyColumn { items(users, key = { it.id }) { UserRow(it) } }
  Box(Modifier.offset { IntOffset(scrollOffset.roundToInt(), 0) })
  // Bad: 컴포지션 중 읽은 상태를 같은 컴포지션에서 다시 쓴다
  ```
- 체크: lazy 아이템에 안정적인 key가 있는가. 컴포지션 본문에서 읽은 상태를 같은 본문에서 쓰지 않는가.

### R-40-06 수명이 긴 객체에 짧은 수명의 참조를 담지 않는다
- 규칙: 싱글턴·Repository·`ViewModel`은 Activity·Fragment·View·`Composable` 스코프의 Context나 콜백을 필드로 보관하지 않는다. Context가 필요하면 Hilt의 `@ApplicationContext`를 주입받고, Activity Context가 필요한 코드는 UI 계층에 둔다. 코루틴은 항상 수명 주기에 묶인 스코프에서 시작해 스코프 취소와 함께 정리되게 한다.
- 근거: Hilt는 Context를 `@ApplicationContext`·`@ActivityContext` 한정자로 구분해 주입한다 — 스코프가 다른 Context를 구분하는 것이 이 한정자의 목적이다 [S05](https://developer.android.com/training/dependency-injection/hilt-android). 코루틴은 적절한 스코프에서 만들어 취소가 전파되게 해야 한다 [S40](https://developer.android.com/kotlin/coroutines/coroutines-best-practices).
- 예시:
  ```kotlin
  // Good
  @Singleton class ImageCache @Inject constructor(@ApplicationContext private val context: Context)
  // Bad
  @Singleton class ImageCache { lateinit var activity: Activity }
  ```
- 체크: 싱글턴이 Activity·View 타입을 들고 있는가. `GlobalScope`나 수명 주기와 무관한 스코프에서 시작된 코루틴이 있는가.

### R-40-07 모든 컴포넌트에 `android:exported`를 명시하고 기본은 false다
- 규칙: 매니페스트의 Activity·Service·Receiver·Provider에 `android:exported`를 항상 적고 기본값은 `false`로 둔다. 외부에 열어야 하면 왜 열어야 하는지 주석으로 남기고 인텐트 데이터를 검증한다.
- 근거: 컴포넌트 노출은 명시적으로 선언하고 필요 없으면 열지 말라는 보안 권장사항이다 [S38](https://developer.android.com/privacy-and-security/security-tips).
- 예시:
  ```xml
  <!-- Good -->
  <activity android:name=".MainActivity" android:exported="false" />
  <!-- Bad: exported 를 적지 않고 인텐트 필터만 붙인다 -->
  ```
- 체크: 매니페스트에 exported가 빠진 컴포넌트가 있는가. `true`인 항목에 이유와 입력 검증이 있는가.

### R-40-08 서비스는 명시적 인텐트로만 시작·바인딩한다
- 규칙: 자체 서비스는 클래스나 컴포넌트 이름을 지정한 명시적 인텐트로만 시작·바인딩한다. 암시적 인텐트로 서비스를 시작하지 않는다.
- 근거: 서비스는 명시적 인텐트로만 시작·바인딩하라는 보안 권장사항이다(암시적 `bindService()`는 플랫폼에서 막힌다) [S38](https://developer.android.com/privacy-and-security/security-tips).
- 예시:
  ```kotlin
  // Good
  context.startService(Intent(context, SyncService::class.java))
  // Bad
  context.startService(Intent("com.example.action.SYNC"))
  ```
- 체크: 액션 문자열만으로 서비스를 시작하는 코드가 있는가.

### R-40-09 평문 HTTP를 막고 통신은 HTTPS로만 한다
- 규칙: network security config를 기본 포함하고 `cleartextTrafficPermitted="false"`로 둔다. 디버그용 예외가 필요하면 debug 소스셋 전용 설정으로 분리한다. localhost 포트로 앱 간 통신을 하지 않는다.
- 근거: HTTPS 사용과 network security config로 평문 트래픽을 막고, localhost 포트를 IPC로 쓰지 말라는 권장사항이다 [S38](https://developer.android.com/privacy-and-security/security-tips).
- 예시:
  ```xml
  <!-- Good: res/xml/network_security_config.xml -->
  <network-security-config>
      <base-config cleartextTrafficPermitted="false" />
  </network-security-config>
  <!-- Bad: 릴리스 매니페스트에 android:usesCleartextTraffic="true" -->
  ```
- 체크: 릴리스 구성에서 평문 트래픽이 허용돼 있는가. 디버그 예외가 debug 소스셋으로 분리돼 있는가.

### R-40-10 민감 데이터는 내부 저장소에 두고 KeyStore 기반으로 암호화한다
- 규칙: 자격 증명·개인정보는 앱 내부 저장소에만 저장한다. 암호화가 필요하면 AES-256 + GCM 이상, 무결성은 HMAC-SHA-256 이상, 키는 Android KeyStore나 Jetpack Security로 관리하고 난수는 `SecureRandom`으로 만든다. 외부 저장소에는 비민감 데이터만 둔다.
- 근거: 외부 저장소는 전역 읽기·쓰기가 가능하므로 비민감 정보만 저장하고, 암호화는 AES-256/GCM·HMAC-SHA-256 이상과 KeyStore·`SecureRandom`을 쓰라는 권장사항이다 [S38](https://developer.android.com/privacy-and-security/security-tips).
- 예시:
  ```kotlin
  // Good: context.filesDir 하위 + KeyStore 로 감싼 DataStore
  // Bad: Environment.getExternalStorageDirectory() 에 토큰을 평문 저장
  ```
- 체크: 토큰·개인정보가 외부 저장소나 평문 SharedPreferences에 있는가. 키가 소스에 상수로 박혀 있지 않은가.

### R-40-11 API 키와 시크릿을 소스에 커밋하지 않는다
- 규칙: API 키·시크릿은 소스와 버전 관리에 넣지 않고 `local.properties`와 secrets Gradle 플러그인으로 빌드 시점에 주입한다(CI는 R-31-08). 유출된 키는 즉시 폐기하고, 정기 로테이션 주기를 정해 둔다.
- 근거: API 키를 커밋하지 말고 `local.properties` + secrets 플러그인으로 주입하며 주기적으로 로테이션하라는 권장사항이다 [S38](https://developer.android.com/privacy-and-security/security-tips).
- 예시:
  ```kotlin
  // Good
  buildConfigField("String", "MAPS_API_KEY", "\"${properties["MAPS_API_KEY"]}\"")
  // Bad
  const val MAPS_API_KEY = "AIza..."
  ```
- 체크: 저장소 이력에 키 문자열이 남아 있는가. `local.properties`가 무시 목록에 있는가.

### R-40-12 권한은 최소로 요청하고 커스텀 dangerous 권한을 만들지 않는다
- 규칙: 기능에 실제로 필요한 권한만 매니페스트에 넣고, 대안 API로 대체 가능하면 권한을 쓰지 않는다. 커스텀 dangerous 권한을 정의하지 않고, 같은 개발사 앱 사이 IPC는 `signature` 보호 수준으로 제한한다.
- 근거: "Minimize the number of permissions that your app requests", 커스텀 dangerous 권한 지양, 동일 개발사 IPC는 signature 권한을 쓰라는 권장사항이다 [S38](https://developer.android.com/privacy-and-security/security-tips).
- 예시:
  ```xml
  <!-- Good -->
  <permission android:name="com.example.SYNC" android:protectionLevel="signature" />
  <!-- Bad -->
  <permission android:name="com.example.SYNC" android:protectionLevel="dangerous" />
  ```
- 체크: 쓰지 않는 권한이 매니페스트에 남아 있는가. 커스텀 권한의 보호 수준이 signature인가.

### R-40-13 WebView는 필요한 기능만 켠다
- 규칙: WebView가 JavaScript를 필요로 하지 않으면 `setJavaScriptEnabled()`를 호출하지 않는다. `addJavaScriptInterface()`는 앱 패키지 안의 콘텐츠를 띄울 때만 쓰고, 원격 콘텐츠에는 노출하지 않는다.
- 근거: JavaScript가 필요 없으면 켜지 말고 `addJavaScriptInterface()`는 APK 내 콘텐츠에만 쓰라는 권장사항이다 [S38](https://developer.android.com/privacy-and-security/security-tips).
- 예시:
  ```kotlin
  // Good: 정적 도움말 HTML 은 JavaScript 없이 표시
  // Bad
  webView.settings.javaScriptEnabled = true
  webView.addJavaScriptInterface(Bridge(), "android") // 원격 URL 로드 중
  ```
- 체크: 원격 URL을 띄우는 WebView에 JS 인터페이스가 붙어 있는가. JavaScript를 켤 필요가 실제로 있는가.

## 출처가 침묵하는 것 (규칙으로 쓰지 않음)

- **런타임 권한을 요청할 화면·시점**: 조사 출처는 권한 최소화까지만 정하고 요청 위치를 정하지 않는다 [S38](https://developer.android.com/privacy-and-security/security-tips). 요청 코드는 UI 계층에 두되 시점은 기능 설계에 맡긴다.
- **메모리 사용량 상한 수치**: 앱 메모리 예산을 정한 출처가 없어 숫자를 규칙으로 쓰지 않는다. R-40-06의 참조 보관 금지와 R-40-01의 릴리스 측정으로 대신한다.
