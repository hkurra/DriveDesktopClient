package com.gdrive.desktop.client.cache;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gdrive.desktop.client.Global.DriveDesktopClient;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Revision;
import com.google.api.services.drive.model.RevisionList;

/**
 * <p>
 * Maintain caching of File's revision
 * </p>
 * 
 * @author harsh
 * 
 */
public class GDriveFileRevisions {

	private static HashMap<String, List<Revision>> mAllFileRevision;

	static {
		mAllFileRevision = new HashMap<String, List<Revision>>();
	}

	/**
	 * <p>
	 * refresh all files revision
	 * </p>
	 * 
	 * @throws Exception
	 */
	public static void cacheAllFileRevision() throws Exception {
		final List<File> allFile = GDriveFiles.getAllFiles();
		getAllFileRevision().clear();
		try {
			for (final File file : allFile) {

				if (!file.getMimeType().equals(
						DriveDesktopClient.FOLDER_MIME_TYPE)) {
					getAllFileRevision().put(file.getId(),
							getFileRevisionFromServer(file.getId()));
				}
			}

		} catch (final Exception e) {
			getAllFileRevision().clear();
			Logger.getLogger(DriveDesktopClient.class.getName()).log(
					Level.SEVERE, null, e);
		}
	}

	/**
	 * <p>
	 * get file revision from server
	 * </p>
	 * 
	 * @param fileID
	 * @return
	 * @throws Exception
	 */
	public static List<Revision> getFileRevisionFromServer(final String fileID)
	throws Exception {

		try {
			final RevisionList revisions = DriveDesktopClient.DRIVE.revisions()
			.list(fileID).execute();
			return revisions.getItems();
		} catch (final IOException e) {
			System.out.print(GDriveFiles.searchFileID(fileID, false, true)
					.getTitle());
			System.out.print(fileID);
			throw e;
		}
	}

	/**
	 * sync drive file revision to local cache
	 * 
	 * @param fileID
	 */
	public static void updateFileRevision(final String fileID) {

		try {
			getAllFileRevision().put(fileID, getFileRevisionFromServer(fileID));
		} catch (final Exception e) {

		}
	}

	/**
	 * 
	 * search for fileID within cached filesRevsions & return match
	 * fileRevisions
	 * 
	 * <p>
	 * if refreshCaching is true this will reread whole Drive(very costlier call
	 * so think twice before using it)
	 * </p>
	 * <p>
	 * if server version is true it will redirect search call to Google Drive
	 * <p>
	 * use it if query of file revision is not frequent
	 * </p>
	 * </p>
	 * 
	 * @param fileID
	 * @param refreshCaching
	 * @param serverVersion
	 * @return Searched File or null in case file not found
	 */
	public static List<Revision> searchFileRevisionsByID(final String fileID,
			final boolean refreshCaching, final boolean serverVersion) {

		List<Revision> fileRevisions = null;
		if (refreshCaching) {
			try {
				cacheAllFileRevision();
			} catch (Exception e) {
				Logger.getLogger(DriveDesktopClient.class.getName()).log(
						Level.SEVERE, null, e);
			}
		}
		if (serverVersion) {
			try {
				fileRevisions = getFileRevisionFromServer(fileID);
			} catch (final Exception e) {
				Logger.getLogger(DriveDesktopClient.class.getName()).log(
						Level.SEVERE, null, e);
			}
		} else {

			for (final Entry<String, List<Revision>> entry : getAllFileRevision()
					.entrySet()) {
				if (entry.getKey().equals(fileID)) {
					fileRevisions = entry.getValue();
				}
			}
		}
		return fileRevisions;
	}

	public static List<Revision> getFileRevision(String fileID) {
		return searchFileRevisionsByID(fileID, false, false);
	}

	/**
	 * @return the mAllFileRevision
	 */
	public static HashMap<String, List<Revision>> getAllFileRevision() {
		return mAllFileRevision;
	}

	/**
	 * <p>
	 * remove file revision from cache
	 * </p>
	 * 
	 * @param fileID
	 */
	public static void deleteRevision(String fileID) {
		getAllFileRevision().remove(fileID);
	}

}
