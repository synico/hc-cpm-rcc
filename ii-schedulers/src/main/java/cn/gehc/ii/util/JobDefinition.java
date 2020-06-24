package cn.gehc.ii.util;

public enum JobDefinition {

    JOB_ID("job_id"),
    COLUMN1("column1"),
    COLUMN2("column2"),
    COLUMN3("column3"),
    COLUMN4("column4"),
    COLUMN5("column5"),
    COLUMN6("column6"),
    COLUMN7("column7"),
    COLUMN8("column8"),
    COLUMN9("column9"),
    JOB_NAME("triggerName"),
    JOB_TYPE("job_type"),
    JOB_GROUP("triggerGroup"),
    LAST_FIRE_TIME("scheduledFireTime"),
    IS_ACTIVE("is_active");

    private String code;

    JobDefinition(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
