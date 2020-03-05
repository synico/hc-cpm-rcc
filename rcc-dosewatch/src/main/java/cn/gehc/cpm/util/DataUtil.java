package cn.gehc.cpm.util;

import cn.gehc.cpm.domain.*;
import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

public class DataUtil {

    public enum DatabaseType {
        MYSQL,
        POSTGRES
    }

    private static final Logger log = LoggerFactory.getLogger(DataUtil.class);

    private static final DateTimeFormatter mysqlDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    private static final DateTimeFormatter pgDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final String LOCAL_TIME_ZONE = "Asia/Shanghai";

    public static String getStringFromProperties(Map<String, Object> props, String key) {
        String result = null;
        Object value = props.get(key);
        if(value != null) {
            result = value.toString();
        }
        return result;
    }

    public static Long getLongFromProperties(Map<String, Object> props, String key) {
        Long result = null;
        Object value = props.get(key);
        if(value != null) {
            result = Long.valueOf(value.toString());
        }
        return result;
    }

    public static Integer getIntegerFromProperties(Map<String, Object> props, String key) {
        Integer result = null;
        Object value = props.get(key);
        if(value != null) {
            result = Integer.valueOf(value.toString());
        }
        return result;
    }

    public static Double getDoubleFromProperties(Map<String, Object> props, String key) {
        Double result = null;
        Object value = props.get(key);
        if(value != null) {
            result = Double.valueOf(value.toString());
        }
        return result;
    }

    public static Float getFloatFromProperties(Map<String, Object> props, String key) {
        Float result = null;
        Object value = props.get(key);
        if(value != null) {
            result = Float.valueOf(value.toString());
        }
        return result;
    }

    public static Date getDateFromProperties(Map<String, Object> props, String key) {
        Date result = null;
        Object value = props.get(key);
        if(value != null) {
            LocalDateTime localDateTime = LocalDateTime.parse(value.toString(), mysqlDateFormatter);
            ZoneId localZone = ZoneId.of(LOCAL_TIME_ZONE);
            result = Date.from(localDateTime.atZone(localZone).toInstant());
        }
        return result;
    }

    public static String convertDate2String(Date date, DatabaseType dbType) {
        String result = null;
        if(date != null) {
            Instant instant = date.toInstant();
            ZoneId localZone = ZoneId.of(LOCAL_TIME_ZONE);
            if(dbType == DatabaseType.MYSQL) {
                result = mysqlDateFormatter.format(LocalDateTime.ofInstant(instant, localZone));
            } else {
                result = pgDateFormatter.format(LocalDateTime.ofInstant(instant, localZone));
            }
        }
        return result;
    }

    public static Study convertProps2Study(Map<String, Object> studyProps) {
        Study study = new Study();
        StudyKey studyKey = new StudyKey();

        studyKey.setId(getLongFromProperties(studyProps,"local_studyId"));
        studyKey.setAet(getStringFromProperties(studyProps, "aet"));
        studyKey.setModality(getStringFromProperties(studyProps, "modality"));
        study.setStudyKey(studyKey);

        study.setOrgId(getLongFromProperties(studyProps, "org_id"));
        String localStudyId = getLongFromProperties(studyProps, "org_id") + "|" +
                getStringFromProperties(studyProps, "local_study_id");
        study.setLocalStudyId(localStudyId);
        study.setAeKey(getIntegerFromProperties(studyProps, "ae_key"));
        study.setAccessionNumber(getStringFromProperties(studyProps, "accession_number"));
        study.setDType(getStringFromProperties(studyProps, "study_dtype"));
        study.setPatientId(getStringFromProperties(studyProps, "patient_id"));
        study.setPatientSex(getStringFromProperties(studyProps, "patient_sex"));
        study.setPatientAge(getIntegerFromProperties(studyProps, "patient_age"));
        study.setStudyId(getStringFromProperties(studyProps, "study_id"));
        study.setStudyInstanceUid(getStringFromProperties(studyProps, "study_instance_uid"));
        study.setStudyDate(getDateFromProperties(studyProps, "study_date"));
        study.setStudyDescKey(getIntegerFromProperties(studyProps, "sd_key"));
        study.setStudyDescription(getStringFromProperties(studyProps, "study_description"));
        study.setPerformingPhysician(getStringFromProperties(studyProps, "performing_physician"));
        study.setCreateTime(Date.from(Instant.now()));

        return study;
    }

