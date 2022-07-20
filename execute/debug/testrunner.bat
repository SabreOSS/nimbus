@echo off
setlocal enabledelayedexpansion
set EXEC_ARGS=
set SYSTEM_PARAMS=
set NIMBUS_TAGS=
cd ..\..
:Loop
IF "%~1"=="" GOTO Continue
    set arg=%~1
    set val=%~2
    IF "%arg%"=="-I" (
	    echo ""
    ) ELSE IF "%arg%"=="-DNimbusTags" (
	    set "NIMBUS_TAGS=%arg%=%val%"
	SHIFT
    ) ELSE IF "%arg:~0,2%"=="-D" (
	    set "SYSTEM_PARAMS=%arg%=%val% %SYSTEM_PARAMS%"
	SHIFT
    ) ELSE IF "%arg:~0,1%"=="@" (
	    set "NIMBUS_TAGS=%NIMBUS_TAGS%;%arg%"
    ) ELSE (
	    set "EXEC_ARGS=%arg% %EXEC_ARGS%"
    )
SHIFT
GOTO Loop
:Continue
RD /S /Q reports
@set MAVEN_DEBUG_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=9876
echo "mvn clean compile exec:java -Pbuild,execute -Dexec.args="-a -freports\req_res_files -tconfig\soap-ui\soapui-settings.xml %EXEC_ARGS% %SYSTEM_PARAMS%" %NIMBUS_TAGS%
mvn clean compile exec:java -Pbuild,execute -Dexec.args="-a -freports\req_res_files -tconfig\soap-ui\soapui-settings.xml %EXEC_ARGS% %SYSTEM_PARAMS%" %NIMBUS_TAGS%