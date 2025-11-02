-- Xóa dữ liệu cũ (để tránh lỗi ràng buộc khóa ngoại)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE room_images;
TRUNCATE TABLE rooms;
TRUNCATE TABLE buildings;
TRUNCATE TABLE landlords;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- ==============================================
-- 1. USERS (tạo 1 admin, 1 landlord, 1 tenant)
-- ==============================================
INSERT INTO users (username, password, full_name, email, phone, role, status)
VALUES
('admin', '123456', 'Quản trị viên', 'admin@example.com', '0900000000', 0, 'ACTIVE'),
('landlord1', '123456', 'Nguyễn Văn A', 'landlord1@example.com', '0901111111', 1, 'ACTIVE'),
('tenant1', '123456', 'Trần Thị B', 'tenant1@example.com', '0902222222', 2, 'ACTIVE');

-- ==============================================
-- 2. LANDLORDS
-- ==============================================
INSERT INTO landlords (user_id, business_license, address, approved)
VALUES
(2, 'BL-001', '47 Nguyễn Nhạc, An Nhơn, Gia Lai', 'APPROVED');

-- ==============================================
-- 3. BUILDINGS
-- ==============================================
INSERT INTO buildings (landlord_id, name, address, description)
VALUES
(1, 'Dãy trọ Nguyễn Nhạc',
'47 Nguyễn Nhạc, Phường Nhơn Hưng, Thị xã An Nhơn, Tỉnh Gia Lai',
'Dãy trọ gồm nhiều phòng có gác, gần trung tâm, tiện sinh hoạt.');

-- ==============================================
-- 4. ROOMS
-- ==============================================
INSERT INTO rooms (building_id, name, price, area, status, description)
VALUES
(1, 'Phòng trọ 47 Nguyễn Nhạc', 1500000.00, 20.00, 'AVAILABLE',
 'Phòng sạch sẽ, gần đường, có gác lửng, có sân phơi, cây xanh, ban công không gian thoáng mát. Thích hợp cho sinh viên và người đi làm.'),
(1, 'Phòng trọ trung tâm TP Quy Nhơn', 4500000.00, 25.00, 'AVAILABLE',
 'Phòng ngay trung tâm thành phố, gần Đại học Quy Nhơn, có nội thất cơ bản, 2 giường, toilet riêng, an ninh 24/7.');

-- ==============================================
-- 5. ROOM_IMAGES
-- ==============================================
-- Phòng 1 (47 Nguyễn Nhạc)
INSERT INTO room_images (room_id, image_url)
VALUES
(1, '/images/rooms/room1.jpg'),
(1, '/images/rooms/room1-detail1.jpg'),
(1, '/images/rooms/room1-detail2.jpg'),
(1, '/images/rooms/room1-detail3.jpg'),
(1, '/images/rooms/room1-detail4.jpg'),
(1, '/images/rooms/room1-detail5.jpg');

-- Phòng 2 (Quy Nhơn)
INSERT INTO room_images (room_id, image_url)
VALUES
(2, '/images/rooms/room2.jpg'),
(2, '/images/rooms/room2-detail1.jpg'),
(2, '/images/rooms/room2-detail2.jpg'),
(2, '/images/rooms/room2-detail3.jpg'),
(2, '/images/rooms/room2-detail4.jpg');
