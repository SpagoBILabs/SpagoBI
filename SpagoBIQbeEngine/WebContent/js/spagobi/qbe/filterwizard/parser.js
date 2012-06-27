/** SpagoBI, the Open Source Business Intelligence suite

 * © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
 /*
    Default template driver for JS/CC generated parsers running as
    browser-based JavaScript/ECMAScript applications.
    
    This class parses a InLineCalculated field. If an error occurs than the
    expression is not well formed.
      
	The LALR grammar:

/~ --- Token definitions --- ~/

	

		/~ Characters to be ignored ~/
			!	' |\r|\n|\t'
			
			/~ Non-associative tokens ~/
			    "AND"
			    "GROUP"
			    "OR"		
			    '$F{[¿¡¬√ƒ≈∆«»… ÀÃÕŒœ–—“”‘’÷ÿ˛Ÿ⁄€‹›‡·‚„‰ÂÊÁËÈÍÎÏÌÓÔÒÚÛÙıˆ¯ﬁ˘˙˚¸˝A-Za-z0-9_ ]+}'					Identifier
			    ;
			
                  			/~ Left-associative tokens, lowest precedence ~/

			        
			/~ Left-associative tokens, highest precedence ~/

			
			##
			
			/~ --- Grammar specification --- ~/
			
			p: ex;
			
		          	ex:	ex 'OR' ex
			       		| ex 'AND' ex
						| ex 'GROUP' ex  
						| Identifier
							;
				



*/

// create namespace 
boolstaf = {}; 

boolstaf.module = function(){ 
// do NOT access DOM from here; elements don't exist yet 

// private variables 

var expressionNode; 

// ============================================================================================= 





var _dbg_withtrace        = false;
var _dbg_string            = new String();

function __dbg_print( text )
{
    _dbg_string += text + "\n";
}

function __lex( info )
{
    var state        = 0;
    var match        = -1;
    var match_pos    = 0;
    var start        = 0;
    var pos            = info.offset + 1;

    do
    {
        pos--;
        state = 0;
        match = -2;
        start = pos;

        if( info.src.length <= start )
            return 8;

        do
        {

switch( state )
{
    case 0:
        if( ( info.src.charCodeAt( pos ) >= 9 && info.src.charCodeAt( pos ) <= 10 ) || info.src.charCodeAt( pos ) == 13 || info.src.charCodeAt( pos ) == 32 ) state = 1;
        else if( info.src.charCodeAt( pos ) == 36 ) state = 6;
        else if( info.src.charCodeAt( pos ) == 65 || info.src.charCodeAt( pos ) == 97 ) state = 7;
        else if( info.src.charCodeAt( pos ) == 71 || info.src.charCodeAt( pos ) == 103 ) state = 8;
        else if( info.src.charCodeAt( pos ) == 79 || info.src.charCodeAt( pos ) == 111 ) state = 9;
        else state = -1;
        break;

    case 1:
        state = -1;
        match = 1;
        match_pos = pos;
        break;

    case 2:
        state = -1;
        match = 4;
        match_pos = pos;
        break;

    case 3:
        state = -1;
        match = 2;
        match_pos = pos;
        break;

    case 4:
        state = -1;
        match = 5;
        match_pos = pos;
        break;

    case 5:
        state = -1;
        match = 3;
        match_pos = pos;
        break;

    case 6:
        if( info.src.charCodeAt( pos ) == 70 ) state = 10;
        else state = -1;
        break;

    case 7:
        if( info.src.charCodeAt( pos ) == 78 || info.src.charCodeAt( pos ) == 110 ) state = 11;
        else state = -1;
        break;

    case 8:
        if( info.src.charCodeAt( pos ) == 82 || info.src.charCodeAt( pos ) == 114 ) state = 12;
        else state = -1;
        break;

    case 9:
        if( info.src.charCodeAt( pos ) == 82 || info.src.charCodeAt( pos ) == 114 ) state = 2;
        else state = -1;
        break;

    case 10:
        if( info.src.charCodeAt( pos ) == 123 ) state = 13;
        else state = -1;
        break;

    case 11:
        if( info.src.charCodeAt( pos ) == 68 || info.src.charCodeAt( pos ) == 100 ) state = 3;
        else state = -1;
        break;

    case 12:
        if( info.src.charCodeAt( pos ) == 79 || info.src.charCodeAt( pos ) == 111 ) state = 14;
        else state = -1;
        break;

    case 13:
        if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 122 ) || ( info.src.charCodeAt( pos ) >= 192 && info.src.charCodeAt( pos ) <= 214 ) || ( info.src.charCodeAt( pos ) >= 216 && info.src.charCodeAt( pos ) <= 222 ) || ( info.src.charCodeAt( pos ) >= 224 && info.src.charCodeAt( pos ) <= 246 ) || ( info.src.charCodeAt( pos ) >= 248 && info.src.charCodeAt( pos ) <= 254 ) ) state = 15;
        else state = -1;
        break;

    case 14:
        if( info.src.charCodeAt( pos ) == 85 || info.src.charCodeAt( pos ) == 117 ) state = 16;
        else state = -1;
        break;

    case 15:
        if( info.src.charCodeAt( pos ) == 125 ) state = 4;
        else if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 122 ) || ( info.src.charCodeAt( pos ) >= 192 && info.src.charCodeAt( pos ) <= 214 ) || ( info.src.charCodeAt( pos ) >= 216 && info.src.charCodeAt( pos ) <= 222 ) || ( info.src.charCodeAt( pos ) >= 224 && info.src.charCodeAt( pos ) <= 246 ) || ( info.src.charCodeAt( pos ) >= 248 && info.src.charCodeAt( pos ) <= 254 ) ) state = 15;
        else state = -1;
        break;

    case 16:
        if( info.src.charCodeAt( pos ) == 80 || info.src.charCodeAt( pos ) == 112 ) state = 5;
        else state = -1;
        break;

}


            pos++;

        }
        while( state > -1 );

    }
    while( 1 > -1 && match == 1 );

    if( match > -1 )
    {
        info.att = info.src.substr( start, match_pos - start );
        info.offset = match_pos;
        

    }
    else
    {
        info.att = new String();
        match = -1;
    }

    return match;
}


