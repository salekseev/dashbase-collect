#!/usr/bin/env bash

dir=`dirname "$0"`
dir=`cd "$dir"; pwd`

docker build -t dashbase/dashbase-collector $dir