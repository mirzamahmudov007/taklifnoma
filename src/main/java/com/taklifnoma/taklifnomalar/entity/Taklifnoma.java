package com.taklifnoma.taklifnomalar.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalTime;

@NoArgsConstructor
@Entity
@Table(name = "taklifnomalar")
public class Taklifnoma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private String template;

    // To'y ma'lumotlari
    private String kuyovIsmi;
    private String kelinIsmi;
    private LocalTime nikohVaqti;
    private boolean ayollarToyOshi;
    private boolean erkaklarToyOshi;
    private LocalTime ayollarToyOshiVaqti;
    private LocalTime erkaklarToyOshiVaqti;

    // Tug'ilgan kun ma'lumotlari
    private String tugulganKunEgasi;
    private Integer yosh;
    private LocalTime tadbirVaqti;

    // Tabriknoma ma'lumotlari
    private String tabrikMatni;

    // Umumiy ma'lumotlar
    private String manzil;
    private Double longitude;
    private Double latitude;
    private String buyurtmachiId;
    private String paymentReceiptPath;

    @Enumerated(EnumType.STRING)
    private TaklifnomaStatus status = TaklifnomaStatus.TEKSHIRILMOQDA;

    public enum TaklifnomaStatus {
        TEKSHIRILMOQDA,
        TOPSHIRILDI,
        ATKAZ_QILINDI
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getKuyovIsmi() {
        return kuyovIsmi;
    }

    public void setKuyovIsmi(String kuyovIsmi) {
        this.kuyovIsmi = kuyovIsmi;
    }

    public String getKelinIsmi() {
        return kelinIsmi;
    }

    public void setKelinIsmi(String kelinIsmi) {
        this.kelinIsmi = kelinIsmi;
    }

    public LocalTime getNikohVaqti() {
        return nikohVaqti;
    }

    public void setNikohVaqti(LocalTime nikohVaqti) {
        this.nikohVaqti = nikohVaqti;
    }

    public boolean isAyollarToyOshi() {
        return ayollarToyOshi;
    }

    public void setAyollarToyOshi(boolean ayollarToyOshi) {
        this.ayollarToyOshi = ayollarToyOshi;
    }

    public boolean isErkaklarToyOshi() {
        return erkaklarToyOshi;
    }

    public void setErkaklarToyOshi(boolean erkaklarToyOshi) {
        this.erkaklarToyOshi = erkaklarToyOshi;
    }

    public LocalTime getAyollarToyOshiVaqti() {
        return ayollarToyOshiVaqti;
    }

    public void setAyollarToyOshiVaqti(LocalTime ayollarToyOshiVaqti) {
        this.ayollarToyOshiVaqti = ayollarToyOshiVaqti;
    }

    public LocalTime getErkaklarToyOshiVaqti() {
        return erkaklarToyOshiVaqti;
    }

    public void setErkaklarToyOshiVaqti(LocalTime erkaklarToyOshiVaqti) {
        this.erkaklarToyOshiVaqti = erkaklarToyOshiVaqti;
    }

    public String getTugulganKunEgasi() {
        return tugulganKunEgasi;
    }

    public void setTugulganKunEgasi(String tugulganKunEgasi) {
        this.tugulganKunEgasi = tugulganKunEgasi;
    }

    public Integer getYosh() {
        return yosh;
    }

    public void setYosh(Integer yosh) {
        this.yosh = yosh;
    }

    public LocalTime getTadbirVaqti() {
        return tadbirVaqti;
    }

    public void setTadbirVaqti(LocalTime tadbirVaqti) {
        this.tadbirVaqti = tadbirVaqti;
    }

    public String getTabrikMatni() {
        return tabrikMatni;
    }

    public void setTabrikMatni(String tabrikMatni) {
        this.tabrikMatni = tabrikMatni;
    }

    public String getManzil() {
        return manzil;
    }

    public void setManzil(String manzil) {
        this.manzil = manzil;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getBuyurtmachiId() {
        return buyurtmachiId;
    }

    public void setBuyurtmachiId(String buyurtmachiId) {
        this.buyurtmachiId = buyurtmachiId;
    }

    public String getPaymentReceiptPath() {
        return paymentReceiptPath;
    }

    public void setPaymentReceiptPath(String paymentReceiptPath) {
        this.paymentReceiptPath = paymentReceiptPath;
    }

    public TaklifnomaStatus getStatus() {
        return status;
    }

    public void setStatus(TaklifnomaStatus status) {
        this.status = status;
    }
}

