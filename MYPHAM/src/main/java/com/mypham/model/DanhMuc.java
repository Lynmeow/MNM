package com.mypham.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "DanhMuc")

public class DanhMuc {
    public String getMaDM() {
        return maDM;
    }

    public void setMaDM(String maDM) {
        this.maDM = maDM;
    }

    public String getTenDM() {
        return tenDM;
    }

    public void setTenDM(String tenDM) {
        this.tenDM = tenDM;
    }

    public List<SanPham> getSanphams() {
        return sanphams;
    }

    public void setSanphams(List<SanPham> sanphams) {
        this.sanphams = sanphams;
    }

    @Id
    @Column(length = 10)
    private String maDM;
    private String tenDM;

    @OneToMany(mappedBy = "danhmuc")
    private List<SanPham> sanphams;

}
