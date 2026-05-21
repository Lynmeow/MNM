package com.mypham.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mypham.model.DanhMuc;
import com.mypham.model.DonHang;
import com.mypham.model.DonHangChiTiet;
import com.mypham.model.SanPham;
import com.mypham.service.DanhMucService;
import com.mypham.service.DonHangService;
import com.mypham.service.SanPhamService;
import com.mypham.util.AuthHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private SanPhamService ss;

    @Autowired
    private DanhMucService ds;

    @Autowired
    private DonHangService donHangService;

    // ===================== GUARD - Kiểm tra quyền truy cập =====================
    private boolean checkAdminAuth(HttpSession session) {
        return AuthHelper.isLoggedIn(session) && AuthHelper.isAdmin(session);
    }

    // ===================== TẠO MÃ SP (SP001...) =====================
    private String generateMaSP() {
        List<SanPham> list = ss.findAll();
        if (list == null || list.isEmpty()) {
            return "SP001";
        }

        int maxNum = 0;
        for (SanPham sp : list) {
            try {
                String ma = sp.getMaSP();
                if (ma != null && ma.startsWith("SP") && ma.length() >= 5) {
                    int num = Integer.parseInt(ma.substring(2));
                    if (num > maxNum) {
                        maxNum = num;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return String.format("SP%03d", maxNum + 1);
    }

    // ===================== UPLOAD ẢNH =====================
    private String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // Validate file type
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null
                    || !originalFilename.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|webp)$")) {
                throw new IllegalArgumentException("Chỉ chấp nhận file ảnh!");
            }

            // Validate file size (5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("File quá lớn! Tối đa 5MB");
            }

            // Tránh trùng tên file bằng cách thêm timestamp
            String fileName = System.currentTimeMillis() + "_"
                    + Path.of(originalFilename).getFileName().toString();

            // Tạo thư mục upload nếu chưa tồn tại
            Path uploadDir = Paths.get("src/main/resources/static/anh");
            Files.createDirectories(uploadDir);

            Path target = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ===================== DASHBOARD =====================
    @GetMapping
    public String adminHome(HttpSession session, HttpServletRequest request) {
        if (!checkAdminAuth(session)) {
            return "redirect:" + request.getContextPath() + "/auth";
        }
        return "redirect:/admin/sanpham";
    }

    // ===================== SẢN PHẨM =====================
    @GetMapping("/sanpham")
    public String showProductList(HttpSession session, HttpServletRequest request, Model model) {
        if (!checkAdminAuth(session)) {
            return "redirect:" + request.getContextPath() + "/auth";
        }
        model.addAttribute("sanPhams", ss.findAll());
        return "admin/danhsachsanpham";
    }

    @GetMapping("/sanpham/add")
    public String showAddProductForm(HttpSession session, HttpServletRequest request, Model model) {
        if (!checkAdminAuth(session)) {
            return "redirect:" + request.getContextPath() + "/auth";
        }
        model.addAttribute("dsdanhmuc", ds.getAllDanhMuc());
        return "admin/themsanpham";
    }

    @PostMapping("/sanpham/add")
    public String addProduct(HttpSession session, HttpServletRequest request,
            @RequestParam String tenSP,
            @RequestParam Double gia,
            @RequestParam(defaultValue = "0") Integer soLuong,
            @RequestParam String mota,
            @RequestParam("hinhAnh") MultipartFile file,
            @RequestParam String maDM) {

        if (!checkAdminAuth(session)) {
            return "redirect:" + request.getContextPath() + "/auth";
        }

        SanPham sp = new SanPham();
        sp.setMaSP(generateMaSP());
        sp.setTenSP(tenSP);
        sp.setGia(gia);
        sp.setSoLuong(soLuong);
        sp.setMota(mota);

        String fileName = saveImage(file);
        if (fileName != null) {
            sp.setHinhAnh(fileName);
        }

        DanhMuc danhMuc = ds.findByMaDM(maDM);
        sp.setDanhmuc(danhMuc);

        ss.save(sp);
        return "redirect:/admin/sanpham";
    }

    @GetMapping("/sanpham/edit/{maSP}")
    public String showEditProductForm(HttpSession session, HttpServletRequest request,
            @PathVariable String maSP, Model model) {
        if (!checkAdminAuth(session)) {
            return "redirect:" + request.getContextPath() + "/auth";
        }

        SanPham sanPham = ss.findByMaSP(maSP);
        if (sanPham == null) {
            return "redirect:/admin/sanpham";
        }

        model.addAttribute("sanPham", sanPham);
        model.addAttribute("dsdanhmuc", ds.getAllDanhMuc());
        return "admin/suasanpham";
    }

    @PostMapping("/sanpham/edit/{maSP}")
    public String editProduct(HttpSession session, HttpServletRequest request,
            @PathVariable String maSP,
            @RequestParam String tenSP,
            @RequestParam Double gia,
            @RequestParam(defaultValue = "0") Integer soLuong,
            @RequestParam String mota,
            @RequestParam("hinhAnh") MultipartFile file,
            @RequestParam String maDM) {

        if (!checkAdminAuth(session)) {
            return "redirect:" + request.getContextPath() + "/auth";
        }

        SanPham sp = ss.findByMaSP(maSP);
        if (sp == null) {
            return "redirect:/admin/sanpham";
        }

        sp.setTenSP(tenSP);
        sp.setGia(gia);
        sp.setSoLuong(soLuong);
        sp.setMota(mota);

        // Chỉ update ảnh nếu có file mới
        if (file != null && !file.isEmpty()) {
            String fileName = saveImage(file);
            if (fileName != null) {
                sp.setHinhAnh(fileName);
            }
        }

        DanhMuc danhMuc = ds.findByMaDM(maDM);
        sp.setDanhmuc(danhMuc);

        ss.save(sp);
        return "redirect:/admin/sanpham";
    }

    @PostMapping("/sanpham/delete/{maSP}")
    public String deleteProduct(HttpSession session, HttpServletRequest request,
            @PathVariable String maSP) {
        if (!checkAdminAuth(session)) {
            return "redirect:" + request.getContextPath() + "/auth";
        }
        ss.delete(maSP);
        return "redirect:/admin/sanpham";
    }

    @GetMapping("/sanpham/danhsach/{maDM}")
    public String showProductListByCategory(HttpSession session, HttpServletRequest request,
            @PathVariable String maDM, Model model) {
        if (!checkAdminAuth(session)) {
            return "redirect:" + request.getContextPath() + "/auth";
        }
        model.addAttribute("sanPhams", ss.getSanPhamByDanhMuc(maDM));
        return "admin/danhsachsanpham";
    }

    // ===================== ĐƠN HÀNG =====================
    @GetMapping("/donhang")
    public String showDonHangList(HttpSession session, HttpServletRequest request, Model model) {
        if (!checkAdminAuth(session)) {
            return "redirect:" + request.getContextPath() + "/auth";
        }
        model.addAttribute("donHangs", donHangService.getAllDonHang());
        return "admin/danhsachdonhang";
    }

    @GetMapping("/donhang/filter")
    public String filterDonHang(HttpSession session, HttpServletRequest request,
            @RequestParam(required = false) String trangThai, Model model) {
        if (!checkAdminAuth(session)) {
            return "redirect:" + request.getContextPath() + "/auth";
        }

        List<DonHang> donHangs = (trangThai != null && !trangThai.isBlank())
                ? donHangService.getDonHangByTrangThai(trangThai)
                : donHangService.getAllDonHang();

        model.addAttribute("donHangs", donHangs);
        model.addAttribute("selectedTrangThai", trangThai);
        return "admin/danhsachdonhang";
    }

    @GetMapping("/donhang/detail/{id}")
    public String showDonHangDetail(HttpSession session, HttpServletRequest request,
            @PathVariable Integer id, Model model) {
        if (!checkAdminAuth(session)) {
            return "redirect:" + request.getContextPath() + "/auth";
        }

        DonHang donHang = donHangService.getDonHangById(id);
        if (donHang == null) {
            return "redirect:/admin/donhang";
        }

        List<DonHangChiTiet> chiTiet = donHangService.getChiTietDonHang(id);
        model.addAttribute("donHang", donHang);
        model.addAttribute("chiTiet", chiTiet);
        return "admin/chitietdonhang";
    }

    @PostMapping("/donhang/update-status/{id}")
    public String updateTrangThaiDonHang(HttpSession session, HttpServletRequest request,
            @PathVariable Integer id, @RequestParam String trangThai) {
        if (!checkAdminAuth(session)) {
            return "redirect:" + request.getContextPath() + "/auth";
        }
        donHangService.updateTrangThai(id, trangThai);
        return "redirect:/admin/donhang/detail/" + id;
    }

    @PostMapping("/donhang/delete/{id}")
    public String deleteDonHang(HttpSession session, HttpServletRequest request,
            @PathVariable Integer id) {
        if (!checkAdminAuth(session)) {
            return "redirect:" + request.getContextPath() + "/auth";
        }
        donHangService.deleteDonHang(id);
        return "redirect:/admin/donhang";
    }
    // Thêm các method sau vào AdminController

// ===================== TẠO ĐƠN HÀNG HỘ KHÁCH =====================
// Hiển thị form tạo đơn hàng
    @GetMapping("/donhang/add")
    public String showCreateOrderForm(HttpSession session, HttpServletRequest request, Model model) {
        if (!checkAdminAuth(session)) {
            return "redirect:" + request.getContextPath() + "/auth";
        }

        // Lấy danh sách sản phẩm để chọn
        model.addAttribute("sanPhams", ss.findAll());
        return "admin/themdonhang";
    }

// Xử lý tạo đơn hàng
    @PostMapping("/donhang/add")
    public String createOrder(HttpSession session, HttpServletRequest request,
            @RequestParam(required = false) String diaChi,
            @RequestParam(required = false) String phuongThucThanhToan,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) List<String> maSanPham,
            @RequestParam(required = false) List<Integer> soLuong,
            Model model) {

        if (!checkAdminAuth(session)) {
            return "redirect:" + request.getContextPath() + "/auth";
        }

        // Validate input
        if (diaChi == null || diaChi.isBlank()) {
            model.addAttribute("error", "Vui lòng nhập địa chỉ giao hàng!");
            model.addAttribute("sanPhams", ss.findAll());
            return "admin/themdonhang";
        }

        if (phuongThucThanhToan == null || phuongThucThanhToan.isBlank()) {
            model.addAttribute("error", "Vui lòng chọn phương thức thanh toán!");
            model.addAttribute("sanPhams", ss.findAll());
            return "admin/themdonhang";
        }

        if (maSanPham == null || maSanPham.isEmpty()
                || soLuong == null || soLuong.isEmpty()
                || maSanPham.size() != soLuong.size()) {
            model.addAttribute("error", "Vui lòng chọn ít nhất 1 sản phẩm!");
            model.addAttribute("sanPhams", ss.findAll());
            return "admin/themdonhang";
        }

        try {
            // Tạo đơn hàng
            DonHang donHang = new DonHang();

            // Nếu không có userId, dùng ID của user "Khách vãng lai" 
            // Thay số 1 bằng ID thực tế từ database của bạn
            donHang.setUserId(userId != null && userId > 0 ? userId : 1);

            donHang.setDiaChi(diaChi);
            donHang.setPhuongThucThanhToan(phuongThucThanhToan);
            donHang.setTrangThai("Đã xác nhận");
            donHang.setNgayTao(new java.util.Date());

            // Tính tổng tiền
            double tongTien = 0;
            List<DonHangChiTiet> chiTietList = new java.util.ArrayList<>();

            for (int i = 0; i < maSanPham.size(); i++) {
                String maSP = maSanPham.get(i);
                int sl = soLuong.get(i);

                if (sl <= 0) {
                    continue;
                }

                SanPham sp = ss.findByMaSP(maSP);
                if (sp == null) {
                    continue;
                }

                double donGia = sp.getGia();
                double thanhTien = donGia * sl;

                DonHangChiTiet chiTiet = new DonHangChiTiet();
                chiTiet.setMaSP(maSP);
                chiTiet.setSoLuong(sl);
                chiTiet.setDonGia(donGia);
                chiTiet.setThanhTien(thanhTien);

                chiTietList.add(chiTiet);
                tongTien += thanhTien;
            }

            donHang.setTongTien(tongTien);
            donHang.setGiamGia(0.0);  // Mặc định không giảm giá
            donHang.setThanhToan(tongTien);  // Thành toán = tổng tiền - giảm giá

            // Lưu đơn hàng trước
            donHangService.saveDonHang(donHang);

            // Sau đó lưu chi tiết với donHangId
            for (DonHangChiTiet chiTiet : chiTietList) {
                chiTiet.setDonHangId(donHang.getId());
                donHangService.saveChiTiet(chiTiet);
            }

            return "redirect:/admin/donhang?success=created";

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("===== LỖI TẠO ĐƠN HÀNG =====");
            System.out.println("Error message: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("sanPhams", ss.findAll());
            return "admin/themdonhang";
        }
    }

// API lấy thông tin sản phẩm (dùng cho AJAX)
    @GetMapping("/sanpham/info/{maSP}")
    @ResponseBody
    public SanPham getProductInfo(@PathVariable String maSP) {
        return ss.findByMaSP(maSP);
    }
}
