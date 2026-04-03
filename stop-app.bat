@echo off
echo Stopping services on port 8081 and 3001...

for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8081 "') do (
    taskkill /F /T /PID %%a >nul 2>&1
)

for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":3001 "') do (
    taskkill /F /T /PID %%a >nul 2>&1
)

echo Done.
timeout /t 2 /nobreak >nul
