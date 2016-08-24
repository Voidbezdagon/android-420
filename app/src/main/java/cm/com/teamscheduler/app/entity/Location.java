package cm.com.teamscheduler.app.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by void on 24.08.16.
 */
public class Location implements Serializable {

    private Long id;
    private String name;
    private String region;
    private String city;
    private Integer zip;
    private String street;
    private Integer streetNumber;
    private String details;
    private Float lat;
    private Float lng;
    private List<LocationItem> locationItems;
    private List<Schedule> schedules;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getZip() {
        return zip;
    }

    public void setZip(Integer zip) {
        this.zip = zip;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(Integer streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public List<LocationItem> getLocationItems() {
        return locationItems;
    }

    public void setLocationItems(List<LocationItem> locationItems) {
        this.locationItems = locationItems;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }
}
