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

Sbi.isNull = function(o) {
	return o === null;
};

Sbi.isNotNull = function(o) {
	return !Sbi.isNull(o);
};