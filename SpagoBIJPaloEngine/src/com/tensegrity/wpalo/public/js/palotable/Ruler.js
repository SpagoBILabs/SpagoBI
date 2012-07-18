/** 
 * A simple singleton to measure the size of header items.
 */
Ruler =  (function() {
	
	/** the dummy item for measuring... */
	var dummy;
	
	function create() {		
		var body = document.getElementsByTagName("body")[0];
		if(!body)
			alert("body is undefined!! cannot create ruler!!");
		//create an invisible dummy item:
		dummy = new TableHeaderItem("ruler");
		dummy.div.style.visibility = "hidden";
		body.appendChild(dummy.div);
	}

	return {
		//PUBLIC METHOD:
		/**
		 * Determines and returns the width and height of the given 
		 * TableHeaderItem.
		 */
		getSize: function(item) {
			if(!dummy) create();
			dummy.setName(item.getName());
			if(item.hasChildren()) {
				dummy.icon.className = "plus visible";
			} else {
				dummy.icon.className = "hidden";
			}
			return dummy.getSize();
		}
	}
})();
