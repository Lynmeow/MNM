package com.mypham.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mypham.service.DanhMucService;
 // Đảm bảo import đúng lớp service
@Controller
public class DanhMucController {

    @Autowired
    private DanhMucService danhMucService;  // Inject DanhMucService

    @GetMapping("/danhmuc/danhsach")
    public String danhSachDanhMuc(Model model) {
        // Gọi phương thức getAllDanhMuc và thêm vào model
        model.addAttribute("danhMucs", danhMucService.getAllDanhMuc());
        return "danhmuc/danhsach";  // Trả về view danh-sach-danhmuc
    }
}
