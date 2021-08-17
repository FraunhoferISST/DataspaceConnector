#!/usr/bin/env bash

function dsc::setup_build_folder() {
    export PROVIDER_CHART=$BUILD_FOLDER/$TEST_SUITE/provider
    export CONSUMER_CHART=$BUILD_FOLDER/$TEST_SUITE/consumer

    # Provider setup
    rm -r -f $PROVIDER_CHART
    mkdir -p $PROVIDER_CHART
    cp -r charts $PROVIDER_CHART
    sed -i "s/^appVersion:.*$/appVersion: $PROVIDER_VERSION/" $PROVIDER_CHART/charts/dataspace-connector/Chart.yaml

    # Consumer setup
    rm -r -f $CONSUMER_CHART
    mkdir -p $CONSUMER_CHART
    cp -r charts $CONSUMER_CHART
    sed -i "s/^appVersion:.*$/appVersion: $CONSUMER_VERSION/" $CONSUMER_CHART/charts/dataspace-connector/Chart.yaml
}

function dsc::cleanup_build_folder() {
    rm -r -f $PROVIDER_CHART
    rm -r -f $CONSUMER_CHART
}

function dsc::run_provider_consumer_test() {
    test::reset_test_suit
    echo "$LINE_BREAK_STAR"

    dsc::setup_build_folder

    echo "Runnning test suite: $TEST_SUITE"
    echo "Setup provider ($PROVIDER_VERSION) and consumer ($CONSUMER_VERSION)"

    # Provider setup
    helm install provider "$PROVIDER_CHART"/charts/dataspace-connector --set env.config.SPRING_APPLICATION_NAME="Provider Connector" 2>&1 > /dev/null

    # Consumer setup
    helm install consumer "$CONSUMER_CHART"/charts/dataspace-connector --set env.config.SPRING_APPLICATION_NAME="Consumer Connector" 2>&1 > /dev/null

    echo "Waiting for readiness"
    kubectl rollout status deployments/provider-dataspace-connector --timeout=360s 2>&1 > /dev/null
    kubectl rollout status deployments/consumer-dataspace-connector --timeout=60s  2>&1 > /dev/null

    # Make sure the deployments are really ready and the rollout did not just timeout
    kubectl wait --for=condition=available deployments/provider-dataspace-connector --timeout=1s 2>&1 > /dev/null
    kubectl wait --for=condition=available deployments/consumer-dataspace-connector --timeout=1s 2>&1 > /dev/null

    echo "Exposing services to localhost"
    export PROVIDER_POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=dataspace-connector,app.kubernetes.io/instance=provider" -o jsonpath="{.items[0].metadata.name}")
    export PROVIDER_CONTAINER_PORT=$(kubectl get pod --namespace default $PROVIDER_POD_NAME -o jsonpath="{.spec.containers[0].ports[0].containerPort}")
    export CONSUMER_POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=dataspace-connector,app.kubernetes.io/instance=consumer" -o jsonpath="{.items[0].metadata.name}")
    export CONSUMER_CONTAINER_PORT=$(kubectl get pod --namespace default $CONSUMER_POD_NAME -o jsonpath="{.spec.containers[0].ports[0].containerPort}")

    kubectl port-forward "$PROVIDER_POD_NAME" 8080:"$PROVIDER_CONTAINER_PORT" 2>&1 > /dev/null &
    kubectl port-forward "$CONSUMER_POD_NAME" 8081:"$CONSUMER_CONTAINER_PORT" 2>&1 > /dev/null &

    # Give the port-forwarding some time
    sleep 5s

    echo "$LINE_BREAK_DASH"
    test::run_test_suite
    test::report_test_runs
    echo "$LINE_BREAK_DASH"

    echo "Cleanup"
    helm uninstall provider 2>&1 > /dev/null
    helm uninstall consumer 2>&1 > /dev/null
    # Stop port forwarding
    pkill -f "port-forward"

    dsc::cleanup_build_folder
}
