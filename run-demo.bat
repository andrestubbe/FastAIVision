@echo off
chcp 65001 >nul
cd /d "%~dp0"
set "MAVEN_OPTS=--enable-native-access=ALL-UNNAMED -Dorg.slf4j.simpleLogger.defaultLogLevel=warn"

call mvn -q compile exec:java -Dexec.mainClass=fastaivision.Demo -Dorg.slf4j.simpleLogger.defaultLogLevel=warn
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Demo execution failed!
)
pause