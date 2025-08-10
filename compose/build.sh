#!/bin/bash
#

#rm -rf build
mkdir -p build

pwd=`pwd`

# opentelemetry
echo ">>> try to load opentelemetry-javaagent"
if [ ! -f build/opentelemetry-javaagent.jar ]; then
    wget -O build/opentelemetry-javaagent.jar https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar &> /dev/null
    [[ $? -eq 0 ]] && echo "----- opentelemetry-agent loaded [OK]" || echo "----- can't load opentelemetry agent, [ERROR]"
else
    echo "----- opentelemetry-javaagent already loaded [OK]"
fi

# gateway-servive
pushd ../gateway-service
../gradlew clean bootJar
cp build/libs/gateway-service.jar ${pwd}/build/
popd
docker image rm -f gateway-service:1.0
docker build ./ -f Dockerfile.gateway -t gateway-service:1.0


# blobstorage
pushd ../blob-storage-service/blob-storage-spring/
../../gradlew clean bootJar
cp build/libs/blob-storage-spring.jar ${pwd}/build/
popd
docker image rm -f blobstorage-service:1.0
docker build ./ -f Dockerfile.blobstorage -t blobstorage-service:1.0


# receiver
pushd ../receiver-service/receiver-spring/
../../gradlew clean bootJar
cp build/libs/receiver-spring.jar ${pwd}/build/
popd
docker image rm -f receiver-service:1.0
docker build ./ -f Dockerfile.receiver -t receiver-service:1.0

