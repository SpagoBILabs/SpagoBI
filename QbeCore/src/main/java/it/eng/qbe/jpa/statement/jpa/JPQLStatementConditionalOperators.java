/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.qbe.jpa.statement.jpa;

import it.eng.qbe.runtime.query.CriteriaConstants;
import it.eng.qbe.runtime.statement.IConditionalOperator;
import it.eng.spagobi.utilities.StringUtils;
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class JPQLStatementConditionalOperators {

	private static Map<String, IConditionalOperator> conditionalOperators;

	public static IConditionalOperator getOperator(String operatorName) {
		return conditionalOperators.get(operatorName);
	}

	static {
		conditionalOperators = new HashMap<String, IConditionalOperator>();
		conditionalOperators.put(CriteriaConstants.EQUALS_TO, new IConditionalOperator() {
			public String getName() {
				return CriteriaConstants.EQUALS_TO;
			}

			@Override
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				return leftHandValue + "=" + rightHandValues[0];
			}
		});
		conditionalOperators.put(CriteriaConstants.NOT_EQUALS_TO, new IConditionalOperator() {
			public String getName() {
				return CriteriaConstants.NOT_EQUALS_TO;
			}

			@Override
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				return leftHandValue + "!=" + rightHandValues[0];
			}
		});
		conditionalOperators.put(CriteriaConstants.GREATER_THAN, new IConditionalOperator() {
			public String getName() {
				return CriteriaConstants.GREATER_THAN;
			}

			@Override
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				return leftHandValue + ">" + rightHandValues[0];
			}
		});
		conditionalOperators.put(CriteriaConstants.EQUALS_OR_GREATER_THAN, new IConditionalOperator() {
			public String getName() {
				return CriteriaConstants.EQUALS_OR_GREATER_THAN;
			}

			@Override
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				return leftHandValue + ">=" + rightHandValues[0];
			}
		});
		conditionalOperators.put(CriteriaConstants.LESS_THAN, new IConditionalOperator() {
			public String getName() {
				return CriteriaConstants.LESS_THAN;
			}

			@Override
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				return leftHandValue + "<" + rightHandValues[0];
			}
		});
		conditionalOperators.put(CriteriaConstants.EQUALS_OR_LESS_THAN, new IConditionalOperator() {
			public String getName() {
				return CriteriaConstants.EQUALS_OR_LESS_THAN;
			}

			@Override
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				return leftHandValue + "<=" + rightHandValues[0];
			}
		});
		conditionalOperators.put(CriteriaConstants.STARTS_WITH, new IConditionalOperator() {
			public String getName() {
				return CriteriaConstants.STARTS_WITH;
			}

			@Override
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				String rightHandValue = rightHandValues[0].trim();
				if (rightHandValue.startsWith("'")) {
					rightHandValue = rightHandValue.substring(1, rightHandValue.length() - 1);
					rightHandValue = rightHandValue + "%";
				} else {
					// field reference
					rightHandValue = "' || " + rightHandValue + " || '%";
				}
				return leftHandValue + " like '" + rightHandValue + "'";
			}
		});
		conditionalOperators.put(CriteriaConstants.NOT_STARTS_WITH, new IConditionalOperator() {
			public String getName() {
				return CriteriaConstants.NOT_STARTS_WITH;
			}

			@Override
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				String rightHandValue = rightHandValues[0].trim();
				if (rightHandValue.startsWith("'")) {
					rightHandValue = rightHandValue.substring(1, rightHandValue.length() - 1);
					rightHandValue = rightHandValue + "%";
				} else {
					// field reference
					rightHandValue = "' || " + rightHandValue + " || '%";
				}
				return leftHandValue + " not like '" + rightHandValue + "'";
			}
		});
		conditionalOperators.put(CriteriaConstants.ENDS_WITH, new IConditionalOperator() {
			public String getName() {
				return CriteriaConstants.ENDS_WITH;
			}

			@Override
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				String rightHandValue = rightHandValues[0].trim();
				if (rightHandValue.startsWith("'")) {
					rightHandValue = rightHandValue.substring(1, rightHandValue.length() - 1);
					rightHandValue = "%" + rightHandValue;
				} else {
					// field reference
					rightHandValue = "%' || " + rightHandValue + " || '";
				}
				return leftHandValue + " like '" + rightHandValue + "'";
			}
		});
		conditionalOperators.put(CriteriaConstants.NOT_ENDS_WITH, new IConditionalOperator() {
			public String getName() {
				return CriteriaConstants.NOT_ENDS_WITH;
			}

			@Override
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				String rightHandValue = rightHandValues[0].trim();
				if (rightHandValue.startsWith("'")) {
					rightHandValue = rightHandValue.substring(1, rightHandValue.length() - 1);
					rightHandValue = "%" + rightHandValue;
				} else {
					// field reference
					rightHandValue = "%' || " + rightHandValue + " || '";
				}
				return leftHandValue + " not like '" + rightHandValue + "'";
			}
		});
		conditionalOperators.put(CriteriaConstants.CONTAINS, new IConditionalOperator() {
			public String getName() {
				return CriteriaConstants.CONTAINS;
			}

			@Override
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				String rightHandValue = rightHandValues[0].trim();
				if (rightHandValue.startsWith("'")) {
					rightHandValue = rightHandValue.substring(1, rightHandValue.length() - 1);
					rightHandValue = "%" + rightHandValue + "%";
				} else {
					// field reference
					rightHandValue = "%' || " + rightHandValue + " || '%";
				}
				return leftHandValue + " like '" + rightHandValue + "'";
			}
		});
		conditionalOperators.put(CriteriaConstants.NOT_CONTAINS, new IConditionalOperator() {
			public String getName() {
				return CriteriaConstants.NOT_CONTAINS;
			}

			@Override
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				String rightHandValue = rightHandValues[0].trim();
				if (rightHandValue.startsWith("'")) {
					rightHandValue = rightHandValue.substring(1, rightHandValue.length() - 1);
					rightHandValue = "%" + rightHandValue + "%";
				} else {
					// field reference
					rightHandValue = "%' || " + rightHandValue + " || '%";
				}
				return leftHandValue + " not like '" + rightHandValue + "'";
			}
		});
		conditionalOperators.put(CriteriaConstants.IS_NULL, new IConditionalOperator() {
			public String getName() {
				return CriteriaConstants.IS_NULL;
			}

			@Override
			public String apply(String leftHandValue, String[] rightHandValue) {
				return leftHandValue + " IS NULL";
			}
		});
		conditionalOperators.put(CriteriaConstants.NOT_NULL, new IConditionalOperator() {
			public String getName() {
				return CriteriaConstants.NOT_NULL;
			}

			@Override
			public String apply(String leftHandValue, String[] rightHandValue) {
				return leftHandValue + " IS NOT NULL";
			}
		});

		conditionalOperators.put(CriteriaConstants.BETWEEN, new IConditionalOperator() {
			public String getName() {
				return CriteriaConstants.BETWEEN;
			}

			@Override
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues.length == 2,
						"When BEETWEEN operator is used the operand must contain minValue and MaxValue");
				return leftHandValue + " BETWEEN " + rightHandValues[0] + " AND " + rightHandValues[1];
			}
		});
		conditionalOperators.put(CriteriaConstants.NOT_BETWEEN, new IConditionalOperator() {
			public String getName() {
				return CriteriaConstants.NOT_BETWEEN;
			}

			@Override
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues.length == 2,
						"When BEETWEEN operator is used the operand must contain minValue and MaxValue");
				return leftHandValue + " NOT BETWEEN " + rightHandValues[0] + " AND " + rightHandValues[1];
			}
		});

		conditionalOperators.put(CriteriaConstants.IN, new IConditionalOperator() {
			public String getName() {
				return CriteriaConstants.IN;
			}

			@Override
			public String apply(String leftHandValue, String[] rightHandValues) {
				String rightHandValue = StringUtils.join(rightHandValues, ",");
				return leftHandValue + " IN (" + rightHandValue + ")";
			}
		});
		conditionalOperators.put(CriteriaConstants.NOT_IN, new IConditionalOperator() {
			public String getName() {
				return CriteriaConstants.NOT_IN;
			}

			@Override
			public String apply(String leftHandValue, String[] rightHandValues) {
				String rightHandValue = StringUtils.join(rightHandValues, ",");
				return leftHandValue + " NOT IN (" + rightHandValue + ")";
			}
		});
	}
}
