package me.lensevents.model;

import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class Event {

    private String name;
    private Calendar date;
    private Set<String> tags;
    private String description;
    private Location location;
    private Boolean isPrivate;
    private Set<FirebaseUser> administrators;
    private Set<FirebaseUser> assistants;
    private String photo;
    private Calendar confirmationDate;
    private List<EventMessage> eventMessages;

    public Event() {
    }

    public Event(String name, Calendar date, Set<String> tags, String description, Location location) {
        this.name = name;
        this.date = date;
        this.tags = tags;
        this.description = description;
        this.location = location;
    }

    public Event(String name, Calendar date, Set<String> tags, String description,
                 Location location, Boolean isPrivate, Set<FirebaseUser> administrators, Set<FirebaseUser> assistants,
                 String photo, Calendar confirmationDate, List<EventMessage> eventMessages) {
        this.name = name;
        this.date = date;
        this.tags = tags;
        this.description = description;
        this.location = location;
        this.isPrivate = isPrivate;
        this.administrators = administrators;
        this.assistants = assistants;
        this.photo = photo;
        this.confirmationDate = confirmationDate;
        this.eventMessages = eventMessages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Set<FirebaseUser> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(Set<FirebaseUser> administrators) {
        this.administrators = administrators;
    }

    public Set<FirebaseUser> getAssistants() {
        return assistants;
    }

    public void setAssistants(Set<FirebaseUser> assistants) {
        this.assistants = assistants;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Calendar getConfirmationDate() {
        return confirmationDate;
    }

    public void setConfirmationDate(Calendar confirmationDate) {
        this.confirmationDate = confirmationDate;
    }

    public List<EventMessage> getEventMessages() {
        return eventMessages;
    }

    public void setEventMessages(List<EventMessage> eventMessages) {
        this.eventMessages = eventMessages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (name != null ? !name.equals(event.name) : event.name != null) return false;
        if (date != null ? !date.equals(event.date) : event.date != null) return false;
        if (tags != null ? !tags.equals(event.tags) : event.tags != null) return false;
        if (description != null ? !description.equals(event.description) : event.description != null)
            return false;
        if (location != null ? !location.equals(event.location) : event.location != null)
            return false;
        if (isPrivate != null ? !isPrivate.equals(event.isPrivate) : event.isPrivate != null)
            return false;
        if (administrators != null ? !administrators.equals(event.administrators) : event.administrators != null)
            return false;
        if (assistants != null ? !assistants.equals(event.assistants) : event.assistants != null)
            return false;
        if (photo != null ? !photo.equals(event.photo) : event.photo != null) return false;
        if (confirmationDate != null ? !confirmationDate.equals(event.confirmationDate) : event.confirmationDate != null)
            return false;
        return eventMessages != null ? eventMessages.equals(event.eventMessages) : event.eventMessages == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (isPrivate != null ? isPrivate.hashCode() : 0);
        result = 31 * result + (administrators != null ? administrators.hashCode() : 0);
        result = 31 * result + (assistants != null ? assistants.hashCode() : 0);
        result = 31 * result + (photo != null ? photo.hashCode() : 0);
        result = 31 * result + (confirmationDate != null ? confirmationDate.hashCode() : 0);
        result = 31 * result + (eventMessages != null ? eventMessages.hashCode() : 0);
        return result;
    }
}
