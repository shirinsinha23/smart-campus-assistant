# Timetable API Testing Guide

## Overview
This document provides comprehensive testing instructions for all Timetable API endpoints in the Smart Campus Assistant application.

## API Endpoints

| # | Endpoint | Method | Auth Required | Purpose |
|---|----------|--------|---------------|---------|
| 1 | `/api/timetable` | POST | ADMIN | Create a new timetable slot |
| 2 | `/api/timetable` | GET | STUDENT/FACULTY/ADMIN | Get all timetable slots |
| 3 | `/api/timetable/faculty/{facultyId}` | GET | STUDENT/FACULTY/ADMIN | Get faculty-specific timetable |
| 4 | `/api/timetable/{slotId}` | DELETE | ADMIN | Delete a timetable slot |

---

## Prerequisites

### 1. Start MySQL Server
Ensure MySQL is running on `localhost:3306` with:
- **Database:** `smartcampus_db`
- **Username:** `root`
- **Password:** `Shirin@23`

### 2. Build and Run the Application

```bash
# Navigate to project directory
cd "c:\Users\shirin sinha\IdeaProjects\smart-campus-assistant"

# Clean build
mvnw.cmd clean package -DskipTests

# Run the application
mvnw.cmd spring-boot:run

# OR run the JAR directly
java -jar target/smart-campus-assistant-0.0.1-SNAPSHOT.jar
```

Wait for the message: `Tomcat started on port(s): 8080`

### 3. Test Environment Setup
- Base URL: `http://localhost:8080`
- You need `curl` installed (comes with Windows 10+)

---

## Step-by-Step Test Instructions

### Step 1: Register an ADMIN User

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Admin User\", \"email\": \"admin@test.com\", \"password\": \"Admin@123\", \"role\": \"ADMIN\"}"
```

**Expected Response:** 201 CREATED
```json
{
  "message": "User registered successfully",
  "userId": 1,
  "email": "admin@test.com",
  "role": "ADMIN"
}
```

---

### Step 2: Register a FACULTY User

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Dr. John Smith\", \"email\": \"faculty1@test.com\", \"password\": \"Faculty@123\", \"role\": \"FACULTY\"}"
```

**Expected Response:** 201 CREATED
```json
{
  "message": "User registered successfully",
  "userId": 2,
  "email": "faculty1@test.com",
  "role": "FACULTY"
}
```

**Important:** Note down the `facultyId` (usually 2 for first faculty)

---

### Step 3: Register a STUDENT User (Optional, for testing GET access)

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Student User\", \"email\": \"student@test.com\", \"password\": \"Student@123\", \"role\": \"STUDENT\"}"
```

---

### Step 4: Login as ADMIN to Get JWT Token

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"admin@test.com\", \"password\": \"Admin@123\"}"
```

**Expected Response:** 200 OK
```json
{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIsImlhdCI6MTcyN2Q...",
  "userId": 1,
  "email": "admin@test.com",
  "role": "ADMIN"
}
```

**Important:** Copy the `token` value. You'll need it for all protected endpoints.

Save it as an environment variable in your terminal:
```bash
set ADMIN_TOKEN=your_token_here
```

---

## API Test Cases

### TEST 1: Create Timetable Slot (POST /api/timetable)

**Description:** Only ADMIN can create timetable slots. Faculty cannot double-book same day+period.

**Request:**
```bash
curl -X POST http://localhost:8080/api/timetable \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -d "{
    \"day\": \"MONDAY\",
    \"period\": \"PERIOD_1\",
    \"subject\": \"DBMS\",
    \"facultyId\": 2,
    \"room\": \"Room 101\"
  }"
```

**Expected Response:** 201 CREATED
```json
{
  "id": 1,
  "day": "MONDAY",
  "period": "PERIOD_1",
  "subject": "DBMS",
  "facultyId": 2,
  "facultyName": "Dr. John Smith",
  "room": "Room 101"
}
```

✅ **PASS:** If slot is created successfully

---

### TEST 2: Create Multiple Slots

Create slots for different days and periods to test:

**Slot 2 - Tuesday:**
```bash
curl -X POST http://localhost:8080/api/timetable \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -d "{
    \"day\": \"TUESDAY\",
    \"period\": \"PERIOD_2\",
    \"subject\": \"OS\",
    \"facultyId\": 2,
    \"room\": \"Room 102\"
  }"
```

**Slot 3 - Wednesday:**
```bash
curl -X POST http://localhost:8080/api/timetable \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -d "{
    \"day\": \"WEDNESDAY\",
    \"period\": \"PERIOD_3\",
    \"subject\": \"CN\",
    \"facultyId\": 2,
    \"room\": \"Room 103\"
  }"
```

