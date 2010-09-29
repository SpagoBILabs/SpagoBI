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
 * This class represents a hierarchy of Organizational Units, just the object, not the actual structure
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class OrganizationalUnitHierarchy {


    // Fields    

     private Integer id;
     private String label;
     private String name;
     private String description;
     private String target;


    // Constructors

    /** default constructor */
    public OrganizationalUnitHierarchy() {
    }

	/** minimal constructor */
    public OrganizationalUnitHierarchy(Integer id, String label,String name) {
        this.id = id;
        this.label = label;
        this.name = name;
    }
    
    /** full constructor */
    public OrganizationalUnitHierarchy(Integer id, String label, String name, String description, String target) {
        this.id = id;
        this.label = label;
        this.name = name;
        this.description = description;
        this.target = target;
    }
    

   
    // Property accessors

    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public String getTarget() {
        return this.target;
    }
    
    public void setTarget(String target) {
        this.target = target;
    }

}
