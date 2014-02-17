/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * Container for the pivot table. Contains:
 * <ul>
 * <li>Filters definition</li>
 * <li>Columns definition</li>
 * <li>Rows definition</li>
 * <li>Table</li>
 * </ul>
 * 
 * The tree of the panel layout is:
 * <ul>
 *	<li>this (vbox)
 *		<ul>
 *			<li>filtersPanel</li>
 *			<li>pivotPanel(vbox)
 *				<ul>
 *					<li>columnsPanel (vbox)</li>
 *					<li>tableAndRowsPanel (hbox)
 *						<ul> 
 *							<li>rowsPanel</li>
 *							<li>tablePanel</li>
 *						</ul>
 *					</li>
 *				</ul>
 *			</li>		
 *		</ul>	
 *	</li>		
 *  </ul>
 * 
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */


Ext.define('Sbi.olap.execution.table.OlapExecutionPivot', {
	extend: 'Ext.panel.Panel',
	
	layout: {
	    type: 'vbox',
	    align : 'stretch'
	},
	
	config:{
		border: false,
    	/**
    	 * @cfg {Number} filtersHeight
    	 * Height of the filters definition panel. Default 125 
    	 */
		filtersHeight: 125,
    	/**
    	 * @cfg {Number} columnsHeight
    	 * Height of the columns definition panel. Default 100
    	 */
		filtersHeight: 100,
    	/**
    	 * @cfg {Number} rowsWidth
    	 * Width of the rows panel. Default 75 
    	 */
		filtersHeight: 75
	},

	/**
     * @property {Sbi.olap.execution.table.OlapExecutionFilters} olapExecutionFilters
     *  The table with the data
     */
	olapExecutionFilters: null,
	
	/**
     * @property {Sbi.olap.execution.table.OlapExecutionColumns} olapExecutionColumns
     *  The table with the data
     */
	olapExecutionColumns: null,
	
	/**
     * @property {Sbi.olap.execution.table.OlapExecutionRows} olapExecutionRows
     *  The table with the data
     */
	olapExecutionRows: null,
	
	/**
     * @property {Sbi.olap.execution.table.OlapExecutionTable} olapExecutionTable
     *  The table with the data
     */
	olapExecutionTable: null,

		
	constructor : function(config) {
		this.initConfig(config);
		if(Sbi.settings && Sbi.settings.olap && Sbi.settings.olap.execution && Sbi.settings.olap.execution.table && Sbi.settings.olap.execution.table.OlapExecutionPivot) {
			this.initConfig(Sbi.settings.olap.execution.table.OlapExecutionPivot);
		}
		this.callParent(arguments);
	},
	
	initComponent: function() {
		
		//defining the components
		this.olapExecutionFilters   = Ext.create('Sbi.olap.execution.table.OlapExecutionFilters',  {height: this.filtersHeight}); 
		this.olapExecutionColumns   = Ext.create('Sbi.olap.execution.table.OlapExecutionColumns',  {height: this.olapExecutionColumns});
		this.olapExecutionRows   = Ext.create('Sbi.olap.execution.table.OlapExecutionRows',  {width: this.olapExecutionRows});
		this.olapExecutionTable   = Ext.create('Sbi.olap.execution.table.OlapExecutionTable',  {flex: 1});
		
		
		//defining the structure of the layout
		Ext.apply(this, {
			items: [this.olapExecutionFilters,
			        {
						border: false,
						flex:1,
						layout: {
							type: 'vbox',
							align : 'stretch'
						},
						items:[this.olapExecutionColumns ,
						        {
									border: false,
									flex:1,
									layout: {
										type: 'hbox',
										align : 'stretch'
									},
									items:[this.olapExecutionRows ,this.olapExecutionTable]
						        }
						]
			        }
			]
			
		});
		
		this.callParent();
	}
});





