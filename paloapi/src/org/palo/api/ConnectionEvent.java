/*
*
* @file ConnectionEvent.java
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
* @version $Id: ConnectionEvent.java,v 1.20 2009/09/03 09:05:59 ArndHouben Exp $
*
*/

/*
 * (c) Tensegrity Software 2005. All rights reserved.
 */
package org.palo.api;

/**
 * <code>ConnectionEvent</code>
 * <p>
 * Every action which alters the data of the palo server triggers an event.
 * Therefore this class defines constants for different event types which
 * describe the performed modification.
 * </p>
 * 
 * @author Stepan Rutz
 * @version $Id: ConnectionEvent.java,v 1.20 2009/09/03 09:05:59 ArndHouben Exp $
 */
public class ConnectionEvent
{
    // Constant identifying the <code>ConnectionEvent</code> type.
	/** 
	 * Signals adding of databases. Use {@link #getDatabases()} to obtain the 
	 * affected <code>Database</code>s
	 */
    public static final int CONNECTION_EVENT_DATABASES_ADDED = 0;
	/** 
	 * Signals removing of databases. Use {@link #getDatabases()} to obtain the 
	 * affected <code>Database</code>s
	 */
    public static final int CONNECTION_EVENT_DATABASES_REMOVED = 1;
	/** 
	 * Signals adding of dimensions. Use {@link #getDimensions()} to obtain the 
	 * affected <code>Dimension</code>s
	 */
    public static final int CONNECTION_EVENT_DIMENSIONS_ADDED = 2;
	/** 
	 * Signals removing of dimensions. Use {@link #getDimensions()} to obtain 
	 * the affected <code>Dimension</code>s
	 */
    public static final int CONNECTION_EVENT_DIMENSIONS_REMOVED = 3;
	/** 
	 * Signals renaming of dimensions. Use {@link #getDimensions()} to obtain 
	 * the affected <code>Dimension</code>s
	 */
    public static final int CONNECTION_EVENT_DIMENSIONS_RENAMED = 4;
	/** 
	 * Signals adding of elements. Use {@link #getElements()} to obtain the 
	 * affected <code>Element</code>s
	 */
    public static final int CONNECTION_EVENT_ELEMENTS_ADDED = 5;
	/** 
	 * Signals removing of elements. Use {@link #getElements()} to obtain the 
	 * affected <code>Element</code>s
	 */
    public static final int CONNECTION_EVENT_ELEMENTS_REMOVED = 6;
	/** 
	 * Signals renaming of elements. Use {@link #getElements()} to obtain the 
	 * affected <code>Element</code>s
	 */
    public static final int CONNECTION_EVENT_ELEMENTS_RENAMED = 7;
	/** 
	 * Signals changing of elements types. Use {@link #getElements()} to obtain 
	 * the affected <code>Element</code>s
	 */
    public static final int CONNECTION_EVENT_ELEMENTS_TYPE_CHANGED = 8;
	/** 
	 * Signals adding of cubes. Use {@link #getCubes()} to obtain the affected 
	 * <code>Cube</code>s
	 */
    public static final int CONNECTION_EVENT_CUBES_ADDED = 9;
	/** 
	 * Signals removing of cubes. Use {@link #getCubes()} to obtain the affected 
	 * <code>Cube</code>s
	 */
    public static final int CONNECTION_EVENT_CUBES_REMOVED = 10;
	/** 
	 * Signals adding of consolidations. Use {@link #getConsolidation()} to 
	 * obtain the affected <code>Consolidation</code>s
	 */
    public static final int CONNECTION_EVENT_CONSOLIDATIONS_ADDED = 11;
	/** 
	 * Signals removing of consolidations. Use {@link #getConsolidation()} to 
	 * obtain the affected <code>Consolidation</code>s
	 */
    public static final int CONNECTION_EVENT_CONSOLIDATIONS_REMOVED= 12; 
    /**
     * Signals a structural change within the palo server which is raised by
     * an external application. So called <i>structural changes</i> occur when
     * the palo objects like <code>{@link Database}</code>, 
     * <code>{@link Dimension}</code>, <code>{@link Cube}</code> or
     * <code>{@link Element}</code> are altered, e.g. by deleting, adding or 
     * renaming. Changing the values of cube cells will not raise such an event.
     * <p>
     * <b>NOTE:</b>
     * This event is neither raised on changes of subset definitions nor on 
     * changes of cube views content, because internally subsets and views are 
     * stored in cube cells and the changing of cube cells do not raise an event
     * by definition.
     * </p>
     */
    public static final int CONNECTION_EVENT_SERVER_STRUCTURE_CHANGED = 13;
    /**
     * Signals that the palo server is currently not available.
     */
    public static final int CONNECTION_EVENT_SERVER_DOWN = 14;
	/** 
	 * Signals adding of attributes. Use {@link #getAttributes()} to obtain the 
	 * affected <code>Attribute</code>s
	 */
    public static final int CONNECTION_EVENT_ATTRIBUTES_ADDED = 15;
	/** 
	 * Signals removing of attributes. Use {@link #getAttributes()} to obtain 
	 * the affected <code>Attribute</code>s
	 */
    public static final int CONNECTION_EVENT_ATTRIBUTES_REMOVED = 16;
	/** 
	 * Signals changing of attributes. Use {@link #getAttributes()} to obtain 
	 * the affected <code>Attribute</code>s
	 */
    public static final int CONNECTION_EVENT_ATTRIBUTES_CHANGED = 17;
    
