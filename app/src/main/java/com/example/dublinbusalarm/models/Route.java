package com.example.dublinbusalarm.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Route /*implements Parcelable*/ {

    ArrayList<Trip> trips = new ArrayList<>();
    public static class Trip {
        String origin;
        String destination;
        ArrayList<ArrayList<String>> shapePoints = new ArrayList<>();
        ArrayList<Stop> stops = new ArrayList<>();

        public static class Stop {
            String name;
            ArrayList<String> latlng = new ArrayList<>();

            public Stop() {}

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public ArrayList<String> getLatlng() {
                return latlng;
            }

        }

        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public ArrayList<ArrayList<String>> getShapePoints() {
            return shapePoints;
        }

        public void setShapePoints(ArrayList<ArrayList<String>> shapePoints) {
            this.shapePoints = shapePoints;
        }

        public ArrayList<Stop> getStops() {
            return stops;
        }

        public void setStops(ArrayList<Stop> stops) {
            this.stops = stops;
        }
    }

    public ArrayList<Trip> getTrips() {
        return trips;
    }

    public void setTrips(ArrayList<Trip> trips) {
        this.trips = trips;
    }
    public void setTrips(Trip trip) { this.trips.add(trip); }
    public void addTrip(Trip t) {
        trips.add(t);
    }
}

