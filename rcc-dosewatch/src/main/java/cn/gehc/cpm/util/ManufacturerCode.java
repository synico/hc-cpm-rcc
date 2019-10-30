package cn.gehc.cpm.util;

public enum ManufacturerCode {
  GE("GE"),
  SIEMENS("SIEMENS");

  private String mfCode;

  ManufacturerCode(String mfCode) {
    this.mfCode = mfCode;
  }

  public String getMfCode() {
    return this.mfCode;
  }

}
