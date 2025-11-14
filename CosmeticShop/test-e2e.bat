@echo off
echo ========================================
echo Run All E2E Tests (Test on Web)
echo ========================================
echo.
echo Note: Make sure web server is running at http://localhost:8080/CosmeticShop
echo.

echo Running all E2E tests...
call mvn test -Dtest="E2E.LoginE2ETest,E2E.AddToCartE2ETest,E2E.AdminE2ETest"

echo.
echo ========================================
echo Done!
echo ========================================
echo Test reports: target\surefire-reports\
echo.
pause

