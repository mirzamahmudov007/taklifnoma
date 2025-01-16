package com.taklifnoma.taklifnomalar.dto;

import com.taklifnoma.taklifnomalar.entity.Taklifnoma.TaklifnomaStatus;
import lombok.Data;


public class TaklifnomaDTO {
    private Long id;
    private String buyurtmachiId;
    private String type;
    private TaklifnomaStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBuyurtmachiId() {
        return buyurtmachiId;
    }

    public void setBuyurtmachiId(String buyurtmachiId) {
        this.buyurtmachiId = buyurtmachiId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TaklifnomaStatus getStatus() {
        return status;
    }

    public void setStatus(TaklifnomaStatus status) {
        this.status = status;
    }
}