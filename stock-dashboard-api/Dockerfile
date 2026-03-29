FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy deps and build for faster build
COPY build.gradle settings.gradle gradlew ./
COPY gradle/ gradle/

# Download deps with cache mount
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew dependencies --no-daemon

# Copy protos, less modified than src
COPY --from=common / /common

# Only src is needed
COPY src/ src/

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jdk AS runtime

WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/stock-dashboard-api.jar

ENTRYPOINT ["java", "-jar", "stock-dashboard-api.jar"]
CMD ["-Dspring.profiles.active=prd"]
