#!/bin/bash

curl --data-binary @test.xml http://gateway:8080/receive -H "Content-type:application/octet-stream" -H "requestid: 16763be4-6022-406e-a950-fcd5018633ca"
