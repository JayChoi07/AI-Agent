#!/usr/bin/env bash
# 새 프로젝트에 AI Native 하네스 설치
# 사용법: ./bootstrap.sh /path/to/project [프로젝트명]
set -euo pipefail
# bash 5.2+의 patsub_replacement가 켜져 있으면 치환 문자열의 &가 매치로 확장됨 — 끔 (구버전은 옵션 없음)
shopt -u patsub_replacement 2>/dev/null || true

KIT_DIR="$(cd "$(dirname "$0")" && pwd)"
# --pack <name> 옵션 (위치 인자 뒤에 둔다). 현재 지원: android
PACK=""
args=()
while [ $# -gt 0 ]; do
  case "$1" in
    --pack) PACK="${2:?--pack 값 필요}"; shift 2;;
    *) args+=("$1"); shift;;
  esac
done
# bash 4.3 이하는 set -u 에서 빈 배열 확장이 unbound variable 이다 — ${arr[@]+"${arr[@]}"} 로 감싼다
set -- ${args[@]+"${args[@]}"}
TARGET="${1:?사용법: ./bootstrap.sh /path/to/project [프로젝트명] [--pack android]}"
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

# 도메인 팩 설치
if [ -n "$PACK" ]; then
  # 아래에서 "$TARGET/harness/$PACK" 을 rm -rf 하므로 경로 조각을 이름 하나로 제한한다
  case "$PACK" in */*|*\\*|.|..) echo "error: 팩 이름에 경로를 쓸 수 없다 '$PACK'" >&2; exit 1;; esac
  if [ ! -d "$KIT_DIR/$PACK" ] || [ ! -f "$KIT_DIR/$PACK/SKILL.md" ]; then
    echo "error: 알 수 없는 팩 '$PACK' (지원: android)" >&2; exit 1
  fi
  if [ -e "$TARGET/harness/$PACK" ]; then
    # 필수 파일/디렉터리 검사 — 이전 실행이 중간에 끊겨 반쪽만 남았으면 '설치됨'으로 넘기지 않는다
    incomplete=0
    for req in SKILL.md README.md references checklists templates; do
      [ -e "$TARGET/harness/$PACK/$req" ] || incomplete=1
    done
    if [ "$incomplete" -eq 1 ]; then
      echo "error: harness/$PACK 이(가) 불완전하다 (SKILL.md 없음). 지우고 다시 실행: rm -rf '$TARGET/harness/$PACK'" >&2
      exit 1
    fi
    echo "skip: harness/$PACK 이미 존재"
  else
    mkdir -p "$TARGET/harness"
    # 같은 부모 아래 임시 디렉터리에 복사·정리·검증한 뒤 최종 이름으로 mv 한다(같은 파일시스템이라 원자적).
    # 중간에 끊기면 최종 경로는 아예 생기지 않고 임시 디렉터리만 남는다.
    stage="$(mktemp -d "$TARGET/harness/.$PACK.XXXXXX")"
    cp -R "$KIT_DIR/$PACK" "$stage/$PACK" || { rm -rf "$stage"; echo "error: 팩 복사 실패" >&2; exit 1; }
    rm -rf "$stage/$PACK/research"   # 조사 노트는 프로젝트에 불필요
    if [ ! -f "$stage/$PACK/SKILL.md" ]; then
      rm -rf "$stage"; echo "error: 팩 복사 결과에 SKILL.md 가 없다" >&2; exit 1
    fi
    mv "$stage/$PACK" "$TARGET/harness/$PACK"
    rmdir "$stage"
    echo "ok:   harness/$PACK 설치"
  fi
  marker="## Android 표준"
  if [ "$PACK" = "android" ] && ! grep -q -F "$marker" "$TARGET/AGENTS.md"; then
    tmp="$TARGET/.AGENTS.md.tmp"
    {
      cat "$TARGET/AGENTS.md"
      printf '\n%s\n\n' "$marker"
      printf '%s\n' \
        'Android 작업(화면·기능 모듈·데이터 소스·함수·버그 수정·리팩터링·리뷰)은 시작 전에 `harness/android/SKILL.md`를 읽고 그 절차(분류→결정→구현→표준 준수 보고)를 따른다.' \
        '규칙 근거는 `harness/android/references/`의 R-ID로 인용한다. 이 파일의 다른 규칙과 충돌하면 이 파일이 우선한다.'
    } > "$tmp"
    mv "$tmp" "$TARGET/AGENTS.md"
    echo "ok:   AGENTS.md에 '$marker' 섹션 추가"
  fi
fi

echo ""
echo "설치 완료: $TARGET"
echo "다음 할 일: AGENTS.md의 '프로젝트 개요'와 '명령어' 빈칸 채우기 (특히 배포 명령)"
