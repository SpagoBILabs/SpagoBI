/*
*
* @file SubsetIOHandler.java
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
* @version $Id: SubsetIOHandler.java,v 1.26 2009/12/16 12:33:25 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.palo.api.Connection;
import org.palo.api.Cube;
import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.Subset;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.subsets.Subset2;
import org.palo.api.subsets.SubsetFilter;
import org.palo.api.subsets.impl.SubsetHandlerImpl;
import org.palo.api.subsets.impl.SubsetStorageHandlerImpl;

/**
 * <p><b>- API INTERNAL -</b></p>
 * This class handles the raw access to the palo server subset cubes and 
 * hierarchys.
 * 
 * @author ArndHouben
 * @version $Id: SubsetIOHandler.java,v 1.26 2009/12/16 12:33:25 PhilippBouillon Exp $
 **/
public class SubsetIOHandler extends SubsetStorageHandlerImpl {

	//the newly provided hierarchys and cubes for storing subsets:
	private final static String SUBSET_DIM = "#_SUBSET_";	
	private final static String SUBSET_USER_DIM = "#_USER_";
	private final static String SUBSET_DIMENSION_DIM = "#_DIMENSION_";
	private final static String SUBSET_CUBE_LOCAL = "#_SUBSET_LOCAL";	
	private final static String SUBSET_CUBE_GLOBAL = "#_SUBSET_GLOBAL";	
	
	//-- cache --
	private final HashMap<Hierarchy, HashMap<String, SubsetCell>> localCells;
	private final HashMap<Hierarchy, HashMap<String, SubsetCell>> globalCells;

	private final Database database;

	
	public SubsetIOHandler(Database database) {
		this.database = database;
		this.localCells = new LinkedHashMap<Hierarchy, HashMap<String,SubsetCell>>();
		this.globalCells = new LinkedHashMap<Hierarchy, HashMap<String,SubsetCell>>();
	}

	/**
	 * Returns <code>true</code> if this hierarchy is a palo server native 
	 * subset hierarchy, <code>false</code> otherwise.
	 * @param hierarchy
	 * @return
	 */
	public static final boolean isSubsetHierarchy(Hierarchy hierarchy) {
		return hierarchy != null && hierarchy.getName().equals(SUBSET_DIM);
	}

	public static final boolean isSubsetDimension(Dimension dimension) {
		return dimension != null && dimension.getName().equals(SUBSET_DIM);
	}
	
	/**
	 * Returns <code>true</code> if new subsets, i.e. <code>Subset2</code>, are
	 * supported by the given database
	 * @param database
	 * @return
	 */
	public static final boolean supportsNewSubsets(Database database) {
		if (database.getConnection().getType() == Connection.TYPE_XMLA) {
			return false;
		}
		return database.getCubeByName(SUBSET_CUBE_LOCAL) != null
				&& database.getCubeByName(SUBSET_CUBE_GLOBAL) != null;
	}
	
	public final boolean canRead(int type) {		
		Cube cube = getSubsetCube(type);
		return cube != null && database.getRights().mayRead(cube);
	}

	public final boolean canWrite(int type) {
		Cube cube = getSubsetCube(type);
		return cube != null && database.getRights().mayWrite(cube);
	}

	public final void reset() {
		//clear cache:
		localCells.clear();
		globalCells.clear();
	}

	public final void convert(Subset[] legacySubsets, int type, boolean remove) {
		SubsetConverter transformer = new SubsetConverter();
		try {
			transformer.convert(legacySubsets, type, remove);
		} catch (PaloIOException e) {
			throw new PaloAPIException(
					"Errors during transform of legacy subsets!", e);
		}
	}
	
	protected final String getSubsetId(Hierarchy hierarchy, String name, int type) {
		String matchingId = null;
		HashMap<String, SubsetCell> cache = getCache(hierarchy, type);
		Hierarchy subsetHierarchy = database.getDimensionByName(SUBSET_DIM).
			getDefaultHierarchy();
		for(String id : cache.keySet() ) {
			Element subset = subsetHierarchy.getElementById(id);
			if(subset.getName().equalsIgnoreCase(name)) {
				matchingId = id;
				break;
			}				
		}
		return matchingId;
	}
	
	protected String[] getSubsetIDs(Hierarchy hierarchy) {
		ArrayList<String> subsetIDs = new ArrayList<String>();
		HashMap<String, SubsetCell> cache = 
				getCache(hierarchy, Subset2.TYPE_GLOBAL);
		subsetIDs.addAll(cache.keySet());
		cache = getCache(hierarchy, Subset2.TYPE_LOCAL);
		subsetIDs.addAll(cache.keySet());
		return subsetIDs.toArray(new String[subsetIDs.size()]);
	}

