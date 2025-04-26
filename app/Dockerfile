FROM eclipse-temurin:17-jdk-alpine
RUN apk --no-cache add curl

COPY build/libs/*.jar app.jar

EXPOSE 8080 8080
CMD ["sh", "-c", "java -jar -Dserver.port=$PORT $JAVA_OPTS /app.jar"]