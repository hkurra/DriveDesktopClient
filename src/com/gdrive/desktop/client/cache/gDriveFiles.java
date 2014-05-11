package com.gdrive.desktop.client.cache;

/**
 * maintain caching of metadata of files of User drive 
 * 
 * @author harsh
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gdrive.desktop.client.Global.SharedInstances;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

public class gDriveFiles {
	/**
	 * cache copy of drive files
	 */
	private static List<File> mAllFiles;
	/**
	 * directory structure of Files of Drive
	 */
	private static List<TreeNodeInfo> mDirectoryStructure;

	// STRING CONSTANT AS KEY FOR HASHMAP
	public final static String SELF_KEY = "SELF";
	public final static String FILE_ID_KEY = "FILE_ID";
	public final static String IS_FOLDER_KEY = "IS_FOLDER";
	public final static String CHILD_KEY = "CHILD";
	public final static String REVISION = "REVISION";

	/**
	 * static block
	 */
	static {
		setAllFiles(new ArrayList<File>());
		setDirectoryStructure(new ArrayList<TreeNodeInfo>());
	}

	/**
	 * </p>do caching of drive files</p>
	 * <p>
	 * Must Call this method before any file operation and validate current
	 * cache
	 * </p>
	 * <p>
	 * its your responsibility to cache old copy of structure in case of failure
	 * of refreshing this caching always maintain fresh structure until you
	 * refresh
	 * </p>
	 */
	public static void CacheAllFiles() {
		try {
			getAllFiles().clear();
			final Drive.Files.List list = SharedInstances.DRIVE.files().list();
			final FileList fileList = list.execute();
			getAllFiles().addAll(fileList.getItems());
		} catch (final IOException e) {
			e.printStackTrace();
			getAllFiles().clear();
		} catch (final Exception e) {
			e.printStackTrace();
			getAllFiles().clear();
		} finally {
			// (TODO need testing)in case of failure or refresh we have to clear
			// this structure otherwise it lead to wrong information
			// so its your responsibility to cache old copy of structure in case
			// of failure of refreshing
			// this caching always maintain fresh structure until you refresh
			getDirectoryStructure().clear();
		}
		CreateDirectoryStructure();
		
		printDirectoryStructure();
	}

	private static void childStructure(final Object dummyContent) {

		final List<TreeNodeInfo> childList = (List<TreeNodeInfo>) dummyContent;
		for (int childNode = 0; childNode < childList.size(); childNode++) {
			final TreeNodeInfo content = childList.get(childNode);
			System.out
			.println("->" + ((File) content.get(SELF_KEY)).getTitle());
			if (content.get(CHILD_KEY) != null) {
				System.out.print("->");
				childStructure(content.get(CHILD_KEY));
			}
		}
	}

	/**
	 * replicate and cache google Drive current directory structure must call
	 * this to refresh UI
	 */
	// TODO 9/feb/14 write now i know only this way to implement may be some API
	// already there
	// TODO NEED TO HANDLE CASE OF FILE UNDER MULTIPLE FOLDER AND FILE UNDER
	// TRASH and shared with me FoLDER
	private static void CreateDirectoryStructure() {
		for (int index = 0; index < getAllFiles().size(); index++) {
			final List<ParentReference> parentList = getAllFiles().get(index)
			.getParents();

			// if it is a file/folder under default(MyDrive) directory
			if (!parentList.isEmpty() && parentList.get(0).getIsRoot()) {
				// if it is file
				File currentFile = getAllFiles().get(index);
				if (currentFile.getFileExtension() != null) {
					if (currentFile.getExplicitlyTrashed() != null && currentFile.getExplicitlyTrashed()) {
						continue;
					}
					getDirectoryStructure().add(
							FileProcessing(getAllFiles().get(index)));
				}
				// if it is folder
				else if (getAllFiles().get(index).getFileExtension() == null) {
					final TreeNodeInfo folderDict = FolderProcessing(getAllFiles()
							.get(index));
					getDirectoryStructure().add(folderDict);
				}
			}
		}
	}

	/**
	 * Do file Processing
	 * 
	 * @param driveFileRef
	 * @return TreeNodeInfo
	 */
	private static TreeNodeInfo FileProcessing(final File driveFileRef) {

		final TreeNodeInfo fileDict = new TreeNodeInfo();
		fileDict.put(SELF_KEY, driveFileRef);
		fileDict.put(CHILD_KEY, null);
		fileDict.put(FILE_ID_KEY, driveFileRef.getId());
		fileDict.put(IS_FOLDER_KEY, new Boolean(false));

		return fileDict;
	}

	/**
	 * DO folder Processing imply find out its child hierarchy and return as map
	 * structure
	 * 
	 * @param driveFileRef
	 * @return
	 */
	private static TreeNodeInfo FolderProcessing(final File driveFileRef) {

		final TreeNodeInfo treeNodeInfo = new TreeNodeInfo();
		treeNodeInfo.put(SELF_KEY, driveFileRef);
		treeNodeInfo.put(FILE_ID_KEY, driveFileRef.getId());
		treeNodeInfo.put(IS_FOLDER_KEY, new Boolean(true));

		List<TreeNodeInfo> childList = new ArrayList<TreeNodeInfo>();
		Drive.Children.List request = null;
		try {
			request = SharedInstances.DRIVE.children().list(
					driveFileRef.getId());
		} catch (final IOException e1) {
			e1.printStackTrace();
		}
		do {
			try {
				final ChildList children = request.execute();
				for (final ChildReference child : children.getItems()) {

					final File childFileRef = searchFileID(child.getId(),
							false, false);

					// if it is file
					if (childFileRef.getFileExtension() != null) {
						childList.add(FileProcessing(childFileRef));
					}

					// if it is folder
					else if (childFileRef.getFileExtension() == null) {
						childList.add(FolderProcessing(childFileRef));
					}
				}
				request.setPageToken(children.getNextPageToken());
			} catch (final IOException e) {
				System.out.println("An error occurred: " + e);
				request.setPageToken(null);
			}
		} while (request.getPageToken() != null
				&& request.getPageToken().length() > 0);

		if (childList.size() == 0)
			childList = null;

		treeNodeInfo.put(CHILD_KEY, childList);
		return treeNodeInfo;
	}

	/**
	 * @return the mAllFiles
	 */
	public static List<File> getAllFiles() {
		return mAllFiles;
	}

	/**
	 * @return the mDirectoryStructure
	 */
	public static List<TreeNodeInfo> getDirectoryStructure() {
		return mDirectoryStructure;
	}

	private static void printDirectoryStructure() {
		for (int i = 0; i < mDirectoryStructure.size(); i++) {
			final TreeNodeInfo content = mDirectoryStructure.get(i);
			System.out.println(((File) content.get(SELF_KEY)).getTitle());
			if (content.get(CHILD_KEY) != null) {
				// System.out.print("->"
				// +((File)content.get("SELF")).getTitle());
				childStructure(content.get(CHILD_KEY));
			}
		}
	}

	/**
	 * 
	 * search for fileID within cached files & return match file
	 * 
	 * <p>
	 * if refreshCashing is true this will reread whole Drive(very costlier call
	 * so think twice before using it)
	 * </p>
	 * <p>
	 * if serverversion is true it will redirect search call to Google Drive
	 * </p>
	 * 
	 * @param fileID
	 * @param refreshCaching
	 * @param serverVersion
	 * @return Searched File or null in case file not found
	 */
	static public File searchFileID(final String fileID,
			final boolean refreshCaching, final boolean serverVersion) {

		File searchFile = null;
		if (refreshCaching) {
			CacheAllFiles();
		}
		if (serverVersion) {
			try {
				searchFile = SharedInstances.DRIVE.files().get(fileID)
				.execute();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		} else {
			for (final File file : getAllFiles()) {
				if (file.getId().equals(fileID)) {
					searchFile = file;
					break;
				}
			}
		}
		return searchFile;
	}

	/**
	 * @param mAllFiles
	 *            the mAllFiles to set
	 */
	private static void setAllFiles(final List<File> mAllFiles) {
		gDriveFiles.mAllFiles = mAllFiles;
	}

	/**
	 * @param mDirectoryStructure
	 *            the mDirectoryStructure to set
	 */
	public static void setDirectoryStructure(
			final List<TreeNodeInfo> mDirectoryStructure) {
		gDriveFiles.mDirectoryStructure = mDirectoryStructure;
	}

}
