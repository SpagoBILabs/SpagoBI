/*
*
* @file Cube.java
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
* @version $Id: Cube.java,v 1.79 2010/03/11 10:42:20 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2005. All rights reserved.
 */
package org.palo.api;

import java.math.BigInteger;
import java.text.NumberFormat;

import org.palo.api.exceptions.PaloIOException;
import org.palo.api.exceptions.PaloObjectNotFoundException;
import org.palo.api.persistence.PersistenceObserver;

import com.tensegrity.palojava.CellInfo;
import com.tensegrity.palojava.CubeInfo;

/**
 * <code>Cube</code>
 * 
 * <p>A <code>Cube</code> instance corresponds to a PALO cube. Data can be
 * read from and stored to a PALO cube.</p>
 *
 *<p>
 * A reference to this owner {@link org.palo.api.Database} is obtained by
 * invoking {@link #getDatabase()}.
 * </p>
 * 
 * <p>A <code>Cube</code> is associated with a number of {@link org.palo.api.Dimension}s.
 * These {@link org.palo.api.Dimension}s must belong to the same parent {@link org.palo.api.Database}
 * as the cube. (The cube's dimensions are thus a subset of the parent database's dimensions.)
 * </p>
 * 
 *
 * @author Stepan Rutz
 * @author Axel Kiselev
 * @version $Id: Cube.java,v 1.79 2010/03/11 10:42:20 PhilippBouillon Exp $
 * 
 * @see org.palo.api.PaloAPIException
 */
public interface Cube extends PaloObject {

	/**
	 * Splash-mode constant to disable splashing of a consolidated cell.
	 */
    public static final int
            SPLASHMODE_DISABLED = CellInfo.SPLASH_MODE_DISABLED;
    
    /**
     * Splash-mode constant enumeration.
     */
    public static final int
        SPLASHMODE_DEFAULT = CellInfo.SPLASH_MODE_DEFAULT,
        SPLASHMODE_BASE_ADD = CellInfo.SPLASH_MODE_ADD,
        SPLASHMODE_BASE_SET = CellInfo.SPLASH_MODE_SET,        
        SPLASHMODE_UNKNOWN = CellInfo.SPLASH_MODE_UNKNOWN;

//    //cube status:
//	public static final int STATUS_UNLOADED = 0;
//	public static final int STATUS_LOADED = 1;
//	public static final int STATUS_CHANGED = 2;

    /**
     * Constants for cube-type
     */
    public static final int
        CUBEEXTENDEDTYPE_REGULAR = 0,
        CUBEEXTENDEDTYPE_VIRTUAL = 1;
//    	CUBETYPE_NORMAL = 1,
//    	CUBETYPE_SYSTEM = 2,
//    	CUBETYPE_ATTRIBUTE = 4,
//    	CUBETYPE_USERINFO = 8;
        
                          
    /**
     * Returns the extended-type of this <code>Cube</code>.
     * @return the extended-type of this <code>Cube</code>.
     */
    int getExtendedType();
    
    /**
     * Returns the name of this <code>Cube</code>
     * @return the name of this <code>Cube</code>.
     */
    String getName();
    
    /**
     * Returns the parent {@link Database} of this instance.
     * @return the parent {@link Database} of this instance.
     */
    Database getDatabase();
    
    /**
     * Returns the number of {@link Dimension}s of this instance.
     * @return the number of {@link Dimension}s of this instance.
     */
    int getDimensionCount();
    
//    /**
//     * Returns the total number of cells this cube has, i.e. incl. not
//     * filled cells. 
//     * @return total number of cells
//     */
//    BigInteger getCellCount();
//    /**
//     * Returns only the number of filled cells this cube has.
//     * @return number of filled cells
//     */
//    BigInteger getFilledCellCount();
//    /**
//     * Returns the current status of this cube. The status is one of the 
//     * predefined constants. 
//     * @return the cube status
//     */
//    int getStatus();
    
