Ext.ns("Sbi.locale");

Sbi.locale.dummyFormatter = function(v){return v;};
Sbi.locale.formatters = {
	//number: Sbi.locale.dummyFormatter,
	int: Sbi.locale.dummyFormatter,
	float: Sbi.locale.dummyFormatter,
	string: Sbi.locale.dummyFormatter,		
	date: Sbi.locale.dummyFormatter,		
	boolean: Sbi.locale.dummyFormatter,
	html: Sbi.locale.dummyFormatter,
	inlineBar: Sbi.locale.dummyFormatter,
	inlinePoint: Sbi.locale.dummyFormatter
};


if(Sbi.console.commons.Format){
	if(Sbi.locale.formats) {
		Sbi.locale.formatters.int  = Sbi.console.commons.Format.numberRenderer(Sbi.locale.formats['int']);		
		Sbi.locale.formatters.float  = Sbi.console.commons.Format.numberRenderer(Sbi.locale.formats['float']);		
		Sbi.locale.formatters.string  = Sbi.console.commons.Format.stringRenderer(Sbi.locale.formats['string']);		
		Sbi.locale.formatters.date    = Sbi.console.commons.Format.dateRenderer(Sbi.locale.formats['date']);		
		Sbi.locale.formatters.boolean = Sbi.console.commons.Format.booleanRenderer(Sbi.locale.formats['boolean']);
		Sbi.locale.formatters.html    = Sbi.console.commons.Format.htmlRenderer();
		Sbi.locale.formatters.inlineBar    = Sbi.console.commons.Format.inlineBarRenderer();
		Sbi.locale.formatters.inlinePoint = Sbi.console.commons.Format.inlinePointRenderer();
	} else {
		Sbi.locale.formatters.int  = Sbi.console.commons.Format.numberRenderer( );	
		Sbi.locale.formatters.float  = Sbi.console.commons.Format.numberRenderer( );	
		Sbi.locale.formatters.string  = Sbi.console.commons.Format.stringRenderer( );		
		Sbi.locale.formatters.date    = Sbi.console.commons.Format.dateRenderer( );		
		Sbi.locale.formatters.boolean = Sbi.console.commons.Format.booleanRenderer( );
		Sbi.locale.formatters.html    = Sbi.console.commons.Format.htmlRenderer();
		Sbi.locale.formatters.inlineBar   = Sbi.console.commons.Format.inlineBarRenderer();
		Sbi.locale.formatters.inlinePoint = Sbi.console.commons.Format.inlinePointRenderer();
	}
};


Sbi.locale.localize = function(key) {
	if(!Sbi.locale.ln) return key;
	return Sbi.locale.ln[key] || key;
};

// alias
LN = Sbi.locale.localize;
FORMATTERS = Sbi.locale.formatters;




