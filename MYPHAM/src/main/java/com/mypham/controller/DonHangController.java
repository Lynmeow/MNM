package com.mypham.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mypham.model.DonHang;
import com.mypham.model.DonHangChiTiet;
import com.mypham.model.TaiKhoan;
import com.mypham.service.DonHangService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/donhang")
public class DonHangController {

    @Autowired
    private DonHangService donHangService;

    // Hiển thị danh sách đơn hàng của user
    @GetMapping("/my-orders")
    public String myOrders(Model model, HttpSession session) {
        // Lấy thông tin user từ session
        TaiKhoan user = (TaiKhoan) session.getAttribute("user");

        // Kiểm tra đăng nhập
        if (user == null) {
            return "redirect:/auth";
        }

        // SỬA: Gọi đúng method getDonHangByUserId thay vì getDonHangById
        List<DonHang> donHangs = donHangService.getDonHangByUserId(user.getId());

        model.addAttribute("donHangs", donHangs);
        model.addAttribute("user", user);

        return "donhang/list";
    }

    // Xem chi tiết 1 đơn hàng
    @GetMapping("/detail/{id}")
    public String detailDonHang(@PathVariable Integer id, Model model, HttpSession session) {
        // Kiểm tra đăng nhập
        TaiKhoan user = (TaiKhoan) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        DonHang donHang = donHangService.getDonHangById(id);

        // Kiểm tra đơn hàng có tồn tại không
        if (donHang == null) {
            return "redirect:/donhang/my-orders";
        }

        // Kiểm tra đơn hàng có thuộc về user này không (bảo mật)
        if (!donHang.getUserId().equals(user.getId())) {
            return "redirect:/donhang/my-orders";
        }

        // Lấy chi tiết sản phẩm trong đơn hàng
        List<DonHangChiTiet> chiTiet = donHangService.getChiTietDonHang(id);

        model.addAttribute("donHang", donHang);
        model.addAttribute("chiTiet", chiTiet);
        model.addAttribute("user", user);

        return "donhang/detail";
    }

    @PostMapping("/cancel/{id}")
    @ResponseBody
    public ResponseEntity<?> cancel(@PathVariable Integer id, HttpSession session) {
        TaiKhoan user = (TaiKhoan) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Chưa đăng nhập"));
        }

        DonHang donHang = donHangService.getDonHangById(id);
        if (donHang == null) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Không tìm thấy đơn"));
        }

        // Bảo mật: đơn phải thuộc user
        if (!donHang.getUserId().equals(user.getId())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Không có quyền"));
        }

        // Chỉ cho hủy khi "Chờ xác nhận"
        if (!"Chờ xác nhận".equalsIgnoreCase(donHang.getTrangThai())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Đơn không thể hủy ở trạng thái hiện tại"));
        }

        // Update trạng thái
        donHangService.cancelOrder(id); // em tạo method này (hoặc save trực tiếp)
        return ResponseEntity.ok(Map.of("success", true));
    }

}
