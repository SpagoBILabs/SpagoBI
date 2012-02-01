var browserTreeCreation = function(jsonData){
	//alert(jsonData);
	var treeModel ={};
	var folders = jsonData.folderContent[0];
	if(folders !== undefined && folders.title == 'Folders'){
		var children = folders.samples;
		treeModel = folders;
	}
	
	return treeModel;
	
};