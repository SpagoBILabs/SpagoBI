/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.whatif.template;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.utilities.assertion.Assert;

import org.apache.log4j.Logger;


/**
 * @author Zerbetto Davide (davide.zerbetto@eng.it)
 */
public class WhatIfXMLTemplateParser implements IWhatIfTemplateParser {
	
	public static String TAG_ROOT = "OLAP";
	public static String TAG_CUBE = "CUBE";
	public static String TAG_MDX_QUERY = "MDXquery";
	public static String TAG_MDX_MONDRIAN_QUERY = "MDXMondrianQuery";
	public static String PROP_SCHEMA_REFERENCE = "reference";

	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(WhatIfXMLTemplateParser.class);
	
    public WhatIfTemplate parse(Object template) {
    	Assert.assertNotNull(template, "Input parameter [template] cannot be null");
    	Assert.assertTrue(template instanceof SourceBean, "Input parameter [template] cannot be of type [" + template.getClass().getName() + "]");
    	return parse((SourceBean)template);
    }
    
	private WhatIfTemplate parse(SourceBean template) {
		
		WhatIfTemplate toReturn = null;
		
		try {
			logger.debug("Starting template parsing....");
			
			toReturn = new WhatIfTemplate();
			
			SourceBean cubeSB = (SourceBean) template.getAttribute( TAG_CUBE );
			logger.debug(TAG_CUBE + ": " + cubeSB);
			Assert.assertNotNull(cubeSB, "Template is missing " + TAG_CUBE + " tag");
			String reference = (String) cubeSB.getAttribute(PROP_SCHEMA_REFERENCE);
			logger.debug(PROP_SCHEMA_REFERENCE + ": " + reference);
			toReturn.setMondrianSchema(reference);
			
			SourceBean mdxSB = (SourceBean) template.getAttribute( TAG_MDX_QUERY );
			logger.debug(TAG_MDX_QUERY + ": " + mdxSB);
			Assert.assertNotNull(mdxSB, "Template is missing " + TAG_MDX_QUERY + " tag");
			String mdxQuery = mdxSB.getCharacters();
			toReturn.setMdxQuery(mdxQuery);
			
			SourceBean mdxMondrianSB = (SourceBean) template.getAttribute( TAG_MDX_MONDRIAN_QUERY );
			logger.debug(TAG_MDX_MONDRIAN_QUERY + ": " + mdxMondrianSB);
			//Assert.assertNotNull(mdxMondrianSB, "Template is missing " + TAG_MDX_MONDRIAN_QUERY + " tag");
			String mdxMondrianQuery = mdxMondrianSB.getCharacters();
			toReturn.setMondrianMdxQuery(mdxMondrianQuery);
			
			logger.debug("Templete parsed succesfully");
		} catch(Throwable t) {
			throw new WhatIfTemplateParseException("Impossible to parse template [" + template.toString()+ "]", t);
		} finally {
			logger.debug("OUT");
		}	
		
		return toReturn;
	}
}
