package me.lensevents.dto;

import java.io.Serializable;
import java.util.List;

import me.lensevents.model.Category;

public class GroupDto implements Serializable{

    private String name;
    private String description;
    private List<String> members;
    private List<String> administrators;
    private String photo;
    private List<String> media;
    private Category category;
    private String accessCode;

    public GroupDto() {
    }

    public GroupDto(String name, String description, String photo, Category category) {
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.category = category;
    }

    public GroupDto(String name, String description, List<String> members, List<String> administrators, String photo, List<String> media, Category category, String accessCode) {
        this.name = name;
        this.description = description;
        this.members = members;
        this.administrators = administrators;
        this.photo = photo;
        this.media = media;
        this.category = category;
        this.accessCode = accessCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<String> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(List<String> administrators) {
        this.administrators = administrators;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<String> getMedia() {
        return media;
    }

    public void setMedia(List<String> media) {
        this.media = media;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupDto group = (GroupDto) o;

        if (name != null ? !name.equals(group.name) : group.name != null) return false;
        if (description != null ? !description.equals(group.description) : group.description != null)
            return false;
        if (members != null ? !members.equals(group.members) : group.members != null) return false;
        if (administrators != null ? !administrators.equals(group.administrators) : group.administrators != null)
            return false;
        if (photo != null ? !photo.equals(group.photo) : group.photo != null) return false;
        if (media != null ? !media.equals(group.media) : group.media != null) return false;
        if (category != group.category) return false;
        return accessCode != null ? accessCode.equals(group.accessCode) : group.accessCode == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (members != null ? members.hashCode() : 0);
        result = 31 * result + (administrators != null ? administrators.hashCode() : 0);
        result = 31 * result + (photo != null ? photo.hashCode() : 0);
        result = 31 * result + (media != null ? media.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (accessCode != null ? accessCode.hashCode() : 0);
        return result;
    }
}
