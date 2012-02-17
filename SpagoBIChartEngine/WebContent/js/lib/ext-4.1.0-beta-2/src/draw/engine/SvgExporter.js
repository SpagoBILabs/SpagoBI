Ext.define('Ext.draw.engine.SvgExporter', {
    singleton: true,
   
    statics: (function(){ 
        var surface, len, width,
        height,
        init = function(s){
            surface = s;
            len = surface.length,
            width = surface.width,
            height = surface.height;
        },
        spriteProcessor = {
            path: function(sprite){  
               
                var attr = sprite.attr,
                    path = attr.path,
                    pathString = '', props;
                
                if(Ext.isArray(path[0])){
                    Ext.each(path, function(p){
                        pathString += p.join(' ');
                    });
                }else if(Ext.isArray(path)){
                    pathString = path.join(' ');
                }else{
                    pathString = path.replace(/,/g,' ');
                }

                props = toPropertyString({
                    d: pathString,
                    fill: attr.fill || 'none',
                    stroke: attr.stroke,
                    'fill-opacity': attr.opacity,
                    'stroke-width': attr['stroke-width'],
                    'stroke-opacity': attr['stroke-opacity'],
                    transform: sprite.matrix.toSvg()    
                });

                return '<path ' + props + '/>';
            },
            text: function(sprite){
                
                // TODO
                // implement multi line support (@see Svg.js tuneText)
                
                var attr = sprite.attr,
                    fontRegex = /(-?\d*\.?\d*){1}(em|ex|px|in|cm|mm|pt|pc|%)\s('*.*'*)/,
                    match = fontRegex.exec(attr.font),
                    size = (match && match[1]) || "12",
                    // default font family is Arial
                    family = (match && match[3]) || 'Arial',
                    text = attr.text,
                    factor = (Ext.isFF3_0 || Ext.isFF3_5) ? 2 : 4,
                    bbox = sprite.getBBox(),
                    tspanString = '',
                    props;

                tspanString += '<tspan x="' + (attr.x || '') + '" dy="';
                tspanString += (size/factor)+'">';
                tspanString += Ext.htmlEncode(text) + '</tspan>';


                props = toPropertyString({
                        x: attr.x,
                        y: attr.y,
                        'font-size': size,
                        'font-family': family,
                        'font-weight': attr['font-weight'],
                        'text-anchor': attr['text-anchor'],
                        // if no fill property is set it will be black
                        fill: attr.fill || '#000',
                        'fill-opacity': attr.opacity,
                        transform: sprite.matrix.toSvg()
                });

                    
                    
                return '<text '+ props + '>' +  tspanString + '</text>';
            },
            rect: function(sprite){

                var attr = sprite.attr,
                    props =  toPropertyString({
                        x: attr.x,
                        y: attr.y,
                        rx: attr.rx,
                        ry: attr.ry,
                        width: attr.width,
                        height: attr.height,
                        fill: attr.fill || 'none',
                        'fill-opacity': attr.opacity,
                        stroke: attr.stroke,
                        'stroke-opacity': attr['stroke-opacity'],
                        'stroke-width':attr['stroke-width'],
                        transform: sprite.matrix && sprite.matrix.toSvg()
                    });
                
                return '<rect ' + props + '/>';
            },
            circle: function(sprite){

                var attr = sprite.attr,                
                    props = toPropertyString({
                        cx: attr.x,
                        cy: attr.y,
                        r: attr.radius,
                        fill: attr.translation.fill || attr.fill || 'none',
                        'fill-opacity': attr.opacity,
                        stroke: attr.stroke,
                        'stroke-opacity': attr['stroke-opacity'],
                        'stroke-width':attr['stroke-width'],
                        transform: sprite.matrix.toSvg()
                    });

                return '<circle ' + props + ' />';
            },
            image: function(sprite){

                var attr = sprite.attr,
                    props = toPropertyString({
                        x: attr.x - (attr.width/2 >> 0),
                        y: attr.y - (attr.height/2 >> 0),
                        width: attr.width,
                        height: attr.height,
                        'xlink:href': attr.src,
                        transform: sprite.matrix.toSvg()
                    });

                return '<image ' + props + ' />';
            }
        },
        svgHeader = function(){
            var svg = '<?xml version="1.0" standalone="yes"?>';
            svg += '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">';
            return svg;
        },
        svgContent = function(){
            var svg = '<svg width="'+width+'px" height="'+height+'px" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" version="1.1">',
                defs = '', item, itemsLen, items, gradient,
                groups, groupsLen, group, getSvgString, colorstops, stop;

            items = surface.items.items;
            itemsLen = items.length;
           
            
            getSvgString = function(node){
                      
                var childs = node.childNodes,
                    childLength = childs.length,
                    svgString = '', child, attr, tagName, attrItem;

                    for(var i=0; i < childLength; i++){
                        child = childs[i],
                        attr = child.attributes,
                        tagName = child.tagName;
                        
                        svgString += '<' +tagName;
                        
                        for(var o=0, attrLength = attr.length; o < attrLength; o++){
                            attrItem = attr.item(o);
                            svgString += ' '+attrItem.name+'="'+attrItem.value+'"';
                        }
                        
                        svgString += '>';
                        
                        if(child.childNodes.length > 0){
                            svgString += getSvgString(child);
                        }
                        
                        svgString += '</' + tagName + '>';
                        
                    }
                return svgString;
            };
            
            
            if(surface.getDefs){
                defs = getSvgString(surface.getDefs());
            }else{
                // IE
                surface.gradientsColl && surface.gradientsColl.eachKey(function(key){
                   
                    gradient = surface.gradientsColl.getByKey(key);
                    defs += '<linearGradient id="' + key + '" x1="0" y1="0" x2="1" y2="1">';
                    
                    colorstops = gradient.colors.split(",");
                    for(var i=0, stopsLen = colorstops.length; i < stopsLen; i++){
                        stop = colorstops[i].split(' ');
                        defs += '<stop offset="'+stop[0]+'" stop-color="'+stop[1]+'" stop-opacity="1"></stop>';
                    }
                    defs += '</linearGradient>';

                });
            }
            
            svg += '<defs>' + defs + '</defs>';

            // thats the background rectangle
            svg += spriteProcessor.rect({
                attr: {
                        width: '100%',
                        height: '100%',
                        fill: '#fff',
                        stroke: 'none',
                        opacity: '0'
                }
            });
                
            for(var o = 0; o < itemsLen; o++){
                item = items[o];
                
                if(!item.attr.hidden){
                    svg += spriteProcessor[item.type](item);
                }
            }

            svg += '</svg>';

            return svg;
        },                
        toPropertyString = function(obj){
            var propString = '';

            for(var key in obj){

                if(obj.hasOwnProperty(key) && obj[key] != null){
                    propString += key +'="'+ obj[key]+'" ';
                }

            }

            return propString;
        };

        return {
            generate: function(config, surface){
                init(surface);
                return svgHeader() + svgContent()
            }
        }
    })()

});