    /**
	 * Signals renaming of cubes. Use {@link #getCubes()} to obtain the 
	 * affected <code>Cube</code>s
     */
    public static final int CONNECTION_EVENT_CUBES_RENAMED = 18;
    
    public static final int CONNECTION_EVENT_RULES_ADDED = 19;
    public static final int CONNECTION_EVENT_RULES_REMOVED = 20;
    public static final int CONNECTION_EVENT_RULES_CHANGED = 21;
    
	/** 
	 * Signals renaming of databases.
	 */
    public static final int CONNECTION_EVENT_DATABASES_RENAMED = 22;

    
    private final Connection source;
    private final Object parent;
    private final int type;
    private final Object items[];
    
    /** 
     * A general data field which holds a value before the connection event
     * occurred. The content of this field depends on the event type, e.g.
     * for a rename event this field holds the old name or for a type change
     * event the old type. However it is not guaranteed that this field is set
     * and even null is permitted.
     */
    public Object oldValue;
    
    /**
     * Constructs a new <code>ConnectionEvent</code> with the given properties.
     * @param source the source {@link Connection} of the event.
     * @param parent the parent domain object of the event.
     * @param type the type of the event.
     * @param items the affected domain objects.
     */
    public ConnectionEvent(Connection source, Object parent, int type, Object items[])
    {
        this.source = source;
        this.parent = parent;
        this.type = type;
        this.items = (Object[]) items.clone();
    }
    
    /**
     * Returns the source {@link Connection} of the event.
     * @return the source {@link Connection} of the event.
     */
    public Connection getSource()
    {
        return source;
    }
    
    /**
     * Returns the parent domain object of the event.
     * @return the parent domain object of the event.
     */
    public Object getParent()
    {
        return parent;
    }
    
    /**
     * Returns the type of the event as defined by the
     * constants in this class.
     * @return the type of the event as defined by the
     */
    public int getType()
    {
        return type;
    }
    
    /**
     * Returns the affected {@link Database}s of the event
     * if the event is applicable to databases, otherwise
     * <code>null</code> is returned.
     * @return the affected {@link Database}s.
     */
    public Database[] getDatabases()
    {
        if (type != CONNECTION_EVENT_DATABASES_ADDED &&
            type != CONNECTION_EVENT_DATABASES_REMOVED)
            return new Database[0];
        
        Database dbs[] = new Database[items.length];
        System.arraycopy(items, 0, dbs, 0, items.length);
        return dbs;
    }
    
