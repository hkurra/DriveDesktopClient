package com.gdrive.desktop.client.cache;

/**
 * maintain caching of metadata of files of User drive 
 * 
 * @author harsh
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.gdrive.desktop.client.Global.SharedInstances;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;


public class GDriveFiles
{
  private static List<File> mAllFiles;
  private static List<TreeNodeInfo> mDirectoryStructure;
  private static List<TreeNodeInfo> mMyDriveDirectoryStructure;
  private static List<TreeNodeInfo> mTrashedDirectoryStructure;
  private static TreeNodeInfo mMyDriveRootNode ;
  private static TreeNodeInfo mTrashedRootNode ;
  private static HashMap<String, TreeNodeInfo> mAllFileTreeNodeInfo;
  private static HashMap<String, TreeNodeInfo> mAllFileParentTreeNodeInfo;
  
  //KEY CONSTANT FOR Directory structure
  public static final String SELF_KEY = "SELF";
  public static final String FILE_ID_KEY = "FILE_ID";
  public static final String IS_FOLDER_KEY = "IS_FOLDER";
  public static final String CHILD_KEY = "CHILD";
  public static final String REVISION = "REVISION";

  static
  {
	  mAllFileParentTreeNodeInfo = new HashMap<String, TreeNodeInfo>();
	  mAllFileTreeNodeInfo = new HashMap<String, TreeNodeInfo>();
	  mMyDriveRootNode = createDummyNode("Drive", true);
	  mTrashedRootNode = createDummyNode("Trashed", true);
    setMyDriveDirectoryStructure(new ArrayList<TreeNodeInfo>());
    setTrashedDirectoryStructure(new ArrayList<TreeNodeInfo>());
    setAllFiles(new ArrayList<File>());
    setDirectoryStructure(new ArrayList<TreeNodeInfo>());
  }

  public static void CacheAllFiles()
  {
    try
    {
      getAllFiles().clear();
      Drive.Files.List list = SharedInstances.DRIVE.files().list();
      FileList fileList = (FileList)list.execute();
      getAllFiles().addAll(fileList.getItems());
    } 
	catch (TokenResponseException tokenException) {
		
//		if (SharedInstances.requestCount <3) {
//			SharedInstances.changeUser();
//			SharedInstances.requestCount = 0;
//		}
	}
    catch (IOException e) {
      e.printStackTrace();
      getAllFiles().clear();
    } catch (Exception e) {
      e.printStackTrace();
      getAllFiles().clear();
    }
    finally
    {
      getDirectoryStructure().clear();
    }
    CreateDirectoryStructure();

    mMyDriveRootNode.put("CHILD", getMyDriveDirectoryStructure());
    mTrashedRootNode.put("CHILD", getTrashedDirectoryStructure());
    getDirectoryStructure().add(mMyDriveRootNode);
    getDirectoryStructure().add(mTrashedRootNode);

    printDirectoryStructure();
  }

  private static void childStructure(Object dummyContent)
  {
	  final List<TreeNodeInfo> childList = (List<TreeNodeInfo>) dummyContent;
    for (int childNode = 0; childNode < childList.size(); childNode++) {
      TreeNodeInfo content = (TreeNodeInfo)childList.get(childNode);
      System.out
        .println("->" + ((File)content.get("SELF")).getTitle());
      if (content.get("CHILD") != null) {
        System.out.print("->");
        childStructure(content.get("CHILD"));
      }
    }
  }

  private static void CreateDirectoryStructure()
  {
    for (int index = 0; index < getAllFiles().size(); index++) {
      List<ParentReference> parentList = ((File)getAllFiles().get(index))
        .getParents();
      File currentFile = (File)getAllFiles().get(index);

      if ((parentList.isEmpty()) || (!((ParentReference)parentList.get(0)).getIsRoot().booleanValue()))
        continue;
      if (((File)getAllFiles().get(index)).getMimeType().equals(SharedInstances.FOLDER_MIME_TYPE))
      {
        if ((currentFile.getExplicitlyTrashed() != null) && (currentFile.getExplicitlyTrashed().booleanValue())) {
          getTrashedDirectoryStructure().add(FolderProcessing(
            (File)getAllFiles()
            .get(index), mTrashedRootNode));
        }
        else {
          getMyDriveDirectoryStructure().add(FolderProcessing(
            (File)getAllFiles()
            .get(index), mMyDriveRootNode));
        }

      }
      else if ((currentFile.getExplicitlyTrashed() != null) && (currentFile.getExplicitlyTrashed().booleanValue())) {
        getTrashedDirectoryStructure().add(FileProcessing((File)getAllFiles().get(index), mTrashedRootNode));
      }
      else
        getMyDriveDirectoryStructure().add(
          FileProcessing((File)getAllFiles().get(index), mMyDriveRootNode));
    }
  }

  public static TreeNodeInfo FileProcessing(File driveFileRef, TreeNodeInfo parentNodeInfo)
  {
	String fileID = driveFileRef.getId();
    TreeNodeInfo treeNodeInfo = new TreeNodeInfo();
    treeNodeInfo.put("SELF", driveFileRef);
    treeNodeInfo.put("CHILD", null);
    treeNodeInfo.put("FILE_ID", fileID);
    treeNodeInfo.put("IS_FOLDER", new Boolean(false));

    mAllFileTreeNodeInfo.put(fileID, treeNodeInfo);
    mAllFileParentTreeNodeInfo.put(fileID, parentNodeInfo);
    
    return treeNodeInfo;
  }

  public static TreeNodeInfo FolderProcessing(File driveFileRef, TreeNodeInfo parentNodeInfo)
  {
    TreeNodeInfo treeNodeInfo = new TreeNodeInfo();
    treeNodeInfo.put("SELF", driveFileRef);
    treeNodeInfo.put("FILE_ID", driveFileRef.getId());
    treeNodeInfo.put("IS_FOLDER", new Boolean(true));

    List<TreeNodeInfo> childList = new ArrayList<TreeNodeInfo>();
    Drive.Children.List request = null;
    try {
      request = SharedInstances.DRIVE.children().list(
        driveFileRef.getId());
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    do
      try {
        ChildList children = (ChildList)request.execute();
        for (ChildReference child : children.getItems())
        {
          File childFileRef = searchFileID(child.getId(), 
            false, false);

          Boolean isParentTrashed = Boolean.valueOf((driveFileRef.getExplicitlyTrashed() != null) && (driveFileRef.getExplicitlyTrashed().booleanValue()));
          Boolean isFileTrashded = Boolean.valueOf((childFileRef.getExplicitlyTrashed() != null) && (childFileRef.getExplicitlyTrashed().booleanValue()));

          Boolean isFallUnderTrashedDirStruct = Boolean.valueOf((!isParentTrashed.booleanValue()) && (isFileTrashded.booleanValue()));
          boolean testVar;
          if (childFileRef.getMimeType().equals(SharedInstances.FOLDER_MIME_TYPE)) {
            testVar = isFallUnderTrashedDirStruct.booleanValue() ? getTrashedDirectoryStructure().add(FolderProcessing(childFileRef, treeNodeInfo)) : childList.add(FolderProcessing(childFileRef, treeNodeInfo));
          }
          else
          {
            testVar = isFallUnderTrashedDirStruct.booleanValue() ? getTrashedDirectoryStructure().add(FileProcessing(childFileRef, treeNodeInfo)) : childList.add(FileProcessing(childFileRef, treeNodeInfo));
          }
          if(testVar == false) {
    
          }
        }
        request.setPageToken(children.getNextPageToken());
      } catch (IOException e) {
        System.out.println("An error occurred: " + e);
        request.setPageToken(null);
      }
    while ((request.getPageToken() != null) && 
      (request.getPageToken().length() > 0));

    if (childList.size() == 0) {
      childList.add(createDummyNode("Empty", false));
    }
    treeNodeInfo.put("CHILD", childList);

    mAllFileTreeNodeInfo.put(driveFileRef.getId(), treeNodeInfo);
    mAllFileParentTreeNodeInfo.put(driveFileRef.getId(), parentNodeInfo);
    return treeNodeInfo;
  }

  public static List<File> getAllFiles()
  {
    return mAllFiles;
  }

  public static List<TreeNodeInfo> getDirectoryStructure()
  {
    return mDirectoryStructure;
  }

  private static void printDirectoryStructure() {
    for (int i = 0; i < getDirectoryStructure().size(); i++) {
      TreeNodeInfo content = (TreeNodeInfo)getDirectoryStructure().get(i);
      System.out.println(((File)content.get("SELF")).getTitle());
      if (content.get("CHILD") == null) {
        continue;
      }
      childStructure(content.get("CHILD"));
    }
  }

	/**
	 * 
	 * search for fileID within cached files & return match file
	 * 
	 * <p>
	 * if refreshCaching is true this will reread whole Drive(very costlier call
	 * so think twice before using it)
	 * </p>
	 * <p>
	 * if serverversion is true it will redirect search call to Google Drive
	 * </p>
	 * 
	 * @param --> fileID
	 * @param --> refreshCaching
	 * @param --> serverVersion
	 * @return <-- Searched File or null in case file not found
	 */
  public static File searchFileID(String fileID, boolean refreshCaching, boolean serverVersion)
  {
    File searchFile = null;
    if (refreshCaching) {
      CacheAllFiles();
    }
    if (serverVersion)
      try {
        searchFile = 
          (File)SharedInstances.DRIVE.files().get(fileID)
          .execute();
      } catch (IOException e) {
        e.printStackTrace();
      }
    else {
      for (File file : getAllFiles()) {
        if (file.getId().equals(fileID)) {
          searchFile = file;
          break;
        }
      }
    }
    return searchFile;
  }

  public static TreeNodeInfo getFileTreeNodeInfo(String fileId)
  {
    return (TreeNodeInfo)mAllFileTreeNodeInfo.get(fileId);
  }
  
  public static TreeNodeInfo getFileParentTreeNodeInfo(String fileId)
  {
    return (TreeNodeInfo)mAllFileParentTreeNodeInfo.get(fileId);
  }

  private static void setAllFiles(List<File> allFiles)
  {
    mAllFiles = allFiles;
  }

  public static void setDirectoryStructure(List<TreeNodeInfo> directoryStructure)
  {
    mDirectoryStructure = directoryStructure;
  }
  private static TreeNodeInfo createDummyNode(String rootNodename, boolean isFolder) {
    TreeNodeInfo rootNode = new TreeNodeInfo();
    File rootNodeDummyFile = new File();

    rootNodeDummyFile.setTitle(rootNodename);
    rootNodeDummyFile.setId("-1");
    rootNode.put("SELF", rootNodeDummyFile);
    rootNode.put("FILE_ID", rootNodeDummyFile.getId());
    rootNode.put("IS_FOLDER", new Boolean(isFolder));

    return rootNode;
  }

  /**
   * <p>must call it after deleting some node permanently from tree</p>
   * <p>otherwise it will lead to wrong cache + extra memory</p>
   * 
   * @param --> FileID
   */
   static void removeTreeNodeRefrence(String FileID) {
	  mAllFileParentTreeNodeInfo.remove(FileID);
	  mAllFileTreeNodeInfo.remove(FileID);
  }
  
  public static void setMyDriveDirectoryStructure(List<TreeNodeInfo> myDriveDirectoryStructure)
  {
    mMyDriveDirectoryStructure = myDriveDirectoryStructure;
  }

  public static List<TreeNodeInfo> getMyDriveDirectoryStructure()
  {
    return mMyDriveDirectoryStructure;
  }

  public static void setTrashedDirectoryStructure(List<TreeNodeInfo> trashedDirectoryStructure)
  {
    mTrashedDirectoryStructure = trashedDirectoryStructure;
  }

  public static TreeNodeInfo getMyDriveRootNode()
  {
    return mMyDriveRootNode;
  }
  
  public static TreeNodeInfo getTrashedRootNode()
  {
    return mTrashedRootNode;
  }
  
  public static List<TreeNodeInfo> getTrashedDirectoryStructure()
  {
    return mTrashedDirectoryStructure;
  }
}