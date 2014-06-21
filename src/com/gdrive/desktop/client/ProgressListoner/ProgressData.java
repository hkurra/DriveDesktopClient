package com.gdrive.desktop.client.ProgressListoner;

public class ProgressData
{
  private Double progressValue;
  private Double totalValue;
  private long totalByteUploaded;
  private String message;
  private int mUploadingPhase;

  public void setProgressValue(Double progressValue)
  {
    this.progressValue = progressValue;
  }

  public Double getProgressValue()
  {
    return this.progressValue;
  }

  void setTotalValue(Double totalValue)
  {
    this.totalValue = totalValue;
  }

  public Double getTotalValue()
  {
    return this.totalValue;
  }

  void setTotalByteUploaded(long totalByteUploaded)
  {
    this.totalByteUploaded = totalByteUploaded;
  }

  public long getTotalByteUploaded()
  {
    return this.totalByteUploaded;
  }

  public void setMessage(String message)
  {
    this.message = message;
  }

  public String getMessage()
  {
    return this.message;
  }

  public void setUploadingPhase(int mUpoadingPhase)
  {
    this.mUploadingPhase = mUpoadingPhase;
  }

  public int getUploadingPhase()
  {
    return this.mUploadingPhase;
  }
}