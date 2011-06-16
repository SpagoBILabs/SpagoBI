/**
 * 
 */
package it.eng.spagobi.tools.dataset.common.datareader;

import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.DataStoreMetaData;
import it.eng.spagobi.tools.dataset.common.datastore.Field;
import it.eng.spagobi.tools.dataset.common.datastore.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.datastore.Record;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JSONDataReader extends AbstractDataReader {

	DocumentBuilderFactory domFactory;
	
	private static transient Logger logger = Logger.getLogger(JSONDataReader.class);

	public JSONDataReader() {
		super();        
	}



	public IDataStore read( Object data ) {
		DataStore dataStore;
		DataStoreMetaData dataStoreMeta;

	

		logger.debug("IN");
		
		dataStore = null;

//		if (!(data instanceof InputStream)) {
//			inputDataStream = new StringBufferInputStream((String)data);
//		}
//		else{
//			inputDataStream = (InputStream)data;
//		}
//
//		dataStore = new DataStore();
//		dataStoreMeta = new DataStoreMetaData();
//		dataStore.setMetaData(dataStoreMeta);
//
//
//		try {
//			documentBuilder = domFactory.newDocumentBuilder();
//			
//			Document document =  documentBuilder.parse(inputDataStream);
//			
//			NodeList nodes = readXMLNodes(document, "/ROWS/ROW");
//			
//			if(nodes == null) {
//				throw new RuntimeException("Malformed data. Impossible to find tag rows.row");
//			}
//
//			int rowNumber = nodes.getLength();
//			boolean firstRow = true;
//			for (int i = 0; i < rowNumber; i++, firstRow = false) {
//				IRecord record = new Record(dataStore);
//				
//			    NamedNodeMap nodeAttributes = nodes.item(i).getAttributes();
//			    for(int j = 0; j < nodeAttributes.getLength(); j++) {
//				    Node attribute = nodeAttributes.item(j);
//				    String columnName = attribute.getNodeName();
//				    String columnValue = attribute.getNodeValue();
//				    Class columnType = attribute.getNodeValue().getClass();
//				    
//				    if(firstRow==true) {
//						FieldMetadata fieldMeta = new FieldMetadata();
//						fieldMeta.setName( columnName );
//						fieldMeta.setType( columnType );
//						dataStoreMeta.addFiedMeta(fieldMeta);
//					}
//				    
//				    IField field = new Field(columnValue);
//					record.appendField(field);
//			    }
//			    
//			    dataStore.appendRecord(record);
//			}
//			 
//			 
//		} catch (Throwable t) {
//			logger.error("Exception reading data", t);
//		} finally{
//			if(inputDataStream!=null)
//				try {
//					inputDataStream.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//					logger.error("IOException during File Closure");
//				}
//		}

		return dataStore;
	}
	
	private NodeList readXMLNodes(Document doc, String xpathExpression) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile(xpathExpression);
 
        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
 
        return nodes;
    }

}
