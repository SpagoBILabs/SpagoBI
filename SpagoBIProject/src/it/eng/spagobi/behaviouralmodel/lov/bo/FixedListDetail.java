/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.behaviouralmodel.lov.bo;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.StringUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Defines method to manage lov of fixed list type
 */
public class FixedListDetail  implements ILovDetail  {
    
	/**
	 * items of the list
	 */
	List items = new ArrayList();    
	
	
	private List visibleColumnNames = null;
	private String valueColumnName = "VALUE";
	private String descriptionColumnName = "DESCRIPTION";
	private List invisibleColumnNames = null;
	
	/**
	 * constructor.
	 */
	public FixedListDetail() {
		visibleColumnNames = new ArrayList();
		visibleColumnNames.add("DESCRIPTION");
		invisibleColumnNames = new ArrayList();
		invisibleColumnNames.add("VALUE");
	} 
	
	/**
	 * constructor.
	 * 
	 * @param dataDefinition the data definition
	 * 
	 * @throws SourceBeanException the source bean exception
	 */
	public FixedListDetail(String dataDefinition) throws SourceBeanException {
		loadFromXML(dataDefinition);
	}
	
	/**
	 * loads the lov from an xml string.
	 * 
	 * @param dataDefinition the xml definition of the lov
	 * 
	 * @throws SourceBeanException the source bean exception
	 */
	public void  loadFromXML (String dataDefinition) throws SourceBeanException {
		dataDefinition.trim();
		SourceBean source = SourceBean.fromXMLString(dataDefinition);
		if(!source.getName().equals("FIXLISTLOV")) {
			SourceBean wrapper = new SourceBean("FIXLISTLOV");
			wrapper.setAttribute(source);	
			source = wrapper;
		}
		// load data from xml
		List listRows = source.getAttributeAsList("ROWS.ROW");
		Iterator iterRows = listRows.iterator();
		ArrayList lovList = new ArrayList();
		while(iterRows.hasNext()){
			FixedListItemDetail lov = new FixedListItemDetail();
			SourceBean element = (SourceBean)iterRows.next();
			String value = (String)element.getAttribute("VALUE");
			// ******** only for retro compatibility
			if(value==null)
				value = (String)element.getAttribute("NAME");
			// *************************************
			lov.setValue(value);
			String description = (String)element.getAttribute("DESCRIPTION");
			lov.setDescription(description);
			lovList.add(lov);
		}
 		setLovs(lovList);
 		
        // get and set value column
	    String valueColumn = "VALUE";
	    SourceBean valCol = (SourceBean)source.getAttribute("VALUE-COLUMN");
		if(valCol!=null)
			valueColumn = valCol.getCharacters();
		setValueColumnName(valueColumn);
		 // get and set the description column
	    String descrColumn = "DESCRIPTION";
	    SourceBean descColSB = (SourceBean)source.getAttribute("DESCRIPTION-COLUMN");
		if(descColSB!=null)
			descrColumn = descColSB.getCharacters();
		setDescriptionColumnName(descrColumn);
		// get and set list of visible columns
		List visColNames = new ArrayList();
		SourceBean visColSB = (SourceBean)source.getAttribute("VISIBLE-COLUMNS");
		if(visColSB!=null){
			String visColConc = visColSB.getCharacters();
			if( (visColConc!=null) && !visColConc.trim().equalsIgnoreCase("") ) {
				String[] visColArr = visColConc.split(",");
				visColNames = Arrays.asList(visColArr);
			}
		}
		setVisibleColumnNames(visColNames);
		// get and set list of invisible columns
		List invisColNames = new ArrayList();
		SourceBean invisColSB = (SourceBean)source.getAttribute("INVISIBLE-COLUMNS");
		if(invisColSB!=null){
			String invisColConc = invisColSB.getCharacters();
			if( (invisColConc!=null) && !invisColConc.trim().equalsIgnoreCase("") ) {
				String[] invisColArr = invisColConc.split(",");
				invisColNames = Arrays.asList(invisColArr);
			}
		}
		setInvisibleColumnNames(invisColNames);

// 		// set visible and invisible columns
// 		List visColList = new ArrayList();
// 		visColList.add("DESCRIPTION");
// 		List invisColList = new ArrayList();
//		invisColList.add("VALUE");
//		setInvisibleColumnNames(invisColList);
//		setVisibleColumnNames(visColList);
	}	
	
	/**
	 * serialize the lov to an xml string.
	 * 
	 * @return the serialized xml string
	 */
	public String toXML() {
		String lovXML = "";
		lovXML += "<FIXLISTLOV>";
		lovXML += "<ROWS>";
		FixedListItemDetail lov = null;
		Iterator iter = items.iterator();
		while(iter.hasNext()){
			lov = (FixedListItemDetail)iter.next();
			String value = lov.getValue();
			String description = lov.getDescription();
			lovXML += "<ROW" +
					  " VALUE=\"" + value + "\"" +
					  " DESCRIPTION=\"" + description + "\"" +
					  "/>";
		}
		lovXML += "</ROWS>";
		lovXML += "<VALUE-COLUMN>"+valueColumnName+"</VALUE-COLUMN>" +
				  "<DESCRIPTION-COLUMN>"+descriptionColumnName+"</DESCRIPTION-COLUMN>" +
				  "<VISIBLE-COLUMNS>"+GeneralUtilities.fromListToString(visibleColumnNames, ",")+"</VISIBLE-COLUMNS>" +
				  "<INVISIBLE-COLUMNS>"+GeneralUtilities.fromListToString(invisibleColumnNames, ",")+"</INVISIBLE-COLUMNS>" +
				  "</FIXLISTLOV>";
		return lovXML;
	}
	
