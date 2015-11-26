function localeMapping(labelId, labelText) 
{
//	console.log("labelId: " + labelId + " labelText: " + labelText);
	if(document.getElementById(labelId))
	{
		document.getElementById(labelId).innerHTML = LN(labelText);
	}	 

};