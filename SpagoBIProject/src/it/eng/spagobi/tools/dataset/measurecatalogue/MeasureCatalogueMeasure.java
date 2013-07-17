 /* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.tools.dataset.measurecatalogue;

import it.eng.spagobi.metamodel.HierarchyWrapper;
import it.eng.spagobi.metamodel.MetaModelWrapper;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 
 * This class is the Measure for the MeasureCatalogue
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */

public class MeasureCatalogueMeasure {
	
	private String alias;
	private String columnName;
	private Class dataType;
	private IDataSet dataset;
	private Set<MeasureCatalogueDimension> datasetDimensions;
	private MetaModelWrapper metaModel;
	

	
	public MeasureCatalogueMeasure( MetaModelWrapper metaModel){
		this.metaModel = metaModel;
		datasetDimensions = new HashSet<MeasureCatalogueDimension>();
	}
	
	
	public MeasureCatalogueMeasure( IFieldMetaData field, MetaModelWrapper metaModel, IDataSet ds, Set<MeasureCatalogueDimension> datasetDimension){
		this(metaModel);
		this.alias=field.getAlias();
		if(this.alias==null){
			this.alias=field.getName();
		}
		this.dataType = field.getType();
		columnName = this.alias;
		if(datasetDimension!=null){
			this.dataset = ds;
			this.datasetDimensions = datasetDimension;
		}else{
			refreshDataSet(ds);
		}
	}
	
	
	/**
	 * Refresh the list of dimensions linked to the measure and the dataset
	 * @param ds
	 */
	public void refreshDataSet(IDataSet ds){
		this.dataset = ds;
		Set<MeasureCatalogueDimension> dimensions = new HashSet<MeasureCatalogueDimension>();
		int fields = ds.getMetadata().getFieldCount();
		for(int i=0; i<fields; i++){
			IFieldMetaData aFieldMetadata = ds.getMetadata().getFieldMeta(i);
			if(!isMeasure(aFieldMetadata)){
				dimensions.add(new MeasureCatalogueDimension(aFieldMetadata,metaModel, ds));
			}
		}
		datasetDimensions = dimensions;
	}
	
	/**
	 * Check if this measure is equal to a field of a dataset
	 * @param measure IFieldMetaData
	 * @return
	 */
	public boolean isEqual(IFieldMetaData measure, IDataSet ds) {
		String alias = measure.getAlias();
		if(alias==null){
			alias = measure.getName();
		}
		return ds.equals(dataset) && alias.equals(alias);
	}

	/**
	 * Get the hierarchies of the associated dataset
	 * @return
	 */
	public Set<HierarchyWrapper> getHierarchies(){
		Set<HierarchyWrapper> hierarchies = new HashSet<HierarchyWrapper>();
		for (Iterator<MeasureCatalogueDimension> iterator = datasetDimensions.iterator(); iterator.hasNext();) {
			MeasureCatalogueDimension dimensionWrapper = (MeasureCatalogueDimension) iterator.next();
			hierarchies.add(dimensionWrapper.getHierarchy());
		}
		return hierarchies;
	}
	
	
	/**
	 * Utility method that check if a field of a dataset is a measure
	 * @param fieldMetadata
	 * @return
	 */
	public static boolean isMeasure(IFieldMetaData fieldMetadata){
		return (fieldMetadata.getFieldType()!=null && fieldMetadata.getFieldType().name().equals(MeasureCatalogueCostants.MEASURE));
	}
	

	
	public Set<MeasureCatalogueDimension> getDatasetDimension() {
		return datasetDimensions;
	}
	
	
	public IDataSet getDataset() {
		return dataset;
	}
	
	
	


	public String getColumnName() {
		return columnName;
	}


	public String getAlias() {
		return alias;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
		result = prime * result + ((dataset == null) ? 0 : dataset.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MeasureCatalogueMeasure other = (MeasureCatalogueMeasure) obj;
		if (alias == null) {
			if (other.alias != null)
				return false;
		} else if (!alias.equals(other.alias))
			return false;
		if (dataset == null) {
			if (other.dataset != null)
				return false;
		} else if (!dataset.equals(other.dataset))
			return false;
		return true;
	}



	

	
}
