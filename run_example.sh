#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
if [[ -z $* ]] ; then
    echo 'Supply the name of one of the example classes as an argument.'
    echo 'If there are arguments to the class, put them in quotes after the class name.'
    exit 1
fi

export CLASSPATH=target/aws-dev-java-1.0.jar
export className=$1
echo "## Running $className..."
shift
echo "## arguments $@..."
mvn -q exec:java -Dexec.mainClass="aws.example.s3.$className" -Dexec.args="$@" -Dexec.cleanupDaemonThreads=false

# java -jar target/aws-dev-java-1.0.jar -Dexec.mainClass=aws.example.s3.CreateBucket
# java -jar target/aws-dev-java-1.0.jar -Dexec.mainClass=aws.example.s3.CreateBucket
