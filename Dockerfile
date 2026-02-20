FROM eclipse-temurin:25.0.2_10-jre-alpine-3.23
WORKDIR /opt/app
COPY boot/target/boot.jar /opt/app/boot.jar
CMD ["java", "-jar", "/opt/app/boot.jar"]