-- ============================================================
-- DỮ LIỆU 63 TỈNH THÀNH & QUẬN HUYỆN VIỆT NAM
-- Tỉnh quan trọng: đầy đủ quận huyện
-- Tỉnh khác: 1-2 quận huyện trung tâm
-- ============================================================

USE roommanagement_db;

-- Xóa dữ liệu cũ

-- ============================================================
-- INSERT 63 TỈNH THÀNH
-- ============================================================
INSERT INTO provinces (code, name, division_type) VALUES
(1, 'Hà Nội', 'Thành phố Trung ương'),
(2, 'Hà Giang', 'Tỉnh'),
(4, 'Cao Bằng', 'Tỉnh'),
(6, 'Bắc Kạn', 'Tỉnh'),
(8, 'Tuyên Quang', 'Tỉnh'),
(10, 'Lào Cai', 'Tỉnh'),
(11, 'Điện Biên', 'Tỉnh'),
(12, 'Lai Châu', 'Tỉnh'),
(14, 'Sơn La', 'Tỉnh'),
(15, 'Yên Bái', 'Tỉnh'),
(17, 'Hoà Bình', 'Tỉnh'),
(19, 'Thái Nguyên', 'Tỉnh'),
(20, 'Lạng Sơn', 'Tỉnh'),
(22, 'Quảng Ninh', 'Tỉnh'),
(24, 'Bắc Giang', 'Tỉnh'),
(25, 'Phú Thọ', 'Tỉnh'),
(26, 'Vĩnh Phúc', 'Tỉnh'),
(27, 'Bắc Ninh', 'Tỉnh'),
(30, 'Hải Dương', 'Tỉnh'),
(31, 'Hải Phòng', 'Thành phố Trung ương'),
(33, 'Hưng Yên', 'Tỉnh'),
(34, 'Thái Bình', 'Tỉnh'),
(35, 'Hà Nam', 'Tỉnh'),
(36, 'Nam Định', 'Tỉnh'),
(37, 'Ninh Bình', 'Tỉnh'),
(38, 'Thanh Hóa', 'Tỉnh'),
(40, 'Nghệ An', 'Tỉnh'),
(42, 'Hà Tĩnh', 'Tỉnh'),
(44, 'Quảng Bình', 'Tỉnh'),
(45, 'Quảng Trị', 'Tỉnh'),
(46, 'Thừa Thiên Huế', 'Tỉnh'),
(48, 'Đà Nẵng', 'Thành phố Trung ương'),
(49, 'Quảng Nam', 'Tỉnh'),
(51, 'Quảng Ngãi', 'Tỉnh'),
(52, 'Bình Định', 'Tỉnh'),
(54, 'Phú Yên', 'Tỉnh'),
(56, 'Khánh Hòa', 'Tỉnh'),
(58, 'Ninh Thuận', 'Tỉnh'),
(60, 'Bình Thuận', 'Tỉnh'),
(62, 'Kon Tum', 'Tỉnh'),
(64, 'Gia Lai', 'Tỉnh'),
(66, 'Đắk Lắk', 'Tỉnh'),
(67, 'Đắk Nông', 'Tỉnh'),
(68, 'Lâm Đồng', 'Tỉnh'),
(70, 'Bình Phước', 'Tỉnh'),
(72, 'Tây Ninh', 'Tỉnh'),
(74, 'Bình Dương', 'Tỉnh'),
(75, 'Đồng Nai', 'Tỉnh'),
(77, 'Bà Rịa - Vũng Tàu', 'Tỉnh'),
(79, 'Hồ Chí Minh', 'Thành phố Trung ương'),
(80, 'Long An', 'Tỉnh'),
(82, 'Tiền Giang', 'Tỉnh'),
(83, 'Bến Tre', 'Tỉnh'),
(84, 'Trà Vinh', 'Tỉnh'),
(86, 'Vĩnh Long', 'Tỉnh'),
(87, 'Đồng Tháp', 'Tỉnh'),
(89, 'An Giang', 'Tỉnh'),
(91, 'Kiên Giang', 'Tỉnh'),
(92, 'Cần Thơ', 'Thành phố Trung ương'),
(93, 'Hậu Giang', 'Tỉnh'),
(94, 'Sóc Trăng', 'Tỉnh'),
(95, 'Bạc Liêu', 'Tỉnh'),
(96, 'Cà Mau', 'Tỉnh');

-- ============================================================
-- INSERT QUẬN/HUYỆN
-- ============================================================

-- HÀ NỘI (30 quận/huyện - ĐẦY ĐỦ)
INSERT INTO districts (code, name, province_code, division_type) VALUES
(1, 'Ba Đình', 1, 'Quận'),
(2, 'Hoàn Kiếm', 1, 'Quận'),
(3, 'Tây Hồ', 1, 'Quận'),
(4, 'Long Biên', 1, 'Quận'),
(5, 'Cầu Giấy', 1, 'Quận'),
(6, 'Đống Đa', 1, 'Quận'),
(7, 'Hai Bà Trưng', 1, 'Quận'),
(8, 'Hoàng Mai', 1, 'Quận'),
(9, 'Thanh Xuân', 1, 'Quận'),
(16, 'Sóc Sơn', 1, 'Huyện'),
(17, 'Đông Anh', 1, 'Huyện'),
(18, 'Gia Lâm', 1, 'Huyện'),
(19, 'Nam Từ Liêm', 1, 'Quận'),
(20, 'Thanh Trì', 1, 'Huyện'),
(21, 'Bắc Từ Liêm', 1, 'Quận'),
(250, 'Mê Linh', 1, 'Huyện'),
(268, 'Hà Đông', 1, 'Quận'),
(269, 'Sơn Tây', 1, 'Thị xã'),
(271, 'Ba Vì', 1, 'Huyện'),
(272, 'Phúc Thọ', 1, 'Huyện'),
(273, 'Đan Phượng', 1, 'Huyện'),
(274, 'Hoài Đức', 1, 'Huyện'),
(275, 'Quốc Oai', 1, 'Huyện'),
(276, 'Thạch Thất', 1, 'Huyện'),
(277, 'Chương Mỹ', 1, 'Huyện'),
(278, 'Thanh Oai', 1, 'Huyện'),
(279, 'Thường Tín', 1, 'Huyện'),
(280, 'Phú Xuyên', 1, 'Huyện'),
(281, 'Ứng Hòa', 1, 'Huyện'),
(282, 'Mỹ Đức', 1, 'Huyện');

