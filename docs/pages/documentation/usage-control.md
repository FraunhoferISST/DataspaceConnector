---
layout: default
title: Usage Control
nav_order: 5
description: ""
permalink: /Documentation/UsageControl
parent: Documentation
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
enforcement of data sovereignty." (p.22, [IDSA Position Paper Usage Control in the IDS](https://internationaldataspaces.org/wp-content/uploads/IDSA-Position-Paper-Usage-Control-in-the-IDS-V3.0.pdf))

## Policy Patterns

Following the specifications of the IDSA Position Paper about Usage Control, the IDS defines 21 
policy classes. The Dataspace Connector currently implements eight of these.

Examples for each of them can be found by using the endpoint `POST /api/examples/policy`.
The usage policy is added to the metadata of a resource. The classes at
`io.dataspaceconnector.services.usagecontrol` read, classify, verify, and enforce the policies at
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
```
{
  "@type": "ids:Permission",
  "@id": "https://w3id.org/idsa/autogen/permission/770f890f-9ea1-4cd6-9f87-8d8d3f126188",
  "ids:target": [...],
  "ids:description": [
    {
      "@value": "provide-access",
      "@type": "http://www.w3.org/2001/XMLSchema#string"
    }
  ],
  "ids:title": [
    {
      "@value": "Allow Data Usage",
      "@type": "http://www.w3.org/2001/XMLSchema#string"
    }
  ],
  "ids:action": [
    {
      "@id": "idsc:USE"
    }
  ]
}
```

### Prohibit Access
```
{
  "@type": "ids:Prohibition",
  "@id": "https://w3id.org/idsa/autogen/prohibition/cc051cd8-061a-4169-a031-22ffb7706b7e",
  "ids:target": [...],
  "ids:description": [
    {
      "@value": "prohibit-access",
      "@type": "http://www.w3.org/2001/XMLSchema#string"
    }
  ],
  "ids:title": [
    {
      "@value": "Example Usage Policy",
      "@type": "http://www.w3.org/2001/XMLSchema#string"
    }
  ],
  "ids:action": [
    {
      "@id": "idsc:USE"
    }
  ]
}
```

### N Times Usage
```
{
  "@type": "ids:Permission",
  "@id": "https://w3id.org/idsa/autogen/permission/bf4de731-4320-4485-aed1-64735aa3e80c",
  "ids:target": [...],
  "ids:description": [
    {
      "@value": "n-times-usage",
      "@type": "http://www.w3.org/2001/XMLSchema#string"
    }
  ],
  "ids:title": [
    {
      "@value": "Example Usage Policy",
      "@type": "http://www.w3.org/2001/XMLSchema#string"
    }
  ],
  "ids:constraint": [
    {
      "@type": "ids:Constraint",
      "@id": "https://w3id.org/idsa/autogen/constraint/3d453647-a3c7-4bd8-840b-8f245991b635",
      "ids:rightOperand": {
        "@value": "5",
        "@type": "xsd:double"
      },
      "ids:leftOperand": {
        "@id": "idsc:COUNT"
      },
      "ids:operator": {
        "@id": "idsc:LTEQ"
      }
    }
  ],
  "ids:action": [
    {
      "@id": "idsc:USE"
    }
  ]
}
```

### Duration Usage
```
{
  "@type": "ids:Permission",
  "@id": "https://w3id.org/idsa/autogen/permission/7f27821b-dd4c-416a-a9f8-0b163709c4d0",
  "ids:target": [...],
  "ids:description": [
    {
      "@value": "duration-usage",
      "@type": "http://www.w3.org/2001/XMLSchema#string"
    }
  ],
  "ids:title": [
    {
      "@value": "Example Usage Policy",
      "@type": "http://www.w3.org/2001/XMLSchema#string"
    }
  ],
  "ids:constraint": [
    {
      "@type": "ids:Constraint",
      "@id": "https://w3id.org/idsa/autogen/constraint/aca6428e-01f5-4012-a598-0e03b02133c8",
      "ids:rightOperand": {
        "@value": "PT1M30.5S",
        "@type": "xsd:duration"
      },
      "ids:leftOperand": {
        "@id": "idsc:ELAPSED_TIME"
      },
      "ids:operator": {
        "@id": "idsc:SHORTER_EQ"
      }
    }
  ],
  "ids:action": [
    {
      "@id": "idsc:USE"
    }
  ]
}
```

