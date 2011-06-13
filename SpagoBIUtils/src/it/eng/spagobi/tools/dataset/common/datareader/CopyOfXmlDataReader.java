/**
 * 
 */
package it.eng.spagobi.tools.dataset.common.datareader;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanAttribute;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.DataStoreMetaData;
import it.eng.spagobi.tools.dataset.common.datastore.Field;
import it.eng.spagobi.tools.dataset.common.datastore.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.datastore.Record;

/**
 * @author Angelo Bernabei
 *         angelo.bernabei@eng.it
 */
public class CopyOfXmlDataReader extends AbstractDataReader {

	private static transient Logger logger = Logger.getLogger(CopyOfXmlDataReader.class);

	public CopyOfXmlDataReader() {
		super();
	}



	public IDataStore read( Object data ) {
		DataStore dataStore;
		DataStoreMetaData dataStoreMeta;

		InputStream inputDataStream;
		InputSource inputDataSource;

		logger.debug("IN");

		if (!(data instanceof InputStream)) {
			inputDataStream = new StringBufferInputStream((String)data);
		}
		else{
			inputDataStream = (InputStream)data;
		}

		dataStore = new DataStore();
		dataStoreMeta = new DataStoreMetaData();
		dataStore.setMetaData(dataStoreMeta);


		try{
			inputDataSource = new InputSource(inputDataStream);

			SourceBean rowsSourceBean = null;
			List colNames = new ArrayList();

			rowsSourceBean = SourceBean.fromXMLStream(inputDataSource);
			if(rowsSourceBean != null){

				List rows = rowsSourceBean.getAttributeAsList("ROW");
				Iterator iterator = rows.iterator(); 						
				boolean firstRow=true;
				while(iterator.hasNext()){												
					SourceBean rowSB = (SourceBean) iterator.next();
					IRecord record = new Record(dataStore);

					List columns = rowSB.getContainedAttributes();
					for (int i = 0; i < columns.size(); i++) {								
						SourceBeanAttribute columnSB = (SourceBeanAttribute) columns.get(i);

						if(firstRow==true) {
							FieldMetadata fieldMeta = new FieldMetadata();
							fieldMeta.setName( columnSB.getKey() );
							fieldMeta.setType( columnSB.getValue().getClass() );
							dataStoreMeta.addFiedMeta(fieldMeta);
						}				

						IField field = new Field(columnSB.getValue());
						record.appendField(field);
					}
					dataStore.appendRecord(record);
				if(firstRow==true)firstRow=false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception reading File data");
		} finally{
			if(inputDataStream!=null)
				try {
					inputDataStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("IOException during File Closure");
				}
		}

		return dataStore;
	}

}
