# dataspace-connector

![Version: 0.3.2](https://img.shields.io/badge/Version-0.3.1-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square) ![AppVersion: 7.0.3](https://img.shields.io/badge/AppVersion-7.0.3-informational?style=flat-square)

A Helm chart for Kubernetes

## Requirements

| Repository | Name | Version |
|------------|------|---------|
| https://charts.bitnami.com/bitnami | postgresql | 10.4.6 |

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| affinity | object | `{}` |  |
| autoscaling | object | `{"enabled":false,"maxReplicas":100,"minReplicas":1,"targetCPUUtilizationPercentage":80}` | Connector horizontal autoscaling settings |
| env | object | `{"config":{"CONFIGURATION_PATH":"/etc/dataspace-connector/config.json","LOGGING_CONFIG":"file:///etc/dataspace-connector/log4j2.xml","MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE":"metrics,health","MANAGEMENT_ENDPOINT_HEALTH_ENABLED":"true","MANAGEMENT_ENDPOINT_METRICS_ENABLED":"true","SERVER_SSL_ENABLED":"false","SERVER_SSL_KEY-STORE":"/var/run/certs/keystore.p12"},"flyway":{"SPRING_FLYWAY_BASELINE-ON-MIGRATE":"false","SPRING_FLYWAY_BASELINE-VERSION":"7.0.0","SPRING_FLYWAY_ENABLED":"false","SPRING_JPA_HIBERNATE_DDL-AUTO":"update"},"secrets":{"SPRING_SECURITY_USER_NAME":"admin","SPRING_SECURITY_USER_PASSWORD":"password"}}` | Connector properties |
| env.config | object | `{"CONFIGURATION_PATH":"/etc/dataspace-connector/config.json","LOGGING_CONFIG":"file:///etc/dataspace-connector/log4j2.xml","MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE":"metrics,health","MANAGEMENT_ENDPOINT_HEALTH_ENABLED":"true","MANAGEMENT_ENDPOINT_METRICS_ENABLED":"true","SERVER_SSL_ENABLED":"false","SERVER_SSL_KEY-STORE":"/var/run/certs/keystore.p12"}` | Connector environment variables |
| env.config.CONFIGURATION_PATH | string | `"/etc/dataspace-connector/config.json"` | Path to the connector configuration |
| env.config.LOGGING_CONFIG | string | `"file:///etc/dataspace-connector/log4j2.xml"` | Connector logging configuration location |
| env.config.MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE | string | `"metrics,health"` | Actuactor endpoints to expose |
| env.config.MANAGEMENT_ENDPOINT_HEALTH_ENABLED | string | `"true"` | Whether to enable health actuactor |
| env.config.MANAGEMENT_ENDPOINT_METRICS_ENABLED | string | `"true"` | Whether to enable metrics actuactor |
| env.config.SERVER_SSL_ENABLED | string | `"false"` | Whether TLS is enabled |
| env.config.SERVER_SSL_KEY-STORE | string | `"/var/run/certs/keystore.p12"` | SSL keystore location |
| env.secrets | object | `{"SPRING_SECURITY_USER_NAME":"admin","SPRING_SECURITY_USER_PASSWORD":"password"}` | Connector secrets |
| env.secrets.SPRING_SECURITY_USER_NAME | string | `"admin"` | Connector admin username |
| env.secrets.SPRING_SECURITY_USER_PASSWORD | string | `"password"` | Connector admin password |
| image.pullPolicy | string | `"Always"` | Image pull policy |
| image.repository | string | `"ghcr.io/international-data-spaces-association/dataspace-connector"` | Connector image name |
| image.tag | string | `""` | Connector version without the "v" |
| imagePullSecrets | list | `[]` | Secrets for pulling images |
| ingress | object | `{"annotations":null,"className":"","enabled":false,"hosts":[{"host":"localhost","paths":[{"path":"/","pathType":"Prefix"}]}],"tls":[{"hosts":["localhost"],"secretName":"testsecret-tls"}]}` | Connector kubernetes ingress settings |
| ingress.enabled | bool | `false` | Whether to enable ingress for the connector |
| nodeSelector | object | `{}` |  |
| podAnnotations | object | `{"seccomp.security.alpha.kubernetes.io/pod":"runtime/default"}` | Annotation for the deployed pods |
| podSecurityContext | object | `{}` | Security context for the pods |
| postgresql | object | `{"enabled":true,"postgresqlDatabase":"test","postgresqlPassword":"username","postgresqlUsername":"password","service":{"port":"5432"}}` | Persistent database properties |
| postgresql.enabled | bool | `true` | Whether to use a postgresql backend |
| postgresql.postgresqlDatabase | string | `"test"` | Postgresql database name |
| postgresql.postgresqlPassword | string | `"username"` | Postgresql password |
| postgresql.postgresqlUsername | string | `"password"` | Postgresql username |
| postgresql.service | object | `{"port":"5432"}` | Kubernetes postgresql service |
| postgresql.service.port | string | `"5432"` | Postgresql service port |
| replicaCount | int | `1` | Number of connector instances |
| resources | object | `{"limits":{"cpu":"8","memory":"4Gi"},"requests":{"cpu":"250m","memory":"1Gi"}}` | Connector kubernetes resource settings |
| securityContext | object | `{"allowPrivilegeEscalation":false,"capabilities":{"drop":["ALL"]},"runAsUser":65532}` | Security context applied to the pods |
| securityContext.allowPrivilegeEscalation | bool | `false` | Whether to allow privilege escalations |
| securityContext.capabilities | object | `{"drop":["ALL"]}` | Capabilities of the pods |
| securityContext.runAsUser | int | `65532` | User running the pods |
| service | object | `{"port":80,"type":"ClusterIP"}` | Kubernetes connector service settings |
| service.port | int | `80` | Connector service port |
| service.type | string | `"ClusterIP"` | Connector service type |
| serviceAccount | object | `{"annotations":{},"create":true,"name":null}` | Kubernetes service account for the connector |
| serviceAccount.annotations | object | `{}` | Annotations to add to the service account |
| serviceAccount.create | bool | `true` | Specifies whether a service account should be created |
| serviceAccount.name | string | `nil` | The name of the service account to use. If not set and create is true, a name is generated using the fullname template |
| tolerations | list | `[]` |  |

----------------------------------------------
Autogenerated from chart metadata using [helm-docs v1.7.0](https://github.com/norwoodj/helm-docs/releases/v1.7.0)
