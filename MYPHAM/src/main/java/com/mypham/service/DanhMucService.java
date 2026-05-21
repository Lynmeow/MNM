package com.mypham.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.mypham.repository.DanhMucRepository;
import com.mypham.model.DanhMuc;  // Đảm bảo import đúng lớp DanhMuc

@Service
public class DanhMucService {

    @Autowired
    private DanhMucRepository danhMucRepository;

    // Phương thức để lấy tất cả danh mục
    public List<DanhMuc> getAllDanhMuc() {
        return danhMucRepository.findAll();  // Trả về tất cả danh mục từ repository
    }

    public DanhMuc findByMaDM(String maDM) {
        return danhMucRepository.findByMaDM(maDM);  // Gọi phương thức từ repository
    }
}
