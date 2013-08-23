/*
*
* @file VirtualCubeImpl.java
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
* @author Stepan Rutz
*
* @version $Id$
*
*/

/*
 * (c) Tensegrity Software 2005. All rights reserved.
 */
package org.palo.api.impl;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.palo.api.Cell;
import org.palo.api.Cube;
import org.palo.api.CubeView;
import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.ExportContext;
import org.palo.api.ExportDataset;
import org.palo.api.Hierarchy;
import org.palo.api.Lock;
import org.palo.api.PaloAPIException;
import org.palo.api.Property;
import org.palo.api.Property2;
import org.palo.api.Rule;
import org.palo.api.VirtualCubeDefinition;
import org.palo.api.VirtualDimensionDefinition;
import org.palo.api.VirtualObject;
import org.palo.api.persistence.PersistenceObserver;

import com.tensegrity.palojava.CubeInfo;
import com.tensegrity.palojava.ElementInfo;
import com.tensegrity.palojava.LockInfo;
import com.tensegrity.palojava.PaloException;

/**
 * <code>VirtualCubeImpl</code>
 *
 * @author Stepan Rutz
 * @version $ID$
 */
class VirtualCubeImpl implements Cube, VirtualObject
{
    private final VirtualCubeDefinition definition;
    private final VirtualDimensionImpl vdims[];
//    private final Map vdim2dim;
    private final Map dim2vdim;
    private final CompoundKey key;
    
    VirtualCubeImpl(VirtualCubeDefinition definition)
    {
        this.definition = definition;
        
        VirtualDimensionDefinition[] vdimdefs = 
        	definition.getVirtualDimensionDefinitions();
        this.vdims = new VirtualDimensionImpl[vdimdefs.length];
        for (int i = 0; i < vdims.length; ++i)
        {
//        	if (vdimdefs[i].getFilter() == null) {
				VirtualDimensionImpl vdim = new VirtualDimensionImpl(
						vdimdefs[i].getSourceDimension(),
						vdimdefs[i].getElements(), 
						vdimdefs[i].getRootElements(), 
						vdimdefs[i].isFlat(),
						vdimdefs[i].getActiveHierarchy());
				vdims[i] = vdim;
				vdim.setVirtualDefinition(vdimdefs[i]);
//
//			} else {
//				VirtualDimensionImpl vdim = new VirtualDimensionImpl(
//						vdimdefs[i].getSourceDimension(), vdimdefs[i]
//								.getFilter());
//				vdims[i] = vdim;
//			}
        }
        
        
//        this.vdim2dim = new HashMap();
        this.dim2vdim = new HashMap();
        
        for (int i = 0; i < vdims.length; ++i)
        {
            VirtualDimensionImpl vdim = vdims[i];
            Dimension dim = vdim.getSourceDimension();
//            vdim2dim.put(vdim, dim);
            dim2vdim.put(dim, vdim);
        }

        this.key = this.createKey();
    }
    
//    private final void dump(Dimension vdim) {
//    	if(vdim == null) {
//    		System.out.println("dimension = null");
//    		return;
//    	}    		
//    	ElementNode[] tree = vdim.getElementsTree();
//    	dump(tree);
//    }
//    private final void dump(ElementNode[] tree) {
//    	for(ElementNode node : tree) {
//    		System.out.println("node: "+node.getElement().getName()+ " - "+node.getChildren().length);
//    		dump(node.getChildren());
//    	}
//    }
    
    
    private final CompoundKey createKey() {
		return new CompoundKey(new Object[] { 
				VirtualCubeImpl.class,
				definition.getSourceCube().getName(),
				definition.getSourceCube().getDatabase().getName(), 
				getName() });
	}

    public int getExtendedType()
    {
        return CUBEEXTENDEDTYPE_VIRTUAL;
    }
    
    public final String getId() {
		return definition.getSourceCube().getId() + "@@"
				+ Integer.toHexString(System.identityHashCode(this));
	}
    
    public final String getName()
    {
    	String postfix  = definition.getName();
    	if(postfix == null)
    		postfix = Integer.toHexString(System.identityHashCode(this));
        return definition.getSourceCube().getName() + "@@" + postfix;
    }
    
    public Database getDatabase()
    {
        return definition.getSourceCube().getDatabase();
    }
    
    public int getDimensionCount()
    {
        return definition.getSourceCube().getDimensionCount();
    }
    
    public Dimension getDimensionAt(int index)
    {
        Dimension dim = definition.getSourceCube().getDimensionAt(index);
        Dimension vdim = (Dimension) dim2vdim.get(dim);
        return vdim == null ? dim : vdim;
    }
    
