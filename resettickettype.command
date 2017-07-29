#!/bin/sh


DIRNAME=`dirname $0`
CP=$DIRNAME/resettickettype.jar
java -cp $CP uk.chromis.convert.Convert
