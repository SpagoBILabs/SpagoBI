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
			    '\('
			    '\)'
			    "AVG"
			    "SUM"
			    "COUNT"
			    "MIN"
			    "MAX"
			    "GG_between_dates"
			    "\|\|"
			    '[0-9]+'                        				INT   [* %match = parseInt( %match ); *]
			    '[0-9]+\.[0-9]*|[0-9]*\.[0-9]+' 				FLOAT [* %match = parseFloat( %match ); *]
			    '\'[A-Za-z0-9_]+\''								String
			    '[A-Za-z0-9_ ]+'								Identifier
			    ;
			
			/~ Left-associative tokens, lowest precedence ~/
			<  '\+'
			   '\-'
			   ;
			        
			/~ Left-associative tokens, highest precedence ~/
			<  '\*'
			   '/'
			   ;
			
			##
			
			/~ --- Grammar specification --- ~/
			
			p:      numericexpression              
			        | stringexpression
					;
			
			numericexpression:	numericexpression '+' numericexpression        
			       				| numericexpression '-' numericexpression      
								| numericexpression '*' numericexpression      
								| numericexpression '/' numericexpression      
								| '-' numericexpression &'*'   
								| SUM '(' numericexpression ')'
								| COUNT '(' numericexpression ')'  
								| AVG '(' numericexpression ')'  
								| MIN '(' numericexpression ')'
								| MAX '(' numericexpression ')'  
								| '(' numericexpression ')'    
								| INT
								| FLOAT
								| Identifier
								;
				
			stringexpression:	stringexpression '||' stringexpression   
								| '(' stringexpression ')'
								| String
								| Identifier
								;
	    
*/
SQLExpressionParser = {}; 

