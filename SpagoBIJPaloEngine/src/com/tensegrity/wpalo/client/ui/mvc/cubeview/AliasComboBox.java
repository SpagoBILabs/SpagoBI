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
package com.tensegrity.wpalo.client.ui.mvc.cubeview;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAlias;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.ui.model.XObjectModel;

/**
 * <code>AliasComboBox</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AliasComboBox.java,v 1.10 2010/02/12 13:49:50 PhilippBouillon Exp $
 **/
public class AliasComboBox {
	protected static transient final ILocalConstants constants = Resources.getInstance().getConstants();
	private static final XAlias NO_ALIAS = new XAlias("NO_ALIAS", constants.noAlias()); 

	private XAlias initialAlias;
	private final ComboBox<XObjectModel> aliases= new EnhancedComboBox();
	
	
	public AliasComboBox() {
		initComponent();
	}
	
	public final ComboBox<XObjectModel> getComboBox() {
		return aliases;
	}
	
	public final XAlias getSelection() {
		XAlias selectedAlias = getFirstAliasSelection();
		if(selectedAlias.equals(NO_ALIAS))
			selectedAlias = null;
		return selectedAlias;
	}
		
	public final void setInput(XAxisHierarchy hierarchy) {
		aliases.disableEvents(true);
		ListStore<XObjectModel> aliasStore = aliases.getStore();
		for(XAlias alias : hierarchy.getAliases())
			aliasStore.add(new XObjectModel(alias));
		initialAlias = hierarchy.getActiveAlias();
		selectAlias(initialAlias);
		aliases.disableEvents(false);
	}
	
	public final void reset() {
		selectAlias(initialAlias);
	}
	
	private final void initComponent() {
		aliases.setFieldLabel(constants.alias());
		aliases.setDisplayField("name");
		aliases.setAutoWidth(true);
		aliases.setSelectOnFocus(true);
		
		//data:
		ListStore<XObjectModel> store = createAliasStore();
		aliases.setStore(store);
	}	
	
	private final ListStore<XObjectModel> createAliasStore() {
		ListStore<XObjectModel> store = new ListStore<XObjectModel>();
		store.add(new XObjectModel(NO_ALIAS));
		return store;
	}
	
	public final void selectAlias(XAlias alias) {		
		if(alias == null)
			alias = NO_ALIAS;
		List<XObjectModel> selection = new ArrayList<XObjectModel>();
		ListStore<XObjectModel> store = aliases.getStore();
		for(int i=0, n=store.getCount(); i<n; i++) {
			XObjectModel model = store.getAt(i);
			if(alias.equals(model.getXObject())) {
				selection.add(model);
				aliases.setSelection(selection);				
				break;
			}
		}
//		aliases.getListView().setStore(aliases.getStore());
//		aliases.getListView().getSelectionModel().select(0);
	}

	private final XAlias getFirstAliasSelection() {
		List<XObjectModel> selection = aliases.getSelection();
		if(selection.size() > 0)
			return (XAlias) selection.get(0).getXObject();
		return NO_ALIAS;
	}
}