-- HỒ CHÍ MINH (22 quận/huyện - ĐẦY ĐỦ)
INSERT INTO districts (code, name, province_code, division_type) VALUES
(760, 'Quận 1', 79, 'Quận'),
(761, 'Quận 12', 79, 'Quận'),
(762, 'Gò Vấp', 79, 'Quận'),
(763, 'Bình Thạnh', 79, 'Quận'),
(764, 'Tân Bình', 79, 'Quận'),
(765, 'Tân Phú', 79, 'Quận'),
(766, 'Phú Nhuận', 79, 'Quận'),
(767, 'Thủ Đức', 79, 'Thành phố'),
(768, 'Quận 3', 79, 'Quận'),
(769, 'Quận 10', 79, 'Quận'),
(770, 'Quận 11', 79, 'Quận'),
(771, 'Quận 4', 79, 'Quận'),
(772, 'Quận 5', 79, 'Quận'),
(773, 'Quận 6', 79, 'Quận'),
(774, 'Quận 8', 79, 'Quận'),
(775, 'Bình Tân', 79, 'Quận'),
(776, 'Quận 7', 79, 'Quận'),
(777, 'Hóc Môn', 79, 'Huyện'),
(778, 'Củ Chi', 79, 'Huyện'),
(783, 'Bình Chánh', 79, 'Huyện'),
(784, 'Nhà Bè', 79, 'Huyện'),
(785, 'Cần Giờ', 79, 'Huyện');

-- ĐÀ NẴNG (8 quận/huyện - ĐẦY ĐỦ)
INSERT INTO districts (code, name, province_code, division_type) VALUES
(490, 'Liên Chiểu', 48, 'Quận'),
(491, 'Thanh Khê', 48, 'Quận'),
(492, 'Hải Châu', 48, 'Quận'),
(493, 'Sơn Trà', 48, 'Quận'),
(494, 'Ngũ Hành Sơn', 48, 'Quận'),
(495, 'Cẩm Lệ', 48, 'Quận'),
(497, 'Hòa Vang', 48, 'Huyện'),
(498, 'Hoàng Sa', 48, 'Huyện');

-- HẢI PHÒNG (15 quận/huyện - ĐẦY ĐỦ)
INSERT INTO districts (code, name, province_code, division_type) VALUES
(303, 'Hồng Bàng', 31, 'Quận'),
(304, 'Ngô Quyền', 31, 'Quận'),
(305, 'Lê Chân', 31, 'Quận'),
(306, 'Hải An', 31, 'Quận'),
(307, 'Kiến An', 31, 'Quận'),
(308, 'Đồ Sơn', 31, 'Quận'),
(309, 'Dương Kinh', 31, 'Quận'),
(311, 'Thuỷ Nguyên', 31, 'Huyện'),
(312, 'An Dương', 31, 'Huyện'),
(313, 'An Lão', 31, 'Huyện'),
(314, 'Kiến Thuỵ', 31, 'Huyện'),
(315, 'Tiên Lãng', 31, 'Huyện'),
(316, 'Vĩnh Bảo', 31, 'Huyện'),
(317, 'Cát Hải', 31, 'Huyện'),
(318, 'Bạch Long Vĩ', 31, 'Huyện');

-- CẦN THƠ (9 quận/huyện - ĐẦY ĐỦ)
INSERT INTO districts (code, name, province_code, division_type) VALUES
(916, 'Ninh Kiều', 92, 'Quận'),
(917, 'Ô Môn', 92, 'Quận'),
(918, 'Bình Thuỷ', 92, 'Quận'),
(919, 'Cái Răng', 92, 'Quận'),
(923, 'Thốt Nốt', 92, 'Quận'),
(924, 'Vĩnh Thạnh', 92, 'Huyện'),
(925, 'Cờ Đỏ', 92, 'Huyện'),
(926, 'Phong Điền', 92, 'Huyện'),
(927, 'Thới Lai', 92, 'Huyện');

-- BÌNH ĐỊNH (11 huyện - ĐẦY ĐỦ)
INSERT INTO districts (code, name, province_code, division_type) VALUES
(522, 'Quy Nhơn', 52, 'Thành phố'),
(524, 'An Lão', 52, 'Huyện'),
(525, 'KBang', 52, 'Huyện'),
(527, 'An Nhơn', 52, 'Thị xã'),
(528, 'Tuy Phước', 52, 'Huyện'),
(529, 'Tây Sơn', 52, 'Huyện'),
(530, 'Phù Cát', 52, 'Huyện'),
(531, 'Phù Mỹ', 52, 'Huyện'),
(532, 'Vĩnh Thạnh', 52, 'Huyện'),
(533, 'Hoài Nhơn', 52, 'Thị xã'),
(534, 'Hoài Ân', 52, 'Huyện');

-- PHÚ YÊN (9 huyện - ĐẦY ĐỦ)
INSERT INTO districts (code, name, province_code, division_type) VALUES
(540, 'Tuy Hòa', 54, 'Thành phố'),
(542, 'Sông Cầu', 54, 'Thị xã'),
(543, 'Đồng Xuân', 54, 'Huyện'),
(544, 'Tuy An', 54, 'Huyện'),
(545, 'Sơn Hòa', 54, 'Huyện'),
(546, 'Sông Hinh', 54, 'Huyện'),
(547, 'Tây Hoà', 54, 'Huyện'),
(548, 'Phú Hoà', 54, 'Huyện'),
(549, 'Đông Hòa', 54, 'Huyện');

-- KHÁNH HÒA (9 huyện - ĐẦY ĐỦ)
INSERT INTO districts (code, name, province_code, division_type) VALUES
(568, 'Nha Trang', 56, 'Thành phố'),
(569, 'Cam Ranh', 56, 'Thành phố'),
(570, 'Cam Lâm', 56, 'Huyện'),
(571, 'Vạn Ninh', 56, 'Huyện'),
(572, 'Ninh Hòa', 56, 'Thị xã'),
(573, 'Khánh Vĩnh', 56, 'Huyện'),
(574, 'Diên Khánh', 56, 'Huyện'),
(575, 'Khánh Sơn', 56, 'Huyện'),
(576, 'Trường Sa', 56, 'Huyện');

