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
import masters.fuzzylogic.SenderFuzzy;
import masters.inference.EventInference;
import masters.inter.BeadInputInterface;
import masters.inter.BeadOutputInterface;
import masters.models.InfoItemFields;
import masters.models.InformationBead;
import masters.models.Triplet;
import masters.models.UpliftedNotification;

public class SenderInfoBead extends InformationBead implements BeadInputInterface, BeadOutputInterface,
Runnable{
	
	private static final long serialVersionUID = -8514150933203047825L;

	ArrayList<CalendarEvent> events;
	
	private List<BeadInputInterface> senderListeners = new ArrayList<BeadInputInterface>();
	private UpliftedNotification  notification;
	private NabsManager nm;
	
	public SenderInfoBead(){
		ArrayList<String> sendToList = new ArrayList<String>();
		sendToList.add("AlertInfoBead");
		this.setAuthorizationToSendToID(sendToList);
	}

	/**
	 * Add a bead which will listen for push requests.
	 * @param addListener
	 */
	public void addListener(BeadInputInterface bead){
		this.senderListeners.add(bead);
	}
	
	/**
	 * Remove a bead from the listening list.
	 * @param bead
	 */
	public void removeListener(BeadInputInterface bead){
		this.senderListeners.remove(bead);
	}

	/**
	 * Called when updates need to be pushed to other beads.
	 */
	@Override
	public void sendToConsumer(String senderId, Date sentTime, Triplet outputData, NabsManager nm) {
		for(BeadInputInterface listener : senderListeners){
			listener.getEvidence(senderId, sentTime, outputData, nm);
		}
	}

	/**
	 * This will only be fired when information is pushed from the notification 
	 * info bead. This will then activate this bead and run the process. 
	 * TODO: Check the authorized list to ensure the bead can be sent to
	 * TODO: Get the next 5 events (need to send the notification date)
	 */
	@Override
	public void getEvidence(String senderId, Date sentTime, Triplet inputData, NabsManager nm) {
		System.out.println("Sender");
		
		/**
		 * Storing the upliftedNotification in the Triplet as a json string.
		 */
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
		} catch (IOException |  ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.nm = nm;
		this.run();
	}
	
	/**
	 * Infer whether the notification sender is important based on event and sender importance.
	 * The importance of the event is dependent on whether it matches with the event "sender"
	 * and is also based on how close the event is to the current time. Get the next 10 events.
	 */
	@Override
	public void inferInfoBeadAttr() {
		
		double eventInput = EventInference.getEventImportanceValue("Sender", events, notification, nm);

		if(eventInput == 0.0){
			eventInput = 0.00001;
		}
		
		// Mamdami inferrence controller 
		SenderFuzzy senderFuzzy = new SenderFuzzy(nm);
		double senderInput = (double) notification.getSenderRank()/10.0;
		System.out.println("SenderInput: "+senderInput);
		System.out.println("EventInput: "+eventInput);
		double inferredValue = senderFuzzy.processSender(senderInput, eventInput);
		System.out.println("MastersProject.BeadRepo.SenderInfoBead: Inferred value = "+inferredValue);
		
		Triplet operational = new Triplet();
		InfoItemFields info = new InfoItemFields();
		info.setInformationValue(String.valueOf(inferredValue));
		operational.setInformationItem(info);
		operational.setDetectionTime(new Date());
		info.setInfoBeadId(notification.getNotificationId());
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
		sendToConsumer(this.getAttributeValueType().toString(), new Date(), this.getOperational(), nm);
		storeInfoBeadAttr();
	}

}
