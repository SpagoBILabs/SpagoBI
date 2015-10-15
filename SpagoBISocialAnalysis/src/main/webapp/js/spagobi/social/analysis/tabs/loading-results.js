$(document).ready(function(){
			
    $(".navtabs").click(function(){
    	
    	var width = $("#navigation").width();
        var height = $("#navigation").height()
    	
    	$("#report-loading").css({
	        top: (100),
	        left: ((width / 2) - 50),
	        display: "block"
	    })			
    });

  });