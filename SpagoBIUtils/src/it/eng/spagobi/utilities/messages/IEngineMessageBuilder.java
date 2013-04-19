package it.eng.spagobi.utilities.messages;

import java.util.Locale;

/**
 * @author Andrea Frantappiè (andrea.frantappiè@eng.it)
 * 
 * DATE            CONTRIBUTOR/DEVELOPER                        NOTE
 * 19-04-2013      Andrea Frantappiè (andrea.frantappiè@eng.it) added internationalization management
 *  															for highchart engine
 *
 */

public interface IEngineMessageBuilder
{
	public String getI18nMessage(Locale locale, String code);
}
