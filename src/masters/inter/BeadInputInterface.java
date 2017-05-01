package masters.inter;

import java.util.Date;

import managers.NabsManager;
import masters.models.Triplet;

public interface BeadInputInterface {

	public void getEvidence(String senderId, Date sentTime, Triplet inputData, NabsManager nm);
	
}
