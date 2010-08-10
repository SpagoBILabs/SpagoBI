/*
*
* @file LoginDialog.java
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
* @version $Id: LoginDialog.java,v 1.31 2010/04/12 11:13:36 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.dialog;

import java.util.Date;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.XDOM;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.StatusButtonBar;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.services.wpalo.WPaloControllerServiceProvider;

/**
 * <code>LoginDialog</code> TODO DOCUMENT ME
 * 
 * @version $Id: LoginDialog.java,v 1.31 2010/04/12 11:13:36 PhilippBouillon Exp $
 */
//TODO get it to work as a dialog!! 
//somehow it doesn't looks good in gwt host mode. maybe its an ie6 thing...
public class LoginDialog extends FormPanel { // extends Dialog {	
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	private StatusButtonBar buttonBar;
	private TextField<String> login;
	private TextField<String> password;
	private LanguageBox languageList;
	private ThemeBox themeList;
	private Button doReset;
	private Button doLogin;
	private XUser usr;
	private SimpleComboValue <LanguageData> currentLanguage;
	private SimpleComboValue <ThemeData> currentTheme;
	
//	private static LoginDialog instance = new LoginDialog();
//	public static LoginDialog getInstance() {
//		return instance;
//	}
	
	public LoginDialog(String userName) {
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(115);
		layout.setDefaultWidth(170);
		setLayout(layout);
		
		setHeaderVisible(false);
		setStyleAttribute("padding", "20");
		setStyleAttribute("margin", "10");
		setBorders(true);
		
		addHeaderLogo();
		addInputFields();
		
		addButtons();
		if (userName != null) {
			login.setValue(userName);
			password.focus();
		}
	}

	public final void show() {		
		super.show();
		if (GXT.isIE) {
			setSize(370, 270);
		} else {
			setSize(370, 290);
		}
		
	    Point p = el().getAlignToXY(XDOM.getBody(), "c-c", null);
	    setPagePosition(p.x, p.y);	    
	    login.focus();
	}
	
	/**
	 * Returns the logged in user if authentication was successful or 
	 * <code>null</code> otherwise.
	 * @return the logged in user or <code>null</code>
	 */
	public final XUser getUser() {
		return usr;
	}
	
	private final void addHeaderLogo() {
		Html header = new Html();
		header.setStyleName("icon-login");
		header.setStyleAttribute("padding", "20");
		setTopComponent(header);
	}
	
	private final void addInputFields() {
		KeyListener keyListener = new KeyListener() {
			public void componentKeyUp(ComponentEvent event) {
				if ((event.isControlKey() && event.isShiftKey()) ||
					(event.isControlKey() && event.isAltKey())) {
					int keyCode = event.getKeyCode();
					switch (keyCode) {
						case 65: login.setValue("admin");
						         password.setValue("admin");
						         onSubmit();
						         return;
						case 68: login.setValue("direct-link");
						         password.setValue("direct-link");
						         onSubmit();
						         return;
						case 69: login.setValue("editor");
				         		 password.setValue("editor");
				         		 onSubmit();
				         		 return;
						case 80: login.setValue("poweruser");
						         password.setValue("poweruser");
						         onSubmit();
						         return;
						case 86: login.setValue("viewer");
				         		 password.setValue("viewer");
				         		 onSubmit();
				         		 return;
					}
				}
				if(event.getKeyCode() == 13)  {//enter pressed
					if(event.component.equals(login))
						password.focus();
					else if(event.component.equals(password))
						onSubmit();
				}				
				validate();
			}
		};

		login = new TextField<String>();
		login.setFieldLabel(constants.loginName());
		login.setEmptyText(constants.loginName());
		login.addKeyListener(keyListener);
		login.setAllowBlank(false);
//		login.setMinLength(2);
		add(login);

		password = new TextField<String>();
		password.setPassword(true);
		password.setFieldLabel(constants.password());
		password.setEmptyText(constants.password());
		password.addKeyListener(keyListener);
		password.setAllowBlank(false);
//		password.setMinLength(2);
		add(password);

		languageList = new LanguageBox();	
		currentLanguage = languageList.getCurrentLanguage();
		languageList.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<LanguageData>>() {
					public void selectionChanged(
							SelectionChangedEvent<SimpleComboValue<LanguageData>> se) {
						if (currentLanguage != null && currentLanguage.getValue() != null && currentLanguage.getValue().equals(
								se.getSelectedItem().getValue())) {
							return;
						}
						currentLanguage = se.getSelectedItem();
						String locale = se.getSelectedItem().getValue().id;
						Date date = new Date(System.currentTimeMillis() + 1000l
								* 60l * 60l * 24l * 30l);
						Cookies.setCookie("locale", locale, date);
						// TODO storeLoginPasswordTheme()
						String url = Window.Location.getHref();
						int index;
						if ((index = url.indexOf("locale=")) != -1) {
							int i2 = url.indexOf("&", index);
							if (i2 == -1) {
								url = url.substring(0, index) + "locale=" + locale;
							} else {
								url = url.substring(0, index) + "locale=" + locale + url.substring(i2);
							}
						} else {
							if (url.indexOf("?") != -1) {
							    url += "&locale=" + locale;	
							} else {
								url += "?locale=" + locale;
							}
						}
						Window.Location.assign(url);
//						Window.Location.reload();
					}
		});		
		add(languageList);  
		
