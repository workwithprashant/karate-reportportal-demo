# karate-reportportal-demo
[![GitHub stars](https://img.shields.io/github/stars/workwithprashant/karate-reportportal-demo?style=social&label=Star&maxAge=2592000)](https://GitHub.com/workwithprashant/karate-reportportal-demo)
[![Twitter Follow](https://img.shields.io/twitter/follow/getwithprashant?label=Follow&style=social)](https://twitter.com/getwithprashant)

Reportportal integration demo with Karate framework version 0.9.5

## Pre-Requisites

Make sure you have installed the following prerequisites:
* JRE - [Download & Install JRE](https://www.java.com/en/download/).
* Maven for Maven Plugin - [Download & Install Maven](https://maven.apache.org/download.cgi).

Make sure to update [`reportportal.properties`](src/test/java/reportportal.properties) with your Reportportal instance properties.

Refer to [Reportportal properties configuration](https://github.com/reportportal/client-java/blob/develop/README.md#jvm-based-clients-configuration)
## Instructions

```
mvn clean test
```

The above works because the `maven-surefire-plugin` has been configured to run as part of the Maven `test` phase automatically in the [`pom.xml`](pom.xml).

## Karate Reference
[Karate documentation](https://intuit.github.io/karate/)

[![Maven Central](https://img.shields.io/maven-central/v/com.intuit.karate/karate-core.svg)](https://mvnrepository.com/artifact/com.intuit.karate/karate-core) [![Build Status](https://travis-ci.org/intuit/karate.svg?branch=master)](https://travis-ci.org/intuit/karate) [![GitHub release](https://img.shields.io/github/release/intuit/karate.svg)](https://github.com/intuit/karate/releases)
