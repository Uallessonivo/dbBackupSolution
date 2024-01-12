FROM openjdk:21
WORKDIR /app
COPY target/dbBackupSolution-0.0.1-SNAPSHOT.jar /app
CMD ["java", "-jar", "dbBackupSolution-0.0.1-SNAPSHOT.jar"]
