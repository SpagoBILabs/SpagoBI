Ext.setup({
	tabletStartupScreen : "../img/tablet_startup.png",
	phoneStartupScreen  : "phone_startup.png",
	icon                : "icon.png",
	glossOnIcon         : true,
	onReady             : function() {
		var deleteConfirm;

		
		var paramsList = {MESSAGE_DET: "GET_DATA"};
		this.configurationObject = {};
		var serviceReg = new Sbi.service.ServiceRegistry();
		
		var getData = serviceReg.getServiceUrl({
			serviceName: 'TABLE_ACTION'
			, baseParams: paramsList
		});
		
      var store = new Ext.data.Store({
   		root: 'columns'
   		, fields: ['label','header']
   		, proxy: {
            type: 'ajax',
            url: getData,
            reader: {
                type: 'json',
                root: 'columns',
                totalCount: 'total'
            }
        }
   		, url: getData 
   	  });
      store.load();
      store.on('load', function(){
    	  alert('loaded!!!');
      });
		var grid = new Ext.ux.TouchGridPanel({
			fullscreen  : true,
			store       : store,
            plugins    : new Ext.ux.touch.PagingToolbar({
                store : store
            }),
			multiSelect : false,
			dockedItems : [{
				xtype : "toolbar",
				dock  : "top",
				title : "Widget Tabella",
				style:  "color:red; font-family:Arial; font-size:16px; font-weight: bold;"
			}],
			colModel    : [{
				header   : "Company",
				mapping  : "company",
				style:     "color:green; font-family:Arial; font-size:12px; ",
				flex     : 2
			},{
				header   : "Price",
				mapping  : "price",
				style    : "text-align: center; background-color: silver;"
			},{
				header   : "Change",
				mapping  : "change",
				cls      : "centered-cell",
				renderer : function(val) {
					var color = (val > 0) ? "00FF00" : "FF0000";
					return "<span style='color: #" + color + ";'>" + val + "</span>";
				}

			},{
				header   : "Last Updated",
				mapping  : "updated",
				hidden   : true,
				style    : "text-align: right;"
			}],
			listeners: {
				beforeselect: function(dataview, nodes, selections) {
					console.log(selections);
				},
				containertap: function(dataview, e) {
					console.log(dataview);
				},
				itemdoubletap: function(dataview, index, el, e) {
					console.log(index);
				},
				itemswipe: function(dataview, index, el, e) {
					console.log(index);
				},
				itemtap: function(dataview, index, el, e) {
					console.log(index);
				},
				selectionchange: function(selectionModel, selections) {
					console.log(selections);
				}
			}
		});
	}
});