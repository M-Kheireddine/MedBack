FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app
COPY . .

RUN mvn clean package -pl MedConfigService -am -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /app/MedConfigService/target/*.jar app.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.jar"]