    /**
     * Returns the {@link Dimension} stored at the given index.
     * If the index does not correspond to a legal position
     * in the internally managed array of dimensions of this
     * instance, then <code>null</code> is returned.
     * @param index the index
     * @return the {@link Dimension} stored at the given index
     * or <code>null</code>.
     */
    Dimension getDimensionAt(int index);
    
    /**
     * Returns an array of {@link Dimension} instances available
     * for this instance.
     * <p>The returned array is a copy of the internal data structure.
     * Changing the returned array does not change this instance.
     * </p>
     * 
     * @return an array of {@link Dimension} instances available
     * for this connection.
     */
    Dimension[] getDimensions();
    
    /**
     * Returns the {@link Dimension} stored under the given name or
     * <code>null</code> if no such {@link Dimension} exists.
     * @param name the dimension-name to look-up.
     * @return the {@link Dimension} stored under the given name or
     * <code>null</code> if no such {@link Dimension} exists.
     */
    Dimension getDimensionByName(String name);

    /**
     * Returns the {@link Dimension} associated with the given identifier or
     * <code>null</code> if no such {@link Dimension} exists.
     * @param id the id of the dimension to look-up.
     * @return the {@link Dimension} stored under the given id or
     * <code>null</code> if no such {@link Dimension} exists.
     */
    Dimension getDimensionById(String id);
    
    /**
     * Commits all setdata operations (logged in .cube.log) to the cube data
     * file (.cube.data).
     */
    void commitLog();
    
    /**
     * Returns the data stored at the given coordinates.
     * The given array of coordinates must be of the same length
     * as the number of dimensions of the cube. This allows to
     * identify a single data-cell of the cube. The
     * coordinates-array consists of element names, one element
     * name for each dimension
     * 
     * <p>A valid coordinates specification
     * must meet the following criteria:
     * <ul>
     * <li><p>The coordinates array length must the equal
     * to the dimension-count of the cube</p></li>
     * <li><p>The entry in the coordinates array at position
     * <code>i</code>must be the name of an element of the dimension
     * that is stored at position <code>i</code> in the cube (
     * as returned by {@link #getDimensions()}). 
     * </p></li>
     * </ul>
     * 
     * @param coordinates the coordinate array identifying the
     * data cell.
     * @return the value stored at the specified data-cell.
     */
    Object getData(String coordinates[]);
   
    /**
     * Returns the data stored at the cube area determined by the given element 
     * arrays.<br/>
     * 
     * <p>The element arrays must meet the following criteria:
     * <ul>
     * <li><p>The first dimension of the elements array must have
     * the same length as the number of dimensions in this cube.
     * </p></li>
     * <li><p>Each of the String arrays in the second dimension
     * of the elements array must consist of one or more element
     * names that belong to an element in the dimension of
     * this cube that is stored at the some position (
     * as returned by {@link #getDimensions()}.
     * </p></li>
     * </ul>
     * 
     * 
     * <p>
     * The number of returned elements is determined by the cartesian product 
     * of the given string-arrays.
     * </p>
     * 
     * <p>Example:
     * Suppose you have a cube with 3 dimensions which have the following names
     * and elements. 
     * <ul>
     * <li><p>
     * &quot;Dim1&quot; with elements &quot;a1&quot;, &quot;a2&quot;, &quot;a3&quot; 
     * </p></li>
     * <li><p>
     * &quot;Dim2&quot; with elements &quot;b1&quot;, &quot;b2&quot;, &quot;b3&quot;, &quot;b4&quot;,   
     * </p></li>
     * <li><p>
     * &quot;Dim3&quot; with elements &quot;c1&quot;, &quot;c2&quot;, &quot;c3&quot;  
     * </p></li>
     * </ul>
     * 
     * As order matters, these dimensions would be returned in the above order when invoking
     * {@link #getDimensions()} on the cube.
     * 
     * The following query
     * 
     * <pre>
     * cube.getDataArray(new String[][] {
     *   new String[] { "a1", "a2" },  // 2 elements
     *   new String[] { "b1", "b2" },  // 2 elements
     *   new String[] { "c1" },        // 1 element
     * });
     * </pre>
     * 
     * would return 2 * 2 * 1 = 4 results. Which correspond to the coordinates
     * 
     * <pre>
     *   ("a1", "b1", "c1" ),
     *   ("a2", "b1", "c1" ),
     *   ("a1", "b2", "c1" ),
     *   ("a2", "b2", "c1" )
     * </pre>
     * 
     * A second example shows the definition of a cell area:
     * <pre>
     * cube.getDataArray(new String[][] {
     *   new String[] { "a1", "a2", "a3" },  // 3 elements
     *   new String[] { "b1", "b2", "b3" },  // 3 elements
     *   new String[] { "c1" },              // 1 element
     * });
     * </pre>
     * 
     * would return 3 * 3 * 1 = 9 results. Which correspond to the coordinates
     * 
     * <pre>
     *   ("a1", "b1", "c1" ), ("a2", "b1", "c1" ), ("a3", "b1", "c1" ),
     *   ("a1", "b2", "c1" ), ("a2", "b2", "c1" ), ("a3", "b2", "c1" ),
     *   ("a1", "b3", "c1" ), ("a3", "b3", "c1" ), ("a3", "b3", "c1" )
     * </pre>
     * (The above sequence is from left to right and top to bottom)
     * </p>
     * 
     * @param elements an array of string arrays that specifies
     * the data to read.
     * @return the values stored at the determined data-cells.
     */
    Object[] getDataArray(String elements[][]);
    
    

