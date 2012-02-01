/* global Ext */
/*
 * Copyright 2007-2010, Active Group, Inc.  All rights reserved.
 * ******************************************************************************
 * This file is distributed on an AS IS BASIS WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * ***********************************************************************************
 * @version 2.1.4
 * [For Ext 3.1.1 or higher only]
 *
 * License: ux.ManagedIFrame, ux.ManagedIFrame.Panel, ux.ManagedIFrame.Portlet, ux.ManagedIFrame.Window  
 * are licensed under the terms of the Open Source GPL 3.0 license:
 * http://www.gnu.org/licenses/gpl.html
 *
 * Commercial use is prohibited without a Commercial Developement License. See
 * http://licensing.theactivegroup.com.
 *
 * Donations are welcomed: http://donate.theactivegroup.com
 *
 */
 
(function(){
    
    var El = Ext.Element, 
        ElFrame, 
        ELD = Ext.lib.Dom,
        EMPTYFN = function(){},
        OP = Object.prototype,
        addListener = function () {
            var handler;
            if (window.addEventListener) {
                handler = function F(el, eventName, fn, capture) {
                    el.addEventListener(eventName, fn, !!capture);
                };
            } else if (window.attachEvent) {
                handler = function F(el, eventName, fn, capture) {
                    el.attachEvent("on" + eventName, fn);
                };
            } else {
                handler = function F(){};
            }
            var F = null; //Gbg collect
            return handler;
        }(),
       removeListener = function() {
            var handler;
            if (window.removeEventListener) {
                handler = function F(el, eventName, fn, capture) {
                    el.removeEventListener(eventName, fn, (capture));
                };
            } else if (window.detachEvent) {
                handler = function F(el, eventName, fn) {
                    el.detachEvent("on" + eventName, fn);
                };
            } else {
                handler = function F(){};
            }
            var F = null; //Gbg collect
            return handler;
        }();
 
  //assert multidom support: REQUIRED for Ext 3 or higher!
  if(typeof ELD.getDocument != 'function'){
     alert("MIF 2.1.4 requires multidom support" );
  }
  //assert Ext 3.1.1+ 
  if(!Ext.elCache || parseInt( Ext.version.replace(/\./g,''),10) < 311 ) {
    alert ('Ext Release '+Ext.version+' is not supported');
   }
  
  Ext.ns('Ext.ux.ManagedIFrame', 'Ext.ux.plugin');
  
  var MIM, MIF = Ext.ux.ManagedIFrame, MIFC;
  var frameEvents = ['documentloaded',
                     'domready',
                     'focus',
                     'blur',
                     'resize',
                     'scroll',
                     'unload',
                     'scroll',
                     'exception', 
                     'message',
                     'reset'];
                     
    var reSynthEvents = new RegExp('^('+frameEvents.join('|')+ ')', 'i');

    /**
     * @class Ext.ux.ManagedIFrame.Element
     * @extends Ext.Element
     * @version 2.1.4 
     * @license <a href="http://www.gnu.org/licenses/gpl.html">GPL 3.0</a> 
     * @author Doug Hendricks. Forum ID: <a href="http://extjs.com/forum/member.php?u=8730">hendricd</a> 
     * @donate <a target="tag_donate" href="http://donate.theactivegroup.com"><img border="0" src="http://www.paypal.com/en_US/i/btn/x-click-butcc-donate.gif" border="0" alt="Make a donation to support ongoing development"></a>
     * @copyright 2007-2010, Active Group, Inc. All rights reserved.
     * @constructor Create a new Ext.ux.ManagedIFrame.Element directly. 
     * @param {String/HTMLElement} element
     * @param {Boolean} forceNew (optional) By default the constructor checks to see if there is already an instance of this element in the cache and if there is it returns the same instance. This will skip that check (useful for extending this class).
     * @param {DocumentElement} (optional) Document context uses to resolve an Element search by its id.
     */
     
    Ext.ux.ManagedIFrame.Element = Ext.extend(Ext.Element, {
                         
            constructor : function(element, forceNew, doc ){
                var d = doc || document,
                	elCache  = ELD.resolveDocumentCache(d),
                    dom = Ext.getDom(element, false, d);
                
                if(!dom || !(/^(iframe|frame)/i).test(dom.tagName)) { // invalid id/element
                    return null;
                }
                var id = Ext.id(dom);
                
                /**
                 * The DOM element
                 * @type HTMLElement
                 */
                this.dom = dom;
                
                /**
                 * The DOM element ID
                 * @type String
                 */
                this.id = id ;
                
                (elCache[id] || 
                   (elCache[id] = {
                     el: this,
                     events : {},
                     data : {}
                    })
                ).el = this;
                
                this.dom.name || (this.dom.name = this.id);
                 
                if(Ext.isIE){
                     document.frames && (document.frames[this.dom.name] || (document.frames[this.dom.name] = this.dom));
                 }
                 
                this.dom.ownerCt = this;
                MIM.register(this);

                if(!this._observable){
	                    (this._observable = new Ext.util.Observable()).addEvents(
	                    
	                    /**
	                     * Fires when the iFrame has reached a loaded/complete state.
	                     * @event documentloaded
	                     * @param {Ext.ux.MIF.Element} this
	                     */
	                    'documentloaded',
	                    
	                    /**
	                     * Fires ONLY when an iFrame's Document(DOM) has reach a
	                     * state where the DOM may be manipulated ('same origin' policy)
	                     * Note: This event is only available when overwriting the iframe
	                     * document using the update or load methods and "same-origin"
	                     * documents. Returning false from the eventHandler stops further event
	                     * (documentloaded) processing.
	                     * @event domready 
	                     * @param {Ext.ux.MIF.Element} this
	                     */
	
	                    'domready',
	                    
	                    /**
	                     * Fires when the frame actions raise an error
	                     * @event exception
	                     * @param {Ext.ux.MIF.Element} this.iframe
	                     * @param {Error/string} exception
	                     */
	                     'exception',
	                     
	                    /**
	                     * Fires when the frame's window is resized.  This event, when raised from a "same-origin" frame,
	                     * will send current height/width reports with the event.
	                     * @event resize
	                     * @param {Ext.ux.MIF.Element} this.iframe
	                     * @param {Object} documentSize A height/width object signifying the new document size
	                     * @param {Object} viewPortSize A height/width object signifying the size of the frame's viewport
	                     * @param {Object} viewSize A height/width object signifying the size of the frame's view
	                     */
	                     'resize',
	                     
	                    /**
	                     * Fires upon receipt of a message generated by window.sendMessage
	                     * method of the embedded Iframe.window object
	                     * @event message
	                     * @param {Ext.ux.MIF} this.iframe
	                     * @param {object}
	                     *            message (members: type: {string} literal "message", data
	                     *            {Mixed} [the message payload], domain [the document domain
	                     *            from which the message originated ], uri {string} the
	                     *            document URI of the message sender source (Object) the
	                     *            window context of the message sender tag {string} optional
	                     *            reference tag sent by the message sender
	                     * <p>Alternate event handler syntax for message:tag filtering Fires upon
	                     * receipt of a message generated by window.sendMessage method which
	                     * includes a specific tag value of the embedded Iframe.window object
	                     */
	                    'message',
	
	                    /**
	                     * Fires when the frame is blurred (loses focus).
	                     * @event blur
	                     * @param {Ext.ux.MIF} this
	                     * @param {Ext.Event}
	                     *            Note: This event is only available when overwriting the
	                     *            iframe document using the update method and to pages
	                     *            retrieved from a "same domain". Returning false from the
	                     *            eventHandler [MAY] NOT cancel the event, as this event is
	                     *            NOT ALWAYS cancellable in all browsers.
	                     */
	                     'blur',
	
	                    /**
	                     * Fires when the frame gets focus. Note: This event is only available
	                     * when overwriting the iframe document using the update method and to
	                     * pages retrieved from a "same domain". Returning false from the
	                     * eventHandler [MAY] NOT cancel the event, as this event is NOT ALWAYS
	                     * cancellable in all browsers.
	                     * @event focus
	                     * @param {Ext.ux.MIF.Element} this
	                     * @param {Ext.Event}
	                     *
	                    */
	                    'focus',
	
	                    /**
	                     * Note: This event is only available when overwriting the iframe
	                     * document using the update method and to pages retrieved from a "same-origin"
	                     * domain. Note: Opera does not raise this event.
	                     * @event unload * Fires when(if) the frames window object raises the unload event
	                     * @param {Ext.ux.MIF.Element} this.
	                     * @param {Ext.Event}
	                     */
	                     'unload',
	                     
	                     /**
	                     * Note: This event is only available when overwriting the iframe
	                     * document using the update method and to pages retrieved from a "same-origin"
	                     * domain.  To prevent numerous scroll events from being raised use the buffer listener 
	                     * option to limit the number of times the event is raised.
	                     * @event scroll 
	                     * @param {Ext.ux.MIF.Element} this.
	                     * @param {Ext.Event}
	                     */
	                     'scroll',
	                     
	                    /**
	                     * Fires when the iFrame has been reset to a neutral domain state (blank document).
	                     * @event reset
	                     * @param {Ext.ux.MIF.Element} this
	                     */
	                    'reset'
	                 );
	                    //  Private internal document state events.
	                 this._observable.addEvents('_docready','_docload');
                 } 
                 
                 // Hook the Iframes loaded and error state handlers
                 this.on(
                    Ext.isIE? 'readystatechange' : 'load', 
                    this.loadHandler, 
                    this, 
                    /**
                     * Opera still fires LOAD events for images within the FRAME as well,
                     * so we'll buffer hopefully catching one of the later events
                     */ 
                    Ext.isOpera ? {buffer: this.operaLoadBuffer|| 2000} : null
                 );
                 this.on('error', this.loadHandler, this);
            },

            /** @private
             * Removes the MIFElement interface from the FRAME Element.
             * It does NOT remove the managed FRAME from the DOM.  Use the {@link Ext.#ux.ManagedIFrame.Element-remove} method to perfom both functions.
             */
            destructor   :  function () {

                MIM.deRegister(this);
                this.removeAllListeners();
                Ext.destroy(this.frameShim, this.DDM);
                this.hideMask(true);
                delete this.loadMask;
                this.reset(); 
                this.manager = null;
                this.dom.ownerCt = null;
            },
            
            /**
             * Deep cleansing childNode Removal
             * @param {Boolean} forceReclean (optional) By default the element
             * keeps track if it has been cleansed already so
             * you can call this over and over. However, if you update the element and
             * need to force a reclean, you can pass true.
             * @param {Boolean} deep (optional) Perform a deep cleanse of all childNodes as well.
             */
            cleanse : function(forceReclean, deep){
                if(this.isCleansed && forceReclean !== true){
                    return this;
                }
                var d = this.dom, n = d.firstChild, nx;
                while(d && n){
                     nx = n.nextSibling;
                     deep && Ext.fly(n).cleanse(forceReclean, deep);
                     Ext.removeNode(n);
                     n = nx;
                }
                this.isCleansed = true;
                return this;
            },

            /** (read-only) The last known URI set programmatically by the Component
             * @property  
             * @type {String|Function}
             */
            src     : null,

            /** (read-only) For "same-origin" frames only.  Provides a reference to
             * the Ext.util.CSS singleton to manipulate the style sheets of the frame's
             * embedded document.
             *
             * @property
             * @type Ext.util.CSS
             */
            CSS     : null,

            /** Provides a reference to the managing Ext.ux.MIF.Manager instance.
             *
             * @property
             * @type Ext.ux.MIF.Manager
             */
            manager : null,
            
            /**
             * @cfg {Number} operaLoadBuffer Listener buffer time (in milliseconds) to buffer
             * Opera's errant load events (fired for inline images as well) for IFRAMES.
             */
            operaLoadBuffer   : 2000,

            /**
              * @cfg {Boolean} disableMessaging False to enable cross-frame messaging API
              * @default true
              *
              */
            disableMessaging  :  true,

             /**
              * @cfg {Integer} domReadyRetries 
              * Maximum number of domready event detection retries for IE.  IE does not provide
              * a native DOM event to signal when the frames DOM may be manipulated, so a polling process
              * is used to determine when the documents BODY is available. <p> Certain documents may not contain
              * a BODY tag:  eg. MHT(rfc/822), XML, or other non-HTML content. Detection polling will stop after this number of 2ms retries 
              * or when the documentloaded event is raised.</p>
              * @default 7500 (* 2 = 15 seconds) 
              */
            domReadyRetries   :  7500,
            
            /**
             * True to set focus on the frame Window as soon as its document
             * reports loaded.  <p>(Many external sites use IE's document.createRange to create 
             * DOM elements, but to be successful, IE requires that the FRAME have focus before
             * such methods are called)</p>
             * @cfg focusOnLoad
             * @default true if IE
             */
            focusOnLoad   : Ext.isIE,
            
            /**
              * Toggles raising of events for URL actions that the Component did not initiate. 
              * @cfg {Boolean} eventsFollowFrameLinks set true to propogate domready and documentloaded
              * events anytime the IFRAME's URL changes
              * @default true
              */
            eventsFollowFrameLinks   : true,
           

            /**
             * Removes the FRAME from the DOM and deletes it from the cache
             */
            remove  : function(){
                this.destructor.apply(this, arguments);
                ElFrame.superclass.remove.apply(this,arguments);
            },
            
            /**
             * Return the ownerDocument property of the IFRAME Element.
             * (Note: This is not the document context of the FRAME's loaded document. 
             * See the getFrameDocument method for that.)
             */
            getDocument :  
                function(){ return this.dom ? this.dom.ownerDocument : document;},
            
            /**
	         * Loads the frame Element with the response from a form submit to the 
	         * specified URL with the ManagedIframe.Element as it's submit target.
	         *
	         * @param {Object} submitCfg A config object containing any of the following options:
	         * <pre><code>
	         *      myIframe.submitAsTarget({
	         *         form : formPanel.form,  //optional Ext.FormPanel, Ext form element, or HTMLFormElement
	         *         url: &quot;your-url.php&quot;,
             *         action : (see url) ,
	         *         params: {param1: &quot;foo&quot;, param2: &quot;bar&quot;}, // or URL encoded string or function that returns either
	         *         callback: yourFunction,  //optional, called with the signature (frame)
	         *         scope: yourObject, // optional scope for the callback
	         *         method: 'POST', //optional form.method 
             *         encoding : "multipart/form-data" //optional, default = HTMLForm default  
	         *      });
	         *
	         * </code></pre>
             * @return {Ext.ux.ManagedIFrame.Element} this
	         *
	         */
            submitAsTarget : function(submitCfg){
                var opt = submitCfg || {}, 
                D = this.getDocument(),
  	            form = Ext.getDom(
                       opt.form ? opt.form.form || opt.form: null, false, D) || 
                  Ext.DomHelper.append(D.body, { 
                    tag: 'form', 
                    cls : 'x-hidden x-mif-form',
                    encoding : 'multipart/form-data'
                  }),
                formFly = Ext.fly(form, '_dynaForm'),
                formState = {
                    target: form.target || '',
                    method: form.method || '',
                    encoding: form.encoding || '',
                    enctype: form.enctype || '',
                    action: form.action || '' 
                 },
                encoding = opt.encoding || form.encoding,
                method = opt.method || form.method || 'POST';
        
                formFly.set({
                   target  : this.dom.name,
                   method  : method,
                   encoding: encoding,
                   action  : opt.url || opt.action || form.action
                });
                
                if(method == 'POST' || !!opt.enctype){
                    formFly.set({enctype : opt.enctype || form.enctype || encoding});
                }
                
		        var hiddens, hd, ps;
                // add any additional dynamic params
		        if(opt.params && (ps = Ext.isFunction(opt.params) ? opt.params() : opt.params)){ 
		            hiddens = [];
                     
		            Ext.iterate(ps = typeof ps == 'string'? Ext.urlDecode(ps, false): ps, 
                        function(n, v){
		                    Ext.fly(hd = D.createElement('input')).set({
		                     type : 'hidden',
		                     name : n,
		                     value: v
                            });
		                    form.appendChild(hd);
		                    hiddens.push(hd);
		                });
		        }
		
		        opt.callback && 
                    this._observable.addListener('_docready',opt.callback, opt.scope,{single:true});
                     
                this._frameAction = true;
                this._targetURI = location.href;
		        this.showMask();
		        
		        //slight delay for masking
		        (function(){
                    
		            form.submit();
                    // remove dynamic inputs
		            hiddens && Ext.each(hiddens, Ext.removeNode, Ext);

                    //Remove if dynamically generated, restore state otherwise
		            if(formFly.hasClass('x-mif-form')){
                        formFly.remove();
                    }else{
                        formFly.set(formState);
                    }
                    delete El._flyweights['_dynaForm'];
                    formFly = null;
		            this.hideMask(true);
		        }).defer(100, this);
                
                return this;
		    },

            /**
             * @cfg {String} resetUrl Frame document reset string for use with the {@link #Ext.ux.ManagedIFrame.Element-reset} method.
             * Defaults:<p> For IE on SSL domains - the current value of Ext.SSL_SECURE_URL<p> "about:blank" for all others.
             */
            resetUrl : (function(){
                return Ext.isIE && Ext.isSecure ? Ext.SSL_SECURE_URL : 'about:blank';
            })(),

            /**
             * Sets the embedded Iframe src property. Note: invoke the function with
             * no arguments to refresh the iframe based on the current src value.
             *
             * @param {String/Function} url (Optional) A string or reference to a Function that
             *            returns a URI string when called
             * @param {Boolean} discardUrl (Optional) If not passed as <tt>false</tt>
             *            the URL of this action becomes the default SRC attribute
             *            for this iframe, and will be subsequently used in future
             *            setSrc calls (emulates autoRefresh by calling setSrc
             *            without params).
             * @param {Function} callback (Optional) A callback function invoked when the
             *            frame document has been fully loaded.
             * @param {Object} scope (Optional) scope by which the callback function is
             *            invoked.
             */
            setSrc : function(url, discardUrl, callback, scope) {
                var src = url || this.src || this.resetUrl;
                
                var O = this._observable;
                this._unHook();
                Ext.isFunction(callback) && O.addListener('_docload', callback, scope||this, {single:true});
                this.showMask();
                (discardUrl !== true) && (this.src = src);
                var s = this._targetURI = (Ext.isFunction(src) ? src() || '' : src);
                try {
                    this._frameAction = true; // signal listening now
                    this.dom.src = s;
                    this.checkDOM();
                } catch (ex) {
                    O.fireEvent.call(O, 'exception', this, ex);
                }
                return this;
            },

            /**
             * Sets the embedded Iframe location using its replace method (precluding a history update). 
             * Note: invoke the function with no arguments to refresh the iframe based on the current src value.
             *
             * @param {String/Function} url (Optional) A string or reference to a Function that
             *            returns a URI string when called
             * @param {Boolean} discardUrl (Optional) If not passed as <tt>false</tt>
             *            the URL of this action becomes the default SRC attribute
             *            for this iframe, and will be subsequently used in future
             *            setSrc calls (emulates autoRefresh by calling setSrc
             *            without params).
             * @param {Function} callback (Optional) A callback function invoked when the
             *            frame document has been fully loaded.
             * @param {Object} scope (Optional) scope by which the callback function is
             *            invoked.
             *
             */
            setLocation : function(url, discardUrl, callback, scope) {

                var src = url || this.src || this.resetUrl;
                var O = this._observable;
                this._unHook();
                Ext.isFunction(callback) && O.addListener('_docload', callback, scope||this, {single:true});
                this.showMask();
                var s = this._targetURI = (Ext.isFunction(src) ? src() || '' : src);
                if (discardUrl !== true) {
                    this.src = src;
                }
                try {
                    this._frameAction = true; // signal listening now
                    this.getWindow().location.replace(s);
                    this.checkDOM();
                } catch (ex) {
                    O.fireEvent.call(O,'exception', this, ex);
                }
                return this;
            },

            /**
             * Resets the frame to a neutral (blank document) state without
             * loadMasking.
             *
             * @param {String}
             *            src (Optional) A specific reset string (eg. 'about:blank')
             *            to use for resetting the frame.
             * @param {Function}
             *            callback (Optional) A callback function invoked when the
             *            frame reset is complete.
             * @param {Object}
             *            scope (Optional) scope by which the callback function is
             *            invoked.
             */
            reset : function(src, callback, scope) {
                
                this._unHook();
                var loadMaskOff = false,
                    s = src, 
                    win = this.getWindow(),
                    O = this._observable;
                    
                if(this.loadMask){
                    loadMaskOff = this.loadMask.disabled;
                    this.loadMask.disabled = false;
                 }
                this.hideMask(true);
                
                if(win){
                    this.isReset= true;
                    var cb = callback;
	                O.addListener('_docload',
	                  function(frame) {
	                    if(this.loadMask){
	                        this.loadMask.disabled = loadMaskOff;
	                    };
	                    Ext.isFunction(cb) &&  (cb = cb.apply(scope || this, arguments));
                        O.fireEvent("reset", this);
	                }, this, {single:true});
	            
                    Ext.isFunction(s) && ( s = src());
                    s = this._targetURI = Ext.isEmpty(s, true)? this.resetUrl: s;
                    win.location ? (win.location.href = s) : O.fireEvent('_docload', this);
                }
                
                return this;
            },

           /**
            * @private
            * Regular Expression filter pattern for script tag removal.
            * @cfg {regexp} scriptRE script removal RegeXp
            * Default: "/(?:<script.*?>)((\n|\r|.)*?)(?:<\/script>)/gi"
            */
            scriptRE : /(?:<script.*?>)((\n|\r|.)*?)(?:<\/script>)/gi,

            /**
             * Write(replacing) string content into the IFrames document structure
             * @param {String} content The new content
             * @param {Boolean} loadScripts
             * (optional) true to also render and process embedded scripts
             * @param {Function} callback (Optional) A callback function invoked when the
             * frame document has been written and fully loaded. @param {Object}
             * scope (Optional) scope by which the callback function is invoked.
             */
            update : function(content, loadScripts, callback, scope) {
                loadScripts = loadScripts || this.getUpdater().loadScripts || false;
                content = Ext.DomHelper.markup(content || '');
                content = loadScripts === true ? content : content.replace(this.scriptRE, "");
                var doc;
                if ((doc = this.getFrameDocument()) && !!content.length) {
                    this._unHook();
                    this.src = null;
                    this.showMask();
                    Ext.isFunction(callback) &&
                        this._observable.addListener('_docload', callback, scope||this, {single:true});
                    this._targetURI = location.href;
                    doc.open();
                    this._frameAction = true;
                    doc.write(content);
                    doc.close();
                    this.checkDOM();

                } else {
                    this.hideMask(true);
                    Ext.isFunction(callback) && callback.call(scope, this);
                }
                
                return this;
            },
            
            /**
             * Executes a Midas command on the current document, current selection, or the given range.
             * @param {String} command The command string to execute in the frame's document context.
             * @param {Booloean} userInterface (optional) True to enable user interface (if supported by the command)
             * @param {Mixed} value (optional)
             * @param {Boolean} validate If true, the command is validated to ensure it's invocation is permitted.
             * @return {Boolean} indication whether command execution succeeded
             */
            execCommand : function(command, userInterface, value, validate){
               var doc, assert;
               if ((doc = this.getFrameDocument()) && !!command) {
                  try{
                      Ext.isIE && this.getWindow().focus();
	                  assert = validate && Ext.isFunction(doc.queryCommandEnabled) ? 
	                    doc.queryCommandEnabled(command) : true;
                  
                      return assert && doc.execCommand(command, !!userInterface, value);
                  }catch(eex){return false;}
               }
               return false;
                
            },

            /**
             * Sets the current DesignMode attribute of the Frame's document
             * @param {Boolean/String} active True (or "on"), to enable designMode
             * 
             */
            setDesignMode : function(active){
               var doc;
               (doc = this.getFrameDocument()) && 
                 (doc.designMode = (/on|true/i).test(String(active))?'on':'off');
            },
            
            /**
            * Gets this element's Updater
            * 
            * @return {Ext.ux.ManagedIFrame.Updater} The Updater
            */
            getUpdater : function(){
               return this.updateManager || 
                    (this.updateManager = new MIF.Updater(this));
                
            },

            /**
             * Method to retrieve frame's history object.
             * @return {object} or null if permission was denied
             */
            getHistory  : function(){
                var h=null;
                try{ h=this.getWindow().history; }catch(eh){}
                return h;
            },
            
            /**
             * Method to retrieve embedded frame Element objects. Uses simple
             * caching (per frame) to consistently return the same object.
             * Automatically fixes if an object was recreated with the same id via
             * AJAX or DOM.
             *
             * @param {Mixed}
             *            el The id of the node, a DOM Node or an existing Element.
             * @return {Element} The Element object (or null if no matching element
             *         was found)
             */
            get : function(el) {
                var doc = this.getFrameDocument();
                return doc? Ext.get(el, doc) : doc=null;
            },

            /**
             * Gets the globally shared flyweight Element for the frame, with the
             * passed node as the active element. Do not store a reference to this
             * element - the dom node can be overwritten by other code.
             *
             * @param {String/HTMLElement}
             *            el The dom node or id
             * @param {String}
             *            named (optional) Allows for creation of named reusable
             *            flyweights to prevent conflicts (e.g. internally Ext uses
             *            "_internal")
             * @return {Element} The shared Element object (or null if no matching
             *         element was found)
             */
            fly : function(el, named) {
                var doc = this.getFrameDocument();
                return doc ? Ext.fly(el, named, doc) : null;
            },

            /**
             * Return the dom node for the passed string (id), dom node, or
             * Ext.Element relative to the embedded frame document context.
             *
             * @param {Mixed} el
             * @return HTMLElement
             */
            getDom : function(el) {
                var d;
                if (!el || !(d = this.getFrameDocument())) {
                    return (d=null);
                }
                return Ext.getDom(el, d);
            },
            
            /**
             * Creates a {@link Ext.CompositeElement} for child nodes based on the
             * passed CSS selector (the selector should not contain an id).
             *
             * @param {String} selector The CSS selector
             * @param {Boolean} unique (optional) True to create a unique Ext.Element for
             *            each child (defaults to false, which creates a single
             *            shared flyweight object)
             * @return {Ext.CompositeElement/Ext.CompositeElementLite} The composite element
             */
            select : function(selector, unique) {
                var d; return (d = this.getFrameDocument()) ? Ext.Element.select(selector,unique, d) : d=null;
            },

            /**
             * Selects frame document child nodes based on the passed CSS selector
             * (the selector should not contain an id).
             *
             * @param {String} selector The CSS selector
             * @return {Array} An array of the matched nodes
             */
            query : function(selector) {
                var d; return (d = this.getFrameDocument()) ? Ext.DomQuery.select(selector, d): null;
            },
            
            /**
             * Removes a DOM Element from the embedded document
             * @param {Element/String} node The node id or node Element to remove
             */
            removeNode : Ext.removeNode,
            
            /**
             * @private execScript sandbox and messaging interface
             */ 
            _renderHook : function() {
                this._windowContext = null;
                this.CSS = this.CSS ? this.CSS.destroy() : null;
                this._hooked = false;
                try {
                    if (this.writeScript('(function(){(window.hostMIF = parent.document.getElementById("'
                                    + this.id
                                    + '").ownerCt)._windowContext='
                                    + (Ext.isIE
                                            ? 'window'
                                            : '{eval:function(s){return new Function("return ("+s+")")();}}')
                                    + ';})()')) {
                        var w, p = this._frameProxy, D = this.getFrameDocument();
                        if(w = this.getWindow()){
                            p || (p = this._frameProxy = this._eventProxy.createDelegate(this));    
                            addListener(w, 'focus', p);
                            addListener(w, 'blur', p);
                            addListener(w, 'resize', p);
                            addListener(w, 'unload', p);
                            D && addListener(Ext.isIE ? w : D, 'scroll', p);
                        }
                        
                        D && (this.CSS = new Ext.ux.ManagedIFrame.CSS(D));
                       
                    }
                } catch (ex) {}
                return this.domWritable();
            },
            
             /** @private : clear all event listeners and Element cache */
            _unHook : function() {
                if (this._hooked) {
                    
                    this._windowContext && (this._windowContext.hostMIF = null);
                    this._windowContext = null;
                
                    var w, p = this._frameProxy;
                    if(p && this.domWritable() && (w = this.getWindow())){
                        removeListener(w, 'focus', p);
                        removeListener(w, 'blur', p);
                        removeListener(w, 'resize', p);
                        removeListener(w, 'unload', p);
                        removeListener(Ext.isIE ? w : this.getFrameDocument(), 'scroll', p);
                    }
                }
                
                ELD.clearDocumentCache && ELD.clearDocumentCache(this.id);
                this.CSS = this.CSS ? this.CSS.destroy() : null;
                this.domFired = this._frameAction = this.domReady = this._hooked = false;
            },
            
            /** @private */
            _windowContext : null,

            /**
             * If sufficient privilege exists, returns the frame's current document
             * as an HTMLElement.
             *
             * @return {HTMLElement} The frame document or false if access to document object was denied.
             */
            getFrameDocument : function() {
                var win = this.getWindow(), doc = null;
                try {
                    doc = (Ext.isIE && win ? win.document : null)
                            || this.dom.contentDocument
                            || window.frames[this.dom.name].document || null;
                } catch (gdEx) {
                    
                    ELD.clearDocumentCache && ELD.clearDocumentCache(this.id);
                    return false; // signifies probable access restriction
                }
                doc = (doc && Ext.isFunction(ELD.getDocument)) ? ELD.getDocument(doc,true) : doc;
                
                return doc;
            },

            /**
             * Returns the frame's current HTML document object as an
             * {@link Ext.Element}.
             * @return {Ext.Element} The document
             */
            getDoc : function() {
                var D = this.getFrameDocument();
                return Ext.get(D,D); 
            },
            
            /**
             * If sufficient privilege exists, returns the frame's current document
             * body as an HTMLElement.
             *
             * @return {HTMLElement} The frame document body or Null if access to
             *         document object was denied.
             */
            getBody : function() {
                var d;
                return (d = this.getFrameDocument()) ? this.get(d.body || d.documentElement) : null;
            },

            /**
             * Attempt to retrieve the frames current URI via frame's document object
             * @return {string} The frame document's current URI or the last know URI if permission was denied.
             */
            getDocumentURI : function() {
                var URI, d;
                try {
                    URI = this.src && (d = this.getFrameDocument()) ? d.location.href: null;
                } catch (ex) { // will fail on NON-same-origin domains
                }
                return URI || (Ext.isFunction(this.src) ? this.src() : this.src);
                // fallback to last known
            },

           /**
            * Attempt to retrieve the frames current URI via frame's Window object
            * @return {string} The frame document's current URI or the last know URI if permission was denied.
            */
            getWindowURI : function() {
                var URI, w;
                try {
                    URI = (w = this.getWindow()) ? w.location.href : null;
                } catch (ex) {
                } // will fail on NON-same-origin domains
                return URI || (Ext.isFunction(this.src) ? this.src() : this.src);
                // fallback to last known
            },

            /**
             * Returns the frame's current window object.
             *
             * @return {Window} The frame Window object.
             */
            getWindow : function() {
                var dom = this.dom, win = null;
                try {
                    win = dom.contentWindow || window.frames[dom.name] || null;
                } catch (gwEx) {}
                return win;
            },
            
            /**
             * Scrolls a frame document's child element into view within the passed container.
             * @param {String} child The id of the element to scroll into view. 
             * @param {Mixed} container (optional) The container element to scroll (defaults to the frame's document.body).  Should be a 
             * string (id), dom node, or Ext.Element.
             * @param {Boolean} hscroll (optional) False to disable horizontal scroll (defaults to true)
             * @return {Ext.ux.ManagedIFrame.Element} this 
             */ 
            scrollChildIntoView : function(child, container, hscroll){
                this.fly(child, '_scrollChildIntoView').scrollIntoView(this.getDom(container) || this.getBody().dom, hscroll);
                return this;
            },

            /**
             * Print the contents of the Iframes (if we own the document)
             * @return {Ext.ux.ManagedIFrame.Element} this 
             */
            print : function() {
                try {
                    var win;
                    if( win = this.getWindow()){
                        Ext.isIE && win.focus();
                        win.print();
                    }
                } catch (ex) {
                    throw new MIF.Error('printexception' , ex.description || ex.message || ex);
                }
                return this;
            },

            /**
             * Returns the general DOM modification capability (same-origin status) of the frame. 
             * @return {Boolean} accessible If True, the frame's inner DOM can be manipulated, queried, and
             * Event Listeners set.
             */
            domWritable : function() {
                return !!Ext.isDocument(this.getFrameDocument(),true) //test access
                    && !!this._windowContext;
            },

            /**
             * eval a javascript code block(string) within the context of the
             * Iframes' window object.
             * @param {String} block A valid ('eval'able) script source block.
             * @param {Boolean} useDOM  if true, inserts the function
             * into a dynamic script tag, false does a simple eval on the function
             * definition. (useful for debugging) <p> Note: will only work after a
             * successful iframe.(Updater) update or after same-domain document has
             * been hooked, otherwise an exception is raised.
             * @return {Mixed}  
             */
            execScript : function(block, useDOM) {
                try {
                    if (this.domWritable()) {
                        if (useDOM) {
                            this.writeScript(block);
                        } else {
                            return this._windowContext.eval(block);
                        }
                    } else {
                        throw new MIF.Error('execscript-secure-context');
                    }
                } catch (ex) {
                    this._observable.fireEvent.call(this._observable,'exception', this, ex);
                    return false;
                }
                return true;
            },

            /**
             * Write a script block into the iframe's document
             * @param {String} block A valid (executable) script source block.
             * @param {object} attributes Additional Script tag attributes to apply to the script
             * Element (for other language specs [vbscript, Javascript] etc.) <p>
             * Note: writeScript will only work after a successful iframe.(Updater)
             * update or after same-domain document has been hooked, otherwise an
             * exception is raised.
             */
            writeScript : function(block, attributes) {
                attributes = Ext.apply({}, attributes || {}, {
                            type : "text/javascript",
                            text : block
                        });
                try {
                    var head, script, doc = this.getFrameDocument();
                    if (doc && typeof doc.getElementsByTagName != 'undefined') {
                        if (!(head = doc.getElementsByTagName("head")[0])) {
                            // some browsers (Webkit, Safari) do not auto-create
                            // head elements during document.write
                            head = doc.createElement("head");
                            doc.getElementsByTagName("html")[0].appendChild(head);
                        }
                        if (head && (script = doc.createElement("script"))) {
                            for (var attrib in attributes) {
                                if (attributes.hasOwnProperty(attrib)
                                        && attrib in script) {
                                    script[attrib] = attributes[attrib];
                                }
                            }
                            return !!head.appendChild(script);
                        }
                    }
                } catch (ex) {
                    this._observable.fireEvent.call(this._observable, 'exception', this, ex);

                }finally{
                    script = head = null;
                }
                return false;
            },

            /**
             * Eval a function definition into the iframe window context.
             * @param {String/Object} fn Name of the function or function map
             * object: {name:'encodeHTML',fn:Ext.util.Format.htmlEncode}
             * @param {Boolean} useDOM  if true, inserts the fn into a dynamic script tag,
             * false does a simple eval on the function definition
             * @param {Boolean} invokeIt if true, the function specified is also executed in the
             * Window context of the frame. Function arguments are not supported.
             * @example <pre><code> var trim = function(s){ return s.replace(/^\s+|\s+$/g,''); }; 
             * iframe.loadFunction('trim');
             * iframe.loadFunction({name:'myTrim',fn:String.prototype.trim || trim});</code></pre>
             */
            loadFunction : function(fn, useDOM, invokeIt) {
                var name = fn.name || fn;
                var fnSrc = fn.fn || window[fn];
                name && fnSrc && this.execScript(name + '=' + fnSrc, useDOM); // fn.toString coercion
                invokeIt && this.execScript(name + '()'); // no args only
            },

            /**
             * @private
             * Evaluate the Iframes readyState/load event to determine its
             * 'load' state, and raise the 'domready/documentloaded' event when
             * applicable.
             */
            loadHandler : function(e, target) {
                
                var rstatus = (this.dom||{}).readyState || (e || {}).type ;
                
                if (this.eventsFollowFrameLinks || this._frameAction || this.isReset ) {
                                       
	                switch (rstatus) {
	                    case 'domready' : // MIF
                        case 'DOMFrameContentLoaded' :
	                    case 'domfail' : // MIF
	                        this._onDocReady (rstatus);
	                        break;
	                    case 'load' : // Gecko, Opera, IE
	                    case 'complete' :
                            var frame = this;
	                        this._frameAction && setTimeout( function(){frame._onDocLoaded(rstatus); }, .01);
                            this._frameAction = false;
	                        break;
	                    case 'error':
	                        this._observable.fireEvent.apply(this._observable,['exception', this].concat(arguments));
	                        break;
	                    default :
	                }
                    this.frameState = rstatus;
                }
                
            },

            /**
             * @private
             * @param {String} eventName
             */
            _onDocReady  : function(eventName ){
                var w, obv = this._observable, D;
                try {
                    if(!this.isReset && this.focusOnLoad && (w = this.getWindow())){
                        w.focus(); 
                    }
                    (D = this.getDoc()) && (D.isReady = true);
                } catch(ex){}
                
                //raise internal event regardless of state.
                obv.fireEvent("_docready", this);
               
                if ( !this.domFired && 
                     (this._hooked = this._renderHook())) {
                        // Only raise if sandBox injection succeeded (same origin)
                        this.domFired = true;
                        this.isReset || obv.fireEvent.call(obv, 'domready', this);
                }
                
                this.domReady = true;
                this.hideMask();
            },

            /**
             * @private
             * @param {String} eventName
             */
            _onDocLoaded  : function(eventName ){
                var obv = this._observable, w;
                this.domReady || this._onDocReady('domready');
                
                obv.fireEvent("_docload", this);  //invoke any callbacks
                this.isReset || obv.fireEvent("documentloaded", this);
                this.hideMask(true);
                this._frameAction = this.isReset = false;
            },

            /**
             * @private
             * Poll the Iframes document structure to determine DOM ready
             * state, and raise the 'domready' event when applicable.
             */
            checkDOM : function( win) {
                if ( Ext.isGecko ) { return; } 
                // initialise the counter
                var n = 0, frame = this, domReady = false,
                    b, l, d, 
                    max = this.domReadyRetries || 2500, //default max 5 seconds 
                    polling = false,
                    startLocation = (this.getFrameDocument() || {location : {}}).location.href;
                (function() { // DOM polling for IE and others
                    d = frame.getFrameDocument() || {location : {}};
                    // wait for location.href transition
                    polling = (d.location.href !== startLocation || d.location.href === frame._targetURI);
                    if ( frame.domReady) { return;}
                    domReady = polling && ((b = frame.getBody()) && !!(b.dom.innerHTML || '').length) || false;
                    // null href is a 'same-origin' document access violation,
                    // so we assume the DOM is built when the browser updates it
                    if (d.location.href && !domReady && (++n < max)) {
                        setTimeout(arguments.callee, 2); // try again
                        return;
                    }
                    frame.loadHandler({ type : domReady ? 'domready' : 'domfail'});
                })();
            },
            
            /**
            * @private 
            */
            filterEventOptionsRe: /^(?:scope|delay|buffer|single|stopEvent|preventDefault|stopPropagation|normalized|args|delegate)$/,

           /**
            * @private override to handle synthetic events vs DOM events
            */
            addListener : function(eventName, fn, scope, options){

                if(typeof eventName == "object"){
                    var o = eventName;
                    for(var e in o){
                        if(this.filterEventOptionsRe.test(e)){
                            continue;
                        }
                        if(typeof o[e] == "function"){
                            // shared options
                            this.addListener(e, o[e], o.scope,  o);
                        }else{
                            // individual options
                            this.addListener(e, o[e].fn, o[e].scope, o[e]);
                        }
                    }
                    return;
                }

                if(reSynthEvents.test(eventName)){
                    var O = this._observable; 
                    if(O){
                        O.events[eventName] || (O.addEvents(eventName)); 
                        O.addListener.call(O, eventName, fn, scope || this, options) ;}
                }else {
                    ElFrame.superclass.addListener.call(this, eventName,
                            fn, scope || this, options);
                }
                return this;
            },

            /**
             * @private override
             * Removes an event handler from this element.
             */
            removeListener : function(eventName, fn, scope){
                var O = this._observable;
                if(reSynthEvents.test(eventName)){
                    O && O.removeListener.call(O, eventName, fn, scope || this, options);
                }else {
                  ElFrame.superclass.removeListener.call(this, eventName, fn, scope || this);
              }
              return this;
            },

            /**
             * Removes all previous added listeners from this element
             * @private override
             */
            removeAllListeners : function(){
                Ext.EventManager.removeAll(this.dom);
                var O = this._observable;
                O && O.purgeListeners.call(this._observable);
                return this;
            },
            
            /**
             * Forcefully show the defined loadMask
             * @param {String} msg Mask text to display during the mask operation, defaults to previous defined
             * loadMask config value.
             * @param {String} msgCls The CSS class to apply to the loading message element (defaults to "x-mask-loading")
             * @param {String} maskCls The CSS class to apply to the mask element
             */
            showMask : function(msg, msgCls, maskCls) {
                var lmask = this.loadMask;
                if (lmask && !lmask.disabled ){
                    this.mask(msg || lmask.msg, msgCls || lmask.msgCls, maskCls || lmask.maskCls, lmask.maskEl);
                }
            },
            
            /**
             * Hide the defined loadMask 
             * @param {Boolean} forced True to hide the mask regardless of document ready/loaded state.
             */
            hideMask : function(forced) {
                var tlm = this.loadMask || {};
                if (forced || (tlm.hideOnReady && this.domReady)) {
                     this.unmask();
                }
            },
            
            /**
             * Puts a mask over the FRAME to disable user interaction. Requires core.css.
             * @param {String} msg (optional) A message to display in the mask
             * @param {String} msgCls (optional) A css class to apply to the msg element
             * @param {String} maskCls (optional) A css class to apply to the mask element
             * @param {String/Element} maskEl (optional) A targeted Element (parent of the IFRAME) to use the masking agent
             * @return {Element} The mask element
             */
            mask : function(msg, msgCls, maskCls, maskEl){
                this._mask && this.unmask();
                var p = Ext.get(maskEl) || this.parent('.ux-mif-mask-target') || this.parent();
                if(p.getStyle("position") == "static" && 
                    !p.select('iframe,frame,object,embed').elements.length){
                        p.addClass("x-masked-relative");
                }
                
                p.addClass("x-masked");
                
                this._mask = Ext.DomHelper.append(p, {cls: maskCls || "ux-mif-el-mask"} , true);
                this._mask.setDisplayed(true);
                this._mask._agent = p;
                
                if(typeof msg == 'string'){
                     this._maskMsg = Ext.DomHelper.append(p, {cls: msgCls || "ux-mif-el-mask-msg" , style: {visibility:'hidden'}, cn:{tag:'div', html:msg}}, true);
                     this._maskMsg
                        .setVisibilityMode(Ext.Element.VISIBILITY)
                        .center(p).setVisible(true);
                }
                if(Ext.isIE && !(Ext.isIE7 && Ext.isStrict) && this.getStyle('height') == 'auto'){ // ie will not expand full height automatically
                    this._mask.setSize(undefined, this._mask.getHeight());
                }
                return this._mask;
            },

            /**
             * Removes a previously applied mask.
             */
            unmask : function(){
                
                var a;
                if(this._mask){
                    (a = this._mask._agent) && a.removeClass(["x-masked-relative","x-masked"]);
                    if(this._maskMsg){
                        this._maskMsg.remove();
                        delete this._maskMsg;
                    }
                    this._mask.remove();
                    delete this._mask;
                }
             },

             /**
              * Creates an (frontal) transparent shim agent for the frame.  Used primarily for masking the frame during drag operations.
              * @return {Ext.Element} The new shim element.
              * @param {String} imgUrl Optional Url of image source to use during shimming (defaults to Ext.BLANK_IMAGE_URL).
              * @param {String} shimCls Optional CSS style selector for the shimming agent. (defaults to 'ux-mif-shim' ).
              * @return (HTMLElement} the shim element
              */
             createFrameShim : function(imgUrl, shimCls ){
                 this.shimCls = shimCls || this.shimCls || 'ux-mif-shim';
                 this.frameShim || (this.frameShim = this.next('.'+this.shimCls) ||  //already there ?
                  Ext.DomHelper.append(
                     this.dom.parentNode,{
                         tag : 'img',
                         src : imgUrl|| Ext.BLANK_IMAGE_URL,
                         cls : this.shimCls ,
                         galleryimg : "no"
                    }, true)) ;
                 this.frameShim && (this.frameShim.autoBoxAdjust = false); 
                 return this.frameShim;
             },
             
             /**
              * Toggles visibility of the (frontal) transparent shim agent for the frame.  Used primarily for masking the frame during drag operations.
              * @param {Boolean} show Optional True to activate the shim, false to hide the shim agent.
              */
             toggleShim : function(show){
                var shim = this.frameShim || this.createFrameShim();
                var cls = this.shimCls + '-on';
                !show && shim.removeClass(cls);
                show && !shim.hasClass(cls) && shim.addClass(cls);
             },

            /**
             * Loads this panel's iframe immediately with content returned from an XHR call.
             * @param {Object/String/Function} config A config object containing any of the following options:
             * <pre><code>
             *      frame.load({
             *         url: &quot;your-url.php&quot;,
             *         params: {param1: &quot;foo&quot;, param2: &quot;bar&quot;}, // or encoded string
             *         callback: yourFunction,
             *         scope: yourObject, // optional scope for the callback
             *         discardUrl: false,
             *         nocache: false,
             *         text: &quot;Loading...&quot;,
             *         timeout: 30,
             *         scripts: false,
             *         //optional custom renderer
             *         renderer:{render:function(el, response, updater, callback){....}}  
             *      });
             * </code></pre>
             * The only required property is url. The optional properties
             *            nocache, text and scripts are shorthand for
             *            disableCaching, indicatorText and loadScripts and are used
             *            to set their associated property on this panel Updater
             *            instance.
             * @return {Ext.ManagedIFrame.Element} this
             */
            load : function(loadCfg) {
                var um;
                if (um = this.getUpdater()) {
                    if (loadCfg && loadCfg.renderer) {
                        um.setRenderer(loadCfg.renderer);
                        delete loadCfg.renderer;
                    }
                    um.update.apply(um, arguments);
                }
                return this;
            },

             /** @private
              * Frame document event proxy
              */
             _eventProxy : function(e) {
                 if (!e) return;
                 e = Ext.EventObject.setEvent(e);
                 var be = e.browserEvent || e, er, args = [e.type, this];
                 
                 if (!be['eventPhase']
                         || (be['eventPhase'] == (be['AT_TARGET'] || 2))) {
                            
                     if(e.type == 'resize'){
	                    var doc = this.getFrameDocument();
	                    doc && (args.push(
	                        { height: ELD.getDocumentHeight(doc), width : ELD.getDocumentWidth(doc) },
	                        { height: ELD.getViewportHeight(doc), width : ELD.getViewportWidth(doc) },
	                        { height: ELD.getViewHeight(false, doc), width : ELD.getViewWidth(false, doc) }
	                      ));  
	                 }
                     
                     er =  this._observable ? 
                           this._observable.fireEvent.apply(this._observable, args.concat(
                              Array.prototype.slice.call(arguments,0))) 
                           : null;
                 
	                 // same-domain unloads should clear ElCache for use with the
	                 // next document rendering
	                 (e.type == 'unload') && this._unHook();
                     
                 }
                 return er;
            },
            
            /**
	         * dispatch a message to the embedded frame-window context (same-origin frames only)
	         * @name sendMessage
	         * @param {Mixed} message The message payload.  The payload can be any supported JS type. 
	         * @param {String} tag Optional reference tag 
	         * @param {String} origin Optional domain designation of the sender (defaults
	         * to document.domain).
	         */
	        sendMessage : function(message, tag, origin) {
	          //(implemented by mifmsg.js )
	        },
            
            /**
	         * Dispatch a cross-document message (per HTML5 specification) if the browser supports it natively.
	         * @name postMessage
	         * @param {String} message Required message payload (String only)
	         * @param {String} origin (Optional) Site designation of the sender (defaults
	         * to the current site in the form: http://site.example.com ). 
	         * <p>Notes:  on IE8, this action is synchronous.<br/>
             * Messaging support requires that the optional messaging driver source 
             * file (mifmsg.js) is also included in your project.
             * 
	         */
	        postMessage : function(message ,origin ){
	            //(implemented by mifmsg.js )
	        }

    });
   
    ElFrame = Ext.Element.IFRAME = Ext.Element.FRAME = Ext.ux.ManagedIFrame.Element;
    
      
    var fp = ElFrame.prototype;
    /**
     * @ignore
     */
    Ext.override ( ElFrame , {
          
    /**
     * Appends an event handler (shorthand for {@link #addListener}).
     * @param {String} eventName The type of event to handle
     * @param {Function} fn The handler function the event invokes
     * @param {Object} scope (optional) The scope (this element) of the handler function
     * @param {Object} options (optional) An object containing standard {@link #addListener} options
     * @member Ext.Element
     * @method on
     */
        on :  fp.addListener,
        
    /**
     * Removes an event handler from this element (shorthand for {@link #removeListener}).
     * @param {String} eventName the type of event to remove
     * @param {Function} fn the method the event invokes
     * @return {MIF.Element} this
     * @member Ext.Element
     * @method un
     */
        un : fp.removeListener,
        
        getUpdateManager : fp.getUpdater
    });

  /**
   * @class Ext.ux.ManagedIFrame.ComponentAdapter
   * @version 2.1.4 
   * @author Doug Hendricks. doug[always-At]theactivegroup.com
   * @donate <a target="tag_donate" href="http://donate.theactivegroup.com"><img border="0" src="http://www.paypal.com/en_US/i/btn/x-click-butcc-donate.gif" border="0" alt="Make a donation to support ongoing development"></a>
   * @copyright 2007-2010, Active Group, Inc.  All rights reserved.
   * @license <a href="http://www.gnu.org/licenses/gpl.html">GPL 3.0</a>
   * @constructor
   * @desc
   * Abstract class.  This class should not be instantiated.
   */
  
   Ext.ux.ManagedIFrame.ComponentAdapter = function(){}; 
   Ext.ux.ManagedIFrame.ComponentAdapter.prototype = {
       
        /** @property */
        version : 2.14,
        
        /**
         * @cfg {String} defaultSrc the default src property assigned to the Managed Frame when the component is rendered.
         * @default null
         */
        defaultSrc : null,
        
        /**
         * @cfg {String} unsupportedText Text to display when the IFRAMES/FRAMESETS are disabled by the browser.
         *
         */
        unsupportedText : 'Inline frames are NOT enabled\/supported by your browser.',
        
        hideMode   : !Ext.isIE && !!Ext.ux.plugin.VisibilityMode ? 'nosize' : 'display',
        
        animCollapse  : Ext.isIE ,

        animFloat  : Ext.isIE ,
        
        /**
          * @cfg {Boolean} disableMessaging False to enable cross-frame messaging API
          * @default true
          *
          */
        disableMessaging : true, 
        
        /**
          * @cfg {Boolean} eventsFollowFrameLinks set true to propagate domready and documentloaded
          * events anytime the IFRAME's URL changes
          * @default true
          */
        eventsFollowFrameLinks   : true,
        
        /**
         * @cfg {object} frameConfig Frames DOM configuration options
         * This optional configuration permits override of the IFRAME's DOM attributes
         * @example
          frameConfig : {
              name : 'framePreview',
              frameborder : 1,
              allowtransparency : true
             }
         */
        frameConfig  : null,
        
        /**
         * @cfg focusOnLoad True to set focus on the frame Window as soon as its document
         * reports loaded.  (Many external sites use IE's document.createRange to create 
         * DOM elements, but to be successfull IE requires that the FRAME have focus before
         * the method is called)
         * @default false (true for Internet Explorer)
         */
        focusOnLoad   : Ext.isIE,
        
        /**
         * @property {Object} frameEl An {@link #Ext.ux.ManagedIFrame.Element} reference to rendered frame Element.
         */
        frameEl : null, 
  
        /**
         * @cfg {Boolean} useShim
         * True to use to create a transparent shimming agent for use in masking the frame during
         * drag operations.
         * @default false
         */
        useShim   : false,

        /**
         * @cfg {Boolean} autoScroll
         * True to use overflow:'auto' on the frame element and show scroll bars automatically when necessary,
         * false to clip any overflowing content (defaults to true).
         * @default true
         */
        autoScroll: true,
        
         /**
         * @cfg {String/Object} autoLoad
         * Loads this Components frame after the Component is rendered with content returned from an
         * XHR call or optionally from a form submission.  See {@link #Ext.ux.ManagedIFrame.ComponentAdapter-load} and {@link #Ext.ux.ManagedIFrame.ComponentAdapter-submitAsTarget} methods for
         * available configuration options.
         * @default null
         */
        autoLoad: null,
        
        /** @private */
        getId : function(){
             return this.id   || (this.id = "mif-comp-" + (++Ext.Component.AUTO_ID));
        },
        
        stateEvents : ['documentloaded'],
        
        stateful    : false,
        
        /**
         * Sets the autoScroll state for the frame.
         * @param {Boolean} auto True to set overflow:auto on the frame, false for overflow:hidden
         * @return {Ext.ux.ManagedIFrame.Component} this
         */
        setAutoScroll : function(auto){
            var scroll = Ext.value(auto, this.autoScroll === true);
            this.rendered && this.getFrame() &&  
                this.frameEl.setOverflow( (this.autoScroll = scroll) ? 'auto':'hidden');
            return this;
        },
        
        getContentTarget : function(){
            return this.getFrame();
        },
        
        /**
         * Returns the Ext.ux.ManagedIFrame.Element of the frame.
         * @return {Ext.ux.ManagedIFrame.Element} this.frameEl 
         */
        getFrame : function(){
             if(this.rendered){
                if(this.frameEl){ return this.frameEl;}
                var f = this.items && this.items.first ? this.items.first() : null;
                f && (this.frameEl = f.frameEl);
                return this.frameEl;
             }
             return null;
            },
        
        /**
         * Returns the frame's current window object.
         *
         * @return {Window} The frame Window object.
         */
        getFrameWindow : function() {
            return this.getFrame() ? this.frameEl.getWindow() : null;
        },

        /**
         * If sufficient privilege exists, returns the frame's current document
         * as an HTMLElement.
         *
         * @return {HTMLElement} The frame document or false if access to
         *         document object was denied.
         */
        getFrameDocument : function() {
            return this.getFrame() ? this.frameEl.getFrameDocument() : null;
        },

        /**
         * Get the embedded iframe's document as an Ext.Element.
         *
         * @return {Ext.Element object} or null if unavailable
         */
        getFrameDoc : function() {
            return this.getFrame() ? this.frameEl.getDoc() : null;
        },

        /**
         * If sufficient privilege exists, returns the frame's current document
         * body as an HTMLElement.
         *
         * @return {Ext.Element} The frame document body or Null if access to
         *         document object was denied.
         */
        getFrameBody : function() {
            return this.getFrame() ? this.frameEl.getBody() : null;
        },
        
        /**
         * Reset the embedded frame to a neutral domain state and clear its contents
          * @param {String}src (Optional) A specific reset string (eg. 'about:blank')
         *            to use for resetting the frame.
         * @param {Function} callback (Optional) A callback function invoked when the
         *            frame reset is complete.
         * @param {Object} scope (Optional) scope by which the callback function is
         *            invoked.
         * @return {Ext.ux.ManagedIFrame.Component} this
         */
        resetFrame : function() {
            this.getFrame() && this.frameEl.reset.apply(this.frameEl, arguments);
            return this;
        },
        
        /**
         * Loads the Components frame with the response from a form submit to the 
         * specified URL with the ManagedIframe.Element as it's submit target.
         * @param {Object} submitCfg A config object containing any of the following options:
         * <pre><code>
         *      mifPanel.submitAsTarget({
         *         form : formPanel.form,  //optional Ext.FormPanel, Ext form element, or HTMLFormElement
         *         url: &quot;your-url.php&quot;,
         *         params: {param1: &quot;foo&quot;, param2: &quot;bar&quot;}, // or a URL encoded string
         *         callback: yourFunction,  //optional
         *         scope: yourObject, // optional scope for the callback
         *         method: 'POST', //optional form.action (default:'POST')
         *         encoding : "multipart/form-data" //optional, default HTMLForm default
         *      });
         *
         * </code></pre>
         *
         * @return {Ext.ux.ManagedIFrame.Component} this
         */
        submitAsTarget  : function(submitCfg){
            this.getFrame() && this.frameEl.submitAsTarget.apply(this.frameEl, arguments);
            return this;
        },
        
        /**
         * Loads this Components's frame immediately with content returned from an
         * XHR call.
         *
         * @param {Object/String/Function} loadCfg A config object containing any of the following
         *            options:
         *
         * <pre><code>
         *      mifPanel.load({
         *         url: &quot;your-url.php&quot;,
         *         params: {param1: &quot;foo&quot;, param2: &quot;bar&quot;}, // or a URL encoded string
         *         callback: yourFunction,
         *         scope: yourObject, // optional scope for the callback
         *         discardUrl: false,
         *         nocache: false,
         *         text: &quot;Loading...&quot;,
         *         timeout: 30,
         *         scripts: false,
         *         submitAsTarget : false,  //optional true, to use Form submit to load the frame (see submitAsTarget method)
         *         renderer:{render:function(el, response, updater, callback){....}}  //optional custom renderer
         *      });
         *
         * </code></pre>
         *
         * The only required property is url. The optional properties
         *            nocache, text and scripts are shorthand for
         *            disableCaching, indicatorText and loadScripts and are used
         *            to set their associated property on this panel Updater
         *            instance.
         * @return {Ext.ux.ManagedIFrame.Component} this
         */
        load : function(loadCfg) {
            if(loadCfg && this.getFrame()){
                var args = arguments;
                this.resetFrame(null, function(){ 
                    loadCfg.submitAsTarget ?
                    this.submitAsTarget.apply(this,args):
                    this.frameEl.load.apply(this.frameEl,args);
                },this);
            }
            this.autoLoad = loadCfg;
            return this;
        },

        /** @private */
        doAutoLoad : function() {
            this.autoLoad && this.load(typeof this.autoLoad == 'object' ? 
                this.autoLoad : { url : this.autoLoad });
        },

        /**
         * Get the {@link #Ext.ux.ManagedIFrame.Updater} for this panel's iframe. Enables
         * Ajax-based document replacement of this panel's iframe document.
         *
         * @return {Ext.ux.ManagedIFrame.Updater} The Updater
         */
        getUpdater : function() {
            return this.getFrame() ? this.frameEl.getUpdater() : null;
        },
        
        /**
         * Sets the embedded Iframe src property. Note: invoke the function with
         * no arguments to refresh the iframe based on the current src value.
         *
         * @param {String/Function} url (Optional) A string or reference to a Function that
         *            returns a URI string when called
         * @param {Boolean} discardUrl (Optional) If not passed as <tt>false</tt>
         *            the URL of this action becomes the default SRC attribute
         *            for this iframe, and will be subsequently used in future
         *            setSrc calls (emulates autoRefresh by calling setSrc
         *            without params).
         * @param {Function} callback (Optional) A callback function invoked when the
         *            frame document has been fully loaded.
         * @param {Object} scope (Optional) scope by which the callback function is
         *            invoked.
         * @return {Ext.ux.ManagedIFrame.Component} this
         */
        setSrc : function(url, discardUrl, callback, scope) {
            this.getFrame() && this.frameEl.setSrc.apply(this.frameEl, arguments);
            return this;
        },

        /**
         * Sets the embedded Iframe location using its replace method. Note: invoke the function with
         * no arguments to refresh the iframe based on the current src value.
         *
         * @param {String/Function} url (Optional) A string or reference to a Function that
         *            returns a URI string when called
         * @param {Boolean} discardUrl (Optional) If not passed as <tt>false</tt>
         *            the URL of this action becomes the default SRC attribute
         *            for this iframe, and will be subsequently used in future
         *            setSrc calls (emulates autoRefresh by calling setSrc
         *            without params).
         * @param {Function} callback (Optional) A callback function invoked when the
         *            frame document has been fully loaded.
         * @param {Object} scope (Optional) scope by which the callback function is
         *            invoked.
         * @return {Ext.ux.ManagedIFrame.Component} this
         */
        setLocation : function(url, discardUrl, callback, scope) {
           this.getFrame() && this.frameEl.setLocation.apply(this.frameEl, arguments);
           return this;
        },

        /**
         * @private //Make it state-aware
         */
        getState : function() {
            var URI = this.getFrame() ? this.frameEl.getDocumentURI() || null : null;
            var state = this.supr().getState.call(this);
            state = Ext.apply(state || {}, 
                {defaultSrc : Ext.isFunction(URI) ? URI() : URI,
                 autoLoad   : this.autoLoad
                });
            return state;
        },
        
        /**
         * @private
         */
        setMIFEvents : function(){
            
            this.addEvents(

                    /**
                     * Fires when the iFrame has reached a loaded/complete state.
                     * @event documentloaded
                     * @memberOf Ext.ux.ManagedIFrame.ComponentAdapter
                     * @param {Ext.ux.ManagedIFrame.Element} frameEl
                     */
                    'documentloaded',  
                      
                    /**
                     * Fires ONLY when an iFrame's Document(DOM) has reach a
                     * state where the DOM may be manipulated (ie same domain policy)
                     * Note: This event is only available when overwriting the iframe
                     * document using the update method and to pages retrieved from a "same
                     * domain". Returning false from the eventHandler stops further event
                     * (documentloaded) processing.
                     * @event domready 
                     * @memberOf Ext.ux.ManagedIFrame.ComponentAdapter
                     * @param {Ext.ux.ManagedIFrame.Element} this.frameEl
                     */
                    'domready',
                    /**
                     * Fires when the frame actions raise an error
                     * @event exception
                     * @memberOf Ext.ux.ManagedIFrame.ComponentAdapter
                     * @param {Ext.ux.MIF.Element} frameEl
                     * @param {Error/string} exception
                     */
                    'exception',

                    /**
                     * Fires upon receipt of a message generated by window.sendMessage
                     * method of the embedded Iframe.window object
                     * @event message
                     * @memberOf Ext.ux.ManagedIFrame.ComponentAdapter
                     * @param {Ext.ux.ManagedIFrame.Element} this.frameEl
                     * @param {object}
                     *            message (members: type: {string} literal "message", data
                     *            {Mixed} [the message payload], domain [the document domain
                     *            from which the message originated ], uri {string} the
                     *            document URI of the message sender source (Object) the
                     *            window context of the message sender tag {string} optional
                     *            reference tag sent by the message sender
                     * <p>Alternate event handler syntax for message:tag filtering Fires upon
                     * receipt of a message generated by window.sendMessage method which
                     * includes a specific tag value of the embedded Iframe.window object
                     *
                     */
                    'message',

                    /**
                     * Fires when the frame is blurred (loses focus).
                     * @event blur
                     * @memberOf Ext.ux.ManagedIFrame.ComponentAdapter
                     * @param {Ext.ux.ManagedIFrame.Element} frameEl
                     * @param {Ext.Event} e Note: This event is only available when overwriting the
                     *            iframe document using the update method and to pages
                     *            retrieved from a "same domain". Returning false from the
                     *            eventHandler [MAY] NOT cancel the event, as this event is
                     *            NOT ALWAYS cancellable in all browsers.
                     */
                    'blur',

                    /**
                     * Fires when the frame gets focus. Note: This event is only available
                     * when overwriting the iframe document using the update method and to
                     * pages retrieved from a "same domain". Returning false from the
                     * eventHandler [MAY] NOT cancel the event, as this event is NOT ALWAYS
                     * cancellable in all browsers.
                     * @event focus
                     * @memberOf Ext.ux.ManagedIFrame.ComponentAdapter
                     * @param {Ext.ux.ManagedIFrame.Element} frameEl
                     * @param {Ext.Event} e
                     *
                    */
                    'focus',
                    
                     /**
                     * Note: This event is only available when overwriting the iframe
                     * document using the update method and to pages retrieved from a "same-origin"
                     * domain.  To prevent numerous scroll events from being raised use the <i>buffer</i> listener 
                     * option to limit the number of times the event is raised.
                     * @event scroll 
                     * @param {Ext.ux.MIF.Element} this.
                     * @param {Ext.Event}
                     */
                    'scroll',
                    
                    /**
                     * Fires when the frames window is resized. Note: This event is only available
                     * when overwriting the iframe document using the update method and to
                     * pages retrieved from a "same domain". 
                     * @event resize
                     * @memberOf Ext.ux.ManagedIFrame.ComponentAdapter
                     * @param {Ext.ux.ManagedIFrame.Element} frameEl
                     * @param {Ext.Event} e
                     * @param {Object} documentSize A height/width object signifying the new document size
                     * @param {Object} viewPortSize A height/width object signifying the size of the frame's viewport
                     * @param {Object} viewSize A height/width object signifying the size of the frame's view
                     *
                    */
                    'resize',
                    
                    /**
                     * Fires when(if) the frames window object raises the unload event
                     * Note: This event is only available when overwriting the iframe
                     * document using the update method and to pages retrieved from a "same-origin"
                     * domain. Note: Opera does not raise this event.
                     * @event unload 
                     * @memberOf Ext.ux.ManagedIFrame.ComponentAdapter
                     * @param {Ext.ux.ManagedIFrame.Element} frameEl
                     * @param {Ext.Event}
                     */
                    'unload',
                    
                    /**
                     * Fires when the iFrame has been reset to a neutral domain state (blank document).
                     * @event reset
                     * @param {Ext.ux.ManagedIFrame.Element} frameEl
                     */
                    'reset'
                );
        },
        
        /**
         * dispatch a message to the embedded frame-window context (same-origin frames only)
         * @name sendMessage
         * @memberOf Ext.ux.ManagedIFrame.Element
         * @param {Mixed} message The message payload.  The payload can be any supported JS type. 
         * @param {String} tag Optional reference tag 
         * @param {String} origin Optional domain designation of the sender (defaults
         * to document.domain).
         */
        sendMessage : function(message, tag, origin) {
       
          //(implemented by mifmsg.js )
        },
        //Suspend (and queue) host container events until the child MIF.Component is rendered.
        onAdd : function(C){
             C.relayTarget && this.suspendEvents(true); 
        },
        
        initRef: function() {
      
	        if(this.ref){
	            var t = this,
	                levels = this.ref.split('/'),
	                l = levels.length,
	                i;
	            for (i = 0; i < l; i++) {
	                if(t.ownerCt){
	                    t = t.ownerCt;
	                }
	            }
	            this.refName = levels[--i];
	            t[this.refName] || (t[this.refName] = this);
	            
	            this.refOwner = t;
	        }
	    }
      
   };
   
   /*
    * end Adapter
    */
   
  /**
   * @class Ext.ux.ManagedIFrame.Component
   * @extends Ext.BoxComponent
   * @version 2.1.4 
   * @author Doug Hendricks. doug[always-At]theactivegroup.com
   * @donate <a target="tag_donate" href="http://donate.theactivegroup.com"><img border="0" src="http://www.paypal.com/en_US/i/btn/x-click-butcc-donate.gif" border="0" alt="Make a donation to support ongoing development"></a>
   * @copyright 2007-2010, Active Group, Inc.  All rights reserved.
   * @license <a href="http://www.gnu.org/licenses/gpl.html">GPL 3.0</a>
   * @constructor
   * @base Ext.ux.ManagedIFrame.ComponentAdapter
   * @param {Object} config The config object
   */
  Ext.ux.ManagedIFrame.Component = Ext.extend(Ext.BoxComponent , { 
            
            ctype     : "Ext.ux.ManagedIFrame.Component",
            
            /** @private */
            initComponent : function() {
               
                var C = {
	                monitorResize : this.monitorResize || (this.monitorResize = !!this.fitToParent),
	                plugins : (this.plugins ||[]).concat(
	                    this.hideMode === 'nosize' && Ext.ux.plugin.VisibilityMode ? 
		                    [new Ext.ux.plugin.VisibilityMode(
		                        {hideMode :'nosize',
		                         elements : ['bwrap']
		                        })] : [] )
                  };
                  
                MIF.Component.superclass.initComponent.call(
                  Ext.apply(this,
                    Ext.apply(this.initialConfig, C)
                    ));
                    
                this.setMIFEvents();
            },   

            /** @private */
            onRender : function(ct, position){
                
                //default child frame's name to that of MIF-parent id (if not specified on frameCfg).
                var frCfg = this.frameCfg || this.frameConfig || (this.relayTarget ? {name : this.relayTarget.id}: {}) || {};
                
                //backward compatability with MIF 1.x
                var frDOM = frCfg.autoCreate || frCfg;
                frDOM = Ext.apply({tag  : 'iframe', id: Ext.id()}, frDOM);
                
                var el = Ext.getDom(this.el);

                (el && el.tagName == 'iframe') || 
                  (this.autoEl = Ext.apply({
                                    name : frDOM.id,
                                    frameborder : 0
                                   }, frDOM ));
                 
                MIF.Component.superclass.onRender.apply(this, arguments);
               
                if(this.unsupportedText){
                    ct.child('noframes') || ct.createChild({tag: 'noframes', html : this.unsupportedText || null});  
                }   
                var frame = this.el ;
                
                var F;
                if( F = this.frameEl = (this.el ? new MIF.Element(this.el.dom, true): null)){
                    
                    Ext.apply(F,{
                        ownerCt          : this.relayTarget || this,
                        disableMessaging : Ext.value(this.disableMessaging, true),
                        focusOnLoad      : Ext.value(this.focusOnLoad, Ext.isIE),
                        eventsFollowFrameLinks : Ext.value(this.eventsFollowFrameLinks ,true)
                    });
                    F.ownerCt.frameEl = F;
                    F.addClass('ux-mif'); 
                    if (this.loadMask) {
                        //resolve possible maskEl by Element name eg. 'body', 'bwrap', 'actionEl'
                        var mEl = this.loadMask.maskEl;
                        F.loadMask = Ext.apply({
                                    disabled    : false,
                                    hideOnReady : false,
                                    msgCls      : 'ext-el-mask-msg x-mask-loading',  
                                    maskCls     : 'ext-el-mask'
                                },
                                {
                                  maskEl : F.ownerCt[String(mEl)] || F.parent('.' + String(mEl)) || F.parent('.ux-mif-mask-target') || mEl 
                                },
                                Ext.isString(this.loadMask) ? {msg:this.loadMask} : this.loadMask
                              );
                        Ext.get(F.loadMask.maskEl) && Ext.get(F.loadMask.maskEl).addClass('ux-mif-mask-target');
                    }
                    
                    F._observable && 
                        (this.relayTarget || this).relayEvents(F._observable, frameEvents.concat(this._msgTagHandlers || []));
                        
                    delete this.contentEl;
                    
                    //Template support for writable frames
                    
                 }
            },
            
            /** @private */
            afterRender  : function(container) {
                MIF.Component.superclass.afterRender.apply(this,arguments);
                
                // only resize (to Parent) if the panel is NOT in a layout.
                // parentNode should have {style:overflow:hidden;} applied.
                if (this.fitToParent && !this.ownerCt) {
                    var pos = this.getPosition(), size = (Ext.get(this.fitToParent)
                            || this.getEl().parent()).getViewSize();
                    this.setSize(size.width - pos[0], size.height - pos[1]);
                }

                this.getEl().setOverflow('hidden'); //disable competing scrollers
                this.setAutoScroll();
                var F;
               /* Enable auto-Shims if the Component participates in (nested?)
                * border layout.
                * Setup event handlers on the SplitBars and region panels to enable the frame
                * shims when needed
                */
                if(F = this.frameEl){
                    var ownerCt = this.ownerCt;
                    while (ownerCt) {
                        ownerCt.on('afterlayout', function(container, layout) {
                            Ext.each(['north', 'south', 'east', 'west'],
                                    function(region) {
                                        var reg;
                                        if ((reg = layout[region]) && 
                                             reg.split && reg.split.dd &&
                                             !reg._splitTrapped) {
                                               reg.split.dd.endDrag = reg.split.dd.endDrag.createSequence(MIM.hideShims, MIM );
                                               reg.split.on('beforeresize',MIM.showShims,MIM);
                                               reg._splitTrapped = MIM._splitTrapped = true;
                                        }
                            }, this);
                        }, this, { single : true}); // and discard
                        ownerCt = ownerCt.ownerCt; // nested layouts?
                    }
                    /*
                     * Create an img shim if the component participates in a layout or forced
                     */
                    if(!!this.ownerCt || this.useShim ){ this.frameShim = F.createFrameShim(); }
                    this.getUpdater().showLoadIndicator = this.showLoadIndicator || false;
                    
                    //Resume Parent containers' events callback
                    var resumeEvents = this.relayTarget && this.ownerCt ?                         
                       this.ownerCt.resumeEvents.createDelegate(this.ownerCt) : null;
                       
                    if (this.autoload) {
                       this.doAutoLoad();
                    } else if(this.tpl && (this.frameData || this.data)) {
                       F.update(this.tpl.apply(this.frameData || this.data), true, resumeEvents);
                       delete this.frameData;
                       delete this.data;
                       return;
                    } else if(this.frameMarkup  || this.html) {
                       F.update(this.frameMarkup  || this.html , true, resumeEvents);
                       delete this.html;
                       delete this.frameMarkup;
                       return;
                    } else {
                       if (this.defaultSrc) {
                            F.setSrc(this.defaultSrc, false);
                       } else {
                            /* If this is a no-action frame, reset it first, then resume parent events
                             * allowing access to a fully reset frame by upstream afterrender/layout events
                             */ 
                            F.reset(null, resumeEvents);
                            return;
                       }
                    }
                    resumeEvents && resumeEvents();
                }
            },
            
            /** @private */
            beforeDestroy : function() {
                var F;
                if(F = this.getFrame()){
                    F.remove();
                    this.frameEl = this.frameShim = null;
                }
                this.relayTarget && (this.relayTarget.frameEl = null);
                MIF.Component.superclass.beforeDestroy.call(this);
            }
    });

    Ext.override(MIF.Component, MIF.ComponentAdapter.prototype);
    Ext.reg('mif', MIF.Component);
   
    /*
    * end Component
    */
    
  /**
   * @private
   * this function renders a child MIF.Component to MIF.Panel and MIF.Window
   * designed to be called by the constructor of higher-level MIF.Components only.
   */
  function embed_MIF(config){
    
    config || (config={});
    config.layout = 'fit';
    config.items = {
             xtype    : 'mif',
               ref    : 'mifChild',
            useShim   : true,
                  tpl : Ext.value(config.tpl , this.tpl),
           autoScroll : Ext.value(config.autoScroll , this.autoScroll),
          defaultSrc  : Ext.value(config.defaultSrc , this.defaultSrc),
         frameMarkup  : Ext.value(config.html , this.html),
           frameData  : Ext.value(config.data , this.data),
            loadMask  : Ext.value(config.loadMask , this.loadMask),
    disableMessaging  : Ext.value(config.disableMessaging, this.disableMessaging),
 eventsFollowFrameLinks : Ext.value(config.eventsFollowFrameLinks, this.eventsFollowFrameLinks),
         focusOnLoad  : Ext.value(config.focusOnLoad, this.focusOnLoad),
          frameConfig : Ext.value(config.frameConfig || config.frameCfg , this.frameConfig),
          relayTarget : this  //direct relay of events to the parent component
        };
    delete config.html;
    delete config.data;
    this.setMIFEvents();
    return config; 
    
  };
    
  /**
   * @class Ext.ux.ManagedIFrame.Panel
   * @extends Ext.Panel
   * @version 2.1.4 
   * @author Doug Hendricks. doug[always-At]theactivegroup.com
   * @donate <a target="tag_donate" href="http://donate.theactivegroup.com"><img border="0" src="http://www.paypal.com/en_US/i/btn/x-click-butcc-donate.gif" border="0" alt="Make a donation to support ongoing development"></a>
   * @copyright 2007-2010, Active Group, Inc.  All rights reserved.
   * @license <a href="http://www.gnu.org/licenses/gpl.html">GPL 3.0</a>
   * @constructor
   * @base Ext.ux.ManagedIFrame.ComponentAdapter
   * @param {Object} config The config object
   */

  Ext.ux.ManagedIFrame.Panel = Ext.extend( Ext.Panel , {
        ctype       : 'Ext.ux.ManagedIFrame.Panel',
        bodyCssClass: 'ux-mif-mask-target',
        constructor : function(config){
            MIF.Panel.superclass.constructor.call(this, embed_MIF.call(this, config));
         }
  });
  
  Ext.override(MIF.Panel, MIF.ComponentAdapter.prototype);
  Ext.reg('iframepanel', MIF.Panel);
    /*
    * end Panel
    */

    /**
     * @class Ext.ux.ManagedIFrame.Portlet
     * @extends Ext.ux.ManagedIFrame.Panel
     * @version 2.1.4 
     * @donate <a target="tag_donate" href="http://donate.theactivegroup.com"><img border="0" src="http://www.paypal.com/en_US/i/btn/x-click-butcc-donate.gif" border="0" alt="Make a donation to support ongoing development"></a>
     * @license <a href="http://www.gnu.org/licenses/gpl.html">GPL 3.0</a> 
     * @author Doug Hendricks. Forum ID: <a href="http://extjs.com/forum/member.php?u=8730">hendricd</a> 
     * @copyright 2007-2010, Active Group, Inc. All rights reserved.
     * @constructor Create a new Ext.ux.ManagedIFramePortlet 
     * @param {Object} config The config object
     */

    Ext.ux.ManagedIFrame.Portlet = Ext.extend(Ext.ux.ManagedIFrame.Panel, {
                ctype      : "Ext.ux.ManagedIFrame.Portlet",
                anchor     : '100%',
                frame      : true,
                collapseEl : 'bwrap',
                collapsible: true,
                draggable  : true,
                cls        : 'x-portlet'
                
            });
            
    Ext.reg('iframeportlet', MIF.Portlet);
   /*
    * end Portlet
    */
    
  /**
   * @class Ext.ux.ManagedIFrame.Window
   * @extends Ext.Window
   * @version 2.1.4 
   * @author Doug Hendricks. 
   * @donate <a target="tag_donate" href="http://donate.theactivegroup.com"><img border="0" src="http://www.paypal.com/en_US/i/btn/x-click-butcc-donate.gif" border="0" alt="Make a donation to support ongoing development"></a>
   * @copyright 2007-2010, Active Group, Inc.  All rights reserved.
   * @license <a href="http://www.gnu.org/licenses/gpl.html">GPL 3.0</a>
   * @constructor
   * @base Ext.ux.ManagedIFrame.ComponentAdapter
   * @param {Object} config The config object
   */
    
  Ext.ux.ManagedIFrame.Window = Ext.extend( Ext.Window , 
       {
            ctype       : "Ext.ux.ManagedIFrame.Window",
            bodyCssClass: 'ux-mif-mask-target',
            constructor : function(config){
			    MIF.Window.superclass.constructor.call(this, embed_MIF.call(this, config));
            }
    });
    Ext.override(MIF.Window, MIF.ComponentAdapter.prototype);
    Ext.reg('iframewindow', MIF.Window);
    
    /*
    * end Window
    */
    
    /**
     * @class Ext.ux.ManagedIFrame.Updater
     * @extends Ext.Updater
     * @version 2.1.4 
     * @donate <a target="tag_donate" href="http://donate.theactivegroup.com"><img border="0" src="http://www.paypal.com/en_US/i/btn/x-click-butcc-donate.gif" border="0" alt="Make a donation to support ongoing development"></a>
     * @license <a href="http://www.gnu.org/licenses/gpl.html">GPL 3.0</a> 
     * @author Doug Hendricks. Forum ID: <a href="http://extjs.com/forum/member.php?u=8730">hendricd</a> 
     * @copyright 2007-2010, Active Group, Inc. All rights reserved.
     * @constructor Creates a new Ext.ux.ManagedIFrame.Updater instance.
     * @param {String/Object} el The element to bind the Updater instance to.
     */
    Ext.ux.ManagedIFrame.Updater = Ext.extend(Ext.Updater, {
    
       /**
         * Display the element's "loading" state. By default, the element is updated with {@link #indicatorText}. This
         * method may be overridden to perform a custom action while this Updater is actively updating its contents.
         */
        showLoading : function(){
            this.showLoadIndicator && this.el && this.el.mask(this.indicatorText);
            
        },
        
        /**
         * Hide the Frames masking agent.
         */
        hideLoading : function(){
            this.showLoadIndicator && this.el && this.el.unmask();
        },
        
        // private
        updateComplete : function(response){
            MIF.Updater.superclass.updateComplete.apply(this,arguments);
            this.hideLoading();
        },
    
        // private
        processFailure : function(response){
            MIF.Updater.superclass.processFailure.apply(this,arguments);
            this.hideLoading();
        }
        
    }); 
    
    
    var styleCamelRe = /(-[a-z])/gi;
    var styleCamelFn = function(m, a) {
        return a.charAt(1).toUpperCase();
    };
    
    /**
     * @class Ext.ux.ManagedIFrame.CSS
     * Stylesheet interface object
     * @version 2.1.4 
     * @author Doug Hendricks. doug[always-At]theactivegroup.com
     * @donate <a target="tag_donate" href="http://donate.theactivegroup.com"><img border="0" src="http://www.paypal.com/en_US/i/btn/x-click-butcc-donate.gif" border="0" alt="Make a donation to support ongoing development"></a>
     * @copyright 2007-2010, Active Group, Inc.  All rights reserved.
     * @license <a href="http://www.gnu.org/licenses/gpl.html">GPL 3.0</a>
     */
    Ext.ux.ManagedIFrame.CSS = function(hostDocument) {
        var doc;
        if (hostDocument) {
            doc = hostDocument;
            return {
                rules : null,
                /** @private */
                destroy  :  function(){  return doc = null; },

                /**
                 * Creates a stylesheet from a text blob of rules. These rules
                 * will be wrapped in a STYLE tag and appended to the HEAD of
                 * the document.
                 *
                 * @param {String}
                 *            cssText The text containing the css rules
                 * @param {String} id An (optional) id to add to the stylesheet for later removal
                 * @return {StyleSheet}
                 */
                createStyleSheet : function(cssText, id) {
                    var ss;
                    if (!doc)return;
                    var head = doc.getElementsByTagName("head")[0];
                    var rules = doc.createElement("style");
                    rules.setAttribute("type", "text/css");
                    Ext.isString(id) && rules.setAttribute("id", id);

                    if (Ext.isIE) {
                        head.appendChild(rules);
                        ss = rules.styleSheet;
                        ss.cssText = cssText;
                    } else {
                        try {
                            rules.appendChild(doc.createTextNode(cssText));
                        } catch (e) {
                            rules.cssText = cssText;
                        }
                        head.appendChild(rules);
                        ss = rules.styleSheet
                                ? rules.styleSheet
                                : (rules.sheet || doc.styleSheets[doc.styleSheets.length - 1]);
                    }
                    this.cacheStyleSheet(ss);
                    return ss;
                },

                /**
                 * Removes a style or link tag by id
                 *
                 * @param {String}
                 *            id The id of the tag
                 */
                removeStyleSheet : function(id) {

                    if (!doc || !id)return;
                    var existing = doc.getElementById(id);
                    if (existing) {
                        existing.parentNode.removeChild(existing);
                    }
                },

                /**
                 * Dynamically swaps an existing stylesheet reference for a new
                 * one
                 *
                 * @param {String}
                 *            id The id of an existing link tag to remove
                 * @param {String}
                 *            url The href of the new stylesheet to include
                 */
                swapStyleSheet : function(id, url) {
                    if (!doc)return;
                    this.removeStyleSheet(id);
                    var ss = doc.createElement("link");
                    ss.setAttribute("rel", "stylesheet");
                    ss.setAttribute("type", "text/css");
                    Ext.isString(id) && ss.setAttribute("id", id);
                    ss.setAttribute("href", url);
                    doc.getElementsByTagName("head")[0].appendChild(ss);
                },

                /**
                 * Refresh the rule cache if you have dynamically added stylesheets
                 * @return {Object} An object (hash) of rules indexed by selector
                 */
                refreshCache : function() {
                    return this.getRules(true);
                },

                // private
                cacheStyleSheet : function(ss, media) {
                    this.rules || (this.rules = {});
                    
                     try{// try catch for cross domain access issue
			          
				          Ext.each(ss.cssRules || ss.rules || [], 
				            function(rule){ 
				              this.hashRule(rule, ss, media);
				          }, this);  
				          
				          //IE @imports
				          Ext.each(ss.imports || [], 
				           function(sheet){
				              sheet && this.cacheStyleSheet(sheet,this.resolveMedia([sheet, sheet.parentStyleSheet]));
				           }
				          ,this);
			          
			        }catch(e){}
                },
                 // @private
			   hashRule  :  function(rule, sheet, mediaOverride){
			      
			      var mediaSelector = mediaOverride || this.resolveMedia(rule);
			      
			      //W3C @media
			      if( rule.cssRules || rule.rules){
			          this.cacheStyleSheet(rule, this.resolveMedia([rule, rule.parentRule ]));
			      } 
			      
			       //W3C @imports
			      if(rule.styleSheet){ 
			         this.cacheStyleSheet(rule.styleSheet, this.resolveMedia([rule, rule.ownerRule, rule.parentStyleSheet]));
			      }
			      
			      rule.selectorText && 
			        Ext.each((mediaSelector || '').split(','), 
			           function(media){
			            this.rules[((media ? media.trim() + ':' : '') + rule.selectorText).toLowerCase()] = rule;
			        }, this);
			      
			   },
			
			   /**
			    * @private
			    * @param {Object/Array} rule CSS Rule (or array of Rules/sheets) to evaluate media types.
			    * @return a comma-delimited string of media types. 
			    */
			   resolveMedia  : function(rule){
			        var media;
			        Ext.each([].concat(rule),function(r){
			            if(r && r.media && r.media.length){
			                media = r.media;
			                return false;
			            }
			        });
			        return media ? (Ext.isIE ? String(media) : media.mediaText ) : '';
			     },

                /**
                 * Gets all css rules for the document
                 *
                 * @param {Boolean}
                 *            refreshCache true to refresh the internal cache
                 * @return {Object} An object (hash) of rules indexed by
                 *         selector
                 */
                getRules : function(refreshCache) {
                    if (!this.rules || refreshCache) {
                        this.rules = {};
                        if (doc) {
                            var ds = doc.styleSheets;
                            for (var i = 0, len = ds.length; i < len; i++) {
                                try {
                                    this.cacheStyleSheet(ds[i]);
                                } catch (e) {}
                            }
                        }
                    }
                    return this.rules;
                },

               /**
			    * Gets an an individual CSS rule by selector(s)
			    * @param {String/Array} selector The CSS selector or an array of selectors to try. The first selector that is found is returned.
			    * @param {Boolean} refreshCache true to refresh the internal cache if you have recently updated any rules or added styles dynamically
			    * @param {String} mediaSelector Name of optional CSS media context (eg. print, screen)
			    * @return {CSSRule} The CSS rule or null if one is not found
			    */
                getRule : function(selector, refreshCache, mediaSelector) {
                    var rs = this.getRules(refreshCache);

			        if(Ext.type(mediaSelector) == 'string'){
			            mediaSelector = mediaSelector.trim() + ':';
			        }else{
			            mediaSelector = '';
			        }
			
			        if(!Ext.isArray(selector)){
			            return rs[(mediaSelector + selector).toLowerCase()];
			        }
			        var select;
			        for(var i = 0; i < selector.length; i++){
			            select = (mediaSelector + selector[i]).toLowerCase();
			            if(rs[select]){
			                return rs[select];
			            }
			        }
			        return null;
                },

               /**
			    * Updates a rule property
			    * @param {String/Array} selector If it's an array it tries each selector until it finds one. Stops immediately once one is found.
			    * @param {String} property The css property
			    * @param {String} value The new value for the property
			    * @param {String} mediaSelector Name(s) of optional media contexts. Multiple may be specified, delimited by commas (eg. print,screen)
			    * @return {Boolean} true If a rule was found and updated
			    */
                updateRule : function(selector, property, value, mediaSelector){
    
			         Ext.each((mediaSelector || '').split(','), function(mediaSelect){    
			            if(!Ext.isArray(selector)){
			                var rule = this.getRule(selector, false, mediaSelect);
			                if(rule){
			                    rule.style[property.replace(camelRe, camelFn)] = value;
			                    return true;
			                }
			            }else{
			                for(var i = 0; i < selector.length; i++){
			                    if(this.updateRule(selector[i], property, value, mediaSelect)){
			                        return true;
			                    }
			                }
			            }
			            return false;
			         }, this);
                }
            };
        }
    };

    /**
     * @class Ext.ux.ManagedIFrame.Manager
     * @version 2.1.4 
	 * @author Doug Hendricks. doug[always-At]theactivegroup.com
	 * @donate <a target="tag_donate" href="http://donate.theactivegroup.com"><img border="0" src="http://www.paypal.com/en_US/i/btn/x-click-butcc-donate.gif" border="0" alt="Make a donation to support ongoing development"></a>
	 * @copyright 2007-2010, Active Group, Inc.  All rights reserved.
	 * @license <a href="http://www.gnu.org/licenses/gpl.html">GPL 3.0</a>
	 * @singleton
     */
    Ext.ux.ManagedIFrame.Manager = function() {
        var frames = {};
        var implementation = {
            // private DOMFrameContentLoaded handler for browsers (Gecko, Webkit, Opera) that support it.
            _DOMFrameReadyHandler : function(e) {
                try {
                    var $frame ;
                    if ($frame = e.target.ownerCt){
                        $frame.loadHandler.call($frame,e);
                    }
                } catch (rhEx) {} //nested iframes will throw when accessing target.id
            },
            /**
             * @cfg {String} shimCls
             * @default "ux-mif-shim"
             * The default CSS rule applied to MIF image shims to toggle their visibility.
             */
            shimCls : 'ux-mif-shim',

            /** @private */
            register : function(frame) {
                frame.manager = this;
                frames[frame.id] = frames[frame.name] = {ref : frame };
                return frame;
            },
            /** @private */
            deRegister : function(frame) {
                delete frames[frame.id];
                delete frames[frame.name];
                
            },
            /**
             * Toggles the built-in MIF shim off on all visible MIFs
             * @methodOf Ext.ux.MIF.Manager
             *
             */
            hideShims : function() {
                var mm = MIF.Manager;
                mm.shimsApplied && Ext.select('.' + mm.shimCls, true).removeClass(mm.shimCls+ '-on');
                mm.shimsApplied = false;
            },

            /**
             * Shim ALL MIFs (eg. when a region-layout.splitter is on the move or before start of a drag operation)
             * @methodOf Ext.ux.MIF.Manager
             */
            showShims : function() {
                var mm = MIF.Manager;
                !mm.shimsApplied && Ext.select('.' + mm.shimCls, true).addClass(mm.shimCls+ '-on');
                mm.shimsApplied = true;
            },

            /**
             * Retrieve a MIF instance by its DOM ID
             * @methodOf Ext.ux.MIF.Manager
             * @param {Ext.ux.MIF/string} id
             */
            getFrameById : function(id) {
                return typeof id == 'string' ? (frames[id] ? frames[id].ref
                        || null : null) : null;
            },

            /**
             * Retrieve a MIF instance by its DOM name
             * @methodOf Ext.ux.MIF.Manager
             * @param {Ext.ux.MIF/string} name
             */
            getFrameByName : function(name) {
                return this.getFrameById(name);
            },

            /** @private */
            // retrieve the internal frameCache object
            getFrameHash : function(frame) {
                return frames[frame.id] || frames[frame.id] || null;
            },

            /** @private */
            destroy : function() {
                if (document.addEventListener && !Ext.isOpera) {
                      window.removeEventListener("DOMFrameContentLoaded", this._DOMFrameReadyHandler , false);
                }
            }
        };
        // for Gecko and any who might support it later 
        document.addEventListener && !Ext.isOpera &&
            window.addEventListener("DOMFrameContentLoaded", implementation._DOMFrameReadyHandler , false);

        Ext.EventManager.on(window, 'beforeunload', implementation.destroy, implementation);
        return implementation;
    }();
    
    MIM = MIF.Manager;
    MIM.showDragMask = MIM.showShims;
    MIM.hideDragMask = MIM.hideShims;
    
    /**
     * Shim all MIF's during a Window drag operation.
     */
    var winDD = Ext.Window.DD;
    Ext.override(winDD, {
       startDrag : winDD.prototype.startDrag.createInterceptor(MIM.showShims),
       endDrag   : winDD.prototype.endDrag.createInterceptor(MIM.hideShims)
    });

    //Previous release compatibility
    Ext.ux.ManagedIFramePanel = MIF.Panel;
    Ext.ux.ManagedIFramePortlet = MIF.Portlet;
    Ext.ux.ManagedIframe = function(el,opt){
        
        var args = Array.prototype.slice.call(arguments, 0),
            el = Ext.get(args[0]),
            config = args[0];

        if (el && el.dom && el.dom.tagName == 'IFRAME') {
            config = args[1] || {};
        } else {
            config = args[0] || args[1] || {};

            el = config.autoCreate ? Ext.get(Ext.DomHelper.append(
                    config.autoCreate.parent || Ext.getBody(), Ext.apply({
                        tag : 'iframe',
                        frameborder : 0,
                        cls : 'x-mif',
                        src : (Ext.isIE && Ext.isSecure)? Ext.SSL_SECURE_URL: 'about:blank'
                    }, config.autoCreate)))
                    : null;

            if(el && config.unsupportedText){
                Ext.DomHelper.append(el.dom.parentNode, {tag:'noframes',html: config.unsupportedText } );
            }
        }
        
        var mif = new MIF.Element(el,true);
        if(mif){
            Ext.apply(mif, {
                disableMessaging : Ext.value(config.disableMessaging , true),
                focusOnLoad : Ext.value(config.focusOnLoad , Ext.isIE),
                eventsFollowFrameLinks : Ext.value(config.eventsFollowFrameLinks ,true),
                loadMask : !!config.loadMask ? Ext.apply({
                            msg : 'Loading..',
                            msgCls : 'x-mask-loading',
                            maskEl : null,
                            hideOnReady : false,
                            disabled : false
                        }, config.loadMask) : false,
                _windowContext : null
                
            });
            
            config.listeners && mif.on(config.listeners);
            
            if(!!config.html){
                mif.update(config.html);
            } else {
                !!config.src && mif.setSrc(config.src);
            }
        }
        
        return mif;   
    };

    /**
     * Internal Error class for ManagedIFrame Components
	 * @class Ext.ux.ManagedIFrame.Error
     * @extends Ext.Error
     * @version 2.1.4 
     * @donate <a target="tag_donate" href="http://donate.theactivegroup.com"><img border="0" src="http://www.paypal.com/en_US/i/btn/x-click-butcc-donate.gif" border="0" alt="Make a donation to support ongoing development"></a>
     * @license <a href="http://www.gnu.org/licenses/gpl.html">GPL 3.0</a> 
     * @author Doug Hendricks. Forum ID: <a href="http://extjs.com/forum/member.php?u=8730">hendricd</a> 
     * @copyright 2007-2010, Active Group, Inc. All rights reserved.
	 * @constructor 
     * @param {String} message
     * @param {Mixed} arg optional argument to include in Error object.
	 */
	Ext.ux.ManagedIFrame.Error = Ext.extend(Ext.Error, {
	    constructor : function(message, arg) {
	        this.arg = arg;
	        Ext.Error.call(this, message);
	    },
	    name : 'Ext.ux.ManagedIFrame'
	});
    
	Ext.apply(Ext.ux.ManagedIFrame.Error.prototype, {
	    lang: {
	        'documentcontext-remove': 'An attempt was made to remove an Element from the wrong document context.',
	        'execscript-secure-context': 'An attempt was made at script execution within a document context with limited access permissions.',
	        'printexception': 'An Error was encountered attempting the print the frame contents (document access is likely restricted).'
	    }
	});
    
    /** @private */
    Ext.onReady(function() {
            // Generate CSS Rules but allow for overrides.
            var CSS = new Ext.ux.ManagedIFrame.CSS(document), rules = [];

            CSS.getRule('.ux-mif-fill')|| (rules.push('.ux-mif-fill{height:100%;width:100%;}'));
            CSS.getRule('.ux-mif-mask-target')|| (rules.push('.ux-mif-mask-target{position:relative;zoom:1;}'));
            CSS.getRule('.ux-mif-el-mask')|| (rules.push(
              '.ux-mif-el-mask {z-index: 100;position: absolute;top:0;left:0;-moz-opacity: 0.5;opacity: .50;*filter: alpha(opacity=50);width: 100%;height: 100%;zoom: 1;} ',
              '.ux-mif-el-mask-msg {z-index: 1;position: absolute;top: 0;left: 0;border:1px solid;background:repeat-x 0 -16px;padding:2px;} ',
              '.ux-mif-el-mask-msg div {padding:5px 10px 5px 10px;border:1px solid;cursor:wait;} '
              ));


            if (!CSS.getRule('.ux-mif-shim')) {
                rules.push('.ux-mif-shim {z-index:8500;position:absolute;top:0px;left:0px;background:transparent!important;overflow:hidden;display:none;}');
                rules.push('.ux-mif-shim-on{width:100%;height:100%;display:block;zoom:1;}');
                rules.push('.ext-ie6 .ux-mif-shim{margin-left:5px;margin-top:3px;}');
            }
            
            if (!CSS.getRule('.x-hide-nosize')){ 
                rules.push ('.x-hide-nosize{height:0px!important;width:0px!important;visibility:hidden!important;border:none!important;zoom:1;}.x-hide-nosize * {height:0px!important;width:0px!important;visibility:hidden!important;border:none!important;zoom:1;}');
            }
  
            !!rules.length && CSS.createStyleSheet(rules.join(' '), 'mifCSS');
            
        });

    /** @sourceURL=<mif.js> */
    Ext.provide && Ext.provide('mif');
})();