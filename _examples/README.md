# Outbox Pattern :: Examples

![Maintenance](https://img.shields.io/badge/yes-4FCA21?label=maintained&style=flat)
![GitHub](https://img.shields.io/github/license/StevenJDH/outbox-pattern)

A collection of code examples that use the Outbox Pattern. The Spring Boot and Quarkus projects have been designed to expose the same features and endpoints, so their functionality is nearly identical.

[![Buy me a coffee](https://img.shields.io/static/v1?label=Buy%20me%20a&message=coffee&color=important&style=flat&logo=buy-me-a-coffee&logoColor=white)](https://www.buymeacoffee.com/stevenjdh)

## Prerequisites

* Java 21+ ([Temurin/Adopt](https://adoptium.net)) OpenJDK for compiling.
* make CLI 3+ for convenience. Windows users can install from [here](https://gnuwin32.sourceforge.net/packages/make.htm), and add the bin folder to the system `PATH`.
* Optional: [Docker](https://www.docker.com/products/docker-desktop)/[Rancher](https://rancherdesktop.io) Desktop for building containers.

## Examples

* [Java - Quarkus (DDD)](./Java%20-%20Quarkus%20(DDD)): A Java-based service using DDD Architecture that relies on the Outbox Pattern.
* [Java - Quarkus (Layered)](./Java%20-%20Quarkus%20(Layered)): A Java-based service using Layered Architecture that relies on the Outbox Pattern.
* [Java - Spring Boot (DDD)](./Java%20-%20Spring%20Boot%20(DDD)): A Java-based service using DDD Architecture that relies on the Outbox Pattern.
* [Java - Spring Boot (Layered)](./Java%20-%20Spring%20Boot%20(Layered)): A Java-based service using Layered Architecture that relies on the Outbox Pattern.
* [Postman Collection](./Postman): A collection of endpoints used by the code examples.

## Build

### Makefile approach
From the root of this repository, run the following command:

```bash
# Default is Spring Boot.
make build [app=quarkus]

# Or, use compile to create a cloud native binary using Quarkus. Requires Docker.
make compile
```

### Maven CLI approach
From within the desired project directory, run the following command:

```bash
./mvnw clean package -DskipTests
```

## Run

### Makefile approach
From the root of this repository, run the following command:

```bash
# Default is Spring Boot.
make run [app=quarkus]
```

### Maven CLI approach
From within the desired project directory, run the following command:

```bash
# Spring Boot
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Quarkus
./mvnw clean quarkus:dev
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
