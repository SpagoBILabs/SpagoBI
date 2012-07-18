/**
 * A table cell.
 * Currently a cell isn't much more then a value, but this change in future.
 * TODO: things to add are styles, e.g. different background or borders... 
 */
var TableCell = function(value) {

	this._value = value;

	//constructor
	function create(cell, value) {
		cell.div = document.createElement('div');
		cell.div.className = "cell";
		cell.div.style.position = "absolute";
		cell.div.style.display = "inline";
		cell.div.style.visibility = "hidden";
		cell.div.style.overflow = "hidden";
		cell.div.style.verticalAlign = "middle";

		cell.div.appendChild(document.createTextNode(value));
	}

	create(this, value);
};

//METODS:
TableCell.prototype = {
	/** sets cells x position and its width, hence size should be a 2d array */
	setX : function(size) {
		this.div.style.left = size[0] + "px";
		this.div.style.width = size[1] + "px";
	},
	/** sets cells y position and its height, hence size should be a 2d array */
	setY : function(size) {
		this.div.style.top = size[0] + "px";
		this.div.style.height = size[1] + "px";
	},
	toString : function() {
		return "[" + this._value + "]";
	}
};