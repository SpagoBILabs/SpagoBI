/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.jpivotaddins.crossnavigation;

import java.util.ArrayList;
import java.util.List;

import mondrian.olap.Cell;
import mondrian.olap.Connection;
import mondrian.olap.Cube;
import mondrian.olap.Dimension;
import mondrian.olap.Hierarchy;
import mondrian.olap.Level;
import mondrian.olap.Member;
import mondrian.olap.Query;
import mondrian.rolap.RolapCubeMember;
import mondrian.rolap.RolapMember;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.dom4j.Node;

import com.tonbeller.jpivot.mondrian.MondrianModel;

/**
 * An instance of this class contains information for cross navigation choices, retrieved by SpagoBI OLAP document template.
 * An example of this configuration could be:<br/>
 * 	&lt;CROSS_NAVIGATION&gt;<br/>
 * 	 &lt;TARGET documentLabel="QBE_FOODMART" customizedView="Unit sales on product family"&gt;<br/>
 *     &lt;TITLE&gt;<br/>
 * 	   &lt;![CDATA[<br/>
 *         Go to QbE over Foodmart DB<br/>
 *        ]]&gt;<br/>
 *     &lt;/TITLE&gt;<br/>
 *     &lt;DESCRIPTION&gt;<br/>
 * 	   &lt;![CDATA[<br/>
 *        Detail on unit sales per selected product family<br/>
 *        ]]&gt;<br/>
 *     &lt;/DESCRIPTION&gt;<br/>
 * 	  &lt;PARAMETERS&gt;<br/>
 * 	   &lt;PARAMETER name="family" scope="relative" dimension="Product" hierarchy="[Product]" level="[Product].[Product Family]" /&gt;<br/>
 *      &lt;PARAMETER name="city" scope="relative" dimension="Region" hierarchy="[Region]" level="[Region].[Sales City]" property="code" /&gt;<br/>
 * 	  &lt;/PARAMETERS&gt;<br/>
 * 	 &lt;/TARGET&gt;<br/>
 * 	&lt;/CROSS_NAVIGATION&gt;<br/>
 * 
 * @author Zerbetto Davide (davide.zerbetto@eng.it)
 * 
 * DATE            CONTRIBUTOR/DEVELOPER                        NOTE
 * 17-12-2009      Zerbetto Davide/Gilles CAFIERO (G2C)			parameter value can be retrieved by a member property
 * 
 */
public class SpagoBICrossNavigationConfig {

	private static transient Logger logger = Logger.getLogger(SpagoBICrossNavigationConfig.class);
	
	private List<Target> targets = null;
	
	public static final String ID = "cross_navigation_config"; 
	
	/**
	 * Constructor given the CROSS_NAVIGATION node of the xml document template.
	 * @param config: the CROSS_NAVIGATION node of the xml document template
	 */
	public SpagoBICrossNavigationConfig(Node config) {
		logger.debug("Configuration:\n" + config.asXML());
		init(config);
	}
	
	private void init(Node node){
		targets = new ArrayList<Target>();
		List targetNodes = node.selectNodes("TARGET");
		if (targetNodes != null && !targetNodes.isEmpty()) {
			for (int i = 0; i < targetNodes.size(); i++) {
				Target target = new Target((Node) targetNodes.get(i));
				if (target != null) {
					targets.add(target);
				}
			}
		}
	}
	
	public int getChoicesNumber() {
		logger.debug("IN");
		int toReturn = targets.size();
		logger.debug("OUT: returning [" + toReturn + "]");
		return toReturn;
	}

	public String[] getChoice(int rowIndex, Cell cell, MondrianModel model) {
		logger.debug("IN");
		String[] toReturn = new String[2];
		Target target = targets.get(rowIndex);
		String url = getCrossNavigationUrl(target, cell, model);
		toReturn[0] = target.title;
		toReturn[1] = url;
		logger.debug("OUT: returning [" + toReturn + "]");
		return toReturn;
	}
	
	private String getCrossNavigationUrl(Target target, Cell cell, MondrianModel model) {
		logger.debug("IN");
		StringBuffer buffer = new StringBuffer("parent.execCrossNavigation(window.name, '" 
				+ StringEscapeUtils.escapeJavaScript(target.documentLabel) + "', '");
		String query = model.getCurrentMdx();
		Connection monConnection = model.getConnection();
	    Query monQuery = monConnection.parseQuery(query);
	    Cube cube = monQuery.getCube();
	    
	    List<TargetParameter> parameters = target.parameters;
	    if (!parameters.isEmpty()) {
	    	for (int i = 0; i < parameters.size(); i++) {
	    		TargetParameter aParameter = parameters.get(i);
		    	String parameterName = aParameter.name;
		    	String parameterValue = getParameterValue(aParameter, cube, cell);
		    	if (parameterValue != null) {
		    		buffer.append(StringEscapeUtils.escapeJavaScript(parameterName + "=" + parameterValue + "&"));
		    	}
	    	}
	    }
	    
    	if (buffer.charAt(buffer.length() - 1) == '&') {
    		buffer.deleteCharAt(buffer.length() - 1);
    	}
    	if (target.customizedView != null) {
    		buffer.append("', '" + StringEscapeUtils.escapeJavaScript(target.customizedView) + "'");
    	} else {
    		buffer.append("', ''");
    	}
    	
    	if(target.titleCross!=null && target.targetCross!=null && target.targetCross.equalsIgnoreCase("tab")){
    		buffer.append(",'"+target.titleCross+"','tab'");
		}else if(target.titleCross!=null){
			buffer.append(",'"+target.titleCross+"'");
		}
    	
    	buffer.append(");");
	    String toReturn = buffer.toString();
	    logger.debug("OUT: returning [" + toReturn + "]");
		return toReturn;
	}

