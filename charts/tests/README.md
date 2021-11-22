# E2E Tests

There are multiple test scripts for e2e tests between Dataspace Connectors. Each script tests a
different functionality (e.g. Camel routes, different usage policies) to ensure that it
works as expected. Some of these tests require additional components. This directory
contains the Helm charts for these additional components required in the e2e test infrastructure.

This *README* gives a short overview over the different tests scripts and the additional
components. It also describes how to run the e2e tests yourself.

## Components
Next to the Dataspace Connector instances, two additional components are required for the e2e
tests: a route backend and a runner.

### Route backend
The backend is a simple flask server which offers `GET` and `POST` endpoints. In the tests,
these endpoints are called through Camel routes to fetch or dispatch data.

The code and dockerfile for the backend can be found [here](../../scripts/tests/route-backend).

### Runner
The runner is the component that executes the tests. It's a Python Docker image which contains all
e2e test scripts. The entrypoint for the Docker image can be set to any of the scripts to run a
specific test script.

The test scripts and dockerfile for the runner can be found [here](../../scripts/tests/runner).

## Test scripts

There are currently 14 [test scripts](../../scripts/tests/runner/scripts):

| Name | Action |
| ---- | ------ |
| contract_negotiation_allow_access | Creates a complete resource on provider side with the allow-access policy, which poses no restrictions on data usage, attached. Requests this resource via the consumer. Verifies that the expected data has been received. |
| contract_negotiation_connector_restricted_access | Creates a complete resource on provider side with the connector-restricted-usage policy, which restricts the data usage to only the consumer connector, attached. Requests this resource via the consumer. Verifies that the expected data has been received. |
| contract_negotiation_count_access | Creates a complete resource on provider side with the count-access policy, which only allows using the data twice, attached. Requests this resource via the consumer. Accesses the data twice and verifies that the expected data has been received. Verifies that a third try to access the data results in an error message. |
| contract_negotiation_log_access | Creates a complete resource on provider side with the log-access policy, which requires logging data usage to the Clearing House, attached. Requests this resource via the consumer. Verifies that the expected data has been received. |
| contract_negotiation_notify_access | Creates a complete resource on provider side with the notify-access policy, which requires notifying the provider about data usage, attached. Requests this resource via the consumer. Verifies that the expected data has been received. |
| contract_negotiation_prohibit_access | Creates a complete resource on provider side with the prohibit-access policy, which prohibits all data usage, attached. Requests this resource via the consumer. Verifies that the try to access the data results in an error message. |
| contract_negotiation_security_level_access | Creates a complete resource on provider side with the security-level-access policy, which restricts the data usage to connectors with the TRUST security profile, attached. Requests this resource via the consumer. Verifies that the try to access the data results in an error message. |
| data_transfer_with_routes | Creates a route on provider side that fetches data from the route backend. Creates a route on consumer side that sends data to the route backend. Creates a complete resource on provider side, where the artifact points to the provider route. Requests this resource via the consumer and accesses the data using the consumer route. Verifies that the expected data has been received, which also means that the data was successfully dispatched via the route. |
| multiple_artifacts | Creates two complete resources on provider side, one with a remote and one with a local artifact. Negotiates a contract for both resources at once via the consumer. |
| remote_data_can_be_updated | Creates and then updates an artifact that points to a remote data source. |
| remote_data_is_unique | Creates two resources linked to the same contract and representation on provider side. Requests both resources via the consumer. Verifies that the representation, artifact, contract and rule were only persisted once on the consumer side (no duplication). |
| single_artifact_multiple_policies | Creates a full resource on provider side with two different usage rules attached to the contract. Negotiates a contract including both rules via the consumer. |
| subscription_creation | Creates a complete resource on provider side. Subscribes for updates on that resource via the consumer. Verifies that the subscription has been created on provider side. |
| subscription_with_route | Creates a complete resource on provider side. Subscribes for updates on the resource's artifact via the consumer. Creates a route on consumer side that sends data to the route backend. Creates a subscription on consumer side that sends updates via the route. Updates the artifact's data on provider side and verifies that the backend has received the updated data. |

## Running e2e tests
The e2e tests are run before each merge through the GitHub actions, but you can also run them
locally.

### Cluster instances

If you want to run the e2e tests in a local Kubernetes cluster, you can do so by using an
automated script that runs all tests or by deploying the components manually and executing
a single test.

#### Requirements
In order to deploy the e2e test infrastructure in Kubernetes, a cluster needs to be running
beforehand. If you do not have a cluster yet, you can use e.g.
[kind](https://kind.sigs.k8s.io/docs/user/quick-start/) to set up one.

Next to a cluster, a [Helm](https://helm.sh/) installation is also required, as Helm charts
are used for deploying the different components. If you do not have Helm installed yet, have
a look at the [installation guide](https://helm.sh/docs/intro/install/).

#### Automated
The simplest way to run the e2e tests is to execute
[this](../../scripts/ci/e2e/test-provider-consumer.sh) script. It will deploy two connector
instances and the backend in your local cluster, and then run the runner as a Kubernetes job
once for each test script. The results of the tests will be printed to the console.

#### Manual
You can also deploy the components via Helm manually in order to run single tests. The commands
for deploying the connector and backend can be found [here](../../scripts/ci/libraries/dsc.sh)
and should be executed in the project's root directory.

Once the connectors and backend are running, start the runner job by executing:

```
helm install e2e charts/tests/runner --set entrypoint="contract_negotiation_allow_access.py"
```

The value `entrypoint` defines the script that will be run by the job. Therefore, set this
to the name of the test script that you want to execute. All valid values can be found
[here](../../scripts/ci/e2e/active-tests.txt)

### Local instaces
You can also run the [test scripts](../../scripts/tests/runner/scripts) against a local instance
of the connector. Just start a connector and execute one of the scripts directly using Python.
Make sure to change the `provider_url`, `consumer_url` and `backend_url` declared at the beginning
of the script to your localhost URI beforehand.

Two test scripts - `data_transfer_with_routes.py` and `subscription_with_route.py` - require
the route backend to be running for successful completion. If you want to run these two tests
locally, go to `scripts/test/route-backend` and start the application by running:

```
export FLASK_APP=backend_server
export FLASK_ENV=development
flask run
```
