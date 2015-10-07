/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.behaviouralmodel.lov.bo;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ObjParuse;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.SpagoBITracer;
import it.eng.spagobi.commons.utilities.SpagoBIUtilities;
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
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

/**
 * Defines the <code>JavaClassDetail</code> objects. This object is used to store JavaClass Wizard detail information.
 */
public class JavaClassDetail extends DependenciesPostProcessingLov implements ILovDetail {

	private static transient Logger logger = Logger.getLogger(JavaClassDetail.class);

	/**
	 * name of the class which return the data
	 */
	private String javaClassName = "";
	private List visibleColumnNames = null;
	private String valueColumnName = "";
	private String descriptionColumnName = "";
	private List invisibleColumnNames = null;

	// each entry of the list contains the name of the column to be considered as value column as first item, and the name of the column to be considered as
	// description column as second item
	private List<Couple<String, String>> treeLevelsColumns = null;

	private String lovType = "simple";

	/**
	 * constructor.
	 */
	public JavaClassDetail() {
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
	public JavaClassDetail(String dataDefinition) throws SourceBeanException {
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
		// build the sourcebean
		SourceBean source = SourceBean.fromXMLString(dataDefinition);
		// get and set the java class name
		SourceBean javaClassNameSB = (SourceBean) source.getAttribute("JAVA_CLASS_NAME");
		String javaClassName = javaClassNameSB.getCharacters();
		setJavaClassName(javaClassName);
		// get and set value column
		String valueColumn = "";
		SourceBean valCol = (SourceBean) source.getAttribute("VALUE-COLUMN");
		if (valCol != null)
			valueColumn = valCol.getCharacters();
		setValueColumnName(valueColumn);
		// get and set the description column
		String descrColumn = "";
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
	}

	/**
	 * serialize the lov to an xml string.
	 *
	 * @return the serialized xml string
	 */
	@Override
	public String toXML() {
		String XML = "<JAVACLASSLOV>" + "<JAVA_CLASS_NAME>" + this.getJavaClassName() + "</JAVA_CLASS_NAME>" + "<VISIBLE-COLUMNS>"
				+ SpagoBIUtilities.fromListToString(this.getVisibleColumnNames(), ",") + "</VISIBLE-COLUMNS>" + "<INVISIBLE-COLUMNS>"
				+ SpagoBIUtilities.fromListToString(this.getInvisibleColumnNames(), ",") + "</INVISIBLE-COLUMNS>" + "<LOVTYPE>" + this.getLovType()
				+ "</LOVTYPE>";
		if (this.isSimpleLovType()) {
			XML += "<VALUE-COLUMN>" + valueColumnName + "</VALUE-COLUMN>" + "<DESCRIPTION-COLUMN>" + descriptionColumnName + "</DESCRIPTION-COLUMN>";
		} else {
			XML += "<VALUE-COLUMNS>" + GeneralUtilities.fromListToString(this.getTreeValueColumns(), ",") + "</VALUE-COLUMNS>" + "<DESCRIPTION-COLUMNS>"
					+ GeneralUtilities.fromListToString(this.getTreeDescriptionColumns(), ",") + "</DESCRIPTION-COLUMNS>";
		}
		XML += "</JAVACLASSLOV>";
		return XML;
	}

	/**
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#getLovResult(IEngUserProfile profile, List<ObjParuse> dependencies, ExecutionInstance
	 *      executionInstance) throws Exception;
	 */
	@Override
	public String getLovResult(IEngUserProfile profile, List<ObjParuse> dependencies, List<BIObjectParameter> BIObjectParameters, Locale locale)
			throws Exception {
		IJavaClassLov javaClassLov = createClassInstance();
		if (javaClassLov instanceof AbstractJavaClassLov) {
			((AbstractJavaClassLov) javaClassLov).setBIObjectParameter(BIObjectParameters);

		}

		String result = javaClassLov.getValues(profile);
		result = result.trim();

		// check if the result must be converted into the right xml sintax
		boolean toconvert = checkSintax(result);
		if (toconvert) {
			result = convertResult(result);
		}

		return result;
	}

	/**
	 * Method returns result of the defined LOV of type Java class as data store.
	 * */

	public DataStore getLovResultAsDataStore(String inputXML) throws Exception {

		DataStore dsToReturn = new DataStore();
		IFieldMetaData fieldMetaData = new FieldMetadata();

		/*
		 * We have just one field to set - the VALUE field (only one column and N rows).
		 */
		fieldMetaData.setAlias("VALUE");
		fieldMetaData.setName("VALUE");
		fieldMetaData.setType(String.class);
		fieldMetaData.setFieldType(FieldType.ATTRIBUTE);

		IMetaData metadata = new MetaData();
		metadata.addFiedMeta(fieldMetaData);

		dsToReturn.setMetaData(metadata);

		int startIndexRows = inputXML.indexOf("<ROWS>") + "<ROWS>".length();
		int endIndexRows = inputXML.indexOf("</ROWS>");

		String rowList = inputXML.substring(startIndexRows, endIndexRows);

		String[] lista = rowList.split("<ROW VALUE=\"");
		String value = "";

		for (int i = 1; i < lista.length; i++) {
			value = lista[i].substring(0, lista[i].length() - "\"/>".length());
			Field field = new Field();
			Record record = new Record();
			List<IField> listOfFields = new ArrayList<IField>();
			field.setValue(value);
			listOfFields.add(field);

			record.setFields(listOfFields);
			dsToReturn.appendRecord(record);
		}

		return dsToReturn;
	}

	/**
	 * checks if the result is formatted in the right xml structure
	 *
	 * @param result
	 *            the result of the lov
	 * @return true if the result is formatted correctly false otherwise
	 * @throws EMFUserError
	 */
	public boolean checkSintax(String result) throws EMFUserError {
		return JavaClassUtils.checkSintax(result);
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
		IJavaClassLov javaClassLov = createClassInstance();
		List attrNames = javaClassLov.getNamesOfProfileAttributeRequired();
		return attrNames;
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
		boolean requires = false;
		IJavaClassLov javaClassLov = createClassInstance();
		List attrNames = javaClassLov.getNamesOfProfileAttributeRequired();
		if (attrNames.size() != 0) {
			requires = true;
		}
		return requires;
	}

	/**
	 * Creates and returns an instance of the lov class
	 *
	 * @return instance of the lov class which must implement IJavaClassLov interface
	 * @throws EMFUserError
	 */
	private IJavaClassLov createClassInstance() throws EMFUserError {
		String javaClassName = getJavaClassName();
		if (javaClassName == null || javaClassName.trim().equals("")) {
			SpagoBITracer.major(SpagoBIConstants.NAME_MODULE, this.getClass().getName(), "getLovResult", "The java class name is not specified");
			throw new EMFUserError(EMFErrorSeverity.ERROR, "1071");
		}
		IJavaClassLov javaClassLov = null;
		Class javaClass = null;
		try {
			javaClass = Class.forName(javaClassName);
		} catch (ClassNotFoundException e) {
			SpagoBITracer.major(SpagoBIConstants.NAME_MODULE, this.getClass().getName(), "getLovResult", "Java class '" + javaClassName + "' not found!!");
			List pars = new ArrayList();
			pars.add(javaClassName);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "1072", pars);
		}
		try {
			javaClassLov = (IJavaClassLov) javaClass.newInstance();
		} catch (Exception e) {
			SpagoBITracer.major(SpagoBIConstants.NAME_MODULE, this.getClass().getName(), "getLovResult", "Error while instatiating Java class '"
					+ javaClassName + "'.");
			List pars = new ArrayList();
			pars.add(javaClassName);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "1073", pars);
		}
		return javaClassLov;
	}

	/**
	 * Wraps the result of the query execution into the right xml structure
	 *
	 * @param result
	 *            the result of the query (which is not formatted with the right xml structure)
	 * @return the xml structure of the result
	 */
	public String convertResult(String result) {
		return JavaClassUtils.convertResult(result);
	}

	/**
	 * Gets the class name.
	 *
	 * @return the complete name of the class
	 */
	public String getJavaClassName() {
		return javaClassName;
	}

	/**
	 * Sets the class name.
	 *
	 * @param javaClassName
	 *            the complete name of the class
	 */
	public void setJavaClassName(String javaClassName) {
		this.javaClassName = javaClassName;
	}

	/**
	 * Builds a JavaClassDetail starting from ax xml representation.
	 *
	 * @param dataDefinition
	 *            the data definition
	 *
	 * @return The JavaClassDetail object
	 *
	 * @throws SourceBeanException
	 *             the source bean exception
	 */
	public static JavaClassDetail fromXML(String dataDefinition) throws SourceBeanException {
		JavaClassDetail jcd = new JavaClassDetail();
		jcd.loadFromXML(dataDefinition);
		return jcd;
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
