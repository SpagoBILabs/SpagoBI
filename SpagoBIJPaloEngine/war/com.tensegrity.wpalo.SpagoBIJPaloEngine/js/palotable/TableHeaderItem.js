/**
 * An itm within a table header.
 */
var ICON_SIZE = 9;
var MIN_WIDTH = 194;
var MIN_HEIGHT = 19; 

var TableHeaderItem = function(path) {
	
	//an item consists of:
	this.div = document.createElement('div');
	this.icon = document.createElement('img');
	this.caption = document.createElement('span');
	
	
	this.level = 0;	//the hierarchy level to which this item belongs
	this.depth = 0;	//the depth of this item within its hierarchy		
	this.leaf = -1;	//the leaf index or -1 if item is no leaf...	
	this.isLoaded = false; //item already loaded?
	
	//tree hierarchy:
	this.children = [];
	this.rootsNextLevel = [];

	//next fields are private, because setting them trigger additional actions
	var hasKids = false;
	var isExpand = false;
	
	//setters/getters for private fields:
	this.hasChildren = function() {
		return hasKids;
	}
	this.isExpanded = function() {
		return isExpand;
	}
	this.setHasChildren = function(bool) {		
		hasKids = bool;
		if(hasKids) {
			this.icon.className = "plus visible";
		} else {
			this.icon.className = "hidden";
		}
	}
	this.setIsExpanded = function(bool) {
		if(hasKids) {
			isExpand = bool;
			this.icon.className = isExpand ? "minus visible" : "plus visible";
		}
	}
		
	//CONSTRUCTOR:
	function create(item) {
		item.div.id = path;
		item.div.className = "item";
		item.div.style.display = "inline";
		item.div.style.position = "absolute";

		item.icon.className = "hidden";	
		item.icon.style.display = "inline";
		
		item.caption.style.margin = "2px";
		item.div.appendChild(item.icon);
		item.div.appendChild(item.caption);
	}
	 
	//call constructor:
	create(this);
};


TableHeaderItem.prototype = {
	
	/** Returns the item bounds as int array [x, y, w, h] */
	getBounds: function() {
		return [this.div.offsetLeft, this.div.offsetTop, this.div.offsetWidth, this.div.offsetHeight];
	},
	
	/** Returns the item name */
	getName: function() {
		return this.caption.innerHTML;
	},
	
	/** 
	 * Returns the item path within its axis. The path is equal to the path
	 * of the underlying AxisItem model.
	 */
	getPath: function() {
	 	return this.div.id;
	},
	
	/** Returns the item size as an int array [w, h] */
	getSize: function() {
		return [this.div.offsetWidth, this.div.offsetHeight];
	},
	
	/** Sets the name of the item */
	setName: function(str) {
		this.caption.innerHTML = str;
	},
	
	/** Sets the item position (relative to its parent) */
	setPosition: function(x, y) {
		this.div.style.left = x+"px";
		this.div.style.top = y+"px";
	},
	
	/** Sets the item size. Param size must be an int array [w, h] */ 
	setSize: function(size) {
		this.setWidth(size[0]);
		this.setHeight(size[1]);
	},
	
	/** Sets the item width */
	setWidth: function(w) {
		this.div.style.width = w+"px";
	},
	
	/** Sets the item height */
	setHeight: function(h) {
		this.div.style.height = h+"px";
	},
	
	//overwritten
	toString: function() {
		return this.getName()+"[" + this.getPath() +"]";
	}
	
};

//static method:

/** Draws or hides the item by simply setting its visibility style flag */
TableHeaderItem.draw = function(item, doIt) {
	var showIt = doIt ? "visible" : "hidden";
	item.div.style.visibility = showIt;
}

