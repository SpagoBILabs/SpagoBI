function hideMenu(event, divId) {
	var divM = document.getElementById(divId);
  var theTop;
	if (document.documentElement && document.documentElement.scrollTop) {
		theTop = document.documentElement.scrollTop;
	}
	else {
		if (document.body){
			theTop = document.body.scrollTop;
		}
	}
	
	parentContainerPosition = findParentContainerPosition(divM);
	
  yup = parseInt(divM.style.top) + parentContainerPosition[1] - parseInt(theTop);
  ydown = parseInt(divM.style.top) + parentContainerPosition[1] + parseInt(divM.offsetHeight) - parseInt(theTop);
  xleft = parseInt(divM.style.left) + parentContainerPosition[0];
  xright = parseInt(divM.style.left) + parentContainerPosition[0] + parseInt(divM.offsetWidth);
  if( (event.clientY<=(yup+2)) || (event.clientY>=ydown) || (event.clientX<=(xleft+2)) || (event.clientX>=xright) ) {
		divM.style.display = 'none' ;
	}
}	

function forceHideMenu(divId) {
	var divM = document.getElementById(divId);
	divM.style.display = 'none';
}

function showMenu(event, divId) {
  var divM = document.getElementById(divId);
	var theTop;
	if (document.documentElement && document.documentElement.scrollTop) {
		theTop = document.documentElement.scrollTop;
	}
	else {
		if (document.body){
			theTop = document.body.scrollTop;
		}
	}
	var theLeft;
	if (document.documentElement && document.documentElement.scrollLeft) {
		theLeft = document.documentElement.scrollLeft;
	}
	else {
		if (document.body){
			theLeft = document.body.scrollLeft;
		}
	}
	
	parentContainerPosition = findParentContainerPosition(divM);
	
  divM.style.left = '' + (event.clientX + theLeft - parentContainerPosition[0] - 5) + 'px';
	divM.style.top = '' + (event.clientY + theTop - parentContainerPosition[1] - 5) + 'px';
	divM.style.display = 'inline' ;

}

function findParentContainerPosition(obj) {
	var parentNode = obj.parentNode;
	while (true) {
  	 if (parentNode == document) {break;}
  	 if (parentNode.style && parentNode.style.position) {
  	   break;
     } else {
       parentNode = parentNode.parentNode;
     }
  }
  if (parentNode != null) {
  	return findPos(parentNode);
	} else {
    return [0,0];
  }
}

function findPos(obj) {
       curleft = curtop = 0;
	   if (obj.offsetParent) {
		    curleft = obj.offsetLeft
		    curtop = obj.offsetTop
		    while (obj = obj.offsetParent) {
			     curleft += obj.offsetLeft
			     curtop += obj.offsetTop
		    }
	   }
	   return [curleft,curtop];
}
