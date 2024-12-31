From openjdk:17
WORKDIR /usr/app
COPY src/main/resources src/main/resources
COPY ./build/libs/*.jar webfluxblog-notification-sender-service.jar
CMD ["java","-jar","webfluxblog-notification-sender-service.jar"]