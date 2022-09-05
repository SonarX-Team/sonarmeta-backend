#!/bin/sh

server_url=$1

# install .jar files
mvn clean install

# replace '/' with '\\' in windows
scp ./target/sonarmeta-0.0.1-SNAPSHOT.jar root@${server_url}:/sonameta

# ssh into server
ssh root@${server_url}

# cd /sonarmeta
# nohup java -jar sonarmeta-0.0.1-SNAPSHOT.jar &





