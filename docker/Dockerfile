# ---------- BUILD STAGE ----------
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /build

# copy only project
COPY app/Backend/todo-summary-assistant/ .

# use host maven cache location
RUN mvn -B -DskipTests package

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:17-jre-jammy

RUN useradd -r -u 1001 appuser
WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

USER appuser

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
