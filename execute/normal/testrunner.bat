@echo off
setlocal enabledelayedexpansion
set EXEC_ARGS=
set SYSTEM_PARAMS=
set NIMBUS_TAGS=
set SOAPUI_PROJECT_FILE=
set SOAPUI_TEST_SUITE=
set SOAPUI_TEST_CASE=
cd ..\..
:Loop
IF "%~1"=="" GOTO Continue
    set arg=%~1
    set val=%~2
    IF "%arg%"=="-I" (
	    echo ""
    )ELSE IF "%arg:~0,2%"=="-s" (
	    SET SOAPUI_TEST_SUITE=%arg:~2%
    ) ELSE IF "%arg:~0,2%"=="-c" (
	    SET SOAPUI_TEST_CASE=%arg:~2%
    ) ELSE IF "%arg%"=="-DNimbusTags" (
	    set "NIMBUS_TAGS=%arg%=%val%"
	SHIFT
    ) ELSE IF "%arg:~0,2%"=="-D" (
	    set "SYSTEM_PARAMS=%arg%=%val% %SYSTEM_PARAMS%"
	SHIFT
    ) ELSE IF "%arg:~0,1%"=="@" (
	    set "NIMBUS_TAGS=%NIMBUS_TAGS%;%arg%"
    ) ELSE (
	    set SOAPUI_PROJECT_FILE="%arg%"
    )
SHIFT
GOTO Loop
:Continue
RD /S /Q reports
echo "mvn install -Psoapui -Dmaven.test.failure.ignore=false -Dsoapui.project.file=%SOAPUI_PROJECT_FILE% %SYSTEM_PARAMS% %NIMBUS_TAGS% -Dsoapui.project.testSuite=%SOAPUI_TEST_SUITE% -Dsoapui.project.testcase=%SOAPUI_TEST_CASE%"
call mvn clean install -Pbuild
call mvn clean install -Psoapui -Dmaven.test.failure.ignore=false -Dsoapui.project.file=%SOAPUI_PROJECT_FILE% %SYSTEM_PARAMS% %NIMBUS_TAGS% -Dsoapui.project.testSuite=%SOAPUI_TEST_SUITE% -Dsoapui.project.testcase=%SOAPUI_TEST_CASE% 