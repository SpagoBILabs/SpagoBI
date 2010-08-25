/* =============================================================================
* Added by Chiara Chiarelli (August 2010)
* In Ext 3.1.1 the method getCount didn't control if this.data existed
============================================================================= */
Ext.override(Ext.data.Store, {
	getCount : function(){
		if(this.data!==null && this.data!== undefined){
			return this.data.length || 0;
		}else{
			return 0;
		}
	}
});