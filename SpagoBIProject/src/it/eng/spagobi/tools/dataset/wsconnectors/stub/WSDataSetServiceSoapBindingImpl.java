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

package it.eng.spagobi.tools.dataset.wsconnectors.stub;

public class WSDataSetServiceSoapBindingImpl implements it.eng.spagobi.tools.dataset.wsconnectors.stub.IWsConnector{
    public java.lang.String readDataSet(java.lang.String in0, java.util.Map in1, java.lang.String in2) throws java.rmi.RemoteException {
        if(in2.equalsIgnoreCase("a"))
    	return "<ROWS><ROW name='io' value='30'/></ROWS>";
        if(in2.equalsIgnoreCase("b"))
        	return "<ROWS><ROW name='io' value='80'/></ROWS>";
        return null;
    }

}
	