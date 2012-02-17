/**
 * @class Ext.dom.Element
 */
Ext.apply(Ext.core.Element, {
    /**
     * Serializes a DOM form into a url encoded string
     * @param {Object} form The form
     * @return {String} The url encoded form
     * @static
     */
    serializeForm: function(form) {
        var fElements = form.elements || (document.forms[form] || Ext.getDom(form)).elements,
            hasSubmit = false,
            encoder = encodeURIComponent,
            name,
            data = '',
            type,
            hasValue;

        Ext.each(fElements, function(element){
            name = element.name;
            type = element.type;

            if (!element.disabled && name) {
                if (/select-(one|multiple)/i.test(type)) {
                    Ext.each(element.options, function(opt){
                        if (opt.selected) {
                            hasValue = opt.hasAttribute ? opt.hasAttribute('value') : opt.getAttributeNode('value').specified;
                            data += Ext.String.format("{0}={1}&", encoder(name), encoder(hasValue ? opt.value : opt.text));
                        }
                    });
                } else if (!(/file|undefined|reset|button/i.test(type))) {
                    if (!(/radio|checkbox/i.test(type) && !element.checked) && !(type == 'submit' && hasSubmit)) {
                        data += encoder(name) + '=' + encoder(element.value) + '&';
                        hasSubmit = /submit/i.test(type);
                    }
                }
            }
        });
        return data.substr(0, data.length - 1);
    }
});
