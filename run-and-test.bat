@echo off
REM ============================================
REM Smart Campus Assistant - Backend & Integration Test
REM ============================================
setlocal enabledelayedexpansion

cd /d "c:\Users\shirin sinha\IdeaProjects\smart-campus-assistant"

echo.
echo ============================================
echo Starting Smart Campus Assistant Backend...
echo ============================================
echo.

REM Start backend in a new window
start "Smart Campus Backend" cmd /k "mvnw.cmd spring-boot:run"

REM Wait for backend to start
echo.
echo Waiting 30 seconds for backend to initialize...
timeout /t 30 /nobreak

echo.
echo ============================================
echo Testing Backend APIs...
echo ============================================
echo.

REM Test 1: Health Check
echo [TEST 1] Checking if backend is running...
curl -s http://localhost:8080/actuator/health > nul
if !errorlevel! equ 0 (
    echo ✓ Backend is RUNNING on http://localhost:8080
) else (
    echo ✗ Backend is NOT responding - Check if MySQL is running
    echo.
    pause
    exit /b 1
)

echo.
echo ============================================
echo [TEST 2] User Registration Test
echo ============================================
echo.

REM Create unique test user
setlocal enabledelayedexpansion
for /f "tokens=2-4 delims=/ " %%a in ('date /t') do (set mydate=%%c%%a%%b)
for /f "tokens=1-2 delims=/:" %%a in ('time /t') do (set mytime=%%a%%b)
set TEST_USER=testuser_%mydate%_%mytime%@test.com
set TEST_PASSWORD=TestPass@123

echo Registering test user: %TEST_USER%
echo.

curl -X POST http://localhost:8080/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"!TEST_USER!\",\"password\":\"!TEST_PASSWORD!\",\"role\":\"STUDENT\"}" ^
  -w "\nHTTP Status: %%{http_code}\n"

echo.
echo.
echo ============================================
echo [TEST 3] User Login Test
echo ============================================
echo.

echo Logging in as: !TEST_USER!
echo.

REM Login and capture token
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"!TEST_USER!\",\"password\":\"!TEST_PASSWORD!\"}" ^
  -w "\nHTTP Status: %%{http_code}\n"

echo.
echo.
echo ============================================
echo [TEST 4] Get All Timetables
echo ============================================
echo.

echo Note: Run this in a separate terminal to test frontend:
echo   Open: c:\Users\shirin sinha\IdeaProjects\smart-campus-assistant\frontend-app.html
echo.
echo   Use credentials:
echo     Email: !TEST_USER!
echo     Password: !TEST_PASSWORD!
echo.

echo.
echo ============================================
echo BACKEND TESTING COMPLETE
echo ============================================
echo.
echo The backend is running in a separate window.
echo Keep this window open while testing the frontend.
echo.
echo When done testing, close the backend window or press Ctrl+C here.
echo.

pause
