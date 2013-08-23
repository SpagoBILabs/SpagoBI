/*
*
* @file RuleLoader.java
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
* @version $Id: RuleLoader.java,v 1.10 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palojava.loader;

import com.tensegrity.palojava.CubeInfo;
import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.ElementInfo;
import com.tensegrity.palojava.PaloException;
import com.tensegrity.palojava.PaloInfo;
import com.tensegrity.palojava.RuleInfo;
import com.tensegrity.palojava.ServerInfo;

/**
 * <p><code>RuleLoader</code></p>
 * This abstract base class manages the loading of {@link RuleInfo} objects.
 *
 * @author ArndHouben
 * @version $Id: RuleLoader.java,v 1.10 2009/04/29 10:35:49 PhilippBouillon Exp $
 **/
public abstract class RuleLoader extends PaloInfoLoader {

	// Fix for PR 6732: Jedox Server does not support rules for this version.
	private final static int MIN_RULES_MAJOR = 1;
	private final static int MIN_RULES_MINOR = 5;
	private final static int MIN_RULES_BUILD = 1646;

	protected final CubeInfo cube;
	
	/**
	 * Creates a new loader instance.
	 * @param paloConnection
	 * @param cube
	 */
	public RuleLoader(DbConnection paloConnection, CubeInfo cube) {
		super(paloConnection);
		this.cube = cube;
	}

	/**
	 * Returns the identifiers of all rules currently known to the palo server.
	 * @return ids of all known palo rules
	 */
	public abstract String[] getAllRuleIds();

	/**
	 * Creates a new {@link RuleInfo} instance for the given rule definition
	 * @param definition a valid rule definition.
	 * @return a new <code>RuleInfo</code> object
	 */
	public final RuleInfo create(String definition) {
		return create(definition, null,false,null, true);
	}
	
	/**
	 * Creates a new {@link RuleInfo} instance for the given rule definition 
	 * and with the given external id and comment
	 * @param definition a valid rule definition
	 * @param externalId an optional external id
	 * @param useIt set to <code>true</code> if external id should be used, to
	 * <code>false</code> otherwise
	 * @param an optional comment for the rule
	 * @param activate specifies if the rule is active or not 
	 * @return a new <code>RuleInfo</code> object
	 */
	public final RuleInfo create(String definition, String externalId,
			boolean useIt, String comment, boolean activate) {
		checkRuleSupport();
		RuleInfo rule = paloConnection.createRule(cube, definition, externalId,
				useIt, comment, activate);
		loaded(rule);
		return rule;
	}
		
	/**
	 * Deletes the given <code>RuleInfo</code> instance from the palo server as
	 * well as from the internal cache.
	 * @param rule the <code>RuleInfo</code> instance to delete
	 * @return <code>true</code> if deletion was successful, <code>false</code>
	 * otherwise
	 */
	public final boolean  delete(RuleInfo rule) {
		if(paloConnection.delete(rule)) {
			removed(rule);
			return true;
		} 
		return false;
	}

	/**
	 * Deletes the <code>RuleInfo</code> which corresponds to the given id. The 
	 * difference to {@link #delete(RuleInfo)} is that this method can delete
	 * rules which could not be loaded and therefore no corresponding 
	 * <code>RuleInfo</code> instance exists.
	 * @param rule the <code>RuleInfo</code> instance to delete
	 * @return <code>true</code> if deletion was successful, <code>false</code>
	 * otherwise
	 */
	public final boolean delete(String ruleId) {
		if(paloConnection.delete(ruleId, cube)) {
			//remove a possible loaded info...
			loadedInfo.remove(ruleId);
			return true;
		} 
		return false;
		
	}

	/**
	 * Loads the <code>RuleInfo</code> object which corresponds to the given
	 * id
	 * @param id the identifier of the <code>RuleInfo</code> object to load
	 * @return the loaded <code>RuleInfo</code> object
	 */	
	public final RuleInfo load(String id) {
		PaloInfo rule = loadedInfo.get(id);
		if (rule == null) {
			rule = paloConnection.getRule(cube, id);
			loaded(rule);
		}
		return (RuleInfo)rule;
	}
	
	/**
	 * Loads the <code>RuleInfo</code> object at the given cell coordinate
	 * @param coordinate a valid cell coordinate
	 * @return the loaded <code>RuleInfo</code> object or <code>null</code> if
	 * corresponding cell has no rule.
	 * @throws PaloException if loading fails   
	 */
	public final RuleInfo load(ElementInfo[] coordinate) {
//		try {
		String id = paloConnection.getRule(cube, coordinate);
		if (id != null && id.trim().length() > 0)
			return load(id);
//		} catch (PaloException pex) {
//			/* ignore */
//		}
		return null;
	}

	/**
	 * Checks if the given connection supports rules
	 * @param paloConnection the connection to check rules support for
	 * @return <code>true</code> if rules are supported, <code>false</code>
	 * otherwise
	 */
	public static boolean supportsRules(DbConnection paloConnection) {
		ServerInfo server = paloConnection.getServerInfo();
		if (server.getMajor() < MIN_RULES_MAJOR) {
			return false;
		} else if (server.getMajor() == MIN_RULES_MAJOR) {
			if (server.getMinor() < MIN_RULES_MINOR) {
				return false;
			} else if (server.getMinor() == MIN_RULES_MINOR) {
				if (server.getBuildNumber() <= MIN_RULES_BUILD) {
					return false;
				}
			}
		}
		return true;
	}
	
	private final void checkRuleSupport() {
		if (!supportsRules(paloConnection)) {
			ServerInfo srvInfo = paloConnection.getServerInfo();
			int major = srvInfo.getMajor();
			int minor = srvInfo.getMinor();
			int build = srvInfo.getBuildNumber();
			String srvVersion = major + "." + minor + " (" + build + ")";
			throw new PaloException("Palo Server " + srvVersion
					+ " does not support rules!");
		}
	}
}
