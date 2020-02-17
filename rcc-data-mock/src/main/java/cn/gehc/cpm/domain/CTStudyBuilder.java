package cn.gehc.cpm.domain;

import cn.gehc.cpm.utils.DeviceConstant;
import cn.gehc.cpm.utils.SerieConstant;

import java.util.List;

import cn.gehc.cpm.utils.DeviceConstant.AE;

public class CTStudyBuilder {

    public void initSerieMap() {
        // init SpiralSerie
        CTSerie spiralSerie = new CTSerie();

        // init StationarySerie
        CTSerie stationarySerie = new CTSerie();

        // init CineSerie
        CTSerie cineSerie = new CTSerie();

        // init SmartPrepSerie
        CTSerie smartPrepSerie = new CTSerie();

        // init SequencedSerie
        CTSerie sequenceSerie = new CTSerie();

    }

    private AE initAE() {
        AE ae = AE.CT1;
        Long index = System.currentTimeMillis() % 3;
        switch (index.intValue()) {
            case 0:
                ae = AE.CT1;
            case 1:
                ae = AE.CT2;
            case 2:
                ae = AE.CT3;
        }
        return ae;
    }

    private void initConstantAngleSerie() {
        CTSerie serie = new CTSerie();
        serie.setDType(SerieConstant.CT_SERIE_TYPE.CONSTANT_ANGLE.getSerieType());
    }

    public static void main(String args[]) {
        for(int i = 0; i < 100; i++) {
            long index = System.currentTimeMillis();
            System.out.println(index%3);
        }
    }


}
