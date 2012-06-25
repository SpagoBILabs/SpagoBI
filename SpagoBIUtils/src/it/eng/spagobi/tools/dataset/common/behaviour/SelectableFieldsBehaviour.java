/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.common.behaviour;

import it.eng.spagobi.tools.dataset.bo.IDataSet;

import java.util.List;

/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class SelectableFieldsBehaviour extends AbstractDataSetBehaviour {
	
	public static final String ID = SelectableFieldsBehaviour.class.getName();
	
	private List<String> fields = null;

	public SelectableFieldsBehaviour(IDataSet targetDataSet) {
		super(SelectableFieldsBehaviour.class.getName(), targetDataSet);
	}
	
	public List<String> getSelectedFields() {
		return fields;
	}

	public void setSelectedFields(List<String> fields) {
		this.fields = fields;
	}

}
