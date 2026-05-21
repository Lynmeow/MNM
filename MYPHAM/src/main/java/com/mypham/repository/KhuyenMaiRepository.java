package com.mypham.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mypham.model.KhuyenMai;

public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, Integer> {
    KhuyenMai findByMaKM(String maKM);
}
