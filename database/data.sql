USE roommanagement_db;

-- Tắt Safe Mode tạm thời
SET SQL_SAFE_UPDATES = 0;

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
