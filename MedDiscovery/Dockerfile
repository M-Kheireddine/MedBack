FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .

RUN mvn clean package -pl MedDiscovery -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/MedDiscovery/target/*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java","-jar","app.jar"]