package it.eng.spagobi.tools.dataset.bo;

import it.eng.spagobi.tools.dataset.common.datareader.JDBCHBaseDataReader;

public class JDBCHBaseDataSet extends AbstractJDBCDataset {
	
    public JDBCHBaseDataSet() {
		super();
		setDataReader( new JDBCHBaseDataReader() );
	}
}
