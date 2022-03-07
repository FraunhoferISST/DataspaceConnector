#!/usr/bin/env python3
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

from resourceapi import ResourceApi

connector = ResourceApi("http://provider-dataspace-connector")
artifact = connector.create_artifact(
    data={
        "title": "string",
        "description": "string",
        "accessUrl": "https://string",
        "basicAuth": {"key": "string", "value": "string"},
        "apiKey": {"key": "string", "value": "string"},
        "value": "string",
        "automatedDownload": True,
    }
)

success = connector.update_artifact(
    artifact=artifact,
    data={
        "title": "string",
        "description": "string",
        "accessUrl": "https://string",
        "basicAuth": {"key": "string", "value": "string"},
        "apiKey": {"key": "string", "value": "string"},
        "value": "string",
        "automatedDownload": True,
    },
)

exit(not success)  # Exit success == 0 but True == 1
