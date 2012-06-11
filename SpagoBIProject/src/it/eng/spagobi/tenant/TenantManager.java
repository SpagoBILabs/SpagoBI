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
package it.eng.spagobi.tenant;


/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class TenantManager {

	private static final ThreadLocal<Tenant> _tenant = new ThreadLocal<Tenant>();

	public static void setTenant(Tenant tenant) {
		_tenant.set(tenant);
	}
	
	public static Tenant getTenant() {
		return _tenant.get();
	}
	
	public static void unset() {
		_tenant.remove();
	}
	
}
