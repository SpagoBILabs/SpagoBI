/*
*
* @file FolderWriter.java
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
* @author Philipp Bouillon
*
* @version $Id: FolderWriter.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.palo.api.exceptions.PaloIOException;

/**
 * <code>FolderWriter</code>
 * Writes a folder structure.
 *
 * @author Philipp Bouillon
 * @version $Id: FolderWriter.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
class FolderWriter {
	private static FolderWriter instance = new FolderWriter();
	public static final FolderWriter getInstance() {
		return instance;
	}

	private FolderWriter() {
	}

	final void toXML(OutputStream out, ExplorerTreeNode root) throws PaloIOException {
		try {
			toXMLInternal(out, root);
		} catch (Exception e) {
			PaloIOException pex = 
				new PaloIOException("Writing folder to xml failed!", e);
			pex.setData(root);
			throw pex;
		}
	}

	private final void toXMLInternal(OutputStream output, ExplorerTreeNode root)
			throws Exception {
		PrintWriter w = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(output, "UTF-8"))); //$NON-NLS-1$
		try {
			// preamble:
			w.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
			w.write("<?palofolder version=\"0.1\"?>\r\n");
			
			w.write("<folder>\r\n");
			if (root != null) {
				// folder element:
				w.write(root.getPersistenceString());
			}
			w.write("</folder>\r\n");
		} finally {
			w.close();
		}
	}
}
