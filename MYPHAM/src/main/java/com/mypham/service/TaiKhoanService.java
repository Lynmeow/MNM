package com.mypham.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mypham.model.TaiKhoan;
import com.mypham.repository.TaiKhoanRepository;

@Service
public class TaiKhoanService {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ======= XỬ LÝ ĐĂNG NHẬP =======
    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhap(tenDangNhap);
        if (taiKhoan == null) {
            return null;
        }
        if (passwordEncoder.matches(matKhau, taiKhoan.getMatKhau())) {
            return taiKhoan;
        }
        return null;
    }

    // ======= KIỂM TRA TÊN ĐĂNG NHẬP ĐÃ TỒN TẠI =======
    public boolean tonTaiUsername(String tenDangNhap) {
        return taiKhoanRepository.existsByTenDangNhap(tenDangNhap);
    }

    // ======= XỬ LÝ ĐĂNG KÝ =======
    public void dangKy(TaiKhoan taiKhoan) {
        if (taiKhoan.getVaiTro() == null || taiKhoan.getVaiTro().isBlank()) {
            taiKhoan.setVaiTro("USER");
        }
        taiKhoan.setMatKhau(passwordEncoder.encode(taiKhoan.getMatKhau()));
        taiKhoanRepository.save(taiKhoan);
    }
}
