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
import masters.fuzzylogic.SubjectFuzzy;
import masters.inference.EventInference;
import masters.inter.BeadInputInterface;
import masters.inter.BeadOutputInterface;
import masters.models.InfoItemFields;
import masters.models.InformationBead;
import masters.models.Triplet;
import masters.models.UpliftedNotification;

public class SubjectInfoBead extends InformationBead implements BeadInputInterface, BeadOutputInterface,
Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1253534945156596424L;

	ArrayList<CalendarEvent> events;
	private List<BeadInputInterface> subjectListeners = new ArrayList<BeadInputInterface>();
	private UpliftedNotification  notification;
	private NabsManager nm;
	
	public SubjectInfoBead(){
		ArrayList<String> sendToList = new ArrayList<String>();
		sendToList.add("AlertInfoBead");
		this.setAuthorizationToSendToID(sendToList);
	}

	/**
	 * Add a bead which will listen for push requests.
	 * @param addListener
	 */
	public void addListener(BeadInputInterface bead){
		this.subjectListeners.add(bead);
	}
	
	/**
	 * Remove a bead from the listening list.
	 * @param bead
	 */
	public void removeListener(BeadInputInterface bead){
		this.subjectListeners.remove(bead);
	}

	/**
	 * Called when updates need to be pushed to other beads.
	 */
	@Override
	public void sendToConsumer(String senderId, Date sentTime, Triplet outputData, NabsManager nm) {
		for(BeadInputInterface listener : subjectListeners){
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
		System.out.println("Subject");
		ObjectMapper mapper = new ObjectMapper();
		try {

			System.out.println("Getting the information item of the notifications");
			notification = mapper.readValue(inputData.getInformationItem().getInformationValue(),
					UpliftedNotification.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("Stuck here");
		}
		
		// get the calendar data for the next 10 events
		try {
			System.out.println("Getting the next n events");
			events = GoogleCalendarData.getNextNEvents(10, notification.getDate(), nm);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block

			System.out.println("Stuck here 2");
		}
		this.nm = nm;
		this.run();
	}
	
	@Override
	public void inferInfoBeadAttr() {	
		
		double eventInput = EventInference.getEventImportanceValue("Subject", events, notification, nm);
		
		if(eventInput == 0.0){
			eventInput = 0.00001;
		}
		
		// Mamdami inferrence controller 
		SubjectFuzzy senderFuzzy = new SubjectFuzzy(nm);
		double subjectInput = (double) notification.getSubjectRank()/10.0;
		System.out.println("event relevance = "+ eventInput);
		double inferredValue = senderFuzzy.processSubject(subjectInput, eventInput);
		System.out.println("MastersProject.BeadRepo.SubjectInfoBead: Inferred value = "+inferredValue);
		
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
