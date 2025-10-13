
# Digital Disclosure Service Frontend

This public-facing microservice is part of the Digital Disclosure Service. This service is designed to allow customers to tell HMRC about unpaid tax from previous years for both onshore and offshore liabilities. It is the replacement for the DO4SUB iForm which is the original digital incarnation of the Disclosure Service.

## Running the service

### Start all dependent services
This service depends on multiple other services, including:
- Auth
- Address Lookup Frontend
- Digital Disclosure Service
- Digital Disclosure Service Store

The easiest way to set up required microservices is to use Service Manager and the DDS_ALL profile from service-manager-config repository:

```
sm2 --start DDS_ALL
```

To stop the frontend microservice from running on service manager (e.g. to run your own version locally), you can run:

```
sm2 -stop DIGITAL_DISCLOSURE_SERVICE_FRONTEND
```

### Using localhost

To run this frontend microservice locally on the configured port **'15003'**, you can run:

```
sbt run 
```

**NOTE:** Ensure that you are not running the microservice via service manager before starting your service locally (vice versa) or the service will fail to start

### Accessing the service
The service will then be available via `http://localhost:15003/tell-hmrc-about-underpaid-tax-from-previous-years`.
This will redirect you to the auth stub. Users who are signed up with an Individual GG account will have to have a confidence level of 250 to access the service.
You can set this in the stub along with a NINO, or you can continue, and you will be redirected to the IV Uplift stub
which will allow you to uplift your stubbed user to level 250.

More details can be found on the
[DDCY Live Services Credentials sheet](https://docs.google.com/spreadsheets/d/1ecLTROmzZtv97jxM-5LgoujinGxmDoAuZauu2tFoAVU/edit?gid=1186990023#gid=1186990023)
for both staging and local url's or check the Tech Overview section in the
[service summary page ](https://confluence.tools.tax.service.gov.uk/display/ELSY/DDS+Service+Summary)


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


## Monitoring

The following grafana and kibana dashboards are available for this service:

* [Grafana](https://grafana.tools.production.tax.service.gov.uk/d/digital-disclosure-service-frontend/digital-disclosure-service-frontend?orgId=1&from=now-24h&to=now&timezone=browser&var-ecsServiceName=ecs-digital-disclosure-service-frontend-public-Service-srAPg1gW8rK1&var-ecsServicePrefix=ecs-digital-disclosure-service-frontend-public&refresh=15m)
* [Kibana](https://kibana.tools.production.tax.service.gov.uk/app/dashboards#/view/digital-disclosure-service-frontend?_g=(filters:!(),refreshInterval:(pause:!t,value:60000),time:(from:now-15m,to:now))


## Other helpful documentation

* [Service Runbook](https://confluence.tools.tax.service.gov.uk/display/ELSY/Digital+Disclosure+Service+%28DDS%29+Runbook)

* [Architecture Links](https://confluence.tools.tax.service.gov.uk/pages/viewpage.action?pageId=857113254)

## Local Debugging

When local testing you may come across the following error:
* Service Unavailable in the browser
* Forbidden error in the terminal: Unexpected response from DDS Store, status: 403, body: {"statusCode":403,"message":"Forbidden"}

The fix for this is to drop the collection 'internal-auth' from your local Mongo Database (usually in Studio3T).
Then restart all the services using Service Manager 2, and try again.
