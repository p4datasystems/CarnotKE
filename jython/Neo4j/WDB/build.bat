@echo off

setlocal

set DIR_HOME=%~dP0

if "%JAVA_HOME%" == "" goto noJavaHome
goto runAnt

:noJavaHome
echo Error: JAVA_HOME environment variable is not set.
echo        Please set it to a valid Java 5 installation.
goto end


:runAnt
"%DIR_HOME%\ext\apache-ant-1.7.0\bin\ant" %*


:end

