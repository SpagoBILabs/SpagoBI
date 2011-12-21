/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.commons.bo;

import java.io.Serializable;

/**
 * @author Gioia
 *
 */
public class Subreport implements Serializable{
	private Integer  master_rpt_id;
	private Integer  sub_rpt_id;
	
	/**
	 * Instantiates a new subreport.
	 */
	public Subreport() {}
	
	/**
	 * Instantiates a new subreport.
	 * 
	 * @param master_rpt_id the master_rpt_id
	 * @param sub_rpt_id the sub_rpt_id
	 */
	public Subreport(Integer master_rpt_id, Integer sub_rpt_id) {
		this.master_rpt_id = master_rpt_id;
		this.sub_rpt_id = sub_rpt_id;
	}
	
	/**
	 * Gets the master_rpt_id.
	 * 
	 * @return the master_rpt_id
	 */
	public Integer getMaster_rpt_id() {
		return master_rpt_id;
	}
	
	/**
	 * Sets the master_rpt_id.
	 * 
	 * @param master_rpt_id the new master_rpt_id
	 */
	public void setMaster_rpt_id(Integer master_rpt_id) {
		this.master_rpt_id = master_rpt_id;
	}
	
	/**
	 * Gets the sub_rpt_id.
	 * 
	 * @return the sub_rpt_id
	 */
	public Integer getSub_rpt_id() {
		return sub_rpt_id;
	}
	
	/**
	 * Sets the sub_rpt_id.
	 * 
	 * @param sub_rpt_id the new sub_rpt_id
	 */
	public void setSub_rpt_id(Integer sub_rpt_id) {
		this.sub_rpt_id = sub_rpt_id;
	}
}
