package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import PhDProject.FriendsFamily.Models.Notification;
import PhDProject.FriendsFamily.Models.User;
import controllers.ResultController;
import managers.BeadRepoManager;
import managers.FirebaseManager;
import masters.models.UpliftedNotification;
import phd.utilities.DateUtility;

@SpringBootApplication
@ComponentScan(basePackageClasses = ResultController.class)
public class NabsDemo {
	
	private static String exitFlag = "";
	private static final String EXIT = "exit";
	
	public static ArrayList<User> users;
	private static BeadRepoManager repo;
	
	private static User selectedUser = null;
	
	private static int family = 0;
	private static int work = 0;
	private static int social = 0;
	
	// temp variables for getting contextual timings for alert
	private static Date nextBreak = new Date();
	private static Date nextFreePeriod = new Date();
	private static Date nextContextRelevant = new Date();
	private static String userLocation = "";
	private static String userEvent = "";

	public static void main(String[] args){
		SpringApplication.run(NabsDemo.class, args);
		/*initNabsServer();
		listenForClose();		
		while(!exitFlag.trim().equals(EXIT));*/
	}
	
	@Bean
	public TaskExecutor taskExecutor() {
	    return new SimpleAsyncTaskExecutor(); // Or use another one of your liking
	}
	
	@Bean
	public CommandLineRunner schedulingRunner(TaskExecutor executor) {
	    return new CommandLineRunner() {
	        public void run(String... args) throws Exception {
	            initNabsServer();
	        }
	    };
	}

