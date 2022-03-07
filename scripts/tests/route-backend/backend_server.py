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

from flask import Flask
from flask import request
from flask import json

app = Flask(__name__)

update_received = [0]

@app.route("/get")
def get_data():
    return "data string"

@app.route("/post", methods = ['POST'])
def post_data():
    data = (request.get_data().decode("ascii"))
    print(data)
    if data == "data string":
        return "Success", 200
    else:
        return "Unexpected data received.", 400

@app.route("/subscription", methods = ['POST'])
def subscription():
    data = (request.get_data().decode("ascii"))
    print(data)
    if data == "data string":
        update_received[0] = True
        return "Success", 200
    else:
        update_received[0] = False
        return "Unexpected data received.", 400

@app.route("/subscription", methods = ['GET'])
def is_update_received():
    return json.dumps(update_received[0]), 200

