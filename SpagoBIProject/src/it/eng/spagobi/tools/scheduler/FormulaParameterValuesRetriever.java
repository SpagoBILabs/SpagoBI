/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.scheduler;

import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ParameterValuesRetriever;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * This class retrieves values executing a Formula object
 * 
 * @author Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class FormulaParameterValuesRetriever extends
		ParameterValuesRetriever {
	
	static private Logger logger = Logger.getLogger(FormulaParameterValuesRetriever.class);	

	private Formula formula;
	
	@Override
	public List<String> retrieveValues(BIObjectParameter parameter) throws Exception {
		logger.debug("IN");
		List<String> toReturn = new ArrayList<String>();
		String result = formula.execute();
		logger.debug("Result obtained from formula is [" + result + "]");
		if (result != null) {
			toReturn.add(result);
		}
		logger.debug("IN");
		return toReturn;
	}

	public Formula getFormula() {
		return formula;
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}

}
