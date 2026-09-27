FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/Tuntiharjoitukset_OTP1-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-cp", "app.jar", "assignments.inclass1.Main"]