    /**
     * Returns the data stored at the given coordinates. The coordinates
     * are specified as {@link Element} instance. This method is similar
     * to {@link #getData(String[])}, the only difference is that the
     * coordinates are now specified as element instances and not as 
     * names of elements. All preconditions of {@link #getData(String[])}
     * apply to this method as well. 
     * 
     * 
     * @param coordinates the coordinate array identifying the
     * data cell.
     * @return the value stored at the specified data-cell.
     */
    Object getData(Element coordinates[]);


    /**
     * Returns the data stored at the given coordinates. The coordinates
     * are specified as {@link Element} instance. This method is similar
     * to {@link #getDataArray(String[][])}, the only difference is that the
     * coordinates are now specified as element instances and not as 
     * names of elements. All preconditions of {@link #getDataArray(String[][])}
     * apply to this method as well. <br/>
     * <b>Note:</b> using this method it is possible to define an area of cells
     * which values are returned. If an area is not required it is probably 
     * easier to use {@link #getDataBulk(Element[][])}.
     * 
     * @see #getDataArray(String[][])
     * @see #getDataBulk(Element[][])
     * 
     * @param elements an array of {@link Element}-arrays that specifies
     * the data to read.
     * data cell.
     * @return the values stored at the specified data-cells.
     */
    Object[] getDataArray(Element elements[][]);
    
    /**
     * Returns the data stored at the given coordinates. The coordinates are 
     * specified as {@link Element} instances. This method behaves differently
     * from {@link #getDataArray(Element[][])} because it returns only the
     * values from those cells which are specified, i.e. no area could be 
     * defined
     * 
     * @param coordinates an array of {@link Element}-coordinates that specifies
     * the data to read.
     * @return the values stored at the specified cells.
     */
    Object[] getDataBulk(Element[][] coordinates);
    
    
    /**
     * Sets the data for the given coordinates. The coordinates
     * are specified as strings which refer to element names.
     * Consolidated coordinates will not allow writing of data.
     * 
     * @param coordinates the string array identifying the
     * data cell.
     * @param value the value to set, the type of the value must
     * match the element-type.
     */
    void setData(String coordinates[], Object value);
    
    /**
     * Sets the data for the given coordinates. The coordinates
     * are specified as {@link Element} instance.
     * Consolidated coordinates will not allow writing of data.
     * 
     * @param coordinates the {@link Element} array identifying the
     * data cell.
     * @param value the value to set, the type of the value must
     * match the element-type.
     */
    void setData(Element coordinates[], Object value);
    
