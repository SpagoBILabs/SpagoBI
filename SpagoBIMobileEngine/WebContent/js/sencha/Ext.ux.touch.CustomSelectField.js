Ext.ns("Ext.ux.touch");

Ext.ux.touch.CustomSelectField = Ext.extend(Ext.form.Select, {
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
     },
     
     /**
      * Overrides {@link #Ext.form.Select}' setValue method.
      */
     /*
     setValue: function(value) {
          var idx = this.autoSelectDefault? 0 : -1,  //Check if we need to auto select the first row
              hiddenField = this.hiddenField,
              record;
  
          if (value) {
              idx = this.store.findExact(this.valueField, value)
          }else{
        	  idx =-1;
          }
          record = this.store.getAt(idx);
  
          if (record && this.rendered) {
              this.fieldEl.dom.value = record.get(this.displayField);
              this.value = record.get(this.valueField);
              if (hiddenField) {
                  hiddenField.dom.value = this.value;
              }
          } else {
              if (this.rendered) {
                  this.fieldEl.dom.value = value;
              }
              this.value = value;
          }
  
          
          if (this.picker) {
              var pickerValue = {};
              pickerValue[this.name] = this.value;
              this.picker.setValue(pickerValue);
          }
          
          return this;
     }
     */
});

Ext.reg('comboboxfield', Ext.ux.touch.CustomSelectField); 



// Render Form Panel
/*var form = new Ext.form.FormPanel({
        scroll: 'vertical',
        standardSubmit : false,
        fullscreen: true,
        modal: true,
        autoShow: true,
        items: [{
            xtype: 'comboboxfield',
            name: 'someFieldName',
            label: 'Some Field Label',
            displayField: 'some_display_field',
            valueField: 'some_value_field',
            store: someStore
	}]
});*/