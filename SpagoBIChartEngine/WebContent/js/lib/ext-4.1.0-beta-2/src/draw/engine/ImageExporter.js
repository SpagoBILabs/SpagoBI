Ext.define('Ext.draw.engine.ImageExporter', {
    singleton: true,
   
    statics: (function(){
        var exportTypes = {
            "image/png": 1,
            "image/jpeg": 1
            },
            init = function(config){
            
                if(config.hasOwnProperty('width')){
                    width = config['width'];
                }
                if(config.hasOwnProperty('height')){
                    height = config['height'];
                }
                if(config.hasOwnProperty('type') && exportTypes[config['type']]){
                    type = config['type'];
                }else{
                    return false;
                }
                
                // if all the elements were set up before
                // we don't need to reset their values and reappend them to the form
                if(formEl && svgEl && typeEl && widthEl && heightEl){
                    return true;
                }
                
                formEl = formEl || Ext.get(document.createElement('form'));
                formEl.set({
                    action: 'http://svg.sencha.io',
                    method: 'POST'
                });
                
                svgEl = svgEl || Ext.get(document.createElement('input'));
                svgEl.set({
                    name: 'svg',
                    type: 'hidden'
                });
                
                typeEl = typeEl || Ext.get(document.createElement('input'));
                typeEl.set({ 
                    name: 'type',
                    type: 'hidden'
                });
                widthEl = widthEl || Ext.get(document.createElement('input'));
                widthEl.set({
                    name: 'width',
                    type: 'hidden'
                });
                heightEl = heightEl || Ext.get(document.createElement('input'));
                heightEl.set({
                    name: 'height',
                    type: 'hidden'
                });
                
                formEl.appendChild(svgEl);
                formEl.appendChild(typeEl);
                formEl.appendChild(widthEl);
                formEl.appendChild(heightEl);
                
                Ext.getBody().appendChild(formEl);
                
                return true;
            },
            process = function(surface){

                var svgString = Ext.draw.engine.SvgExporter.self.generate({}, surface);

                widthEl.set({
                    value: width || surface.width
                });
                
                heightEl.set({
                    value: height || surface.height
                });
 
                if(type){
                    typeEl.set({
                        value: type
                    });
                }
                svgEl.set({
                    value: svgString
                });
                
                formEl.dom.submit();
                
            },
            formEl, typeEl, svgEl, widthEl, heightEl, type, width, height;

        return {
            generate: function(config, surface){
                if(init(config)){
                    process(surface);
                }else{
                    return false;
                }
            }
        }
    })()

});
