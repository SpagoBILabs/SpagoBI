/*
*
* @file ColorDescriptor.java
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
* @version $Id: ColorDescriptor.java,v 1.2 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.ext.ui;


/**
 * A <code>ColorDescriptor</code> is a platform-independent color description  
 *
 * @author ArndHouben
 * @version $Id: ColorDescriptor.java,v 1.2 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
public class ColorDescriptor {

	// the color parts:
	private int red, green, blue;
	
	/**
	 * Creates a new <code>ColorDescriptor</code> instance with the given color
	 * parts values
	 * @param red the red fraction
	 * @param green the green fraction
	 * @param blue the blue fraction
	 */
	public ColorDescriptor(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}


	/**
	 * Returns the blue fraction of specified color
	 * @return blue fraction
	 */
	public final int getBlue() {
		return blue;
	}


	/**
	 * Sets the blue fraction of specified color 
	 * @param blue blue color fraction
	 */
	public final void setBlue(int blue) {
		this.blue = blue;
	}

	/**
	 * Returns the green fraction of specified color
	 * @return green fraction
	 */
	public final int getGreen() {
		return green;
	}

	/**
	 * Sets the green color fraction
	 * @param green green color fraction
	 */
	public final void setGreen(int green) {
		this.green = green;
	}

	/**
	 * Returns the red fraction of specified color
	 * @return red fraction
	 */
	public final int getRed() {
		return red;
	}

	/**
	 * Sets the red color fraction
	 * @param red red color fraction
	 */
	public final void setRed(int red) {
		this.red = red;
	}
	
	
	public boolean equals(Object obj) {
		if (obj instanceof ColorDescriptor) {
			ColorDescriptor other = (ColorDescriptor) obj;
			return ((red == other.red) 
					&& (green == other.green) 
					&& (blue == other.blue));
		}
		return false;
	}

	public int hashCode() {
		int hc = 17;
		hc += 37 * blue;
		hc += 37 * green;
		hc += 37 * red;
		return hc;
	}

	public String toString() {
		return "Color [" + red + ", " + green + ", " + blue + "]"; 
	}
	
}