	protected String[] getSubsetIDs(Hierarchy hierarchy, int type) {
		HashMap<String, SubsetCell> cache = getCache(hierarchy, type);
		return cache.keySet().toArray(new String[0]);
	}

	protected String getSubsetName(String id) {
		//utilize api cache ;)
		Element subElement = database.getDimensionByName(SUBSET_DIM)
				.getDefaultHierarchy().getElementById(id);
		if(subElement == null)
			return null;
		return subElement.getName();
	}

	protected String[] getSubsetNames(Hierarchy hierarchy) {
		ArrayList<String> names = new ArrayList<String>();
		String[] subIDs = getSubsetIDs(hierarchy);
		for(String id : subIDs) {
			String name = getSubsetName(id);
			if(name != null)
				names.add(name);
		}
		return names.toArray(new String[names.size()]);
	}
	protected String[] getSubsetNames(Hierarchy hierarchy, int type) {
		ArrayList<String> names = new ArrayList<String>();
		String[] subIDs = getSubsetIDs(hierarchy, type);
		for(String id : subIDs) {
			String name = getSubsetName(id);
			if(name != null)
				names.add(name);
		}
		return names.toArray(new String[names.size()]);
	}
	
	protected final boolean hasSubsets(Hierarchy hierarchy, int type) {
		HashMap<String, SubsetCell> cache = getCache(hierarchy, type);
		return !cache.isEmpty();
	}
	
	protected Subset2 load(String id, Hierarchy hierarchy, int type, SubsetHandlerImpl handler)
			throws PaloIOException {
		Subset2 subset = null;
		HashMap<String, SubsetCell> cache = getCache(hierarchy, type);
		SubsetCell cell = cache.get(id);
		if (cell != null) {
			String xmlDef = cell.getXmlDef();
			try {
				subset = loadFromXML(handler, getSubsetName(id), xmlDef, type);
			} catch (IOException e) {
//				cache.remove(id); 	//REMOVE FAILED SUBSET IT FROM CACHE!
				throw new PaloIOException("failed to load subset '"
						+ getSubsetName(id) + "' for hierarchy '"
						+ hierarchy.getName() + "'!!", e);
			} catch (PaloIOException e) {
				throw new PaloIOException("failed to load subset '"
						+ getSubsetName(id) + "' for hierarchy '"
						+ hierarchy.getName() + "'!!", e);
			}
		}
		return subset;
	}

	protected String newSubsetCell(String name, Hierarchy hierarchy, int type)
			throws PaloIOException {
		Hierarchy subsetHierarchy = database.getDimensionByName(SUBSET_DIM).
			getDefaultHierarchy();
		Element subElement = subsetHierarchy.getElementByName(name);
		if (subElement == null)
			subElement = 
				subsetHierarchy.addElement(name,Element.ELEMENTTYPE_STRING);

		HashMap<String, SubsetCell> cache = getCache(hierarchy, type);
		String subsetId = subElement.getId();
		SubsetCell cell = cache.get(subsetId);
		if (cell != null) {
			String xmlDef = cell.getXmlDef();
			if (xmlDef != null && xmlDef.length() > 0)
				throw new PaloIOException("A subset '" + name
						+ "' already exists in hierarchy '"
						+ hierarchy.getName() + "'!");

		}
		cell = new SubsetCell(subsetId);
		Element[] coordinate = getCoordinate(subsetId,hierarchy,type);
		if(coordinate == null)
			return null; //system hierarchy!!
		cell.setCoordinate(coordinate);
		// add to cache:
		cache.put(cell.getSubsetId(), cell);
		return cell.getSubsetId();
	}

	protected void remove(Subset2 subset) {
		int type = subset.getType();
		Hierarchy hierarchy = subset.getDimHierarchy();
		HashMap<String, SubsetCell> cache = getCache(hierarchy, type);
		SubsetCell cell = cache.get(subset.getId());
		if(cell != null) {
			Cube subCube = getSubsetCube(type);
			if(subCube != null) {
				//clear data:
				//PR 6909: clear doesn't work here anymore...
//				Element[] coord = cell.getCoordinate();
//				Element[][] area = new Element[coord.length][];
//				for(int i=0;i<area.length;++i)
//					area[i] = new Element[]{coord[i]};
//				subCube.clear(area);
				//... so simply erase data:
				subCube.setData(cell.getCoordinate(), "");
				cache.remove(cell.getSubsetId());
				removeCompletely(subset.getId());
			}
		}
	}
	
	protected final void remove(String id, int type, Hierarchy hierarchy) {
		HashMap<String, SubsetCell> cache = getCache(hierarchy, type);
		SubsetCell cell = cache.get(id);
		if(cell != null) {
			Cube subCube = getSubsetCube(type);
			if(subCube != null) {
				//... so simply erase data:
				subCube.setData(cell.getCoordinate(), "");
				cache.remove(cell.getSubsetId());
				removeCompletely(id);
			}
		}
		
	}

