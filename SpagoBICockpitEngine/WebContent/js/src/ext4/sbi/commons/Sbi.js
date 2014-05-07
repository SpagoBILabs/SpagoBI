Ext.ns("Sbi");

Sbi.isValorized = function(o, subProperties) {
	var isValorized = Ext.isDefined(o) && Sbi.isNotNull(o);
	if(isValorized && subProperties) {
		if(Ext.isString( subProperties ) ) {
			subProperties = subProperties.split(".");
		}
		if(subProperties.length > 0) {
			var property = subProperties.shift();
			isValorized = Sbi.isValorized(o[property], subProperties);
		}	
	}
	return isValorized;
};

Sbi.isNotValorized = function(o, subProperties) {
	return !Sbi.isValorized(o, subProperties);
};

Sbi.isNull = function(o) {
	return o === null;
};

Sbi.isNotNull = function(o) {
	return !Sbi.isNull(o);
};

Sbi.isExtObject = function(o) {
	var objectClass = Ext.ClassManager.getClass(o);
	if(objectClass != null) {
		Sbi.trace("[Sbi.isExtObject]: type of: " + Ext.ClassManager.getName(o));	
	}
	return objectClass != null;
//	Sbi.trace("[Sbi.isExtObject]: Is an insance of [Ext.data.Store]: " + (o instanceof Ext.data.Store));	
//	Sbi.trace("[Sbi.isExtObject]: Is an insance of [Ext.util.Observable]: " + (o instanceof Ext.util.Observable));
//	return typeof o === 'object' && (o instanceof Ext.util.Observable);
};

Sbi.isNotExtObject = function(o) {
	return !Sbi.isExtObject(o);
};


Sbi.getObjectSettings = function(objectQilifiedName, defaultSettings) {
	var settings = null;
	
	if( Sbi.isValorized(objectQilifiedName) && Ext.isString(objectQilifiedName)) {
		var nameParts = objectQilifiedName.split(".");
		if(nameParts.length > 0 && nameParts[0] === "Sbi") {
			nameParts.shift(); // remove the first element
			nameParts.unshift("Sbi", "settings");
		}
		if(nameParts.length > 2) { // it's a real object not just general settings
			var objectName = nameParts.pop();
			objectName = objectName.charAt(0).toLowerCase() + objectName.slice(1);
			nameParts.push(objectName);
		}
		
		settings = Sbi.getObjectByName(nameParts.join("."));
		if(settings) {
			settings = Ext.apply(defaultSettings || {}, settings);	
		}
	} 
	
	if(settings) {
		//Sbi.trace("[Sbi.getObjectSettings]: for object [" + objectQilifiedName + "] the following settings [" + Sbi.toSource(settings) + "] has been found");
	} else {
		//Sbi.trace("[Sbi.getObjectSettings]: No settings has been found for object [" + objectQilifiedName + "]");
	}
	
	if(!settings && defaultSettings) {
		settings = defaultSettings;
	}
	
	
	return settings;
};


Sbi.getObjectByName  = function(objectName) {
	
	//Sbi.trace("[Sbi.getObjectByName]: IN");
	
	if( Sbi.isNotValorized(objectName)) {
		Sbi.showErrorMessage("Input parameter [objectName] must be valorized");
		return null;
	}
	
	if( Ext.isString(objectName) === false) {
		Sbi.showErrorMessage("Input parameter [objectName] must be of type string");
		return null;
	}
	
	//Sbi.trace("[Sbi.getObjectByName]: Input parameter [objectName] is equal to [" + objectName + "]");
	
	var scope = window; 
	var scopeStr = 'window'; // used by debug logs
	
	
	var namespace = objectName.split('.');
	objectName = namespace.pop();
	if(namespace.length > 0) {
		for(var i = 0; i< namespace.length; i++) {
			var o = scope[namespace[i]];
			if (typeof o === "object") {
				//Sbi.trace("[Sbi.getObjectByName]: Object [" + namespace[i] + "] found in scope [" + scopeStr + "]");
				scope = o;
				if(scopeStr === 'window') {
					scopeStr = namespace[i];
				} else {
					scopeStr += '.' + namespace[i];
				}
			} else {
				//Sbi.warn("Impossible to find an object named [" + namespace[i] + "] in scope [" + scopeStr + "]");
				return null;
			}
		}
	} 
	
	//Sbi.trace("[Sbi.getObjectByName]: OUT");
	
	return scope[objectName];
};

Sbi.createObjectByClassName = function(fnName, fnArgs) {
	
	var output;
	
	Sbi.trace("[Sbi.execFunctionByName]: IN");
	
	Sbi.trace("[Sbi.execFunctionByName]: function name is equal to [" + fnName + "]");
	if( Sbi.isNotValorized(fnName)) {
		Sbi.showErrorMessage("Input parameter [fnName] must be valorized");
		return null;
	}
	
	// find object
	var fn = Sbi.getObjectByName(fnName);
	 
	// is object a function?
	if (typeof fn === "function") {
		Sbi.trace("Function [" + fnName + "] found in scope");
		output =  new fn(fnArgs);
		Sbi.trace("Function [" + fnName + "] sucesfully called in scope");
	} else {
		Sbi.showErrorMessage("Impossible to find a function named [" + fnName + "] in scope");
	}
	
	Sbi.trace("[Sbi.execFunctionByName]: OUT");
	
	return output;
};

Sbi.isEmptyObject = function(o) {
	for(var p in o) {
		if(o.hasOwnProperty(p)) {
			return false;
		}       
	}
	return true;
};

Sbi.isNotEmptyObject = function(o) {
	Sbi.isEmptyObject(o) === false;
};
