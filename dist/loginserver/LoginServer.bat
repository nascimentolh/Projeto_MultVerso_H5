@echo off
title LII MultVerso [LOGIN SERVER]
:start
echo Starting [LOGIN SERVER].
echo.

set JAVA_OPTS=%JAVA_OPTS% -Xmn16m
set JAVA_OPTS=%JAVA_OPTS% -Xms64m
set JAVA_OPTS=%JAVA_OPTS% -Xmx64m

java -server %JAVA_OPTS% -Dfile.encoding=UTF-8 -cp config;./../libs/* l2mv.loginserver.AuthServer

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Login Server restarted ...
echo.
goto start
:error
echo.
echo Login Server terminated abnormaly ...
echo.
:end
echo.
echo Login Server terminated ...
echo.

pause