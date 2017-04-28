package me.lensevents.dto;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import me.lensevents.model.EventMessage;
import me.lensevents.model.Location;

public class EventDto {

    private String name;
    private String date;
    private List<String> tags;
    private String description;
    private Location location;
    private Boolean isPrivate;
    private List<FirebaseUser> administrators;
    private List<FirebaseUser> assistants;
    private String photo;
    private String confirmationDate;
    private List<EventMessageDto> eventMessages;

    public EventDto() {
    }

    public EventDto(String name, String date, List<String> tags, String description, Location location) {
        this.name = name;
        this.date = date;
        this.tags = tags;
        this.description = description;
        this.location = location;
    }

    public EventDto(String name, String date, List<String> tags, String description,
                    Location location, Boolean isPrivate, List<FirebaseUser> administrators, List<FirebaseUser> assistants,
                    String photo, String confirmationDate, List<EventMessageDto> eventMessages) {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
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

    public List<FirebaseUser> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(List<FirebaseUser> administrators) {
        this.administrators = administrators;
    }

    public List<FirebaseUser> getAssistants() {
        return assistants;
    }

    public void setAssistants(List<FirebaseUser> assistants) {
        this.assistants = assistants;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getConfirmationDate() {
        return confirmationDate;
    }

    public void setConfirmationDate(String confirmationDate) {
        this.confirmationDate = confirmationDate;
    }

    public List<EventMessageDto> getEventMessages() {
        return eventMessages;
    }

    public void setEventMessages(List<EventMessageDto> eventMessages) {
        this.eventMessages = eventMessages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventDto event = (EventDto) o;

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
