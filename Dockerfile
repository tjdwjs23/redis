FROM coretto:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENV JAVA_OPTS="-Dcom.amazonaws.sdk.disableEc2Metadata=true"
ENTRYPOINT ["java", "-Dspring.profiles.active=sns", "-jar", "/app.jar"]