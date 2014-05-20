/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package test.parser;

import it.eng.spagobi.engines.whatif.parser.Lexer;
import it.eng.spagobi.engines.whatif.parser.parser;
import junit.framework.TestCase;

/**
 * @author Marco Cortella (marco.cortella@eng.it)
 *
 */
public class MetalanguageParserTestCase extends TestCase {

	parser parserIstance;
	protected void setUp() throws Exception {
		parserIstance = new parser();
		parserIstance.setVerbose(true);
	}
	
	public void testSetSimpleValue(){
    	String expression ="30";
		Lexer lexerInstance = new Lexer(new java.io.StringReader(expression));
		lexerInstance.setVerbose(true);
		parserIstance.setScanner(lexerInstance);
		Object result = null;
		try {
			result = parserIstance.parse().value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertEquals(result, 30.0);

	}
	
	public void testSetDecimalCommaValue(){
    	String expression ="30,0";
		Lexer lexerInstance = new Lexer(new java.io.StringReader(expression));
		lexerInstance.setVerbose(true);
		parserIstance.setScanner(lexerInstance);
		Object result = null;
		try {
			result = parserIstance.parse().value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertEquals(result, 30.0);

	}	
	
	public void testSetDecimalDotValue(){
    	String expression ="30.0";
		Lexer lexerInstance = new Lexer(new java.io.StringReader(expression));
		lexerInstance.setVerbose(true);
		parserIstance.setScanner(lexerInstance);
		Object result = null;
		try {
			result = parserIstance.parse().value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertEquals(result, 30.0);

	}	
	
	public void testSetMember(){
		boolean noException = true;
		
    	String expression ="Measures.Total";
		Lexer lexerInstance = new Lexer(new java.io.StringReader(expression));
		lexerInstance.setVerbose(true);
		parserIstance.setScanner(lexerInstance);
		Object result = null;
		try {
			result = parserIstance.parse().value;
		} catch (Exception e) {
			noException = false;
		}
		assertEquals(noException, true);
	}
	
	public void testSetMultipleMember(){
		boolean noException = true;
		
    	String expression ="Measures.Total;Time.2013";
		Lexer lexerInstance = new Lexer(new java.io.StringReader(expression));
		lexerInstance.setVerbose(true);
		parserIstance.setScanner(lexerInstance);
		Object result = null;
		try {
			result = parserIstance.parse().value;
		} catch (Exception e) {
			noException = false;
		}
		assertEquals(noException, true);
	}
	
	public void testSetMultipleMemberWithAmbiguity(){
		boolean noException = true;
		
    	String expression ="Measures.Total;[2013].[1]";
		Lexer lexerInstance = new Lexer(new java.io.StringReader(expression));
		lexerInstance.setVerbose(true);
		parserIstance.setScanner(lexerInstance);
		Object result = null;
		try {
			result = parserIstance.parse().value;
		} catch (Exception e) {
			noException = false;
		}
		assertEquals(noException, true);
	}
	
	public void testSetPercent(){
		
    	String expression ="50+10%";
		Lexer lexerInstance = new Lexer(new java.io.StringReader(expression));
		lexerInstance.setVerbose(true);
		parserIstance.setScanner(lexerInstance);
		Object result = null;
		try {
			result = parserIstance.parse().value;
		} catch (Exception e) {
		}
		assertEquals(result, 55.0);
	}
	
	public void testSetVariable(){
		/*
		 * TODO: al momento le variabili vengono tutte valorizzate a 50
		 */
    	String expression ="MyVariable";
		Lexer lexerInstance = new Lexer(new java.io.StringReader(expression));
		lexerInstance.setVerbose(true);
		parserIstance.setScanner(lexerInstance);
		Object result = null;
		try {
			result = parserIstance.parse().value;
		} catch (Exception e) {
		}
		assertEquals(result, 50.0);
	}
	
	public void testSetVariablePercent(){
		/*
		 * TODO: al momento le variabili vengono tutte valorizzate a 50
		 */
    	String expression ="MyVariable+10%";
		Lexer lexerInstance = new Lexer(new java.io.StringReader(expression));
		lexerInstance.setVerbose(true);
		parserIstance.setScanner(lexerInstance);
		Object result = null;
		try {
			result = parserIstance.parse().value;
		} catch (Exception e) {
		}
		assertEquals(result, 55.0);
	}
	
	public void testSetDecimalNumbersExpression(){
    	String expression ="5,7+1.3+[1].[1]";
		Lexer lexerInstance = new Lexer(new java.io.StringReader(expression));
		lexerInstance.setVerbose(true);
		parserIstance.setScanner(lexerInstance);
		Object result = null;
		try {
			result = parserIstance.parse().value;
		} catch (Exception e) {
		}
		assertFalse(result.equals(8.1));
	}
	
	public void testSetEqualExpression(){
    	String expression ="=5,7+1.3+[1].[1]";
		Lexer lexerInstance = new Lexer(new java.io.StringReader(expression));
		lexerInstance.setVerbose(true);
		parserIstance.setScanner(lexerInstance);
		Object result = null;
		try {
			result = parserIstance.parse().value;
		} catch (Exception e) {
		}
		assertFalse(result.equals(8.1));
	}
	
	public void testSetVariablesExpression(){
    	String expression ="pippo+5*(6-3)+1+VARIABILE";
		Lexer lexerInstance = new Lexer(new java.io.StringReader(expression));
		lexerInstance.setVerbose(true);
		parserIstance.setScanner(lexerInstance);
		Object result = null;
		try {
			result = parserIstance.parse().value;
		} catch (Exception e) {
		}
		assertEquals(result,116.0);
	}
	
	public void testSetVariableMember(){
		boolean noException = true;
		
    	String expression ="Measures.Total*variab";
		Lexer lexerInstance = new Lexer(new java.io.StringReader(expression));
		lexerInstance.setVerbose(true);
		parserIstance.setScanner(lexerInstance);
		Object result = null;
		try {
			result = parserIstance.parse().value;
		} catch (Exception e) {
			noException = false;
		}
		assertEquals(noException, true);
	}
	
	
	public void testSetMemberAdd(){
		boolean noException = true;
		
    	String expression ="Measures.Total+100";
		Lexer lexerInstance = new Lexer(new java.io.StringReader(expression));
		lexerInstance.setVerbose(true);
		parserIstance.setScanner(lexerInstance);
		Object result = null;
		try {
			result = parserIstance.parse().value;
		} catch (Exception e) {
			noException = false;
		}
		assertEquals(noException, true);
	}
	
	public void testSetMemberPercent(){
		boolean noException = true;
		
    	String expression ="Measures.Total+4%";
		Lexer lexerInstance = new Lexer(new java.io.StringReader(expression));
		lexerInstance.setVerbose(true);
		parserIstance.setScanner(lexerInstance);
		Object result = null;
		try {
			result = parserIstance.parse().value;
		} catch (Exception e) {
			noException = false;
		}
		assertEquals(noException, true);
	}
	
	public void testSetExpressionMemberVariable(){
		boolean noException = true;
		
    	String expression ="Measures.Total-X+100";
		Lexer lexerInstance = new Lexer(new java.io.StringReader(expression));
		lexerInstance.setVerbose(true);
		parserIstance.setScanner(lexerInstance);
		Object result = null;
		try {
			result = parserIstance.parse().value;
		} catch (Exception e) {
			noException = false;
		}
		assertEquals(noException, true);
	}
	
	public void testSetExpressionParentheses(){
		boolean noException = true;
		
    	String expression ="((X*2)-10)+2";
		Lexer lexerInstance = new Lexer(new java.io.StringReader(expression));
		lexerInstance.setVerbose(true);
		parserIstance.setScanner(lexerInstance);
		Object result = null;
		try {
			result = parserIstance.parse().value;
		} catch (Exception e) {
			noException = false;
		}
		assertEquals(result,92.0);
	}
	
	public void testSetMultipleMembersAdd(){
		boolean noException = true;
		
    	String expression ="Measures.total;Year.2012;Account.1+100";
		Lexer lexerInstance = new Lexer(new java.io.StringReader(expression));
		lexerInstance.setVerbose(true);
		parserIstance.setScanner(lexerInstance);
		Object result = null;
		try {
			result = parserIstance.parse().value;
		} catch (Exception e) {
			noException = false;
		}
		assertEquals(noException, true);
	}
	
	public void testSetMemberWithSpace(){
		boolean noException = true;
		
    	String expression ="Measures.total sales;Year.2012;Product.Food.Canned foods+100";
		Lexer lexerInstance = new Lexer(new java.io.StringReader(expression));
		lexerInstance.setVerbose(true);
		parserIstance.setScanner(lexerInstance);
		Object result = null;
		try {
			result = parserIstance.parse().value;
		} catch (Exception e) {
			noException = false;
		}
		assertEquals(noException, true);
	}
	
	
	

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
