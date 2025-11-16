-- ============================================================
-- RESET DATABASE - CHẠY TOÀN BỘ SCRIPT NÀY
-- ============================================================

USE roommanagement_db;

-- Tắt safe mode và foreign key checks
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

-- Xóa tất cả dữ liệu (giữ nguyên cấu trúc bảng)
TRUNCATE TABLE room_images;
TRUNCATE TABLE room_amenities;
TRUNCATE TABLE reviews;
TRUNCATE TABLE review_reports;
TRUNCATE TABLE contracts;
TRUNCATE TABLE invoices;
TRUNCATE TABLE invoice_items;
TRUNCATE TABLE payments;
TRUNCATE TABLE utilities_electric;
TRUNCATE TABLE utilities_water;
TRUNCATE TABLE utility_submissions;
TRUNCATE TABLE extra_costs;
TRUNCATE TABLE rooms;
TRUNCATE TABLE buildings;
TRUNCATE TABLE landlords;
TRUNCATE TABLE tenants;
TRUNCATE TABLE notifications;
TRUNCATE TABLE feedbacks;
TRUNCATE TABLE refresh_tokens;
TRUNCATE TABLE users;
TRUNCATE TABLE amenities;
TRUNCATE TABLE districts;
TRUNCATE TABLE provinces;

-- ============================================================
-- INSERT DỮ LIỆU MỚI
-- ============================================================

-- PROVINCES
INSERT INTO provinces (code, name, division_type) VALUES
(1, 'Bình Định', 'tỉnh'),
(2, 'TP.HCM', 'thành phố trực thuộc trung ương'),
(3, 'Phú Yên', 'tỉnh');

-- DISTRICTS
INSERT INTO districts (code, name, province_code, division_type) VALUES
(1, 'Quy Nhơn', 1, 'thành phố'),
(2, 'An Nhơn', 1, 'huyện'),
(3, 'Quận 7', 2, 'quận'),
(4, 'Tuy Hòa', 3, 'thành phố');

