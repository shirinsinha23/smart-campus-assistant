# Smart Campus Assistant - Frontend Setup Guide

## Quick Start Options

### Option 1: Use Pre-built CDN Version (Fastest - No Setup)
Use the `index.html` file in this directory. It includes React, Tailwind CSS via CDN.
- Just open `frontend/index.html` in a browser
- All pages included: Login, Register, Attendance, Timetable
- No build process needed

### Option 2: Full React Development Setup (Recommended for Development)

#### Step 1: Create Vite Project
```bash
cd c:\Users\shirin sinha\IdeaProjects
npm create vite@latest smart-campus-frontend -- --template react
cd smart-campus-frontend
npm install
```

#### Step 2: Install Tailwind CSS
```bash
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init -p
```

#### Step 3: Configure Tailwind
Edit `tailwind.config.js`:
```javascript
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,jsx}",
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
```

Edit `src/index.css`:
```css
@tailwind base;
@tailwind components;
@tailwind utilities;
```

#### Step 4: Copy React Components
Copy all files from `src/` directory into your Vite project's `src/` directory.

#### Step 5: Install Dependencies
```bash
npm install axios react-router-dom
```

#### Step 6: Run Development Server
```bash
npm run dev
```

The frontend will be available at: `http://localhost:5173`

---

## Backend Configuration

Make sure your backend is running on `http://localhost:8080`

In the components, update the API base URL if needed:
- Login/Register: `http://localhost:8080/api/auth`
- Attendance: `http://localhost:8080/api/attendance`
- Timetable: `http://localhost:8080/api/timetable`

---

## Features Implemented

### 1. **Login Page**
- Email and password input
- Error message handling
- JWT token storage in localStorage
- Redirect to dashboard on success
- Link to register page

### 2. **Register Page**
- Name, email, password input
- Role selection (STUDENT, FACULTY, ADMIN)
- Form validation
- Error message handling
- Link to login page

### 3. **Attendance Page**
- Mark attendance (Present/Absent)
- View attendance records
- Filter by date/subject
- Show attendance percentage
- Admin can view all attendance records

### 4. **Timetable Page**
- View full timetable (all slots)
- View faculty-specific timetable
- View class-specific timetable
- Create new slot (ADMIN only)
- Delete slot (ADMIN only)
- Sorted by day and period

### 5. **Dashboard**
- Navigation menu
- User profile info
- Logout functionality

---

## File Structure

```
smart-campus-frontend/
├── public/
├── src/
│   ├── components/
│   │   ├── Login.jsx
│   │   ├── Register.jsx
│   │   ├── Attendance.jsx
│   │   ├── Timetable.jsx
│   │   ├── Navigation.jsx
│   │   └── ProtectedRoute.jsx
│   ├── api/
│   │   └── api.js
│   ├── App.jsx
│   ├── App.css
│   └── index.css
├── index.html
├── vite.config.js
├── tailwind.config.js
├── postcss.config.js
└── package.json
```

