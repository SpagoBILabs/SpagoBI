package com.tensegrity.wpalo.client.i18n;

import com.google.gwt.i18n.client.Messages;

public interface ILocalMessages extends Messages {
	String aboutMessage(String appName, int major, int minor, int bugfix, String buildNumber);
	String account(String name);
	
	String browser(String name);
	
	String cancelViewUpdateFailed(String name);
	String choose(String name);
	String connection(String name);
	String collapseItemFailed(String name);
	String collapsingItem(String name);
	String couldNotDeleteFolder(String name);
	String couldNotDeleteView(String name);
	
	String deleteAccount(String name);
	String deleteConnection(String name);
	String deleteGroup(String name);
	String deleteRole(String name);
	String deleteUser(String name);
	String directLinkHeading(String viewName);
	String downloadPDF(String link);
	String downloadPDFOnlyClose(String link);
	
	String editProperties(String viewName);
	String errorCause(String cause);
	String expandingItem(String name);
	String expandingItemFailed(String name);
	
	String failedToLoadView(String name, String reason);
	String failedToSave(String name);
	
	String group(String name);
	
	String hideElement(String name);
	
	String impossibleToDeleteGroup(String name);
	String impossibleToDeleteRole(String name);
	String impossibleToDeleteUser(String name);
	
	String largeCellQueryWarning(int loadCells, int totalCells, int visibleCells);
	String largeTreeQueryWarning(int treeChildren);
	String loadingViewFailed(String name);
	
	String newerVersionExists(String buildNumber, String link);
	String noNewerVersionAvailable(String link);
	
	String printHeading(String title);
	
	String role(String name);
	
	String saveEditorBeforeClosing(String title);
	String saveViewBeforeClosing(String name);
	String sureToDeleteFolder(String name);
	String sureToDeleteView(String name);
	
	String thresholds(String t1, String t2, String t3, String t4, String t5, String t6);
	String tipMessage(int number);
	
	String updatingViewFailed(String name);
	String useLocalFilter(int number);
	String user(String login);
	
	String view(String name);
	
	String warningsWhenOpeningView(String name);
	
}
