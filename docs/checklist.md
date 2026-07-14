# 사전 준비 체크리스트

## 지금 당장 (모집 마감 7/17)

- [ ] **지원서 제출** — 트랙 선택 (FDE vs AI 엔지니어)
- [ ] 이 저장소 push, 노트북에 클론 확인

## D-7 ~ D-3

- [ ] **배포 경로 리허설** — 웹 산출물 가정, "코드 → 공개 URL" 30분 안에:
  - Vercel 또는 Netlify 계정 + CLI 로그인, hello-world 1회 배포
  - 백엔드 필요 시: Railway/Render/Fly.io 중 1개 리허설
- [ ] **AI 계정/쿼터**: OpenAI API 키(크레딧 확인), Claude Code 로그인, Codex CLI 로그인
- [ ] bootstrap.sh 를 빈 폴더에 실행해보고 전체 흐름 1회 리허설 (설치 → task-plan → 구현 → WORKLOG → report)
- [ ] K-뷰티 커머스 도메인 감 잡기 (올리브영 트랙 선택 시): 상품 검색/추천/리뷰 등 서비스 구조 훑기

## D-1

- [ ] 노트북: Node, Python, git, gh CLI 로그인 상태 확인
- [ ] 폰 테더링 동작 확인 (행사장 네트워크 불안 대비)
- [ ] GitHub에 당일용 빈 repo 만들 준비 (org 권한 등)
- [ ] Probe 안내 메일/문서 재확인 → docs/cofathon.md 미확인 사항 채우기
- [ ] 충전기, 보조배터리

## 당일 아침

- [ ] docs/workflow.md 한 번 읽기
- [ ] 하네스 업로드 방식 확인되면 이 저장소 업로드
- [ ] **업로드 직후 smoke test**: "AGENTS.md의 작업 원칙 4가지를 요약해봐"라고 물어서
      Probe가 실제로 지침을 로드했는지 확인 (안 됐으면 프롬프트에 직접 붙여넣기)

## 제출 직전

- [ ] **익명화 게이트**: 리포트/WORKLOG/데모 URL에서 실명·GitHub 계정명·로컬 절대경로·
      시크릿·스크린샷 메타데이터 제거 (블라인드 심사)
