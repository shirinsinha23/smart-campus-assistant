// ==================== DASHBOARD PAGE ====================
function DashboardPage({ setCurrentPage }) {
    const role = getRole();
    const email = getEmail();

    const studentCards = [
        { id: 'timetable', title: '📅 My Timetable', desc: 'View your class schedule', color: 'blue' },
        { id: 'attendance', title: '✓ My Attendance', desc: 'Check your attendance records', color: 'green' }
    ];

    const facultyCards = [
        { id: 'timetable', title: '📅 Class Schedule', desc: 'View your teaching schedule', color: 'blue' },
        { id: 'mark-attendance', title: '📝 Mark Attendance', desc: 'Record student attendance', color: 'purple' },
        { id: 'attendance', title: '📊 View Attendance', desc: 'Check student attendance', color: 'green' }
    ];

    const adminCards = [
        { id: 'timetable', title: '📅 Manage Timetable', desc: 'Create & delete time slots', color: 'blue' },
        { id: 'attendance', title: '📊 All Attendance', desc: 'View system-wide attendance', color: 'green' },
        { id: 'users', title: '👥 User Management', desc: 'Manage all users', color: 'yellow' },
        { id: 'settings', title: '⚙️ System Settings', desc: 'Configure the system', color: 'purple' }
    ];

    const cardMap = {
        STUDENT: studentCards,
        FACULTY: facultyCards,
        ADMIN: adminCards
    };

    const cards = cardMap[role] || [];

    return (
        <div className="p-8">
            <div className="bg-white rounded-xl shadow-lg p-8">
                <h2 className="text-3xl font-bold text-gray-800 mb-2">
                    Welcome to Smart Campus Assistant
                </h2>
                <p className="text-gray-600 text-lg">
                    {role === 'STUDENT' && '🎓 Access your timetable and attendance'}
                    {role === 'FACULTY' && '👨‍🏫 Manage classes and student attendance'}
                    {role === 'ADMIN' && '👨‍💼 Full system management and control'}
                </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mt-8">
                {cards.map((card) => (
                    <div
                        key={card.id}
                        onClick={() => setCurrentPage(card.id)}
                        className={`bg-${card.color}-100 rounded-xl p-6 border-l-4 border-${card.color}-600 card-hover`}
                    >
                        <h3 className="font-bold text-lg mb-2">{card.title}</h3>
                        <p className="text-gray-600">{card.desc}</p>
                        <div className="mt-4 text-sm text-gray-500">Click to open →</div>
                    </div>
                ))}
            </div>

            <div className="mt-8 grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="bg-white rounded-xl shadow-md p-6">
                    <h4 className="font-bold text-gray-700 mb-2">📊 Quick Stats</h4>
                    <p className="text-2xl font-bold text-blue-600">0</p>
                    <p className="text-sm text-gray-500">Total Classes Today</p>
                </div>
                <div className="bg-white rounded-xl shadow-md p-6">
                    <h4 className="font-bold text-gray-700 mb-2">✅ Attendance</h4>
                    <p className="text-2xl font-bold text-green-600">0%</p>
                    <p className="text-sm text-gray-500">Overall Attendance</p>
                </div>
                <div className="bg-white rounded-xl shadow-md p-6">
                    <h4 className="font-bold text-gray-700 mb-2">📅 Events</h4>
                    <p className="text-2xl font-bold text-purple-600">0</p>
                    <p className="text-sm text-gray-500">Upcoming Events</p>
                </div>
            </div>
        </div>
    );
}

