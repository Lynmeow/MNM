package com.mypham.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.mypham.model.DonHangChiTiet;

import jakarta.transaction.Transactional;

public interface DonHangChiTietRepository extends JpaRepository<DonHangChiTiet, Integer> {

    List<DonHangChiTiet> findByDonHangId(Integer donHangId);

    @Modifying
    @Transactional
    void deleteByDonHangId(Integer donHangId);
}
