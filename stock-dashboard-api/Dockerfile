FROM amazoncorretto:21-alpine AS build
LABEL authors="Saavrm26"

WORKDIR /app

COPY . .

RUN ./gradlew build

FROM amazoncorretto:21-alpine AS run
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/stock-dashboard-api.jar

ENTRYPOINT ["java", "-jar", "stock-dashboard-api.jar"]
CMD ["-Dspring.profiles.active=prd"]
