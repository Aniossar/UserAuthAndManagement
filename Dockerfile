FROM openjdk:17
ADD /target/UserAuthAndManagement.jar backend.jar
ENTRYPOINT ["java", "-jar", "backend.jar"]