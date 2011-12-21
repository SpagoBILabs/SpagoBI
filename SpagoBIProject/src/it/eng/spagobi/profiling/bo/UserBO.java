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
package it.eng.spagobi.profiling.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class UserBO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5176913503788924840L;
	
	private int id;
	private String userId;
	private String password;
	private String fullName;
	private Date dtPwdBegin;
	private Date dtPwdEnd;
	private Boolean flgPwdBlocked;
	private Date dtLastAccess;
	
	private List sbiExtUserRoleses = new ArrayList();
	private HashMap<Integer, HashMap<String, String>> sbiUserAttributeses = new HashMap<Integer, HashMap<String,String>>();
	

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public Date getDtPwdBegin() {
		return dtPwdBegin;
	}
	public void setDtPwdBegin(Date dtPwdBegin) {
		this.dtPwdBegin = dtPwdBegin;
	}
	public Date getDtPwdEnd() {
		return dtPwdEnd;
	}
	public void setDtPwdEnd(Date dtPwdEnd) {
		this.dtPwdEnd = dtPwdEnd;
	}
	public Boolean getFlgPwdBlocked() {
		return flgPwdBlocked;
	}
	public void setFlgPwdBlocked(Boolean flgPwdBlocked) {
		this.flgPwdBlocked = flgPwdBlocked;
	}
	public Date getDtLastAccess() {
		return dtLastAccess;
	}
	public void setDtLastAccess(Date dtLastAccess) {
		this.dtLastAccess = dtLastAccess;
	}
	public List getSbiExtUserRoleses() {
		return sbiExtUserRoleses;
	}
	public void setSbiExtUserRoleses(List sbiExtUserRoleses) {
		this.sbiExtUserRoleses = sbiExtUserRoleses;
	}
	public HashMap<Integer, HashMap<String, String>> getSbiUserAttributeses() {
		return sbiUserAttributeses;
	}
	public void setSbiUserAttributeses(
			HashMap<Integer, HashMap<String, String>> sbiUserAttributeses) {
		this.sbiUserAttributeses = sbiUserAttributeses;
	}


}
