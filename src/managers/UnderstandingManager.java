package managers;

import java.util.Map;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SentimentOptions;

public class UnderstandingManager {
	
	private NaturalLanguageUnderstanding nlService;
	private ConversationService cService;
	private final double CONTINUE_THRESHOLD = 0.0;

	public UnderstandingManager(){
		nlService = new NaturalLanguageUnderstanding(
				  NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
				  "6b1514c7-f791-4d27-8de6-c8b44c276121",
				  "oZYCXa7aztcc");
		
		cService = new ConversationService(ConversationService.VERSION_DATE_2017_02_03);
		cService.setUsernameAndPassword("21626a48-0dae-4916-95e4-218eb8adf48b", "BcEiF0H4o7Ci");
		/*SentimentOptions sentiment = new SentimentOptions.Builder().build();
		Features features = new Features.Builder().sentiment(sentiment).build();
		AnalyzeOptions parameters = new AnalyzeOptions.Builder().text("No, the sender is a close friend, it should have been sent sooner").features(features).build();
		AnalysisResults results = service.analyze(parameters).execute();
		
		System.out.println(results);*/
	}
	
	/**
	 * Check whether the user is happy with the result or not.
	 * @param response
	 * @return
	 */
	public boolean needToContinueUnderstanding(String response){
		boolean needContinue = true;
		
		SentimentOptions sentiment = new SentimentOptions.Builder().build();
		Features features = new Features.Builder().sentiment(sentiment).build();
		AnalyzeOptions parameters = new AnalyzeOptions.Builder().text(response).features(features).build();
		AnalysisResults results = nlService.analyze(parameters).execute();
		double score = results.getSentiment().getDocument().getScore();
		System.out.println("Score: "+score);
		if(score >= CONTINUE_THRESHOLD){
			needContinue = false;
		}		
		return needContinue;
	}
	
	public String intelligentModification(String directionResponse, String controlResponse){
		String conclusion = "NAbs has updated the system as follows: ";
		
		// identify the direction in which to change the control
		
		// identify the control part to change
		
		// continuously loop through control part until changed or run out of options
		
		return conclusion;
	}
	
	public MessageResponse conversation(String text, Map<String, Object> context){
		MessageRequest newMessage;
		if(text != null && context != null){
			System.out.println("\n \n In the continued conversation section");
			System.out.println(context);
			MessageRequest.Builder msgBuilder = new MessageRequest.Builder();
			msgBuilder.inputText(text);
			msgBuilder.context(context);
			newMessage = msgBuilder.build();
		}
		else{
			newMessage = new MessageRequest.Builder().inputText("").build();
		}
		MessageResponse response = cService.message("7d248ded-fdf5-425e-9641-b3e4c466bc23", newMessage).execute();
		System.out.println("The actual context object!");
		System.out.println(response.getContext());
		return response;
	}
	
}
