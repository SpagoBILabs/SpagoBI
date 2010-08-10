package it.eng.spagobabel.scanner;


import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JSFileScanner extends AbstractScanner {
	
	private Pattern keyPattern;
	private Pattern lnfnPattern;
	
	private static final String keyRegExp = "'[\\.a-zA-Z0-9]+'";
	private static final String lnfnRegExp = "LN[\\s]*\\([\\s]*" + keyRegExp + "[\\s]*\\)";
	
	public JSFileScanner() {
		keyPattern = Pattern.compile(keyRegExp);
		lnfnPattern = Pattern.compile(lnfnRegExp);
	}
	
	private String extractKey(String lnfnMatch) {
		String key =  null;
		
		Matcher m = keyPattern.matcher(lnfnMatch);
		if(m.find()) {
			key = m.group();
			key = key.substring(1, key.length()-1);
		}
		
		return key;
	}
	
	
	public Set scan(String s) {
		Set results;
		
		results = new HashSet();
		
		// LN('sbi.qbe.filtergridpanel.boperators.name.and')
		
		
		Matcher m = lnfnPattern.matcher(s);
		while(m.find()) {
			String lnfnMatch = m.group();
			results.add( extractKey(lnfnMatch) );
		}
		 
		return results;
	}
	
}