// ==================== TIMETABLE PAGE ====================
function TimetablePage() {
    const [slots, setSlots] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showCreateForm, setShowCreateForm] = useState(false);
    const role = getRole();

    const [formData, setFormData] = useState({
        day: 'MONDAY',
        period: 'PERIOD_1',
        subject: 'DBMS',
        facultyId: '',
        room: ''
    });

    useEffect(() => {
        fetchTimetable();
    }, []);

    const fetchTimetable = async () => {
        setLoading(true);
        setError('');
        try {
            const response = await api.get('/timetable');
            setSlots(response.data);
        } catch (err) {
            setError('Failed to load timetable. Please try again.');
            console.error(err);
        }
        setLoading(false);
    };

    const handleCreateSlot = async (e) => {
        e.preventDefault();
        setError('');
        try {
            await api.post('/timetable', {
                ...formData,
                facultyId: parseInt(formData.facultyId)
            });
            setShowCreateForm(false);
            setFormData({ day: 'MONDAY', period: 'PERIOD_1', subject: 'DBMS', facultyId: '', room: '' });
            fetchTimetable();
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to create slot');
        }
    };

    const handleDelete = async (slotId) => {
        if (confirm('Are you sure you want to delete this slot?')) {
            try {
                await api.delete(`/timetable/${slotId}`);
                fetchTimetable();
            } catch (err) {
                setError('Failed to delete slot');
            }
        }
    };

    return (
        <div className="p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-3xl font-bold text-gray-800">📅 Timetable</h1>
                {role === 'ADMIN' && (
                    <button
                        onClick={() => setShowCreateForm(!showCreateForm)}
                        className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition transform hover:scale-[1.02]"
                    >
                        {showCreateForm ? '✕ Cancel' : '+ Add Slot'}
                    </button>
                )}
            </div>

            <ErrorMessage message={error} onDismiss={() => setError('')} />

            {showCreateForm && role === 'ADMIN' && (
                <div className="bg-white rounded-xl shadow-lg p-6 mb-6 fade-in">
                    <h2 className="text-xl font-bold mb-4">Create New Timetable Slot</h2>
                    <form onSubmit={handleCreateSlot} className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                        <select
                            value={formData.day}
                            onChange={(e) => setFormData({...formData, day: e.target.value})}
                            className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                            required
                        >
                            <option value="MONDAY">Monday</option>
                            <option value="TUESDAY">Tuesday</option>
                            <option value="WEDNESDAY">Wednesday</option>
                            <option value="THURSDAY">Thursday</option>
                            <option value="FRIDAY">Friday</option>
                            <option value="SATURDAY">Saturday</option>
                        </select>

                        <select
                            value={formData.period}
                            onChange={(e) => setFormData({...formData, period: e.target.value})}
                            className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                            required
                        >
                            <option value="PERIOD_1">Period 1</option>
                            <option value="PERIOD_2">Period 2</option>
                            <option value="PERIOD_3">Period 3</option>
                            <option value="PERIOD_4">Period 4</option>
                            <option value="PERIOD_5">Period 5</option>
                            <option value="PERIOD_6">Period 6</option>
                        </select>

                        <select
                            value={formData.subject}
                            onChange={(e) => setFormData({...formData, subject: e.target.value})}
                            className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                            required
                        >
                            <option value="DBMS">DBMS</option>
                            <option value="OS">OS</option>
                            <option value="CN">CN</option>
                            <option value="DSA">DSA</option>
                            <option value="AI">AI</option>
                            <option value="ML">ML</option>
                        </select>

                        <input
                            type="number"
                            placeholder="Faculty ID"
                            value={formData.facultyId}
                            onChange={(e) => setFormData({...formData, facultyId: e.target.value})}
                            className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                            required
                        />

                        <input
                            type="text"
                            placeholder="Room (e.g., Room 101)"
                            value={formData.room}
                            onChange={(e) => setFormData({...formData, room: e.target.value})}
                            className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />

                        <button
                            type="submit"
                            className="bg-green-600 text-white rounded-lg px-6 py-2 hover:bg-green-700 transition font-bold"
                        >
                            Create Slot
                        </button>
                    </form>
                </div>
            )}

            {loading ? (
                <LoadingSpinner />
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    {slots.length === 0 ? (
                        <p className="col-span-full text-center text-gray-600 py-8">No timetable slots found</p>
                    ) : (
                        slots.map(slot => (
                            <div key={slot.id} className="bg-white rounded-xl shadow-md p-6 border-l-4 border-blue-600 hover:shadow-lg transition">
                                <h3 className="font-bold text-lg text-blue-600">{slot.subject}</h3>
                                <div className="mt-3 space-y-1 text-gray-700">
                                    <p><span className="font-semibold">Day:</span> {slot.day}</p>
                                    <p><span className="font-semibold">Period:</span> {slot.period}</p>
                                    <p><span className="font-semibold">Faculty:</span> {slot.facultyName}</p>
                                    <p><span className="font-semibold">Room:</span> {slot.room || 'Not assigned'}</p>
                                </div>
                                {role === 'ADMIN' && (
                                    <button
                                        onClick={() => handleDelete(slot.id)}
                                        className="mt-4 bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition text-sm font-bold"
                                    >
                                        Delete
                                    </button>
                                )}
                            </div>
                        ))
                    )}
                </div>
            )}
        </div>
    );
}