function __parse( src, err_off, err_la )
{
    var        sstack            = new Array();
    var        vstack            = new Array();
    var     err_cnt            = 0;
    var        act;
    var        go;
    var        la;
    var        rval;
    var     parseinfo        = new Function( "", "var offset; var src; var att;" );
    var        info            = new parseinfo();
    
/* Pop-Table */
var pop_tab = new Array(
    new Array( 0/* p' */, 1 ),
    new Array( 7/* p */, 1 ),
    new Array( 6/* ex */, 3 ),
    new Array( 6/* ex */, 3 ),
    new Array( 6/* ex */, 3 ),
    new Array( 6/* ex */, 1 )
);

/* Action-Table */
var act_tab = new Array(
    /* State 0 */ new Array( 5/* "Identifier" */,3 ),
    /* State 1 */ new Array( 8/* "$" */,0 ),
    /* State 2 */ new Array( 3/* "GROUP" */,4 , 2/* "AND" */,5 , 4/* "OR" */,6 , 8/* "$" */,-1 ),
    /* State 3 */ new Array( 8/* "$" */,-5 , 4/* "OR" */,-5 , 2/* "AND" */,-5 , 3/* "GROUP" */,-5 ),
    /* State 4 */ new Array( 5/* "Identifier" */,3 ),
    /* State 5 */ new Array( 5/* "Identifier" */,3 ),
    /* State 6 */ new Array( 5/* "Identifier" */,3 ),
    /* State 7 */ new Array( 3/* "GROUP" */,4 , 2/* "AND" */,5 , 4/* "OR" */,6 , 8/* "$" */,-4 ),
    /* State 8 */ new Array( 3/* "GROUP" */,4 , 2/* "AND" */,5 , 4/* "OR" */,6 , 8/* "$" */,-3 ),
    /* State 9 */ new Array( 3/* "GROUP" */,4 , 2/* "AND" */,5 , 4/* "OR" */,6 , 8/* "$" */,-2 )
);

/* Goto-Table */
var goto_tab = new Array(
    /* State 0 */ new Array( 7/* p */,1 , 6/* ex */,2 ),
    /* State 1 */ new Array( ),
    /* State 2 */ new Array( ),
    /* State 3 */ new Array( ),
    /* State 4 */ new Array( 6/* ex */,7 ),
    /* State 5 */ new Array( 6/* ex */,8 ),
    /* State 6 */ new Array( 6/* ex */,9 ),
    /* State 7 */ new Array( ),
    /* State 8 */ new Array( ),
    /* State 9 */ new Array( )
);



/* Symbol labels */
var labels = new Array(
    "p'" /* Non-terminal symbol */,
    "WHITESPACE" /* Terminal symbol */,
    "AND" /* Terminal symbol */,
    "GROUP" /* Terminal symbol */,
    "OR" /* Terminal symbol */,
    "Identifier" /* Terminal symbol */,
    "ex" /* Non-terminal symbol */,
    "p" /* Non-terminal symbol */,
    "$" /* Terminal symbol */
);


    
    info.offset = 0;
    info.src = src;
    info.att = new String();
    
    if( !err_off )
        err_off    = new Array();
    if( !err_la )
    err_la = new Array();
    
    sstack.push( 0 );
    vstack.push( 0 );
    
    la = __lex( info );

    while( true )
    {
        act = 11;
        for( var i = 0; i < act_tab[sstack[sstack.length-1]].length; i+=2 )
        {
            if( act_tab[sstack[sstack.length-1]][i] == la )
            {
                act = act_tab[sstack[sstack.length-1]][i+1];
                break;
            }
        }

        if( _dbg_withtrace && sstack.length > 0 )
        {
            __dbg_print( "\nState " + sstack[sstack.length-1] + "\n" +
                            "\tLookahead: " + labels[la] + " (\"" + info.att + "\")\n" +
                            "\tAction: " + act + "\n" +
                            "\tSource: \"" + info.src.substr( info.offset, 30 ) + ( ( info.offset + 30 < info.src.length ) ?
                                    "..." : "" ) + "\"\n" +
                            "\tStack: " + sstack.join() + "\n" +
                            "\tValue stack: " + vstack.join() + "\n" );
        }
        
            
        //Panic-mode: Try recovery when parse-error occurs!
        if( act == 11 )
        {
            if( _dbg_withtrace )
                __dbg_print( "Error detected: There is no reduce or shift on the symbol " + labels[la] );
            
            err_cnt++;
            err_off.push( info.offset - info.att.length );            
            err_la.push( new Array() );
            for( var i = 0; i < act_tab[sstack[sstack.length-1]].length; i+=2 )
                err_la[err_la.length-1].push( labels[act_tab[sstack[sstack.length-1]][i]] );
            
            //Remember the original stack!
            var rsstack = new Array();
            var rvstack = new Array();
            for( var i = 0; i < sstack.length; i++ )
            {
                rsstack[i] = sstack[i];
                rvstack[i] = vstack[i];
            }
            
            while( act == 11 && la != 8 )
            {
                if( _dbg_withtrace )
                    __dbg_print( "\tError recovery\n" +
                                    "Current lookahead: " + labels[la] + " (" + info.att + ")\n" +
                                    "Action: " + act + "\n\n" );
                if( la == -1 )
                    info.offset++;
                    
                while( act == 11 && sstack.length > 0 )
                {
                    sstack.pop();
                    vstack.pop();
                    
                    if( sstack.length == 0 )
                        break;
                        
                    act = 11;
                    for( var i = 0; i < act_tab[sstack[sstack.length-1]].length; i+=2 )
                    {
                        if( act_tab[sstack[sstack.length-1]][i] == la )
                        {
                            act = act_tab[sstack[sstack.length-1]][i+1];
                            break;
                        }
                    }
                }
                
                if( act != 11 )
                    break;
                
                for( var i = 0; i < rsstack.length; i++ )
                {
                    sstack.push( rsstack[i] );
                    vstack.push( rvstack[i] );
                }
                
                la = __lex( info );
            }
            
            if( act == 11 )
            {
                if( _dbg_withtrace )
                    __dbg_print( "\tError recovery failed, terminating parse process..." );
                break;
            }


            if( _dbg_withtrace )
                __dbg_print( "\tError recovery succeeded, continuing" );
        }
        
        /*
        if( act == 11 )
            break;
        */
        
        
        //Shift
        if( act > 0 )
        {            
            if( _dbg_withtrace )
                __dbg_print( "Shifting symbol: " + labels[la] + " (" + info.att + ")" );
        
            sstack.push( act );
            vstack.push( info.att );
            
            la = __lex( info );
            
            if( _dbg_withtrace )
                __dbg_print( "\tNew lookahead symbol: " + labels[la] + " (" + info.att + ")" );
        }
        //Reduce
        else
        {        
            act *= -1;
            
            if( _dbg_withtrace )
                __dbg_print( "Reducing by producution: " + act );
            
            rval = void(0);
            
            if( _dbg_withtrace )
                __dbg_print( "\tPerforming semantic action..." );
            
switch( act )
{
   case 0:
    {
        rval = vstack[ vstack.length - 1 ];
    }
    break;
    case 1:
    {
         boolstaf.module.setExpressionNode( vstack[ vstack.length - 1 ] ); 
    }
    break;
    case 2:
    {
         rval = boolstaf.module.createNode( boolstaf.module.NODE_OP, boolstaf.module.OR, 'OR', vstack[ vstack.length - 3 ], vstack[ vstack.length - 1 ]) 
    }
    break;
    case 3:
    {
         rval = boolstaf.module.createNode( boolstaf.module.NODE_OP, boolstaf.module.AND, 'AND', vstack[ vstack.length - 3 ], vstack[ vstack.length - 1 ]) 
    }
    break;
    case 4:
    {
         rval = boolstaf.module.createNode( boolstaf.module.NODE_OP, boolstaf.module.GROUP, 'GROUP', vstack[ vstack.length - 2 ]) 
    }
    break;
    case 5:
    {
         rval = boolstaf.module.createNode( boolstaf.module.NODE_CONST, vstack[ vstack.length - 1 ], vstack[ vstack.length - 1 ]) 
    }
    break;
}



            if( _dbg_withtrace )
                __dbg_print( "\tPopping " + pop_tab[act][1] + " off the stack..." );
                
            for( var i = 0; i < pop_tab[act][1]; i++ )
            {
                sstack.pop();
                vstack.pop();
            }
                                    
            go = -1;
            for( var i = 0; i < goto_tab[sstack[sstack.length-1]].length; i+=2 )
            {
                if( goto_tab[sstack[sstack.length-1]][i] == pop_tab[act][0] )
                {
                    go = goto_tab[sstack[sstack.length-1]][i+1];
                    break;
                }
            }
            
            if( act == 0 )
                break;
                
            if( _dbg_withtrace )
                __dbg_print( "\tPushing non-terminal " + labels[ pop_tab[act][0] ] );
                
            sstack.push( go );
            vstack.push( rval );            
        }
        
        if( _dbg_withtrace )
        {        
            alert( _dbg_string );
            _dbg_string = new String();
        }
    }

    if( _dbg_withtrace )
    {
        __dbg_print( "\nParse complete." );
        alert( _dbg_string );
    }
    
    return err_cnt;
}
				

