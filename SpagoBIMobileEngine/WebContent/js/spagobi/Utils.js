var executeDocument = function(id, label){

	  Ext.dispatch({
		  controller: app.controllers.mobileController,
		  action: 'executeDocument',
		  id: id,
		  label: label
	  });
};