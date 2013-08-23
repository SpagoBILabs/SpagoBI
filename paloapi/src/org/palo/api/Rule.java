/*
*
* @file Rule.java
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
* @version $Id: Rule.java,v 1.10 2009/10/27 08:33:18 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api;

import com.tensegrity.palojava.RuleInfo;

/**
 * The <code>Rule</code> interface represents an enterprise rule definition for
 * a palo {@link <code>Cube</code>}. It is characterized by its id and a raw
 * rule definition. Furthermore a rule corresponds to only one cube.
 *  
 * 
 * @author ArndHouben
 * @version $Id: Rule.java,v 1.10 2009/10/27 08:33:18 PhilippBouillon Exp $
 */
public interface Rule {

	/**
	 * Returns the unique identifier for this rule instance
	 * @return the rule id
	 */
	String getId();
	
	/**
	 * Returns the <code>Cube</code> which is affected by this rule 
	 * @return the affected cube
	 */
	Cube getCube();

	/**
	 * Sets a definition for this rule. 
	 * <b>NOTE:</b> this will update current rule and therefore performs a 
	 * server request!
	 * @param definition new rule definition
	 */
	void setDefinition(String definition);

	/**
	 * Returns the rule definition, i.e. its textual representation
	 * @return the rule definition
	 */
	String getDefinition();
	
	/**
	 * Returns the creation time of this rule in milliseconds since 1970-01-01
	 * @return the creation time in milliseconds since 1970-01-01
	 */
	long getTimestamp();

//	/**
//	 * Sets the functions used by this rule <b>NOTE:</b> this will update 
//	 * current rule and therefore performs a server request!
//	 * @param functions comma separated list of function names
//	 */
//	void setFunctions(String functions);
//	
//	/**
//	 * Returns a comma separated list of function names used by this rule. If 
//	 * no functions are used this method returns <code>null</code>
//	 * @return function names or <code>null</code>
//	 */
//	String getFunctions();
	
	
	/**
	 * Sets an optional comment for this rule. <b>NOTE:</b> this will update 
	 * current rule and therefore performs a server request!
	 * @param comment a rule comment
	 */
	void setComment(String comment);
	
	/**
	 * Returns an optional comment for this rule or <code>null</code> if none
	 * was set.
	 * @return the rule comment or <code>null</code> if none was set
	 */
	String getComment();
		
	/**
	 * Sets a new external identifier string to use inside rule definition 
	 * instead of definition name.
	 * <b>NOTE:</b> this will update current rule and therefore performs a 
	 * server request!
	 * @param externalId the identifier to use
	 */
	void setExternalIdentifier(String externalId);
	
	/**
	 * Sets a new external identifier string and use it inside rule definition
	 * <b>NOTE:</b> this will update current rule and therefore performs a 
	 * server request!
	 * @param externalId the identifier to use
	 * @param useIt set to <code>true</code> if new identifier should be used
	 * in rule defintion, to <code>false</code> otherwise
	 */
	void setExternalIdentifier(String externalId, boolean useIt);
	
	/**
	 * Returns the optional external identifier or <code>null</code> if none
	 * was set
	 * @return external identifier or <code>null</code>
	 */
	String getExternalIdentifier();
	
	/**
	 * En- or disables the usage of a specified external identifier. If no 
	 * identifier was specified calling this method has no effect.
	 * <b>NOTE:</b> this will update current rule and therefore performs a 
	 * server request!
	 */
	void useExternalIdentifier(boolean useIt);
	
	/**
	 * Updates this rule with the given parameters.
	 * <b>NOTE:</b> this will update current rule and therefore performs a
	 * server request! 
	 * @param definition the new rule definition
	 * @param externalIdentifier the new rule external identifier
	 * @param useIt specify if external identifier should be used
	 * @param comment a comment
	 */
	void update(String definition, String externalIdentifier, boolean useIt, String comment);
	/**
	 * Updates this rule with the given parameters.
	 * <b>NOTE:</b> this will update current rule and therefore performs a
	 * server request! 
	 * @param definition the new rule definition
	 * @param externalIdentifier the new rule external identifier
	 * @param useIt specify if external identifier should be used
	 * @param comment a comment
	 * @param activate specify if this rule should be activated or deactivated
	 */
	void update(String definition, String externalIdentifier, boolean useIt, String comment, boolean activate);
	/**
	 * Returns <code>true</code> if this rule is currently active, otherwise 
	 * <code>false</code>
	 * @return <code>true</code> if this rule is active, <code>false</code> 
	 * otherwise
	 */
	boolean isActive();
	/**
	 * Activates or deactivates this rule.
	 * <b>NOTE:</b> this will update current rule and therefore performs a 
	 * server request!
	 * @param activate specify <code>true</code> to activate this rule or 
	 * <code>false</code> to deactivate it.
	 */
	void setActive(boolean activate);
	
    /**
     * Returns additional information about the rule.
     * @return additional information about the rule.
     */
	RuleInfo getInfo();
}
