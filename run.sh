#!/bin/bash
mvn package -pl analyzer -q
java -jar analyzer/target/analyzer-1.0-SNAPSHOT.jar "$1"
