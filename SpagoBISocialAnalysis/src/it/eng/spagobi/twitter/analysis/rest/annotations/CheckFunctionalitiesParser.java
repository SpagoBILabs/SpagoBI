package it.eng.spagobi.twitter.analysis.rest.annotations;

import it.eng.spagobi.commons.bo.UserProfile;

import java.lang.reflect.Method;
import java.util.Collection;

public class CheckFunctionalitiesParser {

	public boolean isPublicService(Method method) throws Exception {

		if (method.isAnnotationPresent(CheckFunctionalities.class)) {
			return false;
		} else {
			return true;
		}
	}

	public boolean checkFunctionalitiesByAnnotation(Method method, UserProfile profile) throws Exception {

		boolean authorized = false;

		if (method.isAnnotationPresent(CheckFunctionalities.class)) {

			Collection functionalities = profile.getFunctionalities();

			CheckFunctionalities checkFuncs = method.getAnnotation(CheckFunctionalities.class);
			String[] funcsAnnotated = checkFuncs.funcs();

			for (int i = 0; i < funcsAnnotated.length; i++) {
				if (functionalities.contains(funcsAnnotated[i])) {
					authorized = true;
					break;
				}
			}
		} else {
			authorized = true;
		}

		return authorized;
	}
}
