# Outbox Pattern :: Local

![Maintenance](https://img.shields.io/badge/yes-4FCA21?label=maintained&style=flat)
![GitHub](https://img.shields.io/github/license/StevenJDH/outbox-pattern)

This section of the repository is for quickly bringing up the dependant services needed for testing the outbox pattern.

[![Buy me a coffee](https://img.shields.io/static/v1?label=Buy%20me%20a&message=coffee&color=important&style=flat&logo=buy-me-a-coffee&logoColor=white)](https://www.buymeacoffee.com/stevenjdh)

## Prerequisites

* [Docker](https://www.docker.com/products/docker-desktop)/[Rancher](https://rancherdesktop.io) Desktop for running containers.
* make CLI 3+ for convenience. Windows users can install from [here](https://gnuwin32.sourceforge.net/packages/make.htm), and add the bin folder to the system `PATH`.
* Optional: [DBeaver Community](https://dbeaver.io) 25+ or equivalent tool for accessing PostgreSQL database.

## Usage

### Makefile approach
From the root of this repository, run the following commands:

```bash
make start [app=quarkus] # Default is Spring Boot.
make logs [name=<kafka|postgres|debezium|debezium-configurer|schemaregistry|akhq|kafka-ui|zipkin|otel-collector|outbox-pattern>]
make run # Runs one of the examples.
make stop # Or 'make clean' to stop and remove image cache used.
```

### Docker Compose approach

```bash
docker-compose up -d
docker-compose logs [kafka|postgres|debezium|debezium-configurer|kafka-ui|zipkin|otel-collector|outbox-pattern]
pushd ../_examples/<example>/
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
popd # Pops back to the local folder.
docker-compose down --volumes --remove-orphans --timeout 20
```

## Cleanup idle DB connections

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

> [!TIP]
> Alternatively, just run the application from a terminal to avoid the need for all of this, since `Ctrl+C` sends a SIGTERM for graceful shutdown.

## URL references
Below is a selection of URL references exposed by Docker Compose on the host system.

| Service    | Reference                                               |
|------------|---------------------------------------------------------|
| PostgreSQL | postgresql://postgres:password@localhost:5432/orders_db |
| Kafka UI   | http://localhost:8080                                   |
| Zipkin     | http://localhost:9412                                   |

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