    public static CTStudy convertProps2CTStudy(Map<String, Object> studyProps) {
        CTStudy ctStudy = new CTStudy();
        StudyKey studyKey = new StudyKey();

        studyKey.setId(getLongFromProperties(studyProps,"local_studyId"));
        studyKey.setAet(getStringFromProperties(studyProps, "aet"));
        studyKey.setModality(getStringFromProperties(studyProps, "modality"));
        ctStudy.setStudyKey(studyKey);

        String localStudyId = getStringFromProperties(studyProps, "org_id") + "|" +
                getStringFromProperties(studyProps, "local_study_id");
        ctStudy.setLocalStudyId(localStudyId);
        ctStudy.setDlpTotal(getDoubleFromProperties(studyProps, "ct_dose_length_product_total"));
        ctStudy.setDlpSSDE(getDoubleFromProperties(studyProps, "dlp_ssde"));
        ctStudy.setExamCTDI(getDoubleFromProperties(studyProps, "exam_ctdi"));
        ctStudy.setNumSeries(getIntegerFromProperties(studyProps, "num_series"));
        ctStudy.setProtocolKey(getLongFromProperties(studyProps, "protocol_key"));
        ctStudy.setProtocolName(getStringFromProperties(studyProps, "protocol_name"));
        ctStudy.setCreateTime(Date.from(Instant.now()));

        return ctStudy;
    }

    public static MRStudy convertProps2MRStudy(Map<String, Object> studyProps) {
        MRStudy mrStudy = new MRStudy();
        StudyKey studyKey = new StudyKey();

        studyKey.setId(getLongFromProperties(studyProps, "local_studyId"));
        studyKey.setAet(getStringFromProperties(studyProps, "aet"));
        studyKey.setModality(getStringFromProperties(studyProps, "modality"));
        mrStudy.setStudyKey(studyKey);

        mrStudy.setLocalStudyId(getStringFromProperties(studyProps, "local_study_id"));
        //For protocol key and protocol description will be retrieved from 1st serie

        mrStudy.setCreateTime(Date.from(Instant.now()));

        return mrStudy;
    }

    public static XAStudy convertProps2XAStudy(Map<String, Object> studyProps) {
        XAStudy xaStudy = new XAStudy();
        StudyKey studyKey = new StudyKey();

        studyKey.setId(getLongFromProperties(studyProps, "local_studyId"));
        studyKey.setAet(getStringFromProperties(studyProps, "aet"));
        studyKey.setModality(getStringFromProperties(studyProps, "modality"));
        xaStudy.setStudyKey(studyKey);

        xaStudy.setLocalStudyId(getStringFromProperties(studyProps, "local_study_id"));
        xaStudy.setDap(getDoubleFromProperties(studyProps, "dap"));
        xaStudy.setFluoroDap(getDoubleFromProperties(studyProps, "fluoro_dap"));
        xaStudy.setRecordDap(getDoubleFromProperties(studyProps, "record_dap"));
        xaStudy.setCreateTime(Date.from(Instant.now()));

        return xaStudy;
    }

