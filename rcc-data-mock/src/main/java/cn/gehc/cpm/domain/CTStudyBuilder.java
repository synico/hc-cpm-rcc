package cn.gehc.cpm.domain;

import org.springframework.util.Assert;

import java.util.*;

import cn.gehc.cpm.utils.SerieConstant.*;

public class CTStudyBuilder {

    public static List<List<CTSerie>> serieListByStudy = new ArrayList<>();

    public static List<CTSerie> of(Study study, Long theSerieId) {
        Random random = new Random();
        List<CTSerie> serieList = serieListByStudy.get(random.nextInt(serieListByStudy.size()));
        Long serieId = theSerieId;
        String aet = study.getStudyKey().getAet();
        SerieKey serieKey;
        for(CTSerie ctSerie : serieList) {
            serieId++;
            serieKey = new SerieKey();
            serieKey.setAet(aet);
            serieKey.setId(serieId);
            ctSerie.setSerieKey(serieKey);

            ctSerie.setDtLastUpdate(new Date());
            ctSerie.setLocalSerieId(aet + "|" + serieId);
            ctSerie.setLocalStudyKey(study.getLocalStudyId());
        }
        return serieList;
    }

    private static List<CTSerie> generateCTSeries(CT_SERIE_TYPE ...seriesType) {
        Assert.notEmpty(seriesType, "Need to specify serie type");
        List<CTSerie> serieList = new ArrayList<>(seriesType.length);
        for(CT_SERIE_TYPE serieType : seriesType) {
            CTSerie serie = createSerie(serieType);
            serieList.add(serie);
        }
        return serieList;
    }

    private static final CTSerie createSerie(CT_SERIE_TYPE serieType) {
        CTSerie ctSerie = new CTSerie();
        ctSerie.setDType(serieType.getSerieType());
        ctSerie.setIsRepeated(Boolean.FALSE);

        switch (serieType) {
            case CONSTANT_ANGLE:
                ctSerie.setSeriesDescription(generateRandomValue(CT_SERIE_DESCRIPTION.CONSTANT_ANGLE));
                ctSerie.setTargetRegion(generateRandomValue(CT_TARGET_REGION.CONSTANT_ANGLE));
                break;
            case SEQUENCED:
                ctSerie.setSeriesDescription(generateRandomValue(CT_SERIE_DESCRIPTION.SEQUENCED));
                ctSerie.setTargetRegion(generateRandomValue(CT_TARGET_REGION.SEQUENCED));
                break;
            case STATIONARY:
                ctSerie.setSeriesDescription(generateRandomValue(CT_SERIE_DESCRIPTION.STATIONARY));
                ctSerie.setTargetRegion(generateRandomValue(CT_TARGET_REGION.STATIONARY));
                break;
            case SPIRAL:
                ctSerie.setSeriesDescription(generateRandomValue(CT_SERIE_DESCRIPTION.SPIRAL));
                ctSerie.setTargetRegion(generateRandomValue(CT_TARGET_REGION.SPIRAL));
                break;
            case CINE:
                ctSerie.setSeriesDescription(generateRandomValue(CT_SERIE_DESCRIPTION.CINE));
                ctSerie.setTargetRegion(generateRandomValue(CT_TARGET_REGION.CINE));
                break;
        }

        return ctSerie;
    }

    private static String generateRandomValue(List<String> seed) {
        Random random = new Random();
        return seed.get(random.nextInt(seed.size()));
    }

    static {
        // study with 3 series
        serieListByStudy.add(generateCTSeries(CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.SEQUENCED,
                CT_SERIE_TYPE.SEQUENCED));
        serieListByStudy.add(generateCTSeries(CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.STATIONARY,
                CT_SERIE_TYPE.STATIONARY));
        serieListByStudy.add(generateCTSeries(CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.SPIRAL,
                CT_SERIE_TYPE.SPIRAL));
        serieListByStudy.add(generateCTSeries(CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.SPIRAL));
        serieListByStudy.add(generateCTSeries(CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.SPIRAL,
                CT_SERIE_TYPE.STATIONARY));
        // study with 4 series
        serieListByStudy.add(generateCTSeries(CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.STATIONARY,
                CT_SERIE_TYPE.STATIONARY,
                CT_SERIE_TYPE.STATIONARY));
        serieListByStudy.add(generateCTSeries(CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.SPIRAL,
                CT_SERIE_TYPE.STATIONARY));
        serieListByStudy.add(generateCTSeries(CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.STATIONARY,
                CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.SPIRAL));
        serieListByStudy.add(generateCTSeries(CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.STATIONARY,
                CT_SERIE_TYPE.STATIONARY,
                CT_SERIE_TYPE.SPIRAL));
        serieListByStudy.add(generateCTSeries(CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.STATIONARY,
                CT_SERIE_TYPE.STATIONARY,
                CT_SERIE_TYPE.STATIONARY));
        // study with 5 series
        serieListByStudy.add(generateCTSeries(CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.STATIONARY,
                CT_SERIE_TYPE.STATIONARY,
                CT_SERIE_TYPE.STATIONARY));
        serieListByStudy.add(generateCTSeries(CT_SERIE_TYPE.CINE,
                CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.SPIRAL,
                CT_SERIE_TYPE.STATIONARY));
        serieListByStudy.add(generateCTSeries(CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.SPIRAL,
                CT_SERIE_TYPE.STATIONARY,
                CT_SERIE_TYPE.STATIONARY));
        serieListByStudy.add(generateCTSeries(CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.SPIRAL,
                CT_SERIE_TYPE.SPIRAL,
                CT_SERIE_TYPE.SPIRAL,
                CT_SERIE_TYPE.STATIONARY));
        serieListByStudy.add(generateCTSeries(CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.CONSTANT_ANGLE,
                CT_SERIE_TYPE.STATIONARY,
                CT_SERIE_TYPE.STATIONARY,
                CT_SERIE_TYPE.STATIONARY));
    }

}
