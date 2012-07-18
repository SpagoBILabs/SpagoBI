/*
*
* @file ShowTipsAtStartupDialog.java
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
* @version $Id: ShowTipsAtStartupDialog.java,v 1.6 2010/02/12 13:49:50 PhilippBouillon Exp $
*
*/

package com.tensegrity.wpalo.client.ui.mvc.viewbrowser;

import java.util.Date;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tensegrity.wpalo.client.WPaloPropertyServiceProvider;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;

public class ShowTipsAtStartupDialog extends Dialog {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages  messages  = Resources.getInstance().getMessages();
	
	private CheckBox doNotShowAgain;
	private int tipNumber; 
	private LabelField headerLabel;
	private LabelField messageLabel;
	private Date date;
	private String user;
	
	private final void readFirstTip(final Date date, final String user, final boolean update) {
		WPaloPropertyServiceProvider.getInstance().getStringProperty(messages.tipMessage(1), new AsyncCallback <String>(){
			public void onFailure(Throwable t) {
			}

			public void onSuccess(String result) {
				if (result != null && result.length() != 0) {
					ShowTipsAtStartupDialog.this.tipNumber = 2;
					if (update) {
						updateTexts(result);
					} else {
						doShowDialog(result, date, user);
					}
				}
			}					
		});				
	}
	
	public ShowTipsAtStartupDialog(int tipNumber, final Date date, final String user) {
		this.tipNumber = tipNumber;
		this.date = date;
		this.user = user;
		WPaloPropertyServiceProvider.getInstance().getStringProperty(messages.tipMessage(tipNumber), new AsyncCallback <String>(){
			public void onFailure(Throwable arg0) {
				readFirstTip(date, user, false);
			}

			public void onSuccess(String result) {
				if (result == null || result.length() == 0) {
					readFirstTip(date, user, false);
				} else {
					ShowTipsAtStartupDialog.this.tipNumber++;
					doShowDialog(result, date, user);
				}
			}
		});
	}
	
	private final void navigateTips() {
    	WPaloPropertyServiceProvider.getInstance().getStringProperty(messages.tipMessage(tipNumber), 
    			new AsyncCallback <String>(){
			public void onFailure(Throwable arg0) {
				readFirstTip(date, user, true);
			}

			public void onSuccess(String result) {
				if (result == null || result.length() == 0) {
					readFirstTip(date, user, true);
				} else {
					ShowTipsAtStartupDialog.this.tipNumber++;						
					updateTexts(result);
				}
			}
		});	    			
	}
	
	protected void onButtonPressed(Button button) {
	    if (button.getItemId().equalsIgnoreCase(Dialog.NO)) {
	    	close();
	    } else {
			if (button.getItemId().equalsIgnoreCase(Dialog.YES)) {
				tipNumber -= 2;
				if (tipNumber < 1) {
					WPaloPropertyServiceProvider.getInstance().getIntProperty("lastTip", 1, new AsyncCallback<Integer>() {
						public void onFailure(Throwable arg0) {
							tipNumber = 1;
							navigateTips();
						}

						public void onSuccess(Integer result) {
							tipNumber = result;
							navigateTips();
						}
					});
				} else {
					navigateTips();
				}
			} else {
				navigateTips();
			}
	    }
	}
	  
	private final void doShowDialog(String msg, final Date date, final String user) {
		setData("messageBox", true);
	    setHeading(constants.didYouKnow());
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
	    setButtons(MessageBox.YESNOCANCEL);
	    setHideOnButtonClick(false);
	    setCloseAction(CloseAction.CLOSE);
	    addListener(Events.Close, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				boolean showTips = true;
				if (doNotShowAgain != null) {
					showTips = !doNotShowAgain.getValue();
				}
				String cookieData = showTips + "," + tipNumber;
				Cookies.setCookie(ViewBrowser.SHOW_TIPS_COOKIE + user, cookieData, date);				
			}	    
		});
	    if (getButtons() != null) {
	        if (getButtons().contains(Dialog.NO)) {
	          setFocusWidget(getButtonBar().getButtonById(Dialog.NO));
	        } 
	    }
		
		ButtonBar bb = getButtonBar();
		for (int i = 0; i < bb.getItemCount(); i++) {
			if (bb.getItem(i).getItemId().equalsIgnoreCase(Dialog.CANCEL)) {
				bb.getItem(i).setText(constants.showNextTip());
			} else if (bb.getItem(i).getItemId().equalsIgnoreCase(Dialog.YES)) {
				bb.getItem(i).setText(constants.showPreviousTip());
			} else if (bb.getItem(i).getItemId().equalsIgnoreCase(Dialog.NO)) {
				bb.getItem(i).setText(constants.ok());
			}			
		}

	    doNotShowAgain = new CheckBox();
	    doNotShowAgain.setBoxLabel(constants.doNotShowTipsAtStartup());	    	   
		
	    String [] msgs = msg.split("\\|");
	    String header = "<b>" + constants.didYouKnow() + "</b>";
	    String message = msg;
	    if (msgs != null && msgs.length == 2) {
	    	header = msgs[0];
	    	message = msgs[1];
	    }
		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlign(VerticalAlignment.MIDDLE);		
		hp.setStyleName("margin10");
		
	    LabelField l1 = new LabelField();
		l1.addStyleName("icon-lightbulb");
		hp.add(l1);

		headerLabel = new LabelField(header);
		hp.add(headerLabel);
		
	    messageLabel = new LabelField(message);
	    messageLabel.setStyleName("margin10");		
		add(hp);
		add(messageLabel);		
		
		doNotShowAgain.setStyleName("margin10");
		add(doNotShowAgain);
		setHeight(370);
		setWidth(420);		
		show();		
	}	
	
	private final void updateTexts(String result) {
	    String [] msgs = result.split("\\|");
	    String header = "<b>" + constants.didYouKnow() + "</b>";
	    String message = result;
	    if (msgs != null && msgs.length == 2) {
	    	header = msgs[0];
	    	message = msgs[1];
	    }
	    headerLabel.setText(header);
	    messageLabel.setText(message);
	}
}
