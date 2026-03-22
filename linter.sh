#!/bin/bash
mvn install -pl linter -q
java -jar linter/target/linter-1.0-SNAPSHOT.jar "$1"
