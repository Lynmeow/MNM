package com.mypham.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mypham.model.DonHang;
import com.mypham.model.DonHangChiTiet;
import com.mypham.repository.DonHangChiTietRepository;
import com.mypham.repository.DonHangRepository;

import jakarta.transaction.Transactional;

@Service
public class DonHangService {

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private DonHangChiTietRepository donHangChiTietRepository;

    // Lấy tất cả đơn hàng của 1 user
    public List<DonHang> getDonHangByUserId(Integer userId) {
        return donHangRepository.findByUserId(userId);
    }

    // Lấy chi tiết 1 đơn hàng theo ID
    public DonHang getDonHangById(Integer id) {
        return donHangRepository.findById(id).orElse(null);
    }

    // Lấy tất cả đơn hàng (admin) - sắp xếp theo ngày tạo mới nhất
    public List<DonHang> getAllDonHang() {
        return donHangRepository.findAllByOrderByNgayTaoDesc();
    }

    // Lấy đơn hàng theo trạng thái
    public List<DonHang> getDonHangByTrangThai(String trangThai) {
        return donHangRepository.findByTrangThai(trangThai);
    }

    // Cập nhật trạng thái đơn hàng
    public void updateTrangThai(Integer id, String trangThai) {
        DonHang donHang = donHangRepository.findById(id).orElse(null);
        if (donHang != null) {
            donHang.setTrangThai(trangThai);
            donHangRepository.save(donHang);
        }
    }

    // Lấy chi tiết đơn hàng
    public List<DonHangChiTiet> getChiTietDonHang(Integer donHangId) {
        return donHangChiTietRepository.findByDonHangId(donHangId);
    }

    // Xóa đơn hàng
    @Transactional
    public void deleteDonHang(Integer id) {
        // Bước 1: Xóa tất cả chi tiết đơn hàng trước
        donHangChiTietRepository.deleteByDonHangId(id);

        // Bước 2: Xóa đơn hàng
        donHangRepository.deleteById(id);
    }

    // Hủy đơn hàng
    public void cancelOrder(Integer id) {
        DonHang dh = getDonHangById(id);
        if (dh != null) {
            dh.setTrangThai("Đã hủy");
            // nếu cần: hoàn tồn kho tại đây
            donHangRepository.save(dh);
        }
    }

    // ===== LƯU ĐƠN HÀNG VÀ CHI TIẾT =====
    // Lưu đơn hàng (trả về đối tượng đã lưu)
    @Transactional
    public DonHang saveDonHang(DonHang donHang) {
        return donHangRepository.save(donHang);
    }

    // Lưu chi tiết đơn hàng
    @Transactional
    public void saveChiTiet(DonHangChiTiet chiTiet) {
        donHangChiTietRepository.save(chiTiet);
    }

    // Lưu đơn hàng kèm chi tiết (dùng cho tạo đơn hàng mới)
    @Transactional
    public DonHang saveDonHangWithChiTiet(DonHang donHang, List<DonHangChiTiet> chiTietList) {
        // Lưu đơn hàng trước để có ID
        DonHang savedDonHang = donHangRepository.save(donHang);

        // Sau đó lưu chi tiết với donHangId đã có
        for (DonHangChiTiet chiTiet : chiTietList) {
            chiTiet.setDonHangId(savedDonHang.getId());
            donHangChiTietRepository.save(chiTiet);
        }

        return savedDonHang;
    }
}
