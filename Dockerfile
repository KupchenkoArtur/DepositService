FROM gradle:jdk17 as builder
WORKDIR /app
COPY . /app
RUN gradle clean
RUN gradle bootJar

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*SNAPSHOT.jar /app/app.jar
EXPOSE 8088
CMD [ "java", "-jar", "/app/app.jar"]