### Usage During Interval
```
{
  "@type": "ids:Permission",
  "@id": "https://w3id.org/idsa/autogen/permission/d77fcb4e-9b84-4cd7-967c-fe56ec6d544c",
  "ids:target": [...],
  "ids:description": [
    {
      "@value": "usage-during-interval",
      "@type": "http://www.w3.org/2001/XMLSchema#string"
    }
  ],
  "ids:title": [
    {
      "@value": "Example Usage Policy",
      "@type": "http://www.w3.org/2001/XMLSchema#string"
    }
  ],
  "ids:constraint": [
    {
      "@type": "ids:Constraint",
      "@id": "https://w3id.org/idsa/autogen/constraint/52556f49-cc86-4074-b87f-9404acae4442",
      "ids:rightOperand": {
        "@value": "2020-07-11T00:00:00Z",
        "@type": "xsd:dateTimeStamp"
      },
      "ids:leftOperand": {
        "@id": "idsc:POLICY_EVALUATION_TIME"
      },
      "ids:operator": {
        "@id": "idsc:AFTER"
      }
    },
    {
      "@type": "ids:Constraint",
      "@id": "https://w3id.org/idsa/autogen/constraint/8c6937da-cfb2-4a86-be3e-19a2631bfcc0",
      "ids:rightOperand": {
        "@value": "2020-07-11T00:00:00Z",
        "@type": "xsd:dateTimeStamp"
      },
      "ids:leftOperand": {
        "@id": "idsc:POLICY_EVALUATION_TIME"
      },
      "ids:operator": {
        "@id": "idsc:BEFORE"
      }
    }
  ],
  "ids:action": [
    {
      "@id": "idsc:USE"
    }
  ]
}
```

### Usage Until Deletion
```
{
  "@type": "ids:Permission",
  "@id": "https://w3id.org/idsa/autogen/permission/5b4b134f-e821-4fff-9ec3-4819c2af1cea",
  "ids:target": [...],
  "ids:description": [
    {
      "@value": "usage-until-deletion",
      "@type": "http://www.w3.org/2001/XMLSchema#string"
    }
  ],
  "ids:title": [
    {
      "@value": "Example Usage Policy",
      "@type": "http://www.w3.org/2001/XMLSchema#string"
    }
  ],
  "ids:postDuty": [
    {
      "@type": "ids:Duty",
      "@id": "https://w3id.org/idsa/autogen/duty/7cb69671-0b6a-40d3-95f1-f94b3ed00810",
      "ids:constraint": [
        {
          "@type": "ids:Constraint",
          "@id": "https://w3id.org/idsa/autogen/constraint/a3adedd5-4da7-4d91-8351-93521874f26b",
          "ids:rightOperand": {
            "@value": "2020-07-11T00:00:00Z",
            "@type": "xsd:dateTimeStamp"
          },
          "ids:leftOperand": {
            "@id": "idsc:POLICY_EVALUATION_TIME"
          },
          "ids:operator": {
            "@id": "idsc:TEMPORAL_EQUALS"
          }
        }
      ],
      "ids:action": [
        {
          "@id": "idsc:DELETE"
        }
      ]
    }
  ],
  "ids:constraint": [
    {
      "@type": "ids:Constraint",
      "@id": "https://w3id.org/idsa/autogen/constraint/197bef7a-09e0-4d10-ad36-a18be9e885f1",
      "ids:rightOperand": {
        "@value": "2020-07-11T00:00:00Z",
        "@type": "xsd:dateTimeStamp"
      },
      "ids:leftOperand": {
        "@id": "idsc:POLICY_EVALUATION_TIME"
      },
      "ids:operator": {
        "@id": "idsc:AFTER"
      }
    },
    {
      "@type": "ids:Constraint",
      "@id": "https://w3id.org/idsa/autogen/constraint/4c3f8547-2246-440e-9011-41b916767ee7",
      "ids:rightOperand": {
        "@value": "2020-07-11T00:00:00Z",
        "@type": "xsd:dateTimeStamp"
      },
      "ids:leftOperand": {
        "@id": "idsc:POLICY_EVALUATION_TIME"
      },
      "ids:operator": {
        "@id": "idsc:BEFORE"
      }
    }
  ],
  "ids:action": [
    {
      "@id": "idsc:USE"
    }
  ]
}
```

