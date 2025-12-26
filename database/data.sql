USE roommanagement_db;

-- Tắt Safe Mode tạm thời
SET SQL_SAFE_UPDATES = 0;

ALTER TABLE rooms 
ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
AFTER created_at;

-- Thêm contract_code
ALTER TABLE contracts 
ADD COLUMN contract_code VARCHAR(20) UNIQUE AFTER id;

-- Thêm monthly_rent
ALTER TABLE contracts 
ADD COLUMN monthly_rent DECIMAL(12,2) AFTER deposit;

-- Sửa status ENUM
ALTER TABLE contracts 
MODIFY COLUMN status ENUM('PENDING','APPROVED','ACTIVE','EXPIRED','CANCELLED','REJECTED') 
DEFAULT 'PENDING';

-- Tạo contract_code
UPDATE contracts 
SET contract_code = CONCAT('HD', LPAD(id, 7, '0'))
WHERE contract_code IS NULL;

-- Cập nhật monthly_rent
UPDATE contracts c
INNER JOIN rooms r ON c.room_id = r.id
SET c.monthly_rent = r.price
WHERE c.monthly_rent IS NULL;

-- Bật lại Safe Mode
SET SQL_SAFE_UPDATES = 1;

-- Kiểm tra
DESCRIBE contracts;
SELECT * FROM contracts LIMIT 5;

-- I'm Mạnh
-- Thêm cột note (CHỈ THÊM NẾU CHƯA CÓ)
SET @col_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'review_reports'
      AND COLUMN_NAME = 'note'
      AND TABLE_SCHEMA = DATABASE()
);

SET @sql := IF(
    @col_exists = 0,
    'ALTER TABLE review_reports ADD COLUMN note VARCHAR(255);',
    'SELECT "note column already exists";'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
