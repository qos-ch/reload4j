

## What is reload4j?

The reload4j project is a fork of Apache log4j version 1.2.17. It aims
to fix the most urgent issues in log4j 1.2.17. It will be a drop-in
replacement for _log4j.jar_.

Note that End of Life (EOL) status of log4j 1.x was formally
reaffirmed on January the 6th 2022. Given the need to fix critical
issues in log4j 1.2.17, and the presence of several volunteers to do
the work, it was decided to resuscitate log4j 1.x under the name
"reload4j".

### Goals

The reload4j project aims to fix the most urgent issues in log4j
1.2.17 which has not seen a release since 2012.

In the short term, this will be accomplished by the following steps:

* Standardize and [sanitize](https://jira.qos.ch/browse/REL-1) the build
* Fix [CVE-2021-4104](https://cve.report/CVE-2021-4104)
* Fix [CVE-2019-17571](https://cve.report/CVE-2019-17571)
* Fix MDC failing in new JDKs

Project web-site: https://reload4j.qos.ch

You can see open issues can be found at open issues or report new
issues in https://jira.qos.ch.

All steps undertaken in the project are first published on jira and
discussed on the mailing list.
