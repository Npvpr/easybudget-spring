@echo off
setlocal

:: Define variables
set DB_USER=easybudgetadmin
set DB_PASSWORD=easybudgetpw
set DB_NAME=easybudgetdb
set BACKUP_DIR=..\db_backups
for /f "tokens=1-4 delims=/: " %%a in ('date /t') do set DATE=%%d-%%b-%%c_%%a
for /f "tokens=1-2 delims=:" %%a in ('time /t') do set TIME=%%a-%%b
set BACKUP_FILE=%BACKUP_DIR%\backup_%DATE%_%TIME%.sql

:: Run the mysqldump command
mysqldump -u %DB_USER% -p%DB_PASSWORD% %DB_NAME% > %BACKUP_FILE%

:: Check if the command succeeded
if %ERRORLEVEL% equ 0 (
    echo Backup completed successfully: %BACKUP_FILE%
) else (
    echo Backup failed.
    exit /b 1
)

endlocal
