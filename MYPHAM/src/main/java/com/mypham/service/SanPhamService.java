package com.mypham.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mypham.model.SanPham;
import com.mypham.repository.SanPhamRepository;

import jakarta.transaction.Transactional;  // Đảm bảo import đúng lớp sp

@Service
public class SanPhamService {

    @Autowired
    private SanPhamRepository sanphamRepository; // Inject repository để truy vấn cơ sở dữ liệu

    public List<SanPham> getAllSanphams() {
        return sanphamRepository.findAll(); // Trả về danh sách tất cả sản phẩm từ cơ sở dữ liệu
    }

    public List<SanPham> TimKiemSanPham(String q) {
        return sanphamRepository.findByTenSPContainingIgnoreCase(q);
    }

    public void save(SanPham sanpham) {
        sanphamRepository.save(sanpham);
    }

    @Transactional
    public void delete(String maSP) {
        sanphamRepository.deleteByMaSP(maSP);
    }

    public SanPham findByMaSP(String maSP) {
        return sanphamRepository.findByMaSP(maSP);  // Truy vấn sản phẩm theo mã SP
    }

    public List<SanPham> findAll() {
        return sanphamRepository.findAll();  // Trả về danh sách tất cả sản phẩm
    }

    public List<SanPham> getSanPhamByDanhMuc(String maDM) {
        return sanphamRepository.findBydanhmuc_MaDM(maDM);
    }
}
