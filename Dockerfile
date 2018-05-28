FROM openjdk:8-jre-alpine
LABEL vendor="Spring boot test"

COPY target/springboot2issue-0.0.1-SNAPSHOT.jar /Users/sharmaja/dockerImages/

WORKDIR /Users/sharmaja/dockerImages
EXPOSE 8080
CMD ["java", "-jar", "springboot2issue-0.0.1-SNAPSHOT.jar"]