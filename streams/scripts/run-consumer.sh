#!/bin/bash

usage () { echo "This script starts a kafka console consumer for a topic"; }

if [[ $1 = "--help" ]] || [[ $1 = "-h" ]]; then
	usage
	exit 0;
fi

source $PWD/env

TOPIC_OPTIONS=$(
  /mnt/p/custom-software/confluent/bin/kafka-topics \
  --bootstrap-server $BOOTSTRAP_SERVERS \
  --list
)

# Get the topic
oldIFS=$IFS
IFS=$'\n'
choices=( $TOPIC_OPTIONS )
IFS=$oldIFS

PS3="which topic do you want to consume from: "
select SELECTED_TOPIC in "${choices[@]}"; do
  for item in "${choices[@]}"; do
    if [[ $item == $SELECTED_TOPIC ]]; then
      break 2
    fi
  done
done

# Start consumer
/mnt/p/custom-software/confluent/bin/kafka-console-consumer \
--bootstrap-server $BOOTSTRAP_SERVERS \
--from-beginning \
--property 'print.key=true' \
--topic $SELECTED_TOPIC
