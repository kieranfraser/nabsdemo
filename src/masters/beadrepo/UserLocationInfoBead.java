package masters.beadrepo;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import managers.FirebaseManager;
import managers.NabsManager;
import masters.calendar.CalendarEvent;
import masters.calendar.GoogleCalendarData;
import masters.inference.EventInference;
import masters.inter.BeadInputInterface;
import masters.inter.BeadOutputInterface;
import masters.models.InfoItemFields;
import masters.models.InformationBead;
import masters.models.Triplet;
import masters.models.UpliftedNotification;

public class UserLocationInfoBead extends InformationBead implements BeadInputInterface, BeadOutputInterface,
Runnable{
	
	private static final long serialVersionUID = 6974917675221221989L;
	
	private UpliftedNotification  notification;
	
	private String userLocation;
	private String calendarLocation;
		
	private List<BeadInputInterface> locationListeners = new ArrayList<BeadInputInterface>();
	
	private NabsManager nm;
	
	public UserLocationInfoBead(){
		ArrayList<String> sendToList = new ArrayList<String>();
		sendToList.add("AlertInfoBead");
		this.setAuthorizationToSendToID(sendToList);
	}

	/**
	 * Add a bead which will listen for push requests.
	 * @param addListener
	 */
	public void addListener(BeadInputInterface bead){
		this.locationListeners.add(bead);
	}
	
	/**
	 * Remove a bead from the listening list.
	 * @param bead
	 */
	public void removeListener(BeadInputInterface bead){
		this.locationListeners.remove(bead);
	}

	/**
	 * Called when updates need to be pushed to other beads.
	 */
	@Override
	public void sendToConsumer(String senderId, Date sentTime, Triplet outputData, NabsManager nm) {
		for(BeadInputInterface listener : locationListeners){
			listener.getEvidence(senderId, sentTime, outputData, nm);
		}
	}

	/**
	 * Changed for NAbSim - get Next Event is drawn from the inferred events generated using
	 * the friends & family data-set.
	 */
	@Override
	public void getEvidence(String senderId, Date sentTime, Triplet inputData, NabsManager nm) {
		System.out.println("Location");
		ObjectMapper mapper = new ObjectMapper();
		try {
			notification = mapper.readValue(inputData.getInformationItem().getInformationValue(),
					UpliftedNotification.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Get the location data
		userLocation = nm.getUserLocation();
		
		CalendarEvent event = null;
		try {
			event = GoogleCalendarData.getNextEvent(notification.getDate(), nm);
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<String> userDetails = EventInference.getCurrentLocationAndEventName(event, notification, nm);
		calendarLocation = userDetails.get(1);
							
		if( (notification.getDate().before(event.getEndDate()) || notification.getDate().equals(event.getEndDate())) 
				&& (notification.getDate().after(event.getStartDate()) || notification.getDate().equals(event.getStartDate())) ){
    		calendarLocation = "event";
    	}
		this.nm = nm;
		this.run();
	}
	
	@Override
	public void inferInfoBeadAttr() {	
		double inferredValue = -1.0;
		System.out.println("userLocation "+userLocation);
		if(userLocation == null){
			userLocation = "unknown";
		}
		if(calendarLocation == null){
			calendarLocation = "unknown";
		}
		System.out.println(calendarLocation);
		/*if(calendarLocation.contains(userLocation) && !userLocation.contains("unknown")){ // if there's an event occurring and the user is attending it
			inferredValue = 1.0;
		}
		else{ 
			inferredValue = 0.0;
		}*/
		
		if(calendarLocation == "event"){
			inferredValue = 1.0;
		}
		else{
			inferredValue = 0.0;
		}
		
		System.out.println("kieran fraser "+calendarLocation+" "+inferredValue);
		
		Triplet operational = new Triplet();
		InfoItemFields info = new InfoItemFields();
		info.setInformationValue(String.valueOf(inferredValue));
		info.setInfoBeadId(notification.getNotificationId());
		operational.setInformationItem(info);
		operational.setDetectionTime(new Date());
		this.setOperational(operational);
	}

	@Override
	public void storeInfoBeadAttr() {
		FirebaseManager.getDatabase().child("BeadRepo/"+
				this.getAttributeValueType()+"/").setValue((InformationBead) this);
	}

	@Override
	public void run() {
		this.activate();		
		inferInfoBeadAttr();
		sendToConsumer(this.getAttributeValueType().toString(), new Date(), this.getOperational(), this.nm);
		storeInfoBeadAttr();
	}

}