    /**
     * Returns the affected {@link Dimension}s of the event
     * if the event is applicable to dimensions, otherwise
     * <code>null</code> is returned.
     * @return the affected {@link Dimension}s.
     */
    public Dimension[] getDimensions()
    {
        if (type != CONNECTION_EVENT_DIMENSIONS_ADDED &&
            type != CONNECTION_EVENT_DIMENSIONS_REMOVED &&
            type != CONNECTION_EVENT_DIMENSIONS_RENAMED)
            return new Dimension[0];
        
        Dimension dims[] = new Dimension[items.length];
        System.arraycopy(items, 0, dims, 0, items.length);
        return dims;
    }
    
    /**
     * Returns the affected {@link Element}s of the event
     * if the event is applicable to elements, otherwise
     * <code>null</code> is returned.
     * @return the affected {@link Element}s.
     */
    public Element[] getElements()
    {
        if (type != CONNECTION_EVENT_ELEMENTS_ADDED &&
            type != CONNECTION_EVENT_ELEMENTS_REMOVED &&
            type != CONNECTION_EVENT_ELEMENTS_RENAMED &&
            type != CONNECTION_EVENT_ELEMENTS_TYPE_CHANGED)
            return new Element[0];
        
        Element elements[] = new Element[items.length];
        System.arraycopy(items, 0, elements, 0, items.length);
        return elements;
    }

    /**
     * Returns the affected {@link Attribute}s of the event
     * if the event is applicable to attributes, otherwise
     * <code>null</code> is returned.
     * @return the affected {@link Attribute}s.
     */
    public Attribute[] getAttributes() {
    	switch(type) {
    	case CONNECTION_EVENT_ATTRIBUTES_ADDED:
    	case CONNECTION_EVENT_ATTRIBUTES_REMOVED:
    	case CONNECTION_EVENT_ATTRIBUTES_CHANGED:
    		Attribute[] attributes = new Attribute[items.length];
    		System.arraycopy(items, 0, attributes, 0, items.length);
    		return attributes;
    	}
    	return new Attribute[0];
    }
    /**
     * Returns the affected {@link Consolidation}s of the event
     * if the event is applicable to consolidations, otherwise
     * <code>null</code> is returned.
     * @return the affected {@link Consolidation}s.
     */
    public Consolidation[] getConsolidation()
    {
        if (type != CONNECTION_EVENT_CONSOLIDATIONS_ADDED &&
            type != CONNECTION_EVENT_CONSOLIDATIONS_REMOVED)
            return new Consolidation[0];
        
        Consolidation consolidations[] = new Consolidation[items.length];
        System.arraycopy(items, 0, consolidations, 0, items.length);
        return consolidations;
    }
    /**
     * Returns the affected {@link Cube}s of the event
     * if the event is applicable to cubes, otherwise
     * <code>null</code> is returned.
     * @return the affected {@link Cube}s.
     */
    public Cube[] getCubes()
    {
        if (type != CONNECTION_EVENT_CUBES_ADDED &&
        	type != CONNECTION_EVENT_CUBES_RENAMED &&
            type != CONNECTION_EVENT_CUBES_REMOVED)
            return new Cube[0];
        
        Cube cubes[] = new Cube[items.length];
        System.arraycopy(items, 0, cubes, 0, items.length);
        return cubes;
    }
    /**
     * Returns the affected {@link Rule}s of the event
     * if the event is applicable to rules, otherwise
     * <code>null</code> is returned.
     * @return the affected {@link Rule}s.
     */
    public final Rule[] getRules() {
    	switch(type) {
    	case CONNECTION_EVENT_RULES_ADDED:
    	case CONNECTION_EVENT_RULES_REMOVED:
    	case CONNECTION_EVENT_RULES_CHANGED:                
            Rule[] rules = new Rule[items.length];
            System.arraycopy(items, 0, rules, 0, items.length);
            return rules;
    	}
    	return new Rule[0];
    }
    
    public String toString()
    {
        return getClass().getName() + " { type=" + type + " }";
    }
}
