package com.mypham.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mypham.model.TaiKhoan;
import com.mypham.service.TaiKhoanService;

import jakarta.servlet.http.HttpSession;

@Controller
public class DangNhapController {

    @Autowired
    private TaiKhoanService taiKhoanService;

    // ======= HIỂN THỊ TRANG ĐĂNG NHẬP / ĐĂNG KÝ CHUNG =======
    @GetMapping("/auth")
    public String loginPage() {
        return "auth/login-register";  // Trả về trang login (ví dụ: /src/main/resources/templates/auth/login.html)
    }

    // ======= XỬ LÝ ĐĂNG NHẬP =======
    @PostMapping("/dangnhap")
    public String dangNhap(@RequestParam("tenDangNhap") String tenDangNhap,
            @RequestParam("matKhau") String matKhau,
            HttpSession session,
            RedirectAttributes ra) {

        TaiKhoan tk = taiKhoanService.dangNhap(tenDangNhap, matKhau);

        if (tk == null) {
            // Nếu không có tài khoản hoặc mật khẩu sai
            ra.addFlashAttribute("loginError", "Sai tài khoản hoặc mật khẩu.");
            return "redirect:/auth";  // Quay lại trang đăng nhập
        }

        // Lưu thông tin người dùng vào session
        session.setAttribute("user", tk);
        session.setAttribute("vaiTro", tk.getVaiTro());  // Lưu vai trò của người dùng vào session

        return "redirect:/";  // Quay lại trang chủ
    }

    // ======= XỬ LÝ ĐĂNG KÝ =======
    @PostMapping("/dangky")
    public String dangKy(TaiKhoan taiKhoan, RedirectAttributes ra) {
        if (taiKhoanService.tonTaiUsername(taiKhoan.getTenDangNhap())) {
            ra.addFlashAttribute("registerError", "Tên đăng nhập đã tồn tại!");
            return "redirect:/auth";  // Quay lại trang đăng nhập nếu tài khoản đã tồn tại
        }

        // Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu
        taiKhoanService.dangKy(taiKhoan);
        ra.addFlashAttribute("registerSuccess", "Đăng ký thành công! Hãy đăng nhập.");

        // Sau khi đăng ký thành công, chuyển hướng về trang đăng nhập
        return "redirect:/auth";  // Chuyển hướng về trang đăng nhập sau khi đăng ký thành công
    }

    // ======= XỬ LÝ ĐĂNG XUẤT =======
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // Hủy session khi đăng xuất
        return "redirect:/";  // Chuyển hướng về trang đăng nhập
    }
}
