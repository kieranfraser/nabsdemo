package PhDProject.FriendsFamily.Models;

import java.io.Serializable;
import java.util.Date;

import phd.utilities.DateUtility;

public class Notification implements Serializable, Comparable<Notification>{
	
	private static final long serialVersionUID = 2628171005311587410L;
	
	private String dbId;

	private int id;
	
	private String sender;

	private Subject subject;

	private MobileApp app;
	
	private String body;
	private String date;
	
	private int senderRank;
	private int subjectRank;
	private int appRank;
	private int bodyRank;
	private int dateRank;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public Subject getSubject() {
		return subject;
	}
	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	public MobileApp getApp() {
		return app;
	}
	public void setApp(MobileApp app) {
		this.app = app;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getSenderRank() {
		return senderRank;
	}
	public void setSenderRank(int senderRank) {
		this.senderRank = senderRank;
	}
	public int getSubjectRank() {
		return subjectRank;
	}
	public void setSubjectRank(int subjectRank) {
		this.subjectRank = subjectRank;
	}
	public int getAppRank() {
		return appRank;
	}
	public void setAppRank(int appRank) {
		this.appRank = appRank;
	}
	public int getBodyRank() {
		return bodyRank;
	}
	public void setBodyRank(int bodyRank) {
		this.bodyRank = bodyRank;
	}
	public int getDateRank() {
		return dateRank;
	}
	public void setDateRank(int dateRank) {
		this.dateRank = dateRank;
	}
	@Override
	public String toString() {
		return "Notification "+this.id+":\n"
				+"\nSender: "+this.sender+"\n"
				+"Rank:"+this.senderRank+"\n"
				+"Subject: "+this.subject+"\n"
				+"App: "+this.app+"\n"
				+"Date: "+this.date+"\n\n********************************\n";
	}
	public String getDbId() {
		return dbId;
	}
	public void setDbId(String dbId) {
		this.dbId = dbId;
	}
	@Override
	public int compareTo(Notification notification) {
		return DateUtility.stringToDate(this.date).compareTo(DateUtility.stringToDate(notification.date));
	}	
}
