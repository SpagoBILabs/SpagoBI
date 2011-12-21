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
package it.eng.spagobi.container;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class SpagoBIContainerFactory {
	public static IContainer getContainer(Object o) {
		IContainer container;
		
		container = null;
		
		if(o instanceof ServletRequest) {
			container =  new SpagoBIServletRequestContainer( (ServletRequest)o );
		} else if(o instanceof HttpSession){
			container =  new SpagoBIHttpSessionContainer( (HttpSession)o );
		} else {
			throw new IllegalArgumentException("Impossible to build a container around an instance of [" + o.getClass().getName() + "]");
		}
		
		return container;
	}
}
