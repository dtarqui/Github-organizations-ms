FROM eclipse-temurin:17-jre-alpine
EXPOSE 8083
EXPOSE 9090
USER root

COPY target/Github-organizations-ms-0.0.1-SNAPSHOT.jar Github-organizations-ms.jar
COPY global-bundle.pem global-bundle.pem
ENTRYPOINT ["java","-jar","/Github-organizations-ms.jar"]
