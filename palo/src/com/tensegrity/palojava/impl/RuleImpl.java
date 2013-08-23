/*
*
* @file RuleImpl.java
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
* @version $Id: RuleImpl.java,v 1.7 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palojava.impl;

import com.tensegrity.palojava.CubeInfo;
import com.tensegrity.palojava.RuleInfo;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * 
 * @author Arnd Houben
 * @version $Id: RuleImpl.java,v 1.7 2009/04/29 10:35:49 PhilippBouillon Exp $
 */
public class RuleImpl implements RuleInfo {
	
	private final String id;
	private final CubeInfo cube;
	private String definition;	
	private long timestamp;
//	private String functions;
	
	//optional fields:
	private String comment;
	private String externalId;
	private boolean useExternalId;
	private boolean active = true; // a rule is active by default
	
	public RuleImpl(CubeInfo cube, String id) {
		this.cube = cube;
		this.id = id;
	}
	
	public final CubeInfo getCube() {
		return cube;
	}

	public final String getDefinition() {
		return definition;
	}
	
//	public final String getFunctions() {
//		return functions;
//	}

	public final String getExternalIdentifier() {
		return externalId;
	}

	public final String getId() {
		return id;
	}

	public final int getType() {
		return UNDEFINED;
	}

	public final String getComment() {
		return comment;
	}

	public final void setComment(String comment) {
		this.comment = comment;
	}

	public final void setDefinition(String definition) {
		this.definition = definition;
	}
	
//	public final void setFunctions(String functions) {
//		this.functions = functions;
//	}
	
	public final void setExternalIdentifier(String externalId) {
		this.externalId = externalId;
	}
	
	public final void useExternalIdentifier(boolean b) {
		useExternalId = b;
	}
		
	public final boolean useExternalIdentifier() {
		return useExternalId;
	}

	public boolean canBeModified() {
		return true;
	}

	public boolean canCreateChildren() {
		return true;
	}
	
	public final boolean isActive() {
		return active;
	}
	public final void setActive(boolean activate) {
		active = activate;
	}
	
	public final long getTimestamp() {
		return timestamp;
	}
	/**
	 * Sets the creation time of this rule in milliseconds since 1970-01-01
	 * @param timestamp creation time in milliseconds
	 */
	public final void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
