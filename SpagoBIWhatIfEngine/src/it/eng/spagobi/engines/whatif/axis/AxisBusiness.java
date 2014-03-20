package it.eng.spagobi.engines.whatif.axis;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.utilis.CubeUtilities;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import java.util.List;

import org.apache.log4j.Logger;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.OlapConnection;
import org.olap4j.OlapException;
import org.olap4j.metadata.Hierarchy;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.mdx.MdxStatement;
import com.eyeq.pivot4j.query.QueryAdapter;
import com.eyeq.pivot4j.transform.ChangeSlicer;
import com.eyeq.pivot4j.transform.PlaceHierarchiesOnAxes;
import com.eyeq.pivot4j.transform.impl.ChangeSlicerImpl;
import com.eyeq.pivot4j.transform.impl.PlaceHierarchiesOnAxesImpl;

public class AxisBusiness {

	public static transient Logger logger = Logger.getLogger(AxisBusiness.class);
	
	private WhatIfEngineInstance engineInstance;

	
	public AxisBusiness(WhatIfEngineInstance engineInstance) {
		super();
		this.engineInstance = engineInstance;
	}

	/**
	 * Service to move an hierarchy from an axis to another
	 * @param qa the query adapter used to manipulate the query
	 * @param connection the connection to the server
	 * @param fromAxisPos the source axis(0 for rows, 1 for columns, -1 for filters)  
	 * @param toAxisPos the destination axis(0 for rows, 1 for columns, -1 for filters)  
	 * @param hierarchyName the unique name of the hierarchy to move
	 * @return the moved hierarchy
	 */
	public Hierarchy moveHierarchy(int fromAxisPos, int toAxisPos,  String hierarchyName){
		logger.debug("IN");

		QueryAdapter qa = getQueryAdapter();
		Hierarchy hierarchy = null;
		PlaceHierarchiesOnAxes ph = new PlaceHierarchiesOnAxesImpl(qa, getOlapConnection());
		CellSet cellSet = qa.getModel().getCellSet();
		List<CellSetAxis> axes = cellSet.getAxes();
		
		try {
			logger.debug("getting the hierarchy object from the cube");
			hierarchy = CubeUtilities.getHierarchy(qa.getModel().getCube(), hierarchyName);
		} catch (OlapException e) {
			logger.error("Error getting the hierrarchy "+hierarchyName+" from the cube ",e);
			throw new SpagoBIEngineRuntimeException("Error addingthe hierrarchy "+hierarchyName+" in the axis "+toAxisPos,e);
		}

		//if the axis is -1 the source are the filters
		if(fromAxisPos>=0){
			logger.debug("Removing the hierarchy from the axis "+fromAxisPos);
			CellSetAxis fromAxis = axes.get(fromAxisPos);
			List<Hierarchy> axisHerarchies = fromAxis.getAxisMetaData().getHierarchies();
			axisHerarchies.remove(hierarchy);
			ph.placeHierarchies(fromAxis.getAxisOrdinal(),axisHerarchies ,false);
			logger.debug("Removed the hierarchy from the axis "+fromAxisPos);
		}else{
			//removes the slicers
			ChangeSlicer cs = new ChangeSlicerImpl(qa, getOlapConnection());
			List<org.olap4j.metadata.Member> slicers = cs.getSlicer(hierarchy);
			slicers.clear();
			cs.setSlicer(hierarchy,slicers);
		}

		//if the axis is -1 the destination are the filters
		if(toAxisPos>=0){
			logger.debug("Adding the hierarchy in the axis "+fromAxisPos);
			CellSetAxis toAxis = axes.get(toAxisPos);	
			List<Hierarchy> axisHerarchies = toAxis.getAxisMetaData().getHierarchies();
			axisHerarchies.add(hierarchy);
			ph.placeHierarchies(toAxis.getAxisOrdinal(),axisHerarchies ,false);
			logger.debug("Added the hierarchy in the axis "+fromAxisPos);
		}
		
		MdxStatement s = qa.updateQuery();
		qa.getModel().setMdx(s.toMdx());
		logger.debug("Mdx updated");
		
		logger.debug("OUT");
		return hierarchy;
	}

