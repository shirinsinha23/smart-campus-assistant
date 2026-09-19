@echo off
REM Timetable API Test Suite
REM This script tests all timetable creation APIs

setlocal enabledelayedexpansion

echo.
echo ====================================================
echo  SMART CAMPUS ASSISTANT - TIMETABLE API TEST SUITE
echo ====================================================
echo.

REM Define base URL
set BASE_URL=http://localhost:8080

echo [STEP 1/7] Registering ADMIN user...
curl -X POST %BASE_URL%/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"Admin User\", \"email\": \"admin-test@test.com\", \"password\": \"Admin@123\", \"role\": \"ADMIN\"}" ^
  2>nul
echo.

echo [STEP 2/7] Registering FACULTY user...
curl -X POST %BASE_URL%/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"Dr. John Smith\", \"email\": \"faculty-test@test.com\", \"password\": \"Faculty@123\", \"role\": \"FACULTY\"}" ^
  2>nul
echo.

echo [STEP 3/7] Login as ADMIN to get JWT token...
for /f "tokens=*" %%a in ('curl -s -X POST %BASE_URL%/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\": \"admin-test@test.com\", \"password\": \"Admin@123\"}" ^
  2^>nul') do set RESPONSE=%%a

echo.
echo Response: !RESPONSE!
echo.
echo Note: Copy the token value from the response above
echo Save it as: set ADMIN_TOKEN=your_token_here
echo.
pause
echo.

echo [STEP 4/7] CREATE Timetable Slot (POST /api/timetable)
echo Enter your ADMIN_TOKEN when prompted:
set /p ADMIN_TOKEN=

echo Creating timetable slot...
curl -X POST %BASE_URL%/api/timetable ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer !ADMIN_TOKEN!" ^
  -d "{\"day\": \"MONDAY\", \"period\": \"PERIOD_1\", \"subject\": \"DBMS\", \"facultyId\": 2, \"room\": \"Room 101\"}" ^
  2>nul
echo.

echo Creating second slot (Tuesday)...
curl -X POST %BASE_URL%/api/timetable ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer !ADMIN_TOKEN!" ^
  -d "{\"day\": \"TUESDAY\", \"period\": \"PERIOD_2\", \"subject\": \"OS\", \"facultyId\": 2, \"room\": \"Room 102\"}" ^
  2>nul
echo.

echo Creating third slot (Wednesday)...
curl -X POST %BASE_URL%/api/timetable ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer !ADMIN_TOKEN!" ^
  -d "{\"day\": \"WEDNESDAY\", \"period\": \"PERIOD_3\", \"subject\": \"CN\", \"facultyId\": 2, \"room\": \"Room 103\"}" ^
  2>nul
echo.

echo [STEP 5/7] GET All Timetable Slots (GET /api/timetable)
curl -X GET %BASE_URL%/api/timetable ^
  -H "Authorization: Bearer !ADMIN_TOKEN!" ^
  2>nul
echo.

echo [STEP 6/7] GET Faculty Timetable (GET /api/timetable/faculty/2)
curl -X GET %BASE_URL%/api/timetable/faculty/2 ^
  -H "Authorization: Bearer !ADMIN_TOKEN!" ^
  2>nul
echo.

echo [STEP 7/7] DELETE Timetable Slot (DELETE /api/timetable/1)
echo This will delete the first slot (ID=1)
echo.
curl -X DELETE %BASE_URL%/api/timetable/1 ^
  -H "Authorization: Bearer !ADMIN_TOKEN!" ^
  2>nul
echo.

echo ====================================================
echo TEST COMPLETED
echo ====================================================
echo.
pause
