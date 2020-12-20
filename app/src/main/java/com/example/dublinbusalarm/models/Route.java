package com.example.dublinbusalarm.models;

import java.io.Serializable;
import java.util.ArrayList;

import static java.lang.Double.parseDouble;

public class Route implements Serializable {

    ArrayList<Trip> trips = new ArrayList<>();

    public static class Trip implements Serializable{

        String origin;
        String destination;
        ArrayList<ArrayList<String>> shapePoints = new ArrayList<>();
        ArrayList<Stop> stops = new ArrayList<>();

        public static class Stop implements Serializable {

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

            public double getLat() {
                return parseDouble(this.getLatlng().get(0));
            }

            public double getLng() {
                return parseDouble(this.getLatlng().get(1));
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

        public double getShapePtLat(int point) {
            return parseDouble(this.shapePoints.get(point).get(0));
        }

        public double getShapePtLng(int point) {
            return parseDouble(this.shapePoints.get(point).get(1));
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
}

