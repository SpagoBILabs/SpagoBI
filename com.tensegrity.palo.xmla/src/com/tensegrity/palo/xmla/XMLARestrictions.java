/*
*
* @file XMLARestrictions.java
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
* @version $Id: XMLARestrictions.java,v 1.4 2009/04/29 10:35:37 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.xmla;

public class XMLARestrictions 
{
	public static String elementIndent = "  ";
	private String catalog;
	private String cubeName;
	private String dimensionUniqueName;
	private String dimensionName;
	private String hierarchyUniqueName;
	private String hierarchyName;
	private String hierarchyOrigin;
	private String hierarchyVisibility;
	private String dimensionVisibility;
	private String levelUniqueName;
	private String memberUniqueName;
	private String treeOp;
	private String levelNumber;
    private String schemaName;
    private String cubeSource;
    private String baseCube;
    private String memberType;
    
	public String getCatalog() {
		return catalog;
	}
	
	public void setCatalog(String s) {
		catalog = s;
	}
	
	public String getSchema() {
		return schemaName;
	}
	
	public void setSchema(String s) {
		schemaName = s;
	}
	
	public String getCubeSource() {
		return cubeSource;
	}
	
	public void setCubeSource(String s) {
		cubeSource = s;
	}
	
	public String getBaseCube() {
		return baseCube;
	}
	
	public void setBaseCube(String s) {
		baseCube = s;
	}
	
 	public String getCubeName() {
		return cubeName;
	}
	
	public void setCubeName(String s) {
		cubeName = s;
	}
	
	public String getDimensionUniqueName() {
		return dimensionUniqueName;
	}
	
	public void setDimensionUniqueName(String s) {
		dimensionUniqueName = s;
	}
	
	public String getDimensionName() {
		return dimensionName;
	}
	
	public void setDimensionName(String s) {
		dimensionName = s;
	}
	
	public String getHierarchyUniqueName() {
		return hierarchyUniqueName;
	}

	public void setHierarchyUniqueName(String s) {
		hierarchyUniqueName = s;
	}
	
	public String getHierarchyName() {
		return hierarchyName;
	}
	
	public void setHierarchyName(String s) {
		hierarchyName = s;
	}
	
	public String getHierarchyOrigin() {
		return hierarchyOrigin;
	}
	
	public void setHierarchyOrigin(String s) {
		hierarchyOrigin = s;
	}
	
	public String getHierarchyVisibility() {
		return hierarchyVisibility;
	}
	
	public void setHierarchyVisibility(String s) {
		hierarchyVisibility = s;
	}
	
	public String getDimensionVisibility() {
		return dimensionVisibility;
	}
	
	public void setDimensionVisibility(String s) {
		dimensionVisibility = s;
	}
	
	public void setLevelUniqueName(String s) {
		levelUniqueName = s;
	}
	
	public String getLevelUniqueName() {
		return levelUniqueName;
	}
	
	public void setMemberUniqueName(String s) {
		memberUniqueName = s;
	}
	
	public String getMemberUniqueName() {
		return memberUniqueName;
	}
	
	public void setMemberType(int i) {
		memberType = "" + i;
	}
	
	public String getMemberType() {
		return memberType;
	}
	
	public void setTreeOp(int _treeOp) {
		treeOp = "" + _treeOp;
	}
	
	public String getTreeOp() {
		return treeOp;
	}
	
	public String getLevelNumber()  {
		return levelNumber;
	}
	
	public void setLevelNumber(String levelNumber)  {
		this.levelNumber = levelNumber;
	}
	
	private final boolean valid(String s) {
		return s != null && s.trim().length() > 0;
	}

	private final String format(String indent, String tag, String content) {
		return "\n" + indent + elementIndent + "<" + tag + ">" + 
					content.trim() + "</" + tag + ">";
	}
	
	protected String getRestrictionListXML(String indent) {
		StringBuffer sb = new StringBuffer("\n" + indent + "<RestrictionList>");
		
		if (valid(catalog))             sb.append(format(indent, "CATALOG_NAME",          catalog));
		if (valid(cubeName))            sb.append(format(indent, "CUBE_NAME",             cubeName));
		if (valid(dimensionUniqueName)) sb.append(format(indent, "DIMENSION_UNIQUE_NAME", dimensionUniqueName));
		if (valid(dimensionName))       sb.append(format(indent, "DIMENSION_NAME",        dimensionName));
		if (valid(hierarchyUniqueName)) sb.append(format(indent, "HIERARCHY_UNIQUE_NAME", hierarchyUniqueName));
		if (valid(levelUniqueName))     sb.append(format(indent, "LEVEL_UNIQUE_NAME",     levelUniqueName));
		if (valid(memberUniqueName))    sb.append(format(indent, "MEMBER_UNIQUE_NAME",    memberUniqueName));
		if (valid(treeOp))              sb.append(format(indent, "TREE_OP",               treeOp));
		if (valid(levelNumber))         sb.append(format(indent, "LEVEL_NUMBER",          levelNumber));
		if (valid(schemaName))          sb.append(format(indent, "SCHEMA_NAME",           schemaName));
		if (valid(cubeSource))          sb.append(format(indent, "CUBE_SOURCE",           cubeSource));
		if (valid(baseCube))            sb.append(format(indent, "BASE_CUBE_NAME",        baseCube));
		if (valid(hierarchyName))       sb.append(format(indent, "HIERARCHY_NAME",        hierarchyName));
		if (valid(hierarchyOrigin))     sb.append(format(indent, "HIERARCHY_ORIGIN",      hierarchyOrigin));
		if (valid(hierarchyVisibility)) sb.append(format(indent, "HIERARCHY_VISIBILITY",  hierarchyVisibility));
		if (valid(dimensionVisibility)) sb.append(format(indent, "DIMENSION_VISIBILITY",  dimensionVisibility));
		if (valid(memberType))          sb.append(format(indent, "MEMBER_TYPE",           memberType));
		sb.append("\n" + indent + "</RestrictionList>");
		return sb.toString();
	}
	
	public String getXML(String indent) {
		return "\n" + indent + "<Restrictions>" + getRestrictionListXML(indent + elementIndent)
			 + "\n" + indent + "</Restrictions>";
		
	}
}
