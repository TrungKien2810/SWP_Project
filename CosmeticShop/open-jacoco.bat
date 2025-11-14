@echo off
chcp 65001 >nul
echo ========================================
echo Open JaCoCo Coverage Report
echo ========================================
echo.

set REPORT_PATH=target\site\jacoco\index.html

if not exist "%REPORT_PATH%" (
    echo.
    echo ========================================
    echo Report not found!
    echo ========================================
    echo.
    echo JaCoCo report does not exist yet.
    echo.
    echo To generate the report, run:
    echo   mvn clean verify
    echo.
    echo Or use: test-coverage.bat
    echo.
    pause
    exit /b 1
)

echo Report found: %REPORT_PATH%
echo.
echo Opening report in browser...
start "" "%REPORT_PATH%"

echo.
echo ========================================
echo Report opened!
echo ========================================
echo.
echo If browser did not open, manually open:
echo %CD%\%REPORT_PATH%
echo.
pause

