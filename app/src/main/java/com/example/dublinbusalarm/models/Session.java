package com.example.dublinbusalarm.models;

import java.util.ArrayList;

/**
 * The Session class describes the information we want to save on the database,
 * regarding the user's use of the app.
 * In particular, we'll store:
 * - user's location coordinates (lat, lng) when a stop is selected on the map
 * - selected stop's coordinates (lat, lng)
 * - time if took for the bus to reach the selected stop (since the stop was selected until it was reached)
 * - the date the trip was made
 */
public class Session {

    // userOriginCoords holds the user's coordinates when they select a stop
    private ArrayList<Double> userOriginCoords;
    // coords of the stop selected by user
    private ArrayList<Double> selectedStopCoords;
    // time that took to reach the selected stop
    private long timeTakenToStop;
    // Date the trip was made
    private long date;

    public Session(ArrayList<Double> userOriginCoords,
                   ArrayList<Double> selectedStopCoords,
                   long timeTakenToStop,
                   long date) {
        this.userOriginCoords = userOriginCoords;
        this.selectedStopCoords = selectedStopCoords;
        this.timeTakenToStop = timeTakenToStop;
        this.date = date;
    }

    public ArrayList<Double> getUserOriginCoords() {
        return userOriginCoords;
    }

    public ArrayList<Double> getSelectedStopCoords() {
        return selectedStopCoords;
    }

    public long getTimeTakenToStop() {
        return timeTakenToStop;
    }

    public long getDate() {
        return date;
    }
}