    /**
     * Sets the data for the given coordinate. This method determines the 
     * splashmode from the specified value which could be optional prefixed by
     * splash parameters. 
     * <p>
     * Following splash parameters are supported:
     * <ul>
     * <li>#...<p>sets the consolidated value to specified value. Used splashmode is {@link Cube#SPLASHMODE_DEFAULT}</p></li>
     * <li>#...%<p>adds the given percent value to the consolidated value. Used splashmode is {@link Cube#SPLASHMODE_DEFAULT}</p></li>
     * <li>!!...<p>adds the given value to all base cells of this consolidated cell. Used splashmode is {@link Cube#SPLASHMODE_BASE_ADD}</p></li>
     * <li>!...<p>sets value of all base cells to the given value. Used splashmode is {@link Cube#SPLASHMODE_BASE_SET}</p></li>
     * </ul>
     * </p> 
     * <p>
     * As an example a specified value of #100 would set the consolidated cell 
     * value to 100 and distribute this to all its base cells according to 
     * their weights. An additional #19% would set the consolidated cell value 
     * to 119 and adjust the base cells accordingly. 
     * </p>
     * 
     * <p>If given value was not prefixed with a splash parameter 
     * {@link Cube#SPLASHMODE_DEFAULT} is used as splash mode. In this case 
     * calling  this method is equal to 
     * {@link Cube#setDataSplashed(Element[], Object, int)}</p> 
     * @param coordinate the cell coordinate
     * @param value the value to set optional prefixed by a splash parameter. 
     */
    void setDataSplashed(Element[] coordinate, Object value);

    /**
     * Sets the data for the given coordinate. This method determines the 
     * splashmode from the specified value which could be optional prefixed by
     * splash parameters. 
     * <p>
     * Following splash parameters are supported:
     * <ul>
     * <li>#...<p>sets the consolidated value to specified value. Used splashmode is {@link Cube#SPLASHMODE_DEFAULT}</p></li>
     * <li>#...%<p>adds the given percent value to the consolidated value. Used splashmode is {@link Cube#SPLASHMODE_DEFAULT}</p></li>
     * <li>!!...<p>adds the given value to all base cells of this consolidated cell. Used splashmode is {@link Cube#SPLASHMODE_BASE_ADD}</p></li>
     * <li>!...<p>sets value of all base cells to the given value. Used splashmode is {@link Cube#SPLASHMODE_BASE_SET}</p></li>
     * </ul>
     * </p> 
     * <p>
     * As an example a specified value of #100 would set the consolidated cell 
     * value to 100 and distribute this to all its base cells according to 
     * their weights. An additional #19% would set the consolidated cell value 
     * to 119 and adjust the base cells accordingly. 
     * </p>
     * 
     * <p>If given value was not prefixed with a splash parameter 
     * {@link Cube#SPLASHMODE_DEFAULT} is used as splash mode. In this case 
     * calling  this method is equal to 
     * {@link Cube#setDataSplashed(Element[], Object, int)}</p> 
     * @param coordinate the cell coordinate
     * @param value the value to set optional prefixed by a splash parameter.
     * @param formatter the <code>NumberFormat</code> to use to format the value.
     * If <code>null</code> is specified no formatting is applied. 
     * @deprecated PLEASE DON'T USE! SUBJECT TO CHANGE!
     */
    void setDataSplashed(Element[] coordinate, Object value, NumberFormat formatter);
    /**
	 * @deprecated PLEASE DON'T USE! SUBJECT TO CHANGE!
	 */
	void setData(Element[] coordinate, Object value, NumberFormat formatter);
    
    /**
     * Sets the data for the given coordinates. This method is
     * identical to {@link #setData(String[], Object)} except
     * that the splashing behavior is explicitly specified.
     * 
     * <p>
     * The splashMode parameter must be set to one of the constants
     * defined in the <code>Cube</code> class.
     * </p>
     * 
     * @param coordinates the string array identifying the
     * data cell.
     * @param value the value to set, the type of the value must
     * match the element-type.
     * @param splashMode the splashMode to use.
     */
    void setDataSplashed(String coordinates[], Object value, int splashMode);
    
