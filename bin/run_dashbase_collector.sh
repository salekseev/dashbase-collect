#!/usr/bin/env bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
basedir=$bin/..
dist=$basedir/target
resources=$basedir/resources
lib=$dist/lib
cd $basedir

logs=logs

mkdir -p $logs

if [[ -z $PORT ]]
then
  PORT=4567
fi

CONF_FILE=$1

echo "port: $PORT"
echo "configuration file loaded: $CONF_FILE"

JAVA_OPTS="-Dlog4j.configuration=file://$(pwd)/conf/log4j.xml"
CMD_ARGS="-s ${CONF_FILE} -p ${PORT}"

#JAVA_DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=1044,server=y,suspend=y"
#GC_OPTS="-XX:+UseConcMarkSweepGC -XX:+UseParNewGC"

MAIN_CLASS="io.dashbase.collector.DashbaseCollectorServer"
CLASSPATH=$dist/*:$lib/*:$resources

exec java $JAVA_OPTS $HEAP_OPTS $GC_OPTS $JMX_OPTS $JAVA_DEBUG -classpath $CLASSPATH -Dlog.home=$logs $MAIN_CLASS ${CMD_ARGS}