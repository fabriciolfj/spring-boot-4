# Subscription

## Description
 API responsible to create and manage subscriptions

## Stack

- Java 21
- Kotlin 1.9.22
- Spring Boot 3.2.0
- Spring Cloud 2023.0.0
- Newrelic 8.9.1

## Github Registry

configure the github registry in your settings.xml, replace `[GITHUB_TOKEN]` with your github token

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <servers>
    <server>
      <id>github</id>
      <username>_</username>
      <password>[GITHUB_TOKEN]</password>
    </server>
  </servers>

</settings>
```

## Observability

### New Relic

- [Api APM](https://one.newrelic.com/-/...)

### RabbitMQ
- [hml](http://rabbitmq.hml.ecomm.grupoboticario.digital:15672/)
- [PROD](http://lb.rabbitmq.common.prod.blzlocal.com.br:15672/)

## Connections and config
Config for PostgreSQL and RabbitMQ environment access are defined in the config-repo repository

1. [config-repo ...](https://github.com/belezanaweb/config-repo/blob/master/...yml)
2. [config-repo ..](https://github.com/belezanaweb/config-repo/blob/master/...yml)

## Local/Docker (dev/test)

```shell
# Start
docker compose up -d
```

```shell
# Stop
docker compose down
```


## Testing
``` bash
./mvnw test
```

## Running

``` bash
export JAVA_TOOL_OPTIONS=-Djavax.net.ssl.trustStore=/Library/Java/JavaVirtualMachines/jdk1.8.0_261.jdk/Contents/Home/jre/lib/security/cacerts
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
./mvnw spring-boot:run -Dspring-boot.run.profiles=hml,local
```

## Endpoints

Endpoints available on documentation below:

1. [Postman Documentation](https://gb-tech.postman.co/workspace/Ecommerce%2FMarketplace-BR~2b4082fa-04e6-44da-9cbc-1f1537b38409)

