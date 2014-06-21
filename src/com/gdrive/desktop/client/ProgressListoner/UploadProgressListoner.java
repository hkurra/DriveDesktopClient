package com.gdrive.desktop.client.ProgressListoner;

import java.io.IOException;
import java.text.NumberFormat;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;

public class UploadProgressListoner
implements MediaHttpUploaderProgressListener
{
private IProgressListoner mIProgressListoner;
private ProgressData mProgressData;

public UploadProgressListoner(IProgressListoner iProgressListoner)
{
  this.mIProgressListoner = iProgressListoner;
  this.mProgressData = new ProgressData();
}

public UploadProgressListoner() {
  this.mIProgressListoner = null;
  this.mProgressData = new ProgressData();
}

public void fillUploadDataAndCallListoner(MediaHttpUploader up) {
  this.mProgressData.setTotalByteUploaded(up.getNumBytesUploaded());
  try {
    this.mProgressData.setProgressValue(Double.valueOf(up.getProgress()));
  }
  catch (IOException e) {
    e.printStackTrace();
  }
  this.mIProgressListoner.progressChanged(this.mProgressData);
}

public void progressChanged(MediaHttpUploader uploader) throws IOException {
  if (this.mIProgressListoner == null) {
    return;
  }
  switch (uploader.getUploadState())
  {
  case INITIATION_STARTED:
    this.mProgressData.setMessage("Upload Initiation has started.");
    this.mProgressData.setUploadingPhase(1);
    fillUploadDataAndCallListoner(uploader);
    break;
  case INITIATION_COMPLETE:
    this.mProgressData.setMessage("Upload Initiation is Complete.");
    this.mProgressData.setUploadingPhase(2);
    fillUploadDataAndCallListoner(uploader);
    break;
  case MEDIA_IN_PROGRESS:
    this.mProgressData.setMessage("Upload is In Progress: " + 
      NumberFormat.getPercentInstance().format(
      uploader.getProgress()));
    this.mProgressData.setUploadingPhase(3);
    fillUploadDataAndCallListoner(uploader);
    break;
  case MEDIA_COMPLETE:
    this.mProgressData.setMessage("Upload is Complete!");
    this.mProgressData.setUploadingPhase(4);
    fillUploadDataAndCallListoner(uploader);
  }
}
}
