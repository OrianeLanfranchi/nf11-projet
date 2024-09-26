@echo off
set SAVECLASSPATH=%CLASSPATH%

set CLASSPATH=lib/antlr-4.9.1-complete.jar
"C:\Program Files\Java\jdk-20\bin\java" org.antlr.v4.Tool -visitor -o src/logoparsing  grammar/Logo.g4
rem java org.antlr.v4.Tool -visitor -o src/logoparsing  grammar/Logo.g4
pause

set CLASSPATH=%SAVECLASSPATH%
 