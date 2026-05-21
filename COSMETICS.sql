-- ======================================
-- 🪷 Tạo database
-- ======================================
CREATE DATABASE COSMETICS;
GO
USE COSMETICS;
GO

-- Thêm tài khoản admin vào cơ sở dữ liệu
INSERT INTO TaiKhoan (tenDangNhap, matKhau, vaiTro)
VALUES ('admin', 'password123', 'ADMIN');

 select* from TaiKhoan;
DROP TABLE IF EXISTS DonHang;

-- Tạo lại bảng DonHang
CREATE TABLE DonHang (
    id INT IDENTITY(1,1) PRIMARY KEY,
    userId INT NOT NULL,
    diaChi NVARCHAR(255),
    phuongThucThanhToan NVARCHAR(50),
    tongTien DECIMAL(18,2),
    giamGia DECIMAL(18,2),
    thanhToan DECIMAL(18,2),
    ngayTao DATETIME DEFAULT GETDATE(),
    trangThai NVARCHAR(50) DEFAULT 'Chờ xác nhận'
);
-- Thêm các cột vào bảng DonHang
ALTER TABLE DonHang
ADD
    id INT IDENTITY(1,1) PRIMARY KEY,          -- Cột ID làm khóa chính, tự động tăng
    userId INT NOT NULL,                       -- Cột userId, không thể null
    giamGia DECIMAL(18, 2) DEFAULT 0,           -- Cột giảm giá, mặc định là 0
    thanhToan DECIMAL(18, 2) NOT NULL,          -- Cột thanh toán, không thể null
    phuongThucThanhToan NVARCHAR(50),           -- Cột phương thức thanh toán
    trangThai NVARCHAR(50) DEFAULT 'Chờ xác nhận', -- Cột trạng thái đơn hàng, mặc định là 'Chờ xác nhận'
    diaChi NVARCHAR(255),                       -- Cột địa chỉ
    ngayTao DATETIME DEFAULT GETDATE();         -- Cột ngày tạo, mặc định là ngày hiện tại

  -- Thay FK_name bằng tên ràng buộc khóa ngoại thực tế

  DELETE FROM dbo.DonHang;  -- Xóa toàn bộ bản ghi trong bảng DonHang

 DELETE FROM DonHang;

select* from DonHang;
CREATE TABLE DonHangChiTiet (
    id INT IDENTITY(1,1) PRIMARY KEY,
    donHangId INT NOT NULL,
    maSP VARCHAR(10) NOT NULL,
    soLuong INT NOT NULL,
    donGia INT NOT NULL,
    FOREIGN KEY (donHangId) REFERENCES DonHang(id)
);
GO

ALTER TABLE DonHangChiTiet
ADD thanhTien DECIMAL(18,2);

ALTER TABLE DonHangChiTiet
ADD CONSTRAINT FK_DonHang FOREIGN KEY (donHangId) REFERENCES DonHang(id);

DROP TABLE DonHang;

CREATE TABLE DonHang (
    id INT IDENTITY(1,1) PRIMARY KEY,  -- Tạo cột id là khóa chính và tự động tăng
    userId INT NOT NULL,                -- Cột userId kiểu INT không được NULL
    diaChi NVARCHAR(255),               -- Cột diaChi kiểu chuỗi văn bản, tối đa 255 ký tự
    phuongThucThanhToan NVARCHAR(50),   -- Cột phương thức thanh toán
    tongTien DECIMAL(18,2),             -- Cột tongTien kiểu số thập phân, 18 chữ số và 2 chữ số sau dấu phẩy
    giamGia DECIMAL(18,2),              -- Cột giamGia kiểu số thập phân, 18 chữ số và 2 chữ số sau dấu phẩy
    thanhToan DECIMAL(18,2),            -- Cột thanhToan kiểu số thập phân, 18 chữ số và 2 chữ số sau dấu phẩy
    ngayTao DATETIME DEFAULT GETDATE(), -- Cột ngayTao kiểu ngày tháng và có giá trị mặc định là ngày giờ hiện tại
    trangThai NVARCHAR(50) DEFAULT N'Chờ xác nhận' -- Cột trangThai kiểu chuỗi, mặc định là 'Chờ xác nhận'
);