	private static void initNabsServer(){
		FirebaseManager.getDatabase().child("users/").addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				System.out.println("Received users");
	  			users = new ArrayList<>();
	  			Map<String, String> result = snapshot.getValue(HashMap.class);
	  			for (Map.Entry<String, String> entry : result.entrySet())
	  			{
	  			    try {
						User user = FirebaseManager.convertStringToUser(entry.getValue());
						if(user!=null){
							users.add(user);
						}
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
	  			}
	  			//saveUserList(users);
	  	    	/*repo = new BeadRepoManager();
	  	    	repo.activateBead("SenderInfoBead");
	  	    	repo.activateBead("SubjectInfoBead");
	  	    	repo.activateBead("AlertInfoBead");
	  	    	repo.activateBead("UserLocationInfoBead");
	  	    	repo.activateBead("NotificationInfoBead");
	  	    	repo.activateBead("AppInfoBead");
	  	    	repo.initialize();

	  	    	repo.activateNotificationListener();

	  			subscribeToWebEvents();*/
	  		  }
			@Override public void onCancelled(FirebaseError error) { }
		});
	}
	
	private static void subscribeToWebEvents(){
		subscribeToSelectedUser();
		subscribeToVariableValues();
		//selectedNotificationEvent();
		notificationToFire();
		singleNotificationToFire();
	}
	
	/**
	 * Subscribes to the firebase event of a "selected user" changing on the web interface.
	 * Updates the selected user 
	 */
	private static void subscribeToSelectedUser(){
		FirebaseManager.getDatabase().child("web/selectedUser/id").addValueEventListener( new ValueEventListener() {
	  		  @Override
	  		  public void onDataChange(DataSnapshot snapshot) {
	  			  if(snapshot.getValue() != null){
	  				selectedUser = getUserFromId((String) snapshot.getValue());
	  			  }
	  		  }
	  		  @Override public void onCancelled(FirebaseError error) {}
		});
	}
	
	/**
	 * Finds a given user in the current user list.
	 * @param id
	 * @return the user object
	 */
	private static User getUserFromId(String id){
		for(User user: users){
			if(user.getId().equals(id)){
				return user;
			}
		}
		return null;
	}
	

	private static void subscribeToVariableValues(){
		FirebaseManager.getDatabase().child("web/variable").addValueEventListener( new ValueEventListener() {
	  		  @Override
	  		  public void onDataChange(DataSnapshot snapshot) {
	  			  if(snapshot.getValue() != null){
		  				HashMap subjectValues = snapshot.getValue(HashMap.class);
		  				family = (int) subjectValues.get("family");
		  				work = (int) subjectValues.get("work");
		  				social = (int) subjectValues.get("social");
	  			  }
	  		  }
	  		  @Override public void onCancelled(FirebaseError error) {}
		});
	}
	
	private static void notificationToFire(){
		FirebaseManager.getDatabase().child("web/fire").addValueEventListener( new ValueEventListener() {
	  		  @Override
	  		  public void onDataChange(DataSnapshot snapshot) {
	  			  System.out.println("*******************Firing notification******************************");
	  			  if(snapshot.getValue() != null){
		  				User user = getUserFromId((String) snapshot.getValue());
			  			for(Notification n: user.getNotifications()){
			  				UpliftedNotification nToSend = new UpliftedNotification();
			  				nToSend.setSender(n.getSender());
			  				nToSend.setSubject(n.getSubject().getSubject());
			  				nToSend.setApp(n.getApp().getName());
			  				nToSend.setNotificationId(n.getId());
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
			  				fireNotification(nToSend, "Custom");
			  			}
	  			  }
	  		  }
	  		  @Override public void onCancelled(FirebaseError error) {}
		});
	}

	private static void singleNotificationToFire(){
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
  		  				
  		  				fireNotification(n, "Custom");
  		  				
  		  			}
  	  		  }
  	  		  @Override public void onCancelled(FirebaseError error) {}
  		});
	}
	
	public static void fireNotification(UpliftedNotification customNotification, String type){
		System.out.println("firingNotification");
		switch(type){
		case "Custom":
			/**
			 * For a given notification create a bead group
			 */
			setNewNotification(customNotification);
			break;
		}
	}

	private static void setNewNotification(UpliftedNotification notification){
		FirebaseManager.getDatabase().child("currentnotification/").setValue(notification);
	}

	/*public static String getUserLocation() {
		return userLocation;
	}

	public static void setUserLocation(String userLocation) {
		NabsDemo.userLocation = userLocation;
	}

	public static String getUserEvent() {
		return userEvent;
	}

	public static void setUserEvent(String userEvent) {
		NabsDemo.userEvent = userEvent;
	}

	public static Date getNextBreak() {
		return nextBreak;
	}

	public static void setNextBreak(Date nextBreak) {
		NabsDemo.nextBreak = nextBreak;
	}

	public static Date getNextFreePeriod() {
		return nextFreePeriod;
	}

	public static void setNextFreePeriod(Date nextFreePeriod) {
		NabsDemo.nextFreePeriod = nextFreePeriod;
	}

	public static Date getNextContextRelevant() {
		return nextContextRelevant;
	}

	public static void setNextContextRelevant(Date nextContextRelevant) {
		NabsDemo.nextContextRelevant = nextContextRelevant;
	}
	
	public static User getSelectedUser(){
		return selectedUser;
	}*/
	
	private static void listenForClose() {
		FirebaseManager.getDatabase().child("exitFlag/").addValueEventListener(new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot arg0) {
				if(arg0.getValue()!=null){
					exitFlag = (String) arg0.getValue();
				}
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {}
		});
	}
	
	public static void saveUserList(ArrayList<User> users){
		ArrayList<String> userStrings = new ArrayList<>();
		Firebase database = new Firebase("https://nabsdemo.firebaseio.com/");
		
		/*for(User user: users){
			try {
				userStrings.add(FirebaseManager.convertUserToString(user));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(String userString: userStrings){
			Firebase ref = database.child("users/").push();
			ref.setValue(userString);
		}*/
		
		for(User user: users){
			String baseRef = "web/users/"+user.getId()+"/";
			FirebaseManager.getDatabase().child(baseRef+"id/").setValue(user.getId());
			FirebaseManager.getDatabase().child(baseRef+"favoriteApps/").setValue(user.getFavoriteApps());
			FirebaseManager.getDatabase().child(baseRef+"activities/").setValue(user.getActivities());
			FirebaseManager.getDatabase().child(baseRef+"events/").setValue(user.getEvents());
			FirebaseManager.getDatabase().child(baseRef+"randomChoice/").setValue(user.getRandomChoice());
			FirebaseManager.getDatabase().child(baseRef+"student/").setValue(user.isStudent());
			FirebaseManager.getDatabase().child(baseRef+"stranger/").setValue(user.isStranger());
			FirebaseManager.getDatabase().child(baseRef+"personality/").setValue(user.getPersonality());
			FirebaseManager.getDatabase().child(baseRef+"percentageHome/").setValue(user.getPercentageHome());
			FirebaseManager.getDatabase().child(baseRef+"percentageWork/").setValue(user.getPercentageWork());
			FirebaseManager.getDatabase().child(baseRef+"percentageSocial/").setValue(user.getPercentageSocial());
			FirebaseManager.getDatabase().child(baseRef+"sendingUserIds/").setValue(user.getSendingUserIds());
			String notificationBaseRef = baseRef+"notifications/";
			int id = 0;
			for(Notification n: user.getNotifications()){
				FirebaseManager.getDatabase().child(notificationBaseRef+"/"+id+"/"+"sender").setValue(n.getSender());
				FirebaseManager.getDatabase().child(notificationBaseRef+"/"+id+"/"+"subject").setValue(n.getSubject());
				FirebaseManager.getDatabase().child(notificationBaseRef+"/"+id+"/"+"app").setValue(n.getApp());
				FirebaseManager.getDatabase().child(notificationBaseRef+"/"+id+"/"+"body").setValue(n.getBody());
				FirebaseManager.getDatabase().child(notificationBaseRef+"/"+id+"/"+"date").setValue(n.getDate());
				FirebaseManager.getDatabase().child(notificationBaseRef+"/"+id+"/"+"senderRank").setValue(n.getSenderRank());
				FirebaseManager.getDatabase().child(notificationBaseRef+"/"+id+"/"+"subjectRank").setValue(n.getSubjectRank());
				FirebaseManager.getDatabase().child(notificationBaseRef+"/"+id+"/"+"appRank").setValue(n.getAppRank());
				FirebaseManager.getDatabase().child(notificationBaseRef+"/"+id+"/"+"bodyRank").setValue(n.getBodyRank());
				FirebaseManager.getDatabase().child(notificationBaseRef+"/"+id+"/"+"dateRank").setValue(n.getDateRank());
				id++;
			}		
		}
	}
}
