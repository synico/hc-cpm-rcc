package cn.gehc.cpm.util;

public enum SerieType {
  CINE("CineSerie"),
  CONSTANT_ANGLE("ConstantAngleSerie"),
  SEQUENCED("SequencedSerie"),
  SMART_PREP("SmartPrepSerie"),
  SPIRAL("SpiralSerie"),
  STATIONARY("StationarySerie");

  private String type;

  SerieType(String type) {
    this.type = type;
  }

  public String getType() {
    return this.type;
  }
}
