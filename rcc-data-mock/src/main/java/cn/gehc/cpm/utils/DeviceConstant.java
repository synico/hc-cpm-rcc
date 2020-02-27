package cn.gehc.cpm.utils;

import lombok.Getter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class DeviceConstant {

    public static final String TIMEZONE = "Asia/Shanghai";

    public static final String CT = "CT";

    public static final String MR = "MR";

    public enum AE {

        CT1("ANCTAWP2020", 1L, Type.CT.name(), MFCode.SIEMENS.name()),
        CT2("CT750", 2L, Type.CT.name(), MFCode.GE.name()),
        CT3("CT16", 3L, Type.CT.name(), MFCode.PHILIPS.name()),
        MR1("MRAWP157", 4L, Type.MR.name(), MFCode.SIEMENS.name()),
        MR2("GEMR750W", 5L, Type.MR.name(), MFCode.GE.name()),
        XA1("IGS520", 6L, Type.XA.name(), MFCode.GE.name()),
        XA2("TERRA2020", 7L, Type.XA.name(), MFCode.GE.name()),
        RF1("ARISTOS01", 8L, Type.RF.name(), MFCode.SIEMENS.name()),
        RF2("TERRARF1", 9L, Type.RF.name(), MFCode.GE.name())
        ;

        @Getter
        private String aet;

        @Getter
        private Long aeKey;

        @Getter
        private String type;

        @Getter
        private String mfCode;

        AE(String aet, Long aeKey, String type, String mfCode) {
            this.aet = aet;
            this.aeKey = aeKey;
            this.type = type;
            this.mfCode = mfCode;
        }
    }

    public static final List<AE> DEVICE_LIST = Arrays.asList(AE.CT1, AE.CT2, AE.CT3, AE.MR1, AE.MR2);

    public static final EnumSet<AE> CT_LIST = EnumSet.of(AE.CT1, AE.CT2, AE.CT3);

    public static final EnumSet<AE> MR_LIST = EnumSet.of(AE.MR1, AE.MR2);

    public static final EnumSet<AE> XA_LIST = EnumSet.of(AE.XA1, AE.XA2);

    public static final EnumSet<AE> RF_LIST = EnumSet.of(AE.RF1, AE.RF2);

    public enum Type {
        CT,
        MR,
        XA,
        RF
    }

    public enum MFCode {
        SIEMENS,
        GE,
        PHILIPS
    }

}
