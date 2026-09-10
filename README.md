# AI-Agent — 휴대용 AI Native 하네스

신규 프로젝트 어디서든 **10분 안에 AI 우선 개발 워크플로우를 까는** 스타터 킷. 두 층으로 되어 있다.
**하네스 코어**는 도구 중립 지침 [`AGENTS.md`](AGENTS.md)를 정본으로 삼아 Codex·Claude Code·Cursor가 같은 규칙에서
출발하게 하고, 작업 기록(`WORKLOG.md`)을 그대로 제출 리포트의 재료로 쓰며, [`bootstrap.sh`](bootstrap.sh)로 새
프로젝트에 설치되고, 코파톤(하루 해커톤) 당일 플레이북을 함께 담는다. **도메인 팩**은 그 위에 얹는 분야별
표준으로, 지금은 Android 그린필드 표준 스킬 `android-standards`([`android/`](android/README.md)) 하나가 있다.
팩 구조라 iOS·백엔드 팩도 같은 틀(`<팩>/SKILL.md` + `--pack <이름>`)로 추가할 수 있다.

## 상황별로 어디서 시작하나

| 상황 | 할 일 | 자세히 |
|---|---|---|
| 새 프로젝트에 AI 워크플로우 깔기 | `./bootstrap.sh <프로젝트>` — AGENTS.md·CLAUDE.md·WORKLOG.md·templates 설치 | [빠른 시작](#빠른-시작) |
| Android 그린필드 프로젝트 시작 | 팩을 주입한 뒤 build-logic·detekt·ktlint·Konsist·CI 강제 장치를 프로젝트에 설치 | [새 Android 프로젝트 세팅](android/README.md) |
| Claude Code에서 안드로이드 작업 요청하기 | 그냥 "로그인 페이지 만들어줘"라고 한다. 스킬이 자동으로 붙어 결정 표 → 구현 → 검증 → 표준 준수 보고를 낸다 | [android/SKILL.md](android/SKILL.md) |
| Codex·Cursor로 같은 규칙 쓰기 | `./bootstrap.sh <프로젝트> --pack android` — `harness/android/` 복사 + AGENTS.md에 섹션 추가 | [팀·다른 도구와 공유](android/README.md) |
| 기존 앱에 적용 | 신규 Kotlin·Compose 코드에만 쓴다. 충돌하면 프로젝트 지침 파일이 우선 | [기존 앱에 적용](android/README.md) |
| 규칙 고치기 | 출처 확보 → research 기록 → 출처 번호 등록 → 규칙 작성 → 연동 갱신 → 형식 검사 | [규칙 고치기·추가하기](android/README.md) |
| 해커톤 당일 | 페이즈 배분대로 굴리고 WORKLOG를 리포트로 옮긴다 | [docs/workflow.md](docs/workflow.md), [templates/report.md](templates/report.md) |

## 구성

```
AI-Agent/
├── AGENTS.md            # 하네스 본체 — 도구 중립 에이전트 지침 (Codex/Claude/Cursor 공용)
├── CLAUDE.md            # Claude Code 전용 확장 (AGENTS.md를 정본으로 참조)
├── bootstrap.sh         # 새 프로젝트에 하네스 설치 스크립트
├── templates/
│   ├── AGENTS.template.md   # 새 프로젝트용 빈칸 채우기 템플릿
│   ├── task-plan.md         # 작업 분해 템플릿 (목표 → 검증 기준)
│   └── report.md            # 블라인드 리포트 템플릿 (심사 제출용)
├── docs/
│   ├── cofathon.md      # 코파톤 조사 결과 + 시사점
│   ├── checklist.md     # 사전 준비 체크리스트 (D-7 ~ 당일)
│   └── workflow.md      # 당일 운영 플레이북
└── android/             # Android 그린필드 표준 팩 (스킬 android-standards) — [android/README.md](android/README.md)
```

## 빠른 시작

```bash
# 새 프로젝트에 하네스 설치
./bootstrap.sh /path/to/new-project

# Android 프로젝트면 표준 팩까지 함께 주입 (harness/android/ + AGENTS.md 섹션)
./bootstrap.sh /path/to/new-project --pack android

# 설치되는 것: AGENTS.md, CLAUDE.md, WORKLOG.md, templates/{task-plan,report}.md,
#              .gitignore, git init (없을 때만 — 기존 파일은 절대 덮어쓰지 않음)
```

`bootstrap.sh` 는 bash 4 이상에서 돌린다. Windows 에서는 **Git Bash** 를 쓴다(PowerShell·cmd 불가).
Android 팩의 설치·사용법은 [android/README.md](android/README.md) 에 있다.

## 핵심 설계

1. **AGENTS.md가 정본** — OpenAI Codex 표준 포맷이라 어떤 AI 도구를 붙여도 같은 규칙으로 시작한다.
2. **WORKLOG가 리포트가 된다** — 결정·프롬프트 전략·검증 결과를 append 하면 그대로 블라인드 리포트의
   재료가 된다(병렬 작업 시엔 오케스트레이터만 기록하는 단일 writer 원칙).
3. **생성자↔검증자 분리** — 구현자의 자기 테스트에 더해, 별도 에이전트·도구가 교차 리뷰한다.
