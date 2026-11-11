package com.woori.ajs.imgmock.dto;

public class ImageResponse {

    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_TEXT = "text";

    /** type=image : JPEG Base64, type=text : SWIFT 전문 원문 */
    private String imgStr;

    /** 데이터 구분 (image / text) */
    private String type;

    public ImageResponse() {
    }

    public ImageResponse(String imgStr, String type) {
        this.imgStr = imgStr;
        this.type = type;
    }

    public static ImageResponse image(String base64) {
        return new ImageResponse(base64, TYPE_IMAGE);
    }

    public static ImageResponse text(String text) {
        return new ImageResponse(text, TYPE_TEXT);
    }

    public String getImgStr() {
        return imgStr;
    }

    public void setImgStr(String imgStr) {
        this.imgStr = imgStr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
