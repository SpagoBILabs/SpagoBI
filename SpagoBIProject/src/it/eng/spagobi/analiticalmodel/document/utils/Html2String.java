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
package it.eng.spagobi.analiticalmodel.document.utils;

import it.eng.spagobi.analiticalmodel.execution.service.PrintNotesAction;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.apache.log4j.Logger;


public class Html2String extends HTMLEditorKit.ParserCallback {
	StringBuffer buffer;
	String toConvert=null;

	private static Logger logger = Logger.getLogger(Html2String.class);

	public Html2String(String toConvert) {
		super();
		this.toConvert = toConvert;
	}

	public void parse() throws IOException {
		logger.debug("IN");
		// put a capo
		toConvert=toConvert.replaceAll("<BR>", "|*|");
		toConvert=toConvert.replaceAll("<BR/>", "|*|");
		toConvert=toConvert.replaceAll("<br>", "|*|");
		toConvert=toConvert.replaceAll("<br/>", "|*|");
		StringReader stringReader=new StringReader(toConvert);
		buffer = new StringBuffer();
		ParserDelegator delegator = new ParserDelegator();
		// the third parameter is TRUE to ignore charset directive
		delegator.parse(stringReader, this, Boolean.FALSE);
		stringReader.close(); 
		logger.debug("OUT");
	}

	public void handleText(char[] text, int pos) {
		buffer.append(text);
	}

	public String getText() {
		return buffer.toString();
	}

	public static synchronized String convertHtml2String(String toConvert){
		logger.debug("IN");
		try{
			Html2String parser=new Html2String(toConvert);
			parser.parse();
			toConvert=parser.getText();
		}
		catch (Exception e) {
			logger.error("parsing failed",e);
			return toConvert;
		}
		logger.debug("OUT");
		return toConvert;
	}

	@Override
	public void handleEndOfLineString(String eol) {
		// TODO Auto-generated method stub
		boolean finish=false;
		int index=buffer.indexOf("|*|");
		while(index!=-1 && finish==false){
			if(buffer.length()>=(index+3)){
				buffer.replace(index, index+3,"\n");
			}
			else{
				finish=true;
				buffer.replace(index, index+3,"");				
			}
			index=buffer.indexOf("|*|");
		}
		super.handleEndOfLineString(eol);
	}

//	public static void main (String[] args) {
//	try {
//	// the HTML to convert
//	FileReader in = new FileReader("java-new.html");
//	Html2String parser = new Html2String();
//	parser.parse(in);
//	in.close();
//	System.out.println(parser.getText());
//	}
//	catch (Exception e) {
//	e.printStackTrace();
//	}
//	}



}