-- QUẢNG NINH (14 quận/huyện - ĐẦY ĐỦ)
INSERT INTO districts (code, name, province_code, division_type) VALUES
(193, 'Hạ Long', 22, 'Thành phố'),
(194, 'Móng Cái', 22, 'Thành phố'),
(195, 'Cẩm Phả', 22, 'Thành phố'),
(196, 'Uông Bí', 22, 'Thành phố'),
(198, 'Bình Liêu', 22, 'Huyện'),
(199, 'Tiên Yên', 22, 'Huyện'),
(200, 'Đầm Hà', 22, 'Huyện'),
(201, 'Hải Hà', 22, 'Huyện'),
(202, 'Ba Chẽ', 22, 'Huyện'),
(203, 'Vân Đồn', 22, 'Huyện'),
(204, 'Hoành Bồ', 22, 'Huyện'),
(205, 'Đông Triều', 22, 'Thị xã'),
(206, 'Quảng Yên', 22, 'Thị xã'),
(207, 'Cô Tô', 22, 'Huyện');

-- BÌNH DƯƠNG (9 quận/huyện - ĐẦY ĐỦ)
INSERT INTO districts (code, name, province_code, division_type) VALUES
(718, 'Thủ Dầu Một', 74, 'Thành phố'),
(719, 'Bàu Bàng', 74, 'Huyện'),
(720, 'Dầu Tiếng', 74, 'Huyện'),
(721, 'Bến Cát', 74, 'Thị xã'),
(722, 'Phú Giáo', 74, 'Huyện'),
(723, 'Tân Uyên', 74, 'Thành phố'),
(724, 'Dĩ An', 74, 'Thành phố'),
(725, 'Thuận An', 74, 'Thành phố'),
(726, 'Bắc Tân Uyên', 74, 'Huyện');

-- ĐỒNG NAI (11 quận/huyện - ĐẦY ĐỦ)
INSERT INTO districts (code, name, province_code, division_type) VALUES
(731, 'Biên Hòa', 75, 'Thành phố'),
(732, 'Long Khánh', 75, 'Thành phố'),
(734, 'Tân Phú', 75, 'Huyện'),
(735, 'Vĩnh Cửu', 75, 'Huyện'),
(736, 'Định Quán', 75, 'Huyện'),
(737, 'Trảng Bom', 75, 'Huyện'),
(738, 'Thống Nhất', 75, 'Huyện'),
(739, 'Cẩm Mỹ', 75, 'Huyện'),
(740, 'Long Thành', 75, 'Huyện'),
(741, 'Xuân Lộc', 75, 'Huyện'),
(742, 'Nhơn Trạch', 75, 'Huyện');

-- CÁC TỈNH CÒN LẠI (1-2 quận/huyện trung tâm)

-- Hà Giang
INSERT INTO districts (code, name, province_code, division_type) VALUES
(24, 'Hà Giang', 2, 'Thành phố');

-- Cao Bằng
INSERT INTO districts (code, name, province_code, division_type) VALUES
(40, 'Cao Bằng', 4, 'Thành phố');

-- Bắc Kạn
INSERT INTO districts (code, name, province_code, division_type) VALUES
(58, 'Bắc Kạn', 6, 'Thành phố');

-- Tuyên Quang
INSERT INTO districts (code, name, province_code, division_type) VALUES
(70, 'Tuyên Quang', 8, 'Thành phố');

-- Lào Cai
INSERT INTO districts (code, name, province_code, division_type) VALUES
(80, 'Lào Cai', 10, 'Thành phố'),
(82, 'Sa Pa', 10, 'Thị xã');

-- Điện Biên
INSERT INTO districts (code, name, province_code, division_type) VALUES
(94, 'Điện Biên Phủ', 11, 'Thành phố');

-- Lai Châu
INSERT INTO districts (code, name, province_code, division_type) VALUES
(105, 'Lai Châu', 12, 'Thành phố');

-- Sơn La
INSERT INTO districts (code, name, province_code, division_type) VALUES
(116, 'Sơn La', 14, 'Thành phố');

-- Yên Bái
INSERT INTO districts (code, name, province_code, division_type) VALUES
(132, 'Yên Bái', 15, 'Thành phố');

-- Hoà Bình
INSERT INTO districts (code, name, province_code, division_type) VALUES
(148, 'Hoà Bình', 17, 'Thành phố');

-- Thái Nguyên
INSERT INTO districts (code, name, province_code, division_type) VALUES
(164, 'Thái Nguyên', 19, 'Thành phố'),
(165, 'Sông Công', 19, 'Thành phố');

-- Lạng Sơn
INSERT INTO districts (code, name, province_code, division_type) VALUES
(178, 'Lạng Sơn', 20, 'Thành phố');

-- Bắc Giang
INSERT INTO districts (code, name, province_code, division_type) VALUES
(213, 'Bắc Giang', 24, 'Thành phố');

-- Phú Thọ
INSERT INTO districts (code, name, province_code, division_type) VALUES
(227, 'Việt Trì', 25, 'Thành phố');

-- Vĩnh Phúc
INSERT INTO districts (code, name, province_code, division_type) VALUES
(243, 'Vĩnh Yên', 26, 'Thành phố'),
(244, 'Phúc Yên', 26, 'Thành phố');

-- Bắc Ninh
INSERT INTO districts (code, name, province_code, division_type) VALUES
(256, 'Bắc Ninh', 27, 'Thành phố');

-- Hải Dương
INSERT INTO districts (code, name, province_code, division_type) VALUES
(288, 'Hải Dương', 30, 'Thành phố'),
(290, 'Chí Linh', 30, 'Thành phố');

-- Hưng Yên
INSERT INTO districts (code, name, province_code, division_type) VALUES
(323, 'Hưng Yên', 33, 'Thành phố');

-- Thái Bình
INSERT INTO districts (code, name, province_code, division_type) VALUES
(336, 'Thái Bình', 34, 'Thành phố');

-- Hà Nam
INSERT INTO districts (code, name, province_code, division_type) VALUES
(347, 'Phủ Lý', 35, 'Thành phố');

-- Nam Định
INSERT INTO districts (code, name, province_code, division_type) VALUES
(356, 'Nam Định', 36, 'Thành phố');

-- Ninh Bình
INSERT INTO districts (code, name, province_code, division_type) VALUES
(369, 'Ninh Bình', 37, 'Thành phố'),
(370, 'Tam Điệp', 37, 'Thành phố');

