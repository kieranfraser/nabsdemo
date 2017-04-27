package masters.beadrepo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import managers.FirebaseManager;
import masters.inter.BeadInputInterface;
import masters.inter.BeadOutputInterface;
import masters.models.InformationBead;
import masters.models.Triplet;
import masters.models.UpliftedNotification;

public class DateInfoBead extends InformationBead implements BeadInputInterface,
BeadOutputInterface, Runnable{

	private static final long serialVersionUID = 3383755625574043643L;
	
	private List<BeadInputInterface> dateListeners = new ArrayList<BeadInputInterface>();
	
	private UpliftedNotification  notification;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Add a bead which will listen for push requests.
	 * @param addListener
	 */
	public void addListener(BeadInputInterface bead){
		this.dateListeners.add(bead);
	}
	
	/**
	 * Remove a bead from the listening list.
	 * @param bead
	 */
	public void removeListener(BeadInputInterface bead){
		this.dateListeners.remove(bead);
	}

	@Override
	public void sendToConsumer(String senderId, Date sentTime, Triplet outputData) {
		for(BeadInputInterface listener : dateListeners){
			listener.getEvidence(senderId, sentTime, outputData);
		}
		
	}

	@Override
	public void getEvidence(String senderId, Date sentTime, Triplet inputData) {
		System.out.println("Date");
		ObjectMapper mapper = new ObjectMapper();
		try {
			notification = mapper.readValue(inputData.getInformationItem().getInformationValue(),
					UpliftedNotification.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.run();		
	}

	/**
	 * SL for ranking importance of applications
	 */
	@Override
	public void inferInfoBeadAttr() {
		// TODO Auto-generated method stub
		super.inferInfoBeadAttr();
	}

	@Override
	public void storeInfoBeadAttr() {
		FirebaseManager.getDatabase().child("BeadRepo/"+
				this.getAttributeValueType()+"/").setValue((InformationBead) this);
	}

}
