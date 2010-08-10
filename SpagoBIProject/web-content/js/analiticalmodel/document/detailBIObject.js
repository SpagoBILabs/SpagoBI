<script>

var engineComboBoxId = 'doc_engine';

var Engine = function(id, name, datasource, dataset) {
	this.id = id;
	this.name = name;
	this.useDatasource = datasource;
	this.useDataset = dataset;
};

var DocumentDetails = function() {
	this.engines = [];
	// the actual engine associated to this object on the client side
	this.selectedEngineId;
	// the engine associated to this object on the server side
	this.selectedEngineIdOnServer;
};

var DocumentDetails.prototype.addEngine = function(engine) {
	this.engines[engine.id] = engine;
};

var DocumentDetails.prototype.selectEngine = function(engineId) {
	this.selectedEngineId = engineId;
};

var DocumentDetails.prototype.getSelectedEngine = function() {
	return this.engines[this.selectedEngineId];
};

//showEngField
var refreshEngineComboBox = function(rowDocType) {
	var comboBox = document.getElementById( engineComboBoxId );
	if(!comboBox) return;
	
	var index = docType.indexOf(",");
	var docType = docType.substring(index+1);
	
	comboBox.options = [];
	for(engineId in DocumentDetails.engines) {
		var engine = DocumentDetails.engines[engineId];
		if(engine.type == docType) {			
			comboBox.options[ comboBox.options.length ] = new Option(engine.name, engine.id);
			if( DocumentDetails.selectedEngineIdOnServer && DocumentDetails.selectedEngineIdOnServer == engine.id) {
				DocumentDetails.selectEngine( engine.id );
				comboBox.selectedIndex = comboBox.options.length - 1;
			}
		}
	}
};

function checkSourceVisibility(engineId) {
	var datasource = engineSource[engineId];
	var dataset = engineSet[engineId];;
	
	var engine = DocumentDetails.getEngine( engineId );
	
	
	// hide template dynamic creation button for dossier and olap document 
	var datasourcecontainer = document.getElementById("datasourcecontainer");

	var datasetcontainer = document.getElementById("datasetcontainer");


	if(engine.useDatasource == '1') {
		datasourcecontainer.style.display="inline";
	document.getElementById("doc_datasource").disabled=false;
	} else {
		datasourcecontainer.style.display="none";
	document.getElementById("doc_datasource").disabled=true;
	}

	if(engine.useDataset == '1') {
		datasetcontainer.style.display="inline";
		document.getElementById("dataset").disabled=false;

	} else {
		datasetcontainer.style.display="none";
		document.getElementById("dataset").disabled=true;
		
	}	
};

function checkFormVisibility( docType ) {
	var ind = docType.indexOf(",");
	var type = docType.substring(ind+1);
	
	// hide template dynamic creation button for dossier and olap document 
	var divLinkConf = document.getElementById("link_obj_conf");
	if(type=="OLAP" || type=="DOSSIER") {
		divLinkConf.style.display="inline";
	} else {
		divLinkConf.style.display="none";
	}
};

</script>