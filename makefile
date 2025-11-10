# This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
# Copyright (c) 2025 Steven Jenkins De Haro
#
# This source code is licensed under the MIT license found in the
# LICENSE file in the root directory of this source tree or at
# https://opensource.org/licenses/MIT.

# Variables
MVN=mvnw
DOCKER=docker
DOCKER_COMPOSE=docker-compose
PROJECT_NAME=outbox-pattern

SPRING_BOOT_DIR=_examples/Java - Spring Boot (Layered)
QUARKUS_DIR=_examples/Java - Quarkus (DDD)
TARGET_EXAMPLE_DIR=${SPRING_BOOT_DIR}
EXAMPLE_RUN_GOAL=spring-boot:run -Dspring-boot.run.profiles=local

app ?= spring
ifeq ($(app),quarkus)
TARGET_EXAMPLE_DIR=${QUARKUS_DIR}
EXAMPLE_RUN_GOAL=clean quarkus:dev
endif

# Default target
.PHONY: all
all: start build run

# Start Docker Compose services.
.PHONY: start
start:
	$(DOCKER_COMPOSE) --project-name $(PROJECT_NAME) -f ./local/docker-compose.yaml up -d

# Stream all logs or for a specific service, e.g., make logs name=kafka-ui.
.PHONY: logs
logs:
	$(DOCKER_COMPOSE) --project-name $(PROJECT_NAME) -f ./local/docker-compose.yaml logs --follow $(name)

# Build Maven project.
.PHONY: build
build:
	cd $(TARGET_EXAMPLE_DIR) && \
	$(MVN) clean package -DskipTests

# Run Maven project (you can change this to whatever you need, e.g., run a Java class, tests, etc.).
.PHONY: run
run:
	cd $(TARGET_EXAMPLE_DIR) && \
	$(MVN) $(EXAMPLE_RUN_GOAL)

# Compile Quarkus native image using multi-stage Dockerfile.
.PHONY: compile
compile:
	cd $(QUARKUS_DIR) && \
	$(MVN) git-commit-id:revision && \
	$(DOCKER) build -f src/main/docker/Dockerfile.native-micro-multistage -t stevenjdh/outbox-pattern .

.PHONY: docker-down
docker-down:
	$(DOCKER_COMPOSE) --project-name $(PROJECT_NAME) -f ./local/docker-compose.yaml down --volumes --remove-orphans --timeout 20 $(FLAGS)

# Stop Docker Compose services with a grace period before forcefully stopping.
.PHONY: stop
stop: FLAGS=
stop: docker-down

# Clean up Docker Compose services, volumes, and cached images pulled by the compose file.
.PHONY: cleanup
cleanup: FLAGS=--rmi all
cleanup: docker-down

# Full pipeline: start docker, build Maven, run the app, stop docker.
.PHONY: full-pipeline
full-pipeline: start build run stop
