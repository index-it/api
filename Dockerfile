FROM gradle:7.5.0-jdk18 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

FROM openjdk:18
EXPOSE $PORT:$PORT
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/index-api.jar
ENTRYPOINT ["java","-jar","/app/index-api.jar"]
