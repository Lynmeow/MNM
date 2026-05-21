package com.mypham.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mypham.model.GioHang;
import com.mypham.model.GioHangChiTiet;
import com.mypham.model.TaiKhoan;
import com.mypham.service.GioHangService;

import jakarta.servlet.http.HttpSession;

@Controller
public class GioHangController {

    @Autowired
    private GioHangService gioHangService;

    // Xem giỏ hàng
    @GetMapping("/giohang")
    public String xemGioHang(Model model, HttpSession ses) {
        // Lấy người dùng từ session
        TaiKhoan user = (TaiKhoan) ses.getAttribute("user");

        // Kiểm tra xem người dùng đã đăng nhập chưa
        if (user == null) {
            return "redirect:/auth";  // Chuyển hướng đến trang đăng nhập
        }

        // Lấy giỏ hàng của người dùng
        GioHang gio = gioHangService.layGioHangTheoUser(user.getId());

        if (gio == null) {
            model.addAttribute("giohang", new ArrayList<>());
            model.addAttribute("tongTien", 0);
            return "giohang";  // Trả về trang giỏ hàng với giỏ hàng rỗng
        }

        Integer gioHangId = gio.getId();
        List<GioHangChiTiet> ds = gioHangService.layChiTietGio(gioHangId);  // Lấy chi tiết giỏ hàng
        model.addAttribute("giohang", ds);

        Double tongTien = gioHangService.tinhTongTien(gioHangId);
        model.addAttribute("tongTien", tongTien);

        return "giohang";  // Trả về trang giỏ hàng
    }

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/giohang/them")
    public String themVaoGio(@RequestParam String maSP, HttpSession ses, RedirectAttributes ra) {
        // Kiểm tra xem người dùng đã đăng nhập chưa
        TaiKhoan user = (TaiKhoan) ses.getAttribute("user");

        if (user == null) {
            ra.addFlashAttribute("loginError", "Bạn cần đăng nhập để thực hiện hành động này");
            return "redirect:/auth";  // Điều hướng tới trang đăng nhập
        }

        // Thêm sản phẩm vào giỏ hàng của người dùng
        gioHangService.themVaoGio(user.getId(), maSP);

        return "redirect:/giohang";  // Quay lại trang giỏ hàng
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @PostMapping("/giohang/xoa/{id}")
    public String xoa(@PathVariable Integer id, HttpSession ses, RedirectAttributes ra) {
        TaiKhoan user = (TaiKhoan) ses.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        // Kiểm tra item có thuộc giỏ hàng của user này không
        GioHang gio = gioHangService.layGioHangTheoUser(user.getId());
        if (gio == null) {
            return "redirect:/giohang";
        }
        GioHangChiTiet ct = gioHangService.layChiTietById(id);
        if (ct == null || !ct.getGioHangId().equals(gio.getId())) {
            ra.addFlashAttribute("error", "Không có quyền xóa sản phẩm này!");
            return "redirect:/giohang";
        }

        gioHangService.xoaChiTiet(id);
        return "redirect:/giohang";
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    @PostMapping("/giohang/sua")
    public String sua(@RequestParam Integer id, @RequestParam Integer soLuong, HttpSession session, RedirectAttributes ra) {
        TaiKhoan user = (TaiKhoan) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        // Kiểm tra item có thuộc giỏ hàng của user này không
        GioHang gio = gioHangService.layGioHangTheoUser(user.getId());
        if (gio == null) {
            return "redirect:/giohang";
        }
        GioHangChiTiet ct = gioHangService.layChiTietById(id);
        if (ct == null || !ct.getGioHangId().equals(gio.getId())) {
            ra.addFlashAttribute("error", "Không có quyền sửa sản phẩm này!");
            return "redirect:/giohang";
        }

        // Nếu số lượng = 0 thì xóa hẳn, không âm thầm bỏ qua
        if (soLuong <= 0) {
            gioHangService.xoaChiTiet(id);
        } else {
            gioHangService.capNhatSoLuong(id, soLuong);
        }

        return "redirect:/giohang";
    }

}
