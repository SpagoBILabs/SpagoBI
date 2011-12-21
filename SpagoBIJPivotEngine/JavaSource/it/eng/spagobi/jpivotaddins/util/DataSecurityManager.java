/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.jpivotaddins.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mondrian.olap.Access;
import mondrian.olap.Connection;
import mondrian.olap.Cube;
import mondrian.olap.Dimension;
import mondrian.olap.Id;
import mondrian.olap.Level;
import mondrian.olap.Query;
import mondrian.olap.Role;
import mondrian.olap.RoleImpl;
import mondrian.olap.SchemaReader;
import mondrian.olap.Util;

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.mondrian.ScriptableMondrianDrillThrough;
import com.tonbeller.jpivot.olap.model.OlapModel;

public class DataSecurityManager {

	private static Logger logger = Logger.getLogger(DataSecurityManager.class);
	
	private OlapModel olapModel = null;
	private String dimAccRulStr = null;
	private String query = null;
	
	
	public DataSecurityManager(OlapModel olapModel, String dimAccRulStr, String query){
		this.olapModel = olapModel;
		this.dimAccRulStr = dimAccRulStr;
		this.query = query;
	}
	
	
	
	
	public void setMondrianRole() {
		
		List dimensionRules = getDimensionRules();
		if(dimensionRules.isEmpty()) {
			return;
		}
		
		ScriptableMondrianDrillThrough smdt = (ScriptableMondrianDrillThrough) olapModel.getExtension("drillThrough");
		Connection monConnection = smdt.getConnection();
		// get the connection role, cube and schema reader
		RoleImpl connRole = (RoleImpl) monConnection.getRole();
		//connRole.makeMutableClone();
	    logger.debug("DataSecurityManager::setMondrianRole:connection role retrived: " + connRole);
	    Query monQuery = monConnection.parseQuery(query);
	    Cube cube = monQuery.getCube();
	    logger.debug("DataSecurityManager::setMondrianRole: cube retrived: " + cube);
	    SchemaReader schemaReader = cube.getSchemaReader(null);
	    logger.debug("DataSecurityManager::setMondrianRole: schema reader retrived: " + schemaReader);
		
	    
	    // FOR EACH DIMENSION NAME SET THE RIGHT GRANT TO THE DIMENSION OR HIERARCHY
	    logger.debug("DataSecurityManager::setMondrianRole: start setting grant for each dimension or hierachy");
	    Iterator iterDimRules = dimensionRules.iterator();
	    while(iterDimRules.hasNext()){
	    	DimensionRulesBean drb = (DimensionRulesBean)iterDimRules.next();
	    	String dimName = drb.getName();
	    	String dimAccess = drb.getAccess();
	    	String bottomLevel = drb.getBottomLevel();
	    	String topLevel = drb.getTopLevel();
	    	String rollupPolicy = drb.getRollupPolicy();
	    	logger.debug("DataSecurityManager::setMondrianRole: processing dimension named: " + dimName);
	    	//List dimMembs = drb.getMembers();
	    	logger.debug("DataSecurityManager::setMondrianRole: try to search the dimension into the cube");
			Dimension[] dimensions = cube.getDimensions();
	 		for (int i = 0; i < dimensions.length; i++) {
	 		   	Dimension dim = dimensions[i];
	 		   	String cubeDimKey = dim.getName();
	 		   	if(cubeDimKey.equalsIgnoreCase(dimName)) {
	 		   		logger.debug("DataSecurityManager::setMondrianRole: dimension found into the cube");
 		    		mondrian.olap.Hierarchy[] hierarchies = dim.getHierarchies();
 		    		if(hierarchies == null || hierarchies.length == 0) {
 		    			if(dimAccess.equalsIgnoreCase("none")) {
 		    				connRole.grant(dim, Access.NONE);
 		    				logger.debug("DataSecurityManager::setMondrianRole: setted access.none to the dimension");	
 		    			} else {
 		    				connRole.grant(dim, Access.ALL);
 		    				logger.debug("DataSecurityManager::setMondrianRole: setted access.all to the dimension");
 		    			}
 		    			break;
 		    		} else {
			 		   	for (int j = 0; j < hierarchies.length; j++) {
			 		   		mondrian.olap.Hierarchy aHierarchy =  hierarchies[j];
			 		   		if (aHierarchy.getName().equalsIgnoreCase(dimName)) { 
			 		   			Level[] levels = aHierarchy.getLevels();
			 		   			Level topLev = null;
			 		   			Level bottomLev = null;
			 		   			for(int k=0; k<levels.length; k++) {
			 		   				Level level = levels[k];
			 		   				if(level.getUniqueName().equals(topLevel)) {
			 		   					topLev = level;
			 		   				}
				 		   			if(level.getUniqueName().equals(bottomLevel)) {
			 		   					bottomLev = level;
			 		   				}
			 		   			}
			 		   			Role.RollupPolicy rp = null;
			 		   			if (rollupPolicy == null) {
			 		   				rp = Role.RollupPolicy.FULL;
			 		   			} else {
				 		   			try {
				 		   				rp = Role.RollupPolicy.valueOf(rollupPolicy);
				 		   			} catch (Exception e) {
				 		   				logger.error("Error evaluating rollup policy: " + rollupPolicy, e);
				 		   				logger.warn("Using default policy Role.RollupPolicy.FULL");
				 		   				rp = Role.RollupPolicy.FULL;
				 		   			}
			 		   			}
			 		   			logger.debug("DataSecurityManager::setMondrianRole: hierarchy found into the cube");
			 		   			connRole.grant(aHierarchy, Access.CUSTOM, topLev, bottomLev, rp);
			 		   			logger.debug("DataSecurityManager::setMondrianRole: setted access.custom to the hierarchy");
			 		   		 }
			 		   	}
	 		    	}
	 		    }
 		   }
    	   logger.debug("DataSecurityManager::setMondrianRole: end search dimension into the cube");
	    }
	    logger.debug("DataSecurityManager::setMondrianRole: end setting grant for each dimension or hierachy");
	    
	    
	    // FOR EACH MEMBER SET THE GRANT
	    logger.debug("DataSecurityManager::setMondrianRole: start setting grant for members of dimensions");
	    iterDimRules = dimensionRules.iterator();
	    while(iterDimRules.hasNext()){
	    	DimensionRulesBean drb = (DimensionRulesBean)iterDimRules.next();
	    	String dimName = drb.getName();
	    	logger.debug("DataSecurityManager::setMondrianRole: processing dimension named: " + dimName);
	    	List dimMembs = drb.getMembers();
	    	logger.debug("DataSecurityManager::setMondrianRole: start processing dimension named: " + dimName);
	    	Iterator iterDimMembs = dimMembs.iterator();
	        while(iterDimMembs.hasNext()) {
	        	MemberRulesBean mrb = (MemberRulesBean)iterDimMembs.next();
	        	String dimMemb = mrb.getName();
	        	String membAccess = mrb.getAccess();
	        	logger.debug("DataSecurityManager::setMondrianRole: processing member : " + dimMemb);
	        	List<Id.Segment> membParts = Util.parseIdentifier(dimMemb);
	        	mondrian.olap.Member member = schemaReader.getMemberByUniqueName(membParts,true);
	    	    logger.debug("DataSecurityManager::setMondrianRole: mondrian member object retrived: " + member);
	    	    if(membAccess.equalsIgnoreCase("none")) {
	    	    	connRole.grant(member, Access.NONE);	
	    			logger.debug("DataSecurityManager::setMondrianRole: setted access.none to the member");		
	    		} else {
	    			connRole.grant(member, Access.ALL);	
	    			logger.debug("DataSecurityManager::setMondrianRole: setted access.all to the member");	
	    		}
	        }
	    }
	    logger.debug("DataSecurityManager::setMondrianRole: end setting grant for members of dimensions");
	        
	    
	    // SET THE ROLE INTO CONNECTION
	    //connRole.makeImmutable();
	    monConnection.setRole(connRole); 
	    logger.debug("DataSecurityManager::setMondrianRole: setted role with grants into connection");
	    logger.debug("DataSecurityManager::setMondrianRole: end setting data access");
	   
	    
	    
	}
	
	
	
	
	
	
	private List getDimensionRules() {
		List dimRules = new ArrayList();
		if( (dimAccRulStr==null) || dimAccRulStr.trim().equals("")) {
			return dimRules;
		}
		if( !dimAccRulStr.startsWith("{") || !dimAccRulStr.endsWith("}") ) {
			return dimRules;
		}
		String rules = dimAccRulStr.substring(1, dimAccRulStr.length()-1);
		String[] ruleDims = rules.split(";");
		for(int i=0; i<ruleDims.length; i++) {
			String ruleDim = ruleDims[i];
			ruleDim = ruleDim.trim();
			if( (ruleDim.indexOf("{")!=-1) && (ruleDim.indexOf("}")!=-1) ) {
				String dimName = ruleDim.substring(0, ruleDim.indexOf("{"));
				DimensionRulesBean drb = new DimensionRulesBean();
				drb.setName(dimName);
				String settingsStr = ruleDim.substring(ruleDim.indexOf("{")+1, ruleDim.indexOf("}"));
				String[] settings = settingsStr.split(",");
				for(int j=0; j<settings.length; j++) {
					String setting = settings[j];
					setting = setting.trim();
					if(setting.startsWith("access=")) {
						String access = setting.substring(setting.indexOf("=")+1);
						drb.setAccess(access);
 					}
					if(setting.startsWith("topLevel=")) {
						String tl = setting.substring(setting.indexOf("=")+1);
						drb.setTopLevel(tl);
					}
					if(setting.startsWith("bottomLevel=")) {
						String bl = setting.substring(setting.indexOf("=")+1);
						drb.setBottomLevel(bl);
					}
					if(setting.startsWith("rollupPolicy=")) {
						String rp = setting.substring(setting.indexOf("=")+1);
						drb.setRollupPolicy(rp);
					}
					if(setting.startsWith("member=")) {
						String memberRules = setting.substring(setting.indexOf("=")+1);
						if(memberRules.indexOf("=")!=-1) {
							String nameMemb = memberRules.substring(0, memberRules.indexOf("="));
							String accessMemb = memberRules.substring(memberRules.indexOf("=") + 1);
							MemberRulesBean mrb = new MemberRulesBean();
							mrb.setAccess(accessMemb);
							mrb.setName(nameMemb);
							drb.addMember(mrb);
						}
					}
				}
				dimRules.add(drb);
			}
		}
		return dimRules;
	}
	
	
	private class DimensionRulesBean {
		
