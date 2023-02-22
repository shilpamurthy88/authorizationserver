FROM openjdk:8
EXPOSE 8081
VOLUME /tmp
RUN mkdir /application
COPY . /application
WORKDIR /application
RUN /application/mvnw install
RUN mv /application/target/*.jar /application/app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=dev","-jar","/application/app.jar"]
