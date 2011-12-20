/**
Copyright (C) 2004 - 2011, Engineering Ingegneria Informatica s.p.a.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this list of 
      conditions and the following disclaimer.
      
 * Redistributions in binary form must reproduce the above copyright notice, this list of 
      conditions and the following disclaimer in the documentation and/or other materials 
      provided with the distribution.
      
 * Neither the name of the Engineering Ingegneria Informatica s.p.a. nor the names of its contributors may
      be used to endorse or promote products derived from this software without specific
      prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 **/
package it.eng.spagobi.engines.drivers.accessibility;

import it.eng.spago.base.SourceBean;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.utilities.ParameterValuesEncoder;
import it.eng.spagobi.engines.drivers.AbstractDriver;
import it.eng.spagobi.engines.drivers.EngineURL;
import it.eng.spagobi.engines.drivers.IEngineDriver;
import it.eng.spagobi.engines.drivers.exceptions.InvalidOperationRequest;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

public class AccessibilityDriver extends AbstractDriver implements
		IEngineDriver {

	static Logger logger = Logger.getLogger(AccessibilityDriver.class);

	public EngineURL getEditDocumentTemplateBuildUrl(Object biobject,
			IEngUserProfile profile) throws InvalidOperationRequest {
		logger.warn("Function not implemented");
		throw new InvalidOperationRequest();
	}

	public EngineURL getNewDocumentTemplateBuildUrl(Object biobject,
			IEngUserProfile profile) throws InvalidOperationRequest {
		logger.warn("Function not implemented");
		throw new InvalidOperationRequest();
	}

	public Map getParameterMap(Object biobject, 
			IEngUserProfile profile,
			String roleName) {
		logger.debug("IN");
		Map map = new Hashtable();
		try {
			BIObject biobj = (BIObject) biobject;
			map = getMap(biobj);
		} catch (ClassCastException cce) {
			logger.error("The parameter is not a BIObject type", cce);
		}
		map = applySecurity(map, profile);
		logger.debug("OUT");
		return map;
	}

	public Map getParameterMap(Object object, 
			Object subObject,
			IEngUserProfile profile, 
			String roleName) {
		return getParameterMap(object, profile, roleName);
	}

	private Map getMap(BIObject biobj) {
		logger.debug("IN");
		Map pars = new Hashtable();

		String documentId = biobj.getId().toString();
		pars.put("document", documentId);
		logger.debug("Add document parameter:" + documentId);
		pars.put("documentLabel", biobj.getLabel());
		logger.debug("Add document parameter:" + biobj.getLabel());

		// adding date format parameter
		SingletonConfig config = SingletonConfig.getInstance();
		String formatSB = config.getConfigValue("SPAGOBI.DATE-FORMAT.format");
		String format = (formatSB == null) ? "DD-MM-YYYY" : formatSB;
		pars.put("dateformat", format);

		pars = addBIParameters(biobj, pars);
		pars = addBIParameterDescriptions(biobj, pars);

		logger.debug("OUT");
		return pars;
	}

	private Map addBIParameters(BIObject biobj, Map pars) {
		logger.debug("IN");
		if (biobj == null) {
			logger.warn("BIObject is null");
			return pars;
		}

		ParameterValuesEncoder parValuesEncoder = new ParameterValuesEncoder();
		if (biobj.getBiObjectParameters() != null) {
			BIObjectParameter biobjPar = null;
			for (Iterator it = biobj.getBiObjectParameters().iterator(); it
					.hasNext();) {
				try {
					biobjPar = (BIObjectParameter) it.next();
					String value = parValuesEncoder.encode(biobjPar);
					if (value != null)
						pars.put(biobjPar.getParameterUrlName(), value);
					else
						logger.warn("value encoded IS null");
					logger.debug("Add parameter:"
							+ biobjPar.getParameterUrlName() + "/" + value);
				} catch (Exception e) {
					logger.error("Error while processing a BIParameter", e);
				}
			}
		}
		logger.debug("OUT");
		return pars;
	}
}
