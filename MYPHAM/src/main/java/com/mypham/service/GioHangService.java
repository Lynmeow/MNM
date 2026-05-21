package com.mypham.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mypham.model.GioHang;
import com.mypham.model.GioHangChiTiet;
import com.mypham.repository.GioHangChiTietRepository;
import com.mypham.repository.GioHangRepository;
import com.mypham.repository.SanPhamRepository;
import java.util.List;
import java.util.Date;




@Service
public class GioHangService  {

@Autowired
SanPhamRepository sanPhamRepo;

@Autowired
GioHangRepository giohangrp;

@Autowired
GioHangChiTietRepository giohangctrp;

public void themVaoGio(Integer userId, String maSP) {

    GioHang gio = giohangrp.findByUserId(userId);

    // Nếu user chưa có giỏ -> tạo mới
    if (gio == null) {
        gio = new GioHang();
        gio.setUserId(userId);
        gio.setNgayTao(new Date());
        gio = giohangrp.save(gio);
    }

    // Kiểm tra sản phẩm có trong giỏ chưa
    GioHangChiTiet ct = giohangctrp.findByGioHangIdAndMaSP(gio.getId(), maSP);

    if (ct != null) {
        ct.setSoLuong(ct.getSoLuong() + 1);
    } else {
        ct = new GioHangChiTiet();
        ct.setGioHangId(gio.getId());
        ct.setMaSP(maSP);
        ct.setSoLuong(1);
    }

    giohangctrp.save(ct);
}

public GioHangChiTiet layChiTietById(Integer id) {
    return giohangctrp.findById(id).orElse(null);
}

public void xoaChiTiet(Integer id) {
    giohangctrp.deleteById(id);
}

public void capNhatSoLuong(Integer id, Integer soLuong) {
    GioHangChiTiet ct = giohangctrp.findById(id).orElse(null);
    if (ct != null && soLuong > 0) {
        ct.setSoLuong(soLuong);
        giohangctrp.save(ct);
    }
}

    public GioHang layGioHangTheoUser(Integer userId) {
        return giohangrp.findByUserId(userId);
    }

    public List<GioHangChiTiet> layChiTietGio(Integer gioHangId) {
        return giohangctrp.findByGioHangId(gioHangId); // ĐÚNG
    }


    public Double tinhTongTien(Integer Id) {
        List<GioHangChiTiet> list = giohangctrp.findByGioHangId(Id);
        double total = 0.0;

        for (GioHangChiTiet ct : list) {
            Double gia = sanPhamRepo.findById(ct.getMaSP())
                                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại: " + ct.getMaSP()))
                                .getGia();
            total += gia * ct.getSoLuong();
        }
        return total;
    }
}






