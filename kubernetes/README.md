# Outbox Pattern :: Kubernetes

![Maintenance](https://img.shields.io/badge/yes-4FCA21?label=maintained&style=flat)
![GitHub](https://img.shields.io/github/license/StevenJDH/outbox-pattern)

This guide provides step-by-step instructions for implementing the Outbox Pattern on Kubernetes using Strimzi's Kafka Connect, Debezium's PostgreSQL Connector plugin, and PostgreSQL. Open-source tooling and Helm charts are used to streamline deployment and facilitate experimentation or extension for various use cases.

[![Buy me a coffee](https://img.shields.io/static/v1?label=Buy%20me%20a&message=coffee&color=important&style=flat&logo=buy-me-a-coffee&logoColor=white)](https://www.buymeacoffee.com/stevenjdh)

## Prerequisites

* [Rancher Desktop](https://rancherdesktop.io) for running Kubernetes 1.31+ (depends on Strimzi version) locally.
* [Helm CLI](https://helm.sh/docs/intro/install/) 4.2+ installed.
* [Kubectl CLI](https://kubernetes.io/docs/tasks/tools/#kubectl) installed and configured to access the cluster.
* [Strimzi](https://artifacthub.io/packages/olm/community-operators/strimzi-kafka-operator) 1.2.0+ Helm Chart (version tested).
* [Bitnami PostgreSQL](https://artifacthub.io/packages/helm/bitnami/postgresql) 18+ Helm Chart or equivalent.
* [DBeaver Community](https://dbeaver.io) 25+ or equivalent tool for accessing PostgreSQL database.
* A strong cup of coffee ☕😏.

## Configuration steps

1. Strimzi 0.51.0 or newer needs to be installed with a cluster called `my-strimzi-cluster` running at least 3 brokers. The Kafka resource must also be configured with an internal listener for port 9094 that has authentication type set to `tls` and supports `simple` authorization. Both, `CN=kafka-ui` and `CN=connect-cluster` users need to be add as super users to reduce complexity. The following commands provide a quick way to meet all of these requirements if needed.

    ```bash
    helm repo add stevenjdh https://StevenJDH.github.io/helm-charts
    helm repo update
    helm upgrade --install my-strimzi-cluster stevenjdh/strimzi-cluster --version 0.3.0 \
        -f ./config/strimzi/values.yaml \
        --namespace strimzi \
        --create-namespace \
        [--rollback-on-failure | --atomic]
    ```

2. Install the PostgreSQL database server.

    ```bash
    helm upgrade --install my-postgresql oci://registry-1.docker.io/bitnamicharts/postgresql --version 18.9.0 \
        -f ./config/postgresql/values.yaml \
        --set-file "primary.initdb.scripts.init\.sql=../local/init.sql" \
        --namespace strimzi \
        [--rollback-on-failure | --atomic]
    ```

    Use something like [DBeaver Community](https://dbeaver.io/download/) to explore the `orders_db` database and for directly testing the `outbox_event` table using port forwarding, `kubectl port-forward svc/my-postgresql-hl 5432:5432 -n strimzi`.

3. Similar to the [Confluent Schema Registry and Avro support](../debezium/README.md#confluent-schema-registry-and-avro-support) process, run the below command to download the `debezium-connector-postgres` plugin and its dependencies. It is important to use the version number in the folder name to support rolling updates.

    ```bash
    # The '-DoutputDirectory' property requires an absolute path or it will output to the debezium folder.
    mvn -f ../debezium/pom-debezium.xml dependency:copy-dependencies \
        -DoutputDirectory=$PWD/kafka-connect-plugins/debezium-connector-postgres-3.6.2.Final \
        -DdebeziumVersion="3.6.2.Final"
    ```

    The command above should have created the following structure:

    ```text
    kafka-connect-plugins/
    └───debezium-connector-postgres-3.6.2.Final/
            checker-qual-3.55.1.jar
            connect-api-4.3.0.jar
            debezium-api-3.6.2.Final.jar
            debezium-config-3.6.2.Final.jar
            debezium-connect-plugins-3.6.2.Final.jar
            debezium-connector-common-3.6.2.Final.jar
            debezium-connector-postgres-3.6.2.Final.jar
            debezium-openlineage-api-3.6.2.Final.jar
            debezium-util-3.6.2.Final.jar
            jackson-annotations-2.21.jar
            jackson-core-2.21.2.jar
            jackson-databind-2.21.2.jar
            jackson-datatype-jsr310-2.21.2.jar
            jakarta.ws.rs-api-3.1.0.jar
            kafka-clients-4.3.0.jar
            lz4-java-1.10.2.jar
            postgresql-42.7.13.jar
            protobuf-java-3.25.5.jar
            sketches-java-0.8.2.jar
            slf4j-api-1.7.36.jar
            snappy-java-1.1.10.7.jar
            zstd-jni-1.5.6-10.jar
    ```

    <table>
    <tr>
    <td>

    > [!NOTE]
    > Versions after `debezium-connector-postgres-3.5.2.Final` seem to no longer include all transitive dependencies, so using a [download link](https://repo1.maven.org/maven2/io/debezium/debezium-connector-postgres/3.6.2.Final/debezium-connector-postgres-3.6.2.Final-plugin.tar.gz) like that one is not enough. As such, the `pom-debezium.xml` approach will work reliably for any version. Also, the `-DdebeziumVersion="3.6.2.Final"` flag should target versions of the plugin that come close to key libraries in use. For example, `debezium-connector-postgres` depends on `kafka-clients`, so this dependency's version should come close to the version used by the cluster (e.g., 4.3.1). Likewise, when setting the `CLASSPATH` variable, make sure to append after '/opt/kafka/libs/*' to give existing library versions priority and to avoid class not found or collision errors. See the KafkaConnect resource comments in the `outbox-connector.yaml` file for more information.

    </td>
    </tr>
    </table>

4. Move the generated `kafka-connect-plugins` folder to `C:\Users\Public\Downloads` if using Windows. If using a different operating system, or a different path is preferred, then edit the `kafka-connect-plugins-pv` PersistentVolume resource in the `outbox-connector.yaml` file so that the `.spec.hostPath.path` property points to where the `kafka-connect-plugins` folder is located on the host machine. This path will be used for loading additional plugins with Connect. Use the following command to locate and verify the contents in that path if needed. For example, paths on Windows machines will start with `/mnt/c/` since Rancher Desktop is creating a bridge with the Windows filesystem to provide access. For macOS and Linux users, see [Volumes](https://docs.rancherdesktop.io/ui/preferences/virtual-machine/volumes) for more information.

    ```bash
    # Opens the Rancher Desktop shell environment.
    rdctl shell
    ```

    <table>
    <tr>
    <td>

    > [!NOTE]
    > This step assumes Rancher Desktop is being used. If not, then configure the PersistentVolume to any other volume type desired just as long as the Connect plugin files can be added there.

    </td>
    </tr>
    </table>

5. Create the Connect and Connector resources along with other required resources to launch the main part of this setup.

    ```bash
    kubectl create -f outbox-connector.yaml
    ```

    **Output:**

    ```bash
    persistentvolume/kafka-connect-plugins-pv created
    persistentvolumeclaim/kafka-connect-plugins-pvc created
    kafkauser.kafka.strimzi.io/kafka-ui created
    kafkauser.kafka.strimzi.io/connect-cluster created
    kafkatopic.kafka.strimzi.io/connect-cluster-offsets created
    kafkatopic.kafka.strimzi.io/connect-cluster-configs created
    kafkatopic.kafka.strimzi.io/connect-cluster-status created
    kafkatopic.kafka.strimzi.io/outbox.order created
    kafkatopic.kafka.strimzi.io/outbox.unknown created
    kafkatopic.kafka.strimzi.io/outbox.failed.events-dlt created
    kafkaconnect.kafka.strimzi.io/debezium-connect-cluster created
    role.rbac.authorization.k8s.io/debezium-connect-secret-access created
    rolebinding.rbac.authorization.k8s.io/debezium-connect-secret-access-binding created
    networkpolicy.networking.k8s.io/allow-kafkaui-to-kafkaconnect created
    kafkaconnector.kafka.strimzi.io/outbox-connector created
    ```

    <table>
    <tr>
    <td>

    > [!IMPORTANT]
    > Resources created with the `outbox-connector.yaml` file require Strimzi 0.51.0 and later to work as they use the CRD v1 APIs from Strimzi.

    </td>
    </tr>
    </table>

6. Install kafbat/kafka-ui to make it easier to explore topics and connector resources.

    ```bash
    helm repo add kafbat https://kafbat.github.io/helm-charts
    helm repo update
    helm upgrade --install kafka-ui kafbat/kafka-ui --version 1.6.5 \
        -f ./config/kafka-ui/values.yaml \
        --namespace strimzi \
        [--rollback-on-failure | --atomic]
    ```

    Use port forwarding to access the UI, `kubectl port-forward svc/kafka-ui 8080:80 -n strimzi`.

7. Finally, trigger a test by manually inserting an event into the outbox_event table to see if it ends up in the outbox.order topic, or the outbox.unknown topic when aggregate_type is anything else other than `order`.

    ```sql
    INSERT INTO public.outbox_event (
        id,
        aggregate_type,
        aggregate_id,
        "type",
        payload,
        tracing_span_context,
        "timestamp"
    )
    VALUES (
        gen_random_uuid(),                            -- generates a UUID (requires pgcrypto extension)
        'order',                                      -- aggregate_type (must be in lowercase)
        '99999',                                      -- aggregate_id
        'OrderCreated',                               -- type
        '{"id": "99999", "status": "PLACED"}'::jsonb, -- payload (JSONB)
        'baggage=promo\\=12345,region\\=eu-west\r\n', -- tracing_span_context
        now() AT TIME ZONE 'UTC'                      -- timestamp (may cause error if omitted and not UTC)
    );
    ```

    <table>
    <tr>
    <td>

    > [!NOTE]
    > This step assumes that the Kafka resource is configured with `auto.create.topics.enable` set to `false`, which requires pre-creating topics. This is generally a good idea as it will prevent race conditions between clients creating topics and topics being creating via CRDs.

    </td>
    </tr>
    </table>

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
