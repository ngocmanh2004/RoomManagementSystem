DROP DATABASE IF EXISTS roommanagement_db;
CREATE DATABASE roommanagement_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE roommanagement_db;

-- ============================================================
-- 1. USERS - Thông tin tài khoản người dùng
-- ============================================================
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL COMMENT 'Tên đăng nhập duy nhất của người dùng',
  password VARCHAR(255) NOT NULL COMMENT 'Mật khẩu đã được mã hóa (hash)',
  full_name VARCHAR(100) NOT NULL COMMENT 'Họ tên đầy đủ của người dùng',
  email VARCHAR(100) UNIQUE COMMENT 'Địa chỉ email liên hệ',
  phone VARCHAR(20) COMMENT 'Số điện thoại',
  role TINYINT NOT NULL DEFAULT 2 COMMENT '0=Admin, 1=Chủ trọ, 2=Khách thuê',
  status ENUM('ACTIVE','BANNED','PENDING') DEFAULT 'ACTIVE' COMMENT 'Trạng thái tài khoản',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo tài khoản'
);

-- ============================================================
-- 2. LANDLORDS - Thông tin chi tiết của chủ trọ
-- ============================================================
CREATE TABLE landlords (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL COMMENT 'Liên kết đến users.id (role=1)',
  business_license VARCHAR(255) COMMENT 'Giấy phép kinh doanh (nếu có)',
  address VARCHAR(255) COMMENT 'Địa chỉ liên hệ của chủ trọ',
  approved ENUM('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING' COMMENT 'Trạng thái phê duyệt hồ sơ',
  rejection_reason TEXT COMMENT 'Lý do bị từ chối (nếu có)',
  utility_mode ENUM('LANDLORD_INPUT','TENANT_SUBMIT') DEFAULT 'LANDLORD_INPUT' COMMENT 'Phương thức ghi điện/nước: LANDLORD_INPUT=Chủ trọ nhập, TENANT_SUBMIT=Khách thuê gửi ảnh',
  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============================================================
-- 3. TENANTS - Thông tin chi tiết của khách thuê
-- ============================================================
CREATE TABLE tenants (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL COMMENT 'Liên kết đến users.id (role=2)',
  cccd VARCHAR(20) UNIQUE COMMENT 'Số CCCD/CMND',
  date_of_birth DATE COMMENT 'Ngày sinh',
  address VARCHAR(255) COMMENT 'Địa chỉ thường trú',
  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============================================================
-- 4. BUILDINGS - Dãy trọ / Tòa nhà
-- ============================================================
CREATE TABLE buildings (
  id INT AUTO_INCREMENT PRIMARY KEY,
  landlord_id INT NOT NULL COMMENT 'Chủ trọ sở hữu dãy trọ',
  name VARCHAR(100) NOT NULL COMMENT 'Tên dãy trọ / tòa nhà',
  address VARCHAR(255) NOT NULL COMMENT 'Địa chỉ cụ thể',
  description TEXT COMMENT 'Mô tả chi tiết (số tầng, khu vực,...)',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (landlord_id) REFERENCES landlords(id)
);

-- ============================================================
-- 5. ROOMS - Phòng trọ
-- ============================================================
CREATE TABLE rooms (
  id INT AUTO_INCREMENT PRIMARY KEY,
  building_id INT NOT NULL COMMENT 'Khóa ngoại liên kết đến tòa nhà',
  name VARCHAR(120) NOT NULL COMMENT 'Tên hoặc mã phòng (VD: P101, A2-03)',
  price DECIMAL(12,2) NOT NULL COMMENT 'Giá thuê hàng tháng (VNĐ)',
  area DECIMAL(5,2) COMMENT 'Diện tích (m2)',
  status ENUM('AVAILABLE','OCCUPIED','REPAIRING') DEFAULT 'AVAILABLE' COMMENT 'Trạng thái phòng',
  description TEXT COMMENT 'Mô tả chi tiết phòng trọ',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (building_id) REFERENCES buildings(id)
);

-- ============================================================
-- 6. ROOM_IMAGES - Ảnh phòng trọ
-- ============================================================
CREATE TABLE room_images (
  id INT AUTO_INCREMENT PRIMARY KEY,
  room_id INT NOT NULL COMMENT 'Phòng trọ liên kết',
  image_url VARCHAR(255) NOT NULL COMMENT 'Đường dẫn ảnh',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- ============================================================
-- 7. AMENITIES - Tiện nghi
-- ============================================================
CREATE TABLE amenities (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL COMMENT 'Tên tiện nghi (VD: Máy lạnh, Gác lửng)',
  icon VARCHAR(255) COMMENT 'Icon hoặc URL icon',
  description VARCHAR(255) COMMENT 'Mô tả chi tiết tiện nghi'
);

-- ============================================================
-- 8. ROOM_AMENITIES - Liên kết N-N giữa phòng và tiện nghi
-- ============================================================
CREATE TABLE room_amenities (
  room_id INT NOT NULL,
  amenity_id INT NOT NULL,
  PRIMARY KEY (room_id, amenity_id),
  FOREIGN KEY (room_id) REFERENCES rooms(id),
  FOREIGN KEY (amenity_id) REFERENCES amenities(id)
);

-- ============================================================
-- 9. CONTRACTS - Hợp đồng thuê phòng
-- ============================================================
CREATE TABLE contracts (
  id INT AUTO_INCREMENT PRIMARY KEY,
  room_id INT NOT NULL COMMENT 'Phòng thuê',
  tenant_id INT NOT NULL COMMENT 'Người thuê',
  start_date DATE NOT NULL COMMENT 'Ngày bắt đầu hợp đồng',
  end_date DATE COMMENT 'Ngày kết thúc hợp đồng',
  deposit DECIMAL(12,2) DEFAULT 0 COMMENT 'Tiền đặt cọc',
  status ENUM('PENDING','ACTIVE','EXPIRED','CANCELLED') DEFAULT 'PENDING',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (room_id) REFERENCES rooms(id),
  FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

-- ============================================================
-- 10. INVOICES - Hóa đơn
-- ============================================================
CREATE TABLE invoices (
  id INT AUTO_INCREMENT PRIMARY KEY,
  contract_id INT NOT NULL,
  month VARCHAR(7) NOT NULL COMMENT 'Tháng lập hóa đơn (YYYY-MM)',
  total_amount DECIMAL(12,2) DEFAULT 0,
  status ENUM('UNPAID','PAID','OVERDUE','PENDING_CONFIRM') DEFAULT 'UNPAID',
  issue_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_invoice_month (contract_id, month),
  FOREIGN KEY (contract_id) REFERENCES contracts(id)
);

-- ============================================================
-- 11. INVOICE_ITEMS - Chi tiết hóa đơn
-- ============================================================
CREATE TABLE invoice_items (
  id INT AUTO_INCREMENT PRIMARY KEY,
  invoice_id INT NOT NULL,
  type ENUM('RENT','ELECTRICITY','WATER','SERVICE','OTHER'),
  description VARCHAR(255),
  amount DECIMAL(12,2) NOT NULL,
  FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);

-- ============================================================
-- 12. PAYMENTS - Giao dịch thanh toán
-- ============================================================
CREATE TABLE payments (
  id INT AUTO_INCREMENT PRIMARY KEY,
  invoice_id INT NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  method ENUM('BANK','MOMO','ZALO','CASH'),
  status ENUM('SUCCESS','PENDING','FAILED') DEFAULT 'PENDING',
  proof_url VARCHAR(255) COMMENT 'Ảnh hoặc file biên lai',
  paid_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);

-- ============================================================
-- 13. UTILITIES - Ghi chỉ số điện nước (Chủ trọ hoặc khách thuê)
-- ============================================================
CREATE TABLE utilities_electric (
  id INT AUTO_INCREMENT PRIMARY KEY,
  room_id INT NOT NULL,
  old_index INT,
  new_index INT,
  unit_price DECIMAL(10,2),
  total_amount DECIMAL(12,2),
  meter_photo_url VARCHAR(255) COMMENT 'Ảnh công tơ điện',
  month VARCHAR(7),
  status ENUM('PAID','UNPAID') DEFAULT 'UNPAID',
  source ENUM('LANDLORD','TENANT','SYSTEM') DEFAULT 'LANDLORD' COMMENT 'Nguồn nhập dữ liệu',
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
  meter_photo_url VARCHAR(255) COMMENT 'Ảnh công tơ nước',
  month VARCHAR(7),
  status ENUM('PAID','UNPAID') DEFAULT 'UNPAID',
  source ENUM('LANDLORD','TENANT','SYSTEM') DEFAULT 'LANDLORD',
  UNIQUE KEY uq_water_room_month (room_id, month),
  FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- ============================================================
-- 14. UTILITY_SUBMISSIONS - Khách thuê gửi ảnh công tơ và chỉ số mới
-- ============================================================
CREATE TABLE utility_submissions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  tenant_id INT NOT NULL COMMENT 'Người thuê gửi thông tin công tơ',
  room_id INT NOT NULL COMMENT 'Phòng liên quan',
  type ENUM('ELECTRICITY','WATER') NOT NULL COMMENT 'Loại công tơ',
  new_index INT NOT NULL COMMENT 'Chỉ số mới khách nhập',
  photo_url VARCHAR(255) COMMENT 'Ảnh chụp công tơ',
  month VARCHAR(7) NOT NULL COMMENT 'Kỳ ghi nhận (YYYY-MM)',
  status ENUM('PENDING','VERIFIED','REJECTED') DEFAULT 'PENDING',
  verified_by INT COMMENT 'Chủ trọ xác nhận (user_id)',
  verified_at DATETIME,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (tenant_id) REFERENCES tenants(id),
  FOREIGN KEY (room_id) REFERENCES rooms(id),
  FOREIGN KEY (verified_by) REFERENCES users(id)
);

-- ============================================================
-- 15. EXTRA_COSTS - Chi phí phát sinh
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
-- 16. REVIEWS & REPORTS - Đánh giá và báo cáo sai phạm
-- ============================================================
CREATE TABLE reviews (
  id INT AUTO_INCREMENT PRIMARY KEY,
  room_id INT NOT NULL,
  tenant_id INT NOT NULL,
  rating INT CHECK (rating BETWEEN 1 AND 5),
  comment TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (room_id) REFERENCES rooms(id),
  FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

CREATE TABLE review_reports (
  id INT AUTO_INCREMENT PRIMARY KEY,
  review_id INT NOT NULL,
  reporter_id INT NOT NULL,
  reason ENUM('SPAM','OFFENSIVE','FALSE','OTHER'),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (review_id) REFERENCES reviews(id),
  FOREIGN KEY (reporter_id) REFERENCES users(id)
);

-- ============================================================
-- 17. NOTIFICATIONS - Thông báo hệ thống
-- ============================================================
CREATE TABLE notifications (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  title VARCHAR(100),
  message TEXT,
  type ENUM('SYSTEM','UTILITY_REQUEST','UTILITY_CONFIRMED','PAYMENT_RECEIVED','FEEDBACK') DEFAULT 'SYSTEM' COMMENT 'Loại thông báo',
  is_read TINYINT(1) DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============================================================
-- 18. FEEDBACKS - Phản hồi / Yêu cầu sửa chữa
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
CREATE TABLE refresh_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    expiry_date DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
