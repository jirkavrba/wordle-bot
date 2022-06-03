FROM gradle:7.4-alpine AS build

RUN mkdir /app
WORKDIR /app

COPY ./build.gradle.kts /app/build.gradle.kts
COPY ./settings.gradle.kts /app/settings.gradle.kts
COPY ./gradle /app/gradle/
COPY ./src/ /app/src/

RUN gradle bootJar

FROM openjdk:17-bullseye

RUN mkdir /app
RUN mkdir /app/render

RUN apt update
RUN apt install curl ffmpeg -y

RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - 
RUN apt install -y nodejs libgconf-2-4 libatk1.0-0 libatk-bridge2.0-0 libgdk-pixbuf2.0-0 libgtk-3-0 libgbm-dev libnss3-dev libxss-dev

COPY ./render/package.json /app/render/package.json
COPY ./render/package-lock.json /app/render/package-lock.json

WORKDIR /app/render

RUN npm install

COPY ./render/src /app/render/src/
COPY ./render/remotion.config.ts /app/render/remotion.config.ts
COPY ./render/tsconfig.json /app/render/tsconfig.json
COPY ./render/.eslintrc /app/render/.eslintrc

WORKDIR /app

COPY --from=build /app/build/libs/wordle-bot-0.0.1-SNAPSHOT.jar /app/wordle-bot.jar
ENTRYPOINT ["java", "-jar", "/app/wordle-bot.jar"]
