function interactionsGraph(links, profiles)
{
	var nodes = {}
	
	// Compute the distinct nodes from the links.
	links.forEach(function(link) {
		  link.source = nodes[link.source] || (nodes[link.source] = {name: link.source});
		  link.target = nodes[link.target] || (nodes[link.target] = {name: link.target});
		});			

	var width =  $('#usersMainGraph').width();
	var height = $('#usersMainGraph').height()-$('#usersGraphTitle').innerHeight();
	var r = 25;

	var force = d3.layout.force()
	    .nodes(d3.values(nodes))
	    .links(links)
	    .size([width, height])
	    .charge(-780)
	    .linkDistance(50)
		.on("tick", tick)
	    .start();
	
	var drag = force.drag()
    .on("dragstart", dragstart);
	
	var svg = d3.select("#usersGraph").append("svg")
    .attr("width", width)
    .attr("height", height);
	
	svg.append("svg:rect")
    .attr("width", width)
    .attr("height", height)
    .style("stroke", "#000");
	
	var link = svg.selectAll(".link")
    .data(force.links())
  .enter().append("line")
    .attr("class", "link");

	var node = svg.selectAll(".node")
	    .data(force.nodes())
	  .enter().append("g")
	    .attr("class", "node")
	    .call(force.drag);

	node.append("svg:defs")
		.append("svg:pattern")
		.attr("id", function(d, i) {
			return "image"+i;	
		})
		.attr("x", "25")
		.attr("y", "25")
		.attr("width", "50")
		.attr("height", "50")
		.attr("patternUnits", "userSpaceOnUse")				
		.append("svg:image")
		.attr("xlink:href", function(d, i) {
	        // d is the node data, i is the index of the node
				return profiles[d.name];
	    })
		.attr("x", "0")
		.attr("y", "0")
//			.attr("id", "fillImage");
		.attr("width", "50")
		.attr("height", "50");			
	
 var circle = node.append("circle")
    .attr("r", r)
	.attr("fill",function(d, i) {
		return "url(#image"+i+")";	
	});
 
 function tick() {
	 
	 node.attr("cx", function(d) { return d.x = Math.max(r, Math.min(width - r, d.x)); })
	    .attr("cy", function(d) { return d.y = Math.max(r, Math.min(height - r, d.y)); });

	link.attr("x1", function(d) { return d.source.x; })
	    .attr("y1", function(d) { return d.source.y; })
	    .attr("x2", function(d) { return d.target.x; })
	    .attr("y2", function(d) { return d.target.y; });

	node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
	}
 
 function dragstart(d) {
	  d3.select(this).classed("fixed", d.fixed = true);
	}
}