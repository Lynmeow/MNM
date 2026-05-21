package com.mypham.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mypham.model.DanhMuc;

public interface DanhMucRepository extends JpaRepository<DanhMuc, String> {

    DanhMuc findByMaDM(String maDM);
}
