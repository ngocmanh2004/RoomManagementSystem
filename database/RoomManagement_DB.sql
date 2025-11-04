-- ============================================================
-- Reset database
-- ============================================================
DROP DATABASE IF EXISTS roommanagement_db;
CREATE DATABASE roommanagement_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE roommanagement_db;

-- ============================================================
-- 1. USERS
-- ============================================================
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
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
-- 2. LANDLORDS
-- ============================================================
CREATE TABLE landlords (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  business_license VARCHAR(255),
  address VARCHAR(255),
  approved ENUM('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
  rejection_reason TEXT,
  utility_mode ENUM('LANDLORD_INPUT','TENANT_SUBMIT') DEFAULT 'LANDLORD_INPUT',
  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============================================================
-- 3. TENANTS
-- ============================================================
CREATE TABLE tenants (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  cccd VARCHAR(20) UNIQUE,
  date_of_birth DATE,
  address VARCHAR(255),
  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============================================================
-- 4. BUILDINGS
-- ============================================================
CREATE TABLE buildings (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  landlord_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  address VARCHAR(255) NOT NULL,
  description TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (landlord_id) REFERENCES landlords(id)
);

-- ============================================================
-- 5. ROOMS
-- ============================================================
CREATE TABLE rooms (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  building_id BIGINT NOT NULL,
  name VARCHAR(120) NOT NULL,
  price DECIMAL(12,2) NOT NULL,
  area DECIMAL(5,2),
  status ENUM('AVAILABLE','OCCUPIED','REPAIRING') DEFAULT 'AVAILABLE',
  description TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (building_id) REFERENCES buildings(id)
);

-- ============================================================
-- 6. ROOM_IMAGES
-- ============================================================
CREATE TABLE room_images (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  room_id BIGINT NOT NULL,
  image_url VARCHAR(255) NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- ============================================================
-- 7. AMENITIES
-- ============================================================
CREATE TABLE amenities (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  icon VARCHAR(255),
  description VARCHAR(255)
);

-- ============================================================
-- 8. ROOM_AMENITIES
-- ============================================================
CREATE TABLE room_amenities (
  room_id BIGINT NOT NULL,
  amenity_id BIGINT NOT NULL,
  PRIMARY KEY (room_id, amenity_id),
  FOREIGN KEY (room_id) REFERENCES rooms(id),
  FOREIGN KEY (amenity_id) REFERENCES amenities(id)
);

-- ============================================================
-- 9. CONTRACTS
-- ============================================================
CREATE TABLE contracts (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  room_id BIGINT NOT NULL,
  tenant_id BIGINT NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE,
  deposit DECIMAL(12,2) DEFAULT 0,
  status ENUM('PENDING','ACTIVE','EXPIRED','CANCELLED') DEFAULT 'PENDING',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (room_id) REFERENCES rooms(id),
  FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

-- ============================================================
-- 10. INVOICES
-- ============================================================
CREATE TABLE invoices (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  contract_id BIGINT NOT NULL,
  month VARCHAR(7) NOT NULL,
  total_amount DECIMAL(12,2) DEFAULT 0,
  status ENUM('UNPAID','PAID','OVERDUE','PENDING_CONFIRM') DEFAULT 'UNPAID',
  issue_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_invoice_month (contract_id, month),
  FOREIGN KEY (contract_id) REFERENCES contracts(id)
);

-- ============================================================
-- 11. INVOICE_ITEMS
-- ============================================================
CREATE TABLE invoice_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  invoice_id BIGINT NOT NULL,
  type ENUM('RENT','ELECTRICITY','WATER','SERVICE','OTHER'),
  description VARCHAR(255),
  amount DECIMAL(12,2) NOT NULL,
  FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);

-- ============================================================
-- 12. PAYMENTS
-- ============================================================
CREATE TABLE payments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  invoice_id BIGINT NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  method ENUM('BANK','MOMO','ZALO','CASH'),
  status ENUM('SUCCESS','PENDING','FAILED') DEFAULT 'PENDING',
  proof_url VARCHAR(255),
  paid_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);

-- ============================================================
-- 13. UTILITIES (ELECTRIC & WATER)
-- ============================================================
CREATE TABLE utilities_electric (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  room_id BIGINT NOT NULL,
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
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  room_id BIGINT NOT NULL,
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
-- 14. UTILITY_SUBMISSIONS
-- ============================================================
CREATE TABLE utility_submissions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id BIGINT NOT NULL,
  room_id BIGINT NOT NULL,
  type ENUM('ELECTRICITY','WATER') NOT NULL,
  new_index INT NOT NULL,
  photo_url VARCHAR(255),
  month VARCHAR(7) NOT NULL,
  status ENUM('PENDING','VERIFIED','REJECTED') DEFAULT 'PENDING',
  verified_by BIGINT,
  verified_at DATETIME,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (tenant_id) REFERENCES tenants(id),
  FOREIGN KEY (room_id) REFERENCES rooms(id),
  FOREIGN KEY (verified_by) REFERENCES users(id)
);

-- ============================================================
-- 15. EXTRA_COSTS
-- ============================================================
CREATE TABLE extra_costs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  room_id BIGINT,
  type ENUM('INTERNET','CLEANING','MAINTENANCE','OTHER'),
  description TEXT,
  amount DECIMAL(12,2),
  month VARCHAR(7),
  status ENUM('PAID','UNPAID') DEFAULT 'UNPAID',
  FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- ============================================================
-- 16. REVIEWS & REPORTS
-- ============================================================
CREATE TABLE reviews (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  room_id BIGINT NOT NULL,
  tenant_id BIGINT NOT NULL,
  rating INT CHECK (rating BETWEEN 1 AND 5),
  comment TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (room_id) REFERENCES rooms(id),
  FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

CREATE TABLE review_reports (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  review_id BIGINT NOT NULL,
  reporter_id BIGINT NOT NULL,
  reason ENUM('SPAM','OFFENSIVE','FALSE','OTHER'),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (review_id) REFERENCES reviews(id),
  FOREIGN KEY (reporter_id) REFERENCES users(id)
);

-- ============================================================
-- 17. NOTIFICATIONS
-- ============================================================
CREATE TABLE notifications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
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
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  sender_id BIGINT NOT NULL,
  receiver_id BIGINT NOT NULL,
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
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
