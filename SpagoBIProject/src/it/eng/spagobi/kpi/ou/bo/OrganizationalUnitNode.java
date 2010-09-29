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
package it.eng.spagobi.kpi.ou.bo;

/**
 * This class represents a node of a hierarchy of Organizational Units
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class OrganizationalUnitNode {

    // Fields    

     private Integer nodeId;
     private OrganizationalUnit ou;
     private OrganizationalUnitHierarchy hierarchy;
     private Integer parentNodeId;
     private String path;


    // Constructors

    /** default constructor */
    public OrganizationalUnitNode() {
    }

    public OrganizationalUnitNode(Integer nodeId, OrganizationalUnit ou, OrganizationalUnitHierarchy hierarchy, String path, Integer parentNodeId) {
        this.nodeId = nodeId;
        this.ou = ou;
        this.hierarchy = hierarchy;
        this.path = path;
        this.parentNodeId = parentNodeId;
    }

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}

	public OrganizationalUnit getOu() {
		return ou;
	}

	public void setOu(OrganizationalUnit ou) {
		this.ou = ou;
	}

	public OrganizationalUnitHierarchy getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(OrganizationalUnitHierarchy hierarchy) {
		this.hierarchy = hierarchy;
	}

	public Integer getParentNodeId() {
		return parentNodeId;
	}

	public void setParentNodeId(Integer parentNodeId) {
		this.parentNodeId = parentNodeId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
