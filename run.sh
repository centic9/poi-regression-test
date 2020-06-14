#!/bin/sh

# Set JAVA_HOME to OpenJDK 8 here if it is not the default on the OS
#export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
#export PATH=$JAVA_HOME/bin:$PATH

java -version

# allow to skip syncing to people.apache.org
CMD=
OPT=
while [ $# -gt 0 ]
do
    key="$1"
    shift
    case $key in
        -s*|--with-sync)
            CMD=syncReport
            ;;
        -P*)
            OPT="${OPT} ${key}"
            ;;
        *)
            echo unknown commandline option ${key}
            exit 1
            ;;
    esac
done

# --stacktrace
nice -n 19 ./gradlew --no-daemon --no-parallel --info ${OPT} processFiles processResults report ${CMD} 2>&1 | tee output.txt

# To re-run use something like this:
#
# nice -n 19 ./gradlew -PresultFile=result-4.1.2-4.1.1-RC2-2019-10-14-07-44.json --no-daemon --no-parallel --info --stacktrace report syncReport
