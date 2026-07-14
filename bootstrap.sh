#!/usr/bin/env bash
# 새 프로젝트에 AI Native 하네스 설치
# 사용법: ./bootstrap.sh /path/to/project [프로젝트명]
set -euo pipefail
# bash 5.2+의 patsub_replacement가 켜져 있으면 치환 문자열의 &가 매치로 확장됨 — 끔 (구버전은 옵션 없음)
shopt -u patsub_replacement 2>/dev/null || true

KIT_DIR="$(cd "$(dirname "$0")" && pwd)"
TARGET="${1:?사용법: ./bootstrap.sh /path/to/project [프로젝트명]}"
NAME="${2:-$(basename "$TARGET")}"

mkdir -p "$TARGET/templates"

# AGENTS.md — 이미 있으면 덮어쓰지 않음. bash 치환(특수문자 안전) + 임시파일 경유 원자적 쓰기
if [ -e "$TARGET/AGENTS.md" ]; then
  echo "skip: AGENTS.md 이미 존재"
else
  content="$(cat "$KIT_DIR/templates/AGENTS.template.md")"
  tmp="$TARGET/.AGENTS.md.tmp"
  printf '%s\n' "${content//\{\{PROJECT_NAME\}\}/$NAME}" > "$tmp"
  mv "$tmp" "$TARGET/AGENTS.md"
  echo "ok:   AGENTS.md 생성 (빈칸 채울 것: 개요/명령어)"
fi

# CLAUDE.md — AGENTS.md를 정본으로 참조
if [ -e "$TARGET/CLAUDE.md" ]; then
  echo "skip: CLAUDE.md 이미 존재"
else
  cp "$KIT_DIR/CLAUDE.md" "$TARGET/CLAUDE.md"
  echo "ok:   CLAUDE.md 생성"
fi

# WORKLOG.md — 리포트 원재료
if [ -e "$TARGET/WORKLOG.md" ]; then
  echo "skip: WORKLOG.md 이미 존재"
else
  {
    echo "# WORKLOG — $NAME"
    echo ""
    echo "> append-only. 형식: [HH:MM] [작업ID][역할] 무엇을 왜 — 결정/프롬프트 전략/검증 결과/막힌 것"
    echo ""
  } > "$TARGET/WORKLOG.md"
  echo "ok:   WORKLOG.md 생성"
fi

# 템플릿 복사 — cp -n은 POSIX 아님: 존재 검사 후 일반 cp, 실패는 그대로 전파
for f in task-plan.md report.md; do
  if [ -e "$TARGET/templates/$f" ]; then
    echo "skip: templates/$f 이미 존재"
  else
    cp "$KIT_DIR/templates/$f" "$TARGET/templates/$f"
    echo "ok:   templates/$f"
  fi
done

# git 초기화 — worktree/submodule은 .git이 파일이므로 -e로 검사
if [ ! -e "$TARGET/.git" ]; then
  git -C "$TARGET" init -q
  echo "ok:   git init"
fi

# .gitignore 기본 — '.env.*'는 인용 필수(현재 디렉터리 glob 확장 방지), 샘플 파일은 예외
if [ ! -e "$TARGET/.gitignore" ]; then
  printf '%s\n' node_modules/ .env '.env.*' '!.env.example' dist/ build/ __pycache__/ .DS_Store > "$TARGET/.gitignore"
  echo "ok:   .gitignore 생성"
fi

echo ""
echo "설치 완료: $TARGET"
echo "다음 할 일: AGENTS.md의 '프로젝트 개요'와 '명령어' 빈칸 채우기 (특히 배포 명령)"