    /**
     * Sets the data for the given coordinates. This method is
     * identical to {@link #setData(String[], Object)} except
     * that the splashing behavior is explicitly specified.
     * 
     * <p>
     * The splashMode parameter must be set to one of the constants
     * defined in the <code>Cube</code> class.
     * </p>
     * 
     * @param coordinates the {@link Element} array identifying the
     * data cell.
     * @param value the value to set, the type of the value must
     * match the element-type.
     * @param splashMode the splashMode to use.
     */
    void setDataSplashed(Element coordinates[], Object value, int splashMode);
    
    
    /**
     * Sets the data for multiple cells, specified by the given coordinates. 
     * <p>
     * Note that this method is not the opposite of getDataArray() since it does 
     * not expect a defined area of cells. Instead each cell must be defined
     * by a coordinate as an array of elements.
     * </p> 
     * @param coordinates an array of {@link Element} coordinates which identify
     * each cell 
     * @param values the cell values to set
     * @param splashMode the splashing mode to use
     */
    void setDataArray(Element[][] coordinates, Object[] values, int splashMode);

    
    //--------------------------------------------------------------------------
    //PALO 1.5 - PART OF ATTRIBUTE API...
    //
    /**
     * Checks if this <code>Cube</code> is an attribute cube, i.e.
     * its cells represent <code>Attribute</code> values.
     * @return true if this cube is an attribute cube, false otherwise
     */
    boolean isAttributeCube();

    /**
     * Checks if this <code>Cube</code> is a subset cube, i.e.
     * its cells represent <code>Subset</code> definitions.
     * @return true if this cube is a subset cube, false otherwise
     */
    boolean isSubsetCube();
    
    //--------------------------------------------------------------------------
    //PALO 1.5 - PART OF VIEW API...
    //
    /**
     * Checks if this <code>Cube</code> is a view cube, i.e.
     * its cells represent <code>CubeView</code> definitions.
     * @return true if this cube is a view cube, false otherwise
     */
    boolean isViewCube();
    
    /**
     * Adds the specified cube view to this cube. 
     * <p>
     * <b>Note:</b> if a view with the given id was already added before this
     * method throws an {@link PaloAPIException}
     * </p> 
     * @param id an unique identifier for the new view
     * @param name the view name
     * @param properties array of Property objects for the cube view.
     * @return the new cube view instance
     * @deprecated please use {@link #addCubeView(String, Property[])} instead
     */
    CubeView addCubeView(String id, String name, Property [] properties);
    
    /**
     * Adds a new cube view to this cube. It is encouraged to use this method
     * instead of {@link #addCubeView(String, String, boolean)}.
     * @param name the view name
     * @param properties array of Property objects for the cube view.
     * @return the new cube view instance
     */
    CubeView addCubeView(String name, Property [] properties);
    
    
//    /**
//     * Adds the specified cube view to this cube. 
//     * <p>
//     * <b>Note:</b> if a view with the given id was already added before this
//     * method throws an {@link PaloAPIException}
//     * </p> 
//     * @param id an unique identifier for the new view
//     * @param name the view name
//     * @param hideEmpty empty rows and columns
//     * @return the new cube view instance
//     * @deprecated please use {@link #addCubeView(String, Property[])} instead
//     */
//    CubeView addCubeView(String id,String name,boolean hideEmpty);
    
//    /**
//     * Adds a new cube view to this cube. It is encouraged to use this method
//     * instead of {@link #addCubeView(String, String, boolean)}.
//     * @param name the view name
//     * @param hideEmpty flag for hiding empty rows and columns
//     * @return the new cube view instance
//     * @deprecated please use {@link #addCubeView(String, Property[])} instead.
//     */
//    CubeView addCubeView(String name, boolean hideEmpty);

    /**
     * Removes the given cube view instance from the cube
     * @param view the cube view to remove
     * @throws PaloObjectNotFoundException if corresponding palo object could
     * not be found
     */
    void removeCubeView(CubeView view);
    
