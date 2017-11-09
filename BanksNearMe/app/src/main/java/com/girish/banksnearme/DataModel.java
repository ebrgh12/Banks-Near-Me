package com.girish.banksnearme;

/**
 * Created by girish on 08-10-2017.
 */

public class DataModel {

    String name;
    String image;
    String address;
    Double rating;
    Boolean isOpen;
    Double latitude, longitude;

    public DataModel(String name, String image, String address, Double latitude, Double longitude) {
        this.name = name;
        this.image = image;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public DataModel(String name, String image, String address, Double rating, Boolean isOpen,
                     Double latitude, Double longitude) {
        this.name = name;
        this.image = image;
        this.address = address;
        this.rating = rating;
        this.isOpen = isOpen;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}