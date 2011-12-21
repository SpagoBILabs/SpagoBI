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
package com.tensegrity.wpalo.client.ui.mvc.workbench;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XDirectLinkData;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.wpalo.client.DisplayFlags;
import com.tensegrity.wpalo.client.WPalo;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.WPaloPropertyServiceProvider;
import com.tensegrity.wpalo.client.WPaloServiceProvider;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.services.wpalo.WPaloControllerServiceProvider;
import com.tensegrity.wpalo.client.ui.dialog.LoginDialog;
import com.tensegrity.wpalo.client.ui.dialog.MessageBoxUtils;
import com.tensegrity.wpalo.client.ui.editor.CloseObserver;
import com.tensegrity.wpalo.client.ui.editor.IEditor;
import com.tensegrity.wpalo.client.ui.mvc.cubeview.LargeQueryWarningDialog;
import com.tensegrity.wpalo.client.ui.mvc.viewbrowser.ViewBrowserEditor;
import com.tensegrity.wpalo.client.ui.widgets.BusyIndicatorPanel;
import com.tensegrity.wpalo.client.ui.widgets.Hyperlink;
import com.tensegrity.wpalo.client.ui.widgets.OnClickListener;
import com.tensegrity.wpalo.client.ui.window.AbstractTopLevelView;

/**
 * <code>Workbench</code>
 * <p>The workbench is the main view of the new wpalo implementation. It simply 
 * defines a view panel on the left side and an editor panel attached on the 
 * right.</p>  
 *
 * @version $Id: Workbench.java,v 1.44 2010/02/16 13:53:42 PhilippBouillon Exp $
 **/
public class Workbench extends AbstractTopLevelView {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages  messages  = Resources.getInstance().getMessages();

	/** id to access a workbench instance via the global {@link Registry} */
	public static final String ID = "com.tensegrity.wpalo.workbench";

	private XUser user;	//the logged in user
	private IEditor currentEditor;
	private BusyIndicatorPanel waitCursor;
	private boolean isPaloSuite = false;
	
	Workbench(Controller controller) {
		super(controller);
	}
			
	/** Returns the currently logged in user or <code>null</code> */
	public final XUser getUser() {
		return user;
	}
	
	public final boolean isPaloSuite() {
		return isPaloSuite;
	}
	
	public final void setPaloSuite(boolean v) {
		isPaloSuite = v;
	}
	
	public final void hideWaitCursor() {
		if(waitCursor == null)
			return;
		waitCursor.hide();
		waitCursor = null;
	}
	public final void showWaitCursor(String msg) {
		hideWaitCursor();
		waitCursor = new BusyIndicatorPanel();
		waitCursor.show(msg, false);
	}

	public final void showWaitCursor(String msg, boolean pushToFront) {
		hideWaitCursor();
		waitCursor = new BusyIndicatorPanel();
		waitCursor.show(msg, pushToFront);
	}
	
	public final IEditor getCurrentEditor() {
		return currentEditor;
	}
	
	public final void checkOpen(IEditor newEditor, final AsyncCallback <Boolean> callback) {
		if (currentEditor != null && !currentEditor.isDirty() && currentEditor instanceof ViewBrowserEditor && !(newEditor instanceof ViewBrowserEditor)) {
			ViewBrowserEditor viewEdi = (ViewBrowserEditor) currentEditor;
			if (viewEdi.hasOpenViews()) {
				String messageText = constants.closeViewHint();				
				MessageBox.confirm(constants.closeEditor(), messageText,
						new Listener<WindowEvent>() {
							public void handleEvent(WindowEvent be) {
								if (be.buttonClicked.getItemId()
										.equalsIgnoreCase(Dialog.YES)) {
									((ViewBrowserEditor) currentEditor).setUnDirty();
									currentEditor.close(new CloseObserver() {
										public void finishedClosed() {
											((ViewBrowserEditor) currentEditor).setCloseAll(false);
											callback.onSuccess(true);
										}
									});
									return;
								} else if (be.buttonClicked.getItemId().equalsIgnoreCase(Dialog.NO)) {
									if (callback != null) {
										callback.onSuccess(false);
									}
									return;
								} 
								if (callback != null) {
									callback.onSuccess(true);
								}
							}
						});												
			} else {
				if (callback != null) {
					callback.onSuccess(true);
				}
			}
			return;
		}
		if (currentEditor != null && currentEditor.isDirty() &&
				(!currentEditor.equals(newEditor) || !(currentEditor instanceof ViewBrowserEditor))) {
			String messageText = messages.saveEditorBeforeClosing(currentEditor.getTitle()); 
			if (currentEditor.getTitle().equals(constants.views())) {
				messageText = constants.closeModifiedViewHint(); 
			}
			MessageBoxUtils.yesNoCancel(constants.saveEditor(), messageText,
					new Listener<WindowEvent>() {
						public void handleEvent(WindowEvent be) {
							if (be.buttonClicked.getItemId()
									.equalsIgnoreCase(Dialog.YES)) {
								currentEditor.doSave(callback);
								return;
							} else if (be.buttonClicked.getItemId().equalsIgnoreCase(Dialog.NO)) {
								if (currentEditor instanceof ViewBrowserEditor) {
									((ViewBrowserEditor) currentEditor).setUnDirty();
									currentEditor.close(new CloseObserver() {
										public void finishedClosed() {
											((ViewBrowserEditor) currentEditor).setCloseAll(false);
											callback.onSuccess(true);
										}
									});
									return;
								}
								// Do nothing...
							} else if (be.buttonClicked.getItemId().equalsIgnoreCase(Dialog.CANCEL)) {								
								if (callback != null) {
									callback.onSuccess(false);
								}
								return;
							}
							if (callback != null) {
								callback.onSuccess(true);
							}
						}
					});								
		} else {
			if (callback != null) {
				callback.onSuccess(true);
			}			
		}
	}
	
