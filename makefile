# Variables
MVN=mvn
DOCKER_COMPOSE=docker-compose
PROJECT_NAME=outbox-pattern

# Default target
.PHONY: all
all: start build run

# Start Docker Compose services
app ?= spring
ifeq ($(app),quarkus)
ENV_FLAG += --env-file ./local/quarkus.env 
endif
.PHONY: start
start:
	$(DOCKER_COMPOSE) --project-name $(PROJECT_NAME) -f ./local/docker-compose.yaml $(ENV_FLAG)up -d

# Stream all logs or for a specific service, e.g., make logs name=kafka-ui
.PHONY: logs
logs:
	$(DOCKER_COMPOSE) --project-name $(PROJECT_NAME) -f ./local/docker-compose.yaml logs --follow $(name)

# Build Maven project
.PHONY: build
build:
	$(MVN) clean package -DskipTests

# Run Maven project (you can change this to whatever you need, e.g., run a Java class, tests, etc.)
.PHONY: run
run:
	cd examples/spring/spring-boot/ddd && \
	$(MVN) spring-boot:run -Dspring-boot.run.profiles=local

.PHONY: docker-down
docker-down:
	$(DOCKER_COMPOSE) --project-name $(PROJECT_NAME) -f ./local/docker-compose.yaml down --volumes --remove-orphans --timeout 20 $(FLAGS)

# Stop Docker Compose services with a grace period before forcefully stopping
.PHONY: stop
stop: FLAGS=
stop: docker-down

# Clean up Docker Compose services, volumes, and cached images pulled by the compose file
.PHONY: cleanup
cleanup: FLAGS=--rmi all
cleanup: docker-down

# Full pipeline: start docker, build Maven, run the app, stop docker
.PHONY: full-pipeline
full-pipeline: start build run stop
