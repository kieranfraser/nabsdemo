package masters.calendar;

import java.io.IOException;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import PhDProject.FriendsFamily.Models.Event;
import PhDProject.FriendsFamily.Models.User;
import main.NabsDemo;
import phd.utilities.DateFormatUtility;


public class GoogleCalendarData {
	
    private static int counter = 0;
	private static ArrayList<Event> possibleEvents = new ArrayList<>();

    
    /**
     * Get the next n events from the date and time input
     * 
     * This needs to be changed for Nabsim - the events are generated 
     * from the friends&family data-set. - N always being 10, events ordered by start
     * time/date
     * 
     * @param n - number of events
     * @param date - the date to compare
     * @return An array list of calendar events 
     * @throws ParseException 
     * @throws IOException 
     */
    public static ArrayList<CalendarEvent> getNextNEvents(int n, Date dateFrom) 
    		throws ParseException, IOException {
    	counter = 0;
    	possibleEvents = new ArrayList<>();
    	boolean greaterDate = false;
    	
    	ArrayList<CalendarEvent> requiredEvents = new ArrayList<>();
    	
    	User user = NabsDemo.getSelectedUser();
    	
		// check day - if day of week, get work/study
		LocalDateTime notificationDate = DateFormatUtility.convertDateToLDT(dateFrom);
		DayOfWeek dayOfWeek = notificationDate.getDayOfWeek();
		
		while(counter <10){

    		if(dayOfWeek.equals(DayOfWeek.MONDAY) || dayOfWeek.equals(DayOfWeek.TUESDAY) ||
    				dayOfWeek.equals(DayOfWeek.WEDNESDAY) || dayOfWeek.equals(DayOfWeek.THURSDAY) || 
    						dayOfWeek.equals(DayOfWeek.FRIDAY)){
    			if(counter < 10){
    				addMorningEvent(user, notificationDate, greaterDate);
    			} else break;
    			if(counter < 10){
    				addLunchEvent(user, notificationDate, greaterDate);
    			} else break;
    			if(counter < 10){
    				addAfternoonEvent(user, notificationDate, greaterDate);
    			} else break;
    		}
    		ArrayList<Event> events = User.getTodaysEvents(notificationDate, user);
    		for(Event event :events){
    			if(counter<10){
    				possibleEvents.add(event);
    				counter++;
    			}
    		}
    		notificationDate = notificationDate.plusDays(1);
    		notificationDate = notificationDate.withHour(0);
    		notificationDate = notificationDate.withMinute(0);
    		notificationDate = notificationDate.withMinute(0);
    		dayOfWeek = notificationDate.getDayOfWeek();
    		greaterDate = true;
		}
		
		for(Event event: possibleEvents){
			CalendarEvent calEvent = new CalendarEvent();
			calEvent.setDescription(event.getInferredDescription());
			calEvent.setEndDate(DateFormatUtility.convertLDTToCalendarEventDate(event.getInferredEndDate()));
			calEvent.setLocation("unknown");
			calEvent.setStartDate(DateFormatUtility.convertLDTToCalendarEventDate(event.getInferredStartDate()));
			calEvent.setSummary(event.getNameGT());
			requiredEvents.add(calEvent);
		}
		
		// if notification before 5 add 1-5 event increment counter
		
		// if notification before 1 add lunch event increment counter
		
		// if notification before 12 add morning event to possible events increment counter
		
		// get all other events for today. add counter for each.
		
		// check counter - repeat until 10 met.
    	
    	/*Thread t = new Thread(new Runnable(){
    	    @Override
    	    public void run() {
    	    	try {
					Event.printListEvents(requiredEvents);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	    }
    	});
    	t.start();*/
    	
    	return requiredEvents;
    }
    
    private static void addMorningEvent(User user, LocalDateTime notificationDate, boolean greaterDate){
    	Event newEvent = new Event();
    	System.out.println(user.isStudent());
		newEvent.setFixedEventMorning(user.isStudent(), notificationDate);
		if(DateFormatUtility.checkBeforeHour(notificationDate, newEvent.getInferredEndDate())){
			possibleEvents.add(newEvent);
			counter++;
		};
    }
    
    private static void addLunchEvent(User user, LocalDateTime notificationDate, boolean greaterDate){
    	Event newEvent = new Event();
		newEvent.setFixedLunchEvent(user.isStudent(), notificationDate);
		if(DateFormatUtility.checkBeforeHour(notificationDate, newEvent.getInferredEndDate()) || greaterDate){
			possibleEvents.add(newEvent);
			counter++;
		};
    }
    
    private static void addAfternoonEvent(User user, LocalDateTime notificationDate, boolean greaterDate){
    	Event newEvent = new Event();
		newEvent.setFixedAfternoonEvent(user.isStudent(), notificationDate);
		if(DateFormatUtility.checkBeforeHour(notificationDate, newEvent.getInferredEndDate())){
			possibleEvents.add(newEvent);
			counter++;
		};
    }
    
    /**
     * Get the next event - to be used for the user context - 
     * now generated based on the inferred values from Friends & Family data-set
     * @param dateFrom
     * @return
     * @throws ParseException
     * @throws IOException
     */
   public static CalendarEvent getNextEvent(Date dateFrom)
	   throws ParseException, IOException {
   		/*com.google.api.services.calendar.Calendar service = getCalendarService();
    	calendarIDS = new ArrayList<String>();
    	calendars = new ArrayList<Calendar>();

    	DateTime date = new DateTime(dateFrom, TimeZone.getDefault());
        Events events = service.events().list("primary")
            .setTimeMin(date)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute();
        List<Event> items = events.getItems();
    	Date convertedStartDate = DateUtility.convertEventDateTimeToDate(items.get(0).getStart());
    	Date convertedEndDate = DateUtility.convertEventDateTimeToDate(items.get(0).getEnd());
    	CalendarEvent event = new CalendarEvent(items.get(0).getDescription(),
    			convertedStartDate, convertedEndDate, items.get(0).getLocation(), items.get(0).getSummary());*/
	    ArrayList<CalendarEvent> events = getNextNEvents(10, dateFrom);
	    CalendarEvent nextEvent = events.get(0);
        return nextEvent;
    }
    
      
}
