/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.commons.serializer;

import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ParameterUse;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Lazar Kostic (lazar.kostic@mht.net)
 */
public class ParametersUseJSONSerialize implements Serializer {

	public static final String ID = "ID";
	public static final String USEID = "USEID";
	public static final String LOVID = "LOVID";
	public static final String DEFAULTLOVID = "DEFAULTLOVID";
	public static final String LABEL = "LABEL";
	public static final String DESCRIPTION = "DESCRIPTION";
	public static final String NAME = "NAME";
	public static final String MANUALINPUT = "MANUALINPUT";
	public static final String SELECTIONTYPE = "SELECTIONTYPE";
	public static final String EXPENDABLE = "EXPENDABLE";
	public static final String DEFAULTFORMULA = "DEFAULTFORMULA";
	public static final String ROLESLIST = "ROLESLIST";
	public static final String CONSTLIST = "CONSTLIST";
	private static final String OPTIONS = "OPTIONS";

	@Override
	public Object serialize(Object o, Locale locale) throws SerializationException {

		JSONObject result = null;

		if (!(o instanceof ParameterUse)) {

			throw new SerializationException("ParametersUseJSONSerializer is unable to serialize object of type: " + o.getClass().getName());

		}

		try {

			ParameterUse parameterUse = null;
			result = new JSONObject();
			parameterUse = (ParameterUse) o;

			result.put(ID, parameterUse.getId());
			result.put(USEID, parameterUse.getUseID());
			result.put(LOVID, parameterUse.getIdLov());
			result.put(LABEL, parameterUse.getLabel());
			result.put(NAME, parameterUse.getName());
			result.put(DESCRIPTION, parameterUse.getDescription());
			result.put(MANUALINPUT, parameterUse.getManualInput());
			result.put(SELECTIONTYPE, parameterUse.getSelectionType());
			result.put(EXPENDABLE, parameterUse.isMaximizerEnabled());
			result.put(DEFAULTLOVID, parameterUse.getIdLovForDefault());
			result.put(DEFAULTFORMULA, parameterUse.getDefaultFormula());
			result.put(OPTIONS, parameterUse.getOptions());

			JSONArray ja = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(parameterUse.getAssociatedRoles(), null);
			if (ja != null) {

				for (int i = 0; i < ja.length(); i++) {
					ja.getJSONObject(i).put("CHECKED", true);
				}
			}
			result.put(ROLESLIST, ja);

			JSONArray ca = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(parameterUse.getAssociatedChecks(), null);
			if (ca != null) {

				for (int i = 0; i < ca.length(); i++) {
					ca.getJSONObject(i).put("CHECKED", true);
				}
			}
			result.put(CONSTLIST, ca);

		} catch (Throwable t) {

			throw new SerializationException("An error occurred while serializing object: " + o, t);

		} finally {

		}

		return result;
	}
}
