package com.mypham.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mypham.model.GioHangChiTiet;
import java.util.List;


public interface GioHangChiTietRepository extends JpaRepository <GioHangChiTiet, Integer> {
List<GioHangChiTiet> findByGioHangId(Integer gioHangId);
GioHangChiTiet findByGioHangIdAndMaSP(Integer gioHangId, String maSP);

}
