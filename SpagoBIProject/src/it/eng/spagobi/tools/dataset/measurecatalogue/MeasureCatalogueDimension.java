package it.eng.spagobi.tools.dataset.measurecatalogue;

import it.eng.spagobi.metamodel.HierarchyWrapper;
import it.eng.spagobi.metamodel.MetaModelWrapper;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;

import java.util.List;



public class MeasureCatalogueDimension {
	IFieldMetaData dimensionMetadata;
	HierarchyWrapper hierarchy;
	String hierarchyLevel;
	int hierarchyLevelPosition;

	public MeasureCatalogueDimension(IFieldMetaData dimensionMetadata, MetaModelWrapper metaModel, IDataSet ds){
		this.dimensionMetadata = dimensionMetadata;
		hierarchyLevelPosition = -1;
		
		if(dimensionMetadata.getProperties()!=null){
			String hierarchyName = (String) dimensionMetadata.getProperty(MeasureCatalogueCostants.dimensionHierarchyMetadata);
			if(hierarchyName!=null){
				setHierarchy(hierarchyName, metaModel);
				hierarchyLevel =  (String) dimensionMetadata.getProperty(MeasureCatalogueCostants.dimensionHierarchyMetadataLevel);
				hierarchyLevelPosition = hierarchy.getLevel(hierarchyLevel);
			}
		}

	}
	
	public String getAlias(){
		if(dimensionMetadata.getAlias()!=null){
			return dimensionMetadata.getAlias();
		}
		return dimensionMetadata.getName();
	}
	
	public void setHierarchy(String hierarchyName, MetaModelWrapper metaModel){
		List<HierarchyWrapper> hierarchies = metaModel.getHierarchies();
		for(int i=0; i<hierarchies.size(); i++){
			if(hierarchies.get(i).getName().equals(hierarchyName)){
				hierarchy = hierarchies.get(i);
			}
		}
	}
	
	public boolean hasHierarchy() {
		return hierarchy!=null;
	}

	public HierarchyWrapper getHierarchy() {
		return hierarchy;
	}

	public String getHierarchyLevel() {
		return hierarchyLevel;
	}

	public int getHierarchyLevelPosition() {
		return hierarchyLevelPosition;
	}
	
	

}
