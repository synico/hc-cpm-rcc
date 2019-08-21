package cn.gehc.cpm.util;

import cn.gehc.cpm.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

public class DataUtil {

    private static final Logger log = LoggerFactory.getLogger(DataUtil.class);

    private static final DateTimeFormatter mysqlDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

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

    public static String ConvertDate2String(Date date) {
        String result = null;
        if(date != null) {
            Instant instant = date.toInstant();
            ZoneId localZone = ZoneId.of(LOCAL_TIME_ZONE);
            result = mysqlDateFormatter.format(LocalDateTime.ofInstant(instant, localZone));
        }
        return result;
    }

    public static Study convertProps2Study(Map<String, Object> studyProps) {
        Study study = new Study();
        StudyKey studyKey = new StudyKey();

        studyKey.setId(getLongFromProperties(studyProps,"local_studyId"));
        studyKey.setAet(getStringFromProperties(studyProps, "aet"));
        study.setStudyKey(studyKey);

        study.setLocalStudyId(getStringFromProperties(studyProps, "local_study_id"));
        study.setAeKey(getIntegerFromProperties(studyProps, "ae_key"));
        study.setAccessionNumber(getStringFromProperties(studyProps, "accession_number"));
        study.setDType(getStringFromProperties(studyProps, "study_dtype"));
        study.setModality(getStringFromProperties(studyProps, "modality"));
        study.setPatientKey(getLongFromProperties(studyProps, "patient_key"));
        study.setPatientAge(getIntegerFromProperties(studyProps, "patient_age"));
        study.setStudyId(getStringFromProperties(studyProps, "study_id"));
        study.setStudyInstanceUid(getStringFromProperties(studyProps, "study_instance_uid"));
        study.setStudyDate(getDateFromProperties(studyProps, "study_date"));

        return study;
    }

    public static CTStudy convertProps2CTStudy(Map<String, Object> studyProps) {
        CTStudy ctStudy = new CTStudy();
        StudyKey studyKey = new StudyKey();

        studyKey.setId(getLongFromProperties(studyProps,"local_studyId"));
        studyKey.setAet(getStringFromProperties(studyProps, "aet"));
        ctStudy.setStudyKey(studyKey);

        ctStudy.setLocalStudyId(getStringFromProperties(studyProps, "local_study_id"));
        ctStudy.setDlpTotal(getDoubleFromProperties(studyProps, "ct_dose_length_product_total"));
        ctStudy.setDlpSSDE(getDoubleFromProperties(studyProps, "dlp_ssde"));
        ctStudy.setExamCTDI(getDoubleFromProperties(studyProps, "exam_ctdi"));
        ctStudy.setNumSeries(getIntegerFromProperties(studyProps, "num_series"));
        ctStudy.setProtocolKey(getLongFromProperties(studyProps, "protocol_key"));

        return ctStudy;
    }

    public static CTSerie convertProps2CTSerie(Map<String, Object> serieProps) {
        CTSerie ctSerie = new CTSerie();
        SerieKey serieKey = new SerieKey();

        serieKey.setId(getLongFromProperties(serieProps, "local_serieId"));
        serieKey.setAet(getStringFromProperties(serieProps, "aet"));
        ctSerie.setSerieKey(serieKey);

        ctSerie.setLocalStudyKey(getStringFromProperties(serieProps, "local_study_id"));
        ctSerie.setLocalSerieId(serieKey.toString());
        ctSerie.setSerieId(getStringFromProperties(serieProps, "serie_id"));
        ctSerie.setDType(getStringFromProperties(serieProps, "serie_dtype"));
        ctSerie.setStudyKey(getLongFromProperties(serieProps, "study_key"));
        ctSerie.setSeriesDate(getDateFromProperties(serieProps, "series_date"));
        ctSerie.setAapmFactorSSDE(getDoubleFromProperties(serieProps, "aapm_factor_ssde"));
        ctSerie.setCtdiVolSSDE(getDoubleFromProperties(serieProps, "ctdi_vol_ssde"));
        ctSerie.setDlpSSDE(getDoubleFromProperties(serieProps, "dlp_ssde"));
        ctSerie.setEndSeriesTime(getDateFromProperties(serieProps, "end_series_time"));
        ctSerie.setExposureTime(getFloatFromProperties(serieProps, "exposure_time"));
        ctSerie.setDtLastUpdate(getDateFromProperties(serieProps, "dt_last_update"));

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
        mrSerie.setAcquisitionDatetime(getDateFromProperties(serieProps, "acquisition_datetime"));
        mrSerie.setAcquisitionDuration(getDoubleFromProperties(serieProps, "acquisition_duration"));
        mrSerie.setDtLastUpdate(getDateFromProperties(serieProps, "dt_last_update"));

        return mrSerie;
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

}
