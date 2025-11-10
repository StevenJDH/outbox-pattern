# Outbox Pattern :: Debezium

![Maintenance](https://img.shields.io/badge/yes-4FCA21?label=maintained&style=flat)
![GitHub](https://img.shields.io/github/license/StevenJDH/outbox-pattern)

As an alternative to loading connector plugins dynamically as seen in the [Kubernetes](../kubernetes/) setup, one can optionally create a custom image with the debezium-connector-postgres plugin already included. The provided Dockerfile shows how this is done. 

[![Buy me a coffee](https://img.shields.io/static/v1?label=Buy%20me%20a&message=coffee&color=important&style=flat&logo=buy-me-a-coffee&logoColor=white)](https://www.buymeacoffee.com/stevenjdh)

## Build

The following commands can help build and push the customized image to a container registry. Do keep in mind that, although this example uses the `latest-kafka-3.9.0` tag for the base image, in practice, it's better to use the more explicit `0.45.0-kafka-3.9.0` tag to avoid issues with the Strimzi Operator.

```bash
nerdctl/docker build -t debezium-connect . [--build-arg STRIMZI_VERSION=latest-kafka-3.9.0 --build-arg DEBEZIUM_CONNECTOR_VERSION=3.1.1.Final]
nerdctl/docker tag debezium-connect:latest <my-container-registry>/debezium-connect:3.1.1.Final-kafka-3.9.0
nerdctl/docker push <my-container-registry>/debezium-connect:3.1.1.Final-kafka-3.9.0
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
