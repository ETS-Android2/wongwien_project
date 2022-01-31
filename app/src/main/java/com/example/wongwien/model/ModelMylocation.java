package com.example.wongwien.model;

public class ModelMylocation {
    String address;
    String latitude;
    String longitude;
    String map_title;

    public ModelMylocation() {
    }

    public ModelMylocation(String address, String latitude, String longitude, String map_title) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.map_title = map_title;
    }

    @Override
    public String toString() {
        return "ModelMylocation{" +
                "address='" + address + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", map_title='" + map_title + '\'' +
                '}';
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMap_title() {
        return map_title;
    }

    public void setMap_title(String map_title) {
        this.map_title = map_title;
    }
}
