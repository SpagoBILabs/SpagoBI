var executeDocument = function(id){
	//alert(id);
	  Ext.dispatch({
		  controller: app.controllers.mobileController,
		  action: 'executeDocument',
		  id: id
	  });
};