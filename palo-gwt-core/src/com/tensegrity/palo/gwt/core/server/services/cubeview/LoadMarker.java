/*
*
* @file LoadMarker.java
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
* @version $Id: LoadMarker.java,v 1.5 2010/02/12 13:50:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview;

import java.util.HashSet;
import java.util.Set;

/**
 * <code>LoadedCellsMarker</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: LoadMarker.java,v 1.5 2010/02/12 13:50:49 PhilippBouillon Exp $
 **/
class LoadMarker {

	private int rowCount;
	private int columnCount;
	//should be large enough:
	private Set<Long> loaded = new HashSet<Long>();
	
	public final void clear() {
		rowCount = 0;
		columnCount = 0;
		loaded.clear();
	}
	
	public final int getLoadLevel() {
		return loaded.size();
	}
	public final int getRowCount() {
		return rowCount;
	}
	public final int getColumnCount() {
		return columnCount;
	}
	
	public final boolean isLoaded(int row, int column) {
		long index = getIndex(row, column);
		return loaded.contains(index);
	}
	
	private final long getIndex(int row, int column) {
		return row * columnCount + column;
	}
	
	public final void loaded(int row, int column, boolean itIs) {
		long index = getIndex(row, column);
		if(itIs)
			loaded.add(index);
		else
			loaded.remove(index);
	}

	public final void insertRows(int atIndex, int rows) {
		int newRowCount = rowCount + rows;
		Set<Long> newLoaded = new HashSet<Long>();
		// transform old marks:
		for (int r = 0; r < rowCount; ++r) {
			for (int c = 0; c < columnCount; ++c) {
				if(isLoaded(r, c)) {
					// new index:
					int row = r < atIndex ? r : r + rows;
					long newIndex = row * columnCount + c;
					newLoaded.add(newIndex);					
				}
			}
		}
		loaded = newLoaded;
		rowCount = newRowCount;
//		print();
	}
	
	public final void removeRows(int atIndex, int rows) {
		int newRowCount = rowCount - rows;
		Set<Long> newLoaded = new HashSet<Long>();
		//transform old marks:
		int tail = atIndex + rows;
		for(int r = 0; r < rowCount; ++r) {
			for(int c = 0; c < columnCount; ++c) {
				if(r>= atIndex && r < tail)
					continue;
				if(isLoaded(r, c)) {
					//new index:
					int row = r < tail ? r : r - rows;
					long newIndex = row * columnCount + c;
					newLoaded.add(newIndex);				
				}
			}
		}
		loaded = newLoaded;
		rowCount = newRowCount;
//		print();
	}

	public final void insertColumns(int atIndex, int columns) {
		int newColumnCount = columnCount + columns;
		Set<Long> newLoaded = new HashSet<Long>();
		// transform old marks:
		for (int r = 0; r < rowCount; ++r) {
			for (int c = 0; c < columnCount; ++c) {
				if (isLoaded(r, c)) {
					// new index:
					int col = c < atIndex ? c : c + columns;
					long newIndex = r * newColumnCount + col;
					newLoaded.add(newIndex);
				}
			}
		}
		loaded = newLoaded;
		columnCount = newColumnCount;
//		print();
	}
	public final void removeColumns(int atIndex, int columns) {
		int newColumnCount = columnCount - columns;
		Set<Long> newLoaded = new HashSet<Long>();
		// transform old marks:
		int tail = atIndex + columns;
		for (int r = 0; r < rowCount; ++r) {
			for (int c = 0; c < columnCount; ++c) {
				if (c >= atIndex && c < tail)
					continue;
				if (isLoaded(r, c)) {
					// new index
					int col = c < tail ? c : c - columns;
					long newIndex = r * newColumnCount + col;
					newLoaded.add(newIndex);
				}
			}
		}
		loaded = newLoaded;
		columnCount = newColumnCount;
//		print();
	}
}

