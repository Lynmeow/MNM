package com.mypham.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mypham.model.DonHang;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Integer> {

    // Tìm đơn hàng theo userId
    List<DonHang> findByUserId(Integer userId);

    // Tìm đơn hàng theo trạng thái
    List<DonHang> findByTrangThai(String trangThai);

    // Tìm đơn hàng theo userId và sắp xếp theo ngày tạo giảm dần
    List<DonHang> findByUserIdOrderByNgayTaoDesc(Integer userId);

    List<DonHang> findAllByOrderByNgayTaoDesc();
}
