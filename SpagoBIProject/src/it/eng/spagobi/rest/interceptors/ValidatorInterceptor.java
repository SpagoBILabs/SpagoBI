/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.rest.interceptors;

import it.eng.spagobi.rest.annotations.ToValidate;
import it.eng.spagobi.rest.validation.FieldsValidatorFactory;
import it.eng.spagobi.rest.validation.IFieldsValidator;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.json.JSONArray;
import org.json.JSONObject;

/**Notice this interceptor is implementing an the interface AcceptedByMethod. We also have to implement 
 * the method accept, where we receive the target method information and we can decide whether  
 * if this interceptor will be executed or not.
 * The org.jboss.resteasy.spi.interception.PreProcessInterceptor runs after a JAX-RS resource 
 * method is found to invoke on, but before the actual invocation happens
 * 
 * @author Monica Franceschini (monica.franceschini@eng.it)
 *
 */
@Provider
@ServerInterceptor
@Precedence("DECODER")
public class ValidatorInterceptor implements PreProcessInterceptor, AcceptedByMethod {

	static private Logger logger = Logger.getLogger(ValidatorInterceptor.class);
	
	@Context
	private HttpServletRequest servletRequest;
	
	public boolean accept(Class c, Method m) {

		ToValidate validationAnnot = m.getAnnotation(ToValidate.class);
		if(validationAnnot == null){
			return false;
		}else{

			if(validationAnnot.typeName().equals("dataset")){
				return true;
			}else{
				return false;
			}

		}
		
	}
	/**
	 * Preprocess all the REST requests..
	 */
	public ServerResponse preProcess(HttpRequest req, ResourceMethod resourceMethod)throws Failure, WebApplicationException {

		// start server response as null and perform the validations, if it gets
		// some value, it will be a valid response and the interceptor will stop
		// the request
		ServerResponse response = null;
		try {
			FieldsValidatorFactory fvf = new FieldsValidatorFactory();
			
			IFieldsValidator validator = fvf.getValidator(resourceMethod.getMethod().getAnnotation(ToValidate.class).typeName());
			JSONArray errors = validator.validateFields(req.getFormParameters());

			if (errors != null && errors.length() !=0) {
				JSONObject errorMsg = new JSONObject();
				errorMsg.put("errors", errors);
				errorMsg.put("message", "validation-error");
				response = new ServerResponse(errorMsg.toString(), 400, new Headers<Object>());

			}

		} catch (Exception e) {
			response = new ServerResponse("error", 500, new Headers<Object>());
		}

		return response;
	}
	

}
