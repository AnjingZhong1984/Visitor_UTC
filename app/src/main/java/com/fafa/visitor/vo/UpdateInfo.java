package com.fafa.visitor.vo;

/**
 * Created by ZhouBing on 2017/7/27.
 * 程序更新信息
 */
public class UpdateInfo {

    private String versionName;
    private String url;
    private String description;

    public UpdateInfo() {
    }

    public UpdateInfo(String versionName, String url, String description) {
        this.versionName = versionName;
        this.url = url;
        this.description = description;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }
}
