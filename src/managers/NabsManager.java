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
	
	
	public ArrayList<Result> fireNotifications(){
		
		System.out.println("*******************Firing notification******************************");
		int i = 1;
		for(Notification n: this.selectedUser.getNotifications()){
			UpliftedNotification nToSend = new UpliftedNotification();
			nToSend.setSender(n.getSender());
			nToSend.setSubject(n.getSubject().getSubject());
			nToSend.setApp(n.getApp().getName());
			nToSend.setNotificationId(i);
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
	    	i++;
		}
		return results;
	}

	private void singleNotificationToFire(){
  			FirebaseManager.getDatabase().child("web/fireSingle").addValueEventListener( new ValueEventListener() {
  	  		  @Override
  	  		  public void onDataChange(DataSnapshot snapshot) {
  		  			if(snapshot.getValue()!=null){
  		  				System.out.println("single fire notification");
  		  				HashMap result = snapshot.getValue(HashMap.class);
  		  				UpliftedNotification n = new UpliftedNotification();
  		  				HashMap app = (HashMap) result.get("app");
  		  				HashMap subject = (HashMap) result.get("subject");
  		  				
  		  				n.setNotificationId((Integer) result.get("id"));
  		  				
  		  				n.setSender((String) result.get("sender"));
  		  				n.setSubject((String) subject.get("subject"));
  		  				n.setApp((String) app.get("name"));
  		  				n.setDate(DateUtility.stringToDate((String) result.get("date")));
  		  				
  		  				n.setSenderRank((Integer) result.get("senderRank"));
  		  				n.setAppRank((Integer) result.get("appRank"));
  		  				n.setSubjectRank((Integer) result.get("subjectRank"));
  		  				
  		  				//fireNotification(n, "Custom");
  		  				
  		  			}
  	  		  }
  	  		  @Override public void onCancelled(FirebaseError error) {}
  		});
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