    public Dimension[] getDimensions()
    {
        Dimension dims[] = definition.getSourceCube().getDimensions();
        if (dims == null)
            return null;
        for (int i = 0; i < dims.length; ++i)
        {
            Dimension dim = dims[i];
            Dimension vdim = (Dimension) dim2vdim.get(dim);
            if (vdim == null)
                continue;
            dims[i] = vdim;
        }
        return dims;
    }
    
    public Dimension getDimensionByName(String name)
    {
        if (name == null)
            return null;
        
        for (int i = 0; i < vdims.length; ++i)
        {
            VirtualDimensionImpl vdim = vdims[i];
            if (vdim != null && vdim.getName().equalsIgnoreCase(name))
                return vdim;
        }
        
        return definition.getSourceCube().getDimensionByName(name);
    }

    public final Dimension getDimensionById(String id) {
    	for(int i=0;i<vdims.length;++i) {
    		if(vdims[i].getId().equals(id))
    			return vdims[i];
    	}
    	return definition.getSourceCube().getDimensionById(id);
    }
    
    public void commitLog()
    {
        definition.getSourceCube().commitLog();
    }
    
    public Object getData(String coordinates[])
    {
        return definition.getSourceCube().getData(coordinates);
    }
   
    public Object[] getDataArray(String elements[][])
    {
        return definition.getSourceCube().getDataArray(elements);
    }
 
    public void convert(int type) {
    	definition.getSourceCube().convert(type);
    }
    
//    public ExportDataset getDataExport(ExportContext context)
//    {
//        return definition.getSourceCube().getDataExport(context);
//    }
    
    public Object getData(Element coordinates[])
    {
        return definition.getSourceCube().getData(coordinates);
    }

    public Object[] getDataArray(Element elements[][])
    {
        return definition.getSourceCube().getDataArray(elements);
    }

    public Object[] getDataBulk(Element elements[][])
    {
        return definition.getSourceCube().getDataBulk(elements);
    }

    public void setData(String coordinates[], Object value)
    {
        definition.getSourceCube().setData(coordinates, value);
    }
    
    public void setData(Element coordinates[], Object value)
    {
        definition.getSourceCube().setData(coordinates, value);
    }

    public void setData(Element coordinates[], Object value,
			NumberFormat formatter) {
		definition.getSourceCube().setData(coordinates, value);
	}

    public void setDataSplashed(Element[] coordinate, Object value) {
		definition.getSourceCube().setDataSplashed(coordinate, value);
	}

    public void setDataSplashed(Element[] coordinate, Object value,NumberFormat formatter) {
		definition.getSourceCube().setDataSplashed(coordinate, value,formatter);
	}

    public void setDataSplashed(String coordinates[], Object value, int splashMode)
    {
        definition.getSourceCube().setDataSplashed(coordinates, value, splashMode);
    }
    
    public void setDataSplashed(Element coordinates[], Object value, int splashMode)
    {
        definition.getSourceCube().setDataSplashed(coordinates, value, splashMode);
    }
    
    public void setDataArray(Element[][] coordinates, Object[] values, int splashMode) {
    	definition.getSourceCube().setDataArray(coordinates, values, splashMode);
    }

	public boolean isAttributeCube() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSubsetCube() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isViewCube() {
		return false;
	}

	public CubeView addCubeView(String name, Property [] properties) {
		// TODO Auto-generated method stub
		return null;
	}

	public CubeView addCubeView(String id ,String name, Property [] properties) {
		// TODO Auto-generated method stub
		return null;
	}

	public CubeView[] getCubeViews() {
		return new CubeView[0];
	}

	public final int getCubeViewCount() {
		return 0;
	}
	
	public void removeCubeView(CubeView view) {
	}
    
	public CubeView getCubeView(String id) {
		return null;
	}
	public final String[] getCubeViewIds() {
		return new String[0];
	}

	public String getCubeViewName(String id) {
		return null;
	}

	public void getCubeViews(PersistenceObserver observer) {		
	}


	
	public boolean equals(Object o) {
		if(!(o instanceof VirtualCubeImpl))
			return false;
		VirtualCubeImpl other = (VirtualCubeImpl)o;
//TODO check: the key is not quite right here. do we want to leave the virtual
//		dimensions outside? or take them into account for equality... 
//		(and check against PR 6567)
		return key.equals(other.key);
	}
	
	public int hashCode() {
		int result = 17;
		result = 37 * result * key.hashCode();
		return result;
	}

//	public ExportDataset getDataExport() {
//		return definition.getSourceCube().getDataExport();
//	}

