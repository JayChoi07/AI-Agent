#!/usr/bin/env bash
# 릴리스 번들을 만든다: 게이트 → versionCode 올리기 → bundleRelease (R-31-18, R-31-11).
# 프로젝트의 scripts/release.sh 로 복사한다. 업로드는 하지 않는다 — Play Console 에서 사람이 올린다 (R-31-10).
# 번들 빌드가 실패해도 올린 versionCode 는 되돌리지 않는다. 한 번 쓴 값은 다시 쓰지 않는다.
set -euo pipefail
root=$(git rev-parse --show-toplevel)
cd "$root"

scripts/check.sh
code=$(scripts/bump-version-code.sh)

printf '\n== bundleRelease (versionCode %s) ==\n' "$code"
./gradlew bundleRelease

cat <<DONE

versionCode $code 번들 완료: app/build/outputs/bundle/release/
다음 할 일
  1. Play Console 의 내부 테스트 트랙에 AAB 를 올린다.
  2. distribution/version-code.txt 를 커밋한다.
DONE
