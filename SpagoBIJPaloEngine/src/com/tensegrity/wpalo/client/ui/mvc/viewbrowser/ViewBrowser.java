/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.viewbrowser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.binder.TreeBinder;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.dnd.TreeDragSource;
import com.extjs.gxt.ui.client.dnd.TreeDropTarget;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.EditorEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreeEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Editor;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.extjs.gxt.ui.client.widget.tree.TreeItemUI;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tensegrity.palo.gwt.core.client.exceptions.SessionExpiredException;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.folders.XFolderElement;
import com.tensegrity.palo.gwt.core.client.models.folders.XStaticFolder;
import com.tensegrity.palo.gwt.widgets.client.util.UserAgent;
import com.tensegrity.wpalo.client.DisplayFlags;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.WPaloPropertyServiceProvider;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.serialization.XObjectWrapper;
import com.tensegrity.wpalo.client.services.cubeview.WPaloCubeViewServiceProvider;
import com.tensegrity.wpalo.client.services.folder.WPaloFolderServiceProvider;
import com.tensegrity.wpalo.client.ui.model.TreeLoaderProxy;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.mvc.cubeview.CubeViewEditor;
import com.tensegrity.wpalo.client.ui.mvc.cubeview.LargeQueryWarningDialog;
import com.tensegrity.wpalo.client.ui.mvc.viewbrowser.importer.ViewImportDialog;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>ViewBrowser</code> TODO DOCUMENT ME
 * 
 * @version $Id: ViewBrowser.java,v 1.33 2009/08/17 13:24:23 PhilippBouillon Exp
 *          $
 **/
public class ViewBrowser extends View {
	protected static transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected static transient final ILocalMessages  messages  = Resources.getInstance().getMessages();
	
	public static final String SHOW_TIPS_COOKIE = "showTipsAtStartup";
	
	private static final int F2 = 113;

	private static final String OPEN = "Open";
	private static final String DELETE = "Delete";
	private static final String RENAME = "Rename";
	
	/** id to access view browser instance via the global {@link Registry} */
	public static final String ID = ViewBrowser.class.getName();
	
	private ContentPanel navigator;
	private TreeBinder<TreeNode> treeBinder;
	private Tree viewsTree;
	private ViewBrowserModel browserModel;
	private TextToolItem deleteItem;
	private TextToolItem renameItem;
	private boolean mayDelete = false;
	private int createRight = -1;
	private MenuItem editViewProperties;
	private MenuItem createDirectLink;

	public ViewBrowser(Controller controller) {
		super(controller);
		Registry.register(ID, this);
	}

	public final void clear() {
		viewsTree.removeAll();
	}

	public final XView[] getViews() {
		return browserModel.getViews();
	}

	final void addView(XView originalView, XView xView, boolean openIt,
			boolean isPublic, boolean isEditable) {
		TreeNode selection = getSelectedNode();
		if (originalView != null) {
			selection = findNodeOf(originalView);
		}
		TreeNode parent = getParentFolder(selection);
		browserModel.addView(xView, parent, openIt, this, isPublic, isEditable);
	}

	final void open(TreeNode node) {
		fireEvent(new AppEvent<TreeNode>(WPaloEvent.EDIT_VIEWBROWSER_VIEW, node));
	}
	

	
	protected void handleEvent(AppEvent<?> event) {
		switch (event.type) {
		case WPaloEvent.INIT:			
			try {
			if (event.data instanceof XUser) {
				initUI((XUser) event.data);
				checkShowTipsAtStartup((XUser) event.data);
			} else if (event.data != null && event.data instanceof DisplayFlags){
				DisplayFlags flags = (DisplayFlags) event.data;
				if (!flags.isHideNavigator()) {
					initUI(flags.getUser());
				}
			}
			} catch (Throwable t) {
				t.printStackTrace();
			}
			break;
		case WPaloEvent.EXPANDED_VIEWBROWSER_SECTION:
			// reload tree
//			browserModel.reload();
			break;
		case WPaloEvent.WILL_DELETE_VIEWBROWSER_VIEW:
			TreeNode viewNode = null;
			Object obj = event.data;
			if (event.data instanceof XObjectWrapper) {
				obj = ((XObjectWrapper) event.data).getXObject();
			}
			if (obj instanceof XView) {
				viewNode = findNodeOf((XView) obj);
			} else if (obj instanceof XFolderElement) {
				viewNode = findNodeOf((XFolderElement) obj);
			} else {
				viewNode = (TreeNode) obj;
			}
			if (viewNode != null)
				delete(viewNode, false);
			break;
		case WPaloEvent.DELETED_ITEM:
			TreeNode node = (TreeNode) event.data;
			// Account account = getAccount((TreeNode)event.data);
			// if(account != null) {
			// deleteAllViewsFor(account)
			// }
			// TreeNode viewNode = (TreeNode)event.data;
			// if(viewNode != null)
			// delete(viewNode);
			break;

		}
	}

	private final void checkShowTipCookie(XUser user) {
		String cookieData = Cookies.getCookie(SHOW_TIPS_COOKIE + user.getLogin());
		Date date = new Date(System.currentTimeMillis() + 1000l * 60l * 60l * 24l * 30l);			
		if (cookieData == null) {
			cookieData = "true,1";									
		}
		// Update cookie data expiration date:
		Cookies.setCookie(SHOW_TIPS_COOKIE + user.getLogin(), cookieData, date);
				
		String [] values = cookieData.split(",");
		if (values == null || values.length != 2) {
			values = new String[2];
			values[0] = "true";
			values[1] = "1";
		}
		boolean doShow = true;
		int tipNumber = 1;
		try {
			doShow = Boolean.parseBoolean(values[0]);
			tipNumber = Integer.parseInt(values[1]);
		} catch (Throwable t) {
			doShow = true;
			tipNumber = 1;
		}
		
		if (doShow) {
			new ShowTipsAtStartupDialog(tipNumber, date, user.getLogin());
		} else {
			cookieData = doShow + "," + tipNumber;
			Cookies.setCookie(SHOW_TIPS_COOKIE + user.getLogin(), cookieData, date);
		}		
	}
	
	private final void checkShowTipsAtStartup(final XUser user) {
		WPaloPropertyServiceProvider.getInstance().getBooleanProperty(
				"showTipsAtStartup", true, new AsyncCallback<Boolean>() {
					public void onFailure(Throwable t) {
						checkShowTipCookie(user);
					}
					public void onSuccess(Boolean result) {
						if (result) {
							checkShowTipCookie(user);
						}
					}
				});
	}
		
