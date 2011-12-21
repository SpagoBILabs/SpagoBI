/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.ui.mvc.cubeview;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XLoadInfo;
import com.tensegrity.palo.gwt.widgets.client.util.UserAgent;
import com.tensegrity.wpalo.client.WPaloPropertyServiceProvider;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.services.cubeview.WPaloCubeViewServiceProvider;

public class LargeQueryWarningDialog extends Dialog {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages  messages  = Resources.getInstance().getMessages();

	private String message;
	private CheckBox doNotShowAgain;
	
	public static boolean showWarning = true;
	
	private static int newLoadedCellsWarningThreshold = 1000000;
	private static int displayCellsWarningThreshold = 1000000;
	private static int totalLoadedCellsWarningThreshold = 1000000;
	private static int totalTreeChildrenWarningThreshold = 200;
	private static boolean showWarnDialog = true;
	private static boolean showTreeWarning = true;
	public static boolean hideWarnDialog = false;
	
	static {
		readThresholds(new AsyncCallback <String []>() {
			public void onFailure(Throwable cause) {
			}

			public void onSuccess(String[] result) {
				if (result == null || result.length != 6) {
					return;
				}
				showWarnDialog = Boolean.parseBoolean(result[0]);
				showTreeWarning = Boolean.parseBoolean(result[1]);
				displayCellsWarningThreshold = Integer.parseInt(result[2]);
				newLoadedCellsWarningThreshold = Integer.parseInt(result[3]);
				totalLoadedCellsWarningThreshold = Integer.parseInt(result[4]);
				totalTreeChildrenWarningThreshold = Integer.parseInt(result[5]);
			}
		});
	}

	
	private LargeQueryWarningDialog(XLoadInfo loadInfo, final Listener<WindowEvent> callback) {		
		final String msg = messages.largeCellQueryWarning(loadInfo.loadCells, loadInfo.totalCells, loadInfo.visibleCells);
			
		WPaloPropertyServiceProvider.getInstance().getStringProperty(constants.warningMessageAllBrowsers(), new AsyncCallback <String>(){
			public void onFailure(Throwable arg0) {
				showDialog(true, msg, callback);
			}

			public void onSuccess(String result) {
				showDialog(true, msg + result, callback);
			}
		});
	}
	
	private LargeQueryWarningDialog(int treeChildren, final Listener<WindowEvent> callback) {		
		final String msg = messages.largeTreeQueryWarning(treeChildren); 
			
		WPaloPropertyServiceProvider.getInstance().getStringProperty(constants.warningTreeMessageAllBrowsers(), new AsyncCallback <String>(){
			public void onFailure(Throwable arg0) {
				showDialog(false, msg, callback);
			}

			public void onSuccess(String result) {
				showDialog(false, msg + result, callback);
			}
		});
	}

	private final void showDialog(final boolean isCells, String msg, final Listener <WindowEvent> callback) {
		boolean showDialog = false;
		if (UserAgent.getInstance().isIE && !UserAgent.getInstance().isIE8) {
			final String mmsg = msg;
			WPaloPropertyServiceProvider.getInstance().getStringProperty(
					constants.warningMessageNotIE8(), new AsyncCallback <String>(){
				public void onFailure(Throwable arg0) {
					doShowDialog(isCells, mmsg, callback);
				}

				public void onSuccess(String result) {
					doShowDialog(isCells, mmsg + result, callback);
				}
			});
		} else if (UserAgent.getInstance().isGecko) {
			final String mmsg = msg;
			WPaloPropertyServiceProvider.getInstance().getStringProperty(constants.warningMessageFF(), new AsyncCallback <String>(){
				public void onFailure(Throwable arg0) {
					doShowDialog(isCells, mmsg, callback);
				}

				public void onSuccess(String result) {
					doShowDialog(isCells, mmsg + result, callback);
				}
			});
		} else if (UserAgent.getInstance().isIE8) {
			final String mmsg = msg;
			WPaloPropertyServiceProvider.getInstance().getStringProperty(constants.warningMessageIE8(), new AsyncCallback <String>(){
				public void onFailure(Throwable arg0) {
					doShowDialog(isCells, mmsg, callback);
				}

				public void onSuccess(String result) {
					doShowDialog(isCells, mmsg + result, callback);
				}
			});			
		} else {
			msg += "</p>";
			showDialog = true;
		}

		if (showDialog) {
			doShowDialog(isCells, msg, callback);
		}		
	}
	
