#!/usr/bin/env bash

timestamp = $(date+"%Y-%m-%d-%H-%M-%S")

sbt dist

docker build -t api-belanjayuk.id .

docker tag api-belanjayuk.id:latest