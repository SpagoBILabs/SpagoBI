Ext.ns("Sbi.constants");

Sbi.constants.qbe = {
		
	// select clause field types (twins java constants are defined in class ISelectField)
	FIELD_TYPE_SIMPLE: 'simple.field'
	, FIELD_TYPE_CALCULATED: 'calculated.field'
	, FIELD_TYPE_INLINE_CALCULATED: 'inline.calculated.field'
			
	// where clause operand types (twins java constants are defined in class AbstractStatement)
	, OPERAND_TYPE_STATIC_VALUE: 'static.value'
	, OPERAND_TYPE_SUBQUERY: 'subquery'
	, OPERAND_TYPE_PARENT_FIELD: 'parent.query.field'
	, OPERAND_TYPE_SIMPLE_FIELD: 'simple.field'
	, OPERAND_TYPE_CALCULATED_FIELD: 'calculated.field'
	, OPERAND_TYPE_INLINE_CALCULATED_FIELD: 'inline.calculated.field'
				
	// tree field types (twins java constants are defined in class ExtJsQbeTreeBuilder)
	, NODE_TYPE_ENTITY: 'entity'
	, NODE_TYPE_SIMPLE_FIELD: 'field'
	, NODE_TYPE_CALCULATED_FIELD: 'calculatedField'
	, NODE_TYPE_INLINE_CALCULATED_FIELD: 'inLineCalculatedField'	
		
	, WHERE_CLAUSE_COMPARISON_FUNCTIONS: [
	    ['NONE', LN('sbi.qbe.filtergridpanel.foperators.name.none'), LN()],
	    ['EQUALS TO', LN('sbi.qbe.filtergridpanel.foperators.name.eq'),  LN('sbi.qbe.filtergridpanel.foperators.desc.eq')],
	    ['NOT EQUALS TO', LN('sbi.qbe.filtergridpanel.foperators.name.noteq'),  LN('sbi.qbe.filtergridpanel.foperators.desc.noteq')],
	    ['GREATER THAN', LN('sbi.qbe.filtergridpanel.foperators.name.gt'),  LN('sbi.qbe.filtergridpanel.foperators.desc.gt')],
	    ['EQUALS OR GREATER THAN', LN('sbi.qbe.filtergridpanel.foperators.name.eqgt'),  LN('sbi.qbe.filtergridpanel.foperators.desc.eqgt')],
	    ['LESS THAN', LN('sbi.qbe.filtergridpanel.foperators.name.lt'),  LN('sbi.qbe.filtergridpanel.foperators.desc.lt')],
	    ['EQUALS OR LESS THAN', LN('sbi.qbe.filtergridpanel.foperators.name.eqlt'),  LN('sbi.qbe.filtergridpanel.foperators.desc.eqlt')],
	    ['STARTS WITH', LN('sbi.qbe.filtergridpanel.foperators.name.starts'),  LN('sbi.qbe.filtergridpanel.foperators.desc.starts')],
	    ['NOT STARTS WITH', LN('sbi.qbe.filtergridpanel.foperators.name.notstarts'),  LN('sbi.qbe.filtergridpanel.foperators.desc.notstarts')],
	    ['ENDS WITH', LN('sbi.qbe.filtergridpanel.foperators.name.ends'),  LN('sbi.qbe.filtergridpanel.foperators.desc.ends')],
	    ['NOT ENDS WITH', LN('sbi.qbe.filtergridpanel.foperators.name.notends'),  LN('sbi.qbe.filtergridpanel.foperators.desc.notends')],
	    ['CONTAINS', LN('sbi.qbe.filtergridpanel.foperators.name.contains'),  LN('sbi.qbe.filtergridpanel.foperators.desc.contains')],
	    ['NOT CONTAINS', LN('sbi.qbe.filtergridpanel.foperators.name.notcontains'),  LN('sbi.qbe.filtergridpanel.foperators.desc.notcontains')],
	    ['BETWEEN', LN('sbi.qbe.filtergridpanel.foperators.name.between'),  LN('sbi.qbe.filtergridpanel.foperators.desc.between')],
	    ['NOT BETWEEN', LN('sbi.qbe.filtergridpanel.foperators.name.notbetween'),  LN('sbi.qbe.filtergridpanel.foperators.desc.notbetween')],
	    ['IN', LN('sbi.qbe.filtergridpanel.foperators.name.in'),  LN('sbi.qbe.filtergridpanel.foperators.desc.in')],
	    ['NOT IN', LN('sbi.qbe.filtergridpanel.foperators.name.notin'),  LN('sbi.qbe.filtergridpanel.foperators.desc.notin')],
	    ['NOT NULL', LN('sbi.qbe.filtergridpanel.foperators.name.notnull'),  LN('sbi.qbe.filtergridpanel.foperators.desc.notnull')],
	    ['IS NULL', LN('sbi.qbe.filtergridpanel.foperators.name.isnull'),  LN('sbi.qbe.filtergridpanel.foperators.desc.isnull')]
	]

	, HAVING_CLAUSE_COMPARISON_FUNCTIONS : [
        ['NONE', LN('sbi.qbe.filtergridpanel.foperators.name.none'), LN()],
        ['EQUALS TO', LN('sbi.qbe.filtergridpanel.foperators.name.eq'),  LN('sbi.qbe.filtergridpanel.foperators.desc.eq')],
        ['NOT EQUALS TO', LN('sbi.qbe.filtergridpanel.foperators.name.noteq'),  LN('sbi.qbe.filtergridpanel.foperators.desc.noteq')],
        ['GREATER THAN', LN('sbi.qbe.filtergridpanel.foperators.name.gt'),  LN('sbi.qbe.filtergridpanel.foperators.desc.gt')],
        ['EQUALS OR GREATER THAN', LN('sbi.qbe.filtergridpanel.foperators.name.eqgt'),  LN('sbi.qbe.filtergridpanel.foperators.desc.eqgt')],
        ['LESS THAN', LN('sbi.qbe.filtergridpanel.foperators.name.lt'),  LN('sbi.qbe.filtergridpanel.foperators.desc.lt')],
        ['EQUALS OR LESS THAN', LN('sbi.qbe.filtergridpanel.foperators.name.eqlt'),  LN('sbi.qbe.filtergridpanel.foperators.desc.eqlt')],
        ['STARTS WITH', LN('sbi.qbe.filtergridpanel.foperators.name.starts'),  LN('sbi.qbe.filtergridpanel.foperators.desc.starts')],
        ['NOT STARTS WITH', LN('sbi.qbe.filtergridpanel.foperators.name.notstarts'),  LN('sbi.qbe.filtergridpanel.foperators.desc.notstarts')],
        ['ENDS WITH', LN('sbi.qbe.filtergridpanel.foperators.name.ends'),  LN('sbi.qbe.filtergridpanel.foperators.desc.ends')],
        ['NOT ENDS WITH', LN('sbi.qbe.filtergridpanel.foperators.name.notends'),  LN('sbi.qbe.filtergridpanel.foperators.desc.notends')],
        ['CONTAINS', LN('sbi.qbe.filtergridpanel.foperators.name.contains'),  LN('sbi.qbe.filtergridpanel.foperators.desc.contains')],
        ['NOT CONTAINS', LN('sbi.qbe.filtergridpanel.foperators.name.notcontains'),  LN('sbi.qbe.filtergridpanel.foperators.desc.notcontains')],        
        ['BETWEEN', LN('sbi.qbe.filtergridpanel.foperators.name.between'),  LN('sbi.qbe.filtergridpanel.foperators.desc.between')],
        ['NOT BETWEEN', LN('sbi.qbe.filtergridpanel.foperators.name.notbetween'),  LN('sbi.qbe.filtergridpanel.foperators.desc.notbetween')],
        ['IN', LN('sbi.qbe.filtergridpanel.foperators.name.in'),  LN('sbi.qbe.filtergridpanel.foperators.desc.in')],
        ['NOT IN', LN('sbi.qbe.filtergridpanel.foperators.name.notin'),  LN('sbi.qbe.filtergridpanel.foperators.desc.notin')],        
        ['NOT NULL', LN('sbi.qbe.filtergridpanel.foperators.name.notnull'),  LN('sbi.qbe.filtergridpanel.foperators.desc.notnull')],
        ['IS NULL', LN('sbi.qbe.filtergridpanel.foperators.name.isnull'),  LN('sbi.qbe.filtergridpanel.foperators.desc.isnull')]
    ]
	
	, HAVING_CLAUSE_AGGREGATION_FUNCTION: [
        ['NONE', LN('sbi.qbe.selectgridpanel.aggfunc.name.none'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.none')],
        ['SUM', LN('sbi.qbe.selectgridpanel.aggfunc.name.sum'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.sum')],
        ['AVG', LN('sbi.qbe.selectgridpanel.aggfunc.name.avg'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.avg')],
        ['MAX', LN('sbi.qbe.selectgridpanel.aggfunc.name.max'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.max')],
        ['MIN', LN('sbi.qbe.selectgridpanel.aggfunc.name.min'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.min')],
        ['COUNT', LN('sbi.qbe.selectgridpanel.aggfunc.name.count'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.count')],
        ['COUNT_DISTINCT', LN('sbi.qbe.selectgridpanel.aggfunc.name.countdistinct'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.countdistinct')]
     ] 

	, SELECT_CLAUSE_AGGREGATION_FUNCTION : [
        ['NONE', LN('sbi.qbe.selectgridpanel.aggfunc.name.none'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.none')],
        ['SUM', LN('sbi.qbe.selectgridpanel.aggfunc.name.sum'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.sum')],
        ['AVG', LN('sbi.qbe.selectgridpanel.aggfunc.name.avg'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.avg')],
        ['MAX', LN('sbi.qbe.selectgridpanel.aggfunc.name.max'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.max')],
        ['MIN', LN('sbi.qbe.selectgridpanel.aggfunc.name.min'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.min')],
        ['COUNT', LN('sbi.qbe.selectgridpanel.aggfunc.name.count'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.count')],
        ['COUNT_DISTINCT', LN('sbi.qbe.selectgridpanel.aggfunc.name.countdistinct'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.countdistinct')]
    ] 

	, INLINE_CALCULATED_FIELD_ARITHMETIC_FUNCTIONS : [
        {
        	text: '+'
            , qtip: LN('sbi.qbe.selectgridpanel.func.sum.tip')
            , type: 'function'
            , value: Ext.util.Format.htmlEncode('+')
            , alias: Ext.util.Format.htmlEncode('+')
        }, {
            text: '-' 
            , qtip: LN('sbi.qbe.selectgridpanel.func.difference.tip')
            , type: 'function'
            , value: Ext.util.Format.htmlEncode('-')
            , alias: Ext.util.Format.htmlEncode('-')
        }, {
            text: '*'
            , qtip: LN('sbi.qbe.selectgridpanel.func.multiplication.tip')
            , type: 'function'
            , value: Ext.util.Format.htmlEncode('*')
            , alias: Ext.util.Format.htmlEncode('*')
         }, {
            text: '/'
            , qtip: LN('sbi.qbe.selectgridpanel.func.division.tip')
            , type: 'function'
            , value: Ext.util.Format.htmlEncode('/')
            , alias: Ext.util.Format.htmlEncode('/')
         }, {
            text: '||'
         	, qtip: LN('sbi.qbe.selectgridpanel.func.pipe.tip')
         	, type: 'function'
         	, value: Ext.util.Format.htmlEncode('||')
         	, alias: Ext.util.Format.htmlEncode('||')
         }, {
            text: '('
         	, qtip: LN('sbi.qbe.selectgridpanel.func.openpar.tip')
         	, type: 'function'
         	, value: Ext.util.Format.htmlEncode('(')
            , alias: Ext.util.Format.htmlEncode('(')
         }, {
            text: ')'
            , qtip: LN('sbi.qbe.selectgridpanel.func.closepar.tip')
            , type: 'function'
            , value: Ext.util.Format.htmlEncode(')')
            , alias: Ext.util.Format.htmlEncode(')')
         }
    ]

	, INLINE_CALCULATED_FIELD_AGGREGATIN_FUNCTIONS : [
       {
    	   text: 'SUM'
           , qtip: LN('sbi.qbe.selectgridpanel.aggfunc.desc.sum')
           , type: 'function'
           , value: Ext.util.Format.htmlEncode('SUM(op1)')
           , alias: Ext.util.Format.htmlEncode('SUM(op1)')
           , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpSum')}]
       }, {
    	   text: 'MIN'  
           , qtip: LN('sbi.qbe.selectgridpanel.aggfunc.desc.min')
           , type: 'function'
           , value: Ext.util.Format.htmlEncode('MIN(op1)')
           , alias: Ext.util.Format.htmlEncode('MIN(op1)')
           , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpMin')}]
       }, {
           text: 'MAX' 
           , qtip: LN('sbi.qbe.selectgridpanel.aggfunc.desc.max')
           , type: 'function'
           , value: Ext.util.Format.htmlEncode('MAX(op1)')
           , alias: Ext.util.Format.htmlEncode('MAX(op1)')
           , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpMax')}]
       }, {
           text: 'COUNT'
           , qtip: LN('sbi.qbe.selectgridpanel.aggfunc.desc.count')
           , type: 'function'
           , value: Ext.util.Format.htmlEncode('COUNT(op1)')
           , alias: Ext.util.Format.htmlEncode('COUNT(op1)')
           , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpCount')}]
       }, {
           text: 'COUNT_DISTINCT'
           , qtip: LN('sbi.qbe.selectgridpanel.aggfunc.desc.countdistinct')
           , type: 'function'
           , value: Ext.util.Format.htmlEncode('COUNT(DISTINCT op1)')
           , alias: Ext.util.Format.htmlEncode('COUNT(DISTINCT op1)')
           , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpCountDist')}]
       }, {
           text: 'AVG'
           , qtip: LN('sbi.qbe.selectgridpanel.aggfunc.desc.avg')
           , type: 'function'
           , value: Ext.util.Format.htmlEncode('AVG(op1)')
           , alias: Ext.util.Format.htmlEncode('AVG(op1)')
           , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpAVG')}]
       }
    ]
                		
    //, dateFunctions = [
	, INLINE_CALCULATED_FIELD_DATE_FUNCTIONS : [
       {
           text: 'GG_between_dates'
           , qtip: LN('sbi.qbe.selectgridpanel.datefunc.desc.ggbetweendates')
           , type: 'function'
           , value: Ext.util.Format.htmlEncode('GG_between_dates(op1,op2)')
           , alias: Ext.util.Format.htmlEncode('GG_between_dates(op1,op2)')
           , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate1')}
           , {label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate2')}]
       }, {
           text: 'MM_between_dates'
           , qtip: LN('sbi.qbe.selectgridpanel.datefunc.desc.mmbetweendates')
           , type: 'function'
           , value: Ext.util.Format.htmlEncode('MM_between_dates(op1,op2)')
           , alias: Ext.util.Format.htmlEncode('MM_between_dates(op1,op2)')
           , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate1')}
           , {label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate2')}]
       },{
    	   text: 'AA_between_dates'
           , qtip: LN('sbi.qbe.selectgridpanel.datefunc.desc.aabetweendates')
           , type: 'function'
           , value: Ext.util.Format.htmlEncode('AA_between_dates(op1,op2)')
           , alias: Ext.util.Format.htmlEncode('AA_between_dates(op1,op2)')
           , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate1')}
           , {label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate2')}]
       }, {
           text: 'GG_up_today'
           , qtip: LN('sbi.qbe.selectgridpanel.datefunc.desc.gguptoday')
           , type: 'function'
           , value: Ext.util.Format.htmlEncode('GG_up_today(op1)')
           , alias: Ext.util.Format.htmlEncode('GG_up_today(op1)')
           , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate')}]
        }, {
           text: 'MM_up_today'
           , qtip: LN('sbi.qbe.selectgridpanel.datefunc.desc.mmuptoday')
           , type: 'function'
           , value: Ext.util.Format.htmlEncode('MM_up_today(op1)')
           , alias: Ext.util.Format.htmlEncode('MM_up_today(op1)')
           , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate')}]
        }, {
           text: 'AA_up_today'
           , qtip: LN('sbi.qbe.selectgridpanel.datefunc.desc.aauptoday')
           , type: 'function'
           , value: Ext.util.Format.htmlEncode('AA_up_today(op1)')
           , alias: Ext.util.Format.htmlEncode('AA_up_today(op1)')
           , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate')}]
        }
    ]
};

Sbi.constants.worksheet = {
		
};

Sbi.constants.formviewer = {
		
};