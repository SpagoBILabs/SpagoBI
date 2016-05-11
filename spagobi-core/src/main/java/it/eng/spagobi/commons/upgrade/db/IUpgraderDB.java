package it.eng.spagobi.commons.upgrade.db;

import it.eng.spagobi.commons.metadata.SbiCommonInfo;

import java.sql.Connection;

public interface IUpgraderDB {
	
	public final static String CURRENT_VERSION = SbiCommonInfo.SBI_VERSION; 
	
	void upgrade(Connection connection, String version);
}
