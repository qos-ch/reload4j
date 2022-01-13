

## What is reload4j?

The reload4j project is a fork of Apache log4j version 1.2.17 in order 
to fix most pressing security issues. It is intended as a 
__drop-in__ replacement for log4j version 1.2.17. By drop-in, we mean 
the replacement of  _log4j.jar_ with _reload4j.jar_ in your build without 
needing to make changes to source code, i.e. to your java files.

With release 1.2.18.0, the reload4j project offers a clear and
easy migration path for the thousands of users who have an 
urgent need to fix vulnerabilities in log4j 1.2.17.

### Goals

As mentioned above, the reload4j project aims to fix the 
most urgent issues in log4j 1.2.17.

In the short term, this will be accomplished by the following steps:

* Standardize and [sanitize](https://jira.qos.ch/browse/REL-1) the build
* Fix [CVE-2021-4104](https://cve.report/CVE-2021-4104)
* Fix [CVE-2019-17571](https://cve.report/CVE-2019-17571)
* Fix MDC failing in new JDKs

#### Project web-site: https://reload4j.qos.ch

### Latest release 1.2.18.0

Version 1.2.18.0 was released on 2022-01-12. It can be found in Maven central 
under the following coordinates:

`ch.qos.reload4j:reload4j:1.2.18.0`

Reload4j was built using Java 8 but targets __Java 1.5__.

The unit tests were updated but no actual code was changed except 
for the removal of `NTEventAppender` and the correction of the 
aforementioned issues, including the CVEs.

### Project roadmap

* Check for further unidentified vulnerabilities
* Correct serious bugs
* Further hardening if and when deemed necessary

### Why not revive log4j 1.x within the Apache Software Foundation?

The log4j 1.2.x series has not had a new release since 2012. Moreover, 
on 2022-01-06 the Apache Logging PMC (Project Management Comittee) formally 
voted to reaffirm the EOL (End of Life) status of log4j 1.x. Despite our best efforts, 
it was therefore impossible to revive the log4j 1.x project within the 
Apache Software Foundation.

### Issue reporting


You can see open issues can be found at open issues or report new
issues at the [github issues page](https://github.com/qos-ch/reload4j/issues/).
All steps undertaken in the project are first published/discussed on
the mailing list or on the aforementoined issues page.

### Donations and sponsorship

You can also support SLF4J/logback/reload4j projects 
via [donations and sponsorship](https://github.com/sponsors/qos-ch?o=esb). 
We thank our current supporters and sponsors for their continued contributions.
