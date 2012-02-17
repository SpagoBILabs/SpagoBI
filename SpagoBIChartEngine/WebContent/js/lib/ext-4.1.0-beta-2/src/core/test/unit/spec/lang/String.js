describe("Ext.String", function() {

    describe("ellipsis", function() {
        var ellipsis = Ext.String.ellipsis,
            shortString = "A short string",
            longString  = "A somewhat longer string";
        
        it("should keep short strings intact", function() {
            expect(ellipsis(shortString, 100)).toEqual(shortString);
        });
        
        it("should truncate a longer string", function() {
            expect(ellipsis(longString, 10)).toEqual("A somew...");
        });
        
        describe("word break", function() {
            var longStringWithDot  = "www.sencha.com",
                longStringWithExclamationMark = "Yeah!Yeah!Yeah!",
                longStringWithQuestionMark = "Who?When?What?";
                           
            it("should find a word break on ' '", function() {
                expect(ellipsis(longString, 10, true)).toEqual("A...");
            });      
            
            it("should be able to break on '.'", function() {
                expect(ellipsis(longStringWithDot, 9, true)).toEqual("www...");
            });  
            
            it("should be able to break on '!'", function() {
                expect(ellipsis(longStringWithExclamationMark, 9, true)).toEqual("Yeah...");
            }); 
            
            it("should be able to break on '?'", function() {
                expect(ellipsis(longStringWithQuestionMark, 8, true)).toEqual("Who...");
            });       
        });
    });
    
    describe("escapeRegex", function() {
        var str,
            escapeRegex = Ext.String.escapeRegex;
        
        it("should escape minus", function() {
            str = "12 - 175";
            
            expect(escapeRegex(str)).toEqual("12 \\- 175");
        });
        
        it("should escape dot", function() {
            str = "Brian is in the kitchen.";
            
            expect(escapeRegex(str)).toEqual("Brian is in the kitchen\\.");
        });
        
        it("should escape asterisk", function() {
            str = "12 * 175";
            
            expect(escapeRegex(str)).toEqual("12 \\* 175");
        });
        
        it("should escape plus", function() {
            str = "12 + 175";
            
            expect(escapeRegex(str)).toEqual("12 \\+ 175");
        });
        
        it("should escape question mark", function() {
            str = "What else ?";
            
            expect(escapeRegex(str)).toEqual("What else \\?");
        });
        
        it("should escape caret", function() {
            str = "^^";
            
            expect(escapeRegex(str)).toEqual("\\^\\^");
        });
        
        it("should escape dollar", function() {
            str = "500$";
            
            expect(escapeRegex(str)).toEqual("500\\$");
        });
        
        it("should escape open brace", function() {
            str = "something{stupid";
            
            expect(escapeRegex(str)).toEqual("something\\{stupid");
        });
        
        it("should escape close brace", function() {
            str = "something}stupid";
            
            expect(escapeRegex(str)).toEqual("something\\}stupid");
        });
        
        it("should escape open bracket", function() {
            str = "something[stupid";
            
            expect(escapeRegex(str)).toEqual("something\\[stupid");
        });
        
        it("should escape close bracket", function() {
            str = "something]stupid";
            
            expect(escapeRegex(str)).toEqual("something\\]stupid");
        });
        
        it("should escape open parenthesis", function() {
            str = "something(stupid";
            
            expect(escapeRegex(str)).toEqual("something\\(stupid");
        });
        
        it("should escape close parenthesis", function() {
            str = "something)stupid";
            
            expect(escapeRegex(str)).toEqual("something\\)stupid");
        });
        
        it("should escape vertival bar", function() {
            str = "something|stupid";
            
            expect(escapeRegex(str)).toEqual("something\\|stupid");
        });
        
        it("should escape forward slash", function() {
            str = "something/stupid";
            
            expect(escapeRegex(str)).toEqual("something\\/stupid");
        });
        
        it("should escape backslash", function() {
            str = "something\\stupid";
            
            expect(escapeRegex(str)).toEqual("something\\\\stupid");
        });
    });
    
    describe("htmlEncode", function() {
        var htmlEncode = Ext.String.htmlEncode,
            str;
        
        it("should replace ampersands", function() {
            str = "Fish & Chips";
            
            expect(htmlEncode(str)).toEqual("Fish &amp; Chips");
        });
        
        it("should replace less than", function() {
            str = "Fish > Chips";
            
            expect(htmlEncode(str)).toEqual("Fish &gt; Chips");
        });
        
        it("should replace greater than", function() {
            str = "Fish < Chips";
            
            expect(htmlEncode(str)).toEqual("Fish &lt; Chips");
        });
        
        it("should replace double quote", function() {
            str = 'Fish " Chips';
            
            expect(htmlEncode(str)).toEqual("Fish &quot; Chips");
        });
    });
    
    describe("htmlDecode", function() {
        var htmlDecode = Ext.String.htmlDecode,
            str;
        
        it("should replace ampersands", function() {
            str = "Fish &amp; Chips";
            
            expect(htmlDecode(str)).toEqual("Fish & Chips");
        });
        
        it("should replace less than", function() {
            str = "Fish &gt; Chips";
            
            expect(htmlDecode(str)).toEqual("Fish > Chips");
        });
        
        it("should replace greater than", function() {
            str = "Fish &lt; Chips";
            
            expect(htmlDecode(str)).toEqual("Fish < Chips");
        });
        
        it("should replace double quote", function() {
            str = 'Fish &quot; Chips';
            
            expect(htmlDecode(str)).toEqual('Fish " Chips');
        });
    });
    
    describe("escaping", function() {
        var escape = Ext.String.escape;
        
        it("should leave an empty string alone", function() {
            expect(escape('')).toEqual('');
        });
        
        it("should leave a non-empty string without escapable characters alone", function() {
            expect(escape('Ed')).toEqual('Ed');
        });
        
        it("should correctly escape a double backslash", function() {
            expect(escape("\\")).toEqual("\\\\");
        });
        
        it("should correctly escape a single backslash", function() {
            expect(escape('\'')).toEqual('\\\'');
        });
        
        it("should correctly escape a mixture of escape and non-escape characters", function() {
            expect(escape('\'foo\\')).toEqual('\\\'foo\\\\');
        });
    });
    
    describe("formatting", function() {
        var format = Ext.String.format;
        
        it("should leave a string without format parameters alone", function() {
            expect(format('Ed')).toEqual('Ed');
        });
        
        it("should ignore arguments that don't map to format params", function() {
            expect(format("{0} person", 1, 123)).toEqual("1 person");
        });
        
        it("should accept several format parameters", function() {
            expect(format("{0} person {1}", 1, 'came')).toEqual('1 person came');
        });
    });
    
    describe("leftPad", function() {
        var leftPad = Ext.String.leftPad;
        
        it("should pad the left side of an empty string", function() {
            expect(leftPad("", 5)).toEqual("     ");
        });
        
        it("should pad the left side of a non-empty string", function() {
            expect(leftPad("Ed", 5)).toEqual("   Ed");
        });
        
        it("should not pad a string where the character count already exceeds the pad count", function() {
            expect(leftPad("Abraham", 5)).toEqual("Abraham");
        });
        
        it("should allow a custom padding character", function() {
            expect(leftPad("Ed", 5, "0")).toEqual("000Ed");
        });
    });
    
    describe("when toggling between two values", function() {
        var toggle = Ext.String.toggle;
        
        it("should use the first toggle value if the string is not already one of the toggle values", function() {
            expect(toggle("Aaron", "Ed", "Abe")).toEqual("Ed");
        });
        
        it("should toggle to the second toggle value if the string is currently the first", function() {
            expect(toggle("Ed", "Ed", "Abe")).toEqual("Abe");
        });
        
        it("should toggle to the first toggle value if the string is currently the second", function() {
            expect(toggle("Abe", "Ed", "Abe")).toEqual("Ed");
        });
    });
    
    describe("trimming", function() {
        var trim = Ext.String.trim;
        
        it("should not modify an empty string", function() {
            expect(trim("")).toEqual("");
        });
        
        it("should not modify a string with no whitespace", function() {
            expect(trim("Abe")).toEqual("Abe");
        });
        
        it("should trim a whitespace-only string", function() {
            expect(trim("     ")).toEqual("");
        });
        
        it("should trim leading whitespace", function() {
            expect(trim("  Ed")).toEqual("Ed");
        });
        
        it("should trim trailing whitespace", function() {
            expect(trim("Ed   ")).toEqual("Ed");
        });
        
        it("should trim leading and trailing whitespace", function() {
            expect(trim("   Ed  ")).toEqual("Ed");
        });
        
        it("should not trim whitespace between words", function() {
            expect(trim("Fish and chips")).toEqual("Fish and chips");
            expect(trim("   Fish and chips  ")).toEqual("Fish and chips");
        });
        
        it("should trim tabs", function() {
            expect(trim("\tEd")).toEqual("Ed");
        });
        
        it("should trim a mixture of tabs and whitespace", function() {
            expect(trim("\tEd   ")).toEqual("Ed");
        });
    });
    
    describe("urlAppend", function(){
        var urlAppend = Ext.String.urlAppend;
        
        it("should leave the string untouched if the second argument is empty", function(){
            expect(urlAppend('sencha.com')).toEqual('sencha.com');    
        });
        
        it("should append a ? if one doesn't exist", function(){
            expect(urlAppend('sencha.com', 'foo=bar')).toEqual('sencha.com?foo=bar');
        });
        
        it("should append any new values with & if a ? exists", function(){
            expect(urlAppend('sencha.com?x=y', 'foo=bar')).toEqual('sencha.com?x=y&foo=bar');
        });
    });
    
    describe("capitalize", function(){
        var capitalize = Ext.String.capitalize;
        
        it("should handle an empty string", function(){
            expect(capitalize('')).toEqual('');
        });
        
        it("should capitalize the first letter of the string", function(){
            expect(capitalize('open')).toEqual('Open');
        });
        
        it("should leave the first letter capitalized if it is already capitalized", function(){
            expect(capitalize('Closed')).toEqual('Closed');
        });
        
        it("should capitalize a single letter", function(){
            expect(capitalize('a')).toEqual('A');
        });
        
        it("should capitalize even when spaces are included", function(){
            expect(capitalize('this is a sentence')).toEqual('This is a sentence');
        });
    });

    describe("uncapitalize", function(){
        var uncapitalize = Ext.String.uncapitalize;
        
        it("should handle an empty string", function(){
            expect(uncapitalize('')).toEqual('');
        });
        
        it("should uncapitalize the first letter of the string", function(){
            expect(uncapitalize('Foo')).toEqual('foo');
        });
        
        it("should ignore case in the rest of the string", function() {
            expect(uncapitalize('FooBar')).toEqual('fooBar'); 
        });
        
        it("should leave the first letter uncapitalized if it is already uncapitalized", function(){
            expect(uncapitalize('fooBar')).toEqual('fooBar');
        });
        
        it("should uncapitalize a single letter", function(){
            expect(uncapitalize('F')).toEqual('f');
        });

        it("should uncapitalize even when spaces are included", function(){
            expect(uncapitalize('This is a sentence')).toEqual('this is a sentence');
        });
    });

    describe('splitWords', function () {
        // ensure it works using a direct fn:
        var splitWords = Ext.String.splitWords;
        
        it('should handle no args', function () {
            var words = splitWords();
            expect(Ext.encode(words)).toEqual('[]');
        });
        it('should handle null', function () {
            var words = splitWords(null);
            expect(Ext.encode(words)).toEqual('[]');
        });
        it('should handle an empty string', function () {
            var words = splitWords('');
            expect(Ext.encode(words)).toEqual('[]');
        });
        it('should handle one trimmed word', function () {
            var words = splitWords('foo');
            expect(Ext.encode(words)).toEqual('["foo"]');
        });
        it('should handle one word with spaces around it', function () {
            var words = splitWords(' foo ');
            expect(Ext.encode(words)).toEqual('["foo"]');
        });
        it('should handle two trimmed words', function () {
            var words = splitWords('foo bar');
            expect(Ext.encode(words)).toEqual('["foo","bar"]');
        });
        it('should handle two untrimmed words', function () {
            var words = splitWords('  foo  bar  ');
            expect(Ext.encode(words)).toEqual('["foo","bar"]');
        });
        it('should handle five trimmed words', function () {
            var words = splitWords('foo bar bif boo foobar');
            expect(Ext.encode(words)).toEqual('["foo","bar","bif","boo","foobar"]');
        });
        it('should handle five untrimmed words', function () {
            var words = splitWords(' foo   bar   bif   boo  foobar    \t');
            expect(Ext.encode(words)).toEqual('["foo","bar","bif","boo","foobar"]');
        });
    })
});