	public final void open(final IEditor editor) {
		if (editor.equals(currentEditor) && editor instanceof ViewBrowserEditor) {
			return;
		}
		currentEditor = editor;
		editorpanel.setHeading(editor.getTitle());
		editorpanel.removeAll();
		editorpanel.add(editor.getPanel(), new RowData(1, 1));
		editorpanel.layout();
	}
	
	/** for testing purpose only */
	public final void open(Widget widget) {
		editorpanel.removeAll();
		editorpanel.add(widget);
		editorpanel.layout();
	}
	
	public final void close(final IEditor editor, final CloseObserver observer) {
		if(editor != null){
			editor.close(observer);						
		}
		editorpanel.setHeading("");
		editorpanel.removeAll();		
	}
	
	protected final void handleEvent(AppEvent<?> event) {
		switch(event.type) {
		case WPaloEvent.INIT:
			if (event.data != null && event.data instanceof DisplayFlags) {				
				DisplayFlags displayFlags = (DisplayFlags) event.data;
				boolean createViewPanel = !displayFlags.isHideNavigator();
				this.user = displayFlags.getUser();
				initializeUI(createViewPanel, displayFlags);	
			} else {
				initializeUI(true, null);
			}
			
			break;
		case WPaloEvent.LOGIN:
			try {
			login();
			} catch (Throwable t) {
				t.printStackTrace();
			}
			break;
		case WPaloEvent.VIEW_MODE_LOGOUT:
			logout();
			break;
		}
	}
	
	private final void initializeUI(boolean createViewPanel, DisplayFlags displayFlags) {
		viewport = new LayoutContainer();
		viewport.setSize("100%", "100%");
		viewport.setLayout(new BorderLayout());
		viewport.setMonitorWindowResize(true);
		
		if (createViewPanel) {
			createViewPanel();
			viewpanel.setHeight("95%");
			createViewPanelStatusLine(1);
			fillViewPanelStatusLine();
		}
		createEditorPanel(createViewPanel);
		createEditorPanelStatusLine(displayFlags != null);
		fillEditorPanelStatusLine();
	
		//registry serves as a global context:
		Registry.register(ID, this);
		setPaloSuite(false);
		viewport.getLayout().setExtraStyle("wpalo-overview");
		((WPalo) Registry.get(WPalo.ID)).show(viewport);				
	}

	
	private final void fillViewPanelStatusLine() {		
		Hyperlink logout = new Hyperlink(constants.logout());
		logout.addListener(new OnClickListener() {
			public void clicked(ComponentEvent ce) {
				Dispatcher.forwardEvent(WPaloEvent.LOGOUT_CLICKED);
			}
		});
		viewpanelStatusLine.add(logout);
		
	}

	private final void fillEditorPanelStatusLine() {
		// FOR TESTING PURPOSE ONLY
		if (!true) {
			Hyperlink showViewEditor = new Hyperlink("View Editor");
			showViewEditor.addListener(new OnClickListener() {
				public void clicked(ComponentEvent ce) {
					showViewEditor();
				}
			});
			editorpanelStatusLine.add(showViewEditor);
		}
		// ~ FOR TESTING PURPOSE ONLY
	}

