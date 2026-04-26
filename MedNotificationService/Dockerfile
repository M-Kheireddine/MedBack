FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY MedNotificationService/pom.xml .
COPY MedNotificationService/src ./src

RUN mvn clean package -DskipTests -B --no-transfer-progress

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java","-jar", "app.jar"]