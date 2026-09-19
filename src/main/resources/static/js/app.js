// ==================== MAIN APPLICATION ====================
function App() {
    const [currentPage, setCurrentPage] = useState('home');
    const [isAuth, setIsAuth] = useState(isAuthenticated());

    useEffect(() => {
        // Check authentication status on load
        setIsAuth(isAuthenticated());
    }, []);

    const handleLogout = () => {
        logout();
    };

    // If not authenticated, show login/register
    if (!isAuth) {
        return (
            <div>
                <LoginPage onSwitchToRegister={() => {
                    // We need to re-render to show register
                    const root = document.getElementById('root');
                    ReactDOM.render(<RegisterPage onSwitchToLogin={() => {
                        ReactDOM.render(<LoginPage onSwitchToRegister={() => {
                            ReactDOM.render(<RegisterPage onSwitchToLogin={() => {
                                ReactDOM.render(<LoginPage onSwitchToRegister={() => {}} />, root);
                            }} />, root);
                        }} />, root);
                    }} />, root);
                }} />
            </div>
        );
    }

    // Authenticated - show dashboard with sidebar
    return (
        <div className="flex h-screen bg-gray-100">
            <SidebarNav
                currentPage={currentPage}
                setCurrentPage={setCurrentPage}
                onLogout={handleLogout}
            />

            <div className="flex-1 overflow-auto">
                {currentPage === 'home' && <DashboardPage setCurrentPage={setCurrentPage} />}
                {currentPage === 'timetable' && <TimetablePage />}
                {currentPage === 'attendance' && <AttendancePage />}
                {currentPage === 'mark-attendance' && <MarkAttendancePage />}
                {currentPage === 'users' && (
                    <div className="p-6">
                        <h1 className="text-3xl font-bold text-gray-800">👥 User Management</h1>
                        <p className="text-gray-600 mt-4">Coming soon...</p>
                    </div>
                )}
                {currentPage === 'settings' && (
                    <div className="p-6">
                        <h1 className="text-3xl font-bold text-gray-800">⚙️ Settings</h1>
                        <p className="text-gray-600 mt-4">Coming soon...</p>
                    </div>
                )}
            </div>
        </div>
    );
}

// ==================== RENDER APP ====================
ReactDOM.render(<App />, document.getElementById('root'));