#!/usr/bin/env bash
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

echo "
********************************************************************************
Testing contract_negotation_allow_access.py
********************************************************************************
"
chmod +x ./scripts/tests/contract_negotation_allow_access.py
./scripts/tests/contract_negotation_allow_access.py "http://provider-dataspace-connector" "http://consumer-dataspace-connector"

echo "
********************************************************************************
Testing contract_negotation_connector_restricted_access.py
********************************************************************************
"
chmod +x ./scripts/tests/contract_negotation_connector_restricted_access.py
./scripts/tests/contract_negotation_connector_restricted_access.py "http://provider-dataspace-connector" "http://consumer-dataspace-connector"

echo "
********************************************************************************
Testing contract_negotation_count_access.py
********************************************************************************
"
chmod +x ./scripts/tests/contract_negotation_count_access.py
./scripts/tests/contract_negotation_count_access.py "http://provider-dataspace-connector" "http://consumer-dataspace-connector"

echo "
********************************************************************************
Testing contract_negotation_log_access.py
********************************************************************************
"
chmod +x ./scripts/tests/contract_negotation_log_access.py
./scripts/tests/contract_negotation_log_access.py "http://provider-dataspace-connector" "http://consumer-dataspace-connector"

echo "
********************************************************************************
Testing contract_negotation_notify_access.py
********************************************************************************
"
chmod +x ./scripts/tests/contract_negotation_notify_access.py
./scripts/tests/contract_negotation_notify_access.py "http://provider-dataspace-connector" "http://consumer-dataspace-connector"

echo "
********************************************************************************
Testing contract_negotation_prohibit_access.py
********************************************************************************
"
chmod +x ./scripts/tests/contract_negotation_prohibit_access.py
./scripts/tests/contract_negotation_prohibit_access.py "http://provider-dataspace-connector" "http://consumer-dataspace-connector"

echo "
********************************************************************************
Testing contract_negotation_security_level_access.py
********************************************************************************
"
chmod +x ./scripts/tests/contract_negotation_security_level_access.py
./scripts/tests/contract_negotation_security_level_access.py "http://provider-dataspace-connector" "http://consumer-dataspace-connector"

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
