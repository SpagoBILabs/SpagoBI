/*
*
* @file ParameterHandler.java
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
* @author ArndHouben
*
* @version $Id: ParameterHandler.java,v 1.3 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.io.xml;

import org.palo.api.impl.xml.XMLUtil;
import org.palo.api.subsets.filter.settings.Parameter;

/**
 * <code>ParameterHandler</code>
 * <p>
 * Defines helper methods to store {@link Parameter} instances to xml.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: ParameterHandler.java,v 1.3 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
class ParameterHandler {

	/**
	 * Returns the xml string for given parameter. Note that the parameter
	 * value is transformed into its string representation and that it is 
	 * neither quoted nor encoded. 
	 * @param param
	 * @return
	 */
	final static String getXML(Parameter param) {
		StringBuffer buff = new StringBuffer();
		addParameter(param, buff);
		buff.append("<value>");
		buff.append(param.getValue().toString());
		buff.append("</value>\r\n");
		return buff.toString();
	}

	/**
	 * Returns the xml string for given parameter. <code>null</code> values 
	 * are represented by an empty string. 
	 * @param param
	 * @param quoteValue specify <code>true</code> to quote the parameter value,
	 * <code>false</code> otherwise
	 * @return
	 */
	final static String getXML(Parameter param, boolean quoteValue) {
		StringBuffer buff = new StringBuffer();
		addParameter(param, buff);
		buff.append("<value>");
		Object value = param.getValue();
		String strValue = value != null ? value.toString() : "";
		if(quoteValue)
			buff.append(XMLUtil.printQuoted(strValue));
		else
			buff.append(strValue);
		buff.append("</value>\r\n");
		return buff.toString();
	}
//	/**
//	 * Returns the xml string for a given {@link Parameter} which value 
//	 * should be quoted by {@link XMLUtil#quote(param.getValue())}. 
//	 * @param param
//	 * @return
//	 */
//	final static String getQuotedXML(Parameter param) {
//		StringBuffer buff = new StringBuffer();
//		addParameter(param, buff);
//		buff.append("<value>");
//		buff.append(XMLUtil.quote(param.getValue()));
//		buff.append("</value>\r\n");
//		return buff.toString();
//	}
	
	/**
	 * Adds the <code>parameter</code>-tag to the specified 
	 * <code>StringBuffer</code> if the given parameter has a name. If the 
	 * name of the parameter is not defined, then this method has no effect.  
	 */
	final static void addParameter(Parameter param, StringBuffer buff) {
		if(param.getName()!=null) {
			buff.append("<parameter>");
			buff.append(param.getName());
			buff.append("</parameter>\r\n");
		}
	}
	
}
