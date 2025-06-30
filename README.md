# Kan AI CLI with Distributed Tracing

This project is a CLI application that uses Spring AI to interact with AI models (OpenAI and Ollama). It has been configured with distributed tracing using OpenTelemetry and Micrometer, with visualization in Grafana Tempo.

## Prerequisites

- Docker and Docker Compose
- OpenAI API key (set as environment variable `OPENAI_API_KEY`)
- Node.js and npm (for local development)
- oh-my-logo npm package (install with `npm install -g oh-my-logo` for local development)

## Running the Application

### Option 1: Run Everything in Docker (Recommended)

Run the entire application stack (including the application, Ollama, Tempo, Prometheus, and Grafana) in Docker:

```bash
export OPENAI_API_KEY=your_openai_api_key
docker-compose up
```

This will:
- Build and start the application container
- Start an Ollama container for local LLM support
- Start the observability stack (Grafana, Tempo, Prometheus)

Note: The application runs in interactive mode, so you need to run it in the foreground (without `-d`) to interact with it. You can use `docker-compose up -d tempo prometheus grafana ollama` to run the supporting services in the background, and then `docker-compose up app` to run just the application in the foreground.

### Option 2: Run Application Locally

1. Start the observability stack (Grafana, Tempo, Prometheus):

```bash
docker-compose up -d tempo prometheus grafana
```

2. Run the application locally:

```bash
export OPENAI_API_KEY=your_openai_api_key
./gradlew bootRun
```

## Viewing Traces

1. Open Grafana at http://localhost:3000
2. Navigate to Explore
3. Select "Tempo" as the data source
4. Use the "Search" tab to find traces by service name "kan-ai-cli"
5. Click on a trace to view its details

## Tracing Architecture

- The application uses Spring Boot's built-in support for distributed tracing
- OpenTelemetry is used as the tracing backend
- Traces are sent to Grafana Tempo using the OTLP protocol
- Grafana is used for visualization

## Configuration Files

- `application.yml` - Application configuration for local development
- `application-docker.yml` - Application configuration for Docker environment
- `Dockerfile` - Instructions for building the application container
- `docker-compose.yml` - Docker Compose configuration for the entire stack
- `tempo-config.yaml` - Tempo configuration
- `prometheus.yml` - Prometheus configuration
- `grafana-datasources.yaml` - Grafana datasources configuration

## Notes

- Spring AI has built-in tracing support, so no changes to the CommandTool were needed
- Tracing is sampled at 100% (all requests are traced)
- Traces include spans for AI model interactions
- When running in Docker, the application container is configured to send traces to the Tempo container using the Docker network service name
- The Ollama container is included in the Docker setup to provide local LLM support without requiring a separate installation
- The application uses oh-my-logo for displaying a CLI logo. This package is automatically installed in the Docker container, but needs to be installed manually for local development
- The application is interactive and requires a terminal for input. When running in Docker, it must be run with the `stdin_open: true` and `tty: true` options (which are configured in the docker-compose.yml file)
- If no input is available (e.g., when running in a non-interactive environment), the application will exit gracefully with a message
