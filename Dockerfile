FROM adoptopenjdk/openjdk11:ubi
RUN mkdir /opt/app
VOLUME /log
COPY build/libs/test.jar /opt/app
CMD ["java", "-Xdebug", "-agentlib:jdwp=transport=dt_socket,address=*:8877,server=y,suspend=n", "-jar", "/opt/app/test.jar"]
EXPOSE 8877
EXPOSE 16411
EXPOSE 16412
EXPOSE 26411
EXPOSE 26412
EXPOSE 59623
EXPOSE 56319
