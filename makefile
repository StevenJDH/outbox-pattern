# Variables
MVN=mvn
DOCKER_COMPOSE=docker-compose
PROJECT_NAME=outbox_pattern

# Default target
all: start build run

# Start Docker Compose services
start:
	$(DOCKER_COMPOSE) --project-name $(PROJECT_NAME) -f ./local/docker-compose.yaml up -d

# Stream all logs or for a specific service, e.g., make logs name=kafka-ui
logs:
	$(DOCKER_COMPOSE) --project-name $(PROJECT_NAME) -f ./local/docker-compose.yaml logs --follow $(name)

# Build Maven project
build:
	$(MVN) clean package -DskipTests

# Run Maven project (you can change this to whatever you need, e.g., run a Java class, tests, etc.)
run:
	$(MVN) spring-boot:run

# Stop Docker Compose services
stop:
	$(DOCKER_COMPOSE) --project-name $(PROJECT_NAME) -f ./local/docker-compose.yaml down --volumes

# Clean up Docker Compose services and volumes
cleanup:
	$(DOCKER_COMPOSE) --project-name $(PROJECT_NAME) -f ./local/docker-compose.yaml down --volumes --rmi all

# Full pipeline: start docker, build Maven, run the app, stop docker
full-pipeline: start build run stop
