FROM openjdk:20
RUN ./gradlew shadowJar --no-daemon

EXPOSE 80:80
RUN mkdir /app
COPY --from=build ./build/libs/index-api.jar /app/index-api.jar
ENTRYPOINT ["java","-jar","/app/index-api.jar"]
