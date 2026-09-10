#!/usr/bin/env bash
# android 팩 형식 검사. 사용법: android/scripts/check-pack.sh
# 검사: SKILL.md<=150줄, references/*.md<=300줄, 규칙 5요소, 근거 URL, R-ID 유일, 체크리스트 필수 헤더
set -uo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
fail=0
err() { echo "FAIL: $*"; fail=1; }

# 1. 줄 수 한도
if [ -f "$ROOT/SKILL.md" ]; then
  n=$(awk 'END{print NR}' "$ROOT/SKILL.md"); [ "$n" -le 150 ] || err "SKILL.md ${n}줄 > 150"
else
  err "SKILL.md 없음"
fi
shopt -s nullglob
refs=("$ROOT"/references/*.md)
[ ${#refs[@]} -gt 0 ] || err "references/*.md 없음"
for f in "${refs[@]}"; do
  n=$(awk 'END{print NR}' "$f"); [ "$n" -le 300 ] || err "$(basename "$f") ${n}줄 > 300"
done

# 2. R-ID 유일성
if [ ${#refs[@]} -gt 0 ]; then
  dup=$(grep -h -o -E '^### R-[0-9]{2}-[0-9]{2}' "${refs[@]}" | sed 's/^### //' | sort | uniq -d)
  [ -z "$dup" ] || err "중복 R-ID: $(echo "$dup" | tr '\n' ' ')"
fi

# 3. 규칙 5요소 + 근거 URL
for f in "${refs[@]}"; do
  case "$(basename "$f")" in 90-sources.md) continue;; esac
  out=$(awk -v file="$(basename "$f")" '
    function flush() {
      if (id != "") {
        if (!(r && g && e && c)) printf("FAIL: %s %s 요소 누락(규칙=%d 근거=%d 예시=%d 체크=%d)\n", file, id, r, g, e, c)
        if (g && !url) printf("FAIL: %s %s 근거에 URL 없음\n", file, id)
      }
    }
    /^### R-[0-9][0-9]-[0-9][0-9]/ { flush(); id=$2; r=g=e=c=url=0; next }
    /^- 규칙:/ { r=1 }
    /^- 근거:/ { g=1; if ($0 ~ /https?:\/\//) url=1 }
    /^- 예시:/ { e=1 }
    /^- 체크:/ { c=1 }
    END { flush() }' "$f")
  if [ -n "$out" ]; then echo "$out"; fail=1; fi
  grep -q -E '^### R-' "$f" || err "$(basename "$f") 규칙 항목(### R-) 없음"
done

# 4. 체크리스트 필수 헤더
cls=("$ROOT"/checklists/*.md)
[ ${#cls[@]} -gt 0 ] || err "checklists/*.md 없음"
for f in "${cls[@]}"; do
  for h in '^읽을 references:' '^## 결정 항목' '^## 구현 순서' '^## 산출물 검증'; do
    grep -q -E "$h" "$f" || err "$(basename "$f") 헤더 누락: ${h#^}"   # 메시지에는 정규식 앵커를 빼고 보여준다
  done
done

# 5. references 밖에서 인용한 R-ID 가 실제로 존재하는가 (오인용·유령 ID 방지)
if [ ${#refs[@]} -gt 0 ]; then
  known=$(grep -h -o -E '^### R-[0-9]{2}-[0-9]{2}' "${refs[@]}" | sed 's/^### //' | sort -u)
  cited_files=("$ROOT/SKILL.md" "$ROOT/README.md" "${cls[@]}")
  for d in templates enforcement; do
    while IFS= read -r p; do cited_files+=("$p"); done < <(find "$ROOT/$d" -type f 2>/dev/null)
  done
  cited=$(grep -h -o -E 'R-[0-9]{2}-[0-9]{2}' "${cited_files[@]}" 2>/dev/null | sort -u)
  missing=$(comm -23 <(echo "$cited") <(echo "$known") | tr -d '\r')
  [ -z "$missing" ] || err "references 에 없는 R-ID 인용: $(echo "$missing" | tr '\n' ' ')"
fi

if [ "$fail" -eq 0 ]; then echo "OK: 형식 검사 통과"; fi
exit "$fail"
