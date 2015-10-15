function sentimentTopics(positiveData, neutralData, negativeData)
{				
		d3.select(".positive_barchart")
		  .selectAll("div")
		  .data(positiveData)
		  .enter()
		  .append("div")
		  .style("width", function(d)
		    				{ if(d.value > 30) return "300px"; else return (d.value*10) + "px"; })
		   .text(function(d) { return d.value; });
	       
		
		d3.select(".posTopics_label")
		  .selectAll("div")
		  .data(positiveData)
		  .enter()
		  .append("div")
		  .style("margin", "1px 1px 10px 1px")
		  .style("padding", "3px")
		  .text(function(d) { return d.name });
		
	

		
		d3.select(".neutral_barchart")
		  .selectAll("div")
		    .data(neutralData)
		  .enter().append("div")
		    .style("width", function(d) { if(d.value > 30) return "300px"; else return (d.value*10) + "px"; })
		    .text(function(d) { return d.value; });
		
		d3.select(".neuTopics_label")
		  .selectAll("div")
		  .data(neutralData)
		  .enter()
		  .append("div")
		  .style("margin", "1px 1px 10px 1px")
		  .style("padding", "3px")
		  .text(function(d) { return d.name });
	
	
		d3.select(".negative_barchart")
		  .selectAll("div")
		    .data(negativeData)
		  .enter().append("div")
		    .style("width", function(d) { if(d.value > 30) return "300px"; else return (d.value*10) + "px"; })
		    .text(function(d) { return d.value; });
		
		d3.select(".negTopics_label")
		  .selectAll("div")
		  .data(negativeData)
		  .enter()
		  .append("div")
		  .style("margin", "1px 1px 10px 1px")
		  .style("padding", "3px")
		  .text(function(d) { return d.name });
};
	
	