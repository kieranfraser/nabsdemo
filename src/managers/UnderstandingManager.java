package managers;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SentimentOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SentimentResult;

public class UnderstandingManager {
	
	private NaturalLanguageUnderstanding service;
	private final double CONTINUE_THRESHOLD = 0.0;

	public UnderstandingManager(){
		service = new NaturalLanguageUnderstanding(
				  NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
				  "6b1514c7-f791-4d27-8de6-c8b44c276121",
				  "oZYCXa7aztcc"
				);
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
		AnalysisResults results = service.analyze(parameters).execute();
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
	
}
