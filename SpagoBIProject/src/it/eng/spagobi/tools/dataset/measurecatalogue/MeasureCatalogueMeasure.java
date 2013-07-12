 /* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.tools.dataset.measurecatalogue;

import it.eng.spagobi.metamodel.MetaModelWrapper;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * This class is the Measure for the MeasureCatalogue
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */

public class MeasureCatalogueMeasure {
	
	private String name;
	private Class dataType;
	private Map<IDataSet, Set<MeasureCatalogueDimension>> datasetDimensionMap;
	private MetaModelWrapper metaModel;
	
	/**
	 * List of datasets that contains the measure
	 */
	private Set<IDataSet> datasets = null;
	
	public MeasureCatalogueMeasure( MetaModelWrapper metaModel){
		this.metaModel = metaModel;
		datasets = new HashSet<IDataSet>();

	}
	
	public MeasureCatalogueMeasure( IFieldMetaData field, MetaModelWrapper metaModel){
		this(metaModel);
		this.name=field.getAlias();
		if(this.name==null){
			this.name=field.getName();
		}
		this.dataType = field.getType();
	}

	
	/**
	 * If the dataset is already present in the linked datasets list we update it.
	 * If it's not present we add it.
	 * @param ds
	 */
	public void refrehDataSet(IDataSet ds){
		if(!isDataSetContained(ds)){
			datasets.remove(ds);
		}
		datasets.add(ds);
		refreshDataSetDimension(ds);
	}
	
	/**
	 * Refresh the list of dimensions linked to the measure and the dataset
	 * @param ds
	 */
	public void refreshDataSetDimension(IDataSet ds){
		Set<MeasureCatalogueDimension> dimensions = new HashSet<MeasureCatalogueDimension>();
		int fields = ds.getMetadata().getFieldCount();
		for(int i=0; i<fields; i++){
			IFieldMetaData aFieldMetadata = ds.getMetadata().getFieldMeta(i);
			if(!isMeasure(aFieldMetadata)){
				dimensions.add(new MeasureCatalogueDimension(aFieldMetadata,metaModel, ds));
			}
		}
		datasetDimensionMap.put(ds, dimensions);
	}
	
	/**
	 * Check if this measure is equal to a field of a dataset
	 * @param measure IFieldMetaData
	 * @return
	 */
	public boolean isEqual(IFieldMetaData measure) {
		String alias = measure.getAlias();
		if(alias==null){
			alias = measure.getName();
		}
		return alias.equals(name);
	}

	/**
	 * Utility method that check if a field of a dataset is a measure
	 * @param fieldMetadata
	 * @return
	 */
	public static boolean isMeasure(IFieldMetaData fieldMetadata){
		return (fieldMetadata.getFieldType()!=null && fieldMetadata.getFieldType().name().equals(MeasureCatalogueCostants.MEASURE));
	}

	
	public boolean isDataSetContained(IDataSet aDs){
		return datasets.contains(aDs);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	

	
}
