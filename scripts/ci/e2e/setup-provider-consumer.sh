#!/bin/bash
#
# Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
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

echo "Setup provider and consumer"

# Consumer setup
helm install consumer charts/dataspace-connector --set env.config.SPRING_APPLICATION_NAME="Consumer Connector"

# Provider setup
#sed -i "s/^appVersion:.*$/appVersion: ci/" charts/dataspace-connector/Chart.yaml
helm install provider charts/dataspace-connector --set env.config.SPRING_APPLICATION_NAME="Producer Connector"

echo "Waiting for readiness"
kubectl rollout status deployments/provider-dataspace-connector
kubectl rollout status deployments/consumer-dataspace-connector

echo "Exposing services to localhost"
export PROVIDER_POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=dataspace-connector,app.kubernetes.io/instance=provider" -o jsonpath="{.items[0].metadata.name}")
export PROVIDER_CONTAINER_PORT=$(kubectl get pod --namespace default $PROVIDER_POD_NAME -o jsonpath="{.spec.containers[0].ports[0].containerPort}")
export CONSUMER_POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=dataspace-connector,app.kubernetes.io/instance=consumer" -o jsonpath="{.items[0].metadata.name}")
export CONSUMER_CONTAINER_PORT=$(kubectl get pod --namespace default $CONSUMER_POD_NAME -o jsonpath="{.spec.containers[0].ports[0].containerPort}")

kubectl port-forward $PROVIDER_POD_NAME 8080:$PROVIDER_CONTAINER_PORT &
kubectl port-forward $CONSUMER_POD_NAME 8081:$CONSUMER_CONTAINER_PORT &

echo "Provider name: $PROVIDER_POD_NAME"
echo "Provider port: $PROVIDER_CONTAINER_PORT"
echo "Consumer name: $CONSUMER_POD_NAME"
echo "Consumer port: $CONSUMER_CONTAINER_PORT"

echo "Run scripts"
echo "
********************************************************************************
Testing contract_negotation_allow_access.py
********************************************************************************
"
chmod +x ./scripts/tests/contract_negotation_allow_access.py
./scripts/tests/contract_negotation_allow_access.py "http://provider-dataspace-connector" "http://consumer-dataspace-connector"

echo "
********************************************************************************
Testing multiple_artifacts.py
********************************************************************************
"
chmod +x ./scripts/tests/multiple_artifacts.py
./scripts/tests/multiple_artifacts.py "http://provider-dataspace-connector" "http://consumer-dataspace-connector"

echo "
********************************************************************************
Testing single_artifact_multiple_policies.py
********************************************************************************
"
chmod +x ./scripts/tests/single_artifact_multiple_policies.py
./scripts/tests/single_artifact_multiple_policies.py "http://provider-dataspace-connector" "http://consumer-dataspace-connector"

echo "Cleanup"
#helm uninstall provider
#helm uninstall consumer
