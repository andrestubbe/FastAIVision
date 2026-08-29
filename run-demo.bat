@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo ===================================================
echo  FastAIVision 0.1.0 — 120-Column Interactive Demo
echo ===================================================
call mvn compile exec:java -Dexec.mainClass=fastaivision.Demo
pause