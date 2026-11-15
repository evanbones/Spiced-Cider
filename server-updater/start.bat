@echo off
cd /d "%~dp0"
java -Xmx6G -jar "%~dp0spicedcider-server.jar" %*
