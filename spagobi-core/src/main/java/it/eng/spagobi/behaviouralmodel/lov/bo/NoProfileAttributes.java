package it.eng.spagobi.behaviouralmodel.lov.bo;

import it.eng.spago.security.IEngUserProfile;

import java.util.ArrayList;
import java.util.List;

public class NoProfileAttributes implements IJavaClassLov {

	public String getValues(IEngUserProfile profile) {

		/* Defining data (fields) in XML form. */
		String result = "<ROWS>";
		result += "<ROW VALUE=\"";
		int i = 2 * 100;
		result += new Integer(i).toString() + "\"/>";
		result += "<ROW VALUE=\"";
		int j = 3 * 100;
		result += new Integer(j).toString() + "\"/>";
		result += "</ROWS>";

		return result;

	}

	public List<String> getNamesOfProfileAttributeRequired() {

		/*
		 * The user will define the profile attribute(s) needed for the LOV.
		 */
		List<String> necessaryProfileAttr = new ArrayList<String>();

		necessaryProfileAttr.add("email");
		necessaryProfileAttr.add("name");

		return necessaryProfileAttr;

	}
}