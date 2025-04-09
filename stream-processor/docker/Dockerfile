FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/stream-processor-1.0-SNAPSHOT.jar stream-processor.jar

ENTRYPOINT ["java", "-jar", "stream-processor.jar"]