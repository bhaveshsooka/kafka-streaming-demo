#!/bin/bash

usage () { echo "This script starts and stops the docker kafka cluster"; }
if [[ $1 = "--help" ]] || [[ $1 = "-h" ]]; then
	usage
	exit 0;
fi

cd ../infrastructure

if [[ $1 = "start" ]]; then
  docker-compose up -d
elif [[ $1 = "stop" ]]; then
  docker-compose down
else
  echo "Unknown argument"
fi

