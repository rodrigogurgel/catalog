FROM gradle:8.13-jdk17-alpine AS build
WORKDIR /home/gradle/project
COPY . .

# Clean build and download opentelemetry agent using gradle task
RUN gradle clean build downloadOtelAgent -x koverVerify -x test -x check

FROM eclipse-temurin:17-jdk-alpine
RUN apk --no-cache add curl

# Copy opentelemetry agent and disable by default
COPY --from=build /home/gradle/project/build/otel/opentelemetry-javaagent.jar opentelemetry-javaagent.jar
ENV OTEL_JAVAAGENT_ENABLED=false

COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

EXPOSE 8080
CMD ["sh", "-c", "java -javaagent:/opentelemetry-javaagent.jar -jar $JAVA_OPTS /app.jar"]