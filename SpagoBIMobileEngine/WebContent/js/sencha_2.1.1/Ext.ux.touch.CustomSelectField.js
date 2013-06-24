Ext.define('Ext.ux.touch.CustomSelectFiel',{
	extend: 'Ext.form.Select',
	xtype: "comboboxfield",
     /**
     * @cfg {Boolean} selectFirstRow the flag to initially select the first row of the list.  This flag will be disregarded if you set a valid/ existing {@link #value}
     */
     autoSelectDefault: false,
     
     /**
      * Overrides {@link #Ext.form.Select}' showComponent method.
      */
     showComponent: function() {
          if (Ext.is.Phone) {
              this.getPicker().show();
          }
          else {
              var listPanel = this.getListPanel(),
                  index = this.store.findExact(this.valueField, this.value);
  
              listPanel.showBy(this.el, 'fade', false);
              
              //Check if we need to auto select the first row
              if (index == -1 && !this.autoSelectDefault) {
                  return;
              }
              
              listPanel.down('#list').getSelectionModel().select(index != -1 ? index: 0, false, true);
          }
     }
});
