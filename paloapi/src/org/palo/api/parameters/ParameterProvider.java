/*
*
* @file ParameterProvider.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author PhilippBouillon
*
* @version $Id: ParameterProvider.java,v 1.2 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008.
 * All rights reserved.
 */
package org.palo.api.parameters;

/**
 * A <code>ParameterProvider</code> can be used to transport arbitrary
 * parameters to any <code>ParameterReceiver</code>. How these parameters are
 * generated is up to the specific implementation of the provider. For an
 * example, have a look at the <link>org.palo.viewapi.uimodels.folders</link>
 * package.
 * 
 * @author PhilippBouillon
 * @version $Id: ParameterProvider.java,v 1.2 2009/04/29 10:21:58 PhilippBouillon Exp $
 */
public interface ParameterProvider {
	/**
	 * Returns the parameter receiver that is listening to this provider.
	 * TODO what about multiple receivers? 
	 * @return the parameter receiver that is listening to this provider.
	 */
	ParameterReceiver getSourceObject();
	
	/**
	 * Sets the parameter receiver for this provider.
	 * @param sourceObject the new parameter receiver.
	 */
	void setSourceObject(ParameterReceiver sourceObject);
	
	/**
	 * Returns all parameter names sent by this provider.
	 * @return all parameter names sent by this provider.
	 */
	String [] getParameterNames();
	
	/**
	 * Sets all parameter names that can be sent by this provider.
	 * @param parameterNames the new set of parameter names understood by this
	 * provider.
	 */
	void setParameterNames(String [] parameterNames);
	
	/**
	 * Returns all possible values for the given parameter name.
	 * @param parameterName the parameter name for which all possible values are
	 * to be returned.
	 * @return all possible values for the given parameter name. 
	 */
	Object [] getPossibleValuesFor(String parameterName);
	
	/**
	 * Sets all possible values for the given parameter name.
	 * @param parameterName the parameter name for which all possible values
	 * are to be set. 
	 * @param possibleValues the possible values for the given parameter.
	 */
	void setPossibleValuesFor(String parameterName, Object [] possibleValues);	
}
