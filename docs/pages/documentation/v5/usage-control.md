---
layout: default
title: Usage Control
nav_order: 5
description: ""
permalink: /Documentation/v5/UsageControl
parent: Version 5
grand_parent: Documentation
---

# IDS Usage Control Policies
{: .fs-9 }

Usage policies are an important aspect of IDS, further details are explained on this page.
{: .fs-6 .fw-300 }

---

The Dataspace Connector supports usage policies written
in the `IDS Usage Control Language` based on [ODRL](https://www.w3.org/TR/odrl-model/#policy).

"An IDS Contract is implicitly divided to two main sections: the contract specific metadata and the
`IDS Usage Control Policy` of the contract.
The contract specific information (e.g., date when the contract has been issued or references to the
sensitive information about the involved parties) has no effect on the enforcement. However, the
`IDS Usage Control Policy` is the key motive of organizational and technical Usage Control
enforcement.
Furthermore, an `IDS Usage Control Policy` contains several Data Usage Control statements (e.g.,
permissions, prohibitions and obligations) called `IDS Rules` and is specified in the `IDS Usage
Control Language` which is a technology independent language. The technically enforceable rules
shall be transformed to a technology dependent policy (e.g., MYDATA) to facilitate the Usage Control
enforcement of data sovereignty." (p.22, [IDSA Position Paper Usage Control in the IDS](https://internationaldataspaces.org/download/21053/))

## Policy Patterns

Following the specifications of the IDSA Position Paper about Usage Control, the IDS defines 21
policy classes. The Dataspace Connector currently implements eight of these.

Examples for each of them can be found by using the endpoint `POST /api/examples/policy`.
The usage policy is added to the metadata of a resource. The classes at
`io.dataspaceconnector.service.usagecontrol` read, classify, verify, and enforce the policies at
runtime.

| No. | Title                                          | Support | Implementation |
|:----|:-----------------------------------------------|:-------:|:-------|
| 1   | Allow the Usage of the Data                    | x       | provides data usage without any restrictions
| 2   | Connector-restricted Data Usage                | x       | allows data usage for a specific connector
| 3   | Application-restricted Data Usage              | -       |
| 4   | Interval-restricted Data Usage                 | x       | provides data usage within a specified time interval
| 5   | Duration-restricted Data Usage                 | x       | allows data usage for a specified time period
| 6   | Location Restricted Policy                     | -       |
| 7   | Perpetual Data Sale (Payment once)             | -       |
| 8   | Data Rental (Payment frequently)               | -       |
| 9   | Role-restricted Data Usage                     | -       |
| 10  | Purpose-restricted Data Usage Policy           | -       |
| 11  | Event-restricted Usage Policy                  | -       |
| 12  | Restricted Number of Usages                    | x       | allows data usage for n times
| 13  | Security Level Restricted Policy               | -       |
| 14  | Use Data and Delete it After                   | x       | allows data usage within a specified time interval with the restriction to delete it at a specified time stamp
| 15  | Modify Data (in Transit)                       | -       |
| 16  | Modify Data (in Rest)                          | -       |
| 17  | Local Logging                                  | x       | allows data usage if logged to the Clearing House
| 18  | Remote Notifications                           | x       | allows data usage with notification message
| 19  | Attach Policy when Distribute to a Third-party | -       |
| 20  | Distribute only if Encrypted                   | -       |
| 21  | State Restricted Policy                        | -       |

## Pattern Examples

### Provide Access
```json
{
  "@context" : {
    "ids" : "https://w3id.org/idsa/core/",
    "idsc" : "https://w3id.org/idsa/code/"
  },
  "@type" : "ids:Permission",
  "@id" : "https://w3id.org/idsa/autogen/permission/14af94cf-2a29-4ddd-8595-945d9a16be4f",
  "ids:description" : [ {
    "@value" : "provide-access",
    "@type" : "http://www.w3.org/2001/XMLSchema#string"
  } ],
  "ids:title" : [ {
    "@value" : "Allow Data Usage",
    "@type" : "http://www.w3.org/2001/XMLSchema#string"
  } ],
  "ids:action" : [ {
    "@id" : "idsc:USE"
  } ],
  "ids:target": [...]
}
```

### Prohibit Access
```json
{
  "@context" : {
    "ids" : "https://w3id.org/idsa/core/",
    "idsc" : "https://w3id.org/idsa/code/"
  },
  "@type" : "ids:Prohibition",
  "@id" : "https://w3id.org/idsa/autogen/prohibition/a838e2a5-d3e8-4891-af73-0f3bf39381ce",
  "ids:description" : [ {
    "@value" : "prohibit-access",
    "@type" : "http://www.w3.org/2001/XMLSchema#string"
  } ],
  "ids:title" : [ {
    "@value" : "Example Usage Policy",
    "@type" : "http://www.w3.org/2001/XMLSchema#string"
  } ],
  "ids:action" : [ {
    "@id" : "idsc:USE"
  } ],
  "ids:target": [...]
}
```

### N Times Usage
```json
{
  "@context" : {
    "ids" : "https://w3id.org/idsa/core/",
    "idsc" : "https://w3id.org/idsa/code/"
  },
  "@type" : "ids:Permission",
  "@id" : "https://w3id.org/idsa/autogen/permission/4ad88c11-a00c-4479-94f6-2a68cce005ea",
  "ids:description" : [ {
    "@value" : "n-times-usage",
    "@type" : "http://www.w3.org/2001/XMLSchema#string"
  } ],
  "ids:title" : [ {
    "@value" : "Example Usage Policy",
    "@type" : "http://www.w3.org/2001/XMLSchema#string"
  } ],
  "ids:action" : [ {
    "@id" : "idsc:USE"
  } ],
  "ids:constraint" : [ {
    "@type" : "ids:Constraint",
    "@id" : "https://w3id.org/idsa/autogen/constraint/a5d77dcd-f838-48e9-bdc1-4b219946f8ac",
    "ids:rightOperand" : {
      "@value" : "5",
      "@type" : "http://www.w3.org/2001/XMLSchema#double"
    },
    "ids:leftOperand" : {
      "@id" : "idsc:COUNT"
    },
    "ids:operator" : {
      "@id" : "idsc:LTEQ"
    }
  } ],
  "ids:target": [...]
}
```

### Duration Usage
```json
{
  "@context" : {
    "ids" : "https://w3id.org/idsa/core/",
    "idsc" : "https://w3id.org/idsa/code/"
  },
  "@type" : "ids:Permission",
  "@id" : "https://w3id.org/idsa/autogen/permission/3b1439a1-4136-4675-b5a0-798ec3148996",
  "ids:description" : [ {
    "@value" : "duration-usage",
    "@type" : "http://www.w3.org/2001/XMLSchema#string"
  } ],
  "ids:title" : [ {
    "@value" : "Example Usage Policy",
    "@type" : "http://www.w3.org/2001/XMLSchema#string"
  } ],
  "ids:action" : [ {
    "@id" : "idsc:USE"
  } ],
  "ids:constraint" : [ {
    "@type" : "ids:Constraint",
    "@id" : "https://w3id.org/idsa/autogen/constraint/b7d8beaf-0765-4d40-b2e9-4eddeda1c89b",
    "ids:rightOperand" : {
      "@value" : "PT1M30.5S",
      "@type" : "http://www.w3.org/2001/XMLSchema#duration"
    },
    "ids:leftOperand" : {
      "@id" : "idsc:ELAPSED_TIME"
    },
    "ids:operator" : {
      "@id" : "idsc:SHORTER_EQ"
    }
  } ],
  "ids:target": [...]
}
```

### Usage During Interval
```json
{
  "@context" : {
    "ids" : "https://w3id.org/idsa/core/",
    "idsc" : "https://w3id.org/idsa/code/"
  },
  "@type" : "ids:Permission",
  "@id" : "https://w3id.org/idsa/autogen/permission/1fcac0c3-8946-4880-a8cc-a7eab0543204",
  "ids:description" : [ {
    "@value" : "usage-during-interval",
    "@type" : "http://www.w3.org/2001/XMLSchema#string"
  } ],
  "ids:title" : [ {
    "@value" : "Example Usage Policy",
    "@type" : "http://www.w3.org/2001/XMLSchema#string"
  } ],
  "ids:action" : [ {
    "@id" : "idsc:USE"
  } ],
  "ids:constraint" : [ {
    "@type" : "ids:Constraint",
    "@id" : "https://w3id.org/idsa/autogen/constraint/28653654-3024-4435-8626-f1878de39c22",
    "ids:rightOperand" : {
      "@value" : "2020-07-11T00:00:00Z",
      "@type" : "http://www.w3.org/2001/XMLSchema#dateTimeStamp"
    },
    "ids:leftOperand" : {
      "@id" : "idsc:POLICY_EVALUATION_TIME"
    },
    "ids:operator" : {
      "@id" : "idsc:AFTER"
    }
  }, {
    "@type" : "ids:Constraint",
    "@id" : "https://w3id.org/idsa/autogen/constraint/c8408f4a-8c65-4894-a17d-4e3999bc0669",
    "ids:rightOperand" : {
      "@value" : "2020-07-11T00:00:00Z",
      "@type" : "http://www.w3.org/2001/XMLSchema#dateTimeStamp"
    },
    "ids:leftOperand" : {
      "@id" : "idsc:POLICY_EVALUATION_TIME"
    },
    "ids:operator" : {
      "@id" : "idsc:BEFORE"
    }
  } ],
  "ids:target": [...]
}
```

### Usage Until Deletion
```json
{
  "@context" : {
    "ids" : "https://w3id.org/idsa/core/",
    "idsc" : "https://w3id.org/idsa/code/"
  },
  "@type" : "ids:Permission",
  "@id" : "https://w3id.org/idsa/autogen/permission/98d47a9d-d4e2-4048-97c2-9c632f5e235f",
  "ids:description" : [ {
    "@value" : "usage-until-deletion",
    "@type" : "http://www.w3.org/2001/XMLSchema#string"
  } ],
  "ids:title" : [ {
    "@value" : "Example Usage Policy",
    "@type" : "http://www.w3.org/2001/XMLSchema#string"
  } ],
  "ids:postDuty" : [ {
    "@type" : "ids:Duty",
    "@id" : "https://w3id.org/idsa/autogen/duty/97b8cc94-fa44-4bed-8036-73d6bc4b69ab",
    "ids:action" : [ {
      "@id" : "idsc:DELETE"
    } ],
    "ids:constraint" : [ {
      "@type" : "ids:Constraint",
      "@id" : "https://w3id.org/idsa/autogen/constraint/90abcfe4-9901-4128-b787-c077a9bd363b",
      "ids:rightOperand" : {
        "@value" : "2020-07-11T00:00:00Z",
        "@type" : "http://www.w3.org/2001/XMLSchema#dateTimeStamp"
      },
      "ids:leftOperand" : {
        "@id" : "idsc:POLICY_EVALUATION_TIME"
      },
      "ids:operator" : {
        "@id" : "idsc:TEMPORAL_EQUALS"
      }
    } ]
  } ],
  "ids:action" : [ {
    "@id" : "idsc:USE"
  } ],
  "ids:constraint" : [ {
    "@type" : "ids:Constraint",
    "@id" : "https://w3id.org/idsa/autogen/constraint/3c218b76-7c32-4fd3-930f-69f728161096",
    "ids:rightOperand" : {
      "@value" : "2020-07-11T00:00:00Z",
      "@type" : "http://www.w3.org/2001/XMLSchema#dateTimeStamp"
    },
    "ids:leftOperand" : {
      "@id" : "idsc:POLICY_EVALUATION_TIME"
    },
    "ids:operator" : {
      "@id" : "idsc:AFTER"
    }
  }, {
    "@type" : "ids:Constraint",
    "@id" : "https://w3id.org/idsa/autogen/constraint/937684bd-0e81-44a5-b4e3-02664a1bf4c9",
    "ids:rightOperand" : {
      "@value" : "2020-07-11T00:00:00Z",
      "@type" : "http://www.w3.org/2001/XMLSchema#dateTimeStamp"
    },
    "ids:leftOperand" : {
      "@id" : "idsc:POLICY_EVALUATION_TIME"
    },
    "ids:operator" : {
      "@id" : "idsc:BEFORE"
    }
  } ],
  "ids:target": [...]
}
```

### Usage Logging
```json
{
  "@context" : {
    "ids" : "https://w3id.org/idsa/core/",
    "idsc" : "https://w3id.org/idsa/code/"
  },
  "@type" : "ids:Permission",
  "@id" : "https://w3id.org/idsa/autogen/permission/cbc802b1-03a7-4563-b6a3-688e9dd4ccdf",
  "ids:description" : [ {
    "@value" : "usage-logging",
    "@type" : "http://www.w3.org/2001/XMLSchema#string"
  } ],
  "ids:title" : [ {
    "@value" : "Example Usage Policy",
    "@type" : "http://www.w3.org/2001/XMLSchema#string"
  } ],
  "ids:postDuty" : [ {
    "@type" : "ids:Duty",
    "@id" : "https://w3id.org/idsa/autogen/duty/f022bf97-5601-4cb9-a241-638906100c18",
    "ids:action" : [ {
      "@id" : "idsc:LOG"
    } ]
  } ],
  "ids:action" : [ {
    "@id" : "idsc:USE"
  } ],
  "ids:target": [...]
}
```

### Usage Notification
```json
{
  "@context" : {
    "ids" : "https://w3id.org/idsa/core/",
    "idsc" : "https://w3id.org/idsa/code/"
  },
  "@type" : "ids:Permission",
  "@id" : "https://w3id.org/idsa/autogen/permission/75050633-0762-47d2-8a06-6a318eaf4b76",
  "ids:description" : [ {
    "@value" : "usage-notification",
    "@type" : "http://www.w3.org/2001/XMLSchema#string"
  } ],
  "ids:title" : [ {
    "@value" : "Example Usage Policy",
    "@type" : "http://www.w3.org/2001/XMLSchema#string"
  } ],
  "ids:postDuty" : [ {
    "@type" : "ids:Duty",
    "@id" : "https://w3id.org/idsa/autogen/duty/6d7cc949-cdea-495a-b88b-6d902ddd017c",
    "ids:action" : [ {
      "@id" : "idsc:NOTIFY"
    } ],
    "ids:constraint" : [ {
      "@type" : "ids:Constraint",
      "@id" : "https://w3id.org/idsa/autogen/constraint/0f940426-d83e-4d2c-a59f-9d5f17ad5f4d",
      "ids:rightOperand" : {
        "@value" : "https://localhost:8080/api/ids/data",
        "@type" : "http://www.w3.org/2001/XMLSchema#anyURI"
      },
      "ids:leftOperand" : {
        "@id" : "idsc:ENDPOINT"
      },
      "ids:operator" : {
        "@id" : "idsc:DEFINES_AS"
      }
    } ]
  } ],
  "ids:action" : [ {
    "@id" : "idsc:USE"
  } ],
  "ids:target": [...]
}
```

### Connector Restricted Usage
```json
{
  "@context" : {
    "ids" : "https://w3id.org/idsa/core/",
    "idsc" : "https://w3id.org/idsa/code/"
  },
  "@type" : "ids:Permission",
  "@id" : "https://w3id.org/idsa/autogen/permission/d504b82f-79dd-4c93-969d-937ab6a1d676",
  "ids:description" : [ {
    "@value" : "connector-restriction",
    "@type" : "http://www.w3.org/2001/XMLSchema#string"
  } ],
  "ids:title" : [ {
    "@value" : "Example Usage Policy",
    "@type" : "http://www.w3.org/2001/XMLSchema#string"
  } ],
  "ids:action" : [ {
    "@id" : "idsc:USE"
  } ],
  "ids:constraint" : [ {
    "@type" : "ids:Constraint",
    "@id" : "https://w3id.org/idsa/autogen/constraint/572c96ec-dd86-4b20-a849-a0ce8c255eee",
    "ids:rightOperand" : {
      "@value" : "https://example.com",
      "@type" : "http://www.w3.org/2001/XMLSchema#anyURI"
    },
    "ids:leftOperand" : {
      "@id" : "idsc:SYSTEM"
    },
    "ids:operator" : {
      "@id" : "idsc:SAME_AS"
    }
  } ],
  "ids:target": [...]
}
```
