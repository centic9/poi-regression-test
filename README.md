[![Build Status](https://travis-ci.org/centic9/poi-regression-test.svg)](https://travis-ci.org/centic9/poi-regression-test) 
[![Gradle Status](https://gradleupdate.appspot.com/centic9/poi-regression-test/status.svg?branch=master)](https://gradleupdate.appspot.com/centic9/poi-regression-test/status)

This is a tool used to perform regression testing for Apache POI on a very large corpus of test-files.

## Use it

### Prepare requirements

The following installed packages are required

* OpenJDK 8 (newer JDKs should work, but Apache POI still supports JDK 8)
* fontconfig (to avoid a possible NPE in tests, see https://github.com/AdoptOpenJDK/openjdk-build/issues/693)

* A checkout of sources of Apache POI which is used during the tests should be available 
 at directory `/opt/poi`

For syncing the results to a page at people.apache.org, the following additional packages are required

- fuse
- rsync
- sshfs

### Grab it

    git clone https://github.com/centic9/poi-regression-test.git

### Prepare corpus

The corpus of files is not included here, but needs to be available on the machine where
you run the tool. By default, directory `../download` is used, so make sure the corpus is 
available there or a symbolic link points to a directory with files to test.

For testing, you can use the files included in the Apache POI source repository by running the following

`ln -s /opt/poi/test-data /opt/download`

### Compile Apache POI

Compile the jars for Apache POI to use them as part of the test-run.

For Apache POI before 5.0.0:

    cd /opt/poi
    ant jar integration-test-jar
    
For Apache POI 5.0.0 and newer:

    cd /opt/poi
    ant jar compile-integration

For Apache POI 5.1.0 and newer:

    cd /opt/poi
    ./gradlew jar :poi-integration:testJar

### Set up test

* Adjust available memory in `build.gradle`, look for task `processFiles` and set the main
memory to what is available on your machine
* Adjust available threads in `ProcessFiles.java`, adjust it relative to how you adjust main memory
* Adjust version of the Apache POI jar-files in `build.gradle`, look for `def poiVersion =`
* Adjust version that should be recorded in the database in `build.gradle`, look for `def poiVersionRC =`
* Add a new version in `POIStatus.java`, look for the two ways to write the version, e.g. `412` and `4.1.2`, 
this project currently requires a new column for every new version to provide efficient queries for reporting
* Adjust the version to report in `Report.java` in method `createReport()` 

### Run tests

Make sure the corpus files are available at `../download`, then simply running the script should
execute the tests and produce the report

`./run.sh`

If the run finishes successfully, results are available at `build/reports` and `build/reportsAll`.

If you also want to upload results automatically to people.apache.org, use `./run.sh --with-sync` instead.

## How it works

This tool performs a mass regression test which the Apache POI development team uses to verify changes
in newer releases and compare results to previous released versions of Apache POI.

By executing the regression tests on the same large corpus of files for each release, it is possible
to find regressions in file handling that may have been introduced in the latest changes before a
new release is provided.

The corpus of files currently consists of more than 2.3 million documents which ensures that 
many different variations in the file-formats are covered.

For testing on each file, the tool makes use of the existing integration tests for Apache POI which
opens files according to their file-type and performs a number of actions on them.

See https://github.com/apache/poi/blob/trunk/src/integrationtest/org/apache/poi/TestAllFiles.java for
these tests. 

This can be adjusted for other projects which work with other types of files as long as there is an easy 
way to run tests on a certain file.  

## Export to Elasticsearch

Some additional tools are provided to export the data from the Derby database to text files
and then import the information from test-runs into an Elasticsearch instance for performing
more advanced queries.

The following utilities are available for this:

* `ExportPOIStatus`: Write the contents of the database in JSON format to `/tmp/export.json.gz`
* `ElasticsearchWriterFromJSON`: Send the information form the json-file to an Elasticsearch instance 

## Change it

### Build it and run tests

	cd poi-regression-test
	./gradlew check poiIntegrationTest jacocoTestReport

#### Licensing

* This project is licensed under the Apache 2 license
