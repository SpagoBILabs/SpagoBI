/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata;

import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjPar;


public class SbiObjParuseId  implements java.io.Serializable {

    // Fields    
     private SbiParuse sbiParuse;
     private SbiObjPar sbiObjPar;
     private SbiObjPar sbiObjParFather;
     private String filterOperation;


    // Constructors
    /**
     * default constructor.
     */
    public SbiObjParuseId() {
    }
    

    // Getter and Setter
    /**
     * Gets the filter operation.
     * 
     * @return the filter operation
     */
    public String getFilterOperation() {
    	return filterOperation;
    }

    /**
     * Sets the filter operation.
     * 
     * @param filterOperation the new filter operation
     */
    public void setFilterOperation(String filterOperation) {
    	this.filterOperation = filterOperation;
    }

    /**
     * Gets the sbi obj par.
     * 
     * @return the sbi obj par
     */
    public SbiObjPar getSbiObjPar() {
    	return sbiObjPar;
    }

    /**
     * Sets the sbi obj par.
     * 
     * @param sbiObjPar the new sbi obj par
     */
    public void setSbiObjPar(SbiObjPar sbiObjPar) {
    	this.sbiObjPar = sbiObjPar;
    }

    /**
     * Gets the sbi obj par father.
     * 
     * @return the sbi obj par father
     */
    public SbiObjPar getSbiObjParFather() {
    	return sbiObjParFather;
    }

    /**
     * Sets the sbi obj par father.
     * 
     * @param sbiObjParFather the new sbi obj par father
     */
    public void setSbiObjParFather(SbiObjPar sbiObjParFather) {
    	this.sbiObjParFather = sbiObjParFather;
    }

    /**
     * Gets the sbi paruse.
     * 
     * @return the sbi paruse
     */
    public SbiParuse getSbiParuse() {
    	return sbiParuse;
    }

    /**
     * Sets the sbi paruse.
     * 
     * @param sbiParuse the new sbi paruse
     */
    public void setSbiParuse(SbiParuse sbiParuse) {
    	this.sbiParuse = sbiParuse;
    }   
    
    
    // hashcode generator
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.getSbiParuse().hashCode();
        result = 37 * result + this.getSbiObjPar().hashCode();
        result = 37 * result + this.getSbiObjParFather().hashCode();
        result = 37 * result + this.getFilterOperation().hashCode();
        return result;
    }
    
    // override equals method
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof SbiObjParuseId) ) return false;
		 SbiObjParuseId castOther = ( SbiObjParuseId ) other; 
         return (this.getSbiParuse()==castOther.getSbiParuse()) || 
                (this.getSbiParuse()!=null && castOther.getSbiParuse()!=null && this.getSbiParuse().equals(castOther.getSbiParuse()) ) &&
                (this.getSbiObjPar()==castOther.getSbiObjPar()) || 
                (this.getSbiObjPar()!=null && castOther.getSbiObjPar()!=null && this.getSbiObjPar().equals(castOther.getSbiObjPar()) &&
                (this.getSbiObjParFather()==castOther.getSbiObjParFather()) || 
                (this.getSbiObjParFather()!=null && castOther.getSbiObjParFather()!=null && this.getSbiObjParFather().equals(castOther.getSbiObjParFather()) ) &&
                (this.getFilterOperation()==castOther.getFilterOperation()) || 
                (this.getFilterOperation()!=null && castOther.getFilterOperation()!=null && this.getFilterOperation().equals(castOther.getFilterOperation()) ) );	
   }
   
   









}