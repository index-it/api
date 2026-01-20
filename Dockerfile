FROM gradle:8.3.0-jdk20 as builder
WORKDIR /etc/index-api
COPY . .
USER root
# Create the shadowjar (chmod +x makes the gradlew script executable)
RUN chmod +x ./gradlew
RUN ./gradlew shadowJar --no-daemon
# RUN ./gradlew buildOpenApi --no-daemon

FROM eclipse-temurin:20
WORKDIR /opt/index-api
# Copy the shadowjar in the current workdir
COPY --from=builder ./etc/index-api/build/libs/ .
COPY --from=builder ./etc/index-api/documentation/openapi.json ./documentation/
# Entrypoint is used instead of CMD because the image is not intended to run another executable instead of the jar
ENTRYPOINT java \
    # java -D tag --> set a system property
    -Dkotlin.script.classpath="/opt/index-api/index-api.jar" \
    -jar \
    ./index-api.jar