package me.lensevents.utilities;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.lensevents.dto.EventDto;
import me.lensevents.dto.EventMessageDto;
import me.lensevents.model.Category;
import me.lensevents.model.Group;
import me.lensevents.model.Location;

public class PopulateDatabase {

    public static void main(String[] args) {
        //PopulateDatabase.createEvents();
        PopulateDatabase.createGroups();
    }

    public static void createEvents() {

        List<EventDto> eventList = new ArrayList<>();

        Location location = new Location();
        location.setCity("Sevilla");
        location.setCountry("España");
        location.setProvince("Sevilla");
        location.setStreet("Avenida Reina Mercedes");

        EventMessageDto eventMessage = new EventMessageDto();
        eventMessage.setDate("2017-04-18");
        eventMessage.setText("Que gran idea!");

        EventMessageDto eventMessage1 = new EventMessageDto();
        eventMessage1.setDate("2017-04-18");
        eventMessage1.setText("Que mierda!");

        List<EventMessageDto> eventMessages = new ArrayList<>();
        eventMessages.add(eventMessage);
        eventMessages.add(eventMessage1);

        EventDto event = new EventDto();
        event.setDate("2017-04-18");
        event.setDescription("Este es un evento de prueba");
        event.setLocation(location);
        event.setName("Evento #1");
        event.setPhoto("https://upload.wikimedia.org/wikipedia/commons/c/c7/Messe_Luzern_Corporate_Event.jpg");
        event.setPrivate(Boolean.FALSE);
        event.setTags(new ArrayList<String>(Arrays.asList("Fiesta", "Droga")));
        event.setEventMessages(eventMessages);

        EventDto event2 = new EventDto();
        event2.setDate("2017-04-28");
        event2.setDescription("Este es un evento de prueba");
        event2.setLocation(location);
        event2.setName("Evento #2");
        event2.setPhoto("https://cdn.pixabay.com/photo/2015/11/20/18/37/event-1053648_960_720.jpg");
        event2.setPrivate(Boolean.FALSE);
        event2.setTags(new ArrayList<String>(Arrays.asList("Fiesta", "Droga")));

        EventDto event3 = new EventDto();
        event3.setDate("2017-04-23");
        event3.setDescription("Este es un evento de prueba");
        event3.setLocation(location);
        event3.setName("Evento #3");
        event3.setPhoto("https://upload.wikimedia.org/wikipedia/commons/thumb/3/3d/Event_theater_frss1.jpg/1280px-Event_theater_frss1.jpg");
        event3.setPrivate(Boolean.FALSE);
        event3.setTags(new ArrayList<String>(Arrays.asList("Teatro")));

        EventDto event4 = new EventDto();
        event4.setDate("2017-04-26");
        event4.setDescription("Este es un evento de prueba");
        event4.setLocation(location);
        event4.setName("Evento #4");
        event4.setPhoto("https://upload.wikimedia.org/wikipedia/commons/d/d3/Windows_8_Launch_Event_in_Akihabara%2C_Tokyo.jpg");
        event4.setPrivate(Boolean.FALSE);
        event4.setTags(new ArrayList<String>(Arrays.asList("Tecnología")));

        eventList.add(event);
        eventList.add(event2);
        eventList.add(event3);
        eventList.add(event4);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Events");
        for (EventDto e : eventList) {
            databaseReference.push().setValue(e);
        }

    }

    public static void createGroups() {

        List<Group> groupList = new ArrayList<>();

        Group group1 = new Group();
        group1.setPhoto("https://www.knewton.com/wp-content/uploads/George-meetup.jpg");
        group1.setName("Amantes del vino en Sevilla");
        group1.setCategory(Category.COMIDA_Y_BEBIDA);
        group1.setAccessCode("98H9H8H983HD8H");
        group1.setDescription("Esto es un grupo de prueba");
        groupList.add(group1);

        Group group2 = new Group();
        group2.setPhoto("http://6798-presscdn-0-89.pagely.netdna-cdn.com/wp-content/uploads/2015/09/FranceMeetup.jpeg");
        group2.setName("Fines de semana en parquest");
        group2.setCategory(Category.NATURALEZA_Y_AVENTURA);
        group2.setDescription("Esto es un grupo de prueba");
        groupList.add(group2);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Groups");
        for (Group e : groupList) {
            databaseReference.push().setValue(e);
        }

    }
}
