/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.editor;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.services.admin.WPaloAdminServiceProvider;
import com.tensegrity.wpalo.client.ui.dialog.MessageBoxUtils;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.mvc.viewbrowser.ViewBrowserModel;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;
import com.tensegrity.wpalo.client.ui.widgets.EditorTab;

/**
 * <code>AdminEditor</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AbstractTabEditor.java,v 1.29 2010/04/15 09:55:22 PhilippBouillon Exp $
 **/
public abstract class AbstractTabEditor implements IEditor {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages messages = Resources.getInstance().getMessages();
	
	protected ContentPanel content;
	protected EditorTab[] tabs;
	protected String title;
	
	private TreeNode input;
	private TextToolItem saveBtn;
	private boolean isDirty = false;
	protected TabPanel tabFolder;
	
	public AbstractTabEditor() {
	    //create content:
	    content = new ContentPanel();
	    content.setBodyBorder(false);
	    content.setHeaderVisible(false);
	    content.setScrollMode(Scroll.AUTO);
//		content.setHeading("Properties");
		content.setButtonAlign(HorizontalAlignment.RIGHT);

		//da toolbar
		ToolBar toolbar = new ToolBar();
		saveBtn = new TextToolItem("", "icon-save");
		saveBtn.setToolTip(constants.save());
		saveBtn.addSelectionListener(new SelectionListener<ComponentEvent>() {
			public final void componentSelected(ComponentEvent ce) {
			}
			public final void handleEvent(ComponentEvent be) {
				doSave(null);
			}
		});
		toolbar.add(saveBtn);
		toolbar.add(new SeparatorToolItem());
		content.setTopComponent(toolbar);

		tabFolder = new TabPanel();
		tabFolder.setTabScroll(true);

		tabs = getEditorTabs();
		for(EditorTab tab : tabs)
			tabFolder.add(tab);


		RowLayout layout = new RowLayout(Orientation.VERTICAL);
		content.setLayout(layout);
		content.add(tabFolder, new RowData(1, 1));

	}

	public final void close(final CloseObserver observer) {
		if (tabFolder != null && tabFolder.getItemCount() > 0) {
			try {
				tabFolder.removeAll();
			} catch (Throwable t) {
				// Ignore... (Bug in GXT: If no items are attached, removing
				// 	the tabs will result in a NPE -- and yes, getItemCount is
				// > 0 in that case...)
			}
		} 
		tabFolder.removeFromParent();
		if(observer != null)
			observer.finishedClosed();					
	}
	
	public void beforeClose(final AsyncCallback<Boolean> cb) {
		if (isDirty()) {
			MessageBoxUtils.yesNoCancel(constants.saveEditor(),
					messages.saveEditorBeforeClosing(getTitle()),					
				new Listener<WindowEvent>() {
					public void handleEvent(WindowEvent be) {
						boolean result = true;
						if (be.buttonClicked.getId().equalsIgnoreCase(Dialog.YES)) {
							doSave(null);							
						} else if (be.buttonClicked.getId().equalsIgnoreCase(Dialog.NO)) {
							// Do nothing...
						} else if (be.buttonClicked.getId().equalsIgnoreCase(Dialog.CANCEL)) {								
							result = false;
						}
						cb.onSuccess(result);					}
				});								
		} else {
			cb.onSuccess(true);
		}
	}

	public final ContentPanel getPanel() {
		isDirty = false;
		saveBtn.setEnabled(isDirty);
		return content;
	}

	public String getTitle() {
		return title;
	}
	
	public final Object getInput() {
		return input;
	}
	
	public final void setInput(Object input) {
		if(input instanceof TreeNode) {
			this.input = (TreeNode)input;
			XObject _input = this.input.getXObject();
			title = getTitle(_input);
			for(EditorTab tab : tabs)
				tab.set(_input);
			isDirty = false;
			saveBtn.setEnabled(false);
		}
	}
	
