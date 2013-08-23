/*
*
* @file BuilderUtils.java
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
* @author Arnd Houben
*
* @version $Id: BuilderUtils.java,v 1.2 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palojava.http.builders;

/**
 * <code></code>
 * TODO DOCUMENT ME
 *
 * @author Arnd Houben
 * @version $Id: BuilderUtils.java,v 1.2 2009/04/29 10:35:49 PhilippBouillon Exp $
 */
public class BuilderUtils {

	static final String[] getIDs(String idStr) {
		String[] ids = idStr.split(",");
		if(ids.length==1 && ids[0].equals(""))
			return new String[0];
		return ids;
	}

	static final double[] getWeights(String weightStr) {
		String[] weights = weightStr.split(",");
		if(weights.length == 1 && weights[0].equals(""))
			return new double[0];
		double[] w = new double[weights.length];
		for(int i=0;i<w.length;++i) {
			w[i] = Double.parseDouble(weights[i]);
		}
		return w;
	}

}
