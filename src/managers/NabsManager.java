package managers;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import PhDProject.FriendsFamily.Models.Notification;
import PhDProject.FriendsFamily.Models.Result;
import PhDProject.FriendsFamily.Models.User;
import main.NabsDemo;
import masters.calendar.CalendarEvent;
import masters.calendar.GoogleCalendarData;
import masters.models.UpliftedNotification;
import phd.utilities.DateUtility;

public class NabsManager {
	
	private BeadRepoManager repo;
	
	private User selectedUser = null;
	private Notification selectedNotification = null;
	
	private int family = 9;
	private int work = 5;
	private int social = 7;
	
	// temp variables for getting contextual timings for alert
	private Date nextBreak = new Date();
	private Date nextFreePeriod = new Date();
	private Date nextContextRelevant = new Date();
	private String userLocation = "";
	private String userEvent = "";
	
	public ArrayList<Result> results = null;
	public ParamManager pm = null;
	
	/**
	 * Fire all user notifications - given only user value, assume
	 * default variables and rulesets.
	 * @param user
	 */
	public NabsManager(String user){
	  	selectedUser = getUserFromId(user);
	  	for(Notification n: selectedUser.getNotifications()){
	  		System.out.println(n.getId());
	  	}
	  	pm = new ParamManager();
	  	
	  	repo = new BeadRepoManager();
    	repo.activateBead("SenderInfoBead");
    	repo.activateBead("SubjectInfoBead");
    	repo.activateBead("AlertInfoBead");
    	repo.activateBead("UserLocationInfoBead");
    	repo.activateBead("NotificationInfoBead");
    	repo.activateBead("AppInfoBead");
    	repo.initialize();
	  	
    	results = new ArrayList<Result>();	  	
	}	
	
	public NabsManager(String user, int notificationId){
	  	selectedUser = getUserFromId(user);
	  	selectedNotification = getNotificationFromId(notificationId);
	  	
	  	pm = new ParamManager();
	  	
	  	repo = new BeadRepoManager();
    	repo.activateBead("SenderInfoBead");
    	repo.activateBead("SubjectInfoBead");
    	repo.activateBead("AlertInfoBead");
    	repo.activateBead("UserLocationInfoBead");
    	repo.activateBead("NotificationInfoBead");
    	repo.activateBead("AppInfoBead");
    	repo.initialize();
	  	
    	results = new ArrayList<Result>();	  	
	}
	
	/**
	 * Finds a given user in the current user list.
	 * @param id
	 * @return the user object
	 */
	private User getUserFromId(String id){
		for(User user: NabsDemo.users){
			if(user.getId().equals(id)){
				return user;
			}
		}
		return null;
	}
	
	private Notification getNotificationFromId(int id){
		 for(Notification n: selectedUser.getNotifications()){
			 if(n.getId() == id){
				 return n;
			 }
		 }
		 return null;
	}
	
	
	public ArrayList<Result> fireNotifications(){
		
		System.out.println("*******************Firing notification******************************");
		for(Notification n: this.selectedUser.getNotifications()){
			UpliftedNotification nToSend = new UpliftedNotification();
			nToSend.setNotificationId(n.getId());
			nToSend.setSender(n.getSender());
			nToSend.setSubject(n.getSubject().getSubject());
			nToSend.setApp(n.getApp().getName());
			nToSend.setSenderRank(n.getSenderRank());
			nToSend.setSubjectRank(n.getSubjectRank());
			nToSend.setAppRank(n.getAppRank());
			nToSend.setDate(DateUtility.stringToDate(n.getDate()));
			switch(n.getSubject().getSubject()){
			case "family":
			nToSend.setSubjectRank(family);
			break;
			case "work":
				nToSend.setSubjectRank(work);
				break;
			case "social":
				nToSend.setSubjectRank(social);
				break;
			}
	    	repo.activateNotificationListener(nToSend, this);
		}
		return results;
	}

	private void fireSingleNotification(){
  			
		UpliftedNotification nToSend = new UpliftedNotification();
		nToSend.setSender(selectedNotification.getSender());
		nToSend.setSender(selectedNotification.getSender());
		nToSend.setSubject(selectedNotification.getSubject().getSubject());
		nToSend.setApp(selectedNotification.getApp().getName());
		nToSend.setSenderRank(10);
		nToSend.setSubjectRank(selectedNotification.getSubjectRank());
		nToSend.setAppRank(selectedNotification.getAppRank());
		nToSend.setDate(DateUtility.stringToDate(selectedNotification.getDate()));
		switch(selectedNotification.getSubject().getSubject()){
		case "family":
		nToSend.setSubjectRank(family);
		break;
		case "work":
			nToSend.setSubjectRank(work);
			break;
		case "social":
			nToSend.setSubjectRank(social);
			break;
		}
    	repo.activateNotificationListener(nToSend, this);
		
		//fireNotification(n, "Custom");
  		  				
	}
	
	public String getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(String userLocation) {
		this.userLocation = userLocation;
	}

	public String getUserEvent() {
		return userEvent;
	}

	public void setUserEvent(String userEvent) {
		this.userEvent = userEvent;
	}

	public Date getNextBreak() {
		return nextBreak;
	}

	public void setNextBreak(Date nextBreak) {
		this.nextBreak = nextBreak;
	}

	public Date getNextFreePeriod() {
		return nextFreePeriod;
	}

	public void setNextFreePeriod(Date nextFreePeriod) {
		this.nextFreePeriod = nextFreePeriod;
	}

	public Date getNextContextRelevant() {
		return nextContextRelevant;
	}

	public void setNextContextRelevant(Date nextContextRelevant) {
		this.nextContextRelevant = nextContextRelevant;
	}
	
	public User getSelectedUser(){
		return selectedUser;
	}

	public CalendarEvent[] getNotificationEvents(String userId, String notificationDate, NabsManager nm){
		
		
		ArrayList<CalendarEvent> events = null;
		CalendarEvent[] result = null;
		try {
			events = GoogleCalendarData.getNextNEvents(10, DateUtility.stringToDate(notificationDate), nm);
			result = new CalendarEvent[events.size()];
			for(int i=0; i<events.size(); i++){
				result[i] = events.get(i);
			}
		} catch (NumberFormatException | ParseException | IOException e) {
			System.out.println("NabsManager: Error getting notification events.");
		}
		Arrays.sort(result);
		return result;
	}
}
