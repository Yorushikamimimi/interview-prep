@echo off
echo Releasing ports 8081 and 3001...

for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8081 "') do (
    taskkill /F /T /PID %%a >nul 2>&1
)
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":3001 "') do (
    taskkill /F /T /PID %%a >nul 2>&1
)

timeout /t 1 /nobreak >nul

cd /d "%~dp0backend"
start "Backend - Spring Boot :8081" cmd /k "mvn spring-boot:run"

cd /d "%~dp0frontend"
start "Frontend - Vue :3001" cmd /k "npm run dev"

echo Started! Visit: http://localhost:3001
timeout /t 3 /nobreak >nul
