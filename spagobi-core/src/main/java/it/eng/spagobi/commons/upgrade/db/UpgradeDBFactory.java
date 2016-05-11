package it.eng.spagobi.commons.upgrade.db;

public class UpgradeDBFactory {

	public static IUpgraderDB getUpgraderDB(String upgraderDBName) {
		if (upgraderDBName.equals("liquibase")){
			return new LiquibaseUpgradeDB();
		}
		return null;
	}

	public static IUpgraderDB getUpgraderDB() {
			//TODO read from xml or DB
			return new LiquibaseUpgradeDB();
		}

}
