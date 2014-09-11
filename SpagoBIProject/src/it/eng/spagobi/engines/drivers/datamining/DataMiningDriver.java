/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.drivers.datamining;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.ParameterValuesEncoder;
import it.eng.spagobi.engines.drivers.EngineURL;
import it.eng.spagobi.engines.drivers.exceptions.InvalidOperationRequest;
import it.eng.spagobi.engines.drivers.generic.GenericDriver;
import it.eng.spagobi.engines.drivers.whatif.WhatIfDriver;
import it.eng.spagobi.tools.catalogue.bo.Artifact;
import it.eng.spagobi.tools.catalogue.bo.Content;
import it.eng.spagobi.tools.catalogue.dao.IArtifactsDAO;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;



/**
 * Driver Implementation for DataMining Engine. 
 */
public class DataMiningDriver extends GenericDriver {

	
    static private Logger logger = Logger.getLogger(DataMiningDriver.class);


	protected ObjTemplate getTemplate(Object biobject) {
		ObjTemplate template = null;
		try {
			BIObject biobj = (BIObject) biobject;
			template = DAOFactory.getObjTemplateDAO().getBIObjectActiveTemplate(biobj.getId());
			if (template == null)
				throw new Exception("Active Template null");
		} catch (Exception e) {
			throw new RuntimeException("Error while getting document's template", e);
		}
		return template;
	}

	protected byte[] getTemplateAsByteArray(Object biobject) {
		ObjTemplate template = this.getTemplate(biobject);
		byte[] bytes;
		try {
			bytes = template.getContent();
		} catch (Exception e) {
			throw new RuntimeException("Error while getting document's template", e);
		}
		if (bytes == null)
			throw new RuntimeException("Content of the Active template null");
		return bytes;
	}


	@Override
	public EngineURL getEditDocumentTemplateBuildUrl(Object biobject,
			IEngUserProfile profile) throws InvalidOperationRequest {
		return super.getEditDocumentTemplateBuildUrl(biobject, profile);
	}

	@Override
	public EngineURL getNewDocumentTemplateBuildUrl(Object biobject,
			IEngUserProfile profile) throws InvalidOperationRequest {
		return super.getNewDocumentTemplateBuildUrl(biobject, profile);
	}


}