	protected void rename(Subset2 subset, String newName) {
		Hierarchy subHierarchy = database.getDimensionByName(SUBSET_DIM).
			getDefaultHierarchy();
		Element subElement = subHierarchy.getElementById(subset.getId());
		subElement.rename(newName);
	}

	protected void save(Subset2 subset) throws PaloIOException {
		validate(subset);
		Hierarchy hierarchy = subset.getDimHierarchy();
		HashMap<String, SubsetCell> cache = 
				getCache(hierarchy, subset.getType());
		SubsetCell cell = cache.get(subset.getId());
		if (cell == null)
			throw new PaloIOException("Subset saving failed!\nUnknown subset '"
					+ subset.getName() + "' for hierarchy '"
					+ hierarchy.getName() + "'!");

		try {
			Cube subCube = getSubsetCube(subset.getType());
			String xmlDef = storeToXML(subset);
			subCube.setData(cell.getCoordinate(), xmlDef);
			cell.setXmlDef(xmlDef);
		} catch (PaloIOException pex) {
			throw new PaloIOException("Could not store subset'"
					+ subset.getName() + "'!!", pex);
		} catch (PaloAPIException pex) {
			throw new PaloIOException("Could not store subset'"
					+ subset.getName() + "'!!", pex);
		} catch (IOException ioe) {
			throw new PaloIOException("Could not store subset'"
					+ subset.getName() + "'!!", ioe);
		}

	}

	protected String getDefinition(Subset2 subset) throws PaloIOException {
		validate(subset);
		Hierarchy hierarchy = subset.getDimHierarchy();
		HashMap<String, SubsetCell> cache = 
				getCache(hierarchy, subset.getType());
		SubsetCell cell = cache.get(subset.getId());
		if (cell == null)
			throw new PaloIOException("Subset saving failed!\nUnknown subset '"
					+ subset.getName() + "' for hierarchy '"
					+ hierarchy.getName() + "'!");

		try {
			return cell.getXmlDef();
		} catch (Exception e) {
			throw new PaloIOException("Could not read subset definition for "
					+ "subset '" + subset.getName() + "'", e);
		}
	}
	
	protected void setDefinition(Subset2 subset, String xmlDefinition) throws PaloIOException {
		Hierarchy hierarchy = subset.getDimHierarchy();
		HashMap<String, SubsetCell> cache = 
				getCache(hierarchy, subset.getType());
		SubsetCell cell = cache.get(subset.getId());
		if (cell == null)
			throw new PaloIOException("Setting subset definition failed!\nUnknown subset '"
					+ subset.getName() + "' for hierarchy '"
					+ hierarchy.getName() + "'!");

		try {
			cell.setXmlDef(xmlDefinition);
		} catch (Exception e) {
			throw new PaloIOException("Could not write subset definition for "
					+ "subset '" + subset.getName() + "'", e);
		}		
	}
	
	//-------------------------------------------------------------------------
	// PRIVATE METHODS
	//
	private final Cube getSubsetCube(int type) {
		String cube = type == Subset2.TYPE_GLOBAL ? SUBSET_CUBE_GLOBAL
				: SUBSET_CUBE_LOCAL;
		return database.getCubeByName(cube);
	}
	
	private final HashMap<String, SubsetCell> getCache(Hierarchy hierarchy,
			int type) {
		HashMap<Hierarchy, HashMap<String, SubsetCell>> cache = 
				type == Subset2.TYPE_GLOBAL ? globalCells : localCells;
		HashMap<String, SubsetCell> cells = cache.get(hierarchy);
		if(cells == null) {
			cells = new LinkedHashMap<String, SubsetCell>();
			cache.put(hierarchy, cells);
			//due to server flaws we currently have to load cells in front :(
			fillCache(hierarchy, type, cells);
		}
		return cells;
	}
	
	private final void fillCache(Hierarchy hierarchy, int type,
			HashMap<String, SubsetCell> cache) {
		int idIndex = type == Subset2.TYPE_GLOBAL ? 1 : 2;
		Cube subsets = getSubsetCube(type);
		Element[][] rowCoordinates = getRow(hierarchy, type);
		if(rowCoordinates == null || rowCoordinates.length == 0)
			return; //seems to be a system hierarchy...
		Object[] values = subsets.getDataBulk(rowCoordinates);
		for(int i=0;i<values.length;++i) {
			if(values[i] != null) {
				String xmlDef = values[i].toString();
				if(xmlDef.length() > 1) {
					//create new subset cell and register it...
					SubsetCell cell =
						new SubsetCell(rowCoordinates[i][idIndex].getId());
					cell.setCoordinate(rowCoordinates[i]);
					cell.setXmlDef(xmlDef);
					cache.put(cell.getSubsetId(), cell);
				}
			}
		}
	}
	
