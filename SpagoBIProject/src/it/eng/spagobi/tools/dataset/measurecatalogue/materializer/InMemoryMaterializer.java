 /* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.tools.dataset.measurecatalogue.materializer;

import it.eng.spagobi.metamodel.HierarchyWrapper;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.datastore.Record;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.query.AggregationFunctions;
import it.eng.spagobi.tools.dataset.common.query.IAggregationFunction;
import it.eng.spagobi.tools.dataset.measurecatalogue.MeasureCatalogueDimension;
import it.eng.spagobi.tools.dataset.measurecatalogue.MeasureCatalogueMeasure;
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.emory.mathcs.backport.java.util.Collections;

public class InMemoryMaterializer implements IMaterializer {
	
	 
    public void joinMeasures(MeasureCatalogueMeasure measure1, MeasureCatalogueMeasure measure2){
		/**
		 * STEPS:
		 * 1) find common hierarchies
		 * 2) in the dataset with the highest level in the hierarchy add the column with the value of the level of the other dataset
		 * 3) group by the hierarchies and remove the columns not necessaries  
		 */


    	//STEP1: gets the common dimensions
    	List<MeasureCatalogueDimension> commonDimensions =  getCommonDimensions(measure1,measure2);
    	List<MeasureCatalogueDimension> commonDimensionsFilterd = filterHierarchies(commonDimensions);
		
		//STEP2/3: 
		 List<IRecord> groupMeasure1 =  groupBy(measure1, commonDimensionsFilterd, AggregationFunctions.AVG_FUNCTION);
		 List<IRecord> groupMeasure2 =  groupBy(measure2, commonDimensionsFilterd, AggregationFunctions.AVG_FUNCTION);
    	
    }
    
    /**
     * Get the dimensions associated to a hierarchy in common between the 2 measures
     * @param measure1
     * @param measure2
     * @return
     */
    public List<MeasureCatalogueDimension> getCommonDimensions(MeasureCatalogueMeasure measure1, MeasureCatalogueMeasure measure2){	
    	List<List<MeasureCatalogueDimension>> toReturn = new ArrayList<List<MeasureCatalogueDimension>>();
    	
    	List<MeasureCatalogueDimension> measureDimensionsCommon = new ArrayList<MeasureCatalogueDimension>();
    	
    	Set<MeasureCatalogueDimension> measure1Dimensions = (measure1.getDatasetDimension());
    	Set<MeasureCatalogueDimension> measure2Dimensions = (measure2.getDatasetDimension());
    	
    	if(measure1Dimensions!=null && measure2Dimensions!=null){
        	for (Iterator<MeasureCatalogueDimension> iterator1 = measure1Dimensions.iterator(); iterator1.hasNext();) {
    			MeasureCatalogueDimension dimension1 = (MeasureCatalogueDimension) iterator1.next();
    	    	for (Iterator<MeasureCatalogueDimension> iterator2 = measure2Dimensions.iterator(); iterator2.hasNext();) {
    				MeasureCatalogueDimension dimension2 = (MeasureCatalogueDimension) iterator2.next();
    				if(dimension1.getHierarchy()!= null && dimension2.getHierarchy()!= null && dimension1.getHierarchy().equals(dimension2.getHierarchy()) && dimension1.getHierarchyLevel().equals(dimension2.getHierarchyLevel())){
    					measureDimensionsCommon.add(dimension1);
    					break;
    				}
    			} 
    		} 
    	}

    	return measureDimensionsCommon;
    }
    
    /**
     * Check if in the dimensions the hierarchies are complete. For complete we mean that they start from the top level and 
     * there is no step between levels
     * @param dimensions 
     * @return
     */
    public List<MeasureCatalogueDimension> filterHierarchies( List<MeasureCatalogueDimension> dimensions){
    	//map with the hiearchy and the list of levels
    	Map<HierarchyWrapper, List<Integer>> hierarchiesLevels = new HashMap<HierarchyWrapper, List<Integer>>();
    	Map<HierarchyWrapper, Boolean> hierarchiesLevelsValid = new HashMap<HierarchyWrapper, Boolean>();
    	List<MeasureCatalogueDimension> filteredDimensions = new ArrayList<MeasureCatalogueDimension>();
    	
    	//create the map with the hierarchies and levels
    	for (Iterator<MeasureCatalogueDimension> iterator1 = dimensions.iterator(); iterator1.hasNext();) {
    		MeasureCatalogueDimension dimension = (MeasureCatalogueDimension) iterator1.next();
    		HierarchyWrapper hierarchy = dimension.getHierarchy();
    		List<Integer> hierarchyPositions = hierarchiesLevels.get(hierarchy);
    		if(hierarchyPositions==null){
    			hierarchyPositions = new ArrayList<Integer>();
    		}
    		hierarchyPositions.add(dimension.getHierarchyLevelPosition());
    		hierarchiesLevels.put(hierarchy, hierarchyPositions);
    	}
    	
    	//check if the hierarchies has not steps
    	for (Iterator<HierarchyWrapper> iterator = hierarchiesLevels.keySet().iterator(); iterator.hasNext();) {
    		HierarchyWrapper hierarchy = (HierarchyWrapper) iterator.next();
    		List<Integer> hierarchyPositions = hierarchiesLevels.get(hierarchy);
    		Collections.sort(hierarchyPositions);
    		hierarchiesLevelsValid.put(hierarchy, true);
    		if(hierarchyPositions.size()>1){
    			 
        		for(int i=0; i<hierarchyPositions.size(); i++){

        			if(hierarchyPositions.get(i)!=i){
        				hierarchiesLevelsValid.put(hierarchy, false);
        				break; 
        			}
        		}
    		}
		}
    	
    	//filter the array of dimensions
    	for(int i=0; i<dimensions.size(); i++){
    		if(hierarchiesLevelsValid.get(dimensions.get(i).getHierarchy())){
    			filteredDimensions.add(dimensions.get(i));
    		}
    	}
    	
    	return filteredDimensions;
    	
    	
    	
    }
    

    
    public List<IRecord>  groupBy(MeasureCatalogueMeasure measure, List<MeasureCatalogueDimension> commonDimensions, IAggregationFunction aggreationFunction){
    	IDataSet dataSet;
    	IDataStore dataStore;
    	List<Integer> hierarchiesColumnsIndexInDataSet = new ArrayList<Integer>();//columns of the datastore that contains data of the dimensions
    	int measureColumnIndex = -1;


    	//execute dataset
    	dataSet = measure.getDataset();
    	dataSet.loadData();
    	dataStore = dataSet.getDataStore();



    	
    	//A1: gets the columns that contains the measure and the dimensions
    	for(int i=0; i<dataStore.getMetaData().getFieldCount(); i++){
    		IFieldMetaData fmd = dataStore.getMetaData().getFieldMeta(i);
    		String alias = fmd.getAlias();
    		if(alias==null){
    			alias = fmd.getName();
    		}
    		//get the index of the measure
    		if(alias.equals(measure.getColumnName())){
    			measureColumnIndex=i;
    		}else if(commonDimensions!=null && commonDimensions.size()>0){
    			//get the indexes of the hierarchies columns in the dataset (by alias)
    			for (Iterator iterator = commonDimensions.iterator(); iterator.hasNext();) {
    				MeasureCatalogueDimension dimension = (MeasureCatalogueDimension) iterator.next();
    				if(dimension.getAlias().equals(alias)){
    					hierarchiesColumnsIndexInDataSet.add(i);
    					break;
    				}
    			}
    		}
    	}
    	
    	Assert.assertNotNull(dataStore, "the datastore is null");
    	Assert.assertTrue(measureColumnIndex>=0, "no measures found in teh datastore");
    	
    	
    	//A2: aggregate
    	InMemoryAggregator inMemoryAggregator = new InMemoryAggregator(aggreationFunction, measureColumnIndex);
    	
    	if(dataStore.getRecordsCount()>0){
    		int recordLength = dataStore.getRecordAt(0).getFields().size();
        	//scan the datastore
        	for(int i=0; i<dataStore.getRecordsCount(); i++){
        		IRecord dataStoreRecord = dataStore.getRecordAt(i);
        		IRecord grouppedRecord = new  Record();
        		//for each record create a record that contains only the hierarchies columns
        		for(int j=0; j<recordLength;j++){
        			IField columnField = dataStoreRecord.getFieldAt(j);
        			if(hierarchiesColumnsIndexInDataSet.contains(j) || j==measureColumnIndex){
        				grouppedRecord.appendField(columnField);
        			}
        		}
        		inMemoryAggregator.addRecord(grouppedRecord) ;     		
        	}
    	}
    	
    	return inMemoryAggregator.aggregate();
    	
    	
    	
    	
    }
    
    private class InMemoryAggregator{
    	private IAggregationFunction aggreationFunction;
    	private List<IRecord> records;
    	private List<Integer> recordsCount;
    	private int measureColumnIndex;
    	
    	public InMemoryAggregator(IAggregationFunction aggreationFunction, int measureColumnIndex){
    		this.aggreationFunction = aggreationFunction;
    		this.measureColumnIndex = measureColumnIndex;
    		records = new ArrayList<IRecord>();
    		recordsCount = new ArrayList<Integer>();
    		
    	}
    	
    	public void addRecord(IRecord record){
    		int indexOfRecord = records.indexOf(record);
    		if(indexOfRecord>=0){
    			int recordCount = recordsCount.get(indexOfRecord);
    			recordsCount.set(indexOfRecord, recordCount+1);
    		}else{
    			records.add(record);
    			recordsCount.add(1);
    		}
    	}
    	
    	public List<IRecord> aggregate(){
    		return records;
    	}
    }




    

}
