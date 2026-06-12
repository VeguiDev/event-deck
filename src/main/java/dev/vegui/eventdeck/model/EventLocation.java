package dev.vegui.eventdeck.model;

public class EventLocation {
    private String venueName;
    private String street;
    private String city;
    private String province;
    private String country;

    public EventLocation() {}

    public EventLocation(
            String venueName,
            String street,
            String city,
            String province,
            String country
    ) {
        this.venueName = venueName;
        this.street = street;
        this.city = city;
        this.province = province;
        this.country = country;
    }

    public String getFormattedAddress() {
        return "%s, %s, %s, %s".formatted(
                street,
                city,
                province,
                country
        );
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        if (venueName == null || venueName.isBlank()) {
            return getFormattedAddress();
        }

        return venueName + " - " + getFormattedAddress();
    }

}
