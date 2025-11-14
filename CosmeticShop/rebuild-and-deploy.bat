@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion
echo ========================================
echo Rebuild and Deploy Workflow
echo ========================================
echo.

echo === Step 1: Cleaning and Building WAR file ===
echo.

call mvn clean package -DskipTests

if errorlevel 1 (
    echo.
    echo ========================================
    echo Build failed!
    echo ========================================
    pause
    exit /b 1
)

echo.
echo Build successful! WAR file at: target\CosmeticShop-1.0-SNAPSHOT.war
echo.

set /p deploy="Do you want to deploy to Tomcat? y/n: "

if /i "!deploy!"=="y" (
    echo.
    echo === Step 2: Deploying to Tomcat ===
    echo.
    
    if not defined tomcatPath (
        set "tomcatPath=C:\Edu\Tools\apache-tomcat-11.0.13"
        echo Using default Tomcat path: !tomcatPath!
    ) else (
        echo Using predefined tomcatPath: !tomcatPath!
    )

    if not exist "!tomcatPath!" (
        echo Tomcat path not found: !tomcatPath!
        pause
        exit /b 1
    )
    
    set "webappsPath=!tomcatPath!\webapps"
    set "workPath=!tomcatPath!\work\Catalina\localhost\CosmeticShop"
    set "warFile=target\CosmeticShop-1.0-SNAPSHOT.war"
    set "deployWar=!webappsPath!\CosmeticShop.war"
    
    if not exist "!warFile!" (
        echo WAR file not found: !warFile!
        pause
        exit /b 1
    )
    
    echo Stopping Tomcat if running...
    echo Please stop Tomcat manually if needed
    echo.
    
    echo Removing old deployment...
    if exist "!webappsPath!\CosmeticShop" (
        rmdir /s /q "!webappsPath!\CosmeticShop"
        echo Removed: !webappsPath!\CosmeticShop
    )
    
    echo Clearing JSP cache...
    if exist "!workPath!" (
        rmdir /s /q "!workPath!"
        echo Removed JSP cache: !workPath!
    )
    
    echo Copying new WAR file...
    copy /Y "!warFile!" "!deployWar!"
    echo Copied WAR file to: !deployWar!
    
    echo.
    echo ========================================
    echo Deployment completed!
    echo ========================================
    echo.
    echo Next steps:
    echo 1. Start Tomcat
    echo 2. Access: http://localhost:8080/CosmeticShop
    echo 3. If still have JSP errors, clear work folder manually
) else (
    echo.
    echo Skipping deployment.
    echo WAR file ready at: target\CosmeticShop-1.0-SNAPSHOT.war
    echo.
    echo To deploy manually:
    echo 1. Stop Tomcat
    echo 2. Delete: webapps\CosmeticShop and work\Catalina\localhost\CosmeticShop
    echo 3. Copy WAR file to webapps\CosmeticShop.war
    echo 4. Start Tomcat
)

echo.
echo ========================================
echo Done!
echo ========================================
pause
endlocal
