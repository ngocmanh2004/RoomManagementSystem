USE roommanagement_db;

-- ========== USERS ==========
INSERT INTO users (username, password, full_name, email, phone, role)
VALUES
('admin', '123456', 'Quản trị viên', 'admin@techroom.vn', '0909000001', 0),
('landlord1', '123456', 'Nguyễn Văn A', 'vana@techroom.vn', '0909000002', 1),
('landlord2', '123456', 'Trần Thị B', 'thib@techroom.vn', '0909000003', 1),
('tenant1', '123456', 'Phạm Minh C', 'minhc@techroom.vn', '0909000004', 2),
('tenant2', '123456', 'Lê Ngọc D', 'ngocd@techroom.vn', '0909000005', 2);

-- ========== LANDLORDS ==========
INSERT INTO landlords (user_id, business_license, address, approved)
VALUES
(2, 'GP123456', '69 Cần Vương, Quy Nhơn, Bình Định', 'APPROVED'),
(3, 'GP654321', '102 Nguyễn Thị Minh Khai, TP.HCM', 'APPROVED');

-- ========== BUILDINGS ==========
INSERT INTO buildings (landlord_id, name, address, description)
VALUES
(1, 'Dãy trọ Quy Nhơn', '69 Cần Vương, Quy Nhơn, Bình Định', 'Gồm 10 phòng, gần trung tâm, có sân phơi'),
(1, 'Dãy trọ 47 Nguyễn Nhạc', '47 Nguyễn Nhạc, An Nhơn, Bình Định', 'Phòng trọ sinh viên giá rẻ'),
(2, 'Nhà trọ Quận 7', '789 Nguyễn Thị Thập, Quận 7, TP.HCM', 'Phòng gia đình và căn hộ mini'),
(2, 'Dãy trọ Nguyễn Thị Minh Khai', '456 Nguyễn Thị Minh Khai, TP.HCM', 'Khu trọ cho dân văn phòng, yên tĩnh'),
(2, 'Dãy trọ Tuy Hòa', '110 Hùng Vương, Tuy Hòa, Phú Yên', 'Phòng gần biển, thoáng mát, giá hợp lý');

-- ========== ROOMS ==========
INSERT INTO rooms (building_id, name, price, area, status, description)
VALUES
(1, 'Phòng trọ trung tâm TP Quy Nhơn, gần ĐH Quy Nhơn', 4500000, 25, 'AVAILABLE', 'Phòng sạch sẽ, gần trung tâm, có Wifi & máy lạnh, phù hợp người đi làm.'),
(2, 'Phòng trọ 47 Nguyễn Nhạc', 1500000, 20, 'AVAILABLE', 'Phòng nhỏ gọn, giá rẻ, phù hợp sinh viên.'),
(3, 'Phòng gia đình Quận 7', 7800000, 45, 'AVAILABLE', 'Phòng rộng, có bếp nấu ăn, máy giặt và chỗ đậu xe riêng.'),
(4, 'Dãy trọ đường 102 Nguyễn Thị Minh Khai', 6200000, 35, 'AVAILABLE', 'Phòng hiện đại, có điều hòa, wifi và sân để xe.'),
(5, 'Phòng trọ Hùng Vương Tuy Hòa', 2800000, 28, 'AVAILABLE', 'Phòng gần biển, view thoáng, có máy lạnh và wifi miễn phí.');

-- ========== ROOM IMAGES ==========
INSERT INTO room_images (room_id, image_url)
VALUES
-- Phòng 1 (ảnh chính + chi tiết)
(1, '/images/rooms/room1.jpg'),
(1, '/images/rooms/room1-detail1.jpg'),
(1, '/images/rooms/room1-detail2.jpg'),
(1, '/images/rooms/room1-detail3.jpg'),
(1, '/images/rooms/room1-detail4.jpg'),

-- Phòng 2
(2, '/images/rooms/room1-detail1.jpg'),
(2, '/images/rooms/room1-detail2.jpg'),
(2, '/images/rooms/room1-detail3.jpg'),
(2, '/images/rooms/room1-detail4.jpg'),
(2, '/images/rooms/room1-detail5.jpg'),

-- Phòng 3
(3, '/images/rooms/room1-detail3.jpg'),
(3, '/images/rooms/room1-detail4.jpg'),
(3, '/images/rooms/room1-detail5.jpg'),
(3, '/images/rooms/room1-detail1.jpg'),

-- Phòng 4
(4, '/images/rooms/room1-detail5.jpg'),
(4, '/images/rooms/room1-detail3.jpg'),
(4, '/images/rooms/room1-detail2.jpg'),

-- Phòng 5
(5, '/images/rooms/room1-detail2.jpg'),
(5, '/images/rooms/room1-detail3.jpg'),
(5, '/images/rooms/room1-detail4.jpg');

-- ========== AMENITIES ==========
INSERT INTO amenities (name, icon, description)
VALUES
('Wifi miễn phí', 'fa-wifi', 'Truy cập Internet tốc độ cao miễn phí'),
('Máy lạnh', 'fa-snowflake', 'Điều hòa không khí trong phòng'),
('Chỗ đậu xe', 'fa-car', 'Khu vực đỗ xe riêng cho cư dân'),
('Bếp nấu ăn', 'fa-utensils', 'Có bếp và bồn rửa riêng'),
('Máy giặt', 'fa-soap', 'Có sẵn máy giặt trong phòng hoặc khu vực chung'),
('Hồ bơi', 'fa-water-ladder', 'Hồ bơi ngoài trời cho cư dân'),
('Thang máy', 'fa-elevator', 'Hỗ trợ di chuyển giữa các tầng dễ dàng');

-- ========== ROOM - AMENITY RELATION ==========
INSERT INTO room_amenities (room_id, amenity_id)
VALUES
-- Quy Nhơn
(1, 1), (1, 2), (1, 3), (1, 4),
-- Nguyễn Nhạc
(2, 1), (2, 4),
-- Quận 7
(3, 1), (3, 2), (3, 4), (3, 5),
-- Nguyễn Thị Minh Khai
(4, 1), (4, 2), (4, 3), (4, 5),
-- Tuy Hòa
(5, 1), (5, 2), (5, 3);

-- ========== TENANTS ==========
INSERT INTO tenants (user_id, cccd, date_of_birth, address)
VALUES
(4, '079123456789', '2000-04-12', 'Tuy Phước, Bình Định'),
(5, '079987654321', '1999-10-22', 'Quận 7, TP.HCM');
