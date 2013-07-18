package it.eng.spagobi.tools.dataset.measurecatalogue;

import it.eng.spagobi.metamodel.HierarchyWrapper;
import it.eng.spagobi.metamodel.MetaModelWrapper;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;

import java.util.List;



public class MeasureCatalogueDimension implements IMeasureCatalogueField{
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((hierarchy == null) ? 0 : hierarchy.hashCode());
		result = prime * result
				+ ((hierarchyLevel == null) ? 0 : hierarchyLevel.hashCode());
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
		MeasureCatalogueDimension other = (MeasureCatalogueDimension) obj;
		if (hierarchy == null) {
			if (other.hierarchy != null)
				return false;
		} else if (!hierarchy.equals(other.hierarchy))
			return false;
		if (hierarchyLevel == null) {
			if (other.hierarchyLevel != null)
				return false;
		} else if (!hierarchyLevel.equals(other.hierarchyLevel))
			return false;
		return true;
	}
	
	
	

}
