#!/bin/bash
# ============================================
# Smart Campus Assistant - Backend Startup
# ============================================

cd "$(dirname "$0")" || exit

echo ""
echo "============================================"
echo "Starting Smart Campus Assistant Backend..."
echo "============================================"
echo ""

# Check if mvnw exists
if [ ! -f "./mvnw" ]; then
    echo "Error: mvnw not found!"
    echo "Trying with global maven..."
    mvn spring-boot:run
else
    ./mvnw spring-boot:run
fi
