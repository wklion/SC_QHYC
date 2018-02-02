package com.spd.grid.domain;

public class ZSCS
{
  private String publictime;
  private String stationnum;
  private Double tzs;
  private Double tcs;
  private Double rzs;
  private Double rcs;

  public String getPublictime()
  {
    return this.publictime;
  }

  public void setPublictime(String publictime) {
    this.publictime = publictime;
  }

  public String getStationnum() {
    return this.stationnum;
  }

  public void setStationnum(String stationnum) {
    this.stationnum = stationnum;
  }

  public Double getTzs() {
    return this.tzs;
  }

  public void setTzs(Double tzs) {
    this.tzs = tzs;
  }

  public Double getTcs() {
    return this.tcs;
  }

  public void setTcs(Double tcs) {
    this.tcs = tcs;
  }

  public Double getRzs() {
    return this.rzs;
  }

  public void setRzs(Double rzs) {
    this.rzs = rzs;
  }

  public Double getRcs() {
    return this.rcs;
  }

  public void setRcs(Double rcs) {
    this.rcs = rcs;
  }

  public ZSCS(String publicTime, String stationNum, Double tzs, Double tcs, Double rzs, Double rcs)
  {
    this.publictime = publicTime;
    this.stationnum = stationNum;
    this.tzs = tzs;
    this.tcs = tcs;
    this.rzs = rzs;
    this.rcs = rcs;
  }
}