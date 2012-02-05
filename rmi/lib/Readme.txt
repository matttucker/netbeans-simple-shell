%   
% Notes for packaging the ShellBridge bean
%
% Matt Tucker
%  2010/01/28
%
%



%
%INSTALLATION
%
1) install sdk by running PSDK-x86.exe
2) install Microsoft Visual C++ 2008 Express edition

3) add to path:
C:\Program Files\Java\jdk1.6.0_10\bin
c:\Program Files\Microsoft Platform SDK
C:\Program Files\Microsoft Visual Studio 9.0\Common7\Tools

%
% Package the bean
%
C:\Program Files\Java\jdk1.6.0_10\bin>packager
Usage: Packager [-options] <jar file name> <JavaBean name>

where options include:
   -clsid <class-id>         CLSID for the packaged JavaBean
   -out <output directory>   destination directory for the packaged JavaBean
   -reg                      consent to register the packaged JavaBean

% cd to directory of com-sun-datastorage-shell-rmi.jar
cd C:\NetBeansProjects\simpleshell\build\cluster\modules

% setup visual c++ for compiling from the command line.
vsvars32

% setup SDK environment
SetEnv.Cmd

% run the packager to create ShellBridge.dll
packager  com-sun-datastorage-shell-rmi.jar com.sun.datastorage.shell.rmi.ShellBridge

