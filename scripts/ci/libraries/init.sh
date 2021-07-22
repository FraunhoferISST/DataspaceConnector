#!/usr/bin/env bash

set -eo pipefail

export FILE_PATH=./scripts/ci/libraries/

. ${FILE_PATH}libs.sh

function init::setup_env_vars() {
    export TEST_FAILURES=0
    export TEST_SUCCESS=0
    export COLOR_DEFAULT=$'\e[0m'
    export COLOR_GREEN=$'\e[32m'
    export COLOR_RED=$'\e[31m'
    export LINE_BREAK_STAR="********************************************************************************"
    export LINE_BREAK_DASH="--------------------------------------------------------------------------------"
}

init::setup_env_vars