-- Thanh Hóa
INSERT INTO districts (code, name, province_code, division_type) VALUES
(380, 'Thanh Hóa', 38, 'Thành phố'),
(381, 'Bỉm Sơn', 38, 'Thị xã'),
(382, 'Sầm Sơn', 38, 'Thành phố');

-- Nghệ An
INSERT INTO districts (code, name, province_code, division_type) VALUES
(412, 'Vinh', 40, 'Thành phố'),
(413, 'Cửa Lò', 40, 'Thị xã');

-- Hà Tĩnh
INSERT INTO districts (code, name, province_code, division_type) VALUES
(436, 'Hà Tĩnh', 42, 'Thành phố');

-- Quảng Bình
INSERT INTO districts (code, name, province_code, division_type) VALUES
(450, 'Đồng Hới', 44, 'Thành phố');

-- Quảng Trị
INSERT INTO districts (code, name, province_code, division_type) VALUES
(461, 'Đông Hà', 45, 'Thành phố');

-- Thừa Thiên Huế
INSERT INTO districts (code, name, province_code, division_type) VALUES
(474, 'Huế', 46, 'Thành phố');

-- Quảng Nam
INSERT INTO districts (code, name, province_code, division_type) VALUES
(502, 'Tam Kỳ', 49, 'Thành phố'),
(503, 'Hội An', 49, 'Thành phố');

-- Quảng Ngãi
INSERT INTO districts (code, name, province_code, division_type) VALUES
(515, 'Quảng Ngãi', 51, 'Thành phố');

-- Ninh Thuận
INSERT INTO districts (code, name, province_code, division_type) VALUES
(582, 'Phan Rang-Tháp Chàm', 58, 'Thành phố');

-- Bình Thuận
INSERT INTO districts (code, name, province_code, division_type) VALUES
(593, 'Phan Thiết', 60, 'Thành phố');

-- Kon Tum
INSERT INTO districts (code, name, province_code, division_type) VALUES
(608, 'Kon Tum', 62, 'Thành phố');

-- Gia Lai
INSERT INTO districts (code, name, province_code, division_type) VALUES
(622, 'Pleiku', 64, 'Thành phố'),
(623, 'An Khê', 64, 'Thị xã');

-- Đắk Lắk
INSERT INTO districts (code, name, province_code, division_type) VALUES
(643, 'Buôn Ma Thuột', 66, 'Thành phố');

-- Đắk Nông
INSERT INTO districts (code, name, province_code, division_type) VALUES
(660, 'Gia Nghĩa', 67, 'Thành phố');

-- Lâm Đồng
INSERT INTO districts (code, name, province_code, division_type) VALUES
(672, 'Đà Lạt', 68, 'Thành phố'),
(673, 'Bảo Lộc', 68, 'Thành phố');

-- Bình Phước
INSERT INTO districts (code, name, province_code, division_type) VALUES
(688, 'Đồng Xoài', 70, 'Thành phố');

-- Tây Ninh
INSERT INTO districts (code, name, province_code, division_type) VALUES
(703, 'Tây Ninh', 72, 'Thành phố');

-- Bà Rịa - Vũng Tàu
INSERT INTO districts (code, name, province_code, division_type) VALUES
(747, 'Vũng Tàu', 77, 'Thành phố'),
(748, 'Bà Rịa', 77, 'Thành phố');

-- Long An
INSERT INTO districts (code, name, province_code, division_type) VALUES
(794, 'Tân An', 80, 'Thành phố');

-- Tiền Giang
INSERT INTO districts (code, name, province_code, division_type) VALUES
(815, 'Mỹ Tho', 82, 'Thành phố');

-- Bến Tre
INSERT INTO districts (code, name, province_code, division_type) VALUES
(829, 'Bến Tre', 83, 'Thành phố');

-- Trà Vinh
INSERT INTO districts (code, name, province_code, division_type) VALUES
(842, 'Trà Vinh', 84, 'Thành phố');

-- Vĩnh Long
INSERT INTO districts (code, name, province_code, division_type) VALUES
(855, 'Vĩnh Long', 86, 'Thành phố');

-- Đồng Tháp
INSERT INTO districts (code, name, province_code, division_type) VALUES
(866, 'Cao Lãnh', 87, 'Thành phố'),
(867, 'Sa Đéc', 87, 'Thành phố');

-- An Giang
INSERT INTO districts (code, name, province_code, division_type) VALUES
(883, 'Long Xuyên', 89, 'Thành phố'),
(884, 'Châu Đốc', 89, 'Thành phố');

-- Kiên Giang
INSERT INTO districts (code, name, province_code, division_type) VALUES
(899, 'Rạch Giá', 91, 'Thành phố'),
(900, 'Hà Tiên', 91, 'Thành phố');

-- Hậu Giang
INSERT INTO districts (code, name, province_code, division_type) VALUES
(930, 'Vị Thanh', 93, 'Thành phố');

-- Sóc Trăng
INSERT INTO districts (code, name, province_code, division_type) VALUES
(941, 'Sóc Trăng', 94, 'Thành phố');

-- Bạc Liêu
INSERT INTO districts (code, name, province_code, division_type) VALUES
(954, 'Bạc Liêu', 95, 'Thành phố');

-- Cà Mau
INSERT INTO districts (code, name, province_code, division_type) VALUES
(964, 'Cà Mau', 96, 'Thành phố');

-- ============================================================
-- KẾT QUẢ
-- ============================================================
SELECT '=== ĐÃ THÊM DỮ LIỆU ===' AS '';
SELECT COUNT(*) as total_provinces FROM provinces;
SELECT COUNT(*) as total_districts FROM districts;

SELECT '=== TOP 10 TỈNH CÓ NHIỀU QUẬN HUYỆN NHẤT ===' AS '';
SELECT p.name, COUNT(d.code) as total_districts
FROM provinces p
LEFT JOIN districts d ON p.code = d.province_code
GROUP BY p.code, p.name
ORDER BY total_districts DESC
LIMIT 10;

-- ============================================================
-- DỮ LIỆU USERS, LANDLORDS, BUILDINGS, ROOMS...
-- ============================================================

SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

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
(2, '079123456789', '69 Cần Vương, Quy Nhơn', 50, 52, 522, 'GP123456', 'APPROVED'),
(3, '080987654321', '789 Nguyễn Thị Thập, TP.HCM', 80, 79, 776, 'GP654321', 'APPROVED'),
(4, '081123456789', '110 Hùng Vương, Tuy Hòa', 50, 54, 540, 'GP789012', 'APPROVED');

-- BUILDINGS (10 dãy trọ)
INSERT INTO buildings (landlord_id, name, province_code, district_code, address, description, image_url) VALUES
(1, 'Dãy trọ Quy Nhơn', 52, 522, '69 Cần Vương, Quy Nhơn, Bình Định', 'Gần trung tâm, có sân phơi, bảo vệ 24/7', '/images/1/main.png'),
(1, 'Nhà trọ Nguyễn Huệ', 52, 522, '128 Nguyễn Huệ, Quy Nhơn, Bình Định', 'View biển, thoáng mát, gần chợ', '/images/2/main.png'),
(1, 'Dãy trọ 47 Nguyễn Nhạc', 52, 527, '47 Nguyễn Nhạc, An Nhơn, Bình Định', 'Phòng trọ sinh viên giá rẻ, gần trường', '/images/3/main.png'),
(1, 'Nhà trọ An Phú', 52, 527, '89 Lê Duẩn, An Nhơn, Bình Định', 'Khu yên tĩnh, an ninh tốt', '/images/4/main.png'),
(1, 'Dãy trọ Phù Cát Center', 52, 530, '234 Hùng Vương, Phù Cát, Bình Định', 'Gần khu công nghiệp, phù hợp công nhân', '/images/5/main.png'),
(2, 'Nhà trọ Quận 7', 79, 776, '789 Nguyễn Thị Thập, Quận 7, TP.HCM', 'Căn hộ mini cao cấp, đầy đủ nội thất', '/images/6/main.png'),
(2, 'Dãy trọ Phú Mỹ Hưng', 79, 776, '456 Nguyễn Văn Linh, Quận 7, TP.HCM', 'Khu compound an ninh, có hồ bơi', '/images/7/main.png'),
(2, 'Nhà trọ Bến Thành', 79, 760, '78 Lê Thánh Tôn, Quận 1, TP.HCM', 'Vị trí đắc địa, view đẹp', '/images/8/main.png'),
(3, 'Dãy trọ Tuy Hòa', 54, 540, '110 Hùng Vương, Tuy Hòa, Phú Yên', 'Gần biển, thoáng mát, view đẹp', '/images/9/main.png'),
(3, 'Nhà trọ Phú Yên Center', 54, 540, '45 Trần Phú, Tuy Hòa, Phú Yên', 'Trung tâm thành phố, đầy đủ tiện ích', '/images/10/main.png');

-- ROOMS (Theo cấu trúc thư mục: B1=10, B2=10, B3=5, B4=4, B5=6, B6=7, B7=8, B8=5, B9=5, B10=2)
INSERT INTO rooms (building_id, name, price, area, status, description) VALUES
-- Building 1: rooms 1-10 (10 phòng)
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
-- Building 2: rooms 11-20 (10 phòng)
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
-- Building 3: rooms 21-25 (5 phòng)
(3, 'Phòng 1', 1500000, 20, 'AVAILABLE', 'Phòng sinh viên giá rẻ, có wifi miễn phí'),
(3, 'Phòng 2', 1600000, 21, 'OCCUPIED', 'Gần trường học, an ninh tốt'),
(3, 'Phòng 3', 1800000, 22, 'AVAILABLE', 'Có máy lạnh, giá tốt'),
(3, 'Phòng 4', 2000000, 24, 'AVAILABLE', 'Phòng rộng hơn, có ban công nhỏ'),
(3, 'Phòng 5', 1700000, 21, 'OCCUPIED', 'Tầng 1, thuận tiện ra vào'),
-- Building 4: rooms 26-29 (4 phòng)
(4, 'Phòng A1', 2800000, 28, 'AVAILABLE', 'Khu yên tĩnh, an ninh tốt'),
(4, 'Phòng A2', 3000000, 30, 'AVAILABLE', 'Có bếp riêng, máy lạnh'),
(4, 'Phòng A3', 2700000, 27, 'OCCUPIED', 'Phòng tiêu chuẩn, đầy đủ nội thất'),
(4, 'Phòng A4', 3200000, 32, 'AVAILABLE', 'Phòng rộng, có ban công'),
-- Building 5: rooms 30-35 (6 phòng)
(5, 'P101', 2000000, 20, 'AVAILABLE', 'Gần khu công nghiệp, giá công nhân'),
(5, 'P102', 2100000, 21, 'OCCUPIED', 'Có máy lạnh, wifi miễn phí'),
(5, 'P103', 2200000, 22, 'AVAILABLE', 'Phòng sạch sẽ, an ninh tốt'),
(5, 'P104', 2300000, 23, 'AVAILABLE', 'Có bếp riêng, nấu ăn được'),
(5, 'P201', 2400000, 24, 'OCCUPIED', 'Tầng 2, yên tĩnh, thoáng mát'),
(5, 'P202', 2500000, 25, 'AVAILABLE', 'Phòng rộng, đầy đủ nội thất'),
-- Building 6: rooms 36-42 (7 phòng)
(6, 'Căn 01', 7800000, 45, 'AVAILABLE', 'Căn hộ mini cao cấp, đầy đủ nội thất'),
(6, 'Căn 02', 7500000, 42, 'OCCUPIED', 'Phòng gia đình, có bếp, máy giặt'),
(6, 'Căn 03', 8000000, 48, 'AVAILABLE', 'View sông, ban công rộng'),
(6, 'Căn 04', 7200000, 40, 'AVAILABLE', 'Phòng tiêu chuẩn, giá tốt'),
(6, 'Căn 05', 8500000, 50, 'AVAILABLE', 'Căn góc, 2 phòng ngủ'),
(6, 'Căn 06', 7600000, 43, 'AVAILABLE', 'View đẹp, yên tĩnh'),
(6, 'Căn 07', 8200000, 47, 'OCCUPIED', 'Phòng gia đình, có ban công'),
-- Building 7: rooms 43-50 (8 phòng)
(7, 'PMH-A1', 9000000, 50, 'AVAILABLE', 'Khu compound an ninh, có hồ bơi'),
(7, 'PMH-A2', 8500000, 48, 'OCCUPIED', 'Căn hộ dịch vụ, full nội thất'),
(7, 'PMH-A3', 9200000, 52, 'AVAILABLE', 'View công viên, yên tĩnh'),
(7, 'PMH-B1', 8800000, 49, 'AVAILABLE', 'Có 2 phòng ngủ, phù hợp gia đình'),
(7, 'PMH-B2', 9500000, 55, 'AVAILABLE', 'Căn góc, ban công rộng'),
(7, 'PMH-C1', 9100000, 51, 'AVAILABLE', 'Phòng cao cấp, đầy đủ tiện nghi'),
(7, 'PMH-C2', 8900000, 50, 'OCCUPIED', 'View đẹp, có gác lửng'),
(7, 'PMH-C3', 9300000, 53, 'AVAILABLE', 'Penthouse, view toàn cảnh'),
-- Building 8: rooms 51-55 (5 phòng)
(8, 'BT-101', 6500000, 38, 'AVAILABLE', 'Trung tâm Quận 1, tiện đi lại'),
(8, 'BT-102', 6200000, 35, 'OCCUPIED', 'Có bếp riêng, máy giặt'),
(8, 'BT-201', 7000000, 42, 'AVAILABLE', 'Tầng 2, yên tĩnh, view đẹp'),
(8, 'BT-202', 6700000, 39, 'AVAILABLE', 'Gần chợ Bến Thành, thuận tiện'),
(8, 'BT-301', 7200000, 43, 'AVAILABLE', 'Tầng cao, view thành phố'),
-- Building 9: rooms 56-60 (5 phòng)
(9, 'TH-101', 2800000, 28, 'AVAILABLE', 'Gần biển, thoáng mát, view đẹp'),
(9, 'TH-102', 3000000, 30, 'OCCUPIED', 'Phòng rộng, có ban công'),
(9, 'TH-201', 3200000, 32, 'AVAILABLE', 'Tầng cao, yên tĩnh'),
(9, 'TH-202', 3100000, 31, 'AVAILABLE', 'View biển, ban công rộng'),
(9, 'TH-301', 3400000, 34, 'AVAILABLE', 'Phòng VIP, view đẹp nhất'),
-- Building 10: rooms 61-62 (2 phòng)
(10, 'PY-101', 3500000, 35, 'AVAILABLE', 'Trung tâm thành phố, tiện ích đầy đủ'),
(10, 'PY-102', 3300000, 33, 'AVAILABLE', 'Phòng tiêu chuẩn, giá tốt');

