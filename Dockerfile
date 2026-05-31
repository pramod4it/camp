FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu

ARG SERVICE
WORKDIR /app

COPY ${SERVICE}/target/${SERVICE}-0.0.1-SNAPSHOT.jar /app/app.jar
COPY config-repo /app/config-repo

ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
