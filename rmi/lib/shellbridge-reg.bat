rem
rem shellbridge-reg.bat
rem
rem register shell bridge activex object from registry
rem
rem mkt
rem Sun Microsystems

set JRE_HOME="c:\Program Files\Java\jre6"

copy ShellBridge.dll %JRE_HOME%\axbridge\bin
copy com.sun.datastorage.shell.rmi.jar $JRE_HOME\axbridge\lib


rem unregister first
call shellbridge-unreg.bat

if not exist c:\windows\system32\regsvr32.exe goto :no_win
set REGSRV32_CMD= c:\windows\system32\regsvr32
goto register

:no_win
echo c:\windows\system32\regsvr32 does not exist. Trying in c:\winnt...
if not exist c:\winnt\system32\regsvr32.exe goto error
set REGSRV32_CMD= c:\winnt\system32\regsvr32


:register

echo Registering shell bridge...
%REGSRV32_CMD% %JRE_HOME%\axbridge\bin\ShellBridge.dll

set REGSRV32_CMD=
goto done

:error
echo regsvr32 could not be found.
echo Unable to register shell bridge!
goto done

:done