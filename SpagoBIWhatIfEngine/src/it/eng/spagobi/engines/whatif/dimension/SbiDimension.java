 /* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 */
package it.eng.spagobi.engines.whatif.dimension;

import it.eng.spagobi.engines.whatif.hierarchy.SbiHierarchy;

import java.util.ArrayList;
import java.util.List;

public class SbiDimension {

	private String name;
	private String uniqueName;
	private List<SbiHierarchy> hierarchies;
	private String selectedHierarchyUniqueName;
	private int selectedHierarchyPosition;
	private int axis;
	
	public SbiDimension(String name, String uniqueName,
			List<SbiHierarchy> hierarchies,
			String selectedHierarchyUniqueName, int axis,  int selectedHierarchyPosition) {
		super();
		this.name = name;
		this.uniqueName = uniqueName;
		this.hierarchies = hierarchies;
		this.axis = axis;
		this.selectedHierarchyUniqueName = selectedHierarchyUniqueName;
		this.selectedHierarchyPosition = selectedHierarchyPosition;
	}
	
	public SbiDimension(String name, String uniqueName, int axis) {
		super();
		this.name = name;
		this.uniqueName = uniqueName;
		this.axis = axis;
		this.hierarchies = new ArrayList<SbiHierarchy>();
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
	public List<SbiHierarchy> getHierarchies() {
		return hierarchies;
	}
	public void setHierarchy(List<SbiHierarchy> hierarchies) {
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

	public int getAxis() {
		return axis;
	}

	public void setAxis(int axis) {
		this.axis = axis;
	}

	
	
}
