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

set -eo pipefail

# shellcheck source=scripts/ci/libraries/init.sh
. ./scripts/ci/libraries/init.sh

# Get the last release tag
LAST_RELEASE=$(git::get_last_tag)
LAST_RELEASE=${LAST_RELEASE:1:${#LAST_RELEASE}}
export LAST_RELEASE

CURRENT_BUILD=ci
export CURRENT_BUILD

## TEST 1
export TEST_SUITE=new-version
export PROVIDER_VERSION=$CURRENT_BUILD
export CONSUMER_VERSION=$CURRENT_BUILD
dsc::run_provider_consumer_test

## TEST 2
export TEST_SUITE=old-provider
export PROVIDER_VERSION=$LAST_RELEASE
export CONSUMER_VERSION=$CURRENT_BUILD
dsc::run_provider_consumer_test

## TEST 3
export TEST_SUITE=old-consumer
export PROVIDER_VERSION=$CURRENT_BUILD
export CONSUMER_VERSION=$LAST_RELEASE
dsc::run_provider_consumer_test

# Crash if test failures exists
test::exit
