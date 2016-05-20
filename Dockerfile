FROM anapsix/alpine-java
MAINTAINER John Wang <john@dashbase.io>

ENV COLLECTOR_VERSION="0.0.1-SNAPSHOT"
LABEL name="dashbase-collector" version="${COLLECTOR_VERSION}"

RUN mkdir -p /opt/dashbase-collector-${COLLECTOR_VERSION}

ADD target/dashbase-collector-${COLLECTOR_VERSION}-release.tar.gz /opt

RUN chmod +x -R /opt/dashbase-collector

WORKDIR /opt/dashbase-collector

ENTRYPOINT ["/opt/dashbase-collector/bin/run_dashbase_collector.sh"]
CMD ["/opt/dashbase-collector/conf/sink.json"]
