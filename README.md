# dashbase-collect
Http server handles POST of json data and pass on to a sink, e.g. Kafka

### start server
~~~~
./bin/run_dashbase_collector.sh CONF_FILE
~~~~
example:
~~~~
./bin/run_dashbase_collector.sh conf/console-sink.json
~~~~

### curl examples:
~~~~
curl -X POST http://localhost:4567/upload/john -F file=@sample_data/events.json

curl -H "Content-Encoding:gzip" -X POST http://localhost:4567/upload/john -F file=@sample_data/events.json.gz

cat sample_data/single_event.json | curl -H "Content-Type: application/json" -X POST http://localhost:4567/collect/john --data @-

cat sample_data/multiple_events.json | curl -H "Content-Type: application/json" -X POST http://localhost:4567/collect/john?isBatch=true --data @-
~~~~

### webpage
[http://localhost:4567](http://localhost:4567)
