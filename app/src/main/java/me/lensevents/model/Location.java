package me.lensevents.model;


public class Location {

    private String country;
    private String province;
    private String city;
    private String street;

    public Location() {
    }

    public Location(String country, String province, String city, String street) {
        this.country = country;
        this.province = province;
        this.city = city;
        this.street = street;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (country != null ? !country.equals(location.country) : location.country != null)
            return false;
        if (province != null ? !province.equals(location.province) : location.province != null)
            return false;
        if (city != null ? !city.equals(location.city) : location.city != null) return false;
        return street != null ? street.equals(location.street) : location.street == null;

    }

    @Override
    public int hashCode() {
        int result = country != null ? country.hashCode() : 0;
        result = 31 * result + (province != null ? province.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (street != null ? street.hashCode() : 0);
        return result;
    }
}