---

### TEST 3: Get All Timetable Slots (GET /api/timetable)

**Description:** Anyone (STUDENT, FACULTY, ADMIN) can view all slots.

**Request:**
```bash
curl -X GET http://localhost:8080/api/timetable \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

**Expected Response:** 200 OK
```json
[
  {
    "id": 1,
    "day": "MONDAY",
    "period": "PERIOD_1",
    "subject": "DBMS",
    "facultyId": 2,
    "facultyName": "Dr. John Smith",
    "room": "Room 101"
  },
  {
    "id": 2,
    "day": "TUESDAY",
    "period": "PERIOD_2",
    "subject": "OS",
    "facultyId": 2,
    "facultyName": "Dr. John Smith",
    "room": "Room 102"
  },
  {
    "id": 3,
    "day": "WEDNESDAY",
    "period": "PERIOD_3",
    "subject": "CN",
    "facultyId": 2,
    "facultyName": "Dr. John Smith",
    "room": "Room 103"
  }
]
```

✅ **PASS:** If all created slots are returned in order (sorted by day and period)

---

### TEST 4: Get Faculty-Specific Timetable (GET /api/timetable/faculty/{facultyId})

**Description:** View timetable for a specific faculty member.

**Request:**
```bash
curl -X GET http://localhost:8080/api/timetable/faculty/2 \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

**Expected Response:** 200 OK
```json
[
  {
    "id": 1,
    "day": "MONDAY",
    "period": "PERIOD_1",
    "subject": "DBMS",
    "facultyId": 2,
    "facultyName": "Dr. John Smith",
    "room": "Room 101"
  },
  {
    "id": 2,
    "day": "TUESDAY",
    "period": "PERIOD_2",
    "subject": "OS",
    "facultyId": 2,
    "facultyName": "Dr. John Smith",
    "room": "Room 102"
  },
  {
    "id": 3,
    "day": "WEDNESDAY",
    "period": "PERIOD_3",
    "subject": "CN",
    "facultyId": 2,
    "facultyName": "Dr. John Smith",
    "room": "Room 103"
  }
]
```

✅ **PASS:** If only the faculty's slots are returned

---

### TEST 5: Delete Timetable Slot (DELETE /api/timetable/{slotId})

**Description:** Only ADMIN can delete slots.

**Request:**
```bash
curl -X DELETE http://localhost:8080/api/timetable/1 \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

**Expected Response:** 204 No Content (empty response)

**Verify deletion:**
```bash
curl -X GET http://localhost:8080/api/timetable \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

Should now show only 2 slots (ID 2 and 3).

✅ **PASS:** If slot is deleted and no longer appears in the GET response

---

## Error Case Tests

### ERROR TEST 1: Create Slot Without Authentication

**Request:**
```bash
curl -X POST http://localhost:8080/api/timetable \
  -H "Content-Type: application/json" \
  -d "{
    \"day\": \"THURSDAY\",
    \"period\": \"PERIOD_4\",
    \"subject\": \"DSA\",
    \"facultyId\": 2,
    \"room\": \"Room 104\"
  }"
```

**Expected Response:** 401 Unauthorized
```json
{
  "error": "Unauthorized",
  "message": "No authentication token provided"
}
```

✅ **PASS:** If access is denied

---

### ERROR TEST 2: Create Slot as FACULTY (Not ADMIN)

First, login as faculty:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"faculty1@test.com\", \"password\": \"Faculty@123\"}"
```

Save the faculty token, then try to create:
```bash
curl -X POST http://localhost:8080/api/timetable \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_FACULTY_TOKEN" \
  -d "{
    \"day\": \"THURSDAY\",
    \"period\": \"PERIOD_4\",
    \"subject\": \"DSA\",
    \"facultyId\": 2,
    \"room\": \"Room 104\"
  }"
```

**Expected Response:** 403 Forbidden
```json
{
  "error": "Forbidden",
  "message": "Access Denied"
}
```

✅ **PASS:** If faculty cannot create slots

---

### ERROR TEST 3: Double-Booking Same Time Slot

Try to create a slot for the same day/period as an existing slot:

**Request:**
```bash
curl -X POST http://localhost:8080/api/timetable \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -d "{
    \"day\": \"TUESDAY\",
    \"period\": \"PERIOD_2\",
    \"subject\": \"DSA\",
    \"facultyId\": 3,
    \"room\": \"Room 105\"
  }"
