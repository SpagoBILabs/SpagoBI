describe("Ext.Date", function() {

    var D = Ext.Date,
        parse = function(date, format){
            return D.parse(date, format);    
        },
        
        getValue = function(date, name){
            switch (name) {
                case 'year':
                    return date.getFullYear();
                case 'month':
                    return date.getMonth();
                case 'day':
                    return date.getDate();
                case 'hour':
                    return date.getHours();
                case 'minute':
                    return date.getMinutes();
                case 'seconds':
                    return date.getSeconds();
                case 'milliseconds':
                    return date.getMilliseconds();
            }
        },
        
        expectDate = function(date, cfg){
            for (var key in cfg) {
                if (cfg.hasOwnProperty(key)) {
                    expect(getValue(date, key)).toBe(cfg[key]);
                }
            }
        };
    
    describe("parsing", function(){
       
       describe("using separators", function(){
           it("should work with hyphen separators", function() {
               expectDate(parse('2010-03-04', 'Y-m-d'), {
                   year: 2010,
                   month: 2,
                   day: 4
               });     
           });
           
           it("should work with slash separators", function() {
               expectDate(parse('2010/03/04', 'Y/m/d'), {
                   year: 2010,
                   month: 2,
                   day: 4
               });     
           });
           
           it("should work with space separators", function() {
               expectDate(parse('2010 03 04', 'Y m d'), {
                   year: 2010,
                   month: 2,
                   day: 4
               });     
           });
       });
       
       describe("various parseFormats", function(){
           // these can be expanded a great deal
           
           it("should read am/pm", function() {
               expectDate(parse('2010/01/01 12:45 am', 'Y/m/d G:i a'), {
                   year: 2010,
                   month: 0,
                   day: 1,
                   hour: 0,
                   minute: 45
               });
           });
           
           it("should allow am/pm before minutes", function() {
               expectDate(parse('2010/01/01 am 12:45', 'Y/m/d a G:i'), {
                   year: 2010,
                   month: 0,
                   day: 1,
                   hour: 0,
                   minute: 45
               });
           });
           
           it("should correctly parse ISO format", function() {
               expectDate(parse('2012-01-13T01:00:00', 'c'), {
                   year: 2012,
                   month: 0,
                   day: 13,
                   hour: 1,
                   minute: 0,
                   seconds: 0
               });
               expectDate(parse('2012-01-13T13:00:00', 'c'), {
                   year: 2012,
                   month: 0,
                   day: 13,
                   hour: 13,
                   minute: 0,
                   seconds: 0
               });
           });
       });
        
    });
    
    describe("formatting", function(){
        
    });

});
