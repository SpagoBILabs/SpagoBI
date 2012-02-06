package it.eng.spagobi.tools.massiveExport.bo;


public class ProgressThread {

	private Integer progressThreadId; 
	private String userId; 	
	private Integer partial;
	private Integer total;
	private String functionCd;
	private String status;
	private String randomKey;
	private String type;
	
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public ProgressThread( String userId,
			Integer total, 
			String functionCd, 
			String status,
			String randomKey,
			String type) {
		super();
		this.userId = userId;
		this.total = total;
		this.functionCd = functionCd;
		this.status = status;
		this.randomKey = randomKey;
		this.type = type;
		
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

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "userID: "+getUserId()+" functionCd: "+getFunctionCd() + "Partial/Total:"+getPartial()+"/"+getTotal()+" Messgae: "+getStatus()+" Type:"+type;
	}
	
}
