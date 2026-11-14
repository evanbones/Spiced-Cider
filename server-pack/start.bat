@echo off
cd /d "%~dp0"
if not exist "%~dp0run.bat" (
  java -jar "%~dp0neoforge-installer.jar" --installServer
  if errorlevel 1 exit /b 1
  del /q "%~dp0neoforge-installer.jar.log" 2>nul
)
findstr /b /c:"-Xmx" user_jvm_args.txt >nul 2>&1 || (echo.& echo -Xmx6G)>> user_jvm_args.txt
call "%~dp0run.bat" nogui %*
