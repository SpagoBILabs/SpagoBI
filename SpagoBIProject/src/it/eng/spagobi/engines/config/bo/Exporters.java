/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.config.bo;

import java.io.Serializable;

public class Exporters implements Serializable{


		Integer domainId;
		Integer engineId;
		boolean defaultValue;
		
		
		public Integer getDomainId() {
			return domainId;
		}
		public void setDomainId(Integer domainId) {
			this.domainId = domainId;
		}
		public Integer getEngineId() {
			return engineId;
		}
		public void setEngineId(Integer engineId) {
			this.engineId = engineId;
		}
		public boolean isDefaultValue() {
			return defaultValue;
		}
		public void setDefaultValue(boolean defaultValue) {
			this.defaultValue = defaultValue;
		}
		
		

		


}
