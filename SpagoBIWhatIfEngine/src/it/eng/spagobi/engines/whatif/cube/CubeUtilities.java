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
package it.eng.spagobi.engines.whatif.cube;

import it.eng.spagobi.engines.whatif.model.SpagoBICellWrapper;
import it.eng.spagobi.engines.whatif.model.SpagoBIPivotModel;
import it.eng.spagobi.pivot4j.mdx.MDXQueryBuilder;
import it.eng.spagobi.pivot4j.mdx.MdxQueryExecutor;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.olap4j.Axis;
import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.OlapDataSource;
import org.olap4j.OlapException;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Member;
import org.olap4j.metadata.NamedList;

import com.eyeq.pivot4j.PivotModel;

public class CubeUtilities {
	
	public static final String PATH_DELIM ="[";
	public static transient Logger logger = Logger.getLogger(CubeUtilities.class);

	
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
	
	/**
	 * Return the axis for the position
	 * @param axis
	 * @return
	 */
	public static Axis getAxis(int axis){
		if(axis==Axis.COLUMNS.axisOrdinal()){
			return Axis.COLUMNS;
		}
		if(axis==Axis.ROWS.axisOrdinal()){
			return Axis.ROWS;
		}
		return Axis.FILTER;
		
	}
	
	/*
	 * TODO: to complete	
	 * Calculate the members value based on the passed expression
	 */
	public static Double getMemberValue(LinkedList membersExpression, SpagoBICellWrapper cellWrapper, PivotModel pivotModel,OlapDataSource olapDataSource) {
		Double toReturn = null;
		
		//Members are the dimensional "coordinates" that identify the specific value inserted in the cell
		Member[] cellMembersOriginal = cellWrapper.getMembers();
		Member[] cellMembers = new Member[cellMembersOriginal.length];
		System.arraycopy( cellMembersOriginal, 0, cellMembers, 0, cellMembersOriginal.length );
		
		//TODO: gestire casi di errore tramite eccezioni
		boolean errorFound = false;
		
		//Iterate the list for each member specified
		for (Object memberExp:membersExpression){
			String memberExpression =(String)memberExp;
			//TODO: considerare i casi con gerarchie multiple e notazione del tipo Dimensione.gerarchia.membro

			String[] memberExpressionParts;
			if(memberExpression.contains("[")){
				//The member is using the notation with square brackets. Ex. [Dimension].[MemberName]
				memberExpressionParts = splitSquareBracketNames(memberExpression);
			} else {
				memberExpressionParts = memberExpression.split("\\.");
			}
			
			boolean memberFound = searchMember(cellMembers, memberExpressionParts);

			if (!memberFound){
				logger.error("ERROR: Cannot calculate Value, Member not found: "+memberExpression);
				errorFound = true;
				//throw new SpagoBIEngineException("Cannot calculate Value, Member not found: "+memberExpression);
			}
			
		}

		if (!errorFound){
			//Calculate the new value 
			MdxQueryExecutor mdxQueryExecutor = new MdxQueryExecutor(olapDataSource);
			Cube cube = pivotModel.getCube();
			SpagoBIPivotModel spagoBIPivotModel = null;
			if (pivotModel instanceof SpagoBIPivotModel){
				spagoBIPivotModel = (SpagoBIPivotModel)pivotModel;
			} else {
				//TODO: throw exception
			}
			Object value = mdxQueryExecutor.getValueForTuple(cellMembers,cube,spagoBIPivotModel);
			if (value instanceof Double){
				toReturn = (Double)value;
			}
		}
		return toReturn;

		
	}
	
	private static boolean searchMember(Member[] cellMembers, String[] memberExpressionParts){
		boolean memberFound = false;
		String memberExpressionDimension = memberExpressionParts[0];
		boolean hierarchySpecified = false;
		
		if(memberExpressionParts.length > 2){
			//Notation with Hierarchy specified
			memberExpressionDimension = memberExpressionDimension + "."+memberExpressionParts[1];
			hierarchySpecified =  true;
		}

		for (int i=0; i<cellMembers.length; i++){
			Member aMember = cellMembers[i];
			String memberUniqueName = aMember.getUniqueName();
			/*
			String uniqueNameParts[] = memberUniqueName.split("\\.");
			String dimensionName = uniqueNameParts[0];
			//remove the square brackets from the dimensionName
			dimensionName = dimensionName.replaceAll("\\[", "");
			dimensionName = dimensionName.replaceAll("\\]", "");
			*/
			String uniqueNameParts[] = splitSquareBracketNames(memberUniqueName);
			String dimensionName = uniqueNameParts[0];
			//Search the member to modify first by dimensionName (first part of the uniqueName)
			if (dimensionName.equalsIgnoreCase(memberExpressionDimension)){
				
				//Compose the uniqueName of the member to search using the prefix of the current member
				//of the selected cell
				String memberToSearchUniqueName = "";

				int endIndex = memberUniqueName.lastIndexOf(".");
				if (endIndex != -1)  
			    {
			        memberToSearchUniqueName = memberUniqueName.substring(0, endIndex); 
			    }
				if (hierarchySpecified){
					memberToSearchUniqueName = memberToSearchUniqueName + "."+"["+ memberExpressionParts[2]+"]";
				} else {
					memberToSearchUniqueName = memberToSearchUniqueName + "."+"["+ memberExpressionParts[1]+"]";
				}
				
				//get Level of the interested member
				Level levelOfMember = aMember.getLevel();
				try {
					List<Member> levelMembers = levelOfMember.getMembers();
					for (Member levelMember :  levelMembers){
						String levelMemberName = levelMember.getUniqueName();
						
						if (levelMemberName.equalsIgnoreCase(memberToSearchUniqueName)){
							//Found the member specified in the expression, use it to substitute
							//the original member in the cellMembers
							cellMembers[i] = levelMember;
							memberFound = true;
							break;
						}
					}
				} catch (OlapException e) {
					e.printStackTrace();
				}
				if (memberFound){
					break;
				}
				
				
			}
			
			
		}
		return memberFound;
	}
	
	private static String[] splitSquareBracketNames(String memberExpression){
		ArrayList<String> memberExpressionParts = new ArrayList<String>();
		
		StringTokenizer st = new StringTokenizer(memberExpression,"[]", false);
		while(st.hasMoreTokens()){
			String memberPart = st.nextToken();
			if (!memberPart.equals(".")){
				memberExpressionParts.add(memberPart);
			}
		}
		
		return memberExpressionParts.toArray(new String[0]);			
	}
	
}
