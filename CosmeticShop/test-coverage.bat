@echo off
chcp 65001 >nul
echo ========================================
echo Run Tests with JaCoCo Coverage
echo ========================================
echo.
echo This will:
echo 1. Run all tests with coverage
echo 2. Generate JaCoCo report
echo 3. Open report in browser
echo.
echo Note: This may take a few minutes...
echo.

echo === Step 1: Running tests with coverage ===
call mvn clean verify

if errorlevel 1 (
    echo.
    echo ========================================
    echo Tests failed! Check errors above.
    echo ========================================
    pause
    exit /b 1
)

echo.
echo === Step 2: Checking JaCoCo report ===
set REPORT_PATH=target\site\jacoco\index.html

if not exist "%REPORT_PATH%" (
    echo.
    echo ========================================
    echo Report not found: %REPORT_PATH%
    echo ========================================
    echo.
    echo Please check if tests ran successfully.
    pause
    exit /b 1
)

echo.
echo ========================================
echo Coverage report generated successfully!
echo ========================================
echo.
echo Report location: %REPORT_PATH%
echo.

echo === Step 3: Opening report in browser ===
start "" "%REPORT_PATH%"

echo.
echo Report opened in your default browser!
echo.
echo ========================================
echo Done!
echo ========================================
echo.
pause

