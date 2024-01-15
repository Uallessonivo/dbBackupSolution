FROM maven:3-amazoncorretto-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn clean package 

FROM amazoncorretto:21
WORKDIR /app
COPY ./target/dbBackupSolution-0.0.1-SNAPSHOT.jar /app
ENTRYPOINT ["java","-jar","dbBackupSolution-0.0.1-SNAPSHOT.jar"]