select* from KhuyenMai;

CREATE TABLE LichSuMaGiamGia (
    id INT IDENTITY(1,1) PRIMARY KEY,
    userId INT NOT NULL,
    maGiamGia VARCHAR(50),
    donHangId INT NOT NULL,
    ngaySuDung DATETIME DEFAULT GETDATE()
);
GO



-- Tạo user Khách vãng lai trong bảng TaiKhoan
INSERT INTO TaiKhoan (tenDangNhap, matKhau, email, vaiTro, trangThai)
VALUES ('khachvanglai', 'N/A', 'guest@system.com', 'GUEST', 1);



CREATE TABLE TaiKhoan (
    id INT IDENTITY(1,1) PRIMARY KEY,
    tenDangNhap VARCHAR(50) UNIQUE NOT NULL,
    matKhau VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    vaiTro VARCHAR(20) DEFAULT 'USER',
    trangThai BIT DEFAULT 1
);

ALTER TABLE TaiKhoan
ADD anhDaiDien VARCHAR(255);


CREATE TABLE GioHang (
    id INT IDENTITY(1,1) PRIMARY KEY,
    userId INT NOT NULL,
    ngayTao DATETIME DEFAULT GETDATE(),

    FOREIGN KEY (userId) REFERENCES TaiKhoan(id)
);
GO

CREATE TABLE GioHangChiTiet (
    id INT IDENTITY(1,1) PRIMARY KEY,
    gioHangId INT NOT NULL,
    maSP NVARCHAR(10) ,
    soLuong INT DEFAULT 1,

    FOREIGN KEY (gioHangId) REFERENCES GioHang(id),
    FOREIGN KEY (maSP) REFERENCES SanPham(maSP)
);
GO


-- ======================================
-- 🪷 Tạo bảng DANHMUC và SANPHAM
-- ======================================
CREATE TABLE DanhMuc (
    maDM NVARCHAR(10) PRIMARY KEY,
    tenDM NVARCHAR(100)
);

CREATE TABLE SanPham (
    maSP NVARCHAR(10) PRIMARY KEY,
    tenSP NVARCHAR(150),
    gia FLOAT,
    soLuong INT,
    mota NVARCHAR(MAX),
    hinhAnh NVARCHAR(255),
    maDM NVARCHAR(10),
    FOREIGN KEY (maDM) REFERENCES DanhMuc(maDM)
);
GO

ALTER TABLE SanPham
ADD hdsd NVARCHAR(1000) NULL;

-- Sữa rửa mặt
UPDATE SanPham
SET hdsd = N'Làm ướt mặt, lấy lượng vừa đủ, massage nhẹ 30–60 giây rồi rửa sạch.'
WHERE maDM = 'DM01';

-- Serum
UPDATE SanPham
SET hdsd = N'Sau khi rửa mặt và toner, lấy 2–3 giọt serum, vỗ nhẹ cho thẩm thấu.'
WHERE maDM = 'DM02';

-- Kem dưỡng
UPDATE SanPham
SET hdsd = N'Lấy lượng vừa đủ, thoa đều mặt và cổ, dùng sáng và tối.'
WHERE maDM = 'DM03';

-- Nước tẩy trang
UPDATE SanPham
SET hdsd = N'Thấm ra bông tẩy trang, lau nhẹ toàn mặt đến khi sạch.'
WHERE maDM = 'DM04';

-- Sữa tắm
UPDATE SanPham
SET hdsd = N'Làm ướt cơ thể, tạo bọt và massage nhẹ rồi xả sạch.'
WHERE maDM = 'DM05';

-- Son
UPDATE SanPham
SET hdsd = N'Thoa trực tiếp lên môi, có thể dùng cọ để tán đều.'
WHERE maDM = 'DM06';

-- Toner
UPDATE SanPham
SET hdsd = N'Sau rửa mặt, vỗ nhẹ toner lên da để cân bằng ẩm.'
WHERE maDM = 'DM07';

