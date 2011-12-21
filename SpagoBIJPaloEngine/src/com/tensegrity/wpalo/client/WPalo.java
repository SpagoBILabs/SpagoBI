/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client;

import java.util.Date;
import java.util.List;


import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.Theme;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.MarginData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XDirectLinkData;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.exceptions.WPaloUncaughtExceptionHandler;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.services.folder.WPaloFolderServiceProvider;
import com.tensegrity.wpalo.client.services.wpalo.WPaloController;
import com.tensegrity.wpalo.client.services.wpalo.WPaloControllerServiceProvider;
import com.tensegrity.wpalo.client.ui.mvc.account.AccountController;
import com.tensegrity.wpalo.client.ui.mvc.admin.AdminController;
import com.tensegrity.wpalo.client.ui.mvc.cubeview.LargeQueryWarningDialog;
import com.tensegrity.wpalo.client.ui.mvc.cubeview.PrintDialog;
import com.tensegrity.wpalo.client.ui.mvc.viewbrowser.ViewBrowserController;
import com.tensegrity.wpalo.client.ui.mvc.viewbrowser.importer.PaloSuiteViewCreationDialog;
import com.tensegrity.wpalo.client.ui.mvc.viewbrowser.importer.ViewImportDialog;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;
import com.tensegrity.wpalo.client.ui.mvc.workbench.WorkbenchController;
import com.tensegrity.wpalo.client.ui.widgets.BusyIndicatorPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WPalo implements EntryPoint {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages  messages  = Resources.getInstance().getMessages();

	public static final String ID = "WPalo";
	
	public static WPaloConstants i18n;
	private BusyIndicatorPanel waitPanel = null;
	
	// the main panel for the content
	private Viewport contentPane = null;

	public static native void refreshSubobjects(String id) /*-{
	  $wnd.alert(id);
	  $wnd.msgToSend = 'Sub Object Saved!!';
	  $wnd.sendMessage({'id': id, 'msg': msgToSend},'subobjectsaved');
	}-*/;
	
	public void show(Widget panel) {		
		contentPane.setVisible(false);
		contentPane.removeAll();
		contentPane.add(panel, new MarginData(0));
		contentPane.setVisible(true);
		contentPane.layout();
	}
	
	public void attach(Widget panel) {
		contentPane.setVisible(false);
		contentPane.removeAll();
		contentPane.add(panel, new MarginData(0));
		contentPane.layout();
	}
	
	public static void loadCss(String filename) {
		Element link = DOM.createElement("link");
		DOM.setElementAttribute(link, "rel", "stylesheet");
		DOM.setElementAttribute(link, "type", "text/css");
		DOM.setElementAttribute(link, "href", filename);
		Element headElement = DOM.getElementById("head");
		DOM.appendChild(headElement, link);
	}
	
	public void show() {
		contentPane.setVisible(true);
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
	
	private final void createView(XView xView, final String viewId, final List <Boolean> displayFlags, final List <Boolean> globalFlags) {
		((Workbench) Registry.get(Workbench.ID)).showWaitCursor(constants.creatingView());
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloFolderServiceProvider.getInstance().importView(sessionId,
				xView,
					new Callback<XView>(constants.couldNotCreateView()) {
						public void onSuccess(XView xView) {
							hideWaitCursor();
							((Workbench) Registry.get(Workbench.ID)).showWaitCursor(constants.openingView());
							XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
							DisplayFlags.setDisplayFlagsFor(xView, user, displayFlags, globalFlags);
							xView.setExternalId(viewId);
							Dispatcher.get().dispatch(WPaloEvent.EDIT_VIEWBROWSER_VIEW, xView);
							String subobjId = "OK - 4";
							refreshSubobjects(subobjId);
						}
					});
	}		
	
	private final void parsePaloSuiteLinkData(final Dispatcher dispatcher, final ViewBrowserController viewBrowserController) {

		final String directLink = Window.Location.getQueryString();
		final String locale = Window.Location.getParameter("locale");
		WPaloServiceProvider.getInstance().openPaloSuiteView(locale, directLink,
				new AsyncCallback<XDirectLinkData>() {
					public void onFailure(Throwable t) {
						MessageBox.alert(constants.error(), t.getMessage(), null);
					}

					public void onSuccess(final XDirectLinkData data) {
						if (data.getErrors().length > 0) {
							StringBuffer buf = new StringBuffer();
							for (String s: data.getErrors()) {
								buf.append(s + "\n");
							}
							MessageBox.alert(constants.errorsWhileProcessingOptions(), buf.toString(), null);
						} else {
//							CubeViewEditor.hasBeenResized = true;
							WPaloControllerServiceProvider.getInstance().loginHash(
									Window.Location.getParameter("user"),
									data.getUserPassword(), 
									Window.Location.getParameter("locale"),
									new Callback<XUser>() {
										public void onFailure(Throwable t) {
											super.onFailure(t);
										}
										public void onSuccess(XUser user) {
											XView [] xViews = data.getViews();	

											if (xViews == null || xViews.length == 0) {
												try {
												DisplayFlags df = DisplayFlags.createDisplayFlags(user, data.getGlobalDisplayFlags());												
												dispatcher.dispatch(WPaloEvent.INIT, df);
												((Workbench)Registry.get(Workbench.ID)).directLogin(user);
												((Workbench)Registry.get(Workbench.ID)).setPaloSuite(true);
												} catch (Throwable t) {
													t.printStackTrace();
												}
												try {
												final PaloSuiteViewCreationDialog dia =
													new PaloSuiteViewCreationDialog();
												dia.addButtonListener(ViewImportDialog.BUTTON_OK,
														new Listener<BaseEvent>() {
															public void handleEvent(BaseEvent be) {
																XView[] xViews = dia.getSelectedViews();
																if (xViews != null && xViews.length == 1) {
																	xViews[0].setExternalId(data.getPaloSuiteViewId());
																	createView(xViews[0], data.getPaloSuiteViewId(), data.getDisplayFlags(), data.getGlobalDisplayFlags());
																}
															}
														});
												dia.show();
												} catch (Throwable t) {
													t.printStackTrace();
												}
											} else {
												try {
													XView xView = xViews[0];
													
													DisplayFlags.setDisplayFlagsFor(xView, user, xView.getDisplayFlags(), data.getGlobalDisplayFlags());
													DisplayFlags displayFlags = DisplayFlags.getDisplayFlagsFor(xView);
													dispatcher.dispatch(WPaloEvent.INIT, displayFlags);
													((Workbench)Registry.get(Workbench.ID)).directLogin(user);
													((Workbench)Registry.get(Workbench.ID)).setPaloSuite(true);
													dispatcher.dispatch(WPaloEvent.EDIT_VIEWBROWSER_VIEW, xView);
													if (data.getErrors().length > 0) {
														StringBuffer buf = new StringBuffer();
														for (String s: data.getErrors()) {
															buf.append(s + "\n");
														}
														MessageBox.alert(constants.errorsWhileProcessingOptions(), buf.toString(), null);
													}	
												} catch (Throwable t) {
													t.printStackTrace();
												}
											}											
										}
									});

						}
					}
				});
	}
	
	private final void parseDefaultLinkData(final Dispatcher dispatcher, final ViewBrowserController viewBrowserController) {

		final String directLink = Window.Location.getParameter("options");
		final String locale = Window.Location.getParameter("locale");
		if (directLink == null) {
			// ...and dispatch start event, i.e. we begin with login ;)
			dispatcher.dispatch(WPaloEvent.APP_START);
		} else {
			final String user = getValue("user", directLink);
			final String pass = getValue("pass", directLink);
			if (user == null || pass == null) {
				dispatcher.dispatch(WPaloEvent.APP_START);
			} else {

				waitPanel = new BusyIndicatorPanel();
				waitPanel.show(constants.startingApplication(), false);
				WPaloServiceProvider.getInstance().openViewDirectly(locale, directLink,
					new AsyncCallback<XDirectLinkData>() {
						public void onSuccess(final XDirectLinkData data) {
							if (!data.isAuthenticated()) {
								waitPanel.hide();
								dispatcher.dispatch(WPaloEvent.APP_START);
								if (data.getErrors().length > 0) {
									StringBuffer buf = new StringBuffer();
									for (String s: data.getErrors()) {
										buf.append(s + "\n");
									}
									MessageBox.alert(constants.errorsWhileProcessingOptions(), buf.toString(), null);
								}								
							} else {

//								CubeViewEditor.hasBeenResized = true;
//								CubeViewEditor.fromDirectLink = true;
								WPaloControllerServiceProvider.getInstance().loginHash(
										user,
										pass, 
										Window.Location.getParameter("locale"),
										new Callback<XUser>() {
											public void onFailure(Throwable t) {
												super.onFailure(t);
												waitPanel.hide();
											}
											public void onSuccess(XUser user) {

												XView [] xViews = data.getViews();
												if (xViews == null || xViews.length == 0) {
													try {																		
														dispatcher.dispatch(WPaloEvent.INIT, DisplayFlags.createDisplayFlags(user, data.getGlobalDisplayFlags()));
														((Workbench)Registry.get(Workbench.ID)).directLogin(user);
													} catch (Throwable t) {
														t.printStackTrace();
														MessageBox.alert(t.getMessage(), t.getCause().getMessage(), null);
													}
													waitPanel.hide();
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
													viewBrowserController.addViewToLoad(xViews[i]);
												}
												XView xView = xViews[0];
												
												DisplayFlags.setDisplayFlagsFor(xView, user, xView.getDisplayFlags(), data.getGlobalDisplayFlags());
												DisplayFlags displayFlags = DisplayFlags.getDisplayFlagsFor(xView);

												dispatcher.dispatch(WPaloEvent.INIT, displayFlags);
												((Workbench)Registry.get(Workbench.ID)).directLogin(user);
//												CubeViewEditor.hasBeenResized = true;
												dispatcher.dispatch(WPaloEvent.EDIT_VIEWBROWSER_VIEW, xView);
												waitPanel.hide();
												if (data.getErrors().length > 0) {
													StringBuffer buf = new StringBuffer();
													for (String s: data.getErrors()) {
														buf.append(s + "\n");
													}
													MessageBox.alert(constants.errorsWhileProcessingOptions(), buf.toString(), null);
												}												
											}
										});
							}								
						}
						
						public void onFailure(Throwable arg0) {
							dispatcher.dispatch(WPaloEvent.APP_START);
						}
					});
			}
		}		
	}
	
	private final String setLocale() {
		String val = Window.Location.getParameter("locale");
		if (val == null) {
			val = Cookies.getCookie("locale");
			if (val != null) {
				String url = Window.Location.getHref();
				int index;
				if ((index = url.indexOf("locale=")) != -1) {
					int i2 = url.indexOf("&", index);
					if (i2 == -1) {
						url = url.substring(0, index) + "locale=" + val;
					} else {
						url = url.substring(0, index) + "locale=" + val + url.substring(i2);
					}
				} else {
					if (url.indexOf("?") != -1) {
					    url += "&locale=" + val;	
					} else {
						url += "?locale=" + val;
					}
				}
				//Window.Location.assign(url);
				return url;
			}
		}
		if (val == null || val.isEmpty()) {
			return null;
		}
		if (val.equals(Cookies.getCookie("locale"))) {
			return null;
		}
		Date date = new Date(System.currentTimeMillis() + 1000l
				* 60l * 60l * 24l * 30l);
		Cookies.setCookie("locale", val, date);
		return null;
	}
	
	private final String setTheme(String url) {
		String val = Window.Location.getParameter("theme");
		if (val == null) {
			val = Cookies.getCookie("theme");
			if (val != null) {
				if (url == null) {
					url = Window.Location.getHref();
				}
				int index;
				if ((index = url.indexOf("theme=")) != -1) {
					int i2 = url.indexOf("&", index);
					if (i2 == -1) {
						url = url.substring(0, index) + "theme=" + val;
					} else {
						url = url.substring(0, index) + "theme=" + val + url.substring(i2);
					}
				} else {
					if (url.indexOf("?") != -1) {
					    url += "&theme=" + val;	
					} else {
						url += "?theme=" + val;
					}
				}
				//Window.Location.assign(url);
				return url;
			}
		}
		if (val == null || val.isEmpty()) {
			return url;
		}
		if (val.equals(Cookies.getCookie("theme"))) {
			return url;
		}
		Date date = new Date(System.currentTimeMillis() + 1000l
				* 60l * 60l * 24l * 30l);
		Cookies.setCookie("theme", val, date);
		return url;
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		String theme = Window.Location.getParameter("theme");
		if (theme == null || theme.isEmpty() || theme.equalsIgnoreCase("blue") || theme.equalsIgnoreCase("default")) {
			GXT.setDefaultTheme(Theme.BLUE, true);
			loadCss("blue_theme.css");
		} else if (theme.equalsIgnoreCase("grey") || theme.equalsIgnoreCase("gray")) {
			GXT.setDefaultTheme(Theme.GRAY, true);
			loadCss("gray_theme.css");
		} else {
			GXT.setDefaultTheme(Theme.BLUE, true);
			loadCss("blue_theme.css");
		}
				
		Registry.register(ID, this);
		String url = setLocale();
		url = setTheme(url);
		if (url != null) {
			Window.Location.assign(url);
		}
		initialize();
		
		if (!GXT.isIE) {
			loadCss("firefox.css");
		}
		// simply register main controller...
		final Dispatcher dispatcher = Dispatcher.get();
		dispatcher.addController(new WPaloController());
		WorkbenchController wBenchController = new WorkbenchController();
		dispatcher.addController(wBenchController);
		// the left side content:		
		final ViewBrowserController viewBrowserController = new ViewBrowserController();
		wBenchController.setViewBrowserController(viewBrowserController);
		dispatcher.addController(viewBrowserController);
		// dispatcher.addController(new ReportController());
		// dispatcher.addController(new ReportStructureController());
		// dispatcher.addController(new ModellerController());
		dispatcher.addController(new AdminController());
		dispatcher.addController(new AccountController());
		// dispatcher.addController(new ViewModeWorkbenchController());
		// dispatcher.addController(new ViewModeController());

		PrintDialog.setDefaults();
		
		WPaloPropertyServiceProvider.getInstance().getBooleanProperty("isPaloSuite", false,
				new AsyncCallback<Boolean>() {
					public void onFailure(Throwable arg0) {
						parseDefaultLinkData(dispatcher, viewBrowserController);
					}

					public void onSuccess(Boolean result) {
						if (result) {							
							parsePaloSuiteLinkData(dispatcher, viewBrowserController);
						} else {
							parseDefaultLinkData(dispatcher, viewBrowserController);
						}
						
					}
				});
	}

	
	private final void initialize() {
		// Initialize i18n:
		i18n = GWT.create(WPaloConstants.class);
		
		GWT.setUncaughtExceptionHandler(new WPaloUncaughtExceptionHandler());
//		init();
		
		// required for palo cube table!!
		// PaloTableJSBridge.defineMethods();

		// main panel:
		if (contentPane == null) {
			contentPane = new Viewport();
//			{				
//				protected void onWindowResize(int width, int height) {		
//					if (waitPanel != null) {
//						waitPanel.hide();
//						waitPanel = null;			
//					}
//					super.onWindowResize(width, height);
//				}				
//			};
			contentPane.setMonitorWindowResize(true);
			contentPane.setSize("100%", "100%");
			RootPanel.get().add(contentPane);
		}		
	}
	
//	static void onResize() {
//		if (waitPanel == null) {
//			waitPanel = new BusyIndicatorPanel();
//			waitPanel.show("Resizing, please wait...");
//			System.out.println("Resizing, please wait...");
//		}
//	}
		
//    private static native void init() /*-{
//	    // Magic function defined by the selection script.
//	    __gwt_initHandlers(
//	      function() {
//	        @com.tensegrity.wpalo.client.WPalo::onResize()();
//	      },
//	      function() {
//	        return @com.google.gwt.user.client.Window::onClosing()();
//	      },
//	      function() {
//	        @com.google.gwt.user.client.Window::onClosed()();
//	      }
//	    );
//	  }-*/;	
}