-- USERS
INSERT INTO users (username, password, full_name, email, phone, role, status) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Quản trị viên', 'admin@techroom.vn', '0909000001', 0, 'ACTIVE'),
('chutroplk', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Nguyễn Văn An', 'vanan@techroom.vn', '0909111001', 1, 'ACTIVE'),
('chutrohcm', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Trần Thị Bích', 'thibich@techroom.vn', '0909111002', 1, 'ACTIVE'),
('chutrodn', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Lê Minh Cường', 'lecuong@techroom.vn', '0909111003', 1, 'ACTIVE'),
('nguoithue1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Phạm Thị Dung', 'dungpham@gmail.com', '0909222001', 2, 'ACTIVE'),
('nguoithue2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Hoàng Văn Em', 'emhoang@gmail.com', '0909222002', 2, 'ACTIVE'),
('nguoithue3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Võ Thị Phượng', 'phuongvo@gmail.com', '0909222003', 2, 'ACTIVE');

-- LANDLORDS
INSERT INTO landlords (user_id, business_license, province_code, district_code, approved) VALUES
(2, 'GP123456', 1, 1, 'APPROVED'),
(3, 'GP654321', 2, 3, 'APPROVED'),
(4, 'GP789012', 3, 4, 'APPROVED');

-- BUILDINGS
INSERT INTO buildings (landlord_id, name, province_code, district_code, address, description) VALUES
(1, 'Dãy trọ Quy Nhơn', 1, 1, '69 Cần Vương, Quy Nhơn, Bình Định', 'Gồm 10 phòng, gần trung tâm, có sân phơi'),
(1, 'Dãy trọ 47 Nguyễn Nhạc', 1, 2, '47 Nguyễn Nhạc, An Nhơn, Bình Định', 'Phòng trọ sinh viên giá rẻ'),
(2, 'Nhà trọ Quận 7', 2, 3, '789 Nguyễn Thị Thập, Quận 7, TP.HCM', 'Phòng gia đình và căn hộ mini'),
(2, 'Dãy trọ Nguyễn Thị Minh Khai', 2, 3, '456 Nguyễn Thị Minh Khai, TP.HCM', 'Khu trọ cho dân văn phòng, yên tĩnh'),
(3, 'Dãy trọ Tuy Hòa', 3, 4, '110 Hùng Vương, Tuy Hòa, Phú Yên', 'Phòng gần biển, thoáng mát, giá hợp lý');

-- ROOMS
INSERT INTO rooms (building_id, name, price, area, status, description, created_at) VALUES
(1, 'Phòng trọ trung tâm TP Quy Nhơn, gần ĐH Quy Nhơn', 4500000, 25, 'AVAILABLE', 'Phòng sạch sẽ, gần trung tâm, có Wifi & máy lạnh, phù hợp người đi làm.', '2025-11-10 10:00:00'),
(2, 'Phòng trọ 47 Nguyễn Nhạc', 1500000, 20, 'AVAILABLE', 'Phòng nhỏ gọn, giá rẻ, phù hợp sinh viên.', '2025-09-15 14:00:00'),
(3, 'Phòng gia đình Quận 7', 7800000, 45, 'AVAILABLE', 'Phòng rộng, có bếp nấu ăn, máy giặt và chỗ đậu xe riêng.', '2025-10-20 08:00:00'),
(4, 'Dãy trọ đường 102 Nguyễn Thị Minh Khai', 6200000, 35, 'AVAILABLE', 'Phòng hiện đại, có điều hòa, wifi và sân để xe.', '2025-11-09 17:00:00'),
(5, 'Phòng trọ Hùng Vương Tuy Hòa', 2800000, 28, 'AVAILABLE', 'Phòng gần biển, view thoáng, có máy lạnh và wifi miễn phí.', '2025-09-01 12:00:00');

-- ✅ ROOM IMAGES - CHỈ LƯU TÊN FILE (KHÔNG CÓ /images/rooms/)
INSERT INTO room_images (room_id, image_url) VALUES
-- Room 1
(1, 'room1.jpg'),
(1, 'room1-detail1.jpg'),
(1, 'room1-detail2.jpg'),
(1, 'room1-detail3.jpg'),
(1, 'room1-detail4.jpg'),
-- Room 2
(2, 'room1-detail1.jpg'),
(2, 'room1-detail2.jpg'),
(2, 'room1-detail3.jpg'),
(2, 'room1-detail4.jpg'),
(2, 'room1-detail5.jpg'),
-- Room 3
(3, 'room1-detail3.jpg'),
(3, 'room1-detail4.jpg'),
(3, 'room1-detail5.jpg'),
(3, 'room1-detail1.jpg'),
-- Room 4
(4, 'room1-detail5.jpg'),
(4, 'room1-detail3.jpg'),
(4, 'room1-detail2.jpg'),
-- Room 5
(5, 'room1-detail2.jpg'),
(5, 'room1-detail3.jpg'),
(5, 'room1-detail4.jpg');

-- AMENITIES
INSERT INTO amenities (name, icon, description) VALUES
('Wifi miễn phí', 'fa-wifi', 'Truy cập Internet tốc độ cao miễn phí'),
('Máy lạnh', 'fa-snowflake', 'Điều hòa không khí trong phòng'),
('Chỗ đậu xe', 'fa-car', 'Khu vực đỗ xe riêng cho cư dân'),
('Bếp nấu ăn', 'fa-utensils', 'Có bếp và bồn rửa riêng'),
('Máy giặt', 'fa-soap', 'Có sẵn máy giặt trong phòng hoặc khu vực chung'),
('Hồ bơi', 'fa-water-ladder', 'Hồ bơi ngoài trời cho cư dân'),
('Thang máy', 'fa-elevator', 'Hỗ trợ di chuyển giữa các tầng dễ dàng');

-- ROOM AMENITIES
INSERT INTO room_amenities (room_id, amenity_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4),
(2, 1), (2, 4),
(3, 1), (3, 2), (3, 4), (3, 5),
(4, 1), (4, 2), (4, 3), (4, 5),
(5, 1), (5, 2), (5, 3);

-- TENANTS
INSERT INTO tenants (user_id, cccd, date_of_birth, province_code, district_code) VALUES
(5, '079123456789', '2000-04-12', 1, 2),
(6, '079987654321', '1999-10-22', 2, 3),
(7, '080123456789', '2001-05-15', 3, 4);

-- Bật lại safe mode và foreign key checks
SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;

-- ============================================================
-- ✅ VERIFY RESULTS
-- ============================================================
SELECT 
    'Provinces' as table_name, COUNT(*) as count FROM provinces
UNION ALL
SELECT 'Districts', COUNT(*) FROM districts
UNION ALL
SELECT 'Users', COUNT(*) FROM users
UNION ALL
SELECT 'Rooms', COUNT(*) FROM rooms
UNION ALL
SELECT 'Room Images', COUNT(*) FROM room_images;

-- Xem chi tiết room images
SELECT 
    r.id as room_id,
    r.name as room_name,
    ri.id as image_id,
    ri.image_url as filename,
    CONCAT('/images/', r.id, '/', ri.image_url) as full_url
FROM rooms r
JOIN room_images ri ON r.id = ri.room_id
ORDER BY r.id, ri.id;
