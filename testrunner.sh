#!/bin/sh
SYSTEM_PARAMS=""
EXEC_ARGS=""

echo $JAVA_HOME

for arg in "$@"
do
    case $arg in
        -D*)
        SYSTEM_PARAMS="$1 $SYSTEM_PARAMS"
        shift # Remove --cache= from processing
        ;;
        -I)
        shift # Remove argument name from processing
        ;;
        *)
        EXEC_ARGS="$1 $EXEC_ARGS"
        shift # Remove generic argument from processing
        ;;
    esac
done
rm -rf reports || true
echo "mvn clean install"
call mvn clean install -Pbuild
echo "mvn exec:java -Pexecute -Dexec.args="-a -freports/req_res_files -tsoapui-settings.xml ${EXEC_ARGS}" ${SYSTEM_PARAMS}"
mvn exec:java -Pexecute -Dexec.args="-a -freports/req_res_files -tconfig/soap-ui/soapui-settings.xml ${EXEC_ARGS}" ${SYSTEM_PARAMS}