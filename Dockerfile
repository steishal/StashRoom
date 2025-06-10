FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /workspace/app
COPY pom.xml .
COPY .env .env
COPY src src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk
VOLUME /tmp
COPY --from=build /workspace/app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]