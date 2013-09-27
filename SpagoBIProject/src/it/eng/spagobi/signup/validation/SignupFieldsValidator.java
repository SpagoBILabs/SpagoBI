package it.eng.spagobi.signup.validation;


import it.eng.spagobi.rest.validation.IFieldsValidator;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignupFieldsValidator implements IFieldsValidator {

	private static transient Logger logger = Logger.getLogger(SignupFieldsValidator.class);
	
	public JSONArray validateFields(MultivaluedMap<String, String> parameters) {

        
        JSONArray validationErrors = new JSONArray();
        String nome     = parameters.getFirst("nome");
        String cognome  = parameters.getFirst("cognome");
        String username = parameters.getFirst("username");
        String password = parameters.getFirst("password");
        String confermaPassword 
                        = parameters.getFirst("confermaPassword");
        String email    = parameters.getFirst("email");
        
        try{
          if( nome == null || nome.trim().length() == 0 ) 
        	  validationErrors.put( new JSONObject("{message: 'Campo Nome obbligatorio'}") );
          if( cognome == null || cognome.trim().length() == 0 ) 
        	  validationErrors.put( new JSONObject("{message: 'Campo Cognome obbligatorio'}") );
          if( username == null || username.trim().length() == 0 ) 
        	  validationErrors.put( new JSONObject("{message: 'Campo Username obbligatorio'}") );
          if( password == null || password.trim().length() == 0 ) 
        	  validationErrors.put( new JSONObject("{message: 'Campo Password obbligatorio'}") );
          if( confermaPassword == null || confermaPassword.trim().length() == 0 ) 
        	  validationErrors.put( new JSONObject("{message: 'Campo Conferma password obbligatorio'}") );
          if( email == null || email.trim().length() == 0 ) 
        	  validationErrors.put( new JSONObject("{message: 'Campo Email obbligatorio'}") );
          
          if( password != null && confermaPassword != null )
        	 if( ! password.equals(confermaPassword)) 
        	   validationErrors.put( new JSONObject("{message: 'Campo Password e Conferma password devono assumere lo stesso valore'}") );
          
		} catch (JSONException e1) {
		  logger.error(e1.getMessage());
		}
		
        return validationErrors;
		
	}
}
