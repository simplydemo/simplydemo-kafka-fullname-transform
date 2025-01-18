# simplydemo-kafka-fullname-transform
This is a sample that implements a custom Transform module used in the Kafka connector.


## Getting Started

You can provision data from Source to Target through various pipeline channels of the Kafka streaming service platform. 
In particular, using [Debezium](https://debezium.io/) as an open source for CDC (Change Data Capture), 
you can easily implement a streaming integration platform for various use cases.


## Git

```
git clone https://github.com/simplydemo/simplydemo-kafka-fullname-transform.git
```

## Build

```
mvn clean package -DskipTests=true
```

## Usage Kafka Connect like Debezium

Below is the configuration of FullNameTransform, which merges the first_name and last_name column values of <target_table_name> and maps them to the full_name column value through `io.debezium.connector.jdbc.JdbcSinkConnector`, one of the Kafka connectors, for messages received as my-topic.

```json
{
  "name": "sink-connector-mysql",
  "config": {
    "connector.class": "io.debezium.connector.jdbc.JdbcSinkConnector",
    "tasks.max": "1",
    "topics": "my-topic",
    "connection.url": "jdbc:mysql://mysql:3306/demo",
    "connection.username": "<username>",
    "connection.password": "<password>",
    "table.name.format": "<target_table_name>",
    "insert.mode": "upsert",
    "schema.evolution": "basic",
    "primary.key.mode": "record_key",
    "primary.key.fields": "id",
    "transforms": "fullName",
    "transforms.fullName.type": "io.github.simplydemo.kafka.transforms.FullNameTransform"
  }
}
```


## Install

- Copy the JAR file to the Kafka Connect plugin directory:

```
cp target/simplydemo-kafka-fullname-transform-0.0.1-SNAPSHOT-uber.jar /kafka/connect/plugins/
```

- Restart Kafka Connect: Restart the Kafka Connect worker to load the plugin

```
systemctl restart kafka-connect
```

- Verify plugin loaded: Check Kafka Connect logs to see if `FullNameTransform` is loaded


### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.1.0/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.1.0/maven-plugin/reference/html/#build-image)

