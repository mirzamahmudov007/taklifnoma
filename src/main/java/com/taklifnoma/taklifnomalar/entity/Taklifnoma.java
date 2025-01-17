package com.taklifnoma.taklifnomalar.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "taklifnomalar")
public class Taklifnoma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private String template;
    private String kuyovIsmi;
    private String kelinIsmi;
    private String taklifQiluvchiIsmi; // Yangi qo'shilgan maydon
    private String manzil;
    private Double longitude;
    private Double latitude;
    private String buyurtmachiId;
    private String paymentReceiptPath;
    private boolean ayollarToyOshi;
    private boolean erkaklarToyOshi;
    private LocalTime ayollarToyOshiVaqti;
    private LocalTime erkaklarToyOshiVaqti;
    private LocalTime nikohVaqti;

    @Enumerated(EnumType.STRING)
    private TaklifnomaStatus status = TaklifnomaStatus.TEKSHIRILMOQDA;

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public void setKuyovIsmi(String kuyovIsmi) {
        this.kuyovIsmi = kuyovIsmi;
    }

    public void setKelinIsmi(String kelinIsmi) {
        this.kelinIsmi = kelinIsmi;
    }

    public void setTaklifQiluvchiIsmi(String taklifQiluvchiIsmi) {
        this.taklifQiluvchiIsmi = taklifQiluvchiIsmi;
    }

    public void setManzil(String manzil) {
        this.manzil = manzil;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setBuyurtmachiId(String buyurtmachiId) {
        this.buyurtmachiId = buyurtmachiId;
    }

    public void setPaymentReceiptPath(String paymentReceiptPath) {
        this.paymentReceiptPath = paymentReceiptPath;
    }

    public void setAyollarToyOshi(boolean ayollarToyOshi) {
        this.ayollarToyOshi = ayollarToyOshi;
    }

    public void setErkaklarToyOshi(boolean erkaklarToyOshi) {
        this.erkaklarToyOshi = erkaklarToyOshi;
    }

    public void setAyollarToyOshiVaqti(LocalTime ayollarToyOshiVaqti) {
        this.ayollarToyOshiVaqti = ayollarToyOshiVaqti;
    }

    public void setErkaklarToyOshiVaqti(LocalTime erkaklarToyOshiVaqti) {
        this.erkaklarToyOshiVaqti = erkaklarToyOshiVaqti;
    }

    public void setNikohVaqti(LocalTime nikohVaqti) {
        this.nikohVaqti = nikohVaqti;
    }

    public void setStatus(TaklifnomaStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTemplate() {
        return template;
    }

    public String getKuyovIsmi() {
        return kuyovIsmi;
    }

    public String getKelinIsmi() {
        return kelinIsmi;
    }

    public String getTaklifQiluvchiIsmi() {
        return taklifQiluvchiIsmi;
    }

    public String getManzil() {
        return manzil;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public String getBuyurtmachiId() {
        return buyurtmachiId;
    }

    public String getPaymentReceiptPath() {
        return paymentReceiptPath;
    }

    public boolean isAyollarToyOshi() {
        return ayollarToyOshi;
    }

    public boolean isErkaklarToyOshi() {
        return erkaklarToyOshi;
    }

    public LocalTime getAyollarToyOshiVaqti() {
        return ayollarToyOshiVaqti;
    }

    public LocalTime getErkaklarToyOshiVaqti() {
        return erkaklarToyOshiVaqti;
    }

    public LocalTime getNikohVaqti() {
        return nikohVaqti;
    }

    public TaklifnomaStatus getStatus() {
        return status;
    }

    public enum TaklifnomaStatus {
        TEKSHIRILMOQDA,
        TOPSHIRILDI,
        ATKAZ_QILINDI
    }
}