    public static CTSerie convertProps2CTSerie(Map<String, Object> serieProps) {
        CTSerie ctSerie = new CTSerie();
        SerieKey serieKey = new SerieKey();

        serieKey.setId(getLongFromProperties(serieProps, "local_serieId"));
        serieKey.setAet(getStringFromProperties(serieProps, "aet"));
        ctSerie.setSerieKey(serieKey);

        String localStudyId = getStringFromProperties(serieProps, "org_id") + "|" +
                getStringFromProperties(serieProps, "local_study_id");
        ctSerie.setLocalStudyKey(localStudyId);
        ctSerie.setLocalSerieId(serieKey.toString());
        ctSerie.setSerieId(getStringFromProperties(serieProps, "serie_id"));
        ctSerie.setDType(getStringFromProperties(serieProps, "serie_dtype"));
        ctSerie.setStudyKey(getLongFromProperties(serieProps, "study_key"));
        ctSerie.setSeriesDate(getDateFromProperties(serieProps, "series_date"));
        ctSerie.setTargetRegion(getStringFromProperties(serieProps, "target_region_key"));
        ctSerie.setAapmFactorSSDE(getDoubleFromProperties(serieProps, "aapm_factor_ssde"));
        ctSerie.setCtdiVolSSDE(getDoubleFromProperties(serieProps, "ctdi_vol_ssde"));
        ctSerie.setDlpSSDE(getDoubleFromProperties(serieProps, "dlp_ssde"));
        ctSerie.setEndSeriesTime(getDateFromProperties(serieProps, "end_series_time"));
        ctSerie.setExposureTime(getFloatFromProperties(serieProps, "exposure_time"));
        ctSerie.setStartSliceLocation(getDoubleFromProperties(serieProps, "start_slice_location"));
        ctSerie.setEndSliceLocation(getDoubleFromProperties(serieProps, "end_slice_location"));
        ctSerie.setDlp(getDoubleFromProperties(serieProps, "dlp"));
        ctSerie.setSeriesDescription(getStringFromProperties(serieProps, "series_description"));
        ctSerie.setEffectiveDose(getDoubleFromProperties(serieProps, "effective_dose_in_msv"));
        ctSerie.setDtLastUpdate(getDateFromProperties(serieProps, "dt_last_update"));
        ctSerie.setCreateTime(Date.from(Instant.now()));

        return ctSerie;
    }

    public static MRSerie convertProps2MRSerie(Map<String, Object> serieProps) {
        MRSerie mrSerie = new MRSerie();
        SerieKey serieKey = new SerieKey();

        serieKey.setId(getLongFromProperties(serieProps, "local_serieId"));
        serieKey.setAet(getStringFromProperties(serieProps, "aet"));
        mrSerie.setSerieKey(serieKey);

        mrSerie.setLocalStudyKey(getStringFromProperties(serieProps, "local_study_id"));
        mrSerie.setLocalSerieId(getStringFromProperties(serieProps, "local_serie_id"));
        mrSerie.setProtocolKey(getLongFromProperties(serieProps, "protocol_key"));
        mrSerie.setSerieId(getStringFromProperties(serieProps, "serie_id"));
        mrSerie.setStudyKey(getLongFromProperties(serieProps, "study_key"));
        mrSerie.setDType(getStringFromProperties(serieProps, "serie_dtype"));
        mrSerie.setSeriesDate(getDateFromProperties(serieProps, "series_date"));
        mrSerie.setProtocolName(getStringFromProperties(serieProps, "protocol_name"));
        mrSerie.setAcquisitionDatetime(getDateFromProperties(serieProps, "acquisition_datetime"));
        mrSerie.setStartSliceLocation(getDoubleFromProperties(serieProps, "start_slice_location"));
        mrSerie.setEndSliceLocation(getDoubleFromProperties(serieProps, "end_slice_location"));
        mrSerie.setSeriesDescription(getStringFromProperties(serieProps, "series_description"));
        mrSerie.setDtLastUpdate(getDateFromProperties(serieProps, "dt_last_update"));
        mrSerie.setCreateTime(Date.from(Instant.now()));

        if(ManufacturerCode.SIEMENS.getMfCode().equals(getStringFromProperties(serieProps, "mf_code"))) {
            // acquisition_duration of SIEMENS is "TA 01.23*11" or "TA 01:23*11"
            String acquisitionDuration = getStringFromProperties(serieProps, "acquisition_duration");
            mrSerie.setAcquisitionDuration(parseAcquisitionDuration(acquisitionDuration));
        } else {
            mrSerie.setAcquisitionDuration(getDoubleFromProperties(serieProps, "acquisition_duration"));
        }

        return mrSerie;
    }

