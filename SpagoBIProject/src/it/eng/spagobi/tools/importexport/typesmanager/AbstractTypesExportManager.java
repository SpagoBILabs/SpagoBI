package it.eng.spagobi.tools.importexport.typesmanager;

import it.eng.spagobi.tools.importexport.ExportManager;
import it.eng.spagobi.tools.importexport.ExporterMetadata;

import org.apache.log4j.Logger;

/** class for specific types export managers
 * 
 * @author gavardi
 *
 */

public abstract class AbstractTypesExportManager  implements ITypesExportManager  {

	String type;
	ExporterMetadata exporter;
	ExportManager exportManager;

	
	

	public AbstractTypesExportManager(String type, ExporterMetadata exporter, ExportManager manager) {
		super();
		this.type = type;
		this.exporter = exporter;
		this.exportManager = manager;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
	
}
