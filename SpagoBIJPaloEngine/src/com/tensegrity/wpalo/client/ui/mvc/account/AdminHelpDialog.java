/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.ui.mvc.account;

import java.util.Date;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.google.gwt.user.client.Cookies;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.Resources;

public class AdminHelpDialog extends Dialog {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();

	public static final String SHOW_ADMIN_TIPS_COOKIE = "showAdminTipsAtStartup";
	
	private CheckBox doNotShowAgain;
	private LabelField messageLabel;
	private final String title;
	private final String cookieTitle;
	private final String user;
	private final String message;
	
	public AdminHelpDialog(String message, final String user) {
		this.title = constants.info();
		this.cookieTitle = "" + message.hashCode();
		this.user = user;
		this.message = message;
	}

	public boolean showDialog() {
		if (checkShowTipCookie(title, user)) {
			doShowDialog(title, message, user);
			return true;
		}		
		return false;
	}
	
	protected void onButtonPressed(Button button) {
    	close();
	}
	 
	private final boolean checkShowTipCookie(String title, String user) {
		String cookieData = Cookies.getCookie(SHOW_ADMIN_TIPS_COOKIE + cookieTitle + user);
		Date date = new Date(System.currentTimeMillis() + 1000l * 60l * 60l * 24l * 30l);			
		if (cookieData == null) {
			cookieData = "true";									
		}
		// Update cookie data expiration date:
		Cookies.setCookie(SHOW_ADMIN_TIPS_COOKIE + cookieTitle + user, cookieData, date);
				
		String value = cookieData;
		if (value == null) {
			value = "true";
		}
		boolean doShow = true;
		try {
			doShow = Boolean.parseBoolean(value);
		} catch (Throwable t) {
			doShow = true;
		}
		
		if (doShow) {
			return true;			
		} else {
			cookieData = "" + doShow;
			Cookies.setCookie(SHOW_ADMIN_TIPS_COOKIE + cookieTitle + user, cookieData, date);
		}		
		return false;
	}
	
	private final void doShowDialog(final String title, String msg, final String user) {
		setData("messageBox", true);
	    setHeading(title);
	    setResizable(false);
	    setConstrain(true);
	    setMinimizable(false);
	    setMaximizable(false);
	    setMinWidth(100);
	    setClosable(false);
	    setModal(false);
	    setButtonAlign(HorizontalAlignment.CENTER);
	    setMinHeight(80);
	    setPlain(true);
	    setFooter(true);
	    setButtons(MessageBox.OK);
	    setHideOnButtonClick(false);
	    setCloseAction(CloseAction.CLOSE);
	    addListener(Events.Close, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				boolean showTips = true;
				if (doNotShowAgain != null) {
					showTips = !doNotShowAgain.getValue();
				}
				String cookieData = "" + showTips;
				Date date = new Date(System.currentTimeMillis() + 1000l * 60l * 60l * 24l * 30l);
				Cookies.setCookie(SHOW_ADMIN_TIPS_COOKIE + cookieTitle + user, cookieData, date);				
			}	    
		});

	    doNotShowAgain = new CheckBox();
	    doNotShowAgain.setBoxLabel(constants.doNotShowThisMessageAgain());	    			    	  
		
	    String message = msg;
				
	    messageLabel = new LabelField(message);
	    messageLabel.setStyleName("margin10");		
		add(messageLabel);		
		
		doNotShowAgain.setStyleName("margin10");
		add(doNotShowAgain);
		show();		
	}		
}
