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

package it.eng.spagobi.services.exportrecords.stub;

public class ExportRecordsServiceSoapBindingImpl implements it.eng.spagobi.services.exportrecords.stub.ExportRecordsService{
    public java.lang.String processRecords(java.lang.String in0, java.lang.String in1) throws java.rmi.RemoteException {
        System.out.println("******************");
        System.out.println("Service START");
        System.out.println("Records: " + in0);
        System.out.println("Operation: " + in1);
        System.out.println("Service END");
        System.out.println("******************");
    	return "Records processed properly";
    }

}
