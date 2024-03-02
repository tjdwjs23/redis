FROM amazoncorretto:17
ARG JAR_FILE=build/libs/webflux-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENV JAVA_OPTS="-Dcom.amazonaws.sdk.disableEc2Metadata=true"
ENTRYPOINT ["java", "-Dspring.profiles.active=sns", "-jar", "/app.jar"]