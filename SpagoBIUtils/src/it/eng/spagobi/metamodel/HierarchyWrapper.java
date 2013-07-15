 /* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.metamodel;

import it.eng.spagobi.meta.model.olap.Hierarchy;

/**
 * 
 * This class wraps a it.eng.spagobi.meta.model.olap.Hierarchy
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */

public class HierarchyWrapper{
	private  it.eng.spagobi.meta.model.olap.Hierarchy wrappedHierarchy;
	
	
	public HierarchyWrapper( Hierarchy wrappedHierarchy){
		this.wrappedHierarchy = wrappedHierarchy;
	}
	
	public String getName(){
		return wrappedHierarchy.getName();
	}


}
