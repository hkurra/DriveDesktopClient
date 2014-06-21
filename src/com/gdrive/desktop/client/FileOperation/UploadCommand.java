/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdrive.desktop.client.FileOperation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.nio.file.Files;

import javax.swing.tree.TreePath;

import com.gdrive.desktop.client.Global.ServiceManager;
import com.gdrive.desktop.client.Global.SharedInstances;
import com.gdrive.desktop.client.Global.ResponderData.AfterFileUploadRespoderData;
import com.gdrive.desktop.client.Global.ResponderData.BeforeFileUploadResponderData;
import com.gdrive.desktop.client.ProgressListoner.IProgressListoner;
import com.gdrive.desktop.client.ProgressListoner.UploadProgressListoner;
import com.gdrive.desktop.client.cache.GDriveFile;
import com.gdrive.desktop.client.cache.GDriveFileRevisions;
import com.gdrive.desktop.client.cache.GDriveFiles;
import com.gdrive.desktop.client.cache.TreeNodeInfo;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ParentReference;

/**
 * 
 * @author harsh
 */

public class UploadCommand extends ICommand
{
  private String mParentID = null;

  private String mMimeType = null;

  private String mDescription = null;

  private String mTitle = null;

  private boolean mDeleteFile = false;

  private boolean mFolder = true;

  private GDriveFile mGDriveFile = null;
  private java.io.File mLocalDiskFile;
  private com.google.api.services.drive.model.File mUploadedFile;
  private UploadOperation mUploadOperation;
  private IProgressListoner uploadProgressListoner = null;
  private TreePath mUplodedFileTreePath;
  
  public static enum UploadOperation
  {
    NEW_REVISION, PATCH, TOUCH, NEW_UPLOAD;
  }

  public UploadCommand(GDriveFile gDrivefile, UploadOperation isUpdatefile)
  {
    setUploadOperation(isUpdatefile);
    Init(gDrivefile);
  }

  public UploadCommand(GDriveFile gDrivefile, String parentID)
  {
    Init(gDrivefile);
    this.mParentID = parentID;
    setUploadOperation(UploadOperation.NEW_UPLOAD);
  }

  private void Init(GDriveFile gDrivefile)
  {
    setGDriveFile(gDrivefile);
    setDeleteFile(getGDriveFile().getDelateFile().booleanValue());
    setFolder(getGDriveFile().isFolder().booleanValue());
    setMimeType(getGDriveFile().getDFile().getMimeType());
    setTitle(getGDriveFile().getDFile().getTitle());
    setFileDescription(getGDriveFile().getDFile().getDescription());
    setLocalDiskFile(new java.io.File(getGDriveFile().getFilePath()));
  }

  public int Execute() throws Exception
  {
    try {
      com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();

      fileMetadata.setTitle(getTitle());
      fileMetadata.setDescription(getFileDescription());
      fileMetadata.setMimeType(getMimeType());

      if ((this.mParentID != null) && (this.mParentID.length() > 0)) {
        fileMetadata.setParents(Arrays.asList(new ParentReference[] { new ParentReference()
          .setId(this.mParentID) }));
      }
      FileContent fileContent = new FileContent(getMimeType(), 
        getLocalDiskFile());

      switch (getUploadOperation())
      {
      case NEW_REVISION:
        String fileID = getGDriveFile().getDFile().getId();
        Drive.Files.Update update = SharedInstances.DRIVE.files().update(fileID, fileMetadata, fileContent);
        enableProgressListoner(update.getMediaHttpUploader());
        if (!setUploadedFile((com.google.api.services.drive.model.File)update.execute()).booleanValue()) break;
        GDriveFileRevisions.updateFileRevision(fileID);

        break;
      case NEW_UPLOAD:
        Drive.Files.Insert insert = isFolder() ? SharedInstances.DRIVE
          .files().insert(fileMetadata) : SharedInstances.DRIVE
          .files().insert(fileMetadata, fileContent);

        enableProgressListoner(insert.getMediaHttpUploader());

        if (!setUploadedFile((com.google.api.services.drive.model.File)insert.execute()).booleanValue())
          break;
        TreeNodeInfo uplodedFileTreeNodeInfo = GDriveFiles.FileProcessing(getUploadedFile());
        if (this.mParentID == null)
        {
          List<TreeNodeInfo> directoryStructure = GDriveFiles.getMyDriveDirectoryStructure();

          directoryStructure.add(uplodedFileTreeNodeInfo);
          Object[] treePathArray = { uplodedFileTreeNodeInfo };

          TreePath uploadedTreePath = new TreePath(treePathArray);
          this.mUplodedFileTreePath = uploadedTreePath;
        }
        else {
          TreeNodeInfo parentNodeInfo = GDriveFiles.getFileTreeNodeInfo(this.mParentID);
          if (parentNodeInfo == null) break;
          List<TreeNodeInfo> childList = (List<TreeNodeInfo>)parentNodeInfo.get("CHILD");
          childList.add(uplodedFileTreeNodeInfo);
        }

        break;
      case PATCH:
        setUploadedFile(
          (com.google.api.services.drive.model.File)SharedInstances.DRIVE
          .files()
          .patch(getGDriveFile().getDFile().getId(), fileMetadata)
          .execute());
        break;
      case TOUCH:
        setUploadedFile(
          (com.google.api.services.drive.model.File)SharedInstances.DRIVE.files()
          .touch(getGDriveFile().getDFile().getId()).execute());
      }

    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
      throw e;
    } catch (IOException e) {
      System.out.println(SharedInstances.MY_RESOURCE
        .getString("I/O_ERROR"));
      e.printStackTrace();
      throw e;
    } catch (Exception ex) {
      throw ex;
    }
    return 0;
  }

