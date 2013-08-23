/*
*
* @file SubsetPersistence.java
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
* @author Stepan Rutz, Arnd Houben
*
* @version $Id: SubsetPersistence.java,v 1.18 2010/02/09 11:44:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2006. All rights reserved.
 */
package org.palo.api.impl.subsets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.palo.api.Cube;
import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.Subset;

/**
 * <code>SubsetPersistence</code>, API and access point for subset persistence.
 * Creates palo system tables for subset storage on demand.
 *
 * @author Stepan Rutz, Arnd Houben
 * @version $Id: SubsetPersistence.java,v 1.18 2010/02/09 11:44:57 PhilippBouillon Exp $
 */
public class SubsetPersistence {
	
	private static final String SYSTEM_PREFIX = "#"; //$NON-NLS-1$
	static final String DIMENSION_SUBSET_COLUMNS = 
			SYSTEM_PREFIX + "subsetcolumns"; //$NON-NLS-1$
	static final String DIMENSION_SUBSET_ROWS = 
			SYSTEM_PREFIX + "subsetrows"; //$NON-NLS-1$
	static final String CUBE_SUBSETS = SYSTEM_PREFIX + "subsets"; //$NON-NLS-1$
	
	static final String PATH_DELIM = ":";
	static final String ELEMENT_DELIM = ",";

	//CANNOT DELETE THIS ENTRY BECAUSE IT IS USED BY THE CubeQuery/DimensionSpecifier
	//to set the subset name on cube editor saving!!!!!!!!
	static final String COL_NAME = "Name"; //$NON-NLS-1$
	static final String COL_DEF = "Def"; //$NON-NLS-1$

	private static SubsetPersistence instance = new SubsetPersistence();
	public final static SubsetPersistence getInstance() {
		return instance;
	}

	//--------------------------------------------------------------------------
	// INSTANCE
	//
	private SubsetPersistence() {
	}

	public final boolean isSubsetCube(Cube cube) {
		String cubeName = cube.getName();
		return cubeName.equals(CUBE_SUBSETS);
	}
	
	public final boolean isSubsetDimension(Dimension dimension) {
		String dimName = dimension.getName();
		return dimName.equals(DIMENSION_SUBSET_COLUMNS)
				|| dimName.equals(DIMENSION_SUBSET_ROWS);
	}


	public final boolean hasSubsets(Database database) {
		Hierarchy subsets = getSubsetRows(database);
		Hierarchy columns = getSubsetColumns(database);
		Cube cube = getSubsetCube(database);
		if(subsets != null && columns != null && cube != null) {
			return subsets.getElementCount() > 0;
		}
		return false;
	}
	
	public final boolean delete(Subset subset) {
		try {
			return deleteInternal(subset);
		} catch (Exception e) {
			System.err.println("SubsetPersistence.delete: " + e); //$NON-NLS-1$
		}
		return false;
	}

	public final Subset[] loadAll(Database database) {
		if(!hasSubsets(database))
			return new Subset[0];
		
		return loadBulky(database);
		
//		Hierarchy rows = getSubsetRows(database);
//		Cube cube = getSubsetCube(database);
//		
//		Element elements[] = rows.getElements();
//		ArrayList<Subset> subsets = new ArrayList<Subset>();
//		for (int i = 0; i < elements.length; ++i) {
//			Element element = elements[i];
//			try {
//				Subset subset = loadSubset(database, cube, element);
//				if (subset != null) {
//					subsets.add(subset);
//				}
//			} catch (IOException e) {
//				//TODO nested errors?
//			}
//		}
//		return subsets.toArray(new Subset[subsets.size()]);
	}
	