	private final String getValue(String key, String link) {
		String temp = link.toLowerCase();
		int index = temp.indexOf(key);
		if (index == -1) {
			return null;
		}
		int begin = temp.indexOf("\"", index);
		int end = temp.indexOf("\"", begin + 1);
		if (begin == -1 || end == -1) {
			return null;
		}
		return link.substring(begin + 1, end);
	}

	
	private final void login() {
//		CubeViewEditor.hasBeenResized = false;
//		CubeViewEditor.fromDirectLink = false;
		final String directLink = Window.Location.getParameter("options");
		String usr = null;
		if (directLink != null) {
			usr = getValue("user", directLink);
		}
		final LoginDialog dlg = new LoginDialog(usr);		
		dlg.addListener(Events.Hide, new Listener<BoxComponentEvent>() {
			public void handleEvent(BoxComponentEvent be) {	
				user = dlg.getUser();				
				if (directLink == null) {
					Dispatcher.forwardEvent(WPaloEvent.INIT, user);
				} else {
					final Dispatcher dispatcher = Dispatcher.get();
					final String locale = Window.Location.getParameter("locale");
					WPaloServiceProvider.getInstance().openViewAfterLogin(locale, user.getSessionId(), directLink,
							new AsyncCallback<XDirectLinkData>() {
								public void onSuccess(final XDirectLinkData data) {									
									if (!data.isAuthenticated()) {
										dispatcher.dispatch(WPaloEvent.APP_START);
										if (data.getErrors().length > 0) {
											StringBuffer buf = new StringBuffer();
											for (String s: data.getErrors()) {
												buf.append(s + "\n");
											}
											MessageBox.alert(constants.errorsWhileProcessingOptions(),
													buf.toString(), null);
										}
									} else {
//										CubeViewEditor.fromDirectLink = true;
										XView [] xViews = data.getViews();
										if (xViews == null || xViews.length == 0) {
											dispatcher.dispatch(WPaloEvent.INIT, DisplayFlags.createDisplayFlags(user, data.getGlobalDisplayFlags()));
											directLogin(user);
											if (data.getErrors().length > 0) {
												StringBuffer buf = new StringBuffer();
												for (String s: data.getErrors()) {
													buf.append(s + "\n");
												}
												MessageBox.alert(constants.errorsWhileProcessingOptions(), buf.toString(), null);
											}
											return;
										}
										LargeQueryWarningDialog.hideWarnDialog = true;										
										for (int i = 1; i < xViews.length; i++) {
											((WorkbenchController) getController()).getViewBrowserController().
												addViewToLoad(xViews[i]);
										}
										XView xView = xViews[0];
										DisplayFlags.setDisplayFlagsFor(xView, user, xView.getDisplayFlags(), data.getGlobalDisplayFlags());
										DisplayFlags displayFlags = DisplayFlags.getDisplayFlagsFor(xView);
										dispatcher.dispatch(WPaloEvent.INIT, displayFlags);
										directLogin(user);
//										CubeViewEditor.hasBeenResized = true;
										dispatcher.dispatch(WPaloEvent.EDIT_VIEWBROWSER_VIEW, xView);
										if (data.getErrors().length > 0) {
											StringBuffer buf = new StringBuffer();
											for (String s: data.getErrors()) {
												buf.append(s + "\n");
											}
											MessageBox.alert(constants.errorsWhileProcessingOptions(), buf.toString(), null);
										}
									}
								}
								
								public void onFailure(Throwable arg0) {
									dispatcher.dispatch(WPaloEvent.APP_START);
								}
							});					
				}
			}
		});
		WPaloPropertyServiceProvider.getInstance().getBooleanProperty("isPaloSuite", false, new AsyncCallback<Boolean>() {
			public void onFailure(Throwable arg0) {
				((WPalo) Registry.get(WPalo.ID)).show(dlg);
				dlg.show();				
			}

			public void onSuccess(Boolean result) {
				if (!result) {
					((WPalo) Registry.get(WPalo.ID)).show(dlg);
					dlg.show();					
				}
			}
		});
		
	}
	
	public final void directLogin(XUser user) {
		this.user = user;
	}
	
	final void checkForLogout() {
		final IEditor editor = currentEditor;
		final CloseObserver observer = new CloseObserver() {
			public void finishedClosed() {
				Dispatcher.forwardEvent(WPaloEvent.LOGOUT);
			}
		};
		editor.beforeClose(new AsyncCallback<Boolean>() {
			public void onSuccess(Boolean result) {
				if (result) {
					editor.close(observer);						
					editorpanel.setHeading("");
					editorpanel.removeAll();
				}
			}
			
			public void onFailure(Throwable arg0) {
				arg0.printStackTrace();
			}
		});		
	}
	
	final void logout() {
		closeOnLogout(currentEditor);
		if (viewpanel != null && viewpanel.isAttached() && viewpanel.isRendered()) {
			viewpanel.hide();
			Widget w = viewpanel.getParent();
			if (w != null && w.isVisible()) {
				w.setVisible(false);
			}
			WPaloControllerServiceProvider.getInstance().logout(user.getSessionId(),
					new Callback<Void>() {
						public void onSuccess(Void arg0) {
						}
					});
//			if (viewpanel.getLayoutTarget() != null) {
//				El e = viewpanel.getLayoutTarget();
//				while (e != null && e.isVisible()) {
//					e.setVisible(false);
//					e = e.getParent();
//				}				
//				viewpanel.getLayoutTarget().setVisible(false);				
//				viewpanel.getLayoutTarget().removeFromParent();				
//			}
//			viewpanel.removeFromParent();			
		}
		user = null;
	}
	private final void closeOnLogout(IEditor editor) {
		CloseObserver observer = new CloseObserver() {
			public void finishedClosed() {
				Dispatcher.forwardEvent(WPaloEvent.APP_STOP);
			}
		};
		close(editor, observer);
	}
	
	private final void openViewMode() {
		Dispatcher.forwardEvent(WPaloEvent.INIT_VIEW_MODE, user);		
	}
	
	/** for testing purpose only */
	private final void showViewEditor() {
//		//close current editor:
//		close(null);
//		CubeViewEditor editor = new CubeViewEditor();
//		editor.setInput(new TreeNode());
//		open(editor);
	}	
}
