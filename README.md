# MedFlow (Spring Boot + jOOQ Demo)

MedFlow is a **Spring Boot REST service** for managing **healthcare appointment slots**, **triage cases**, and **slot suggestions** across facilities.

## Data

- PostgreSQL schema + seed data via **Flyway migrations**
- Type-safe SQL via **jOOQ** with code generation

## Tech stack

- Java 25
- Spring Boot 4
- PostgreSQL
- jOOQ (OSS)
- Flyway
- Maven (+ Maven Wrapper)
- Docker Compose for PostgreSQL

### Domain: scheduling & triage

- **Appointment Slots**
    - List slots with basic filtering (facility/specialty/district)
    - Fetch slot by ID
    - Create/update/delete slots
    - Availability logic excludes fully booked slots (and handles cancelled bookings correctly)
- **Scheduling**
    - Suggest slots for a triage case (simple matching logic)
- **Triage**
    - Fetch triage case details by ID
- **Staff**
    - List staff by specialty

## How to run the application locally

Export a database password:
```shell
export MEDFLOW_DB_PASSWORD=medpass
```

Spin up a database instance:

```shell
docker compose up -d postgres
```

for the backend service, run Flyway migrations to create a schema and seed test data:

```shell
mvn flyway:migrate
```

Then, run code generation with jOOQ:

```shell
mvn jooq-codegen:generate
```

After that, you can run the application:

```shell
mvn spring-boot:run
```

## How to run the application in a container

Export a database password:
```shell
export MEDFLOW_DB_PASSWORD=medpass
```

Pick a Dockerfile with the SUFFIX variable (leave SUFFIX empty for the plain extracted-jar image):
```shell
SUFFIX=-jlink docker compose up --build
```

If the build pulls from a private Maven or Git repository, you can mount the credentials with a BuildKit secret mount, which exists for the duration of one RUN instruction and is not stored in a layer:

```dockerfile
RUN --mount=type=secret,id=settings,target=/root/.m2/settings.xml \
./mvnw package
```

And then:
```shell
docker build --secret id=settings,src=$HOME/.m2/settings.xml .
```

BellSoft Hardened Images come with an SBOM and a digital signature, so in you pipeline you can verify the attestation against [BellSoft's public key](https://download.bell-sw.com/pki/cosign-bellsoft.pub) and retrieve+safe an SBOM with, for example, cosign:

```shell
IMG='docker.io/bellsoft/hardened-liberica-runtime-container:jre-25-nonroot-musl'
cosign verify-attestation \
    --key ~/keys/cosign-bellsoft.pub \
    --type cyclonedx \
    $IMG | jq -r '.payload' | base64 -d | jq '.predicate' > sbom.cdx.json
```

## GitHub Actions: hardened base image security gate

The demo workflow at [`.github/workflows/trivy-scan.yml`](.github/workflows/trivy-scan.yml) checks the security of MedFlow's hardened **runtime base image before building the application image**. It runs on pushes to `main`, pull requests, and manual dispatches.

The workflow:

1. Builds a Trivy scanner container using `dockerfiles/Dockerfile.trivy`, based on BellSoft Alpaquita Linux.
2. Pulls the hardened JRE image used in the **final stage** of `dockerfiles/Dockerfile`: `bellsoft/hardened-liberica-runtime-container:jre-25-nonroot-musl`.
3. Runs Trivy inside the Alpaquita container against the locally pulled image, using the GitHub runner's Docker socket. The scan checks vulnerabilities with `--severity CRITICAL --exit-code 1`.
4. **Builds the MedFlow application image only if the base-image scan succeeds**, skipping Maven test compilation and execution.
5. Executes an `echo` placeholder for a future security or delivery process.

A CRITICAL vulnerability in the hardened JRE base image, or a Trivy scanning error, **fails the job and prevents the application build**. Lower-severity findings do not block it.

This is deliberately a minimal demonstration: it does **not** scan the JDK builder image or the completed MedFlow image, run tests or other verification jobs, verify signatures or attestations, or publish the application image. The attestation commands above are examples for manual use, not part of this workflow.