package it.eng.spagobi.tools.dataset.bo;

import it.eng.spagobi.tools.dataset.common.datareader.JDBCHiveDataReader;


public class JDBCHiveDataSet extends AbstractJDBCDataset {
	
    public JDBCHiveDataSet() {
		super();
		setDataReader( new JDBCHiveDataReader() );
	}
}
