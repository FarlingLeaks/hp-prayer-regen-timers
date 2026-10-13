@echo off
cd /d "%~dp0"
where java >nul 2>nul || (echo Java 11 was not found. Please install Temurin JDK 11. & pause & exit /b 1)
call gradlew.bat run
pause
