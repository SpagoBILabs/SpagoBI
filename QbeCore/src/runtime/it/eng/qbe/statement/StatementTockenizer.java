/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.qbe.statement;

import java.util.StringTokenizer;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class StatementTockenizer extends StringTokenizer{

	private String satement;
	private String currentToken;
	
	private static final String DELIMITERS = "+-|*/()<>=!";
	private static final String[] ADDITIONALS_DELIMITERS_SUBSTRING_FUNCTIONS = {" like ", "case when", " then ", " end ", "not in ", " in ", " between", "is not null ", "is null ", "is not empty " , "is empty ", "not member of", "member of"};
	
	//private static final String[] ADDITIONALS_DELIMITERS_FUNCTIONS = {"concat", "current_date", "current_time", "current_timestamp","substring", "trim", "lower", "upper", "length", "locate", "abs", "sqrt", "bit_length", "mod", "coalesce","nullif","str","size", "minelement", "maxelement", "minindex", "maxindex","elements","sign","trunc","rtrim","sin"};
	
	/**
	 * @param str
	 */
	public StatementTockenizer(String str) {
		super(str, DELIMITERS);
		satement = str;
		currentToken = null;
	}
	
	public String nextTokenInStatement(){
		String nextToken;
		
		nextToken = null;
		try {
			nextToken =  super.nextToken();
			if(nextToken != null){
				nextToken = cleanTockenFromKeyWords(nextToken);
				nextToken = nextToken.trim();
			} 	
			currentToken = nextToken;
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured during tokenization of statement [" + satement + "] (current token: [" + currentToken + "]; next: token: [" + nextToken + "])", t);
		}
		
		return nextToken;
	}
	
	public String cleanTockenFromKeyWords(String tocken){
		for(int i=0; i<ADDITIONALS_DELIMITERS_SUBSTRING_FUNCTIONS.length; i++){
			tocken = tocken.replace( ADDITIONALS_DELIMITERS_SUBSTRING_FUNCTIONS[i], "");
		}
		return tocken;
		
	}
	


}
