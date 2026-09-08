# This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
# Copyright (c) 2025-2026 Steven Jenkins De Haro
#
# This source code is licensed under the MIT license found in the
# LICENSE file in the root directory of this source tree or at
# https://opensource.org/licenses/MIT.

ARG MAVEN_VERSION=3.9.16-eclipse-temurin-21
# Release and changelog: https://debezium.io/releases/3.5/
ARG DEBEZIUM_CONNECTOR_VERSION=3.6.2.Final
ARG STRIMZI_VERSION=latest-kafka-4.3.1

############################
# Resolve Debezium plugins
############################

FROM maven:${MAVEN_VERSION} AS debezium-dependencies
ARG DEBEZIUM_CONNECTOR_VERSION

WORKDIR /build
COPY pom-debezium.xml ./pom.xml
RUN mvn -B -ntp dependency:copy-dependencies \
      -DoutputDirectory="debezium-connector-postgres-${DEBEZIUM_CONNECTOR_VERSION}" \
      -DdebeziumVersion="${DEBEZIUM_CONNECTOR_VERSION}" \
    && rm -rf /root/.m2

############################
# Kafka Connect
############################

FROM quay.io/strimzi/kafka:${STRIMZI_VERSION}
ARG DEBEZIUM_CONNECTOR_VERSION

USER root:root

WORKDIR /opt/kafka/plugins/
COPY --from=debezium-dependencies \
    /build/debezium-connector-postgres-${DEBEZIUM_CONNECTOR_VERSION}/ \
    /opt/kafka/plugins/debezium-connector-postgres-${DEBEZIUM_CONNECTOR_VERSION}/

# OPTIONAL: Uncomment the following line to include the Confluent Avro Converter and its dependencies in the image.
# COPY confluent-avro/ confluent-avro/

USER 1001