	private final void doShowDialog(final boolean isCells, String msg, Listener<WindowEvent> callback) {
		String link = constants.performanceLink();
			
		String additionalInfo = isCells ? "" : constants.confirmTreeDisplay(); 
		message = msg + link + additionalInfo;

		setData("messageBox", true);
	    setHeading(isCells ? constants.confirmCellLoading() : constants.confirmTreeLoading()); 
	    setResizable(false);
	    setConstrain(true);
	    setMinimizable(false);
	    setMaximizable(false);
	    setMinWidth(100);
	    setClosable(false);
	    setModal(true);
	    setButtonAlign(HorizontalAlignment.CENTER);
	    setMinHeight(80);
	    setPlain(true);
	    setFooter(true);
	    setButtons(isCells ? MessageBox.YESNO : MessageBox.YESNOCANCEL);
	    setHideOnButtonClick(true);
	    setCloseAction(CloseAction.CLOSE);
	    addListener(Events.Close, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				if (doNotShowAgain != null) {
					if (isCells) {
						showWarnDialog = !doNotShowAgain.getValue();
					} else {
						showTreeWarning = !doNotShowAgain.getValue();
					}
				}
			}	    
		});
	    if (callback != null) {
	        addListener(Events.Close, callback);
	    }
	    if (getButtons() != null) {
	        if (getButtons().contains(Dialog.YES)) {
	          setFocusWidget(getButtonBar().getButtonById(Dialog.YES));
	        } 
	    }
		
	    doNotShowAgain = new CheckBox();
	    doNotShowAgain.setBoxLabel(constants.doNotNeedThisInformation());	    			    	  
		ButtonBar bb = getButtonBar();
		for (int i = 0; i < bb.getItemCount(); i++) {
			if (bb.getItem(i).getItemId().equalsIgnoreCase(Dialog.YES) && isCells) {
				bb.getItem(i).setText(constants.continueOp());
			} else if (bb.getItem(i).getItemId().equalsIgnoreCase(Dialog.NO) && isCells) {
				bb.getItem(i).setText(constants.discardChanges());
			}
		}
		
		LabelField l = new LabelField(message);		
		l.setStyleName("margin10");
		add(l);		
		doNotShowAgain.setStyleName("margin10");
		add(doNotShowAgain);
		setHeight(370);
		setWidth(420);
		show();		
	}
	
	public static final void readThresholds(AsyncCallback <String []> cb) {
		String [] prefixes = getBrowserPrefixes();
		WPaloCubeViewServiceProvider.getInstance().getWarningThresholds("",
				prefixes, cb);
	}	

	private static final String [] getBrowserPrefixes() {
		StringBuffer buf = new StringBuffer("default.,");
		
		UserAgent ua = UserAgent.getInstance();
		if (ua.isOpera)   buf.append("opera.,"); 
		if (ua.isChrome)  buf.append("chrome.,");
		if (ua.isChrome4) buf.append("chrome4.,");
		if (ua.isChrome3) buf.append("chrome3.,");
		if (ua.isChrome2) buf.append("chrome2.,");
		if (ua.isChrome1) buf.append("chrome1.,");
		if (ua.isIron)    buf.append("iron.,");
		if (ua.isIron4)   buf.append("iron4.,");
		if (ua.isIron3)   buf.append("iron3.,");
		if (ua.isIron2)   buf.append("iron2.,");
		if (ua.isIron1)   buf.append("iron1.,");				
		if (ua.isSafari)  buf.append("safari.,");
		if (ua.isSafari4) buf.append("safari4.,");
		if (ua.isSafari3) buf.append("safari3.,");		     
		if (ua.isIE)      buf.append("ie.,");
		if (ua.isIE8)     buf.append("ie8.,");
		if (ua.isIE7)     buf.append("ie7.,");
		if (ua.isIE6)     buf.append("ie6.,");		
		if (ua.isGecko)   buf.append("firefox.,");
		if (ua.isGecko36) buf.append("firefox36.,");
		if (ua.isGecko35) buf.append("firefox35.,");
		if (ua.isGecko3)  buf.append("firefox3.,");
		if (ua.isGecko2)  buf.append("firefox2.,");
		
		return buf.toString().split(",");
	}

	public static final void confirm(XLoadInfo loadInfo, final ConfirmLoadDialogListener dlgListener) {
		if (hideWarnDialog) {
			dlgListener.proceed(true);
			return;
		}
		if(showWarnDialog && LargeQueryWarningDialog.showWarning) {
			if (loadInfo.loadCells> newLoadedCellsWarningThreshold
					|| loadInfo.visibleCells > displayCellsWarningThreshold
					|| loadInfo.totalCells > totalLoadedCellsWarningThreshold) {
				new LargeQueryWarningDialog(loadInfo, new Listener<WindowEvent>() {
					public void handleEvent(WindowEvent be) {
						if (be.buttonClicked.getItemId().equalsIgnoreCase(Dialog.YES))
							dlgListener.proceed(true);
						else
							dlgListener.cancel();
					}
				});
			} else
				dlgListener.proceed(true);
		} else
			dlgListener.proceed(true);
	}
	
	public static final void confirm(int treeChildren, final ConfirmLoadDialogListener dlgListener) {
		if (hideWarnDialog) {
			dlgListener.proceed(true);
			return;
		}
		if(showWarning && LargeQueryWarningDialog.showTreeWarning) {
			if (treeChildren > totalTreeChildrenWarningThreshold) {
				new LargeQueryWarningDialog(treeChildren, new Listener<WindowEvent>() {
					public void handleEvent(WindowEvent be) {
						if (be.buttonClicked.getItemId().equalsIgnoreCase(Dialog.YES))
							dlgListener.proceed(true);
						else if (be.buttonClicked.getItemId().equalsIgnoreCase(Dialog.NO)) 
							dlgListener.proceed(false);
						else 
							dlgListener.cancel();
					}
				});
			} else
				dlgListener.proceed(true);
		} else
			dlgListener.proceed(true);
	}
}
