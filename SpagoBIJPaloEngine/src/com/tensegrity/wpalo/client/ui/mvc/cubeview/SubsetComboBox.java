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

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.subsets.XSubset;
import com.tensegrity.palo.gwt.core.client.models.subsets.XSubsetType;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.ui.model.XObjectModel;

/**
 * <code>SubsetComboBox</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: SubsetComboBox.java,v 1.12 2010/02/12 13:49:50 PhilippBouillon Exp $
 **/
public class SubsetComboBox {
	protected static transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	private static final XSubset NO_SUBSET = new XSubset("NO_SUBSET", constants.noSubset(), XSubsetType.GLOBAL);	

	private XSubset initialSubset;
	private final ComboBox<XObjectModel> subsets = new EnhancedComboBox();
	
	
	public SubsetComboBox() {
		subsets.getListView().getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		initComponent();
	}
	
	public final ComboBox<XObjectModel> getComboBox() {
		return subsets;
	}
	
	public final XSubset getSelection() {
		XSubset selectedSubset = getFirstSubsetSelection();
		if(selectedSubset.equals(NO_SUBSET))
			selectedSubset = null;
		return selectedSubset;
	}
	
	public final void setInput(XAxisHierarchy hierarchy) {
		subsets.disableEvents(true);		
		ListStore<XObjectModel> subsetStore = subsets.getStore();
		for(XSubset subset : hierarchy.getSubsets())
			subsetStore.add(new XObjectModel(subset));
		initialSubset = hierarchy.getActiveSubset();
		selectSubset(initialSubset);		
		subsets.disableEvents(false);
	}
	
	public final void reset() {
		selectSubset(initialSubset);
	}
	
	private final void initComponent() {
		subsets.setFieldLabel(constants.subset());
		subsets.setDisplayField("name");
		subsets.setAutoWidth(true);		
		subsets.setSelectOnFocus(true);
		
		//data:
		ListStore<XObjectModel> store = createSubsetStore();
		subsets.setStore(store);
	}	
	
	private final ListStore<XObjectModel> createSubsetStore() {
		ListStore<XObjectModel> subsetStore = new ListStore<XObjectModel>();
		subsetStore.add(new XObjectModel(NO_SUBSET));
		return subsetStore;
	}
	
	public final void selectSubset(XSubset subset) {		
		if(subset == null)
			subset = NO_SUBSET;
		List<XObjectModel> selection = new ArrayList<XObjectModel>();
		ListStore<XObjectModel> store = subsets.getStore();
		for(int i=0, n=store.getCount(); i<n; i++) {
			XObjectModel model = store.getAt(i);
			if(subset.equals(model.getXObject())) {
				selection.add(model);
				subsets.setSelection(selection);
				break;
			}
		}
//		subsets.getListView().setStore(subsets.getStore());
//		subsets.getListView().getSelectionModel().select(0);
	}

	private final XSubset getFirstSubsetSelection() {
		List<XObjectModel> selection = subsets.getSelection();
		if(selection.size() > 0)
			return (XSubset) selection.get(0).getXObject();			
		return NO_SUBSET;
	}
}
