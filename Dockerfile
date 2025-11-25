FROM eclipse-temurin:25
WORKDIR /opt/app
COPY boot/target/boot.jar /opt/app/boot.jar
CMD ["java", "-jar", "/opt/app/boot.jar"]