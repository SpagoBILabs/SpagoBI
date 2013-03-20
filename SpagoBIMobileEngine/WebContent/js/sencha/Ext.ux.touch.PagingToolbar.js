/*
    Author       : Mitchell Simoens
    Site         : http://simoens.org/Sencha-Projects/demos/
    Contact Info : mitchellsimoens@gmail.com
    Purpose      : Needed to have a paging toolbar for Ext.DataView, Ext.List, and Ext.ux.TouchGridPanel

	License      : GPL v3 (http://www.gnu.org/licenses/gpl.html)
    Warranty     : none
    Price        : free
    Version      : 1.0
    Date         : 2/5/2011
*/

Ext.ns('Ext.ux.touch');

Ext.define("Ext.ux.touch.PagingToolbar",{
	extend:"Ext.Toolbar",
	xtype: "pagingtoolbar",
	
	/**
	  * Dock Location. Default - 'bottom' Options: 'bottom', 'top', 'left', 'right'
	  * @type String
	  */
	dock: 'bottom',
	/**
	  * True to hide/show buttons when option not available. False to enable/disable
	  * @type Boolean
	  */
	hideBtn: true,
	/**
	  * {@link Ext.Button} config object for the Previous button
	  * @type Object
	  */
	prevBtnCfg: {},
	/**
	  * {@link Ext.Button} config object for the Next button
	  * @type Object
	  */
	nextBtnCfg: {},

	//private
	constructor: function(config) {
		config = config || {};
		Ext.apply(config, this.setupToolbar() || {});

		Ext.apply(this, config);
		Ext.ux.touch.PagingToolbar.superclass.constructor.call(this, config);
	},

	//private
	init: function(cmp) {
        var me = this,
            store;

        me.cmp = cmp;

		if (!me.store && typeof cmp.store === 'object') {
			me.store = cmp.store;
		}

        store = me.store;

		store.on('load', me.fillSelectField, me, { single: true });
		store.on('load', me.handleStoreLoad, me);

		cmp.on('afterrender', me.initToolbar, me, { single : true });
	},

	//private
	initToolbar: function() {
		this.cmp.addDocked(this);
		this.cmp.doComponentLayout();
	},

	//private
	setupToolbar: function() {
        var me = this;

		Ext.applyIf(this.prevBtnCfg, {
			text : 'Previous',
			ui   : 'back'
		});
		Ext.applyIf(this.nextBtnCfg, {
			text : 'Next',
			ui   : 'forward'
		});

		me.prevBtn = new Ext.Button(me.prevBtnCfg);
		me.nextBtn = new Ext.Button(me.nextBtnCfg);

	    me.prevBtn.on('tap', me.handlePrevPage, me);
		me.nextBtn.on('tap', me.handleNextPage, me);

		me.selectField = new Ext.form.Select(me.createSelectField());

		return {
			items: [
				me.prevBtn,
				{ xtype: 'spacer' },
				me.selectField,
				me.nextBtn
			]
		};
	},

	//private
	handlePrevPage: function() {
		this.store.previousPage();
	},

	//private
	handleNextPage: function() {
		this.store.nextPage();
	},

	//private
	onPageChoose: function(select, value) {
		this.store.loadPage(value);
	},

	/**
     * Returns the number of records possible in Store
     * @return {Number} total The total number of records
     */
	getTotalRecs: function() {
		var store  = this.store,
		    proxy  = store.getProxy(),
		    reader = proxy.getReader();

        if (reader.type === 'json') {
            return reader.jsonData.total;
        } else if (reader.type === 'xml') {
            return Number (Ext.DomQuery.selectNode(reader.totalProperty, proxy.reader.rawData).textContent);
        }
	},

	//private
	handleStoreLoadBtn: function(store, btn, pageMatch) {
		var doThis = 'hide';
		if (store.currentPage === pageMatch) {
			if (!this.hideBtn) {
				doThis = 'disable';
			}
		} else {
			if (this.hideBtn) {
				doThis = 'show';
			} else {
				doThis = 'enable'
			}
		}
		btn[doThis]();
	},

	//private
	handleStoreLoad: function(store, recs, success) {
        var me = this;

		if (success) {
			me.handleStoreLoadBtn(store, me.prevBtn, 1);

			var totalNum = me.getTotalRecs(),
			    numPages = Math.ceil(totalNum / store.pageSize);

			me.handleStoreLoadBtn(store, me.nextBtn, numPages);

			me.selectField.setValue(store.currentPage);
		}
	},

	//private
	createSelectField: function() {
		return {
			name      : 'pt-options',
			options   : [
				{ text: 'Go to', value: null }
			],
			listeners : {
				scope  : this,
				change : this.onPageChoose
			}
		};
	},

	//private
	fillSelectField: function(store, recs, success) {
		if (success) {
			var totalNum = this.getTotalRecs(),
			    numPages = Math.ceil(totalNum / store.pageSize),
			    options  = [];

			for (var i = 1; i <= numPages; i++) {
				options.push({ text: 'Page '+i, value: i });
			}

			this.selectField.setOptions(options);
		}
	}
});

