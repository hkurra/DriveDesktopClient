package com.gdrive.desktop.client.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.api.services.drive.model.File;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * 
 * @author harsh
 */
public class TreeNodeInfo extends HashMap<String, Object> {

	public Object getChild(final int index) {
		Object childNodeInfo = null;
		if (get(GDriveFiles.CHILD_KEY) != null) {
			final List<HashMap<String, Object>> hjk = (List<HashMap<String, Object>>) get(GDriveFiles.CHILD_KEY);
			childNodeInfo = hjk.get(index);
		}
		return childNodeInfo;
	}

	public int getChildCount() {

		int childCount = 0;
		final List<TreeNodeInfo> hjk = (List<TreeNodeInfo>) get(GDriveFiles.CHILD_KEY);
		if (hjk != null) {
			childCount = hjk.size();
		}
		return childCount;
	}

	public int getIndexOfChild(final TreeNodeInfo child) {

		int childIndex = -1;
		final List<TreeNodeInfo> hjk = getChildrenList();
		final Iterator<TreeNodeInfo> iterator = hjk.iterator();
		while (iterator.hasNext()) {
			childIndex++;
			if (child.get(GDriveFiles.FILE_ID_KEY).equals(
					iterator.next().get(GDriveFiles.FILE_ID_KEY))) {
				return childIndex;
			}

		}

		return 0;
	}

	public void deleteChildren(TreeNodeInfo child) {
		int childIndex  = getIndexOfChild(child);
		final List<TreeNodeInfo> childList = getChildrenList();
		childList.remove(childIndex);
		GDriveFiles.removeTreeNodeRefrence((String)child.get(GDriveFiles.FILE_ID_KEY));
	}
	
	public List<TreeNodeInfo> getChildrenList() {
		
		final List<TreeNodeInfo> childList = (List<TreeNodeInfo>) get(GDriveFiles.CHILD_KEY);
		
		return childList;
	}
	@Override
	public String toString() {

		String fileName = "Unknown File";
		final File currentFile = ((File) get(GDriveFiles.SELF_KEY));
		fileName = currentFile.getTitle();
		return fileName;
	}
}