	public ExportDataset getDataExport(ExportContext context) {
		return definition.getSourceCube().getDataExport(context);
	}

	public ExportContext getExportContext() {
		return definition.getSourceCube().getExportContext();
	}

	public ExportContext getExportContext(Element[][] area) {
		return definition.getSourceCube().getExportContext(area);
	}

	public boolean isSystemCube() {
		return definition.getSourceCube().isSystemCube();
	}
	
	public Rule addRule(String definition) {
		return this.definition.getSourceCube().addRule(definition);
	}
	
	public Rule addRule(String definition, String externalIdentifier,
			boolean useIt, String comment) {
		return this.definition.getSourceCube().addRule(definition,
				externalIdentifier, useIt, comment);
	}

	public Rule addRule(String definition, String externalIdentifier,
			boolean useIt, String comment, boolean activate) {
		return this.definition.getSourceCube().addRule(definition,
				externalIdentifier, useIt, comment, activate);
	}


	public Rule[] getRules() {
		return definition.getSourceCube().getRules();
	}
	
	public Rule getRule(Element[] coordinate) {
		return definition.getSourceCube().getRule(coordinate);
	}

	public boolean removeRule(Rule rule) {
		return definition.getSourceCube().removeRule(rule);
	}
	public boolean removeRule(String ruleId) {
		return definition.getSourceCube().removeRule(ruleId);
	}

	public void setDataArray(Element[][] coordinates, Object[] values,
			boolean add, int splashMode, boolean notifyEventProcessors) {
		definition.getSourceCube().setDataArray(coordinates, values, add,
				splashMode, notifyEventProcessors);
	}

    public void addDataArray(Element[][] coordinates, Object[] values,
			int splashMode) {
    	definition.getSourceCube().addDataArray(coordinates, values, splashMode);
    }

	public CubeView addCubeView(String id, String name, boolean hideEmpty) {
		return null;
	}

	public CubeView addCubeView(String name, boolean hideEmpty) {
		return null;
	}

	public void registerViewObserver(PersistenceObserver cubeViewObserver) {
		// TODO Auto-generated method stub
		
	}

	public void unregisterViewObserver(PersistenceObserver cubeViewObserver) {
		// TODO Auto-generated method stub
		
	}

	public final Object getVirtualDefinition() {
		return definition;
	}

	public final void rename(String newName) {
		Util.noopWarning();		
	}

	public void addProperty(Property2 property) {
		definition.getSourceCube().addProperty(property);
	}

	public String[] getAllPropertyIds() {
		return definition.getSourceCube().getAllPropertyIds();
	}

	public Property2 getProperty(String id) {
		return definition.getSourceCube().getProperty(id);
	}

	public void removeProperty(String id) {
		definition.getSourceCube().removeProperty(id);
	}

	public final void clear() {
		definition.getSourceCube().clear();
	}

	public final void clear(Element[][] area) {
		definition.getSourceCube().clear(area);
	}

	public boolean canBeModified() {
		return true;
	}

	public boolean canCreateChildren() {
		return true;
	}

	public Cell getCell(Element[] coordinate) {
		return definition.getSourceCube().getCell(coordinate);
	}

	public Cell[] getCellArea(Element[][] coordinates) {
		return definition.getSourceCube().getCellArea(coordinates);
	}

	public Cell[] getCells(Element[][] coordinates) {
		return definition.getSourceCube().getCells(coordinates);
	}

	public Cell[] getCells(Element[][] coordinates, boolean hideEmpty) {
		return definition.getSourceCube().getCells(coordinates, hideEmpty);
	}

	public boolean isUserInfoCube() {
		return definition.getSourceCube().isUserInfoCube();
	}

	public int getType() {
		return 0;
	}
	
    public final Lock requestLock(Element[][] area) {
    	return ((CubeImpl)definition.getSourceCube()).requestLock(area);
	}
    public final Lock[] getLocks() {
    	return ((CubeImpl)definition.getSourceCube()).getLocks();
    }
    public final boolean commit(Lock lock) {
    	return ((CubeImpl)definition.getSourceCube()).commit(lock);
    }
    public final boolean rollback(Lock lock, int steps) {
    	return ((CubeImpl)definition.getSourceCube()).rollback(lock, steps);
	}
    public final CubeInfo getInfo() {
    	return ((CubeImpl)definition.getSourceCube()).getInfo();
    }

	public final BigInteger getCellCount() {
		return ((CubeImpl)definition.getSourceCube()).getCellCount();
	}

	public final BigInteger getFilledCellCount() {
		return ((CubeImpl)definition.getSourceCube()).getFilledCellCount();
	}

}
