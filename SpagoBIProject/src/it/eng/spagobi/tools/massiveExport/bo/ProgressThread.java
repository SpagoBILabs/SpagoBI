package it.eng.spagobi.tools.massiveExport.bo;


public class ProgressThread {

	private Integer progressThreadId; 
	private String userId; 	
	private Integer partial;
	private Integer total;
	private String functionCd;
	private String message;
	private String randomKey;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Integer getProgressThreadId() {
		return progressThreadId;
	}
	public void setProgressThreadId(Integer progressThreadId) {
		this.progressThreadId = progressThreadId;
	}
	public Integer getPartial() {
		return partial;
	}
	public void setPartial(Integer partial) {
		this.partial = partial;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public String getFunctionCd() {
		return functionCd;
	}
	public void setFunctionCd(String functionCd) {
		this.functionCd = functionCd;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public ProgressThread( String userId,
			Integer total, 
			String functionCd, 
			String message,
			String randomKey) {
		super();
		this.userId = userId;
		this.total = total;
		this.functionCd = functionCd;
		this.message = message;
		this.randomKey = randomKey;
	}
	public ProgressThread() {
		super();

	}
	public String getRandomKey() {
		return randomKey;
	}
	public void setRandomKey(String randomKey) {
		this.randomKey = randomKey;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "userID: "+getUserId()+" functionCd: "+getFunctionCd() + "Partial/Total:"+getPartial()+"/"+getTotal()+" Messgae: "+getMessage();
	}
	
}
