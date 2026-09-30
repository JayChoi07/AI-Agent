#!/usr/bin/env bash
# distribution/version-code.txt 의 숫자를 1 올리고 새 값을 출력한다 (R-31-11).
# 프로젝트의 scripts/bump-version-code.sh 로 복사한다. 파일이 없으면 1 로 만든다.
set -euo pipefail
root=$(git rev-parse --show-toplevel)
cd "$root"

file=distribution/version-code.txt
max=2100000000

current=0
if [ -f "$file" ]; then
  current=$(tr -d '[:space:]' < "$file")
fi

case "$current" in
  '' | *[!0-9]*)
    echo "$file 의 내용이 숫자가 아니다: '$current'" >&2
    exit 1
    ;;
esac

# 산술 오버플로로 상한 검사를 우회하지 못하게, 계산 전에 자릿수부터 막는다.
if [ "${#current}" -gt 10 ]; then
  echo "$file 의 값이 Play 상한 $max 을 넘는다: $current" >&2
  exit 1
fi

next=$((10#$current + 1))
if [ "$next" -gt "$max" ]; then
  echo "versionCode 가 Play 상한 $max 을 넘는다: $next" >&2
  exit 1
fi

mkdir -p "$(dirname "$file")"
printf '%s\n' "$next" > "$file"
echo "$next"
