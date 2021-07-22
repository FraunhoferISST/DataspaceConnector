#!/usr/bin/env bash

function test::run_test_script() {
    chmod +x ${CURRENT_TEST_SCRIPT}
    set +e
    SCRIPT_OUTPUT=$(${CURRENT_TEST_SCRIPT} "http://provider-dataspace-connector" "http://consumer-dataspace-connector" 2>&1)
    if [ $? -eq 0 ]; then
        export TEST_SUCCESS=$(($TEST_SUCCESS+1))
        echo "${COLOR_GREEN}PASSED${COLOR_DEFAULT} $CURRENT_TEST_SCRIPT"
    else
        echo "$LINE_BREAK_STAR"
        export TEST_FAILURES=$(($TEST_FAILURES+1))
        echo "${COLOR_RED}FAILED${COLOR_DEFAULT} $CURRENT_TEST_SCRIPT"
        echo "$LINE_BREAK_STAR"
        echo ${SCRIPT_OUTPUT}
        echo "$LINE_BREAK_STAR"
    fi
    set -eo pipefail
}

function test::evaluate_test_runs() {
    echo "$LINE_BREAK_STAR"
    echo "${COLOR_GREEN}SUCCESSES${COLOR_DEFAULT}: ${TEST_SUCCESS} ${COLOR_RED}Failures${COLOR_DEFAULT}: ${TEST_FAILURES}"
}
