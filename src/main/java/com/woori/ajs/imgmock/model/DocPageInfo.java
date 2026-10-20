package com.woori.ajs.imgmock.model;

public class DocPageInfo {

    private final DocType docType;
    private final String page;

    public DocPageInfo(DocType docType, String page) {
        this.docType = docType;
        this.page = page;
    }

    public DocType getDocType() {
        return docType;
    }

    public String getPage() {
        return page;
    }
}
