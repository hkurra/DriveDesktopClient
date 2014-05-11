package com.gdrive.desktop.client.cache;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.gdrive.desktop.client.Global.SharedInstances;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Revision;
import com.google.api.services.drive.model.RevisionList;

public class gDriveFileRevisions {

	private static HashMap<String, List<Revision>> mAllFileRevision;

	static {
		mAllFileRevision = new HashMap<String, List<Revision>>();
	}

	public static void cacheAllFileRevision() {
		final List<File> allFile = gDriveFiles.getAllFiles();
		getAllFileRevision().clear();
		try {
			for (final File file : allFile) {

				getAllFileRevision().put(file.getId(),
						getFileRevision(file.getId()));
			}
		} catch (final Exception e) {
			getAllFileRevision().clear();
		}
	}

	private static List<Revision> getFileRevision(final String fileID)
			throws Exception {

		try {
			final RevisionList revisions = SharedInstances.DRIVE.revisions()
					.list(fileID).execute();
			return revisions.getItems();
		} catch (final IOException e) {
			throw e;
		}
	}

	public void updateFileRevision(final String fileID) {

		try {
			getAllFileRevision().put(fileID, getFileRevision(fileID));
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
	 * if serverversion is true it will redirect search call to Google Drive
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
			cacheAllFileRevision();
		}
		if (serverVersion) {
			try {
				fileRevisions = getFileRevision(fileID);
			} catch (final Exception e) {
				e.printStackTrace();
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

	/**
	 * @return the mAllFileRevision
	 */
	public static HashMap<String, List<Revision>> getAllFileRevision() {
		return mAllFileRevision;
	}

}