    /**
     * Returns the cube view which is registered with the given id or 
     * <code>null</code> if no such view exists.<br/>
     * @param id identifier of the view to load
     * @return the cube view or <code>null</code>
     * @throws PaloIOException to signal the occurrence of errors during 
     * loading of <code>CubeView</code>
     */
    CubeView getCubeView(String id) throws PaloIOException;

    
    /**
     * Returns the ids of all registered <code>CubeView</code>s
     * @return
     */
    String[] getCubeViewIds();

    /**
     * Rturns the name of the <code>CubeView</code> which corresponds to the
     * given id or <code>null</code> if no corresponding view exists
     * @param id a valid view identifier
     * @return the <code>CubeView</code> name or <code>null</code>
     */
    String getCubeViewName(String id);
    
//    /**
//     * Returns the view which is registered under the given name. If no such
//     * view exists <code>null</code> is returned.
//     * @param name a valid view name
//     * @return the corresponding <code>CubeView</code> instance or 
//     * <code>null</code>
//     * @throws PaloIOException to signal the occurrence of errors during 
//     * loading of <code>CubeView</code>
//     */
//    CubeView getCubeViewByName(String name) throws PaloIOException;
    
    /**
     * Returns the number of views which are registered with this 
     * <code>Cube</code>.
     * @return the number of views of this <code>Cube</code>
     */
    int getCubeViewCount();
    
    /**
     * Returns all registered cube views. This may include views which could 
     * not be loaded completely.<br/>
     * <b>Note:</b> use {@link #registerViewObserver(PersistenceObserver)} to 
     * monitor loading of cube views and to get notified about any errors 
     * during the load process
     * @return the registered <code>CubeView</code>s
	 * @deprecated please use {@link #getCubeViews(PersistenceObserver)} instead
     */
    CubeView[] getCubeViews();

    void getCubeViews(PersistenceObserver observer);
    
    
    //--------------------------------------------------------------------------
    // Export Context
    //
    
    /**
     * Returns the default <code>IExportContext</code> which effects all cube
     * elements.
     */
    ExportContext getExportContext();
    
    /**
     * Returns an <code>IExportContext</code> which effects only the cube 
     * elements which are specified by the given area paramter
     * @param area
     * @return <code>ExportContext</code>
     */
    ExportContext getExportContext(Element[][] area);
    

    /**
     * Exports the dataset using given {@link ExportContext}. If the specified
     * context is <code>null</code>, he default context is used which effects
     * all cube elements.
     * To get the internally used context use {@link #getExportContext()}, for 
     * the default context, or {@link #getExportContext(Element[][])} 
     * respectively.
     *
     * @return a dataset with exported cube {@link Cell}s 
     */
    ExportDataset getDataExport(ExportContext context);

    /**
     * Adds the given values to the existing values of the specified cells.
     * If a cell has no value then it is set. 
     * <p>
     * <b>Note:</b> the splash mode must be either DISABLED, DEFAULT or ADD.
     * </p> 
     * @param coordinates specify the cells to add the values to
     * @param values  the cell value to add or set
     * @param splashMode either DISABLED, DEFAULT or ADD
     */
    void addDataArray(Element[][] coordinates, Object[] values, int splashMode);
    
    /**
     * Sets the data for multiple cells, specified by the given coordinates.
     * Additionally one can define if the given values should be added to 
     * existing ones and if the external supervision server should be notified,
     * which is the default.
     * <b>Note:</b> to disable the notification of the supervision server admin
     * rights are required.
     * <p>
     * The supervision server is an external process which listens to changes
     * within a certain area of a palo cube. For more detailed information 
     * please refer to <code>www.jedox.com</code>
     * </p>
     *  
     * @param coordinates specify the cells to change
     * @param values the values to set or add  
     * @param add set to true to add the given values, to false otherwise
     * @param splashMode if values should be added then the splash mode has to
     * be either DISABLED, DEFAULT or ADD
     * @param notifyEventProcessors set to false to disable the notification of
     * the external event processors, true otherwise. Changing the default
     * requires admin user rights.
     */
    void setDataArray(Element[][] coordinates, Object[] values, boolean add, int splashMode, boolean notifyEventProcessors);
    
