/*
*
* @file ParameterReceiver.java
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
* @version $Id: ParameterReceiver.java,v 1.2 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008.
 * All rights reserved.
 */
package org.palo.api.parameters;

/**
 * A <code>ParameterReceiver</code> can be used to receive arbitrary
 * parameters from any <code>ParameterProvider</code>. How these parameters are
 * interpreted is up to the specific implementation of the receiver. For an
 * example, have a look at the <link>org.palo.viewapi.uimodels.folders</link>
 * package.
 * 
 * @author PhilippBouillon
 * @version $Id: ParameterReceiver.java,v 1.2 2009/04/29 10:21:58 PhilippBouillon Exp $
 */
public interface ParameterReceiver {
	/**
	 * Returns all parameter names understood by this receiver.
	 * @return all parameter names understood by this receiver.
	 */
	String [] getParameterNames();

	/**
	 * Sets all parameter names that can be interpreted by this receiver.
	 * @param parameterNames the new set of parameter names understood by this
	 * receiver.
	 */	
	void setParameterNames(String [] parameterNames);
	
	/**
	 * Returns the value for the given parameter or null, if no such parameter
	 * is set.
	 * @param parameterName the name of the parameter of which the value is to
	 * be returned.
	 * @return the value for the given parameter or null.
	 */
	Object getParameterValue(String parameterName);
	
	/**
	 * Returns the default value for the given parameter or null, if no such
	 * parameter exists.
	 * @param parameterName the name of the parameter of which the default value
	 * is to be returned.
	 * @return the default value for the given parameter or null.
	 */
	Object getDefaultValue(String parameterName);
	
	/**
	 * Sets the specified parameter to the specified value.
	 * 
	 * @param parameterName the name of the parameter to set.
	 * @param parameterValue the value of the parameter to set.
	 */
	void setParameter(String parameterName, Object parameterValue);
	
	/**
	 * Adds the specified value to the current value of the specified parameter;
	 *  
	 * @param parameterName the name of the parameter to set.
	 * @param parameterValue the value of the parameter to set.
	 */
	void addParameterValue(String parameterName, Object parameterValue);
	
	/**
	 * Returns true if the current instance of the object is parameterized,
	 * false otherwise.
	 * @return true if the object is parameterized, false otherwise.
	 */
	boolean isParameterized();
}
