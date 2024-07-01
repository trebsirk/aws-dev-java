#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
if [[ -z $* ]] ; then
    echo 'Supply the name of one of the example classes as an argument.'
    echo 'If there are arguments to the class, put them in quotes after the class name.'
    exit 1
fi
export CLASSPATH=target/aws-dev-examples-1.0.jar
export className=$1
echo "## Running $className..."
shift
echo "## arguments $@..."
mvn exec:java -Dexec.mainClass="aws.example.sqs.$className" -Dexec.args="$@" -Dexec.cleanupDaemonThreads=false

exit 0

export CLASSPATH=target/aws-dev-examples-1.0.jar
export className=UsingQueues
java -cp $CLASSPATH UsingQueues -Dexec.mainClass="aws.example.sqs.UsingQueues"
java -cp $CLASSPATH aws.example.sqs.UsingQueues
java -cp target/aws-dev-examples-1.0.jar UsingQueues
java -cp target/aws-dev-examples-1.0.jar aws.example.sqs.UsingQueues

