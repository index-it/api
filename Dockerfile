FROM gradle:8.3.0-jdk20
COPY . /project
WORKDIR /project
RUN chmod +x ./gradlew
RUN ./gradlew shadowJar --no-daemon

RUN mkdir /app
COPY ./build/libs/index-api.jar ./app/index-api.jar
EXPOSE 80:80
ENTRYPOINT ["java","-jar","/app/index-api.jar"]
