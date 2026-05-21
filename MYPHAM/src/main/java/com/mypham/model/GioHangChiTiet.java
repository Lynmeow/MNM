package com.mypham.model;

import jakarta.persistence.*;

@Entity
@Table(name = "GioHangChiTiet")
public class GioHangChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer gioHangId;

    @Column(length = 10)
    private String maSP;

    private Integer soLuong;

    // ====== LIÊN KẾT VỚI SANPHAM ======
    @ManyToOne
    @JoinColumn(name = "maSP", insertable = false, updatable = false)
    private SanPham sanPham; // field này dùng để lấy tênSP, giá, ảnh,...

    // ====== GETTER / SETTER ======

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGioHangId() {
        return gioHangId;
    }

    public void setGioHangId(Integer gioHangId) {
        this.gioHangId = gioHangId;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }
}
