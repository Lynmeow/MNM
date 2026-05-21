package com.mypham.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mypham.model.TaiKhoan;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Integer> {

    TaiKhoan findByTenDangNhap(String tenDangNhap);

    public boolean existsByTenDangNhap(String tenDangNhap);

}
