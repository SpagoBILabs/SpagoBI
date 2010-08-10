/**
 * A container for TableCells
 */
var TableContent = function() {
	
	this.sizeX = []; //contains the x position and the width per column
	this.sizeY = []; //contains the y position and the height per row
	this.cells = []; //contains all known/added cells	
	this.insertCells = []; //contains the cells to insert
	this.rowIndizes = []; //contains the leaf index of currently visible rows 
	this.colIndizes = []; //contains the leaf index of currently visible columns
	this.colCount = 0;  //overall added columns
	
	//constructor:
	function create(content) {
		content.div = document.createElement('div');
		content.div.style.position = "absolute";
		content.div.style.display = "inline";
		content.div.style.width = "100%";
		content.div.style.height = "100%";
	}
	 
	//call constructor:
	create(this);
};

//METHODS:
TableContent.prototype = {
	/**
	 * Adds the given cell to this container.
	 */
	add: function(cell) {
		this.cells[this.cells.length] = cell;
		this.div.appendChild(cell.div);
	},
	/**
	 * Stores the given cell as to be inserted.
	 * To insert cells doInsert() must be called...
	 */
	insert: function(cell) {
		this.insertCells[this.insertCells.length] = cell;
		this.div.appendChild(cell.div);
	},
	/**
	 * Treats the cells to be inserted as new rows and adds them at the 
	 * specified index.
	 */
	insertRows: function(index) {
		//simply insert as rows
		var newCells = [];
		var cellsLength = this.cells.length;
		var insertCellsLength = this.insertCells.length;
		
		if(index > cellsLength) {
			index = cellsLength;
		}
		
		for(var i=0; i<index; ++i) {
			newCells[i] = this.cells[i];
		}
		for(var i=0; i<insertCellsLength; ++i) {
			newCells[index + i] = this.insertCells[i];
		}
		for(var i=index; i<cellsLength; ++i) {
			newCells[insertCellsLength + i] = this.cells[i];
		}				
		this.cells = newCells;		 
	},
	/**
	 * Treats the cells to be inserted as new columns and adds them at the 
	 * specified index. Parameter cols specifies the number of columns to insert.
	 */
	insertColumns: function(index, cols) {
		var newCells = [];
		var newCols = this.colCount + cols;
		//copy old stuff:
		for(var i=0, n=this.cells.length; i<n ;++i) {
			var r = Math.floor(i/this.colCount);
			var c = i%this.colCount;
			if(c >= index)
				c += cols;
			//new index:
			var _index = r * newCols + c;
			newCells[_index] = this.cells[i];			
		}
		//insert new columns:
		for(var i=0, n=this.insertCells.length; i<n ; ++i) {
			var r = Math.floor(i/cols);
			var c = (index+i%cols)%newCols;
			//new index:
			var _index = r * newCols + c;
			newCells[_index] = this.insertCells[i];
		}
		this.cells = newCells;		 
	},
	/**
	 * Must be called to insert cells which where added via insert(cell).
	 * Parameter index specifies the row or table index to insert at. In case
	 * of columns insert the cols parameter specifies the number of columns to
	 * inert.
	 */
	doInsert: function(index, cols, isRowInsert) {
		if(isRowInsert) {
			index *= this.colCount;
			this.insertRows(index);
		} else {
			this.insertColumns(index, cols);
		}
		this.insertCells = [];
	},	
	/**
	 * Shows each visible cell.
	 */
	draw: function() {
		var m = this.colCount; 
		for(var r=0; r<this.rowIndizes.length; r++) {
			for(var c=0; c<this.colIndizes.length; c++) {				
				var index = this.rowIndizes[r]*m + this.colIndizes[c];
				this.cells[index].setX(this.sizeX[c]);
				this.cells[index].setY(this.sizeY[r]);
				this.cells[index].div.style.visibility = "visible";				
			}
		}
	},
	/**
	 * Sets the position of this container.
	 */
	setPosition: function(x, y) {
		this.div.style.left = x;
		this.div.style.top = y;
	},
	/**
	 * Hides all known cells.
	 */
	clear: function() {
		for(var c = 0, n=this.cells.length; c<n; ++c) {
			this.div.removeChild(this.cells[c].div);
			delete this.cells[c];
			//this.cells[c].div.style.visibility = "hidden";
		}
		this.cells = [];
	},	
	/**
	 * Resets the per column or row stored information.  
	 */
	reset: function(columns) {
		if(columns) {
			this.sizeX = [];
			this.colIndizes = [];
		} else {
			this.sizeY = [];
			this.rowIndizes = [];
		}
	},
	/**
	 * Called when the layouter visited a leaf header item.
	 */
	rowLeaf: function(item) {
		var bounds = item.getBounds();
		var index = this.rowIndizes.length;
		this.rowIndizes[index] = item.leaf;
		this.sizeY[index] = [bounds[1], bounds[3]];
	},
	/**
	 * Called when the layouter visited a leaf header item.
	 */
	columnLeaf: function(item) {
		var bounds = item.getBounds();
		var index = this.colIndizes.length;
		this.colIndizes[index] = item.leaf;
		this.sizeX[index] = [bounds[0], bounds[2]];
	},
	/**
	 * Called when the horizontal layouter has visited all leaf items.
	 */	
	columns: function(count) {
		this.colCount = count;
	}
};