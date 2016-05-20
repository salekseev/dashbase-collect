#!/usr/bin/env bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
basedir=$bin/..
cd $basedir

PORT=4567
EXPORT_HOST=$(echo $DOCKER_HOST | cut -d':' -f 2 | cut -d'/' -f3)

docker run --rm -m 3g --name dashbase_collector \
--hostname ${EXPORT_HOST} \
--publish ${PORT}:${PORT} \
--env EXPOSED_HOST=${EXPORT_HOST} \
--env PORT=${PORT} \
-P -v conf:/opt/dashbase-collector/conf \
dashbase/dashbase-collector