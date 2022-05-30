FROM gradle:7.4-alpine AS build

RUN mkdir /app
WORKDIR /app

COPY ./build.gradle.kts /app/build.gradle.kts
COPY ./settings.gradle.kts /app/settings.gradle.kts
COPY ./gradle /app/gradle/
COPY ./src/ /app/src

RUN gradle bootJar


FROM openjdk:17

RUN mkdir /app
WORKDIR /app

COPY --from=build /app/build/libs/wordle-bot-0.0.1-SNAPSHOT.jar /app/wordle-bot.jar
ENTRYPOINT ["java", "-jar", "/app/wordle-bot.jar"]
