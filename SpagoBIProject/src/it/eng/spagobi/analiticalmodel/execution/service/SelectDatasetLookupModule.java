/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.analiticalmodel.execution.service;

import it.eng.spago.base.SourceBean;
import it.eng.spago.dispatching.module.list.basic.AbstractBasicListModule;
import it.eng.spago.paginator.basic.ListIFace;
import it.eng.spagobi.commons.services.DelegatedHibernateConnectionListService;

import org.apache.log4j.Logger;

public class SelectDatasetLookupModule extends AbstractBasicListModule {

	static private Logger logger = Logger.getLogger(SelectDatasetLookupModule.class);

		/* (non-Javadoc)
		 * @see it.eng.spago.dispatching.service.list.basic.IFaceBasicListService#getList(it.eng.spago.base.SourceBean, it.eng.spago.base.SourceBean)
		 */
		public ListIFace getList(SourceBean request, SourceBean response) throws Exception {
			return DelegatedHibernateConnectionListService.getList(this, request, response);
		}
	
	}

