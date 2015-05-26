package it.eng.spagobi.behaviouralmodel.lov.bo;

import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;

import java.util.ArrayList;
import java.util.List;

public class WithProfileAttributes implements IJavaClassLov {
	public String getValues(IEngUserProfile profile) {

		/* Defining data (fields) in XML form. */
		String result = "<ROWS>";
		result += "<ROW VALUE=\"";
		try {
			result += profile.getUserAttribute("surname") + "\"/>";
		} catch (EMFInternalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result += "<ROW VALUE=\"";
		try {
			result += profile.getUserAttribute("address") + "\"/>";
		} catch (EMFInternalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result += "<ROW VALUE=\"";
		try {
			result += profile.getUserAttribute("email") + "\"/>";
		} catch (EMFInternalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result += "<ROW VALUE=\"";
		try {
			result += profile.getUserAttribute("name") + "\"/>";
		} catch (EMFInternalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result += "<ROW VALUE=\"";
		try {
			result += profile.getUserAttribute("birth_date") + "\"/>";
		} catch (EMFInternalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result += "</ROWS>";

		return result;

	}

	public List<String> getNamesOfProfileAttributeRequired() {

		/*
		 * The user will define the profile attribute(s) needed for the LOV.
		 */
		List<String> necessaryProfileAttr = new ArrayList<String>();

		necessaryProfileAttr.add("surname");
		necessaryProfileAttr.add("address");
		necessaryProfileAttr.add("email");
		necessaryProfileAttr.add("name");
		necessaryProfileAttr.add("birth_date");

		return necessaryProfileAttr;

	}
}