		private String name = "";
		private String access = "";
		private String topLevel = "";
		private String bottomLevel = "";
		private String rollupPolicy = "";
		private List members = new ArrayList();
		
		/*
		public DimensionRulesBean(String name, String tl, String bl, List membs) {
			this.name = name;
			this.topLevel = tl;
			this.bottomLevel = bl;
			this.members = membs;
		}
		*/
		public void addMember(MemberRulesBean mrb) {
			members.add(mrb);
		}
		
		public String getBottomLevel() {
			return bottomLevel;
		}
		public void setBottomLevel(String bottomLevel) {
			this.bottomLevel = bottomLevel;
		}
		public List getMembers() {
			return members;
		}
		public void setMembers(List members) {
			this.members = members;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getTopLevel() {
			return topLevel;
		}
		public void setTopLevel(String topLevel) {
			this.topLevel = topLevel;
		}
		public String getAccess() {
			return access;
		}
		public void setAccess(String access) {
			this.access = access;
		}
		public String getRollupPolicy() {
			return rollupPolicy;
		}
		public void setRollupPolicy(String rollupPolicy) {
			this.rollupPolicy = rollupPolicy;
		}
	}
	
	
	
	
	
	private class MemberRulesBean {
	
		private String name  = "";
		private String access = "" ;
		
		/*
		public MemberRulesBean(String name, String access) {
			this.name = name;
			this.access = access;
		}
		*/
		
		public String getAccess() {
			return access;
		}
		public void setAccess(String access) {
			this.access = access;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		
	}
	
	
}
