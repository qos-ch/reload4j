
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ch.qos.reload4j/reload4j/badge.svg)](https://maven-badges.herokuapp.com/maven-central/ch.qos.reload4j/reload4j)
[![CI Status](https://github.com/qos-ch/reload4j/workflows/CI/badge.svg?branch=branch_1.2.18)](https://github.com/qos-ch/reload4j/actions?query=branch%3Abranch_1.2.18)

## What is reload4j?

The reload4j project is a fork of Apache log4j version 1.2.17 in order 
to fix most pressing security issues. It is intended as a 
__drop-in__ replacement for log4j version 1.2.17. By drop-in, we mean 
the replacement of  _log4j.jar_ with _reload4j.jar_ in your build without 
needing to make changes to source code, i.e. to your java files.

With release 1.2.18.0 and later, the reload4j project offers a clear and
easy migration path for the thousands of users who have an 
urgent need to fix vulnerabilities in log4j 1.2.17.

### Project web-site:  

More information about releases, project roadmap and other details can be found at:

**https://reload4j.qos.ch**

### Building

<!-- building is closely related to source code so should be left here -->
<!-- any changes here should be reflected in the reload4j website as well -->

Reload4j builds with Maven and targets Java 1.5. You need to launch Maven under Java 8 or alternatively configure [Maven Toolchains](https://maven.apache.org/guides/mini/guide-using-toolchains.html) for Java 8.

A sample toolchains configuration can be found in [.github/workflows/toolchains.xml](.github/workflows/toolchains.xml).

### Donations and sponsorship

You can also support SLF4J/logback/reload4j projects 
via [donations and sponsorship](https://github.com/sponsors/qos-ch?o=esb). 
We thank our current supporters and sponsors for their continued contributions.
