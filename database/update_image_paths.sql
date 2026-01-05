-- Script cập nhật image_url của buildings và room_images theo cấu trúc mới
-- Cấu trúc cũ: images/roomId/file.jpg
-- Cấu trúc mới: images/buildingId/roomId/file.jpg hoặc images/buildingId/main.jpg

USE roommanagement_db;

-- Cập nhật image_url cho buildings (10 dãy trọ)
UPDATE buildings SET image_url = '/images/1/main.jpg' WHERE id = 1;
UPDATE buildings SET image_url = '/images/2/main.jpg' WHERE id = 2;
UPDATE buildings SET image_url = '/images/3/main.jpg' WHERE id = 3;
UPDATE buildings SET image_url = '/images/4/main.jpg' WHERE id = 4;
UPDATE buildings SET image_url = '/images/5/main.jpg' WHERE id = 5;
UPDATE buildings SET image_url = '/images/6/main.jpg' WHERE id = 6;
UPDATE buildings SET image_url = '/images/7/main.jpg' WHERE id = 7;
UPDATE buildings SET image_url = '/images/8/main.jpg' WHERE id = 8;
UPDATE buildings SET image_url = '/images/9/main.jpg' WHERE id = 9;
UPDATE buildings SET image_url = '/images/10/main.jpg' WHERE id = 10;

-- Cập nhật image_url cho room_images
-- Hàm SQL để cập nhật: Lấy buildingId từ rooms và update path
UPDATE room_images ri
JOIN rooms r ON ri.room_id = r.id
JOIN buildings b ON r.building_id = b.id
SET ri.image_url = CONCAT('/images/', b.id, '/', r.id, '/main.jpg')
WHERE ri.image_url LIKE CONCAT('%', r.id, '%') AND ri.image_url LIKE '%main%';

-- Cập nhật ảnh detail (detail1, detail2, detail3, ...)
-- Giả sử mỗi phòng có nhiều ảnh, ta update theo pattern
UPDATE room_images ri
JOIN rooms r ON ri.room_id = r.id
JOIN buildings b ON r.building_id = b.id
SET ri.image_url = CONCAT('/images/', b.id, '/', r.id, '/detail1.jpg')
WHERE ri.image_url LIKE CONCAT('%', r.id, '%') AND ri.image_url LIKE '%detail1%';

UPDATE room_images ri
JOIN rooms r ON ri.room_id = r.id
JOIN buildings b ON r.building_id = b.id
SET ri.image_url = CONCAT('/images/', b.id, '/', r.id, '/detail2.jpg')
WHERE ri.image_url LIKE CONCAT('%', r.id, '%') AND ri.image_url LIKE '%detail2%';

UPDATE room_images ri
JOIN rooms r ON ri.room_id = r.id
JOIN buildings b ON r.building_id = b.id
SET ri.image_url = CONCAT('/images/', b.id, '/', r.id, '/detail3.jpg')
WHERE ri.image_url LIKE CONCAT('%', r.id, '%') AND ri.image_url LIKE '%detail3%';

-- Hiển thị kết quả
SELECT 'Buildings' as table_name, id, name, image_url FROM buildings LIMIT 10;
SELECT 'Room Images' as table_name, ri.id, r.name as room_name, ri.image_url 
FROM room_images ri 
JOIN rooms r ON ri.room_id = r.id 
LIMIT 20;
