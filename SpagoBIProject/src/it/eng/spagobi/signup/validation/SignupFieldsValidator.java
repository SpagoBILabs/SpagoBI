package it.eng.spagobi.signup.validation;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.rest.validation.IFieldsValidator;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignupFieldsValidator implements IFieldsValidator {

	private static transient Logger logger = Logger.getLogger(SignupFieldsValidator.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private static final String regex_password = "[^\\d][a-zA-Z0-9]{7,9}";
	private static final String regex_email = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
	
	private boolean validatePassword( String password, String username ){
		
	  if( username != null && password.indexOf(username) != -1 ) return false;
	  return password.matches(regex_password);
	}
	private boolean validateEmail( String email ){
		
	  return email.matches(regex_email);
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
        
        
        try{ 
          if( nome != null )             nome = URLDecoder.decode(nome, "ISO-8859-1");
          if( cognome != null )          cognome = URLDecoder.decode(cognome, "ISO-8859-1");
          if( username != null )         username = URLDecoder.decode(username, "ISO-8859-1");
          if( password != null )         password = URLDecoder.decode(password, "ISO-8859-1");
          if( confermaPassword != null ) confermaPassword = URLDecoder.decode(confermaPassword, "ISO-8859-1");
          if( email != null )            email = URLDecoder.decode(email, "ISO-8859-1");
          if( dataNascita != null )      dataNascita = URLDecoder.decode(dataNascita, "ISO-8859-1");
        	
        }
        catch( Exception ex ){ logger.error(ex.getMessage()); throw new RuntimeException( ex ); }
        
        try{
          if( nome == null) 
        	  validationErrors.put( new JSONObject("{message: 'Campo Nome obbligatorio'}") );
          if( cognome == null ) 
        	  validationErrors.put( new JSONObject("{message: 'Campo Cognome obbligatorio'}") );
          if( username == null ) 
        	  validationErrors.put( new JSONObject("{message: 'Campo Username obbligatorio'}") );
          if( password == null ) 
        	  validationErrors.put( new JSONObject("{message: 'Campo Password obbligatorio'}") );
          else{
        	 if( !validatePassword(password, username )) 
               validationErrors.put( new JSONObject("{message: 'Campo Password non conforme'}") );
          }
          if( confermaPassword == null ) 
        	  validationErrors.put( new JSONObject("{message: 'Campo Conferma password obbligatorio'}") );
          if( email == null ) 
        	  validationErrors.put( new JSONObject("{message: 'Campo Email obbligatorio'}") );
          else{
        	  if( !validateEmail( email )) 
                validationErrors.put( new JSONObject("{message: 'Campo Email non conforme'}") );
          }
          if( !Boolean.valueOf(termini) ) 
        	  validationErrors.put( new JSONObject("{message: 'Accetta termini servizio'}") );
          if( dataNascita != null )
        	  try{sdf.parse(dataNascita);}catch( ParseException pex ){
        	    validationErrors.put( new JSONObject("{message: 'Campo Data Nascita non valido'}") );
        	  }
        	  
          if( password != null && confermaPassword != null )
        	 if( ! password.equals(confermaPassword)) 
        	   validationErrors.put( new JSONObject("{message: 'Campo Password e Conferma password devono assumere lo stesso valore'}") );
          if( captcha == null ) 
        	  validationErrors.put( new JSONObject("{message: 'Campo Captcha obbligatorio'}") );
          
		} catch (JSONException e1) {
		  logger.error(e1.getMessage());
		  throw new RuntimeException( e1 );
		}
		
        return validationErrors;
		
	}
}
