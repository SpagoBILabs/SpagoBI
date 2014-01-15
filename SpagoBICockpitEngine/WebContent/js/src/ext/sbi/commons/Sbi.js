Ext.ns("Sbi");

Sbi.isValorized = function(o, subProperties) {
	var isValorized = Ext.isDefined(o) && Sbi.isNotNull(o);
	if(isValorized && subProperties) {
		if(Ext.isString( subProperties ) ) {
			subProperties = subProperty.split(".");
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

// TODO ...
//Sbi.getObjectByName  = function(objectName) {
//	
//};

//TODO ...
//Sbi.execFunctionByName = function(fnName, fnArgs) {
//	
//};

Sbi.createObjectByClassName = function(fnName, fnArgs) {
	
	var output;
	
	Sbi.trace("[Sbi.execFunctionByName]: IN");
	
	Sbi.trace("[Sbi.execFunctionByName]: function name is equal to [" + fnName + "]");
	if( Sbi.isNotValorized(fnName)) {
		Sbi.showErrorMessage("Input parameter [fnName] must be valorized");
		return undefined;
	}
	
	var namespaceStr = 'window';
	var namespace = fnName.split('.');
	if(namespace.length > 1) {
		fnName = namespace.pop();
		
		var scope = window; 
		for(var i = 0; i< namespace.length; i++) {
			
			var o = scope[namespace[i]];
			Sbi.trace(typeof o);
			if (typeof o === "object") {
				Sbi.trace("Object [" + namespace[i] + "] found in scope [" + namespaceStr + "]");
				scope = o;
				if(namespaceStr === 'window') {
					namespaceStr = namespace[i];
				} else {
					namespaceStr += '.' + namespace[i];
				}
			} else {
				Sbi.showErrorMessage("Impossible to find an object named [" + namespace[i] + "] in scope [" + namespaceStr + "]");
			}
		}
	} 
	
	// find object
	var fn = scope[fnName];
	 
	// is object a function?
	if (typeof fn === "function") {
		Sbi.trace("Function [" + fnName + "] found in scope [" + namespaceStr + "]");
		output =  new fn(fnArgs);
		Sbi.trace("Function [" + fnName + "] sucesfully called in scope [" + namespaceStr + "]");
	} else {
		Sbi.showErrorMessage("Impossible to find a function named [" + fnName + "] in scope [" + namespaceStr + "]");
	}
	
	Sbi.trace("[Sbi.execFunctionByName]: OUT");
	
	return output;
};