/*
*
* @file PicklistHandler.java
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
* @author ArndHouben
*
* @version $Id: PicklistHandler.java,v 1.9 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.io.xml;

import java.util.HashSet;

import org.palo.api.Dimension;
import org.palo.api.subsets.SubsetFilter;
import org.palo.api.subsets.filter.PicklistFilter;
import org.palo.api.subsets.filter.settings.IntegerParameter;
import org.palo.api.subsets.filter.settings.ObjectParameter;
import org.palo.api.subsets.filter.settings.PicklistFilterSetting;

/**
 * <code>PicklistHandler</code>
 * <p>
 * API internal implementation of the {@link SubsetFilterHandler} interface 
 * which handles {@link PicklistFilter}s.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: PicklistHandler.java,v 1.9 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
class PicklistHandler extends AbstractSubsetFilterHandler {
	
	
	public static final String XPATH = "/subset/picklist_filter";
	
	//all other elements:
	private static final String PICK_TYPE_VALUE = "/subset/picklist_filter/pick_type/value";
	private static final String PICK_TYPE_PARAMETER = "/subset/picklist_filter/pick_type/parameter";
//	private static final String PICK_ELEM = "/subset/picklist/manual_definition/pick_elem";
	private static final String PICK_ELEM_VALUE = "/subset/picklist_filter/manual_definition/value/pick_elem";
	private static final String PICK_ELEM_PARAMETER = "/subset/picklist_filter/manual_definition/parameter";
	
	private final PicklistFilterSetting plInfo;
	
	
	public PicklistHandler() {
		plInfo = new PicklistFilterSetting();
	}
	
	public final String getXPath() {
		return XPATH;
	}

	public final void enter(String path) {		
	}
	public final void leave(String path, String value) {
		if (path.equals(PICK_TYPE_VALUE))
			plInfo.setInsertMode(SubsetXMLHandler.getInteger(value));
		else if (path.equals(PICK_TYPE_PARAMETER)) {
			IntegerParameter oldParam = plInfo.getInsertMode();
			plInfo.setInsertMode(new IntegerParameter(value));
			plInfo.setInsertMode(oldParam.getValue());
		} else if (path.equals(PICK_ELEM_VALUE))
			plInfo.addElement(value);
		else if(path.equals(PICK_ELEM_PARAMETER)) {
			ObjectParameter oldSelection = plInfo.getSelection();
			ObjectParameter newSelection = new ObjectParameter(value);
			newSelection.setValue(oldSelection.getValue());
			plInfo.setSelection(newSelection);
		}
	}

	public final SubsetFilter createFilter(Dimension dimension) {
		return new PicklistFilter(dimension, plInfo);
	}
	
	public static final String getPersistenceString(PicklistFilter filter) {
		PicklistFilterSetting plInfo = filter.getSettings();
		StringBuffer xmlStr = new StringBuffer();
		xmlStr.append("<picklist_filter>\r\n<manual_definition>\r\n");
		ObjectParameter selection = plInfo.getSelection();
		ParameterHandler.addParameter(selection, xmlStr);
		HashSet<String> pickedElements = (HashSet<String>)selection.getValue();
		xmlStr.append("<value>\r\n");
		for (String picked : pickedElements) {
			xmlStr.append("<pick_elem>");
			xmlStr.append(picked);
			xmlStr.append("</pick_elem>\r\n");
		}
		xmlStr.append("</value>\r\n");
		xmlStr.append("</manual_definition>\r\n");
		
		// picklist insert mode:
		xmlStr.append("<pick_type>\r\n");
		xmlStr.append(ParameterHandler.getXML(plInfo
				.getInsertMode()));
		xmlStr.append("</pick_type>\r\n");
		
		xmlStr.append("</picklist_filter>\r\n");
		return xmlStr.toString();
	}

}
