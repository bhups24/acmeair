#!/bin/bash

echo "🚀 Starting Acme Air API"
echo "📋 Platform: $(uname)"
echo "☕ Java Version:"
java -version

echo ""
echo "🔧 Building application..."
./gradlew clean build

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Build successful! Starting server..."
    echo "🌐 API: http://localhost:8080"
    echo "🔑 API Key: acme-air-demo-2025-secure-token-12345"
    echo ""
    echo "🔍 Example API Call:"
    echo "curl -H \"X-API-Key: acme-air-demo-2025-secure-token-12345\" \\"
    echo "  \"http://localhost:8080/api/v1/flights/search?flightType=ONE_WAY&departureAirport=SYD&arrivalAirport=MEL&departureDate=2025-08-15\""
    echo ""
    echo "⏰ Starting in 3 seconds..."
    sleep 3

    ./gradlew bootRun
else
    echo "❌ Build failed. Check error messages above."
    exit 1
fi