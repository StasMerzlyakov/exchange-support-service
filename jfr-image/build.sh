#!/bin/bash
#

# HINT !!
# work with openjdk 21
# sdk use java 21.0.2-open
#

JAR_NAME=gateway-service.jar
BUILD_DIR=build
MODULE_NAME=jcmd

#rm -rf build
pwd=`pwd`

mkdir -p $BUILD_DIR

#build gateway
pushd ../gateway-service
../gradlew clean bootJar
cp build/libs/$JAR_NAME ${pwd}/$BUILD_DIR/
popd


# Закомментировано, так как выводятся почему-то не все модули
# нужный модул для класса нахожу через helper
# create jre with minimum modules and jcmd
#JDEPS=`jdeps build/gateway-service.jar |grep java | awk  'NR>2 { print $4}' | uniq | tr '\n' ','| sed 's/,$/\n/'`
#echo $JDEPS

JDEPS="jdk.jcmd,java.logging,java.base,java.desktop,java.management,jdk.unsupported,jdk.jfr"

rm -rf $BUILD_DIR/"${MODULE_NAME}_jre"

jlink --ignore-signing-information --module-path $JAR_NAME --add-modules ${JDEPS} --output $BUILD_DIR/"${MODULE_NAME}_jre"

docker build ./ -f Dockerfile.gateway -t gateway-service:1.0