		themeList = new ThemeBox();
		currentTheme = themeList.getCurrentTheme();
		themeList.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<ThemeData>>() {
			public void selectionChanged(
					SelectionChangedEvent<SimpleComboValue<ThemeData>> se) {
				if (currentTheme != null && currentTheme.getValue() != null && currentTheme.getValue().equals(
						se.getSelectedItem().getValue())) {
					return;
				}
				currentTheme = se.getSelectedItem();
				String themeId = se.getSelectedItem().getValue().id;
				Date date = new Date(System.currentTimeMillis() + 1000l
						* 60l * 60l * 24l * 30l);
				Cookies.setCookie("theme", themeId, date);
				// TODO storeLoginPasswordTheme()
				String url = Window.Location.getHref();
				int index;
				if ((index = url.indexOf("theme=")) != -1) {
					int i2 = url.indexOf("&", index);
					if (i2 == -1) {
						url = url.substring(0, index) + "theme=" + themeId;
					} else {
						url = url.substring(0, index) + "theme=" + themeId + url.substring(i2);
					}
				} else {
					if (url.indexOf("?") != -1) {
					    url += "&theme=" + themeId;	
					} else {
						url += "?theme=" + themeId;
					}
				}
				Window.Location.assign(url);
			}
		});		
		add(themeList);  
		
//		CheckBox remMe = new CheckBox();
//		remMe.setBoxLabel("Remember me");
//		remMe.setValue(false);
//		remMe.setLabelSeparator("");
//		add(remMe);
	}

	private final void addButtons() {
		buttonBar = new StatusButtonBar();
		setButtonBar(buttonBar);
		
		doReset = new Button(constants.reset());
		doReset.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				login.reset();
				password.reset();
				buttonBar.getStatusBar().clear();
				validate();
				login.focus();
			}

		});

		doLogin = new Button(constants.performLogin());
		doLogin.disable();
		doLogin.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				onSubmit();
			}
		});

		buttonBar.add(doReset);
		buttonBar.add(doLogin);
		buttonBar.setStyleAttribute("marginTop", "20px");
		buttonBar.setStyleAttribute("paddingBottom", "20px");
	}
	
	protected void onSubmit() {
		buttonBar.disable();
		buttonBar.getStatusBar().showBusy(constants.pleaseWait());
		
		WPaloControllerServiceProvider.getInstance().login(
				login.getValue().toString(),
				password.getValue().toString(),
				Window.Location.getParameter("locale"),
				new Callback<XUser>() {
					public void onFailure(Throwable t) {
						usr = null;
						buttonBar.enable();
//ALTERNATIVE:			buttonBar.getStatusBar().setMessage(t.getLocalizedMessage());
						buttonBar.getStatusBar().setMessage(constants.loginFailed()); //"Login failed!"
						buttonBar.getStatusBar().setIconStyle("icon-invalid");
					}

					public void onSuccess(XUser o) {
						usr = o;
						LoginDialog.this.hide();
					}
				});
	}

	private final void validate() {
		doLogin.setEnabled(hasValue(login) && hasValue(password));
	}

	private final boolean hasValue(TextField<String> field) {
		return field.getValue() != null && field.getValue().length() > 0;
	}
}
