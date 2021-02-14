@echo off
:loop
title Alexandria EMU - Server
"C:\Program Files\BellSoft\LibericaJRE-15\bin\java.exe" -Xmx1024M -jar server.jar -o true
goto loop
PAUSE
