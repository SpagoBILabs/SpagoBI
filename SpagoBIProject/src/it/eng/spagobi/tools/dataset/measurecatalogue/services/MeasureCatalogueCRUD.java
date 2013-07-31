 /* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.tools.dataset.measurecatalogue.services;

import it.eng.spagobi.services.exceptions.ExceptionUtilities;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.tools.dataset.measurecatalogue.MeasureCatalogue;
import it.eng.spagobi.tools.dataset.measurecatalogue.MeasureCatalogueMeasure;
import it.eng.spagobi.tools.dataset.measurecatalogue.MeasureCatalogueSingleton;
import it.eng.spagobi.tools.dataset.measurecatalogue.materializer.InMemoryMaterializer;
import it.eng.spagobi.tools.dataset.measurecatalogue.materializer.exception.NoCommonDimensionsRuntimeException;
import it.eng.spagobi.tools.dataset.measurecatalogue.materializer.exception.NoCompleteCommonDimensionsRuntimeException;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.json.JSONObject;

/**
 * This class contains the services to perform the CRUD action on the measure catalogue
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */

@Path("/measures")
public class MeasureCatalogueCRUD {
	
	static private String noCommonDimensionsRuntimeException = "error.mesage.description.measure.join.no.common.dimension";
	static private String noCompleteCommonDimensionsRuntimeException = "error.mesage.description.measure.join.no.complete.common.dimension";
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllMeasures(@Context HttpServletRequest req) {
		String measures =  MeasureCatalogueSingleton.getMeasureCatologue().toString();
		return measures;
	}
	
	@POST
	@Path("/join")
	@Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.APPLICATION_JSON)
	public String join(@Context HttpServletRequest req, MultivaluedMap<String, String> form) {
		MeasureCatalogue catalogue = MeasureCatalogueSingleton.getMeasureCatologue();
		IDataStore dataStore;
		
		List<String> ids = form.get("ids");
		
		List<MeasureCatalogueMeasure> measures= new ArrayList<MeasureCatalogueMeasure>();
		
		for(int i=0; i<ids.size(); i++){
			MeasureCatalogueMeasure aMeasure = catalogue.getMeasureById(new Integer(ids.get(i)));
			if(aMeasure!=null){
				measures.add(aMeasure);
			}
		}

		InMemoryMaterializer imm = new InMemoryMaterializer();
		try {
			dataStore =  imm.joinMeasures(measures);
		} catch (NoCommonDimensionsRuntimeException e) {
			return ( ExceptionUtilities.serializeException(noCommonDimensionsRuntimeException,null));
		} catch (NoCompleteCommonDimensionsRuntimeException e) {
			return ( ExceptionUtilities.serializeException(noCompleteCommonDimensionsRuntimeException,null));
		}
		

		JSONDataWriter dataSetWriter = new JSONDataWriter();
		JSONObject dataStroreJSON =  (JSONObject) dataSetWriter.write(dataStore);
		
		return  dataStroreJSON.toString();
	}
	

}
