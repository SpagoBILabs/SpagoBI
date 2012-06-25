/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.jpivotaddins.analysis;

import it.eng.spagobi.jpivotaddins.bean.SaveAnalysisBean;

import com.tonbeller.wcf.component.Component;
import com.tonbeller.wcf.component.ComponentTag;
import com.tonbeller.wcf.controller.RequestContext;

public class SaveAnalysisTag extends ComponentTag {

  /**
   * creates a Print Component
   */
  public Component createComponent(RequestContext context) throws Exception {
	return new SaveAnalysisBean(id, context);
  }
  
}
