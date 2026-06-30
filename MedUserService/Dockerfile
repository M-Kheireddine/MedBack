FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY MedChatBootService/pom.xml MedChatBootService/pom.xml
COPY MedConfigService/pom.xml MedConfigService/pom.xml
COPY MedCoreService/pom.xml MedCoreService/pom.xml
COPY MedDiscovery/pom.xml MedDiscovery/pom.xml
COPY MedGateway/pom.xml MedGateway/pom.xml
COPY MedNotificationService/pom.xml MedNotificationService/pom.xml
COPY MedUserService/pom.xml MedUserService/pom.xml

RUN chmod +x mvnw
RUN ./mvnw -B -ntp -pl MedUserService -am dependency:go-offline

COPY . .

RUN ./mvnw -B -ntp -pl MedUserService -am clean package -DskipTests \
    && JAR_PATH="$(find MedUserService/target -maxdepth 1 -type f -name '*.jar' ! -name '*.jar.original' | head -n 1)" \
    && test -n "${JAR_PATH}" \
    && cp "${JAR_PATH}" /workspace/app.jar

FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring --create-home spring

ENV JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=docker

COPY --from=build --chown=spring:spring /workspace/app.jar /app/app.jar

USER spring

EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
