package it.eng.spagobi.signup.validation;

import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.rest.validation.IFieldsValidator;

import java.net.URLDecoder;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignupFieldsValidator implements IFieldsValidator {

	private static transient Logger logger = Logger.getLogger(SignupFieldsValidator.class);
	private static final String regex_password = "[^\\d][a-zA-Z0-9]{7,9}";
	private static final String regex_email = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
	private static final String regex_date = "(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d";
	
	private boolean validatePassword( String password, String username ){
		
	  if( username != null && password.indexOf(username) != -1 ) return false;
	  return password.matches(regex_password);
	}
	private boolean validateEmail( String email ){
		
	  return email.matches(regex_email);
	}
	private boolean validateDate( String date ){
		
      return date.matches(regex_date);
	}
	public JSONArray validateFields(MultivaluedMap<String, String> parameters) {

        JSONArray validationErrors = new JSONArray();
        
        String nome     = GeneralUtilities.trim(parameters.getFirst("nome"));
        String cognome  = GeneralUtilities.trim(parameters.getFirst("cognome"));
        String username = GeneralUtilities.trim(parameters.getFirst("username"));
        String password = GeneralUtilities.trim(parameters.getFirst("password"));
        String confermaPassword 
                        = GeneralUtilities.trim(parameters.getFirst("confermaPassword"));
        String email    = GeneralUtilities.trim(parameters.getFirst("email"));
        String dataNascita  
                        = GeneralUtilities.trim(parameters.getFirst("dataNascita"));
        String captcha  = GeneralUtilities.trim(parameters.getFirst("captcha"));
        String termini  = parameters.getFirst("termini");
        String modify  = GeneralUtilities.trim(parameters.getFirst("modify"));
        
        try{ 
          if( nome != null )             nome = URLDecoder.decode(nome, "ISO-8859-1");
          if( cognome != null )          cognome = URLDecoder.decode(cognome, "ISO-8859-1");
          if( username != null )         username = URLDecoder.decode(username, "ISO-8859-1");
          if( password != null )         password = URLDecoder.decode(password, "ISO-8859-1");
          if( confermaPassword != null ) confermaPassword = URLDecoder.decode(confermaPassword, "ISO-8859-1");
          if( email != null )            email = URLDecoder.decode(email, "ISO-8859-1");
          if( dataNascita != null )      dataNascita = URLDecoder.decode(dataNascita, "ISO-8859-1");
        	
        }
        catch( Exception ex ){ logger.error(ex.getMessage());  throw new RuntimeException( ex ); }
        
        try{
          
          if( email == null ) 
        	  validationErrors.put( new JSONObject("{message: 'Field Email mandatory'}") );
          else{
        	  if( !validateEmail( email )) 
                validationErrors.put( new JSONObject("{message: 'Field Email invalid syntax'}") );
          }
          if( dataNascita != null )
            if( !validateDate(dataNascita) )
        	  validationErrors.put( new JSONObject("{message: 'Field Birthday invalid syntax'}") );
        	  
          if( nome == null) 
        	  validationErrors.put( new JSONObject("{message: 'Field Name mandatory'}") );
          if( cognome == null ) 
        	  validationErrors.put( new JSONObject("{message: 'Field Surname mandatory'}") );
            
          if( modify == null ){	  
            if( password == null ) 
              	  validationErrors.put( new JSONObject("{message: 'Field Password mandatory'}") );
            else{
              	 if( !validatePassword(password, username )) 
                     validationErrors.put( new JSONObject("{message: 'Field Password invalid syntax'}") );
            }	  
            
            if( username == null ) 
        	  validationErrors.put( new JSONObject("{message: 'Field Username mandatory'}") );
          
            if( confermaPassword == null ) 
        	  validationErrors.put( new JSONObject("{message: 'Field Confirm Password mandatory'}") );
          
            if( !Boolean.valueOf(termini) ) 
        	  validationErrors.put( new JSONObject("{message: 'Agree with the terms of service mandatory'}") );
          
        	  
            if( password != null && confermaPassword != null )
        	 if( ! password.equals(confermaPassword)) 
        	   validationErrors.put( new JSONObject("{message: 'Field Password and Confirm Password not equal'}") );
            if( captcha == null ) 
        	  validationErrors.put( new JSONObject("{message: 'Field Captcha mandatory'}") );
          }
		} catch (JSONException e1) {
		  logger.error(e1.getMessage());
		  throw new RuntimeException( e1 );
		}
		
        return validationErrors;
		
	}
}
