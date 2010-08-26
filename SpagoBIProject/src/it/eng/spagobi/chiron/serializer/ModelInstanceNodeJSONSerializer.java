package it.eng.spagobi.chiron.serializer;

import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.kpi.model.bo.Model;
import it.eng.spagobi.kpi.model.bo.ModelInstance;
import it.eng.spagobi.kpi.model.bo.ModelResources;
import it.eng.spagobi.kpi.model.bo.Resource;

import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

public class ModelInstanceNodeJSONSerializer implements Serializer {

	public static final String 	MODEL_INST_ID = "modelInstId";
	private static final String PARENT_ID = "parentId";
	private static final String MODEL_ID = "modelId";
	private static final String KPI_INST_ID = "kpiInstId";
	private static final String NAME = "name";
	private static final String LABEL = "label";
	private static final String TEXT = "text";
	private static final String DESCRIPTION = "description";
	private static final String STARTDATE = "startdate";
	private static final String ENDDATE = "enddate";
	private static final String MODELUUID = "modelUuid";
	private static final String MODEL_NAME = "modelName";
	private static final String MODEL_CODE = "modelCode";
	private static final String MODEL_DESCR = "modelDescr";
	private static final String MODEL_TYPE = "modelType";
	private static final String MODEL_TYPEDESCR = "modelTypeDescr";
	private static final String MODEL_TEXT = "modelText";

	public Object serialize(Object o, Locale locale) throws SerializationException {
		JSONObject  result = null;
		
		if( !(o instanceof ModelInstance) ) {
			throw new SerializationException("ModelInstanceNodeJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}
		
		try {
			ModelInstance res = (ModelInstance)o;
			result = new JSONObject();
			
			result.put(MODEL_INST_ID, res.getId() );
			result.put(PARENT_ID, res.getParentId() );
			
			if(res.getModel() != null){
				result.put(MODEL_ID, res.getModel().getId() );
				Model model = DAOFactory.getModelDAO().loadModelWithoutChildrenById(res.getModel().getId() );
				result.put(MODEL_TEXT, model.getCode() +" - "+model.getName() );
				result.put(MODEL_CODE, model.getCode());
				result.put(MODEL_NAME, model.getName() );
				result.put(MODEL_DESCR, model.getDescription() );
				result.put(MODEL_TYPE, model.getTypeName() );
				result.put(MODEL_TYPEDESCR, model.getTypeDescription() );				

			}
			
			if(res.getKpiInstance() != null){
				result.put(KPI_INST_ID, res.getKpiInstance().getKpiInstanceId() );

			}
			result.put(NAME, res.getName() );
			result.put(TEXT, res.getName() );
			result.put(LABEL, res.getLabel());			
			result.put(DESCRIPTION, res.getDescription() );
			result.put(STARTDATE, res.getStartDate());
			result.put(ENDDATE, res.getEndDate());
			result.put(MODELUUID, res.getModelUUID() );

		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}
		
		return result;
	}

}
