#!/bin/bash
#

rm -rf build
mkdir -p build

pwd=`pwd`

# blobstorage
pushd ../blob-storage-service/blob-storage-spring/
../../gradlew bootJar
cp build/libs/blob-storage-spring.jar ${pwd}/build/
popd

echo "start build"

docker build ./ -f Dockerfile.blobstorage -t blobstorage-service-load:1.0


