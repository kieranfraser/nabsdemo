package masters.inter;

import java.util.Date;

import managers.NabsManager;
import masters.models.Triplet;

public interface BeadOutputInterface {

	public void sendToConsumer(String senderId, Date sentTime, Triplet outputData, NabsManager nm);

}