-- Soap
UPDATE SanPham
SET hdsd = N'Tạo bọt xà phòng, massage nhẹ rồi rửa sạch.'
WHERE maDM = 'DM08';

-- Dầu gội
UPDATE SanPham
SET hdsd = N'Làm ướt tóc, massage da đầu 1–2 phút rồi xả sạch.'
WHERE maDM = 'DM09';


-- ======================================
-- 🪷 Dữ liệu danh mục
-- ======================================
INSERT INTO DanhMuc VALUES
('DM01', N'Sữa Rửa Mặt'),
('DM02', N'Serum'),
('DM03', N'Kem Dưỡng & Face Cream'),
('DM04', N'Nước Tẩy Trang'),
('DM05', N'Sữa Tắm'),
('DM06', N'Son Môi'),
('DM07', N'Toner'),
('DM08', N'Xà Phòng & Soap'),
('DM09', N'Dầu Gội');
GO

-- ======================================
-- 🧴 DM01 – SỮA RỬA MẶT
-- ======================================
INSERT INTO SanPham VALUES
('SP01', N'Sữa Rửa Mặt SVR', 320000, 50, N'Sữa rửa mặt dịu nhẹ, làm sạch da nhạy cảm.', N'Sữa Rửa Mặt SVR.png', 'DM01'),
('SP02', N'Sữa Rửa Mặt Trà Xanh', 250000, 60, N'Thành phần trà xanh giúp giảm mụn.', N'Sữa Rửa Mặt Trà Xanh.png', 'DM01'),
('SP03', N'Sữa Rửa Mặt Acnes', 200000, 40, N'Làm sạch sâu, ngăn ngừa mụn.', N'Sữa Rửa Mặt Acnes.png', 'DM01'),
('SP04', N'Sữa Rửa Mặt Simple', 280000, 35, N'Dịu nhẹ, phù hợp da nhạy cảm.', N'Sữa Rửa Mặt Simple.png', 'DM01'),
('SP05', N'Sữa Rửa Mặt Himalaya', 260000, 40, N'Chiết xuất thảo dược làm dịu da.', N'Sữa Rửa Mặt Himalaya.png', 'DM01'),
('SP06', N'Sữa Rửa Mặt Muối Biển', 270000, 50, N'Loại bỏ bã nhờn, làm sáng da.', N'Sữa Rửa Mặt Muối Biển.png', 'DM01'),
('SP07', N'Sữa Rửa Mặt Origin', 300000, 45, N'Làm sạch và cân bằng độ ẩm tự nhiên.', N'Sữa Rửa Mặt Origin.png', 'DM01');

-- ======================================
-- 💧 DM02 – SERUM
-- ======================================
INSERT INTO SanPham VALUES
('SP08', N'Serum Sothys', 820000, 25, N'Dưỡng sáng và phục hồi da.', N'Serum Sothys.png', 'DM02'),
('SP09', N'Serum SVR', 760000, 30, N'Phục hồi da sau mụn.', N'Serum SVR.png', 'DM02'),
('SP10', N'Serum Exemia', 980000, 20, N'Chống lão hóa, săn chắc da.', N'Serum Exemia.png', 'DM02'),
('SP11', N'Serum Elixage', 880000, 30, N'Cải thiện độ đàn hồi cho da.', N'Serum Elixage.png', 'DM02'),
('SP12', N'Serum Biotherm', 950000, 25, N'Dưỡng ẩm chuyên sâu, sáng da.', N'Serum Biotherm.png', 'DM02'),
('SP13', N'Serum Dropped', 900000, 25, N'Tái tạo tế bào, cho làn da tươi trẻ.', N'Serum Dropped.png', 'DM02'),
('SP14', N'Serum Thảo Mộc', 780000, 20, N'Tinh dầu thiên nhiên, làm dịu da.', N'Serum Thảo Mộc.png', 'DM02');

