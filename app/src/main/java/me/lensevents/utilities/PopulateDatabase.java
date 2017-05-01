package me.lensevents.utilities;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.lensevents.dto.EventDto;
import me.lensevents.dto.EventMessageDto;
import me.lensevents.dto.GroupDto;
import me.lensevents.model.Category;
import me.lensevents.model.Location;
import me.lensevents.model.User;

public class PopulateDatabase {

    public static void main(String[] args) {
        //PopulateDatabase.createEvents();
        //PopulateDatabase.createUsers();
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

    public static void createUsers() {

        List<User> userList = new ArrayList<>();

        User user = new User();
        user.setUid("uid1");
        user.setDisplayName("Javier Bonilla");
        user.setPhotoUrl("http://pbs.twimg.com/profile_images/820073293478854656/dA2_6WI9_normal.jpg");
        user.setEmail("jeffavy.74@gmail.com");
        userList.add(user);

        User user2 = new User();
        user2.setUid("uid2");
        user2.setDisplayName("Danié Moreno");
        user2.setPhotoUrl("https://lh3.googleusercontent.com/-lZXJodk_UzM/AAAAAAAAAAI/AAAAAAAAFmk/iRXOEFRL69I/s96-c/photo.jpg");
        user2.setEmail("danielmoreno@gmail.com");
        userList.add(user2);

        User user3 = new User();
        user3.setUid("uid3");
        user3.setDisplayName("Gitano de Pueblo");
        user3.setPhotoUrl("https://pbs.twimg.com/profile_images/426737413034348545/AvYpXpN9_400x400.jpeg");
        user3.setEmail("gitanodelreino@gmail.com");
        userList.add(user3);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
        for (User e : userList) {
            databaseReference.push().setValue(e);
        }
    }

    public static void createGroups() {

        List<GroupDto> groupList = new ArrayList<>();
        final List<String> admins = new ArrayList<>();
        final List<String> members = new ArrayList<>();
        final List<String> admins2 = new ArrayList<>();
        final List<String> members2 = new ArrayList<>();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        admins.add("uid1");
        admins2.add("uid2");
        members.add("uid1");
        members.add("uid3");
        members2.add("uid2");
        members2.add("uid3");

        GroupDto group1 = new GroupDto();
        group1.setAdministrators(admins);
        group1.setMembers(members);
        group1.setPhoto("https://www.knewton.com/wp-content/uploads/George-meetup.jpg");
        group1.setName("Amantes del vino en Sevilla");
        group1.setCategory(Category.COMIDA_Y_BEBIDA);
        group1.setAccessCode("98H9H8H983HD8H");
        group1.setDescription("Esto es un grupo de prueba");
        groupList.add(group1);


        GroupDto group2 = new GroupDto();
        group2.setAdministrators(admins2);
        group2.setMembers(members2);
        group2.setPhoto("http://6798-presscdn-0-89.pagely.netdna-cdn.com/wp-content/uploads/2015/09/FranceMeetup.jpeg");
        group2.setName("Fines de semana en parquest");
        group2.setCategory(Category.NATURALEZA_Y_AVENTURA);
        group2.setDescription("Esto es un grupo de prueba");
        groupList.add(group2);

        DatabaseReference databaseReference = firebaseDatabase.getReference("Groups");
        for (GroupDto e : groupList) {
            databaseReference.push().setValue(e);
        }

    }
}
