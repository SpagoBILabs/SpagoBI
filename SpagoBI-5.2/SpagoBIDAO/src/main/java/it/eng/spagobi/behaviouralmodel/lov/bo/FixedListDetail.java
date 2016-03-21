/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.behaviouralmodel.lov.bo;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ObjParuse;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.Field;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.Record;
import it.eng.spagobi.tools.dataset.common.metadata.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData.FieldType;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.MetaData;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.objects.Couple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

/**
 * Defines method to manage lov of fixed list type
 */
public class FixedListDetail extends DependenciesPostProcessingLov implements ILovDetail {

	private static transient Logger logger = Logger.getLogger(FixedListDetail.class);

	/**
	 * items of the list
	 */
	List items = new ArrayList();

	private List visibleColumnNames = null;
	private String valueColumnName = "VALUE";
	private String descriptionColumnName = "DESCRIPTION";
	private List invisibleColumnNames = null;

	// each entry of the list contains the name of the column to be considered as value column as first item, and the name of the column to be considered as
	// description column as second item
	private List<Couple<String, String>> treeLevelsColumns = null;

	private String lovType = "simple";

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
	 * @param dataDefinition
	 *            the data definition
	 *
	 * @throws SourceBeanException
	 *             the source bean exception
	 */
	public FixedListDetail(String dataDefinition) throws SourceBeanException {
		loadFromXML(dataDefinition);
	}

