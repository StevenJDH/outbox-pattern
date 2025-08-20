# Variables
MVN=mvn
DOCKER_COMPOSE=docker-compose
PROJECT_NAME=outbox_pattern

# Default target
all: start build run

# Start Docker Compose services
app ?= spring
ifeq ($(app),quarkus)
ENV_FLAG += --env-file ./local/quarkus.env 
endif
start:
	$(DOCKER_COMPOSE) --project-name $(PROJECT_NAME) -f ./local/docker-compose.yaml $(ENV_FLAG)up -d

# Stream all logs or for a specific service, e.g., make logs name=kafka-ui
logs:
	$(DOCKER_COMPOSE) --project-name $(PROJECT_NAME) -f ./local/docker-compose.yaml logs --follow $(name)

# Build Maven project
build:
	$(MVN) clean package -DskipTests

# Run Maven project (you can change this to whatever you need, e.g., run a Java class, tests, etc.)
run:
	cd examples/spring/spring-boot/ddd && \
	$(MVN) spring-boot:run -Dspring-boot.run.profiles=local

# Stop Docker Compose services with a grace period before forcefully stopping
stop:
	$(DOCKER_COMPOSE) --project-name $(PROJECT_NAME) -f ./local/docker-compose.yaml down --volumes --remove-orphans --timeout 20

# Clean up Docker Compose services, volumes, and cached images pulled by the compose file
cleanup:
	$(DOCKER_COMPOSE) --project-name $(PROJECT_NAME) -f ./local/docker-compose.yaml down --volumes --remove-orphans --timeout 20 --rmi all

# Full pipeline: start docker, build Maven, run the app, stop docker
full-pipeline: start build run stop
