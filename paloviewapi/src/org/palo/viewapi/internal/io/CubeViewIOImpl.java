/*
*
* @file CubeViewIOImpl.java
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
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: CubeViewIOImpl.java,v 1.6 2010/01/13 08:02:42 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.palo.api.Cube;
import org.palo.api.exceptions.PaloIOException;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.View;

/**
 * <code>CubeViewIOImpl</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: CubeViewIOImpl.java,v 1.6 2010/01/13 08:02:42 PhilippBouillon Exp $
 **/
class CubeViewIOImpl extends CubeViewIO {

	protected final CubeView viewFromXML(AuthUser user, View view, Cube cube, String xml,
			CubeViewReader reader) throws PaloIOException {
//		if(xml == null) //create a simple default view if no xml is given...
//			return viewHandler.createDefaultView(viewName, CubeView.TYPE_GLOBAL);
//		
		CubeView cView = null;
		try {
			//System.out.print(xml);
			ByteArrayInputStream bin = new ByteArrayInputStream(xml
					.getBytes("UTF-8")); //$NON-NLS-1$
			try {
				cView = reader.fromXML(user, view, cube, bin);
			} finally {
				bin.close();
			}
		} catch (PaloIOException e) {
			throw e;
		} catch (Exception e) {
			/* ignore since we return null in this case... */
		}
		return cView;
	}

	protected final String toXML(CubeView view, CubeViewWriter writer) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try {
				writer.toXML(bout, view);
				return bout.toString("UTF-8");
			} finally {
				bout.close();
			}
		} catch (Exception e) {
			/* ignore */
		}
		return null;
	}

	
}
