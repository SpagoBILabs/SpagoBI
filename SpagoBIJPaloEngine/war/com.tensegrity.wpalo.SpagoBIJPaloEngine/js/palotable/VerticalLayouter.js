/**
 * A layouter for the vertical TableHeader 
 */

/** the children indent */
var INDENT = 15; //Item.ICON_SIZE + 2 * Item.ICON_PADDING;

var VerticalLayouter = function() {
	this.header;
	this.maxWidth = new Array();
	this.leafHandler; //the TableContent...
};

//METHODS:
VerticalLayouter.prototype = {
	/**
	 * Computes and returns the width and height of the header as an array. 
	 */
	computeSize: function() {
		var size = [15, 15];
		for(var i=0, n=this.header.items.length; i < n; ++i) {
			var lvl = 0;			
			var item = this.header.items[i];
			var bounds = item.getBounds();
			var width = bounds[0] + this.maxWidth[lvl];
			while(item.rootsNextLevel.length > 0) {
				lvl++;
				width += this.maxWidth[lvl];
				item = item.rootsNextLevel[0];
			}
			if (size[0] < width)
				size[0] = width;
		}
		return size;
	},
	/**
	 * Called by header to notify layouter about a newly added header item.
	 * Used to store the maximum width per level.
	 */
	initialized: function(item) {		
		var lvl = item.level;
		var size = item.getSize();
		if(!this.maxWidth[lvl])
			this.maxWidth[lvl] = 0;
		size[0] += (INDENT * item.depth);
		if(this.maxWidth[lvl] < size[0]) {
			this.maxWidth[lvl] = size[0];
		}
	},
	/**
	 * Shows or hides the given header item.
	 */
	draw: function(item, doIt) {
		TableHeaderItem.draw(item, doIt);
		if(item.hasChildren()) {
			for(var i=0, n=item.children.length; i < n; ++i)
				this.draw(item.children[i],  doIt && item.isExpanded());
		}
		//go down until leaf is reached:
		if(item.rootsNextLevel.length > 0) {
			for(var i=0, n=item.rootsNextLevel.length; i < n; ++i)
				this.draw(item.rootsNextLevel[i], doIt);
		}		
	},
	/**
	 * Arranges the header.
	 */
	layout: function() {
		this.leafHandler.reset(false);
		var size = [0,0];
		var instance = this;
		var leaf = 0;
		for(var i=0, n=this.header.items.length; i < n; ++i) {
			size[1] = layoutItem(this.header.items[i], size[0], size[1], true);
		}
		
		/**
		 * Recursively visit each header item. 
		 * Note that even the hidden items are visited in order to determine
		 * their correct index.
		 */
		function layoutItem(item, x, y, visible) {
			item.setPosition(x, y);
			var bounds = item.getBounds();
			var tmpX = bounds[0] + bounds[2];
			var tmpY = visible ? bounds[1] + bounds[3] : y;
			if(size[0] < tmpX)
				size[0] = tmpX;
			//go down until leaf is reached:
			if(item.rootsNextLevel.length > 0) {
				//start of next hierarchy at _x:
				var _h = y;
				var _x = instance.maxWidth[item.level];
				for(var i=0, n=item.rootsNextLevel.length; i < n; ++i) {
					var nxtRoot = item.rootsNextLevel[i];
					_h = layoutItem(nxtRoot, _x, _h, visible);
					if(visible && _h > tmpY)
						tmpY = _h;
					else
						nxtRoot.setHeight(bounds[3]);
				}
			} else {
				//we count leaf indizes...
				item.leaf = leaf++;
				if(visible)
					instance.leafHandler.rowLeaf(item);
			}
			if(item.hasChildren()) {
				for(var i=0, n=item.children.length; i < n; ++i)
					tmpY = layoutItem(item.children[i], x + INDENT, tmpY, visible && item.isExpanded());			
			}
			return tmpY;
		}
		return size;	
	}
};