#!/usr/bin/env bash
# Room 스키마 JSON 검사 (R-27-04, R-27-05). 프로젝트의 scripts/check-room-schema.sh 로 복사하고
# scripts/check.sh 의 마지막 단계에서 부른다 — KSP 가 스키마 JSON 을 만든 뒤(빌드 뒤)여야
# "version 을 올리지 않고 스키마만 바꾼" 경우가 작업 트리의 JSON 변경으로 드러난다.
#
# 검사 세 가지:
#  1. upstream 대비 기존 스키마 JSON 이 수정·삭제·이름 변경됐으면 실패한다(작업 트리 기준).
#     version 을 올리지 않고 스키마만 바꾸면 Room 이 같은 번호의 JSON 을 조용히 덮어쓴다.
#  2. 커밋하지 않은 새 스키마 JSON 이 있으면 실패한다(version 을 올리고 JSON 을 커밋하지 않은 경우).
#  3. 각 스키마 디렉터리의 가장 큰 번호가 그 데이터베이스의 @Database(version = N) 과 같아야 한다.
#
# 한계: upstream 이 없으면 1번은 건너뛴다. 디렉터리 이름(데이터베이스 클래스의 전체 이름)에서 클래스를 찾지 못하면
# 3번은 그 디렉터리에 대해 경고만 한다. 가짜 저장소로만 확인했고 실제 Room 3 프로젝트로는 돌려 보지 않았다.
set -euo pipefail

root=$(git rev-parse --show-toplevel)
cd "$root"

pattern=':(glob)**/schemas/**/*.json'
fail=0

# 1. 기존 스키마 JSON 수정·삭제·이름 변경 금지 (upstream 대비 작업 트리)
if upstream=$(git rev-parse --abbrev-ref --symbolic-full-name '@{upstream}' 2>/dev/null); then
  base=$(git merge-base HEAD "$upstream")
  changed=$(git diff --name-status --no-renames "$base" -- "$pattern" | awk '$1 ~ /^(M|D)/ { print }')
  if [ -n "$changed" ]; then
    echo "check-room-schema: 이미 올라간 스키마 JSON 이 수정되거나 삭제됐다. version 을 올리고 새 번호의 JSON 을 만든다." >&2
    echo "$changed" >&2
    fail=1
  fi
else
  echo "check-room-schema: upstream 이 없어 기존 JSON 수정 검사는 건너뛴다." >&2
fi

# 2. 커밋하지 않은 새 스키마 JSON 금지
untracked=$(git ls-files --others --exclude-standard -- "$pattern")
if [ -n "$untracked" ]; then
  echo "check-room-schema: 커밋하지 않은 스키마 JSON 이 있다. version 을 올렸다면 새 JSON 을 함께 커밋한다." >&2
  echo "$untracked" >&2
  fail=1
fi

# 3. 최대 스키마 번호 == @Database version (작업 트리의 파일 기준)
all_files=$(git ls-files --cached --others --exclude-standard -- "$pattern" | sort -u)
[ -n "$all_files" ] || exit "$fail"

dirs=$(echo "$all_files" | xargs -n1 dirname | sort -u)
for dir in $dirs; do
  max=$(echo "$all_files" | grep -F "$dir/" | sed -n 's#.*/\([0-9][0-9]*\)\.json$#\1#p' | sort -n | tail -1)
  [ -n "$max" ] || continue

  fqcn=$(basename "$dir")
  simple=${fqcn##*.}
  pkg=""
  case "$fqcn" in *.*) pkg=${fqcn%.*} ;; esac

  # 같은 단순 이름의 데이터베이스가 여럿이어도 패키지까지 맞는 파일만 고른다.
  db_file=""
  for cand in $(grep -rl --include='*.kt' -E "class[[:space:]]+${simple}\b" . 2>/dev/null | xargs grep -l '@Database' 2>/dev/null || true); do
    if [ -z "$pkg" ] || grep -q -E "^package[[:space:]]+${pkg//./\\.}[[:space:]]*$" "$cand"; then
      db_file=$cand
      break
    fi
  done
  if [ -z "$db_file" ]; then
    echo "check-room-schema: 경고 — $dir 에 해당하는 @Database 클래스($fqcn)를 찾지 못했다." >&2
    continue
  fi

  declared=$(grep -o -E 'version[[:space:]]*=[[:space:]]*[0-9]+' "$db_file" | head -1 | grep -o -E '[0-9]+' || true)
  if [ -z "$declared" ]; then
    echo "check-room-schema: 경고 — $db_file 에서 version 을 읽지 못했다." >&2
    continue
  fi
  if [ "$declared" != "$max" ]; then
    echo "check-room-schema: $fqcn 의 @Database version($declared)이 가장 큰 스키마 JSON 번호($max)와 다르다." >&2
    fail=1
  fi
done

exit "$fail"
