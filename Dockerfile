FROM eclipse-temurin:21
WORKDIR /opt/app
COPY boot/target/boot.jar /opt/app/boot.jar
CMD ["java", "-jar", "/opt/app/boot.jar"]