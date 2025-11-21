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
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 2. PROVINCES & DISTRICTS
-- ============================================================
CREATE TABLE provinces (
  code INT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  division_type VARCHAR(50)
);

CREATE TABLE districts (
  code INT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  province_code INT NOT NULL,
  division_type VARCHAR(50),
  FOREIGN KEY (province_code) REFERENCES provinces(code)
);

-- ============================================================
-- 3. LANDLORDS
-- ============================================================
CREATE TABLE landlords (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  business_license VARCHAR(255),
  province_code INT,
  district_code INT,
  address VARCHAR(255),
  approved ENUM('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
  rejection_reason TEXT,
  utility_mode ENUM('LANDLORD_INPUT','TENANT_SUBMIT') DEFAULT 'LANDLORD_INPUT',
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (province_code) REFERENCES provinces(code),
  FOREIGN KEY (district_code) REFERENCES districts(code)
);

-- ============================================================
-- 4. TENANTS
-- ============================================================
CREATE TABLE tenants (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  cccd VARCHAR(20) UNIQUE,
  date_of_birth DATE,
  province_code INT,
  district_code INT,
  address VARCHAR(255),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (province_code) REFERENCES provinces(code),
  FOREIGN KEY (district_code) REFERENCES districts(code)
);

-- ============================================================
-- 5. BUILDINGS
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
  FOREIGN KEY (landlord_id) REFERENCES landlords(id),
  FOREIGN KEY (province_code) REFERENCES provinces(code),
  FOREIGN KEY (district_code) REFERENCES districts(code)
);

-- ============================================================
-- 6. ROOMS
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
  FOREIGN KEY (building_id) REFERENCES buildings(id)
);

-- ============================================================
-- 7. ROOM_IMAGES
-- ============================================================
CREATE TABLE room_images (
  id INT AUTO_INCREMENT PRIMARY KEY,
  room_id INT NOT NULL,
  image_url VARCHAR(255) NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- ============================================================
-- 8. AMENITIES & ROOM_AMENITIES
-- ============================================================
CREATE TABLE amenities (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  icon VARCHAR(255),
  description VARCHAR(255)
);

CREATE TABLE room_amenities (
  room_id INT NOT NULL,
  amenity_id INT NOT NULL,
  PRIMARY KEY (room_id, amenity_id),
  FOREIGN KEY (room_id) REFERENCES rooms(id),
  FOREIGN KEY (amenity_id) REFERENCES amenities(id)
);

-- ============================================================
-- 9. CONTRACTS
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
  FOREIGN KEY (room_id) REFERENCES rooms(id),
  FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

-- ============================================================
-- 10. INVOICES & INVOICE_ITEMS
-- ============================================================
CREATE TABLE invoices (
  id INT AUTO_INCREMENT PRIMARY KEY,
  contract_id INT NOT NULL,
  month VARCHAR(7) NOT NULL,
  total_amount DECIMAL(12,2) DEFAULT 0,
  status ENUM('UNPAID','PAID','OVERDUED','PENDING_CONFIRM') DEFAULT 'UNPAID',
  issue_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_invoice_month (contract_id, month),
  FOREIGN KEY (contract_id) REFERENCES contracts(id)
);

CREATE TABLE invoice_items (
  id INT AUTO_INCREMENT PRIMARY KEY,
  invoice_id INT NOT NULL,
  type ENUM('RENT','ELECTRICITY','WATER','SERVICE','OTHER'),
  description VARCHAR(255),
  amount DECIMAL(12,2) NOT NULL,
  FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);

-- ============================================================
-- 11. PAYMENTS
-- ============================================================
CREATE TABLE payments (
  id INT AUTO_INCREMENT PRIMARY KEY,
  invoice_id INT NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  method ENUM('BANK','MOMO','ZALO','CASH'),
  status ENUM('SUCCESS','PENDING','FAILED') DEFAULT 'PENDING',
  proof_url VARCHAR(255),
  paid_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);

-- ============================================================
-- 12. UTILITIES (ELECTRIC & WATER)
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
  FOREIGN KEY (room_id) REFERENCES rooms(id)
);

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
  FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- ============================================================
-- 13. UTILITY_SUBMISSIONS
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
  FOREIGN KEY (tenant_id) REFERENCES tenants(id),
  FOREIGN KEY (room_id) REFERENCES rooms(id),
  FOREIGN KEY (verified_by) REFERENCES users(id)
);

-- ============================================================
-- 14. EXTRA_COSTS
-- ============================================================
CREATE TABLE extra_costs (
  id INT AUTO_INCREMENT PRIMARY KEY,
  room_id INT,
  type ENUM('INTERNET','CLEANING','MAINTENANCE','OTHER'),
  description TEXT,
  amount DECIMAL(12,2),
  month VARCHAR(7),
  status ENUM('PAID','UNPAID') DEFAULT 'UNPAID',
  FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- ============================================================
-- 15. REVIEWS (EPIC 11)
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
);

-- ============================================================
-- 16. REVIEW_REPORTS (EPIC 11.5)
-- ============================================================
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
);

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
  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============================================================
-- 18. FEEDBACKS
-- ============================================================
CREATE TABLE feedbacks (
  id INT AUTO_INCREMENT PRIMARY KEY,
  sender_id INT NOT NULL,
  receiver_id INT NOT NULL,
  title VARCHAR(100),
  content TEXT,
  attachment_url VARCHAR(255),
  status ENUM('SENT','IN_PROGRESS','RESOLVED') DEFAULT 'SENT',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (sender_id) REFERENCES users(id),
  FOREIGN KEY (receiver_id) REFERENCES users(id)
);

-- ============================================================
-- 19. REFRESH_TOKENS
-- ============================================================
CREATE TABLE refresh_tokens (
  id INT AUTO_INCREMENT PRIMARY KEY,
  token VARCHAR(500) NOT NULL UNIQUE,
  user_id INT NOT NULL,
  expiry_date DATETIME NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================
-- VERIFY TABLES
-- ============================================================
DESCRIBE reviews;
DESCRIBE review_reports;