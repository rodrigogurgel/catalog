FROM gradle:8.13-jdk17-alpine AS build
WORKDIR /home/gradle/project
COPY . .
RUN gradle clean build downloadOtelAgent -x koverVerify -x test -x check

FROM eclipse-temurin:17-jdk-alpine
RUN apk --no-cache add curl

COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

ENV OTEL_JAVAAGENT_ENABLED false

EXPOSE 8080
CMD ["sh", "-c", "java -jar $JAVA_OPTS /app.jar"]