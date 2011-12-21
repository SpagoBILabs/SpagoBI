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

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.gen2.logging.shared.Log;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.tensegrity.palo.gwt.core.client.exceptions.PaloGwtCoreException;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XLoadInfo;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XViewModel;
import com.tensegrity.wpalo.client.DisplayFlags;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.services.cubeview.WPaloCubeViewServiceProvider;
import com.tensegrity.wpalo.client.ui.mvc.cubeview.ConfirmLoadDialogListener;
import com.tensegrity.wpalo.client.ui.mvc.cubeview.CubeViewEditor;
import com.tensegrity.wpalo.client.ui.mvc.cubeview.LargeQueryWarningDialog;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;
import com.tensegrity.wpalo.client.ui.widgets.EditorTab;

/**
 * <code>ViewEditorTab</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ViewEditorTab.java,v 1.50 2010/04/15 09:55:22 PhilippBouillon Exp $
 **/
public class ViewEditorTab extends EditorTab {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages  messages  = Resources.getInstance().getMessages();

//	private TreeNode input;
	private final CubeViewEditor vEditor;
	private XView inputView;
	
	public ViewEditorTab(String name, final boolean showSaveButtons, final DisplayFlags displayFlags) {
		super(modify(name));
		//		setBorders(false);
//		setLayout(new FitLayout());
		setLayoutOnChange(true);
		setLayout(new RowLayout());
		setText(modify(name));
		setAutoHeight(true);
		setAutoWidth(true);
		setDeferHeight(true);
		
		vEditor = new CubeViewEditor(this);
		vEditor.setWidth("100%");
		vEditor.setHeight("100%");
		add(vEditor);
		vEditor.initialize(showSaveButtons, displayFlags);
	}	
		
	protected static String modify(String x) {
		x = x.replaceAll("&", "&amp;");
		x = x.replaceAll("\"", "&quot;");
		x = x.replaceAll("'", "&apos;");
		x = x.replaceAll("<", "&lt;");
		x = x.replaceAll(">", "&gt;");
		return x;
	}

//	protected String demodify(String x) {
//		x = x.replaceAll("&amp;", "&");
//		x = x.replaceAll("&apos;", "'");
//		x = x.replaceAll("&lt;", "<");
//		x = x.replaceAll("&gt;", ">");
//		x = x.replaceAll("&quot;", "\"");
//		return x;
//	}
	
	public CubeViewEditor getEditor() {
		return vEditor;
	}
	public void close() {
		if (vEditor.isDirty()) {
			final XViewModel view = vEditor.getView();
			MessageBox.confirm(constants.saveView(), messages.saveViewBeforeClosing(ViewBrowserModel.modify(view.getName())), 
					new Listener<WindowEvent>() {
						public void handleEvent(WindowEvent be) {
							if (!be.buttonClicked.getItemId().equalsIgnoreCase(
									Dialog.YES))
								saveAndClose(view);
							else
								ViewEditorTab.super.close();
						}
					});
		} else {
			super.close();
		}
	}

	private final void saveAndClose(XViewModel view) {
		save(view);
		super.close();
	}

	
	public boolean save(XObject input) {
		vEditor.save(new Callback<XViewModel>(constants.savingViewFailed()) {
			public void onSuccess(XViewModel v) {
				hideWaitCursor();
				if (v == null) {
					MessageBox.alert(constants.notEnoughRights(),
							constants.notEnoughRightsToSaveView(), null);
				}
//				MessageBox.info("Info", "Saving view successful!!", null);
			}
		});
		return true;
	}

	public void saveAs(String name, XObject input, final boolean isPublic, final boolean isEditable, final Callback <Boolean> callback) {
		if (input instanceof XViewModel) {
			final XViewModel xViewModel = (XViewModel) input;
			((Workbench)Registry.get(Workbench.ID)).showWaitCursor(constants.savingView());
			String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
			WPaloCubeViewServiceProvider.getInstance().saveViewAs(sessionId, name,
					xViewModel,
					new Callback<XView>(constants.savingViewFailed()) {
						public void onFailure(Throwable cause) {
							if (callback != null) {
								callback.onFailure(cause);
							}
							super.onFailure(cause);
						}
						
						public void onSuccess(XView xView) {
							hideWaitCursor();
/*							if (xView == null) {
								MessageBox.alert(constants.notEnoughRights(),
										constants.notEnoughRightsToSaveView(), null);
							} else {*/
/*								ViewBrowser viewBrowser = 
									(ViewBrowser) Registry.get(ViewBrowser.ID);							
								viewBrowser.addView(inputView, xView, true, isPublic, isEditable);*/
							if (callback != null) {
								callback.onSuccess(false);									
								MessageBox.info(constants.subobjectSaved(),
										constants.subobjectSavedMsg(), null);
							}
							//}
//							ViewEditorTab.super.close();
						}
					});
		}
	}
	
