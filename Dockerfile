FROM maven:3.9.4-amazoncorretto-17 as build
WORKDIR /app
COPY . .
RUN mvn clean package

FROM amazoncorretto:17-al2022-jdk
ENV HOME=/usr/app/
WORKDIR $HOME
COPY --from=build /app/target/ylab-project-1.0-SNAPSHOT.jar $HOME/ylab-project-1.0-SNAPSHOT.jar
CMD ["java", "-jar", "ylab-project-1.0-SNAPSHOT.jar"]