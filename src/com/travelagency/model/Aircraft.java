package com.travelagency.model;

public class Aircraft {
    private int aircraftId;
    private String aircraftName;

    public Aircraft() {
    }

    public Aircraft(String aircraftName) {
        this.aircraftName = aircraftName;
    }

    public Aircraft(int aircraftId, String aircraftName) {
        this.aircraftId = aircraftId;
        this.aircraftName = aircraftName;
    }

    public int getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(int aircraftId) {
        this.aircraftId = aircraftId;
    }

    public String getAircraftName() {
        return aircraftName;
    }

    public void setAircraftName(String aircraftName) {
        this.aircraftName = aircraftName;
    }

    @Override
    public String toString() {
        return aircraftName;
    }
}