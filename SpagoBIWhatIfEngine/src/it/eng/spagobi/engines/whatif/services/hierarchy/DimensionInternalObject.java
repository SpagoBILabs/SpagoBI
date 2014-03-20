 /* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 */
package it.eng.spagobi.engines.whatif.services.hierarchy;

import java.util.ArrayList;
import java.util.List;

public class DimensionInternalObject {

	private String name;
	private String uniqueName;
	private List<HierarchyInternalObject> hierarchies;
	private String selectedHierarchyUniqueName;
	private int selectedHierarchyPosition;
	
	
	public DimensionInternalObject(String name, String uniqueName,
			List<HierarchyInternalObject> hierarchies,
			String selectedHierarchyUniqueName, int selectedHierarchyPosition) {
		super();
		this.name = name;
		this.uniqueName = uniqueName;
		this.hierarchies = hierarchies;
		this.selectedHierarchyUniqueName = selectedHierarchyUniqueName;
		this.selectedHierarchyPosition = selectedHierarchyPosition;
	}
	
	public DimensionInternalObject(String name, String uniqueName) {
		super();
		this.name = name;
		this.uniqueName = uniqueName;
		this.hierarchies = new ArrayList<HierarchyInternalObject>();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUniqueName() {
		return uniqueName;
	}
	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}
	public List<HierarchyInternalObject> getHierarchies() {
		return hierarchies;
	}
	public void setHierarchy(List<HierarchyInternalObject> hierarchies) {
		this.hierarchies = hierarchies;
	}
	public String getSelectedHierarchyUniqueName() {
		return selectedHierarchyUniqueName;
	}
	public void setSelectedHierarchyUniqueName(String selectedHierarchyUniqueName) {
		this.selectedHierarchyUniqueName = selectedHierarchyUniqueName;
	}
	public int getSelectedHierarchyPosition() {
		return selectedHierarchyPosition;
	}
	public void setSelectedHierarchyPosition(int selectedHierarchyPosition) {
		this.selectedHierarchyPosition = selectedHierarchyPosition;
	}

	
	
}
