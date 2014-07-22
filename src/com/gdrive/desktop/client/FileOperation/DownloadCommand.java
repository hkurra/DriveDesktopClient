package com.gdrive.desktop.client.FileOperation;

import java.io.FileOutputStream;
import java.io.IOException;

import com.gdrive.desktop.client.Global.DriveDesktopClient;
import com.gdrive.desktop.client.ProgressListoner.FileDownloadProgressListener;
import com.gdrive.desktop.client.ProgressListoner.IProgressListoner;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.GenericUrl;
import com.google.api.services.drive.model.File;


public class DownloadCommand extends ICommand {

	
	/**
	 * file to download
	 */
	File mFileToDownload;
	
	/**
	 * directory path
	 */
	String mLocalDirPath;
	
	/**
	 * Listener for download progress
	 */
	private IProgressListoner mProgressListener;
	
	
	
	/**
	 * Constructor 
	 * 
	 * @param fileToDownload
	 * @param localDirpath
	 */
	public DownloadCommand(File fileToDownload, String localDirpath) {
		mFileToDownload = fileToDownload;
		mLocalDirPath = localDirpath;
	}
	
	@Override
	public Boolean IsExecutable() {
		
		return true;
	}

	@Override
	protected int PreExecute() {
		
		return 0;
	}

	@Override
	protected int Execute() throws Exception {
		
	    if (mFileToDownload.getDownloadUrl() != null && mFileToDownload.getDownloadUrl().length() > 0) {
	        try {
//	          HttpResponse resp =
//	              SharedInstances.DRIVE.getRequestFactory().buildGetRequest(new GenericUrl(mFileToDownload.getDownloadUrl()))
//	                  .execute();
//	           InputStream inputStream = resp.getContent();
	           
	        java.io.File file = new java.io.File(mLocalDirPath + "/" + mFileToDownload.getTitle());
	   		FileOutputStream fileoutputStream = new FileOutputStream(file);
//	   		int read = 0;
//			byte[] bytes = new byte[1024];
//	 
//			
//			while ((read = inputStream.read(bytes)) != -1) {
//				fileoutputStream.write(bytes, 0, read);
//			}
	   		
	   	    MediaHttpDownloader downloader =
	   	        new MediaHttpDownloader(DriveDesktopClient.HTTP_TRANSPORT, DriveDesktopClient.DRIVE.getRequestFactory().getInitializer());
	   	    enableProgressListoner(downloader);
	   	    downloader.download(new GenericUrl(mFileToDownload.getDownloadUrl()), fileoutputStream);
			fileoutputStream.flush();
			fileoutputStream.close();
			
	        } catch (IOException e) {
	          throw e;
	        }
	      }
		return 0;
	}

	@Override
	protected int PostExecute() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	  private void enableProgressListoner(MediaHttpDownloader downloader) {
		  
		  downloader.setProgressListener(new FileDownloadProgressListener(getProgressListoner()));
		    
		      long fileSize = mFileToDownload.getFileSize();
		      long inKB = fileSize / 1024L;
		      if (inKB > 512L) {
		        downloader.setDirectDownloadEnabled(false);
		        downloader.setChunkSize(MediaHttpUploader.MINIMUM_CHUNK_SIZE);
		      
		    }
		  }

	/**
	 * @param mProgressListoner the mProgressListoner to set
	 */
	public void setProgressListoner(IProgressListoner mProgressListoner) {
		this.mProgressListener = mProgressListoner;
	}

	/**
	 * @return the mProgressListoner
	 */
	public IProgressListoner getProgressListoner() {
		return mProgressListener;
	}

}