	final void save(final Subset subset) {
		try {
			saveInternal(subset);			
		}catch(Exception e) {
			throw new PaloAPIException("Could not save subset '"+subset.getName()+"'!",e);
		}
	}


//	final Object load(Database database, String id) {
//		try {
//			Hierarchy columns = getSubsetColumns(database);
//			Hierarchy rows = getSubsetRows(database);
//			if (rows != null && columns != null) {
//				Element subsetElement = rows.getElementByName(id);
//				if (subsetElement != null) {
//					// load it:
//					Cube subsetCube = getSubsetCube(database);
//					return loadSubset(database, subsetCube, subsetElement);
//				}
//			}
//		} catch (Exception e) {
//		}
//		return null;
//	}
	
	
	final String[] getIDs(Database db) {
		Dimension subsets = db.getDimensionByName(DIMENSION_SUBSET_ROWS);
		if(subsets == null)
			return new String[0];
		Hierarchy subsetHier = subsets.getDefaultHierarchy();
		Element[] elements = subsetHier.getElements();
		String[] ids = new String[elements.length];
		for(int i=0;i<elements.length;++i)
			ids[i] = elements[i].getName();
		return ids;
	}

//	final void load(Database database, Map dimId2subsetId, Map subsets) {
//		Hierarchy columns = getSubsetColumns(database);
//		if (columns == null)
//			return;
//		Hierarchy rows = getSubsetRows(database);
//		if (rows == null)
//			return;
//
//		Cube cube = getSubsetCube(database);
//		if (cube == null)
//			return;
//
//		Element elements[] = rows.getElements();
//		for (int i = 0; i < elements.length; ++i) {
//			Element element = elements[i];
//			try {
//				Subset subset = loadSubset(database, cube, element);
//				if (subset != null) {
//					Set dimSubsets = getSubsetIds(dimId2subsetId, subset
//							.getDimension());
//					dimSubsets.add(subset.getId());
//					subsets.put(subset.getId(), subset);
//				}
//			} catch (IOException e) {
//			}
//		}
//	}
	
	
	private boolean deleteInternal(Subset subset) throws Exception {
		if (subset == null)
			return true;
		do {			
			Hierarchy hier = subset.getHierarchy();
			//dimension was deleted before...
			if(hier == null) {
				return false;
			}
			Database database = hier.getDimension().getDatabase();
			Dimension rows;
			Cube cube;
			if ((database.getDimensionByName(DIMENSION_SUBSET_COLUMNS)) == null)
				break;
			if ((rows = database.getDimensionByName(DIMENSION_SUBSET_ROWS)) == null)
				break;
			if ((cube = database.getCubeByName(CUBE_SUBSETS)) == null)
				break;
			Hierarchy rowHier = rows.getDefaultHierarchy();
			
			Element elements[] = rowHier.getElements();
			for (int i = 0; i < elements.length; ++i) {
				Element element = elements[i];
				String xmlDef = cube.getData(
						new String[] { COL_DEF, element.getName() }).toString();
				ByteArrayInputStream bin = new ByteArrayInputStream(xmlDef
						.getBytes("UTF-8")); //$NON-NLS-1$
				Subset xmlSub = SubsetReader.getInstance().fromXML(bin,
						element.getName(), database);
				if (xmlSub != null) {
					Hierarchy xmlDim = xmlSub.getHierarchy();
					Hierarchy subDim = subset.getHierarchy();
					// check name and source dimension:
					if (xmlSub.getName().equals(subset.getName())
							&& xmlDim.getName().equals(subDim.getName()))
						rowHier.removeElement(element);
				}
			}
		} while (false);
		return true;
	}
	
