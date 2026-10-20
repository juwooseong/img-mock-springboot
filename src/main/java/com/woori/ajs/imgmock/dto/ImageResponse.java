package com.woori.ajs.imgmock.dto;

public class ImageResponse {

    private String imgStr;

    public ImageResponse() {
    }

    public ImageResponse(String imgStr) {
        this.imgStr = imgStr;
    }

    public String getImgStr() {
        return imgStr;
    }

    public void setImgStr(String imgStr) {
        this.imgStr = imgStr;
    }
}
