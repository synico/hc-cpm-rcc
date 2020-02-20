package cn.gehc.cpm.utils;

import java.util.*;

public class CTStudyUtils {

    private static final Integer TRY_NUM = 10;

    public static Integer generateNumSeries() {
        // range [2, 5]
        int numSeries = 2;
        Random random = new Random();
        for(int i = 0; i < TRY_NUM; i++) {
            int tmp = random.nextInt(6);
            if(tmp > 1) {
                numSeries = tmp;
                break;
            }
        }
        return numSeries;
    }

    public static Long generateProtocolKey() {
        List<Long> protocolKeys = new ArrayList<>();
        protocolKeys.addAll(StudyConstant.CT_PROTOCOLS.keySet());
        Random random = new Random();
        int index = random.nextInt(protocolKeys.size());
        return protocolKeys.get(index);
    }

    public static void main(String args[]) {
        for(int i = 0; i < TRY_NUM; i++) {
            System.out.println(generateProtocolKey());
        }
    }
}
