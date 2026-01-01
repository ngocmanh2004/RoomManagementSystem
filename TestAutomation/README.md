# Hướng dẫn sử dụng Git cho dự án **RoomManagementSystem** (Monorepo FE + BE)

> Cấu trúc repo: một repo duy nhất chứa 2 thư mục con:
```
RoomManagementSystem/
 ├─ frontend/   # Angular
 └─ backend/    # Spring Boot
```

---

## 1) Clone repo về máy
```bash
git clone https://github.com/ngocmanh2004/RoomManagementSystem.git
cd RoomManagementSystem
```

> FE làm việc trong thư mục `frontend/`, BE làm việc trong thư mục `backend/`.  
> **Git quản lý nhánh ở cấp repo**, không phải theo từng thư mục.

---

## 2) Các nhánh trong dự án
- **main** → code ổn định, dùng để nộp bài / demo / deploy.
- **develop** → nhánh tích hợp chung, dev merge vào đây mỗi ngày.
- **feature-\*** → nhánh cho dev làm tính năng mới (ví dụ: `feature-room-search`).
- **fix-\*** → nhánh cho dev sửa lỗi (ví dụ: `fix-contract-validation`).
- **hotfix-\*** → sửa gấp trên `main` (ví dụ: `hotfix-payment-bug`).
- **release-sprintX** → nhánh tổng hợp để tester kiểm thử toàn bộ Sprint X.

---

## 3) Quy tắc đặt tên nhánh
- Nhánh tính năng: `feature-ten-tinh-nang`
  - Ví dụ: `feature-room-search`, `feature-invoice-api`, `feature-auth-ui`
- Nhánh sửa lỗi: `fix-ten-loi`
  - Ví dụ: `fix-login-bug`, `fix-contract-validation`
- Nhánh hotfix (trên production): `hotfix-ten-loi`
  - Ví dụ: `hotfix-payment-bug`
- Nhánh tester: `release-sprint1`, `release-sprint2`

---

## 4) Quy trình làm việc của **Dev**
### 4.1. Kéo code mới nhất & tạo nhánh làm việc
```bash
git checkout develop
git pull origin develop

# Ví dụ làm FE: trang Home
git checkout -b feature-home-page
```

### 4.2. Code và commit theo từng bước nhỏ
> Làm FE thì chỉ thay đổi trong `frontend/`; làm BE chỉ thay đổi trong `backend/`.
```bash
# Thêm tất cả thay đổi
git add .
git commit -m "feat: tạo layout trang Home"

# Hoặc chỉ add phần FE
git add frontend/
git commit -m "feat: component Home + route"
```

### 4.3. Push nhánh lên GitHub
```bash
git push -u origin feature-home-page
```

### 4.4. Tạo Pull Request (PR) merge vào `develop`
- PR phải được **review** bởi leader hoặc 1 dev khác.
- Mô tả PR rõ ràng: mục tiêu, thay đổi chính, ảnh minh hoạ UI (nếu có).
- Đảm bảo **build OK** (FE: `ng build`; BE: `mvn -q -DskipTests package`).

---

## 5) Quy trình cho **Tester**
1. Khi dev đã merge vào `develop`, leader tạo nhánh release để test:
    ```bash
    git checkout develop
    git pull origin develop
    git checkout -b release-sprint1
    git push origin release-sprint1
    ```
2. Tester dùng nhánh `release-sprint1` để kiểm thử toàn bộ Sprint 1:
    ```bash
    git checkout release-sprint1
    git pull origin release-sprint1
    ```
3. Nếu có bug → tạo ticket, dev sửa trên nhánh `feature-*` hoặc `fix-*` rồi merge lại `develop` → leader cập nhật `release-sprint1`.
4. Khi test OK → **merge `release-sprint1` → `main`** để chốt phiên bản.

> **Tester không test trực tiếp trên `main` hoặc `develop`.**

---

## 6) Commit message chuẩn (khuyến nghị Conventional Commits)
- `feat:` → thêm tính năng mới
- `fix:` → sửa lỗi
- `docs:` → cập nhật tài liệu
- `style:` → chỉnh giao diện/CSS không đổi logic
- `refactor:` → cải tiến code không đổi hành vi
- `test:` → thêm/sửa test

**Ví dụ**
```bash
git commit -m "feat: thêm API đăng nhập"
git commit -m "fix: validate số điện thoại khi đăng ký"
git commit -m "style: chỉnh màu nút Đăng nhập"
```

---

## 7) Lưu ý quan trọng
- **Luôn** cập nhật code mới nhất trước khi tạo nhánh:
  ```bash
  git checkout develop
  git pull origin develop
  ```
- **Không commit file rác / build:**
  ```gitignore
  frontend/node_modules/
  frontend/dist/
  backend/target/
  .idea/
  .vscode/
  *.iml
  *.log
  .DS_Store
  ```
- **Nhánh FE** (ví dụ `features/home`) chỉ là **nhánh của toàn repo**, không phải nhánh “riêng thư mục”. Dev FE chỉ cần làm trong `frontend/` là đủ.
- Sau khi **hotfix trên `main`**, nhớ **merge ngược** về `develop` để tránh lệch nhánh:
  ```bash
  git checkout develop
  git pull origin develop
  git merge main
  git push origin develop
  ```
- Trước khi tạo PR, hãy tự kiểm tra nhanh:
  - FE: `npm run lint && ng build`
  - BE: `mvn -q -DskipTests package`

---

## 8) Ví dụ nhanh cho **Dev FE** (nhánh `features/home`)
```bash
git checkout develop
git pull origin develop
git checkout -b features/home

# code trong thư mục frontend/
git add frontend/
git commit -m "feat: trang Home - banner, search form, room list"
git push -u origin features/home

# Lên GitHub tạo Pull Request: features/home -> develop
```

## 9) Ví dụ nhanh cho **Dev BE** (nhánh `feature-invoice-api`)
```bash
git checkout develop
git pull origin develop
git checkout -b feature-invoice-api

# code trong thư mục backend/
git add backend/
git commit -m "feat: thêm API tạo hóa đơn theo hợp đồng"
git push -u origin feature-invoice-api

# Tạo Pull Request: feature-invoice-api -> develop
```

---

## 10) Quy trình **Release** & **Hotfix**
- Release: `develop` → `release-sprintX` → test OK → merge `release-sprintX` → `main`
- Hotfix (lỗi gấp trên `main`):
  ```bash
  git checkout main
  git pull origin main
  git checkout -b hotfix-payment-bug
  # sửa lỗi...
  git commit -m "hotfix: xử lý lỗi thanh toán 500"
  git push -u origin hotfix-payment-bug
  # tạo PR -> main
  # sau khi merge xong:
  git checkout develop
  git pull origin develop
  git merge main
  git push origin develop
  ```

---

**Tips cho teamwork (6 người):**
- Mỗi người **1 nhánh tính năng** → review chéo → merge `develop` mỗi ngày.
- FE & BE có thể **release riêng từng phần** nhưng vẫn chung một repo, tránh xung đột.
- Viết **README chạy FE/BE** để thầy/cô clone là chạy được ngay.
