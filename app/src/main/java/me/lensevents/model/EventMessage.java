package me.lensevents.model;


import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class EventMessage {

    private String text;
    private Calendar date;
    private FirebaseUser sender;

    public EventMessage() {
    }

    public EventMessage(String text, Calendar date, FirebaseUser sender) {
        this.text = text;
        this.date = date;
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public FirebaseUser getSender() {
        return sender;
    }

    public void setSender(FirebaseUser sender) {
        this.sender = sender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventMessage that = (EventMessage) o;

        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        return sender != null ? sender.equals(that.sender) : that.sender == null;

    }

    @Override
    public int hashCode() {
        int result = text != null ? text.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (sender != null ? sender.hashCode() : 0);
        return result;
    }
}
