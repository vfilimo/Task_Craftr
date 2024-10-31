package project.demo.external.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import project.demo.model.Task;

@Service
public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "TaskCrafter";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final LocalTime END_OF_WORKDAY = LocalTime.of(18, 0);
    private static final LocalTime START_OF_WORKDAY = LocalTime.of(9, 0);
    private static final String SEND_UPDATES_OPTION = "all";
    @Value("${google.calendar-id}")
    private String calendarId;

    private Calendar getCalendar() throws GeneralSecurityException, IOException {
        final NetHttpTransport netHttpTransport = GoogleNetHttpTransport.newTrustedTransport();

        return new Calendar.Builder(netHttpTransport, JSON_FACTORY,
                GoogleAuthorization.getCredentials(netHttpTransport))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public Event createEvent(Task task) {
        Event event = new Event()
                .setSummary(task.getName())
                .setDescription(createDescriptionForEvent(task));
        Date startDate = Date.from(task.getDueDate()
                .atTime(START_OF_WORKDAY)
                .atZone(ZoneId.systemDefault())
                .toInstant());
        Date endDate = Date.from(task.getDueDate()
                .atTime(END_OF_WORKDAY)
                .atZone(ZoneId.systemDefault())
                .toInstant());

        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(startDate))
                .setTimeZone(ZoneId.systemDefault().getId());
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(endDate))
                .setTimeZone(ZoneId.systemDefault().getId());
        event.setEnd(end);
        event.setAttendees(Collections.singletonList(
                new EventAttendee().setEmail(task.getAssignee().getEmail())));
        return event;
    }

    public Event insertEventInToCalendar(Event event)
            throws GeneralSecurityException, IOException {
        return getCalendar()
                .events()
                .insert(calendarId, event)
                .setSendUpdates(SEND_UPDATES_OPTION)
                .execute();
    }

    public Event updateEvent(Event newEvent, String eventId)
            throws IOException, GeneralSecurityException {
        return getCalendar()
                .events()
                .update(calendarId, eventId, newEvent)
                .setSendUpdates(SEND_UPDATES_OPTION)
                .execute();
    }

    public Event getEventFromCalendar(String calendarId, String eventId)
            throws GeneralSecurityException, IOException {
        return getCalendar()
                .events()
                .get(calendarId, eventId)
                .execute();
    }

    private String createDescriptionForEvent(Task task) {
        List<String> labels = task.getLabels().stream()
                .map(label -> String.format("Label name: %s, color: %s",
                        label.getName(), label.getColor()))
                .toList();
        return String.format("""
                        Task: %s;
                        Description: %s;
                        Status: %s;
                        Priority: %s;
                        Labels list: %s
                        """, task.getName(), task.getDescription(), task.getStatus().name(),
                task.getPriority().name(), labels);
    }
}
