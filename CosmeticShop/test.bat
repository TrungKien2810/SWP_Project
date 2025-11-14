@echo off
echo ========================================
echo Run Tests
echo ========================================
echo.
echo Note: Make sure web server is running at http://localhost:8080/CosmeticShop
echo.

echo Select test to run:
echo 1. All tests
echo 2. LoginE2ETest only
echo 3. AddToCartE2ETest only
echo 4. AdminE2ETest only
echo 5. All E2E tests (test on web)
echo 6. Custom test
echo.

set /p choice=Enter choice (1-6): 

if "%choice%"=="1" (
    echo Running all tests...
    call mvn test
    goto :end
)

if "%choice%"=="2" (
    echo Running LoginE2ETest...
    call mvn test -Dtest="E2E.LoginE2ETest"
    goto :end
)

if "%choice%"=="3" (
    echo Running AddToCartE2ETest...
    call mvn test -Dtest="E2E.AddToCartE2ETest"
    goto :end
)

if "%choice%"=="4" (
    echo Running AdminE2ETest...
    call mvn test -Dtest="E2E.AdminE2ETest"
    goto :end
)

if "%choice%"=="5" (
    echo Running all E2E tests...
    call mvn test -Dtest="E2E.LoginE2ETest,E2E.AddToCartE2ETest,E2E.AdminE2ETest"
    goto :end
)

if "%choice%"=="6" (
    set /p testName=Enter test class name: 
    echo Running %testName%...
    call mvn test -Dtest="%testName%"
    goto :end
)

echo Invalid choice. Running all tests...
call mvn test

:end
echo.
echo ========================================
echo Done!
echo ========================================
echo Test reports: target\surefire-reports\
echo.
pause

