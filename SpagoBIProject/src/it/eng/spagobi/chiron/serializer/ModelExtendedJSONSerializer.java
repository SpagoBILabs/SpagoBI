package it.eng.spagobi.chiron.serializer;

import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.kpi.config.bo.Kpi;
import it.eng.spagobi.kpi.model.bo.Model;
import it.eng.spagobi.kpi.model.bo.ModelExtended;
import it.eng.spagobi.kpi.model.bo.Resource;

import java.util.Locale;

import org.json.JSONObject;

public class ModelExtendedJSONSerializer implements Serializer {

	public static final String MODEL_ID = "modelId";
	public static final String MODEL_GUIID = "id";
	public static final String MODEL_PARENT_ID = "parentId";
	private static final String MODEL_CODE = "code";
	private static final String MODEL_DESCRIPTION = "description";
	private static final String MODEL_LABEL = "label";
	private static final String MODEL_NAME = "name";
	private static final String MODEL_TYPE = "type";
	private static final String MODEL_TYPE_ID = "typeId";
	private static final String MODEL_TYPE_DESCR = "typeDescr";
	private static final String MODEL_KPI = "kpi";
	private static final String MODEL_KPI_ID = "kpiId";
	private static final String MODEL_IS_LEAF = "leaf";
	private static final String MODEL_TEXT = "text";
	//extended fields
	private static final String KPI_NAME = "kpiName";
	private static final String KPI_LABEL = "modelUuid";
	private static final String KPI_THRESHOLD = "kpiInstThrName";
	private static final String KPI_WEIGHT = "kpiInstWeight";
	private static final String KPI_TARGET = "kpiInstTarget";
	//unused 
	private static final String KPI_PERIODICITY = "kpiInstPeriodicity";
	private static final String KPI_CHART_TYPE = "kpiInstChartTypeId";
	
	private static final String MODEL_ERROR = "error";

	
	public Object serialize(Object o, Locale locale) throws SerializationException {
		JSONObject  result = null;
		
		if( !(o instanceof ModelExtended) ) {
			throw new SerializationException("ModelExtendedJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}
		
		try {
			ModelExtended modelExtended = (ModelExtended)o;
			Model model = modelExtended.getModel();
			result = new JSONObject();
			
			result.put(MODEL_ID, model.getId() );
			result.put(MODEL_GUIID, model.getGuiId() );
			result.put(MODEL_PARENT_ID, model.getParentId() );
			result.put(MODEL_CODE, model.getCode() );
			result.put(MODEL_NAME, model.getName() );
			result.put(MODEL_LABEL, model.getLabel() );
			result.put(MODEL_DESCRIPTION, model.getDescription() );		
			
			//find kpi name
			if(model.getKpiId() != null){
				result.put(MODEL_KPI_ID, model.getKpiId());
				Kpi kpi = DAOFactory.getKpiDAO().loadKpiById(model.getKpiId());
				if(kpi != null){
					result.put(MODEL_KPI, kpi.getKpiName());
				}else{
					result.put(MODEL_KPI, "");
				}
				result.put(KPI_NAME, kpi.getKpiName());
				result.put(KPI_LABEL, kpi.getModelReference());
				if(kpi.getThreshold() != null){
					result.put(KPI_THRESHOLD, kpi.getThreshold().getName());
				}
				result.put(KPI_WEIGHT, kpi.getStandardWeight());
				result.put(KPI_TARGET, kpi.getTargetAudience());

			
			}
			result.put(MODEL_TYPE, model.getTypeCd() );
			result.put(MODEL_TYPE_ID, model.getTypeId() );
			result.put(MODEL_TYPE_DESCR, model.getTypeDescription() );
			if(model.getChildrenNodes() != null && !model.getChildrenNodes().isEmpty()){
				result.put(MODEL_IS_LEAF, false );
			}else{
				result.put(MODEL_IS_LEAF, true );
			}
			result.put(MODEL_TEXT, model.getCode()+" - "+ model.getName() );
			result.put(MODEL_ERROR, false);
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}
		
		return result;
	}

}
