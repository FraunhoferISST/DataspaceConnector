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

import requests
import sys

from configurationapi import ConfigurationAPI

providerUrl = "http://localhost:8080"

provider_alias = "http://provider-dataspace-connector"


def main(argv):
    if len(argv) == 2:
        provider_alias = argv[0]
        print("Setting provider alias as:", provider_alias)


if __name__ == "__main__":
    main(sys.argv[1:])

print("Starting script")

# Suppress ssl verification warning
requests.packages.urllib3.disable_warnings()

# Get Configuration API
provider = ConfigurationAPI(providerUrl)
configuration = provider.get_configuration()

# Check Status Code
if configuration.status_code == 200:
    exit(0)
else:
    exit(1)
