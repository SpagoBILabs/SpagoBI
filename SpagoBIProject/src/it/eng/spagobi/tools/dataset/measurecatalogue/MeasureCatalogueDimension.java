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
		
		//load the hierarchy
		for(int i=0; i<MeasureCatalogueCostants.dimensionHierarchyTypes.length; i++){
			String aDimensionType = MeasureCatalogueCostants.dimensionHierarchyTypes[i];
			String aDimensionRef = (String) ds.getMetadata().getProperty(aDimensionType+ MeasureCatalogueCostants.dimensionHierarchyTypesRefSuffix);
			if(aDimensionRef!=null && aDimensionRef.equals(getAlias())){
				setHierarchy(aDimensionType, metaModel);
				hierarchyLevel =  (String) ds.getMetadata().getProperty(aDimensionType+ MeasureCatalogueCostants.dimensionHierarchyTypesLevelSuffix);
				break;
			}
		}
		
		if(hierarchy!=null){
			hierarchyLevelPosition = hierarchy.getLevel(hierarchyLevel);
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
