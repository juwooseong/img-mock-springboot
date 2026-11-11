package com.woori.ajs.imgmock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "img-mock")
public class ImgMockProperties {

    private String imageDir = "images";
    private boolean cacheEnabled = true;
    /** images/{task_id}.txt 가 없을 때 쓰는 기본 SWIFT 전문 파일 (상대경로 시 실행 디렉터리 기준) */
    private String swiftSampleFile = "sample.txt";

    public String getSwiftSampleFile() {
        return swiftSampleFile;
    }

    public void setSwiftSampleFile(String swiftSampleFile) {
        this.swiftSampleFile = swiftSampleFile;
    }

    public String getImageDir() {
        return imageDir;
    }

    public void setImageDir(String imageDir) {
        this.imageDir = imageDir;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }
}
