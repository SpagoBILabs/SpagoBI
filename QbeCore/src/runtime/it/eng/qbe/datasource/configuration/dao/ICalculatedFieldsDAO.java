/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.qbe.datasource.configuration.dao;

import it.eng.qbe.model.structure.ModelCalculatedField;

import java.util.List;
import java.util.Map;

/**
 * @author Andrea Gioia
 */
public interface ICalculatedFieldsDAO {	
	
	Map<String, List<ModelCalculatedField>> loadCalculatedFields();		
	void saveCalculatedFields(Map<String, List<ModelCalculatedField>> calculatedFields);
}
