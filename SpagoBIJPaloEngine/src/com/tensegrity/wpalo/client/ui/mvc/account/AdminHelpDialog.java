/*
*
* @file AdminHelpDialog.java
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
* @version $Id: AdminHelpDialog.java,v 1.4 2010/02/12 13:49:50 PhilippBouillon Exp $
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
