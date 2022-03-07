#!/usr/bin/env bash
#
# Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

function test::reset_test_suit() {
    export TEST_SUCCESS=0
    export TEST_FAILURES=0
}

function test::exit() {
    if [ "$TEST_SUITE_FAILURES" -gt 0 ]; then
        exit 1
    fi
}

function test::run_test_suite() {
    while read -r INPUT; do
        export CURRENT_TEST_SCRIPT=$INPUT
        test::run_test_script
    done <./scripts/ci/e2e/active-tests.txt

    if [ "$TEST_FAILURES" -gt 0 ]; then
        export TEST_SUITE_FAILURES=$((TEST_SUITE_FAILURES+1))
    fi
}

function test::wait_for_finish() {
    done=0

    until [ $done -eq 1 ]
    do
        failed=0
        success=0
        if [ "$(kubectl get jobs test-runner -o jsonpath='{.status.conditions[?(@.type=="Failed")].status}')" == "True" ]; then
            failed=1
            export RUNNER_EXIT_CODE=1
        fi
        if [ "$(kubectl get jobs test-runner -o jsonpath='{.status.conditions[?(@.type=="Complete")].status}')" == "True" ]; then
            success=1
            export RUNNER_EXIT_CODE=0
        fi

        if [[ $failed -eq 1 ]] || [[ $success -eq 1 ]]; then
            done=1
        fi
    done
}

function test::run_test_script() {
    helm install e2e charts/tests/runner --set entrypoint="${CURRENT_TEST_SCRIPT}" 2>&1 >/dev/null
    sleep 1
    test::wait_for_finish

    if [ "$RUNNER_EXIT_CODE" -eq 0 ]; then
        export TEST_SUCCESS=$((TEST_SUCCESS+1))
        echo "${COLOR_GREEN}PASSED${COLOR_DEFAULT} $CURRENT_TEST_SCRIPT"
    else
        echo "$LINE_BREAK_STAR"
        export TEST_FAILURES=$((TEST_FAILURES+1))
        echo "${COLOR_RED}FAILED${COLOR_DEFAULT} $CURRENT_TEST_SCRIPT"
        echo "$LINE_BREAK_STAR"
        kubectl logs -l job-name=test-runner --tail=1000
        echo "$LINE_BREAK_STAR"
    fi
    set -eo pipefail
    helm uninstall e2e 2>&1 >/dev/null
}

function test::report_test_runs() {
    echo "$LINE_BREAK_STAR"
    echo "${COLOR_GREEN}SUCCESSES${COLOR_DEFAULT}: ${TEST_SUCCESS} ${COLOR_RED}Failures${COLOR_DEFAULT}: ${TEST_FAILURES}"
}
