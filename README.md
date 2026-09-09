# AI-Agent — 휴대용 AI Native 하네스

신규 프로젝트 어디서든 **10분 안에 AI 우선 개발 워크플로우를 까는** 스타터 킷.

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
└── docs/
    ├── cofathon.md      # 코파톤 조사 결과 + 시사점
    ├── checklist.md     # 사전 준비 체크리스트 (D-7 ~ 당일)
    └── workflow.md      # 당일 운영 플레이북
```

## 빠른 시작

```bash
# 새 프로젝트에 하네스 설치
./bootstrap.sh /path/to/new-project

# 설치되는 것: AGENTS.md, CLAUDE.md, WORKLOG.md, templates/{task-plan,report}.md,
#              .gitignore, git init (없을 때만 — 기존 파일은 절대 덮어쓰지 않음)
```

## 핵심 설계

1. **AGENTS.md가 정본** — OpenAI Codex 표준 포맷. 어떤 AI 도구를 붙여도 같은 규칙으로 시작.
2. **WORKLOG가 리포트가 된다** — 작업 결정·프롬프트 전략·검증 결과를 WORKLOG.md에 append.
   심사(블라인드 리포트)는 작동하는 결과물 + "AI를 어떻게 Steering했는가"를 보므로,
   과정 기록이 제출물 재료가 된다. 병렬 작업 시엔 오케스트레이터만 기록(단일 writer).
3. **생성자↔검증자 분리** — 구현자의 자기 테스트에 더해, 별도 에이전트/도구가 교차 리뷰한다.
