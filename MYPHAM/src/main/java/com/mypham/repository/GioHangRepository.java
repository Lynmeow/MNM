package com.mypham.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mypham.model.GioHang;

public interface GioHangRepository extends JpaRepository <GioHang, Integer> {
    GioHang findByUserId(Integer userId);

}
