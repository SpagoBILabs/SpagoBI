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
package com.tensegrity.wpalo.client.ui.model;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.FastMSTreeItem;

/**
 * A simple model which holds any XObject, useful for a (e.g.){@link ListStore}. 
 * </br></br><b>Example:</b></br>
 * <code>
 *	ListStore<XObjectModel> store = new ListStore<XObjectModel>();</br>
 *	store.add(new XObjectModel(myXObject));</br>
 *	myComboBox.setStore(store);</br>
 * </code>
 * 
 *
 * @version $Id: XObjectModel.java,v 1.6 2010/02/12 13:49:50 PhilippBouillon Exp $
 **/
public class XObjectModel extends BaseModel {
	
	/** generated serial */
	private static final long serialVersionUID = 4473065756678248749L;
	
	private static long ID_COUNTER = 0;
	
//	private int id;
	private final String id;
	private final XObject xObject;
	private transient FastMSTreeItem item;
	
	public XObjectModel(XObject xObj) {
		this.id = (ID_COUNTER++)+ "XOM:"+xObj.getId();
		this.xObject = xObj;
		set("name", xObj.getName());
	}
	
	public void setItem(FastMSTreeItem item) {
		this.item = item;
	}
	
	public FastMSTreeItem getItem() {
		return item;
	}
	
	public String getId() {
		return id;
	}
	
	public final XObject getXObject() {
		return xObject;
	}
	
	public int hashCode() {		
		int xObjectCode = xObject == null ? 17 : xObject.hashCode();
		int hashCode = 3 * id.hashCode() + 5 * xObjectCode;
		return hashCode;
	}
	
	public boolean equals(Object o) {
		if (o instanceof XObjectModel) {
			XObjectModel other = (XObjectModel) o;
			return (id.equals(other.id)) && (xObject.equals(other.xObject));
		}
		return false;
	}

}


