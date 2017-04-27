package masters.models;

import java.io.Serializable;
import java.util.Date;

public class Triplet implements Serializable{

	private static final long serialVersionUID = 4554567158991473317L;

	private String id;
	
	private Date detectionTime;
	
	private InfoItemFields informationItem;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDetectionTime() {
		return detectionTime;
	}

	public void setDetectionTime(Date detectionTime) {
		this.detectionTime = detectionTime;
	}

	public InfoItemFields getInformationItem() {
		return informationItem;
	}

	public void setInformationItem(InfoItemFields informationItem) {
		this.informationItem = informationItem;
	}
	
	
}
