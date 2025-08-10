@echo off
cls

echo 🚀 Starting Acme Air API
echo 📋 Platform: Windows
echo ☕ Java Version:
java -version

echo.
echo 🔧 Building application...
gradlew.bat clean build

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Build successful! Starting server...
    echo 🌐 API: http://localhost:8080
    echo 🔑 API Key: acme-air-demo-2025-secure-token-12345
    echo.
    echo 🔍 Example API Call:
    echo curl -H "X-API-Key: acme-air-demo-2025-secure-token-12345" ^
    echo   "http://localhost:8080/api/v1/flights/search?flightType=ONE_WAY&departureAirport=SYD&arrivalAirport=MEL&departureDate=2025-08-15"
    echo.
    echo ⏰ Starting in 3 seconds...
    timeout /t 3 /nobreak >nul

    gradlew.bat bootRun
) else (
    echo ❌ Build failed. Check error messages above.
    pause
    exit /b 1
)