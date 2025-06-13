# Stage 1: Build the application
FROM gradle:8.14-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :document-service:bootJar --no-daemon

# Stage 2: Run the application
FROM gcr.io/distroless/java21-debian12
WORKDIR /app
COPY --from=build /app/document-service/build/libs/*.jar app.jar
EXPOSE 8091
ENTRYPOINT ["java", "-jar", "app.jar"]
