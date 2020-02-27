package cn.gehc.cpm.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static cn.gehc.cpm.utils.DeviceConstant.*;
import static cn.gehc.cpm.utils.StudyConstant.*;

public class StudyUtils {

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    public static String generateAccessionNumber(String aet) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYYMMddHHmmss");
        return aet + "|" + dateTimeFormatter.format(LocalDateTime.now());
    }

    public static Integer generatePatientAge() {
        return random.nextInt(20, 80);
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
        Integer targetRegionCount = 3;
        switch (studyType) {
            case CTSTUDY:
                targetRegionCount = random.nextInt(3, 6);
                break;
            case MRSTUDY:
                targetRegionCount = random.nextInt(4, 9);
                break;
        }
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

    public static Date generateStudyStartTime(Instant studyInstant) {
        int buffer = random.nextInt(20, 60);
        return Date.from(studyInstant.plusSeconds(buffer));
    }

    /*
     * @return unit: second
     */
    public static Integer generateStudyDuration(String deviceType) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int studyDuration = 180;
        switch (deviceType) {
            case CT:
                studyDuration = random.nextInt(CT_STUDY_DURATION_LOW, CT_STUDY_DURATION_HIGH);
                break;
            case MR:
                studyDuration = random.nextInt(MR_STUDY_DURATION_LOW, MR_STUDY_DURATION_HIGH);
                break;
        }
        return studyDuration;
    }

    public static Date generateStudyEndTime(Instant instant, String deviceType) {
        int studyDuration = generateStudyDuration(deviceType);
        return Date.from(instant.plusSeconds(studyDuration));
    }

    public static void main(String args[]) {
        for(AE ae : DEVICE_LIST) {
            System.out.println(generateTargetRegionCount(StudyConstant.Type.MRSTUDY));
        }
    }

}
