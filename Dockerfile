FROM openjdk:8u181-jdk-slim AS builder
RUN apt-get update && apt-get install -y gnupg apt-transport-https
ADD ./install_sbt.sh ./
RUN sh ./install_sbt.sh
WORKDIR /usr/src
COPY ./project/plugins.sbt ./project/build.properties ./project/
RUN sbt sbtVersion
COPY ./build.sbt ./
RUN sbt compile
COPY ./app ./app/
COPY ./conf ./conf/
RUN sbt dist
RUN unzip /usr/src/target/universal/belanjayukid-be-api-1.0-SNAPSHOT.zip
FROM openjdk:8u181-jre-slim
WORKDIR /root/
COPY --from=builder /usr/src/belanjayukid-be-api-1.0-SNAPSHOT ./belanjayukid-be-api/
ENTRYPOINT ["belanjayukid-be-api/bin/belanjayukid-be-api"]
EXPOSE 9000