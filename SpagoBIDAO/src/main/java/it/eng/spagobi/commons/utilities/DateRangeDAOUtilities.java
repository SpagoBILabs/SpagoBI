package it.eng.spagobi.commons.utilities;

import static it.eng.spagobi.commons.constants.SpagoBIConstants.DATE_RANGE_TYPE;

import java.text.SimpleDateFormat;
import java.util.Date;

import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.Parameter;
import it.eng.spagobi.commons.SingletonConfig;

public class DateRangeDAOUtilities {

	public static final String DATE_RANGE_PARAMETER_SUFFIX = "_dateRange";

	public static boolean isDateRange(BIObjectParameter biObjectParameter) {
		return biObjectParameter != null && isDateRange(biObjectParameter.getParameter());
	}

	public static boolean isDateRange(Parameter parameter) {
		return parameter != null && DATE_RANGE_TYPE.equals(parameter.getType());
	}

	public static String toStringForParamUrl(Date d) {
		SingletonConfig config = SingletonConfig.getInstance();
		String formatSB = config.getConfigValue("SPAGOBI.DATE-FORMAT.format");
		formatSB = formatSB == null ? "dd/MM/yyyy" : formatSB;
		return new SimpleDateFormat(formatSB).format(d);
	}

	public static String getDefaultServerPattern() {
		String pattern = SingletonConfig.getInstance().getConfigValue("SPAGOBI.DATE-FORMAT-SERVER.format");
		return pattern;
	}

}