	private final Subset2 loadFromXML(SubsetHandlerImpl handler, String name,
			String def, int type) throws IOException, PaloIOException {
		Subset2 subset = null;
		ByteArrayInputStream bin = new ByteArrayInputStream(def
				.getBytes("UTF-8")); //$NON-NLS-1$
		try {
			subset = SubsetReader.getInstance().fromXML(handler, name, bin,
					type);
		} finally {
			bin.close();
		}
		return subset;
	}

	private final String storeToXML(Subset2 subset) throws IOException,
			PaloIOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			SubsetWriter.getInstance().toXML(bout, subset);
			return bout.toString("UTF-8");
		} finally {
			bout.close();
		}
	}

	/** tries to remove subset from its internal system hierarchy */
	private final void removeCompletely(String subId) { //Subset2 subset) {
		boolean canBeRemoved = true;		
		// we check all caches if they contains subset => if not => remove subset!
//		String subId = subset.getId();
		for (Dimension dim: database.getDimensions()) {
			if (dim.isSystemDimension()) {
				continue;
			}
			for (Hierarchy hierarchy : dim.getHierarchies()) {
				if (hierarchy.isAttributeHierarchy()
					|| hierarchy.isSubsetHierarchy())
					continue;
				if (existsInCache(subId, hierarchy, Subset2.TYPE_GLOBAL)
						|| existsInCache(subId, hierarchy,
								Subset2.TYPE_LOCAL)) {
					canBeRemoved = false;
					break;
				}
			}
		}
		if(canBeRemoved) {
			Hierarchy subHierarchy = database.getDimensionByName(SUBSET_DIM).
				getDefaultHierarchy();
			Element subElement = subHierarchy.getElementById(subId);
			if(subElement != null)
				subHierarchy.removeElement(subElement);
		}
	}
	
	private final boolean existsInCache(String subId, Hierarchy hierarchy, int type) {
		HashMap<String, SubsetCell> cache = getCache(hierarchy, type);
		SubsetCell cell = cache.get(subId);
		if(cell != null) {
			String xmlDef = cell.getXmlDef();
			return xmlDef != null && xmlDef.length()>0;
		}
		return false;
	}
	
	private final Element[] getCoordinate(String id, Hierarchy hierarchy, int type) {
		Element subset = 
			database.getDimensionByName(SUBSET_DIM).getDefaultHierarchy().
			getElementById(id);
		Element dimElement = getHierarchy(hierarchy);
		if(dimElement == null && hierarchy.getDimension().isSystemDimension())
			return null;
		if(type == Subset2.TYPE_GLOBAL)
			return new Element[]{dimElement, subset};
		
		return new Element[]{dimElement, getUser(), subset};
	}

	private final Element[][] getRow(Hierarchy hierarchy, int type) {
		Hierarchy dim = database.getDimensionByName(SUBSET_DIM).getDefaultHierarchy();
		Element[] subsets = dim.getElements();
		Element dimElement = getHierarchy(hierarchy);
		if(dimElement == null || hierarchy.getDimension().isSystemDimension())
			return null;
		Element[][] coordinates = new Element[subsets.length][];
		if (type == Subset2.TYPE_GLOBAL) {
			for (int i = 0; i < subsets.length; ++i) {
				coordinates[i] = new Element[] { dimElement, subsets[i] };
			}
		} else {
			Element usrElement = getUser();
			for (int i = 0; i < subsets.length; ++i) {
				coordinates[i] = new Element[] { dimElement, usrElement,
						subsets[i] };
			}
		}
		return coordinates;
	}

	private final Element getHierarchy(Hierarchy hierarchy) {
		Hierarchy dimHierarchy = 
			database.getDimensionByName(SUBSET_DIMENSION_DIM).getDefaultHierarchy();
		return dimHierarchy.getElementByName(hierarchy.getName());
	}
	
	private final Element getUser() {
		Hierarchy usrHierarchy = database.getDimensionByName(SUBSET_USER_DIM).
			getDefaultHierarchy();
		return usrHierarchy.getElementByName(database.getConnection()
				.getUsername());
	}
	
	protected final void validate(Subset2 subset) throws PaloIOException {
		// check required settings:
		Hierarchy subsetHierarchy = database.getDimensionByName(SUBSET_DIM).
			getDefaultHierarchy();
		if (subsetHierarchy.getElementById(subset.getId()) == null)
			throw new PaloIOException("Subset(" + subset.getId() + ") in'"
					+ database.getName() + "' has unknown id!");
		if(subset.getDimHierarchy() == null)
			throw new PaloIOException("Subset(" + subset.getId() + ") in'"
					+ database.getName() + "' has no defined hierarchy!");
		for(SubsetFilter filter : subset.getFilters())
			filter.validateSettings();
	}
}
