# Script tổ chức lại ảnh theo cấu trúc buildingId/roomId
# Dãy 1-2: 10 phòng mỗi dãy
# Dãy 3-10: 3-8 phòng mỗi dãy

$baseDir = "D:\RoomManagementSystem\backend\images"
$oldDir = "D:\RoomManagementSystem\backend\images_old"

# Backup folder cũ
Write-Host "==> Backup folder images_old..."
if (Test-Path $oldDir) {
    Remove-Item -Recurse -Force $oldDir
}
Copy-Item -Path $baseDir -Destination $oldDir -Recurse

# Xóa folder images cũ và tạo mới
Write-Host "==> Xóa và tạo lại folder images..."
Remove-Item -Recurse -Force $baseDir
New-Item -ItemType Directory -Path $baseDir | Out-Null

# Danh sách ảnh có sẵn từ folder cũ (1-10)
$availableImages = @()
for ($i = 1; $i -le 10; $i++) {
    $folderPath = "$oldDir\$i"
    if (Test-Path $folderPath) {
        $images = Get-ChildItem -Path $folderPath -File | Select-Object -ExpandProperty FullName
        $availableImages += $images
    }
}

Write-Host "==> Tìm thấy $($availableImages.Count) ảnh có sẵn"

# Hàm coppy ảnh và đặt tên
function Copy-RoomImages {
    param(
        [int]$buildingId,
        [int]$roomId,
        [string]$mainImageSource,
        [string[]]$detailImageSources
    )
    
    $roomFolder = "$baseDir\$buildingId\$roomId"
    New-Item -ItemType Directory -Path $roomFolder -Force | Out-Null
    
    # Coppy ảnh main
    $ext = [System.IO.Path]::GetExtension($mainImageSource)
    Copy-Item -Path $mainImageSource -Destination "$roomFolder\main$ext" -Force
    
    # Coppy ảnh detail (ít nhất 3 ảnh)
    for ($i = 0; $i -lt $detailImageSources.Count; $i++) {
        $detailExt = [System.IO.Path]::GetExtension($detailImageSources[$i])
        $detailNum = $i + 1
        Copy-Item -Path $detailImageSources[$i] -Destination "$roomFolder\detail$detailNum$detailExt" -Force
    }
    
    Write-Host "  Created: $buildingId/$roomId (1 main + $($detailImageSources.Count) details)"
}

# Hàm coppy ảnh building
function Copy-BuildingImage {
    param(
        [int]$buildingId,
        [string]$imageSource
    )
    
    $buildingFolder = "$baseDir\$buildingId"
    New-Item -ItemType Directory -Path $buildingFolder -Force | Out-Null
    
    $ext = [System.IO.Path]::GetExtension($imageSource)
    Copy-Item -Path $imageSource -Destination "$buildingFolder\main$ext" -Force
    
    Write-Host "Building $buildingId - main image created"
}

Write-Host ""
Write-Host "==> Bắt đầu tạo cấu trúc ảnh mới..."
Write-Host ""

# Index để lấy ảnh từ danh sách
$imageIndex = 0

# Cấu trúc phòng theo database
# Building 1: 10 phòng (room_id: 1-10)
# Building 2: 10 phòng (room_id: 11-20)  
# Building 3-10: 3-8 phòng mỗi dãy

$buildings = @(
    @{id=1; rooms=@(1,2,3,4,5,6,7,8,9,10)},
    @{id=2; rooms=@(11,12,13,14,15,16,17,18,19,20)},
    @{id=3; rooms=@(21,22,23,24,25)},
    @{id=4; rooms=@(26,27,28,29)},
    @{id=5; rooms=@(30,31,32,33,34,35)},
    @{id=6; rooms=@(36,37,38,39,40,41,42)},
    @{id=7; rooms=@(43,44,45,46,47,48,49,50)},
    @{id=8; rooms=@(51,52,53,54,55)},
    @{id=9; rooms=@(56,57,58,59,60)},
    @{id=10; rooms=@(61,62)}
)

foreach ($building in $buildings) {
    $buildingId = $building.id
    $rooms = $building.rooms
    
    Write-Host "==> Building $buildingId ($($rooms.Count) phòng)"
    
    # Ảnh building (lấy ảnh đầu tiên của building)
    if ($imageIndex -lt $availableImages.Count) {
        Copy-BuildingImage -buildingId $buildingId -imageSource $availableImages[$imageIndex]
        $buildingMainImage = $imageIndex
        $imageIndex++
    }
    
    # Tạo ảnh cho từng phòng
    foreach ($roomId in $rooms) {
        # Ảnh main của phòng
        $mainIdx = $imageIndex % $availableImages.Count
        $mainImage = $availableImages[$mainIdx]
        $imageIndex++
        
        # Ảnh detail (3-5 ảnh)
        $numDetails = Get-Random -Minimum 3 -Maximum 6
        $detailImages = @()
        for ($d = 0; $d -lt $numDetails; $d++) {
            $detailIdx = ($imageIndex + $d) % $availableImages.Count
            $detailImages += $availableImages[$detailIdx]
        }
        $imageIndex += $numDetails
        
        Copy-RoomImages -buildingId $buildingId -roomId $roomId -mainImageSource $mainImage -detailImageSources $detailImages
    }
    
    Write-Host ""
}

Write-Host ""
Write-Host "==> Hoàn thành! Cấu trúc mới:"
Write-Host "    images/"
Write-Host "      1/"
Write-Host "        main.jpg (ảnh dãy trọ 1)"
Write-Host "        1/main.jpg, detail1.jpg, detail2.jpg, ..."
Write-Host "        2/main.jpg, detail1.jpg, detail2.jpg, ..."
Write-Host "        ..."
Write-Host ""
Write-Host "Backup lưu tại: $oldDir"
