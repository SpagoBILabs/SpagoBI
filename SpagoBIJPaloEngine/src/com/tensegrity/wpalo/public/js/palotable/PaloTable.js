//IMPORT ALL REQUIRED JS FILES
var _import = function(file) {
	var imp = document.createElement("script");
	imp.src = file;
	imp.type = "text/javascript";
	document.getElementsByTagName("head")[0].appendChild(imp);
};
(function() {
	_import("js/palotable/TableHeaderItem.js");
	_import("js/palotable/TableCell.js");
	_import("js/palotable/TableContent.js");
	_import("js/palotable/Ruler.js");
	_import("js/palotable/TableHeader.js");
	_import("js/palotable/VerticalLayouter.js");
	_import("js/palotable/HorizontalLayouter.js");
})();

//used for browser specific stuff...
var isIE = (navigator.appName.indexOf("Microsoft") != -1);

//TODO how to determine the scrollbar height?
var SCROLLBAR_HEIGHT = 17; 

/** 
 * The main palo table widget.
 * Layouts the table headers and content and defines the bridge to GWT.
 */
var PaloTable = function(container) {
	//the view:
	var viewId;
	//header layouter:
	var hLayouter = new HorizontalLayouter();
	var vLayouter = new VerticalLayouter();
	//a palo table consists of: 
	var content = new TableContent();
	var hHeader = new TableHeader(hLayouter);
	var vHeader = new TableHeader(vLayouter);
	//each part is contained within a viewport for scrolling
	var contentVP = document.createElement('div');
	var hHeaderVP = document.createElement('div');
	var vHeaderVP = document.createElement('div');
	
	//PUBLIC METHODs:
	
	/** 
	 * Resets this table completely, i.e. removes all header items and cells 
	 */
	this.reset = function() {
		hHeader.clear();
		alert("clear hHeader");
		vHeader.clear();
		alert("clear vHeader");
		content.clear();
		alert("clear content");
	}
	/**
	 * Draws each header and the content.
	 */
	this.draw = function() {
		hHeader.draw();
		vHeader.draw();
		content.draw();
	}
	/**
	 * Layouts the headers and content.
	 */
	this.layout = function() {
		hHeader.layout();
		vHeader.layout();
		
		var hSize = hHeader.computeSize();
		hHeader.div.style.height = hSize[1];
		
		var vSize = vHeader.computeSize();
		vHeader.div.style.width = vSize[0];
		//content.div.style.width = hSize[0];
		//content.div.style.height = vSize[1];
		
		//IE adjustment: it seems that IE/FF are drawing borders differently...	
		var border = isIE ? -1 : +1;
		var contentWidth = this.container.offsetWidth - vSize[0];
		var contentHeight = this.container.offsetHeight - hSize[1];
		//content:		
		arrange(hHeaderVP, vSize[0] + border, 0, contentWidth - SCROLLBAR_HEIGHT, hSize[1]);
		arrange(vHeaderVP, 0, hSize[1] + border, vSize[0], contentHeight - SCROLLBAR_HEIGHT);
		arrange(contentVP, vSize[0]+border, hSize[1] + border, contentWidth, contentHeight);
	}
	/**
	 * Do a complete layout and draw afterwards.
	 */
	this.redraw = function() {
		content.clear();
		this.layout();
		this.draw();
	}
	/**
	 * Adds the given TableHeaderItem to the vertical header.
	 */
	this.addRowItem = function(item) {
		vHeader.addItem(item);
	}
	/**
	 * The maximum depth per level for rows.
	 * Used by VerticalLayouter#computeSize()
	 */
	this.addMaxRowDepth = function(level, depth) {
		vHeader.setDepthPerLevel(depth, level);
	}
	/**
	 * Adds the given TableHeaderItem to the horizontal header.
	 */
	this.addColumnItem = function(item) {
		hHeader.addItem(item);
	}
	/**
	 * The maximum depth per level for columns.
	 * Used by HorizontalLayouter#computeSize()
	 */
	this.addMaxColDepth = function(level, depth) {
		hHeader.setDepthPerLevel(depth, level);
	}
	/**
	 * Adds the given TableCell to the table content.
	 */
	this.addCell = function(cell) {
		content.add(cell);
	}
	/**
	 * Stores and marks the given TableCell as to be inserted.
	 * To insert these cells call doneInsert().
	 */	
	this.insertCell = function(cell) {
		content.insert(cell);
	}
	/**
	 * Inserts all formely inserted cells.
	 */	
	this.doneInsert = function(item, cols) {
		var leafIndex = getLastLeafIndex(item);
		content.doInsert(leafIndex+1, cols, (item.div.parentNode != hHeader.div));
	}
	/**
	 * Adds a new TableHeaderItem which is a direct child of the given father item. 
	 */
	this.addChild = function(child, father) {
		father.isLoaded = true;
		var header = father.div.parentNode == hHeader.div ? hHeader : vHeader;
		header.addChild(child, father); 
	}
	/**
	 * Called when the given TableHeaderItem was expanded or collapsed. 
	 */
	this.expand = function(item, header) {
		var column = header == hHeader;
		//do we have to load it
		if(item.isExpanded() && !item.isLoaded) {
			load(item, column, this);			
			item.isLoaded = true;
		} else {
			//do a normal header and table redraw...
			content.clear();
			header.layout();
			header.draw();			
			content.draw();
			//expanded(item, column, this);
		}
	}
	/** returns the view id to which this table corresponds */ 
	this.getViewId = function() {
		return viewId;
	}
	/** sets the id of the corresponding view */
	this.setViewId = function(id) {
		viewId = id;
	}
	
	/**
	 * INERNAL METHOD which registers ourself as expand listener to each header.
	 */
	this.addExpandListener = function() {
		//we register ourself as an expand listener
		hHeader.expandListener = this;
		vHeader.expandListener = this;
	}
	/**
	 * INERNAL METHOD which registers the table content container as a leaf handler
	 * to each header.
	 */
	this.registerLeafHandler = function() {
		//a leaf handler should implement columnLeaf(item) and rowLeaf(item)
		hLayouter.leafHandler = content;
		vLayouter.leafHandler = content;
	}
	/**
	 * Called when the TableContent was scrolled.
	 */
	this.scrolled = function(offX, offY) {
		hHeader.div.style.left = -offX;
		vHeader.div.style.top = -offY;		
	}
	/**
	 * Utility method which simply arranges the given element.
	 */
	function arrange(vp, x, y, w, h) {
		vp.style.top = y;
		vp.style.left = x;	
		if (w < 0) w = 1;	
		vp.style.width = w;
		if (h < 0) h = 1;	
		vp.style.height = h;
	}
	/**
	 * Utility method which determines the index of the last leaf item of the 
	 * specified TableHeaderItem.
	 */
	function getLastLeafIndex(item) {
		var leaf = 0;
		function traverse(item) {
			if(item.leaf > -1)
				leaf = item.leaf;
			for(var i=0, n=item.rootsNextLevel.length; i < n; ++i) {
				traverse(item.rootsNextLevel[i]);
			} 
			for(var i=0, n=item.children.length; i < n; ++i) {
				traverse(item.children[i]);
			}			
		}
		traverse(item);
		return leaf;
	}
	//constructor:
	function create(table) {
		table.container = container; 
		table.div = document.createElement('div');
		table.div.style.display = "inline";
		table.div.style.width = "100%";
		table.div.style.height = "100%";
		//add table to container:
		if(!container) {
			//TODO throw an error/exception
			alert("parent container undefined! cannot create palo table!!");			
		}
		container.appendChild(table.div);
		
		//viewport styles:
		hHeaderVP.className = "header";
		vHeaderVP.className = "header";
		contentVP.style.overflow = "auto";
		contentVP.style.position = "absolute";
		//connect headers and contents with their viewports:
		hHeaderVP.appendChild(hHeader.div);
		arrange(hHeader.div, 0,0,"100%", 20);	
		vHeaderVP.appendChild(vHeader.div);	
		arrange(vHeader.div, 0,0,"20", "100%");	
		contentVP.appendChild(content.div);		
	
		//and the viewports with table:
		table.div.appendChild(hHeaderVP);
		table.div.appendChild(vHeaderVP);
		table.div.appendChild(contentVP);
		
		//finally we register handlers and listeners:
		table.addExpandListener();
		table.registerLeafHandler();		
		addEvent(contentVP, "scroll", function() {table.scrolled(this.scrollLeft,this.scrollTop);});
	}
	 
	//finally call constructor:
	create(this);	
};

/**
 * Browser independent event attachment.
 * obj - the dom element to add the event to
 * type - the event type, e.g. "click". Note: do not specify the "on" like "onClick"
 * fn - the function to call when the event happens
 */
function addEvent( obj, type, fn ) {
	if (obj.addEventListener) {
		obj.addEventListener( type, fn, false );
	}
	else if (obj.attachEvent) {
		obj["e"+type+fn] = fn;
		obj[type+fn] = function() { obj["e"+type+fn]( window.event ); }
		obj.attachEvent( "on"+type, obj[type+fn] );
	}
	else {
		obj["on"+type] = obj["e"+type+fn];
	}
}