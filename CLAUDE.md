# CLAUDE.md — Claude Code 전용 확장

**정본은 [AGENTS.md](AGENTS.md)다. 먼저 읽고 그 규칙을 따를 것.** 아래는 Claude Code에서만 적용되는 확장.

## Claude Code 작업 방식

- 대규모 변경(5+ 파일, 신규 기능, 아키텍처 변경) → **플랜 모드 우선**
- 독립 작업 2개 이상 → Agent 도구로 병렬 분배
- 작업 완료 후 별도 검증 에이전트(또는 Codex)로 교차 리뷰
- 레거시 코드 수정 시 Grep으로 호출처 먼저 파악

## WORKLOG 연동

**코드/문서 변경이 있었던 턴**이 끝날 때 WORKLOG.md에 해당 턴의 결정·검증 결과를 append한다
(읽기 전용 질문·상태 확인 턴은 기록하지 않음). 병렬 서브에이전트는 직접 쓰지 않고 메인 세션만 기록한다 (AGENTS.md 단일 writer 원칙).
