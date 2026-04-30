# This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
# Copyright (c) 2025-2026 Steven Jenkins De Haro
#
# This source code is licensed under the MIT license found in the
# LICENSE file in the root directory of this source tree or at
# https://opensource.org/licenses/MIT.

ARG STRIMZI_VERSION=latest-kafka-3.9.0
FROM quay.io/strimzi/kafka:${STRIMZI_VERSION}

USER root:root

ARG DEBEZIUM_CONNECTOR_VERSION=3.1.1.Final
WORKDIR /opt/kafka/plugins/
RUN curl -sfSL "https://repo1.maven.org/maven2/io/debezium/debezium-connector-postgres/${DEBEZIUM_CONNECTOR_VERSION}/debezium-connector-postgres-${DEBEZIUM_CONNECTOR_VERSION}-plugin.tar.gz" | tar xz

# OPTIONAL: Uncomment the following line to include the Confluent Avro Converter and its dependencies to the image.
# COPY confluent-avro/ confluent-avro/

USER 1001