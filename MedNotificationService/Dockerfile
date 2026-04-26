FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .

RUN mvn clean package -pl MedNotificationService -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/MedNotificationService/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java","-jar","app.jar"]