package masters.models;

import java.io.Serializable;
import java.util.ArrayList;

import masters.constants.ActivationType;
import masters.constants.BeadType;
import masters.constants.ConnectionType;

public class InformationBead implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -2697034321795109248L;

	private String id;
	
	private String singleAttributeName;

	private BeadType attributeValueType;
	
	private ConnectionType comMode;
	
	private ActivationType onOff;
	
	private ArrayList<String> authorizationToSendToID;
	
	private String infoBeadName;
	
	private ArrayList<String> keywords;
	
	private ArrayList<String> infoUnits;

	private ArrayList<String> inputInterfaces;
	
	private String outputInterface;
	private String partNumber;
	private String version;
	private String backwardCompatibility;
	private String resource;
	private String contact;
	private String trustworthiness;

	private Triplet operational;
	
	public void inferInfoBeadAttr(){}
	public void storeInfoBeadAttr(){}
	
	public void activate(){
		this.onOff = ActivationType.ON;
	}
	public void deactivate(){
		this.onOff = ActivationType.OFF;
	}
	private void authorize(String id){}
	private void deAuthorize(String id){}
	private void explain(Triplet infoBeadData){}
	private void addInformation(Triplet infoBeadData){}
	private void deleteInformation(Triplet infoBeadData){}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSingleAttributeName() {
		return singleAttributeName;
	}
	public void setSingleAttributeName(String singleAttributeName) {
		this.singleAttributeName = singleAttributeName;
	}
	public BeadType getAttributeValueType() {
		return attributeValueType;
	}
	public void setAttributeValueType(BeadType attributeValueType) {
		this.attributeValueType = attributeValueType;
	}
	public ConnectionType getComMode() {
		return comMode;
	}
	public void setComMode(ConnectionType comMode) {
		this.comMode = comMode;
	}
	public ActivationType getOnOff() {
		return onOff;
	}
	public void setOnOff(ActivationType onOff) {
		this.onOff = onOff;
	}
	public ArrayList<String> getAuthorizationToSendToID() {
		return authorizationToSendToID;
	}
	public void setAuthorizationToSendToID(ArrayList<String> authorizationToSendToID) {
		this.authorizationToSendToID = authorizationToSendToID;
	}
	public String getInfoBeadName() {
		return infoBeadName;
	}
	public void setInfoBeadName(String infoBeadName) {
		this.infoBeadName = infoBeadName;
	}
	public ArrayList<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(ArrayList<String> keywords) {
		this.keywords = keywords;
	}
	public ArrayList<String> getInfoUnits() {
		return infoUnits;
	}
	public void setInfoUnits(ArrayList<String> infoUnits) {
		this.infoUnits = infoUnits;
	}
	public ArrayList<String> getInputInterfaces() {
		return inputInterfaces;
	}
	public void setInputInterfaces(ArrayList<String> inputInterfaces) {
		this.inputInterfaces = inputInterfaces;
	}
	public String getOutputInterface() {
		return outputInterface;
	}
	public void setOutputInterface(String outputInterface) {
		this.outputInterface = outputInterface;
	}
	public String getPartNumber() {
		return partNumber;
	}
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getBackwardCompatibility() {
		return backwardCompatibility;
	}
	public void setBackwardCompatibility(String backwardCompatibility) {
		this.backwardCompatibility = backwardCompatibility;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getTrustworthiness() {
		return trustworthiness;
	}
	public void setTrustworthiness(String trustworthiness) {
		this.trustworthiness = trustworthiness;
	}
	public Triplet getOperational() {
		return operational;
	}
	public void setOperational(Triplet operational) {
		this.operational = operational;
	}
	
	
	
	
}
