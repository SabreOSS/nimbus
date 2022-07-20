#!/bin/sh
SOAPUI_PROJECT_FILE=""
SOAPUI_TEST_SUITE=""
SOAPUI_TEST_CASE=""
SYSTEM_PARAMS=""

for arg in "$@"
do
    case $arg in
        -s*)
        SOAPUI_TEST_SUITE="${arg#*s}"
        shift # Remove --initialize from processing
        ;;
        -c*)
        SOAPUI_TEST_CASE="${arg#*c}"
        shift # Remove --cache= from processing
        ;;
        -D*)
        SYSTEM_PARAMS="$1 $SYSTEM_PARAMS"
        shift # Remove --cache= from processing
        ;;
        -I)
        shift # Remove argument name from processing
        ;;
        *)
        SOAPUI_PROJECT_FILE=$1
        shift # Remove generic argument from processing
        ;;
    esac
done
cd ../..
if [[ -n "$SOAPUI_JAVA_HOME" ]] && [[ -x "$SOAPUI_JAVA_HOME/bin/java" ]];  then
    echo found SOAPUI_JAVA_HOME
    _java="$SOAPUI_JAVA_HOME/bin/java"
    JAVA_HOME="$SOAPUI_JAVA_HOME"
elif type -p java; then
    echo found JAVA_HOME
    _java=java
else
    echo "no java, cannot execute tests"
fi

ADDITIONAL_MVN_PROFILE=""
if [[ "$_java" ]]; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F . '{split($0,a,"."); print a[1]"."a[2]}')
    echo version "$version"
    if [[ $(echo "$version > 1.8" | bc) == 1 ]] ; then
        echo java version is more than 8
        ADDITIONAL_MVN_PROFILE=",latestSOAPUI"
    else
        echo version is less than or equal to 8
        ADDITIONAL_MVN_PROFILE=""
    fi
fi
export MAVEN_DEBUG_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=9876"
echo "mvn clean install -Dmaven.test.failure.ignore=false -Dsoapui.project.file=${SOAPUI_PROJECT_FILE} ${SYSTEM_PARAMS} -Dsoapui.project.testSuite=${SOAPUI_TEST_SUITE} -Dsoapui.project.testcase=${SOAPUI_TEST_CASE} -Psoapui${ADDITIONAL_MVN_PROFILE}"
mvn clean install -Dmaven.test.failure.ignore=false -Dsoapui.project.file=${SOAPUI_PROJECT_FILE} ${SYSTEM_PARAMS} -Dsoapui.project.testSuite=${SOAPUI_TEST_SUITE} -Dsoapui.project.testcase=${SOAPUI_TEST_CASE} -Psoapui${ADDITIONAL_MVN_PROFILE}
