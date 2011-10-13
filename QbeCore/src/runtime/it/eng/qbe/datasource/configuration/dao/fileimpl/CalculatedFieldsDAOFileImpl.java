/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.qbe.datasource.configuration.dao.fileimpl;

import it.eng.qbe.datasource.configuration.dao.DAOException;
import it.eng.qbe.datasource.configuration.dao.ICalculatedFieldsDAO;
import it.eng.qbe.model.structure.ModelCalculatedField;
import it.eng.spagobi.utilities.assertion.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class CalculatedFieldsDAOFileImpl implements ICalculatedFieldsDAO {
	
	protected File modelJarFile;

	public static final String CFIELDS_FILE_NAME = "cfields.xml";
	public final static String ROOT_TAG = "CFIELDS";
	public final static String FIELD_TAG = "CFIELD";
	public final static String FIELD_TAG_ENTIY_ATTR = "entity";
	public final static String FIELD_TAG_NAME_ATTR = "name";
	public final static String FIELD_TAG_TYPE_ATTR = "type";
	public final static String FIELD_TAG_IN_LINE_ATTR = "isInLine";
	
	public static transient Logger logger = Logger.getLogger(CalculatedFieldsDAOFileImpl.class);
	
	public CalculatedFieldsDAOFileImpl(File modelJarFile) {
		this.modelJarFile = modelJarFile;
	}
	
	
	// =============================================================================
	// LOAD
	// =============================================================================
	
	public Map<String, List<ModelCalculatedField>> loadCalculatedFields() {
		
		Map<String, List<ModelCalculatedField>> calculatedFiledsMap;
		
		File calculatedFieldsFile;
		FileInputStream in;
		SAXReader reader;
		Document document;
		String entity;
		String name;
		String type;
		Boolean inlineCalculatedField;
		String expression;
		ModelCalculatedField calculatedField;
		List calculatedFieldNodes;
		Iterator it;
		Node calculatedFieldNode;
		List calculatedFileds;
		
		logger.debug("IN");
		
		calculatedFieldsFile = null;
		in = null;	
		
		try {
			
			calculatedFiledsMap = new HashMap<String, List<ModelCalculatedField>>();
			
			calculatedFieldsFile = getCalculatedFieldsFile();
			logger.debug("Calculated fields will be loaded from file [" + calculatedFieldsFile + "]");
			
			if(calculatedFieldsFile != null && calculatedFieldsFile.exists()) {
							
				document = guardedRead(calculatedFieldsFile);
				Assert.assertNotNull(document, "Document cannot be null");
					
				
				
				calculatedFieldNodes = document.selectNodes("//" + ROOT_TAG + "/" + FIELD_TAG + "");
				logger.debug("Found [" + calculatedFieldNodes.size() + "] calculated field/s");
				
				it = calculatedFieldNodes.iterator();				
				while (it.hasNext()) {
					calculatedFieldNode = (Node) it.next();
					entity = calculatedFieldNode.valueOf("@" + FIELD_TAG_ENTIY_ATTR);
					name = calculatedFieldNode.valueOf("@" + FIELD_TAG_NAME_ATTR);
					type = calculatedFieldNode.valueOf("@" + FIELD_TAG_TYPE_ATTR);
					inlineCalculatedField = new Boolean(calculatedFieldNode.valueOf("@" + FIELD_TAG_IN_LINE_ATTR));					
					expression = loadExpression(calculatedFieldNode);
					calculatedField = new ModelCalculatedField(name, type, expression, inlineCalculatedField.booleanValue());
					
					// parse slots
					List<ModelCalculatedField.Slot> slots = loadSlots(calculatedFieldNode);
					calculatedField.addSlots(slots);
					if(slots.size() > 0) {
						String defaultSlotValue = loadDefaultSlotValue(calculatedFieldNode);
						calculatedField.setDefaultSlotValue(defaultSlotValue);
					}
					
					
					if(!calculatedFiledsMap.containsKey(entity)) {
						calculatedFiledsMap.put(entity, new ArrayList());
					}
					calculatedFileds = (List)calculatedFiledsMap.get(entity);					
					calculatedFileds.add(calculatedField);
					
					logger.debug("Calculated filed [" + calculatedField.getName() + "] loaded succesfully");
				}	
			} else {
				logger.debug("File [" + calculatedFieldsFile + "] does not exist. No calculated fields have been loaded.");
			}
		} catch(Throwable t){
			if(t instanceof DAOException) throw (DAOException)t;
			throw new DAOException("An unpredicted error occurred while loading calculated fields on file [" + calculatedFieldsFile + "]", t);
		}finally {
			if(in != null) {
				try {
					in.close();
				} catch(IOException e) {
					throw new DAOException("Impossible to properly close stream to file file [" + calculatedFieldsFile + "]", e);
				}
			}
			logger.debug("OUT");
		}
		
		return calculatedFiledsMap;
	}
	
	private String loadExpression(Node calculatedFieldNode) {
		String expression;
		
		expression = null;
		
		Node expressionNode = calculatedFieldNode.selectSingleNode("EXPRESSION");
		if(expressionNode != null) {
			expression = expressionNode.getStringValue();
		} else { // for back compatibility
			expression = calculatedFieldNode.getStringValue();
		}
		
		return expression;
	}	
	
	private List<ModelCalculatedField.Slot> loadSlots(Node calculatedFieldNode) {
		
		List<ModelCalculatedField.Slot> slots = new ArrayList<ModelCalculatedField.Slot>();
		
		Node slotBlock = calculatedFieldNode.selectSingleNode("SLOTS");
		if(slotBlock != null) {
			List<Node> slotNodes = slotBlock.selectNodes("SLOT");
			
			for(Node slotNode : slotNodes) {
				ModelCalculatedField.Slot slot = loadSlot(slotNode);
				slots.add(slot);
			}
		}
		
		return slots;
	}
		
	private String loadDefaultSlotValue(Node calculatedFieldNode) {
			
		String defaultSoltValue = null;
			
		Node slotBlock = calculatedFieldNode.selectSingleNode("SLOTS");
		if(slotBlock != null) {
			defaultSoltValue = slotBlock.valueOf("@defaultSoltValue");	
		}
			
		return defaultSoltValue;
	}
	
	
	private ModelCalculatedField.Slot loadSlot(Node slotNode) {
		ModelCalculatedField.Slot slot;
		
		String slotValue = slotNode.valueOf("@value");	
		slot = new ModelCalculatedField.Slot(slotValue);
		
		List<Node> mappedValues = slotNode.selectNodes("VALUESET");
		for(Node mappedValuesNode:  mappedValues) {
			ModelCalculatedField.Slot.IMappedValuesDescriptor descriptor = loadDescriptor(mappedValuesNode);
			slot.addMappedValuesDescriptors(descriptor);
		}
		
		return slot;
	}
	
	private ModelCalculatedField.Slot.IMappedValuesDescriptor loadDescriptor(Node mappedValuesNode) {
		ModelCalculatedField.Slot.IMappedValuesDescriptor descriptor = null;
		
		String descriptorType = mappedValuesNode.valueOf("@type");	
		if(descriptorType.equalsIgnoreCase("range")) {
			descriptor = loadRangeDescriptor(mappedValuesNode);			
		} else if(descriptorType.equalsIgnoreCase("punctual")) {
			descriptor = loadPunctualDescriptor(mappedValuesNode);
		}
		
		return descriptor;
	}
	
	
	private ModelCalculatedField.Slot.MappedValuesPunctualDescriptor loadPunctualDescriptor(Node mappedValuesNode) { 
		ModelCalculatedField.Slot.MappedValuesPunctualDescriptor punctualDescriptor;
		
		punctualDescriptor = new ModelCalculatedField.Slot.MappedValuesPunctualDescriptor();
		List<Node> punctualValueNodes = mappedValuesNode.selectNodes("VALUE");
		for(Node punctualValueNode : punctualValueNodes) {
			String punctualValue = punctualValueNode.valueOf("@value");
			punctualDescriptor.addValue( punctualValue );
		}
		
		return punctualDescriptor;
	}
	
	private ModelCalculatedField.Slot.MappedValuesRangeDescriptor loadRangeDescriptor(Node mappedValuesNode) { 
		ModelCalculatedField.Slot.MappedValuesRangeDescriptor rangeDescriptor = null;
		
		Node fomrNode = mappedValuesNode.selectSingleNode("FROM");
		String fromValue = fomrNode.valueOf("@value");
		Node toNode = mappedValuesNode.selectSingleNode("TO");
		String toValue = toNode.valueOf("@value");
		rangeDescriptor = new ModelCalculatedField.Slot.MappedValuesRangeDescriptor(fromValue, toValue);
		String includeValue = null;
		includeValue = fomrNode.valueOf("@include");
		if(includeValue != null && (includeValue.equalsIgnoreCase("TRUE") || includeValue.equalsIgnoreCase("FALSE"))) {
			rangeDescriptor.setIncludeMinValue(Boolean.parseBoolean(includeValue));
		}
		includeValue = toNode.valueOf("@include");
		if(includeValue != null && (includeValue.equalsIgnoreCase("TRUE") || includeValue.equalsIgnoreCase("FALSE"))) {
			rangeDescriptor.setIncludeMaxValue(Boolean.parseBoolean(includeValue));
		}
		
		return rangeDescriptor;
	}
	
	
	

	// =============================================================================
	// SAVE
	// =============================================================================
	
	public void saveCalculatedFields(Map<String, List<ModelCalculatedField>> calculatedFields) {
		
		File calculatedFieldsFile;
		Iterator it;
		String entityName;
		List fields;
		Document document;
		Element root;
		ModelCalculatedField field;
		
		logger.debug("IN");
		
		calculatedFieldsFile = null;
		
		try {
			Assert.assertNotNull(calculatedFields, "Input parameter [calculatedFields] cannot be null");
			
			calculatedFieldsFile = getCalculatedFieldsFile();
			Assert.assertNotNull(calculatedFieldsFile, "Destination file cannot be null");
			logger.debug("Calculated fields will be saved on file [" + calculatedFieldsFile + "]");
			
			if( !calculatedFieldsFile.getParentFile().exists() ) {
				DAOException e = new DAOException("Destination file folder [" + calculatedFieldsFile.getPath()+ "] does not exist");
				e.addHint("Check if [" + calculatedFieldsFile.getPath()+ "] folder exist on your server filesystem. If not create it.");
				throw e;
			}
			
			if( calculatedFieldsFile.exists() ) {
				logger.warn("File [" + calculatedFieldsFile + "] already exists. New settings will override the old ones.");
			}
			
			document = DocumentHelper.createDocument();
	        root = document.addElement( ROOT_TAG );
	        			
			logger.debug("In the target model there are [" + calculatedFields.keySet() + "] entity/es that contain calculated fields" );
			it = calculatedFields.keySet().iterator();
			while(it.hasNext()) {
				entityName = (String)it.next();
				logger.debug("Serializing [" + calculatedFields.size() + "] calculated fields for entity [" + entityName + "]");
				fields = (List)calculatedFields.get(entityName);
				for(int i = 0; i < fields.size(); i++) {
					field = (ModelCalculatedField)fields.get(i);
					logger.debug("Serializing calculated field [" + field.getName() + "] for entity [" + entityName + "]");
					root.addElement( FIELD_TAG )
		            	.addAttribute( FIELD_TAG_ENTIY_ATTR, entityName )
		            	.addAttribute( FIELD_TAG_NAME_ATTR, field.getName() )
		            	.addAttribute( FIELD_TAG_TYPE_ATTR, field.getType() )
		            	.addAttribute( FIELD_TAG_IN_LINE_ATTR, ""+field.isInLine() )
		            	.addCDATA( field.getExpression() );
				}
			}
			
			guardedWrite(document, calculatedFieldsFile);

		} catch(Throwable t){
			if(t instanceof DAOException) throw (DAOException)t;
			throw new DAOException("An unpredicetd error occurred while saving calculated fields on file [" + calculatedFieldsFile + "]");
		} finally {
			logger.debug("OUT");
		}
	}
	
	private File getCalculatedFieldsFile() {
		File calculatedFieldsFile = null;
		
		calculatedFieldsFile = new File(modelJarFile.getParentFile(), CFIELDS_FILE_NAME);
		
		return calculatedFieldsFile;
	}
	
	
	
	// ------------------------------------------------------------------------------------------------------
	// Guarded actions. see -> http://java.sun.com/docs/books/tutorial/essential/concurrency/guardmeth.html
	// ------------------------------------------------------------------------------------------------------
	
	private boolean locked = false;
	private synchronized void getLock() {
		while(locked) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		locked = true;
	}
	
	private synchronized void releaseLock() {
		locked = false;
	    notifyAll();
	}
	
	private Document guardedRead(File file) {
		FileInputStream in;
		SAXReader reader;
		Document document;
		
		logger.debug("IN");
		
		in = null;
		reader = null;
		
		try {
			
			logger.debug("acquiring lock...");
			getLock();
			logger.debug("Lock acquired");
			
			try {
				in = new FileInputStream(file);
			} catch (FileNotFoundException fnfe) {
				DAOException e = new DAOException("Impossible to load calculated fields from file [" + file.getName() + "]", fnfe);
				e.addHint("Check if [" + file.getPath()+ "] folder exist on your server filesystem. If not create it.");
				throw e;
			}
			Assert.assertNotNull(in, "Input stream cannot be null");				
			
			reader = new SAXReader();
			try {
				document = reader.read(in);
			} catch (DocumentException de) {
				DAOException e = new DAOException("Impossible to parse file [" + file.getName() + "]", de);
				e.addHint("Check if [" + file + "] is a well formed XML file");
				throw e;
			}
			Assert.assertNotNull(document, "Document cannot be null");
		} catch(Throwable t) {
			if(t instanceof DAOException) throw (DAOException)t;
			throw new DAOException("An unpredicetd error occurred while writing on file [" + file + "]");
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch(IOException e) {
					throw new DAOException("Impossible to properly close stream to file [" + file + "]", e);
				}
			}
			logger.debug("releasing lock...");
			releaseLock();
			logger.debug("lock released");
			
			logger.debug("OUT");
		}	
		
		return document;
	}
	private void guardedWrite(Document document, File file) {
		Writer out;
		OutputFormat format;
		XMLWriter writer;
		
		logger.debug("IN");
		
		out = null;
		writer = null;
		
		try {
			
			logger.debug("acquiring lock...");
			getLock();
			logger.debug("Lock acquired");
			
			out = null;
			try {
				out = new FileWriter( file );
			} catch (IOException e) {
				throw new DAOException("Impossible to open file [" + file + "]", e);
			}
			Assert.assertNotNull(out, "Output stream cannot be null");
					
			format = OutputFormat.createPrettyPrint();
			format.setEncoding("ISO-8859-1");
			format.setIndent("    ");
			writer = new XMLWriter(out , format );
	        try {
	        	
				writer.write( document );
				writer.flush();
			} catch (IOException e) {
				throw new DAOException("Impossible to write to file [" + file + "]", e);
			}
		} catch(Throwable t) {
			if(t instanceof DAOException) throw (DAOException)t;
			throw new DAOException("An unpredicetd error occurred while writing on file [" + file + "]");
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch(IOException e) {
					throw new DAOException("Impossible to properly close stream to file file [" + file + "]", e);
				}
			}
			logger.debug("releasing lock...");
			releaseLock();
			logger.debug("lock released");
			
			logger.debug("OUT");
		}
		
	}
	
	
	
}