  public int PreExecute()
  {
    if ((getLocalDiskFile().isDirectory()) || (isFolder())) {
      setMimeType("application/vnd.google-apps.folder");
      setFolder(true);
    }

    if (getMimeType() == null) {
      try {
        setMimeType(
          Files.probeContentType(getLocalDiskFile()
          .toPath()));
      } catch (IOException e) {
        e.printStackTrace();
      }

      if (getMimeType() == null) {
        setMimeType("application/octet-stream");
      }

    }

    if (getTitle() == null) {
      setTitle(getLocalDiskFile().getName());
    }
    ServiceManager.ExecuteResponders(
      ServiceManager.serviceType.BEFORE_UPLOAD_SERVICE_ID, 
      new BeforeFileUploadResponderData(this));
    return 0;
  }

  public int PostExecute()
  {
    ServiceManager.ExecuteResponders(
      ServiceManager.serviceType.AFTER_UPLOAD_SERVICE_ID, 
      new AfterFileUploadRespoderData(this, this.mUploadedFile));

    if (isDeleteFile()) {
      try {
        getLocalDiskFile().delete();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return 0;
  }

  public Boolean IsExecutable() {
    boolean isExecutable = false;
    if (UploadOperation.NEW_REVISION == getUploadOperation()) {
      String fileID = getGDriveFile().getDFile().getId();
      if (fileID != null) {
        isExecutable = !fileID.isEmpty();
      }
    }
    return Boolean.valueOf(isExecutable);
  }

  public void setMimeType(String mMimeType)
  {
    this.mMimeType = mMimeType;
  }

  public String getMimeType()
  {
    return this.mMimeType;
  }

  public void setFileDescription(String mDescription)
  {
    this.mDescription = mDescription;
  }

  public String getFileDescription()
  {
    return this.mDescription;
  }

  public void setTitle(String mTitle)
  {
    this.mTitle = mTitle;
  }

  public String getTitle()
  {
    return this.mTitle;
  }

  public void setDeleteFile(boolean mDeleteFile)
  {
    this.mDeleteFile = mDeleteFile;
  }

  public boolean isDeleteFile()
  {
    return this.mDeleteFile;
  }

  public void setFolder(boolean mFolder)
  {
    this.mFolder = mFolder;
  }

  public boolean isFolder()
  {
    return this.mFolder;
  }

  public void setGDriveFile(GDriveFile mGDriveFile)
  {
    this.mGDriveFile = mGDriveFile;
  }

  public GDriveFile getGDriveFile()
  {
    return this.mGDriveFile;
  }

  public void setLocalDiskFile(java.io.File mLocalDiskFile)
  {
    this.mLocalDiskFile = mLocalDiskFile;
  }

  public java.io.File getLocalDiskFile()
  {
    return this.mLocalDiskFile;
  }

  public Boolean setUploadedFile(com.google.api.services.drive.model.File UploadedFile)
  {
    this.mUploadedFile = UploadedFile;
    return Boolean.valueOf(UploadedFile != null);
  }

  public com.google.api.services.drive.model.File getUploadedFile()
  {
    return this.mUploadedFile;
  }

  public void setUploadOperation(UploadOperation mUploadOperation)
  {
    this.mUploadOperation = mUploadOperation;
  }

  public UploadOperation getUploadOperation()
  {
    return this.mUploadOperation;
  }

  public void setUploadProgressListoner(IProgressListoner uploadProgressListoner)
  {
    this.uploadProgressListoner = uploadProgressListoner;
  }

  public IProgressListoner getUploadProgressListoner()
  {
    return this.uploadProgressListoner;
  }

  private void enableProgressListoner(MediaHttpUploader uploader) {
    uploader.setProgressListener(
      new UploadProgressListoner(getUploadProgressListoner()));
    if (!isFolder()) {
      long fileSize = getLocalDiskFile().length();
      long inKB = fileSize / 1024L;
      if (inKB > 512L) {
        uploader.setDirectUploadEnabled(false);
        uploader.setChunkSize(262144);
      }
    }
  }
}