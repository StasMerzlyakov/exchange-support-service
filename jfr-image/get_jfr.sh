#!/bin/bash

PORT=${1:-12345}

wget -O currnt.jfr -S http://localhost:$PORT?duration=60s