	public void set(XObject input) {
		if (input instanceof XView) {			
			XView xView = (XView) input;
			inputView = xView;
			((Workbench)Registry.get(Workbench.ID)).showWaitCursor(constants.loadingView());
			load(xView);
		}
	}
	
	private final void load(final XView xView) {
		// do server call:
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().willOpen(sessionId, xView, 
				new Callback<XLoadInfo>(constants.openingViewFailed()) {
					public void onFailure(Throwable t) {
//						t.printStackTrace();
						if (handled(t) || handled(t.getCause())) {
							hideWaitCursor();
							return;
						}
						if (t instanceof PaloGwtCoreException
								&& t.getMessage() != null && t.getMessage().equals("Session expired!")) {
							hideWaitCursor();
							Listener<WindowEvent> callback = new Listener<WindowEvent>() {
								public void handleEvent(WindowEvent we) {
									// Button clicked = we.buttonClicked;
									// if(clicked.getText().equalsIgnoreCase(Dialog.YES))
									Dispatcher
											.forwardEvent(WPaloEvent.APP_STOP);
								}
							};
							String locMessage = t.getLocalizedMessage();
							if (locMessage != null && locMessage.toLowerCase().indexOf("session expired") != -1) {
								locMessage = constants.sessionExpired();
							}
							MessageBox.info(constants.sessionExpired(), locMessage
									+ "<br/>" + constants.loginAgain(), callback);
						} else {
							handleFailure(t.getLocalizedMessage(), xView);
						}
					}
					public void onSuccess(XLoadInfo loadInfo) {
						ConfirmLoadDialogListener dlgListener = new ConfirmLoadDialogListener() {
							public void cancel() {
								hideWaitCursor();
								close();
							}
							public void proceed(boolean state) {
								try {
									proceedOpenView(xView);
								} catch (Throwable t) {
									t.printStackTrace();
								}
							}
						};
						LargeQueryWarningDialog.confirm(loadInfo, dlgListener);
					}
		});
	}
	
	private final void displayWarningDialog(XViewModel model, String [] warnings) {
		StringBuffer message = new StringBuffer(messages.warningsWhenOpeningView(ViewBrowserModel.modify(model.getName())));
		for (String w: warnings) {
			message.append(w);
			message.append("<br/>");
		}
		MessageBox.alert(constants.warningsWhenOpeningView(), 
				message.toString(), new Listener<WindowEvent>(){
					public void handleEvent(WindowEvent be) {
						getEditor().markDirty(true);
					}
				});
	}
		
	private final void proceedOpenView(final XView xView) {
		final String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().proceedOpen(sessionId, xView,  
				new Callback<XViewModel>(messages.loadingViewFailed(ViewBrowserModel.modify(xView.getName()))) {
				public final void onFailure(Throwable t) {
//					t.printStackTrace();
					handleFailure(t.getMessage(), xView);
				}
					public void onSuccess(final XViewModel model) {
						if (model == null)
							return;
						DeferredCommand.addCommand(new Command(){
							public void execute() {
								vEditor.setInput(model);
								vEditor.markDirty(false);
								setText(modify(model.getName()));
								//BUG IN GXT??? WE HAVE TO MANUALLY RESIZE TAB FOLDER ON INITIAL OPEN...
								final ViewEditorTabFolder tabFolder = 
									(ViewEditorTabFolder)getParent();
//								int _height = tabFolder.getHeight();
//								tabFolder.setHeight(_height);
								DeferredCommand.addCommand(new Command(){
									public void execute() {
//										tabFolder.layout();
										hideWaitCursor();
										if (model.getWarnings().length != 0) {
											displayWarningDialog(model, model.getWarnings());
											getEditor().markDirty(true);
										}
										Dispatcher.get().dispatch(WPaloEvent.VIEW_LOADED);
									}
								});
							}
						});
					}
				});
	}
	private final void hideWaitCursor() {
		((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
	}
	private final void handleFailure(String reason, final XView xView) {
		hideWaitCursor();
		Listener<WindowEvent> callback = new Listener<WindowEvent>() {
			public void handleEvent(WindowEvent we) {
//				vEditor.markDirty(false);
				close();
//				Button clicked = we.buttonClicked;
//				if (clicked.getText().equalsIgnoreCase(Dialog.YES)) {
//					// remove folder element:
//					Dispatcher.forwardEvent(
//							WPaloEvent.WILL_DELETE_VIEWBROWSER_VIEW, xView);
//				}
			}
		};
		MessageBox.alert(constants.error(),
				messages.failedToLoadView(ViewBrowserModel.modify(xView.getName()), reason), callback);
	}

	public void saveAs(String name, XObject input) {
		saveAs(name, input, true, true, null);
	}
	
	public void onRender(Element parent, int index) {
		super.onRender(parent, index);
//		if (headerHidden) {
//			getHeader().el().getParent().removeFromParent();
//		}
	}

}
