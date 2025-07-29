ARG STRIMZI_VERSION=latest-kafka-3.9.0
FROM quay.io/strimzi/kafka:${STRIMZI_VERSION}

USER root:root

ARG DEBEZIUM_CONNECTOR_VERSION=3.1.1.Final
WORKDIR /opt/kafka/plugins/
RUN curl -sfSL "https://repo1.maven.org/maven2/io/debezium/debezium-connector-postgres/${DEBEZIUM_CONNECTOR_VERSION}/debezium-connector-postgres-${DEBEZIUM_CONNECTOR_VERSION}-plugin.tar.gz" | tar xz

USER 1001