package com.taklifnoma.taklifnomalar.bot;

public class TemplateInfo {
    private String name;
    private String previewUrl;

    public TemplateInfo(String name, String previewUrl) {
        this.name = name;
        this.previewUrl = previewUrl;
    }

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


}