/*
*
* @file AliasComboBox.java
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
* @version $Id: AliasComboBox.java,v 1.10 2010/02/12 13:49:50 PhilippBouillon Exp $
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