// ============================================================================================= 


// public space 
return { 
NODE_OP : 1, 
NODE_CONST : 2, 

AND : 1, 
OR : 2, 
GROUP : 3, 

EOF : 9, 
LABELS : new Array( 
"p'" /* Non-terminal symbol */, 
"^" /* Terminal symbol */, 
"(" /* Terminal symbol */, 
")" /* Terminal symbol */, 
"FILTER" /* Terminal symbol */, 
"OR" /* Terminal symbol */, 
"AND" /* Terminal symbol */, 
"e" /* Non-terminal symbol */, 
"p" /* Non-terminal symbol */, 
"$" /* Terminal symbol */ 
), 

getExperssionNode : function() { 
return expressionNode; 
}, 

setExpressionNode : function (node) { 
expressionNode = node; 
}, 

/** 
* @param info is an object with the following members .... 
* 
* - src 
* - tokennum 
* - soffset 
* - eoffset 
* - lexeme 
* - type 
* - label 
* 
**/ 
getToken : function( info ) { 
var labels = this.LABELS; 

if ( typeof info != "object" ) { 
alert('error'); 
return false; 
} 

info.tokennum = info.tokennum || 0; 
info.soffset = info.soffset || 0; 
info.eoffset = info.eoffset || 0; 
info.lexeme = info.lexeme || ""; 

var rawinfo = { 
src : info.src, 
offset : info.eoffset, 
att : info.lexeme
} 

var la = __lex( rawinfo ); 
info.tokennum = info.tokennum + 1; 
info.soffset = info.eoffset; 
info.eoffset = rawinfo.offset; 
info.lexeme = rawinfo.att; 
info.type = la; 
info.label = labels? labels[la]: undefined; 


//return (la === labels.length-1)? false: true; 
return (la === this.EOF)? false: true; 
}, 

parse : function ( src, err_off, err_la ) { 
return __parse( src, err_off, err_la ); 
}, 

createNode : function(type, value, name, childs) { 
  //alert('createNode -> ' + name); 

  var pendingNodes = []; 

  var cls = (type == this.NODE_OP? 'cube': 'operand');
  var node = new Ext.tree.TreeNode({text:name, iconCls: cls}); 
  node.attributes = {type:type, value:value}; 
  //alert('DB1 createNode -> ' + name); 
  for( var i = 3; i < arguments.length; i++ ) { 
    //alert("subnodes -> " + arguments[i].toSource()); 
    var child = arguments[i]; 
    //node.appendChild(arguments[i]);
    //alert(child.attributes['type'] + " - " + child.attributes['value']); 
     
    if(type == this.NODE_OP && value == this.GROUP) { 
      node = child; 
    } else if (type == child.attributes['type'] && value == child.attributes['value'] ) { 
      //alert(child.attributes['type'] + " >-< " + child.attributes['value']); 
      for( var y = 0; y < child.childNodes.length; y++ ) { 
        var tnode = this.copyNode(child.childNodes[y]);
        node.appendChild( tnode );
       
      } 
    } else { 
      node.appendChild(arguments[i]); 
    }     
  } 
  //alert('DB2 createNode -> ' + name); 
  
  return node; 
},

copyNode: function(srcNode) {
  var node;
  var cls = srcNode.attributes['type'] == this.NODE_OP? 'cube': 'operand';
  var node = new Ext.tree.TreeNode({text:srcNode.text, iconCls: cls }); 
  node.attributes = { 
      //icon:'attribute.gif', 
      type: srcNode.attributes['type'], 
      value:srcNode.attributes['value'] 
  }
  
  if(srcNode.childNodes.length > 0) {
      for(var i = 0; i < srcNode.childNodes.length; i++) {
        node.appendChild( this.copyNode(srcNode.childNodes[i]) );
      }
  } 
  
  return node;
} 

}; 
}();