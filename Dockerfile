FROM eclipse-temurin:17-jdk
VOLUME /tmp
COPY .env .env
ARG JAR_FILE=target/StashRoom-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
