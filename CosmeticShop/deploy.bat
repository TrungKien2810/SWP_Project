@echo off
echo ========================================
echo Deploy CosmeticShop to Tomcat
echo ========================================
echo.

set TOMCAT_PATH=C:\Edu\Tools\apache-tomcat-11.0.13
set WAR_FILE=target\CosmeticShop-1.0-SNAPSHOT.war
set WEBAPPS_PATH=%TOMCAT_PATH%\webapps
set WORK_PATH=%TOMCAT_PATH%\work\Catalina\localhost\CosmeticShop
set DEPLOY_WAR=%WEBAPPS_PATH%\CosmeticShop.war

echo Tomcat path: %TOMCAT_PATH%
echo.

if not exist "%WAR_FILE%" (
    echo WAR file not found: %WAR_FILE%
    echo Please build first: mvn clean package -DskipTests
    pause
    exit /b 1
)

if not exist "%TOMCAT_PATH%" (
    echo Tomcat path not found: %TOMCAT_PATH%
    pause
    exit /b 1
)

echo Stopping Tomcat if running...
echo Please stop Tomcat manually if needed
echo.

echo Removing old deployment...
if exist "%WEBAPPS_PATH%\CosmeticShop" (
    rmdir /s /q "%WEBAPPS_PATH%\CosmeticShop"
    echo Removed: %WEBAPPS_PATH%\CosmeticShop
) else (
    echo No old deployment found.
)

echo Clearing JSP cache...
if exist "%WORK_PATH%" (
    rmdir /s /q "%WORK_PATH%"
    echo Removed JSP cache: %WORK_PATH%
) else (
    echo No JSP cache found.
)

echo.
echo Copying new WAR file...
copy /Y "%WAR_FILE%" "%DEPLOY_WAR%"
echo Copied: %WAR_FILE%
echo To: %DEPLOY_WAR%

echo.
echo ========================================
echo Deployment completed!
echo ========================================
echo.
echo Next steps:
echo 1. Start Tomcat
echo 2. Access: http://localhost:8080/CosmeticShop
echo.
pause

