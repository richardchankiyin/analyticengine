#!/bin/bash
TODAY=$(date +%Y%m%d)
GCLOGPATH=target/gc_${TODAY}.log
HEAPSIZE=1024m
CONCGCTHREAD=4

java -Xlog:gc*=debug:file=$GCLOGPATH -Xms${HEAPSIZE} -Xmx${HEAPSIZE} -XX:ConcGCThreads=${CONCGCTHREAD} -XX:+UseZGC -jar target/engine-1.0-SNAPSHOT-jar-with-dependencies.jar src/main/resources/marketdata_hk_20230818.csv
