#!/bin/bash
# Simple HTTP server for frontend-app.html
# This allows the React app to make API calls

PORT=5000

echo ""
echo "============================================"
echo "Starting Frontend Server..."
echo "============================================"
echo ""

# Check if Python 3 is available
if command -v python3 &> /dev/null; then
    echo "✓ Using Python 3 on http://localhost:$PORT"
    python3 -m http.server $PORT
elif command -v python &> /dev/null; then
    echo "✓ Using Python 2 on http://localhost:$PORT"
    python -m SimpleHTTPServer $PORT
elif command -v node &> /dev/null; then
    echo "✓ Using Node.js (http-server) on http://localhost:$PORT"
    npx http-server -p $PORT
else
    echo "✗ No server available!"
    echo ""
    echo "Install one of these:"
    echo "  - Python 3 (usually pre-installed)"
    echo "  - Node.js with http-server"
    exit 1
fi
