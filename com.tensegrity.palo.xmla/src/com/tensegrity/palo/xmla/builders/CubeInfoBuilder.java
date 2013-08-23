/*
*
* @file CubeInfoBuilder.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author Michael Raue <Michael.Raue@tensegrity-software.com>
*
* @version $Id: CubeInfoBuilder.java,v 1.48 2009/09/30 07:07:14 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.xmla.builders;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tensegrity.palo.xmla.ExtendedCellInfo;
import com.tensegrity.palo.xmla.XMLAClient;
import com.tensegrity.palo.xmla.XMLAConnection;
import com.tensegrity.palo.xmla.XMLACubeInfo;
import com.tensegrity.palo.xmla.XMLADatabaseInfo;
import com.tensegrity.palo.xmla.XMLADimensionInfo;
import com.tensegrity.palo.xmla.XMLAElementInfo;
import com.tensegrity.palo.xmla.XMLAExecuteProperties;
import com.tensegrity.palo.xmla.XMLAProperties;
import com.tensegrity.palo.xmla.XMLARestrictions;
import com.tensegrity.palo.xmla.XMLAVariableInfo;
import com.tensegrity.palojava.CellInfo;
import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.ElementInfo;
import com.tensegrity.palojava.PropertyInfo;
import com.tensegrity.palojava.VariableInfo;
import com.tensegrity.palojava.impl.CellInfoImpl;

public class CubeInfoBuilder {
	private XMLAClient       xmlaClient;
    private String           connectionName;	
	private XMLADatabaseInfo database;
	private BigInteger       cellCount;
	private XMLAConnection   xmlaConnection;
	    
	public XMLACubeInfo getCubeInfo(XMLAClient client, XMLADatabaseInfo dbInfo, String name, XMLAConnection con) {
		xmlaConnection = con;
		xmlaClient = client;
		database = dbInfo;
		connectionName = client.getConnections()[0].getName();
		
		return requestCube(name);			
	}	
	
	private XMLACubeInfo requestCube(String name) {
		try {
    	    XMLARestrictions rest = new XMLARestrictions();
    	    XMLAProperties   prop = new XMLAProperties();

	        prop.setDataSourceInfo(connectionName);
	        prop.setFormat("Tabular");
	        prop.setContent("SchemaData");
	        prop.setCatalog(database.getId());

	        rest.setCatalog(database.getId());
	        rest.setCubeName(name);
	        
	        Document result = xmlaClient.getCubeList(rest, prop);
		    NodeList nl  = result.getElementsByTagName("row");
		    if (nl == null || nl.getLength() == 0) {
		    	return null;
		    }
			NodeList nlRow = nl.item(0).getChildNodes();
			XMLACubeInfo cubeInfo = null;
			for (int j = 0; j < nlRow.getLength(); j++) {
				if (nlRow.item(j).getNodeType() == Node.ELEMENT_NODE) {
					if (nlRow.item(j).getNodeName().equals("CUBE_NAME")) {
						String text = XMLAClient.getTextFromDOMElement(nlRow
								.item(j));
						cubeInfo = new XMLACubeInfo(text, text, database, connectionName, xmlaClient, xmlaConnection);
						cellCount = new BigInteger("1");
						PropertyInfo pi = 
							xmlaConnection.getPropertyLoader().load(XMLAConnection.PROPERTY_SAP_VARIABLES);		
						if (pi != null) {
							if (Boolean.parseBoolean(pi.getValue())) {
								XMLAVariableInfo [] infos = BuilderRegistry.getInstance().
									getVariableInfoBuilder().requestVariables(
											xmlaClient, (XMLADatabaseInfo) cubeInfo.getDatabase(), cubeInfo.getId());
								cubeInfo.setVariables(infos);
							}
						}
					} else if (nlRow.item(j).getNodeName().equals("DESCRIPTION") && xmlaClient.isSAP()) {
						cubeInfo.setName(XMLAClient.getTextFromDOMElement(nlRow.item(j)));
					} else if (nlRow.item(j).getNodeName().equals("CUBE_CAPTION")) {
						cubeInfo.setName(XMLAClient.getTextFromDOMElement(nlRow.item(j)));
					}
				}
			}
		    return cubeInfo;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
					
	private CellInfo parseValueNode(Node node, String [] strCoord) {
	    NamedNodeMap map = node.getAttributes();
		boolean doubleType = false;

		if (map.getLength() > 0) {
			Node type = map.getNamedItem("xsi:type");
			if (type != null) {
				String value = type.getNodeValue();
				if (value.equals("xsd:decimal") || value.equals("xsd:integer")
						|| value.equals("xsd:double")
						|| value.equals("xsd:int")
						|| value.equals("xsd:unsignedint")) {
					doubleType = true;
				}
			}
		}

		String text = XMLAClient.getTextFromDOMElement(node);
		if (doubleType) {
			CellInfoImpl cell = new CellInfoImpl(CellInfo.TYPE_NUMERIC, true, new Double(text));
			cell.setCoordinate(strCoord);
			return cell;
		} else {
			CellInfoImpl cell = new CellInfoImpl(CellInfo.TYPE_STRING, true, text);
			cell.setCoordinate(strCoord);
			return cell;
		}		
	}
	
	private String getCharacteristicVariableString(PropertyInfo info) {
		int selectionType;
		PropertyInfo prop = info.getChild(
				XMLAConnection.PROPERTY_SAP_VAR_SELECTED_VALUES);
		if (prop == null) {
			return "";
		}
		String selectedText = prop.getValue();
		if (selectedText == null || selectedText.length() == 0) {
			return "";
		}
		PropertyInfo selType = info.getChild(
				XMLAConnection.PROPERTY_SAP_VAR_SELECTIONTYPE);
		if (selType == null) {
			return "";
		}
		try {
			selectionType = Integer.parseInt(selType.getValue());
		} catch (NumberFormatException e) {
			return "";
		}
		PropertyInfo idProp = info.getChild(XMLAConnection.PROPERTY_SAP_VAR_ID);
		String idString = idProp.getValue();
		
		String result = "";
		if (selectionType == VariableInfo.VAR_SELECTION_TYPE_VALUE) {						
			result += " " + idString + " INCLUDING ";
			result += XMLADimensionInfo.transformId(selectedText);
		} else if (selectionType == VariableInfo.VAR_SELECTION_TYPE_INTERVAL) {
			StringTokenizer tok = new StringTokenizer(selectedText, "\n");
			String [] uniqueName = new String[2];
			if (tok.hasMoreTokens()) {
				uniqueName[0] = tok.nextToken();
			} else {
				return "";
			}
			if (tok.hasMoreTokens()) {
				uniqueName[1] = tok.nextToken();
			} else {
				uniqueName[1] = uniqueName[0];
			}
			result += " " + idString + " INCLUDING ";
			if (uniqueName[0].equals(uniqueName[1])) {
				result += XMLADimensionInfo.transformId(uniqueName[0]);
			} else {
				result += XMLADimensionInfo.transformId(uniqueName[0]) + ":" + XMLADimensionInfo.transformId(uniqueName[1]); 									
			}
		} else if (selectionType == VariableInfo.VAR_SELECTION_TYPE_COMPLEX) {	
			StringTokenizer tok = new StringTokenizer(selectedText, "\n");
			StringBuffer tempResult = new StringBuffer();
			while (tok.hasMoreTokens()) {
				tempResult.append(" " + idString + " INCLUDING " + 
						XMLADimensionInfo.transformId(tok.nextToken()));
			}
			result = tempResult.toString();
		}
		
		return result;
	}
	
	private String getFloatingPointVariableString(PropertyInfo info) {
		PropertyInfo prop = info.getChild(
				XMLAConnection.PROPERTY_SAP_VAR_SELECTED_VALUES);
		if (prop == null) {
			return "";
		}
		String text = prop.getValue();
		if (text == null || text.length() == 0) {
			return "";
		}
		PropertyInfo idProp = info.getChild(
				XMLAConnection.PROPERTY_SAP_VAR_ID);
		return " " + XMLADimensionInfo.transformId(idProp.getValue()) + " INCLUDING " + text;
	}
	
	private String analyzeVariable(PropertyInfo info) {
		PropertyInfo [] propInfos = info.getChildren();
		if (propInfos == null) {
			return "";
		}
		for (PropertyInfo pi: propInfos) {
			if (pi.getId().equals(XMLAConnection.PROPERTY_SAP_VAR_DATATYPE)) {
				if (pi.getValue().equals("CHAR")) {
					return getCharacteristicVariableString(info);
				} else if (pi.getValue().equals("FLTP")) {
					return getFloatingPointVariableString(info);
				} else {
					System.out.println("Unknown variable datatype: " + pi.getValue());					
				}
			}
		}
		return "";
	}
	
	private String applyDatabaseSpecificMDX(String query, XMLAClient client, XMLACubeInfo cube) {		
		PropertyInfo pi = 
			xmlaConnection.getPropertyLoader().load(
					XMLAConnection.PROPERTY_SAP_VARIABLES);		
		if (pi == null) {
			return query;
		}
		if (Boolean.parseBoolean(pi.getValue())) {
			PropertyInfo varDef = xmlaConnection.
				getTypedPropertyLoader(cube).load(
						XMLAConnection.PROPERTY_SAP_VARIABLE_DEFINITION);		
			if (varDef == null) {
				return query;
			}
			if (varDef.getChildCount() == 0) {
				return query;
			}
			PropertyInfo [] varInfos = varDef.getChildren();
			if (varInfos != null && varInfos.length > 0) {
				boolean added = false;
				boolean first = true;
				for (int i = 0, n = varInfos.length; i < n; i++) {
					PropertyInfo info = varInfos[i];
					String text = analyzeVariable(info);
					if (text.length() != 0) {
						if (!added) {
							query += " SAP VARIABLES";
							added = true;
						}			
						if (!first) {
							query += ",\n";
						}
						query += text; 
						first = false;
					}
				}
			}
		}
		return query;
	}
	
	public CellInfo getData(String conName, XMLAClient client, XMLACubeInfo cube, XMLAElementInfo [] coordinates, XMLAConnection connection) {		
		CellInfo [] result = 
			getDataBulk(conName, client, cube, 
					new XMLAElementInfo [][] {coordinates}, connection);
		if (result != null && result.length > 0) {
			return result[0];
		}
		String [] strCoord = new String[coordinates.length];
		int counter = 0;
		for (XMLAElementInfo el: coordinates) {
			strCoord[counter++] = el.getId();
		}
		CellInfoImpl cell = new CellInfoImpl(CellInfo.TYPE_NUMERIC, false, "");
		cell.setCoordinate(strCoord);
		return cell;
	}
	
	public CellInfo [] getDataArray(String conName, XMLAClient client, XMLACubeInfo cube, XMLAElementInfo[][] elements, XMLAConnection connection) {
		XMLAExecuteProperties prop = new XMLAExecuteProperties();
		xmlaConnection = connection;
		
		if (elements == null || elements.length == 0 || elements[0] == null) {
			return new CellInfo [] {new CellInfoImpl(CellInfo.TYPE_NUMERIC, false, "")};
		}
		int dimensionSize = elements.length;
		prop.setDataSourceInfo(conName);
		prop.setCatalog(cube.getDatabase().getId());

		if (dimensionSize < 2) {
			return new CellInfo [] {new CellInfoImpl(CellInfo.TYPE_NUMERIC, false, "")};
		}

		CellInfo [] resultValues = null;
		StringBuffer [] axes = new StringBuffer[dimensionSize];
		int axisCounter = 0;
		StringBuffer where = new StringBuffer("(");
		HashMap <String, Integer> elementPositions = new HashMap<String, Integer>();
		try {
			Document doc;
			StringBuffer sb = new StringBuffer("SELECT ");
			boolean first = true;
			int [] querySize = new int[dimensionSize];
			for (int i = 0; i < dimensionSize; i++) {
				querySize[i] = 0;
				axes[i] = new StringBuffer();
				if (i > 1 && elements[i].length == 1) {
//					System.out.println("elements[" + i + "][0] == " + elements[i][0].getName() + " -- " + elements[i][0].getPosition());
					querySize[i] = 1;
					String defName = ((XMLADimensionInfo) elements[i][0].
							getDimension()).getDefaultElementName(); 					
					if (elements[i][0].getUniqueName().equals(defName)) {
						// Do not add a dimension of the same name
						// as this causes problems for Mondrian
						axes[i] = new StringBuffer("-");
						continue;
					}
					if (where.length() > 1) {
						where.append(", ");
					}
					where.append(((XMLAElementInfo) elements[i][0]).getUniqueName());					
					elementPositions.put(((XMLAElementInfo) elements[i][0]).getUniqueName(), 0);
					axes[i] = new StringBuffer("-");
				} else {
					for (int j = 0, m = elements[i].length; j < m; j++) {
						String uniqueName = ((XMLAElementInfo) elements[i][j])
								.getUniqueName();
						axes[i].append(uniqueName);
						elementPositions.put(uniqueName, querySize[i]);
						querySize[i]++;
						if (j < (m - 1)) {
							axes[i].append(", ");
						}
					}									
					
					if (!first) {
						sb.append(", ");
					}
					sb.append("{" + axes[i] + "} on Axis (" + axisCounter + ")");
					axisCounter++;
					first = false;
				}				
			}
			where.append(")");
			sb.append(" FROM [" + cube.getId() + "]");
			if (where.length() > 2) {
				sb.append(" WHERE " + where + "\n");
			}
			HashMap <String, Object []> elementMapper =
				new HashMap<String, Object []>();
			for (int i = 0; i < elements.length; i++) {
				for (int j = 0; j < elements[i].length; j++) {
					elementMapper.put(elements[i][j].getUniqueName(), 
							new Object []{elements[i][j], i,
						elementPositions.get(elements[i][j].getUniqueName())});
				}
			}
			if (sb.length() > 100000) {
//				System.err.println("Query too long, reducing query. This will most probably lead to inconsistent data.");
				sb = shortenQuery(client, cube, sb, null, null, axes,
						dimensionSize, where, elementMapper);
			}
			sb = new StringBuffer(applyDatabaseSpecificMDX(sb.toString(), client, cube));
			if (XMLAClient.isDebug()) {
				System.err.println("GetDataArray:\n" + sb + "\n------------\n\n");
			}
//			System.err.println(sb.toString() + "\n");
			doc = client.execute(sb.toString(), prop);

			NodeList faults = doc.getElementsByTagName("SOAP-ENV:Fault");			
			if (faults != null && faults.getLength() != 0) {
				CellInfo [] cInfo = new CellInfo[1];				
				String result = XMLAClient.getErrorString(faults);
				cInfo[0] = new CellInfoImpl(CellInfo.TYPE_ERROR, true, result);
				((CellInfoImpl) cInfo[0]).setCoordinate(new String[0]);
				return cInfo;
			}
			String [][]	resultCoordinates = 
				parseResultCoordinates(doc);
			
			NodeList cl = doc.getElementsByTagName("CellData");
			if (cl == null) {
				return new CellInfo[0];
			}
			if (cl.item(0) == null) {
				return new CellInfo[0];
			}
			cl = cl.item(0).getChildNodes();

			int totalElements = 1;
			for (int i = 0; i < dimensionSize; i++) {
				totalElements *= elements[i].length;
			}

			resultValues = new CellInfo[totalElements];
			for (int i = 0; i < totalElements; i++) {
				resultValues[i] = new CellInfoImpl(CellInfo.TYPE_NUMERIC, false, "");
			}
			
			int cellOrdinal = -1;
			if (cl != null) {
				for (int i = 0; i < cl.getLength(); i++) {
					if (cl.item(i).getNodeType() == Node.ELEMENT_NODE) {
						Node ordinal = cl.item(i).getAttributes().getNamedItem(
								"CellOrdinal");
						if (ordinal == null) {
							continue;
						}
						cellOrdinal = Integer.parseInt(ordinal.getNodeValue());
						Node cellNode = cl.item(i);
						NodeList nl = cellNode.getChildNodes();

						for (int j = 0; j < nl.getLength(); j++) {
							if (nl.item(j).getNodeType() == Node.ELEMENT_NODE) {
								if (nl.item(j).getNodeName().equals("Value")) {
									resultValues[
									             transformCellOrdinal(cellOrdinal, resultCoordinates, elementMapper, querySize)
									] = parseValueNode(nl
											.item(j), new String []{"a", "b"});
								}
							}
						}
					}
				}
			}
			return resultValues;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new CellInfo [] {new CellInfoImpl(CellInfo.TYPE_NUMERIC, false, "")}; 
	}
	
	private StringBuffer shortenQuery(XMLAClient client, XMLACubeInfo cube, StringBuffer sb, int [] querySize, 
			                          final HashMap [] elementIndexMap,
			                          final StringBuffer [] axes,
			                          final int dimensionSize, StringBuffer where,
			                          HashMap <String, Object []> mapper) {
		int length = sb.length();
		boolean end = false;
		do {
			int max = 0;
			int pos = -1;				
			for (int i = 0; i < dimensionSize; i++) {
				if (querySize != null) {
					if (querySize[i] > max && !axes[i].toString().equals("-") && !axes[i].toString().equals("+")) {
						max = querySize[i];
						pos = i;
					}
				} else {
					if (axes[i].length() > max && !axes[i].toString().equals("-") && !axes[i].toString().equals("+")) {
						max = axes[i].length();
						pos = i;
					}
				}
			}
			if ((length - max) < 90000) {
				end = true;
			}
			length -= max;
			axes[pos] = new StringBuffer();
		} while (!end);
		
		sb = new StringBuffer("SELECT ");
		boolean first = true;
		int axisCounter = 0;
		for (int i = 0, n = dimensionSize; i < n; i++) {
			if (axes[i].length() > 0) {
				if (!axes[i].toString().equals("-") && !axes[i].toString().equals("+")) {
					if (!first) {
						sb.append(", ");
					}
					sb.append("{" + axes[i] + "} on Axis (" + axisCounter + ")\n");
					first = false;
					axisCounter++;
				}
			} else if (!axes[i].toString().equals("+")) {
				DimensionInfo [] dimInfo = BuilderRegistry.getInstance().
					getDimensionInfoBuilder().getDimensionInfo(xmlaConnection,
							client, (XMLADatabaseInfo) cube.getDatabase(), cube);
				
				XMLADimensionInfo dim = (XMLADimensionInfo) dimInfo[i];
				if (!first) {
					sb.append(", ");
				}
				first = false;
				if (dim.getHierarchyUniqueName() != null) {
					sb.append("{" + dim.getHierarchyUniqueName() + ".Members} on Axis (" + axisCounter + ")");					
				} else {
					sb.append("{" + dim.getName() + ".Members} on Axis (" + axisCounter + ")");
				}
				int counter = 0;
				for (ElementInfo el: xmlaConnection.getElements(dim)) {					
					mapper.put(((XMLAElementInfo) el).getUniqueName(), 
							new Object []{el, i,
						counter});
					counter++;
				}
				if (querySize != null) {
					querySize[i] = counter;
				}

				axisCounter++;
				if (elementIndexMap != null) {
					elementIndexMap[i].clear();
					XMLAElementInfo [] els = 
						BuilderRegistry.getInstance().getElementInfoBuilder().
							getElements(xmlaConnection, client, cube.getId(), dim); 
					querySize[i] = els.length;
					for (int j = 0, m = els.length; j < m; j++) {
						elementIndexMap[i].put(els[j].getId(), new Integer(j));
					}
				}
			}
			/*if (i < (dimensionSize - 1)) {
				sb.append(",\n");
			}*/						
		}
		sb.append(" FROM [" + cube.getId() + "]");
		if (where.length() > 2) {
			sb.append(" " + "WHERE " + where + "\n");
		}
		return sb;
	}

	private final String [] convertOrdinal(int ordinal, DimensionInfo [] dimInfo) {
		return new String[] {"" + ordinal}; //result;
	}
	
	private final String [][] parseResultCoordinates(Document doc) {
		NodeList ad = doc.getElementsByTagName("Axes");
		//String [][] resultCoordinates = new String[dimLength][0];
		ArrayList <ArrayList<String>> resultCoordinatesArray =
			new ArrayList<ArrayList<String>>();
		if (ad != null) {
			if (ad.item(0) != null) {
				ad = ad.item(0).getChildNodes();
				if (ad != null) {
					for (int i = 0; i < ad.getLength(); i++) {
						if (ad.item(i).getNodeType() == Node.ELEMENT_NODE) {
							String nodeName = ad.item(i).getNodeName();
							if (!nodeName.equals("Axis")) {
								continue;
							}
							ArrayList <String> els =
								new ArrayList<String>();
							NodeList al = ad.item(i).getChildNodes();
							if (al != null && al.getLength() != 0) {
								for (int j = 0; j < al.getLength(); j++) {
									if (al.item(j).getNodeType() == Node.ELEMENT_NODE) {
										NodeList el = al.item(j).getChildNodes();
										if (el != null && el.getLength() > 0) {
											for (int k = 0; k < el.getLength(); k++) {
												if (el.item(k).getNodeType() == Node.ELEMENT_NODE) {
													NodeList eel = el.item(k).getChildNodes();
													if (eel != null && eel.getLength() > 0) {
														for (int l = 0; l < eel.getLength(); l++) {																
															if (eel.item(l).getNodeType() == Node.ELEMENT_NODE) {
																NodeList eeel = eel.item(l).getChildNodes();
																if (eeel != null && eeel.getLength() > 0) {
																	for (int m = 0; m < eeel.getLength(); m++) {
																		if (eeel.item(m).getNodeType() == Node.ELEMENT_NODE) {
																			if (eeel.item(m).getNodeName().equals("UName")) {																					
																				String id = XMLAClient.getTextFromDOMElement(eeel.item(m));
																				els.add(id);
																			}
																		}
																	}
																}
															}																
														}
													}
												}
											}
										}
									}
								}
							}
							resultCoordinatesArray.add(els);//.toArray(new String[0]);
						}
					}
				}
			}
		}	
		String [][] resultCoordinates = new String[resultCoordinatesArray.size()][];
		for (int i = 0, n = resultCoordinatesArray.size(); i < n; i++) {
			resultCoordinates[i] = resultCoordinatesArray.get(i).toArray(new String[0]);
		}
		return resultCoordinates;
	}
	
	public ExtendedCellInfo readWholeCube(String conName, XMLAClient client, XMLACubeInfo cube, XMLAConnection connection) {
		XMLAExecuteProperties prop = new XMLAExecuteProperties();
		xmlaConnection = connection;
		int dimensionSize = cube.getDimensionCount();
		prop.setDataSourceInfo(conName);
		prop.setCatalog(cube.getDatabase().getId());
		
		if (dimensionSize < 2) {
			return new ExtendedCellInfo(new CellInfo[0], new String[0][0]);
		}
		StringBuffer sb = new StringBuffer("SELECT ");
		DimensionInfo [] dimInfo = BuilderRegistry.getInstance().
			getDimensionInfoBuilder().getDimensionInfo(xmlaConnection,
				client, (XMLADatabaseInfo) cube.getDatabase(), cube);
	
		boolean first = true;
		for (int i = 0; i < dimInfo.length; i++) {
			XMLADimensionInfo dim = (XMLADimensionInfo) dimInfo[i];
			if (!first) {
				sb.append(", ");
			}
			first = false;
			if (dim.getHierarchyUniqueName() != null) {
				sb.append("NON EMPTY " + dim.getHierarchyUniqueName() + " on Axis (" + i + ")");
				//sb.append("NON EMPTY Descendants(" + dim.getHierarchyUniqueName() + ",,LEAVES) on Axis (" + i + ")");
			} else {
				sb.append("NON EMPTY " + dim.getName() + " on Axis (" + i + ")");
				//sb.append("NON EMPTY Descendants(" + dim.getName() + ",,LEAVES) on Axis (" + i + ")");
			}
		}
		sb.append(" FROM [" + cube.getId() + "]");
		
		Document doc;
		try {
			System.out.println(sb.toString());
			XMLAClient.setVerbose(true);
			doc = client.execute(sb.toString(), prop);
			XMLAClient.setVerbose(false);
			String [][] resultCoordinates = new String[dimInfo.length][0];			
			NodeList faults = doc.getElementsByTagName("SOAP-ENV:Fault");
			if (faults != null && faults.getLength() != 0) {
				CellInfo[] cInfo = new CellInfo[1];
				String result = XMLAClient.getErrorString(faults);
				cInfo[0] = new CellInfoImpl(CellInfo.TYPE_ERROR, true, result);
				((CellInfoImpl) cInfo[0]).setCoordinate(new String[0]);
				return new ExtendedCellInfo(cInfo, resultCoordinates);
			}
			resultCoordinates = parseResultCoordinates(doc);
			
			NodeList cl = doc.getElementsByTagName("CellData");

			if (cl == null) {
				return new ExtendedCellInfo(new CellInfo[0], new String[0][0]);
			}
			if (cl.item(0) == null) {
				return new ExtendedCellInfo(new CellInfo[0], new String[0][0]);
			}
			cl = cl.item(0).getChildNodes();
			ArrayList <CellInfo> resultValues = new ArrayList<CellInfo>();
			if (cl != null) {
				for (int i = 0; i < cl.getLength(); i++) {
					if (cl.item(i).getNodeType() == Node.ELEMENT_NODE) {
						Node ordinal = cl.item(i).getAttributes().getNamedItem(
								"CellOrdinal");
						if (ordinal == null) {
							continue;
						}
						int cellOrdinal = Integer.parseInt(ordinal.getNodeValue());
						Node cellNode = cl.item(i);
						NodeList nl = cellNode.getChildNodes();

						for (int j = 0; j < nl.getLength(); j++) {
							if (nl.item(j).getNodeType() == Node.ELEMENT_NODE) {
								if (nl.item(j).getNodeName().equals("Value")) {
									resultValues.add(parseValueNode(nl.item(j), 
											convertOrdinal(cellOrdinal, dimInfo)));
								}
							}
						}
					}
				}
			}
			return new ExtendedCellInfo(resultValues.toArray(new CellInfo[0]), resultCoordinates);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return new ExtendedCellInfo(new CellInfo[0], new String[0][0]);
	}
	
//	private final int transformCellOrdinal(int ordinal, String [][] resultCoordinates, XMLAElementInfo[][] elements, int [] elementLengths) {
//		int result = ordinal;
//		int tempOrdinal = ordinal;
//		
//		int newCellOrdinal = 0;
//		
//		// 1st step: determine the coordinates in resultCoordinates		
//		System.out.print("Ordinal " + ordinal + " translates to ");
//		int [] offset = new int[resultCoordinates.length];
//		for (int i = 0; i < resultCoordinates.length; i++) {
//			if (i != 0) {
//				tempOrdinal /= resultCoordinates[i - 1].length;
//			} 
//			offset[i] = tempOrdinal % resultCoordinates[i].length;
//			//resultCoordinates[i][offset]
//			System.out.print(offset[i] + " ");
//		}		
//		for (int i = resultCoordinates.length - 1; i >= 0; i--) {
//			// TODO improve!!
//			boolean found = false;
//			for (int j = 0; j < elements.length; j++) {
//				for (int k = 0; k < elements[j].length; k++) {
//					if (elements[j][k].getUniqueName().
//						equals(resultCoordinates[i][offset[i]])) {
//						int newOffset = elements[j][k].getPosition();
//						for (int l = 0; l < k; l++) {
//							newOffset *= elementLengths[l];
//						}
//						newCellOrdinal += newOffset;
//						found = true;
//						break;
//					}
//				}
//				if (found) {
//					break;
//				}
//			}
//			if (!found) {
//				System.out.println("NOT FOUND!!");
//			}
//			
//		}
//		System.out.println();
//		System.out.println("New ordinal: " + newCellOrdinal);
//		
//		// 2nd step: take those coordinates and transform them to the passed elements
//		
//		// 3rd step: calculate new ordinal from the elements
//		
//		return newCellOrdinal;
//	}
	
	private final int transformCellOrdinal(int ordinal, String [][] resultCoordinates, HashMap <String, Object []> mapper, int [] elementLengths) {
		int tempOrdinal = ordinal;		
		int newCellOrdinal = 0;		
		int [] offset = new int[resultCoordinates.length];
		
		// Translate current cell ordinal to read coordinates
		for (int i = 0; i < resultCoordinates.length; i++) {
			if (i != 0 && resultCoordinates[i - 1].length != 0) {
				tempOrdinal /= resultCoordinates[i - 1].length;
			} 
			if (resultCoordinates[i].length != 0) {
				offset[i] = tempOrdinal % resultCoordinates[i].length;
			}
		}		
		
		// Translate read coordinates to needed coordinates
		for (int i = resultCoordinates.length - 1; i >= 0; i--) {
			if (resultCoordinates[i].length == 0) {
				continue;
			}
			Object [] res = mapper.get(resultCoordinates[i][offset[i]]);
			if (res == null) {
				//System.err.println("No result stored for: " + resultCoordinates[i][offset[i]]);
				continue;
			}
			XMLAElementInfo el = (XMLAElementInfo) res[0];
			int pos = (Integer) res[1];
			if (res[2] == null) {
				continue;
			}
			int elPos = (Integer) res[2];
			if (el == null) {
				System.err.println("coordinate translation error");
				continue;
			}
			int newOffset = elPos;
			for (int l = 0; l < pos; l++) {
				if (l >= elementLengths.length) {
					break;
				}
				newOffset *= elementLengths[l];
			}
			newCellOrdinal += newOffset;
		}
		return newCellOrdinal;
	}

	public CellInfo [] getDataBulk(String conName, XMLAClient client, XMLACubeInfo cube, XMLAElementInfo[][] elements, XMLAConnection connection) {				
		XMLAExecuteProperties prop = new XMLAExecuteProperties();
		xmlaConnection = connection;
		
		if (elements == null || elements.length == 0 || elements[0] == null
				|| elements[0].length == 0) {
			return new CellInfo [] {new CellInfoImpl(CellInfo.TYPE_NUMERIC, false, "")};
		}
		int dimensionSize = elements[0].length;		
		prop.setDataSourceInfo(conName);
		prop.setCatalog(cube.getDatabase().getId());

		if (dimensionSize < 2) {
			return new CellInfo [] {new CellInfoImpl(CellInfo.TYPE_NUMERIC, false, "")};
		}

		int [] querySize = new int[dimensionSize];
		HashMap [] elementIndexMap = new HashMap[dimensionSize];
		CellInfo [] resultValues = null;
		StringBuffer [] axes = new StringBuffer[dimensionSize];
		StringBuffer where = new StringBuffer("(");
		HashSet <String> allXmlaTypes = new HashSet <String> ();
		HashMap <String, Integer> elementPositions = new HashMap<String, Integer>();
		try {
			Document doc;
			StringBuffer sb = new StringBuffer("SELECT ");
			boolean ffirst = true;
			int axisCounter = 0;
			int axesPresent = 0;
			boolean [] pot = new boolean[dimensionSize];
			for (int i = 0; i < dimensionSize; i++) {
				pot[i] = false;
				elementIndexMap[i] = new HashMap();
				Set axisSet = new HashSet();
				axes[i] = new StringBuffer();
				boolean first = true;
				querySize[i] = 0;				
				for (int j = 0, m = elements.length; j < m; j++) {
					if (!axisSet.contains(elements[j][i])) {
						axisSet.add(elements[j][i]);
						elementIndexMap[i].put(elements[j][i].getId(), new Integer(
								axisSet.size() - 1));
					} else {
						continue;
					}
					if (!first) {
						axes[i].append(", ");
					}
					elementPositions.put(elements[j][i].getUniqueName(), querySize[i]);
					querySize[i]++;
					
					String uniqueName = elements[j][i].getUniqueName();
					axes[i].append(uniqueName);
					first = false;
				}				
				if (axisSet.size() > 1) {
					String xmlaDimName =
						((XMLADimensionInfo) elements[0][i].getDimension()).
							getDimensionUniqueName();
					// Only add it here and do not check if it is already
					// present. This will lead to an error if the database
					// does not support two hierarchies in the same cube,
					// but at least the user is informed about that.
					allXmlaTypes.add(xmlaDimName);
					axesPresent++;
					if (!ffirst) {
						sb.append(",\n");
					}
					sb.append("{" + axes[i] + "} on Axis (" + axisCounter + ")");
					axisCounter++;					
					ffirst = false;					
				} else if (axisSet.size() == 1) {
					pot[i] = true;
				}
			}
			for (int i = 0; i < dimensionSize; i++) {
				if (pot[i] && axesPresent < 2) {
					String xmlaDimName =
						((XMLADimensionInfo) elements[0][i].getDimension()).
							getDimensionUniqueName();
					//if (allXmlaTypes.contains(xmlaDimName)) {
						String defName = ((XMLADimensionInfo) elements[0][i].
								getDimension()).getDefaultElementName(); 
						//if (elements[0][i].getPosition() == 0) {
						if (elements[0][i].getUniqueName().equals(defName)) {
							// Do not add a dimension of the same name
							// as this causes problems for Mondrian
							continue;
						}
					//}
					allXmlaTypes.add(xmlaDimName);
					if (!ffirst) {
						sb.append(",\n");
					}
					sb.append("{" + axes[i] + "} on Axis (" + axisCounter + ")");
					axisCounter++;
					axesPresent++;
					ffirst = false;					
				} else if (pot[i]) {
					String xmlaDimName =
						((XMLADimensionInfo) elements[0][i].getDimension()).
							getDimensionUniqueName();
//					if (allXmlaTypes.contains(xmlaDimName)) {
					//if (!xmlaDimName.equals("[Measures]")) {
					String defName = ((XMLADimensionInfo) elements[0][i].
							getDimension()).getDefaultElementName(); 
					if (elements[0][i].getUniqueName().equals(defName)) {
						//if (elements[0][i].getPosition() == 0) {						
							// Do not add a dimension of the same name
							// as this causes problems for Mondrian
							axes[i] = new StringBuffer("+");
							continue;
						}
					//}
//					}
					allXmlaTypes.add(xmlaDimName);
					
					if (where.length() > 1) {
						where.append(", ");
					}
					where.append(axes[i]);
					axes[i] = new StringBuffer("+");
				} 
			}
			where.append(")");
			sb.append(" FROM [" + cube.getId() + "]");
			if (where.length() > 2) {
				sb.append(" WHERE " + where + "\n");
			}
//			System.out.println("GetDataBulk ==\n" + sb.toString());
			HashMap <String, Object []> elementMapper =
				new HashMap<String, Object []>();
			for (int i = 0; i < elements.length; i++) {
				for (int j = 0; j < elements[i].length; j++) {
					elementMapper.put(elements[i][j].getUniqueName(), 
							new Object []{elements[i][j], j,
						elementPositions.get(elements[i][j].getUniqueName())});
				}
			}
			if (sb.length() > 100000) {
				sb = shortenQuery(client, cube, sb, querySize, elementIndexMap, axes,
						dimensionSize, where, elementMapper);
			}
			sb = new StringBuffer(applyDatabaseSpecificMDX(sb.toString(), client, cube));			
			if (XMLAClient.isDebug()) {
				System.out.println("GetDataBulk:\n" + sb + "\n------------\n\n");
			}
			doc = client.execute(sb.toString(), prop);
			
			NodeList faults = doc.getElementsByTagName("SOAP-ENV:Fault");			
			if (faults != null && faults.getLength() != 0) {
				CellInfo [] cInfo = new CellInfo[1];				
				String result = XMLAClient.getErrorString(faults);
				cInfo[0] = new CellInfoImpl(CellInfo.TYPE_ERROR, true, result);
				((CellInfoImpl) cInfo[0]).setCoordinate(new String[0]);
				return cInfo;
			}
			
			String [][] resultCoordinates = parseResultCoordinates(doc);

			NodeList cl = doc.getElementsByTagName("CellData");			
			if (cl == null) {
				return new CellInfo[0];
			}
			if (cl.item(0) == null) {
				return new CellInfo[0];
			}
			cl = cl.item(0).getChildNodes();

			int totalElements = 1;
			for (int i = 0; i < dimensionSize; i++) {
				if (querySize[i] != 0) {
					totalElements *= querySize[i];
				} else {
					querySize[i] = 1;
				}
			}
			int[] childCardinalities = new int[dimensionSize];
			childCardinalities[0] = 1;
			for (int i = 1; i < dimensionSize; i++) {
				childCardinalities[i] = querySize[i - 1]
						* childCardinalities[i - 1];
			}
			resultValues = new CellInfo[totalElements];
			for (int i = 0; i < totalElements; i++) {
				resultValues[i] = new CellInfoImpl(CellInfo.TYPE_NUMERIC, false, "");
			}
			int cellOrdinal = -1;
			
			if (cl != null) {
				for (int i = 0; i < cl.getLength(); i++) {
					if (cl.item(i).getNodeType() == Node.ELEMENT_NODE) {
						Node ordinal = cl.item(i).getAttributes().getNamedItem(
								"CellOrdinal");
						if (ordinal == null) {
							continue;
						}
						cellOrdinal = Integer.parseInt(ordinal.getNodeValue());
						Node cellNode = cl.item(i);
						NodeList nl = cellNode.getChildNodes();

						for (int j = 0; j < nl.getLength(); j++) {
							if (nl.item(j).getNodeType() == Node.ELEMENT_NODE) {
								if (nl.item(j).getNodeName().equals("Value")) {
									resultValues[transformCellOrdinal(cellOrdinal, resultCoordinates, elementMapper, querySize)] 
									             = parseValueNode(nl
											.item(j), new String []{"a", "b"});
								}
							}
						}
					}
				}
			}
			CellInfo [] resValues = new CellInfo[elements.length];
			for (int i = 0, n = elements.length; i < n; i++) {
				int index = 0;
				String [] coords = new String[dimensionSize];
				for (int j = 0; j < dimensionSize; j++) {					
					Integer iOffset = ((Integer) elementIndexMap[j].get(elements[i][j].getId()));
					if (iOffset != null) {
						index += iOffset.intValue() * childCardinalities[j];
					}
					coords[j] = elements[i][j].getId();
				}								
				resValues[i] = resultValues[index];				
				if (resValues[i] == null) {
					resValues[i] = new CellInfoImpl(CellInfo.TYPE_NUMERIC, false, "");
					((CellInfoImpl) resValues[i]).setCoordinate(coords);
				} else {
					((CellInfoImpl) resValues[i]).setCoordinate(coords);
				}
			}
			return resValues;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new CellInfo [] {new CellInfoImpl(CellInfo.TYPE_NUMERIC, false, "")}; 
	}	
		
	public void setCubeListInternal(XMLADatabaseInfo database, List cubeList) {
		throw new RuntimeException("No longer supported.");
		//cubeLists.put(database, cubeList);
	}	
}
