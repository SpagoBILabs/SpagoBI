 /* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/**
 * 
 * Utilities class that provides some usefull method to access the informations of the cube
 * 
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 */
package it.eng.spagobi.engines.whatif.utilis;

import java.util.ArrayList;
import java.util.List;

import org.olap4j.OlapException;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Member;
import org.olap4j.metadata.NamedList;

public class CubeUtilities {
	
	public static final String PATH_DELIM ="[";


	
	/**
	 * Looks for the member with id memberUniqueName in the cube
	 * @param cube the cube wherein to find the member
	 * @param memberUniqueName the member to find
	 * @return the olap Member found.. null otherwise
	 * @throws OlapException
	 */
	public static Member getMember(Cube cube, String memberUniqueName) throws OlapException{
		Hierarchy hierarchy = null;
		NamedList<Hierarchy> hierarchies = cube.getHierarchies();
		String t = memberUniqueName.substring(1, memberUniqueName.indexOf("]"));
		for(int i=0; i<hierarchies.size(); i++){
			String hName = hierarchies.get(i).getName();
			if(hName.equals(t)){
				hierarchy = hierarchies.get(i);
				break;
			}
		}
		
		return getMember(hierarchy.getLevels().get(0).getMembers(),memberUniqueName);
	}
	
	/**
	 * Looks for the member with id memberUniqueName in the hierarchy
	 * @param hierarchy the hierarchy wherein to find the member
	 * @param memberUniqueName the member to find
	 * @return the olap Member found.. null otherwise
	 * @throws OlapException
	 */
	public static Member getMember(Hierarchy hierarchy, String memberUniqueName) throws OlapException{
		return getMember(hierarchy.getLevels().get(0).getMembers(),memberUniqueName);
	}
	
	/**
	 * Check if the member is the root
	 * @param memberUniqueName the name of the member
	 * @return true if the member is the root
	 * @throws OlapException
	 */
	public static boolean isRoot(String memberUniqueName) throws OlapException{
		return memberUniqueName==null || memberUniqueName.substring(1).indexOf(PATH_DELIM)==-1;
	}
	
	public static Member getMember(List<Member> members, String memberUniqueName) throws OlapException{
		for(int i=0; i<members.size();i++){
			Member m = members.get(i);
			if(m.getUniqueName().equals(memberUniqueName)){
				return m;
			}else if(memberUniqueName.contains(m.getUniqueName()) && memberUniqueName.indexOf(m.getUniqueName())==0){
				return getMember((List<Member>)m.getChildMembers(),memberUniqueName);
			}
		}
		//all member
		if(members.size()==1){
			return getMember((List<Member>)members.get(0).getChildMembers(),memberUniqueName);
		}
		return null;
	}
	
	/**
	 * Searches in the cube for the hierarchy
	 * @param cube the cube
	 * @param hierarchyUniqueName the unique name of the hierarchy to search
	 * @return
	 * @throws OlapException
	 */
	public static Hierarchy getHierarchy(Cube cube, String hierarchyUniqueName) throws OlapException{
		Hierarchy hierarchy = null;
		NamedList<Hierarchy> hierarchies = cube.getHierarchies();
		for(int i=0; i<hierarchies.size(); i++){
			String hName = hierarchies.get(i).getUniqueName();
			if(hName.equals(hierarchyUniqueName)){
				hierarchy = hierarchies.get(i);
				break;
			}
		}
		return hierarchy;
	}
	
	public static List<Dimension> getDimensions(List<Hierarchy> hierarchies){
		List<Dimension> dimensions = new ArrayList<Dimension>();
		if(hierarchies!=null){
			for(int i=0; i<hierarchies.size(); i++){
				Hierarchy aHierarchy = hierarchies.get(i);
				Dimension aDimension = aHierarchy.getDimension();
				if(!dimensions.contains(aDimension)){
					dimensions.add(aDimension);
				}
			}
		}

		return dimensions;
		
	}
	
}