SQLExpressionParser.module = function(){ 

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
	            return 21;

	        do
	        {

	switch( state )
	{
	    case 0:
	        if( ( info.src.charCodeAt( pos ) >= 9 && info.src.charCodeAt( pos ) <= 10 ) || info.src.charCodeAt( pos ) == 13 ) state = 1;
	        else if( info.src.charCodeAt( pos ) == 40 ) state = 2;
	        else if( info.src.charCodeAt( pos ) == 41 ) state = 3;
	        else if( info.src.charCodeAt( pos ) == 42 ) state = 4;
	        else if( info.src.charCodeAt( pos ) == 43 ) state = 5;
	        else if( info.src.charCodeAt( pos ) == 45 ) state = 6;
	        else if( info.src.charCodeAt( pos ) == 47 ) state = 7;
	        else if( ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) ) state = 8;
	        else if( info.src.charCodeAt( pos ) == 65 || info.src.charCodeAt( pos ) == 97 ) state = 9;
	        else if( info.src.charCodeAt( pos ) == 39 ) state = 18;
	        else if( info.src.charCodeAt( pos ) == 32 ) state = 19;
	        else if( info.src.charCodeAt( pos ) == 46 ) state = 21;
	        else if( info.src.charCodeAt( pos ) == 124 ) state = 23;
	        else if( info.src.charCodeAt( pos ) == 66 || ( info.src.charCodeAt( pos ) >= 68 && info.src.charCodeAt( pos ) <= 76 ) || ( info.src.charCodeAt( pos ) >= 78 && info.src.charCodeAt( pos ) <= 82 ) || ( info.src.charCodeAt( pos ) >= 84 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || info.src.charCodeAt( pos ) == 98 || ( info.src.charCodeAt( pos ) >= 100 && info.src.charCodeAt( pos ) <= 108 ) || ( info.src.charCodeAt( pos ) >= 110 && info.src.charCodeAt( pos ) <= 114 ) || ( info.src.charCodeAt( pos ) >= 116 && info.src.charCodeAt( pos ) <= 122 ) ) state = 30;
	        else if( info.src.charCodeAt( pos ) == 67 || info.src.charCodeAt( pos ) == 99 ) state = 31;
	        else if( info.src.charCodeAt( pos ) == 77 || info.src.charCodeAt( pos ) == 109 ) state = 32;
	        else if( info.src.charCodeAt( pos ) == 83 || info.src.charCodeAt( pos ) == 115 ) state = 33;
	        else state = -1;
	        break;

	    case 1:
	        state = -1;
	        match = 1;
	        match_pos = pos;
	        break;

	    case 2:
	        state = -1;
	        match = 2;
	        match_pos = pos;
	        break;

	    case 3:
	        state = -1;
	        match = 3;
	        match_pos = pos;
	        break;

	    case 4:
	        state = -1;
	        match = 16;
	        match_pos = pos;
	        break;

	    case 5:
	        state = -1;
	        match = 14;
	        match_pos = pos;
	        break;

	    case 6:
	        state = -1;
	        match = 15;
	        match_pos = pos;
	        break;

	    case 7:
	        state = -1;
	        match = 17;
	        match_pos = pos;
	        break;

	    case 8:
	        if( ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) ) state = 8;
	        else if( info.src.charCodeAt( pos ) == 46 ) state = 10;
	        else if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 122 ) ) state = 30;
	        else state = -1;
	        match = 10;
	        match_pos = pos;
	        break;

	    case 9:
	        if( info.src.charCodeAt( pos ) == 86 || info.src.charCodeAt( pos ) == 118 ) state = 20;
	        else if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 85 ) || ( info.src.charCodeAt( pos ) >= 87 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 117 ) || ( info.src.charCodeAt( pos ) >= 119 && info.src.charCodeAt( pos ) <= 122 ) ) state = 30;
	        else state = -1;
	        match = 13;
	        match_pos = pos;
	        break;

	    case 10:
	        if( ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) ) state = 10;
	        else state = -1;
	        match = 11;
	        match_pos = pos;
	        break;

	    case 11:
	        state = -1;
	        match = 9;
	        match_pos = pos;
	        break;

	    case 12:
	        state = -1;
	        match = 12;
	        match_pos = pos;
	        break;

	    case 13:
	        if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 122 ) ) state = 30;
	        else state = -1;
	        match = 4;
	        match_pos = pos;
	        break;

	    case 14:
	        if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 122 ) ) state = 30;
	        else state = -1;
	        match = 8;
	        match_pos = pos;
	        break;

	    case 15:
	        if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 122 ) ) state = 30;
	        else state = -1;
	        match = 7;
	        match_pos = pos;
	        break;

	    case 16:
	        if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 122 ) ) state = 30;
	        else state = -1;
	        match = 5;
	        match_pos = pos;
	        break;

	    case 17:
	        if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 122 ) ) state = 30;
	        else state = -1;
	        match = 6;
	        match_pos = pos;
	        break;

	    case 18:
	        if( ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 122 ) ) state = 25;
	        else state = -1;
	        break;

	    case 19:
	        if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 122 ) ) state = 30;
	        else state = -1;
	        match = 1;
	        match_pos = pos;
	        break;

	    case 20:
	        if( info.src.charCodeAt( pos ) == 71 || info.src.charCodeAt( pos ) == 103 ) state = 13;
	        else if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 70 ) || ( info.src.charCodeAt( pos ) >= 72 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 102 ) || ( info.src.charCodeAt( pos ) >= 104 && info.src.charCodeAt( pos ) <= 122 ) ) state = 30;
	        else state = -1;
	        match = 13;
	        match_pos = pos;
	        break;

	    case 21:
	        if( ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) ) state = 10;
	        else state = -1;
	        break;

	    case 22:
	        if( info.src.charCodeAt( pos ) == 85 || info.src.charCodeAt( pos ) == 117 ) state = 28;
	        else if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 84 ) || ( info.src.charCodeAt( pos ) >= 86 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 116 ) || ( info.src.charCodeAt( pos ) >= 118 && info.src.charCodeAt( pos ) <= 122 ) ) state = 30;
	        else state = -1;
	        match = 13;
	        match_pos = pos;
	        break;

	    case 23:
	        if( info.src.charCodeAt( pos ) == 124 ) state = 11;
	        else state = -1;
	        break;

	    case 24:
	        if( info.src.charCodeAt( pos ) == 88 || info.src.charCodeAt( pos ) == 120 ) state = 14;
	        else if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 87 ) || ( info.src.charCodeAt( pos ) >= 89 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 119 ) || ( info.src.charCodeAt( pos ) >= 121 && info.src.charCodeAt( pos ) <= 122 ) ) state = 30;
	        else state = -1;
	        match = 13;
	        match_pos = pos;
	        break;

	    case 25:
	        if( info.src.charCodeAt( pos ) == 39 ) state = 12;
	        else if( ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 122 ) ) state = 25;
	        else state = -1;
	        break;

	    case 26:
	        if( info.src.charCodeAt( pos ) == 78 || info.src.charCodeAt( pos ) == 110 ) state = 15;
	        else if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 77 ) || ( info.src.charCodeAt( pos ) >= 79 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 109 ) || ( info.src.charCodeAt( pos ) >= 111 && info.src.charCodeAt( pos ) <= 122 ) ) state = 30;
	        else state = -1;
	        match = 13;
	        match_pos = pos;
	        break;

	    case 27:
	        if( info.src.charCodeAt( pos ) == 77 || info.src.charCodeAt( pos ) == 109 ) state = 16;
	        else if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 76 ) || ( info.src.charCodeAt( pos ) >= 78 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 108 ) || ( info.src.charCodeAt( pos ) >= 110 && info.src.charCodeAt( pos ) <= 122 ) ) state = 30;
	        else state = -1;
	        match = 13;
	        match_pos = pos;
	        break;

	    case 28:
	        if( info.src.charCodeAt( pos ) == 78 || info.src.charCodeAt( pos ) == 110 ) state = 29;
	        else if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 77 ) || ( info.src.charCodeAt( pos ) >= 79 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 109 ) || ( info.src.charCodeAt( pos ) >= 111 && info.src.charCodeAt( pos ) <= 122 ) ) state = 30;
	        else state = -1;
	        match = 13;
	        match_pos = pos;
	        break;

	    case 29:
	        if( info.src.charCodeAt( pos ) == 84 || info.src.charCodeAt( pos ) == 116 ) state = 17;
	        else if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 83 ) || ( info.src.charCodeAt( pos ) >= 85 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 115 ) || ( info.src.charCodeAt( pos ) >= 117 && info.src.charCodeAt( pos ) <= 122 ) ) state = 30;
	        else state = -1;
	        match = 13;
	        match_pos = pos;
	        break;

	    case 30:
	        if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 122 ) ) state = 30;
	        else state = -1;
	        match = 13;
	        match_pos = pos;
	        break;

	    case 31:
	        if( info.src.charCodeAt( pos ) == 79 || info.src.charCodeAt( pos ) == 111 ) state = 22;
	        else if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 78 ) || ( info.src.charCodeAt( pos ) >= 80 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 110 ) || ( info.src.charCodeAt( pos ) >= 112 && info.src.charCodeAt( pos ) <= 122 ) ) state = 30;
	        else state = -1;
	        match = 13;
	        match_pos = pos;
	        break;

	    case 32:
	        if( info.src.charCodeAt( pos ) == 65 || info.src.charCodeAt( pos ) == 97 ) state = 24;
	        else if( info.src.charCodeAt( pos ) == 73 || info.src.charCodeAt( pos ) == 105 ) state = 26;
	        else if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 66 && info.src.charCodeAt( pos ) <= 72 ) || ( info.src.charCodeAt( pos ) >= 74 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 98 && info.src.charCodeAt( pos ) <= 104 ) || ( info.src.charCodeAt( pos ) >= 106 && info.src.charCodeAt( pos ) <= 122 ) ) state = 30;
	        else state = -1;
	        match = 13;
	        match_pos = pos;
	        break;

	    case 33:
	        if( info.src.charCodeAt( pos ) == 85 || info.src.charCodeAt( pos ) == 117 ) state = 27;
	        else if( info.src.charCodeAt( pos ) == 32 || ( info.src.charCodeAt( pos ) >= 48 && info.src.charCodeAt( pos ) <= 57 ) || ( info.src.charCodeAt( pos ) >= 65 && info.src.charCodeAt( pos ) <= 84 ) || ( info.src.charCodeAt( pos ) >= 86 && info.src.charCodeAt( pos ) <= 90 ) || info.src.charCodeAt( pos ) == 95 || ( info.src.charCodeAt( pos ) >= 97 && info.src.charCodeAt( pos ) <= 116 ) || ( info.src.charCodeAt( pos ) >= 118 && info.src.charCodeAt( pos ) <= 122 ) ) state = 30;
	        else state = -1;
	        match = 13;
	        match_pos = pos;
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
	        
	switch( match )
	{
	    case 10:
	        {
	         info.att = parseInt( info.att );
	        }
	        break;

	    case 11:
	        {
	         info.att = parseFloat( info.att );
	        }
	        break;

	}


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
	    new Array( 20/* p */, 1 ),
	    new Array( 20/* p */, 1 ),
	    new Array( 18/* numericexpression */, 3 ),
	    new Array( 18/* numericexpression */, 3 ),
	    new Array( 18/* numericexpression */, 3 ),
	    new Array( 18/* numericexpression */, 3 ),
	    new Array( 18/* numericexpression */, 2 ),
	    new Array( 18/* numericexpression */, 4 ),
	    new Array( 18/* numericexpression */, 4 ),
	    new Array( 18/* numericexpression */, 4 ),
	    new Array( 18/* numericexpression */, 4 ),
	    new Array( 18/* numericexpression */, 4 ),
	    new Array( 18/* numericexpression */, 3 ),
	    new Array( 18/* numericexpression */, 1 ),
	    new Array( 18/* numericexpression */, 1 ),
	    new Array( 18/* numericexpression */, 1 ),
	    new Array( 19/* stringexpression */, 3 ),
	    new Array( 19/* stringexpression */, 3 ),
	    new Array( 19/* stringexpression */, 1 ),
	    new Array( 19/* stringexpression */, 1 )
	);

	/* Action-Table */
	var act_tab = new Array(
	    /* State 0 */ new Array( 15/* "-" */,4 , 5/* "SUM" */,5 , 6/* "COUNT" */,6 , 4/* "AVG" */,7 , 7/* "MIN" */,8 , 8/* "MAX" */,9 , 2/* "(" */,10 , 10/* "INT" */,11 , 11/* "FLOAT" */,12 , 13/* "Identifier" */,13 , 12/* "String" */,14 ),
	    /* State 1 */ new Array( 21/* "$" */,0 ),
	    /* State 2 */ new Array( 17/* "/" */,15 , 16/* "*" */,16 , 15/* "-" */,17 , 14/* "+" */,18 , 21/* "$" */,-1 ),
	    /* State 3 */ new Array( 9/* "||" */,19 , 21/* "$" */,-2 ),
	    /* State 4 */ new Array( 15/* "-" */,4 , 5/* "SUM" */,5 , 6/* "COUNT" */,6 , 4/* "AVG" */,7 , 7/* "MIN" */,8 , 8/* "MAX" */,9 , 2/* "(" */,21 , 10/* "INT" */,11 , 11/* "FLOAT" */,12 , 13/* "Identifier" */,22 ),
	    /* State 5 */ new Array( 2/* "(" */,23 ),
	    /* State 6 */ new Array( 2/* "(" */,24 ),
	    /* State 7 */ new Array( 2/* "(" */,25 ),
	    /* State 8 */ new Array( 2/* "(" */,26 ),
	    /* State 9 */ new Array( 2/* "(" */,27 ),
	    /* State 10 */ new Array( 15/* "-" */,4 , 5/* "SUM" */,5 , 6/* "COUNT" */,6 , 4/* "AVG" */,7 , 7/* "MIN" */,8 , 8/* "MAX" */,9 , 2/* "(" */,10 , 10/* "INT" */,11 , 11/* "FLOAT" */,12 , 13/* "Identifier" */,13 , 12/* "String" */,14 ),
	    /* State 11 */ new Array( 21/* "$" */,-14 , 14/* "+" */,-14 , 15/* "-" */,-14 , 16/* "*" */,-14 , 17/* "/" */,-14 , 3/* ")" */,-14 ),
	    /* State 12 */ new Array( 21/* "$" */,-15 , 14/* "+" */,-15 , 15/* "-" */,-15 , 16/* "*" */,-15 , 17/* "/" */,-15 , 3/* ")" */,-15 ),
	    /* State 13 */ new Array( 21/* "$" */,-16 , 14/* "+" */,-16 , 15/* "-" */,-16 , 16/* "*" */,-16 , 17/* "/" */,-16 , 3/* ")" */,-16 , 9/* "||" */,-20 ),
	    /* State 14 */ new Array( 21/* "$" */,-19 , 9/* "||" */,-19 , 3/* ")" */,-19 ),
	    /* State 15 */ new Array( 15/* "-" */,4 , 5/* "SUM" */,5 , 6/* "COUNT" */,6 , 4/* "AVG" */,7 , 7/* "MIN" */,8 , 8/* "MAX" */,9 , 2/* "(" */,21 , 10/* "INT" */,11 , 11/* "FLOAT" */,12 , 13/* "Identifier" */,22 ),
	    /* State 16 */ new Array( 15/* "-" */,4 , 5/* "SUM" */,5 , 6/* "COUNT" */,6 , 4/* "AVG" */,7 , 7/* "MIN" */,8 , 8/* "MAX" */,9 , 2/* "(" */,21 , 10/* "INT" */,11 , 11/* "FLOAT" */,12 , 13/* "Identifier" */,22 ),
	    /* State 17 */ new Array( 15/* "-" */,4 , 5/* "SUM" */,5 , 6/* "COUNT" */,6 , 4/* "AVG" */,7 , 7/* "MIN" */,8 , 8/* "MAX" */,9 , 2/* "(" */,21 , 10/* "INT" */,11 , 11/* "FLOAT" */,12 , 13/* "Identifier" */,22 ),
	    /* State 18 */ new Array( 15/* "-" */,4 , 5/* "SUM" */,5 , 6/* "COUNT" */,6 , 4/* "AVG" */,7 , 7/* "MIN" */,8 , 8/* "MAX" */,9 , 2/* "(" */,21 , 10/* "INT" */,11 , 11/* "FLOAT" */,12 , 13/* "Identifier" */,22 ),
	    /* State 19 */ new Array( 2/* "(" */,35 , 12/* "String" */,14 , 13/* "Identifier" */,36 ),
	    /* State 20 */ new Array( 17/* "/" */,-7 , 16/* "*" */,-7 , 15/* "-" */,-7 , 14/* "+" */,-7 , 21/* "$" */,-7 , 3/* ")" */,-7 ),
	    /* State 21 */ new Array( 15/* "-" */,4 , 5/* "SUM" */,5 , 6/* "COUNT" */,6 , 4/* "AVG" */,7 , 7/* "MIN" */,8 , 8/* "MAX" */,9 , 2/* "(" */,21 , 10/* "INT" */,11 , 11/* "FLOAT" */,12 , 13/* "Identifier" */,22 ),
	    /* State 22 */ new Array( 21/* "$" */,-16 , 14/* "+" */,-16 , 15/* "-" */,-16 , 16/* "*" */,-16 , 17/* "/" */,-16 , 3/* ")" */,-16 ),
	    /* State 23 */ new Array( 15/* "-" */,4 , 5/* "SUM" */,5 , 6/* "COUNT" */,6 , 4/* "AVG" */,7 , 7/* "MIN" */,8 , 8/* "MAX" */,9 , 2/* "(" */,21 , 10/* "INT" */,11 , 11/* "FLOAT" */,12 , 13/* "Identifier" */,22 ),
	    /* State 24 */ new Array( 15/* "-" */,4 , 5/* "SUM" */,5 , 6/* "COUNT" */,6 , 4/* "AVG" */,7 , 7/* "MIN" */,8 , 8/* "MAX" */,9 , 2/* "(" */,21 , 10/* "INT" */,11 , 11/* "FLOAT" */,12 , 13/* "Identifier" */,22 ),
	    /* State 25 */ new Array( 15/* "-" */,4 , 5/* "SUM" */,5 , 6/* "COUNT" */,6 , 4/* "AVG" */,7 , 7/* "MIN" */,8 , 8/* "MAX" */,9 , 2/* "(" */,21 , 10/* "INT" */,11 , 11/* "FLOAT" */,12 , 13/* "Identifier" */,22 ),
	    /* State 26 */ new Array( 15/* "-" */,4 , 5/* "SUM" */,5 , 6/* "COUNT" */,6 , 4/* "AVG" */,7 , 7/* "MIN" */,8 , 8/* "MAX" */,9 , 2/* "(" */,21 , 10/* "INT" */,11 , 11/* "FLOAT" */,12 , 13/* "Identifier" */,22 ),
	    /* State 27 */ new Array( 15/* "-" */,4 , 5/* "SUM" */,5 , 6/* "COUNT" */,6 , 4/* "AVG" */,7 , 7/* "MIN" */,8 , 8/* "MAX" */,9 , 2/* "(" */,21 , 10/* "INT" */,11 , 11/* "FLOAT" */,12 , 13/* "Identifier" */,22 ),
	    /* State 28 */ new Array( 9/* "||" */,19 , 3/* ")" */,42 ),
	    /* State 29 */ new Array( 17/* "/" */,15 , 16/* "*" */,16 , 15/* "-" */,17 , 14/* "+" */,18 , 3/* ")" */,43 ),
	    /* State 30 */ new Array( 17/* "/" */,-6 , 16/* "*" */,-6 , 15/* "-" */,-6 , 14/* "+" */,-6 , 21/* "$" */,-6 , 3/* ")" */,-6 ),
	    /* State 31 */ new Array( 17/* "/" */,-5 , 16/* "*" */,-5 , 15/* "-" */,-5 , 14/* "+" */,-5 , 21/* "$" */,-5 , 3/* ")" */,-5 ),
	    /* State 32 */ new Array( 17/* "/" */,15 , 16/* "*" */,16 , 15/* "-" */,-4 , 14/* "+" */,-4 , 21/* "$" */,-4 , 3/* ")" */,-4 ),
	    /* State 33 */ new Array( 17/* "/" */,15 , 16/* "*" */,16 , 15/* "-" */,-3 , 14/* "+" */,-3 , 21/* "$" */,-3 , 3/* ")" */,-3 ),
	    /* State 34 */ new Array( 9/* "||" */,19 , 21/* "$" */,-17 , 3/* ")" */,-17 ),
	    /* State 35 */ new Array( 2/* "(" */,35 , 12/* "String" */,14 , 13/* "Identifier" */,36 ),
	    /* State 36 */ new Array( 21/* "$" */,-20 , 9/* "||" */,-20 , 3/* ")" */,-20 ),
	    /* State 37 */ new Array( 17/* "/" */,15 , 16/* "*" */,16 , 15/* "-" */,17 , 14/* "+" */,18 , 3/* ")" */,44 ),
	    /* State 38 */ new Array( 17/* "/" */,15 , 16/* "*" */,16 , 15/* "-" */,17 , 14/* "+" */,18 , 3/* ")" */,45 ),
	    /* State 39 */ new Array( 17/* "/" */,15 , 16/* "*" */,16 , 15/* "-" */,17 , 14/* "+" */,18 , 3/* ")" */,46 ),
	    /* State 40 */ new Array( 17/* "/" */,15 , 16/* "*" */,16 , 15/* "-" */,17 , 14/* "+" */,18 , 3/* ")" */,47 ),
	    /* State 41 */ new Array( 17/* "/" */,15 , 16/* "*" */,16 , 15/* "-" */,17 , 14/* "+" */,18 , 3/* ")" */,48 ),
	    /* State 42 */ new Array( 21/* "$" */,-18 , 9/* "||" */,-18 , 3/* ")" */,-18 ),
	    /* State 43 */ new Array( 21/* "$" */,-13 , 14/* "+" */,-13 , 15/* "-" */,-13 , 16/* "*" */,-13 , 17/* "/" */,-13 , 3/* ")" */,-13 ),
	    /* State 44 */ new Array( 21/* "$" */,-8 , 14/* "+" */,-8 , 15/* "-" */,-8 , 16/* "*" */,-8 , 17/* "/" */,-8 , 3/* ")" */,-8 ),
	    /* State 45 */ new Array( 21/* "$" */,-9 , 14/* "+" */,-9 , 15/* "-" */,-9 , 16/* "*" */,-9 , 17/* "/" */,-9 , 3/* ")" */,-9 ),
	    /* State 46 */ new Array( 21/* "$" */,-10 , 14/* "+" */,-10 , 15/* "-" */,-10 , 16/* "*" */,-10 , 17/* "/" */,-10 , 3/* ")" */,-10 ),
	    /* State 47 */ new Array( 21/* "$" */,-11 , 14/* "+" */,-11 , 15/* "-" */,-11 , 16/* "*" */,-11 , 17/* "/" */,-11 , 3/* ")" */,-11 ),
	    /* State 48 */ new Array( 21/* "$" */,-12 , 14/* "+" */,-12 , 15/* "-" */,-12 , 16/* "*" */,-12 , 17/* "/" */,-12 , 3/* ")" */,-12 )
	);

	/* Goto-Table */
	var goto_tab = new Array(
	    /* State 0 */ new Array( 20/* p */,1 , 18/* numericexpression */,2 , 19/* stringexpression */,3 ),
	    /* State 1 */ new Array( ),
	    /* State 2 */ new Array( ),
	    /* State 3 */ new Array( ),
	    /* State 4 */ new Array( 18/* numericexpression */,20 ),
	    /* State 5 */ new Array( ),
	    /* State 6 */ new Array( ),
	    /* State 7 */ new Array( ),
	    /* State 8 */ new Array( ),
	    /* State 9 */ new Array( ),
	    /* State 10 */ new Array( 19/* stringexpression */,28 , 18/* numericexpression */,29 ),
	    /* State 11 */ new Array( ),
	    /* State 12 */ new Array( ),
	    /* State 13 */ new Array( ),
	    /* State 14 */ new Array( ),
	    /* State 15 */ new Array( 18/* numericexpression */,30 ),
	    /* State 16 */ new Array( 18/* numericexpression */,31 ),
	    /* State 17 */ new Array( 18/* numericexpression */,32 ),
	    /* State 18 */ new Array( 18/* numericexpression */,33 ),
	    /* State 19 */ new Array( 19/* stringexpression */,34 ),
	    /* State 20 */ new Array( ),
	    /* State 21 */ new Array( 18/* numericexpression */,29 ),
	    /* State 22 */ new Array( ),
	    /* State 23 */ new Array( 18/* numericexpression */,37 ),
	    /* State 24 */ new Array( 18/* numericexpression */,38 ),
	    /* State 25 */ new Array( 18/* numericexpression */,39 ),
	    /* State 26 */ new Array( 18/* numericexpression */,40 ),
	    /* State 27 */ new Array( 18/* numericexpression */,41 ),
	    /* State 28 */ new Array( ),
	    /* State 29 */ new Array( ),
	    /* State 30 */ new Array( ),
	    /* State 31 */ new Array( ),
	    /* State 32 */ new Array( ),
	    /* State 33 */ new Array( ),
	    /* State 34 */ new Array( ),
	    /* State 35 */ new Array( 19/* stringexpression */,28 ),
	    /* State 36 */ new Array( ),
	    /* State 37 */ new Array( ),
	    /* State 38 */ new Array( ),
	    /* State 39 */ new Array( ),
	    /* State 40 */ new Array( ),
	    /* State 41 */ new Array( ),
	    /* State 42 */ new Array( ),
	    /* State 43 */ new Array( ),
	    /* State 44 */ new Array( ),
	    /* State 45 */ new Array( ),
	    /* State 46 */ new Array( ),
	    /* State 47 */ new Array( ),
	    /* State 48 */ new Array( )
	);



	/* Symbol labels */
	var labels = new Array(
	    "p'" /* Non-terminal symbol */,
	    "WHITESPACE" /* Terminal symbol */,
	    "(" /* Terminal symbol */,
	    ")" /* Terminal symbol */,
	    "AVG" /* Terminal symbol */,
	    "SUM" /* Terminal symbol */,
	    "COUNT" /* Terminal symbol */,
	    "MIN" /* Terminal symbol */,
	    "MAX" /* Terminal symbol */,
	    "||" /* Terminal symbol */,
	    "INT" /* Terminal symbol */,
	    "FLOAT" /* Terminal symbol */,
	    "String" /* Terminal symbol */,
	    "Identifier" /* Terminal symbol */,
	    "+" /* Terminal symbol */,
	    "-" /* Terminal symbol */,
	    "*" /* Terminal symbol */,
	    "/" /* Terminal symbol */,
	    "numericexpression" /* Non-terminal symbol */,
	    "stringexpression" /* Non-terminal symbol */,
	    "p" /* Non-terminal symbol */,
	    "$" /* Terminal symbol */,
	    "GG_between_dates"
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
	        act = 50;
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
	        if( act == 50 )
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
	            
	            while( act == 50 && la != 21 )
	            {
	                if( _dbg_withtrace )
	                    __dbg_print( "\tError recovery\n" +
	                                    "Current lookahead: " + labels[la] + " (" + info.att + ")\n" +
	                                    "Action: " + act + "\n\n" );
	                if( la == -1 )
	                    info.offset++;
	                    
	                while( act == 50 && sstack.length > 0 )
	                {
	                    sstack.pop();
	                    vstack.pop();
	                    
	                    if( sstack.length == 0 )
	                        break;
	                        
	                    act = 50;
	                    for( var i = 0; i < act_tab[sstack[sstack.length-1]].length; i+=2 )
	                    {
	                        if( act_tab[sstack[sstack.length-1]][i] == la )
	                        {
	                            act = act_tab[sstack[sstack.length-1]][i+1];
	                            break;
	                        }
	                    }
	                }
	                
	                if( act != 50 )
	                    break;
	                
	                for( var i = 0; i < rsstack.length; i++ )
	                {
	                    sstack.push( rsstack[i] );
	                    vstack.push( rvstack[i] );
	                }
	                
	                la = __lex( info );
	            }
	            
	            if( act == 50 )
	            {
	                if( _dbg_withtrace )
	                    __dbg_print( "\tError recovery failed, terminating parse process..." );
	                break;
	            }


	            if( _dbg_withtrace )
	                __dbg_print( "\tError recovery succeeded, continuing" );
	        }
	        
	        /*
	        if( act == 50 )
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
	        rval = vstack[ vstack.length - 1 ];
	    }
	    break;
	    case 2:
	    {
	        rval = vstack[ vstack.length - 1 ];
	    }
	    break;
	    case 3:
	    {
	        rval = vstack[ vstack.length - 3 ];
	    }
	    break;
	    case 4:
	    {
	        rval = vstack[ vstack.length - 3 ];
	    }
	    break;
	    case 5:
	    {
	        rval = vstack[ vstack.length - 3 ];
	    }
	    break;
	    case 6:
	    {
	        rval = vstack[ vstack.length - 3 ];
	    }
	    break;
	    case 7:
	    {
	        rval = vstack[ vstack.length - 2 ];
	    }
	    break;
	    case 8:
	    {
	        rval = vstack[ vstack.length - 4 ];
	    }
	    break;
	    case 9:
	    {
	        rval = vstack[ vstack.length - 4 ];
	    }
	    break;
	    case 10:
	    {
	        rval = vstack[ vstack.length - 4 ];
	    }
	    break;
	    case 11:
	    {
	        rval = vstack[ vstack.length - 4 ];
	    }
	    break;
	    case 12:
	    {
	        rval = vstack[ vstack.length - 4 ];
	    }
	    break;
	    case 13:
	    {
	        rval = vstack[ vstack.length - 3 ];
	    }
	    break;
	    case 14:
	    {
	        rval = vstack[ vstack.length - 1 ];
	    }
	    break;
	    case 15:
	    {
	        rval = vstack[ vstack.length - 1 ];
	    }
	    break;
	    case 16:
	    {
	        rval = vstack[ vstack.length - 1 ];
	    }
	    break;
	    case 17:
	    {
	        rval = vstack[ vstack.length - 3 ];
	    }
	    break;
	    case 18:
	    {
	        rval = vstack[ vstack.length - 3 ];
	    }
	    break;
	    case 19:
	    {
	        rval = vstack[ vstack.length - 1 ];
	    }
	    break;
	    case 20:
	    {
	        rval = vstack[ vstack.length - 1 ];
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
	
return{
	
	validateInLineCalculatedField: function (str){

		var error_offsets = new Array(); 
		var error_lookaheads = new Array(); 
		var error_count = 0; 
		if( ( error_count = __parse( str, error_offsets, error_lookaheads ) ) > 0 ) { 
			var errstr = new String(); 
			for( var i = 0; i < error_count; i++ ) 
				errstr += "Parse error in line " + ( str.substr( 0, error_offsets[i] ).match( /\n/g ) ? str.substr( 0, error_offsets[i] ).match( /\n/g ).length : 1 ) + " near \"" + str.substr( error_offsets[i] ) + "\", expecting \"" + error_lookaheads[i].join() + "\"\n" ; 
				return errstr;
		}else{
			return "";
		}
	}

};
}();