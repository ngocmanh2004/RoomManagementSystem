# Run SQL file to update database
$mysqlPath = "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe"
$sqlFile = "D:\RoomManagementSystem\database\RoomManagement_DB.sql"

Write-Host "Updating database with .png image paths..." -ForegroundColor Cyan
Write-Host "Please enter MySQL root password when prompted:" -ForegroundColor Yellow
Write-Host ""

& $mysqlPath -u root -p -e "source $sqlFile"

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "Database updated successfully!" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "Error updating database. Exit code: $LASTEXITCODE" -ForegroundColor Red
}
