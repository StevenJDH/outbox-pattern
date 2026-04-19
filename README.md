# Outbox Pattern

![Maintenance](https://img.shields.io/badge/yes-4FCA21?label=maintained&style=flat)
![GitHub](https://img.shields.io/github/license/StevenJDH/outbox-pattern)

This repository is part of an introductory blog post on [dev.to](#) (not published yet 😅) for how to use the Outbox Pattern to create a reliable event-driven architecture. The focus is on using PostgreSQL and Strimzi to store data and emit events in a transactional way using a Spring Boot or Quarkus microservice, but these concepts can be applied to other configurations.

![Outbox Pattern Diagram](outbox-pattern.png "Diagram")

[![Buy me a coffee](https://img.shields.io/static/v1?label=Buy%20me%20a&message=coffee&color=important&style=flat&logo=buy-me-a-coffee&logoColor=white)](https://www.buymeacoffee.com/stevenjdh)

## Features

* Code examples that work identically with different architectures.
* Support for storing event payloads in PostgreSQL JSONB fields.
* Support for OpenTelemetry.
* Full infrastructure creation for both local and Kubernetes testing.
* Dynamic event routing to different outbox topics based on aggregate type.

## Contents

* [Local](./local): Uses docker compose to setup a backend for testing the Outbox Pattern.
* [Kubernetes](./kubernetes): Similar to the above, but implements a more real-world setup on Kubernetes.
* [Debezium](./debezium): Shows how to optionally create a custom image with the debezium-connector-postgres plugin.
* [Examples](./_examples): A collection of code examples using the Outbox Pattern.

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
