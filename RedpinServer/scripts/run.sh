#!/bin/sh
java -jar redpin.jar &
echo $! > pid
