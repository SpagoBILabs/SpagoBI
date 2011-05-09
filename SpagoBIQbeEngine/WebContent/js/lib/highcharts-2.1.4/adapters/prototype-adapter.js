/*
 Highcharts JS v2.1.4 (2011-03-02)
 Prototype adapter

 @author Michael Nelson, Torstein H?nsi.

 Feel free to use and modify this script.
 Highcharts license: www.highcharts.com/license.
*/
var HighchartsAdapter=function(){var f=typeof Effect!="undefined";return{init:function(){if(f)Effect.HighchartsTransition=Class.create(Effect.Base,{initialize:function(a,b,c,d){var e;this.element=a;e=a.attr(b);if(b=="d"){this.paths=Highcharts.pathAnim.init(a,a.d,c);this.toD=c;e=0;c=1}this.start(Object.extend(d||{},{from:e,to:c,attribute:b}))},setup:function(){HighchartsAdapter._extend(this.element);this.element._highchart_animation=this},update:function(a){var b=this.paths;if(b)a=Highcharts.pathAnim.step(b[0],
b[1],a,this.toD);this.element.attr(this.options.attribute,a)},finish:function(){this.element._highchart_animation=null}})},addEvent:function(a,b,c){if(a.addEventListener||a.attachEvent)Event.observe($(a),b,c);else{HighchartsAdapter._extend(a);a._highcharts_observe(b,c)}},animate:function(a,b,c){var d;c=c||{};c.delay=0;c.duration=(c.duration||500)/1E3;if(f)for(d in b)new Effect.HighchartsTransition($(a),d,b[d],c);else for(d in b)a.attr(d,b[d]);if(!a.attr)throw"Todo: implement animate DOM objects";
},stop:function(a){a._highcharts_extended&&a._highchart_animation&&a._highchart_animation.cancel()},each:function(a,b){$A(a).each(b)},fireEvent:function(a,b,c,d){if(b.preventDefault)d=null;if(a.fire)a.fire(b,c);else a._highcharts_extended&&a._highcharts_fire(b,c);d&&d(c)},removeEvent:function(a,b,c){if($(a).stopObserving)a.stopObserving(a,b,c);else{HighchartsAdapter._extend(a);a._highcharts_stop_observing(b,c)}},grep:function(a,b){return a.findAll(b)},hyphenate:function(a){return a.replace(/([A-Z])/g,
function(b,c){return"-"+c.toLowerCase()})},map:function(a,b){return a.map(b)},merge:function(){function a(b,c){var d,e;for(e in c){d=c[e];b[e]=d&&typeof d=="object"&&d.constructor!=Array?a(b[e]||{},d):c[e]}return b}return function(){for(var b=arguments,c={},d=0;d<b.length;d++)c=a(c,b[d]);return c}.apply(this,arguments)},_extend:function(a){a._highcharts_extended||Object.extend(a,{_highchart_events:{},_highchart_animation:null,_highcharts_extended:true,_highcharts_observe:function(b,c){this._highchart_events[b]=
[this._highchart_events[b],c].compact().flatten()},_highcharts_stop_observing:function(b,c){this._highchart_events[b]=[this._highchart_events[b]].compact().flatten().without(c)},_highcharts_fire:function(b,c){(this._highchart_events[b]||[]).each(function(d){c&&c.stopped||d.bind(this)(c)}.bind(this))}})}}}();
