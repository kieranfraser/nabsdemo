package controllers;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import managers.NabsManager;
import managers.ParamManager;

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
}
