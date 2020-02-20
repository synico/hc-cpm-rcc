package cn.gehc.cpm.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MRStudyUtils {

    private static final Integer TRY_NUM = 10;

    public static Integer generateNumSeries() {
        // range [3, 8]
        int numSeries = 3;
        Random random = new Random();
        for(int i = 0; i < TRY_NUM; i++) {
            int tmp = random.nextInt(9);
            if(tmp > 2) {
                numSeries = tmp;
                break;
            }
        }
        return numSeries;
    }

    public static Long generateProtocolKey() {
        List<Long> protocolKeys = new ArrayList<>();
        protocolKeys.addAll(StudyConstant.MR_PROTOCOLS.keySet());
        Random random = new Random();
        int index = random.nextInt(protocolKeys.size());
        return protocolKeys.get(index);
    }

    public static Long generateSerieProtocolKey() {
        List<Long> protocolKeys = new ArrayList<>();
        protocolKeys.addAll(SerieConstant.MR_SERIE_PROTOCOLS.keySet());
        Random random = new Random();
        int index = random.nextInt(protocolKeys.size());
        return protocolKeys.get(index);
    }

    public static String generateSerieProtocolName(Long protocolKey) {
        return SerieConstant.MR_SERIE_PROTOCOLS.get(protocolKey);
    }

    public static String generateSerieDescription() {
        Random random = new Random();
        int index = random.nextInt(SerieConstant.MR_SERIE_DESCRIPTIONS.size());
        return SerieConstant.MR_SERIE_DESCRIPTIONS.get(index);
    }

    public static void main(String args[]) {
        System.out.println(generateSerieProtocolKey());
    }

}
