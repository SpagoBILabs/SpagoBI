
function interactionsNetwork(connections, codes, weightsLinks, world110m, countriesCSV)
{
	var connectionsMap = [];
	var linksByOrigin = {};
	
	var w =  $('#usersTweetLinkMapMain').width();
	var h = $('#usersTweetLinkMapMain').height()-$('#usersTweetLinkMapTitle').innerHeight();
	var centered;
	
    var rotate = [0,0];
	
	var tooltip = d3.select("#usersTweetLinkMap").append("div").attr("class", "tooltip hidden");
	
	//offsets for tooltips
	var offsetL = document.getElementById('usersTweetLinkMap').offsetLeft+20;
	var offsetT = document.getElementById('usersTweetLinkMap').offsetTop+10;
	
	var maxPointRadius = 7;
	
	var area = w * h;
	
	var radius = h/3;

	var zoom = d3.behavior.zoom().scaleExtent([1, 9]).on("zoom", move);
	
	function move() {

		  var t = d3.event.translate;
		  var s = d3.event.scale; 
		  zscale = s;
		  var hz = h/4;
		
		
		  t[0] = Math.min(
		    (w/h)  * (s - 1), 
		    Math.max( w * (1 - s), t[0] )
		  );
		
		  t[1] = Math.min(
		    hz * (s - 1) + hz * s, 
		    Math.max(h  * (1 - s) - hz * s, t[1])
		  );
		
		  zoom.translate(t);
		  g.attr("transform", "translate(" + t + ")scale(" + s + ")");

	};
	
	var drag = d3.behavior.drag()
    .on("dragstart", function() {
    // Adapted from http://mbostock.github.io/d3/talk/20111018/azimuthal.html and updated for d3 v3
      var proj = projection.rotate();
      m0 = [d3.event.sourceEvent.pageX, d3.event.sourceEvent.pageY];
      o0 = [-proj[0],-proj[1]];
    })
    .on("drag", function() {
      if (m0) {
        var m1 = [d3.event.sourceEvent.pageX, d3.event.sourceEvent.pageY],
            o1 = [o0[0] + (m0[0] - m1[0]) / 4, o0[1] + (m1[1] - m0[1]) / 4];
        projection.rotate([-o1[0], -o1[1]]);
      }
      
   // Update the map
      path = d3.geo.path().projection(projection);
      d3.selectAll("path").attr("d", path);
    });


// 	var projection = d3.geo.kavrayskiy7().precision(.1),
	var projection = d3.geo.azimuthalEqualArea().scale(h/5).clipAngle(180 - 1e-3)
    .translate([w / 2, h / 2])
    .precision(0.3)
    .rotate(rotate);
    
    var color = d3.scale.category20(),
	graticule = d3.geo.graticule();

// 	var projection = d3.geo.orthographic()
//     .scale(radius)
//     .translate([w / 2, h / 2])
//     .clipAngle(90);
	
	var path = d3.geo.path()
	    .projection(projection);
	
	var svg = d3.select("#usersTweetLinkMap").append("svg")
	    .attr("width", w)
	    .attr("height", h)	    
	    .call(zoom)
// 	    .call(drag)
	    .append("g");

  g = svg.append("g");


	    

	d3.json(world110m, function(error, world) {
		
		
	  var countries = topojson.feature(world, world.objects.countries).features,
	      neighbors = topojson.neighbors(world.objects.countries.geometries);
	  
	  var globe = {type: "Sphere"};
	    g.append("path")
	    .datum(globe)
	    .attr("class", "foreground")
	    .attr("d", path);
	
	  g.selectAll(".country")
	      .data(countries)
	    .enter().insert("path", ".graticule")
	      .attr("class", "country")
	      .attr("d", path);
// 	      .style("fill", "#FFB875");

		
	  
	  d3.csv(countriesCSV, function(error, data) {// read in and plot the circles
		  
		  var filterData = [];
	  	  var pointData = [];
	  	  var counterPointData = 0;
	  
		  for(var i = 0; i < codes.length; i++)
		  {
			  for(var j = 0; j < data.length; j++)
			  {
			  		if(codes[i][0] == data[j].code)
		  			{
		  				filterData[data[j].code] = data[j];
		  				pointData[counterPointData++] = {country: data[j].country.toUpperCase(), code: data[j].code ,lat: data[j].lat ,lon: data[j].lon , rep: codes[i][1]}
// 		  				pointData.push(data[j]);
		  				break;
		  			}
			  }
						  
		  }  
		  
// 		  console.log(pointData);

		  

	  	
		  
	        g.selectAll("circle").data(pointData).enter().append("circle").attr("class", "circle")
	        .attr("cx", function(d) {
	            return projection([d.lon, d.lat])[0];
	        }).attr("cy", function(d) {
	            return projection([d.lon, d.lat])[1];
	        }).attr("r", function(d) {
	        	if(d.rep <= 6)
	        	{
	        		return d.rep;
	        	}
	        	else
	        	{
	        		return maxPointRadius;
	        	}
	        })
	        .on("mouseover", function(d,i) {
	          	var mouse = d3.mouse(svg.node()).map( function(d) { return parseInt(d); } );

	          tooltip.classed("hidden", false)
	                 .attr("style", "left:"+(mouse[0]+offsetL)+"px;top:"+(mouse[1]+offsetT)+"px")
	                 .html("Country: <label style='font-size: 25px; font-weight:bold;'>" + d.country + "</label><br/>" + "Interactions: <label style='font-size: 25px; font-weight:bold;'>" + d.rep + "</label>");

	          })
	          .on("mouseout",  function(d,i) {
	            tooltip.classed("hidden", true);
	          }); 

	        
	        
	        var lineTransition = function lineTransition(path) {
	            path.transition()

	                .duration(5500)
// 	                .attrTween("stroke-dasharray", tweenDash)
	                .each("end", function(d,i) { 

	                });
	        };
	        var tweenDash = function tweenDash() {

	            var len = this.getTotalLength(),
	                interpolate = d3.interpolateString("0," + len, len + "," + len);

	            return function(t) { return interpolate(t); };
	        };


	        console.log(weightsLinks);
	        console.log(connections);	
	        
	        var connectionsData = [];
	        var count={};
	        
	        for(var i = 0; i < connections.length; i++)
        	{
        		var originCode = connections[i][0];
        		var destinationCode = connections[i][1];
        		
        		var label = originCode+destinationCode;
        		var inverseLabel = destinationCode+originCode;
        		
        		var tempArc = 
        		{
       	                type: "LineString",
       	                coordinates: [
       	                    [ filterData[originCode].lon, filterData[originCode].lat ],
       	                    [ filterData[destinationCode].lon, filterData[destinationCode].lat ]
       	                ],
       	                label: label,
       	                inverseLabel: inverseLabel,
       	                origin: filterData[originCode].country,
       	                destination: filterData[destinationCode].country
       	    	};
        		
        		if(weightsLinks[label])
        		{
        			connectionsData.push(tempArc);	
        		}
        		
        		
        	}
	        

	        // Standard enter / update 
	        var pathArcs = g.selectAll(".arc")
	            .data(connectionsData)
	            .enter()
	            .append("path")
	            .attr("class", "arc")
	            .attr("fill", "none")
				.attr("d", path)
	            .attr("stroke", "#0084B4")
	            .attr("stroke-width", function(d)
	            {
	            	var strokeWidth = weightsLinks[d.label] ? weightsLinks[d.label] : 0;
	            	if(strokeWidth < 5)
	            	{
	            		return weightsLinks[d.label] ? weightsLinks[d.label] : 0;
	            	}
	            	else
	            	{
	            		return 6;	
	            	}
	            })
	            // Uncomment this line to remove the transition
	            .call(lineTransition)
	            .on("mouseover", function(d,i) {
	          		var mouse = d3.mouse(svg.node()).map( function(d) { return parseInt(d); } );

	          tooltip.classed("hidden", false)
	                 .attr("style", "left:"+(mouse[0]+offsetL)+"px;top:"+(mouse[1]+offsetT)+"px")
	                 .html("# interactions between " + d.origin + " and " + d.destination + ": " + (weightsLinks[d.label] ? weightsLinks[d.label] : ""));
	          })
	          .on("mouseout",  function(d,i) {
	            tooltip.classed("hidden", true);
	          });

	        //exit
// 	        pathArcs.exit().remove();

	    });

	  
// 	  d3.timer(function() {
// 		    var angle = velocity * (Date.now() - then);
// 		    projection.rotate([angle,0,0]);
// 		    svg.selectAll("path")
// 		      .attr("d", path.projection(projection));
// 		  });
	  
	    });
};
	
	


	
	
// 	for(var i = 0; i < connections.length; i++)
// 	{
// 		var connection = connections[i];
		
// 		d3.geo.greatArc().source(connection["source"]).target(connection["target"]);
// 	}
	