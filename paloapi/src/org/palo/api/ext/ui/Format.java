/*
*
* @file Format.java
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
* @version $Id: Format.java,v 1.3 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.ext.ui;

import java.text.DecimalFormat;


/**
 * <code>Format</code> contains general format settings to use for displaying
 * values and text
 * 
 * @author ArndHouben
 * @version $Id: Format.java,v 1.3 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
public interface Format {

	/**
	 * Returns the priority of this <code>Format</code>. If two 
	 * <code>Format</code>s are defined for the same object the priority can
	 * be used to decide which one to apply.
	 * @return the format priority
	 */
	public int getPriority();



	/**
	 * Returns the format pattern to use for displaying a numeric value.
	 * Usually this pattern can be used to initialize an instance of {@link DecimalFormat} 
	 * @return number format pattern for numeric values
	 */
	public String getNumberFormatPattern();
	/**
	 * Returns the <code>ColorDescriptor</code> instance which describes the 
	 * background color to use
	 * @return an <code>ColorDescriptor</code> instance 
	 */
	public ColorDescriptor getBackGroundColor();
	
	/**
	 * Returns the <code>FontDescriptor</code> instance to use for displaying 
	 * text and values
	 * @return a <code>FontDescriptor</code> instance
	 */
	public FontDescriptor getFont();
	
	/**
	 * Returns the <code>ColorDescriptor</code>instance which describes the 
	 * font color to use
	 * @return an <code>ColorDescriptor</code> instance 
	 */	
	public ColorDescriptor getFontColor();
}
