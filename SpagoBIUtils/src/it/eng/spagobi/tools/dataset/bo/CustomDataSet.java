package it.eng.spagobi.tools.dataset.bo;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/** user defines a javaClass Name (extending IDAtaSet) and a set of properties, written as JSON Object, that are translated in a map
 * Dataset Execution makes inst5ance of user class
 * @author gavardi
 *
 */

public class CustomDataSet extends ConfigurableDataSet {

	String customData;
	String javaClassName;
	
	Map customDataMap = null;

	IDataSet classToLaunch;

	public static String DS_TYPE = "SbiCustomDataSet";

	private static transient Logger logger = Logger.getLogger(CustomDataSet.class);


	public CustomDataSet() {
		super();
	}

	public CustomDataSet(SpagoBiDataSet dataSetConfig) {
		super(dataSetConfig);

		setCustomData( dataSetConfig.getCustomData() );
		setJavaClassName( dataSetConfig.getJavaClassName() );
	}

	public SpagoBiDataSet toSpagoBiDataSet() {
		SpagoBiDataSet sbd;

		sbd = super.toSpagoBiDataSet();

		sbd.setType( DS_TYPE );

		sbd.setCustomData( getCustomData() );
		sbd.setJavaClassName( getJavaClassName() );

		return sbd;
	}


	public void setCustomData(String customData) {
		this.customData = customData;
		this.customDataMap = convertStringToMap(customData);
	}

	public String getCustomData() {
		return customData;
	}

	public void setJavaClassName(String javaClassName) {
		this.javaClassName = javaClassName;;
	}

	public String getJavaClassName() {
		return javaClassName;
	}

	public void loadData() {
		logger.debug("IN");
		classToLaunch.loadData();
		logger.debug("OUT");
	}



	public void loadData(int offset, int fetchSize, int maxResults) {
		logger.debug("IN");
		classToLaunch.loadData(offset, fetchSize, maxResults);
		logger.debug("OUT");

	}

	public IDataStore getDataStore() {
		return classToLaunch.getDataStore();
	}
	
	
	
	private Map convertStringToMap(String _customData){
		logger.debug("IN");
		Map toInsert = new HashMap<String, Object>();
		try{
			if(_customData != null && !_customData.equals("")){
				JSONObject jsonObject = new JSONObject(_customData);
				
				String[] names = JSONObject.getNames(jsonObject);
				
				for (int i = 0; i < names.length; i++) {
					String nm = names[i];
					String value = jsonObject.getString(nm);
					toInsert.put(nm, value);
				}
				
//				JSONArray jsonArray = new JSONArray(_customData);
//				for(int i = 0;i<jsonArray.length();i++){
//					JSONObject obj = (JSONObject)jsonArray.getJSONObject(0);
//					String name = obj.getString("name");
//					String value = obj.getString("value");
//					toInsert.put(name, value);
//				}
			}
		}
		catch (Exception e) {
			logger.error("cannot parse to Map the Json string "+customData);
		}
		logger.debug("IN");
		return toInsert;

	}
	
//	private Map convertStringToMap(String _customData){
//		logger.debug("IN");
//		Map toInsert = new HashMap<String, Object>();
//		try{
//			if(_customData != null && !_customData.equals("")){
//				JSONArray jsonArray = new JSONArray(_customData);
//				for(int i = 0;i<jsonArray.length();i++){
//					JSONObject obj = (JSONObject)jsonArray.getJSONObject(0);
//					String name = obj.getString("name");
//					String value = obj.getString("value");
//					toInsert.put(name, value);
//				}
//			}
//		}
//		catch (Exception e) {
//			logger.error("cannot parse to Map the Json string "+customData);
//		}
//		logger.debug("IN");
//		return toInsert;
//
//	}

	/**
	 *  Methos used to instantiate user class and set theere properties.
	 * @throws EMFUserError 
	 */
	public void init() throws EMFUserError{
		try{
			classToLaunch = (IDataSet) Class.forName( javaClassName ).newInstance();
			classToLaunch.setProperties(customDataMap);
			classToLaunch.setParamsMap(getParamsMap());

		}
		catch (ClassCastException e) {
			logger.error("Class cast ecepstion, check this class implements IDAtaset "+javaClassName, e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 9228);
		}
		catch (Exception e) {
			logger.error("Error in loading class "+javaClassName, e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 9217);
		}

		logger.debug("OUT");
	}

	public IDataSet getClassToLaunch() {
		return classToLaunch;
	}

	public void setClassToLaunch(IDataSet classToLaunch) {
		this.classToLaunch = classToLaunch;
	}


}

