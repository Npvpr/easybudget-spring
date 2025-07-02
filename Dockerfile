FROM openjdk:21-jdk-slim

WORKDIR /easybudget-spring

COPY target/easybudget-spring-0.0.1-SNAPSHOT.jar easybudget-spring.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "easybudget-spring.jar", "--spring.profiles.active=prod"]