-- ROOM_IMAGES (Mỗi phòng có 1 ảnh main + 4 ảnh detail, theo cấu trúc /images/buildingId/roomId/filename.png)
INSERT INTO room_images (room_id, image_url) VALUES
-- Building 1 rooms (ID 1-10) - 10 phòng
(1, '/images/1/1/main.png'),(1, '/images/1/1/detail1.png'),(1, '/images/1/1/detail2.png'),(1, '/images/1/1/detail3.png'),(1, '/images/1/1/detail4.png'),
(2, '/images/1/2/main.png'),(2, '/images/1/2/detail1.png'),(2, '/images/1/2/detail2.png'),(2, '/images/1/2/detail3.png'),(2, '/images/1/2/detail4.png'),
(3, '/images/1/3/main.png'),(3, '/images/1/3/detail1.png'),(3, '/images/1/3/detail2.png'),(3, '/images/1/3/detail3.png'),(3, '/images/1/3/detail4.png'),
(4, '/images/1/4/main.png'),(4, '/images/1/4/detail1.png'),(4, '/images/1/4/detail2.png'),(4, '/images/1/4/detail3.png'),(4, '/images/1/4/detail4.png'),
(5, '/images/1/5/main.png'),(5, '/images/1/5/detail1.png'),(5, '/images/1/5/detail2.png'),(5, '/images/1/5/detail3.png'),(5, '/images/1/5/detail4.png'),
(6, '/images/1/6/main.png'),(6, '/images/1/6/detail1.png'),(6, '/images/1/6/detail2.png'),(6, '/images/1/6/detail3.png'),(6, '/images/1/6/detail4.png'),
(7, '/images/1/7/main.png'),(7, '/images/1/7/detail1.png'),(7, '/images/1/7/detail2.png'),(7, '/images/1/7/detail3.png'),(7, '/images/1/7/detail4.png'),
(8, '/images/1/8/main.png'),(8, '/images/1/8/detail1.png'),(8, '/images/1/8/detail2.png'),(8, '/images/1/8/detail3.png'),(8, '/images/1/8/detail4.png'),
(9, '/images/1/9/main.png'),(9, '/images/1/9/detail1.png'),(9, '/images/1/9/detail2.png'),(9, '/images/1/9/detail3.png'),(9, '/images/1/9/detail4.png'),
(10, '/images/1/10/main.png'),(10, '/images/1/10/detail1.png'),(10, '/images/1/10/detail2.png'),(10, '/images/1/10/detail3.png'),(10, '/images/1/10/detail4.png'),
-- Building 2 rooms (ID 11-20) - 10 phòng
(11, '/images/2/11/main.png'),(11, '/images/2/11/detail1.png'),(11, '/images/2/11/detail2.png'),(11, '/images/2/11/detail3.png'),(11, '/images/2/11/detail4.png'),
(12, '/images/2/12/main.png'),(12, '/images/2/12/detail1.png'),(12, '/images/2/12/detail2.png'),(12, '/images/2/12/detail3.png'),(12, '/images/2/12/detail4.png'),
(13, '/images/2/13/main.png'),(13, '/images/2/13/detail1.png'),(13, '/images/2/13/detail2.png'),(13, '/images/2/13/detail3.png'),(13, '/images/2/13/detail4.png'),
(14, '/images/2/14/main.png'),(14, '/images/2/14/detail1.png'),(14, '/images/2/14/detail2.png'),(14, '/images/2/14/detail3.png'),(14, '/images/2/14/detail4.png'),
(15, '/images/2/15/main.png'),(15, '/images/2/15/detail1.png'),(15, '/images/2/15/detail2.png'),(15, '/images/2/15/detail3.png'),(15, '/images/2/15/detail4.png'),
(16, '/images/2/16/main.png'),(16, '/images/2/16/detail1.png'),(16, '/images/2/16/detail2.png'),(16, '/images/2/16/detail3.png'),(16, '/images/2/16/detail4.png'),
(17, '/images/2/17/main.png'),(17, '/images/2/17/detail1.png'),(17, '/images/2/17/detail2.png'),(17, '/images/2/17/detail3.png'),(17, '/images/2/17/detail4.png'),
(18, '/images/2/18/main.png'),(18, '/images/2/18/detail1.png'),(18, '/images/2/18/detail2.png'),(18, '/images/2/18/detail3.png'),(18, '/images/2/18/detail4.png'),
(19, '/images/2/19/main.png'),(19, '/images/2/19/detail1.png'),(19, '/images/2/19/detail2.png'),(19, '/images/2/19/detail3.png'),(19, '/images/2/19/detail4.png'),
(20, '/images/2/20/main.png'),(20, '/images/2/20/detail1.png'),(20, '/images/2/20/detail2.png'),(20, '/images/2/20/detail3.png'),(20, '/images/2/20/detail4.png'),
-- Building 3 rooms (ID 21-25) - 5 phòng
(21, '/images/3/21/main.png'),(21, '/images/3/21/detail1.png'),(21, '/images/3/21/detail2.png'),(21, '/images/3/21/detail3.png'),(21, '/images/3/21/detail4.png'),
(22, '/images/3/22/main.png'),(22, '/images/3/22/detail1.png'),(22, '/images/3/22/detail2.png'),(22, '/images/3/22/detail3.png'),(22, '/images/3/22/detail4.png'),
(23, '/images/3/23/main.png'),(23, '/images/3/23/detail1.png'),(23, '/images/3/23/detail2.png'),(23, '/images/3/23/detail3.png'),(23, '/images/3/23/detail4.png'),
(24, '/images/3/24/main.png'),(24, '/images/3/24/detail1.png'),(24, '/images/3/24/detail2.png'),(24, '/images/3/24/detail3.png'),(24, '/images/3/24/detail4.png'),
(25, '/images/3/25/main.png'),(25, '/images/3/25/detail1.png'),(25, '/images/3/25/detail2.png'),(25, '/images/3/25/detail3.png'),(25, '/images/3/25/detail4.png'),
-- Building 4 rooms (ID 26-29) - 4 phòng
(26, '/images/4/26/main.png'),(26, '/images/4/26/detail1.png'),(26, '/images/4/26/detail2.png'),(26, '/images/4/26/detail3.png'),(26, '/images/4/26/detail4.png'),
(27, '/images/4/27/main.png'),(27, '/images/4/27/detail1.png'),(27, '/images/4/27/detail2.png'),(27, '/images/4/27/detail3.png'),(27, '/images/4/27/detail4.png'),
(28, '/images/4/28/main.png'),(28, '/images/4/28/detail1.png'),(28, '/images/4/28/detail2.png'),(28, '/images/4/28/detail3.png'),(28, '/images/4/28/detail4.png'),
(29, '/images/4/29/main.png'),(29, '/images/4/29/detail1.png'),(29, '/images/4/29/detail2.png'),(29, '/images/4/29/detail3.png'),(29, '/images/4/29/detail4.png'),
-- Building 5 rooms (ID 30-35) - 6 phòng
(30, '/images/5/30/main.png'),(30, '/images/5/30/detail1.png'),(30, '/images/5/30/detail2.png'),(30, '/images/5/30/detail3.png'),(30, '/images/5/30/detail4.png'),
(31, '/images/5/31/main.png'),(31, '/images/5/31/detail1.png'),(31, '/images/5/31/detail2.png'),(31, '/images/5/31/detail3.png'),(31, '/images/5/31/detail4.png'),
(32, '/images/5/32/main.png'),(32, '/images/5/32/detail1.png'),(32, '/images/5/32/detail2.png'),(32, '/images/5/32/detail3.png'),(32, '/images/5/32/detail4.png'),
(33, '/images/5/33/main.png'),(33, '/images/5/33/detail1.png'),(33, '/images/5/33/detail2.png'),(33, '/images/5/33/detail3.png'),(33, '/images/5/33/detail4.png'),
(34, '/images/5/34/main.png'),(34, '/images/5/34/detail1.png'),(34, '/images/5/34/detail2.png'),(34, '/images/5/34/detail3.png'),(34, '/images/5/34/detail4.png'),
(35, '/images/5/35/main.png'),(35, '/images/5/35/detail1.png'),(35, '/images/5/35/detail2.png'),(35, '/images/5/35/detail3.png'),(35, '/images/5/35/detail4.png'),
-- Building 6 rooms (ID 36-42) - 7 phòng
(36, '/images/6/36/main.png'),(36, '/images/6/36/detail1.png'),(36, '/images/6/36/detail2.png'),(36, '/images/6/36/detail3.png'),(36, '/images/6/36/detail4.png'),
(37, '/images/6/37/main.png'),(37, '/images/6/37/detail1.png'),(37, '/images/6/37/detail2.png'),(37, '/images/6/37/detail3.png'),(37, '/images/6/37/detail4.png'),
(38, '/images/6/38/main.png'),(38, '/images/6/38/detail1.png'),(38, '/images/6/38/detail2.png'),(38, '/images/6/38/detail3.png'),(38, '/images/6/38/detail4.png'),
(39, '/images/6/39/main.png'),(39, '/images/6/39/detail1.png'),(39, '/images/6/39/detail2.png'),(39, '/images/6/39/detail3.png'),(39, '/images/6/39/detail4.png'),
(40, '/images/6/40/main.png'),(40, '/images/6/40/detail1.png'),(40, '/images/6/40/detail2.png'),(40, '/images/6/40/detail3.png'),(40, '/images/6/40/detail4.png'),
(41, '/images/6/41/main.png'),(41, '/images/6/41/detail1.png'),(41, '/images/6/41/detail2.png'),(41, '/images/6/41/detail3.png'),(41, '/images/6/41/detail4.png'),
(42, '/images/6/42/main.png'),(42, '/images/6/42/detail1.png'),(42, '/images/6/42/detail2.png'),(42, '/images/6/42/detail3.png'),(42, '/images/6/42/detail4.png'),
-- Building 7 rooms (ID 43-50) - 8 phòng
(43, '/images/7/43/main.png'),(43, '/images/7/43/detail1.png'),(43, '/images/7/43/detail2.png'),(43, '/images/7/43/detail3.png'),(43, '/images/7/43/detail4.png'),
(44, '/images/7/44/main.png'),(44, '/images/7/44/detail1.png'),(44, '/images/7/44/detail2.png'),(44, '/images/7/44/detail3.png'),(44, '/images/7/44/detail4.png'),
(45, '/images/7/45/main.png'),(45, '/images/7/45/detail1.png'),(45, '/images/7/45/detail2.png'),(45, '/images/7/45/detail3.png'),(45, '/images/7/45/detail4.png'),
(46, '/images/7/46/main.png'),(46, '/images/7/46/detail1.png'),(46, '/images/7/46/detail2.png'),(46, '/images/7/46/detail3.png'),(46, '/images/7/46/detail4.png'),
(47, '/images/7/47/main.png'),(47, '/images/7/47/detail1.png'),(47, '/images/7/47/detail2.png'),(47, '/images/7/47/detail3.png'),(47, '/images/7/47/detail4.png'),
(48, '/images/7/48/main.png'),(48, '/images/7/48/detail1.png'),(48, '/images/7/48/detail2.png'),(48, '/images/7/48/detail3.png'),(48, '/images/7/48/detail4.png'),
(49, '/images/7/49/main.png'),(49, '/images/7/49/detail1.png'),(49, '/images/7/49/detail2.png'),(49, '/images/7/49/detail3.png'),(49, '/images/7/49/detail4.png'),
(50, '/images/7/50/main.png'),(50, '/images/7/50/detail1.png'),(50, '/images/7/50/detail2.png'),(50, '/images/7/50/detail3.png'),(50, '/images/7/50/detail4.png'),
-- Building 8 rooms (ID 51-55) - 5 phòng
(51, '/images/8/51/main.png'),(51, '/images/8/51/detail1.png'),(51, '/images/8/51/detail2.png'),(51, '/images/8/51/detail3.png'),(51, '/images/8/51/detail4.png'),
(52, '/images/8/52/main.png'),(52, '/images/8/52/detail1.png'),(52, '/images/8/52/detail2.png'),(52, '/images/8/52/detail3.png'),(52, '/images/8/52/detail4.png'),
(53, '/images/8/53/main.png'),(53, '/images/8/53/detail1.png'),(53, '/images/8/53/detail2.png'),(53, '/images/8/53/detail3.png'),(53, '/images/8/53/detail4.png'),
(54, '/images/8/54/main.png'),(54, '/images/8/54/detail1.png'),(54, '/images/8/54/detail2.png'),(54, '/images/8/54/detail3.png'),(54, '/images/8/54/detail4.png'),
(55, '/images/8/55/main.png'),(55, '/images/8/55/detail1.png'),(55, '/images/8/55/detail2.png'),(55, '/images/8/55/detail3.png'),(55, '/images/8/55/detail4.png'),
-- Building 9 rooms (ID 56-60) - 5 phòng
(56, '/images/9/56/main.png'),(56, '/images/9/56/detail1.png'),(56, '/images/9/56/detail2.png'),(56, '/images/9/56/detail3.png'),(56, '/images/9/56/detail4.png'),
(57, '/images/9/57/main.png'),(57, '/images/9/57/detail1.png'),(57, '/images/9/57/detail2.png'),(57, '/images/9/57/detail3.png'),(57, '/images/9/57/detail4.png'),
(58, '/images/9/58/main.png'),(58, '/images/9/58/detail1.png'),(58, '/images/9/58/detail2.png'),(58, '/images/9/58/detail3.png'),(58, '/images/9/58/detail4.png'),
(59, '/images/9/59/main.png'),(59, '/images/9/59/detail1.png'),(59, '/images/9/59/detail2.png'),(59, '/images/9/59/detail3.png'),(59, '/images/9/59/detail4.png'),
(60, '/images/9/60/main.png'),(60, '/images/9/60/detail1.png'),(60, '/images/9/60/detail2.png'),(60, '/images/9/60/detail3.png'),(60, '/images/9/60/detail4.png'),
-- Building 10 rooms (ID 61-62) - 2 phòng
(61, '/images/10/61/main.png'),(61, '/images/10/61/detail1.png'),(61, '/images/10/61/detail2.png'),(61, '/images/10/61/detail3.png'),(61, '/images/10/61/detail4.png'),
(62, '/images/10/62/main.png'),(62, '/images/10/62/detail1.png'),(62, '/images/10/62/detail2.png'),(62, '/images/10/62/detail3.png'),(62, '/images/10/62/detail4.png');

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
(28,1),(28,2),(28,4),
(29,1),(29,2),(29,4),
(30,1),(30,2),(30,4),
(31,1),(31,2),(31,4),
(32,1),(32,2),(32,4),
(33,1),(33,2),(33,4),
(34,1),(34,2),(34,4),
(35,1),(35,2),(35,4),
(36,1),(36,2),(36,3),(36,4),(36,5),(36,7),
(37,1),(37,2),(37,3),(37,4),(37,5),(37,7),
(38,1),(38,2),(38,3),(38,4),(38,5),(38,7),
(39,1),(39,2),(39,3),(39,4),(39,5),(39,7),
(40,1),(40,2),(40,3),(40,4),(40,5),(40,7),
(41,1),(41,2),(41,3),(41,4),(41,5),(41,7),
(42,1),(42,2),(42,3),(42,4),(42,5),(42,7),
(43,1),(43,2),(43,3),(43,4),(43,5),(43,6),(43,7),
(44,1),(44,2),(44,3),(44,4),(44,5),(44,6),(44,7),
(45,1),(45,2),(45,3),(45,4),(45,5),(45,6),(45,7),
(46,1),(46,2),(46,3),(46,4),(46,5),(46,6),(46,7),
(47,1),(47,2),(47,3),(47,4),(47,5),(47,6),(47,7),
(48,1),(48,2),(48,3),(48,4),(48,5),(48,6),(48,7),
(49,1),(49,2),(49,3),(49,4),(49,5),(49,6),(49,7),
(50,1),(50,2),(50,3),(50,4),(50,5),(50,6),(50,7),
(51,1),(51,2),(51,3),(51,4),
(52,1),(52,2),(52,3),(52,4),
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
(5, '079123456789', '2000-04-12', 52, 527, 'An Nhơn, Bình Định'),
(6, '079987654321', '1999-10-22', 79, 776, 'Quận 7, TP.HCM'),
(7, '080123456789', '2001-05-15', 54, 540, 'Tuy Hòa, Phú Yên');

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