-- ======================================
-- 🪞 DM03 – KEM DƯỠNG & FACE CREAM
-- ======================================
INSERT INTO SanPham VALUES
('SP15', N'Face Cream - Vichy', 650000, 25, N'Kem dưỡng ẩm sâu, sáng mịn.', N'Face Cream -Vichy.png', 'DM03'),
('SP16', N'Face Cream Jar by Dior', 890000, 25, N'Dưỡng căng bóng da cao cấp.', N'Face Cream Jar by Dior.png', 'DM03'),
('SP17', N'Face Cream - Red Jar', 700000, 30, N'Cải thiện độ đàn hồi, săn chắc.', N'Face Cream - Red Jar.png', 'DM03'),
('SP18', N'Face Cream - Clarins Extra-Firming', 900000, 20, N'Làm săn da, giảm nếp nhăn.', N'Face Cream - Clarins Extra-Firming.png', 'DM03'),
('SP19', N'Face Cream - Clarins Multi-Active Day Cream', 920000, 20, N'Dưỡng ẩm, bảo vệ da cả ngày.', N'Face Cream - Clarins Multi-Active Day Cream.png', 'DM03'),
('SP20', N'Face Cream - Biotherm Aquasource', 940000, 15, N'Giữ ẩm 48h, căng bóng tự nhiên.', N'Face Cream -Biotherm Aquasource .png', 'DM03'),
('SP21', N'Kem Dưỡng Thảo Mộc', 450000, 30, N'Cấp ẩm, dịu da.', N'Kem Dưỡng Thảo Mộc.png', 'DM03');

-- ======================================
-- 💧 DM04 – NƯỚC TẨY TRANG
-- ======================================
INSERT INTO SanPham VALUES
('SP22', N'Nước Tẩy Trang Bioderma', 400000, 45, N'Làm sạch sâu, dịu nhẹ.', N'Nước Tẩy Trang Bioderma.png', 'DM04'),
('SP23', N'Nước Tẩy Trang Bioré', 360000, 45, N'Thành phần dịu nhẹ, sạch nhờn.', N'Nước Tẩy Trang Bioré.png', 'DM04'),
('SP24', N'Nước Tẩy Trang Garnier', 370000, 45, N'Phù hợp da dầu, hỗn hợp.', N'Nước Tẩy Trang Garnier.png', 'DM04'),
('SP25', N'Nước Tẩy Trang Loreal', 380000, 40, N'Làm sạch, mịn da.', N'Nước Tẩy Trang Loreal.png', 'DM04');

-- ======================================
-- 🚿 DM05 – SỮA TẮM
-- ======================================
INSERT INTO SanPham VALUES
('SP26', N'Sữa Tắm Cam Taro', 390000, 35, N'Hương cam tươi mát.', N'Sữa Tắm Cam Taro.png', 'DM05'),
('SP27', N'Sữa Tắm Huyền Bí', 370000, 35, N'Hương thơm quyến rũ.', N'Sữa Tắm Huyền Bí.png', 'DM05'),
('SP28', N'Sữa Tắm Lúa Mạch', 390000, 35, N'Làm mềm và sáng da.', N'Sữa Tắm Lúa Mạch.png', 'DM05'),
('SP29', N'Sữa Tắm Dầu Hạt Mỡ', 420000, 30, N'Dưỡng ẩm tự nhiên.', N'Sữa tắm Dầu Hạt Mỡ.png', 'DM05'),
('SP30', N'Sữa Tắm Rose', 410000, 25, N'Hương hoa hồng nhẹ nhàng.', N'Sữa Tắm Rose.png', 'DM05'),
('SP31', N'Sữa Tắm Cake', 390000, 25, N'Mùi thơm ngọt ngào.', N'Sữa Tắm Cake.png', 'DM05'),
('SP32', N'Sữa Tắm Aroma', 400000, 25, N'Mùi tinh dầu tự nhiên thư giãn.', N'Sữa tắm Aroma.png', 'DM05');

-- ======================================
-- 💄 DM06 – SON MÔI
-- ======================================
INSERT INTO SanPham VALUES
('SP33', N'Son Rose', 420000, 40, N'Màu hồng tự nhiên.', N'Son Rose.png', 'DM06'),
('SP34', N'Son Pinke', 420000, 40, N'Màu hồng nude nhẹ.', N'Son Pinke.png', 'DM06'),
('SP35', N'Son Biute', 420000, 40, N'Màu đỏ đất thời thượng.', N'Son Biute.png', 'DM06');


