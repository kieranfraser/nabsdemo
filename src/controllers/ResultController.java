package controllers;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import PhDProject.FriendsFamily.Models.Notification;
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
    
    @RequestMapping("/resultsingle")
    public ArrayList<PhDProject.FriendsFamily.Models.Result> resultSingle(
    			@RequestParam(value="user", defaultValue="user") String user, 
    			@RequestParam(value="notificationId", defaultValue="null") int notificationId,
    			@RequestParam(value="ruleParams", defaultValue="null") String[] rules, 
    			@RequestParam(value="subjectParams", defaultValue="null") Double[] subjectRankings,
    			@RequestParam(value="senderParams", defaultValue="null") Double[] senderRankings,
    			@RequestParam(value="appParams", defaultValue="null") Double[] appRankings) {
  	
    	
    	NabsManager nm = new NabsManager(user);
    	nm.pm.setAlertParams(rules);
    	return nm.fireNotifications();
    }
    
    @RequestMapping("/resultforchangedelivery")
    public PhDProject.FriendsFamily.Models.Result resultChangeDelivery(
    			@RequestParam(value="user", defaultValue="user") String user,
    			@RequestParam(value="notificationId", defaultValue="null") int notificationId,
    			@RequestParam(value="ruleParams", defaultValue="null") String[] rules, 
    			@RequestParam(value="notificationFeature", defaultValue="") String feature,
    			@RequestParam(value="ranking", defaultValue="") int ranking) {
  	
    	
    	NabsManager nm = new NabsManager(user, notificationId);
    	nm.pm.setAlertParams(rules);
    	return nm.fireForChangeDelivery(ranking, feature).get(0);
    }
    
    
    @RequestMapping("/beginconvo")
    public MessageResponse beginConvo() {
    	
    	UnderstandingManager ssm = new UnderstandingManager();
    	return ssm.conversation(null, null);
    }
    
    @RequestMapping(method = RequestMethod.POST, value="/continueconvo")
    public MessageResponse continueConvo(@RequestParam(value="input", defaultValue="") String inputText,
    		@RequestBody String convoContextString) {

    	Gson g = new Gson(); 
    	
    	Type type = new TypeToken<Map<String, Object>>(){}.getType();
    	Map<String, Object> convoContext = new HashMap<String, Object>();
    	convoContext = g.fromJson(convoContextString, type);
    	
    	System.out.println(convoContext);
    	UnderstandingManager ssm = new UnderstandingManager();
    	MessageResponse response = ssm.conversation(inputText, convoContext);
    	return response;
	}
}
