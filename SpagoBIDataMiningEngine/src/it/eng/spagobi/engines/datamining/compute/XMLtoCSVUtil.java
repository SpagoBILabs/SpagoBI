/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.datamining.compute;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLtoCSVUtil {
	// Default private/protected ?
	String source;
	String result;
	String emptyValue = "-";
	String separator = ",";
	String loopFieldName;
	boolean distinct;
	HashSet<String> keepOnlyFieldsNames = new HashSet<String>();
	HashSet<String> ignoreFieldsNames = new HashSet<String>();
	Map<String, String> fields = new TreeMap<String, String>();
	Map<String, String> values = new TreeMap<String, String>();
	Set<Integer> previousValues = new HashSet<Integer>();
	Map<String, Integer> levels = new TreeMap<String, Integer>();
	Map<String, String> loopFields = new TreeMap<String, String>();
	Map<String, Integer> loops = new TreeMap<String, Integer>();
	boolean gotFirstElement;

	/* SETTERS */

	public void setSource(String source) {
		this.source = source;
	}

	public void setEmptyValue(String emptyValue) {
		this.emptyValue = emptyValue;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public void setIgnoreFieldsNames(HashSet<String> ignoreFieldsNames) {
		this.ignoreFieldsNames = ignoreFieldsNames;
	}

	public void setKeepFieldsNames(HashSet<String> keepFieldsNames) {
		this.keepOnlyFieldsNames = keepFieldsNames;
	}

	public void setLoopFieldName(String loopFieldName) {
		this.loopFieldName = loopFieldName;
	}

	/* GETTERS */

	public String getSource() {
		return source;
	}

	public String getEmptyValue() {
		return emptyValue;
	}

	public String getSeparator() {
		return separator;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public HashSet<String> getIgnoreFieldsNames() {
		return ignoreFieldsNames;
	}

	public HashSet<String> getKeepFieldsNames() {
		return keepOnlyFieldsNames;
	}

	public String getLoopFieldName() {
		return loopFieldName;
	}

	public String getResult() {
		return result;
	}

	/**
	 * Constructor
	 * 
	 * @param source
	 */
	public XMLtoCSVUtil(String source) {
		this(source, null, null, null, false);
	}

	/**
	 * Constructor
	 * 
	 * @param source
	 *            the filename our url ot XML schema
	 * @param loopFieldName
	 *            the name of the field that repeats in XML schema
	 */
	public XMLtoCSVUtil(String source, String loopFieldName) {
		this(source, loopFieldName, null, null, false);
	}

	/**
	 * Constructor
	 * 
	 * @param source
	 *            the filename our url ot XML schema
	 * @param loopFieldName
	 *            the name of the field that repeats in XML schema
	 * @param keepOnlyFieldsNames
	 *            the names of the fields that will be kept
	 * @param ignoreFieldsNames
	 *            the names of the fields that will be ignored
	 * @param distinct
	 *            - if true, returns not duplicated rows
	 */
	public XMLtoCSVUtil(String source, String loopFieldName, HashSet<String> keepOnlyFieldsNames, HashSet<String> ignoreFieldsNames, boolean distinct) {
		this(source, loopFieldName, keepOnlyFieldsNames, ignoreFieldsNames, distinct, null, null);
	}

	/**
	 * Constructor
	 * 
	 * @param source
	 *            the filename our url ot XML schema
	 * @param distinct
	 *            - if true, returns not duplicated rows
	 * @param emptyValue
	 *            the value for empty data
	 * @param separator
	 *            the value for separator in CSV
	 */
	public XMLtoCSVUtil(String source, boolean distinct, String emptyValue, String separator) {
		this(source, null, null, null, distinct, emptyValue, separator);
	}

	/**
	 * Constructor
	 * 
	 * @param source
	 *            the filename our url ot XML schema
	 * @param loopFieldName
	 *            the name of the field that repeats in XML schema
	 * @param keepOnlyFieldsNames
	 *            the names of the fields that will be kept
	 * @param ignoreFieldsNames
	 *            the names of the fields that will be ignored
	 * @param distinct
	 *            - if true, returns not duplicated rows
	 * @param emptyValue
	 *            the value for empty data
	 * @param separator
	 *            the value for separator in CSV
	 */
	public XMLtoCSVUtil(String source, String loopFieldName, HashSet<String> keepOnlyFieldsNames, HashSet<String> ignoreFieldsNames, boolean distinct, String emptyValue,
			String separator) {
		this.source = source;
		this.loopFieldName = loopFieldName;
		if (keepOnlyFieldsNames != null) {
			this.keepOnlyFieldsNames.addAll(keepOnlyFieldsNames);
		}
		if (ignoreFieldsNames != null) {
			this.ignoreFieldsNames.addAll(ignoreFieldsNames);
		}
		this.distinct = distinct;
		if (emptyValue != null) {
			this.emptyValue = emptyValue;
		}
		if (separator != null) {
			this.separator = separator;
		}
	}

	/**
	 * Converts XML data to CSV data
	 * 
	 * @return converted data
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public String convert() throws ParserConfigurationException, SAXException, IOException {
		this.fields.clear();
		this.levels.clear();
		this.values.clear();
		previousValues.clear();
		this.gotFirstElement = false;
		this.result = "";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document root = builder.parse(this.source);
		this.parseFields(root, 0);
		if (this.loopFieldName == null)
			this.determineLoopFieldName();
		this.parseValues(root, 0);
		return this.result;
	}

	/**
	 * Parse nodes to the fields, recursive function
	 * 
	 * @param node
	 * @param level
	 */
	void parseFields(Node node, int level) {
		this.addToLoopFields(level, node.getNodeName());
		if (node.hasAttributes()) {
			NamedNodeMap attributes = node.getAttributes();
			for (int i = 0; i < attributes.getLength(); i++) {
				Node attribute = attributes.item(i);
				this.addToFields(level, node.getNodeName(), attribute.getNodeName());
			}
		}

		boolean hastext = false;
		boolean haselement = false;
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element) {
				haselement = true;
				this.parseFields(child, level + 1);
			} else if (child.getNodeName().equals("#text") && !child.getNodeValue().trim().isEmpty()) {
				hastext = true;
			}
		}
		if (!haselement && hastext) {
			this.addToFields(level, node.getNodeName());
		}
	}

	/**
	 * Parse nodes to the values, writes data to result, recursive function
	 * 
	 * @param node
	 * @param level
	 */
	void parseValues(Node node, int level) {
		if (node.getNodeName().equals(this.loopFieldName)) {
			if (this.gotFirstElement) {
				this.printMapToResult(values);
				this.resetValuesInRow(level);
			} else {
				this.printMapToResult(fields);
				this.gotFirstElement = true;
			}
		}
		if (node.hasAttributes()) {
			NamedNodeMap attributes = node.getAttributes();
			for (int i = 0; i < attributes.getLength(); i++) {
				Node attribute = attributes.item(i);
				this.addToValues(attribute.getNodeValue(), level, node.getNodeName(), attribute.getNodeName());
			}
		}

		boolean hastext = false;
		boolean haselement = false;
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element) {
				haselement = true;
				this.parseValues(child, level + 1);
			} else if (child.getNodeName().equals("#text") && !child.getNodeValue().trim().isEmpty()) {
				hastext = true;
			}
		}
		if (!haselement && hastext) {
			this.addToValues(node.getFirstChild().getNodeValue(), level, node.getNodeName());
		}
	}

	/**
	 * Determine loopFieldName
	 */
	void determineLoopFieldName() {
		int max = 0;
		String maxField = null;
		for (String key : this.loopFields.keySet()) {
			if (this.loops.get(key) > max) {
				max = this.loops.get(key);
				maxField = this.loopFields.get(key);
			}
			if (max >= 2)
				break;
		}
		this.loopFieldName = maxField;
	}

	/**
	 * Add node to the fields' Tree Maps
	 * 
	 * @param level
	 * @param nodeName
	 */
	void addToFields(int level, String nodeName) {
		this.addToFields(level, nodeName, "");
	}

	/**
	 * Add node to the fields' Tree Maps
	 * 
	 * @param level
	 * @param nodeName
	 * @param argName
	 */
	void addToFields(int level, String nodeName, String argName) {
		if (!this.keepOnlyFieldsNames.isEmpty()) {
			if (!this.keepOnlyFieldsNames.contains(nodeName))
				return;
		} else {
			if (this.ignoreFieldsNames.contains(nodeName)) {
				return;
			}
		}
		String key = this.makeKey(level, nodeName, argName);
		String name = this.makeKey(null, nodeName, argName);
		this.fields.put(key, name);
		this.values.put(key, this.emptyValue);
		this.levels.put(key, level);
	}

	/**
	 * Helper function to determine loopFieldName
	 * 
	 * @param level
	 * @param nodeName
	 */
	void addToLoopFields(int level, String nodeName) {
		String key = this.makeKey(level, nodeName, null);
		String name = this.makeKey(null, nodeName, null);
		this.loopFields.put(key, name);
		Integer loop = this.loops.get(key);
		if (loop == null)
			loop = 0;
		this.loops.put(key, loop + 1);
	}

	/**
	 * Add node to the values' Tree Maps
	 * 
	 * @param value
	 * @param level
	 * @param nodeName
	 */
	void addToValues(String value, int level, String nodeName) {
		this.addToValues(value, level, nodeName, null);
	}

	/**
	 * Add node to the values' Tree Maps
	 * 
	 * @param value
	 * @param level
	 * @param nodeName
	 * @param argName
	 */
	void addToValues(String value, int level, String nodeName, String argName) {
		String key = this.makeKey(level, nodeName, argName);
		if (this.fields.containsKey(key)) {
			this.values.put(key, value);
		}
	}

	/**
	 * Generate key to the Tree Maps
	 * 
	 * @param level
	 * @param nodeName
	 * @param argName
	 * @return generated key
	 */
	String makeKey(Integer level, String nodeName, String argName) {

		String ret = "";
		if (level != null) {
			ret += level + ":";
		}
		ret += nodeName;
		if (argName != null && !argName.isEmpty())
			ret += " [" + argName + "]";
		return ret;
	}

	/**
	 * Reset values in the row for fields, which level is greater/equeal then/to
	 * argument
	 * 
	 * @param level
	 */
	void resetValuesInRow(int level) {
		for (String i : this.levels.keySet()) {
			if (this.levels.get(i) >= level) {
				this.values.put(i, this.emptyValue);
			}
		}
	}

	/**
	 * Print row data to result
	 * 
	 * @param map
	 */
	void printMapToResult(Map<String, String> map) {
		if (this.distinct) {
			if (previousValues.contains(map.hashCode())) {
				return;
			}
			previousValues.add(map.hashCode());
		}
		Iterator<String> iter = map.values().iterator();
		while (iter.hasNext()) {
			result += iter.next().replace(this.separator, " ");
			if (iter.hasNext()) {
				result += this.separator;
			}
		}
		result += System.getProperty("line.separator");
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		String loop = null;
		HashSet<String> keep = new HashSet<String>();
		HashSet<String> ignore = new HashSet<String>();
		boolean distinct = false;
		String empty = null;
		String separator = null;

		Set<String> optionsWithArgument = new HashSet<String>();
		optionsWithArgument.add("-l");
		optionsWithArgument.add("-k");
		optionsWithArgument.add("-i");
		optionsWithArgument.add("-e");
		optionsWithArgument.add("-s");
		Set<String> options = new HashSet<String>();
		options.addAll(optionsWithArgument);
		options.add("-d");

		int i;
		for (i = 0; i < args.length; i++) {
			String arg = args[i];
			String nextArg = "";
			if (i + 1 < args.length) {
				nextArg = args[i + 1];
			}
			if (optionsWithArgument.contains(arg.toLowerCase()) && (nextArg.isEmpty() || options.contains(nextArg.toLowerCase()))) {
				printHelp();
				return;
			}
			if (arg.equals("-l")) {
				loop = nextArg.trim();
				i++;
			} else if (arg.equals("-k") || arg.equals("-i")) {
				String[] fields = nextArg.split(",");
				for (String field : fields) {
					field = field.trim();
					if (arg.equals("-k")) {
						keep.add(field);
					} else {
						ignore.add(field);
					}
				}
				i++;
			} else if (arg.equals("-d")) {
				distinct = true;
			} else if (arg.equals("-e")) {
				empty = nextArg;
				i++;
			} else if (arg.equals("-s")) {
				separator = nextArg;
				i++;
			} else {
				break;
			}
		}
		if (i + 2 != args.length) {
			printHelp();
			return;
		}

		String source = args[i];
		String destination = args[i + 1];

		if (source.charAt(0) == '-' || destination.charAt(0) == '-') {
			printHelp();
			return;
		}

		XMLtoCSVUtil main = new XMLtoCSVUtil(source, loop, keep, ignore, distinct, empty, separator);
		try {
			main.convert();
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage() + "!");
			return;
		}

		try {
			PrintWriter out = new PrintWriter(destination);
			out.print(main.result);
			out.close();
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage() + "!");
			return;
		}

		System.out.println("Data was successful converted!");

	}

	public static void printHelp() {
		System.out.println("Syntax error!");
		System.out.println("");
		System.out.println("Usage:");
		System.out.println("    xml2csv-conv [-options] <source filename or url> <destination filename>");
		System.out.println("");
		System.out.println("Options");
		System.out.println("    -l <field name>                 Allows to set the name of the field that");
		System.out.println("                                    repeats in XML schema.");
		System.out.println("    -k <list of fields' names>      Field' names that will be kept,");
		System.out.println("                                    separated by comma without space.");
		System.out.println("    -i <list of fields' names>      Field' names that will be ignored,");
		System.out.println("                                    separated by comma without space.");
		System.out.println("    -d                              Returns not duplicated rows.");
		System.out.println("    -e <value>                      Value for empty data, e.g. \"-\"");
		System.out.println("    -s <value>                      Value for separator in CSV, e.g. \",\"");
		System.out.println("");
		System.out.println("Usage examples:");
		System.out.println("    xml2csv-conv -l field -i city,country -d -s \",\" data.xml data.csv");
		System.out.println("    xml2csv-conv -k \"name, surname\" data.xml data.csv");
		System.out.println("    xml2csv-conv http://www.example.com/data.xml data.csv");
		System.out.println("    xml2csv-conv data.xml data.csv");
	}
}