	private final TreeNode findNodeOf(XView xView) {
		return browserModel.getNodeOf(xView);
	}

	private final TreeNode findNodeOf(XStaticFolder folder) {
		return browserModel.getNodeOf(folder);
	}
	
	private final TreeNode findNodeOf(XFolderElement elem) {
		return browserModel.getNodeOf(elem);
	}
			
	private static final String getBrowserName() {
		String result = constants.unknown();
		
		UserAgent ua = UserAgent.getInstance();
		if (ua.isOpera)   result = "Opera"; 
		if (ua.isChrome)  result = "Chrome";
		if (ua.isChrome4) result = "Chrome 4";
		if (ua.isChrome3) result = "Chrome 3";
		if (ua.isChrome2) result = "Chrome 2";
		if (ua.isChrome1) result = "Chrome 1";
		if (ua.isIron)    result = "Iron";
		if (ua.isIron1)   result = "Iron 1";
		if (ua.isIron2)   result = "Iron 2";
		if (ua.isIron3)   result = "Iron 3";
		if (ua.isIron4)   result = "Iron 4";
		if (ua.isSafari)  result = "Safari";
		if (ua.isSafari4) result = "Safari 4";
		if (ua.isSafari3) result = "Safari 3";		     
		if (ua.isIE)      result = "Internet Explorer";
		if (ua.isIE8)     result = "Internet Explorer 8";
		if (ua.isIE7)     result = "Internet Explorer 7";
		if (ua.isIE6)     result = "Internet Explorer 6";		
		if (ua.isGecko)   result = "Firefox";
		if (ua.isGecko36) result = "Firefox 3.6";
		if (ua.isGecko35) result = "Firefox 3.5";
		if (ua.isGecko3)  result = "Firefox 3";
		if (ua.isGecko2)  result = "Firefox 2";
		
		return result;
	}
	
	private static final boolean isNewerVersionAvailable(String buildNumber, String [] buildInformation) {
		String [] partInfo = buildInformation[0].split("\\.");
		int major = Integer.parseInt(partInfo[0]);
		int minor = Integer.parseInt(partInfo[1]);
		int rev = Integer.parseInt(partInfo[2]);
		int build = Integer.parseInt(partInfo[3]);

		if (major > 3) {
			return true;
		} else if (major == 3 && minor > 2) {
			return true;
		} else if (major == 3 && minor == 2 && rev > 0) {
			return true;
		} else if (major == 3 && minor == 2 && rev == 0) {
			if (Integer.parseInt(buildNumber) < build) {
				return true;
			}
		}
		return false;
	}
	
	public static final void displayAboutDialog(final String buildNumber, final String [] buildInformation) {
		WPaloPropertyServiceProvider.getInstance().getStringProperty("appName",
				new AsyncCallback<String>() {
					private final void showAbout(String appName) {
						final String aboutString = messages.aboutMessage(appName, 3, 2, 0, buildNumber);				
						
						LargeQueryWarningDialog.readThresholds(new AsyncCallback<String []>() {
							public void onFailure(Throwable arg0) {
								MessageBox.info(constants.aboutHeader(), aboutString, null);
							}

							public void onSuccess(String [] result) {
								String browserInfo = messages.browser(getBrowserName()); 
								String thInfo = messages.thresholds(result[0], result[1], result[2], result[3], result[4], result[5]); 
								String link = constants.licenseLink(); 
								String displayInfo = aboutString + link + "<br/>"; 
								if (buildInformation != null) {
									if (isNewerVersionAvailable(buildNumber, buildInformation)) {
										displayInfo += messages.newerVersionExists(buildInformation[0], buildInformation[1]) + "</br>";
									} else {
										String infoLink = buildInformation[1];
										int index;
										if ((index = infoLink.indexOf("#")) != -1) {
											infoLink = infoLink.substring(0, index);
										}
										displayInfo += messages.noNewerVersionAvailable(infoLink) + "<br/>";
									}
								}
								displayInfo += browserInfo + thInfo;

								MessageBox.info(constants.aboutHeader(), displayInfo, null);
							}
						});								
					}
					
					public void onFailure(Throwable arg0) {
						showAbout("Palo Pivot");
					}

					public void onSuccess(String result) {
						if (result == null || result.trim().length() == 0) {
							result = "Palo Pivot";
						}
						showAbout(result);
					}
				});
	}
		
	static final String getIconStyle(TreeNode node) {
		String result = "icon-view";
		
		if (node != null && node.getXObject() != null) {
			XObject x = node.getXObject();
			if (x instanceof XObjectWrapper) {
				x = ((XObjectWrapper) x).getXObject();
			}
			if (x != null && x instanceof XFolderElement) {
				x = ((XFolderElement) x).getSourceObject();
			}
			if (x != null && x instanceof XView) {
				return getIconStyle((XView) x);
			}
		}
		return result;		
	}
	
	static final String getIconStyle(XView view) {
		boolean isViewer = false;
		boolean isOwner = false;
		boolean isEditor = false;
		String result = "icon-view";
		
		XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
		isOwner = user.getId().equals(view.getOwnerId());
		isEditor = view.containsRoleName("EDITOR");
		isViewer = view.containsRoleName("VIEWER");
		if (isViewer) {
			result += "V";
		}
		if (isEditor) {
			result += "E";
		}
		if (isOwner) {
			result += "O";
		}
		return result;		
	}
	