	private String getParameterValue(TargetParameter parameter, Cube cube, Cell cell) {
		if (parameter.isAbsolute) {
			return parameter.value;
		}
		String value = null;
		String dimensionName = parameter.dimension;
		String hierarchyName = parameter.hierarchy;
		String levelName = parameter.level;
		String propertyName = parameter.property;
		logger.debug("Looking for dimension " + dimensionName + ", hierarchy " + hierarchyName + ", level " + levelName + ", property " + propertyName + " ...");
		Dimension dimension = getDimension(cube, dimensionName);
		if (dimension == null) {
			logger.error("Dimension " + dimensionName + " not found in cube " + cube.getName() + "Returning null");
			return null;
		}
		logger.debug("Dimension " + dimensionName + " found.");
		Member member = cell.getContextMember(dimension);
		logger.debug("Considering context member " + member.getUniqueName());
		Hierarchy hierarchy = member.getHierarchy();
		logger.debug("Member hierarchy is " + hierarchy.getUniqueName());
		if (hierarchy.getUniqueName().equals(hierarchyName)) {
			if (propertyName == null || propertyName.trim().equals("")) {
				value = getLevelValue(member, levelName);
			} else {
				value = getMemberPropertyValue(member, propertyName);
			}
		}
		return value;
	}
	
	private Dimension getDimension(Cube cube, String dimensionName) {
		Dimension toReturn = null;
		Dimension[] dimensions = cube.getDimensions();
		for (int i = 0; i < dimensions.length; i++) {
			Dimension aDimension = dimensions[i];
			if (aDimension.getName().equals(dimensionName)) {
				toReturn = aDimension;
				break;
			}
		}
		return toReturn;
	}
	
	private String getLevelValue(Member member, String levelName) {
		logger.debug("IN: Member is " + member.getUniqueName() + ", levelName is " + levelName);
		String toReturn = null;
		Level level = member.getLevel();
		logger.debug("Member level is " + level.getUniqueName());
		if (level.getUniqueName().equals(levelName)) {
			logger.debug("Member level matches input level name " + levelName + "!!");
			String uniqueName = member.getUniqueName();
			// The uniqueName is the name of the member retrieved by the column defined in "nameColumn" property of the level in the xml schema.
			// If the key value is required (retrieved by the column defined in "column" property), use the following code
			// TODO: test it!!!
			//RolapCubeMember rcm = (RolapCubeMember) member;
			//System.out.println(rcm.getKey());
			toReturn = uniqueName.substring(uniqueName.lastIndexOf("].[") + 3, uniqueName.lastIndexOf("]"));
		} else {
			logger.debug("Member level does NOT match input level name " + levelName + "!!");
			// look for parent member at parent level
			Member parent = member.getParentMember();
			if (parent == null) {
				return null;
			} else {
				return getLevelValue(parent, levelName);
			}
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}
	
	private String getMemberPropertyValue(Member member, String propertyName) {
		logger.debug("IN: Member is " + member.getUniqueName() + ", propertyName is " + propertyName);
		String toReturn = null;
		Object propertyNameValue = member.getPropertyValue(propertyName, false);	
		if (propertyNameValue != null) {
			toReturn  = propertyNameValue.toString();
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}
	
	protected class Target {
		String documentLabel;
		String customizedView;
		String title;
		String description;
		String targetCross;
		String titleCross;
		List<TargetParameter> parameters;
		Target(Node node) {
			documentLabel = node.valueOf("@documentLabel");
			customizedView = node.valueOf("@customizedView");
			targetCross = node.valueOf("@target");
			titleCross = node.valueOf("@title");
			if (customizedView != null && customizedView.trim().equals("")) {
				customizedView = null;
			}
			title = node.selectSingleNode("TITLE").getText();
			description = node.selectSingleNode("DESCRIPTION").getText();
			List parametersNodes = node.selectNodes("PARAMETERS/PARAMETER");
			boolean hasParameters = parametersNodes != null && !parametersNodes.isEmpty();
			parameters = new ArrayList<TargetParameter>();
			if (hasParameters) {
				for (int i = 0; i < parametersNodes.size(); i++) {
					TargetParameter aParameter = new TargetParameter((Node) parametersNodes.get(i));
					if (aParameter != null) {
						parameters.add(aParameter);
					}
				}
			}
		}
	}
	
	protected class TargetParameter {
		String name;
		boolean isAbsolute;
		String value;
		String dimension;
		String hierarchy;
		String level;
		String property;
		
		TargetParameter(Node node) {
			name = node.valueOf("@name");
			isAbsolute = node.valueOf("@scope").trim().equalsIgnoreCase("absolute");
			if (isAbsolute) {
				value = node.valueOf("@value");
			} else {
				dimension = node.valueOf("@dimension");
				hierarchy = node.valueOf("@hierarchy");
				level = node.valueOf("@level");
			}
			property = node.valueOf("@property");
		}
	}
	
}
