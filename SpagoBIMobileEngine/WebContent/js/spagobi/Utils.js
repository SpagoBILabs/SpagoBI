var executeDocument = function(id, label, engine, typeCode){

	  Ext.dispatch({
		  controller: app.controllers.mobileController,
		  action: 'executeDocument',
		  id: id,
		  label: label, 
		  engine: engine, 
		  typeCode: typeCode
	  });
};