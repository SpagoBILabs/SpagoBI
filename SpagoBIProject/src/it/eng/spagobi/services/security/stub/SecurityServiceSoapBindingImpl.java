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

package it.eng.spagobi.services.security.stub;

import it.eng.spagobi.services.security.service.SecurityServiceImpl;

public class SecurityServiceSoapBindingImpl implements it.eng.spagobi.services.security.stub.SecurityService{
    public it.eng.spagobi.services.security.bo.SpagoBIUserProfile getUserProfile(java.lang.String in0, java.lang.String in1) throws java.rmi.RemoteException {
	SecurityServiceImpl impl=new SecurityServiceImpl();
	return impl.getUserProfile(in0,in1);
    }

    public boolean isAuthorized(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3) throws java.rmi.RemoteException {
	SecurityServiceImpl impl=new SecurityServiceImpl();
	return impl.isAuthorized(in0,in1,in2,in3);
    }

    public boolean checkAuthorization(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException {
	SecurityServiceImpl impl=new SecurityServiceImpl();
	return impl.checkAuthorization(in0,in1,in2);
    }

}
