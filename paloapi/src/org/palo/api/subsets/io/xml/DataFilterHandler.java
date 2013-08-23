/*
*
* @file DataFilterHandler.java
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
* @author ArndHouben
*
* @version $Id: DataFilterHandler.java,v 1.11 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.io.xml;

import org.palo.api.Dimension;
import org.palo.api.impl.xml.XMLUtil;
import org.palo.api.subsets.SubsetFilter;
import org.palo.api.subsets.filter.DataFilter;
import org.palo.api.subsets.filter.settings.BooleanParameter;
import org.palo.api.subsets.filter.settings.DataCriteria;
import org.palo.api.subsets.filter.settings.DataFilterSetting;
import org.palo.api.subsets.filter.settings.DoubleParameter;
import org.palo.api.subsets.filter.settings.ObjectParameter;
import org.palo.api.subsets.filter.settings.IntegerParameter;
import org.palo.api.subsets.filter.settings.StringParameter;

/**
 * <code>DataFilterHandler</code>
 * <p>
 * API internal implementation of the {@link SubsetFilterHandler} interface 
 * which handles {@link DataFilter}s.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: DataFilterHandler.java,v 1.11 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
class DataFilterHandler extends AbstractSubsetFilterHandler {

	public static final String XPATH = "/subset/data_filter";

	// all the other valid element paths:
	private static final String CELL_OPERATOR_VALUE = "/subset/data_filter/cell_operator/value";
	private static final String CELL_OPERATOR_PARAMETER = "/subset/data_filter/cell_operator/parameter";
//	private static final String CRITERIA_PARAMETER = "/subset/data_filter/criteria/parameter";
	private static final String CRITERIA_PARAM_1_PARAMETER = "/subset/data_filter/criteria/par1/parameter";
	private static final String CRITERIA_PARAM_1_VALUE = "/subset/data_filter/criteria/par1/value";
	private static final String CRITERIA_OPERATOR_1 = "/subset/data_filter/criteria/op1";
	private static final String CRITERIA_PARAM_2_PARAMETER = "/subset/data_filter/criteria/par2/parameter";
	private static final String CRITERIA_PARAM_2_VALUE = "/subset/data_filter/criteria/par2/value";
	private static final String CRITERIA_OPERATOR_2 = "/subset/data_filter/criteria/op2";
	private static final String SRC_CUBE_VALUE = "/subset/data_filter/subcube/source_cube/value";
	private static final String SRC_CUBE_PARAMETER = "/subset/data_filter/subcube/source_cube/parameter";
	private static final String SUB_CUBE_NEW_SLICE_DIMENSION = "/subset/data_filter/subcube/dimension_coordinates";
	private static final String SUB_CUBE_DIMENSION_PARAMETER = "/subset/data_filter/subcube/dimension_coordinates/parameter";
	private static final String SUB_CUBE_DIMENSION_ELEMENT = "/subset/data_filter/subcube/dimension_coordinates/value/element";
	private static final String TOP_VALUE = "/subset/data_filter/top/value";
	private static final String TOP_PARAMETER = "/subset/data_filter/top/parameter";
	private static final String UPPER_PERC_VALUE = "/subset/data_filter/upper_percentage/value";
	private static final String UPPER_PERC_PARAMETER = "/subset/data_filter/upper_percentage/parameter";
	private static final String LOWER_PERC_VALUE = "/subset/data_filter/lower_percentage/value";
	private static final String LOWER_PERC_PARAMETER = "/subset/data_filter/lower_percentage/parameter";
	private static final String NO_RULES_VALUE = "/subset/data_filter/no_rules/value";
	private static final String NO_RULES_PARAMETER = "/subset/data_filter/no_rules/parameter";

	private final DataFilterSetting setting;
	
	public DataFilterHandler() {
		this(null);
	}
	
	public DataFilterHandler(String sourceCube) {
		this.setting = new DataFilterSetting(sourceCube);
		this.setting.setUseRules(true);
	}

	public final String getXPath() {
		return XPATH;
	}

	public final void enter(String path) {
		if(path.equals(SUB_CUBE_NEW_SLICE_DIMENSION))
			setting.addSlicePart(new ObjectParameter());
	}
	public final void leave(String path, String value) {
//		DataCriteria criteria = setting.getCriteria();
		// check which value should be set:
		if (path.equals(CELL_OPERATOR_VALUE))
			setting.setCellOperator(SubsetXMLHandler.getInteger(value));
		else if (path.equals(CELL_OPERATOR_PARAMETER)) {
			IntegerParameter oldParam = setting.getCellOperator();
			setting.setCellOperator(new IntegerParameter(value));
			setting.setCellOperator(oldParam.getValue());
		}
//		else if(path.equals(CRITERIA_PARAMETER)) {
//			ObjectParameter oldParam = setting.getCriteria();
//			setting.setCriteria(new ObjectParameter(value));
//			setting.getCriteria().setValue(oldParam.getValue());
//		}
		else if (path.equals(CRITERIA_PARAM_1_PARAMETER)) {
			DataCriteria criteria = setting.getCriteria();
			StringParameter par1 = criteria.getFirstOperand();
			StringParameter newPar1 = new StringParameter(XMLUtil.dequote(value));
			newPar1.setValue(par1.getValue());
			criteria.setFirstOperand(newPar1);
		}
		else if (path.equals(CRITERIA_PARAM_1_VALUE)) {
			DataCriteria criteria = setting.getCriteria();
			StringParameter par1 = criteria.getFirstOperand();
			par1.setValue(value);
		}
		else if (path.equals(CRITERIA_OPERATOR_1)) {
			DataCriteria criteria = setting.getCriteria();
			criteria.setFirstOperator(value);
		}
		else if (path.equals(CRITERIA_PARAM_2_PARAMETER)) {
			DataCriteria criteria = setting.getCriteria();
			StringParameter par2 = criteria.getSecondOperand();
			StringParameter newPar2 = new StringParameter(XMLUtil.dequote(value));
			newPar2.setValue(par2.getValue());
			criteria.setSecondOperand(newPar2);
		}
		else if (path.equals(CRITERIA_PARAM_2_VALUE)) {
			DataCriteria criteria = setting.getCriteria();
			StringParameter par2 = criteria.getSecondOperand();
			par2.setValue(value);
		}
		else if (path.equals(CRITERIA_OPERATOR_2)) {
			DataCriteria criteria = setting.getCriteria(); 
			criteria.setSecondOperator(value);
		}
		else if (path.equals(SRC_CUBE_VALUE))
			setting.setSourceCube(value);
		else if (path.equals(SRC_CUBE_PARAMETER)) {
			StringParameter oldParam = setting.getSourceCube();
			setting.setSourceCube(new StringParameter(value));
			setting.setSourceCube(oldParam.getValue());
		}
		else if (path.equals(SUB_CUBE_DIMENSION_PARAMETER)) {
			ObjectParameter sliceDimension = setting.getSlicePart();
			sliceDimension.setName(value);
		}
		else if (path.equals(SUB_CUBE_DIMENSION_ELEMENT))
			setting.addSliceElement(XMLUtil.dequote(value));
		else if (path.equals(TOP_VALUE))
			setting.setTop(SubsetXMLHandler.getInteger(value));
		else if (path.equals(TOP_PARAMETER)) {
			IntegerParameter oldParam = setting.getTop();
			setting.setTop(new IntegerParameter(value));
			setting.setTop(oldParam.getValue());
		}
		else if (path.equals(UPPER_PERC_VALUE))
			setting.setUpperPercentage(SubsetXMLHandler.getDouble(value));
		else if (path.equals(UPPER_PERC_PARAMETER)) {
			DoubleParameter oldParam = setting.getUpperPercentage();
			setting.setUpperPercentage(new DoubleParameter(value));
			setting.setUpperPercentage(oldParam.getValue());
		}
		else if (path.equals(LOWER_PERC_VALUE))
			setting.setLowerPercentage(SubsetXMLHandler.getDouble(value));
		else if (path.equals(LOWER_PERC_PARAMETER)) {
			DoubleParameter oldParam = setting.getLowerPercentage();
			setting.setLowerPercentage(new DoubleParameter(value));
			setting.setLowerPercentage(oldParam.getValue());
		}
		else if (path.equals(NO_RULES_VALUE))
			setting.setUseRules(SubsetXMLHandler.getBoolean(value));
		else if (path.equals(NO_RULES_PARAMETER)) {
			BooleanParameter oldParam = setting.getUseRules();
			setting.setUseRules(new BooleanParameter(value));
			setting.setUseRules(oldParam.getValue());
		}
	}
	
	public final SubsetFilter createFilter(Dimension dimension) {
		return new DataFilter(dimension, setting);
	}

	public static final String getPersistenceString(DataFilter filter) {
		DataFilterSetting setting = 
			(DataFilterSetting) filter.getSettings();
		StringBuffer xmlStr = new StringBuffer();
		xmlStr.append("<data_filter>\r\n");
		//sub and source cube:
		xmlStr.append("<subcube>\r\n");
		xmlStr.append("<source_cube>\r\n");
		xmlStr.append(ParameterHandler.getXML(setting.getSourceCube()));
		xmlStr.append("</source_cube>\r\n");
		//add the slice definition if defined:
		ObjectParameter[] slice = setting.getSliceParameters();
		if(slice.length > 0) {
			for(int i=0;i<slice.length;i++) {
				xmlStr.append("<dimension_coordinates>\r\n");
				//add parameter definition if any for this
				ParameterHandler.addParameter(slice[i], xmlStr);
				//add elements definition
				String[] elIDs = (String[])slice[i].getValue();
				xmlStr.append("<value>\r\n");
				for(String id : elIDs) {
					xmlStr.append("<element>");
					xmlStr.append(id);
					xmlStr.append("</element>\r\n");
				}
				xmlStr.append("</value>\r\n");
				xmlStr.append("</dimension_coordinates>\r\n");
			}
		}			
		xmlStr.append("</subcube>\r\n");
		
		//data criteria
		xmlStr.append("<criteria>\r\n");
		DataCriteria criteria = setting.getCriteria();
		if (criteria != null) {
			// write criteria: first parameter:
			StringParameter operand = criteria.getFirstOperand();
			xmlStr.append("<par1>");
			xmlStr.append(ParameterHandler.getXML(operand, true));
//			xmlStr.append(XMLUtil.quote(operand));
			xmlStr.append("</par1>\r\n");
			xmlStr.append("<op1>");
			String operator = criteria.getFirstOperator();
			xmlStr.append(XMLUtil.printQuoted(operator));
			xmlStr.append("</op1>\r\n");
			// write optional second parameter:
			operand = criteria.getSecondOperand();
			if (operand != null && operand.getValue() != null) {
				xmlStr.append("<par2>");
//				xmlStr.append(XMLUtil.quote(secOp.length() > 0 ? secOp : ""));
				xmlStr.append(ParameterHandler.getXML(operand, true));
				xmlStr.append("</par2>\r\n");
				xmlStr.append("<op2>");
				operator = criteria.getSecondOperator();
				xmlStr.append(XMLUtil.printQuoted(operator));
				xmlStr.append("</op2>\r\n");
			}
		}
		xmlStr.append("</criteria>\r\n");
		//top
		IntegerParameter top = setting.getTop();
		if (!(top.getValue() < 0)) { //negative means deactivated
			xmlStr.append("<top>\r\n");
			xmlStr.append(ParameterHandler.getXML(top));
			xmlStr.append("</top>\r\n");
		}
		//upper percentage
		DoubleParameter upper = setting.getUpperPercentage();
		if (!(upper.getValue() < 0)) { // negative means deactivated
			xmlStr.append("<upper_percentage>\r\n");
			xmlStr.append(ParameterHandler.getXML(upper));
			xmlStr.append("</upper_percentage>\r\n");
		}
		// optional lower percentage
		DoubleParameter lower = setting.getLowerPercentage();
		if (!(lower.getValue() < 0)) {
			xmlStr.append("<lower_percentage>\r\n");
			xmlStr.append(ParameterHandler.getXML(lower));
			xmlStr.append("</lower_percentage>\r\n");
		}
		xmlStr.append("<cell_operator>\r\n");
		xmlStr.append(ParameterHandler.getXML(
				setting.getCellOperator()));
		xmlStr.append("</cell_operator>\r\n");
		
		//rules
		xmlStr.append("<no_rules>\r\n");		
		BooleanParameter useRules = setting.getUseRules();
		ParameterHandler.addParameter(useRules, xmlStr);
		xmlStr.append("<value>");
		xmlStr.append(useRules.getValue().booleanValue() ? "0" : "1");
		xmlStr.append("</value>\r\n");
		xmlStr.append("</no_rules>\r\n");
		//prolog ;)
		xmlStr.append("</data_filter>\r\n");

		return xmlStr.toString();
	}
}
