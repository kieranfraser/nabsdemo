package PhDProject.FriendsFamily.Models;

import java.io.Serializable;

/**
 * Class to describe the possible subject which could be chosen for the
 * notification. The subject "uplift" value varies depending on various 
 * aspects such as the sender, the dataset chosen.
 * @author kfraser
 *
 */
public class Subject implements Serializable{

	private static final long serialVersionUID = -6719397405726049610L;
	
	public final static String WORK = "work";
	public final static String SOCIAL = "social";
	public final static String INTEREST = "interest";
	public final static String FAMILY = "family";

	private String ground_truth;
	private String subject;
	private String dataset;
	
	private String id;
	
	public String getGround_truth() {
		return ground_truth;
	}
	public void setGround_truth(String ground_truth) {
		this.ground_truth = ground_truth;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getDataset() {
		return dataset;
	}
	public void setDataset(String dataset) {
		this.dataset = dataset;
	}
	@Override
	public String toString() {
		return "\n\n Ground Truth: "+this.ground_truth+"\n Subject: "+this.subject+"\n Data-set: "+this.dataset+"\n";
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public static String getWork() {
		return WORK;
	}
	public static String getSocial() {
		return SOCIAL;
	}
	public static String getInterest() {
		return INTEREST;
	}
	public static String getFamily() {
		return FAMILY;
	}
	
	
}
