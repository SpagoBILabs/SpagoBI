/**
 * A layouter for the horizontal TableHeader 
 */

var HorizontalLayouter = function() {
	this.header;
	this.maxHeight = new Array();
	this.leafHandler;	//the TableContent
};

//METHODS:
HorizontalLayouter.prototype = {
	/**
	 * Computes and returns the width and height of the header as an array. 
	 */
	computeSize: function() {
		var size = [15, 15];;
		for(var i=0, n=this.header.items.length; i < n; ++i) {
			var root = this.header.items[i];
			while(root.rootsNextLevel.length > 0) {
				root = root.rootsNextLevel[0];
			}
			var bounds = root.getBounds();
			var depth = this.header.getMaxLevelDepth(root.level)+1;
			var height = bounds[1] + (bounds[3]*depth);
			if (size[1] < height)
				size[1] = height;
		}
		return size;
	},
	/**
	 * Called by header to notify layouter about a newly added header item.
	 * Used to store the maximum height per level.
	 */	
	initialized: function(item) {		
		var lvl = item.level;
		var size = item.getSize();
		if(!this.maxHeight[lvl])
			this.maxHeight[lvl] = 0;
		if(this.maxHeight[lvl] < size[1]) {
			this.maxHeight[lvl] = size[1];
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
		this.leafHandler.reset(true);
		var size = [0,0];
		var instance = this;
		var leaf = 0;
		for(var i=0, n=this.header.items.length; i < n; ++i) {
			size[0] = layoutItem(this.header.items[i], size[0], size[1], true);
		}
		
		/**
		 * Recursively visit each header item. 
		 * Note that even the hidden items are visited in order to determine
		 * their correct index and the correct number of columns.
		 */
		function layoutItem(item, x, y, visible) {
			item.setPosition(x, y);
			var bounds = item.getBounds();
			var tmpX = visible ? bounds[0] + bounds[2] : x;
			var tmpY = bounds[1] + bounds[3];
			if((tmpY) > size[1])
				size[1] = tmpY;
			//go down until leaf is reached:
			if(item.rootsNextLevel.length > 0) {
				//start of next hierarchy at _y:
				var _w = x;
				var _y = y + (instance.maxHeight[item.level] * (instance.header.depths[item.level] - item.depth + 1)); //+1 because depth can be 0
				for(var i=0, n=item.rootsNextLevel.length; i < n; ++i) {
					var nxtRoot = item.rootsNextLevel[i];
					_w = layoutItem(nxtRoot, _w, _y, visible);
					if(visible && _w > tmpX) {
						tmpX = _w;
					} else {
						nxtRoot.setWidth(bounds[2]);
					}
				}
			} else {
				//we count leaf indizes...
				item.leaf = leaf++;
				if(visible)
					instance.leafHandler.columnLeaf(item);
			}
			if(item.hasChildren()) {
				for(var i=0, n=item.children.length; i < n; ++i)
					tmpX = layoutItem(item.children[i], tmpX, tmpY, visible && item.isExpanded());							
			}
			return tmpX;
		}
		//the last leaf index determines the current number of columns!
		instance.leafHandler.columns(leaf);
		return size;	
	}
};