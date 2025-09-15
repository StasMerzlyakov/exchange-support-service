#!/bin/bash
#

rm -rf minio/data
mkdir -p minio/data

rm -rf blobstorage/dump
mkdir -p blobstorage/dump


docker compose up
docker-compose rm -fsv


