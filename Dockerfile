FROM eclipse-temurin:24-jdk-alpine AS build
WORKDIR /workspace/app

COPY gradle gradle
COPY gradlew .
COPY settings.gradle .
COPY build.gradle .
COPY src src

RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:24-jre-alpine

# Install Node.js and npm
RUN apk add --update nodejs npm

# Install oh-my-logo globally
RUN npm install -g oh-my-logo

VOLUME /tmp
COPY --from=build /workspace/app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
