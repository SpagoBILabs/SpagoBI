/*
*
* @file BuilderRegistry.java
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
* @author Michael Raue <Michael.Raue@tensegrity-software.com>
*
* @version $Id: BuilderRegistry.java,v 1.4 2009/04/29 10:35:37 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.xmla.builders;

public class BuilderRegistry {
	private static BuilderRegistry instance = new BuilderRegistry();
	
	private final DatabaseInfoBuilder databaseInfoBuilder = new DatabaseInfoBuilder();
	private final CubeInfoBuilder cubeInfoBuilder = new CubeInfoBuilder();
	private final DimensionInfoBuilder dimensionInfoBuilder = new DimensionInfoBuilder();
	private final ElementInfoBuilder elementInfoBuilder = new ElementInfoBuilder();
	private final RuleInfoBuilder ruleInfoBuilder = new RuleInfoBuilder();
	private final VariableInfoBuilder variableInfoBuilder = new VariableInfoBuilder();
	
	private BuilderRegistry() {		
	}
	
	public static BuilderRegistry getInstance() {
		return instance;
	}
	
	public DatabaseInfoBuilder getDatabaseInfoBuilder() {
		return databaseInfoBuilder;
	}
	
	public CubeInfoBuilder getCubeInfoBuilder() {
		return cubeInfoBuilder;
	}
	
	public DimensionInfoBuilder getDimensionInfoBuilder() {
		return dimensionInfoBuilder;
	}
	
	public ElementInfoBuilder getElementInfoBuilder() {
		return elementInfoBuilder;
	}
	
	public RuleInfoBuilder getRuleInfoBuilder() {
		return ruleInfoBuilder;
	}
	
	public VariableInfoBuilder getVariableInfoBuilder() {
		return variableInfoBuilder;
	}
}
