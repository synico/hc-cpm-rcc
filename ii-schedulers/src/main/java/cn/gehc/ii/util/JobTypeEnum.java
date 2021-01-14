package cn.gehc.ii.util;

public enum JobTypeEnum {

    YEARLY("YEARLY"),
    MONTHLY("MONTHLY"),
    DAILY("DAILY"),
    HOURLY("HOURLY");

    private String code;

    JobTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

}
