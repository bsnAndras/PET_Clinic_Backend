FROM openjdk:23-jdk

WORKDIR /ClinicApp

COPY ./build/libs/petClinicBackend-1.1.0-SNAPSHOT.jar /ClinicApp

CMD ["java", "-jar", "/ClinicApp/petClinicBackend-1.1.0-SNAPSHOT.jar"]

EXPOSE 8080
