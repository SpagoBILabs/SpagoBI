Ext.namespace('app.models');
Ext.namespace('app.views');
Ext.namespace('app.data');
Ext.namespace('app.controllers');

Ext.regModel('folder',	{

	fields: [{name: 'devRoles',		type:'array'},	         
	       	 {name: 'biObjects',	type:'array'},
	       	 {name: 'code',	type:'string'},
	       	 {name: 'codType',	type:'string'},	       	 
	       	 {name: 'id',		type:'integer'},
	       	 {name: 'testRoles',	type:'array'},
	       	 {name: 'parentId',		type:'integer'},
	       	 {name: 'prog',	type:'integer'},
	       	 {name: 'description',		type:'string'},
	       	 {name: 'name',		type:'string'},
	       	 {name: 'path',		type:'string'},
	       	 {name: 'execRoles',		type:'array'},	         
	       	 {name: 'actions',	type:'array'}
	       	 ],

	       	 
	 belongsTo: 'folderContent' 
 
});

Ext.regModel('folderContent', 	{
	//"icon":"folder.png","title":"Folders","samples
	fields: [{name: 'icon',		type:'string'},	
	      	 {name: 'title',		type:'string'},
	       	 {name: 'samples',		type:'string'}
	       	 ],
	hasMany: [{model: 'folder',	name: 'samples'} ]
});











