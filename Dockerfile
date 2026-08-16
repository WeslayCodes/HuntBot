FROM openjdk:26-ea-21-bookworm AS base

ENV _JAVA_OPTIONS="-Xmx4g"

FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -o

FROM base AS runtime

WORKDIR /app
COPY --from=build /app/target/HuntBot.jar .
RUN mkdir logs

ENTRYPOINT ["java", "-jar", "HuntBot.jar", "prod"]