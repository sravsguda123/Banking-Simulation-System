@echo off
echo Compiling Java Swing Application...
if not exist bin mkdir bin
javac -d bin src\bank\*.java src\bank\model\*.java src\bank\service\*.java src\bank\ui\*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)
echo Compilation successful. Starting Secure Desktop Interface...
java -cp bin bank.Main
pause
