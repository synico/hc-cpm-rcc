package cn.gethc.ii.util;

import cn.gehc.ii.util.DataUtil;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 212706300
 */

public class DataUtilTest {

    private static final Logger log = LoggerFactory.getLogger(DataUtilTest.class);

    private static final Map<String, Object> props = new HashMap<String, Object>();

    @Before
    public void prepareProps() {
        props.put("string", "value1");
        props.put("long", "12");
        props.put("integer", "4");
        props.put("double", "2.111");
        props.put("float", "3.111");
        props.put("date", "2020-06-16 17:46:20");
    }

    @Test
    public void testGetValueFromProps() {
        String str = DataUtil.getValueFromProperties(props, "string", String.class);
        assert (str instanceof String) : "Failed to get String";
        Long longVal = DataUtil.getValueFromProperties(props, "long", Long.class);
        assert (longVal instanceof Long) : "Failed to get Long";
        Integer intVal = DataUtil.getValueFromProperties(props, "integer", Integer.class);
        assert (intVal instanceof Integer) : "Failed to get Integer";
        Double doubleVal = DataUtil.getValueFromProperties(props, "double", Double.class);
        assert (doubleVal instanceof Double) : "Failed to get Double";
        Float floatVal = DataUtil.getValueFromProperties(props, "float", Float.class);
        assert (floatVal instanceof Float) : "Failed to get Float";
        java.util.Date dateVal = DataUtil.getValueFromProperties(props, "date", java.util.Date.class);
        assert (dateVal instanceof java.util.Date) : "Failed to get Date";
    }

    @Test
    public void testConvertDate2String() {
        java.util.Date now = new java.util.Date();
        log.info(DataUtil.convertDate2String(now, "yyyy-MM-dd HH:mm:ss"));
        log.info("now instant: {}", Instant.now());
    }

}
