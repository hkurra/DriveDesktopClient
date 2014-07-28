package com.gdrive.desktop.client.ProgressListoner;

import java.text.NumberFormat;

import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;


public class FileDownloadProgressListener implements MediaHttpDownloaderProgressListener {

	private IProgressListoner mIProgressListoner;
	private ProgressData mProgressData;
	public FileDownloadProgressListener(IProgressListoner iProgressListoner)
	{
	  this.mIProgressListoner = iProgressListoner;
	  this.mProgressData = new ProgressData();
	}

	public void fillDownloadDataAndCallListoner(MediaHttpDownloader up) {
		  this.mProgressData.setTotalByteUploaded(up.getNumBytesDownloaded());
		  try {
		    this.mProgressData.setProgressValue(Double.valueOf(up.getProgress()));
		  }
		  catch (Exception e) {
		    e.printStackTrace();
		  }
		  this.mIProgressListoner.progressChanged(this.mProgressData);
		}
	
	public FileDownloadProgressListener() {
	  this.mIProgressListoner = null;
	  this.mProgressData = new ProgressData();
	}
	  @Override
	  public void progressChanged(MediaHttpDownloader downloader) {
		  if (mIProgressListoner == null) {
			  return;
		  }
	    switch (downloader.getDownloadState()) {
	      case MEDIA_IN_PROGRESS:
	    	    this.mProgressData.setMessage("Download is in progress: " + NumberFormat.getPercentInstance().format(
	    	    	      downloader.getProgress()));
	    	    this.mProgressData.setUploadingPhase(1);
	    	    fillDownloadDataAndCallListoner(downloader);
	        break;
	      case MEDIA_COMPLETE:
	    	    this.mProgressData.setMessage("Download is Complete!");
	    	    this.mProgressData.setUploadingPhase(1);
	    	    fillDownloadDataAndCallListoner(downloader);
	        break;
	    }
	  }
	}
