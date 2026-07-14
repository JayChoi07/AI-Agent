# WORKLOG — AI-Agent 스타터 킷

> append-only. 형식: [HH:MM] [작업ID][역할] 무엇을 왜 — 결정/프롬프트 전략/검증 결과/막힌 것

[13:30] [kit-v1][구현] 코파톤 조사 후 킷 설계 — 심사가 블라인드 리포트 기반이라 WORKLOG→report 파이프라인을 핵심으로 결정. 기각한 대안: 무거운 멀티에이전트 승인 게이트(하루짜리 행사에 과함).
[13:35] [kit-v1][구현] AGENTS.md를 정본으로 결정 — OpenAI가 기술 파트너(Codex 계열 가능성)이고 도구 중립 표준이므로. CLAUDE.md는 참조형 확장으로.
[14:00] [kit-v1][검증] bootstrap.sh 임시 폴더 설치 테스트 통과.
[14:10] [kit-v1][교차리뷰] Codex 리뷰 실행 — HIGH 5건 (sed 특수문자 치환 깨짐+실패 은폐, .env.* 미인용 glob 확장, cp -n 실패 은폐+POSIX 비표준, 병렬 에이전트 WORKLOG 동시 쓰기, 미커밋 상태), MEDIUM 11건, LOW 다수.
[14:20] [kit-v1][수정] 리뷰 반영 — bootstrap을 bash 치환+원자적 쓰기+명시적 존재검사로 재작성, WORKLOG 단일 writer 원칙 추가, 병렬 분배 조건화, 익명화 게이트·Probe smoke test·경량 eval 섹션 추가, 정본↔템플릿 규칙 동기화, "과정>결과" 단정 완화.
[14:25] [kit-v1][검증] 재테스트에서 신규 버그 발견·수정 — bash 5.2+ patsub_replacement로 프로젝트명 'R&D'의 &가 매치 패턴으로 확장. shopt -u로 해제. 특수문자 이름('R&D 팀 a\b')·공백 경로·재실행 멱등성 테스트 통과.