    /**
     * Returns all rules which are defined for the current cube
     * @return all defined rules
     */
    Rule[] getRules();
    /**
     * Adds the given rule definition to this cube instance
     * @param definition a valid rule definition
     * @return a new rule instance represented by this definition 
     */
    Rule addRule(String definition);
    
    /**
     * Adds the given rule definition to this cube instance
     * @param definition a valid rule definition
     * @param externalIdentifier an optional external identifier 
     * @param useIt set to <code>true</code> to use external identifier, otherwise to <code>false</code>
     * @param comment an optional comment for this rule
     * @return a new rule instance represented by this definition
     */
    Rule addRule(String definition, String externalIdentifier, boolean useIt, String comment);
    /**
     * Adds the given rule definition to this cube instance
     * @param definition a valid rule definition
     * @param externalIdentifier an optional external identifier 
     * @param useIt set to <code>true</code> to use external identifier, otherwise to <code>false</code>
     * @param comment an optional comment for this rule
     * @param activate specify if the added rule should be active or not
     * @return a new rule instance represented by this definition
     */
    Rule addRule(String definition, String externalIdentifier, boolean useIt, String comment, boolean activate);
    
    /**
     * Removes the given rule from this cube instance
     * @param rule the rule to remove
     * @return true if deleting the rule was successful, false otherwise
     */
    boolean removeRule(Rule rule);
    
	/**
	 * Removes the rule which corresponds to the given id from this cube 
	 * instance. This method is useful to delete rules on the server which
	 * could not be loaded. 
	 * However, the preferred way to delete a rule is to use 
	 * {@link #removeRule(Rule rule)}.
	 * @param ruleId the identifier of the rule to delete
	 * @return
	 */
	boolean removeRule(String ruleId);
    
    /**
     * Returns the rule which determines the value of the cell specified by the
     * given coordinate or <code>null</code> if no rule was defined for this cell
     * @param coordinate the cell coordinate
     * @return rule instance which determines cell value or <code>null</code>
     * if none was specified
     * @throws PaloAPIException if rule loading fails
     */
    Rule getRule(Element[] coordinate);
    
    /**
     * Signals if this <code>Cube</code> instance represents a so called system
     * cube. <b>NOTE:</b> native support for system cubes is only available
     * since palo server 1.5
     * @return <code>true</code> if this cube is a system cube, 
     * <code>false</code> otherwise 
     */
	public boolean isSystemCube();    
	
    /**
     * Signals if this <code>Cube</code> instance represents a so called user
     * info cube. <b>NOTE:</b> native support for system cubes is only available
     * since palo server 2.0
     * @return <code>true</code> if this cube is a user info cube, 
     * <code>false</code> otherwise 
     */
	public boolean isUserInfoCube();
	
	/**
	 * Returns the type of this cube. This is one of the constants defined in
	 * this class.
	 * 
	 * @return type of this cube.
	 */
	public int getType();
	
	/**
	 * Registers the given {@link PersistenceObserver} to the list of observers
	 * which monitor the loading and saving process of cube views. The observer 
     * will be notified about any load success or fail. 
	 *  
	 * @param cubeViewObserver the {@link PersistenceObserver} to register
	 * @deprecated please use {@link #getCubeViews(PersistenceObserver)} instead
	 */
	void registerViewObserver(PersistenceObserver cubeViewObserver);
	
	/**
	 * Removes given {@link PersistenceObserver} from the list of observers
	 * @param cubeViewObserver the {@link PersistenceObserver} to remove
	 * @deprecated please use {@link #getCubeViews(PersistenceObserver)} instead
	 */
	void unregisterViewObserver(PersistenceObserver cubeViewObserver);
	
    /**
     * Renames this <code>Cube</code>.
     * @param name the new name for this <code>Cube</code>.
     */
	void rename(String newName);
	
