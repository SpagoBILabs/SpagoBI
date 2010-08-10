package it.eng.spagobi.tools.importexport.typesmanager;

import org.apache.log4j.Logger;

import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.engines.config.bo.Engine;
import it.eng.spagobi.tools.importexport.ExportManager;
import it.eng.spagobi.tools.importexport.ExporterMetadata;

public class TypesExportManagerFactory {

	static private Logger logger = Logger.getLogger(TypesExportManagerFactory.class);

	/**
	 *  Types ghandled by specific export managers TODO with all types 
	 */
	private static final String KPI = "KPI";
	private static final String CONSOLE = "CONSOLE";

	private static String getObjType(BIObject biobj, Engine engine){
		if (biobj.getBiObjectTypeCode().equalsIgnoreCase(KPI) 
				&& engine.getClassName() != null && engine.getClassName().equals("it.eng.spagobi.engines.kpi.SpagoBIKpiInternalEngine")) {
			return KPI;
		}

		if (biobj.getBiObjectTypeCode().equalsIgnoreCase(CONSOLE) ) {
			return CONSOLE;
		}


		return null;
	}

	public static ITypesExportManager createTypesExportManager(BIObject biobj, Engine engine, ExporterMetadata exporter,
			ExportManager manager){

		logger.debug("IN");
		String type = getObjType(biobj, engine);

		ITypesExportManager toReturn = null;

		if (type != null){

			if(type.equals("KPI")){
				logger.debug("kpi export manager");
				toReturn = new KPIExportManager(type, exporter, manager);
			}

			if(type.equals("CONSOLE")){
				logger.debug("console export manager");
				toReturn = new ConsoleExportManager(type, exporter, manager);
			}


		}

		// type has not a specific export manager
		if (toReturn == null) logger.debug("type has not a specific export manager");

		logger.debug("OUT");

		return toReturn;


	}





}
