package it.eng.spagobi.tools.dataset.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.eng.spago.security.IEngUserProfile;

public class provaDataSet implements IJavaClassDataSet {


	public String getValues(Map userProfileAttributes, Map parameters) {
	
		String result = "<ROWS>";
		result += "<ROW VALUE=\"";
		int i = 2*100;
		result += new Integer (i).toString() +"\"/>";
		result += "</ROWS>";
		return result;
		
	}

	public List getNamesOfProfileAttributeRequired(){
		List a=new ArrayList();
		a.add("month");
		return a;
	}
	
	
}
