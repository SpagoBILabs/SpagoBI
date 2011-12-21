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
package com.tensegrity.wpalo.client.ui.mvc.viewbrowser.importer;

import java.util.List;

import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.ui.model.XObjectModel;

/**
 * <code>AccountComboBox</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AccountComboBox.java,v 1.3 2010/02/12 13:49:50 PhilippBouillon Exp $
 **/
public class AccountComboBox {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages  messages  = Resources.getInstance().getMessages();
	
//	private static final XAccount NO_ACCOUNT = 
//		new XAccount("NO_ACCOUNT","Please choose account");

//	private XAccount initialAccount;
	private final ComboBox<XObjectModel> accounts= new ComboBox<XObjectModel>();
	
	
	public AccountComboBox(String title, String inTextTitle) {
		initComponent(title, inTextTitle);
		initEventHandling();
	}
	
	public final ComboBox<XObjectModel> getComboBox() {
		return accounts;
	}
	
	public final XAccount getSelection() {
		return getFirstSelection();
	}
	
	public final void setInput(XAccount[] xAccounts) {
		accounts.disableEvents(true);
		ListStore<XObjectModel> accountsStore = accounts.getStore();
		for (XAccount xAccount : xAccounts) {
			XObjectModel accModel = new XObjectModel(xAccount);
			accModel.set("name", xAccount.getName() + constants.on() + xAccount.getConnection().getName());
			accountsStore.add(accModel);
		}
		accounts.disableEvents(false);
	}
	
	public final void addSelectionChangedListener(
			SelectionChangedListener<XObjectModel> listener) {
		accounts.addSelectionChangedListener(listener);
	}

	//	public final void reset() {
////		selectActiveAccount(initialAccount);
//	}
	
	private final void initComponent(String title, String inTextTitle) {
		accounts.setFieldLabel(title);
		accounts.setDisplayField("name");
		accounts.setEditable(false);
		accounts.setEmptyText(messages.choose(inTextTitle));
//				"Please choose " + title.toLowerCase());
		accounts.setAutoWidth(true);

		//data:
		ListStore<XObjectModel> store = createAccountStore();
		accounts.setStore(store);
	}	
	private final void initEventHandling() {
//		accounts.addSelectionChangedListener(new SelectionChangedListener<XObjectModel>() {
//			public void selectionChanged(SelectionChangedEvent<XObjectModel> se) {
//			}
//		});
	}
	
	private final ListStore<XObjectModel> createAccountStore() {
		ListStore<XObjectModel> store = new ListStore<XObjectModel>();
//		store.add(new XObjectModel(NO_ACCOUNT));
		return store;
	}
	
//	private final void selectActiveAccount(XAccount account) {		
//		if(account == null)
//			account = NO_ACCOUNT;
//		List<XObjectModel> selection = new ArrayList<XObjectModel>();
//		ListStore<XObjectModel> store = accounts.getStore();
//		for(int i=0, n=store.getCount(); i<n; i++) {
//			XObjectModel model = store.getAt(i);
//			if(accounts.equals(model.getXObject())) {
//				selection.add(model);
//				accounts.setSelection(selection);
//				break;
//			}
//		}
//	}

	private final XAccount getFirstSelection() {
		List<XObjectModel> selection = accounts.getSelection();
		if(selection.size() > 0)
			return (XAccount) selection.get(0).getXObject();
		return null;
	}
}
