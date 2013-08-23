/*
*
* @file SortingFilterHandler.java
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
* @version $Id: SortingFilterHandler.java,v 1.9 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.io.xml;

import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.subsets.SubsetFilter;
import org.palo.api.subsets.filter.SortingFilter;
import org.palo.api.subsets.filter.settings.IntegerParameter;
import org.palo.api.subsets.filter.settings.SortingFilterSetting;
import org.palo.api.subsets.filter.settings.StringParameter;

/**
 * <code>SortingFilterHandler</code>
 * <p>
 * API internal implementation of the {@link SubsetFilterHandler} interface 
 * which handles {@link SortingFilter}s.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: SortingFilterHandler.java,v 1.9 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
class SortingFilterHandler extends AbstractSubsetFilterHandler {
	
	public static final String XPATH = "/subset/sorting_filter";
	//all other elements:
	private static final String SORTING_CRITERIA_VALUE = "/subset/sorting_filter/sorting_criteria/value";
	private static final String SORTING_CRITERIA_PARAMETER = "/subset/sorting_filter/sorting_criteria/parameter";
    private static final String REVERSE_VALUE = "/subset/sorting_filter/reverse/value";
    private static final String REVERSE_PARAMETER = "/subset/sorting_filter/reverse/parameter";
    private static final String TYPE_LIMITATION_VALUE = "/subset/sorting_filter/type_limitation/value";
    private static final String TYPE_LIMITATION_PARAMETER = "/subset/sorting_filter/type_limitation/parameter";
    private static final String WHOLE_VALUE = "/subset/sorting_filter/whole/value";
    private static final String WHOLE_PARAMETER = "/subset/sorting_filter/whole/parameter";
    private static final String ATTRIBUTE_VALUE = "/subset/sorting_filter/attribute/value";
    private static final String ATTRIBUTE_PARAMETER = "/subset/sorting_filter/attribute/parameter";
    private static final String LEVEL_ELEMENT_1_0RC2_VALUE = "/subset/sorting_filter/level_element/value";
    private static final String LEVEL_ELEMENT_1_0RC2_PARAMETER = "/subset/sorting_filter/level_element/parameter";
    //new in version 1.0:
    private static final String LEVEL_ELEMENT_VALUE = "/subset/sorting_filter/level/value";
    private static final String LEVEL_ELEMENT_PARAMETER = "/subset/sorting_filter/level/parameter";
	private static final String SHOW_DUPLICATES_VALUE = "/subset/sorting_filter/show_duplicates/value";
	private static final String SHOW_DUPLICATES_PARAMETER = "/subset/sorting_filter/show_duplicates/parameter";
    
    private final SortingFilterSetting sfInfo;
	
    public SortingFilterHandler() {
		sfInfo = new SortingFilterSetting();
	}
	
	
	public final String getXPath() {
		return XPATH;
	}

	public final void enter(String path) {		
	}
	public final void leave(String path, String value) {
		if(path.equals(SORTING_CRITERIA_VALUE))
			sfInfo.setSortCriteria(SubsetXMLHandler.getInteger(value));
		else if(path.equals(SORTING_CRITERIA_PARAMETER)) {
			IntegerParameter oldParam = sfInfo.getSortCriteria();
			sfInfo.setSortCriteria(new IntegerParameter(value));
			sfInfo.setSortCriteria(oldParam.getValue());
		}
		else if(path.equals(REVERSE_VALUE))
			sfInfo.setOrderMode(SubsetXMLHandler.getInteger(value));
		else if(path.equals(REVERSE_PARAMETER)) {
			IntegerParameter oldParam = sfInfo.getOrderMode();
			sfInfo.setOrderMode(new IntegerParameter(value));
			sfInfo.setOrderMode(oldParam.getValue());
		}
		else if(path.equals(TYPE_LIMITATION_VALUE))
			sfInfo.setSortTypeMode(SubsetXMLHandler.getInteger(value));
		else if(path.equals(TYPE_LIMITATION_PARAMETER)) {
			IntegerParameter oldParam = sfInfo.getSortTypeMode();
			sfInfo.setSortTypeMode(new IntegerParameter(value));
			sfInfo.setSortTypeMode(oldParam.getValue());
		}
		else if(path.equals(WHOLE_VALUE))
			sfInfo.setHierarchicalMode(SubsetXMLHandler.getInteger(value));
		else if(path.equals(WHOLE_PARAMETER)) {
			IntegerParameter oldParam = sfInfo.getHierarchicalMode();
			sfInfo.setHierarchicalMode(new IntegerParameter(value));
			sfInfo.setHierarchicalMode(oldParam.getValue());
		}
		else if(path.equals(ATTRIBUTE_VALUE))
			sfInfo.setSortAttribute(value);
		else if(path.equals(ATTRIBUTE_PARAMETER)) {
			StringParameter oldParam = sfInfo.getSortAttribute();
			sfInfo.setSortAttribute(new StringParameter(value));
			sfInfo.setSortAttribute(oldParam.getValue());
		}		
		else if(path.equals(LEVEL_ELEMENT_1_0RC2_VALUE)) {
			sfInfo.setSortLevelElement(value);
		} else if(path.equals(LEVEL_ELEMENT_1_0RC2_PARAMETER)) {
			StringParameter oldParm = sfInfo.getSortLevelElement();
			sfInfo.setSortLevelElement(new StringParameter(value));
			sfInfo.setSortLevelElement(oldParm.getValue());
		}
		else if(path.equals(LEVEL_ELEMENT_VALUE))
			sfInfo.setSortLevel(SubsetXMLHandler.getInteger(value));
		else if(path.equals(LEVEL_ELEMENT_PARAMETER)) {
			IntegerParameter oldParm = sfInfo.getSortLevel();
			sfInfo.setSortLevel(new IntegerParameter(value));
			sfInfo.setSortLevel(oldParm.getValue());
		}
		else if(path.equals(SHOW_DUPLICATES_VALUE))
			sfInfo.setShowDuplicates(SubsetXMLHandler.getInteger(value));
		else if(path.equals(SHOW_DUPLICATES_PARAMETER)) {
			IntegerParameter oldParam = sfInfo.getShowDuplicates();
			sfInfo.setShowDuplicates(new IntegerParameter(value));
			sfInfo.setShowDuplicates(oldParam.getValue());
		}
	}
	
	public final SubsetFilter createFilter(Dimension dimension) {
		//check subset version and adjust level setting:
		if (subsetVersion.equals("1.0rc2")) {
			if (sfInfo.doSortPerLevel()) {
				// level setting contains element id:
				Element sortElement = dimension.getElementById(sfInfo
						.getSortLevelElement().getValue());
				if (sortElement != null) {
					sfInfo.setSortLevel(sortElement.getLevel());
					sfInfo.setSortLevelElement((String)null);
				}
			}
		}
		return new SortingFilter(dimension, sfInfo);
	}
	
	public static final String getPersistenceString(SortingFilter filter) {
		SortingFilterSetting sfInfo = filter.getSettings();
		StringBuffer xmlStr = new StringBuffer();
		xmlStr.append("<sorting_filter>\r\n");
		//whole
		if (sfInfo.doHierarchy()) {
			xmlStr.append("<whole>\r\n");
			xmlStr.append(
					ParameterHandler.getXML(sfInfo.getHierarchicalMode()));
			xmlStr.append("</whole>\r\n");
		}
		//criteria:
		xmlStr.append("<sorting_criteria>\r\n");
		xmlStr.append(ParameterHandler.getXML(sfInfo.getSortCriteria()));
		xmlStr.append("</sorting_criteria>\r\n");
		//attribute
		if(sfInfo.doSortByAttribute()) {
			xmlStr.append("<attribute>\r\n");
			xmlStr.append(ParameterHandler.getXML(sfInfo.getSortAttribute()));
			xmlStr.append("</attribute>\r\n");
		}
		//type limitation
		if(sfInfo.doSortByType()) {
			xmlStr.append("<type_limitation>\r\n");
			xmlStr.append(ParameterHandler.getXML(sfInfo.getSortTypeMode()));
			xmlStr.append("</type_limitation>\r\n");
		}
		//lvl element
		if (sfInfo.doSortPerLevel()) {
			xmlStr.append("<level>\r\n");
			xmlStr.append(ParameterHandler.getXML(sfInfo.getSortLevel()));
			xmlStr.append("</level>\r\n");
		}
		//reverse
		if(sfInfo.doReverseOrder()) {
			xmlStr.append("<reverse>\r\n");
			xmlStr.append(
					ParameterHandler.getXML(sfInfo.getOrderMode()));
			xmlStr.append("</reverse>\r\n");
		}
		//show duplicates
		xmlStr.append("<show_duplicates>\r\n");
		xmlStr.append(ParameterHandler.getXML(sfInfo.getShowDuplicates()));
		xmlStr.append("</show_duplicates>\r\n");

		xmlStr.append("</sorting_filter>\r\n");
		return xmlStr.toString();
	}

}
