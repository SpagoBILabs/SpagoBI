function summaryTimeline(hourlyData, dailyData, weeklyData, monthlyData, hourlyDataOverview, dailyDataOverview, weeklyDataOverview,monthlyDataOverview, ticks)
{
			
		
	// helper for returning the weekends in a period

	function weekendAreas(axes) {

		var markings = [],
			d = new Date(axes.xaxis.min);

		// go to the first Saturday

		d.setUTCDate(d.getUTCDate() - ((d.getUTCDay() + 1) % 7))
		d.setUTCSeconds(0);
		d.setUTCMinutes(0);
		d.setUTCHours(0);

		var i = d.getTime();

		// when we don't set yaxis, the rectangle automatically
		// extends to infinity upwards and downwards

		do {
			markings.push({ xaxis: { from: i, to: i + 2 * 24 * 60 * 60 * 1000 } });
			i += 7 * 24 * 60 * 60 * 1000;
		} while (i < axes.xaxis.max);

		return markings;
	}
		
	var monthlyOptions = 
	{
		xaxis: 
		{
			mode: "time",
			minTickSize: [1, "month"],
			timeformat: "%b %Y"
		},
		yaxis: 
		{
			tickDecimals: 0
		},
		series: 
		{
			lines:
			{
				show: true,
				fill: true
			}
		},
		grid: {
			hoverable: true,
			clickable: true
		},
		legend:
		{
			container: $("#main-graph"),
			noColumns:2,
			margin: '5px',
		},
		colors: ["#0084B4", "#ff0000"]
	};
		
	var weeklyOptions = 
	{
		xaxis: 
		{
			mode: "time",
			ticks: ticks,
			tickFormatter: function (val, axis) 
			{
				var month = new Array(12);
				month[0] = "Jan";
				month[1] = "Feb";
				month[2] = "Mar";
				month[3] = "Apr";
				month[4] = "May";
				month[5] = "Jun";
				month[6] = "Jul";
				month[7] = "Aug";
				month[8] = "Sep";
				month[9] = "Oct";
				month[10] = "Nov";
				month[11] = "Dec";
				
			    var firstDayWeek = new Date(val);
			    firstDayWeek.setUTCSeconds(0);
			    firstDayWeek.setUTCMinutes(0);
			    firstDayWeek.setUTCHours(0);
			    
			    var lastDayWeek = new Date(firstDayWeek.getTime() + 6 * 24 * 60 * 60 * 1000);
			    
			    return ('0' + firstDayWeek.getUTCDate()).slice(-2) + " - " + ('0' + lastDayWeek.getUTCDate()).slice(-2) + " " + month[firstDayWeek.getUTCMonth()];
			},
		},
		yaxis: 
		{
			tickDecimals: 0
		},
		series: 
		{
			lines:
			{
				show: true,
				fill: true
			}
		},
		grid: 
		{
			hoverable: true,
			clickable: true
		},
		legend:
		{
			container: $("#main-graph"),
			noColumns:2,
			margin: '5px',
		},
		colors: ["#0084B4", "#ff0000"]
	};
		
	var dailyOptions = 
	{
		xaxis: 
		{
			mode: "time",
			minTickSize: [1, "day"],
			timeformat: "%d %b"
		},
		yaxis: 
		{
			tickDecimals: 0
		},
		series: 
		{
			lines:
			{
				show: true,
				fill: true
			}
		},
		grid: 
		{
			markings: weekendAreas,
			hoverable: true,
			clickable: true
		},
		legend:
		{
			container: $("#main-graph"),
			noColumns:2,
			margin: '5px',
		},
		colors: ["#0084B4", "#ff0000"]
	};
		
	var hourlyOptions = 
	{
		xaxis: 
		{
			mode: "time",
			minTickSize: [1, "hour"],
			timeformat: "%d %b  %H:%M"
		},
		yaxis: 
		{
			tickDecimals: 0
		},
		series: 
		{
			lines:
			{
				show: true,
				fill: true
			}
		},
		grid: 
		{
			markings: weekendAreas,
			hoverable: true,
			clickable: true
		},
		legend:
		{
			container: $("#main-graph"),
			noColumns:2,
			margin: '5px',
		},
		colors: ["#0084B4", "#ff0000"]
	};
		
	overviewOptionsMonth =
		{
			series: {
				lines: {
					show: true,
					lineWidth: 1
				},							
				shadowSize: 0
			},
			xaxis: {
				mode: "time",
				minTickSize: [1, "month"],
				timeformat: "%b %Y"
			},
			yaxis: {
				ticks: [],
				min: 0,
				autoscaleMargin: 0.1
			},
			selection: {
				mode: "x"
			},
			colors: ["#0084B4", "#ff0000"]
	};
		
	overviewOptionsWeek =
	{
		series: {
			lines: {
				show: true,
				lineWidth: 1
			},							
			shadowSize: 0
		},
		xaxis: {
			mode: "time",
			ticks: ticks,
			tickFormatter: function (val, axis) 
			{
				var month = new Array(12);
				month[0] = "Jan";
				month[1] = "Feb";
				month[2] = "Mar";
				month[3] = "Apr";
				month[4] = "May";
				month[5] = "Jun";
				month[6] = "Jul";
				month[7] = "Aug";
				month[8] = "Sep";
				month[9] = "Oct";
				month[10] = "Nov";
				month[11] = "Dec";
				
			    var firstDayWeek = new Date(val);
			    firstDayWeek.setUTCSeconds(0);
			    firstDayWeek.setUTCMinutes(0);
			    firstDayWeek.setUTCHours(0);
			    
			    var lastDayWeek = new Date(firstDayWeek.getTime() + 6 * 24 * 60 * 60 * 1000);
			    
			    return ('0' + firstDayWeek.getUTCDate()).slice(-2) + " - " + ('0' + lastDayWeek.getUTCDate()).slice(-2) + " " + month[firstDayWeek.getUTCMonth()];
			},							
		},
		yaxis: {
			ticks: [],
			min: 0,
			autoscaleMargin: 0.1
		},
		selection: {
			mode: "x"
		},
		colors: ["#0084B4", "#ff0000"]
	};
	
	overviewOptionsDay =
	{
		series: {
			lines: {
				show: true,
				lineWidth: 1
			},							
			shadowSize: 0
		},
		xaxis: {
			mode: "time",
			minTickSize: [1, "day"],
			timeformat: "%d %b"
		},
		yaxis: {
			ticks: [],
			min: 0,
			autoscaleMargin: 0.1
		},
		selection: {
			mode: "x"
		},
		colors: ["#0084B4", "#ff0000"]
	};
	
	overviewOptionsHour =
	{
		series: {
			lines: {
				show: true,
				lineWidth: 1
			},							
			shadowSize: 0
		},
		xaxis: {
			mode: "time",
			minTickSize: [1, "hour"],
			timeformat: "%d %b",
		},
		yaxis: {
			ticks: [],
			min: 0,
			autoscaleMargin: 0.1
		},
		selection: {
			mode: "x"
		},
		colors: ["#0084B4", "#ff0000"]
	};

	var plot = $.plot("#placeholder", weeklyData, weeklyOptions);
	
	var overview = $.plot("#overview", weeklyDataOverview, overviewOptionsWeek);
	
	$("#placeholder").bind("plotselected", function (event, ranges) {

		// do the zooming
		$.each(plot.getXAxes(), function(_, axis) {
			var opts = axis.options;
			opts.min = ranges.xaxis.from;
			opts.max = ranges.xaxis.to;
		});
		plot.setupGrid();
		plot.draw();
		plot.clearSelection();

		// don't fire event on the overview to prevent eternal loop

		overview.setSelection(ranges, true);
	});

	$("#overview").bind("plotselected", function (event, ranges) {
		plot.setSelection(ranges);
		
	});
					
	$("#placeholder").bind("plothover", function (event, pos, item) 
	{
		if (item) 
		{
			var someData = weeklyData;
			var content = item.series.label + " = " + item.datapoint[1];
			            
			for (var i = 0; i < someData.length; i++)
            {
                if (someData[i].label == item.series.label)
                {					                	
                    continue;   
                }
                
                for (var j=0; j < someData[i].data.length; j++)
                {
                    if (someData[i].data[j][0] == item.datapoint[0] && someData[i].data[j][1] == item.datapoint[1])
                  	{
                          content += '<br/>' + someData[i].label + " = " + item.datapoint[1]; 
                    }
                }                
            }					            
            
            showTooltip(item.pageX, item.pageY, content);
        }
        else 
        {
            $("#tooltip").css('display','none');       
        }
	});	
	
	
	$("#hours").click(function () 
	{
		
		plot = $.plot("#placeholder", hourlyData, hourlyOptions);
		
		 overview = $.plot("#overview", hourlyDataOverview, overviewOptionsHour);
		
		$("#placeholder").bind("plothover", function (event, pos, item) 
		{
		 	if (item) 
		 	{
		 		var someData = hourlyData;
	            var content = item.series.label + " = " + item.datapoint[1];
	            
	            for (var i = 0; i < someData.length; i++)
	            {
	                if (someData[i].label == item.series.label)
	                {					                	
	                    continue;   
	                }
	                
	                for (var j=0; j < someData[i].data.length; j++)
	                {
	                    if (someData[i].data[j][0] == item.datapoint[0] && someData[i].data[j][1] == item.datapoint[1])
	                  	{
	                          content += '<br/>' + someData[i].label + " = " + item.datapoint[1]; 
	                    }
	                }                
	            }					            
	            
	            showTooltip(item.pageX, item.pageY, content);
	        }
	        else 
	        {
	            $("#tooltip").css('display','none');       
	        }
		});					
	});
		
		
	$("#days").click(function () 
	{										
		
		plot = $.plot("#placeholder", dailyData, dailyOptions);
		
		overview = $.plot("#overview", dailyDataOverview, overviewOptionsDay);
		
		 $("#placeholder").bind("plothover", function (event, pos, item) 
					{
					 	if (item) 
					 	{
					 		var someData = dailyData;
				            var content = item.series.label + " = " + item.datapoint[1];
				            
				            for (var i = 0; i < someData.length; i++)
				            {
				                if (someData[i].label == item.series.label)
				                {					                	
				                    continue;   
				                }
				                
				                for (var j=0; j < someData[i].data.length; j++)
				                {
				                    if (someData[i].data[j][0] == item.datapoint[0] && someData[i].data[j][1] == item.datapoint[1])
				                  	{
				                          content += '<br/>' + someData[i].label + " = " + item.datapoint[1]; 
				                    }
				                }                
				            }					            
				            
				            showTooltip(item.pageX, item.pageY, content);
				        }
				        else 
				        {
				            $("#tooltip").css('display','none');       
				        }
			});	
				
		});	
		
		$("#weeks").click(function () 
		{
			
			plot = $.plot("#placeholder", weeklyData, weeklyOptions);
			
			overview = $.plot("#overview", weeklyDataOverview, overviewOptionsWeek);
			
			 $("#placeholder").bind("plothover", function (event, pos, item) 
						{
						 	if (item) 
						 	{
						 		var someData = weeklyData;
					            var content = item.series.label + " = " + item.datapoint[1];
					            
					            for (var i = 0; i < someData.length; i++)
					            {
					                if (someData[i].label == item.series.label)
					                {					                	
					                    continue;   
					                }
					                
					                for (var j=0; j < someData[i].data.length; j++)
					                {
					                    if (someData[i].data[j][0] == item.datapoint[0] && someData[i].data[j][1] == item.datapoint[1])
					                  	{
					                          content += '<br/>' + someData[i].label + " = " + item.datapoint[1]; 
					                    }
					                }                
					            }					            
					            
					            showTooltip(item.pageX, item.pageY, content);
					        }
					        else 
					        {
					            $("#tooltip").css('display','none');       
					        }
				});	
					
			});	
		
		$("#months").click(function () 
		{
			
			plot = $.plot("#placeholder", monthlyData, monthlyOptions);
			
			overview = $.plot("#overview", monthlyDataOverview, overviewOptionsMonth);
			
			$("#placeholder").bind("plothover", function (event, pos, item) 
			{
			 	if (item) 
			 	{
			 		var someData = monthlyData;
		            var content = item.series.label + " = " + item.datapoint[1];
		            
		            for (var i = 0; i < someData.length; i++)
		            {
		                if (someData[i].label == item.series.label)
		                {					                	
		                    continue;   
		                }
		                
		                for (var j=0; j < someData[i].data.length; j++)
		                {
		                    if (someData[i].data[j][0] == item.datapoint[0] && someData[i].data[j][1] == item.datapoint[1])
		                  	{
		                          content += '<br/>' + someData[i].label + " = " + item.datapoint[1]; 
		                    }
		                }                
		            }					            
		            
		            showTooltip(item.pageX, item.pageY, content);
		        }
		        else 
		        {
		            $("#tooltip").css('display','none');       
		        }
			});					
		});
		
	
		$("<div id='tooltip'></div>").css({
			position: "absolute",
			display: "none",
			border: "1px solid #fdd",
			padding: "2px",
			"background-color": "#fee",
			opacity: 0.80
		}).appendTo("body");
		
		function showTooltip(x, y, contents) 
		{
	        $('#tooltip').html(contents);
	        $('#tooltip').css({
	            top: y + 5,
	            left: x + 5,
	            display: 'block'});
	    }				 
};
