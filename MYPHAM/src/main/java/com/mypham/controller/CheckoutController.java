package com.mypham.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mypham.model.DonHang;
import com.mypham.model.GioHang;
import com.mypham.model.KhuyenMai;
import com.mypham.model.TaiKhoan;
import com.mypham.service.CheckoutService;
import com.mypham.service.GioHangService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CheckoutController {

    @Autowired
    CheckoutService checkoutService;

    @Autowired
    GioHangService gioHangService;

    /**
     * Hiển thị trang thanh toán với giỏ hàng và tổng tiền.
     */
    @GetMapping("/thanhtoan")
    public String thanhToan(Model model, HttpSession session) {
        // Kiểm tra người dùng đã đăng nhập chưa
        TaiKhoan user = (TaiKhoan) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";  // Nếu chưa đăng nhập, chuyển đến trang đăng nhập
        }

        // Lấy giỏ hàng và tính tổng tiền
        GioHang gio = gioHangService.layGioHangTheoUser(user.getId());
        if (gio == null) {
            model.addAttribute("giohang", new java.util.ArrayList<>());
            model.addAttribute("tongTien", 0.0);
            return "thanhtoan";
        }
        model.addAttribute("giohang", gioHangService.layChiTietGio(gio.getId()));
        model.addAttribute("tongTien", gioHangService.tinhTongTien(gio.getId()));

        // Kiểm tra xem có mã giảm giá đã áp dụng trong session không
        Double tongTienSauGiam = (Double) session.getAttribute("tongTienSauGiam");
        String maKMDaApDung = (String) session.getAttribute("maKMDaApDung");

        if (tongTienSauGiam != null && maKMDaApDung != null) {
            model.addAttribute("tongTienSauGiam", tongTienSauGiam);
            model.addAttribute("maKMDaApDung", maKMDaApDung);
        }

        return "thanhtoan";  // Trả về trang thanh toán
    }

    /**
     * Áp dụng mã giảm giá và trả về kết quả (AJAX endpoint mới)
     */
    @PostMapping("/thanhtoan/ap-dung-ma-giam-gia")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apDungMaGiamGia(
            @RequestBody Map<String, String> request,
            HttpSession session
    ) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra người dùng đã đăng nhập chưa
        TaiKhoan user = (TaiKhoan) session.getAttribute("user");
        if (user == null) {
            response.put("success", false);
            response.put("message", "Vui lòng đăng nhập để sử dụng mã giảm giá!");
            return ResponseEntity.ok(response);
        }

        String maKM = request.get("maKM");

        // Validate mã khuyến mãi
        if (maKM == null || maKM.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Vui lòng nhập mã giảm giá!");
            return ResponseEntity.ok(response);
        }

        // Lấy tổng tiền từ giỏ hàng
        GioHang gio = gioHangService.layGioHangTheoUser(user.getId());
        if (gio == null) {
            response.put("success", false);
            response.put("message", "Giỏ hàng của bạn đang trống!");
            return ResponseEntity.ok(response);
        }
        double tongTien = gioHangService.tinhTongTien(gio.getId());

        // Kiểm tra mã giảm giá
        KhuyenMai khuyenMai = checkoutService.layKhuyenMai(maKM.trim());

        if (khuyenMai == null) {
            response.put("success", false);
            response.put("message", "Mã giảm giá không tồn tại!");
            return ResponseEntity.ok(response);
        }

        if (!khuyenMai.getHoatDong()) {
            response.put("success", false);
            response.put("message", "Mã giảm giá đã hết hiệu lực!");
            return ResponseEntity.ok(response);
        }

        if (khuyenMai.getSoLuong() <= 0) {
            response.put("success", false);
            response.put("message", "Mã giảm giá đã hết lượt sử dụng!");
            return ResponseEntity.ok(response);
        }

        // Tính toán giảm giá
        double phanTram = khuyenMai.getPhanTram();
        double soTienGiam = tongTien * (phanTram / 100);
        // Áp dụng giới hạn giảm giá tối đa
        if (khuyenMai.getGiamToiDa() != null && soTienGiam > khuyenMai.getGiamToiDa()) {
            soTienGiam = khuyenMai.getGiamToiDa();
        }
        double tongTienSauGiam = tongTien - soTienGiam;

        // Lưu thông tin vào session để sử dụng khi tạo đơn
        session.setAttribute("tongTienSauGiam", tongTienSauGiam);
        session.setAttribute("maKMDaApDung", maKM.trim());
        session.setAttribute("soTienGiam", soTienGiam);

        // Trả về kết quả thành công
        response.put("success", true);
        response.put("message", "Áp dụng mã giảm giá thành công!");
        response.put("phanTram", phanTram);
        response.put("soTienGiam", soTienGiam);
        response.put("tongTienSauGiam", tongTienSauGiam);
        response.put("tongTienGoc", tongTien);

        return ResponseEntity.ok(response);
    }

    /**
     * Tạo đơn hàng mới từ thông tin giỏ hàng, mã giảm giá và phương thức thanh
     * toán.
     */
    @PostMapping("/thanhtoan/taodon")
    public String taoDon(
            @RequestParam String diaChi,
            @RequestParam String phuongThuc,
            @RequestParam(required = false) String hoTen,
            @RequestParam(required = false) String soDienThoai,
            @RequestParam(required = false) String ghiChu,
            HttpSession session,
            RedirectAttributes ra
    ) {
        // Kiểm tra người dùng đã đăng nhập chưa
        TaiKhoan user = (TaiKhoan) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        // Validate thông tin bắt buộc
        if (diaChi == null || diaChi.trim().isEmpty()) {
            ra.addFlashAttribute("error", "Vui lòng nhập địa chỉ nhận hàng!");
            return "redirect:/thanhtoan";
        }

        if (hoTen == null || hoTen.trim().isEmpty()) {
            ra.addFlashAttribute("error", "Vui lòng nhập họ tên người nhận!");
            return "redirect:/thanhtoan";
        }

        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            ra.addFlashAttribute("error", "Vui lòng nhập số điện thoại!");
            return "redirect:/thanhtoan";
        }

        // Lấy giỏ hàng của người dùng
        GioHang gio = gioHangService.layGioHangTheoUser(user.getId());

        // Kiểm tra giỏ hàng có trống không
        if (gio == null || gioHangService.layChiTietGio(gio.getId()).isEmpty()) {
            ra.addFlashAttribute("error", "Giỏ hàng của bạn đang trống!");
            return "redirect:/giohang";
        }

        double tongTien = gioHangService.tinhTongTien(gio.getId());
        double tongTienSauGiam = tongTien;

        // Lấy mã khuyến mãi từ session (đã áp dụng trước đó)
        String maKM = (String) session.getAttribute("maKMDaApDung");
        Double tongTienSauGiamSession = (Double) session.getAttribute("tongTienSauGiam");

        // Nếu có mã giảm giá đã áp dụng, chỉ validate lại - việc trừ soLuong do taoDonHang xử lý
        if (maKM != null && !maKM.isEmpty() && tongTienSauGiamSession != null) {
            KhuyenMai khuyenMai = checkoutService.layKhuyenMai(maKM);
            if (khuyenMai == null || !khuyenMai.getHoatDong() || khuyenMai.getSoLuong() <= 0) {
                ra.addFlashAttribute("error", "Mã giảm giá không còn hợp lệ!");
                return "redirect:/thanhtoan";
            }
            tongTienSauGiam = tongTienSauGiamSession;
        }

        // Tạo đơn hàng mới
        try {
            DonHang dh = checkoutService.taoDonHang(user.getId(), diaChi, phuongThuc, maKM, hoTen, soDienThoai, ghiChu);

            // Xóa thông tin mã giảm giá khỏi session
            session.removeAttribute("tongTienSauGiam");
            session.removeAttribute("maKMDaApDung");
            session.removeAttribute("soTienGiam");

            // Thông báo thành công
            ra.addFlashAttribute("ok", "Đặt hàng thành công! Mã đơn: " + dh.getId());
            ra.addFlashAttribute("tongTienThanhToan", tongTienSauGiam);
            return "redirect:/success";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Có lỗi xảy ra khi tạo đơn hàng: " + e.getMessage());
            return "redirect:/thanhtoan";
        }
    }

    /**
     * Kiểm tra mã khuyến mãi (endpoint cũ - có thể giữ lại cho tương thích)
     */
    @GetMapping("/thanhtoan/kiemtrakam")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> kiemTraMaKM(@RequestParam String maKM) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra mã giảm giá
        KhuyenMai khuyenMai = checkoutService.layKhuyenMai(maKM);

        if (khuyenMai != null && khuyenMai.getHoatDong() && khuyenMai.getSoLuong() > 0) {
            response.put("phanTram", khuyenMai.getPhanTram());
        } else {
            response.put("phanTram", 0);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Hủy áp dụng mã giảm giá
     */
    @PostMapping("/thanhtoan/huy-ma-giam-gia")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> huyMaGiamGia(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Xóa thông tin mã giảm giá khỏi session
        session.removeAttribute("tongTienSauGiam");
        session.removeAttribute("maKMDaApDung");
        session.removeAttribute("soTienGiam");

        response.put("success", true);
        response.put("message", "Đã hủy mã giảm giá!");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/success")
    public String successPage(Model model, HttpSession session) {
        // Có thể thêm thông tin bổ sung nếu cần
        return "success";
    }
}
