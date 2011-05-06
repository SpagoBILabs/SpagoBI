package it.eng.spagobi.engines.qbe.services.worksheet;

import it.eng.spagobi.engines.qbe.services.core.ExecuteQueryAction;

public class ExecuteWorksheetQueryAction extends ExecuteQueryAction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9134072368475124558L;

	/**
	 * Get the active query id
	 * @return
	 */
	@Override
	public String getQueryId() {
		return 	getEngineInstance().getActiveQuery().getId();
	}


}
