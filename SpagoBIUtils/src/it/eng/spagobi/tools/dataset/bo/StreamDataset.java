package it.eng.spagobi.tools.dataset.bo;

import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreFilter;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
 public class StreamDataset extends ConfigurableDataSet {
	
	private static transient Logger logger = Logger.getLogger(StreamDataset.class);
	String topologyJarName = "/stream/testTopologyWords2.jar";
	
	
	public IDataStore test(int offset, int fetchSize, int maxResults) {

		loadData(offset, fetchSize, maxResults);

		return getDataStore();
	}
	public String getEngineResourcePath() {
		return  ConfigSingleton.getRootPath() + System.getProperty("file.separator") + "resources" + System.getProperty("file.separator") + "stream"+ System.getProperty("file.separator") ;

	}

	@Override
	public IDataStore test() {

		loadData();

		return getDataStore();
	}

	@Override
	public IDataStore getDataStore() {
		// TODO Auto-generated method stub
		return super.getDataStore();
	}

	@Override
	public String getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDataStore getDomainValues(String attributeName, Integer start,
			Integer limit, IDataStoreFilter filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDataSetTableDescriptor persist(String tableName,
			Connection connection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadData() {
		logger.debug("IN");

		try {

			ClassLoader loader = URLClassLoader.newInstance(
			    new URL[] { new URL("file:"+getResourcePath() +topologyJarName)},
			    getClass().getClassLoader()
			);

			Thread.currentThread().setContextClassLoader(loader);
			Class<?> clazz = Class.forName("TopologyMain", true, loader);
			
			Class<? extends Runnable> runClass = clazz.asSubclass(Runnable.class);
			// Avoid Class.newInstance, for it is evil.
			Constructor<? extends Runnable> ctor = runClass.getConstructor();
			Runnable doRun = (Runnable)ctor.newInstance();
			//doRun.run();
			Method  method = clazz.getDeclaredMethod ("startTopology");
			method.invoke (doRun);

			Method  methodGetRes = clazz.getDeclaredMethod ("getResultset");
			Map res = (HashMap<String, Integer>)methodGetRes.invoke (doRun);
		} catch (Exception e) {
			logger.error("Could not find jar "+topologyJarName);
			throw new SpagoBIRuntimeException("Could not find jar "+topologyJarName, e);	
		}
		/*
		Object obj = null;
		try {
			obj = classRet.newInstance();
		} catch (InstantiationException e) {
			logger.error("Could not locate class "+javaClassName);
			throw new SpagoBIRuntimeException("Could not locad class "+javaClassName, e);	
		} catch (IllegalAccessException e) {
			logger.error("Could not locad class "+javaClassName);		
			throw new SpagoBIRuntimeException("Could not locate class "+javaClassName, e);	
		}

		if(!(obj instanceof AbstractDataSet)){
			logger.error("class "+javaClassName+ "does not extends AbstractDataset as should do");
			throw new SpagoBIRuntimeException("class "+javaClassName+ "does not extends AbstractDataset as should do");
		}

		IDataSet toreturn = (IDataSet) obj;
		toreturn.setDsMetadata(getDsMetadata());
		toreturn.setMetadata(getMetadata());
		toreturn.setParamsMap(getParamsMap());
		LogMF.debug(logger, "Setting properties into dataset : {0}", this.customDataMap);
		toreturn.setProperties(this.customDataMap);

		logger.debug("OUT");
		return (IDataSet) obj;*/ 
/*		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	@Override
	public void loadData(int offset, int fetchSize, int maxResults) {
		loadData();
	}

}
