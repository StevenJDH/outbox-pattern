# Outbox Pattern

![Maintenance](https://img.shields.io/badge/yes-4FCA21?label=maintained&style=flat)
![GitHub](https://img.shields.io/github/license/StevenJDH/outbox-pattern)

This repository if part of an introductory blog post on dev.to for how to use the Outbox Pattern to create a reliable event-driven architecture. The focus is on using PostgreSQL and Strimzi to store data and emit events in a transactional way using a Spring Boot microservice, but these concepts can be applied to other configurations.

![Outbox Pattern Diagram](outbox-pattern.png "Diagram")

[![Buy me a coffee](https://img.shields.io/static/v1?label=Buy%20me%20a&message=coffee&color=important&style=flat&logo=buy-me-a-coffee&logoColor=white)](https://www.buymeacoffee.com/stevenjdh)


## Build

### Maven projects

....

### Custom Kafka image with Debezium plugin

```bash
cd debezium
nerdctl/docker build -t debezium-connect . [--build-arg STRIMZI_VERSION=latest-kafka-3.9.0 --build-arg DEBEZIUM_CONNECTOR_VERSION=3.1.1.Final]
```

## Usage

### Local

#### Makefie

```bash
make start
make logs [name=<kafka|postgres|debezium|debezium-configurer|kafka-ui>]
make stop
```

#### Docker Compose

```bash
docker-compose up -d
docker-compose logs [kafka|postgres|debezium|debezium-configurer|kafka-ui]
docker-compose down
mvn spring-boot:run -f ....
```

#### Cleanup idle DB connections

When an IDE doesn't support stopping a running application with a graceful shutdown (e.g., SIGINT or SIGTERM), but instead forces a shutdown (e.g., SIGKILL), then the default 10 idle pool connections will be left opened. Eventually, the default 100 connection limit PostgreSQL has will be hit. If you don't want to reset the server, then the follow commands will be useful to clean up the connections.

Show connection limit:

```sql
SHOW max_connections;
```

List current connections:

```sql
SELECT pid, usename, application_name, client_addr, backend_start, state
FROM pg_stat_activity
WHERE datname = current_database() AND state = 'idle';
```

Terminate idle connections:

```sql
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = current_database() AND state = 'idle';
```

Alternatively, just run the application from a terminal to avoid the need for all of this, since `Ctrl+C` sends a SIGTERM for graceful shutdown. 

### Kubernetes

#### Deploy database

```bash
helm upgrade --install my-postgresql oci://registry-1.docker.io/bitnamicharts/postgresql --version 16.6.6 \
    -f values.yaml \
    --namespace strimzi \
    --create-namespace \
    --atomic
```

#### Create outbox connector

```bash
kubectl create -f outbox-connector.yaml -n strimzi
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
