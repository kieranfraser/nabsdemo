package PhDProject.FriendsFamily.Models;

import java.io.Serializable;

public class Sender implements Serializable{
	
	private static final long serialVersionUID = 6347777367001306674L;
	
	/**
	 * Sender identities
	 */
	public final static String STRANGER = "stranger";
	public final static String FRIEND = "friend";
	public final static String COLLEAGUE = "colleague";
	public final static String FAMILY = "family";
	
	private String id;
	
	private String identity;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	
	
	
}
