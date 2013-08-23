/*
*
* @file RuleInfo.java
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
* @version $Id: RuleInfo.java,v 1.7 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palojava;

/**
 * The <code>RuleInfo</code> is a representation of a palo <code>Rule</code> 
 * object. A palo rule belongs to a certain palo <code>Cube</code> and consists 
 * of an identifier and a definition. 
 * 
 * @author Arnd Houben
 * @version $Id: RuleInfo.java,v 1.7 2009/04/29 10:35:49 PhilippBouillon Exp $
 */
public interface RuleInfo extends PaloInfo {

	/** rules have no type */
	public static final int UNDEFINED = -1;
	
	/**
	 * Returns the rule definition
	 * @return rule definition
	 */
	public String getDefinition();
	
//	/**
//	 * Returns a comma separated list of function names used by this rule
//	 * @return function names
//	 */
//	public String getFunctions();
//	
	/**
	 * Returns the palo <code>Cube</code> representation to which this rule
	 * belongs.
	 * @return <code>Cube</code> representation which contains this rule
	 */
	public CubeInfo getCube();
	
	public String getExternalIdentifier();
	
	public String getComment();

	/**
	 * Returns the creation time of this rule in milliseconds since 1970-01-01.
	 * @return creation time of this rule in milliseconds since 1970-01-01
	 */
	public long getTimestamp();
	
	public boolean useExternalIdentifier();
	
	/**
	 * Returns <code>true</code> if this rule is active, otherwise 
	 * <code>false</code>
	 * @return <code>true</code> if this rule is active, <code>false</code> 
	 * otherwise
	 */
	public boolean isActive();
	/**
	 * Activates or deactivate this rule.
	 * @param activate specify <code>true</code> to activate this rule or 
	 * <code>false</code> to deactivate it.
	 */
	public void setActive(boolean activate);
	
}
