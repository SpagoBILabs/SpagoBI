/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

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