	/**
	 * loads the lov from an xml string.
	 *
	 * @param dataDefinition
	 *            the xml definition of the lov
	 *
	 * @throws SourceBeanException
	 *             the source bean exception
	 */
	@Override
	public void loadFromXML(String dataDefinition) throws SourceBeanException {
		dataDefinition.trim();
		SourceBean source = SourceBean.fromXMLString(dataDefinition);
		if (!source.getName().equals("FIXLISTLOV")) {
			SourceBean wrapper = new SourceBean("FIXLISTLOV");
			wrapper.setAttribute(source);
			source = wrapper;
		}
		// load data from xml
		List listRows = source.getAttributeAsList("ROWS.ROW");
		Iterator iterRows = listRows.iterator();
		ArrayList lovList = new ArrayList();
		while (iterRows.hasNext()) {
			FixedListItemDetail lov = new FixedListItemDetail();
			SourceBean element = (SourceBean) iterRows.next();
			String value = (String) element.getAttribute("VALUE");
			// ******** only for retro compatibility
			if (value == null)
				value = (String) element.getAttribute("NAME");
			// *************************************
			lov.setValue(value);
			String description = (String) element.getAttribute("DESCRIPTION");
			lov.setDescription(description);
			lovList.add(lov);
		}
		setLovs(lovList);

		// get and set value column
		String valueColumn = "VALUE";
		SourceBean valCol = (SourceBean) source.getAttribute("VALUE-COLUMN");
		if (valCol != null)
			valueColumn = valCol.getCharacters();
		setValueColumnName(valueColumn);
		// get and set the description column
		String descrColumn = "DESCRIPTION";
		SourceBean descColSB = (SourceBean) source.getAttribute("DESCRIPTION-COLUMN");
		if (descColSB != null)
			descrColumn = descColSB.getCharacters();
		setDescriptionColumnName(descrColumn);
		// get and set list of visible columns
		List visColNames = new ArrayList();
		SourceBean visColSB = (SourceBean) source.getAttribute("VISIBLE-COLUMNS");
		if (visColSB != null) {
			String visColConc = visColSB.getCharacters();
			if ((visColConc != null) && !visColConc.trim().equalsIgnoreCase("")) {
				String[] visColArr = visColConc.split(",");
				visColNames = Arrays.asList(visColArr);
			}
		}
		setVisibleColumnNames(visColNames);
		// get and set list of invisible columns
		List invisColNames = new ArrayList();
		SourceBean invisColSB = (SourceBean) source.getAttribute("INVISIBLE-COLUMNS");
		if (invisColSB != null) {
			String invisColConc = invisColSB.getCharacters();
			if ((invisColConc != null) && !invisColConc.trim().equalsIgnoreCase("")) {
				String[] invisColArr = invisColConc.split(",");
				invisColNames = Arrays.asList(invisColArr);
			}
		}
		setInvisibleColumnNames(invisColNames);

		try {
			SourceBean treeLevelsColumnsBean = (SourceBean) source.getAttribute("TREE-LEVELS-COLUMNS");
			if (treeLevelsColumnsBean != null && treeLevelsColumnsBean.getCharacters() != null && treeLevelsColumnsBean.getCharacters().trim() != "") {
				// compatibility control (versions till 5.1.0 does not have
				// VALUE-COLUMNS and DESCRIPTION-COLUMNS definition)
				String treeLevelsColumnsString = treeLevelsColumnsBean.getCharacters();
				String[] treeLevelsColumnArr = treeLevelsColumnsString.split(",");
				List<Couple<String, String>> levelsMap = new ArrayList<Couple<String, String>>();
				for (int i = 0; i < treeLevelsColumnArr.length; i++) {
					String aValueColumn = treeLevelsColumnArr[i];
					if (i == treeLevelsColumnArr.length - 1) {
						levelsMap.add(new Couple<String, String>(aValueColumn, descrColumn));
					} else {
						levelsMap.add(new Couple<String, String>(aValueColumn, aValueColumn));
					}
				}
				this.treeLevelsColumns = levelsMap;
				this.setValueColumnName(null);
				this.setDescriptionColumnName(null);
			} else {
				SourceBean valuesColumnsBean = (SourceBean) source.getAttribute("VALUE-COLUMNS");
				SourceBean descriptionColumnsBean = (SourceBean) source.getAttribute("DESCRIPTION-COLUMNS");
				if (valuesColumnsBean != null) {

					Assert.assertTrue(descriptionColumnsBean != null, "DESCRIPTION-COLUMNS tag not defined");

					List<Couple<String, String>> levelsMap = new ArrayList<Couple<String, String>>();
					String valuesColumnsStr = valuesColumnsBean.getCharacters();
					logger.debug("VALUE-COLUMNS is [" + valuesColumnsStr + "]");
					String descriptionColumnsStr = descriptionColumnsBean.getCharacters();
					logger.debug("DESCRIPTION-COLUMNS is [" + descriptionColumnsStr + "]");
					String[] valuesColumns = valuesColumnsStr.split(",");
					String[] descriptionColumns = descriptionColumnsStr.split(",");
					List<String> valuesColumnsList = Arrays.asList(valuesColumns);
					List<String> descriptionColumnsList = Arrays.asList(descriptionColumns);

					Assert.assertTrue(valuesColumnsList.size() == descriptionColumnsList.size(),
							"Value columns list and description columns list must have the same length");

					for (int i = 0; i < valuesColumnsList.size(); i++) {
						String aValueColumn = valuesColumnsList.get(i);
						String aDescriptionColumn = descriptionColumnsList.get(i);
						levelsMap.add(new Couple<String, String>(aValueColumn, aDescriptionColumn));
					}
					this.treeLevelsColumns = levelsMap;
				}
			}
		} catch (Exception e) {
			logger.error("Error while reading LOV definition from XML", e);
			throw new SpagoBIRuntimeException("Error while reading LOV definition from XML", e);
		}

		SourceBean lovTypeBean = (SourceBean) source.getAttribute("LOVTYPE");
		String lovType;
		if (lovTypeBean != null) {
			lovType = lovTypeBean.getCharacters();
			this.lovType = lovType;
		}

		// // set visible and invisible columns
		// List visColList = new ArrayList();
		// visColList.add("DESCRIPTION");
		// List invisColList = new ArrayList();
		// invisColList.add("VALUE");
		// setInvisibleColumnNames(invisColList);
		// setVisibleColumnNames(visColList);
	}

