/**
SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

/**
  * SpagoBI Core - box component
  * by Davide Zerbetto
  */

function createBox(title, content, renderTo) {
    var p = new Ext.Panel({
    	title: title,
        collapsible:false,
        frame: true,
        renderTo: renderTo,
        contentEl: content
    });
    p.show();
};


function createToggledBox(title, content, renderTo, toggler, toggled) {
	Ext.onReady(function() {
	    var p = new Ext.Panel({
	    	title: title,
	        collapsible:false,
	        frame: true,
	        renderTo: renderTo,
	        contentEl: content
	    });
	    var visibile;
	    if (!toggled) {
	    	p.hide();
	    	visibile = false;
	    } else {
	    	p.show();
	    	visibile = true;
	    }
	    Ext.get(toggler).on('click', function() {
	        if (!visibile) {
	        	p.show();
	        	visibile = true;
	        } else {
	        	p.hide();
	        	visibile = false;
	        }
	    });
	});	
}
