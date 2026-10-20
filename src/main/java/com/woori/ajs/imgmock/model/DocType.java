package com.woori.ajs.imgmock.model;

import java.awt.Color;

public enum DocType {
    BL("01", "B/L", new Color(37, 99, 235)),
    INVOICE("02", "Invoice", new Color(22, 163, 74)),
    PACKING_LIST("03", "Packing List", new Color(234, 88, 12)),
    CERTIFICATE("04", "Certificate", new Color(168, 85, 247)),
    INSURANCE("05", "Insurance", new Color(14, 165, 233)),
    CO("06", "C/O", new Color(147, 51, 234)),
    NEGO("07", "Nego", new Color(217, 119, 6)),
    BE("08", "B/E", new Color(107, 114, 128)),
    CONTRACT("09", "Contract", new Color(79, 70, 229)),
    LC("10", "L/C", new Color(220, 38, 38)),
    SHIPMENT("11", "Shipment", new Color(5, 150, 105)),
    ETC("12", "ETC", new Color(100, 116, 139)),
    SWIFT("13", "Swift", new Color(15, 118, 110)),
    OTHER("99", "Document", new Color(100, 116, 139));

    private final String code;
    private final String label;
    private final Color accent;

    DocType(String code, String label, Color accent) {
        this.code = code;
        this.label = label;
        this.accent = accent;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public Color getAccent() {
        return accent;
    }

    public static DocType fromCode(String code) {
        for (DocType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return OTHER;
    }
}
