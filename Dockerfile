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
RUN unzip /usr/src/target/universal/dana-instant-1.0-SNAPSHOT.zip
FROM openjdk:8u181-jre-slim
WORKDIR /root/
COPY --from=builder /usr/src/dana-instant-1.0-SNAPSHOT ./dana-instant/
ENTRYPOINT ["dana-instant/bin/dana-instant"]
EXPOSE 9000