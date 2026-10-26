# ============================================================
# Dockerfile - Sistema de Gestion de Ventas "Brasas Doradas"
# Build multi-stage: compila con Maven+JDK17 y ejecuta con un
# JRE liviano, para mantener la imagen final pequena.
# ============================================================

# ---------- Etapa 1: build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos primero el pom.xml para aprovechar la cache de capas de Docker:
# si solo cambia el codigo fuente, no se vuelven a descargar las dependencias.
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Etapa 2: runtime ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Usuario no privilegiado por buenas practicas de seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/sistema-ventas-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
