package it.eng.spagobi.tools.importexport.typesmanager;

import org.hibernate.Session;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;

/** interface for specific types export managers
 * 
 * @author gavardi
 *
 */

public interface ITypesExportManager {

	
	public String getType();
	public void setType(String type);
	
	public void manageExport(BIObject biobj, Session session) throws EMFUserError ;
	
	
}
