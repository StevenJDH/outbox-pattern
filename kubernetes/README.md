# Outbox Pattern :: Kubernetes

![Maintenance](https://img.shields.io/badge/yes-4FCA21?label=maintained&style=flat)
![GitHub](https://img.shields.io/github/license/StevenJDH/outbox-pattern)

This guide provides step-by-step instructions for implementing the Outbox Pattern on Kubernetes using Strimzi's Kafka Connect, Debezium's PostgreSQL Connector plugin, and PostgreSQL. Open-source tooling and Helm charts are used to streamline deployment and facilitate experimentation or extension for various use cases.

[![Buy me a coffee](https://img.shields.io/static/v1?label=Buy%20me%20a&message=coffee&color=important&style=flat&logo=buy-me-a-coffee&logoColor=white)](https://www.buymeacoffee.com/stevenjdh)

## Configuration steps

1. Strimzi 0.45.0 or newer needs to be installed with a cluster called `my-strimzi-cluster` running at least 3 brokers. The Kafka resource must also be configured with an internal listener for port 9094 that has authentication type set to `tls` and supports `simple` authorization. Both, `CN=kafka-ui` and `CN=connect-cluster` users need to be add as super users to reduce complexity. The following commands provide a quick way to meet all of these requirements if needed.

    ```bash
    helm repo add stevenjdh https://StevenJDH.github.io/helm-charts
    helm repo update
    helm upgrade --install my-strimzi-cluster stevenjdh/strimzi-cluster --version 0.1.0 \
        -f ./config/strimzi/values.yaml \
        --namespace strimzi \
        --create-namespace \
        --atomic
    ```

2. Install the PostgreSQL database server.

    ```bash
    helm upgrade --install my-postgresql oci://registry-1.docker.io/bitnamicharts/postgresql --version 16.6.6 \
        -f ./config/postgresql/values.yaml \
        --set-file primary.initdb.scripts.init\\.sql=../local/init.sql \
        --namespace strimzi \
        --atomic
    ```

    Use something like [DBeaver Community](https://dbeaver.io/download/) to explore the `orders_db` database and for directly testing the `outbox_event` table using port forwarding, `kubectl port-forward svc/my-postgresql-hl 5432:5432 -n strimzi`.

3. Download the [debezium-connector-postgres](https://repo1.maven.org/maven2/io/debezium/debezium-connector-postgres/3.1.1.Final/debezium-connector-postgres-3.1.1.Final-plugin.tar.gz) plugin, and decompress its contents to a folder called `kafka-connect-plugins/debezium-connector-postgres-3.1.1.Final`. It is important to use the version number in the folder name to support rolling updates.

    ```text
    kafka-connect-plugins/
    └───debezium-connector-postgres-3.1.1.Final/
            CHANGELOG.md
            CONTRIBUTE.md
            COPYRIGHT.txt
            debezium-api-3.1.1.Final.jar
            debezium-connector-postgres-3.1.1.Final.jar
            debezium-core-3.1.1.Final.jar
            LICENSE-3rd-PARTIES.txt
            LICENSE.txt
            postgresql-42.6.1.jar
            protobuf-java-3.25.5.jar
            README.md
            README_JA.md
            README_KO.md
            README_ZH.md
    ```

4. Edit the `kafka-connect-plugins-pv` PersistentVolume resource in the `outbox-connector.yaml` file so that the `.spec.hostPath.path` property points to the `kafka-connect-plugins` folder path on the host machine that was created in the previous step. This path will be used for loading additional plugins with Connect. Use the following command to locate and verify the contents in that path if needed. For example, paths on Windows machines will start with `/mnt/c/` since Rancher Desktop is creating a bridge with the Windows filesystem to provide access. For macOS and Linux users, see [Volumes](https://docs.rancherdesktop.io/ui/preferences/virtual-machine/volumes) for more information.

    ```bash
    rdctl shell
    ```

> [!NOTE]  
> This step assumes Rancher Desktop is being used. If not, then configure the PersistentVolume to any other volume type desired just as long as the Connect plugin files can be added there.

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

6. Install kafbat/kafka-ui to make it easier to explore topics and connector resources.

    ```bash
    helm repo add kafbat https://kafbat.github.io/helm-charts
    helm repo update
    helm upgrade --install kafka-ui kafbat/kafka-ui --version 1.5.1 \
        -f ./config/kafka-ui/values.yaml \
        --namespace strimzi \
        --atomic
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
        'baggage=promo\=12345,region\=eu-west',       -- tracing_span_context
        now()                                         -- timestamp (can be omitted since DEFAULT is now())
    );
    ```

> [!NOTE]  
> This step assumes that the Kafka resource is configured with `auto.create.topics.enable` set to `false`, which requires pre-creating topics. This is generally a good idea as it will prevent race conditions between clients creating topics and topics being creating via CRDs.

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
