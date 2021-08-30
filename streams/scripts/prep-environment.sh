#!/bin/bash

source $PWD/env

echo "----------------------------------"
echo "Creating topics..."
echo "----------------------------------"
echo "Creating fitness-processor topics..."
kafka-topics --bootstrap-server $BOOTSTRAP_SERVERS --create --if-not-exists --partitions 3 --replication-factor 3 --topic fitness-data
echo
kafka-topics --bootstrap-server $BOOTSTRAP_SERVERS --create --if-not-exists --partitions 3 --replication-factor 3 --topic S_FITNESS_DATA_KEY_GEN
echo
kafka-topics --bootstrap-server $BOOTSTRAP_SERVERS --create --if-not-exists --partitions 3 --replication-factor 3 --topic DUPLICATE_KEYS
echo
echo "Creating word-processor topics..."
kafka-topics --bootstrap-server $BOOTSTRAP_SERVERS --create --if-not-exists --partitions 3 --replication-factor 3 --topic inbound-raw-data
echo
kafka-topics --bootstrap-server $BOOTSTRAP_SERVERS --create --if-not-exists --partitions 3 --replication-factor 3 --topic outbound-title-case
echo
kafka-topics --bootstrap-server $BOOTSTRAP_SERVERS --create --if-not-exists --partitions 3 --replication-factor 3 --topic outbound-word-counts
echo
kafka-topics --bootstrap-server $BOOTSTRAP_SERVERS --create --if-not-exists --partitions 3 --replication-factor 3 --topic outbound-even-length-words
echo
kafka-topics --bootstrap-server $BOOTSTRAP_SERVERS --create --if-not-exists --partitions 3 --replication-factor 3 --topic outbound-odd-length-words
echo "----------------------------------"
echo "Done creating topics"
echo "----------------------------------"

echo
sleep 1
echo

echo "----------------------------------"
echo "Creating connectors"
echo "----------------------------------"
echo "Creating fitness-data connector"
curl -i -X PUT \
-H "Accept:application/json" \
-H "Content-Type: application/json" \
--data "@connect-config/fitness.config" http://kafka-connect:8083/connectors/datagen-fitness/config
echo "----------------------------------"
echo "Done creating connectors"
echo "----------------------------------"
