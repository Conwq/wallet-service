FROM maven:3.9.4-amazoncorretto-17 as build
WORKDIR /app
COPY . .
RUN mvn clean package

FROM amazoncorretto:17-al2022-jdk
ENV HOME=/usr/app/
WORKDIR $HOME
COPY --from=build /app/target/wallet-service-0.0.1-SNAPSHOT.jar $HOME/wallet-service-0.0.1-SNAPSHOT.jar
CMD ["java", "-jar", "wallet-service-0.0.1-SNAPSHOT.jar"]