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
