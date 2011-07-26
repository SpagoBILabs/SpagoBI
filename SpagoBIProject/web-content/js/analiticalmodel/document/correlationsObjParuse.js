function correlationManagerObj(){
  this.correlations = new Array();
  function addCorrelationFunct(corr) {
    this.correlations[this.correlations.length] = corr;
  }
  this.addCorrelation = addCorrelationFunct;
  function getCorrelationFunct(index) {
    return this.correlations[index];
  }
  this.getCorrelation = getCorrelationFunct;
  function setCorrelationFunct(index, corr) {
    this.correlations[index] = corr;
  }
  this.setCorrelation = setCorrelationFunct;
  function deleteCorrelationFunct(index) {
    var prog = 0;
    var tmpCorr = new Array();
    for(i=0; i<this.correlations.length; i++) {
      if(i!=index) {
        tmpCorr[prog] = this.correlations[i];
        prog = prog + 1;
      }
    }
    this.correlations=tmpCorr; 
  }
  this.deleteCorrelation = deleteCorrelationFunct;
  function setPreConditionFunct(index, precondval) {
    this.correlations[index].preCond = precondval; 
  }
  this.setPreCondition = setPreConditionFunct;
  function setPostConditionFunct(index, postcondval) {
    this.correlations[index].postCond = postcondval; 
  }
  this.setPostCondition = setPostConditionFunct;
  function setLogicOperatorFunct(index, logop) {
    this.correlations[index].logicOper = logop; 
  }
  this.setLogicOperator = setLogicOperatorFunct;
  this.correlationExist = correlationExistFunct;
  function correlationExistFunct(idPFath, fOp) {
     var found = false;
     for(i=0; i<this.correlations.length; i++) {
        var corr = this.correlations[i];
        if( (corr.idParFather==idPFath) && (corr.condition==fOp) ) {
          found = true;
        }
     }
     return found; 
  }
}