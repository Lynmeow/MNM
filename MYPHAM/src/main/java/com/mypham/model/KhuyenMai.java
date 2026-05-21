package com.mypham.model;

import jakarta.persistence.*;

@Entity
@Table(name = "KhuyenMai")
public class KhuyenMai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String maKM;

    private Integer phanTram; // Ví dụ: 10 -> giảm 10%

    private Double giamToiDa; // 50000 -> giảm tối đa 50k

    private Integer soLuong; // số lượng mã còn

    private Boolean hoatDong;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMaKM() {
        return maKM;
    }

    public void setMaKM(String maKM) {
        this.maKM = maKM;
    }

    public Integer getPhanTram() {
        return phanTram;
    }

    public void setPhanTram(Integer phanTram) {
        this.phanTram = phanTram;
    }

    public Double getGiamToiDa() {
        return giamToiDa;
    }

    public void setGiamToiDa(Double giamToiDa) {
        this.giamToiDa = giamToiDa;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public Boolean getHoatDong() {
        return hoatDong;
    }

    public void setHoatDong(Boolean hoatDong) {
        this.hoatDong = hoatDong;
    }
}
