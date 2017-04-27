package PhDProject.FriendsFamily.Models;

import java.io.Serializable;

/**
 * Score for the Big Five personality test. 
 * 
 * http://fetzer.org/sites/default/files/images/stories/pdf/selfmeasures/Personality-BigFiveInventory.pdf
 * https://www.ocf.berkeley.edu/~johnlab/pdfs/2008chapter.pdf
 * 
 * @author kfraser
 *
 */
public class Personality implements Serializable{

	private static final long serialVersionUID = -2770428974692328350L;
	
	private int extraversion;
	private int agreeableness;
	private int conscientiousness;
	private int neuroticism;
	private int openness;
	
	private String id;
	
	public int getExtraversion() {
		return extraversion;
	}
	public void setExtraversion(int extraversion) {
		this.extraversion = extraversion;
	}
	public int getAgreeableness() {
		return agreeableness;
	}
	public void setAgreeableness(int agreeableness) {
		this.agreeableness = agreeableness;
	}
	public int getConscientiousness() {
		return conscientiousness;
	}
	public void setConscientiousness(int conscientiousness) {
		this.conscientiousness = conscientiousness;
	}
	public int getNeuroticism() {
		return neuroticism;
	}
	public void setNeuroticism(int neuroticism) {
		this.neuroticism = neuroticism;
	}
	public int getOpenness() {
		return openness;
	}
	public void setOpenness(int openness) {
		this.openness = openness;
	}
	@Override
	public String toString() {
		
		String personality = "extraversion: "+extraversion+"\n"+
				"agreeableness: "+agreeableness+"\n"+
				"conscientiousness: "+conscientiousness+"\n"+
				"neuroticism: "+neuroticism+"\n"+
				"openness: "+openness;
		
		return personality;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
	
	
}
