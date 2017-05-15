package controllers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import PhDProject.FriendsFamily.Models.User;
import main.NabsDemo;
import managers.NabsManager;
import managers.ParamManager;
import managers.SpeechSynthesisManager;
import managers.UnderstandingManager;
import masters.calendar.CalendarEvent;

@CrossOrigin(origins = "*", maxAge=3600)
@RestController
public class ResultController {

    @RequestMapping("/result")
    public ArrayList<PhDProject.FriendsFamily.Models.Result> result(@RequestParam(value="user", defaultValue="user") String user, 
    			@RequestParam(value="notifid", defaultValue="notifId") String notifid) {
    	
    	// todo: additional request to include new values for the rules
    	
    	// create a thread passing in the userid and notifid
    	// thread must have access to static user list
    	
    	// on thread finish - get thread result
    	/*for(User val : NabsDemo.users){
    		System.out.println(val.getId());
    	}*/
    	NabsManager nm = new NabsManager(user);
    	return nm.fireNotifications();
    }
    
    @RequestMapping("/resultparams")
    public ArrayList<PhDProject.FriendsFamily.Models.Result> resultWithParams(@RequestParam(value="user", defaultValue="user") String user, 
    			@RequestParam(value="params", defaultValue="null") String[] params) {
  	
    	
    	NabsManager nm = new NabsManager(user);
    	nm.pm.setAlertParams(params);
    	return nm.fireNotifications();
    }
    
    @RequestMapping("/params")
    public String[] getParams(@RequestParam(value="type", defaultValue="alert") String type) {

    	ParamManager pm = new ParamManager();
    	switch(type){
    	case "alert":
        	return pm.getAlertParams();
    	case "sender":
    		return pm.getSenderParams();
    	case "subject":
    		return pm.getSubjectParams();
    	default:
    		return null;
    	}
    }
    
    @RequestMapping("/users")
    public User[] getParams() {
    	
    	User[] userArray = new User[NabsDemo.users.size()];
    	for(int i=0; i<NabsDemo.users.size(); i++){
    		userArray[i] = NabsDemo.users.get(i);
    	}
    	Arrays.sort(userArray);
    	return userArray;
    }
    
    @RequestMapping("/notificationevents")
    public CalendarEvent[] getNotificationEvents(@RequestParam(value="user", defaultValue="user") String user, 
			@RequestParam(value="date", defaultValue="date") String date) {
    	
    	NabsManager nm = new NabsManager(user);
    	return nm.getNotificationEvents(user, date, nm);
    }
    
    @RequestMapping("/speechcheckcontinue")
    public boolean getNotificationEvents(@RequestParam(value="response", defaultValue="response") String response) {
    	
    	UnderstandingManager um = new UnderstandingManager();
    	return um.needToContinueUnderstanding(response);
    }
    
    @RequestMapping("/texttospeech")
    public InputStream getSpeech(@RequestParam(value="text", defaultValue="text") String text) {
    	
    	SpeechSynthesisManager ssm = new SpeechSynthesisManager();
    	return ssm.getSpeech(text);
    }
}
