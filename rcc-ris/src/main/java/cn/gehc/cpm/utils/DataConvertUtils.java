package cn.gehc.cpm.utils;

import org.apache.commons.lang3.StringUtils;

public class DataConvertUtils {

    public static boolean isBlankField(Object obj) {
        if(StringUtils.isBlank(String.valueOf(obj)) || "null".equals(String.valueOf(obj))) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNotBlankField(Object obj) {
        if(StringUtils.isBlank(String.valueOf(obj)) || "null".equals(String.valueOf(obj))) {
            return false;
        } else {
            return true;
        }
    }

}
