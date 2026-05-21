package com.mypham.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // Import đúng Service

import com.mypham.model.DanhMuc;
import com.mypham.model.SanPham;
import com.mypham.repository.DanhMucRepository;
import com.mypham.repository.SanPhamRepository;
import com.mypham.service.SanPhamService;

@Controller
@RequestMapping("/sanpham")
public class SanPhamController {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private SanPhamService sphamService;
    @Autowired
    private DanhMucRepository danhMucRepository;  // Khai báo DanhMucRepository

    @ModelAttribute("danhMucs")
    public List<DanhMuc> getDanhMucs() {
        return danhMucRepository.findAll();
    }

    @GetMapping("/danhmuc/{maDM}")
    public String hienThiSanPhamTheoDanhMuc(@PathVariable("maDM") String maDM, Model model) {
        // Không cần thêm danhMucs nữa, @ModelAttribute đã tự động thêm
        List<SanPham> sanphams = sanPhamRepository.findBydanhmuc_MaDM(maDM);
        DanhMuc danhMuc = danhMucRepository.findByMaDM(maDM);

        if (danhMuc == null) {
            model.addAttribute("message", "Danh mục không tồn tại.");
            return "error";
        }

        model.addAttribute("maDM", maDM);
        model.addAttribute("tenDM", danhMuc.getTenDM());
        model.addAttribute("sanphams", sanphams);

        if (sanphams.isEmpty()) {
            model.addAttribute("message", "Không có sản phẩm nào trong danh mục này.");
        }

        return "danhmuc/danhsach";
    }

    @GetMapping("/chitiet/{maSP}")
    public String chitetSP(@PathVariable("maSP") String maSP, Model model) {
        SanPham sp = sanPhamRepository.findByMaSP(maSP);
        if (sp == null) {
            return "redirect:/sanpham/danhsach";
        }
        model.addAttribute("sp", sp);
        return "sanpham/chitiet";
    }

    @GetMapping("/danhsach")
    public String TimKiem(@RequestParam(value = "q", required = false) String q, Model model) {
        List<SanPham> sphams = sphamService.getAllSanphams(); // Lấy tất cả sản phẩm từ Service
        if (q != null && !q.isEmpty()) {
            sphams = sphamService.TimKiemSanPham(q); // Tìm kiếm sản phẩm theo từ khóa
        } else {
            sphams = sphamService.getAllSanphams(); // Lấy tất cả sản phẩm nếu không có tìm kiếm
        }
        model.addAttribute("sphams", sphams);
        model.addAttribute("q", q);// Truyền danh sách sản phẩm vào model
        return "sanpham/danhsach"; // Trả về view danh sách sản phẩm
    }
}
