#!/usr/bin/env bash
# 연결된 기기나 실행 중인 에뮬레이터에서 계측 테스트를 돌린다 (R-31-18, R-30-13).
# 프로젝트의 scripts/instrumented.sh 로 복사한다.
set -euo pipefail
root=$(git rev-parse --show-toplevel)
cd "$root"

if ! command -v adb >/dev/null 2>&1; then
  echo "adb 를 찾을 수 없다. Android SDK platform-tools 를 PATH 에 넣는다." >&2
  exit 1
fi

if ! adb devices | tr -d '\r' | awk 'NR > 1 && $2 == "device"' | grep -q .; then
  echo "연결된 기기나 실행 중인 에뮬레이터가 없다." >&2
  exit 1
fi

./gradlew connectedDebugAndroidTest
