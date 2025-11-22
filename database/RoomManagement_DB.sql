-- ============================================================
-- FULL DATABASE SCHEMA + DATA
-- ============================================================

DROP DATABASE IF EXISTS roommanagement_db;
CREATE DATABASE roommanagement_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE roommanagement_db;

-- ============================================================
-- 1. USERS
-- ============================================================
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(100) UNIQUE,
  phone VARCHAR(20),
  role TINYINT NOT NULL DEFAULT 2 COMMENT '0=Admin, 1=Chủ trọ, 2=Khách thuê',
  status ENUM('ACTIVE','BANNED','PENDING') DEFAULT 'ACTIVE',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_username (username),
  INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 2. PROVINCES & DISTRICTS
-- ============================================================
CREATE TABLE provinces (
  code INT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  division_type VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE districts (
  code INT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  province_code INT NOT NULL,
  division_type VARCHAR(50),
  FOREIGN KEY (province_code) REFERENCES provinces(code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 3. LANDLORD_REQUESTS (YÊU CẦU ĐĂNG KÝ CHỦ TRỌ)
-- ============================================================
CREATE TABLE landlord_requests (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  cccd VARCHAR(20) NOT NULL,
  address VARCHAR(255) NOT NULL,
  expected_room_count INT NOT NULL,
  province_code INT,
  district_code INT,
  front_image_path VARCHAR(255) NOT NULL,
  back_image_path VARCHAR(255) NOT NULL,
  business_license_path VARCHAR(255) NOT NULL,
  status ENUM('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING' NOT NULL,
  rejection_reason TEXT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  processed_at DATETIME,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (province_code) REFERENCES provinces(code),
  FOREIGN KEY (district_code) REFERENCES districts(code),
  INDEX idx_user_id (user_id),
  INDEX idx_status (status),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 4. LANDLORDS (CHỦ TRỌ ĐÃ ĐƯỢC DUYỆT)
-- ============================================================
CREATE TABLE landlords (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL UNIQUE,
  cccd VARCHAR(20),
  address VARCHAR(255),
  expected_room_count INT,
  province_code INT,
  district_code INT,
  front_image_path VARCHAR(255),
  back_image_path VARCHAR(255),
  business_license_path VARCHAR(255),
  approved ENUM('APPROVED') DEFAULT 'APPROVED',
  utility_mode ENUM('LANDLORD_INPUT','TENANT_SUBMIT') DEFAULT 'LANDLORD_INPUT',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (province_code) REFERENCES provinces(code),
  FOREIGN KEY (district_code) REFERENCES districts(code),
  INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 5. TENANTS
-- ============================================================
CREATE TABLE tenants (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL UNIQUE,
  cccd VARCHAR(20) UNIQUE,
  date_of_birth DATE,
  province_code INT,
  district_code INT,
  address VARCHAR(255),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (province_code) REFERENCES provinces(code),
  FOREIGN KEY (district_code) REFERENCES districts(code),
  INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 6. BUILDINGS
-- ============================================================
CREATE TABLE buildings (
  id INT AUTO_INCREMENT PRIMARY KEY,
  landlord_id INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  province_code INT,
  district_code INT,
  address VARCHAR(255),
  description TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (landlord_id) REFERENCES landlords(id) ON DELETE CASCADE,
  FOREIGN KEY (province_code) REFERENCES provinces(code),
  FOREIGN KEY (district_code) REFERENCES districts(code),
  INDEX idx_landlord_id (landlord_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 7. ROOMS
-- ============================================================
CREATE TABLE rooms (
  id INT AUTO_INCREMENT PRIMARY KEY,
  building_id INT NOT NULL,
  name VARCHAR(120) NOT NULL,
  price DECIMAL(12,2) NOT NULL,
  area DECIMAL(5,2),
  status ENUM('AVAILABLE','OCCUPIED','REPAIRING') DEFAULT 'AVAILABLE',
  description TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE CASCADE,
  INDEX idx_building_id (building_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 8. ROOM_IMAGES
-- ============================================================
CREATE TABLE room_images (
  id INT AUTO_INCREMENT PRIMARY KEY,
  room_id INT NOT NULL,
  image_url VARCHAR(255) NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
  INDEX idx_room_id (room_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 9. AMENITIES & ROOM_AMENITIES
-- ============================================================
CREATE TABLE amenities (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  icon VARCHAR(255),
  description VARCHAR(255),
  INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE room_amenities (
  room_id INT NOT NULL,
  amenity_id INT NOT NULL,
  PRIMARY KEY (room_id, amenity_id),
  FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
  FOREIGN KEY (amenity_id) REFERENCES amenities(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 10. CONTRACTS
-- ============================================================
CREATE TABLE contracts (
  id INT AUTO_INCREMENT PRIMARY KEY,
  room_id INT NOT NULL,
  tenant_id INT NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE,
  deposit DECIMAL(12,2) DEFAULT 0,
  status ENUM('PENDING','ACTIVE','EXPIRED','CANCELLED') DEFAULT 'PENDING',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
  FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
  INDEX idx_room_id (room_id),
  INDEX idx_tenant_id (tenant_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 11. INVOICES & INVOICE_ITEMS
-- ============================================================
CREATE TABLE invoices (
  id INT AUTO_INCREMENT PRIMARY KEY,
  contract_id INT NOT NULL,
  month VARCHAR(7) NOT NULL,
  total_amount DECIMAL(12,2) DEFAULT 0,
  status ENUM('UNPAID','PAID','OVERDUED','PENDING_CONFIRM') DEFAULT 'UNPAID',
  issue_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_invoice_month (contract_id, month),
  FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE,
  INDEX idx_contract_id (contract_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE invoice_items (
  id INT AUTO_INCREMENT PRIMARY KEY,
  invoice_id INT NOT NULL,
  type ENUM('RENT','ELECTRICITY','WATER','SERVICE','OTHER'),
  description VARCHAR(255),
  amount DECIMAL(12,2) NOT NULL,
  FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
  INDEX idx_invoice_id (invoice_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 12. PAYMENTS
-- ============================================================
CREATE TABLE payments (
  id INT AUTO_INCREMENT PRIMARY KEY,
  invoice_id INT NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  method ENUM('BANK','MOMO','ZALO','CASH'),
  status ENUM('SUCCESS','PENDING','FAILED') DEFAULT 'PENDING',
  proof_url VARCHAR(255),
  paid_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
  INDEX idx_invoice_id (invoice_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 13. UTILITIES (ELECTRIC & WATER)
-- ============================================================
CREATE TABLE utilities_electric (
  id INT AUTO_INCREMENT PRIMARY KEY,
  room_id INT NOT NULL,
  old_index INT,
  new_index INT,
  unit_price DECIMAL(10,2),
  total_amount DECIMAL(12,2),
  meter_photo_url VARCHAR(255),
  month VARCHAR(7),
  status ENUM('PAID','UNPAID') DEFAULT 'UNPAID',
  source ENUM('LANDLORD','TENANT','SYSTEM') DEFAULT 'LANDLORD',
  UNIQUE KEY uq_electric_room_month (room_id, month),
  FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
  INDEX idx_room_id (room_id),
  INDEX idx_month (month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE utilities_water (
  id INT AUTO_INCREMENT PRIMARY KEY,
  room_id INT NOT NULL,
  old_index INT,
  new_index INT,
  unit_price DECIMAL(10,2),
  total_amount DECIMAL(12,2),
  meter_photo_url VARCHAR(255),
  month VARCHAR(7),
  status ENUM('PAID','UNPAID') DEFAULT 'UNPAID',
  source ENUM('LANDLORD','TENANT','SYSTEM') DEFAULT 'LANDLORD',
  UNIQUE KEY uq_water_room_month (room_id, month),
  FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
  INDEX idx_room_id (room_id),
  INDEX idx_month (month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 14. UTILITY_SUBMISSIONS
-- ============================================================
CREATE TABLE utility_submissions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  tenant_id INT NOT NULL,
  room_id INT NOT NULL,
  type ENUM('ELECTRICITY','WATER') NOT NULL,
  new_index INT NOT NULL,
  photo_url VARCHAR(255),
  month VARCHAR(7) NOT NULL,
  status ENUM('PENDING','VERIFIED','REJECTED') DEFAULT 'PENDING',
  verified_by INT,
  verified_at DATETIME,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
  FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
  FOREIGN KEY (verified_by) REFERENCES users(id),
  INDEX idx_tenant_id (tenant_id),
  INDEX idx_room_id (room_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 15. EXTRA_COSTS
-- ============================================================
CREATE TABLE extra_costs (
  id INT AUTO_INCREMENT PRIMARY KEY,
  room_id INT,
  type ENUM('INTERNET','CLEANING','MAINTENANCE','OTHER'),
  description TEXT,
  amount DECIMAL(12,2),
  month VARCHAR(7),
  status ENUM('PAID','UNPAID') DEFAULT 'UNPAID',
  FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
  INDEX idx_room_id (room_id),
  INDEX idx_month (month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 16. REVIEWS & REPORTS
-- ============================================================
CREATE TABLE reviews (
  id INT AUTO_INCREMENT PRIMARY KEY,
  room_id INT NOT NULL,
  tenant_id INT NOT NULL,
  rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
  comment LONGTEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uq_review_room_tenant (room_id, tenant_id),
  FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
  FOREIGN KEY (tenant_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_reviews_room_id (room_id),
  INDEX idx_reviews_tenant_id (tenant_id),
  INDEX idx_reviews_created_at (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE review_reports (
  id INT AUTO_INCREMENT PRIMARY KEY,
  review_id INT NOT NULL,
  reporter_id INT NOT NULL,
  reason VARCHAR(255) NOT NULL DEFAULT 'OTHER',
  description LONGTEXT,
  status ENUM('PENDING','RESOLVED','DISMISSED') DEFAULT 'PENDING',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE,
  FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_review_reports_review_id (review_id),
  INDEX idx_review_reports_reporter_id (reporter_id),
  INDEX idx_review_reports_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 17. NOTIFICATIONS
-- ============================================================
CREATE TABLE notifications (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  title VARCHAR(100),
  message TEXT,
  type ENUM('SYSTEM','UTILITY_REQUEST','UTILITY_CONFIRMED','PAYMENT_RECEIVED','FEEDBACK') DEFAULT 'SYSTEM',
  is_read TINYINT(1) DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  INDEX idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 18. FEEDBACKS
-- ============================================================
CREATE TABLE feedbacks (
  id INT AUTO_INCREMENT PRIMARY KEY,
  sender_id INT NOT NULL,
  receiver_id INT NOT NULL,
  content TEXT,
  rating INT CHECK (rating BETWEEN 1 AND 5),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_sender_id (sender_id),
  INDEX idx_receiver_id (receiver_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 19. REFRESH_TOKENS (JWT)
-- ============================================================
CREATE TABLE refresh_tokens (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  token VARCHAR(500) NOT NULL,
  expiry_date DATETIME NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  INDEX idx_token (token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- INSERT DỮ LIỆU
-- ============================================================

SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

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
('manhdz123', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Phạm Thị Dung', 'dungpham@gmail.com', '0909222001', 2, 'ACTIVE'),
('nguoithue2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Hoàng Văn Em', 'emhoang@gmail.com', '0909222002', 2, 'ACTIVE'),
('nguoithue3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Võ Thị Phượng', 'phuongvo@gmail.com', '0909222003', 2, 'ACTIVE');

-- LANDLORDS
INSERT INTO landlords (user_id, cccd, address, expected_room_count, province_code, district_code, business_license_path, approved) VALUES
(2, '079123456789', '69 Cần Vương, Quy Nhơn', 10, 1, 1, 'GP123456', 'APPROVED'),
(3, '080987654321', '789 Nguyễn Thị Thập, TP.HCM', 8, 2, 3, 'GP654321', 'APPROVED'),
(4, '081123456789', '110 Hùng Vương, Tuy Hòa', 5, 3, 4, 'GP789012', 'APPROVED');

-- BUILDINGS
INSERT INTO buildings (landlord_id, name, province_code, district_code, address, description) VALUES
(1, 'Dãy trọ Quy Nhơn', 1, 1, '69 Cần Vương, Quy Nhơn, Bình Định', 'Gồm 10 phòng, gần trung tâm, có sân phơi'),
(1, 'Dãy trọ 47 Nguyễn Nhạc', 1, 2, '47 Nguyễn Nhạc, An Nhơn, Bình Định', 'Phòng trọ sinh viên giá rẻ'),
(2, 'Nhà trọ Quận 7', 2, 3, '789 Nguyễn Thị Thập, Quận 7, TP.HCM', 'Phòng gia đình và căn hộ mini'),
(2, 'Dãy trọ Nguyễn Thị Minh Khai', 2, 3, '456 Nguyễn Thị Minh Khai, TP.HCM', 'Khu trọ cho dân văn phòng, yên tĩnh'),
(3, 'Dãy trọ Tuy Hòa', 3, 4, '110 Hùng Vương, Tuy Hòa, Phú Yên', 'Phòng gần biển, thoáng mát, giá hợp lý');

-- ROOMS
INSERT INTO rooms (building_id, name, price, area, status, description) VALUES
(1, 'Phòng trọ trung tâm TP Quy Nhơn, gần ĐH Quy Nhơn', 4500000, 25, 'AVAILABLE', 'Phòng sạch sẽ, gần trung tâm, có Wifi & máy lạnh, phù hợp người đi làm.'),
(2, 'Phòng trọ 47 Nguyễn Nhạc', 1500000, 20, 'AVAILABLE', 'Phòng nhỏ gọn, giá rẻ, phù hợp sinh viên.'),
(3, 'Phòng gia đình Quận 7', 7800000, 45, 'AVAILABLE', 'Phòng rộng, có bếp nấu ăn, máy giặt và chỗ đậu xe riêng.'),
(4, 'Dãy trọ đường 102 Nguyễn Thị Minh Khai', 6200000, 35, 'AVAILABLE', 'Phòng hiện đại, có điều hòa, wifi và sân để xe.'),
(5, 'Phòng trọ Hùng Vương Tuy Hòa', 2800000, 28, 'AVAILABLE', 'Phòng gần biển, view thoáng, có máy lạnh và wifi miễn phí.');

-- ROOM IMAGES
INSERT INTO room_images (room_id, image_url) VALUES
(1, 'room1.jpg'), (1, 'room1-detail1.jpg'), (1, 'room1-detail2.jpg'), (1, 'room1-detail3.jpg'), (1, 'room1-detail4.jpg'),
(2, 'room1-detail1.jpg'), (2, 'room1-detail2.jpg'), (2, 'room1-detail3.jpg'), (2, 'room1-detail4.jpg'), (2, 'room1-detail5.jpg'),
(3, 'room1-detail3.jpg'), (3, 'room1-detail4.jpg'), (3, 'room1-detail5.jpg'), (3, 'room1-detail1.jpg'),
(4, 'room1-detail5.jpg'), (4, 'room1-detail3.jpg'), (4, 'room1-detail2.jpg'),
(5, 'room1-detail2.jpg'), (5, 'room1-detail3.jpg'), (5, 'room1-detail4.jpg');

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

-- CONTRACTS
INSERT INTO contracts (room_id, tenant_id, start_date, end_date, deposit, status) VALUES
(1, 1, '2025-09-01', '2025-12-01', 9000000, 'EXPIRED'),
(2, 2, '2025-10-01', '2026-01-01', 3000000, 'ACTIVE'),
(3, 3, '2025-08-15', '2025-11-15', 15600000, 'EXPIRED');

-- REVIEWS
INSERT INTO reviews (room_id, tenant_id, rating, comment) VALUES
(1, 5, 5, 'Phòng rất sạch sẽ, view đẹp, wifi nhanh. Chủ trọ thân thiện, hỗ trợ tốt. Giá hợp lý, rất hài lòng!'),
(2, 6, 4, 'Phòng nhỏ nhưng gọn gàng, tiện nghi cơ bản đầy đủ. Khu vực yên tĩnh, tốt cho học tập. Chỉ tiếc wifi hơi yếu lúc cao điểm.'),
(3, 7, 5, 'Phòng rộng rãi, bếp nấu ăn đầy đủ, máy giặt tiện lợi. An ninh tốt, khu vực sạch sẽ. Rất thích ở đây!');

SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;

-- ============================================================
-- ✅ VERIFY
-- ============================================================
SELECT COUNT(*) as total_users FROM users;
SELECT COUNT(*) as total_rooms FROM rooms;
SELECT COUNT(*) as total_reviews FROM reviews;
SELECT * FROM reviews;