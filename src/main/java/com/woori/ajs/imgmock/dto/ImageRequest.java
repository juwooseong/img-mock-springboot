package com.woori.ajs.imgmock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageRequest {

    @JsonProperty("inpt_task_id")
    private String inptTaskId;

    @JsonProperty("inpt_mst_srno")
    private String inptMstSrno;

    /** SWIFT 전문 여부 (aisanctions: 문서분류코드 01 이면 Y) */
    @JsonProperty("swift_yn")
    private String swiftYn;

    public String getInptTaskId() {
        return inptTaskId;
    }

    public void setInptTaskId(String inptTaskId) {
        this.inptTaskId = inptTaskId;
    }

    public String getInptMstSrno() {
        return inptMstSrno;
    }

    public void setInptMstSrno(String inptMstSrno) {
        this.inptMstSrno = inptMstSrno;
    }

    public String getSwiftYn() {
        return swiftYn;
    }

    public void setSwiftYn(String swiftYn) {
        this.swiftYn = swiftYn;
    }

    public boolean isSwift() {
        return "Y".equalsIgnoreCase(swiftYn == null ? null : swiftYn.trim());
    }
}
