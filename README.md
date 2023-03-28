
# Digital Disclosure Service Frontend

This public-facing microservice is part of the Digital Disclosure Service. This service is designed to allow customers to tell HMRC about unpaid tax from previous years for both onshore and offshore liabilities. It is the replacement for the DO4SUB iForm which is the original digital incarnation of the Disclosure Service.

## How to run the service

### Start a MongoDB instance

### Start the microservice
This service is written in Scala and Play, so needs at a JRE to run and a JDK for development. In order to run the application you need to have SBT installed. Then, it is enough to start the service with:
`sbt run`

### Start all dependent services
This service depends on multiple other services, including:
- Auth
- Address Lookup Frontend
- Digital Disclosure Service
- Digital Disclosure Service Store

The easiest way to set up required microservices is to use Service Manager and the DDS_ALL profile from service-manager-config repository:
`sm --start DDS_ALL`

### Accessing the service
The service will then be available via `http://localhost:15003/tell-hmrc-about-underpaid-tax-from-previous-years`. This will redirect you to the auth stub. Users who are signed up with an Individual GG account will have to have a confidence level of 250 to access the service. You can set this in the stub along with a NINO, or you can continue and you will be redirect to the IV Uplift stub which will allow you to uplift your stubbed user to level 250.

## Testing the application
This repository contains unit tests for the service. In order to run them, simply execute:
`sbt test`
This repository contains integration tests for the service. In order to run them, simply execute:
`sbt it:test`

# Feature flags
This service uses feature flags to enable/disable some of its features. These can be changed/overridden in config where the keys follow the structure: `features.<featureName>`.

The list of feature flags and what they are responsible for:
| Key                      | Functionality |
| ------------------------ | ------------- |
| welsh-translation        | Determines whether or not the welsh translation toggle is active  |
| full-disclosure-journey  | Allows the user to access the Full Disclosure journey and related functionality. Currently only the Notification journey will be available in production |

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