	public final void setInputQuietly(Object input) {
		if(input instanceof TreeNode) {
			this.input = (TreeNode)input;
			XObject _input = this.input.getXObject();
			title = getTitle(_input);
		}
	}
	
	public final void markDirty() {
		isDirty = true;
		saveBtn.setEnabled(isDirty);
	}
	
	public final boolean isDirty() {
		return isDirty;
	}
	
	public final void doSave(final AsyncCallback <Boolean> callback) {
		final Workbench wb = (Workbench)Registry.get(Workbench.ID);
		XUser admin = wb.getUser();
		if(admin != null) {
			final XObject xObj = input.getXObject();
			for (EditorTab tab : tabs) {
				if (!tab.save(xObj)) {
					MessageBox.alert(constants.error(), 
							messages.failedToSave(getTitle()),
							null);		
					return;
				}
			}
			
//			if (getSaveType() == WPaloEvent.SAVED_USER_ITEM) {
//				final TreeNode input = (TreeNode) getInput();
//				final XUser user = (XUser) ((TreeNode) getInput()).getXObject();
//				final XUser loggedInUser = ((Workbench)Registry.get(Workbench.ID)).getUser(); 
//				String sessionId = loggedInUser.getSessionId();
//				WPaloAdminServiceProvider.getInstance().getGroups(sessionId, user,
//						new Callback<XGroup[]>("Failed to load groups!!") {
//								public void onSuccess(XGroup[] groups) {
//									user.clearGroups();
//									for (XGroup group : groups) {
//										user.addGroupID(group.getId());
//									}
//									WPaloAdminServiceProvider.getInstance().saveXObject(loggedInUser.getSessionId(), xObj,
//											new Callback<XObject>() {
//										public void onFailure(Throwable t) {
//													MessageBox.alert("Error", "Failed to save '"
//														+ xObj.getName() + "'!\n" + t.getMessage(),
//														null);
//												saved(false);
//												if (callback != null) {
//													callback.onSuccess(true);
//												}
//											}
//											public void onSuccess(XObject arg0) {
//												input.setXObject(arg0);
//												saved(true);
//												setInput(arg0);
//												if (callback != null) {
//													callback.onSuccess(true);
//												}												
//											}
//										});
//							}
//						});
//			} else {	
				WPaloAdminServiceProvider.getInstance().saveXObject(wb.getUser().getSessionId(), xObj,
					new Callback<XObject>() {
						public void onFailure(Throwable t) {
							if (xObj != null && xObj.getName() != null) {
								MessageBox.alert(constants.error(), 
									messages.failedToSave(ViewBrowserModel.modify(xObj.getName())) + t.getMessage(),
									null);
							} else {
								String id = xObj == null ? "" : xObj.getId();
								MessageBox.alert(constants.error(), 
										messages.failedToSave(id) + t.getMessage(),
										null);								
							}
							saved(false);
							if (callback != null) {
								callback.onSuccess(true);
							}							
						}
						public void onSuccess(XObject arg0) {
							input.setXObject(arg0);							
							setInput(arg0);
							if (callback != null) {
								callback.onSuccess(true);
							}									
							if (needsUpdate()) {
								clearNeedsUpdate();
								doSave(callback);								
							} else {
								saved(true);
							}
						}
					});
//			}
		}
	}
	
	public abstract EditorTab[] getEditorTabs();
	public abstract String getTitle(XObject input);
	
	public boolean needsUpdate() {
		return false;
	}
	
	public void clearNeedsUpdate() {		
	}
	
	protected int getSaveType() {
		return -1;
	}
	private final void saved(boolean ok) {
		isDirty = !ok;
		saveBtn.setEnabled(isDirty);
		final int saveType = getSaveType();
		if (ok && saveType != -1)
			Dispatcher.forwardEvent(new AppEvent<TreeNode>(saveType, input));
	}
	
	public void selectFirstTab() {
		tabFolder.setSelection(tabFolder.getItem(0));
	}
	
	public void setTextCursor() {		
	}
}
