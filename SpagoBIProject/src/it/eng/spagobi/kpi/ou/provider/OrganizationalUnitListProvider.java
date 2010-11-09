/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.kpi.ou.provider;

import it.eng.spagobi.kpi.ou.bo.OrganizationalUnit;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitHierarchy;
import it.eng.spagobi.utilities.tree.Tree;

import java.util.List;

/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public abstract class OrganizationalUnitListProvider {

	public abstract void initialize();
	
	public abstract List<OrganizationalUnit> getOrganizationalUnits();
	
	public abstract List<OrganizationalUnitHierarchy> getHierarchies();
	
	public abstract Tree<OrganizationalUnit> getHierarchyStructure(OrganizationalUnitHierarchy hierarchy);
	
}
