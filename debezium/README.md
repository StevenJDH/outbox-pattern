# Outbox Pattern :: Debezium

![Maintenance](https://img.shields.io/badge/yes-4FCA21?label=maintained&style=flat)
![GitHub](https://img.shields.io/github/license/StevenJDH/outbox-pattern)

As an alternative to loading connector plugins dynamically as seen in the [Kubernetes](../kubernetes/) setup, one can optionally create a custom image with the debezium-connector-postgres plugin already included, and any other plugin needed. The provided Dockerfile shows how this is done.

[![Buy me a coffee](https://img.shields.io/static/v1?label=Buy%20me%20a&message=coffee&color=important&style=flat&logo=buy-me-a-coffee&logoColor=white)](https://www.buymeacoffee.com/stevenjdh)

## Prerequisites

* [Docker](https://www.docker.com/products/docker-desktop)/[Rancher](https://rancherdesktop.io) Desktop for building containers.
* Optional: [Maven CLI](https://maven.apache.org/download.cgi) 3.9.5+ for downloading confluent dependencies.

## Build

The following commands can help build and push the customized image to a container registry. Do keep in mind that, although this example uses the `latest-kafka-3.9.0` tag for the base image, in practice, it's better to use the more explicit `0.45.0-kafka-3.9.0` tag to avoid issues with the Strimzi Operator.

```bash
nerdctl/docker build -t debezium-connect . [--build-arg STRIMZI_VERSION=latest-kafka-3.9.0 --build-arg DEBEZIUM_CONNECTOR_VERSION=3.1.1.Final]
nerdctl/docker tag debezium-connect:latest <my-container-registry>/debezium-connect:3.1.1.Final-kafka-3.9.0
nerdctl/docker push <my-container-registry>/debezium-connect:3.1.1.Final-kafka-3.9.0
```

## Confluent Schema Registry and Avro support
By default, Kafka Connect does not include the Confluent dependencies required for integrating with the Schema Registry or for using Avro. This is particularly relevant when the SMT (EventRouter) is not used, as it allows direct use of the AvroConverter, since both features are mutually exclusive. The following steps describe how to build the `confluent-avro` plugin folder to add this support.

1. From the same directory where the [pom.xml](./pom.xml) is located, run the below command. This process produces a similar result to the list found in the [Debezium documentation](https://debezium.io/documentation/reference/3.5/configuration/avro.html#deploying-confluent-schema-registry-with-debezium-containers), but in a more accurate way because dependencies can change from version to version.

    ```bash
    mvn dependency:copy-dependencies -DoutputDirectory=confluent-avro-7.9.5 -Dconfluent.version=7.9.5
    ```

    📝 **Note:** The `-Dconfluent.version=7.9.5` flag is used to target a particular Schema Registry/[AvroConverter](https://mvnrepository.com/artifact/io.confluent/kafka-connect-avro-converter) version that is compatible with the Kafka version being used. See [Confluent Platform and Apache Kafka compatibility](https://docs.confluent.io/platform/current/installation/versions-interoperability.html#cp-and-apache-ak-compatibility) for the compatibility matrix.

2. The `confluent-avro` plugin folder should have now been created. Keep the version number in the folder name as this will be important for supporting rolling updates.

3. Decide on where the folder will be used. For example, if it will be included with the custom image discussed in the previous section, then open the [Dockerfile](./Dockerfile) for editing, and uncomment the indicated line towards the bottom. For referencing it externally, follow a similar approach to [Step 4](https://github.com/StevenJDH/outbox-pattern/tree/main/kubernetes#configuration-steps) of the Kubernetes setup.

4. Finally, update the KafkaConnector resource to use the AvroConverter class in a similar way as in the following example:

    ```yaml
    apiVersion: kafka.strimzi.io/v1beta2
    kind: KafkaConnector
    metadata:
      name: outbox-connector
      labels:
        strimzi.io/cluster: debezium-connect-cluster
      namespace: strimzi
    spec:
      ...
      config:
        ...
        key.converter: org.apache.kafka.connect.storage.StringConverter
        value.converter: io.confluent.connect.avro.AvroConverter
        value.converter.schema.registry.url: http://schemaregistry:8081
        value.converter.auto.register.schemas: false
        value.converter.use.latest.version: true
        ...
    ```

## Contributing
Thanks for your interest in contributing! There are many ways to contribute to this project. Get started [here](https://github.com/StevenJDH/.github/blob/main/docs/CONTRIBUTING.md).

## Do you have any questions?
Many commonly asked questions are answered in the FAQ:
[https://github.com/StevenJDH/outbox-pattern/wiki/FAQ](https://github.com/StevenJDH/outbox-pattern/wiki/FAQ)

## Want to show your support?

|Method          | Address                                                                                   |
|---------------:|:------------------------------------------------------------------------------------------|
|PayPal:         | [https://www.paypal.me/stevenjdh](https://www.paypal.me/stevenjdh "Steven's Paypal Page") |
|Cryptocurrency: | [Supported options](https://github.com/StevenJDH/StevenJDH/wiki/Donate-Cryptocurrency)    |


// Steven Jenkins De Haro ("StevenJDH" on GitHub)
