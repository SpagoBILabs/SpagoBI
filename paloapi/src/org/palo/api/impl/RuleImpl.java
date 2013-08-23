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
* @author ArndHouben
*
* @version $Id: RuleImpl.java,v 1.11 2010/02/09 12:29:42 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.impl;

import java.util.HashMap;
import java.util.Map;

import org.palo.api.Cube;
import org.palo.api.Property2;
import org.palo.api.Rule;

import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.PropertyInfo;
import com.tensegrity.palojava.RuleInfo;
import com.tensegrity.palojava.loader.PropertyLoader;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * 
 * @author ArndHouben
 * @version $Id: RuleImpl.java,v 1.11 2010/02/09 12:29:42 PhilippBouillon Exp $
 */
class RuleImpl implements Rule {

	private final CubeImpl cube;
	private final RuleInfo ruleInfo;
	private final DbConnection dbConnection;
	private final PropertyLoader propertyLoader;
	private final Map <String, Property2Impl> loadedProperties;
	
	RuleImpl(DbConnection dbConnection, CubeImpl cube, RuleInfo ruleInfo) {
		this.cube = cube;
		this.ruleInfo = ruleInfo;
		this.dbConnection = dbConnection;
		this.loadedProperties = new HashMap <String, Property2Impl> ();
		this.propertyLoader = dbConnection.getTypedPropertyLoader(ruleInfo);
	}
	
	public final Cube getCube() {
		return cube;
	}

	public final String getDefinition() {
		return ruleInfo.getDefinition();
	}

//	public final String getFunctions() {
//		return ruleInfo.getFunctions();
//	}


	public final String getId() {
		return ruleInfo.getId();
	}

	public final RuleInfo getInfo() {
		return ruleInfo;
	}

	public final String getComment() {
		return ruleInfo.getComment();
	}

	public final String getExternalIdentifier() {
		return ruleInfo.getExternalIdentifier();
	}

	public void setComment(String comment) {
		dbConnection.update(ruleInfo, ruleInfo.getDefinition(), ruleInfo
				.getExternalIdentifier(), ruleInfo.useExternalIdentifier(),
				comment, ruleInfo.isActive());
		cube.fireRuleChanged(this, comment);
	}

	public void setDefinition(String definition) {
		dbConnection.update(ruleInfo, definition, ruleInfo
				.getExternalIdentifier(), ruleInfo.useExternalIdentifier(),
				ruleInfo.getComment(), ruleInfo.isActive());
		cube.fireRuleChanged(this, definition);
	}

//	public void setFunctions(String functions) {
//		dbConnection.update(ruleInfo, ruleInfo.getDefinition(), functions,
//				ruleInfo.getExternalIdentifier(), ruleInfo
//						.useExternalIdentifier(), ruleInfo.getComment());
//	}


	public void setExternalIdentifier(String externalId) {
		setExternalIdentifier(externalId, false);
	}

	public void setExternalIdentifier(String externalId, boolean useIt) {
		dbConnection.update(ruleInfo, ruleInfo.getDefinition(), externalId,
				useIt, ruleInfo.getComment(), ruleInfo.isActive());
		cube.fireRuleChanged(this, externalId);
	}

	public void update(String definition, 
			String externalIdentifier, boolean useIt, String comment) {
		this.update(definition, externalIdentifier, useIt, comment, isActive());
	}
	public void update(String definition, String externalIdentifier,
			boolean useIt, String comment, boolean activate) {
		dbConnection.update(ruleInfo, definition, externalIdentifier, useIt,
				comment, activate);
		cube.fireRuleChanged(this, null);
	}

	public void useExternalIdentifier(boolean useIt) {
		String extId = ruleInfo.getExternalIdentifier();
		if (extId == null || extId.equals(""))
			return;
		dbConnection.update(ruleInfo, ruleInfo.getDefinition(), ruleInfo.getExternalIdentifier(), useIt,
				ruleInfo.getComment(), ruleInfo.isActive());
	}

	public String[] getAllPropertyIds() {
		return propertyLoader.getAllPropertyIds();
	}

	public Property2 getProperty(String id) {
		PropertyInfo propInfo = propertyLoader.load(id);
		if (propInfo == null) {
			return null;
		}
		Property2 property = loadedProperties.get(propInfo.getId());
		if (property == null) {
			property = createProperty(propInfo);
		}

		return property;
	}
	
	public void addProperty(Property2 property) {
		if (property == null) {
			return;
		}
		Property2Impl _property = (Property2Impl)property;
		propertyLoader.loaded(_property.getPropInfo());
		loadedProperties.put(_property.getId(), _property);
	}
	
	public void removeProperty(String id) {
		Property2 property = getProperty(id); 
		if (property == null) {
			return;
		}
		if (property.isReadOnly()) {
			return;
		}
		loadedProperties.remove(property);
	}
	
	public final boolean isActive() {
		return ruleInfo.isActive();
	}

	public final void setActive(boolean activate) {
		dbConnection.update(ruleInfo, ruleInfo.getDefinition(), ruleInfo.getExternalIdentifier(), ruleInfo.useExternalIdentifier(),
				ruleInfo.getComment(), activate);
		cube.fireRuleChanged(this, activate);
	}
	
	public final long getTimestamp() {
		return ruleInfo.getTimestamp();
	}

	final void clearCache() {
		for(Property2Impl property : loadedProperties.values())
			property.clearCache();
		loadedProperties.clear();
		propertyLoader.reset();
	}
	private void createProperty(Property2 parent, PropertyInfo kid) {
		Property2 p2Kid = Property2Impl.create(parent, kid);
		parent.addChild(p2Kid);		
		for (PropertyInfo kidd: kid.getChildren()) {
			createProperty(p2Kid, kidd);
		}
	}
	
	private Property2 createProperty(PropertyInfo propInfo) {
		Property2 prop = Property2Impl.create(null, propInfo);
		for (PropertyInfo kid: propInfo.getChildren()) {
			createProperty(prop, kid);
		}
		return prop;
	}

}
