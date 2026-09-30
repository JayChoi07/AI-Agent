#!/usr/bin/env bash
# 게이트 네 단계를 R-31-01 의 순서로 돌린다. 하나라도 실패하면 거기서 멈춘다 (R-31-18).
# 프로젝트의 scripts/check.sh 로 복사한다. JDK 17 이 필요하다 — 셸 기본 JDK 가 17 이 아니면
# JAVA_HOME 을 JDK 17 경로로 지정하고 실행한다.
set -euo pipefail
root=$(git rev-parse --show-toplevel)
cd "$root"

step() {
  printf '\n== %s ==\n' "$*"
  ./gradlew "$@"
}

step ktlintCheck
step detektDebug
step testDebugUnitTest verifyRoborazziDebug
step assembleDebug

# Room 스키마 JSON 이 있는 프로젝트만 (R-27-05). KSP 가 스키마 JSON 을 만든 뒤여야 하므로 빌드 뒤에 돈다.
if [ -x scripts/check-room-schema.sh ]; then
  printf '\n== check-room-schema ==\n'
  scripts/check-room-schema.sh
fi

printf '\n게이트 4단계 통과\n'
