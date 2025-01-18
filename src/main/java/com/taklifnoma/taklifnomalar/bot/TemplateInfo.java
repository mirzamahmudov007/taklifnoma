package com.taklifnoma.taklifnomalar.bot;

import lombok.Getter;
import lombok.AllArgsConstructor;

public class TemplateInfo {
    private String name;
    private String previewUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public TemplateInfo() {
    }

    public TemplateInfo(String name, String previewUrl) {
        this.name = name;
        this.previewUrl = previewUrl;
    }
}
