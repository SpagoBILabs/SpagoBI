/**
 * A table header for the PaloTable.
 */
var TableHeader = function(layouter) {
	//public fields
	this.items = [];	//all header items
	this.depths = [];	//depths per level
	this.layouter = layouter; //the used layouter
	this.expandListener;	//to notify when an item expands/collaps 
	
	//constructor:
	function create(header) {
		header.div = document.createElement('div');
		header.div.style.position = "absolute";
		header.div.style.display = "inline";
		header.layouter.header = header;
	}
	 
	//call constructor:
	create(this);	
};

//METHODS:
TableHeader.prototype = {
	clear: function() {
		for(var i = 0, n=this.items.length; i<n; ++i) {
			this.removeItem(this.items[i]);
			delete this.items[i];
		}
		this.items = [];
		for(var i = 0, n = this.depths.length; i<n; ++i) {
			delete this.depths[i];
		}
		this.depths = [];
	},
	/**
	 * Computes the width and height of the header and returns them an array. 
	 */
	computeSize: function() {
		var size = this.layouter.computeSize();
		return size;
	},
	/**
	 * Arranges all added header items.
	 */
	layout: function() {
		var size = this.layouter.layout();
		return size;
	},
	/**
	 * Adds the given TableHeaderItem to this header.
	 */
	addItem: function(item) {
		this.items[this.items.length] = item;
		this.appendItem(item);
	},
	/**
	 * Adds the given child TableHeaderItem to the given father and to this 
	 * header.
	 */
	addChild: function(child, father) {
		father.children[father.children.length] = child;
		this.appendItem(child);
	},
	/**
	 * Sets the header position relative to its containing parent.
	 */
	setPosition: function(x,y) {
		this.div.style.top = y;
		this.div.style.left = x;		
	},
	/**
	 * Draws each header item.
	 */
	draw: function() {
		for(var i=0, n=this.items.length; i < n; ++i) {
			this.layouter.draw(this.items[i], true);
		}
	},
	/**
	 * Adds this header as an expand/collapse listener to the given header item.
	 */
	addExpandListener: function(item) {
		var instance = this;
		addEvent(item.div, "click", function() {instance.expand(item)});
	},
	/**
	 * Called when the given item was expanded or collapsed.
	 */
	expand: function(item) {
		item.setIsExpanded(!item.isExpanded());
		this.expandListener.expand(item, this);
	},
	/**
	 * Returns the maximum depth for the given level.
	 */
	getMaxLevelDepth: function(lvl) {
		return this.depths[lvl];
	},
	/**
	 * INTERNAL METHOD
	 * adds the given item to this header and attach an listener to it...
	 */
	appendItem: function(item) {
		this.div.appendChild(item.div);
		item.setSize(Ruler.getSize(item));
		this.setDepthPerLevel(item.depth, item.level);
		this.layouter.initialized(item);
		this.addExpandListener(item);
		//append children:
		this.appendKids(item);
		//append roots in next level:
		this.appendRootsNextLevel(item);
	},
	/**
	 * INTERNAL METHOD
	 */	
	appendKids: function(item) {
		if(item.hasChildren()) {
			for(var i=0, n=item.children.length; i < n; ++i) {
				this.appendItem(item.children[i]);
			}
		}
	},
	/**
	 * INTERNAL METHOD
	 */
	appendRootsNextLevel: function(item) {
		for(var i=0, n=item.rootsNextLevel.length; i < n; ++i) {
			this.appendItem(item.rootsNextLevel[i]);
		}
	},
	removeItem: function(item) {
		this.div.removeChild(item.div);
		if(item.hasChildren()) {
			for(var i=0, n=item.children.length; i < n; ++i) {
				this.removeItem(item.children[i]);
			}
		}
		for(var i=0, n=item.rootsNextLevel.length; i < n; ++i) {
			this.removeItem(item.rootsNextLevel[i]);
		}		
	},
	
	/**
	 * Sets the given maximum depth of the specified level
	 */
	setDepthPerLevel: function(depth, lvl) {
		if(!this.depths[lvl])
			this.depths[lvl] = 0;
		if(this.depths[lvl] < depth) {
			this.depths[lvl] = depth;
		}		
	}
};

