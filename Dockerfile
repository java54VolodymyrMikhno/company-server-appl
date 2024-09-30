FROM openjdk:22-slim-bullseye
COPY company-server-appl-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]