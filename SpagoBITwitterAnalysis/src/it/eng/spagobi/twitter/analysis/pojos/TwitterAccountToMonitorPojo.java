package it.eng.spagobi.twitter.analysis.pojos;

import java.sql.Timestamp;

public class TwitterAccountToMonitorPojo {

	private long searchID;
	private String username;
	private int followers;
	private java.sql.Timestamp timestamp;

	public TwitterAccountToMonitorPojo(long searchID, String username, int followers, Timestamp timestamp) {
		this.searchID = searchID;
		this.username = username;
		this.followers = followers;
		this.timestamp = timestamp;
	}

	public long getSearchID() {
		return searchID;
	}

	public void setSearchID(long searchID) {
		this.searchID = searchID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getFollowers() {
		return followers;
	}

	public void setFollowers(int followers) {
		this.followers = followers;
	}

	public java.sql.Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(java.sql.Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "TwitterAccountToMonitorPojo [searchID=" + searchID + ", username=" + username + ", followers=" + followers + ", timestamp=" + timestamp + "]";
	}

}