### Usage Logging
```
{
  "@type": "ids:Permission",
  "@id": "https://w3id.org/idsa/autogen/permission/5f1afeed-41f3-4637-870a-2d73cca75fc7",
  "ids:target": [...],
  "ids:description": [
    {
      "@value": "usage-logging",
      "@type": "http://www.w3.org/2001/XMLSchema#string"
    }
  ],
  "ids:title": [
    {
      "@value": "Example Usage Policy",
      "@type": "http://www.w3.org/2001/XMLSchema#string"
    }
  ],
  "ids:postDuty": [
    {
      "@type": "ids:Duty",
      "@id": "https://w3id.org/idsa/autogen/duty/ae908464-11c0-43e8-80d2-f19145814fdb",
      "ids:action": [
        {
          "@id": "idsc:LOG"
        }
      ]
    }
  ],
  "ids:action": [
    {
      "@id": "idsc:USE"
    }
  ]
}
```

### Usage Notification
```
{
  "@type": "ids:Permission",
  "@id": "https://w3id.org/idsa/autogen/permission/c23b0a48-693d-41ff-908b-86c4560f7daa",
  "ids:target": [...],
  "ids:description": [
    {
      "@value": "usage-notification",
      "@type": "http://www.w3.org/2001/XMLSchema#string"
    }
  ],
  "ids:title": [
    {
      "@value": "Example Usage Policy",
      "@type": "http://www.w3.org/2001/XMLSchema#string"
    }
  ],
  "ids:postDuty": [
    {
      "@type": "ids:Duty",
      "@id": "https://w3id.org/idsa/autogen/duty/f0329f3f-ad81-4eea-98d8-833ae9269dfd",
      "ids:constraint": [
        {
          "@type": "ids:Constraint",
          "@id": "https://w3id.org/idsa/autogen/constraint/b09b4d0d-9e7e-483c-ba3b-07b1614a8fc1",
          "ids:rightOperand": {
            "@value": "https://localhost:8080/api/ids/data",
            "@type": "xsd:anyURI"
          },
          "ids:leftOperand": {
            "@id": "idsc:ENDPOINT"
          },
          "ids:operator": {
            "@id": "idsc:DEFINES_AS"
          }
        }
      ],
      "ids:action": [
        {
          "@id": "idsc:NOTIFY"
        }
      ]
    }
  ],
  "ids:action": [
    {
      "@id": "idsc:USE"
    }
  ]
}
```

### Connector Restricted Usage
```
{
  "@type": "ids:Permission",
  "@id": "https://w3id.org/idsa/autogen/permission/0c2b412d-f5ad-47ee-b715-0a8827963844",
  "ids:target": [...],
  "ids:description": [
    {
      "@value": "connector-restriction",
      "@type": "http://www.w3.org/2001/XMLSchema#string"
    }
  ],
  "ids:title": [
    {
      "@value": "Example Usage Policy",
      "@type": "http://www.w3.org/2001/XMLSchema#string"
    }
  ],
  "ids:constraint": [
    {
      "@type": "ids:Constraint",
      "@id": "https://w3id.org/idsa/autogen/constraint/b3da9618-8f80-4cfe-873b-095efe725901",
      "ids:rightOperand": {
        "@value": "https://example.com",
        "@type": "xsd:anyURI"
      },
      "ids:leftOperand": {
        "@id": "idsc:SYSTEM"
      },
      "ids:operator": {
        "@id": "idsc:SAME_AS"
      }
    }
  ],
  "ids:action": [
    {
      "@id": "idsc:USE"
    }
  ]
}
```
