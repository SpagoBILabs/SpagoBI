//<feature charts>
Ext.define('Sbi.chart.userFunctions', {
    singleton: true,
    
    getColors: function(a){
    	return ["#115fa6", "#94ae0a", "#a61120", "#ff8809", "#ffd13e", "#a61187", "#24ad9a", "#7c7474", "#a66111"];
    },
   
    getGradientColorsHSL: function (args ) {
    	var  baseColor =Ext.draw.Color.create(args[0]);
    	var  from =args[1];
    	var  to =args[2];
    	var  number =args[3];
        var hsl = baseColor.getHSL(),
            fromH = 'h' in from ? from.h : hsl[0],
            fromS = 's' in from ? from.s : hsl[1],
            fromL = 'l' in from ? from.l : hsl[2],
            toH = 'h' in to ? to.h : hsl[0],
            toS = 's' in to ? to.s : hsl[1],
            toL = 'l' in to ? to.l : hsl[2],
            i, colors = [],
            deltaH = (toH - fromH) / number,
            deltaS = (toS - fromS) / number,
            deltaL = (toL - fromL) / number;
        for (i = 0; i <= number; i++) {
            colors.push(Ext.draw.Color.fromHSL(
                fromH + deltaH * i,
                fromS + deltaS * i,
                fromL + deltaL * i
            ).toString());
        }
        return colors;
    }
    
    ,fancyGaugeRenderer: function(sprite, config, rendererData, spriteIndex) {
    	var surface = sprite.getParent(),
    	chart = rendererData.series.getChart(),
    	mainRegion = chart.getMainRegion(),
    	width = mainRegion[2],
    	height = mainRegion[3],
    	bigChart = (width >= 250 && height >= 150),
    	changes, fontSize;
    	if (config.type == "label") {
    		changes = {x:config.x+10, y:config.y+10};
    		if (spriteIndex == 3) {
    			Ext.apply(changes, {fontSize:(bigChart?32:16), strokeStyle:'black'});
    		} else {
    			Ext.apply(changes, {fontSize:(bigChart?24:12)});
    		}
    		switch (spriteIndex) {
    		case 1: Ext.apply(changes, {color:'blue'});     break;
    		case 3: Ext.apply(changes, {color:'white'});    break;
    		case 5: Ext.apply(changes, {color:'darkred'});  break;
    		}
    		return changes;
    	}
    }
    ,axisFormatter: function(a,b,c,d){
    	if(a/1000>1){
    		return Math.floor(a/1000)+"K";
    	}else if(a/1000000>1){
    		return Math.floor(a/1000000)+"M";
    	}else if(a/1000000000>1){
    		return Math.floor(a/1000000000)+"G";
    	}else if(a/1000000000000>1){
    		return Math.floor(a/1000000000000)+"T";
    	}    		
    	
    	return a;
    }
});
