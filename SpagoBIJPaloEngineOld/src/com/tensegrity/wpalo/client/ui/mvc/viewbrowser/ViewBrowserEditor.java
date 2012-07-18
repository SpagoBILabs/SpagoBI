/*
*
* @file ViewBrowserEditor.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: ViewBrowserEditor.java,v 1.46 2010/04/12 11:13:36 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.viewbrowser;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ContainerEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XViewModel;
import com.tensegrity.palo.gwt.core.client.models.folders.XFolderElement;
import com.tensegrity.wpalo.client.DisplayFlags;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.WPaloPropertyServiceProvider;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.serialization.XObjectWrapper;
import com.tensegrity.wpalo.client.services.cubeview.WPaloCubeViewServiceProvider;
import com.tensegrity.wpalo.client.ui.dialog.MessageBoxUtils;
import com.tensegrity.wpalo.client.ui.editor.CloseObserver;
import com.tensegrity.wpalo.client.ui.editor.IEditor;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.mvc.cubeview.CubeViewEditor;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>ViewBrowserEditor</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ViewBrowserEditor.java,v 1.46 2010/04/12 11:13:36 PhilippBouillon Exp $
 **/
public class ViewBrowserEditor implements IEditor {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages  messages  = Resources.getInstance().getMessages();

	private ContentPanel content;
	private CloseObserver closeListener;
	private boolean removeAll;
	private final ViewEditorTabFolder tabFolder;
	private int viewDelay = -1;
	private DisplayFlags displayFlags = null;
	
	public ViewBrowserEditor(boolean hideTitlebar) {
	    //create content:
	    tabFolder = new ViewEditorTabFolder(hideTitlebar);
		RowLayout layout = new RowLayout();
		content = new ContentPanel(layout);
	    content.setBodyBorder(false);
//	    content.setBorders(false);
	    content.setHeaderVisible(false);
	    content.setCollapsible(false);	    
	    content.setScrollMode(Scroll.AUTO);
	    
	    //add tab folder as main content
		tabFolder.setTabScroll(true);
		content.add(tabFolder, new RowData(1, 1));
//		content.setLayout(new FitLayout());
//		content.add(tabFolder);
		Listener<ContainerEvent<TabPanel, TabItem>> listener = new Listener<ContainerEvent<TabPanel, TabItem>>() {
			public void handleEvent(final ContainerEvent<TabPanel, TabItem> ce) {
				if (ce.item instanceof ViewEditorTab) {
					final ViewEditorTab editorTab = (ViewEditorTab) ce.item;
					CubeViewEditor vEditor = editorTab.getEditor();
					switch (ce.type) {
					case Events.BeforeRemove:
						if (vEditor.isDirty()) {
							ce.doit = false;
							final XViewModel view = vEditor.getView();
							MessageBoxUtils.yesNoCancel(constants.saveView(),
									messages.saveViewBeforeClosing(ViewBrowserModel.modify(view.getName())), 
									new Listener<WindowEvent>() {
										public void handleEvent(WindowEvent be) {
											if (be.buttonClicked.getItemId().equalsIgnoreCase(Dialog.YES)) {
												saveAndClose(editorTab);
											} else if (be.buttonClicked.getItemId().equalsIgnoreCase(Dialog.NO)) {
												close(editorTab);
											}
											// Do nothing on cancel...
										}
									});
						}
						break;
					case Events.Remove:
						//free on server cache too!!
						remove(editorTab);
						break;
					}
				}
			}
		};
		tabFolder.addListener(Events.BeforeRemove, listener);
		tabFolder.addListener(Events.Remove, listener);
	}
	