    public static XASerie convertProps2XASerie(Map<String, Object> serieProps) {
        XASerie xaSerie = new XASerie();
        SerieKey serieKey = new SerieKey();

        serieKey.setId(getLongFromProperties(serieProps, "local_serieId"));
        serieKey.setAet(getStringFromProperties(serieProps, "aet"));
        xaSerie.setSerieKey(serieKey);

        xaSerie.setLocalStudyKey(getStringFromProperties(serieProps, "local_study_id"));
        xaSerie.setLocalSerieId(getStringFromProperties(serieProps, "local_serie_id"));
        xaSerie.setSerieId(getStringFromProperties(serieProps, "serie_id"));
        xaSerie.setStudyKey(getLongFromProperties(serieProps, "study_key"));
        xaSerie.setDType(getStringFromProperties(serieProps, "serie_dtype"));
        xaSerie.setSeriesDate(getDateFromProperties(serieProps, "series_date"));
        xaSerie.setExposureTime(getFloatFromProperties(serieProps,"exposure_time"));
        xaSerie.setTargetRegion(getStringFromProperties(serieProps, "target_region"));
        xaSerie.setSeriesDescription(getStringFromProperties(serieProps, "series_description"));
        xaSerie.setProtocolKey(getLongFromProperties(serieProps, "protocol_key"));
        xaSerie.setProtocolName(getStringFromProperties(serieProps, "protocol_name"));
        xaSerie.setSerieType(getStringFromProperties(serieProps, "serie_type"));
        xaSerie.setSeriesSubType(getStringFromProperties(serieProps, "series_sub_type"));
        xaSerie.setSerieDap(getDoubleFromProperties(serieProps, "serie_dap"));
        xaSerie.setEffectiveDose(getDoubleFromProperties(serieProps, "effective_dose_in_msv"));
        xaSerie.setDtLastUpdate(getDateFromProperties(serieProps, "dt_last_update"));
        xaSerie.setCreateTime(Date.from(Instant.now()));

        return xaSerie;
    }

    public static Date getLastSerieDate(CTSerie lastSerie) {
        Instant lastSerieInstant = lastSerie.getSeriesDate()
                .toInstant()
                .plusSeconds(lastSerie.getExposureTime().longValue());
        return new Date(lastSerieInstant.toEpochMilli());
    }

    public static Date getLastSerieDate(MRSerie lastSerie) {
        Instant lastSerieInstant = lastSerie.getAcquisitionDatetime()
                .toInstant()
                .plusSeconds(lastSerie.getAcquisitionDuration().longValue());
        return new Date(lastSerieInstant.toEpochMilli());
    }

    public static Date getLastSerieDate(XASerie lastSerie) {
        Instant lastSerieInstant = lastSerie.getSeriesDate()
                .toInstant()
                .plusSeconds(lastSerie.getExposureTime().longValue());
        return new Date(lastSerieInstant.toEpochMilli());
    }

    public static Double parseAcquisitionDuration(String duration) {
        duration = duration.replace("TA ", "");
        int times = 1;
        BigDecimal acquisitionDuration = BigDecimal.ZERO;
        String dStr = duration;
        if(StringUtils.isNotBlank(duration) && duration.contains("*")) {
            String durationList [] = duration.split("\\*");
            if(durationList.length == 2) {
                dStr = durationList[0];
                times = Integer.parseInt(durationList[1]);
            }
        }
        if(StringUtils.isNotBlank(dStr)) {
            if(dStr.contains(".")) {
                //second
                acquisitionDuration = new BigDecimal(dStr);
            }
            if(dStr.contains(":")) {
                //min:sec
                String dStrList[] = dStr.split(":");
                int min = Integer.parseInt(dStrList[0]);
                int sec = Integer.parseInt(dStrList[1]);
                acquisitionDuration = BigDecimal.valueOf(sec + min * 60);
            }
        }
        BigDecimal parsedDuration = acquisitionDuration.multiply(BigDecimal.valueOf(times));
        return parsedDuration.doubleValue();
    }

}
