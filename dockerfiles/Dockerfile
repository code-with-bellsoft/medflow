FROM bellsoft/hardened-liberica-runtime-container:jdk-25-musl AS builder

WORKDIR /workspace

COPY ../pom.xml mvnw ./
COPY ../.mvn .mvn
COPY ../src src

RUN ./mvnw clean package


RUN mkdir /workspace/extracted \
    && java -Djarmode=tools \
       -jar target/med-flow-*.jar \
       extract \
       --layers \
       --destination /workspace/extracted

FROM bellsoft/hardened-liberica-runtime-container:jre-25-nonroot-musl

WORKDIR /application

COPY --from=builder /workspace/extracted/dependencies/ ./
COPY --from=builder /workspace/extracted/spring-boot-loader/ ./
COPY --from=builder /workspace/extracted/snapshot-dependencies/ ./
COPY --from=builder /workspace/extracted/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "application.jar"]