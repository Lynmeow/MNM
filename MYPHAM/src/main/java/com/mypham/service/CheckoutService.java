package com.mypham.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import com.mypham.model.DonHang;
import com.mypham.model.DonHangChiTiet;
import com.mypham.model.GioHang;
import com.mypham.model.GioHangChiTiet;
import com.mypham.model.KhuyenMai;
import com.mypham.model.SanPham;
import com.mypham.repository.DonHangChiTietRepository;
import com.mypham.repository.DonHangRepository;
import com.mypham.repository.GioHangChiTietRepository;
import com.mypham.repository.GioHangRepository;
import com.mypham.repository.KhuyenMaiRepository;
import com.mypham.repository.SanPhamRepository;

@Service
public class CheckoutService {

    @Autowired
    GioHangRepository gioHangRepo;

    @Autowired
    GioHangChiTietRepository giohangctRepo;

    @Autowired
    DonHangRepository donHangRepo;

    @Autowired
    DonHangChiTietRepository donHangCtRepo;

    @Autowired
    SanPhamRepository sanPhamRepo;

    @Autowired
    KhuyenMaiRepository khuyenMaiRepo;

    /**
     * Tạo đơn hàng mới từ giỏ hàng của người dùng
     *
     * @param userId ID của người dùng
     * @param diaChi Địa chỉ nhận hàng
     * @param method Phương thức thanh toán
     * @param maKM Mã giảm giá (nếu có)
     * @return Đơn hàng mới
     */
    @Transactional
    public DonHang taoDonHang(Integer userId, String diaChi, String phuongThuc, String maKM,
                               String hoTen, String soDienThoai, String ghiChu) {

        // Lấy giỏ hàng của người dùng
        GioHang gio = gioHangRepo.findByUserId(userId);
        List<GioHangChiTiet> list = giohangctRepo.findByGioHangId(gio.getId());

        // Tính tổng tiền + kiểm tra tồn kho
        Double tongTien = 0.0;

        for (GioHangChiTiet ct : list) {
            SanPham sanPham = sanPhamRepo.findById(ct.getMaSP()).orElse(null);
            if (sanPham == null) {
                throw new RuntimeException("Sản phẩm không tồn tại: " + ct.getMaSP());
            }
            if (sanPham.getSoLuong() < ct.getSoLuong()) {
                throw new RuntimeException("Sản phẩm \"" + sanPham.getTenSP()
                        + "\" không đủ hàng! Còn " + sanPham.getSoLuong() + " sản phẩm.");
            }
            tongTien += sanPham.getGia() * ct.getSoLuong();
        }

        Double giamGia = 0.0;

        // Kiểm tra mã giảm giá
        if (maKM != null && !maKM.isBlank()) {
            KhuyenMai km = khuyenMaiRepo.findByMaKM(maKM);

            // Nếu mã giảm giá hợp lệ và còn hiệu lực
            if (km != null && km.getHoatDong() && km.getSoLuong() > 0) {
                // Tính giảm giá
                giamGia = tongTien * km.getPhanTram() / 100;

                // Áp dụng giảm giá tối đa
                if (km.getGiamToiDa() != null && giamGia > km.getGiamToiDa()) {
                    giamGia = km.getGiamToiDa();
                }

                // Cập nhật số lượng mã khuyến mãi còn lại
                km.setSoLuong(km.getSoLuong() - 1);
                khuyenMaiRepo.save(km);  // Lưu cập nhật mã giảm giá
            } else {
                // Nếu mã không hợp lệ, không giảm giá
                giamGia = 0.0;
            }
        }

        // Tính tổng thanh toán sau giảm giá
        Double thanhToan = tongTien - giamGia;

        // Tạo đơn hàng mới
        DonHang dh = new DonHang();
        dh.setUserId(userId);
        dh.setDiaChi(diaChi);
        dh.setHoTen(hoTen);
        dh.setSoDienThoai(soDienThoai);
        dh.setGhiChu(ghiChu);
        dh.setPhuongThucThanhToan(phuongThuc);
        dh.setTongTien(tongTien);
        dh.setGiamGia(giamGia);
        dh.setThanhToan(thanhToan);
        dh.setNgayTao(new java.util.Date());
        dh.setTrangThai("Chờ xác nhận");

        dh = donHangRepo.save(dh);

        // Lưu chi tiết đơn hàng + trừ tồn kho
        for (GioHangChiTiet ct : list) {
            SanPham sanPham = sanPhamRepo.findById(ct.getMaSP())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại: " + ct.getMaSP()));

            DonHangChiTiet c = new DonHangChiTiet();
            c.setDonHangId(dh.getId());
            c.setMaSP(ct.getMaSP());
            c.setSoLuong(ct.getSoLuong());
            c.setDonGia(sanPham.getGia());
            c.setThanhTien(c.getDonGia() * c.getSoLuong());
            donHangCtRepo.save(c);

            // Trừ tồn kho
            sanPham.setSoLuong(sanPham.getSoLuong() - ct.getSoLuong());
            sanPhamRepo.save(sanPham);
        }

        // Xóa giỏ hàng sau khi đặt hàng
        giohangctRepo.deleteAll(list);

        return dh;
    }

    // Thêm phương thức trong CheckoutService để lấy thông tin khuyến mãi từ cơ sở dữ liệu
    public KhuyenMai layKhuyenMai(String maKM) {
        return khuyenMaiRepo.findByMaKM(maKM);  // Truy vấn mã giảm giá từ cơ sở dữ liệu
    }
    // Thêm phương thức trong CheckoutService để cập nhật thông tin khuyến mãi vào cơ sở dữ liệu

    public void capNhatKhuyenMai(KhuyenMai khuyenMai) {
        khuyenMaiRepo.save(khuyenMai);  // Lưu lại mã giảm giá đã thay đổi (số lượng mã giảm giá)
    }

}