	/**
	 * serialize the lov to an xml string.
	 *
	 * @return the serialized xml string
	 */
	@Override
	public String toXML() {
		String lovXML = "";
		lovXML += "<FIXLISTLOV>";
		lovXML += "<ROWS>";
		FixedListItemDetail lov = null;
		Iterator iter = items.iterator();
		while (iter.hasNext()) {
			lov = (FixedListItemDetail) iter.next();
			String value = lov.getValue();
			String description = lov.getDescription();
			lovXML += "<ROW" + " VALUE=\"" + value + "\"" + " DESCRIPTION=\"" + description + "\"" + "/>";
		}
		lovXML += "</ROWS>";
		if (this.isSimpleLovType()) {
			lovXML += "<VALUE-COLUMN>" + valueColumnName + "</VALUE-COLUMN>" + "<DESCRIPTION-COLUMN>" + descriptionColumnName + "</DESCRIPTION-COLUMN>";
		} else {
			lovXML += "<VALUE-COLUMNS>" + GeneralUtilities.fromListToString(this.getTreeValueColumns(), ",") + "</VALUE-COLUMNS>" + "<DESCRIPTION-COLUMNS>"
					+ GeneralUtilities.fromListToString(this.getTreeDescriptionColumns(), ",") + "</DESCRIPTION-COLUMNS>";
		}
		lovXML += "<VISIBLE-COLUMNS>" + GeneralUtilities.fromListToString(visibleColumnNames, ",") + "</VISIBLE-COLUMNS>" + "<INVISIBLE-COLUMNS>"
				+ GeneralUtilities.fromListToString(invisibleColumnNames, ",") + "</INVISIBLE-COLUMNS>" + "<LOVTYPE>" + this.getLovType() + "</LOVTYPE>";
		lovXML += "</FIXLISTLOV>";
		return lovXML;
	}

	/**
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#getLovResult(IEngUserProfile profile, List<ObjParuse> dependencies, ExecutionInstance
	 *      executionInstance) throws Exception;
	 */
	@Override
	public String getLovResult(IEngUserProfile profile, List<ObjParuse> dependencies, List<BIObjectParameter> BIObjectParameters, Locale locale)
			throws Exception {
		String lovResult = "<ROWS>";
		FixedListItemDetail lov = null;
		Iterator iter = items.iterator();
		while (iter.hasNext()) {
			lov = (FixedListItemDetail) iter.next();
			String value = lov.getValue();
			String description = lov.getDescription();
			lovResult += "<ROW" + " VALUE=\"" + value + "\"" + " DESCRIPTION=\"" + description + "\"" + "/>";
		}
		lovResult += "</ROWS>";
		lovResult = StringUtilities.substituteProfileAttributesInString(lovResult, profile);
		return lovResult;
	}

	/**
	 * Method returns result of the defined LOV of type fixed list as data store.
	 * */
	public DataStore getLovResultAsDataStore(IEngUserProfile profile, List<ObjParuse> dependencies, List<BIObjectParameter> BIObjectParameters, Locale locale)
			throws Exception {

		DataStore dsToReturn = new DataStore();
		IFieldMetaData fieldMetaData1 = new FieldMetadata();
		IFieldMetaData fieldMetaData2 = new FieldMetadata();

		fieldMetaData1.setAlias("VALUE");
		fieldMetaData1.setName("VALUE");
		fieldMetaData1.setType(String.class);
		fieldMetaData1.setFieldType(FieldType.ATTRIBUTE);

		IMetaData metadata = new MetaData();
		metadata.addFiedMeta(fieldMetaData1);

		fieldMetaData2.setAlias("DESCRIPTION");
		fieldMetaData2.setName("DESCRIPTION");
		fieldMetaData2.setType(String.class);
		fieldMetaData2.setFieldType(FieldType.ATTRIBUTE);

		metadata.addFiedMeta(fieldMetaData2);

		dsToReturn.setMetaData(metadata);

		FixedListItemDetail fixLovItem = null;

		Iterator iter = items.iterator();

		int i = 0;

		while (iter.hasNext()) {

			Field valueField = new Field();
			Field descriptionField = new Field();
			Record record = new Record();
			List<IField> listOfFields = new ArrayList<IField>();

			fixLovItem = (FixedListItemDetail) iter.next();
			String value = fixLovItem.getValue();

			/*
			 * If fixed LOV items value contains '${' - user has defined a dynamic item (field) for this LOV that will contain value of specified profile
			 * attribute of the current User. We need to take value that User has defined and insert it as a value into this fixed list.
			 */
			if (value.contains("'${")) {

				int startAttr = value.indexOf("'${") + "'${".length();
				int endAttr = value.indexOf("}'");
				String attr = value.substring(startAttr, endAttr); // the name of the specified attribute

				String attrValue = profile.getUserAttribute(attr).toString(); // value of that attribute

				valueField.setValue(attrValue); // set this value for this item

			} else {
				valueField.setValue(fixLovItem.getValue()); // set static (predefined) value for this item
			}

			/* Description value is always statically defined (predefined). */
			descriptionField.setValue(fixLovItem.getDescription());

			listOfFields.add(valueField);
			listOfFields.add(descriptionField);

			record.setFields(listOfFields);

			dsToReturn.appendRecord(record);
		}

		return dsToReturn;
	}

