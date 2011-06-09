package it.eng.spagobi.commons.utilities.messages;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.RequestContainerAccess;
import it.eng.spago.base.RequestContainerPortletAccess;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.message.MessageBundle;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.PortletUtilities;
import it.eng.spagobi.commons.utilities.StringUtilities;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

// Referenced classes of package it.eng.spagobi.commons.utilities.messages:
//            IMessageBuilder

public class MessageBuilder
    implements IMessageBuilder
{

    private static Logger logger = Logger.getLogger(MessageBuilder.class);
    private static final String MESSAGES_FOLDER = "MessageFiles.";

    public MessageBuilder()
    {
    }

    public String getMessageTextFromResource(String resourceName, Locale locale)
    {
        logger.debug((new StringBuilder("IN-resourceName:")).append(resourceName).toString());
        logger.debug((new StringBuilder("IN-locale:")).append(locale == null ? "null" : locale.toString()).toString());
        if(!isValidLocale(locale))
        {
            logger.warn((new StringBuilder("Request locale ")).append(locale).append(" in input is not valid since it is null or not configured.").toString());
            locale = GeneralUtilities.getDefaultLocale();
        }
        String message = "";
        try
        {
            String resourceNameLoc = (new StringBuilder(String.valueOf(resourceName))).append("_").append(locale.getLanguage()).append("_").append(locale.getCountry()).toString();
            ClassLoader classLoad = getClass().getClassLoader();
            java.io.InputStream resIs = classLoad.getResourceAsStream(resourceNameLoc);
            if(resIs == null)
            {
                logger.warn((new StringBuilder("Cannot find resource ")).append(resourceName).toString());
                resIs = classLoad.getResourceAsStream(resourceName);
            }
            byte resBytes[] = GeneralUtilities.getByteArrayFromInputStream(resIs);
            message = new String(resBytes);
        }
        catch(Exception e)
        {
            message = "";
            logger.warn((new StringBuilder("Error while recovering text of the resource name ")).append(resourceName).toString(), e);
        }
        logger.debug((new StringBuilder("OUT-message:")).append(message).toString());
        return message;
    }

    public String getMessage(String code)
    {
        Locale locale = getLocale(null);
        return getMessageInternal(code, null, locale);
    }

    public String getMessage(String code, Locale locale)
    {
        if(!isValidLocale(locale))
        {
            logger.warn((new StringBuilder("Request locale ")).append(locale).append(" in input is not valid since it is null or not configured.").toString());
            locale = GeneralUtilities.getDefaultLocale();
        }
        return getMessageInternal(code, null, locale);
    }

    public String getMessage(String code, String bundle)
    {
        Locale locale = getLocale(null);
        return getMessageInternal(code, bundle, locale);
    }

    public String getMessage(String code, String bundle, Locale locale)
    {
        if(!isValidLocale(locale))
        {
            logger.warn((new StringBuilder("Request locale ")).append(locale).append(" in input is not valid since it is null or not configured.").toString());
            locale = GeneralUtilities.getDefaultLocale();
        }
        return getMessageInternal(code, bundle, locale);
    }

    public String getMessage(String code, HttpServletRequest request)
    {
        Locale locale = getLocale(request);
        return getMessageInternal(code, null, locale);
    }

    public String getMessage(String code, HttpServletRequest request, Locale locale)
    {
        if(!isValidLocale(locale))
        {
            logger.warn((new StringBuilder("Request locale ")).append(locale).append(" in input is not valid since it is null or not configured.").toString());
            locale = GeneralUtilities.getDefaultLocale();
        }
        return getMessageInternal(code, null, locale);
    }

    public String getMessage(String code, String bundle, HttpServletRequest request)
    {
        Locale locale = getLocale(request);
        return getMessageInternal(code, bundle, locale);
    }

    public String getUserMessage(String code, String bundle, HttpServletRequest request)
    {
        Locale locale = getLocale(request);
        String toReturn = code;
        if(code.length() > 4)
        {
            String prefix = code.substring(0, 4);
            if(prefix.equalsIgnoreCase("cod_"))
            {
                String newCode = code.substring(4);
                toReturn = getMessageInternal(newCode, bundle, locale);
            }
        }
        return toReturn;
    }

    public String getUserMessage(String code, String bundle, Locale locale)
    {
        String toReturn = code;
        if(code.length() > 4)
        {
            String prefix = code.substring(0, 4);
            if(prefix.equalsIgnoreCase("cod_"))
            {
                String newCode = code.substring(4);
                toReturn = getMessageInternal(newCode, bundle, locale);
            }
        }
        return toReturn;
    }

    public String getMessage(String code, String bundle, HttpServletRequest request, Locale locale)
    {
        if(!isValidLocale(locale))
        {
            logger.warn((new StringBuilder("Request locale ")).append(locale).append(" in input is not valid since it is null or not configured.").toString());
            locale = GeneralUtilities.getDefaultLocale();
        }
        return getMessageInternal(code, bundle, locale);
    }

    private String getMessageInternal(String code, String bundle, Locale locale)
    {
        logger.debug((new StringBuilder("IN-code:")).append(code).toString());
        logger.debug((new StringBuilder("bundle:")).append(bundle).toString());
        logger.debug((new StringBuilder("locale:")).append(locale).toString());
        String message = null;
        if(bundle == null)
        {
            message = MessageBundle.getMessage(code, locale);
        } else
        {
            message = MessageBundle.getMessage(code, MESSAGES_FOLDER + bundle, locale);
        }
        if(message == null || message.trim().equals(""))
        {
            message = code;
        }
        logger.debug((new StringBuilder("OUT-message:")).append(message).toString());
        return message;
    }

    public static Locale getBrowserLocaleFromSpago()
    {
        logger.debug("IN");
        Locale browserLocale = null;
        RequestContainer reqCont = RequestContainer.getRequestContainer();
        if(reqCont != null)
        {
            Object obj = reqCont.getInternalRequest();
            if(obj != null && (obj instanceof HttpServletRequest))
            {
                HttpServletRequest request = (HttpServletRequest)obj;
                Locale reqLocale = request.getLocale();
                String language = reqLocale.getLanguage();
                String country=GeneralUtilities.getCountry(language);
                browserLocale = new Locale(language, country);

            }
        }
        if(browserLocale == null)
        {
            browserLocale = GeneralUtilities.getDefaultLocale();
        }
        logger.debug("OUT");
        return browserLocale;
    }

    private Locale getBrowserLocale(HttpServletRequest request)
    {
        logger.debug("IN");
        Locale browserLocale = null;
        Locale reqLocale = request.getLocale();
        String language = reqLocale.getLanguage();
        String country=GeneralUtilities.getCountry(language);
        browserLocale = new Locale(language, country);

        if(browserLocale == null)
        {
            browserLocale = GeneralUtilities.getDefaultLocale();
        }
        logger.debug("OUT");
        return browserLocale;
    }

    public Locale getLocale(HttpServletRequest request)
    {
        logger.debug("IN");
        String sbiMode = getSpagoBIMode(request);
        Locale locale = null;
        if(sbiMode.equalsIgnoreCase("WEB"))
        {
            RequestContainer reqCont = RequestContainer.getRequestContainer();
            SessionContainer sessCont = reqCont.getSessionContainer();
            SessionContainer permSess = sessCont.getPermanentContainer();
            String language = (String)permSess.getAttribute("AF_LANGUAGE");
            String country = (String)permSess.getAttribute("AF_COUNTRY");
            if(country == null)
            {
                country = "";
            }
            if(language != null)
            {
                locale = new Locale(language, country, "");
            } else
            if(request == null)
            {
                locale = getBrowserLocaleFromSpago();
            } else
            {
                locale = getBrowserLocale(request);
            }
        } else
        if(sbiMode.equalsIgnoreCase("PORTLET"))
        {
            locale = PortletUtilities.getPortalLocale();
        }
        if(!isValidLocale(locale))
        {
            logger.warn((new StringBuilder("Request locale ")).append(locale).append(" not valid since it is not configured.").toString());
            locale = GeneralUtilities.getDefaultLocale();
            logger.debug((new StringBuilder("Using default locale ")).append(locale).append(".").toString());
        } else
        if(StringUtilities.isEmpty(locale.getCountry()))
        {
            logger.warn((new StringBuilder("Request locale ")).append(locale).append(" not contain the country value. The one specified in configuration will be used").toString());
            SingletonConfig spagobiConfig = SingletonConfig.getInstance();

            String country = GeneralUtilities.getCountry(locale.getLanguage());
            locale = new Locale(locale.getLanguage(), country);
        }
        logger.debug((new StringBuilder("OUT-locale:")).append(locale == null ? "null" : locale.toString()).toString());
        return locale;
    }

    
    private boolean isValidLocale(Locale locale) {
		logger.info("IN");

		String language;
		String country;
		
				
		if (locale == null) return false;
		
		try {
			language = locale.getLanguage();
			country =GeneralUtilities.getCountry(language);
			
			
			if(StringUtilities.isEmpty( locale.getCountry() )) {
				return true;
			} else {
					return locale.getCountry().equalsIgnoreCase(country);
			}
		} finally {
			logger.info("OUT");
		}
	}

   

    public String getSpagoBIMode(HttpServletRequest request)
    {
        logger.debug("IN");
        String sbiMode = null;
        if(request != null)
        {
            RequestContainer aRequestContainer = null;
            aRequestContainer = RequestContainerPortletAccess.getRequestContainer(request);
            if(aRequestContainer == null)
            {
                aRequestContainer = RequestContainerAccess.getRequestContainer(request);
            }
            String channelType = aRequestContainer.getChannelType();
            if("PORTLET".equalsIgnoreCase(channelType))
            {
                sbiMode = "PORTLET";
            } else
            {
                sbiMode = "WEB";
            }
        } else
        {
            sbiMode = (String)SingletonConfig.getInstance().getConfigValue("SPAGOBI.SPAGOBI-MODE.mode");
    		if (sbiMode==null) {
    			logger.error("SPAGOBI.SPAGOBI-MODE.mode IS NULL");
    			sbiMode="WEB";
    		}
        }
        logger.debug((new StringBuilder("OUT: sbiMode = ")).append(sbiMode).toString());
        return sbiMode;
    }

    public String getMessageTextFromResource(String resourceName, HttpServletRequest request)
    {
        logger.debug("IN");
        Locale locale = getLocale(request);
        String message = getMessageTextFromResource(resourceName, locale);
        logger.debug("OUT");
        return message;
    }
    
}
