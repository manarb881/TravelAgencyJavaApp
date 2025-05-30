package com.travelagency.model;

public class City {
    private int cityId;
    private String cityName;

    public City() {
    }

    public City(String cityName) {
        this.cityName = cityName;
    }

    public City(int cityId, String cityName) {
        this.cityId = cityId;
        this.cityName = cityName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public String toString() {
        // This will be used by JComboBox to display the city
        return cityName; //  Or: return cityName + " (ID: " + cityId + ")";
    }

    // Optional: equals and hashCode if you plan to put City objects in Sets or use them as Map keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return cityId == city.cityId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(cityId);
    }
}