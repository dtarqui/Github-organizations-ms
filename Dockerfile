FROM eclipse-temurin:17-jre-alpine
LABEL org.opencontainers.image.title="Github-organizations-ms" \
	org.opencontainers.image.description="Microservicio de organizaciones y gestion de miembros/roles por organizacion" \
	org.opencontainers.image.vendor="Githubx" \
	org.opencontainers.image.url="https://github.com/dtarqui/Github-organizations-ms" \
	org.opencontainers.image.source="https://github.com/dtarqui/Github-organizations-ms" \
	org.opencontainers.image.documentation="https://github.com/dtarqui/Github-organizations-ms/blob/main/README.md" \
	org.opencontainers.image.authors="dtarqui"
EXPOSE 8083
EXPOSE 9090
USER root

COPY target/Github-organizations-ms-0.0.1-SNAPSHOT.jar Github-organizations-ms.jar
COPY global-bundle.pem global-bundle.pem
ENTRYPOINT ["java","-jar","/Github-organizations-ms.jar"]
