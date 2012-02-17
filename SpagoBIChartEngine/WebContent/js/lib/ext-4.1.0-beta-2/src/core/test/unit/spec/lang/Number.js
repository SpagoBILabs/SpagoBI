describe("Ext.Number", function(){
    var Number = Ext.Number;
    
    describe("constraining a number", function(){
        describe("integers", function(){
            describe("if the number is within the constaints", function(){
                it("should leave the number alone if it is equal to the min and the max", function(){
                    expect(Number.constrain(1, 1, 1)).toEqual(1);
                });
                
                it("should leave the number alone if it is equal to the min", function(){
                    expect(Number.constrain(1, 1, 5)).toEqual(1);
                });
                
                it("should leave the number alone if it is equal to the max", function(){
                    expect(Number.constrain(5, 1, 5)).toEqual(5);
                });
                
                it("should leave the number alone if it is within the min and the max", function(){
                    expect(Number.constrain(3, 1, 5)).toEqual(3);
                });
                
                it("should leave a negative number alone if it is within the min and the max", function(){
                    expect(Number.constrain(-3, -5, -1)).toEqual(-3);
                });
            });
            
            describe("if the number is not within the constraints", function(){
                it("should make the number equal to the min value", function(){
                    expect(Number.constrain(1, 3, 5)).toEqual(3);
                });
                
                it("should make the number equal to the max value", function(){
                    expect(Number.constrain(100, 1, 5)).toEqual(5);
                });
                
                describe("and the number is negative", function(){
                    it("should make the number equal to the min value", function(){
                        expect(Number.constrain(-10, -50, -30)).toEqual(-30);
                    });
                    
                    it("should make the number equal to the max value", function(){
                        expect(Number.constrain(-100, -50, -30)).toEqual(-50);
                    });
                });
            });
        });
        
        describe("floating point numbers", function(){
            describe("if the number is within the constaints", function(){
                it("should leave the number alone", function(){
                    expect(Number.constrain(3.3, 3.1, 3.5)).toEqual(3.3);
                });
                
                it("should leave a negative number alone", function(){
                    expect(Number.constrain(-3.3, -3.5, -3.1)).toEqual(-3.3);
                });
            });
            
            describe("and the number is negative", function(){
                it("should make the number equal to the min value", function(){
                    expect(Number.constrain(-3.3, -3.1, -3)).toEqual(-3.1);
                });
                
                it("should make the number equal to the max value", function(){
                    expect(Number.constrain(-2.1, -3.1, -3)).toEqual(-3);
                });
            });
        });
    });
    
    describe("toFixed", function(){
        
        var f = Number.toFixed;
        
        it("should return a string", function(){
            expect(typeof f(1)).toEqual('string');
        });
        
        it("should default precision to 0", function(){
            expect(f(1.23456)).toEqual('1');
        });
        
        it("should output the correct number of decimal places", function(){
            expect(f(1, 3)).toEqual('1.000');
        });
        
        it("should round correctly", function(){
            expect(f(1.9834657, 1)).toEqual('2.0');
        });
        
        it("should round with negative numbers", function(){
            expect(f(-3.4265, 2)).toEqual('-3.43');
        });
    });

    describe("snap", function(){

        // Params are (value, snapincrement, minValue, maxValue)
        var snap = Number.snap;

        it("should enforce minValue if increment is zero", function(){
            expect(snap(50, 0, 0, 100)).toEqual(50);
        });

        it("should enforce maxValue if increment is zero", function(){
            expect(snap(5000, 0, 0, 100)).toEqual(100);
        });

        it("should enforce minValue if passed", function(){
            expect(snap(0, 2, 1, 100)).toEqual(1);
        });

        it("should not enforce a minimum if no minValue passed", function(){
            expect(snap(21, 2, undefined, 100)).toEqual(22);
        });

        it("should enforce maxValue if passed", function(){
            expect(snap(1000, 2, undefined, 100)).toEqual(100);
        });

        it("should not enforce a maximum if no maxValue passed", function(){
            expect(snap(21, 2, undefined, undefined)).toEqual(22);
        });

        it("should snap to a snap point based upon zero", function(){
            expect(snap(56, 2, 55, 65)).toEqual(56);
        });

        it("should enforce the minValue", function(){
            expect(snap(20, 2, 55, 65)).toEqual(55);
        });

        it("should snap to a snap point based upon zero", function(){
            expect(snap(100, 2, 55, 66)).toEqual(66);
        });

        it("should round to the nearest snap point", function(){
            expect(snap(4, 5, 0, 100)).toEqual(5);
        });

    });

    describe("snapInRange", function(){

        // Params are (value, snapincrement, minValue, maxValue)
        var snap = Number.snapInRange;

        it("should enforce minValue if increment is zero", function(){
            expect(snap(50, 0, 0, 100)).toEqual(50);
        });

        it("should enforce maxValue if increment is zero", function(){
            expect(snap(5000, 0, 0, 100)).toEqual(100);
        });

        it("should enforce minValue if passed", function(){
            expect(snap(0, 2, 1, 100)).toEqual(1);
        });

        it("should not enforce a minimum if no minValue passed", function(){
            expect(snap(21, 2, undefined, 100)).toEqual(22);
        });

        it("should enforce maxValue if passed", function(){
            expect(snap(1000, 2, undefined, 100)).toEqual(100);
        });

        it("should not enforce a maximum if no maxValue passed", function(){
            expect(snap(21, 2, undefined, undefined)).toEqual(22);
        });

        // Valid values are 55, 57, 59, 61, 63, 65
        it("should snap to a snap point based upon the minValue", function(){
            expect(snap(56, 2, 55, 65)).toEqual(57);
        });

        it("should enforce the minValue", function(){
            expect(snap(20, 2, 55, 65)).toEqual(55);
        });

        // Valid values are still 55, 57, 59, 61, 63, 65
        it("should snap to a snap point based upon the minValue even if maxValue is not on a snap point", function(){
            expect(snap(100, 2, 55, 66)).toEqual(67);
        });

        it("should round to the nearest snap point", function(){
            expect(snap(4, 5, 0, 100)).toEqual(5);
        });

    });

});