-- ======================================
-- 🌿 DM07 – TONER
-- ======================================
INSERT INTO SanPham VALUES
('SP37', N'Toner Chopin', 350000, 30, N'Cân bằng ẩm, se khít lỗ chân lông.', N'Toner Chopin.png', 'DM07'),
('SP38', N'Toner Trà Xanh', 300000, 30, N'Giảm mụn, làm mát da.', N'Toner Trà Xanh.png', 'DM07'),
('SP39', N'Toner Eucerin', 370000, 30, N'Phục hồi da, chống kích ứng.', N'Toner Eucerin.png', 'DM07'),
('SP40', N'Toner Timeless', 380000, 25, N'Dưỡng sáng, chống oxy hóa.', N'Toner Timeless.png', 'DM07'),
('SP41', N'Toner Nivea', 290000, 40, N'Cấp ẩm, làm mềm da.', N'Toner Nivea.png', 'DM07');

-- ======================================
-- 🧼 DM08 – SOAP
-- ======================================
INSERT INTO SanPham VALUES
('SP42', N'Soap Cam', 280000, 20, N'Thơm dịu, sạch khuẩn.', N'Soap Cam.png', 'DM08'),
('SP43', N'Soap Hoa Sứ', 280000, 20, N'Hương hoa sứ nhẹ, giữ ẩm.', N'Soap Hoa Sứ.png', 'DM08'),
('SP44', N'Soap Hoa&Olive', 280000, 20, N'Dưỡng ẩm từ dầu olive.', N'Soap Hoa&Olive.png', 'DM08'),
('SP45', N'Soap Việt Quất', 280000, 20, N'Hương berry tươi mới.', N'Soap Việt Quất.png', 'DM08'),
('SP46', N'Soap Gừng', 280000, 20, N'Thơm ấm, sạch sâu.', N'Soap Gừng.png', 'DM08'),
('SP47', N'Soap Dầu Olive', 280000, 20, N'Dưỡng ẩm hiệu quả.', N'Soap Dầu Olive.png', 'DM08');

-- ======================================
-- 🧴 DM09 – DẦU GỘI
-- ======================================
INSERT INTO SanPham VALUES
('SP48', N'Dầu gội Beer', 320000, 40, N'Giúp tóc bóng mượt.', N'Dầu gội Beer.png', 'DM09'),
('SP49', N'Dầu gội em bé', 300000, 40, N'Dịu nhẹ cho da đầu bé.', N'Dầu gội em bé.png', 'DM09'),
('SP50', N'Dầu gội hướng hoa', 330000, 40, N'Hương hoa nhẹ nhàng.', N'Dầu gội hướng hoa.png', 'DM09'),
('SP51', N'Dầu gội Lá Nhu', 310000, 40, N'Chiết xuất lá nhu làm mượt tóc.', N'Dầu gội Lá Nhu.png', 'DM09'),
('SP52', N'Dầu gội Olive', 340000, 40, N'Thành phần dầu olive dưỡng tóc.', N'Dầu gội Olive.png', 'DM09'),
('SP53', N'Dầu gội oải hương', 330000, 40, N'Thơm dịu, thư giãn.', N'Dầu gội oải hương.png', 'DM09');
GO
DELETE FROM SanPham
WHERE maSP = 'SP36';

SELECT 
    fk.name AS FK_name,
    tp.name AS parent_table,
    ref.name AS referenced_table
FROM 
    sys.foreign_keys AS fk
    INNER JOIN sys.tables AS tp ON fk.parent_object_id = tp.object_id
    INNER JOIN sys.tables AS ref ON fk.referenced_object_id = ref.object_id
WHERE 
    tp.name = 'DonHangChiTiet';  -- Bảng chứa khóa ngoại


    ALTER TABLE DonHangChiTiet
DROP CONSTRAINT FK__DonHangCh__donHa__74AE54BC;
