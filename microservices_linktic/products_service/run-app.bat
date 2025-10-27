@echo off
setlocal enabledelayedexpansion

REM Set Java 17 path
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
set "PATH=!JAVA_HOME!\bin;!PATH!"

REM Verify Java version
echo Verificando Java version...
java -version

REM Run the application
echo Iniciando la aplicacion...
java -jar target\products_service-0.0.1-SNAPSHOT.jar

endlocal

