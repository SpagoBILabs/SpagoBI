app.controllers.MobileController = Ext.extend(Ext.Controller,
	{

	login: function(options) 
	  {
		console.log('MobileController: Received event of login successfull');
		if(options.record !== undefined){
			alert('login successfull');
		}
		else	{
			
		}  

	  }
});
