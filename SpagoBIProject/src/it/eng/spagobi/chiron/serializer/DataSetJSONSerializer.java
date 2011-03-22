package it.eng.spagobi.chiron.serializer;

import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.tools.dataset.bo.IDataSet;

import java.util.Locale;

import org.json.JSONObject;

public class DataSetJSONSerializer implements Serializer {

	public static final String ID = "id";
	private static final String NAME = "name";
	private static final String DESCRIPTION = "description";
	private static final String LABEL = "label";
	private static final String DS_TYPE_CD = "dsTypeCd";
	private static final String CATEGORY_TYPE_CD = "catTypeCd";
	private static final String USED_BY_N_DOCS = "usedByNDocs";
	private static final String PARS = "pars";
	private static final String TRASFORMER_TYPE_CD = "trasfTypeCd";
	private static final String PIVOT_COL_NAME = "pivotColName";
	private static final String PIVOT_COL_VALUE = "pivotColValue";
	private static final String PIVOT_ROW_NAME = "pivotRowName";
	
	public Object serialize(Object o, Locale locale) throws SerializationException {
		JSONObject  result = null;
		
		if( !(o instanceof IDataSet) ) {
			throw new SerializationException("DataSetJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}
		
		try {
			IDataSet ds = (IDataSet)o;
			result = new JSONObject();
			
			result.put(ID, ds.getId());
			result.put(LABEL, ds.getLabel() );	
			result.put(NAME, ds.getName() );
			result.put(DESCRIPTION, ds.getDescription() );
			result.put(CATEGORY_TYPE_CD, ds.getCategoryCd());
			
			result.put(DS_TYPE_CD, ds.getDsType());
			Integer numObjAssociated = DAOFactory.getDataSetDAO().countBIObjAssociated(new Integer(ds.getId()));
			if(numObjAssociated!=null){
				result.put(USED_BY_N_DOCS, numObjAssociated );
			}
			
			result.put(PARS, ds.getParameters());	
			result.put(TRASFORMER_TYPE_CD, ds.getTransformerCd());
			result.put(PIVOT_COL_NAME, ds.getPivotColumnName());	
			result.put(PIVOT_COL_VALUE, ds.getPivotColumnValue());	
			result.put(PIVOT_ROW_NAME,ds.getPivotRowName());			
			
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}
		
		return result;
	}

}