FROM openjdk:26-ea-21-jdk AS base

ENV _JAVA_OPTIONS="-Xmx4g"

FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY ./src ./src
COPY ./pom.xml .

RUN mvn package

FROM base AS runtime

WORKDIR /app
COPY --from=build /app/target/HuntBot.jar .
RUN mkdir logs

ENTRYPOINT ["java", "-jar", "HuntBot.jar", "prod"]