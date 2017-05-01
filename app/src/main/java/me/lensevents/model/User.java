package me.lensevents.model;

public class User {

    private String displayName;
    private String email;
    private String photoUrl;
    private String uid;

    public User() {
    }

    public User(String displayName, String email, String photoUrl, String uid) {
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (displayName != null ? !displayName.equals(user.displayName) : user.displayName != null)
            return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (photoUrl != null ? !photoUrl.equals(user.photoUrl) : user.photoUrl != null)
            return false;
        return uid != null ? uid.equals(user.uid) : user.uid == null;

    }

    @Override
    public int hashCode() {
        int result = displayName != null ? displayName.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (photoUrl != null ? photoUrl.hashCode() : 0);
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        return result;
    }
}
