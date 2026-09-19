// ==================== LOGIN PAGE ====================
function LoginPage({ onSwitchToRegister }) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false);

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            const response = await api.post('/auth/login', { email, password });

            // Store user data
            localStorage.setItem('token', response.data.token);
            localStorage.setItem('userId', response.data.userId);
            localStorage.setItem('email', response.data.email);
            localStorage.setItem('role', response.data.role);

            // Reload to show dashboard
            window.location.reload();
        } catch (err) {
            setError(err.response?.data?.message || 'Login failed. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const quickLogin = (email, password) => {
        setEmail(email);
        setPassword(password);
        // Auto-submit after a small delay
        setTimeout(() => {
            const form = document.querySelector('#loginForm');
            if (form) form.dispatchEvent(new Event('submit'));
        }, 100);
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-600 to-purple-600 flex items-center justify-center p-4">
            <div className="bg-white rounded-xl shadow-2xl p-8 w-full max-w-md fade-in">
                <div className="text-center mb-8">
                    <h1 className="text-4xl font-bold text-gray-800">🏫 Smart Campus</h1>
                    <p className="text-gray-600 mt-2">Assistant Login Portal</p>
                </div>

                {error && (
                    <div className="bg-red-100 border-l-4 border-red-500 text-red-700 px-4 py-3 rounded mb-4">
                        {error}
                    </div>
                )}

                <form id="loginForm" onSubmit={handleLogin}>
                    <div className="mb-4">
                        <label className="block text-gray-700 text-sm font-bold mb-2">Email Address</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition"
                            placeholder="your@email.com"
                            required
                        />
                    </div>

                    <div className="mb-6">
                        <label className="block text-gray-700 text-sm font-bold mb-2">Password</label>
                        <div className="relative">
                            <input
                                type={showPassword ? "text" : "password"}
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition"
                                placeholder="••••••••"
                                required
                            />
                            <button
                                type="button"
                                onClick={() => setShowPassword(!showPassword)}
                                className="absolute right-3 top-3 text-gray-500 hover:text-gray-700"
                            >
                                {showPassword ? '🙈' : '👁️'}
                            </button>
                        </div>
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-blue-600 text-white font-bold py-3 rounded-lg hover:bg-blue-700 transition disabled:opacity-50 transform hover:scale-[1.02]"
                    >
                        {loading ? (
                            <span className="flex items-center justify-center gap-2">
                                <span className="spinner inline-block w-5 h-5"></span>
                                Logging in...
                            </span>
                        ) : (
                            '🔐 Login'
                        )}
                    </button>
                </form>

                <div className="mt-6">
                    <p className="text-sm text-gray-600 text-center mb-3">Quick Demo Logins</p>
                    <div className="grid grid-cols-3 gap-2">
                        <button
                            onClick={() => quickLogin('student@campus.com', 'student123')}
                            className="bg-green-100 text-green-800 font-bold py-2 rounded-lg hover:bg-green-200 transition text-sm"
                        >
                            🎓 Student
                        </button>
                        <button
                            onClick={() => quickLogin('faculty@campus.com', 'faculty123')}
                            className="bg-blue-100 text-blue-800 font-bold py-2 rounded-lg hover:bg-blue-200 transition text-sm"
                        >
                            👨‍🏫 Faculty
                        </button>
                        <button
                            onClick={() => quickLogin('admin@campus.com', 'admin123')}
                            className="bg-purple-100 text-purple-800 font-bold py-2 rounded-lg hover:bg-purple-200 transition text-sm"
                        >
                            👨‍💼 Admin
                        </button>
                    </div>
                </div>

                <div className="mt-6 text-center">
                    <p className="text-gray-600">
                        Don't have an account?{' '}
                        <button
                            onClick={onSwitchToRegister}
                            className="text-blue-600 font-bold hover:underline"
                        >
                            Register here
                        </button>
                    </p>
                </div>
            </div>
        </div>
    );
}

// ==================== REGISTER PAGE ====================
function RegisterPage({ onSwitchToLogin }) {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('STUDENT');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);

    const handleRegister = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setLoading(true);

        try {
            await api.post('/auth/register', { name, email, password, role });
            setSuccess('Registration successful! Redirecting to login...');
            setTimeout(() => onSwitchToLogin(), 2000);
        } catch (err) {
            setError(err.response?.data?.message || 'Registration failed. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-green-600 to-blue-600 flex items-center justify-center p-4">
            <div className="bg-white rounded-xl shadow-2xl p-8 w-full max-w-md fade-in">
                <div className="text-center mb-8">
                    <h1 className="text-4xl font-bold text-gray-800">📝 Join Us</h1>
                    <p className="text-gray-600 mt-2">Create your Smart Campus account</p>
                </div>

                {error && (
                    <div className="bg-red-100 border-l-4 border-red-500 text-red-700 px-4 py-3 rounded mb-4">
                        {error}
                    </div>
                )}

                {success && (
                    <div className="bg-green-100 border-l-4 border-green-500 text-green-700 px-4 py-3 rounded mb-4">
                        {success}
                    </div>
                )}

                <form onSubmit={handleRegister}>
                    <div className="mb-4">
                        <label className="block text-gray-700 text-sm font-bold mb-2">Full Name</label>
                        <input
                            type="text"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 transition"
                            placeholder="John Doe"
                            required
                        />
                    </div>

                    <div className="mb-4">
                        <label className="block text-gray-700 text-sm font-bold mb-2">Email</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 transition"
                            placeholder="your@email.com"
                            required
                        />
                    </div>

                    <div className="mb-4">
                        <label className="block text-gray-700 text-sm font-bold mb-2">Password</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 transition"
                            placeholder="Min 6 characters"
                            required
                        />
                    </div>

                    <div className="mb-6">
                        <label className="block text-gray-700 text-sm font-bold mb-2">Role</label>
                        <select
                            value={role}
                            onChange={(e) => setRole(e.target.value)}
                            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 transition"
                        >
                            <option value="STUDENT">🎓 Student</option>
                            <option value="FACULTY">👨‍🏫 Faculty</option>
                            <option value="ADMIN">👨‍💼 Admin</option>
                        </select>
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-green-600 text-white font-bold py-3 rounded-lg hover:bg-green-700 transition disabled:opacity-50 transform hover:scale-[1.02]"
                    >
                        {loading ? 'Registering...' : '🚀 Create Account'}
                    </button>
                </form>

                <div className="mt-6 text-center">
                    <p className="text-gray-600">
                        Already have an account?{' '}
                        <button
                            onClick={onSwitchToLogin}
                            className="text-green-600 font-bold hover:underline"
                        >
                            Login here
                        </button>
                    </p>
                </div>
            </div>
        </div>
    );
}