	/**
	 * Gets the list of names of the profile attributes required.
	 *
	 * @return list of profile attribute names
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public List getProfileAttributeNames() throws Exception {
		List names = new ArrayList();
		String lovResult = this.toXML();
		while (lovResult.indexOf("${") != -1) {
			int startind = lovResult.indexOf("${");
			int endind = lovResult.indexOf("}", startind);
			String attributeDef = lovResult.substring(startind + 2, endind);
			if (attributeDef.indexOf("(") != -1) {
				int indroundBrack = lovResult.indexOf("(", startind);
				String nameAttr = lovResult.substring(startind + 2, indroundBrack);
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
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public boolean requireProfileAttributes() throws Exception {
		boolean contains = false;
		String lovResult = this.toXML();
		if (lovResult.indexOf("${") != -1) {
			contains = true;
		}
		return contains;
	}

	/**
	 * Adds a lov to the lov Detail List.
	 *
	 * @param description
	 *            The added lov description
	 * @param value
	 *            the value
	 */
	public void add(String value, String description) {
		// if name or description are empty don't add
		if ((value == null) || (value.trim().equals("")))
			return;
		if ((description == null) || (description.trim().equals("")))
			return;
		// if the element already exists don't add
		Iterator iter = items.iterator();
		while (iter.hasNext()) {
			FixedListItemDetail lovDet = (FixedListItemDetail) iter.next();
			if (value.equals(lovDet.getValue()) && description.equals(lovDet.getDescription())) {
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
	 * @param value
	 *            The deleted lov name
	 * @param description
	 *            The deleted lov description
	 */
	public void remove(String value, String description) {
		Iterator iter = items.iterator();
		while (iter.hasNext()) {
			FixedListItemDetail lovDet = (FixedListItemDetail) iter.next();
			if (value.equals(lovDet.getValue()) && description.equals(lovDet.getDescription())) {
				items.remove(lovDet);
				break;
			}
		}
	}

	/**
	 * Splits an XML string by using some <code>SourceBean</code> object methods in order to obtain the source <code>LovDetail</code> objects whom XML has been
	 * built.
	 *
	 * @param dataDefinition
	 *            The XML input String
	 *
	 * @return The corrispondent <code>LovDetailList</code> object
	 *
	 * @throws SourceBeanException
	 *             If a SourceBean Exception occurred
	 */
	public static FixedListDetail fromXML(String dataDefinition) throws SourceBeanException {
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
	 * @param items
	 *            the items to set
	 */
	public void setLovs(List items) {
		this.items = items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#getDescriptionColumnName()
	 */
	@Override
	public String getDescriptionColumnName() {
		return descriptionColumnName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#setDescriptionColumnName(java.lang.String)
	 */
	@Override
	public void setDescriptionColumnName(String descriptionColumnName) {
		this.descriptionColumnName = descriptionColumnName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#getInvisibleColumnNames()
	 */
	@Override
	public List getInvisibleColumnNames() {
		return invisibleColumnNames;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#setInvisibleColumnNames(java.util.List)
	 */
	@Override
	public void setInvisibleColumnNames(List invisibleColumnNames) {
		this.invisibleColumnNames = invisibleColumnNames;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#getValueColumnName()
	 */
	@Override
	public String getValueColumnName() {
		return valueColumnName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#setValueColumnName(java.lang.String)
	 */
	@Override
	public void setValueColumnName(String valueColumnName) {
		this.valueColumnName = valueColumnName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#getVisibleColumnNames()
	 */
	@Override
	public List getVisibleColumnNames() {
		return visibleColumnNames;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#setVisibleColumnNames(java.util.List)
	 */
	@Override
	public void setVisibleColumnNames(List visibleColumnNames) {
		this.visibleColumnNames = visibleColumnNames;
	}

	@Override
	public String getLovType() {
		return lovType;
	}

	@Override
	public void setLovType(String lovType) {
		this.lovType = lovType;
	}

	@Override
	public List<Couple<String, String>> getTreeLevelsColumns() {
		return treeLevelsColumns;
	}

	@Override
	public void setTreeLevelsColumns(List<Couple<String, String>> treeLevelsColumns) {
		this.treeLevelsColumns = treeLevelsColumns;
	}

}
