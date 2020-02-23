package cn.gehc.cpm.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

import static cn.gehc.cpm.utils.DeviceConstant.*;

public class StudyUtils {

    private static final Integer TRY_NUM = 10;

    public static String generateAccessionNumber(String aet) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYYMMddHHmmss");
        return aet + "|" + dateTimeFormatter.format(LocalDateTime.now());
    }

    public static Integer generatePatientAge() {
        Integer patientAge = 20;
        Random random = new Random();
        for(int i = 0; i < TRY_NUM; i++) {
            int tmp = random.nextInt(80);
            if(tmp > 20 && tmp < 80) {
                patientAge = tmp;
                break;
            }
        }
        return patientAge;
    }

    public static String generatePatientId() {
        String patientId = "";
        Random random = new Random();
        Long tmp = System.currentTimeMillis() + random.nextInt(100);
        patientId = tmp.toString().substring(6);
        return patientId;
    }

    public static String generatePatientSex() {
        String patientSex = StudyConstant.PatientSex.MALE.name();
        Random random = new Random();
        if(random.nextInt() % 2 == 1) {
            patientSex = StudyConstant.PatientSex.FEMALE.name();
        }
        return patientSex;
    }

    public static String generateStudyDesc(StudyConstant.Type studyType) {
        String studyDesc = "";
        Random random = new Random();
        switch (studyType) {
            case CTSTUDY:
                int bond = StudyConstant.CT_STUDY_DESCRIPTIONS.size() - 1;
                studyDesc = StudyConstant.CT_STUDY_DESCRIPTIONS.get(random.nextInt(bond));
        }
        return studyDesc;
    }

    public static Integer generateTargetRegionCount(StudyConstant.Type studyType) {
        Integer targetRegionCount = 0;
        Random random = new Random();
        switch (studyType) {
            case CTSTUDY:
                targetRegionCount = random.nextInt(3);
                break;
            case MRSTUDY:
                for(int i = 0; i < TRY_NUM; i++) {
                    int tmp = random.nextInt(7);
                    if(tmp > 3) {
                        targetRegionCount = tmp;
                        break;
                    }
                }
                break;
        }
        targetRegionCount++;
        return targetRegionCount;
    }

    public static Date generateStudyDate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId zoneId = ZoneId.systemDefault();
        Random random = new Random();
        int buffer = random.nextInt(120);
        localDateTime = localDateTime.plusSeconds(buffer);
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return Date.from(zonedDateTime.toInstant());
    }

    public static Date generateStudyStartTime(Instant instant) {
        Random random = new Random();
        int buffer = 10;
        for(int i = 0; i < TRY_NUM; i++) {
            int tmp = random.nextInt(60);
            if(tmp > 20) {
                buffer = tmp;
                break;
            }
        }
        return Date.from(instant.plusSeconds(buffer));
    }

    public static Date generateStudyEndTime(Instant instant) {
        Random random = new Random();
        int studyDuration = 180;
        for(int i = 0; i < TRY_NUM; i++) {
            int tmp = random.nextInt(300);
            if(tmp > 60) {
                studyDuration = tmp;
                break;
            }
        }
        return Date.from(instant.plusSeconds(studyDuration));
    }

    public static void main(String args[]) {
        for(AE ae : DEVICE_LIST) {
            System.out.println(generateTargetRegionCount(StudyConstant.Type.CTSTUDY));
        }
    }

}