// ==================== ATTENDANCE PAGE ====================
function AttendancePage() {
    const [attendance, setAttendance] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const role = getRole();
    const userId = getUserId();

    useEffect(() => {
        fetchAttendance();
    }, []);

    const fetchAttendance = async () => {
        setLoading(true);
        setError('');
        try {
            let endpoint;
            if (role === 'ADMIN') {
                endpoint = '/attendance/all';
            } else {
                endpoint = `/attendance/student/${userId}`;
            }
            const response = await api.get(endpoint);
            setAttendance(response.data);
        } catch (err) {
            setError('Failed to load attendance records');
            console.error(err);
        }
        setLoading(false);
    };

    return (
        <div className="p-6">
            <h1 className="text-3xl font-bold text-gray-800 mb-6">✓ Attendance</h1>

            <ErrorMessage message={error} onDismiss={() => setError('')} />

            <div className="bg-white rounded-xl shadow-lg overflow-hidden">
                <div className="p-6 border-b">
                    <h2 className="text-xl font-bold">
                        {role === 'ADMIN' ? '📊 All Attendance Records' : '📋 My Attendance Records'}
                    </h2>
                    <p className="text-gray-600 text-sm mt-1">
                        {role === 'ADMIN' ? 'Viewing all students attendance' : 'Your attendance history'}
                    </p>
                </div>

                {loading ? (
                    <LoadingSpinner />
                ) : (
                    <div className="overflow-x-auto">
                        <table className="w-full">
                            <thead className="bg-gray-100">
                                <tr>
                                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">Date</th>
                                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">Subject</th>
                                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">Status</th>
                                    {role === 'ADMIN' && (
                                        <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">Student</th>
                                    )}
                                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">Marked By</th>
                                </tr>
                            </thead>
                            <tbody>
                                {attendance.length === 0 ? (
                                    <tr>
                                        <td colSpan={role === 'ADMIN' ? 5 : 4} className="px-6 py-8 text-center text-gray-500">
                                            No attendance records found
                                        </td>
                                    </tr>
                                ) : (
                                    attendance.map((record, idx) => (
                                        <tr key={idx} className="border-t hover:bg-gray-50 transition">
                                            <td className="px-6 py-3">{record.date}</td>
                                            <td className="px-6 py-3 font-medium">{record.subject}</td>
                                            <td className="px-6 py-3">
                                                <StatusBadge status={record.status} />
                                            </td>
                                            {role === 'ADMIN' && (
                                                <td className="px-6 py-3">{record.studentName || 'N/A'}</td>
                                            )}
                                            <td className="px-6 py-3 text-gray-600">{record.markedByName}</td>
                                        </tr>
                                    ))
                                )}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    );
}

// ==================== MARK ATTENDANCE PAGE (Faculty Only) ====================
function MarkAttendancePage() {
    const [subjects] = useState(['DBMS', 'OS', 'CN', 'DSA', 'AI', 'ML']);
    const [selectedSubject, setSelectedSubject] = useState('DBMS');
    const [students, setStudents] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [date, setDate] = useState(new Date().toISOString().split('T')[0]);

    useEffect(() => {
        // In a real app, fetch students by subject
        // For demo, create mock students
        const mockStudents = [
            { id: 1, name: 'Alice Johnson' },
            { id: 2, name: 'Bob Smith' },
            { id: 3, name: 'Carol White' },
            { id: 4, name: 'David Brown' },
            { id: 5, name: 'Emma Davis' },
        ];
        setStudents(mockStudents.map(s => ({ ...s, status: 'PRESENT' })));
    }, [selectedSubject]);

    const handleMarkAll = (status) => {
        setStudents(students.map(s => ({ ...s, status })));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setSuccess('');

        try {
            const payload = {
                subject: selectedSubject,
                date: date,
                students: students.map(s => ({
                    studentId: s.id,
                    status: s.status
                }))
            };

            await api.post('/attendance/mark', payload);
            setSuccess(`Attendance marked successfully for ${students.length} students!`);

            // Reset statuses to PRESENT
            setStudents(students.map(s => ({ ...s, status: 'PRESENT' })));
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to mark attendance');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="p-6">
            <h1 className="text-3xl font-bold text-gray-800 mb-6">📝 Mark Attendance</h1>

            <ErrorMessage message={error} onDismiss={() => setError('')} />
            <SuccessMessage message={success} onDismiss={() => setSuccess('')} />

            <div className="bg-white rounded-xl shadow-lg p-6">
                <form onSubmit={handleSubmit}>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                        <div>
                            <label className="block text-gray-700 font-bold mb-2">Subject</label>
                            <select
                                value={selectedSubject}
                                onChange={(e) => setSelectedSubject(e.target.value)}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            >
                                {subjects.map(s => (
                                    <option key={s} value={s}>{s}</option>
                                ))}
                            </select>
                        </div>
                        <div>
                            <label className="block text-gray-700 font-bold mb-2">Date</label>
                            <input
                                type="date"
                                value={date}
                                onChange={(e) => setDate(e.target.value)}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            />
                        </div>
                    </div>

                    <div className="flex gap-2 mb-4">
                        <button
                            type="button"
                            onClick={() => handleMarkAll('PRESENT')}
                            className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition"
                        >
                            Mark All Present
                        </button>
                        <button
                            type="button"
                            onClick={() => handleMarkAll('ABSENT')}
                            className="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition"
                        >
                            Mark All Absent
                        </button>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="w-full">
                            <thead className="bg-gray-100">
                                <tr>
                                    <th className="px-4 py-2 text-left">Student ID</th>
                                    <th className="px-4 py-2 text-left">Name</th>
                                    <th className="px-4 py-2 text-left">Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                {students.map((student, idx) => (
                                    <tr key={student.id} className="border-t">
                                        <td className="px-4 py-2">{student.id}</td>
                                        <td className="px-4 py-2">{student.name}</td>
                                        <td className="px-4 py-2">
                                            <select
                                                value={student.status}
                                                onChange={(e) => {
                                                    const updated = [...students];
                                                    updated[idx].status = e.target.value;
                                                    setStudents(updated);
                                                }}
                                                className="border border-gray-300 rounded px-3 py-1 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                            >
                                                <option value="PRESENT">✅ Present</option>
                                                <option value="ABSENT">❌ Absent</option>
                                            </select>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full mt-6 bg-blue-600 text-white font-bold py-3 rounded-lg hover:bg-blue-700 transition disabled:opacity-50"
                    >
                        {loading ? 'Saving...' : '✅ Submit Attendance'}
                    </button>
                </form>
            </div>
        </div>
    );
}