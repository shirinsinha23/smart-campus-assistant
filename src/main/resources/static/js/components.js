// ==================== LOADING SPINNER ====================
function LoadingSpinner() {
    return (
        <div className="flex justify-center items-center py-12">
            <div className="spinner"></div>
        </div>
    );
}

// ==================== ERROR MESSAGE ====================
function ErrorMessage({ message, onDismiss }) {
    if (!message) return null;
    return (
        <div className="bg-red-100 border-l-4 border-red-500 text-red-700 px-4 py-3 rounded mb-4 flex justify-between items-center">
            <span>{message}</span>
            {onDismiss && (
                <button onClick={onDismiss} className="text-red-700 font-bold">×</button>
            )}
        </div>
    );
}

// ==================== SUCCESS MESSAGE ====================
function SuccessMessage({ message, onDismiss }) {
    if (!message) return null;
    return (
        <div className="bg-green-100 border-l-4 border-green-500 text-green-700 px-4 py-3 rounded mb-4 flex justify-between items-center">
            <span>{message}</span>
            {onDismiss && (
                <button onClick={onDismiss} className="text-green-700 font-bold">×</button>
            )}
        </div>
    );
}

// ==================== STATUS BADGE ====================
function StatusBadge({ status }) {
    const colors = {
        PRESENT: 'bg-green-600',
        ABSENT: 'bg-red-600'
    };
    return (
        <span className={`px-3 py-1 rounded-full text-white font-bold text-sm ${colors[status] || 'bg-gray-600'}`}>
            {status || 'UNKNOWN'}
        </span>
    );
}

// ==================== SIDEBAR NAV ====================
function SidebarNav({ currentPage, setCurrentPage, onLogout }) {
    const role = getRole();
    const email = getEmail();

    const menuItems = [
        { id: 'home', label: '🏠 Dashboard', roles: ['STUDENT', 'FACULTY', 'ADMIN'] },
        { id: 'timetable', label: '📅 Timetable', roles: ['STUDENT', 'FACULTY', 'ADMIN'] },
        { id: 'attendance', label: '✓ Attendance', roles: ['STUDENT', 'FACULTY', 'ADMIN'] },
    ];

    // Faculty and Admin get extra options
    if (role === 'FACULTY') {
        menuItems.push({ id: 'mark-attendance', label: '📝 Mark Attendance', roles: ['FACULTY'] });
    }

    if (role === 'ADMIN') {
        menuItems.push({ id: 'users', label: '👥 Users', roles: ['ADMIN'] });
        menuItems.push({ id: 'settings', label: '⚙️ Settings', roles: ['ADMIN'] });
    }

    const visibleItems = menuItems.filter(item => item.roles.includes(role));

    return (
        <div className="sidebar w-64 bg-gray-900 text-white p-6 shadow-lg flex flex-col h-screen sticky top-0">
            <div className="mb-8">
                <h1 className="text-2xl font-bold">🏫 Campus</h1>
                <p className="text-sm text-gray-400 mt-1">Assistant Portal</p>
            </div>

            <nav className="sidebar-nav flex-1 space-y-2">
                {visibleItems.map(item => (
                    <button
                        key={item.id}
                        onClick={() => setCurrentPage(item.id)}
                        className={`w-full text-left px-4 py-3 rounded-lg sidebar-link ${
                            currentPage === item.id
                                ? 'bg-blue-600 text-white'
                                : 'hover:bg-gray-800 text-gray-300'
                        }`}
                    >
                        {item.label}
                    </button>
                ))}
            </nav>

            <hr className="my-4 border-gray-700" />

            <div className="text-sm">
                <p className="text-gray-400">Logged in as</p>
                <p className="font-bold truncate">{email}</p>
                <span className="inline-block bg-blue-600 px-3 py-1 rounded-full text-xs font-bold mt-1">
                    {role}
                </span>
            </div>

            <button
                onClick={onLogout}
                className="w-full mt-4 bg-red-600 hover:bg-red-700 text-white font-bold py-3 rounded-lg transition transform hover:scale-[1.02]"
            >
                🚪 Logout
            </button>
        </div>
    );
}