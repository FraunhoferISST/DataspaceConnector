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

function dsc::run_provider_consumer_test() {
    test::reset_test_suit
    echo "$LINE_BREAK_STAR"

    echo "Runnning test suite: $TEST_SUITE"
    echo "Setup provider ($PROVIDER_VERSION) and consumer ($CONSUMER_VERSION)"

    # Set the pull policy to IfNotPresent so that the postgres image is loaded but the dsc image only read from local registry

    # Provider setup
    helm install provider ./charts/dataspace-connector --set image.pullPolicy=IfNotPresent --set image.tag="${PROVIDER_VERSION}" --set env.config.SPRING_APPLICATION_NAME="Provider Connector" 2>&1 > /dev/null

    # Consumer setup
    helm install consumer ./charts/dataspace-connector --set image.pullPolicy=IfNotPresent --set image.tag="${CONSUMER_VERSION}" --set env.config.SPRING_APPLICATION_NAME="Consumer Connector" 2>&1 > /dev/null

    # Backend setup
    helm install flask ./charts/tests/route-backend --set image.pullPolicy=Never 2>&1 > /dev/null

    echo "Waiting for readiness"
    kubectl rollout status deployments/provider-dataspace-connector --timeout=360s 2>&1 > /dev/null
    kubectl rollout status deployments/consumer-dataspace-connector --timeout=60s  2>&1 > /dev/null
    kubectl rollout status deployments/flask-route-backend --timeout=60s  2>&1 > /dev/null

    # Make sure the deployments are really ready and the rollout did not just timeout
    kubectl wait --for=condition=available deployments/provider-dataspace-connector --timeout=1s 2>&1 > /dev/null
    kubectl wait --for=condition=available deployments/consumer-dataspace-connector --timeout=1s 2>&1 > /dev/null
    kubectl wait --for=condition=available deployments/flask-route-backend --timeout=1s 2>&1 > /dev/null

    # Give the port-forwarding some time
    sleep 5s

    echo "$LINE_BREAK_DASH"
    test::run_test_suite
    test::report_test_runs
    echo "$LINE_BREAK_DASH"

    echo "Cleanup"
    helm uninstall provider 2>&1 > /dev/null
    helm uninstall consumer 2>&1 > /dev/null
    helm uninstall flask 2>&1 > /dev/null
}
