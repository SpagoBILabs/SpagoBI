/*
*
* @file XMLARuleLoader.java
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
* @version $Id: XMLARuleLoader.java,v 1.2 2009/04/29 10:35:37 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palo.xmla.loader;

import com.tensegrity.palo.xmla.XMLAClient;
import com.tensegrity.palojava.CubeInfo;
import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.RuleInfo;
import com.tensegrity.palojava.loader.RuleLoader;

/**
 * <code>HttpRuleInfoLoader</code>
 * TODO DOCUMENT ME
 *
 * @author ArndHouben
 * @version $Id: XMLARuleLoader.java,v 1.2 2009/04/29 10:35:37 PhilippBouillon Exp $
 **/
public class XMLARuleLoader extends RuleLoader {
	private final XMLAClient xmlaClient;
	
	public XMLARuleLoader(DbConnection paloConnection, XMLAClient xmlaClient,
			CubeInfo cube) {
		super(paloConnection, cube);
		this.xmlaClient = xmlaClient;
	}

	public String[] getAllRuleIds() {
		reload();
		return getLoadedIds();
	}

	protected final void reload() {
		reset();
		RuleInfo[] rules = paloConnection.getRules(cube);
		for (RuleInfo rule : rules) {
			loaded(rule);
		}
	}
}

