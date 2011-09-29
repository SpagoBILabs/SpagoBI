/**
Copyright (c) 2005-2008, Engineering Ingegneria Informatica s.p.a.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of 
      conditions and the following disclaimer.
      
    * Redistributions in binary form must reproduce the above copyright notice, this list of 
      conditions and the following disclaimer in the documentation and/or other materials 
      provided with the distribution.
      
    * Neither the name of the Engineering Ingegneria Informatica s.p.a. nor the names of its contributors may
      be used to endorse or promote products derived from this software without specific
      prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE

**/
package it.eng.spagobi.utilities.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.eng.spagobi.utilities.assertion.Assert;

/**
 * @author Andrea Gioia
 *
 */
public class SqlUtils {

	public static boolean isSelectStatement(String query) {
		if(query == null) return false;		
		return query.toUpperCase().trim().startsWith("SELECT");
	}
	
	public static String getSelectClause(String query) {
		String selectClause;
		
		selectClause = null;
		
		Assert.assertNotNull(query, "...");
		Assert.assertTrue( isSelectStatement(query), "...");
		
		int indexOFSelect = query.toUpperCase().indexOf("SELECT");
		int indexOFFrom = query.toUpperCase().indexOf("FROM");
		
		selectClause = query.substring(indexOFSelect + "SELECT".length(), indexOFFrom).trim();
		
		return selectClause;
	}
	
	public static List getSelectFields(String query) {
		return getSelectFields(query, false);
	}
	
	/**
	 * Get the select fields of a query
	 * @param query
	 * @param withAliasSeparator if true remove the quotes at the beginning and end of the alias 
	 * @return a list of String[2] arrays. Where array[0] is the name of the field, array[1] is the alias
	 */
	public static List getSelectFields(String query, boolean withAliasSeparator) {
		List selectFields;
		String selectClause;
		
		Assert.assertNotNull(query, "...");
		Assert.assertTrue( isSelectStatement(query), "...");
		
		selectFields = new ArrayList();
		selectClause = getSelectClause(query);
		String[] fields = selectClause.split(",");
		for(int i = 0; i < fields.length; i++) {
			String f = fields[i];
			String[] field = new String[2];
			String[] tokens = fields[i].trim().split("\\s");
			field[0] = tokens[0]; // the column name
			if(tokens.length > 1) {
				String alias = null;
				if(fields[i].endsWith("'")) {
					Pattern p = Pattern.compile("'[^']*'");
					Matcher m = p.matcher(fields[i]);
					while(m.find()) {
						alias = m.group();
						if(withAliasSeparator){
							alias = alias.trim();
						}else{
							alias.trim().substring(1, alias.length()-1);
						}
					}
				} else if(fields[i].endsWith("\"")) {
					Pattern p = Pattern.compile("\"[^\"]*\"");
					Matcher m = p.matcher(fields[i]);
					while(m.find()) {
						alias = m.group();
						if(withAliasSeparator){
							alias = alias.trim();
						}else{
							alias.trim().substring(1, alias.length()-1);
						}
					}
				} else {
					alias = tokens[tokens.length-1];
				}
				field[1] = alias;
			}
			selectFields.add(field);
		}
		return selectFields;
	}
	
	public static final void main(String args[]) {
		List<String[]> results;
		
		String query = "   select colonna1, " +
				"colonna2 as Colonna2, " +
				"colonna3 as 'Colonna 3', " +
				"colonna4 as \"Colonna 4\", " +
				"\"colonna5\" as \"Colonna 4\", " +
				"'colonna6' as 'Colonna 4', " +
				"'colonna7', " +
				"\"colonna8\", " + 
				"colonna9 Colonna9, " +
				"colonna10 'Colonna 10', " +
				"colonna11 \"Colonna 11\", " +
				"\"colonna12\" \"Colonna 12\", " +
				"'colonna13' 'Colonna 13' " +
				"from table1 where colonna9 = 'pippo'";
				
		
		results = getSelectFields(query);
		for(int i = 0; i < results.size(); i++) {
			System.out.println(results.get(i)[0] + " - " + results.get(i)[1]);
		}
		
	}
}