	/**
	 * Clears the complete cube, i.e. all its cells are cleared
	 */
	void clear();
	/**
	 * Clears the cube data specified by the given area. The area is described 
	 * by the cartesian product of the given <code>Element</code> coordinates.
	 * Please refer to {@link #getDataArray(String[][])} for a description of a
	 * correct area definition. 
	 * @param area an array of coordinates which define the area of cube data
	 * to be cleared
	 */
	void clear(Element[][] area);
	
	/**
	 * Converts a normal cube type to a gpu type, or vice versa.
	 * 
	 * @param type the type to which the cube is to be converted, must be one
	 * of Cube.TYPE_NORMAL or Cube.TYPE_GPU
	 */
	void convert(int type);
	
    /**
     * Returns all ids of properties that can be set for this cube. If no
     * properties are known to this cube, an empty array is returned.
     * 
     * @return all property ids that are understood by this cube.
     */
    String [] getAllPropertyIds();
    
    /**
     * Returns the property identified by the given id. All valid ids can be
     * requested by a call to getAllPropertyIds.
     * 
     * @param id the id of the property to read.
     * @return the property for the given id.
     */
    Property2 getProperty(String id);

    /**
     * Adds the given property to the list of properties for this cube.
     * 
     * @param property the property to add.
     */    
    void addProperty(Property2 property);
    
    /**
     * Removes the given property from the list of properties for this
     * cube. If the specified id was not set, the call is ignored.
     * 
     * @param id the id of the property which is to be cleared.
     */        
    void removeProperty(String id);
    
    /**
     * Returns all hierarchies of this cube. For a palo connection, all
     * hierarchies have exactly one dimension, for xmla connections that
     * does not need to be true, here a hierarchy can have more than one
     * dimension.
     * 
     * @return all hierarchies of this cube.
     */
    //Hierarchy [] getHierarchies();
    
    //NEW CELL API WHICH REPLACES LEGACY GET_DATA API:
//    /** @deprecated please don't use. subject to change */
//    void copy(Cell from, Cell to);
////    void update(Cell cell);
    
    /**
     * Returns the {@link Cell} object for the given cube coordinate.
     * Please note that cells are not cached by the API! Each invocation of 
     * this method will create new cell instance!
     * @return the cell specified by the coordinate
     */ 
    Cell getCell(Element[] coordinate);
    /**
     * Returns the {@link Cell} objects for the given cube coordinates.
     * Please note that cells are not cached by the API! Each invocation of 
     * this method will create new cell instances!
     * @param coordinates an array of <code>Element</code> coordinates which
     * specify the cells to return
     * @return the cells at the given coordinates
     */ 
    Cell[] getCells(Element[][] coordinates);
    Cell[] getCells(Element[][] coordinates, boolean hideEmptyCells);
    
    /**
     * Returns the {@link Cell} objects for the cube area which is defined by 
     * the cartesian product of the given <code>Element</code> coordinates.
     * Please note that cells are not cached by the API! Each invocation of 
     * this method will create new cell instances!
     * @param coordinates an array of <code>Element</code> coordinates defining
     * the cube area.
     * @return the cells of the defined area.
     */ 
    Cell[] getCellArea(Element[][] coordinates);
    
    /**
     * Returns additional information about the cube.
     * @return additional information about the cube.
     */
    CubeInfo getInfo();
        
    /**
     * Returns the total number of {@link Cell}s this cube contains. 
     * @return total number of cells
     */
	public BigInteger getCellCount();
    /**
     * Returns the number of filled {@link Cell}s this cube contains. 
     * @return number of filled cells
     */
	public BigInteger getFilledCellCount();

//    void setData(Cell cell, Object value, int splashMode);
//    void setData(Element[] coordinate, Object value, int splashMode);
//    void setData(Cell[] cells, Object[] values, int splashMode);
//    void setData(Element[][] coordinates, Object values, int splashMode);
//    
//    void addData(Cell cell, Object value, int splashMode);
//    void addData(Element[] coordinate, Object value, int splashMode);
//    void addData(Cell[] cells,Object[] values, int splashMode);
//    void addData(Element[][] coordinates,Object[] values, int splashMode);
}
