package com.gdrive.desktop.client.cache;

/**
 * maintain caching of meta data of files of User drive 
 * 
 * @author harsh
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gdrive.desktop.client.Global.DriveDesktopClient;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

public class GDriveFiles {
	private static List<File> mAllFiles;
	private static List<TreeNodeInfo> mDirectoryStructure;
	private static List<TreeNodeInfo> mMyDriveDirectoryStructure;
	private static List<TreeNodeInfo> mTrashedDirectoryStructure;
	private static TreeNodeInfo mMyDriveRootNode;
	private static TreeNodeInfo mTrashedRootNode;
	private static HashMap<String, TreeNodeInfo> mAllFileTreeNodeInfo;
	private static HashMap<String, TreeNodeInfo> mAllFileParentTreeNodeInfo;

	// KEY CONSTANT FOR Directory structure
	public static final String SELF_KEY = "SELF";
	public static final String FILE_ID_KEY = "FILE_ID";
	public static final String IS_FOLDER_KEY = "IS_FOLDER";
	public static final String CHILD_KEY = "CHILD";
	public static final String REVISION = "REVISION";

	public static final String MYDRIVE_ROOT_NODE_ID = "MyDriveRootNode";
	public static final String TRASHED_ROOT_NODE_ID = "TrashedRootNode";

	static {
		mAllFileParentTreeNodeInfo = new HashMap<String, TreeNodeInfo>();
		mAllFileTreeNodeInfo = new HashMap<String, TreeNodeInfo>();
		mMyDriveRootNode = createDummyNode("MY Drive", true);
		mTrashedRootNode = createDummyNode("Trashed", true);

		setMyDriveDirectoryStructure(new ArrayList<TreeNodeInfo>());
		setTrashedDirectoryStructure(new ArrayList<TreeNodeInfo>());
		setAllFiles(new ArrayList<File>());
		setDirectoryStructure(new ArrayList<TreeNodeInfo>());

		mMyDriveRootNode.put(FILE_ID_KEY, MYDRIVE_ROOT_NODE_ID);
		mTrashedRootNode.put(FILE_ID_KEY, TRASHED_ROOT_NODE_ID);

		mAllFileTreeNodeInfo.put(MYDRIVE_ROOT_NODE_ID, mMyDriveRootNode);
		mAllFileTreeNodeInfo.put(TRASHED_ROOT_NODE_ID, mTrashedRootNode);

	}

	/**
	 * Cache All files meta data and build directory structure
	 * 
	 * @throws Exception
	 */
	public static void CacheAllFiles() throws Exception {
		try {
			getAllFiles().clear();
			Drive.Files.List list = DriveDesktopClient.DRIVE.files().list();
			FileList fileList = (FileList) list.execute();
			getAllFiles().addAll(fileList.getItems());

			do {
				try {
					list.setPageToken(fileList.getNextPageToken());
					fileList = (FileList) list.execute();
					getAllFiles().addAll(fileList.getItems());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} while (list.getPageToken() != null
					&& list.getPageToken().length() > 0);
			createDirectoryStructure();

			mMyDriveRootNode.put("CHILD", getMyDriveDirectoryStructure());
			mTrashedRootNode.put("CHILD", getTrashedDirectoryStructure());
			getDirectoryStructure().add(mMyDriveRootNode);
			getDirectoryStructure().add(mTrashedRootNode);

			printDirectoryStructure();
		} catch (TokenResponseException tokenException) {
			Logger.getLogger(DriveDesktopClient.class.getName()).log(
					Level.SEVERE, null, tokenException);
			throw tokenException;
			// TODO RETRY LOGIN HERE
			// if (SharedInstances.requestCount <3) {
			// SharedInstances.changeUser();
			// SharedInstances.requestCount = 0;
			// }
		} catch (IOException e) {
			Logger.getLogger(DriveDesktopClient.class.getName()).log(
					Level.SEVERE, null, e);
			getAllFiles().clear();
			throw e;
		} catch (Exception e) {
			Logger.getLogger(DriveDesktopClient.class.getName()).log(
					Level.SEVERE, null, e);
			getAllFiles().clear();
			throw e;
		} finally {
			getDirectoryStructure().clear();
			mAllFileTreeNodeInfo.clear();
			mAllFileParentTreeNodeInfo.clear();
			getMyDriveDirectoryStructure().clear();
			getTrashedDirectoryStructure().clear();
		}
	}

	/**
	 * create Directory structure for all derive files
	 * 
	 * @throws Exception
	 */
	private static void createDirectoryStructure() throws Exception {
		try {
			for (int index = 0; index < getAllFiles().size(); index++) {
				List<ParentReference> parentList = ((File) getAllFiles().get(
						index)).getParents();
				File currentFile = (File) getAllFiles().get(index);

				if ((parentList.isEmpty())
						|| (!((ParentReference) parentList.get(0)).getIsRoot()
								.booleanValue()))
					continue;
				if (((File) getAllFiles().get(index)).getMimeType().equals(
						DriveDesktopClient.FOLDER_MIME_TYPE)) {
					if ((currentFile.getExplicitlyTrashed() != null)
							&& (currentFile.getExplicitlyTrashed()
									.booleanValue())) {
						getTrashedDirectoryStructure().add(
								folderProcessing((File) getAllFiles()
										.get(index), mTrashedRootNode));
					} else {
						getMyDriveDirectoryStructure().add(
								folderProcessing((File) getAllFiles()
										.get(index), mMyDriveRootNode));
					}

				} else if ((currentFile.getExplicitlyTrashed() != null)
						&& (currentFile.getExplicitlyTrashed().booleanValue())) {
					getTrashedDirectoryStructure().add(
							fileProcessing((File) getAllFiles().get(index),
									mTrashedRootNode));
				} else
					getMyDriveDirectoryStructure().add(
							fileProcessing((File) getAllFiles().get(index),
									mMyDriveRootNode));
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * create treeNodeInfo for File
	 * 
	 * You May Not need it
	 * 
	 * @param driveFileRef
	 * @param parentNodeInfo
	 * @return TreeNodeInfo
	 */
	public static TreeNodeInfo fileProcessing(File driveFileRef,
			TreeNodeInfo parentNodeInfo) {
		String fileID = driveFileRef.getId();
		TreeNodeInfo treeNodeInfo = new TreeNodeInfo();
		treeNodeInfo.put("SELF", driveFileRef);
		treeNodeInfo.put("CHILD", null);
		treeNodeInfo.put("FILE_ID", fileID);
		treeNodeInfo.put("IS_FOLDER", new Boolean(false));

		mAllFileTreeNodeInfo.put(fileID, treeNodeInfo);
		mAllFileParentTreeNodeInfo.put(fileID, parentNodeInfo);

		if (!getAllFiles().contains(driveFileRef)) {
			getAllFiles().add(driveFileRef);
		}

		return treeNodeInfo;
	}

	/**
	 * create treeNodeInfo for Folder
	 * 
	 * You May Not need it
	 * 
	 * @param driveFileRef
	 * @param parentNodeInfo
	 * @return TreeNodeInfo
	 * @throws Exception
	 */
	public static TreeNodeInfo folderProcessing(File driveFileRef,
			TreeNodeInfo parentNodeInfo) throws Exception {

		TreeNodeInfo treeNodeInfo = new TreeNodeInfo();
		treeNodeInfo.put("SELF", driveFileRef);
		treeNodeInfo.put("FILE_ID", driveFileRef.getId());
		treeNodeInfo.put("IS_FOLDER", new Boolean(true));

		List<TreeNodeInfo> childList = new ArrayList<TreeNodeInfo>();
		Drive.Children.List request = null;
		try {
			request = DriveDesktopClient.DRIVE.children().list(
					driveFileRef.getId()); // TODO reduced this call, its
			// possible but need brainy night
		} catch (IOException e1) {
			Logger.getLogger(DriveDesktopClient.class.getName()).log(
					Level.SEVERE, null, e1);
			throw e1;
		}
		do {
			try {
				ChildList children = (ChildList) request.execute();
				for (ChildReference child : children.getItems()) {
					File childFileRef = searchFileID(child.getId(), false,
							false);
					// TO DO in some cases childFilRef is null specially if it
					// is shared file even if i make above 2 parameter true
					if (childFileRef == null)
						continue;
					Boolean isParentTrashed = Boolean.valueOf((driveFileRef
							.getExplicitlyTrashed() != null)
							&& (driveFileRef.getExplicitlyTrashed()
									.booleanValue()));
					Boolean isFileTrashded = Boolean.valueOf((childFileRef
							.getExplicitlyTrashed() != null)
							&& (childFileRef.getExplicitlyTrashed()
									.booleanValue()));

					Boolean isFallUnderTrashedDirStruct = Boolean
							.valueOf((!isParentTrashed.booleanValue())
									&& (isFileTrashded.booleanValue()));
					boolean testVar;
					if (childFileRef.getMimeType().equals(
							DriveDesktopClient.FOLDER_MIME_TYPE)) {
						testVar = isFallUnderTrashedDirStruct.booleanValue() ? getTrashedDirectoryStructure()
								.add(folderProcessing(childFileRef,
										treeNodeInfo)) : childList
								.add(folderProcessing(childFileRef,
										treeNodeInfo));
					} else {
						testVar = isFallUnderTrashedDirStruct.booleanValue() ? getTrashedDirectoryStructure()
								.add(fileProcessing(childFileRef, treeNodeInfo))
								: childList.add(fileProcessing(childFileRef,
										treeNodeInfo));
					}
					if (testVar == false) {

					}
				}
				request.setPageToken(children.getNextPageToken());
			} catch (IOException e) {
				Logger.getLogger(DriveDesktopClient.class.getName()).log(
						Level.SEVERE, null, e);
				request.setPageToken(null);
			}
		} while ((request.getPageToken() != null)
				&& (request.getPageToken().length() > 0));

		treeNodeInfo.addChildNodes(childList);

		mAllFileTreeNodeInfo.put(driveFileRef.getId(), treeNodeInfo);
		mAllFileParentTreeNodeInfo.put(driveFileRef.getId(), parentNodeInfo);

		return treeNodeInfo;
	}

	/**
	 * Get all Drive Files MetaData
	 * 
	 * @return List<File>
	 */
	public static List<File> getAllFiles() {
		return mAllFiles;
	}

	/**
	 * Get directory structure of Drive Files
	 * <p>
	 * usage eg. JTree jTree = new javax.swing.JTree(); TreeNodeInfo rootNode =
	 * GDriveFiles.createDummyNode("Your RootNode Name", true);
	 * rootNode.put(GDriveFiles.CHILD_KEY, GDriveFiles.getDirectoryStructure);
	 * //Write your own custom data model implementing swings TreeModel
	 * //Wrapper for All necessary Abstract Method You need to implement for
	 * TreeModel are available under TreeNodeInfo //Just call them blindly like:
	 * getIndexOfChild { TreeNodeInfo treeInfo = (TreeNodeInfo)parent; return
	 * treeInfo.getIndexOfChild((TreeNodeInfo) child);} CustomDataModel
	 * treeDataModel = new CustomDataModel(rootNode);
	 * jTree.setModel(treeDataModel);
	 * 
	 * @return List<TreeNodeInfo>
	 */
	public static List<TreeNodeInfo> getDirectoryStructure() {
		return mDirectoryStructure;
	}

	// Unusable code construct
	private static void printDirectoryStructure() {
		for (int i = 0; i < getDirectoryStructure().size(); i++) {
			TreeNodeInfo content = (TreeNodeInfo) getDirectoryStructure()
					.get(i);
			System.out.println(((File) content.get("SELF")).getTitle());
			if (content.get("CHILD") == null) {
				continue;
			}
			childStructure(content.get("CHILD"));
		}
	}

	private static void childStructure(Object dummyContent) {
		final List<TreeNodeInfo> childList = (List<TreeNodeInfo>) dummyContent;
		for (int childNode = 0; childNode < childList.size(); childNode++) {
			TreeNodeInfo content = (TreeNodeInfo) childList.get(childNode);
			System.out.println("->" + ((File) content.get("SELF")).getTitle());
			if (content.get("CHILD") != null) {
				System.out.print("->");
				childStructure(content.get("CHILD"));
			}
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
	public static File searchFileID(String fileID, boolean refreshCaching,
			boolean serverVersion) {
		File searchFile = null;
		if (refreshCaching) {
			try {
				CacheAllFiles();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (serverVersion)
			try {
				searchFile = (File) DriveDesktopClient.DRIVE.files()
						.get(fileID).execute();
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

	/**
	 * <p>
	 * used to crate Dummy Node Which either does Not exist at Drive or does not
	 * have File ID like Trashed Node(May be i am wrong) Or you own root node
	 * </p>
	 * 
	 * @param rootNodename
	 * @param isFolder
	 * @return
	 */
	public static TreeNodeInfo createDummyNode(String rootNodename,
			boolean isFolder) {
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
	 * <p>
	 * must call it after deleting some node permanently from tree
	 * </p>
	 * <p>
	 * otherwise it will lead to wrong cache + extra memory
	 * </p>
	 * 
	 * @param --> FileID
	 */
	static void removeTreeNodeRefrence(String FileID) {
		mAllFileParentTreeNodeInfo.remove(FileID);
		mAllFileTreeNodeInfo.remove(FileID);
	}

	private static boolean isUnderMyDrive(File fileRef) {
		boolean underMYdrive = false;

		List<ParentReference> parentList = fileRef.getParents();
		if (!parentList.isEmpty()) {
			for (ParentReference parentReference : parentList) {
				if (parentReference.getIsRoot()) {
					underMYdrive = true;
					break;
				}
			}
		}
		return underMYdrive;
	}

	/**
	 * get TreeNodeInfo for any file if it exist under tree
	 * 
	 * @param fileId
	 * @return TreeNodeInfo
	 */
	public static TreeNodeInfo getFileTreeNodeInfo(String fileId) {
		return (TreeNodeInfo) (mAllFileTreeNodeInfo.get(fileId));
	}

	/**
	 * get Parent TreeNodeInfo for any File if it exist under tree
	 * 
	 * @param fileId
	 * @return
	 */
	public static TreeNodeInfo getFileParentTreeNodeInfo(String fileId) {
		return (TreeNodeInfo) (mAllFileParentTreeNodeInfo.get(fileId));
	}

	/**
	 * @param allFiles
	 */
	private static void setAllFiles(List<File> allFiles) {
		mAllFiles = allFiles;
	}

	/**
	 * @param directoryStructure
	 */
	private static void setDirectoryStructure(
			List<TreeNodeInfo> directoryStructure) {
		mDirectoryStructure = directoryStructure;
	}

	/**
	 * @param myDriveDirectoryStructure
	 */
	private static void setMyDriveDirectoryStructure(
			List<TreeNodeInfo> myDriveDirectoryStructure) {
		mMyDriveDirectoryStructure = myDriveDirectoryStructure;
	}

	/**
	 * @return
	 */
	public static List<TreeNodeInfo> getMyDriveDirectoryStructure() {
		return mMyDriveDirectoryStructure;
	}

	/**
	 * @param trashedDirectoryStructure
	 */
	private static void setTrashedDirectoryStructure(
			List<TreeNodeInfo> trashedDirectoryStructure) {
		mTrashedDirectoryStructure = trashedDirectoryStructure;
	}

	/**
	 * @return
	 */
	public static TreeNodeInfo getMyDriveRootNode() {
		return mMyDriveRootNode;
	}

	/**
	 * @return
	 */
	public static TreeNodeInfo getTrashedRootNode() {
		return mTrashedRootNode;
	}

	/**
	 * @return
	 */
	public static List<TreeNodeInfo> getTrashedDirectoryStructure() {
		return mTrashedDirectoryStructure;
	}
}