	/**
	 * Returns the result of the lov using a user profile to fill the lov profile attribute.
	 * 
	 * @param profile the profile of the user
	 * 
	 * @return the string result of the lov
	 * 
	 * @throws Exception the exception
	 */
	public String getLovResult(IEngUserProfile profile) throws Exception {
		String lovResult = "<ROWS>";
		FixedListItemDetail lov = null;
		Iterator iter = items.iterator();
		while(iter.hasNext()){
			lov = (FixedListItemDetail)iter.next();
			String value = lov.getValue();
			String description = lov.getDescription();
			lovResult += "<ROW" +
					  " VALUE=\"" + value + "\"" +
					  " DESCRIPTION=\"" + description + "\"" +
					  "/>";
		}
		lovResult += "</ROWS>";
		lovResult = StringUtilities.substituteProfileAttributesInString(lovResult, profile);
		return lovResult;
	}
		

	/**
	 * Gets the list of names of the profile attributes required.
	 * 
	 * @return list of profile attribute names
	 * 
	 * @throws Exception the exception
	 */
	public List getProfileAttributeNames() throws Exception {
		List names = new ArrayList();
		String lovResult = this.toXML();
		while(lovResult.indexOf("${")!=-1) {
			int startind = lovResult.indexOf("${");
			int endind = lovResult.indexOf("}", startind);
			String attributeDef = lovResult.substring(startind + 2, endind);
			if(attributeDef.indexOf("(")!=-1) {
				int indroundBrack = lovResult.indexOf("(", startind);
				String nameAttr = lovResult.substring(startind+2, indroundBrack);
				names.add(nameAttr);
			} else {
				names.add(attributeDef);
			}
			lovResult = lovResult.substring(endind);
		}
		return names;
	}

	/**
	 * Checks if the lov requires one or more profile attributes.
	 * 
	 * @return true if the lov require one or more profile attributes, false otherwise
	 * 
	 * @throws Exception the exception
	 */
	public boolean requireProfileAttributes() throws Exception {
		boolean contains = false;
		String lovResult = this.toXML();
		if(lovResult.indexOf("${")!=-1) {
			contains = true;
		}
		return contains;
	}	
	
	
	/**
	 * Adds a lov to the lov Detail List.
	 * 
	 * @param description The added lov description
	 * @param value the value
	 */
	public void add(String value, String description) {
		// if name or description are empty don't add
		if((value==null) || (value.trim().equals("")))
				return;
		if((description==null) || (description.trim().equals("")))
			return;
		// if the element already exists don't add
		Iterator iter = items.iterator();
		while(iter.hasNext()) {
			FixedListItemDetail lovDet = (FixedListItemDetail)iter.next();
			if(value.equals(lovDet.getValue()) && description.equals(lovDet.getDescription())) {
				return;
			}
		}
		// add the item
		FixedListItemDetail lovdet = new FixedListItemDetail();
		lovdet.setValue(value);
		lovdet.setDescription(description);
		items.add(lovdet);
	}
	
	
	/**
	 * Deletes a lov from the lov Detail List.
	 * 
	 * @param value The deleted lov name
	 * @param description The deleted lov description
	 */
	public void remove(String value, String description) {
		Iterator iter = items.iterator();
		while(iter.hasNext()) {
			FixedListItemDetail lovDet = (FixedListItemDetail)iter.next();
			if(value.equals(lovDet.getValue()) && description.equals(lovDet.getDescription())) {
				items.remove(lovDet);
				break;
			}
		}
	}
	
	
	/**
	 * Splits an XML string by using some <code>SourceBean</code> object methods
	 * in order to obtain the source <code>LovDetail</code> objects whom XML has been
	 * built.
	 * 
	 * @param dataDefinition The XML input String
	 * 
	 * @return The corrispondent <code>LovDetailList</code> object
	 * 
	 * @throws SourceBeanException If a SourceBean Exception occurred
	 */
	public static FixedListDetail  fromXML (String dataDefinition) throws SourceBeanException {
		return new FixedListDetail(dataDefinition);	
	}
	
	
	/**
	 * Gets item of the fixed list.
	 * 
	 * @return items of the fixed list
	 */
	public List getItems() {
		return items;
	}
	
	/**
	 * Sets items of the fixed list.
	 * 
	 * @param items the items to set
	 */
	public void setLovs(List items) {
		this.items = items;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#getDescriptionColumnName()
	 */
	public String getDescriptionColumnName() {
		return descriptionColumnName;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#setDescriptionColumnName(java.lang.String)
	 */
	public void setDescriptionColumnName(String descriptionColumnName) {
		this.descriptionColumnName = descriptionColumnName;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#getInvisibleColumnNames()
	 */
	public List getInvisibleColumnNames() {
		return invisibleColumnNames;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#setInvisibleColumnNames(java.util.List)
	 */
	public void setInvisibleColumnNames(List invisibleColumnNames) {
		this.invisibleColumnNames = invisibleColumnNames;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#getValueColumnName()
	 */
	public String getValueColumnName() {
		return valueColumnName;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#setValueColumnName(java.lang.String)
	 */
	public void setValueColumnName(String valueColumnName) {
		this.valueColumnName = valueColumnName;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#getVisibleColumnNames()
	 */
	public List getVisibleColumnNames() {
		return visibleColumnNames;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#setVisibleColumnNames(java.util.List)
	 */
	public void setVisibleColumnNames(List visibleColumnNames) {
		this.visibleColumnNames = visibleColumnNames;
	}

	
}