```

(Assuming slot with TUESDAY, PERIOD_2 already exists)

**Expected Response:** 400 Bad Request
```json
{
  "error": "Bad Request",
  "message": "Slot already occupied: TUESDAY PERIOD_2"
}
```

✅ **PASS:** If double-booking is prevented

---

### ERROR TEST 4: Faculty Double-Booking

Try to assign the same faculty to two slots at the same time:

First, create a slot for Faculty 2 on Monday Period 1:
```bash
curl -X POST http://localhost:8080/api/timetable \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -d "{
    \"day\": \"MONDAY\",
    \"period\": \"PERIOD_1\",
    \"subject\": \"AI\",
    \"facultyId\": 2,
    \"room\": \"Room 201\"
  }"
```

Try to assign Faculty 2 to another slot at same time:
```bash
curl -X POST http://localhost:8080/api/timetable \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -d "{
    \"day\": \"MONDAY\",
    \"period\": \"PERIOD_1\",
    \"subject\": \"ML\",
    \"facultyId\": 2,
    \"room\": \"Room 202\"
  }"
```

**Expected Response:** 400 Bad Request
```json
{
  "error": "Bad Request",
  "message": "Faculty is already assigned to another class at MONDAY PERIOD_1"
}
```

✅ **PASS:** If faculty double-booking is prevented

---

### ERROR TEST 5: Invalid Faculty ID

**Request:**
```bash
curl -X POST http://localhost:8080/api/timetable \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -d "{
    \"day\": \"FRIDAY\",
    \"period\": \"PERIOD_5\",
    \"subject\": \"ML\",
    \"facultyId\": 999,
    \"room\": \"Room 300\"
  }"
```

**Expected Response:** 404 Not Found
```json
{
  "error": "Not Found",
  "message": "Faculty not found with id: 999"
}
```

✅ **PASS:** If error handling works for invalid faculty

---

### ERROR TEST 6: Missing Required Fields

**Request (missing room and subject):**
```bash
curl -X POST http://localhost:8080/api/timetable \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -d "{
    \"day\": \"FRIDAY\",
    \"period\": \"PERIOD_5\",
    \"facultyId\": 2
  }"
```

**Expected Response:** 400 Bad Request
```json
{
  "error": "Bad Request",
  "message": "Subject is required"
}
```

✅ **PASS:** If validation error is returned

---

## Quick Test Batch Script

A batch script `test-timetable-api.bat` has been created in the project root. Run it with:
```bash
test-timetable-api.bat
```

This script automates the basic test flow.

---

## Valid Enum Values Reference

**Days (Day enum):**
- MONDAY
- TUESDAY
- WEDNESDAY
- THURSDAY
- FRIDAY
- SATURDAY

**Periods (Period enum):**
- PERIOD_1
- PERIOD_2
- PERIOD_3
- PERIOD_4
- PERIOD_5
- PERIOD_6

**Subjects (Subject enum):**
- DBMS
- OS
- CN
- DSA
- AI
- ML
- MOBILE_COMPUTING
- CAPSTONE_PROJECT

---

## Expected Test Summary

| Test | Expected Result | Status |
|------|-----------------|--------|
| Create slot (ADMIN) | 201 CREATED | ✅ |
| Get all slots | 200 OK with array | ✅ |
| Get faculty slots | 200 OK with faculty slots | ✅ |
| Delete slot (ADMIN) | 204 No Content | ✅ |
| Create without auth | 401 Unauthorized | ✅ |
| Create as FACULTY | 403 Forbidden | ✅ |
| Double-book room | 400 Bad Request | ✅ |
| Faculty double-book | 400 Bad Request | ✅ |
| Invalid faculty | 404 Not Found | ✅ |
| Missing fields | 400 Bad Request | ✅ |

---

## Troubleshooting

### Application Won't Start
- Check MySQL is running: `mysql -u root -p`
- Check database: `mysql -u root -p -e "SHOW DATABASES;"`
- Check application logs for detailed errors

### Curl Not Found
- Windows 10+: Built-in with Windows
- Older Windows: Download from https://curl.se/download.html
- Or use PowerShell: `Invoke-WebRequest` instead of curl

### JWT Token Expired
- Login again to get a fresh token
- Default expiration: 24 hours

### Port 8080 Already in Use
- Change port in `application.properties`: `server.port=8081`

---

## Next Steps

1. Start MySQL server
2. Run the application with `mvnw.cmd spring-boot:run`
3. Follow the test instructions above
4. Verify all tests pass
5. If any test fails, check the error message and application logs

