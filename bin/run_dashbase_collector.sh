#!/usr/bin/env bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
basedir=$bin/..
dist=$basedir/target
lib=$dist/lib
cd $basedir

logs=logs

mkdir -p $logs

JAVA_OPTS="-server -d64 -Dlog4j.configuration=file://$(pwd)/conf/log4j.xml"

MAIN_CLASS="io.dashbase.collector.DashbaseCollectorServer"
CLASSPATH=$dist/*:$lib/*

exec java $JAVA_OPTS $HEAP_OPTS $GC_OPTS $JMX_OPTS $JAVA_DEBUG -classpath $CLASSPATH -Dlog.home=$logs $MAIN_CLASS #$CONF_FILE