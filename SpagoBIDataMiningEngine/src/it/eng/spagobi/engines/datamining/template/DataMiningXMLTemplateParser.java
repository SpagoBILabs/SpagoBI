/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.datamining.template;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.datamining.model.FileDataset;
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Monica Franceschini
 */
public class DataMiningXMLTemplateParser implements IDataMiningTemplateParser {

	public static String TAG_ROOT = "DATA_MINING";
	public static String TAG_SCRIPT = "SCRIPT";
	public static String TAG_DATASETS = "DATASETS";
	public static String TAG_DATASET = "DATASET";
	public static String TAG_OUTPUT = "OUTPUT";

	public static String DATASET_ATTRIBUTE_READTYPE = "readType";
	public static String DATASET_ATTRIBUTE_NAME = "name";
	public static String OUTPUT_ATTRIBUTE_TYPE = "type";
	public static String OUTPUT_ATTRIBUTE_NAME = "plotName";

	public static String PROP_PARAMETER_NAME = "name";
	public static String PROP_PARAMETER_ALIAS = "as";
	public static String TAG_PARAMETER = "parameter";

	/** Logger component. */
	public static transient Logger logger = Logger.getLogger(DataMiningXMLTemplateParser.class);

	public DataMiningTemplate parse(Object template) {
		Assert.assertNotNull(template, "Input parameter [template] cannot be null");
		Assert.assertTrue(template instanceof SourceBean, "Input parameter [template] cannot be of type [" + template.getClass().getName() + "]");
		return parse((SourceBean) template);
	}

	private DataMiningTemplate parse(SourceBean template) {

		DataMiningTemplate toReturn = null;

		try {
			logger.debug("Starting template parsing....");

			toReturn = new DataMiningTemplate();

			SourceBean scriptSB = (SourceBean) template.getAttribute(TAG_SCRIPT);
			logger.debug(TAG_SCRIPT + ": " + scriptSB);
			Assert.assertNotNull(scriptSB, "Template is missing " + TAG_SCRIPT + " tag");
			String text = scriptSB.getCharacters();
			logger.debug("script : " + text);
			toReturn.setScript(text);

			SourceBean datasetsSB = (SourceBean) template.getAttribute(TAG_DATASETS);
			if (datasetsSB != null) {

				List<FileDataset> datasets = new ArrayList<FileDataset>();
				List<SourceBean> datasetListSB = datasetsSB.getAttributeAsList(TAG_DATASET);
				if (datasetListSB != null && datasetListSB.size() != 0) {
					for (Iterator iterator = datasetListSB.iterator(); iterator.hasNext();) {
						SourceBean datasetSB = (SourceBean) iterator.next();
						FileDataset ftds = new FileDataset();
						logger.debug("dataset: " + datasetSB);
						Assert.assertNotNull(datasetSB, "Template is missing " + TAG_DATASET + " tag");
						String datasetReadType = (String) datasetSB.getAttribute(DATASET_ATTRIBUTE_READTYPE);
						if (datasetReadType != null) {
							ftds.setReadType(datasetReadType);
						}
						String datasetName = (String) datasetSB.getAttribute(DATASET_ATTRIBUTE_NAME);
						if (datasetName != null) {
							ftds.setName(datasetName);
						}
						datasets.add(ftds);
					}
					toReturn.setDatasets(datasets);
				}
			}

			SourceBean outputSB = (SourceBean) template.getAttribute(TAG_OUTPUT);
			logger.debug("output: " + outputSB);
			Assert.assertNotNull(outputSB, "Template is missing " + TAG_OUTPUT + " tag");
			String outputType = (String) outputSB.getAttribute(OUTPUT_ATTRIBUTE_TYPE);
			toReturn.setOutputType(outputType);
			String outputName = (String) outputSB.getAttribute(OUTPUT_ATTRIBUTE_NAME);
			toReturn.setOutputName(outputName);

			// List<DataMiningTemplate.Parameter> parameters = new
			// ArrayList<DataMiningTemplate.Parameter>();
			// List parametersSB = datasetSB.getAttributeAsList(TAG_PARAMETER);
			// Iterator it = parametersSB.iterator();
			// while (it.hasNext()) {
			// SourceBean parameterSB = (SourceBean) it.next();
			// logger.debug("Found " + TAG_PARAMETER + " definition :" +
			// parameterSB);
			// String name = (String)
			// parameterSB.getAttribute(PROP_PARAMETER_NAME);
			// String alias = (String)
			// parameterSB.getAttribute(PROP_PARAMETER_ALIAS);
			// Assert.assertNotNull(name, "Missing parameter's " +
			// PROP_PARAMETER_NAME + " attribute");
			// Assert.assertNotNull(alias, "Missing parameter's " +
			// PROP_PARAMETER_ALIAS + " attribute");
			// DataMiningTemplate.Parameter parameter = toReturn.new
			// Parameter();
			// parameter.setName(name);
			// parameter.setAlias(alias);
			// parameters.add(parameter);
			// }
			// toReturn.setParameters(parameters);

			// read user profile for profiled data access
			// setProfilingUserAttributes(template, toReturn);

			logger.debug("Template parsed succesfully");
		} catch (Exception e) {
			logger.error("Impossible to parse template [" + template.toString() + "]", e);
			throw new DataMiningTemplateParseException(e);
		} finally {
			logger.debug("OUT");
		}

		return toReturn;
	}

	/*
	 * private void setProfilingUserAttributes(SourceBean template,
	 * DataMiningTemplate toReturn) { SourceBean dataAccessSB = (SourceBean)
	 * template.getAttribute( TAG_DATA_ACCESS ); logger.debug(TAG_DATA_ACCESS +
	 * ": " + dataAccessSB); List<String> attributes = new ArrayList<String>();
	 * if (dataAccessSB != null) { List attributesSB =
	 * dataAccessSB.getAttributeAsList(TAG_USER_ATTRIBUTE); Iterator it =
	 * attributesSB.iterator(); while (it.hasNext()) { SourceBean attributeSB =
	 * (SourceBean) it.next(); logger.debug("Found " + TAG_USER_ATTRIBUTE +
	 * " definition :" + attributeSB); String name = (String)
	 * attributeSB.getAttribute(PROP_USER_ATTRIBUTE_NAME);
	 * Assert.assertNotNull(name, "Missing [" + PROP_PARAMETER_NAME +
	 * "] attribute in user profile attribute"); attributes.add(name); } }
	 * toReturn.setProfilingUserAttributes(attributes); }
	 */

	private static String getBeanValue(String tag, SourceBean bean) {
		String field = null;
		SourceBean fieldBean = null;
		fieldBean = (SourceBean) bean.getAttribute(tag);
		if (fieldBean != null) {
			field = fieldBean.getCharacters();
			if (field == null) {
				field = "";
			}
		}
		return field;
	}

}
