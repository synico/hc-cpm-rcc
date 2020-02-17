package cn.gehc.cpm.utils;

import cn.gehc.cpm.domain.CTSerie;

import java.util.Arrays;
import java.util.List;

public class SerieConstant {

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

}
