#!/bin/bash
mvn install -pl analyzer -q
java -jar analyzer/target/analyzer-1.0-SNAPSHOT.jar "$1"
