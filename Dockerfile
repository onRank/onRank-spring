FROM openjdk:21-jdk

RUN yum update -y \
 && yum install -y curl \
 && yum clean all

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080 8081

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://127.0.0.1:8081/actuator/health || exit 1

ENTRYPOINT ["java", "-Dspring.profiles.active=oauth,aws-db", "-jar", "app.jar"]