	private void saveInternal(Subset subset) throws Exception {		
		Database database = subset.getDimension().getDatabase();

		Dimension columns;
		if ((columns = database.getDimensionByName(DIMENSION_SUBSET_COLUMNS)) == null) {
			columns = database.addDimension(DIMENSION_SUBSET_COLUMNS);
		}
		Hierarchy colHier = columns.getDefaultHierarchy();
		if (colHier.getElementByName(COL_DEF) == null) {
			colHier.addElement(COL_DEF, Element.ELEMENTTYPE_STRING);
		}

		Dimension rows;
		if ((rows = database.getDimensionByName(DIMENSION_SUBSET_ROWS)) == null) {
			rows = database.addDimension(DIMENSION_SUBSET_ROWS);
		}
		Hierarchy rowHier = rows.getDefaultHierarchy();
		String key = subset.getId();  
		Element element = rowHier.getElementByName(key);
		if (element == null)
			element = rowHier.addElement(key, Element.ELEMENTTYPE_STRING);

		Cube cube;
		if ((cube = database.getCubeByName(CUBE_SUBSETS)) == null) {
			cube = database.addCube(CUBE_SUBSETS, new Dimension[] { columns,
					rows });
		}

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			SubsetWriter.getInstance().toXML(bout, subset);
		} finally {
			bout.close();
		}
		cube.setData(new String[] { COL_DEF, element.getName() }, bout
				.toString("UTF-8")); //$NON-NLS-1$
	}

	private final Hierarchy getSubsetColumns(Database database) {
		//legacy check: name column is not used anymore!!!
		Dimension columns = database.getDimensionByName(DIMENSION_SUBSET_COLUMNS);		
		Hierarchy colHier = null;
		if(columns != null) {
			colHier = columns.getDefaultHierarchy();
			//remove not used name column...
			Element nameColumn = colHier.getElementByName(COL_NAME);
			if (nameColumn != null) 
				colHier.removeElement(nameColumn);
		}
		return colHier;
	}
	
	private final Hierarchy getSubsetRows(Database database) {
		Dimension dim = database.getDimensionByName(DIMENSION_SUBSET_ROWS);
		if (dim != null) {
			return dim.getDefaultHierarchy();
		}
		return null;
	}
	
	private final Cube getSubsetCube(Database database) {
		return database.getCubeByName(CUBE_SUBSETS);
	}
	
	
	private final Subset loadSubset(Database database, Cube subsetCube,
			Element subsetElement) throws IOException {
		Subset subset = null;
		String def = subsetCube.getData(
				new String[] { COL_DEF, subsetElement.getName() }).toString();
		// PR 6566: skip subsets without definitions, can occur during
		// first subset creation while loading required cubes...
		if (def.equals("-") || def.equals("")) {
			return subset;
		}
		ByteArrayInputStream bin = new ByteArrayInputStream(def
				.getBytes("UTF-8")); //$NON-NLS-1$
		try {
			subset = SubsetReader.getInstance().fromXML(bin,
					subsetElement.getName(), database);
		} finally {
			bin.close();
		}
		return subset;
	}
	
    private final Set getSubsetIds(Map dimId2subsetId,Dimension dimension) {
    	Set subsets= (Set)dimId2subsetId.get(dimension.getId());
    	if(subsets == null) {
    		subsets = new LinkedHashSet();
    		dimId2subsetId.put(dimension.getId(), subsets);
    	}
    	return subsets;
    }

    private final Subset[] loadBulky(Database database) {
    	Cube cube = getSubsetCube(database);
		Hierarchy rows = getSubsetRows(database);
		Hierarchy cols = getSubsetColumns(database);
		
		if (cols == null || rows == null || cube == null)
			return new Subset[0];
			
		Element elements[] = rows.getElements();
		Element col = cols.getElementByName(COL_DEF);
		//create subset coordinates...
		Element[][] coordinates = new Element[elements.length][];
		for(int i=0;i<elements.length;++i)
			coordinates[i] = new Element[] {col, elements[i]};

		//load all subset definitions:
		Object[] values = cube.getDataBulk(coordinates);

		//create subsets:
		ArrayList<Subset> subsets = new ArrayList<Subset>(elements.length);
		for (int i = 0; i < values.length; ++i) {
			try {
				if (values[i] != null) {
					Subset subset = loadFromDefinition(values[i].toString(),
							elements[i].getName(), database);
					if (subset != null) {
						subsets.add(subset);
					}
				}
			} catch (IOException e) {
				// TODO nested errors?
				System.err.println("Failed to load subset with id "
						+ elements[i].getName());
			}
		}
		return subsets.toArray(new Subset[subsets.size()]);
    }
    
    private final Subset loadFromDefinition(String def, String subId,
			Database database) throws IOException {
		Subset subset = null;
		// PR 6566: skip subsets without definitions, can occur during
		// first subset creation while loading required cubes...
		if (def.equals("-") || def.equals("")) {
			return subset;
		}
		ByteArrayInputStream bin = 
				new ByteArrayInputStream(def.getBytes("UTF-8")); //$NON-NLS-1$
		try {
			subset = SubsetReader.getInstance().fromXML(bin, subId, database);
		} finally {
			bin.close();
		}
		return subset;
	}
}
