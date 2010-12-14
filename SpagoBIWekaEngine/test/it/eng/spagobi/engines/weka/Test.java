/**
 *
 *	LICENSE: see COPYING file
 *
**/
package it.eng.spagobi.engines.weka;

import java.io.File;

import org.apache.log4j.Logger;

import it.eng.spagobi.engines.weka.configurators.FilterConfigurator;

/**
 * @author Andrea Gioia
 *
 */
public class Test {
	
	//static private String pathStr = "C:\\Documents and Settings\\gioia\\Documenti\\Codice\\Java\\SpagoBIBranch\\SpagoBIWekaEngine\\JavaSource";
	static private String pathStr = "D:\\Documenti\\Andrea\\Codice\\Java\\SpagoBIBranch\\SpagoBIWekaEngine\\JavaSource";
	static private File path = new File(pathStr);
	private static transient Logger logger = Logger.getLogger(FilterConfigurator.class);
	
	public static final String WRITE_MODE = "writeMode"; 
	public static final String KEYS = "keys";
	public static final String VERSIONING = "versioning";
	public static final String VERSION_COLUMN_NAME = "versionColumnName";
	public static final String VERSION = "version";
	
	static private void log(String msg) {
		logger.debug("Test:" + msg);
	}
	
	static private File getTemplateFile(String[] args) {
		File inputFile = null;
		
		
		if(args.length > 1)
			inputFile = new File(args[1]);
		
		if(inputFile == null || !inputFile.exists()) {
			log("No input file!");
			log("Default file will be used just for test pourpose.");
			inputFile = new File(path, "clusterer_flow_filtered_params.kfml");
			//inputFile = new File(path b, "simple_flow.kfml");
			//inputFile = new File(path, "complex_flow.kfml");
			
		}
		
		return (inputFile);
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	/*
	public static void main(String[] args) throws Exception {
				
		WekaKnowledgeFlowRunner runner = new WekaKnowledgeFlowRunner();
		
		File inputFile = getTemplateFile(args);
		File filledInputFile = new File(path, "out.xml");
		File propertiesFile = new File(path, "conf.properties");
		Properties params = new Properties();
		params.load(new FileInputStream(propertiesFile));
		//params.put("clusterNum", "15");
		//params.put("clusterer", "weka.clusterers.SimpleKMeans");
		ParametersFiller.fill(inputFile, filledInputFile, params);
		
		log("Starting parsing file: " + inputFile);
		runner.loadKnowledgeFlowTemplate(filledInputFile);
		runner.setDbPassword("root");
		runner.setWriteMode((String)params.get(WRITE_MODE));
		runner.setKeyColumnNames(parseKeysProp((String)params.get(KEYS)));
		String versioning = (String)params.get(VERSIONING);
		if(versioning != null && versioning.equalsIgnoreCase("true")){
			log("Versioning activated");
			runner.setVersioning(true);
			String str;
			if( (str = (String)params.get(VERSION_COLUMN_NAME)) != null) 
				runner.setVersionColumnName(str);
			log("Version column name is " + runner.getVersionColumnName());
			if( (str = (String)params.get(VERSION)) != null) 
				runner.setVersion(str);
			log("Version is " + runner.getVersion());
			
		}
		runner.setupSavers();
		runner.setupLoaders();
		
		log("\nGetting loaders & savers infos ...\n");
		log( Utils.getLoderDesc(runner.getLoaders()) );
		log( Utils.getSaverDesc(runner.getSavers()) );
		
		log("Executing knowledge flow ...");
		runner.run(false, true);
		
		log("Knowledge flow executed successfully (at least i hope so ;-))");
	}
	*/
	private static String[] parseKeysProp(String keysStr) {
		if(keysStr == null) return null;
		return keysStr.split(",");
	}

}
