// API Configuration
const API_BASE_URL = 'const API_BASE_URL = 'http://13.200.246.254:8080/api';';

// Create axios instance
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    },
    timeout: 10000
});

// Request interceptor - Add token
api.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    error => Promise.reject(error)
);

// Response interceptor - Handle 401
api.interceptors.response.use(
    response => response,
    error => {
        if (error.response?.status === 401) {
            // Token expired or invalid
            localStorage.clear();
            window.location.href = window.location.origin + window.location.pathname;
        }
        return Promise.reject(error);
    }
);

// Utility functions
const getStoredUser = () => {
    try {
        return {
            userId: localStorage.getItem('userId'),
            email: localStorage.getItem('email'),
            role: localStorage.getItem('role')
        };
    } catch {
        return null;
    }
};

const isAuthenticated = () => {
    return localStorage.getItem('token') !== null;
};

const logout = () => {
    localStorage.clear();
    window.location.href = window.location.origin + window.location.pathname;
};

const getRole = () => localStorage.getItem('role');
const getUserId = () => localStorage.getItem('userId');
const getEmail = () => localStorage.getItem('email');