	private final ToolBar initUI(XUser user) {
		navigator = new ContentPanel();
		navigator.setHeading(constants.views());
		navigator.setScrollMode(Scroll.AUTO);
		
		// connect with dispatcher:
		navigator.addListener(Events.Expand, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent be) {
				Dispatcher.get().dispatch(
						WPaloEvent.EXPANDED_VIEWBROWSER_SECTION);
			}
		});

		// create the tree which displays the data:
		final TreeLoader<TreeNode> treeLoader = new BaseTreeLoader<TreeNode>(
				new TreeLoaderProxy()) {
			public boolean hasChildren(TreeNode data) {
				return data != null && data.getXObject() != null
						&& data.getXObject().hasChildren();
			}

			protected void onLoadFailure(TreeNode loadConfig, Throwable t) {
				if (t instanceof SessionExpiredException)
					Callback.handle((SessionExpiredException) t);
				else
					super.onLoadFailure(loadConfig, t);
			}
		};
		((Workbench)Registry.get(Workbench.ID)).showWaitCursor(constants.loadingStructure());
		viewsTree = createTree(user, treeLoader);
		LoadListener initialOpenListener = new LoadListener() {
			public void loaderLoadException(LoadEvent le) {
				((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
			}
			
			public void loaderLoad(LoadEvent le) {
				if (le.config != null && le.config instanceof TreeNode
						&& ((TreeNode) le.config).getParent() == null) { // Root
																			// node
					if (le.data != null && le.data instanceof List) {
						List loaded = (List) le.data;
						if (!loaded.isEmpty()) {
							Object o = loaded.get(0);
							if (o instanceof TreeNode) {
								TreeNode tn = (TreeNode) o;
								Component c = treeBinder.findItem(tn);
								if (c != null && c instanceof TreeItem) {
									if (!((TreeItem) c).isExpanded()) {
										((TreeItem) c).setExpanded(true);
										treeLoader.removeLoadListener(this);
									}
								}
							}
						}
					}
				}
				((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
			}			
		};
		treeLoader.addLoadListener(initialOpenListener);
		treeLoader.addLoadListener(new LoadListener(){
			  public void loaderBeforeLoad(LoadEvent le) {
				  ((Workbench)Registry.get(Workbench.ID)).showWaitCursor(constants.loadingChildren());
			  }

			  public void loaderLoadException(LoadEvent le) {
				  ((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
			  }

			  public void loaderLoad(LoadEvent le) {
				  ((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
			  }			
		});
		final ToolBar toolbar = new ToolBar();
		TextToolItem addFolder = new TextToolItem("", "icon-create-folder");
		addFolder.setToolTip(constants.createNewViewFolder());
		addFolder.addSelectionListener(new SelectionListener<ComponentEvent>() {
			public void componentSelected(ComponentEvent ce) {
				createNewFolder();
			}
		});
		toolbar.add(addFolder);

		toolbar.add(new SeparatorToolItem());

		TextToolItem createViews = new TextToolItem("", "icon-create-view");
		createViews.setToolTip(constants.createNewViews());
		createViews
				.addSelectionListener(new SelectionListener<ComponentEvent>() {
					public void componentSelected(ComponentEvent ce) {
						TreeNode node = getSelectedNode();
						createViews(node);
					}
				});
		toolbar.add(createViews);
		TextToolItem importViews = new TextToolItem("", "icon-import-view");
		importViews.setToolTip(constants.importViews());
		importViews
				.addSelectionListener(new SelectionListener<ComponentEvent>() {
					public void componentSelected(ComponentEvent ce) {
						TreeNode node = getSelectedNode();
						importViews(node);
					}
				});
		toolbar.add(importViews);

		toolbar.add(new SeparatorToolItem());

		renameItem = new TextToolItem("", "icon-rename");
		renameItem.setToolTip(constants.renameSelectedItem());
		renameItem.setEnabled(true);
		renameItem.addSelectionListener(new SelectionListener<ComponentEvent>() {
			public void componentSelected(ComponentEvent ce) {
				handleAction(RENAME);
			}
		});
		toolbar.add(renameItem);
		
		deleteItem = new TextToolItem("", "icon-delete-off");
		deleteItem.setToolTip(constants.deleteSelectedItem());
		deleteItem.setEnabled(false);
		deleteItem
				.addSelectionListener(new SelectionListener<ComponentEvent>() {
					public void componentSelected(ComponentEvent ce) {
						handleAction(DELETE);
					}
				});
		toolbar.add(deleteItem);
	
		navigator.setTopComponent(toolbar);
		navigator.add(viewsTree);
		Workbench wb = (Workbench) Registry.get(Workbench.ID);
		wb.addToViewPanel(navigator);
		ComponentHelper.doAttach(viewsTree);
		navigator.addListener(Events.OnKeyUp, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				viewsTree.fireEvent(Events.OnKeyUp, be);
			} 			
		});
		
		toolbar.add(new SeparatorToolItem());
				
//		SplitToolItem about = new SplitToolItem("");
//		about.setIconStyle("icon-about");
		
		TextToolItem about = new TextToolItem("", "icon-about");
		about.setToolTip(constants.extras());
//		about.addSelectionListener(new SelectionListener<ComponentEvent>() {
//			public void componentSelected(ComponentEvent ce) {
//				WPaloPropertyServiceProvider.getInstance().getBuildNumber(new AsyncCallback<String>() {
//					public void onFailure(Throwable arg0) {
//						displayAboutDialog("<Unknown>");
//					}
//
//					public void onSuccess(String result) {
//						displayAboutDialog(result);
//					}
//				});
//			}
//		});
		
		Menu menu = new Menu();
		editViewProperties = new MenuItem(constants.editViewProperties());
		editViewProperties.addSelectionListener(new SelectionListener<ComponentEvent>() {
			public void componentSelected(ComponentEvent ce) {
				XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
				final String sessionId = user.getSessionId();
				XView targetView = null;
				XObject xObj = null;
				TreeItem item = null;
				TreeNode node = null;
				if (treeBinder != null && treeBinder.getTree() != null) {
					TreeItem selection = treeBinder.getTree().getSelectedItem();
					if (selection != null) {
						item = selection;
						node = (TreeNode) selection.getModel();
						xObj = browserModel.getWrappedXObject(node);
						if (xObj instanceof XFolderElement) {
							XObject source = ((XFolderElement) xObj).getSourceObject();
							if (source != null && source instanceof XView) {
								targetView = (XView) source;
							}
						}
					}
				}
				if (targetView != null) {
					final XView viewToModify = targetView;
					final XObject xObject = xObj;
					final TreeItem selectedItem = item;
					final TreeNode nodeOfView = node;
//					int permission = user.getId().equals(targetView.getOwnerId()) ? 2 : 16;
					int permission = user.isAdmin() ? 0 : (user.getId().equals(targetView.getOwnerId()) ? 16 : 22);
					WPaloCubeViewServiceProvider.getInstance().checkPermission(sessionId, permission, new Callback <Boolean>(){
						public void onSuccess(Boolean result) {
							if (result) {
								final EditViewPropertiesDialog dlg = 
									new EditViewPropertiesDialog(viewToModify);
								dlg.addButtonListener(EditViewPropertiesDialog.BUTTON_OK,
										new Listener<BaseEvent>() {
											public void handleEvent(BaseEvent be) {
												boolean pub = dlg.isPublic();
												boolean edi = dlg.isEditable();
												String ownerId = dlg.getOwner();
												String accId = dlg.getAccountId();
												String dbId = dlg.getDatabaseId();
												String cubeId = dlg.getCubeId();
												WPaloCubeViewServiceProvider.getInstance().setVisibility(
														sessionId, (XFolderElement) xObject, pub, edi, ownerId, accId, dbId, cubeId, new Callback <XView>(){
															public void onSuccess(XView view) {																															
																XFolderElement xObj = (XFolderElement) browserModel.getWrappedXObject(nodeOfView);
																xObj.setSourceObject(view);																
																selectedItem.setIconStyle(getIconStyle(view));
																treeBinder.getStore().update(nodeOfView);
															}});
											}
										});
								dlg.show();
							} else {
								MessageBox.alert(constants.notEnoughRights(), constants.notEnoughRightsToModifyVisibility(), null);
							}
						}
					});
				}
			}
		});
		menu.add(editViewProperties);
		
		createDirectLink = new MenuItem(constants.createDirectLink());
		createDirectLink.addSelectionListener(new SelectionListener<ComponentEvent>() {
			public void componentSelected(ComponentEvent ce) {
				if (treeBinder != null && treeBinder.getTree() != null) {
					TreeItem selection = treeBinder.getTree().getSelectedItem();
					if (selection != null) {
						TreeNode node = (TreeNode) selection.getModel();
						final XObject xObj = browserModel.getWrappedXObject(node);
						if (xObj instanceof XFolderElement) {
							final XObject source = ((XFolderElement) xObj).getSourceObject();
							if (source != null && source instanceof XView) {
								final CreateDirectLinkDialog cdl = new CreateDirectLinkDialog((XView) source);
								cdl.show();
							}
						}
					}
				}
			}
		});
		menu.add(createDirectLink);
		
		MenuItem abi = new MenuItem(constants.about());
		abi.addSelectionListener(new SelectionListener<ComponentEvent>() {
			public void componentSelected(ComponentEvent ce) {
				WPaloPropertyServiceProvider.getInstance().getBuildNumber(new AsyncCallback<String>() {
					public void onFailure(Throwable arg0) {
						displayAboutDialog("<" + constants.unknown() + ">", null);
					}

					public void onSuccess(final String result) {
						WPaloPropertyServiceProvider.getInstance().getCurrentBuildNumber(new AsyncCallback<String []>(){
							public void onFailure(Throwable arg0) {
								displayAboutDialog(result, null);
							}

							public void onSuccess(String [] buildInformation) {
								displayAboutDialog(result, buildInformation);
							}
						});						
					}
				});
			}
		});
		menu.add(abi);		
		
//		MenuItem tti = new MenuItem("Test Tree");
//		tti.addSelectionListener(new SelectionListener<ComponentEvent>() {
//			public void componentSelected(ComponentEvent ce) {
//				final TestFastTreeDialog tftd = new TestFastTreeDialog();
//				tftd.show();
//			}
//		});
//		menu.add(tti);
		
		about.setMenu(menu);
				
		toolbar.add(about);
		return toolbar;
	}

	private final void createNewFolder() {
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloFolderServiceProvider.getInstance().hasCreatePermission(sessionId,
				new Callback <Boolean>(){
					public void onSuccess(Boolean result) {
						if (result) {
							TreeNode selection = getSelectedNode();
							final TreeNode parent = getParentFolder(selection);
							String[] usedFolderNames = browserModel.getUsedFolderNames();
							final NewFolderDialog newFolderDlg = new NewFolderDialog();
							newFolderDlg.setUsedFolderNames(usedFolderNames);
							// add close listener:
							newFolderDlg.addListener(Events.Close, new Listener<WindowEvent>() {
								public void handleEvent(WindowEvent be) {
									if (be.buttonClicked.getItemId().equals(NewFolderDialog.CREATE)) {
										createFolder(newFolderDlg.getFolderName(), parent);
									}
								}
							});
							newFolderDlg.show();							
						} else {
							MessageBox
							.alert(constants.notEnoughRights(),
									constants.notEnoughRightsToCreateNewFolders(),
									null);							
						}
					}
				});
	}

	private final void delete(TreeNode node, boolean confirm) {
		if (node != null) {
			XObject xObj = browserModel.getWrappedXObject(node);
			if (xObj instanceof XFolderElement) {
				deleteFolderElement((XFolderElement) xObj, node, confirm);
			} else if (xObj instanceof XStaticFolder) {
				deleteFolder((XStaticFolder) xObj, node);
			}
		}
	}

	private final void deleteFolderElement(final XFolderElement xFolderElement,
			final TreeNode node, final boolean confirm) {
		final XView xView = browserModel.getView(node);
		if (xView != null) {
			String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
			WPaloCubeViewServiceProvider.getInstance().checkPermission(sessionId, xView.getId(),
					CubeViewEditor.RIGHT_DELETE, new Callback<Boolean>() {
						public void onSuccess(Boolean result) {
							if (result) {
								if (confirm) {
									MessageBox.confirm(constants.confirmDelete(),
											messages.sureToDeleteView(ViewBrowserModel.modify(xFolderElement.getName())),
											new Listener<WindowEvent>() {
												public void handleEvent(
														WindowEvent be) {
													if (!be.buttonClicked
															.getItemId()
															.equalsIgnoreCase(
																	Dialog.NO))
														delete(xFolderElement,
																node);
												}
											});
								} else
									delete(xFolderElement, node);
							} else {
								MessageBox
										.alert(
												constants.notEnoughRights(),
												constants.notEnoughRightsToDeleteView(),												
												null);
							}
						}
					});
		} else {
			delete(xFolderElement, node);
		}
	}

	private final void deleteFolder(final XStaticFolder xFolder,
			final TreeNode node) {
		XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
		WPaloCubeViewServiceProvider.getInstance().checkPermission(user.getSessionId(),
				4, new Callback<Boolean>() {
					public void onSuccess(Boolean result) {
						if (result) {
							MessageBox.confirm(constants.confirmDelete(),
									messages.sureToDeleteFolder(ViewBrowserModel.modify(xFolder.getName())), new Listener<WindowEvent>() {
								public void handleEvent(WindowEvent be) {
									if (!be.buttonClicked.getItemId().equalsIgnoreCase(Dialog.NO))
										delete(xFolder, node);
								}
							});							
						} else {
							MessageBox.alert(constants.notEnoughRights(),
									constants.notEnoughRightsToDeleteFolder(),
									null);							
						}
					}
				});
	}

	private final void createViewsAfterCheck(final TreeNode node) {
		// final XStaticFolder xParentFolder = getFolderOrParentFolderOf(node);
		// final ViewCreateDialog createDlg = new ViewCreateDialog();
		XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
		int permission = user.isAdmin() ? 0 : 16;		
		WPaloCubeViewServiceProvider.getInstance().checkPermission(user.getSessionId(), permission, new AsyncCallback <Boolean>(){
			private final void showDialog(boolean showBoxes) {
				final ViewImportDialog createDlg = new ViewImportDialog(false, showBoxes);
				createDlg.addButtonListener(ViewImportDialog.BUTTON_OK,
						new Listener<BaseEvent>() {
							public void handleEvent(BaseEvent be) {
								XView[] xViews = createDlg.getSelectedViews();
								TreeNode parent = getParentFolder(node);
								importViews(constants.creatingView(), xViews, parent, createDlg
										.isPublic(), createDlg.isEditable());
							}
						});
				createDlg.show();						
			}

			public void onFailure(Throwable t) {
				showDialog(false);
			}

			public void onSuccess(Boolean result) {
				showDialog(result);
			}
		});
	}
	
	private final void importViewsAfterCheck(final TreeNode node) {
		XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
		int permission = user.isAdmin() ? 0 : 16;		
		WPaloCubeViewServiceProvider.getInstance().checkPermission(user.getSessionId(), permission, new AsyncCallback <Boolean>(){
			private final void showDialog(boolean showBoxes) {
				final ViewImportDialog importDlg = new ViewImportDialog(true, showBoxes);
				importDlg.addButtonListener(ViewImportDialog.BUTTON_OK,
						new Listener<BaseEvent>() {
							public void handleEvent(BaseEvent be) {
								XView[] xViews = importDlg.getSelectedViews();
								TreeNode parent = getParentFolder(node);
								importViews(constants.importingViews(), xViews, parent, importDlg
										.isPublic(), importDlg.isEditable());
							}
						});
				importDlg.show();						
			}
			
			public void onFailure(Throwable t) {
				showDialog(false);
			}

			public void onSuccess(Boolean result) {
				showDialog(result);
			}
			
		});
	}
	
	private final void createViews(final TreeNode node) {
		if (createRight == -1) { 
			String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
			WPaloCubeViewServiceProvider.getInstance().
				checkPermission(sessionId, CubeViewEditor.RIGHT_CREATE, new Callback <Boolean>(){
					public void onSuccess(Boolean result) {
						if (result) {		
							createRight = 1;
							createViewsAfterCheck(node);
						} else {
							createRight = 0;
							MessageBox.alert(constants.notEnoughRights(),
									constants.notEnoughRightsToCreateNewViews(), null);
						}
					}
				});
		} else if (createRight == 0) {
			MessageBox.alert(constants.notEnoughRights(),
					constants.notEnoughRightsToCreateNewViews(), null);			
		} else {
			createViewsAfterCheck(node);
		}
	}

	private final TreeNode getParentFolder(TreeNode node) {
		XObject xObj = browserModel.getWrappedXObject(node);
		if (xObj instanceof XStaticFolder)
			return node;
		return node.getParent();
	}

	private final void importViews(String operation, final XView[] xViews,
			final TreeNode parent, final boolean isPublic, final boolean isEditable) {
		XStaticFolder xParentFolder = (XStaticFolder) browserModel
				.getWrappedXObject(parent);
		((Workbench) Registry.get(Workbench.ID)).showWaitCursor(operation);
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloFolderServiceProvider.getInstance().importViewsAsFolderElements(sessionId,
				xViews, xParentFolder, isPublic, isEditable,
				new Callback<XFolderElement[]>(constants.couldNotCreateView()) {
					public void onSuccess(XFolderElement[] xFolderElements) {
		
						browserModel.addViews(xFolderElements, parent);
						TreeItem it = viewsTree.getSelectedItem();
						if (it != null) {
							viewsTree.expandPath(it.getPath());
						}
						hideWaitCursor();

					}
				});
	}

	private final void importViews(final TreeNode node) {
		if (createRight == -1) { 
			String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
			WPaloCubeViewServiceProvider.getInstance().
				checkPermission(sessionId, CubeViewEditor.RIGHT_CREATE, new Callback <Boolean>(){
					public void onSuccess(Boolean result) {
						Log.info("nel view browser::importViews");
						if (result) {		
							createRight = 1;
							importViewsAfterCheck(node);
						} else {
							createRight = 0;
							MessageBox.alert(constants.notEnoughRights(),
									constants.notEnoughRightsToCreateNewViews(), null);
						}
					}
				});
		} else if (createRight == 0) {
			MessageBox.alert(constants.notEnoughRights(),
					constants.notEnoughRightsToCreateNewViews(), null);			
		} else {
			importViewsAfterCheck(node);
		}
	}

	final void handleAction(String action) {
		TreeNode selection = getSelectedNode();
		if (selection != null) {
			if (action.equals(OPEN)) {
				doOpen(selection);
			} else if (action.equals(DELETE)) {
				doDelete(viewsTree.getSelectedItems());
			} else if (action.equals(RENAME)) {
				doRename(selection);
			}
		}
	}

	private final void doOpen(TreeNode selection) {
		XObject xObj = browserModel.getWrappedXObject(selection);
		if (xObj instanceof XFolderElement) {
			fireEvent(new AppEvent<TreeNode>(WPaloEvent.EDIT_VIEWBROWSER_VIEW,
					selection));
		}
	}

	private final void doDelete(final List<TreeItem> selection) {
		if (selection.isEmpty())
			return;
		TreeItem sel = selection.get(0);
		if (sel != null) {
			TreeNode tn = (TreeNode) sel.getModel();
			if (tn != null && tn.getXObject() instanceof XObjectWrapper &&
					((XObjectWrapper) tn.getXObject()).getXObject() instanceof XStaticFolder) {
				delete(selection);
				return;
			}
		}
		MessageBox.confirm(constants.confirmDelete(), constants.sureToDeleteItems(),
				new Listener<WindowEvent>() {
					public void handleEvent(WindowEvent be) {
						if (!be.buttonClicked.getItemId().equalsIgnoreCase(Dialog.NO)) {
							delete(selection);
						}
					}
				});
	}
	private final void delete(List<TreeItem> selection) {
		for (TreeItem selectedItem : selection) {
			if (selectedItem != null) {
				TreeNode node = (TreeNode) selectedItem.getModel();
				if (node == null)
					node = getVisibleRoot();
				delete(node, false);
			}
		}
	}

	private final void doRename(TreeNode selection) {
		TreeItem selectedItem = viewsTree.getSelectedItem();
		XObject xObj = browserModel.getWrappedXObject(selection);
		if ((xObj instanceof XFolderElement || xObj instanceof XStaticFolder)
				&& selectedItem instanceof EditableTreeItem)
			rename((EditableTreeItem) selectedItem);
	}

	private final void delete(final XFolderElement xFolderElement,
			final TreeNode node) {
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloFolderServiceProvider.getInstance().deleteFolderElement(sessionId,
				xFolderElement,
				new Callback<Void>(messages.couldNotDeleteView(ViewBrowserModel.modify(xFolderElement.getName()))) {
					public void onSuccess(Void arg0) {
						delete((XView) xFolderElement.getSourceObject(), node);
					}
				});
	}

	private final void delete(final XView xView, final TreeNode node) {
		if (xView == null) {
			fireViewDeleted(node);
		} else {
			String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
			WPaloCubeViewServiceProvider.getInstance().deleteView(sessionId, 
					xView,
					new Callback<Void>(messages.couldNotDeleteView(ViewBrowserModel.modify(xView.getName()))) {
						public void onSuccess(Void arg0) {
							fireViewDeleted(node);
						}
					});
		}
	}

	private final void fireViewDeleted(TreeNode node) {
		fireEvent(new AppEvent<TreeNode>(WPaloEvent.DELETED_VIEWBROWSER_VIEW,
				node));
		browserModel.delete(node);
	}

	private final void delete(final XStaticFolder folder, final TreeNode node) {
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloFolderServiceProvider.getInstance().deleteFolder(sessionId, 
				folder,
				new Callback<Void>(messages.couldNotDeleteFolder(ViewBrowserModel.modify(folder.getName()))) {
					public void onSuccess(Void arg0) {
						browserModel.delete(node);
					}
				});
	}

	private final void checkSelection(Tree tree) {
		TreeItem selection = tree.getSelectedItem();
		if (selection != null) {
			TreeNode node = (TreeNode) selection.getModel();
			XObject xObj = browserModel.getWrappedXObject(node);
			mayDelete = true;
			if (deleteItem != null) {
				deleteItem.setEnabled(true);
				deleteItem.setIconStyle("icon-delete");
			}
			if (xObj instanceof XStaticFolder) {
				if (node.getParent().getParent() == null) {
					mayDelete = false;
					if (deleteItem != null) {
						deleteItem.setEnabled(false);
						deleteItem.setIconStyle("icon-delete-off");
					}							
				}
			}
			if (mayDelete && viewsTree != null) {
				if (viewsTree.getContextMenu().getItemCount() == 2) {
					viewsTree.setContextMenu(createContextMenu());
				}
			} else if (!mayDelete && viewsTree != null) {
				if (viewsTree.getContextMenu().getItemCount() == 3) {
					viewsTree.setContextMenu(createContextMenu());
				}						
			}
			if (mayDelete) {
				if (renameItem != null) {
					renameItem.setEnabled(true);
					renameItem.setIconStyle("icon-rename");
				}
				if (editViewProperties != null) {
					editViewProperties.setEnabled(xObj instanceof XFolderElement);
				} 
				if (createDirectLink != null) {
					createDirectLink.setEnabled(xObj instanceof XFolderElement);
				}
			} else {
				if (editViewProperties != null) {
					editViewProperties.setEnabled(false);
				} 		
				if (createDirectLink != null) {
					createDirectLink.setEnabled(false);
				}
			}
		}		
	}
	
	private final Tree createTree(XUser user, TreeLoader<TreeNode> loader) {
		final Tree tree = new Tree();
		tree.setIndentWidth(18);
		tree.setSelectionMode(SelectionMode.SINGLE);
		TreeStore<TreeNode> treeStore = new TreeStore<TreeNode>(loader);
		treeBinder = new TreeNodeBinder(tree, treeStore);
		treeBinder.setDisplayProperty("name");
		treeBinder.setAutoSelect(true);
		treeBinder.init();
		// tree model:
		browserModel = new ViewBrowserModel(user, treeStore);
		tree.addListener(Events.SelectionChange, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				TreeItem selection = tree.getSelectedItem();
				if (selection == null) {
					if (deleteItem != null) {
						deleteItem.setEnabled(false);
						deleteItem.setIconStyle("icon-delete-off");
					}
					if (renameItem != null) {
						renameItem.setEnabled(false);
					}
					if (editViewProperties != null) {
						editViewProperties.setEnabled(false);
					}
					if (createDirectLink != null) {
						createDirectLink.setEnabled(false);
					}
				} else {
					checkSelection(tree);
				}
			}
		});
		tree.addListener(Events.OnClick, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				checkSelection(tree);				
			}
		});

		tree.addListener(Events.OnDoubleClick, new Listener<TreeEvent>() {
			public void handleEvent(TreeEvent be) {
				handleAction(OPEN);
			}
		});
		tree.addListener(Events.OnKeyUp, new Listener<TreeEvent>() {
			public void handleEvent(TreeEvent te) {
				if (te.getKeyCode() == F2) {
					handleAction(RENAME);
				}
			}
		});
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloFolderServiceProvider.getInstance().hasWritePermission(sessionId,
				new Callback<Boolean>() {
					public void onSuccess(Boolean result) {
						if (result) {
							addDragAndDrop(treeBinder);
						}
					}
				});

		
		tree.setContextMenu(createContextMenu());
		tree.getRootItem().setExpanded(true);

		return tree;
	}

	private final Menu createContextMenu() {
		ContextMenu ctxtMenu = new ContextMenu(this);
		ctxtMenu.add(OPEN, null);
		if (mayDelete) {
			ctxtMenu.add(DELETE, "icon-delete");
		}
		ctxtMenu.add(RENAME, "icon-rename");
		return ctxtMenu.getMenu();
	}

	private final void rename(final EditableTreeItem item) {
		final Editor editor = item.getEditor();
		Listener<EditorEvent> editorListener = new Listener<EditorEvent>() {
			public void handleEvent(EditorEvent ev) {
				editor.removeListener(Events.Complete, this);
				if (!ev.startValue.equals(ev.value))
					doRename(item);
			}
		};
		editor.addListener(Events.Complete, editorListener);
		item.startEdit();
	}

	private final void doRename(final TreeItem item) {
		TreeNode node = (TreeNode) item.getModel();
		final XObject obj = browserModel.getWrappedXObject(node);
		if (obj instanceof XStaticFolder) {
			String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
			WPaloFolderServiceProvider.getInstance().hasWritePermission(sessionId,
					new Callback<Boolean>() {
						public void onSuccess(Boolean result) {
							if (result) {
								rename((XStaticFolder) obj, item);
							} else {
								MessageBox
										.alert(constants.notEnoughRights(),
												constants.notEnoughRightsToRenameFolder(),
												null);
							}
						}
					});
			return;
		}
		final XFolderElement xFolderElement = (XFolderElement) browserModel
				.getWrappedXObject(node);
		final XView xView = browserModel.getView(node);
		if (xView != null) {
			String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
			WPaloCubeViewServiceProvider.getInstance().checkPermission(sessionId, xView.getId(),
					CubeViewEditor.RIGHT_WRITE, new Callback<Boolean>() {
						public void onSuccess(Boolean result) {
							if (result) {
								rename(xFolderElement, xView, item);
							} else {
								MessageBox
										.alert(constants.notEnoughRights(),
												constants.notEnoughRightsToRenameView(),
												null);
							}
						}

						public void onFailure(Throwable arg0) {
							// TODO Auto-generated method stub
							
						}
					});
		}
	}

	protected String demodify(String x) {
		x = x.replaceAll("&amp;", "&");
		x = x.replaceAll("&apos;", "'");
		x = x.replaceAll("&lt;", "<");
		x = x.replaceAll("&gt;", ">");
		x = x.replaceAll("&quot;", "\"");
		return x;
	}
	
	private final void rename(final XFolderElement xFolderElement,
			final XView xView, final TreeItem item) {
		final String newName = demodify(item.getText());
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloFolderServiceProvider.getInstance().renameFolderElement(sessionId,
				xFolderElement, newName,
				new Callback<Void>(constants.failedToRenameView()) {
					public void onSuccess(Void v) {
						xFolderElement.setName(newName);
						// successful => rename internal view too:
						rename(xView, item);						
					}
				});
	}

//	private final void checkStyle(TreeItem item) {
//		if (item.getModel() instanceof TreeNode) {
//			TreeNode tn = (TreeNode) item.getModel();
//			if (tn != null && tn.getXObject() != null) {
//				XObject x = tn.getXObject();
//				if (x instanceof XObjectWrapper) {
//					x = ((XObjectWrapper) x).getXObject();
//				}
//				if (x != null && x instanceof XFolderElement) {
//					x = ((XFolderElement) x).getSourceObject();
//				}
//				if (x != null && x instanceof XView) {
//					XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
//					if (!user.getId().equals(((XView) x).getOwnerId())) {
//						item.setTextStyle("italic");
//					}
//				}
//			}
//		}				
//	}
	
	private final void rename(final XStaticFolder folder, final TreeItem item) {
		final String newName = demodify(item.getText());
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloFolderServiceProvider.getInstance().renameFolder(sessionId, folder, newName,
				new Callback<Void>(constants.failedToRenameFolder()) {
					public void onSuccess(Void arg0) {
						TreeNode node = (TreeNode) item.getModel();
						node.getXObject().setName(newName);
						browserModel.refresh(node);
					}
				});
	}

	private final void rename(final XView xView, final TreeItem item) {
		final String newName = demodify(item.getText());
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().renameView(sessionId, xView, newName,
				new Callback<Void>(constants.failedToRenameView()) {
					public void onSuccess(Void arg0) {
						TreeNode node = (TreeNode) item.getModel();
						node.getXObject().setName(newName);
						browserModel.refresh(node);
//						checkStyle(item);
						fireEvent(new AppEvent<TreeNode>(
								WPaloEvent.RENAMED_VIEWBROWSER_VIEW, node));
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	private final void dropToStaticFolder(XStaticFolder toFolder, TreeItem target, DNDEvent e) {
		final TreeNode newParent = findNodeOf(toFolder);
		List movedNodes = (List) e.data;
		XObject [] xElems = getMoveableElementsFrom(movedNodes, newParent);
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloFolderServiceProvider.getInstance().move(sessionId, xElems, toFolder,
			new Callback<Void>(constants.couldNotMoveViews()) {
				public void onSuccess(Void arg0) {
				}
			}
		);												
	}

	private final void addDragAndDrop(final TreeBinder<TreeNode> binder) {
		final Tree tree = binder.getTree();
		// D 'n' D:
		TreeDragSource dragSource = new TreeDragSource(binder);
		dragSource.addDNDListener(new DNDListener() {
			@SuppressWarnings("unchecked")
			public void dragStart(DNDEvent e) {
				List selection = (List) e.data;
				if (!isValid(selection)) {
					e.doit = false;
					e.status.setStatus(false);
				}
			}
		});

		TreeDropTarget dropTarget = new TreeDropTarget(binder);
		dropTarget.setAllowSelfAsSource(true);
		dropTarget.setFeedback(Feedback.APPEND);
		dropTarget.setOperation(Operation.MOVE);
		dropTarget.addDNDListener(new DNDListener() {
			public void dragDrop(final DNDEvent e) {
				final TreeItem target = tree.findItem(e.getTarget());
				if (target != null) {
					XObject wrappedObj = browserModel.getWrappedXObject((TreeNode) target.getModel());
					if (wrappedObj instanceof XStaticFolder) {
						dropToStaticFolder((XStaticFolder) wrappedObj, target, e);
					}						
				}
			}
		});
	}

	private final boolean isValid(List selection) {
		if (selection == null)
			return false;
		for (Object nd : selection) {
			BaseTreeModel btm = ((BaseTreeModel) nd);
			Object model = btm.getProperties().get("model");
			if (model != null && model instanceof TreeNode) {
				TreeNode node = (TreeNode) model;
				XObject wrappedXObj = browserModel.getWrappedXObject(node);
				// if(wrappedXObj instanceof XStaticFolder)
				// return false;
			} else {
				return false;
			}
		}
		return true;
	}

	private final XObject[] getMoveableElementsFrom(List nodes,
			TreeNode newParent) {
		List<XObject> elements = new ArrayList<XObject>();
		for (Object nd : nodes) {
			BaseTreeModel btm = ((BaseTreeModel) nd);
			Object model = btm.getProperties().get("model");
			if (model != null && model instanceof TreeNode) {
				TreeNode node = (TreeNode) model;
				XObject wrappedXObj = browserModel.getWrappedXObject(node);
				if (wrappedXObj instanceof XFolderElement
						|| wrappedXObj instanceof XStaticFolder) {
					elements.add(wrappedXObj);
					node.setParent(newParent);
				}
			}
		}
		return elements.toArray(new XObject[0]);
	}

	private final TreeNode getSelectedNode() { // TreeNode defaultNode) {
		TreeNode node = null;
		// TreeNode defaultNode = (TreeNode) viewsTree.getRootItem().getModel();
		TreeItem selectedItem = viewsTree.getSelectedItem();
		if (selectedItem != null)
			node = (TreeNode) selectedItem.getModel();
		return node != null ? node : getVisibleRoot();
	}

	private final TreeNode getVisibleRoot() {
		TreeItem root = viewsTree.getRootItem();
		if (root.getItemCount() > 0)
			return (TreeNode) root.getItem(0).getModel();
		return null;
	}

	private final void createFolder(String name, final TreeNode parent) {
		XObjectWrapper wrappedXObj = (XObjectWrapper) parent.getXObject();
		XStaticFolder parentFolder = (XStaticFolder) wrappedXObj.getXObject();
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloFolderServiceProvider.getInstance().createFolder(sessionId, name,
				parentFolder,
				new Callback<XStaticFolder>(constants.couldNotAddFolder()) {
					public void onSuccess(XStaticFolder folder) {
						if (folder != null) {
							browserModel.addFolder(folder, parent);
							TreeItem it = viewsTree.getSelectedItem();
							if (it != null) {
								viewsTree.expandPath(it.getPath());
							}
						} 
					}
				});
	}
}

class TreeNodeBinder extends TreeBinder<TreeNode> {

	private final TreeNodeIconProvider iconProvider = new TreeNodeIconProvider();

	public TreeNodeBinder(Tree tree, TreeStore<TreeNode> store) {
		super(tree, store);
		setIconProvider(iconProvider);
	}

	protected TreeItem createItem(TreeNode model) {
		// final TreeItem item = super.createItem(model);
		TreeItem item = createEditableTreeItem(model);
		item.setLeaf(!iconProvider.isFolder(model));
		return item;
	}

	protected TreeItem createEditableTreeItem(TreeNode model) {
		TreeItem item = new EditableTreeItem();

		update(item, model);

		if (loader != null) {
			item.setLeaf(!loader.hasChildren(model));
		} else {
			item.setLeaf(!hasChildren(model));
		}

		setModel(item, model);
		return item;
	}

}

class EditableTreeItem extends TreeItem {
	private TextField<String> textField = new TextField<String>();
	private Editor editor = new Editor(textField) {
	    public Object postProcessValue(Object value) {
	    	return modify("" + value);
		}

		public Object preProcessValue(Object value) {
		    return demodify("" + value);
		}		
	};
	
	private EditableTreeItemUI ui;
	
	public EditableTreeItem() {
		super();
		editor.setCompleteOnEnter(true);
		editor.setCancelOnEsc(true);
		editor.addListener(Events.Complete, new Listener<EditorEvent>() {
			public void handleEvent(EditorEvent be) {
				setText(demodify((String) be.value));
			}
		});
	}
	
	protected TreeItemUI getTreeItemUI() {
		if (ui == null) {
			ui = new EditableTreeItemUI(this);
		}
		return ui;
	}

	public void setText(String text) {
		String r = modify(text);
		this.textField.setValue(r);
		super.setText(r);
	}

	public String getText() {
		String text = modify(demodify(textField.getValue()));		
		return text;
	}
	
	protected String demodify(String x) {
		x = x.replaceAll("&amp;", "&");
		x = x.replaceAll("&apos;", "'");
		x = x.replaceAll("&lt;", "<");
		x = x.replaceAll("&gt;", ">");
		x = x.replaceAll("&quot;", "\"");
		return x;
	}
	
	protected String modify(String x) {
		x = x.replaceAll("&", "&amp;");
		x = x.replaceAll("\"", "&quot;");
		x = x.replaceAll("'", "&apos;");
		x = x.replaceAll("<", "&lt;");
		x = x.replaceAll(">", "&gt;");
		return x;
	}
	
	public Editor getEditor() {
		return editor;
	}

	public void startEdit() {
		editor.startEdit(this.getElement(), getText());
		align(editor);
	}

	private final void align(Editor editor) {
		Element icon = ui.getIconElement();
		int x = icon.getAbsoluteLeft() + icon.getOffsetWidth() + 2;
		int y = icon.getAbsoluteTop();
		int w = getOffsetWidth() + 15 - x;
		// Need to use the icon's offsetHeight, because otherwise
		// the editor will be too high for expanded folder elements
		int h = icon.getOffsetHeight();
		if (GXT.isIE) {
			h -= 2;
		}
		// getOffsetHeight() + 1;

		editor.setBounds(x, y, w, h);
	}

	public boolean clickedOnIcon(Point mouseXY) {
		Element icon = ui.getIconElement();
		int iconX = icon.getAbsoluteLeft();
		int iconW = iconX + icon.getOffsetWidth();
		int iconY = icon.getAbsoluteTop();
		int iconH = iconY + icon.getOffsetHeight();
		return (mouseXY.x >= iconX && mouseXY.x <= iconW)
				&& (mouseXY.y >= iconY && mouseXY.y <= iconH);
	}

	public void removeAll() {
		// TODO Auto-generated method stub
		super.removeAll();
	}

}

class EditableTreeItemUI extends TreeItemUI {

	public EditableTreeItemUI(TreeItem item) {
		super(item);
	}

	Element getIconElement() {
		return iconEl;
	}

	Element getTextElement() {
		return textEl;
	}
}

class TreeNodeIconProvider implements ModelStringProvider<TreeNode> {

	public final boolean isFolder(TreeNode node) {
		return getObjectType(node).equals(XStaticFolder.TYPE);
	}

	public String getStringValue(TreeNode node, String property) {
		String xType = getObjectType(node);
		
		if (xType.equals(XFolderElement.TYPE) || xType.equals(XView.TYPE)) {
			return ViewBrowser.getIconStyle(node);
		}
		else
			return "icon-static-folder";
	}

	private final String getObjectType(TreeNode node) {
		XObject xObject = getWrappedXObject(node);
		return xObject.getType();
	}

	private final XObject getWrappedXObject(TreeNode node) {
		XObject xObj = node.getXObject();
		if (xObj instanceof XObjectWrapper)
			return ((XObjectWrapper) xObj).getXObject();
		return xObj;
	}
	
}

class ContextMenu extends SelectionListener<MenuEvent> {

	private final Menu contextMenu = new Menu();
	private final ViewBrowser browser;

	ContextMenu(ViewBrowser browser) {
		this.browser = browser;
	}

	final Menu getMenu() {
		return contextMenu;
	}

	final void add(String action, String icon) {
		MenuItem item = new MenuItem();
		item.setText(action);
		item.setIconStyle(icon);
		item.addSelectionListener(this);
		contextMenu.add(item);
	}

	public void componentSelected(MenuEvent me) {
		browser.handleAction(((MenuItem) me.item).getText());
	}	
}