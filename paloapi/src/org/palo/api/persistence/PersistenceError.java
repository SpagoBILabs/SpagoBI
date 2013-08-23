/*
*
* @file PersistenceError.java
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
* @version $Id: PersistenceError.java,v 1.5 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.persistence;

import org.palo.api.CubeView;
import org.palo.api.Subset;

/**
 * A <code>PersistenceError</code> describes an error which could happen 
 * during the loading or saving of jpalo subjects like 
 * <code>{@link Subset}</code>s or <code>{@link CubeView}</code>s. This class 
 * provides methods and constants to get detailed information about what went
 * wrong...  
 *<p>
 *The example below shows how to receive more information on an 
 *<code>UNKNOWN_ELEMENT</code> error during cube view loading:
 *<blockquote><pre>
 * if(error.getType() == PersistenceError.UNKNOWN_ELEMNT) {
 *	CubeView view = (CubeView)error.getSource();
 *	String missingElementID = error.getCause();		//get error cause
 * 	Dimension dimension = (Dimension)error.getLocation();	//at location
 *	System.err.println("Error during loading of cube view: "+view.getName());
 * 	System.err.println("Could not find element with id '"+missingElementID+"' in dimension '"+dimension.getName()+"'!!");
 * 	if(error.getTargetType() == PersistenceError.TARGET_EXPANDED_PATH) {
 * 		System.err.println("Element was part of expanded path!");
 * 		Object section = error.getSection();
 * 		if(section instanceof Axis) 
 * 			System.err.println("\tof axis: "+((Axis)section).getId();
 * 	}
 * }
 *</pre></blockquote>
 *<p>
 * @author ArndHouben
 * @version $Id: PersistenceError.java,v 1.5 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
public interface PersistenceError {
	
	//currently known errors:
	public static final int UNKNOWN_ERROR = -1;
	public static final int UNKNOWN_DIMENSION = 0;
	public static final int UNKNOWN_ELEMENT = 1;
	public static final int UNKNOWN_CUBE_VIEW = 2;
	public static final int UNKNOWN_AXIS = 4;
	public static final int UNKNOWN_SUBSET = 8;
	public static final int UNKNOWN_PATH = 16;
	public static final int LOADING_FAILED = 32;
	
	public static final int TARGET_UNKNOWN = -1;
	public static final int TARGET_GENERAL = 0;
	public static final int TARGET_SELECTED = 1;
	public static final int TARGET_EXPANDED_PATH = 2;
	public static final int TARGET_HIDDEN_PATH = 4;
	public static final int TARGET_SUBSET = 8;
	
	
	//TODO save
	
	
	public static final int[] ALL_ERROR_TYPES = new int[] { 
		UNKNOWN_ERROR,		
		UNKNOWN_DIMENSION, 
		UNKNOWN_ELEMENT,
		UNKNOWN_CUBE_VIEW,
		UNKNOWN_AXIS,		 
		UNKNOWN_SUBSET,
		UNKNOWN_PATH,
		LOADING_FAILED };
	
	
	
	/**
	 * Returns the source which causes this error, e.g. a 
	 * <code>{@link CubeView}</code> or a <code>{@link Subset}</code>
	 * If no source was determined <code>null</code> is returned
	 * @return the error source or <code>null</code>
	 */
	public Object getSource();
	
	/**
	 * Returns the id of the source which causes this error, e.g. the id of a
	 * <code>{@link CubeView}</code>. Note that if <code>getSource()</code> 
	 * returns <code>null</code> that does not automatically mean that this 
	 * method returns <code>null</code> too!
	 * @return the id of the error source instance or <code>null</code>
	 */
	public String getSourceId();
	
	/**
	 * Returns the location where this error was initiated, e.g. if a dimension 
	 * could not be found this will return its containing database
	 * @return the location where this error started or <code>null</code> if 
	 * none could be determined
	 */
	public Object getLocation();
	
	
	/**
	 * Returns the cause of this error depending on the type of error. E.g. if a 
	 * dimension could not be found this method will return the dimension id. 
	 * @return the cause of this error or <code>null</code> if it could not be
	 * determined
	 */
	public String getCause();
	
	/**
	 * Returns the section where the error occurs, e.g. if the error source is 
	 * a <code>{@link CubeView}</code> the section could be an 
	 * <code>{@link Axis}</code>. If no section could be determined 
	 * <code>null</code> is returned.
	 * @return the error section or <code>null</code>
	 */
	public Object getSection();

	/**
	 * Returns one of the defined target constants which describes the target
	 * at which the error occurred, e.g. if the error is of type UNKNOWN_ELEMENT 
	 * the target type could be a TARGET_EXPANDED_PATH meaning that the unknown 
	 * element was detected at an expanded path.
	 * @return defined target type constants
	 */
	public int getTargetType();
	
	
	/**
	 * Returns one of the defined error constants to describe the kind of
	 * error
	 * @return one of the defined error constants
	 */
	public int getType();
	
	
	/**
	 * Returns the error message
	 * @return error message
	 */
	public String getMessage();

}
