var nsName="Sbi.tools.dataset.cometd";
//create namespace
var ns=(function() {
    var o=nsName.split(".");
    var last=window;
    for (var i=0;i<o.length;i++) {
        var pref=o[i];
        if (last[pref]===undefined) {
            var prefO={}
            last[pref]=prefO;
        }
        last=last[pref];
    }
    return last;
})(nsName);

ns.subscribe = function (config) {
    var $=jQuery;
    var cometd=$.cometd;
    
    var channel='/'+Sbi.user.userId+'/dataset/'+config.dsLabel+'/'+config.listenerId;

     // Function that manages the connection status with the Bayeux server
    var _connected = false;
    function _metaConnect(message) {
        if (cometd.isDisconnected()) {
            _connected = false;
            if (config.connectionClosed!=null) {
                config.connectionClosed();
            }
            return;
        }

        var wasConnected = _connected;
        _connected = message.successful === true;
        if (!wasConnected && _connected) {
            if (config.connectionEstablished!=null) {
                config.connectionEstablished();
            }
        } else if (wasConnected && !_connected) {
            if (config.connectionBroken!=null) {
                config.connectionBroken();
            }
        }   
    }

     // Function invoked when first contacting the server and
    // when the server has lost the state of this client
    function _metaHandshake(handshake) {
        if (handshake.successful === true) {
            cometd.batch(function() {
                //example of channel
                cometd.subscribe(channel, function(message) {
                    var callback=config.messageReceived || ns.updateStore;
                    callback(message,config.store);
                });
            });
        }
    }

     // Disconnect when the page unloads
    $(window).unload(function() {
        cometd.disconnect(true);
    });

    var cometURL = location.protocol + "//" + location.host + config.contextPath + "/cometd";
    cometd.configure({
        url: cometURL
    });

    cometd.addListener('/meta/handshake', _metaHandshake);
    cometd.addListener('/meta/connect', _metaConnect);

     //only as an example
    cometd.handshake({
        ext: {
        	'userChannel':channel
        }
    });
};

/**
* s: Store, data: data from cometd server message
*/
ns.updateStore=function(message,s) {

    var data=JSON.parse(message.data);

    var getIdColumn=function() {
        for (var i=0;i<s.fields.getCount();i++) {
            var field=s.fields.get(i);
            if (field.name==='id' || field.header==='id') {
                return field.name;
            }
        }
        return null;
    };


    var idColumn=getIdColumn(); 
    if (idColumn===null) {
        //no update
        return null;
    }
    //added
    var toAdd=[];
    for (var i=0;i<data.added.length;i++) {
        var addRec=data.added[i];
        toAdd.push(new s.recordType(addRec));
    }
    s.add(toAdd);

    //updated O(n), can be done in O(1) with id property set
    for (var i=0;i<data.updated.length;i++) {
        var updRec=data.updated[i];
        //if server paginated then it can not find the record 
        for (var j=0;j<s.getCount();j++) {
            var toUpd=s.getAt(j);
            if (toUpd.get(idColumn)!==updRec[idColumn]) {
                continue;
            }

            var newRec=new s.recordType(updRec);

            //found, update all fields
            for (var k=0;k<toUpd.fields.getCount();k++) {
                var field=toUpd.fields.get(k);
                if (updRec[field.name]===undefined) {
                    //there could be a value of record (recNo for example) not defined in update
                    continue;
                }

                //take the object value from the created record
                var valueUpdate=newRec.get(field.name);
                if (field.type==="date" && typeof(updRec[field.name])==='string') {
                    //date case: parse the date string to date object
                    valueUpdate=Date.parseDate(updRec[field.name],field.dateFormat );
                }
                toUpd.set(field.name,valueUpdate);
            }
            toUpd.commit();
        }
    }

    //deleted O(n), can be done in O(1) with id property set
    for (var i=0;i<data.deleted.length;i++) {
        var delRec=data.deleted[i];
        //if server paginated then it can not find the record 
        for (var j=0;j<s.getCount();j++) {
            var toDel=s.getAt(j);
            if (toDel.get(idColumn)!==delRec[idColumn]) {
                continue;
            }
            //found, remove toDel
            s.removeAt(j);
        }
    }            
};
