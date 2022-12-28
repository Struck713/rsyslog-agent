package com.noah.syslog.message.enums;

public enum Encodings {

    UTF_8("UTF-8", "BOM");

    private String name;
    private String type;

    Encodings(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public String use(String message) {
        switch (this.name) {
            case "UTF-8":
            default: return this.type + " " + message;
        }
    }

}
