/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.qbe.statement;

import java.util.StringTokenizer;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class StatementTockenizer extends StringTokenizer{

	private String satement;
	private String currentToken;
	
	private static final String DELIMITERS = "+-|*/()";
	
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
			if(nextToken != null) nextToken = nextToken.trim();
//			if(nextToken.contains("::")){
//				nextToken =  nextToken + super.nextToken("+-|*/");
//			}		
			currentToken = nextToken;
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured during tokenization of statement [" + satement + "] (current token: [" + currentToken + "]; next: token: [" + nextToken + "])", t);
		}
		
		return nextToken;
	}

}
