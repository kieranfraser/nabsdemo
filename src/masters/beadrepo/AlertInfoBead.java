package masters.beadrepo;

import java.util.ArrayList;
import java.util.Date;

import PhDProject.FriendsFamily.Models.Result;
import managers.NabsManager;
import masters.fuzzylogic.AlertFuzzy;
import masters.inter.BeadInputInterface;
import masters.inter.BeadOutputInterface;
import masters.models.InfoItemFields;
import masters.models.InformationBead;
import masters.models.Triplet;

public class AlertInfoBead extends InformationBead implements BeadInputInterface, BeadOutputInterface,
Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4795515184165812011L;

	
	private double senderInput;
	
	
	private double subjectInput;
	
	
	private double appInput;
	
	
	private double userLocation;
	
	/**
	 * Used for identifying the results in NabSim
	 */
	private int notificationIdPath;
	
	private NabsManager nm;

	/**
	 * Must be initialized to ~0 for fuzzy controller.
	 */
	public AlertInfoBead() {
		this.senderInput = 0.0001;
		this.subjectInput = 0.0001;
		this.appInput = 0.0001;
	}
	
	@Override
	public void inferInfoBeadAttr() {
		// Mamdami inferrence controller 
		AlertFuzzy alertFuzzy = new AlertFuzzy();
		double inferredValue = alertFuzzy.processalert(senderInput, subjectInput, appInput);
		
		//String result = "Receive Notification "+this.getPartNumber()+" in: "+DateUtility.cleanMinutes(inferredValue)+"\n";
		
		String result = "";
		
		
		// now - interrupt
		if(inferredValue<5.0){ 
			result = result + "Now "+this.getPartNumber()+"\n";
		
		// verysoon - next break
		}else if(inferredValue<15){ 
			
			if(userLocation == 1.0){ // if there's an event on
				result = result + "Next break - "+nm.getNextBreak()+" - "+this.getPartNumber()+"\n";
				
			}
			else{
				result = result + "Now "+this.getPartNumber()+"\n";
			}
		
		// soon - next free period
		}else if(inferredValue<40){ 
			
			
			if(userLocation == 1.0){
				result = result + "Next free period "+nm.getNextFreePeriod()+" - "+this.getPartNumber()+"\n";
			}
			else{
				result = result + "Now "+this.getPartNumber()+"\n";
			}
			
		// Later & Much Later	
		}else if(inferredValue<60){ 
			result = result + "Little Later "+nm.getNextContextRelevant()+" - "+"\n";
		}
		else{
			result = result + "Much Later "+nm.getNextContextRelevant()+" - "+"\n";
		}
		//System.out.println(result);
		//App.resultCallback.resultCallback(Integer.valueOf(this.getPartNumber()), result);
		Triplet triplet = new Triplet();
		InfoItemFields infoItem = new InfoItemFields();
		infoItem.setInformationValue(result);
		triplet.setInformationItem(infoItem);
		setOperational(triplet);
	}

	@Override
	public void sendToConsumer(String senderId, Date sentTime, Triplet outputData, NabsManager nm) {}

	/**
	 * The triplets received are from the sender and subject beads.
	 */
	@Override
	public void getEvidence(String senderId, Date sentTime, Triplet inputData, NabsManager nm) {
		notificationIdPath = inputData.getInformationItem().getInfoBeadId();
		switch(senderId){
		case "SUBJECT":
			this.subjectInput = Double.valueOf(inputData.getInformationItem().getInformationValue());
			break;
		case "SENDER":
			this.senderInput = Double.valueOf(inputData.getInformationItem().getInformationValue());
			break;
		case "APPLICATION":
			this.appInput = Double.valueOf(inputData.getInformationItem().getInformationValue());
		case "LOCATION":
			this.userLocation = Double.valueOf(inputData.getInformationItem().getInformationValue());
		}
		this.nm = nm;
		this.run();
	}

	/**
	 * Changed for experiment 1 whereby the results are being stored in firebase under individual paramId, userId
	 * and 
	 */
	@Override
	public void storeInfoBeadAttr() {
		/*FirebaseManager.getDatabase().child("BeadRepo/"+
				this.getAttributeValueType()+"/").setValue((InformationBead) this);
		
		FirebaseManager.getDatabase().child("web/results/"+notificationIdPath).
		setValue(new Result(notificationIdPath, this.getOperational().getInformationItem().getInformationValue()));*/
		ArrayList<Result> results = nm.results;
		boolean found = false;
		for(Result val:results){
			if(val.getId() == notificationIdPath ){
				val.setResult(this.getOperational().getInformationItem().getInformationValue());
				found = true;
				break;
			}
		}
		if(!found)
			nm.results.add(new Result(notificationIdPath, this.getOperational().getInformationItem().getInformationValue()));
		
		/*FirebaseManager.getDatabase().child("Exp1/"+App.getCurrentParamId()+"/"+
				App.getCurrentUserId()+"/"+notificationIdPath).
		setValue(this.getOperational().getInformationItem().getInformationValue());*/
		//StatisticsManager.getStatsManager().updateStats(this.getOperational().getInformationItem().getInformationValue());
	}

	@Override
	public void run() {
		inferInfoBeadAttr();
		storeInfoBeadAttr();		
	}
	
	

}
