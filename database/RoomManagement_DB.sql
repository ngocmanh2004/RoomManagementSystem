-- ============================================================
-- ROOM MANAGEMENT SYSTEM DATABASE
-- 10 DÃY TRỌ: 2 dãy đầu 10 phòng, 8 dãy còn lại 3-8 phòng
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
  image_url VARCHAR(255),
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
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
-- 10. CONTRACTS (ĐÃ SỬA - THÊM CÁC TRƯỜNG THIẾU)
-- ============================================================
CREATE TABLE contracts (
  id INT AUTO_INCREMENT PRIMARY KEY,
  contract_code VARCHAR(20) UNIQUE,
  room_id INT NOT NULL,
  tenant_id INT NOT NULL,
  full_name VARCHAR(100) NOT NULL,
  cccd VARCHAR(20) NOT NULL,
  phone VARCHAR(20) NOT NULL,
  address VARCHAR(255) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE,
  deposit DECIMAL(12,2) DEFAULT 0,
  monthly_rent DECIMAL(12,2),
  notes TEXT,
  rejection_reason TEXT,
  status ENUM('PENDING','APPROVED','ACTIVE','EXPIRED','CANCELLED','REJECTED') DEFAULT 'PENDING',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
  note VARCHAR(255),
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
  sender_id INT NULL,
  title VARCHAR(100),
  message TEXT,
  type ENUM(
    'SYSTEM',
    'LANDLORD_REQUEST','LANDLORD_APPROVED','LANDLORD_REJECTED','LANDLORD_REVOKED',
    'BOOKING_CREATED','BOOKING_APPROVED','BOOKING_REJECTED','BOOKING_CANCELLED',
    'CONTRACT_CREATED','CONTRACT_PENDING','CONTRACT_APPROVED','CONTRACT_REJECTED',
    'CONTRACT_ACTIVE','CONTRACT_EXPIRED','CONTRACT_CANCELLED','CONTRACT_RENEWED',
    'INVOICE_CREATED','INVOICE_REMINDER','PAYMENT_PENDING','PAYMENT_RECEIVED',
    'PAYMENT_OVERDUE','PAYMENT_REJECTED',
    'UTILITY_SUBMITTED','UTILITY_REQUEST','UTILITY_CONFIRMED','UTILITY_REJECTED',
    'FEEDBACK_CREATED','FEEDBACK_PROCESSING','FEEDBACK_RESOLVED','FEEDBACK_REJECTED','FEEDBACK_CANCELLED',
    'REVIEW_POSTED','REVIEW_REPORTED','REVIEW_REPORT_RESOLVED','REVIEW_DELETED',
    'ROOM_STATUS_CHANGED','ROOM_MAINTENANCE','ROOM_AVAILABLE',
    'USER_BANNED','USER_UNBANNED','USER_ROLE_CHANGED',
    'MAINTENANCE_SCHEDULED','MAINTENANCE_COMPLETED','MAINTENANCE_CANCELLED'
  ) DEFAULT 'SYSTEM',
  is_read TINYINT(1) DEFAULT 0,
  status VARCHAR(20) NOT NULL DEFAULT 'SENT',
  send_to VARCHAR(20) NULL,
  room_ids JSON NULL,
  sent_at DATETIME NULL,
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
    tenant_id INT NULL,
    room_id INT NULL,
    title VARCHAR(100),
    content TEXT,
    attachment_url VARCHAR(255),
    status ENUM('PENDING','PROCESSING','RESOLVED','CANCELED','TENANT_CONFIRMED','TENANT_REJECTED') DEFAULT 'PENDING',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    resolved_at DATETIME NULL,
    landlord_note TEXT NULL COMMENT 'Ghi chú của chủ trọ khi xử lý',
    tenant_feedback TEXT NULL COMMENT 'Phản hồi của khách sau khi xử lý',
    tenant_satisfied TINYINT(1) NULL COMMENT '1=hài lòng, 0=chưa hài lòng',
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id),
    FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    FOREIGN KEY (room_id) REFERENCES rooms(id)
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
(3, 'Phù Cát', 1, 'huyện'),
(4, 'Tuy Phước', 1, 'huyện'),
(5, 'Quận 7', 2, 'quận'),
(6, 'Quận 1', 2, 'quận'),
(7, 'Quận Thủ Đức', 2, 'quận'),
(8, 'Tuy Hòa', 3, 'thành phố'),
(9, 'Sông Cầu', 3, 'thị xã'),
(10, 'Đông Hòa', 3, 'huyện');

