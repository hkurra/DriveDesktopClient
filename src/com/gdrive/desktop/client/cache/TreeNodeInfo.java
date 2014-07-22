package com.gdrive.desktop.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.gdrive.desktop.client.Global.ServiceManager;
import com.gdrive.desktop.client.Global.ServiceManager.serviceType;
import com.google.api.services.drive.model.File;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * <p>
 * represent Tree Node Info in drive tree structure
 * </p>
 * 
 * @author harsh
 */
public class TreeNodeInfo extends HashMap<String, Object> {

	/**
	 * @return
	 */
	public List<TreeNodeInfo> getChildNodes() {
		final List<TreeNodeInfo> childList = (List<TreeNodeInfo>) get(GDriveFiles.CHILD_KEY);
		return childList;
	}
	/**
	 * <p>
	 * this method return child of Tree Node at index i if child exist at i else
	 * null
	 * </p>
	 * 
	 * @param index
	 *            of child node
	 * @return
	 */
	public TreeNodeInfo getChild(final int index) {
		TreeNodeInfo childNodeInfo = null;
		if (get(GDriveFiles.CHILD_KEY) != null) {
			 List<TreeNodeInfo> childList = getChildNodes();
			childNodeInfo = childList.get(index);
		}
		return childNodeInfo;
	}

	/**
	 * Number of child nodes of Tree node
	 * @return childCount
	 */
	public int getChildCount() {

		int childCount = 0;
		final List<TreeNodeInfo> childList = getChildNodes();
		if (childList != null) {
			childCount = childList.size();
		}
		return childCount;
	}

	/**
	 * <p>this method return index of child node if it exist in Tree Node else -1</p>
	 * 
	 * @param child
	 * @return child index
	 */
	public int getIndexOfChild(final TreeNodeInfo child) {

		int childIndex = -1;
		final List<TreeNodeInfo> hjk = getChildNodes();
		final Iterator<TreeNodeInfo> iterator = hjk.iterator();
		while (iterator.hasNext()) {
			childIndex++;
			if (child.get(GDriveFiles.FILE_ID_KEY).equals(
					iterator.next().get(GDriveFiles.FILE_ID_KEY))) {
				return childIndex;
			}
		}
		return childIndex;
	}

	/**
	 * <p> this method delete child of tree node if exist</p>
	 * <p>ths also remove child from cached structure of tree node reference & file revision</p>
	 * 
	 * @param child node
	 */
	public void deleteChildren(TreeNodeInfo child) {
		int childIndex = getIndexOfChild(child);
		final List<TreeNodeInfo> childList = getChildNodes();
		childList.remove(childIndex);
		
		String fileID = (String) child.get(GDriveFiles.FILE_ID_KEY);
				
		GDriveFiles.removeTreeNodeRefrence(fileID);
		GDriveFileRevisions.deleteRevision(fileID);
	}

	/**
	 * <p>add child Nodes in TreeNode</p>
	 * <p>replace the previous child nodes</p>
	 * 
	 * @param childList
	 */
	public void addChildNodes(List<TreeNodeInfo> childList) {
		put(GDriveFiles.CHILD_KEY, childList);
	}
	
	/**
	 * <p>add child in TreeNode</p>
	 * 
	 * <p>if silentAdd is false update call is generated</p>
	 * <p>register your listener for this update call<ADD_NEW_NODE_SERVICE_ID></p> 
	 * @param childList
	 * @param silentAdd
	 */
	public void addChild(TreeNodeInfo child, Boolean silentAdd) {
		
		List<TreeNodeInfo> childList =  getChildNodes();
		if (childList == null) {
			childList = new ArrayList<TreeNodeInfo>();
		}
		if (childList.add(child) && !silentAdd) {
			ServiceManager.ExecuteResponders(ServiceManager.serviceType.ADD_NEW_NODE_SERVICE_ID, null);
		}
	}
	
@Override
	public String toString() {

		String fileName = "Unknown File";
		final File currentFile = ((File) get(GDriveFiles.SELF_KEY));
		fileName = currentFile.getTitle();
		return fileName;
	}
}