	/**
	 * Service to swap 2 hierarchies in an axis
	 * @param qa the query adapter used to manipulate the query
	 * @param connection the connection to the server
	 * @param axisPos the source axis(0 for rows, 1 for columns, -1 for filters)  
	 * @param hierarchyPos1 the position of the first hierarchy in the axis
	 * @param hierarchyPos2  the position of the second hierarchy in the axis
	 */
	public void swapHierarchies( int axisPos,  int hierarchyPos1,  int hierarchyPos2){
		logger.debug("IN");
		
		QueryAdapter qa = getQueryAdapter();
		
		int firstPos;
		int lastPos;
		
		if(hierarchyPos1<hierarchyPos2){
			firstPos = hierarchyPos1;
			lastPos = hierarchyPos2;
		}else{
			lastPos = hierarchyPos1;
			firstPos = hierarchyPos2;
		}

		PlaceHierarchiesOnAxes ph = new PlaceHierarchiesOnAxesImpl(qa, getOlapConnection());
		
		CellSet cellSet = qa.getModel().getCellSet();
		List<CellSetAxis> axes = cellSet.getAxes();
		CellSetAxis rowsOrColumns = axes.get(axisPos);
		
		logger.debug("Getting the hierarchies list from the axis");
		List<Hierarchy> hierarchies = rowsOrColumns.getAxisMetaData().getHierarchies();
		
		
		logger.debug("removing the hierarchies");
		Hierarchy hierarchyLast = hierarchies.remove(lastPos);
		Hierarchy hierarchyFirst = hierarchies.remove(firstPos);
		logger.debug("Hierarchies removed");
		
		logger.debug("Adding the hierarchies");
		hierarchies.add(firstPos, hierarchyLast);
		hierarchies.add(lastPos, hierarchyFirst);
		logger.debug("Hierarchies added");	
		
		logger.debug("Commit the changes in the model");	
		ph.placeHierarchies(rowsOrColumns.getAxisOrdinal(),hierarchies ,false);
		
		MdxStatement s = qa.updateQuery();
		qa.getModel().setMdx(s.toMdx());
		logger.debug("Mdx updated");
		
		logger.debug("OUT");
	}
	
	private QueryAdapter getQueryAdapter(){
		PivotModel model = engineInstance.getPivotModel();	
		QueryAdapter qa = new QueryAdapter(model);
		qa.initialize();
		return qa;
	}
	
	private OlapConnection getOlapConnection(){
		return  engineInstance.getOlapConnection();	
	}
	
	
	
//	public Hierarchy removeHierarchy(QueryAdapter qa, OlapConnection connection, int axisPos, String hierarchyName){
//
//		Hierarchy hierarchyToRemove = null;
//		
//		PlaceHierarchiesOnAxes ph = new PlaceHierarchiesOnAxesImpl(qa, connection);
//
//		CellSet cellSet = qa.getModel().getCellSet();
//		List<CellSetAxis> axes = cellSet.getAxes();
//		CellSetAxis rowsOrColumns = axes.get(axisPos);
//			
//		try {
//			hierarchyToRemove = CubeUtilities.getHierarchy(qa.getModel().getCube(), hierarchyName);
//		} catch (OlapException e) {
//			logger.error("Error addingthe hierrarchy "+hierarchyName+" in the axis "+axisPos,e);
//			throw new SpagoBIEngineRuntimeException("Error addingthe hierrarchy "+hierarchyName+" in the axis "+axisPos,e);
//		}
//
//		
//		ph.removeHierarchy(rowsOrColumns.getAxisOrdinal(), hierarchyToRemove);
//		
//		
//		return hierarchyToRemove;
//	}
//	
//	public Hierarchy addHierarchy(QueryAdapter qa, OlapConnection connection, int axisPos, String hierarchyName){
//
//		
//		
//		PlaceHierarchiesOnAxes ph = new PlaceHierarchiesOnAxesImpl(qa, connection);
//		
//		CellSet cellSet = qa.getModel().getCellSet();
//		List<CellSetAxis> axes = cellSet.getAxes();
//		CellSetAxis rowsOrColumns = axes.get(axisPos);
//
//
//		List<Hierarchy> axisHerarchies = rowsOrColumns.getAxisMetaData().getHierarchies();
//		
//		Hierarchy hierarchyToAdd;
//		try {
//			hierarchyToAdd = CubeUtilities.getHierarchy(qa.getModel().getCube(), hierarchyName);
//		} catch (OlapException e) {
//			logger.error("Error addingthe hierrarchy "+hierarchyName+" in the axis "+axisPos,e);
//			throw new SpagoBIEngineRuntimeException("Error adding the hierrarchy "+hierarchyName+" in the axis "+axisPos,e);
//		}
//		
//		axisHerarchies.add(hierarchyToAdd);
//		
//		
//		ph.placeHierarchies(rowsOrColumns.getAxisOrdinal(),axisHerarchies ,false);
//
//
//		return hierarchyToAdd;
//	}
	
	
}
