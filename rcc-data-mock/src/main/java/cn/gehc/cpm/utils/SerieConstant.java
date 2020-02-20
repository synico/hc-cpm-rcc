package cn.gehc.cpm.utils;

import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SerieConstant {

    public static final String MR_SERIE_TYPE = "MRSerie";

    public enum CT_SERIE_TYPE {
        CINE("CineSerie"),
        CONSTANT_ANGLE("ConstantAngleSerie"),
        SEQUENCED("SequencedSerie"),
        SMART_PREP("SmartPrepSerie"),
        SPIRAL("SpiralSerie"),
        STATIONARY("StationarySerie");

        private String serieType;

        CT_SERIE_TYPE(String type) {
            this.serieType = type;
        }

        public String getSerieType() {
            return this.serieType;
        }
    }

    public static final class CT_SERIE_DESCRIPTION {

        public static List<String> CONSTANT_ANGLE = Arrays.asList("-",
                "Scout",
                "Topogram 0.6 T20f");

        public static List<String> SPIRAL = Arrays.asList("-",
                "0.625mm HiRes",
                "2.5mm QC C+A",
                "3.75mm std",
                "5MM C+A",
                "C- 5mm std",
                "chest 5mm C-",
                "N*2+10+4=Time 100ml 35NS",
                "QC 5mm",
                "SS Seg 30-74 BPM 0.625 SS30",
                "Soft Tissue Neck",
                "Thorax  8.0  B70f",
                "VHS Chest CTA  SS40"
                );

        public static List<String> STATIONARY = Arrays.asList("-",
                "std 5mm",
                "Axial 2.5mm",
                "Timing Bolus",
                "Stnd + SS80",
                "2.5mm std"
                );

        public static List<String> CINE = Arrays.asList("-",
                "SScore",
                "SS Pulse 30-65BPM"
                );

        public static List<String> SMART_PREP = Arrays.asList("-");

        public static List<String> SEQUENCED = Arrays.asList("HeadSeq  2.0", "HeadSeq  5.0");

    }

    public static final class CT_TARGET_REGION {
        public static List<String> CONSTANT_ANGLE = Arrays.asList("HEAD", "CHEST", "-");

        public static List<String> SPIRAL = Arrays.asList("Abdomen",
                "Chest",
                "CHEST",
                "Extremity",
                "Eye region",
                "Head",
                "Lumbar spine",
                "Neck",
                "Pelvis",
                "Shoulder"
                );

        public static List<String> STATIONARY = Arrays.asList("Abdomen",
                "Body Axial",
                "Chest",
                "Extremity",
                "Eye region",
                "Head",
                "Head Axial",
                "Lumbar spine",
                "Neck",
                "Pelvis",
                "Shoulder",
                "-"
                );

        public static List<String> CINE = Arrays.asList("Chest", "-");

        public static List<String> SMART_PREP = Arrays.asList("Body Axial",
                "Head Axial",
                "Lumbar spine Axial");

        public static List<String> SEQUENCED = Arrays.asList("HEAD");

    }

    public static final Map<Long, String> MR_SERIE_PROTOCOLS = ImmutableMap.<Long, String>builder()
            .put(55L, "001_8NV_Brain")
            .put(48L, "001_8NV_Brain-ALL")
            .put(52L, "001_8NV_MRA+DWI")
            .put(31L, "OAx T2 Propeller")
            .put(168L, "B01-HNU-Brain+MRA")
            .put(319L, "ep2d_diff_3scan_trace_p2")
            .put(63L, "B01-HNU-Brain")
            .put(68L, "B01-HNU-Brain-ALL")
            .put(70L, "L-Spine")
            .put(332L, "t2_swi_tra_p2_2mm")
            .put(95L, "001_8CH_ABD")
            .put(141L, "01_Quad_KNEE")
            .put(201L, "001_8NV_Brain+bravo")
            .put(312L, "tof_fl3d_tra_p2_multi-slab")
            .put(35L, "8HR-Head")
            .put(315L, "AAHead_Scout")
            .put(397L, "III_AASpine_Scout")
            .put(49L, "t2_tirm_tra_dark-fluid_320")
            .put(66L, "C-spine")
            .put(45L, "AAHead_Scout")
            .put(74L, "001_8NV_Brain+MRA")
            .put(30L, "cine_tf2d13_retro_SA _8slice")
            .put(33L, "tse_11_db_T1_SPAIR")
            .put(316L, "t2_tse_tra")
            .put(44L, "003-YS_CTL_L_Spine")
            .build();

    public static final List<String> MR_SERIE_DESCRIPTIONS = Arrays.asList("OAx T2 frFSE",
            "3-pl T2* Loc",
            "OAx fs PD",
            "3-Pl Loc T1",
            "Cerebral Blood Flow",
            "OAx T2 Propeller",
            "3D ASL PLD=1.5s",
            "OAx T1 FLAIR",
            "Ax 3D T1BRAVO",
            "WATER: BH Ax LAVA-Flex Mask",
            "AX OutPhase",
            "3-Pl T2* FGRE",
            "3pl Loc",
            "OSag T1FSE",
            "MPR Ob_Ax_I -> S_Min IP_sp:10.0_th:10.0",
            "tof_fl3d_tra_p2_multi-slab_MIP_COR",
            "Apparent Diffusion Coefficient (mm2/s)",
            "OCor fs T2",
            "OAx DWI 1000",
            "OSag T1 FSE",
            "ASSET Cal",
            "WATER: OSag T2 Ideal",
            "OAx 3D TOF MRA 3Slab",
            "OAx T1Flair",
            "tof_fl3d_tra_p2_multi-slab_MIP_SAG",
            "OutPhase: BH Ax LAVA-Flex Mask",
            "Processed Images",
            "Ax T2Flair",
            "Ax T2 FRFSE",
            "Ax SWAN FAST",
            "Ax T1",
            "OAx SWAN Out of Phase",
            "OAx T2Flair",
            "OAx T2 FSE",
            "tof_fl3d_tra_p2_multi-slab",
            "FILT_PHA: OAx SWAN Out of Phase",
            "FILT_PHA: Ax SWAN FAST",
            "AAHead_Scout_MPR_tra",
            "ep2d_diff_3scan_trace_p2_TRACEW",
            "AX MASK",
            "OAx DWI Asset",
            "OAx T2 FLAIR",
            "AAHead_Scout_MPR_sag",
            "OAx 3D-TOF-MRA Asset",
            "COL:OAx 3D-TOF-MRA Asset",
            "AAHead_Scout_MPR_cor",
            "t2_tse_tra",
            "t1_tse_dark-fluid_tra_p2",
            "AX InPhase",
            "COL:OAx 3D TOF MRA 3Slab",
            "3-Pl Loc GRE",
            "Ax 3D ASL (non-contrast)",
            "OVX DWI",
            "3-pl T2* FGRE S",
            "OSag STIR",
            "tof_fl3d_tra_p2_multi-slab_MIP_TRA",
            "OSag T2 frFSE",
            "OSag fs PD",
            "<MIP Range[1]>",
            "<MIP Range>",
            "ep2d_diff_3scan_trace_p2_ADC",
            "t2_tse_dark-fluid_tra_320",
            "AAHead_Scout",
            "3pl T2* Loc",
            "OSag T2FSE",
            "InPhase: BH Ax LAVA-Flex Mask");

}