	private final void saveAndClose(final ViewEditorTab tab) {
		tab.getEditor().save(new Callback<XViewModel>(constants.savingViewFailed()) {
			public void onSuccess(XViewModel v) {
				if (v == null) {
					MessageBox.alert(constants.notEnoughRights(),
							constants.notEnoughRightsToSaveView(), null);
					return;
				}
				close(tab);
			}
		});
	}
	private final void saveAndClose(final ViewEditorTab tab, final AsyncCallback <Boolean> cb) {
		((Workbench)Registry.get(Workbench.ID)).showWaitCursor(constants.savingView());
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 0, new Callback<Void>() {
			public void onSuccess(Void none) {
				tab.getEditor().save(new Callback<XViewModel>(constants.savingViewFailed()) {
					public void onFailure(Throwable t) {
						((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
						super.onFailure(t);
						cb.onFailure(t);
					}
					public void onSuccess(XViewModel v) {
						((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
						close(tab);
						cb.onSuccess(true);
					}
				});				
			}
		});
	}	
	
	public final boolean hasOpenViews() {
		if (tabFolder == null) {
			return false;
		}
		return tabFolder.getItemCount() > 0;
	}
	private final void close(ViewEditorTab tab) {
		tab.getEditor().markDirty(false);
		tabFolder.remove(tab);
	}
	private final void remove(final ViewEditorTab tab) {
		XViewModel view = tab.getEditor().getView();
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		CubeViewEditor.removeLocalFilter(view);
		WPaloCubeViewServiceProvider.getInstance().closeView(sessionId, view,
				new Callback<Void>() {
					public void onFailure(Throwable t) {
						hideWaitCursor();
						if(removeAll && tabFolder.getItemCount() > 0)
							removeNextTab();
						else {
							if (tabFolder.getItemCount() < 1 && closeListener != null)
								closeListener.finishedClosed();
						}						
					}
					public void onSuccess(Void arg) {
						if(removeAll && tabFolder.getItemCount() > 0)
							removeNextTab();
						else {
							if (tabFolder.getItemCount() < 1 && closeListener != null)
								closeListener.finishedClosed();
						}
					}
				});
	}
	
	private final void remove(final ViewEditorTab tab, final AsyncCallback <Boolean> cb) {
		XViewModel view = tab.getEditor().getView();
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		CubeViewEditor.removeLocalFilter(view);
		WPaloCubeViewServiceProvider.getInstance().closeView(sessionId, view,
				new Callback<Void>() {
					public void onFailure(Throwable t) {
						hideWaitCursor();
						if(removeAll && tabFolder.getItemCount() > 0)
							removeNextTab();
						else {
							if (tabFolder.getItemCount() < 1 && closeListener != null)
								closeListener.finishedClosed();
						}				
						cb.onSuccess(true);
					}
					public void onSuccess(Void arg) {
						if(removeAll && tabFolder.getItemCount() > 0)
							removeNextTab();
						else {
							if (tabFolder.getItemCount() < 1 && closeListener != null)
								closeListener.finishedClosed();
						}
						cb.onSuccess(true);
					}
				});		
	}
	
	public void doSave(final AsyncCallback <Boolean> cb) {
		if (tabFolder == null) {
			if (cb != null) {
				cb.onSuccess(true);
			}
		}
		final ArrayList <Integer> counter = new ArrayList<Integer>();
		counter.add(0);
		int allDirties = 0;
		for (TabItem item: tabFolder.getItems()) {
			if (item instanceof ViewEditorTab) {
				if (((ViewEditorTab) item).getEditor() != null) {
					if (((ViewEditorTab) item).getEditor().isDirty()) {
						allDirties++;
					}
				}
			}
		}
		final int target = allDirties;
		for (final TabItem item: tabFolder.getItems()) {
			if (item instanceof ViewEditorTab) {
				if (((ViewEditorTab) item).getEditor() != null) {
					if (((ViewEditorTab) item).getEditor().isDirty()) {
						((ViewEditorTab) item).getEditor().save(
								new Callback<XViewModel>() {
									public void onSuccess(XViewModel arg0) {
										removeAll = true;
										((ViewEditorTab) item).getEditor().markDirty(false);
										remove((ViewEditorTab) item, new AsyncCallback <Boolean>(){
											public void onFailure(Throwable arg0) {
												removeAll = false;
											}

											public void onSuccess(Boolean arg0) {
												removeAll = false;
												int val = counter.get(0) + 1;
												counter.set(0, val);
												if (val == target) {
													if (cb != null) {
														cb.onSuccess(true);
													}
												}
											}
										});
									}
								});
					} else {
						remove((ViewEditorTab) item);
					}
				} else {
					remove((ViewEditorTab) item);
				}
			}
		}
	}

	public void doSaveAs() {
		
	}
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getInput() {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentPanel getPanel() {
		return content;
	}

	public final String getTitle() {
		return constants.views();
	}

	public void markDirty() {
	}

	public void setUnDirty() {
		if (tabFolder == null) {
			return;
		}
		for (TabItem item: tabFolder.getItems()) {
			if (item instanceof ViewEditorTab) {
				if (((ViewEditorTab) item).getEditor() != null) {
					((ViewEditorTab) item).getEditor().markDirty(false);
				}
			}
		}
	}
	
	public boolean isDirty() {
		if (tabFolder == null) {
			return false;
		}
		for (TabItem item: tabFolder.getItems()) {
			if (item instanceof ViewEditorTab) {
				if (((ViewEditorTab) item).getEditor() != null) {
					if (((ViewEditorTab) item).getEditor().isDirty()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private final void setInputInternal(final XView xView, final ViewEditorTab viewTab, final XObject input) {
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().
		runAsync(sessionId, viewDelay, new AsyncCallback<Void>() {
			private final void setInternalInput() {
				//XView xView = getWrappedViewFrom(node);
				if(xView == null) {
					handleUnknownView(input, viewTab);
				} else {
					viewTab.set(xView);
				}
			}
			
			public void onFailure(Throwable arg0) {							
				setInternalInput();
			}

			public void onSuccess(Void arg0) {
				setInternalInput();
			}
		});		
	}
	
	public final void initUI(DisplayFlags displayFlags) {
		this.displayFlags = displayFlags;
	}
	
	public void setInput(Object input) {
		if(input instanceof TreeNode || input instanceof XObject) {		
			final XObject _input;
			final XView xView;
			boolean hideHeader = false;
			boolean viewOpen = false;
			if (input instanceof XObject) {
				_input = (XObject) input;
				xView = (XView) _input;
				hideHeader = DisplayFlags.isHideViewTabs();
			} else {
				final TreeNode node = (TreeNode) input;
				_input = node.getXObject();
				xView = getWrappedViewFrom(node);
				if (_input instanceof XObjectWrapper && ((XObjectWrapper) _input).getXObject() instanceof XFolderElement) {
					XObject so =((XFolderElement) ((XObjectWrapper) _input).getXObject()).getSourceObject();
					if (so != null) {
						TabItem item = tabFolder.findItem(so.getId(), false);
						if (item != null) {
							viewOpen = true;
						}
					}
				}
			}
			//check if we have a view:
			
			if(!isAlreadyOpen(_input) && !viewOpen) {
				//open it:
				final boolean hHeader = hideHeader;				
				DeferredCommand.addCommand(new Command(){
					public void execute() {
						final ViewEditorTab viewTab = new ViewEditorTab(_input.getName(), false, DisplayFlags.getDisplayFlagsFor(xView));
						viewTab.setId(_input.getId());
						viewTab.setClosable(true);
						tabFolder.add(viewTab);
						DeferredCommand.addCommand(new Command(){
							public void execute() {
								tabFolder.setSelection(viewTab);
							}
						});						
						if (hHeader) {
							viewTab.hideHeader();
						} else {
							viewTab.getHeader().setHeight("0px");
						}
						if (viewDelay == -1) {
							WPaloPropertyServiceProvider.getInstance().getIntProperty("delayViewOpen", 500, new AsyncCallback<Integer>(){
								public void onFailure(Throwable arg0) {
									viewDelay = 500;
									setInputInternal(xView, viewTab, _input);
								}

								public void onSuccess(Integer result) {
									viewDelay = result;
									setInputInternal(xView, viewTab, _input);
								}
							});
						} else {
							setInputInternal(xView, viewTab, _input);
						}
					}
				});				
//				waitCursor.hide();
//				viewTab.setSize(viewTab.getOffsetWidth(), viewTab.getOffsetHeight());
//				viewTab.layout();
			} else {				
				showView(input);
			}
		}
	}
	
	public void setInputQuietly(Object input) {
	}

	private final XView getWrappedViewFrom(TreeNode node) {
		XObjectWrapper wrapper = (XObjectWrapper) node.getXObject();
		XFolderElement xFolderElement = (XFolderElement) wrapper.getXObject();
		XObject xObj = xFolderElement.getSourceObject();
		if (xObj instanceof XView)
			return (XView) xObj;
		return null;
	}
	private final void handleUnknownView(final XObject xView, final ViewEditorTab tab) {
		Listener<WindowEvent> callback = new Listener<WindowEvent>() {
			public void handleEvent(WindowEvent we) {
				Button clicked = we.buttonClicked;
				if (clicked.getText().equalsIgnoreCase(Dialog.YES)) {
					// remove folder element:
					Dispatcher.forwardEvent(WPaloEvent.WILL_DELETE_VIEWBROWSER_VIEW, xView);
				} else {
					tab.close();
				}
			}
		};
		MessageBox.confirm(constants.error(),
				constants.underlyingViewGone(),
				callback);
	}
	
	/**
	 * shows the view if it is already open, otherwise calling this method has no effect
	 * @param input
	 */
	public final void showView(Object input) {
		if (input instanceof TreeNode) {
			// check if we have a view:
			XObject _input = ((TreeNode) input).getXObject();
			TabItem tab = tabFolder.findItem(_input.getId(), false);
			if (tab == null) {
				if (_input instanceof XObjectWrapper && ((XObjectWrapper) _input).getXObject() instanceof XFolderElement) {
					XObject so =((XFolderElement) ((XObjectWrapper) _input).getXObject()).getSourceObject();
					if (so != null) {
						tab = tabFolder.findItem(so.getId(), false);
					}
				}
			}
			if (tab != null)
				tabFolder.setSelection(tab);
		} 
	}
	
	public final void closeIfOpen(Object input) {
		TabItem item = getTabIfOpen(input);
		if(item != null)
			tabFolder.remove(item);
	}
	public final void showIfOpen(Object input) {
		TabItem item = getTabIfOpen(input);
		if(item != null)
			tabFolder.setSelection(item);
	}
	private final boolean isAlreadyOpen(XObject input) {
		TabItem item = tabFolder.findItem(input.getId(), false);
		return item != null;
	}
	private final TabItem getTabIfOpen(Object input) {
		if(input instanceof TreeNode) {
			TreeNode node = (TreeNode) input; 
			XObject _input = node.getXObject();
			return tabFolder.findItem(_input.getId(), false);
		}
		return null;
	}
	
	public final void renameIfOpen(Object input) {
		TabItem item = getTabIfOpen(input);
		if(item != null) {
			TreeNode node = (TreeNode) input;
			item.setText(node.getXObject().getName());
		}

	}
	
	private final void askBeforeClose(final ViewEditorTab editorTab, final AsyncCallback <Boolean> callback) {
		CubeViewEditor vEditor = editorTab.getEditor();
		final XViewModel view = vEditor.getView();
		MessageBoxUtils.yesNoCancel(constants.saveView(), messages.saveViewBeforeClosing(ViewBrowserModel.modify(view.getName())),
				new Listener<WindowEvent>() {
					public void handleEvent(WindowEvent be) {
						if (be.buttonClicked.getItemId()
								.equalsIgnoreCase(Dialog.YES)) {
							saveAndClose(editorTab, callback);
						} else if (be.buttonClicked.getItemId().equalsIgnoreCase(Dialog.NO)) {
							close(editorTab);
							callback.onSuccess(true);
						} else if (be.buttonClicked.getItemId().equalsIgnoreCase(Dialog.CANCEL)) {
							callback.onSuccess(false);
						}
					}
				});					
	}
	
	class SaveListenerCallback implements AsyncCallback <Boolean> {
		private final ArrayList <ViewEditorTab> dirtyTabs;
		private final AsyncCallback <Boolean> callback;
		private int counter = 0;
		
		SaveListenerCallback(ArrayList <ViewEditorTab> dirtyTabs, AsyncCallback <Boolean> callback) {
			this.dirtyTabs = dirtyTabs;
			this.callback = callback;
		}

		void checkTabs() {
			if (dirtyTabs.size() > 0) {
				ViewEditorTab vet = (ViewEditorTab) tabFolder.getSelectedItem();				
				while (vet != null && !vet.getEditor().isDirty()) {
					vet.close();
					vet = (ViewEditorTab) tabFolder.getSelectedItem();
				}
				if (vet != null) {
					askBeforeClose(vet, this);
				}
			}			
		}
		
		public void onFailure(Throwable arg0) {
			callback.onFailure(arg0);
		}

		public void onSuccess(Boolean result) {
			if (result) {
				counter++;
				if (counter < dirtyTabs.size()) {					
					ViewEditorTab vet = (ViewEditorTab) tabFolder.getSelectedItem();				
					while (vet != null && !vet.getEditor().isDirty()) {
						vet.close();
						vet = (ViewEditorTab) tabFolder.getSelectedItem();
					}
					if (vet != null) {
						askBeforeClose(vet, this);
					}
				} else {
					callback.onSuccess(true);
				}
			} else {
				callback.onSuccess(false);
			}
		}		
	}
	
	public void beforeClose(final AsyncCallback<Boolean> callback) {
		ArrayList <ViewEditorTab> dirtyTabs = new ArrayList<ViewEditorTab>();
		for (TabItem item: tabFolder.getItems()) {
			if (item instanceof ViewEditorTab) {
				final ViewEditorTab editorTab = (ViewEditorTab) item;
				CubeViewEditor vEditor = editorTab.getEditor();
				if (vEditor.isDirty()) {
					dirtyTabs.add(editorTab);
				}
			}			
		}
		if (dirtyTabs.isEmpty()) {
			callback.onSuccess(true);			
		} else {
			SaveListenerCallback slc = new SaveListenerCallback(dirtyTabs, callback);
			slc.checkTabs();
		}
	}
	
	public final void setCloseAll(boolean cAll) {
		removeAll = cAll;
	}
	
	public final void close(CloseObserver closeListener) {
		this.closeListener = closeListener;
		removeAll = true;
		if (tabFolder.getItemCount() > 0) {
			removeNextTab();
		} else {
			if (closeListener != null) {
				closeListener.finishedClosed();				
			}
			removeAll = false;
		}
	}
	private final void removeNextTab() {
		int nextItem = tabFolder.getItemCount() - 1;
		if(nextItem >= 0) {
			TabItem nextTab = tabFolder.getItem(nextItem);
			tabFolder.remove(nextTab);
		} else {
			tabFolder.removeFromParent();
			removeAll = false;
		}
	}

	public void selectFirstTab() {
		// TODO Auto-generated method stub
		
	}

	public void setTextCursor() {
		// TODO Auto-generated method stub
		
	}
}