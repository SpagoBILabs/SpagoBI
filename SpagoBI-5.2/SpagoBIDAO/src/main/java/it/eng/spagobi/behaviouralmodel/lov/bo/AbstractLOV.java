/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.behaviouralmodel.lov.bo;

import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.objects.Couple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractLOV implements ILovDetail {

	@Override
	public boolean isSimpleLovType() {
		return this.getLovType() == null || this.getLovType().equalsIgnoreCase("simple");
	}

	protected List<String> getTreeValueColumns() {
		try {
			List<Couple<String, String>> list = this.getTreeLevelsColumns();
			List<String> toReturn = new ArrayList<String>();
			Iterator<Couple<String, String>> it = list.iterator();
			while (it.hasNext()) {
				toReturn.add(it.next().getFirst());
			}
			return toReturn;
		} catch (Exception e) {
			throw new SpagoBIRuntimeException("Error while getting tree value columns", e);
		}
	}

	protected List<String> getTreeDescriptionColumns() {
		try {
			List<Couple<String, String>> list = this.getTreeLevelsColumns();
			List<String> toReturn = new ArrayList<String>();
			Iterator<Couple<String, String>> it = list.iterator();
			while (it.hasNext()) {
				toReturn.add(it.next().getSecond());
			}
			return toReturn;
		} catch (Exception e) {
			throw new SpagoBIRuntimeException("Error while getting tree value columns", e);
		}
	}

}
