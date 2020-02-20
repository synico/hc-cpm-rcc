package cn.gehc.cpm.domain;

import cn.gehc.cpm.utils.MRStudyUtils;
import cn.gehc.cpm.utils.SerieConstant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MRStudyBuilder {

    public static List<MRSerie> of(Study study, Long theSerieId) {
        Random random = new Random();
        List<MRSerie> serieList = new ArrayList<>();
        Long serieId = theSerieId;
        String aet = study.getStudyKey().getAet();
        Integer numOfSeries = study.getTargetRegionCount();
        MRSerie mrSerie;
        SerieKey serieKey;
        for(int i = 0; i < numOfSeries; i++) {
            serieId++;
            mrSerie = new MRSerie();
            serieKey = new SerieKey();
            serieKey.setAet(aet);
            serieKey.setId(serieId);
            mrSerie.setSerieKey(serieKey);

            mrSerie.setDType(SerieConstant.MR_SERIE_TYPE);
            mrSerie.setDtLastUpdate(new Date());

            mrSerie.setLocalSerieId(aet + "|" + serieId);
            mrSerie.setLocalStudyKey(study.getLocalStudyId());

            Long protocolKey = MRStudyUtils.generateSerieProtocolKey();
            mrSerie.setProtocolKey(protocolKey);
            mrSerie.setProtocolName(MRStudyUtils.generateSerieProtocolName(protocolKey));

            mrSerie.setSeriesDescription(MRStudyUtils.generateSerieDescription());

            mrSerie.setSeriesDate(new Date());

            serieList.add(mrSerie);
        }
        return serieList;
    }

}
