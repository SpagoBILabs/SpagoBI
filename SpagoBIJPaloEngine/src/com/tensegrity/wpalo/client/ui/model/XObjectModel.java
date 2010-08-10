/*
*
* @file XObjectModel.java
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
* @version $Id: XObjectModel.java,v 1.6 2010/02/12 13:49:50 PhilippBouillon Exp $
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


