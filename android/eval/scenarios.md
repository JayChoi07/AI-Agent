# eval 시나리오
스크래치 그린필드 프로젝트에서 아래 요청을 그대로 던지고 rubric.md로 채점한다.

## S1 화면 추가
요청: "로그인 페이지 만들어줘. 이메일·비밀번호 입력, 로그인 버튼, 성공하면 홈으로."
기대 체크포인트: 유형 new-screen 고지 / 결정 6항목 표 / R-12-02 매트릭스 판정(MVI 해당 0/5 → MVVM-UDF) / LoginKey+entry / UiState·Event / model/Login… sealed Error/Result / ViewModel(@HiltViewModel) / Screen+Preview 2종 / ViewModelTest·ScreenshotTest / 표준 준수 보고.

## S2 데이터 소스 추가
요청: "즐겨찾기 목록 API(GET /favorites) 붙여줘. 오프라인에서도 마지막 목록 보이게."
기대: 유형 new-data-source / Remote·Local 분리 / Repository 인터페이스·구현 모두 data (R-11-02) / 오프라인 우선 결정 근거 / Fake DataSource + Repository 테스트 (R-30-10) / 에러 매핑: 도메인별 sealed 계층 + nullable (R-23).

## S3 함수 작성
요청: "주행 기록 리스트에서 날짜 범위로 걸러서 일자별 총 거리 합계 내림차순으로 정렬하는 함수 짜줘."
기대: 유형 function / 순수함수·시그니처 먼저 / 테스트 3종(정상·경계·빈 입력) / 체이닝은 R-21-08 가독성 규칙 준수(한 문장으로 안 읽히면 중간 결과에 이름 — 단계 수 상한은 없다) / R-21-01·R-21-02 준수.
