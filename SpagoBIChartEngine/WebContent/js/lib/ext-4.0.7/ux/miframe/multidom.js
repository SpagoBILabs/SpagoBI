/* global Ext El ElFrame ELD*/
/*
 * ******************************************************************************
 * This file is distributed on an AS IS BASIS WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * ***********************************************************************************
 * License: multidom.js is offered under an MIT License.
 * Donations are welcomed: http://donate.theactivegroup.com
 */

 /**
  * @class multidom
  * @version 2.14
  * @license MIT
  * @author Doug Hendricks. Forum ID: <a href="http://extjs.com/forum/member.php?u=8730">hendricd</a>
  * @donate <a target="tag_donate" href="http://donate.theactivegroup.com"><img border="0" src="http://www.paypal.com/en_US/i/btn/x-click-butcc-donate.gif" border="0" alt="Make a donation to support ongoing development"></a>
  * @copyright 2007-2010, Active Group, Inc. All rights reserved.
  * @description [Designed For Ext Core and ExtJs Frameworks (using ext-base adapter only) 3.0 or higher ONLY]
  * The multidom library extends (overloads) Ext Core DOM methods and functions to
  * provide document-targeted access to the documents loaded in external (FRAME/IFRAME)
  * documents.
  * <p>It maintains seperate DOM Element caches (and more) for each document instance encountered by the
  * framework, permitting safe access to DOM Elements across document instances that may share
  * the same Element id or name.  In essence, multidom extends the functionality provided by Ext Core
  * into any child document without having to load the Core library into the frame's global context.
  * <h3>Custom Element classes.</h3>
  * The Ext.get method is enhanced to support resolution of the custom Ext.Element implementations.
  * (The ux.ManagedIFrame 2 Element class is an example of such a class.)
  * <p>For example: If you were retrieving the Ext.Element instance for an IFRAME and the class
  * Ext.Element.IFRAME were defined:
  * <pre><code>Ext.get('myFrame')</pre></code>
  * would return an instance of Ext.Element.IFRAME for 'myFrame' if it were found.
  * @example
   // return the Ext.Element with an id 'someDiv' located in external document hosted by 'iframe'
   var iframe = Ext.get('myFrame');
   var div = Ext.get('someDiv', iframe.getFrameDocument()); //Firefox example
   if(div){
     div.center();
    }
   Note: ux.ManagedIFrame provides an equivalent 'get' method of it's own to access embedded DOM Elements
   for the document it manages.
   <pre><code>iframe.get('someDiv').center();</pre></code>

   Likewise, you can retrieve the raw Element of another document with:
   var el = Ext.getDom('myDiv', iframe.getFrameDocument());
 */

 (function(){

    /*
     * Ext.Element and Ext.lib.DOM enhancements.
     * Primarily provides the ability to interact with any document context
     * (not just the one Ext was loaded into).
     */
   var El = Ext.Element,
       ElFrame,
       ELD = Ext.lib.Dom,
       A = Ext.lib.Anim,
       Evm = Ext.EventManager,
       E = Ext.lib.Event,
       DOC = document,
       emptyFn = function(){},
       OP = Object.prototype,
       OPString = OP.toString,
       bodyTag = /^body/i,
       HTMLDoc = '[object HTMLDocument]';
       
   if(!Ext.elCache || parseInt( Ext.version.replace(/\./g,''),10) < 311 ) {
    alert ('Ext Release '+Ext.version+' is not supported');
   }

   /**
    * @private
    */
   Ext._documents= {}; 
   Ext._documents[Ext.id(document,'_doc')]=Ext.elCache;

   /**
    * @private
    * Resolve the Element cache for a given element/window/document context.
    */
   var resolveCache = ELD.resolveDocumentCache = function(el, cacheId){
        
        /**
         * MUST re-assert Ext.elCache !! 
         * because of privately scoped references to Ext.elCache in the framework itself.
         */
        Ext._documents[Ext.id(document,'_doc')]=Ext.elCache;
        
        var doc = GETDOC(el),
            c = Ext.isDocument(doc) ? Ext.id(doc) : cacheId,
            cache = Ext._documents[c] || null;
         
         return cache || (c ? Ext._documents[c] = {}: null);
     },
     clearCache = ELD.clearDocumentCache = function(cacheId){
       delete  Ext._documents[cacheId];
     };

   El.addMethods || ( El.addMethods = function(ov){ Ext.apply(El.prototype, ov||{}); });
   
   Ext.removeNode =  function(n){
         var dom = n ? n.dom || n : null,
             el, elc, elCache = resolveCache(dom), parent;

            //clear out any references if found in the El.cache(s)
            if(dom && (elc = elCache[dom.id]) && (el = elc.el) ){
                if(el.dom){
                    Ext.enableNestedListenerRemoval ? Evm.purgeElement(el.dom, true) : Evm.removeAll(el.dom);
                }
                delete elCache[dom.id];
                delete el.dom;
                delete el._context;
                el = null;
            }
            //No removal for window, documents, or bodies
            if(dom && !dom.navigator && !Ext.isDocument(dom) && !bodyTag.test(dom.tagName)){
                (parent = dom.parentElement || dom.parentNode) && parent.removeChild(dom);
            }
            dom = parent = null;
    };

    var overload = function(pfn, fn ){
           var f = typeof pfn === 'function' ? pfn : function t(){};
           var ov = f._ovl; //call signature hash
           if(!ov){
               ov = { base: f};
               ov[f.length|| 0] = f;
               f= function t(){  //the proxy stub
                  var o = arguments.callee._ovl;
                  var fn = o[arguments.length] || o.base;
                  //recursion safety
                  return fn && fn != arguments.callee ? fn.apply(this,arguments): undefined;
               };
           }
           var fnA = [].concat(fn);
           for(var i=0,l=fnA.length; i<l; ++i){
             //ensures no duplicate call signatures, but last in rules!
             ov[fnA[i].length] = fnA[i];
           }
           f._ovl= ov;
           var t = null;
           return f;
       };

    Ext.applyIf( Ext, {
        overload : overload( overload,
           [
             function(fn){ return overload(null, fn);},
             function(obj, mname, fn){
                 return obj[mname] = overload(obj[mname],fn);}
          ]),

        isArray : function(v){
           return !!v && OPString.apply(v) == '[object Array]';
        },

        isObject:function(obj){
            return !!obj && typeof obj == 'object';
        },

        /**
         * HTMLDocument assertion with optional accessibility testing
         * @param {HTMLELement} el The DOM Element to test
         * @param {Boolean} testOrigin (optional) True to test "same-origin" access
         *
         */
        isDocument : function(el, testOrigin){
            var elm = el ? el.dom || el : null;
            var test = elm && ((OPString.apply(elm) == HTMLDoc) || (elm && elm.nodeType == 9));
            if(test && testOrigin){
                try{
                    test = !!elm.location;
                }
                catch(e){return false;}
            }
            return test;
        },

        isWindow : function(el){
          var elm = el ? el.dom || el : null;
          return elm ? !!elm.navigator || OPString.apply(elm) == "[object Window]" : false;
        },

        isIterable : function(v){
            //check for array or arguments
            if(Ext.isArray(v) || v.callee){
                return true;
            }
            //check for node list type
            if(/NodeList|HTMLCollection/.test(OPString.call(v))){
                return true;
            }
            //NodeList has an item and length property
            //IXMLDOMNodeList has nextNode method, needs to be checked first.
            return ((typeof v.nextNode != 'undefined' || v.item) && Ext.isNumber(v.length));
  
        },
        isElement : function(obj){
            return obj && Ext.type(obj)== 'element';
        },

        isEvent : function(obj){
            return OPString.apply(obj) == '[object Event]' || (Ext.isObject(obj) && !Ext.type(o.constructor) && (window.event && obj.clientX && obj.clientX == window.event.clientX));
        },

        isFunction: function(obj){
            return !!obj && typeof obj == 'function';
        },

        /**
         * Determine whether a specified DOMEvent is supported by a given HTMLElement or Object.
         * @param {String} type The eventName (without the 'on' prefix)
         * @param {HTMLElement/Object/String} testEl (optional) A specific HTMLElement/Object to test against, otherwise a tagName to test against.
         * based on the passed eventName is used, or DIV as default.
         * @return {Boolean} True if the passed object supports the named event.
         */
        isEventSupported : function(evName, testEl){
             var TAGNAMES = {
                  'select':'input',
                  'change':'input',
                  'submit':'form',
                  'reset':'form',
                  'load':'img',
                  'error':'img',
                  'abort':'img'
                },
                //Cached results
                cache = {},
                onPrefix = /^on/i,
                //Get a tokenized string of the form nodeName:type
                getKey = function(type, el){
                    var tEl = Ext.getDom(el);
                    return (tEl ?
                           (Ext.isElement(tEl) || Ext.isDocument(tEl) ?
                                tEl.nodeName.toLowerCase() :
                                    el.self ? '#window' : el || '#object')
                       : el || 'div') + ':' + type;
                };

            return function (evName, testEl) {
              evName = (evName || '').replace(onPrefix,'');
              var el, isSupported = false;
              var eventName = 'on' + evName;
              var tag = (testEl ? testEl : TAGNAMES[evName]) || 'div';
              var key = getKey(evName, tag);

              if(key in cache){
                //Use a previously cached result if available
                return cache[key];
              }

              el = Ext.isString(tag) ? DOC.createElement(tag): testEl;
              isSupported = (!!el && (eventName in el));

              isSupported || (isSupported = window.Event && !!(String(evName).toUpperCase() in window.Event));

              if (!isSupported && el) {
                el.setAttribute && el.setAttribute(eventName, 'return;');
                isSupported = Ext.isFunction(el[eventName]);
              }
              //save the cached result for future tests
              cache[key] = isSupported;
              el = null;
              return isSupported;
            };

        }()
    });


    /**
     * @private
     * Determine Ext.Element[tagName] or Ext.Element (default)
     */
    var assertClass = function(el){
    	
    	return El;
        return El[(el.tagName || '-').toUpperCase()] || El;

      };

    var libFlyweight;
    function fly(el, doc) {
        if (!libFlyweight) {
            libFlyweight = new Ext.Element.Flyweight();
        }
        libFlyweight.dom = Ext.getDom(el, null, doc);
        return libFlyweight;
    }


    Ext.apply(Ext, {
    /*
     * Overload Ext.get to permit Ext.Element access to other document objects
     * This implementation maintains safe element caches for each document queried.
     *
     */

      get : El.get = function(el, doc){         //document targeted
            if(!el ){ return null; }
            var isDoc = Ext.isDocument(el); 
            
            Ext.isDocument(doc) || (doc = DOC);
            
            var ex, elm, id, cache = resolveCache(doc);
            if(typeof el == "string"){ // element id
                elm = Ext.getDom(el, null, doc);
                if(!elm) return null;
                if(cache[el] && cache[el].el){
                    ex = cache[el].el;
                    ex.dom = elm;
                }else{
                    ex = El.addToCache(new (assertClass(elm))(elm, null, doc));
                }
                return ex;
            
            }else if(isDoc){

                if(!Ext.isDocument(el, true)){ return false; }  //is it accessible
                cache = resolveCache(el);

                if(cache[Ext.id(el)] && cache[el.id].el){
                    return cache[el.id].el;
                }
                // create a bogus element object representing the document object
                var f = function(){};
                f.prototype = El.prototype;
                var docEl = new f();
                docEl.dom = el;
                docEl.id = Ext.id(el,'_doc');
                docEl._isDoc = true;
                El.addToCache( docEl, null, cache);
                return docEl;
                        
             }else if( el instanceof El ){ 
                
                // refresh dom element in case no longer valid,
                // catch case where it hasn't been appended
                 
                if(el.dom){
                    el.id = Ext.id(el.dom);
                }else{
                    el.dom = el.id ? Ext.getDom(el.id, true) : null;
                }
                if(el.dom){
	                cache = resolveCache(el);
	                (cache[el.id] || 
	                       (cache[el.id] = {data : {}, events : {}}
	                       )).el = el; // in case it was created directly with Element(), let's cache it
                }
                return el;
                
            }else if(el.tagName || Ext.isWindow(el)){ // dom element
                cache = resolveCache(el);
                id = Ext.id(el);
                if(cache[id] && (ex = cache[id].el)){
                    ex.dom = el;
                }else{
                    ex = El.addToCache(new (assertClass(el))(el, null, doc), null, cache); 
                }
                return ex;

            }else if(el.isComposite){
                return el;

            }else if(Ext.isArray(el)){
                return Ext.get(doc,doc).select(el);
            }
           return null;

    },

     /**
      * Ext.getDom to support targeted document contexts
      */
     getDom : function(el, strict, doc){
        var D = doc || DOC;
        if(!el || !D){
            return null;
        }
        if (el.dom){
            return el.dom;
        } else {
            if (Ext.isString(el)) {
                var e = D.getElementById(el);
                // IE returns elements with the 'name' and 'id' attribute.
                // we do a strict check to return the element with only the id attribute
                if (e && Ext.isIE && strict) {
                    if (el == e.getAttribute('id')) {
                        return e;
                    } else {
                        return null;
                    }
                }
                return e;
            } else {
                return el;
            }
        }
            
     },
     /**
     * Returns the current/specified document body as an {@link Ext.Element}.
     * @param {HTMLDocument} doc (optional)
     * @return Ext.Element The document's body
     */
     getBody : function(doc){
            var D = ELD.getDocument(doc) || DOC;
            return Ext.get(D.body || D.documentElement);
       },

     getDoc :Ext.overload([
       Ext.getDoc,
       function(doc){ return Ext.get(doc,doc); }
       ])
   });

   // private method for getting and setting element data
    El.data = function(el, key, value){
        el = El.get(el);
        if (!el) {
            return null;
        }
        var c = resolveCache(el)[el.id].data;
        if(arguments.length == 2){
            return c[key];
        }else{
            return (c[key] = value);
        }
    };
    
    El.addToCache = function(el, id, cache ){
        id = id || Ext.id(el);
        var C = cache || resolveCache(el);
        C[id] = {
            el:  el.dom ? el : Ext.get(el),
            data: {},
            events: {}
        };
        var d = C[id].el.dom;
        (d.getElementById || d.navigator) && (C[id].skipGC = true);
        return C[id].el;
    };
    
    El.removeFromCache = function(el, cache){
        if(el && el.id){
            var C = cache || resolveCache(el);
            delete C[el.id];
        }
    };
    
    /*
     * Add new Visibility Mode to element (sets height and width to 0px instead of display:none )
     */
    El.OFFSETS = 3;
    El.ASCLASS = 4;
    
    El.visibilityCls = 'x-hide-nosize';

    var propCache = {},
        camelRe = /(-[a-z])/gi,
        camelFn = function(m, a){ return a.charAt(1).toUpperCase(); },
        opacityRe = /alpha\(opacity=(.*)\)/i,
        trimRe = /^\s+|\s+$/g,
        marginRightRe = /marginRight/,
        propFloat = Ext.isIE ? 'styleFloat' : 'cssFloat',
        view = DOC.defaultView,
        VISMODE = 'visibilityMode',
        ASCLASS  = "asclass",
        ORIGINALDISPLAY = 'originalDisplay',
        PADDING = "padding",
        MARGIN = "margin",
        BORDER = "border",
        LEFT = "-left",
        RIGHT = "-right",
        TOP = "-top",
        BOTTOM = "-bottom",
        WIDTH = "-width",
        MATH = Math,
        OPACITY = "opacity",
        VISIBILITY = "visibility",
        DISPLAY = "display",
        OFFSETS = "offsets",
        NOSIZE = 'nosize',
        ASCLASS  = "asclass",
        HIDDEN = "hidden",
        NONE = "none", 
        ISVISIBLE = 'isVisible',
        ISCLIPPED = 'isClipped',
        OVERFLOW = 'overflow',
        OVERFLOWX = 'overflow-x',
        OVERFLOWY = 'overflow-y',
        ORIGINALCLIP = 'originalClip',
        XMASKED = "x-masked",
        XMASKEDRELATIVE = "x-masked-relative",
        // special markup used throughout Ext when box wrapping elements
        borders = {l: BORDER + LEFT + WIDTH, r: BORDER + RIGHT + WIDTH, t: BORDER + TOP + WIDTH, b: BORDER + BOTTOM + WIDTH},
        paddings = {l: PADDING + LEFT, r: PADDING + RIGHT, t: PADDING + TOP, b: PADDING + BOTTOM},
        margins = {l: MARGIN + LEFT, r: MARGIN + RIGHT, t: MARGIN + TOP, b: MARGIN + BOTTOM},
        data = El.data,
        GETDOM = Ext.getDom,
        GET = Ext.get,
        DH = Ext.DomHelper,
        propRe = /^(?:scope|delay|buffer|single|stopEvent|preventDefault|stopPropagation|normalized|args|delegate)$/,
        CSS = Ext.util.CSS,  //Not available in Ext Core.
        getDisplay = function(dom){
            var d = data(dom, ORIGINALDISPLAY);
            if(d === undefined){
                data(dom, ORIGINALDISPLAY, d = '');
            }
            return d;
        },
        getVisMode = function(dom){
            var m = data(dom, VISMODE);
            if(m === undefined){
                data(dom, VISMODE, m = El.prototype.visibilityMode)
            }
            return m;
        };

    function chkCache(prop) {
        return propCache[prop] || (propCache[prop] = prop == 'float' ? propFloat : prop.replace(camelRe, camelFn));
    };


    El.addMethods({
        /**
         * Resolves the current document context of this Element
         */
        getDocument : function(){
           return this._context || (this._context = GETDOC(this));
        },

        /**
      * Removes this element from the DOM and deletes it from the cache
      * @param {Boolean} cleanse (optional) Perform a cleanse of immediate childNodes as well.
      * @param {Boolean} deep (optional) Perform a deep cleanse of all nested childNodes as well.
      */

        remove : function(cleanse, deep){
            
          var dom = this.dom;
          //this.isMasked() && this.unmask();
          if(dom){
            Ext.removeNode(dom);
            delete this._context;
            delete this.dom;
          }
        },

         /**
         * Appends the passed element(s) to this element
         * @param {String/HTMLElement/Array/Element/CompositeElement} el
         * @param {Document} doc (optional) specific document context for the Element search
         * @return {Ext.Element} this
         */
        appendChild: function(el, doc){
            return GET(el, doc || this.getDocument()).appendTo(this);
        },

        /**
         * Appends this element to the passed element
         * @param {Mixed} el The new parent element
         * @param {Document} doc (optional) specific document context for the Element search
         * @return {Ext.Element} this
         */
        appendTo: function(el, doc){
            GETDOM(el, false, doc || this.getDocument()).appendChild(this.dom);
            return this;
        },

        /**
         * Inserts this element before the passed element in the DOM
         * @param {Mixed} el The element before which this element will be inserted
         * @param {Document} doc (optional) specific document context for the Element search
         * @return {Ext.Element} this
         */
        insertBefore: function(el, doc){
            (el = GETDOM(el, false, doc || this.getDocument())).parentNode.insertBefore(this.dom, el);
            return this;
        },

        /**
         * Inserts this element after the passed element in the DOM
         * @param {Mixed} el The element to insert after
         * @param {Document} doc (optional) specific document context for the Element search
         * @return {Ext.Element} this
         */
        insertAfter: function(el, doc){
            (el = GETDOM(el, false, doc || this.getDocument())).parentNode.insertBefore(this.dom, el.nextSibling);
            return this;
        },

        /**
         * Inserts (or creates) an element (or DomHelper config) as the first child of this element
         * @param {Mixed/Object} el The id or element to insert or a DomHelper config to create and insert
         * @param {Document} doc (optional) specific document context for the Element search
         * @return {Ext.Element} The new child
         */
        insertFirst: function(el, returnDom){
            el = el || {};
            if(el.nodeType || el.dom || typeof el == 'string'){ // element
                el = GETDOM(el);
                this.dom.insertBefore(el, this.dom.firstChild);
                return !returnDom ? GET(el) : el;
            }else{ // dh config
                return this.createChild(el, this.dom.firstChild, returnDom);
            }
        },

        /**
         * Replaces the passed element with this element
         * @param {Mixed} el The element to replace
         * @param {Document} doc (optional) specific document context for the Element search
         * @return {Ext.Element} this
         */
        replace: function(el, doc){
            el = GET(el, doc || this.getDocument());
            this.insertBefore(el);
            el.remove();
            return this;
        },

        /**
         * Replaces this element with the passed element
         * @param {Mixed/Object} el The new element or a DomHelper config of an element to create
         * @param {Document} doc (optional) specific document context for the Element search
         * @return {Ext.Element} this
         */
        replaceWith: function(el, doc){
            var me = this;
            if(el.nodeType || el.dom || typeof el == 'string'){
                el = GETDOM(el, false, doc || me.getDocument());
                me.dom.parentNode.insertBefore(el, me.dom);
            }else{
                el = DH.insertBefore(me.dom, el);
            }
            var C = resolveCache(me);
            Ext.removeNode(me.dom);
            me.id = Ext.id(me.dom = el);

            El.addToCache(me.isFlyweight ? new (assertClass(me.dom))(me.dom, null, C) : me);     
            return me;
        },


        /**
         * Inserts an html fragment into this element
         * @param {String} where Where to insert the html in relation to this element - beforeBegin, afterBegin, beforeEnd, afterEnd.
         * @param {String} html The HTML fragment
         * @param {Boolean} returnEl (optional) True to return an Ext.Element (defaults to false)
         * @return {HTMLElement/Ext.Element} The inserted node (or nearest related if more than 1 inserted)
         */
        insertHtml : function(where, html, returnEl){
            var el = DH.insertHtml(where, this.dom, html);
            return returnEl ? Ext.get(el, GETDOC(el)) : el;
        },
             
        
        /**
         * Checks whether the element is currently visible using both visibility and display properties.
         * @return {Boolean} True if the element is currently visible, else false
         */
        isVisible : function(deep) {
            var me=this,
                dom = me.dom,
                p = dom.parentNode,
                visible = data(dom, ISVISIBLE);  //use the cached value if registered
               
            if(typeof visible != 'boolean'){ 
               
	            //Determine the initial state based on display states
	            visible = !me.hasClass(me.visibilityCls || El.visibilityCls) && 
	                      !me.isStyle(VISIBILITY, HIDDEN) && 
	                      !me.isStyle(DISPLAY, NONE); 
	                      
	            data(dom, ISVISIBLE, visible);
            }
            
                
            if(deep !== true || !visible){
                return visible;
            }
            while(p && !bodyTag.test(p.tagName)){
                if(!Ext.fly(p, '_isVisible').isVisible()){
                    return false;
                }
                p = p.parentNode;
            }
            return true;
            
        },
                
        /**
         * Sets the visibility of the element (see details). If the visibilityMode is set to Element.DISPLAY, it will use
         * the display property to hide the element, otherwise it uses visibility. The default is to hide and show using the visibility property.
         * @param {Boolean} visible Whether the element is visible
         * @param {Boolean/Object} animate (optional) True for the default animation, or a standard Element animation config object, or one of four
         *         possible hideMode strings: 'display, visibility, offsets, asclass'
         * @return {Ext.Element} this
         */
        setVisible : function(visible, animate){
            var me = this, 
                dom = me.dom,
                visMode = getVisMode(dom);
           
            // hideMode string override
            if (typeof animate == 'string'){
                switch (animate) {
                    case DISPLAY:
                        visMode = El.DISPLAY;
                        break;
                    case VISIBILITY:
                        visMode = El.VISIBILITY;
                        break;
                    case OFFSETS:
                        visMode = El.OFFSETS;
                        break;
                    case NOSIZE:
                    case ASCLASS:
                        visMode = El.ASCLASS;
                        break;
                }
                me.setVisibilityMode(visMode);
                animate = false;
            }
             
            if (!animate || !me.anim) {
                if(visMode == El.ASCLASS ){
                    
                    me[visible?'removeClass':'addClass'](me.visibilityCls || El.visibilityCls);
                    
                } else if (visMode == El.DISPLAY){
                    
                    return me.setDisplayed(visible);
                    
                } else if (visMode == El.OFFSETS){
                    
                    if (!visible){
                        me.hideModeStyles = {
                            position: me.getStyle('position'),
                            top: me.getStyle('top'),
                            left: me.getStyle('left')
                        };
                        me.applyStyles({position: 'absolute', top: '-10000px', left: '-10000px'});
                    } else {
                        me.applyStyles(me.hideModeStyles || {position: '', top: '', left: ''});
                        delete me.hideModeStyles;
                    }
                
                }else{
                    me.fixDisplay();
                    dom.style.visibility = visible ? "visible" : HIDDEN;
                }
            }else{
                // closure for composites            
                if(visible){
                    me.setOpacity(.01);
                    me.setVisible(true);
                }
                me.anim({opacity: { to: (visible?1:0) }},
                        me.preanim(arguments, 1),
                        null,
                        .35,
                        'easeIn',
                        function(){
                            visible || me.setVisible(false).setOpacity(1);
                        });
            }
            data(dom, ISVISIBLE, visible);  //set logical visibility state
            return me;
        },
        
        hasMetrics  : function(){
            var me = this;
            return me.isVisible() || (getVisMode(me.dom) == El.VISIBILITY);     
        },
        /**
         * Sets the CSS display property. Uses originalDisplay if the specified value is a boolean true.
         * @param {Mixed} value Boolean value to display the element using its default display, or a string to set the display directly.
         * @return {Ext.Element} this
         */
        setDisplayed : function(value) {
            var dom = this.dom,
                visMode = getVisMode(dom);
            
            if(typeof value == "boolean"){
               
               if(visMode == El.ASCLASS){
                  return this.setVisible(value);
               }
               data(this.dom, ISVISIBLE, value);
               value = value ? getDisplay(dom) : NONE;
            }
            this.setStyle(DISPLAY, value);
            return this;
        },
        
                
        /**
         * Convenience method for setVisibilityMode(Element.DISPLAY)
         * @param {String} display (optional) What to set display to when visible
         * @return {Ext.Element} this
         */
        enableDisplayMode : function(display){      
            this.setVisibilityMode(El.DISPLAY);
            if(!Ext.isEmpty(display)){
                data(this.dom, ORIGINALDISPLAY, display);
            }
            return this;
        },
        
        
        scrollIntoView : function(container, hscroll){
                var d = this.getDocument(),
                    c = Ext.getDom(container, null, d) || Ext.getBody(d).dom,
                    el = this.dom,
                    o = this.getOffsetsTo(c),
                    l = o[0] + c.scrollLeft,
		            t = o[1] + c.scrollTop,
		            b = t + el.offsetHeight,
		            r = l + el.offsetWidth,
		            ch = c.clientHeight,
		            ct = parseInt(c.scrollTop, 10),
		            cl = parseInt(c.scrollLeft, 10),
		            cb = ct + ch,
		            cr = cl + c.clientWidth;
                    
                if(el.offsetHeight > ch || t < ct){
                    c.scrollTop = t;
                }else if(b > cb){
                    c.scrollTop = b-ch;
                }
                // corrects IE, other browsers will ignore
                c.scrollTop = c.scrollTop; 
                if(hscroll !== false){
                    if(el.offsetWidth > c.clientWidth || l < cl){
                        c.scrollLeft = l;
                    }else if(r > cr){
                        c.scrollLeft = r-c.clientWidth;
                    }
                    c.scrollLeft = c.scrollLeft;
                }
                return this;
        },

        contains : function(el){
            try {
                return !el ? false : ELD.isAncestor(this.dom, el.dom ? el.dom : el);
            } catch(e) {
                return false;
            }
        },

        /**
         * Returns the current scroll position of the element.
         * @return {Object} An object containing the scroll position in the format {left: (scrollLeft), top: (scrollTop)}
         */
        getScroll : function(){
            var d = this.dom,
            doc = this.getDocument(),
            body = doc.body,
            docElement = doc.documentElement,
            l,
            t,
            ret;

            if(Ext.isDocument(d) || d == body){
                if(Ext.isIE && ELD.docIsStrict(doc)){
                    l = docElement.scrollLeft;
                    t = docElement.scrollTop;
                }else{
                    l = window.pageXOffset;
                    t = window.pageYOffset;
                }
                ret = {left: l || (body ? body.scrollLeft : 0), top: t || (body ? body.scrollTop : 0)};
            }else{
                ret = {left: d.scrollLeft, top: d.scrollTop};
            }
            return ret;
        },
        /**
         * Normalizes currentStyle and computedStyle.
         * @param {String} property The style property whose value is returned.
         * @return {String} The current value of the style property for this element.
         */
        getStyle : function(){
            var getStyle =
             view && view.getComputedStyle ?
                function GS(prop){
                    var el = !this._isDoc ? this.dom : null,
                        v,
                        cs,
                        out,
                        display,
                        wk = Ext.isWebKit,
                        display,
                        style;

                    if(!el || !el.style) return null;
                    style = el.style;
                    prop = chkCache(prop);
                    cs = view.getComputedStyle(el, null);
                    out = (cs) ? cs[prop]: null;
                           
                    // Fix bug caused by this: https://bugs.webkit.org/show_bug.cgi?id=13343
                    if(wk){
                        if(out && marginRightRe.test(prop) &&
                            style.position != 'absolute' && 
                            out != '0px'){
		                        display = style.display;
		                        style.display = 'inline-block';
		                        out = view.getComputedStyle(el, null)[prop];
		                        style.display = display;
	                    }else if(out == 'rgba(0, 0, 0, 0)'){
                            //Webkit returns rgb values for transparent.
	                        out = 'transparent';
	                    }
                    }
                    return out || style[prop];
                } :
                function GS(prop){ //IE < 9
                   var el = !this._isDoc ? this.dom : null,
                        m,
                        cs,
                        style;
                    if(!el || !el.style) return null;
                    style = el.style;
                    if (prop == OPACITY ) {
                        if (style.filter.match) {
                            if(m = style.filter.match(opacityRe)){
                                var fv = parseFloat(m[1]);
                                if(!isNaN(fv)){
                                    return fv ? fv / 100 : 0;
                                }
                            }
                        }
                        return 1;
                    }
                    prop = chkCache(prop);
                    return ((cs = el.currentStyle) ? cs[prop] : null) || el.style[prop];
                };
                var GS = null;
                return getStyle;
        }(),
        /**
         * Wrapper for setting style properties, also takes single object parameter of multiple styles.
         * @param {String/Object} property The style property to be set, or an object of multiple styles.
         * @param {String} value (optional) The value to apply to the given property, or null if an object was passed.
         * @return {Ext.Element} this
         */
        setStyle : function(prop, value){
            if(this._isDoc || Ext.isDocument(this.dom)) return this;
            var tmp, style;
                
            if (typeof prop != 'object') {
                tmp = {};
                tmp[prop] = value;
                prop = tmp;
            }
            for (style in prop) {
                if(prop.hasOwnProperty(style)) {
                    value = prop[style];
	                style == OPACITY ?
	                    this.setOpacity(value) :
	                    this.dom.style[chkCache(style)] = value;
                }
            }
            return this;
        },
        /**
        * Centers the Element in either the viewport, or another Element.
        * @param {Mixed} centerIn (optional) The element in which to center the element.
        */
        center : function(centerIn){
            return this.alignTo(centerIn || this.getDocument(), 'c-c');
        },
        
        /**
         * Puts a mask over this element to disable user interaction. Requires core.css.
         * This method can only be applied to elements which accept child nodes.
         * @param {String} msg (optional) A message to display in the mask
         * @param {String} msgCls (optional) A css class to apply to the msg element
         * @return {Element} The mask element
         */
        mask : function(msg, msgCls){
            var me = this,
                dom = me.dom,
                dh = Ext.DomHelper,
                EXTELMASKMSG = "ext-el-mask-msg",
                el, 
                mask;
                
            if(me.getStyle("position") == "static"){
                me.addClass(XMASKEDRELATIVE);
            }
            if((el = data(dom, 'maskMsg'))){
                el.remove();
            }
            if((el = data(dom, 'mask'))){
                el.remove();
            }
    
            mask = dh.append(dom, {cls : "ext-el-mask"}, true);
            data(dom, 'mask', mask);
    
            me.addClass(XMASKED);
            mask.setDisplayed(true);
            if(typeof msg == 'string'){
                var mm = dh.append(dom, {cls : EXTELMASKMSG, cn:{tag:'div'}}, true);
                data(dom, 'maskMsg', mm);
                mm.dom.className = msgCls ? EXTELMASKMSG + " " + msgCls : EXTELMASKMSG;
                mm.dom.firstChild.innerHTML = msg;
                mm.setDisplayed(true);
                mm.center(me);
            }
            if(Ext.isIE && !(Ext.isIE7 && Ext.isStrict) && me.getStyle('height') == 'auto'){ // ie will not expand full height automatically
                mask.setSize(undefined, me.getHeight());
            }
            return mask;
        },
    
        /**
         * Removes a previously applied mask.
         */
        unmask : function(){
            var me = this,
                dom = me.dom,
                mask = data(dom, 'mask'),
                maskMsg = data(dom, 'maskMsg');
            if(mask){
                if(maskMsg){
                    maskMsg.remove();
                    data(dom, 'maskMsg', undefined);
                }
                mask.remove();
                data(dom, 'mask', undefined);
            }
            me.removeClass([XMASKED, XMASKEDRELATIVE]);
        },
        
        /**
         * Returns true if this element is masked
         * @return {Boolean}
         */
        isMasked : function(){
            var m = data(this.dom, 'mask');
            return m && m.isVisible();
        },

        /**
        * Calculates the x, y to center this element on the screen
        * @return {Array} The x, y values [x, y]
        */
        getCenterXY : function(){
            return this.getAlignToXY(this.getDocument(), 'c-c');
        },
        /**
         * Gets the x,y coordinates specified by the anchor position on the element.
         * @param {String} anchor (optional) The specified anchor position (defaults to "c").  See {@link #alignTo}
         * for details on supported anchor positions.
         * @param {Boolean} local (optional) True to get the local (element top/left-relative) anchor position instead
         * of page coordinates
         * @param {Object} size (optional) An object containing the size to use for calculating anchor position
         * {width: (target width), height: (target height)} (defaults to the element's current size)
         * @return {Array} [x, y] An array containing the element's x and y coordinates
         */
        getAnchorXY : function(anchor, local, s){
            //Passing a different size is useful for pre-calculating anchors,
            //especially for anchored animations that change the el size.
            anchor = (anchor || "tl").toLowerCase();
            s = s || {};

            var me = this,  doc = this.getDocument(),
                vp = me.dom == doc.body || me.dom == doc,
                w = s.width || vp ? ELD.getViewWidth(false,doc) : me.getWidth(),
                h = s.height || vp ? ELD.getViewHeight(false,doc) : me.getHeight(),
                xy,
                r = Math.round,
                o = me.getXY(),
                scroll = me.getScroll(),
                extraX = vp ? scroll.left : !local ? o[0] : 0,
                extraY = vp ? scroll.top : !local ? o[1] : 0,
                hash = {
                    c  : [r(w * .5), r(h * .5)],
                    t  : [r(w * .5), 0],
                    l  : [0, r(h * .5)],
                    r  : [w, r(h * .5)],
                    b  : [r(w * .5), h],
                    tl : [0, 0],
                    bl : [0, h],
                    br : [w, h],
                    tr : [w, 0]
                };

            xy = hash[anchor];
            return [xy[0] + extraX, xy[1] + extraY];
        },

        /**
         * Anchors an element to another element and realigns it when the window is resized.
         * @param {Mixed} element The element to align to.
         * @param {String} position The position to align to.
         * @param {Array} offsets (optional) Offset the positioning by [x, y]
         * @param {Boolean/Object} animate (optional) True for the default animation or a standard Element animation config object
         * @param {Boolean/Number} monitorScroll (optional) True to monitor body scroll and reposition. If this parameter
         * is a number, it is used as the buffer delay (defaults to 50ms).
         * @param {Function} callback The function to call after the animation finishes
         * @return {Ext.Element} this
         */
        anchorTo : function(el, alignment, offsets, animate, monitorScroll, callback){
            var me = this,
                dom = me.dom;

            function action(){
                fly(dom).alignTo(el, alignment, offsets, animate);
                Ext.callback(callback, fly(dom));
            };

            Ext.EventManager.onWindowResize(action, me);

            if(!Ext.isEmpty(monitorScroll)){
                Ext.EventManager.on(window, 'scroll', action, me,
                    {buffer: !isNaN(monitorScroll) ? monitorScroll : 50});
            }
            action.call(me); // align immediately
            return me;
        },

        /**
         * Returns the current scroll position of the element.
         * @return {Object} An object containing the scroll position in the format {left: (scrollLeft), top: (scrollTop)}
         */
        getScroll : function(){
            var d = this.dom,
                doc = this.getDocument(),
                body = doc.body,
                docElement = doc.documentElement,
                l,
                t,
                ret;

            if(d == doc || d == body){
                if(Ext.isIE && ELD.docIsStrict(doc)){
                    l = docElement.scrollLeft;
                    t = docElement.scrollTop;
                }else{
                    l = window.pageXOffset;
                    t = window.pageYOffset;
                }
                ret = {left: l || (body ? body.scrollLeft : 0), top: t || (body ? body.scrollTop : 0)};
            }else{
                ret = {left: d.scrollLeft, top: d.scrollTop};
            }
            return ret;
        },

        /**
         * Gets the x,y coordinates to align this element with another element. See {@link #alignTo} for more info on the
         * supported position values.
         * @param {Mixed} element The element to align to.
         * @param {String} position The position to align to.
         * @param {Array} offsets (optional) Offset the positioning by [x, y]
         * @return {Array} [x, y]
         */
        getAlignToXY : function(el, p, o){
            var doc;
            el = Ext.get(el, doc = this.getDocument());

            if(!el || !el.dom){
                throw "Element.getAlignToXY with an element that doesn't exist";
            }

            o = o || [0,0];
            p = (p == "?" ? "tl-bl?" : (!/-/.test(p) && p != "" ? "tl-" + p : p || "tl-bl")).toLowerCase();

            var me = this,
                d = me.dom,
                a1,
                a2,
                x,
                y,
                //constrain the aligned el to viewport if necessary
                w,
                h,
                r,
                dw = ELD.getViewWidth(false,doc) -10, // 10px of margin for ie
                dh = ELD.getViewHeight(false,doc)-10, // 10px of margin for ie
                p1y,
                p1x,
                p2y,
                p2x,
                swapY,
                swapX,
                docElement = doc.documentElement,
                docBody = doc.body,
                scrollX = (docElement.scrollLeft || docBody.scrollLeft || 0)+5,
                scrollY = (docElement.scrollTop || docBody.scrollTop || 0)+5,
                c = false, //constrain to viewport
                p1 = "",
                p2 = "",
                m = p.match(/^([a-z]+)-([a-z]+)(\?)?$/);

            if(!m){
               throw "Element.getAlignToXY with an invalid alignment " + p;
            }

            p1 = m[1];
            p2 = m[2];
            c = !!m[3];

            //Subtract the aligned el's internal xy from the target's offset xy
            //plus custom offset to get the aligned el's new offset xy
            a1 = me.getAnchorXY(p1, true);
            a2 = el.getAnchorXY(p2, false);

            x = a2[0] - a1[0] + o[0];
            y = a2[1] - a1[1] + o[1];

            if(c){
               w = me.getWidth();
               h = me.getHeight();
               r = el.getRegion();
               //If we are at a viewport boundary and the aligned el is anchored on a target border that is
               //perpendicular to the vp border, allow the aligned el to slide on that border,
               //otherwise swap the aligned el to the opposite border of the target.
               p1y = p1.charAt(0);
               p1x = p1.charAt(p1.length-1);
               p2y = p2.charAt(0);
               p2x = p2.charAt(p2.length-1);
               swapY = ((p1y=="t" && p2y=="b") || (p1y=="b" && p2y=="t"));
               swapX = ((p1x=="r" && p2x=="l") || (p1x=="l" && p2x=="r"));


               if (x + w > dw + scrollX) {
                    x = swapX ? r.left-w : dw+scrollX-w;
               }
               if (x < scrollX) {
                   x = swapX ? r.right : scrollX;
               }
               if (y + h > dh + scrollY) {
                    y = swapY ? r.top-h : dh+scrollY-h;
                }
               if (y < scrollY){
                   y = swapY ? r.bottom : scrollY;
               }
            }

            return [x,y];
        },
            // private ==>  used outside of core
        adjustForConstraints : function(xy, parent, offsets){
            return this.getConstrainToXY(parent || this.getDocument(), false, offsets, xy) ||  xy;
        },

        // private ==>  used outside of core
        getConstrainToXY : function(el, local, offsets, proposedXY){
            var os = {top:0, left:0, bottom:0, right: 0};

            return function(el, local, offsets, proposedXY){
                var doc = this.getDocument();
                el = Ext.get(el, doc);
                offsets = offsets ? Ext.applyIf(offsets, os) : os;

                var vw, vh, vx = 0, vy = 0;
                if(el.dom == doc.body || el.dom == doc){
                    vw = ELD.getViewWidth(false,doc);
                    vh = ELD.getViewHeight(false,doc);
                }else{
                    vw = el.dom.clientWidth;
                    vh = el.dom.clientHeight;
                    if(!local){
                        var vxy = el.getXY();
                        vx = vxy[0];
                        vy = vxy[1];
                    }
                }

                var s = el.getScroll();

                vx += offsets.left + s.left;
                vy += offsets.top + s.top;

                vw -= offsets.right;
                vh -= offsets.bottom;

                var vr = vx + vw,
                    vb = vy + vh,
                    xy = proposedXY || (!local ? this.getXY() : [this.getLeft(true), this.getTop(true)]);
                    x = xy[0], y = xy[1],
                    offset = this.getConstrainOffset(),
                    w = this.dom.offsetWidth + offset, 
                    h = this.dom.offsetHeight + offset;

                // only move it if it needs it
                var moved = false;

                // first validate right/bottom
                if((x + w) > vr){
                    x = vr - w;
                    moved = true;
                }
                if((y + h) > vb){
                    y = vb - h;
                    moved = true;
                }
                // then make sure top/left isn't negative
                if(x < vx){
                    x = vx;
                    moved = true;
                }
                if(y < vy){
                    y = vy;
                    moved = true;
                }
                return moved ? [x, y] : false;
            };
        }(),
        
        // private, used internally
	    getConstrainOffset : function(){
	        return 0;
	    },
	    
        /**
        * Calculates the x, y to center this element on the screen
        * @return {Array} The x, y values [x, y]
        */
        getCenterXY : function(){
            return this.getAlignToXY(Ext.getBody(this.getDocument()), 'c-c');
        },
       
        /**
        * Centers the Element in either the viewport, or another Element.
        * @param {Mixed} centerIn (optional) The element in which to center the element.
        */
        center : function(centerIn){
            return this.alignTo(centerIn || Ext.getBody(this.getDocument()), 'c-c');
        } ,

        /**
         * Looks at this node and then at parent nodes for a match of the passed simple selector (e.g. div.some-class or span:first-child)
         * @param {String} selector The simple selector to test
         * @param {Number/Mixed} maxDepth (optional) The max depth to search as a number or element (defaults to 50 || document.body)
         * @param {Boolean} returnEl (optional) True to return a Ext.Element object instead of DOM node
         * @return {HTMLElement} The matching DOM node (or null if no match was found)
         */
        findParent : function(simpleSelector, maxDepth, returnEl){
            var p = this.dom,
                D = this.getDocument(),
                b = D.body,
                depth = 0,
                stopEl;
            if(Ext.isGecko && OPString.call(p) == '[object XULElement]') {
                return null;
            }
            maxDepth = maxDepth || 50;
            if (isNaN(maxDepth)) {
                stopEl = Ext.getDom(maxDepth, null, D);
                maxDepth = Number.MAX_VALUE;
            }
            while(p && p.nodeType == 1 && depth < maxDepth && p != b && p != stopEl){
                if(Ext.DomQuery.is(p, simpleSelector)){
                    return returnEl ? Ext.get(p, D) : p;
                }
                depth++;
                p = p.parentNode;
            }
            return null;
        },
        /**
         *  Store the current overflow setting and clip overflow on the element - use <tt>{@link #unclip}</tt> to remove
         * @return {Ext.Element} this
         */
        clip : function(){
            var me = this,
                dom = me.dom;
                
            if(!data(dom, ISCLIPPED)){
                data(dom, ISCLIPPED, true);
                data(dom, ORIGINALCLIP, {
                    o: me.getStyle(OVERFLOW),
                    x: me.getStyle(OVERFLOWX),
                    y: me.getStyle(OVERFLOWY)
                });
                me.setStyle(OVERFLOW, HIDDEN);
                me.setStyle(OVERFLOWX, HIDDEN);
                me.setStyle(OVERFLOWY, HIDDEN);
            }
            return me;
        },
    
        /**
         *  Return clipping (overflow) to original clipping before <tt>{@link #clip}</tt> was called
         * @return {Ext.Element} this
         */
        unclip : function(){
            var me = this,
                dom = me.dom;
                
            if(data(dom, ISCLIPPED)){
                data(dom, ISCLIPPED, false);
                var o = data(dom, ORIGINALCLIP);
                if(o.o){
                    me.setStyle(OVERFLOW, o.o);
                }
                if(o.x){
                    me.setStyle(OVERFLOWX, o.x);
                }
                if(o.y){
                    me.setStyle(OVERFLOWY, o.y);
                }
            }
            return me;
        },
        
        getViewSize : function(){
            var doc = this.getDocument(),
                d = this.dom,
                isDoc = (d == doc || d == doc.body);

            // If the body, use Ext.lib.Dom
            if (isDoc) {
                var extdom = Ext.lib.Dom;
                return {
                    width : extdom.getViewWidth(),
                    height : extdom.getViewHeight()
                }

            // Else use clientHeight/clientWidth
            } else {
                return {
                    width : d.clientWidth,
                    height : d.clientHeight
                }
            }
        },
        /**
        * <p>Returns the dimensions of the element available to lay content out in.<p>
        *
        * getStyleSize utilizes prefers style sizing if present, otherwise it chooses the larger of offsetHeight/clientHeight and offsetWidth/clientWidth.
        * To obtain the size excluding scrollbars, use getViewSize
        *
        * Sizing of the document body is handled at the adapter level which handles special cases for IE and strict modes, etc.
        */

        getStyleSize : function(){
            var me = this,
                w, h,
                doc = this.getDocument(),
                d = this.dom,
                isDoc = (d == doc || d == doc.body),
                s = d.style;

            // If the body, use Ext.lib.Dom
            if (isDoc) {
                var extdom = Ext.lib.Dom;
                return {
                    width : extdom.getViewWidth(),
                    height : extdom.getViewHeight()
                }
            }
            // Use Styles if they are set
            if(s.width && s.width != 'auto'){
                w = parseFloat(s.width);
                if(me.isBorderBox()){
                   w -= me.getFrameWidth('lr');
                }
            }
            // Use Styles if they are set
            if(s.height && s.height != 'auto'){
                h = parseFloat(s.height);
                if(me.isBorderBox()){
                   h -= me.getFrameWidth('tb');
                }
            }
            // Use getWidth/getHeight if style not set.
            return {width: w || me.getWidth(true), height: h || me.getHeight(true)};
        }
    });
   
    Ext.apply(ELD , {
        /**
         * Resolve the current document context of the passed Element
         */
        getDocument : function(el, accessTest){
          var dom= null;
          try{
            dom = Ext.getDom(el, null, null); //will fail if El.dom is non "same-origin" document
          }catch(ex){}

          var isDoc = Ext.isDocument(dom);
          if(isDoc){
            if(accessTest){
                return Ext.isDocument(dom, accessTest) ? dom : null;
            }
            return dom;
          }
          return dom ?
                dom.ownerDocument ||  //Element
                dom.document //Window
                : null;
        },

        /**
         * Return the Compatability Mode of the passed document or Element
         */
        docIsStrict : function(doc){
            return (Ext.isDocument(doc) ? doc : this.getDocument(doc)).compatMode == "CSS1Compat";
        },

        getViewWidth : Ext.overload ([
           ELD.getViewWidth || function(full){},
            function() { return this.getViewWidth(false);},
            function(full, doc) {
                return full ? this.getDocumentWidth(doc) : this.getViewportWidth(doc);
            }]
         ),

        getViewHeight : Ext.overload ([
            ELD.getViewHeight || function(full){},
            function() { return this.getViewHeight(false);},
            function(full, doc) {
                return full ? this.getDocumentHeight(doc) : this.getViewportHeight(doc);
            }]),

        getDocumentHeight: Ext.overload([
           ELD.getDocumentHeight || emptyFn,
           function(doc) {
            if(doc=this.getDocument(doc)){
              return Math.max(
                 !this.docIsStrict(doc) ? doc.body.scrollHeight : doc.documentElement.scrollHeight
                 , this.getViewportHeight(doc)
                 );
            }
            return undefined;
           }
         ]),

        getDocumentWidth: Ext.overload([
           ELD.getDocumentWidth || emptyFn,
           function(doc) {
              if(doc=this.getDocument(doc)){
                return Math.max(
                 !this.docIsStrict(doc) ? doc.body.scrollWidth : doc.documentElement.scrollWidth
                 , this.getViewportWidth(doc)
                 );
              }
              return undefined;
            }
        ]),

        getViewportHeight: Ext.overload([
           ELD.getViewportHeight || emptyFn,
           function(doc){
             if(doc=this.getDocument(doc)){
                if(Ext.isIE){
                    return this.docIsStrict(doc) ? doc.documentElement.clientHeight : doc.body.clientHeight;
                }else{
                    return doc.defaultView.innerHeight;
                }
             }
             return undefined;
           }
        ]),

        getViewportWidth: Ext.overload([
           ELD.getViewportWidth || emptyFn,
           function(doc) {
              if(doc=this.getDocument(doc)){
                return !this.docIsStrict(doc) && !Ext.isOpera ? doc.body.clientWidth :
                   Ext.isIE ? doc.documentElement.clientWidth : doc.defaultView.innerWidth;
              }
              return undefined;
            }
        ]),

        getXY : Ext.overload([
            ELD.getXY || emptyFn,
            function(el, doc) {
                if(typeof el=='string'){
	                el = Ext.getDom(el, null, doc);
	                var D= this.getDocument(el),
	                    bd = D ? (D.body || D.documentElement): null;
	
	                if(!el || !bd || el == bd){ return [0, 0]; }
                }
                return this.getXY(el);
            }
          ])
    });

    var GETDOC = ELD.getDocument,
        flies = El._flyweights;

    /**
     * @private
     * Add Ext.fly support for targeted document contexts
     */
    
    Ext.fly = El.fly = function(el, named, doc){
        var ret = null;
        named = named || '_global';

        if (el = Ext.getDom(el, null, doc)) {
            (ret = flies[named] = (flies[named] || new El.Flyweight())).dom = el;
            Ext.isDocument(el) && (ret._isDoc = true);
        }
        return ret;
    };

    var flyFn = function(){};
    flyFn.prototype = El.prototype;

    // dom is optional
    El.Flyweight = function(dom){
       this.dom = dom;
    };

    El.Flyweight.prototype = new flyFn();
    El.Flyweight.prototype.isFlyweight = true;
    
    function addListener(el, ename, fn, task, wrap, scope){
        el = Ext.getDom(el);
        if(!el){ return; }

        var id = Ext.id(el),
            cache = resolveCache(el);
            cache[id] || El.addToCache(el, id, cache);
            
         var es = cache[id].events || {}, wfn;

        wfn = E.on(el, ename, wrap);
        es[ename] = es[ename] || [];
        es[ename].push([fn, wrap, scope, wfn, task]);

        // this is a workaround for jQuery and should somehow be removed from Ext Core in the future
        // without breaking ExtJS.
        if(el.addEventListener && ename == "mousewheel" ){ 
            var args = ["DOMMouseScroll", wrap, false];
            el.addEventListener.apply(el, args);
            Ext.EventManager.addListener(window, 'beforeunload', function(){
                el.removeEventListener.apply(el, args);
            });
        }
        if(ename == "mousedown" && DOC == el){ // fix stopped mousedowns on the document
            Ext.EventManager.stoppedMouseDownEvent.addListener(wrap);
        }
    };

    function createTargeted(h, o){
        return function(){
            var args = Ext.toArray(arguments);
            if(o.target == Ext.EventObject.setEvent(args[0]).target){
                h.apply(this, args);
            }
        };
    };

    function createBuffered(h, o, task){
        return function(e){
            // create new event object impl so new events don't wipe out properties
            task.delay(o.buffer, h, null, [new Ext.EventObjectImpl(e)]);
        };
    };

    function createSingle(h, el, ename, fn, scope){
        return function(e){
            Ext.EventManager.removeListener(el, ename, fn, scope);
            h(e);
        };
    };

    function createDelayed(h, o, fn){
        return function(e){
            var task = new Ext.util.DelayedTask(h);
            (fn.tasks || (fn.tasks = [])).push(task);
            task.delay(o.delay || 10, h, null, [new Ext.EventObjectImpl(e)]);
        };
    };

    function listen(element, ename, opt, fn, scope){
        var o = !Ext.isObject(opt) ? {} : opt,
            el = Ext.getDom(element), task;

        fn = fn || o.fn;
        scope = scope || o.scope;

        if(!el){
            throw "Error listening for \"" + ename + '\". Element "' + element + '" doesn\'t exist.';
        }
        function h(e){
            // prevent errors while unload occurring
            if(!window.Ext){ return; }
            e = Ext.EventObject.setEvent(e);
            var t;
            if (o.delegate) {
                if(!(t = e.getTarget(o.delegate, el))){
                    return;
                }
            } else {
                t = e.target;
            }
            if (o.stopEvent) {
                e.stopEvent();
            }
            if (o.preventDefault) {
               e.preventDefault();
            }
            if (o.stopPropagation) {
                e.stopPropagation();
            }
            if (o.normalized) {
                e = e.browserEvent;
            }

            fn.call(scope || el, e, t, o);
        };
        if(o.target){
            h = createTargeted(h, o);
        }
        if(o.delay){
            h = createDelayed(h, o, fn);
        }
        if(o.single){
            h = createSingle(h, el, ename, fn, scope);
        }
        if(o.buffer){
            task = new Ext.util.DelayedTask(h);
            h = createBuffered(h, o, task);
        }

        addListener(el, ename, fn, task, h, scope);
        return h;
    };

    Ext.apply(Evm ,{
         addListener : Evm.on = function(element, eventName, fn, scope, options){
            if(typeof eventName == 'object'){
                var o = eventName, e, val;
                for(e in o){
                    if(!o.hasOwnProperty(e)) {
                        continue;
                    }
                    val = o[e];
                    if(!propRe.test(e)){
                        if(Ext.isFunction(val)){
                            // shared options
                            listen(element, e, o, val, o.scope);
                        }else{
                            // individual options
                            listen(element, e, val);
                        }
                    }
                }
            } else {
                listen(element, eventName, options, fn, scope);
            }
        },

        /**
         * Removes an event handler from an element.  The shorthand version {@link #un} is equivalent.  Typically
         * you will use {@link Ext.Element#removeListener} directly on an Element in favor of calling this version.
         * @param {String/HTMLElement} el The id or html element from which to remove the listener.
         * @param {String} eventName The name of the event.
         * @param {Function} fn The handler function to remove. <b>This must be a reference to the function passed into the {@link #addListener} call.</b>
         * @param {Object} scope If a scope (<b><code>this</code></b> reference) was specified when the listener was added,
         * then this must refer to the same object.
         */
        removeListener : Evm.un = function(element, eventName, fn, scope){
            var el = Ext.getDom(element);
            el && Ext.get(el);
            var elCache = el ? resolveCache(el) : {},
                f = el && ((elCache[el.id]||{events:{}}).events)[eventName] || [],
                wrap, i, l, k, len, fnc, evs;

            for (i = 0, len = f.length; i < len; i++) {
                /* 0 = Original Function,
                   1 = Event Manager Wrapped Function,
                   2 = Scope,
                   3 = Adapter Wrapped Function,
                   4 = Buffered Task
                */
                if (Ext.isArray(fnc = f[i]) && fnc[0] == fn && (!scope || fnc[2] == scope)) {
                    fnc[4] && fnc[4].cancel();
                    k = fn.tasks && fn.tasks.length;
                    if(k) {
                        while(k--) {
                            fn.tasks[k].cancel();
                        }
                        delete fn.tasks;
                    }
                    wrap = fnc[1];
                    E.un(el, eventName, E.extAdapter ? fnc[3] : wrap);
                    
                    // jQuery workaround that should be removed from Ext Core
                    if(wrap && eventName == "mousewheel" && el.addEventListener ){
                        el.removeEventListener("DOMMouseScroll", wrap, false);
                    }
        
                    if(wrap && eventName == "mousedown" && DOC == el){ // fix stopped mousedowns on the document
                        Ext.EventManager.stoppedMouseDownEvent.removeListener(wrap);
                    }
                    
                    f.splice(i,1);
                    if (f.length === 0) {
                        delete elCache[el.id].events[eventName];
                    }
                    evs = elCache[el.id].events;
                    for (k in evs) {
                        if(evs.hasOwnProperty(k)) {
	                         return false;
	                    }
                    }
                    elCache[el.id].events = {};
                    return false;
                }
            }

            
        },

        /**
         * Removes all event handers from an element.  Typically you will use {@link Ext.Element#removeAllListeners}
         * directly on an Element in favor of calling this version.
         * @param {String/HTMLElement} el The id or html element from which to remove all event handlers.
         */
        removeAll : function(el){
            if (!(el = Ext.getDom(el))) {
                return;
            }
            var id = el.id,
                elCache = resolveCache(el)||{},
                es = elCache[id] || {},
                ev = es.events || {},
                f, i, len, ename, fn, k, wrap;

            for(ename in ev){
                if(ev.hasOwnProperty(ename)){
                    f = ev[ename];
                    /* 0 = Original Function,
                       1 = Event Manager Wrapped Function,
                       2 = Scope,
                       3 = Adapter Wrapped Function,
                       4 = Buffered Task
                    */
                    for (i = 0, len = f.length; i < len; i++) {
                        fn = f[i];
                        fn[4] && fn[4].cancel();
                        if(fn[0] && fn[0].tasks && (k = fn[0].tasks.length)) {
                            while(k--) {
                                fn[0].tasks[k].cancel();
                            }
                            delete fn.tasks;
                        }
                        
                        wrap =  fn[1];
                        E.un(el, ename, E.extAdapter ? fn[3] : wrap);

                        // jQuery workaround that should be removed from Ext Core
                        if(wrap && el.addEventListener && ename == "mousewheel"){
                            el.removeEventListener("DOMMouseScroll", wrap, false);
                        }

                        // fix stopped mousedowns on the document
                        if(wrap && (DOC == el) && ename == "mousedown"){
                            Ext.EventManager.stoppedMouseDownEvent.removeListener(wrap);
                        }
                    }
                }
            }
            elCache[id] && (elCache[id].events = {});
        },

        getListeners : function(el, eventName) {
            el = Ext.getDom(el);
            if (!el) {
                return;
            }
            var id = (Ext.get(el)||{}).id,
                elCache = resolveCache(el),
                es = ( elCache[id] || {} ).events || {};

            return es[eventName] || null;
        },

        purgeElement : function(el, recurse, eventName) {
            el = Ext.getDom(el);
            var id = Ext.id(el),
                elCache = resolveCache(el),
                es = (elCache[id] || {}).events || {},
                i, f, len;
            if (eventName) {
                if (es.hasOwnProperty(eventName)) {
                    f = es[eventName];
                    for (i = 0, len = f.length; i < len; i++) {
                        Evm.removeListener(el, eventName, f[i][0]);
                    }
                }
            } else {
                Evm.removeAll(el);
            }
            if (recurse && el && el.childNodes) {
                for (i = 0, len = el.childNodes.length; i < len; i++) {
                    Evm.purgeElement(el.childNodes[i], recurse, eventName);
                }
            }
        }
    });
    
    // deprecated, call from EventManager
    E.getListeners = function(el, eventName) {
       return Ext.EventManager.getListeners(el, eventName);
    };

    /** @sourceURL=<multidom.js> */
    Ext.provide && Ext.provide('multidom');
 })();