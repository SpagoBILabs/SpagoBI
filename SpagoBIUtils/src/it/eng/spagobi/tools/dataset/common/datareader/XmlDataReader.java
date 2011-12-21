/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.tools.dataset.common.datareader;

import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.Field;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.datastore.Record;
import it.eng.spagobi.tools.dataset.common.metadata.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.metadata.MetaData;

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
 * @authors 
 * Angelo Bernabei (angelo.bernabei@eng.it)
 * Andrea Gioia (andrea.gioia@eng.it)
 */
public class XmlDataReader extends AbstractDataReader {

	DocumentBuilderFactory domFactory;
	
	private static transient Logger logger = Logger.getLogger(XmlDataReader.class);

	public XmlDataReader() {
		super();
		domFactory = DocumentBuilderFactory.newInstance();
        
	}



	public IDataStore read( Object data ) {
		DataStore dataStore;
		MetaData dataStoreMeta;

		InputStream inputDataStream;
		DocumentBuilder documentBuilder;

		logger.debug("IN");

		if (!(data instanceof InputStream)) {
			inputDataStream = new StringBufferInputStream((String)data);
		}
		else{
			inputDataStream = (InputStream)data;
		}

		dataStore = new DataStore();
		dataStoreMeta = new MetaData();
		dataStore.setMetaData(dataStoreMeta);


		try {
			documentBuilder = domFactory.newDocumentBuilder();
			
			Document document =  documentBuilder.parse(inputDataStream);
			
			NodeList nodes = readXMLNodes(document, "/ROWS/ROW");
			
			if(nodes == null) {
				throw new RuntimeException("Malformed data. Impossible to find tag rows.row");
			}

			int rowNumber = nodes.getLength();
			boolean firstRow = true;
			for (int i = 0; i < rowNumber; i++, firstRow = false) {
				IRecord record = new Record(dataStore);
				
			    NamedNodeMap nodeAttributes = nodes.item(i).getAttributes();
			    for(int j = 0; j < nodeAttributes.getLength(); j++) {
				    Node attribute = nodeAttributes.item(j);
				    String columnName = attribute.getNodeName();
				    String columnValue = attribute.getNodeValue();
				    Class columnType = attribute.getNodeValue().getClass();
				    
				    if(firstRow==true) {
						FieldMetadata fieldMeta = new FieldMetadata();
						fieldMeta.setName( columnName );
						fieldMeta.setType( columnType );
						dataStoreMeta.addFiedMeta(fieldMeta);
					}
				    
				    IField field = new Field(columnValue);
					record.appendField(field);
			    }
			    
			    dataStore.appendRecord(record);
			}
			 
			 
		} catch (Throwable t) {
			logger.error("Exception reading data", t);
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
	
	private NodeList readXMLNodes(Document doc, String xpathExpression) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile(xpathExpression);
 
        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
 
        return nodes;
    }

}
