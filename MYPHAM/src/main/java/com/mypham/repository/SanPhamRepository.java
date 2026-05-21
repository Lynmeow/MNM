package com.mypham.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mypham.model.SanPham;

public interface SanPhamRepository extends JpaRepository<SanPham, String> {

    SanPham findByMaSP(String maSP);

    // Tìm sản phẩm theo mã danh mục
    List<SanPham> findBydanhmuc_MaDM(String maDM);

    // Tìm sản phẩm theo tên sản phẩm (chứa từ khóa tìm kiếm không phân biệt chữ hoa, chữ thường)
    List<SanPham> findByTenSPContainingIgnoreCase(String q);

    // Tìm sản phẩm theo mã sản phẩm
    // Tìm sản phẩm theo mô tả (chứa từ khóa tìm kiếm không phân biệt chữ hoa, chữ thường)
    List<SanPham> findByMaSPContainingIgnoreCase(String mota);

    // Tìm sản phẩm theo giá trong khoảng
    List<SanPham> findByGiaBetween(Double minGia, Double maxGia);

    // Xóa sản phẩm theo mã sản phẩm
    void deleteByMaSP(String maSP);

}
