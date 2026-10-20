package com.woori.ajs.imgmock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageRequest {

    @JsonProperty("inpt_task_id")
    private String inptTaskId;

    @JsonProperty("inpt_mst_srno")
    private String inptMstSrno;

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
}
