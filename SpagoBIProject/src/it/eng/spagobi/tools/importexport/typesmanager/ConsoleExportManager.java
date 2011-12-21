/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.tools.importexport.typesmanager;

import java.util.Iterator;
import java.util.Vector;

import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.tools.dataset.bo.GuiGenericDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;
import it.eng.spagobi.tools.importexport.ExportManager;
import it.eng.spagobi.tools.importexport.ExporterMetadata;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.star.configuration.backend.StratumCreationException;

import sun.misc.BASE64Decoder;

public class ConsoleExportManager extends AbstractTypesExportManager {


	static private Logger logger = Logger.getLogger(ConsoleExportManager.class);
	private static final BASE64Decoder DECODER = new BASE64Decoder();


	public ConsoleExportManager(String type, ExporterMetadata exporter,
			ExportManager manager) {
		super(type, exporter, manager);
	}

	/**
	 *  export for console needs to get from template datasets relationship and insert them
	 */

	public void manageExport(BIObject biobj, Session session)
	throws EMFUserError {

		logger.debug("IN");

		// get the template
		ObjTemplate template = biobj.getActiveTemplate();
		if (template != null) {
			try {
				byte[] tempFileCont = template.getContent();
				String tempFileStr = new String(tempFileCont);
				//				byte[] templateContent = DECODER.decodeBuffer(new String(template.getContent()));
				//				String tempFileStr = new String(templateContent);
				//System.out.println(tempFileStr);
				JSONObject jsonObject = null;

				try{
					jsonObject = new JSONObject( tempFileStr );
				}
				catch(JSONException e){
					logger.error("error in parsing jason object from template of object with label "+biobj.getLabel());
					throw(e);
				}


				// vector containg labels of dataset to insert
				Vector<String> dsLabels = new Vector<String>();
				Object ob = jsonObject.get("datasets");
				if(ob != null && ob instanceof JSONArray){
					JSONArray dsArrays = (JSONArray) ob;

					for (int i = 0; i < dsArrays.length(); i++) {
						JSONObject ds = (JSONObject)dsArrays.get(i);
						String dsLabel =  ds.getString("label");
						dsLabels.add(dsLabel);
					}					
				}
				else{
					logger.warn("No datasets associated to the console "+biobj.getLabel());
				}

				// insert all datasets
				IDataSetDAO dataSetDAO = DAOFactory.getDataSetDAO();
				for (Iterator iterator = dsLabels.iterator(); iterator.hasNext();) {
					String string = (String) iterator.next();
					logger.debug("get dataset with label " + string);
					GuiGenericDataSet ds = dataSetDAO.loadDataSetByLabel(string);
					// insert dataset if parameter insertDataSet is true (in case of KPI export)
					if(ds != null){
						logger.debug("found dataset with label " + string);
						exporter.insertDataSet(ds, session);
					}
					else {
						logger.error("could not find dataset with label "+string+" : ignore it");
					}

					// search for xxxErrors dataset
					String labelErrors = string+"Errors";
					logger.debug("get dataset with label " + labelErrors+" if present");
					GuiGenericDataSet datasetErrors = dataSetDAO.loadDataSetByLabel(labelErrors);
					if(datasetErrors != null){
						logger.debug("found dataset with label " + labelErrors+"");
						exporter.insertDataSet(datasetErrors, session);
					}
					else {
						logger.warn("could not find dataset with label "+labelErrors);
					}

					// search for xxxAlarms dataset
					String labelAlarms = string+"Alarms";
					logger.debug("get dataset with label " + labelAlarms+" if present");
					GuiGenericDataSet datasetAlarms = dataSetDAO.loadDataSetByLabel(labelAlarms);
					if(datasetAlarms != null){
						logger.debug("found dataset with label " + labelAlarms+"");
						exporter.insertDataSet(datasetAlarms, session);
					}
					else {
						logger.warn("could not find dataset with label "+labelAlarms);
					}

				}
			} catch (Exception e) {
				logger.error("Error while exporting console with id " + biobj.getId() + " and label " + biobj.getLabel() + " : " +
				"could not find dataset reference in its template.");					
				throw new EMFUserError(EMFErrorSeverity.ERROR, "8010", "component_impexp_messages");
			}
		}
		logger.debug("OUT");
	}
}
