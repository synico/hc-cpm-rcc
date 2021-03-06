package cn.gehc.cpm.utils;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StudyConstant {

    public enum Type {
        CTSTUDY("CTStudy"),
        MRSTUDY("MRStudy"),
        XASTUDY("XAStudy")
        ;

        @Getter
        private String name;

        Type(String name) {
            this.name = name;
        }
    }

    public enum PatientSex {
        MALE, FEMALE
    }

    public enum MODALITY {
        CT,
        MR,
        XA
    }

    public static final Integer CT_STUDY_DURATION_LOW = 160;
    public static final Integer CT_STUDY_DURATION_HIGH = 240;
    public static final Integer MR_STUDY_DURATION_LOW = 540;
    public static final Integer MR_STUDY_DURATION_HIGH = 780;

    public static final List<String> CT_STUDY_DESCRIPTIONS = Arrays.asList("1_ThoraxRoutine",
            "CT Kidney  PS",
            "CT L-Ankle PS",
            "CT L-Calcaneus",
            "CT L-Hand",
            "CT L-Knee PS",
            "CT Lower Abdomen CE",
            "CT L-Tibia and fibula",
            "CT Nasal",
            "CT Neck CE",
            "CT R-Ankle PS",
            "CT R-Elbow",
            "CT Rib cage",
            "CT R-Knee PS",
            "CT Sacroiliac joint",
            "CT Shoulder",
            "CT S-spine PS",
            "CT Sternoclavicular jo",
            "CT Temporal bone",
            "CT Temporomandibular j",
            "CT Thyroid PS",
            "CT T-spine PS",
            "CT Upper Abdomen  CE",
            "CT Upper and lower jaw",
            "Head, Plain CT",
            "Head^1_HeadRoutine (Adult)");

    public static final Map<Long, String> CT_PROTOCOLS = ImmutableMap.<Long, String>builder()
            .put(501L, "1.15 DING WEI Helical Head")
            .put(120L, "1.1 HELICAL HEAD 5MM")
            .put(50L, "1.1 Routine Head(Axial) ASIR40 OK")
            .put(42L, "1.2 Helical Head  ASIR40 OK")
            .put(75L, "1.2 Routine Axial Head 2.5mm/8+5mm/16")
            .put(401L, "1.3 GSI Neck Head CT-DSA")
            .put(360L, "1.4 Neck Head CT-DSA")
            .put(464L, "1.5 Head  CTA-DSA")
            .put(61L, "10.1 SnapShot Segment 30-70 BPM ASIR40")
            .put(57L, "2.1 Sinus Supine Helical ASIR30")
            .put(532L, "2.3 IAC HiRes  ASIR30")
            .put(134L, "2.3 Orbits Helical")
            .put(152L, "3.1 C-Spine C3-C7 Axial")
            .put(54L, "3.1 C-Spine Helical  ASIR30")
            .put(129L, "3.3 C-Spine Helical 0.8//5mm")
            .put(350L, "3.3 GSI Soft Tissue Neck C+")
            .put(130L, "3.4 Soft Tissue Neck 5mm")
            .put(340L, "3.4  Soft Tissue Neck C+")
            .put(357L, "3.7 C-Spine 4 Level Axial")
            .put(67L, "3.7 C-Spine Axial  ASIR40")
            .put(179L, "4.1 Shoulder 0.625")
            .put(348L, "4.1 Shoulder")
            .put(544L, "4.3 Wrist")
            .put(390L, "5.10 SnapShot BURST 70-90 BPM")
            .put(359L, "5.17 VHS Chest 4D CTA ASIR80")
            .put(43L, "5.1 Chest")
            .put(40L, "5.1  Chest ASIR40")
            .put(90L, "5.1 Routine Chest")
            .put(543L, "5.2 GSI Chest C+ GSI")
            .put(524L, "5.2 Routine Chest +C")
            .put(85L, "5.3 Chest +abdmen +C")
            .put(355L, "5.4 Chest Abd Pelvis ASIR40")
            .put(338L, "5.5 Rib 3D")
            .put(351L, "5.6 GSI PE")
            .put(230L, "5.6 Routine Chest")
            .put(347L, "5.7 Routine Chest ASIR40 C+")
            .put(363L, "5.8 SnapShot Pulse 30-65 BPM ASIR50")
            .put(345L, "5.9 SnapShot Segment 30-70 BPM  ASIR40")
            .put(51L, "5.9 SnapShot Segment 30-70 BPM ASIR50")
            .put(76L, "6.1 Abdomen 0.7 sec")
            .put(41L, "6.1 Abdomen  345mA  ASIR40")
            .put(542L, "6.26 GSI inferior vena cava CTV")
            .put(344L, "6.2 AB+C 3 phases")
            .put(184L, "6.3 Abdomen")
            .put(47L, "6.3 CH,AB+c 3 phases")
            .put(349L, "6.4 CHEST +C")
            .put(354L, "6.6 GSI CTAV-AB +C")
            .put(352L, "6.7 GSI Tri-Phasic Liver -C")
            .put(638L, "6.9 GSI Tri-Phasic Liver +C")
            .put(97L, "7.1 L-Spine 3 Level Axial")
            .put(342L, "7.1 L-Spine Survey Helical ASIR40")
            .put(343L, "7.2 L-Spine 3 Level Axial")
            .put(341L, "7.2 L-Spine 4 Level Axial")
            .put(77L, "7.3 L-Spine Helical 5mm")
            .put(385L, "7.4 GSI L-Spine Survey Helical Metal")
            .put(545L, "7.6 L-Spine Helical 0.625mm")
            .put(45L, "8.1 Pelvis ASiR40")
            .put(361L, "8.3 Pelvis 1.0 sec. 5mm")
            .put(346L, "9.1 LEG joint")
            .put(358L, "9.3 Ankle 5mm")
            .put(471L, "9.3 GSI Knee Metal")
            .put(353L, "9.5 GSI LEG CTA")
            .put(119L, "9.5 knee 5mm")
            .build();

    public static final List<String> MR_STUDY_DESCRIPTIONS = Arrays.asList("MR",
            "MR Head DWI",
            "MR MRA",
            "MR Head CE",
            "MR LS-spine  PS",
            "MR C-spine  PS",
            "MR Right Knee",
            "MR Left Knee",
            "MR Head",
            "MR R Shoulder",
            "MR MRI",
            "MR Prostate",
            "MR MRCP",
            "MR Hip",
            "MR Anorectal",
            "MR L shoulder",
            "MR Right ankle",
            "MR Saddle Zone CE",
            "MR Saddle Zone 3D",
            "MR Left ankle joint",
            "MR T-spine  PS",
            "MR Pelvic",
            "MR Neck");

    public static final Map<Long, String> MR_PROTOCOLS = ImmutableMap.<Long, String>builder()
            .put(315L, "AAHead_Scout")
            .put(48L, "001_8NV_Brain-ALL")
            .put(68L, "B01-HNU-Brain-ALL")
            .put(33L, "8HR-Head")
            .put(30L, "B01-HNU-Brain-ALL")
            .put(74L, "001_8NV_Brain+MRA")
            .put(70L, "L-Spine")
            .put(95L, "001_8CH_ABD")
            .put(63L, "B01-HNU-Brain")
            .put(79L, "003-L-Spine")
            .put(52L, "001_8NV_MRA+DWI")
            .put(333L, "AAKnee_Scout_15ch")
            .put(322L, "localizer")
            .put(395L, "I_AASpine_Scout")
            .put(441L, "003-YS_CTL_L_Spine")
            .put(201L, "001_8NV_Brain+bravo")
            .put(168L, "B01-HNU-Brain+MRA")
            .put(189L, "001-C-Spine")
            .put(455L, "localizer_haste")
            .put(112L, "001-YS_CTL_C_Spine")
            .put(169L, "02_Quad_ANKLE")
            .put(228L, "005_8NV_PIT")
            .put(114L, "AAShoulder_Scout")
            .put(420L, "B01-HNU-Brain-ALL-FSE")
            .put(206L, "B01_8HD_HIP")
            .put(60L, "A01_HD_BREAST-7")
            .put(36L, "T-Spine/")
            .put(424L, "localizer_tra")
            .put(44L, "AAHead_Scout")
            .put(202L, "A01_HD_BREAST-DTI")
            .put(275L, "03_8HR_WRIST")
            .put(207L, "006_8HR_TMJ-2")
            .put(293L, "005_8NV_PIT-HL")
            .put(69L, "L-Spine/1,2,3,6,4")
            .put(93L, "002_8CH_Kidney")
            .put(283L, "L03-QUAD-Ankle")
            .put(65L, "C-spine/1,2,3,4,5")
            .put(237L, "Sag T2fs PROPELLER")
            .put(156L, "001-ABD")
            .put(308L, "001_8NV_Brain")
            .put(58L, "A102_NV_Soft_Tissue")
            .put(257L, "L02-QUAD-Knee")
            .put(209L, "L02-QUAD-Knee")
            .put(212L, "01_3CH_SHOULDER")
            .put(32L, "8HR-Head/1,5,6,2,3,4")
            .put(294L, "03_Quad_FOOT-hl")
            .put(487L, "B01-HNU-Brain+BRAVO")
            .put(208L, "001_8NV_Brain")
            .put(500L, "Gammar knife")
            .put(174L, "B03_8HD_Big-PELVIS")
            .put(306L, "B03_8HD_FEMALE_PELVIS_LL")
            .put(203L, "L02-QUAD-Knee")
            .put(140L, "Sag T2 fs PROPELLER")
            .put(442L, "02_8HR_ELBOW")
            .put(438L, "B01-HNU-Brain-HR-Vascula")
            .put(250L, "B04-HNU-Pituitary")
            .build();

}
