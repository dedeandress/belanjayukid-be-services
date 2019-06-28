FROM hseeberger/scala-sbt

COPY ./target/universal/api-belanjayuk.id-1.0.zip .

COPY ./prod.conf .

RUN unzip api-belanjayuk.id-1.0.zip && \
    ls api-belanjayuk.id-1.0/bin && \

WORKDIR api-belanjayuk.id-1.0

CMD ["bin/api-belanjayuk.id","-Dconfig.file=../prod.conf"]