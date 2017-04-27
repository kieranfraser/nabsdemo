package masters.inference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import main.NabsDemo;
import masters.calendar.CalendarEvent;
import masters.models.UpliftedNotification;
import phd.utilities.DateUtility;

public class EventInference {
	
	public static double getEventImportanceValue(String attribute, ArrayList<CalendarEvent> events,
			UpliftedNotification notification){
		
		ArrayList<CalendarEvent> unfinishedEvents = new ArrayList<>();
		
		LocalDateTime notificationDate = DateUtility.dateToLocalDateTime(notification.getDate());
		long maxStartTimeDiff = 720;
		
		for(CalendarEvent event : events){
			if(getAllUnfinishedEvents(event, notification)){
				unfinishedEvents.add(event);
				LocalDateTime eventStartDate = DateUtility.dateToLocalDateTime(event.getStartDate());
				long eventStartTimeDiff = DateUtility.getDifferenceBetweenDatesInMinutes(notificationDate, eventStartDate);
				/*if(eventStartTimeDiff >= maxStartTimeDiff ){  			//can be put back in for calendars that don't have sparse events 
					maxStartTimeDiff = eventStartTimeDiff;					//spread out over a number of days (scews the context relevance of event)
				}*/
			}
		}
		
		ArrayList<Double> rankingValue = new ArrayList<Double>();
	    Map<CalendarEvent, Double> eventContext = new HashMap<CalendarEvent, Double>();
	    boolean freePeriodSet = false;
		rankingValue.add(0.0);
		int i = 1;
		for(CalendarEvent event : unfinishedEvents){
			if(hasContextMatch(event.getDescription(), notification, attribute)){
				
				LocalDateTime eventStartDate = DateUtility.dateToLocalDateTime(event.getStartDate());
				long eventStartTimeDiff = DateUtility.getDifferenceBetweenDatesInMinutes(notificationDate, eventStartDate);
				
				rankingValue.add(applyRating(event, maxStartTimeDiff, eventStartTimeDiff));
				eventContext.put(event, applyRating(event, maxStartTimeDiff, eventStartTimeDiff));
			}
			// setting next free period
			if(freePeriodSet == false){
				if(unfinishedEvents.size()>=2 && i < unfinishedEvents.size()){
					LocalDateTime endThisEvent = DateUtility.dateToLocalDateTime(event.getEndDate());
					LocalDateTime startNextEvent = DateUtility.dateToLocalDateTime(unfinishedEvents.get(i).getStartDate());
					long diff = DateUtility.getDifferenceBetweenDatesInMinutes(endThisEvent, startNextEvent);
					if(diff>15){
						
						NabsDemo.setNextFreePeriod(event.getEndDate());
						freePeriodSet = true;
					}
				}
				else{
					NabsDemo.setNextFreePeriod(event.getEndDate());
				}
			}
			
			i++;
		}
		
		Map.Entry<CalendarEvent, Double> maxEntry = null;
		

		for (Map.Entry<CalendarEvent, Double> entry : eventContext.entrySet())
		{
		    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
		    {
		        maxEntry = entry;
		    }
		}
		if(maxEntry!=null){
			System.out.println("Context relevant "+maxEntry.getKey().getStartDate());
			NabsDemo.setNextContextRelevant(maxEntry.getKey().getStartDate());
		}
		else{
			System.out.println("No context relevant");
			NabsDemo.setNextContextRelevant(NabsDemo.getNextFreePeriod());
		}
		
		return Collections.max(rankingValue);
	}
	
	private static boolean getAllUnfinishedEvents(CalendarEvent event, UpliftedNotification notification){
				
		LocalDateTime notificationDate = DateUtility.dateToLocalDateTime(notification.getDate());
		LocalDateTime eventEndDate = DateUtility.dateToLocalDateTime(event.getEndDate());
		
		long endDiff = DateUtility.getDifferenceBetweenDatesInMinutes(notificationDate, eventEndDate);
		
		if(endDiff >= 0){ 
			return true;
		}
		else {
			return false;
		}
	}
		
	private static boolean hasContextMatch(String calendarDescr, UpliftedNotification notification, String attribute){
		boolean contextMatch = false;
		switch(attribute){
			case "Subject":
				if(calendarDescr.contains(notification.getSubject())){
					contextMatch = true;
				}
				else contextMatch = false;
				break;
			case "Sender" :
				if(calendarDescr.contains(notification.getSender())){
					contextMatch = true;
				}
				else contextMatch = false;
				break;
			default:
				break;
		}
		return contextMatch;			
		
	}

	private static double applyRating(CalendarEvent event, 
			long maxTimeDiff, long incomingTimeDiff){
		if(incomingTimeDiff < 0 ){ // ensure that the ongoing event is calculated correctly
			incomingTimeDiff = 0;
		}
		double result = (double) (maxTimeDiff - incomingTimeDiff)/maxTimeDiff;
		return result;
	}
	
	/**
	 * Get the current events location and summary if it exists
	 * @param event
	 * @param notification
	 * @return StringArray with the summary at index 0 and location at index 1
	 */
	public static ArrayList<String> getCurrentLocationAndEventName(CalendarEvent event, UpliftedNotification notification){
		ArrayList<String> result = new ArrayList<String>();
		
		LocalDateTime notificationDate = DateUtility.dateToLocalDateTime(notification.getDate());
		long maxStartTimeDiff = 4320;
		
		if(getAllUnfinishedEvents(event, notification)){
			LocalDateTime eventStartDate = DateUtility.dateToLocalDateTime(event.getStartDate());
			long eventStartTimeDiff = DateUtility.getDifferenceBetweenDatesInMinutes(notificationDate, eventStartDate);
			if(applyRating(event, maxStartTimeDiff, eventStartTimeDiff) == 1){
				String location =  event.getLocation();
				String summary = event.getSummary();
				result.add(summary);
				if(location == null){
					result.add("unknown");
				}else{
					result.add(location);
				}
				// Set next break
				Date nextBreak = event.getEndDate();
				NabsDemo.setNextBreak(nextBreak);
			}
			else{
				result.add("none");
				result.add("unknown");
			}
		}
		else{
			result.add("none");
			result.add("unknown");
		}
		return result;
	}
	
	public static boolean checkIfEventOccurring(UpliftedNotification notification){
		boolean eventOccurring = false;
		
		
		
		return eventOccurring;
	}
}
