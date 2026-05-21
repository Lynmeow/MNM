package com.mypham.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mypham.repository.DanhMucRepository;

import java.util.List;

import com.mypham.model.DanhMuc;
import com.mypham.model.TaiKhoan;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private DanhMucRepository danhMucRepo;

    // Hiển thị trang chủ và thông tin người dùng nếu đã đăng nhập
    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        // Lấy tất cả danh mục từ cơ sở dữ liệu
        List<DanhMuc> danhMucs = danhMucRepo.findAll();
        model.addAttribute("danhMucs", danhMucs);  // Thêm danh mục vào model

        // Lấy thông tin người dùng từ session
        TaiKhoan user = (TaiKhoan) session.getAttribute("user");

        // Nếu người dùng đã đăng nhập, truyền vào model
        if (user != null) {
            model.addAttribute("user", user);  // Truyền thông tin người dùng vào model
        }

        // Trả về trang chủ (index.html)
        return "index";
    }

    @GetMapping("/gioithieu")
    public String GioiThieu() {
        return "gioithieu";  // Trang giới thiệu
    }

    @GetMapping("/lienhe")
    public String LiênHe() {
        return "lienhe";  // Trang giới thiệu
    }
}
