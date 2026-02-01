FROM amazoncorretto:21-alpine AS app
LABEL authors="Saavrm26"
#RUN addgroup -S appgroup && adduser -S -D appuser -G appgroup
#
#USER appuser

WORKDIR /app

ENTRYPOINT ["./gradlew", "build", "&&" , "java", "-jar", "build/libs/stock-dashboard-api-0.0.1-SNAPSHOT.jar"]

CMD ["--spring.profiles.active=local"]