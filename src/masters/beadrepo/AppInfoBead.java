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
import masters.inter.BeadInputInterface;
import masters.inter.BeadOutputInterface;
import masters.models.InfoItemFields;
import masters.models.InformationBead;
import masters.models.Triplet;
import masters.models.UpliftedNotification;

public class AppInfoBead extends InformationBead implements BeadInputInterface,
BeadOutputInterface, Runnable{

	private static final long serialVersionUID = -8726952894217614500L;
	
	ArrayList<CalendarEvent> events;
	private UpliftedNotification  notification;
	private List<BeadInputInterface> appListeners = new ArrayList<BeadInputInterface>();
	private NabsManager nm;
	
	public AppInfoBead(){
		ArrayList<String> sendToList = new ArrayList<String>();
		sendToList.add("AlertInfoBead");
		this.setAuthorizationToSendToID(sendToList);
	}

	/**
	 * Add a bead which will listen for push requests.
	 * @param addListener
	 */
	public void addListener(BeadInputInterface bead){
		this.appListeners.add(bead);
	}
	
	/**
	 * Remove a bead from the listening list.
	 * @param bead
	 */
	public void removeListener(BeadInputInterface bead){
		this.appListeners.remove(bead);
	}
	
	/**
	 * Called when updates need to be pushed to other beads.
	 */
	@Override
	public void sendToConsumer(String senderId, Date sentTime, Triplet outputData, NabsManager nm) {
		for(BeadInputInterface listener : appListeners){
			listener.getEvidence(senderId, sentTime, outputData, nm);
		}
		
	}

	@Override
	public void getEvidence(String senderId, Date sentTime, Triplet inputData, NabsManager nm) {
		System.out.println("App");
		ObjectMapper mapper = new ObjectMapper();
		try {
			notification = mapper.readValue(inputData.getInformationItem().getInformationValue(),
					UpliftedNotification.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// get the calendar data for the next 10 events
		try {
			events = GoogleCalendarData.getNextNEvents(10, notification.getDate(), nm);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.nm = nm;
		this.run();		
	}

	/**
	 * SL for ranking importance of applications
	 */
	@Override
	public void inferInfoBeadAttr() {
				
		double inferredValue = (double) notification.getAppRank()/10.0;
		System.out.println("MastersProject.BeadRepo.AppInfoBead: Inferred value = "+inferredValue);
		
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
	
	/**
	 * Called once the bead has been activated (data has been pushed to it)
	 */
	@Override
	public void run() {
		this.activate();
		inferInfoBeadAttr();
		sendToConsumer(this.getAttributeValueType().toString(), new Date(), this.getOperational(), this.nm);
		storeInfoBeadAttr();
	}	
}