-- USERS
INSERT INTO users (username, password, full_name, email, phone, role, status) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Quản trị viên', 'admin@techroom.vn', '0909000001', 0, 'ACTIVE'),
('chutroplk', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Nguyễn Văn An', 'vanan@techroom.vn', '0909111001', 1, 'ACTIVE'),
('chutrohcm', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Trần Thị Bích', 'thibich@techroom.vn', '0909111002', 1, 'ACTIVE'),
('chutrodn', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Lê Minh Cường', 'lecuong@techroom.vn', '0909111003', 1, 'ACTIVE'),
('user', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Phạm Thị Dung', 'dungpham@gmail.com', '0909222001', 2, 'ACTIVE'),
('nguoithue2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Hoàng Văn Em', 'emhoang@gmail.com', '0909222002', 2, 'ACTIVE'),
('nguoithue3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Võ Thị Phượng', 'phuongvo@gmail.com', '0909222003', 2, 'ACTIVE');

-- LANDLORDS
INSERT INTO landlords (user_id, cccd, address, expected_room_count, province_code, district_code, business_license_path, approved) VALUES
(2, '079123456789', '69 Cần Vương, Quy Nhơn', 50, 1, 1, 'GP123456', 'APPROVED'),
(3, '080987654321', '789 Nguyễn Thị Thập, TP.HCM', 80, 2, 5, 'GP654321', 'APPROVED'),
(4, '081123456789', '110 Hùng Vương, Tuy Hòa', 50, 3, 8, 'GP789012', 'APPROVED');

-- BUILDINGS (10 dãy trọ)
INSERT INTO buildings (landlord_id, name, province_code, district_code, address, description, image_url) VALUES
(1, 'Dãy trọ Quy Nhơn', 1, 1, '69 Cần Vương, Quy Nhơn, Bình Định', 'Gần trung tâm, có sân phơi, bảo vệ 24/7', '1/main.jpg'),
(1, 'Nhà trọ Nguyễn Huệ', 1, 1, '128 Nguyễn Huệ, Quy Nhơn, Bình Định', 'View biển, thoáng mát, gần chợ', '2/main.jpg'),
(1, 'Dãy trọ 47 Nguyễn Nhạc', 1, 2, '47 Nguyễn Nhạc, An Nhơn, Bình Định', 'Phòng trọ sinh viên giá rẻ, gần trường', '3/main.jpg'),
(1, 'Nhà trọ An Phú', 1, 2, '89 Lê Duẩn, An Nhơn, Bình Định', 'Khu yên tĩnh, an ninh tốt', '4/main.jpg'),
(1, 'Dãy trọ Phù Cát Center', 1, 3, '234 Hùng Vương, Phù Cát, Bình Định', 'Gần khu công nghiệp, phù hợp công nhân', '5/main.jpg'),
(2, 'Nhà trọ Quận 7', 2, 5, '789 Nguyễn Thị Thập, Quận 7, TP.HCM', 'Căn hộ mini cao cấp, đầy đủ nội thất', '6/main.jpg'),
(2, 'Dãy trọ Phú Mỹ Hưng', 2, 5, '456 Nguyễn Văn Linh, Quận 7, TP.HCM', 'Khu compound an ninh, có hồ bơi', '7/main.jpg'),
(2, 'Nhà trọ Bến Thành', 2, 6, '78 Lê Thánh Tôn, Quận 1, TP.HCM', 'Vị trí đắc địa, view đẹp', '8/main.jpg'),
(3, 'Dãy trọ Tuy Hòa', 3, 8, '110 Hùng Vương, Tuy Hòa, Phú Yên', 'Gần biển, thoáng mát, view đẹp', '9/main.jpg'),
(3, 'Nhà trọ Phú Yên Center', 3, 8, '45 Trần Phú, Tuy Hòa, Phú Yên', 'Trung tâm thành phố, đầy đủ tiện ích', '10/main.jpg');

-- ROOMS (62 phòng: Building 1-2 có 10 phòng, còn lại 3-8 phòng)
INSERT INTO rooms (building_id, name, price, area, status, description) VALUES
(1, 'Phòng 101', 4500000, 25, 'AVAILABLE', 'Phòng sạch sẽ, gần trung tâm, có Wifi & máy lạnh'),
(1, 'Phòng 102', 4200000, 23, 'OCCUPIED', 'Phòng tầng 1, thoáng mát, đầy đủ nội thất'),
(1, 'Phòng 103', 4800000, 28, 'AVAILABLE', 'Phòng góc, view đẹp, có ban công'),
(1, 'Phòng 201', 5000000, 30, 'AVAILABLE', 'Phòng rộng, có bếp riêng, WC khép kín'),
(1, 'Phòng 202', 4500000, 25, 'OCCUPIED', 'Phòng tầng 2, yên tĩnh, an ninh tốt'),
(1, 'Phòng 203', 4700000, 26, 'AVAILABLE', 'Có máy lạnh, nóng lạnh, giường tủ đầy đủ'),
(1, 'Phòng 301', 5200000, 32, 'AVAILABLE', 'Phòng VIP tầng cao, view thành phố'),
(1, 'Phòng 302', 4600000, 24, 'AVAILABLE', 'Phòng tiêu chuẩn, giá tốt'),
(1, 'Phòng 303', 4900000, 27, 'REPAIRING', 'Đang sửa chữa, dự kiến xong trong 1 tuần'),
(1, 'Phòng 304', 4400000, 22, 'AVAILABLE', 'Phòng nhỏ gọn, phù hợp 1-2 người'),
(2, 'Phòng A01', 5500000, 35, 'AVAILABLE', 'View biển đẹp, có ban công rộng'),
(2, 'Phòng A02', 5200000, 32, 'AVAILABLE', 'Phòng góc, 2 cửa sổ, thoáng mát'),
(2, 'Phòng A03', 4800000, 28, 'OCCUPIED', 'Gần thang máy, tiện di chuyển'),
(2, 'Phòng A04', 5000000, 30, 'AVAILABLE', 'Có bếp riêng, máy giặt riêng'),
(2, 'Phòng B01', 6000000, 40, 'AVAILABLE', 'Phòng gia đình, rộng rãi, view biển'),
(2, 'Phòng B02', 5500000, 35, 'OCCUPIED', 'Căn hộ mini 1 phòng ngủ'),
(2, 'Phòng B03', 5800000, 38, 'AVAILABLE', 'Có gác lửng, phù hợp 3-4 người'),
(2, 'Phòng C01', 4500000, 25, 'AVAILABLE', 'Phòng tiêu chuẩn, giá tốt'),
(2, 'Phòng C02', 4700000, 27, 'AVAILABLE', 'Gần chợ, thuận tiện mua sắm'),
(2, 'Phòng C03', 4600000, 26, 'OCCUPIED', 'Yên tĩnh, an ninh 24/7'),
(3, 'Phòng 1', 1500000, 20, 'AVAILABLE', 'Phòng sinh viên giá rẻ, có wifi miễn phí'),
(3, 'Phòng 2', 1600000, 21, 'OCCUPIED', 'Gần trường học, an ninh tốt'),
(3, 'Phòng 3', 1800000, 22, 'AVAILABLE', 'Có máy lạnh, giá tốt'),
(3, 'Phòng 4', 2000000, 24, 'AVAILABLE', 'Phòng rộng hơn, có ban công nhỏ'),
(3, 'Phòng 5', 1700000, 21, 'OCCUPIED', 'Tầng 1, thuận tiện ra vào'),
(3, 'Phòng 6', 1900000, 23, 'AVAILABLE', 'Có WC riêng, nóng lạnh'),
(3, 'Phòng 7', 2200000, 26, 'AVAILABLE', 'Phòng góc, 2 cửa sổ'),
(3, 'Phòng 8', 1500000, 20, 'AVAILABLE', 'Giá rẻ nhất, phù hợp sinh viên'),
(4, 'Phòng A1', 2800000, 28, 'AVAILABLE', 'Khu yên tĩnh, an ninh tốt'),
(4, 'Phòng A2', 3000000, 30, 'AVAILABLE', 'Có bếp riêng, máy lạnh'),
(4, 'Phòng A3', 2700000, 27, 'OCCUPIED', 'Phòng tiêu chuẩn, đầy đủ nội thất'),
(4, 'Phòng A4', 3200000, 32, 'AVAILABLE', 'Phòng rộng, có ban công'),
(4, 'Phòng B1', 2500000, 25, 'AVAILABLE', 'Giá tốt, gần chợ'),
(4, 'Phòng B2', 2900000, 29, 'OCCUPIED', 'View đẹp, thoáng mát'),
(4, 'Phòng B3', 2600000, 26, 'AVAILABLE', 'An ninh 24/7, có camera'),
(5, 'P101', 2000000, 20, 'AVAILABLE', 'Gần khu công nghiệp, giá công nhân'),
(5, 'P102', 2100000, 21, 'OCCUPIED', 'Có máy lạnh, wifi miễn phí'),
(5, 'P103', 2200000, 22, 'AVAILABLE', 'Phòng sạch sẽ, an ninh tốt'),
(5, 'P104', 2300000, 23, 'AVAILABLE', 'Có bếp riêng, nấu ăn được'),
(5, 'P201', 2400000, 24, 'OCCUPIED', 'Tầng 2, yên tĩnh, thoáng mát'),
(5, 'P202', 2500000, 25, 'AVAILABLE', 'Phòng rộng, đầy đủ nội thất'),
(6, 'Căn 01', 7800000, 45, 'AVAILABLE', 'Căn hộ mini cao cấp, đầy đủ nội thất'),
(6, 'Căn 02', 7500000, 42, 'OCCUPIED', 'Phòng gia đình, có bếp, máy giặt'),
(6, 'Căn 03', 8000000, 48, 'AVAILABLE', 'View sông, ban công rộng'),
(6, 'Căn 04', 7200000, 40, 'AVAILABLE', 'Phòng tiêu chuẩn, giá tốt'),
(6, 'Căn 05', 8500000, 50, 'AVAILABLE', 'Căn góc, 2 phòng ngủ'),
(7, 'PMH-A1', 9000000, 50, 'AVAILABLE', 'Khu compound an ninh, có hồ bơi'),
(7, 'PMH-A2', 8500000, 48, 'OCCUPIED', 'Căn hộ dịch vụ, full nội thất'),
(7, 'PMH-A3', 9200000, 52, 'AVAILABLE', 'View công viên, yên tĩnh'),
(7, 'PMH-B1', 8800000, 49, 'AVAILABLE', 'Có 2 phòng ngủ, phù hợp gia đình'),
(7, 'PMH-B2', 9500000, 55, 'AVAILABLE', 'Căn góc, ban công rộng'),
(8, 'BT-101', 6500000, 38, 'AVAILABLE', 'Trung tâm Quận 1, tiện đi lại'),
(8, 'BT-102', 6200000, 35, 'OCCUPIED', 'Có bếp riêng, máy giặt'),
(8, 'BT-201', 7000000, 42, 'AVAILABLE', 'Tầng 2, yên tĩnh, view đẹp'),
(8, 'BT-202', 6700000, 39, 'AVAILABLE', 'Gần chợ Bến Thành, thuận tiện'),
(9, 'TH-101', 2800000, 28, 'AVAILABLE', 'Gần biển, thoáng mát, view đẹp'),
(9, 'TH-102', 3000000, 30, 'OCCUPIED', 'Phòng rộng, có ban công'),
(9, 'TH-201', 3200000, 32, 'AVAILABLE', 'Tầng cao, yên tĩnh'),
(10, 'PY-101', 3500000, 35, 'AVAILABLE', 'Trung tâm thành phố, tiện ích đầy đủ'),
(10, 'PY-102', 3300000, 33, 'AVAILABLE', 'Phòng tiêu chuẩn, giá tốt'),
(10, 'PY-201', 3800000, 38, 'OCCUPIED', 'Phòng VIP, view đẹp nhất');

-- ROOM_IMAGES (Mỗi phòng có 4 ảnh: 1 chính + 3 phụ, lặp detail1-7)
INSERT INTO room_images (room_id, image_url) VALUES
-- Building 1 rooms (ID 1-10) - 10 phòng
(1, '1/detail1.png'),(1, '1/detail2.png'),(1, '1/detail3.png'),(1, '1/detail4.png'),
(2, '1/detail2.png'),(2, '1/detail3.png'),(2, '1/detail4.png'),(2, '1/detail5.png'),
(3, '1/detail3.png'),(3, '1/detail4.png'),(3, '1/detail5.png'),(3, '1/detail6.png'),
(4, '1/detail4.png'),(4, '1/detail5.png'),(4, '1/detail6.png'),(4, '1/detail7.png'),
(5, '1/detail5.png'),(5, '1/detail6.png'),(5, '1/detail7.png'),(5, '1/detail1.png'),
(6, '1/detail6.png'),(6, '1/detail7.png'),(6, '1/detail1.png'),(6, '1/detail2.png'),
(7, '1/detail7.png'),(7, '1/detail1.png'),(7, '1/detail2.png'),(7, '1/detail3.png'),
(8, '1/detail1.png'),(8, '1/detail2.png'),(8, '1/detail3.png'),(8, '1/detail4.png'),
(9, '1/detail2.png'),(9, '1/detail3.png'),(9, '1/detail4.png'),(9, '1/detail5.png'),
(10, '1/detail3.png'),(10, '1/detail4.png'),(10, '1/detail5.png'),(10, '1/detail6.png'),
-- Building 2 rooms (ID 11-20) - 10 phòng
(11, '2/detail1.png'),(11, '2/detail2.png'),(11, '2/detail3.png'),(11, '2/detail4.png'),
(12, '2/detail2.png'),(12, '2/detail3.png'),(12, '2/detail4.png'),(12, '2/detail5.png'),
(13, '2/detail3.png'),(13, '2/detail4.png'),(13, '2/detail5.png'),(13, '2/detail6.png'),
(14, '2/detail4.png'),(14, '2/detail5.png'),(14, '2/detail6.png'),(14, '2/detail7.png'),
(15, '2/detail5.png'),(15, '2/detail6.png'),(15, '2/detail7.png'),(15, '2/detail1.png'),
(16, '2/detail6.png'),(16, '2/detail7.png'),(16, '2/detail1.png'),(16, '2/detail2.png'),
(17, '2/detail7.png'),(17, '2/detail1.png'),(17, '2/detail2.png'),(17, '2/detail3.png'),
(18, '2/detail1.png'),(18, '2/detail2.png'),(18, '2/detail3.png'),(18, '2/detail4.png'),
(19, '2/detail2.png'),(19, '2/detail3.png'),(19, '2/detail4.png'),(19, '2/detail5.png'),
(20, '2/detail3.png'),(20, '2/detail4.png'),(20, '2/detail5.png'),(20, '2/detail6.png'),
-- Building 3 rooms (ID 21-28) - 8 phòng
(21, '3/detail1.png'),(21, '3/detail2.png'),(21, '3/detail3.png'),(21, '3/detail4.png'),
(22, '3/detail2.png'),(22, '3/detail3.png'),(22, '3/detail4.png'),(22, '3/detail5.png'),
(23, '3/detail3.png'),(23, '3/detail4.png'),(23, '3/detail5.png'),(23, '3/detail6.png'),
(24, '3/detail4.png'),(24, '3/detail5.png'),(24, '3/detail6.png'),(24, '3/detail7.png'),
(25, '3/detail5.png'),(25, '3/detail6.png'),(25, '3/detail7.png'),(25, '3/detail1.png'),
(26, '3/detail6.png'),(26, '3/detail7.png'),(26, '3/detail1.png'),(26, '3/detail2.png'),
(27, '3/detail7.png'),(27, '3/detail1.png'),(27, '3/detail2.png'),(27, '3/detail3.png'),
(28, '3/detail1.png'),(28, '3/detail2.png'),(28, '3/detail3.png'),(28, '3/detail4.png'),
-- Building 4 rooms (ID 29-35) - 7 phòng
(29, '4/detail1.png'),(29, '4/detail2.png'),(29, '4/detail3.png'),(29, '4/detail4.png'),
(30, '4/detail2.png'),(30, '4/detail3.png'),(30, '4/detail4.png'),(30, '4/detail5.png'),
(31, '4/detail3.png'),(31, '4/detail4.png'),(31, '4/detail5.png'),(31, '4/detail6.png'),
(32, '4/detail4.png'),(32, '4/detail5.png'),(32, '4/detail6.png'),(32, '4/detail7.png'),
(33, '4/detail5.png'),(33, '4/detail6.png'),(33, '4/detail7.png'),(33, '4/detail1.png'),
(34, '4/detail6.png'),(34, '4/detail7.png'),(34, '4/detail1.png'),(34, '4/detail2.png'),
(35, '4/detail7.png'),(35, '4/detail1.png'),(35, '4/detail2.png'),(35, '4/detail3.png'),
-- Building 5 rooms (ID 36-41) - 6 phòng
(36, '5/detail1.png'),(36, '5/detail2.png'),(36, '5/detail3.png'),(36, '5/detail4.png'),
(37, '5/detail2.png'),(37, '5/detail3.png'),(37, '5/detail4.png'),(37, '5/detail5.png'),
(38, '5/detail3.png'),(38, '5/detail4.png'),(38, '5/detail5.png'),(38, '5/detail6.png'),
(39, '5/detail4.png'),(39, '5/detail5.png'),(39, '5/detail6.png'),(39, '5/detail7.png'),
(40, '5/detail5.png'),(40, '5/detail6.png'),(40, '5/detail7.png'),(40, '5/detail1.png'),
(41, '5/detail6.png'),(41, '5/detail7.png'),(41, '5/detail1.png'),(41, '5/detail2.png'),
-- Building 6 rooms (ID 42-46) - 5 phòng
(42, '6/detail1.png'),(42, '6/detail2.png'),(42, '6/detail3.png'),(42, '6/detail4.png'),
(43, '6/detail2.png'),(43, '6/detail3.png'),(43, '6/detail4.png'),(43, '6/detail5.png'),
(44, '6/detail3.png'),(44, '6/detail4.png'),(44, '6/detail5.png'),(44, '6/detail6.png'),
(45, '6/detail4.png'),(45, '6/detail5.png'),(45, '6/detail6.png'),(45, '6/detail7.png'),
(46, '6/detail5.png'),(46, '6/detail6.png'),(46, '6/detail7.png'),(46, '6/detail1.png'),
-- Building 7 rooms (ID 47-51) - 5 phòng
(47, '7/detail1.png'),(47, '7/detail2.png'),(47, '7/detail3.png'),(47, '7/detail4.png'),
(48, '7/detail2.png'),(48, '7/detail3.png'),(48, '7/detail4.png'),(48, '7/detail5.png'),
(49, '7/detail3.png'),(49, '7/detail4.png'),(49, '7/detail5.png'),(49, '7/detail6.png'),
(50, '7/detail4.png'),(50, '7/detail5.png'),(50, '7/detail6.png'),(50, '7/detail7.png'),
(51, '7/detail5.png'),(51, '7/detail6.png'),(51, '7/detail7.png'),(51, '7/detail1.png'),
-- Building 8 rooms (ID 52-55) - 4 phòng
(52, '8/detail1.png'),(52, '8/detail2.png'),(52, '8/detail3.png'),(52, '8/detail4.png'),
(53, '8/detail2.png'),(53, '8/detail3.png'),(53, '8/detail4.png'),(53, '8/detail5.png'),
(54, '8/detail3.png'),(54, '8/detail4.png'),(54, '8/detail5.png'),(54, '8/detail6.png'),
(55, '8/detail4.png'),(55, '8/detail5.png'),(55, '8/detail6.png'),(55, '8/detail7.png'),
-- Building 9 rooms (ID 56-58) - 3 phòng
(56, '9/detail1.png'),(56, '9/detail2.png'),(56, '9/detail3.png'),(56, '9/detail4.png'),
(57, '9/detail2.png'),(57, '9/detail3.png'),(57, '9/detail4.png'),(57, '9/detail5.png'),
(58, '9/detail3.png'),(58, '9/detail4.png'),(58, '9/detail5.png'),(58, '9/detail6.png'),
-- Building 10 rooms (ID 59-61) - 3 phòng
(59, '10/detail1.png'),(59, '10/detail2.png'),(59, '10/detail3.png'),(59, '10/detail4.png'),
(60, '10/detail2.png'),(60, '10/detail3.png'),(60, '10/detail4.png'),(60, '10/detail5.png'),
(61, '10/detail3.png'),(61, '10/detail4.png'),(61, '10/detail5.png'),(61, '10/detail6.png');

-- AMENITIES
INSERT INTO amenities (name, icon, description) VALUES
('Wifi miễn phí', 'fa-wifi', 'Truy cập Internet tốc độ cao miễn phí'),
('Máy lạnh', 'fa-snowflake', 'Điều hòa không khí trong phòng'),
('Chỗ đậu xe', 'fa-car', 'Khu vực đỗ xe riêng cho cư dân'),
('Bếp nấu ăn', 'fa-utensils', 'Có bếp và bồn rửa riêng'),
('Máy giặt', 'fa-soap', 'Có sẵn máy giặt trong phòng hoặc khu vực chung'),
('Hồ bơi', 'fa-water-ladder', 'Hồ bơi ngoài trời cho cư dân'),
('Thang máy', 'fa-elevator', 'Hỗ trợ di chuyển giữa các tầng dễ dàng');

-- ROOM_AMENITIES
INSERT INTO room_amenities (room_id, amenity_id) VALUES
(1,1),(1,2),(1,3),(1,4),
(2,1),(2,2),(2,3),
(3,1),(3,2),(3,4),(3,5),
(4,1),(4,2),(4,3),(4,4),(4,5),
(5,1),(5,2),(5,3),
(6,1),(6,2),(6,4),
(7,1),(7,2),(7,3),(7,4),(7,5),
(8,1),(8,2),(8,3),
(9,1),(9,2),
(10,1),(10,2),(10,4),
(11,1),(11,2),(11,3),(11,4),
(12,1),(12,2),(12,4),(12,5),
(13,1),(13,2),(13,3),
(14,1),(14,2),(14,4),(14,5),
(15,1),(15,2),(15,3),(15,4),(15,5),
(16,1),(16,2),(16,4),(16,5),
(17,1),(17,2),(17,3),(17,5),
(18,1),(18,2),(18,3),
(19,1),(19,2),(19,4),
(20,1),(20,2),(20,3),
(21,1),(21,4),
(22,1),(22,4),
(23,1),(23,2),(23,4),
(24,1),(24,2),(24,4),
(25,1),(25,4),
(26,1),(26,2),(26,4),
(27,1),(27,2),(27,4),
(28,1),(28,4),
(29,1),(29,2),(29,4),
(30,1),(30,2),(30,3),(30,4),
(31,1),(31,2),(31,4),
(32,1),(32,2),(32,3),(32,4),
(33,1),(33,2),(33,4),
(34,1),(34,2),(34,4),
(35,1),(35,2),(35,4),
(36,1),(36,2),(36,4),
(37,1),(37,2),(37,4),
(38,1),(38,2),(38,4),
(39,1),(39,2),(39,4),
(40,1),(40,2),(40,3),(40,4),
(41,1),(41,2),(41,3),(41,4),
(42,1),(42,2),(42,4),
(43,1),(43,2),(43,3),(43,4),(43,5),(43,7),
(44,1),(44,2),(44,3),(44,4),(44,5),(44,7),
(45,1),(45,2),(45,3),(45,4),(45,5),(45,7),
(46,1),(46,2),(46,3),(46,4),(46,5),(46,7),
(47,1),(47,2),(47,3),(47,4),(47,5),(47,7),
(48,1),(48,2),(48,3),(48,4),(48,5),(48,6),(48,7),
(49,1),(49,2),(49,3),(49,4),(49,5),(49,6),(49,7),
(50,1),(50,2),(50,3),(50,4),(50,5),(50,6),(50,7),
(51,1),(51,2),(51,3),(51,4),(51,5),(51,6),(51,7),
(52,1),(52,2),(52,3),(52,4),(52,5),(52,6),(52,7),
(53,1),(53,2),(53,3),(53,4),
(54,1),(54,2),(54,3),(54,4),
(55,1),(55,2),(55,3),(55,4),
(56,1),(56,2),(56,3),(56,4),
(57,1),(57,2),(57,4),
(58,1),(58,2),(58,4),
(59,1),(59,2),(59,4),
(60,1),(60,2),(60,4),
(61,1),(61,2),(61,4),
(62,1),(62,2),(62,4);

-- TENANTS
INSERT INTO tenants (user_id, cccd, date_of_birth, province_code, district_code, address) VALUES
(5, '079123456789', '2000-04-12', 1, 2, 'An Nhơn, Bình Định'),
(6, '079987654321', '1999-10-22', 2, 5, 'Quận 7, TP.HCM'),
(7, '080123456789', '2001-05-15', 3, 8, 'Tuy Hòa, Phú Yên');

-- CONTRACTS
INSERT INTO contracts (room_id, tenant_id, full_name, cccd, phone, address, start_date, end_date, deposit, notes, status) VALUES
(2, 1, 'Phạm Thị Dung', '079123456789', '0909222001', 'An Nhơn, Bình Định', '2025-09-01', '2025-12-01', 8400000, 'Hợp đồng cũ', 'EXPIRED'),
(13, 2, 'Hoàng Văn Em', '079987654321', '0909222002', 'Quận 7, TP.HCM', '2025-10-01', '2026-01-01', 9600000, '', 'ACTIVE'),
(58, 3, 'Võ Thị Phượng', '080123456789', '0909222003', 'Tuy Hòa, Phú Yên', '2025-08-15', '2025-11-15', 6000000, '', 'EXPIRED');

-- REVIEWS
INSERT INTO reviews (room_id, tenant_id, rating, comment) VALUES
(2, 5, 5, 'Phòng rất sạch sẽ, view đẹp, wifi nhanh. Chủ trọ thân thiện, hỗ trợ tốt. Giá hợp lý, rất hài lòng!'),
(13, 6, 4, 'Phòng nhỏ nhưng gọn gàng, tiện nghi cơ bản đầy đủ. Khu vực yên tĩnh, tốt cho học tập. Chỉ tiếc wifi hơi yếu lúc cao điểm.'),
(58, 7, 5, 'Phòng rộng rãi, bếp nấu ăn đầy đủ, máy giặt tiện lợi. An ninh tốt, khu vực sạch sẽ. Rất thích ở đây!');

SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;

-- ============================================================
-- SUMMARY
-- ============================================================
SELECT '=== DATABASE SUMMARY ===' AS '';
SELECT COUNT(*) as total_users FROM users;
SELECT COUNT(*) as total_buildings FROM buildings;
SELECT COUNT(*) as total_rooms FROM rooms;
SELECT COUNT(*) as total_amenities FROM amenities;
SELECT COUNT(*) as total_contracts FROM contracts;
SELECT COUNT(*) as total_reviews FROM reviews;

SELECT '=== 10 BUILDINGS ===' AS '';
SELECT id, name, address FROM buildings;

SELECT '=== ROOMS BY BUILDING ===' AS '';
SELECT building_id, COUNT(*) as room_count FROM rooms GROUP BY building_id ORDER BY building_id;
