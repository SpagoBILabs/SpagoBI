 /* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.tools.dataset.measurecatalogue.materializer;

import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.Field;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InMemoryMaterializer implements IMaterializer {
	
	 
    public void joinMeasures(MeasureCatalogueMeasure measure1, MeasureCatalogueMeasure measure2){
		/**
		 * STEPS:
		 * 1) find common hierarchies
		 * 2) in the dataset with the highest level in the hierarchy add the column with the value of the level of the other dataset
		 * 3) group by the hierrchies and remove the columns not necessaries  
		 */

    	List<MeasureCatalogueDimension> measure1fromDimension   = new ArrayList<MeasureCatalogueDimension>();
    	List<MeasureCatalogueDimension> measure1toDimension = new ArrayList<MeasureCatalogueDimension>();
    	List<MeasureCatalogueDimension> measure2fromDimension = new ArrayList<MeasureCatalogueDimension>();
    	List<MeasureCatalogueDimension> measure2toDimension = new ArrayList<MeasureCatalogueDimension>();

    	//STEP1: gets the common dimensions
    	List<List<MeasureCatalogueDimension>> commonDimensions =  getCommonDimensions(measure1,measure2);
    	List<MeasureCatalogueDimension> measure1DimensionsCommon = commonDimensions.get(0);
		List<MeasureCatalogueDimension> measure2DimensionsCommon = commonDimensions.get(1);

		//STEP1: gives an order to the common dimensions (for each measure we should find the dimensions to roll up. The dimensions to roll up are the ones with a level lower than the same dimension of the other measure)
		if(measure1DimensionsCommon.size()>0){
			for(int i=0; i<measure1DimensionsCommon.size(); i++){
				MeasureCatalogueDimension measure1Common = measure1DimensionsCommon.get(i);
				MeasureCatalogueDimension measure2Common = measure2DimensionsCommon.get(i);
				if(measure1Common.getHierarchyLevelPosition() >  measure2Common.getHierarchyLevelPosition()){
					measure1fromDimension.add(measure1Common);
					measure1toDimension.add(measure2Common);
				}else if(measure2Common.getHierarchyLevelPosition() >  measure1Common.getHierarchyLevelPosition()){
					measure2fromDimension.add(measure2Common);
					measure2toDimension.add(measure1Common);
				}
			}
		}
		
		//STEP2/3: 
		 List<IRecord> groupMeasure1 =  groupBy(measure1, measure1DimensionsCommon, measure1fromDimension, measure1toDimension, AggregationFunctions.AVG_FUNCTION);
		 List<IRecord> groupMeasure2 =  groupBy(measure2, measure2DimensionsCommon, measure2fromDimension, measure2toDimension, AggregationFunctions.AVG_FUNCTION);
    	
    }
    
    public List<List<MeasureCatalogueDimension>> getCommonDimensions(MeasureCatalogueMeasure measure1, MeasureCatalogueMeasure measure2){
		
    	List<List<MeasureCatalogueDimension>> toReturn = new ArrayList<List<MeasureCatalogueDimension>>();
    	
    	List<MeasureCatalogueDimension> measure1DimensionsCommon = new ArrayList<MeasureCatalogueDimension>();
		List<MeasureCatalogueDimension> measure2DimensionsCommon = new ArrayList<MeasureCatalogueDimension>();
    	
    	Set<MeasureCatalogueDimension> measure1Dimensions = getDistinctDimensions(measure1.getDatasetDimension());
    	Set<MeasureCatalogueDimension> measure2Dimensions = getDistinctDimensions(measure2.getDatasetDimension());
    	
    	if(measure1Dimensions!=null && measure2Dimensions!=null){
        	for (Iterator<MeasureCatalogueDimension> iterator1 = measure1Dimensions.iterator(); iterator1.hasNext();) {
    			MeasureCatalogueDimension dimension1 = (MeasureCatalogueDimension) iterator1.next();
    	    	for (Iterator<MeasureCatalogueDimension> iterator2 = measure2Dimensions.iterator(); iterator2.hasNext();) {
    				MeasureCatalogueDimension dimension2 = (MeasureCatalogueDimension) iterator2.next();
    				//
    				if(dimension1.getHierarchy()!= null && dimension2.getHierarchy()!= null && dimension1.getHierarchy().equals(dimension2.getHierarchy())){
    					measure1DimensionsCommon.add(dimension1);
    					measure2DimensionsCommon.add(dimension2);
    					break;
    				}
    			} 
    		} 
    	}

    	toReturn.add(measure1DimensionsCommon);
    	toReturn.add(measure2DimensionsCommon);
    	
    	return toReturn;
    }
    
	/**
	 * 
	 * @param dimensions
	 * @return
	 */
    public Set<MeasureCatalogueDimension> getDistinctDimensions(Set<MeasureCatalogueDimension> dimensions){
    	 Set<MeasureCatalogueDimension> distinct = new HashSet<MeasureCatalogueDimension>();
    	 for (Iterator<MeasureCatalogueDimension> iterator = dimensions.iterator(); iterator.hasNext();) {
    		 MeasureCatalogueDimension dimension = iterator.next();
    		 boolean alreadyInserted = false;
        	 for (Iterator<MeasureCatalogueDimension> iterator2 = distinct.iterator(); iterator2.hasNext();) {
        		 MeasureCatalogueDimension distinctDimension = iterator2.next();
        		 if(distinctDimension.getHierarchy()!=null && dimension.getHierarchy()!=null && distinctDimension.getHierarchy().equals(dimension.getHierarchy())){
        			if(distinctDimension.getHierarchyLevelPosition() < dimension.getHierarchyLevelPosition()){
        				distinct.remove(distinctDimension);
        				distinct.add(dimension);
        				alreadyInserted= true;
        			}
        			break;
        		 }
        	 }
        	 if(!alreadyInserted){
        		 distinct.add(dimension);
        	 }
    	 }
    	 return distinct;
    }
    
    //CONTROLLARE METADATI DATASTORE
    public IDataStore rollUp(IDataStore dataStore, List<MeasureCatalogueDimension> fromDimensions, List<MeasureCatalogueDimension> toDimensions){
    	
    	if(fromDimensions!=null && fromDimensions.size()>0 && toDimensions!=null && toDimensions.size()==fromDimensions.size() ){
    		//get the position of the dimensions in the datastore
    		int[] dimensionpositions = new int[fromDimensions.size()];
    		List<Map<Object,Object>> dimensionsMapper  = new ArrayList<Map<Object,Object>>(fromDimensions.size());
    		
        	for(int i=0; i<dataStore.getMetaData().getFieldCount(); i++){
        		IFieldMetaData fmd = dataStore.getMetaData().getFieldMeta(i);
        		String alias = fmd.getAlias();
        		if(alias==null){
        			alias = fmd.getName();
        		}
        		
        		for(int j=0; j<fromDimensions.size(); j++){
        			if(alias.equals(fromDimensions.get(j).getAlias())){
        				dimensionpositions[j] = i;
        	        	//builds the maps for the dimensions levels
        				dimensionsMapper.add(j, fromDimensions.get(j).getHierarchy().getMembersMapBetweenLevels(fromDimensions.get(j).getHierarchyLevel(), toDimensions.get(j).getHierarchyLevel()));
        				break;
        			}
        		}
        	}

        	//add the columns with the toDimension level in the hierarchy
        	for(int i=0; i<dataStore.getRecordsCount(); i++){
        		IRecord rec = dataStore.getRecordAt(i);
        		for(int j=0; j<dimensionpositions.length; j++){
        			IField fromvalue = rec.getFieldAt(dimensionpositions[j]);
        			Object toValue = dimensionsMapper.get(j).get(fromvalue.getValue());
        			rec.appendField(new Field(toValue));
        		}
        	}
    	}
    	return dataStore;
    	   	
    }
    
    
    public List<IRecord>  groupBy(MeasureCatalogueMeasure measure, List<MeasureCatalogueDimension> commonDimensions,List<MeasureCatalogueDimension> fromDimensions, List<MeasureCatalogueDimension> toDimensions, IAggregationFunction aggreationFunction){
    	IDataSet dataSet;
    	IDataStore dataStore;
    	List<Integer> hierarchiesColumnsIndexInDataSet = new ArrayList<Integer>();//columns of the datastore that contains data of the dimensions
    	int measureColumnIndex = -1;
    	List<IRecord> aggregatedRecords = null;

    	//execute dataset
    	dataSet = measure.getDataset();
    	dataSet.loadData();
    	dataStore = dataSet.getDataStore();


    	//A: create a new datastore that contains only the columns of the hierarchies

    	//A0: roll up the common dimensions
    	dataStore = rollUp(dataStore, fromDimensions, toDimensions);
    	
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
    	
    	return aggregatedRecords = inMemoryAggregator.aggregate();
    	
    	
    	
    	
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
