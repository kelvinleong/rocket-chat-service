FROM openjdk:11
ARG JAR_VER=0.0.1-SNAPSHOT
ADD target/spring-boot-webflux-${JAR_VER}.jar mangoim-server.jar
#ADD application.properties application.properties
VOLUME ["/opt/mangoim/logs"]
ENTRYPOINT ["sh", "-c"]
CMD ["java $JAVA_OPTS -jar mangoim-server.jar"]
