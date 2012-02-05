@echo off
rem
rem shellbridge-unreg.bat
rem
rem unregister shell bridge activex object from registry
rem
rem mkt
rem Sun Microsystems

set JRE_HOME="c:\Program Files\Java\jre6"

if not exist c:\windows\system32\regsvr32.exe goto :no_win
set REGSRV32_CMD= c:\windows\system32\regsvr32
goto un_register

:no_win
echo c:\windows\system32\regsvr32 does not exist. Trying in c:\winnt...
if not exist c:\winnt\system32\regsvr32.exe goto error
set REGSRV32_CMD= c:\winnt\system32\regsvr32
goto un_register


:un_register
echo Unregistering ShellBridge.dll...
%REGSRV32_CMD% /u %JRE_HOME%\axbridge\bin\ShellBridge.dll

set REGSRV32_CMD=
goto done

:error
echo regsvr32 could not be found.
echo Unable to unregister shell bridge!
goto done

:done
