/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
 
/**
  * Object name 
  * 
  * Singleton object that handle all errors generated on the client side
  * 
  * 
  * Public Properties
  * 
  * [list]
  * 
  * 
  * Public Methods
  * 
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Davide Zerbetto (davide.zerbetto@eng.it)
  */


Ext.ns("Sbi.execution");

Sbi.execution.SessionParametersManager = function() {
	
    // private variables
	var storeName = 'Sbi_execution_SessionParametersManager'; // dots are not allowed in the store name by the Persist library
	
	var key = 'state';
	
	var isEnabled = Sbi.config.sessionParametersManagerEnabled;

	// public space
	return {

		init: function() {
			try {
			if (isEnabled) {
				Sbi.execution.SessionParametersManager.store = new Persist.Store(storeName, {
				      swf_path: Sbi.config.contextName + '/js/lib/persist-0.1.0/persist.swf'
			    });
			}
			} catch (err) {}
		}
		
		/**
		 * restores the state of all parameters used in the input parameters panel
		 * The input parametersPanel is an instance of class Sbi.execution.ParametersPanel
		 */
		, restoreState: function(parametersPanel) {
			try {
			if (isEnabled) {
				Sbi.execution.SessionParametersManager.store.get(key, function(ok, value) {
					if (ok && value !== undefined && value !== null) {
						var storedParameters = Sbi.commons.JSON.decode(value);
						var state = {};
						for(var p in parametersPanel.fields) {
							var field = parametersPanel.fields[p];
							if (!field.isTransient) {
								var parameterStateObject = storedParameters[Sbi.execution.SessionParametersManager.getParameterStorageKey(field)];
								if (parameterStateObject && parameterStateObject.value) {
									state[field.name] = parameterStateObject.value;
									if (parameterStateObject.description) {
										state[field.name + '_field_visible_description'] = parameterStateObject.description;
									}
								}
							}
						}
						//alert('restoring ' + state.toSource());
						parametersPanel.setFormState(state);
					}
				});
			}
			} catch (err) {}
		}
	
		/**
		 * saves the state of all parameters used in the input parameters panel
		 * The input parametersPanel is an instance of class Sbi.execution.ParametersPanel
		 */
		, saveState: function(parametersPanel) {
			try {
			if (isEnabled) {
				for (var p in parametersPanel.fields) {
					var field = parametersPanel.fields[p];
					if (!field.isTransient) {
						Sbi.execution.SessionParametersManager.save(field);
					}
				}
			}
			} catch (err) {}
		}
		
		/**
		 * saves a parameter state
		 * The input field is a field belonging to class Sbi.execution.ParametersPanel
		 */
		, save: function(field) {
			try {
			if (isEnabled) {
				Sbi.execution.SessionParametersManager.store.get(key, function(ok, value) {
					if (ok) {
						var storedParameters = null;
						if (value === undefined || value === null) {
							storedParameters = {};
						} else {
							storedParameters = Sbi.commons.JSON.decode(value);
						}
						var fieldValue = field.getValue();
						if (fieldValue === undefined || fieldValue === null || fieldValue === '' || fieldValue.length === 0) {
							Sbi.execution.SessionParametersManager.clear(field);
						} else {
							var parameterStateObject = {};
							parameterStateObject.value = fieldValue;
							var rawValue = field.getRawValue();
							if (rawValue !== undefined) {
								parameterStateObject.description = rawValue;
							}
							//alert('saving ' + parameterStateObject.toSource());
							storedParameters[Sbi.execution.SessionParametersManager.getParameterStorageKey(field)] = parameterStateObject;
							Sbi.execution.SessionParametersManager.store.set(key, Sbi.commons.JSON.encode(storedParameters));
						}
					}
				});
			}
			} catch (err) {}
		}
		
		/**
		 * clears a stored parameter
		 * The input field is a field belonging to class Sbi.execution.ParametersPanel
		 */
		, clear: function(field) {
			try {
			if (isEnabled) {
				Sbi.execution.SessionParametersManager.store.get(key, function(ok, value) {
					if (ok) {
						var storedParameters = Sbi.commons.JSON.decode(value);
						if (storedParameters !== undefined && storedParameters !== null) {
							delete storedParameters[Sbi.execution.SessionParametersManager.getParameterStorageKey(field)];
						}
						Sbi.execution.SessionParametersManager.store.set(key, Sbi.commons.JSON.encode(storedParameters));
					}
				});
			}
			} catch (err) {}
		}
		
		/**
		 * resets all stored parameters
		 */
		, reset: function() {
			try {
			if (isEnabled) {
				//Sbi.execution.SessionParametersManager.store.remove(key);
				Sbi.execution.SessionParametersManager.store.set(key, Sbi.commons.JSON.encode({}));
			}
			} catch (err) {}
		}
		
		/**
		 * internal utility method that returns the key that will be used in order to store the parameter state.
		 * The key is composed by the following information retrieved by the parameter that stands behind the input field:
		 * - label of the parameter
		 * - id of the parameter use mode (in order to avoid that parameters with the same labels but different modalities conflict)
		 */
		, getParameterStorageKey: function(field) {
			try {
			var parameterStorageKey = field.behindParameter.label + '_' + field.behindParameter.parameterUseId;
			return parameterStorageKey;
			} catch (err) {}
		}
		
	};
	
}();