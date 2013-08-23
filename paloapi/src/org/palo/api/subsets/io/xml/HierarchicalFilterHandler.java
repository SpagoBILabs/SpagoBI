/*
*
* @file HierarchicalFilterHandler.java
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
* @version $Id: HierarchicalFilterHandler.java,v 1.9 2009/04/29 10:21:58 PhilippBouillon Exp $
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
import org.palo.api.subsets.filter.HierarchicalFilter;
import org.palo.api.subsets.filter.settings.BooleanParameter;
import org.palo.api.subsets.filter.settings.HierarchicalFilterSetting;
import org.palo.api.subsets.filter.settings.IntegerParameter;
import org.palo.api.subsets.filter.settings.StringParameter;

/**
 * <code>HierarchicalFilterHandler</code>
 * <p>
 * API internal implementation of the {@link SubsetFilterHandler} interface 
 * which handles {@link HierarchicalFilter}s.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: HierarchicalFilterHandler.java,v 1.9 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
class HierarchicalFilterHandler extends AbstractSubsetFilterHandler {
	
	public static final String XPATH = "/subset/hierarchical_filter";
	//all the other possible paths
	private static final String ELEMENT_VALUE = "/subset/hierarchical_filter/element/value";
	private static final String ELEMENT_PARAMETER = "/subset/hierarchical_filter/element/parameter";
	private static final String ABOVE_VALUE = "/subset/hierarchical_filter/above/value";
	private static final String ABOVE_PARAMETER = "/subset/hierarchical_filter/above/parameter";
	private static final String EXCLUSIVE_VALUE = "/subset/hierarchical_filter/exclusive/value";
	private static final String EXCLUSIVE_PARAMETER = "/subset/hierarchical_filter/exclusive/parameter";
	private static final String HIDE_VALUE = "/subset/hierarchical_filter/hide/value";
	private static final String HIDE_PARAMETER = "/subset/hierarchical_filter/hide/parameter";
	private static final String REV_ELEMENT_VALUE = "/subset/hierarchical_filter/revolve_element/value";
	private static final String REV_ELEMENT_PARAMETER = "/subset/hierarchical_filter/revolve_element/parameter";
	private static final String REV_COUNT_VALUE = "/subset/hierarchical_filter/revolve_count/value";
	private static final String REV_COUNT_PARAMETER = "/subset/hierarchical_filter/revolve_count/parameter";
	private static final String REV_ADD_VALUE = "/subset/hierarchical_filter/revolve_add/value";
	private static final String REV_ADD_PARAMETER = "/subset/hierarchical_filter/revolve_add/parameter";
	private static final String LEVEL_START_VALUE = "/subset/hierarchical_filter/level_start/value";
	private static final String LEVEL_START_PARAMETER = "/subset/hierarchical_filter/level_start/parameter";
	private static final String LEVEL_END_VALUE = "/subset/hierarchical_filter/level_end/value";
	private static final String LEVEL_END_PARAMETER = "/subset/hierarchical_filter/level_end/parameter";
	
	private final HierarchicalFilterSetting hfInfo;
	
	public HierarchicalFilterHandler() {
		hfInfo = new HierarchicalFilterSetting();
	}
	
	public final String getXPath() {
		return XPATH;
	}
	
	public final void enter(String path) {
//		if(path.equals(ABOVE))
//			hfInfo.setAbove(true);
//		else if(path.equals(EXCLUSIVE))
//			hfInfo.setExclusive(true);
	}
	public final void leave(String path, String value) {
		if(path.equals(ABOVE_VALUE))
			hfInfo.setAbove(SubsetXMLHandler.getBoolean(value));
		else if(path.equals(ABOVE_PARAMETER)) {
			BooleanParameter oldParam = hfInfo.getAbove();
			hfInfo.setAbove(new BooleanParameter(value));
			hfInfo.setAbove(oldParam.getValue());
		}
		else if(path.equals(EXCLUSIVE_VALUE))
			hfInfo.setExclusive(SubsetXMLHandler.getBoolean(value));
		else if(path.equals(EXCLUSIVE_PARAMETER)) {
			BooleanParameter oldParam = hfInfo.getExclusive();
			hfInfo.setExclusive(new BooleanParameter(value));
			hfInfo.setExclusive(oldParam.getValue());
		}
		else if(path.equals(ELEMENT_VALUE))
			hfInfo.setRefElement(value);
		else if(path.equals(ELEMENT_PARAMETER)) {
			StringParameter oldParam = hfInfo.getRefElement();
			hfInfo.setRefElement(new StringParameter(value));
			hfInfo.setRefElement(oldParam.getValue());
		}
		else if(path.equals(HIDE_VALUE)) 
			hfInfo.setHideMode(SubsetXMLHandler.getInteger(value));
		else if(path.equals(HIDE_PARAMETER)) {
			IntegerParameter oldParam = hfInfo.getHideMode();
			hfInfo.setHideMode(new IntegerParameter(value));
			hfInfo.setHideMode(oldParam.getValue());
		} 
		else if(path.equals(REV_ELEMENT_VALUE))
			hfInfo.setRevolveElement(value);
		else if(path.equals(REV_ELEMENT_PARAMETER)) {
			StringParameter oldParam = hfInfo.getRevolveElement();
			hfInfo.setRevolveElement(new StringParameter(value));
			hfInfo.setRevolveElement(oldParam.getValue());
		}
		else if(path.equals(REV_COUNT_VALUE))
			hfInfo.setRevolveCount(SubsetXMLHandler.getInteger(value));
		else if(path.equals(REV_COUNT_PARAMETER)) {
			IntegerParameter oldParam = hfInfo.getRevolveCount();
			hfInfo.setRevolveCount(new IntegerParameter(value));
			hfInfo.setRevolveCount(oldParam.getValue());
		}
		else if(path.equals(REV_ADD_VALUE))
			hfInfo.setRevolveMode(SubsetXMLHandler.getInteger(value));
		else if(path.equals(REV_ADD_PARAMETER)) {
			IntegerParameter oldParam = hfInfo.getRevolveMode();
			hfInfo.setRevolveMode(new IntegerParameter(value));
			hfInfo.setRevolveMode(oldParam.getValue());
		}		
		else if(path.equals(LEVEL_START_VALUE)) {
			if(subsetVersion.equals("1.0rc2")) {
				hfInfo.setStartElement(value);		
			} else
				hfInfo.setStartLevel(SubsetXMLHandler.getInteger(value));
		} else if(path.equals(LEVEL_START_PARAMETER)) {
			if(subsetVersion.equals("1.0rc2")) {
				StringParameter oldParam = hfInfo.getStartElement();
				hfInfo.setStartElement(new StringParameter(value));
				hfInfo.setStartElement(oldParam.getValue());
			} else {
				IntegerParameter oldParam = hfInfo.getStartLevel();
				hfInfo.setStartLevel(new IntegerParameter(value));
				hfInfo.setStartLevel(oldParam.getValue());
			}
		}
		else if(path.equals(LEVEL_END_VALUE)) {
			if(subsetVersion.equals("1.0rc2")) {
				hfInfo.setEndElement(value);		
			} else
				hfInfo.setEndLevel(SubsetXMLHandler.getInteger(value));
		} else if(path.equals(LEVEL_END_PARAMETER)) {
			if(subsetVersion.equals("1.0rc2")) {
				StringParameter oldParam = hfInfo.getEndElement();
				hfInfo.setEndElement(new StringParameter(value));
				hfInfo.setEndElement(oldParam.getValue());
			} else {
				IntegerParameter oldParam = hfInfo.getEndLevel();
				hfInfo.setEndLevel(new IntegerParameter(value));
				hfInfo.setEndLevel(oldParam.getValue());
			}
		}
	}

	public final SubsetFilter createFilter(Dimension dimension) {
		if(hfInfo.doLevelSelection() && subsetVersion.equals("1.0rc2")) {
			//replace any given level element id with its corresponding level
			Element start = 
				dimension.getElementById(hfInfo.getStartElement().getValue());
			if(start != null) {
				hfInfo.setStartLevel(start.getLevel());
				hfInfo.setStartElement((String)null);
			}
			Element end = 
				dimension.getElementById(hfInfo.getEndElement().getValue());
			if(end != null) {				
				hfInfo.setEndLevel(end.getLevel());
				hfInfo.setEndElement((String)null);
			}
		}
		return new HierarchicalFilter(dimension, hfInfo);
	}
	
	public static final String getPersistenceString(HierarchicalFilter filter) {
		HierarchicalFilterSetting hfInfo = filter.getSettings();
		StringBuffer xmlStr = new StringBuffer();
		xmlStr.append("<hierarchical_filter>\r\n");
		if(hfInfo.doAboveBelowSelection()) {
			xmlStr.append("<element>\r\n");
			xmlStr.append(ParameterHandler.getXML(
					hfInfo.getRefElement()));
			xmlStr.append("</element>\r\n");
			xmlStr.append("<above>\r\n");
			xmlStr.append(ParameterHandler.getXML(
					hfInfo.getAbove()));
			xmlStr.append("</above>\r\n");
			xmlStr.append("<exclusive>\r\n");
			xmlStr.append(ParameterHandler.getXML(
					hfInfo.getExclusive()));
			xmlStr.append("</exclusive>\r\n");
		}
		if(hfInfo.doHide()) {
			xmlStr.append("<hide>\r\n");
			xmlStr.append(ParameterHandler.getXML(
					hfInfo.getHideMode()));
			xmlStr.append("</hide>\r\n");
		}
		if(hfInfo.doLevelSelection()) {
			xmlStr.append("<level_start>\r\n");
			xmlStr.append(ParameterHandler.getXML(hfInfo.getStartLevel()));
			xmlStr.append("</level_start>\r\n");
			xmlStr.append("<level_end>\r\n");
			xmlStr.append(ParameterHandler.getXML(hfInfo.getEndLevel()));
			xmlStr.append("</level_end>\r\n");
		}
		if(hfInfo.doRevolve()) {
			xmlStr.append("<revolve_element>\r\n");
			xmlStr.append(ParameterHandler.getXML(
					hfInfo.getRevolveElement()));
			xmlStr.append("</revolve_element>\r\n");
			
			xmlStr.append("<revolve_count>\r\n");
			xmlStr.append(ParameterHandler.getXML(
					hfInfo.getRevolveCount()));
			xmlStr.append("</revolve_count>\r\n");
			
			xmlStr.append("<revolve_add>\r\n");
			xmlStr.append(ParameterHandler.getXML(
					hfInfo.getRevolveMode()));
			xmlStr.append("</revolve_add>\r\n");
		}
		xmlStr.append("</hierarchical_filter>\r\n");
		return xmlStr.toString();
	}

}
