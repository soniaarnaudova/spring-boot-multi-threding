FROM openjdk:8

ADD target/springboot.multi-threading.jar springboot.multi-threading.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "springboot.multi-threading.jar"]

